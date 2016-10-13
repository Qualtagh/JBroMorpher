package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompressedLemma implements Serializable {
  private static final long serialVersionUID = 3L;
  public int id;
  public String name;
  public int paradigmIdx;
  public Dictionary dictionary;
  
  public CompressedLemma( int id, String name, int paradigmIdx, Dictionary dictionary ) {
    this.id = id;
    this.name = name;
    this.paradigmIdx = paradigmIdx;
    this.dictionary = dictionary;
  }
  
  public Lemma getLemma() {
    Lemma ret = new Lemma();
    SuffixParadigm paradigm = getSuffixParadigm();
    ret.id = id;
    ret.name = paradigm.shift == 0 ? name : name + paradigm.forms.values().iterator().next();
    ret.grammemes = paradigm.grammemes;
    for ( Map.Entry< Set< Grammeme >, String > form : paradigm.forms.entrySet() )
      ret.forms.put( form.getKey(), name + form.getValue() );
    return ret;
  }

  @Override
  public String toString() {
    return "CompressedLemma{" + "id=" + id + ", name=" + name + ", paradigm=" + getSuffixParadigm() + '}';
  }
  
  public SuffixParadigm getSuffixParadigm() {
    return dictionary.allParadigms.get( paradigmIdx );
  }
  
  public boolean canProduce( String word ) {
    return !getSuitableGrammemeSets( word ).isEmpty();
  }
  
  public List< Set< Grammeme > > getSuitableGrammemeSets( String word ) {
    if ( !word.startsWith( name ) )
      return Collections.EMPTY_LIST;
    word = word.substring( name.length() );
    SuffixParadigm paradigm = getSuffixParadigm();
    List< Set< Grammeme > > ret = new ArrayList<>();
    for ( Map.Entry< Set< Grammeme >, String > e : paradigm.forms.entrySet() )
      if ( e.getValue().equals( word ) )
        ret.add( e.getKey() );
    return ret;
  }
  
  public List< WordForm > getWordForms( String word ) {
    return getSuitableGrammemeSets( word ).stream().map( gs -> new WordForm( this, gs ) ).collect( Collectors.toList() );
  }

  public boolean isMorphablePOS() {
    return getSuffixParadigm().isMorphablePOS();
  }

  public String getWordWithGrammemes( Iterable< ? > grammemes ) {
    Set< Grammeme > gs = Grammeme.toSet( grammemes );
    SuffixParadigm paradigm = getSuffixParadigm();
    return name + paradigm.forms.get( gs );
  }

  public String getWordWithGrammemes( Grammeme... grammemes ) {
    return getWordWithGrammemes( Grammeme.toSet( grammemes ) );
  }

  public String getWordWithGrammemes( String... grammemes ) {
    return getWordWithGrammemes( Grammeme.toSet( grammemes ) );
  }

  public List< WordForm > getWordFormsWithGrammemes( Quantifier quantifier, Iterable< ? > grammemes ) {
    Set< Grammeme > gs = Grammeme.toSet( grammemes );
    SuffixParadigm paradigm = getSuffixParadigm();
    return paradigm
      .forms
      .keySet()
      .stream()
      .filter( g -> quantifier == Quantifier.CONTAINS_EVERY ? g.containsAll( gs ) :
                    quantifier == Quantifier.CONTAINS_ANY ? g.stream().anyMatch( gs::contains ) :
                    g.equals( gs ) )
      .map( g -> new WordForm( this, g ) )
      .collect( Collectors.toList() );
  }

  public List< WordForm > getWordFormsWithGrammemes( Quantifier quantifier, Grammeme... grammemes ) {
    return getWordFormsWithGrammemes( quantifier, Grammeme.toSet( grammemes ) );
  }

  public List< WordForm > getWordFormsWithGrammemes( Quantifier quantifier, String... grammemes ) {
    return getWordFormsWithGrammemes( quantifier, Grammeme.toSet( grammemes ) );
  }
  
  public boolean hasGrammeme( String grammeme ) {
    return hasGrammeme( new Grammeme( grammeme ) );
  }
  
  public boolean hasGrammeme( Grammeme grammeme ) {
    SuffixParadigm suffixParadigm = getSuffixParadigm();
    return suffixParadigm.grammemes.contains( grammeme ) ||
      suffixParadigm.forms.keySet().stream().anyMatch( set -> set.contains( grammeme ) ) ||
      suffixParadigm.grammemes.stream().anyMatch( g -> g.getAllParents().contains( grammeme ) ) ||
      suffixParadigm.forms.keySet().stream().anyMatch( set -> set.stream().anyMatch( g -> g.getAllParents().contains( grammeme ) ) );
  }
}