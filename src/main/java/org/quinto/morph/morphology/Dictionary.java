package org.quinto.morph.morphology;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.quinto.dawg.DAWGSet;
import org.quinto.dawg.DAWGSetValuedMap;
import org.quinto.dawg.ModifiableDAWGSet;
import org.quinto.dawg.ModifiableDAWGSetValuedMap;

public class Dictionary implements Serializable {
  private static final long serialVersionUID = 12L;
  public Map< String, Grammeme > grammemes = new LinkedHashMap<>();
  public Map< Integer, CompressedLemma > lemmas = new LinkedHashMap<>();
  public List< SuffixParadigm > allParadigms = new ArrayList<>();
  public DAWGSetValuedMap words;
  public DAWGSet allWords;
  public DAWGSet inverseWords;
  public PatriciaTrie< int[] > suffixParadigms;
  public transient PatriciaTrie< Integer > lemmasIndex;
  public static final DAWGSet PREFIXES = new ModifiableDAWGSet( Arrays.asList(
    "авиа",
    "авто",
    "аква",
    "анти",
    "анти-",
    "антропо",
    "архи",
    "арт",
    "арт-",
    "астро",
    "аудио",
    "аэро",
    "без",
    "бес",
    "био",
    "вело",
    "взаимо",
    "вне",
    "внутри",
    "видео",
    "вице-",
    "вперед",
    "впереди",
    "гекто",
    "гелио",
    "гео",
    "гетеро",
    "гига",
    "гигро",
    "гипер",
    "гипо",
    "гомо",
    "дву",
    "двух",
    "де",
    "дез",
    "дека",
    "деци",
    "дис",
    "до",
    "евро",
    "за",
    "зоо",
    "интер",
    "инфра",
    "квази",
    "квази-",
    "кило",
    "кино",
    "контр",
    "контр-",
    "космо",
    "космо-",
    "крипто",
    "лейб-",
    "лже",
    "лже-",
    "макро",
    "макси",
    "макси-",
    "мало",
    "меж",
    "медиа",
    "медиа-",
    "мега",
    "мета",
    "мета-",
    "метео",
    "метро",
    "микро",
    "милли",
    "мини",
    "мини-",
    "моно",
    "мото",
    "много",
    "мульти",
    "нано",
    "нарко",
    "не",
    "небез",
    "недо",
    "нейро",
    "нео",
    "низко",
    "обер-",
    "обще",
    "одно",
    "около",
    "орто",
    "палео",
    "пан",
    "пара",
    "пента",
    "пере",
    "пиро",
    "поли",
    "полу",
    "после",
    "пост",
    "пост-",
    "порно",
    "пра",
    "пра-",
    "пред",
    "пресс-",
    "противо",
    "противо-",
    "прото",
    "псевдо",
    "псевдо-",
    "радио",
    "разно",
    "ре",
    "ретро",
    "ретро-",
    "само",
    "санти",
    "сверх",
    "сверх-",
    "спец",
    "суб",
    "супер",
    "супер-",
    "супра",
    "теле",
    "тетра",
    "топ-",
    "транс",
    "транс-",
    "ультра",
    "унтер-",
    "штаб-",
    "экзо",
    "эко",
    "эндо",
    "эконом-",
    "экс",
    "экс-",
    "экстра",
    "экстра-",
    "электро",
    "энерго",
    "этно"
  ) ).compress();
  
  private void readObject( ObjectInputStream ois ) throws IOException, ClassNotFoundException {
    ois.defaultReadObject();
    init();
  }
  
  public void init() {
    lemmasIndex = new PatriciaTrie<>();
    for ( CompressedLemma lemma : lemmas.values() )
      lemmasIndex.put( lemma.name + lemma.paradigmIdx, lemma.id );
    if ( words == null ) {
      Set< Integer > productiveParadigms = lemmas
        .values()
        .stream()
        .collect( Collectors.groupingBy( l -> l.paradigmIdx, Collectors.counting() ) )
        .entrySet()
        .stream()
        .filter( e -> e.getValue() >= 3L )
        .map( e -> e.getKey() )
        .collect( Collectors.toSet() );
      words = new ModifiableDAWGSetValuedMap( true );
      for ( CompressedLemma lemma : lemmas.values() )
        words.put( lemma.name, String.valueOf( lemma.paradigmIdx ) );
      words = ( ( ModifiableDAWGSetValuedMap )words ).compress();
      allWords = new ModifiableDAWGSet( false );
      inverseWords = new ModifiableDAWGSet( false );
      lemmas.values().stream().map( l -> l.getLemma() ).flatMap( l -> l.forms.values().stream() ).forEach( s -> {
        allWords.add( s );
        inverseWords.add( DictReader.reverse( s ) );
      } );
      allWords = ( ( ModifiableDAWGSet )allWords ).compress();
      inverseWords = ( ( ModifiableDAWGSet )inverseWords ).compress();
      suffixParadigms = new PatriciaTrie<>();
      Map< String, int[] > syncParadigms = Collections.synchronizedMap( suffixParadigms );
      for ( int prefixLength = 1; prefixLength <= 5; prefixLength++ ) {
        final int prefixLen = prefixLength;
        List< String > prefixes = inverseWords
          .stream()
          .filter( s -> s.length() > prefixLen )
          .map( s -> s.substring( 0, prefixLen ) )
          .collect( Collectors.groupingBy( Function.identity(), Collectors.counting() ) )
          .entrySet()
          .stream()
          .filter( e -> e.getValue() > 1L )
          .map( e -> e.getKey() )
          .sorted()
          .collect( Collectors.toList() );
        System.out.println( prefixLen + ": " + prefixes.size() );
        prefixes
          .stream()
          .parallel()
          .forEach( prefix -> {
          NavigableSet< String > prefixSet = inverseWords.prefixSet( prefix );
          int cnt = prefixSet.size();
          int paradigms[] = prefixSet
            .stream()
            .map( s -> DictReader.reverse( s ) )
            .flatMap( s -> getLemmas( s ).stream() )
            .filter( l -> l.isMorphable() )
            .map( l -> l.paradigmIdx )
            .filter( p -> productiveParadigms.contains( p ) )
            .collect( Collectors.groupingBy( Function.identity(), Collectors.counting() ) )
            .entrySet()
            .stream()
            .filter( e -> e.getValue() > cnt / 2 )
            .mapToInt( e -> e.getKey() )
            .toArray();
          if ( paradigms.length > 0 )
            syncParadigms.put( prefix, paradigms );
          System.out.println( prefix + " / " + cnt );
        } );
      }
    }
  }
  
