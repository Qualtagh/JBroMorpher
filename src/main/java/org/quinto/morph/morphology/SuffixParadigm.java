package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SuffixParadigm implements Serializable {
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
  public Set< Grammeme > grammemes = new HashSet<>();
  public Map< Set< Grammeme >, String > forms = new LinkedHashMap<>();
  public int shift;

  @Override
  public String toString() {
    return "SuffixParadigm{" + "forms=" + forms.values() + ", shift=" + shift + '}';
  }

  @Override
  public int hashCode() {
    int ret = forms.hashCode() * 31 + shift;
    ret = ret * 31 + grammemes.hashCode();
    return ret;
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this )
      return true;
    if ( !( obj instanceof SuffixParadigm ) )
      return false;
    SuffixParadigm o = ( SuffixParadigm )obj;
    return shift == o.shift && grammemes.equals( o.grammemes ) && forms.equals( o.forms );
  }
  
  public boolean isMorphablePOS() {
    for ( Grammeme grammeme : grammemes )
      if ( MORPHABLE.contains( grammeme ) )
        return true;
    return false;
  }
}