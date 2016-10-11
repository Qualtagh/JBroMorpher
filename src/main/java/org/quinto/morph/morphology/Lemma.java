package org.quinto.morph.morphology;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Lemma {
  public int id;
  public String name;
  public Set< Grammeme > grammemes = new HashSet<>();
  public Map< Set< Grammeme >, String > forms = new LinkedHashMap<>();

  @Override
  public String toString() {
    return "Lemma{" + "id=" + id + ", name=" + name + ", grammemes=" + grammemes + ", forms=" + forms + '}';
  }
  
  public SuffixParadigm getSuffixParadigm() {
    char text[] = name.toCharArray();
    int len = text.length;
    for ( String form : forms.values() ) {
      char f[] = form.toCharArray();
      if ( len > f.length )
        len = f.length;
      for ( int i = 0; i < len; i++ ) {
        if ( text[ i ] != f[ i ] ) {
          len = i;
          break;
        }
      }
    }
    SuffixParadigm ret = new SuffixParadigm();
    for ( Map.Entry< Set< Grammeme >, String > form : forms.entrySet() ) {
      Set< Grammeme > grammemesSet = form.getKey();
      String suffix = form.getValue().substring( len );
      ret.forms.put( grammemesSet, suffix );
    }
    ret.shift = text.length - len;
    ret.grammemes = grammemes;
    return ret;
  }
}