package org.quinto.morph.morphology;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Utils {
  public static String reverse( String s ) {
    return s == null ? null : new StringBuilder( s ).reverse().toString();
  }
  
  public static < T > Stream< T > i2s( Iterable< T > it ) {
    return it == null ? null : StreamSupport.stream( it.spliterator(), false );
  }
  
  public static < T > Iterable< T > s2i( Stream< T > s ) {
    return s == null ? null : s::iterator;
  }

  public static < T > T getFirstOrNull( Iterable< T > it ) {
    if ( it == null )
      return null;
    Iterator< T > i = it.iterator();
    if ( i == null || !i.hasNext() )
      return null;
    try {
      return i.next();
    } catch ( NoSuchElementException e ) {
      return null;
    }
  }
}