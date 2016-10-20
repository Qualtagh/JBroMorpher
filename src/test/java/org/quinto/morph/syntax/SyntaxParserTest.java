package org.quinto.morph.syntax;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.quinto.morph.morphology.Dictionary;
import org.quinto.morph.morphology.DictionaryReader;
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
    dictionary = DictionaryReader.read();
    parser = new SyntaxParser( dictionary );
  }
  
  @Test
  public void mainGent() throws ParseException {
    Sequence< String > tokens = Splitter.split( "одуванчик мыши" );
    assertEquals( new Sequence<>( "одуванчик", "мыши" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "одуванчик", "мыши" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    Variants< TreeNode > res = parser.parse( input );
    assertEquals( new Variants<>( input.get( 0 ).stream().filter( node -> node.hasTag( "NOMN" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get() ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "ACCS" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get() ) ), res );
  }
  
  @Test
  public void gentAdjf() throws ParseException {
    Sequence< String > tokens = Splitter.split( "мыши полевой" );
    assertEquals( new Sequence<>( "мыши", "полевой" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "мыши", "полевой" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    Variants< TreeNode > res = parser.parse( input );
    assertEquals( new Variants<>( input.get( 0 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get() ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "DATV" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "DATV" ) ).findFirst().get() ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "LOCT" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "LOCT" ) ).findFirst().get() ) ), res );
  }
  
  @Test
  public void mainAdjf() throws ParseException {
    Sequence< String > tokens = Splitter.split( "одуванчик полевой" );
    assertEquals( new Sequence<>( "одуванчик", "полевой" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "одуванчик", "полевой" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    Variants< TreeNode > res = parser.parse( input );
    assertEquals( new Variants<>( input.get( 0 ).stream().filter( node -> node.hasTag( "NOMN" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "NOMN" ) ).findFirst().get() ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "ACCS" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "ACCS" ) ).findFirst().get() ) ), res );
  }
  
  @Test
  public void mainGentAdjf() throws ParseException {
    Sequence< String > tokens = Splitter.split( "одуванчик мыши полевой" );
    assertEquals( new Sequence<>( "одуванчик", "мыши", "полевой" ), tokens );
    Sequence< Variants< TreeNode > > input = dictionary.getWordFormsSequence( tokens );
    assertEquals( Arrays.asList( "одуванчик", "мыши", "полевой" ), input.stream().flatMap( tokenForms -> tokenForms.stream().map( node -> node.tags.get( "word" ) ).distinct() ).collect( Collectors.toList() ) );
    Variants< TreeNode > res = parser.parse( input );
    assertEquals( new Variants<>( input.get( 0 ).stream().filter( node -> node.hasTag( "NOMN" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get(),
                       input.get( 2 ).stream().filter( node -> node.hasTag( "NOMN" ) ).findFirst().get() ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "ACCS" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get(),
                       input.get( 2 ).stream().filter( node -> node.hasTag( "ACCS" ) ).findFirst().get() ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "NOMN" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get()
                         .withChildren( input.get( 2 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get() ) ),
      input.get( 0 ).stream().filter( node -> node.hasTag( "ACCS" ) ).findFirst().get()
        .withChildren( input.get( 1 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get()
                         .withChildren( input.get( 2 ).stream().filter( node -> node.hasTag( "GENT" ) ).findFirst().get() ) ) ), res );
  }
}