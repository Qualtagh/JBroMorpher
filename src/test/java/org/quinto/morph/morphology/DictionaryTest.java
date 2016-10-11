package org.quinto.morph.morphology;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class DictionaryTest {
  private static Dictionary dictionary;
  
  @BeforeClass
  public static void setUpClass() throws IOException, XMLStreamException, ParseException {
    dictionary = DictionaryReader.read();
  }

  @Test
  public void dictionary() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "котики" );
    Assert.assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      Assert.assertEquals( "котик", lemma.name );
      Assert.assertEquals( "котика", lemma.getWordWithGrammemes( new HashSet<>( Arrays.asList( new Grammeme( "GENT" ), new Grammeme( "SING" ) ) ) ) );
      Assert.assertEquals( "котика", lemma.getWordWithGrammemes( Arrays.asList( new Grammeme( "GENT" ), new Grammeme( "SING" ) ) ) );
      Assert.assertEquals( "котика", lemma.getWordWithGrammemes( new Grammeme( "GENT" ), new Grammeme( "SING" ) ) );
      Assert.assertEquals( "котика", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      Assert.assertEquals( "котика", lemma.getWordWithGrammemes( Arrays.asList( "GENT", "SING" ) ) );
      Assert.assertEquals( "котика", lemma.getWordWithGrammemes( new HashSet<>( Arrays.asList( "GENT", "SING" ) ) ) );
      Assert.assertEquals( 2, lemma.getWordFormsWithGrammemes( Quantifier.EVERY, "GENT" ).size() );
      Assert.assertEquals( 6, lemma.getWordFormsWithGrammemes( Quantifier.EVERY, "SING" ).size() );
      List< WordForm > forms = lemma.getWordForms( "котик" );
      for ( WordForm form : forms ) {
        Assert.assertEquals( "котик", form.getWord() );
        Assert.assertEquals( "котика", form.getWordWithGrammemes( "GENT" ) );
      }
    }
  }

  @Test
  public void knownPrefix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "протокотики" );
    Assert.assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      Assert.assertEquals( "протокотик", lemma.name );
      Assert.assertEquals( "протокотика", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      List< WordForm > forms = lemma.getWordForms( "протокотик" );
      for ( WordForm form : forms ) {
        Assert.assertEquals( "протокотик", form.getWord() );
        Assert.assertEquals( "протокотика", form.getWordWithGrammemes( "GENT" ) );
      }
    }
    List< CompressedLemma > expected = dictionary.getLemmas( "котики" );
    for ( int i = 0; i < expected.size(); i++ )
      Assert.assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void unknownPrefix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "прокотики" );
    Assert.assertEquals( 2, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      Assert.assertEquals( "прокотик", lemma.name );
      Assert.assertEquals( "прокотика", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      List< WordForm > forms = lemma.getWordForms( "прокотик" );
      for ( WordForm form : forms ) {
        Assert.assertEquals( "прокотик", form.getWord() );
        Assert.assertEquals( "прокотика", form.getWordWithGrammemes( "GENT" ) );
      }
    }
    List< CompressedLemma > expected = dictionary.getLemmas( "котики" );
    for ( int i = 0; i < expected.size(); i++ )
      Assert.assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void knownSuffix() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "тошки" );
    Assert.assertEquals( 1, lemmas.size() );
    for ( CompressedLemma lemma : lemmas ) {
      Assert.assertEquals( "тош", lemma.name );
      Assert.assertEquals( "тошки", lemma.getWordWithGrammemes( "GENT", "SING" ) );
      List< WordForm > forms = lemma.getWordForms( "тошка" );
      for ( WordForm form : forms ) {
        Assert.assertEquals( "тошка", form.getWord() );
        Assert.assertEquals( "тошки", form.getWordWithGrammemes( "GENT" ) );
      }
    }
    List< CompressedLemma > expected = dictionary.getLemmas( "бошки" );
    for ( int i = 0; i < expected.size(); i++ )
      Assert.assertEquals( expected.get( i ).paradigmIdx, lemmas.get( i ).paradigmIdx );
  }

  @Test
  public void dictionarySwitchingVowel() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "ежи" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.canProduce( "ёж" ) ) {
        Assert.assertEquals( "", lemma.name );
        Assert.assertEquals( "ежа", lemma.getWordWithGrammemes( "GENT", "SING" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "ёж" );
        for ( WordForm form : forms ) {
          Assert.assertEquals( "ёж", form.getWord() );
          Assert.assertEquals( "ежа", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    Assert.assertEquals( 2, size );
  }

  @Test
  public void dictionarySwitchingRoot() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "люди" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.canProduce( "человек" ) ) {
        Assert.assertEquals( "", lemma.name );
        Assert.assertEquals( "человека", lemma.getWordWithGrammemes( "GENT", "SING" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "человек" );
        for ( WordForm form : forms ) {
          if ( !form.hasGrammeme( "SING" ) )
            continue;
          Assert.assertEquals( "человек", form.getWord() );
          Assert.assertEquals( "человека", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    Assert.assertEquals( 1, size );
  }

  @Test
  public void knownPrefixSwitchingRoot() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "суперлюди" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.canProduce( "суперчеловек" ) ) {
        Assert.assertEquals( "супер", lemma.name );
        Assert.assertEquals( "суперчеловека", lemma.getWordWithGrammemes( "GENT", "SING" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "суперчеловек" );
        for ( WordForm form : forms ) {
          if ( !form.hasGrammeme( "SING" ) )
            continue;
          Assert.assertEquals( "суперчеловек", form.getWord() );
          Assert.assertEquals( "суперчеловека", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    Assert.assertEquals( 1, size );
  }

  @Test
  public void knownPrefixAdjective() {
    List< CompressedLemma > lemmas = dictionary.getLemmas( "мегакрасный" );
    int size = 0;
    for ( CompressedLemma lemma : lemmas ) {
      if ( lemma.hasGrammeme( "ADJF" ) ) {
        Assert.assertEquals( "мегакрасн", lemma.name );
        Assert.assertEquals( "мегакрасной", lemma.getWordWithGrammemes( "GENT", "SING", "FEMN" ) );
        size++;
        List< WordForm > forms = lemma.getWordForms( "мегакрасный" );
        for ( WordForm form : forms ) {
          System.out.println( form );
          Assert.assertEquals( "мегакрасный", form.getWord() );
          System.out.println( "123" );
          //Assert.assertEquals( "мегакрасного", form.getWordWithGrammemes( "GENT" ) );
        }
      }
    }
    Assert.assertEquals( 1, size );
  }
}