package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.quinto.morph.syntaxengine.TreeNode;

public class WordForm implements Serializable {
  private static final long serialVersionUID = 1L;
  public CompressedLemma lemma;
  public Set< Grammeme > form;
  private transient Set< Grammeme > grammemesSet;

  public WordForm( CompressedLemma lemma, Set< Grammeme > form ) {
    this.lemma = lemma;
    this.form = form;
  }
  
  public String getWord() {
    return lemma.getWordWithGrammemes( form );
  }
  
  public String getWordWithGrammemes( String... grammemes ) {
    return Utils.getFirstOrNull( getWordsWithGrammemes( grammemes ) );
  }
  
  public String getWordWithGrammemes( Grammeme... grammemes ) {
    return Utils.getFirstOrNull( getWordsWithGrammemes( grammemes ) );
  }
  
  public String getWordWithGrammemes( Iterable< ? > grammemes ) {
    return Utils.getFirstOrNull( getWordsWithGrammemes( grammemes ) );
  }
  
  public WordForm getWordFormWithGrammemes( String... grammemes ) {
    return Utils.getFirstOrNull( getWordFormsWithGrammemes( grammemes ) );
  }
  
  public WordForm getWordFormWithGrammemes( Grammeme... grammemes ) {
    return Utils.getFirstOrNull( getWordFormsWithGrammemes( grammemes ) );
  }
  
  public WordForm getWordFormWithGrammemes( Iterable< ? > grammemes ) {
    return Utils.getFirstOrNull( getWordFormsWithGrammemes( grammemes ) );
  }
  
  public Set< String > getWordsWithGrammemes( String... grammemes ) {
    return getWordsWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public Set< String > getWordsWithGrammemes( Grammeme... grammemes ) {
    return getWordsWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public Set< String > getWordsWithGrammemes( Iterable< ? > grammemes ) {
    return getWordFormsWithGrammemes( Grammeme.toSet( grammemes ) ).stream().map( form -> form.getWord() ).collect( Collectors.toCollection( LinkedHashSet::new ) );
  }
  
  public List< WordForm > getWordFormsWithGrammemes( String... grammemes ) {
    return getWordFormsWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public List< WordForm > getWordFormsWithGrammemes( Grammeme... grammemes ) {
    return getWordFormsWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public List< WordForm > getWordFormsWithGrammemes( Iterable< ? > grammemes ) {
    Set< Grammeme > gs = new HashSet<>( form );
    grammemes = Grammeme.toSet( grammemes ).stream().map( g -> lemma.dictionary.grammemes.get( g.name ) ).collect( Collectors.toSet() );
    Set< Grammeme > parents = Utils.i2s( grammemes ).map( g -> ( ( Grammeme )g ).getMaxParent() ).collect( Collectors.toSet() );
    Iterator< Grammeme > it = gs.iterator();
    while ( it.hasNext() ) {
      Grammeme g = it.next();
      if ( parents.contains( g.getMaxParent() ) )
        it.remove();
    }
    List< WordForm > ret = lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, grammemes );
    return filterOut( ret, gs );
  }
  
  private List< WordForm > filterOut( List< WordForm > forms, Set< Grammeme > gs ) {
    if ( gs.isEmpty() )
      return forms;
    List< Set< Grammeme > > queue = new ArrayList<>();
    queue.add( gs );
    while ( true ) {
      Set< Integer > indexes = new TreeSet<>();
      for ( Set< Grammeme > set : queue ) {
        for ( int i = forms.size() - 1; i >= 0; i-- ) {
          WordForm f = forms.get( i );
          if ( set.stream().allMatch( g -> f.hasGrammeme( g ) ) )
            indexes.add( i );
        }
      }
      if ( !indexes.isEmpty() )
        return indexes.stream().map( i -> forms.get( i ) ).collect( Collectors.toList() );
      List< Set< Grammeme > > newQueue = new ArrayList<>();
      for ( Set< Grammeme > set : queue ) {
        if ( set.size() == 1 )
          return forms;
        for ( Grammeme g : set ) {
          Set< Grammeme > newSet = new HashSet<>( set );
          newSet.remove( g );
          newQueue.add( newSet );
        }
      }
      queue = newQueue;
    }
  }
  
  public Set< Grammeme > getGrammemesSet() {
    if ( grammemesSet == null ) {
      grammemesSet = new HashSet<>( form );
      grammemesSet.addAll( lemma.getSuffixParadigm().grammemes );
    }
    return grammemesSet;
  }
  
  public Dictionary getDictionary() {
    return lemma.dictionary;
  }

  @Override
  public String toString() {
    return "WordForm{" + getWord() + ", form=" + getGrammemesSet() + '}';
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode( this.lemma );
    hash = 59 * hash + Objects.hashCode( this.form );
    return hash;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this )
      return true;
    if ( !( obj instanceof WordForm ) )
      return false;
    WordForm other = ( WordForm )obj;
    return Objects.equals( this.lemma, other.lemma ) && Objects.equals( this.form, other.form );
  }

  public boolean hasGrammeme( String grammeme ) {
    return hasGrammeme( new Grammeme( grammeme ) );
  }

  public boolean hasGrammeme( Grammeme grammeme ) {
    return form.contains( grammeme ) ||
      lemma.getSuffixParadigm().grammemes.contains( grammeme ) ||
      form.stream().anyMatch( g -> g.getAllParents().contains( grammeme ) ) ||
      lemma.getSuffixParadigm().grammemes.stream().anyMatch( g -> g.getAllParents().contains( grammeme ) );
  }
  
  public TreeNode toTreeNode() {
    TreeNode ret = new TreeNode( this );
    ret.withTag( "word", getWord() );
    ret.withTag( "lemma", lemma.name );
    ret.withTag( "lemmaId", String.valueOf( lemma.id ) );
    ret.withTag( "paradigmId", String.valueOf( lemma.paradigmIdx ) );
    getGrammemesSet().stream().flatMap( g -> g.getAllParents().stream() ).filter( g -> g.parent != null ).map( g -> Pair.of( g.parent.name, g.name ) ).sorted( Comparator.comparing( p -> p.getKey() ) ).forEach( p -> ret.withTag( p.getKey(), p.getValue() ) );
    getGrammemesSet().stream().flatMap( g -> g.getAllParents().stream() ).map( g -> g.name ).sorted().forEach( ret::withTag );
    return ret;
  }
}