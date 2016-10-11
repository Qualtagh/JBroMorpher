package org.quinto.morph.morphology;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class Grammeme implements Serializable {
  private static final long serialVersionUID = 1L;
  public Grammeme parent;
  public String name;
  public String alias;
  public String description;

  public Grammeme() {
  }

  public Grammeme( String name ) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Grammeme{" + "parent=" + parent + ", name=" + name + ", alias=" + alias + ", description=" + description + '}';
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public boolean equals( Object obj ) {
    if ( obj == this )
      return true;
    if ( !( obj instanceof Grammeme ) )
      return false;
    return name.equals( ( ( Grammeme )obj ).name );
  }
  
  public static Set< Grammeme > toSet( String... grammemes ) {
    return grammemes == null || grammemes.length == 0 ? Collections.EMPTY_SET : Arrays.stream( grammemes ).map( g -> new Grammeme( g.toUpperCase() ) ).collect( Collectors.toSet() );
  }
  
  public static Set< Grammeme > toSet( Grammeme... grammemes ) {
    return grammemes == null || grammemes.length == 0 ? Collections.EMPTY_SET : Arrays.stream( grammemes ).collect( Collectors.toSet() );
  }
  
  public static Set< Grammeme > toSet( Iterable< ? > grammemes ) {
    return grammemes == null || !grammemes.iterator().hasNext() ? Collections.EMPTY_SET :
      grammemes instanceof Set && grammemes.iterator().next() instanceof Grammeme ? ( Set< Grammeme > )grammemes :
      Utils.i2s( grammemes ).map( g -> g instanceof String ? new Grammeme( ( String )g ) : ( Grammeme )g ).collect( Collectors.toSet() );
  }

  public Grammeme getMaxParent() {
    Grammeme ret = this;
    while ( ret.parent != null )
      ret = ret.parent;
    return ret;
  }
}