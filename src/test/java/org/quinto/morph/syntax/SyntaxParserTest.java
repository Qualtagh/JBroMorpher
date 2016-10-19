package org.quinto.morph.syntax;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.quinto.morph.morphology.Dictionary;
import org.quinto.morph.morphology.DictionaryReader;
import org.quinto.morph.morphology.WordForm;
import org.quinto.morph.syntaxengine.ParseException;
import org.quinto.morph.syntaxengine.Splitter;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.util.Sequence;
import org.quinto.morph.syntaxengine.util.Variants;

public class SyntaxParserTest {
  private static SyntaxParser parser;
  private static Dictionary dictionary;
  
  @BeforeClass
  public static void setUpClass() throws IOException, XMLStreamException, ParseException {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel( Level.ALL );
    Logger logger = Logger.getLogger( Dictionary.class.getName() );
    logger.addHandler( handler );
    logger.setLevel( Level.ALL );
    parser = new SyntaxParser();
    dictionary = DictionaryReader.read();
  }
  
  @Test
  public void mainGent() throws ParseException {
    Sequence< String > tokens = Splitter.split( "одуванчик мыши" );
    assertEquals( new Sequence<>( "одуванчик", "мыши" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "одуванчик", "мыши" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    System.out.println( input );
    System.out.println( "parsing" );
    System.out.println( parser.parse( input ) );
    System.out.println( "ok" );
    input.addAll( dictionary.getWordFormsSequence( Splitter.split( "полевой" ) ) );
    System.out.println( input );
    System.out.println( "parsing" );
    System.out.println( parser.parse( input ) );
    System.out.println( "ok" );
    throw new ParseException( "ok" );
  }
  
  @Test
  public void gentAdjf() throws ParseException {
    Sequence< String > tokens = Splitter.split( "мыши полевой" );
    assertEquals( new Sequence<>( "мыши", "полевой" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "мыши", "полевой" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    System.out.println( input );
    System.out.println( "parsing" );
    System.out.println( parser.parse( input ) );
    System.out.println( "ok" );
    throw new ParseException( "ok" );
  }
  
  @Test
  public void mainAdjf() throws ParseException {
    Sequence< String > tokens = Splitter.split( "одуванчик полевой" );
    assertEquals( new Sequence<>( "одуванчик", "полевой" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "одуванчик", "полевой" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    System.out.println( input );
    System.out.println( "parsing" );
    System.out.println( parser.parse( input ) );
    System.out.println( "ok" );
    throw new ParseException( "ok" );
  }
  
  @Test
  public void mainGentAdjf() throws ParseException {
    Sequence< String > tokens = Splitter.split( "одуванчик мыши полевой" );
    assertEquals( new Sequence<>( "одуванчик", "мыши", "полевой" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "одуванчик", "мыши", "полевой" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    System.out.println( input );
    System.out.println( "parsing" );
    System.out.println( parser.parse( input ) );
    System.out.println( "ok" );
    throw new ParseException( "ok" );
  }
}