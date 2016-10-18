package org.quinto.morph.morphology;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.xml.stream.XMLStreamException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;

public class DictionaryTest {
  private static Dictionary dictionary;
  
  @BeforeClass
  public static void setUpClass() throws IOException, XMLStreamException, ParseException {
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel( Level.ALL );
    Logger logger = Logger.getLogger( Dictionary.class.getName() );
    logger.addHandler( handler );
    logger.setLevel( Level.ALL );
    dictionary = DictionaryReader.read();
  }

  @Test
  public void dictionary() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "котики" );
    assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      assertEquals( "котик", lemma.name );
      assertEquals( "котика", lemma.getWordWithGrammemes( new HashSet<>( Arrays.asList( new Grammeme( "GENT" ), new Grammeme( "SING" ) ) ) ) );
      assertEquals( "котика", lemma.getWordWithGrammemes( Arrays.asList( new Grammeme( "GENT" ), new Grammeme( "SING" ) ) ) );
      assertEquals( "котика", lemma.getWordWithGrammemes( new Grammeme( "GENT" ), new Grammeme( "SING" ) ) );
      assertEquals( "котика", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      assertEquals( "котика", lemma.getWordWithGrammemes( Arrays.asList( "GENT", "SING" ) ) );
      assertEquals( "котика", lemma.getWordWithGrammemes( new HashSet<>( Arrays.asList( "GENT", "SING" ) ) ) );
      assertEquals( 2, lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, "GENT" ).size() );
      assertEquals( 6, lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, "SING" ).size() );
      List< WordForm > forms = lemma.getWordForms( "котик" );
      for ( WordForm form : forms ) {
        assertEquals( "котик", form.getWord() );
        assertEquals( "котика", form.getWordWithGrammemes( "GENT" ) );
      }
    }
  }

  @Test
  public void knownPrefix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "протокотики" );
    assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      assertEquals( "протокотик", lemma.name );
      assertEquals( "протокотика", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      List< WordForm > forms = lemma.getWordForms( "протокотик" );
      for ( WordForm form : forms ) {
        assertEquals( "протокотик", form.getWord() );
        assertEquals( "протокотика", form.getWordWithGrammemes( "GENT" ) );
      }
    }
    List< CompressedLemma > expected = dictionary.getLemmas( "котики" );
    for ( int i = 0; i < expected.size(); i++ )
      assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void unknownPrefix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "прокотики" );
    assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      assertEquals( "прокотик", lemma.name );
      assertEquals( "прокотика", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      List< WordForm > forms = lemma.getWordForms( "прокотик" );
      for ( WordForm form : forms ) {
        assertEquals( "прокотик", form.getWord() );
        assertEquals( "прокотика", form.getWordWithGrammemes( "GENT" ) );
      }
    }
    List< CompressedLemma > expected = dictionary.getLemmas( "котики" );
    for ( int i = 0; i < expected.size(); i++ )
      assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void knownSuffix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "тошки" );
    assertEquals( 1, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      assertEquals( "тош", lemma.name );
      assertEquals( "тошки", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      List< WordForm > forms = lemma.getWordForms( "тошка" );
      for ( WordForm form : forms ) {
        assertEquals( "тошка", form.getWord() );
        assertEquals( "тошки", form.getWordWithGrammemes( "GENT" ) );
      }
    }
    List< CompressedLemma > expected = dictionary.getLemmas( "бошки" );
    for ( int i = 0; i < expected.size(); i++ )
      assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void dictionarySwitchingVowel() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "ежи" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.canProduce( "ёж" ) ) {
        assertEquals( "", lemma.name );
        assertEquals( "ежа", lemma.getWordWithGrammemes( "GENT", "SING" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "ёж" );
        for ( WordForm form : forms ) {
          assertEquals( "ёж", form.getWord() );
          assertEquals( "ежа", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    assertEquals( 2, size );
  }

  @Test
  public void dictionarySwitchingRoot() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "люди" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.canProduce( "человек" ) ) {
        assertEquals( "", lemma.name );
        assertEquals( "человека", lemma.getWordWithGrammemes( "GENT", "SING" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "человек" );
        for ( WordForm form : forms ) {
          if ( !form.hasGrammeme( "SING" ) )
            continue;
          assertEquals( "человек", form.getWord() );
          assertEquals( "человека", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    assertEquals( 1, size );
  }

  @Test
  public void knownPrefixSwitchingRoot() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "суперлюди" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.canProduce( "суперчеловек" ) ) {
        assertEquals( "супер", lemma.name );
        assertEquals( "суперчеловека", lemma.getWordWithGrammemes( "GENT", "SING" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "суперчеловек" );
        for ( WordForm form : forms ) {
          if ( !form.hasGrammeme( "SING" ) )
            continue;
          assertEquals( "суперчеловек", form.getWord() );
          assertEquals( "суперчеловека", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    assertEquals( 1, size );
  }

  @Test
  public void knownPrefixAdjective() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "мегакрасный" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.hasGrammeme( "ADJF" ) ) {
        assertTrue( lemma.hasGrammeme( "ANIMG" ) );
        assertTrue( lemma.hasGrammeme( "POST" ) );
        assertFalse( lemma.hasGrammeme( "TENS" ) );
        assertEquals( "мегакрасн", lemma.name );
        assertEquals( "мегакрасной", lemma.getWordWithGrammemes( "GENT", "SING", "FEMN" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "мегакрасный" );
        for ( WordForm form : forms ) {
          assertEquals( "мегакрасный", form.getWord() );
          assertEquals( 1, form.getWordsWithGrammemes( "GENT" ).size() );
          assertEquals( "мегакрасного", form.getWordWithGrammemes( "GENT" ) );
          assertFalse( form.getWordFormWithGrammemes( "GENT" ).hasGrammeme( "ANIMG" ) );
          if ( form.hasGrammeme( "ACCS" ) ) {
            assertTrue( form.hasGrammeme( "ANIMG" ) );
            assertEquals( 1, form.getWordFormsWithGrammemes( "ACCS" ).size() );
            assertEquals( "мегакрасный", form.getWordWithGrammemes( "ACCS" ) );
          } else {
            assertFalse( form.hasGrammeme( "ANIMG" ) );
            assertEquals( 2, form.getWordFormsWithGrammemes( "ACCS" ).size() );
            assertEquals( new HashSet<>( Arrays.asList( "мегакрасный", "мегакрасного" ) ), new HashSet<>( form.getWordsWithGrammemes( "ACCS" ) ) );
          }
        }
        assertEquals( 4, lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, "GENT" ).size() );
        assertEquals( new HashSet<>( Arrays.asList( "мегакрасного", "мегакрасной", "мегакрасных" ) ),
          lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, "GENT" ).stream().map( f -> f.getWord() ).collect( Collectors.toSet() ) );
        assertEquals( 6, lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, "ACCS" ).size() );
        assertEquals( new HashSet<>( Arrays.asList( "мегакрасного", "мегакрасный", "мегакрасную", "мегакрасное", "мегакрасных", "мегакрасные" ) ),
          lemma.getWordFormsWithGrammemes( Quantifier.CONTAINS_EVERY, "ACCS" ).stream().map( f -> f.getWord() ).collect( Collectors.toSet() ) );
      }
    }
    assertEquals( 1, size );
  }

  @Test
  public void unknownPrefixVerb() {
    for ( WordForm form : dictionary.getWordForms( "броварю" ) ) {
      if ( !form.hasGrammeme( "VERB" ) )
        continue;
      assertTrue( form.hasGrammeme( "SING" ) );
      assertTrue( form.hasGrammeme( "PRES" ) );
      assertTrue( form.hasGrammeme( "1PER" ) );
      assertEquals( "броварим", form.getWordWithGrammemes( "PLUR" ) );
      assertEquals( "броварите", form.getWordWithGrammemes( "PLUR", "2PER" ) );
      assertEquals( "броварил", form.getWordWithGrammemes( "PAST" ) );
      assertEquals( "броварили", form.getWordWithGrammemes( "PLUR", "PAST" ) );
      assertEquals( "броваришь", form.getWordWithGrammemes( "2PER" ) );
      assertFalse( form.getWordFormWithGrammemes( "PAST" ).hasGrammeme( "PERS" ) );
      assertEquals( null, form.getWordWithGrammemes( "PAST", "2PER" ) );
      assertEquals( "броварила", form.getWordWithGrammemes( "PAST", "FEMN" ) );
      assertEquals( "броварил", form.getWordWithGrammemes( "PAST", "MASC" ) );
    }
  }

  @Test
  public void knownSuffixVerb() {
    for ( WordForm form : dictionary.getWordForms( "крокожу" ) ) {
      if ( !form.hasGrammeme( "VERB" ) )
        continue;
      assertEquals( "крокодил", form.getWordWithGrammemes( "PAST" ) );
    }
  }
}