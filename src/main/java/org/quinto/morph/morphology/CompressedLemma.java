package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompressedLemma implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final Set< Grammeme > MORPHABLE = new HashSet<>( Arrays.asList(
    new Grammeme( "NOUN" ),
    new Grammeme( "ADJF" ),
    new Grammeme( "ADJS" ),
    new Grammeme( "COMP" ),
    new Grammeme( "VERB" ),
    new Grammeme( "INFN" ),
    new Grammeme( "PRTF" ),
    new Grammeme( "PRTS" ),
    new Grammeme( "GRND" ),
    new Grammeme( "ADVB" )
  ) );
  public int id;
  public String name;
  public int paradigmIdx;
  public List< SuffixParadigm > paradigms;
  
  public CompressedLemma( int id, String name, int paradigmIdx, List< SuffixParadigm > paradigms ) {
    this.id = id;
    this.name = name;
    this.paradigmIdx = paradigmIdx;
    this.paradigms = paradigms;
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
    return paradigms.get( paradigmIdx );
  }
  
  public boolean canProduce( String word ) {
    return !getWordForms( word ).isEmpty();
  }
  
  public List< Set< Grammeme > > getWordForms( String word ) {
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

  public boolean isMorphable() {
    for ( Grammeme grammeme : getSuffixParadigm().grammemes )
      if ( MORPHABLE.contains( grammeme ) )
        return true;
    return false;
  }
}