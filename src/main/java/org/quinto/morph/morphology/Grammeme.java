package org.quinto.morph.morphology;

import java.io.Serializable;

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
}