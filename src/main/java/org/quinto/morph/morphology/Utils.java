package org.quinto.morph.morphology;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
  public static String reverse( String s ) {
    return new StringBuilder( s ).reverse().toString();
  }
  
  public static < T > Stream< T > i2s( Iterable< T > it ) {
    return StreamSupport.stream( it.spliterator(), false );
  }
  
  public static < T > Iterable< T > s2i( Stream< T > s ) {
    return s::iterator;
  }
}