package org.quinto.morph.morphology;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DictionaryTest {
  private static Dictionary dictionary;
  
  @BeforeClass
  public static void setUpClass() throws IOException, XMLStreamException, ParseException {
    try {
      dictionary = DictReader.readCached();
    } catch ( IOException | ClassNotFoundException e ) {
      dictionary = DictReader.readFromXml();
      DictReader.writeCached( dictionary );
    }
  }

  @Test
  public void dictionary() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "котики" );
    Assert.assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas )
      Assert.assertEquals( "котик", lemma.name );
  }

  @Test
  public void knownPrefix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "протокотики" );
    Assert.assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas )
      Assert.assertEquals( "протокотик", lemma.name );
    List< CompressedLemma > expected = dictionary.getLemmas( "котики" );
    for ( int i = 0; i < expected.size(); i++ )
      Assert.assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void unknownPrefix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "прокотики" );
    Assert.assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas )
      Assert.assertEquals( "прокотик", lemma.name );
    List< CompressedLemma > expected = dictionary.getLemmas( "котики" );
    for ( int i = 0; i < expected.size(); i++ )
      Assert.assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void knownSuffix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "тошки" );
    Assert.assertEquals( 1, lemmas.size() );
    for ( CompressedLemma lemma : lemmas )
      Assert.assertEquals( "тош", lemma.name );
    List< CompressedLemma > expected = dictionary.getLemmas( "бошки" );
    for ( int i = 0; i < expected.size(); i++ )
      Assert.assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }
}