  public List< CompressedLemma > getLemmas( String word ) {
    return getLemmas( word, true );
  }
  
  private List< CompressedLemma > getLemmas( String word, boolean cutPrefixes ) {
    List< CompressedLemma > ret = new ArrayList<>();
    for ( int l = word.length(); l >= 1; l-- ) {
      String w = word.substring( 0, l );
      Set< String > paradigms = words.get( w );
      for ( String paradigm : paradigms ) {
        int idx = Integer.parseInt( paradigm );
        int id = lemmasIndex.get( w + idx );
        CompressedLemma lemma = lemmas.get( id );
        if ( lemma.canProduce( word ) )
          ret.add( lemma );
      }
    }
    if ( !ret.isEmpty() )
      return ret;
    for ( int l = word.length() - 1; l >= 1; l-- ) {
      String w = word.substring( 0, l );
      if ( PREFIXES.contains( w ) ) {
        List< CompressedLemma > ls = getLemmas( word.substring( l ) );
        for ( CompressedLemma lemma : ls )
          if ( lemma.isMorphable() )
            ret.add( new CompressedLemma( -1, w + lemma.name, lemma.paradigmIdx, allParadigms ) );
      }
    }
    if ( !ret.isEmpty() )
      return ret;
    if ( cutPrefixes ) {
      for ( int l = 1; l < 5 && l <= word.length() - 3; l++ ) {
        String prefix = word.substring( 0, l );
        List< CompressedLemma > ls = getLemmas( word.substring( l ), false );
        for ( CompressedLemma lemma : ls )
          if ( lemma.isMorphable() )
            ret.add( new CompressedLemma( -1, prefix + lemma.name, lemma.paradigmIdx, allParadigms ) );
      }
      if ( !ret.isEmpty() )
        return ret;
      String reverse = DictReader.reverse( word );
      for ( int i = 5; i >= 1; i-- ) {
        String w = reverse.substring( 0, i );
        int paradigms[] = suffixParadigms.get( w );
        if ( paradigms != null && paradigms.length > 0 ) {
          for ( int paradigmId : paradigms ) {
            SuffixParadigm paradigm = allParadigms.get( paradigmId );
            if ( paradigm.shift <= i )
              ret.add( new CompressedLemma( -1, word.substring( 0, word.length() - paradigm.shift ), paradigmId, allParadigms ) );
          }
          if ( !ret.isEmpty() )
            return ret;
        }
      }
    }
    return ret;
  }
  
  public static void main( String... args ) throws Exception {
    Dictionary dictionary;
    try {
      dictionary = DictReader.readCached();
    } catch ( IOException | ClassNotFoundException e ) {
      dictionary = DictReader.readFromXml();
      DictReader.writeCached( dictionary );
    }
    for ( CompressedLemma lemma : dictionary.getLemmas( "котики" ) ) {
      System.out.println( lemma );
      System.out.println( lemma.getSuffixParadigm().grammemes );
    }
    System.out.println();
    for ( CompressedLemma lemma : dictionary.getLemmas( "протокотики" ) ) {
      System.out.println( lemma );
      System.out.println( lemma.getSuffixParadigm().grammemes );
    }
    System.out.println();
    for ( CompressedLemma lemma : dictionary.getLemmas( "прокотики" ) ) {
      System.out.println( lemma );
      System.out.println( lemma.getSuffixParadigm().grammemes );
    }
    System.out.println();
    for ( CompressedLemma lemma : dictionary.getLemmas( "тошки" ) ) {
      System.out.println( lemma );
      System.out.println( lemma.getSuffixParadigm().grammemes );
    }
    System.out.println();
    for ( CompressedLemma lemma : dictionary.getLemmas( "бошки" ) ) {
      System.out.println( lemma );
      System.out.println( lemma.getSuffixParadigm().grammemes );
    }
  }
}