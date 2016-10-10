package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SuffixParadigm implements Serializable {
  private static final long serialVersionUID = 1L;
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
}