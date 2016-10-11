package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    return getWordWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public String getWordWithGrammemes( Grammeme... grammemes ) {
    return getWordWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public String getWordWithGrammemes( Iterable< ? > grammemes ) {
    WordForm wordForm = getWordFormWithGrammemes( Grammeme.toSet( grammemes ) );
    return wordForm == null ? null : wordForm.getWord();
  }
  
  public WordForm getWordFormWithGrammemes( String... grammemes ) {
    return getWordFormWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public WordForm getWordFormWithGrammemes( Grammeme... grammemes ) {
    return getWordFormWithGrammemes( Grammeme.toSet( grammemes ) );
  }
  
  public WordForm getWordFormWithGrammemes( Iterable< ? > grammemes ) {
    Set< Grammeme > gs = new HashSet<>( form );
    grammemes = Grammeme.toSet( grammemes ).stream().map( g -> lemma.dictionary.grammemes.get( g.name ) ).collect( Collectors.toSet() );
    Set< Grammeme > parents = Utils.i2s( grammemes ).map( g -> ( ( Grammeme )g ).getMaxParent() ).collect( Collectors.toSet() );
    Iterator< Grammeme > it = gs.iterator();
    while ( it.hasNext() ) {
      Grammeme g = it.next();
      if ( parents.contains( g.getMaxParent() ) )
        it.remove();
    }
    gs.addAll( ( Set< Grammeme > )grammemes );
    System.out.println( gs );
    List< WordForm > ret = lemma.getWordFormsWithGrammemes( Quantifier.EXACTLY, gs );
    return ret.isEmpty() ? null : ret.get( 0 );
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
    return form.contains( grammeme ) || lemma.getSuffixParadigm().grammemes.contains( grammeme );
  }
}