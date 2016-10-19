package org.quinto.morph.morphology;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.quinto.morph.syntaxengine.ParseException;

public class DictionaryReader {
  public static void writeCached( Dictionary dictionary ) throws IOException {
    writeCached( dictionary, "cached" );
  }
  
  public static void writeCached( Dictionary dictionary, String cachedFilePath ) throws IOException {
    try ( FileOutputStream fos = new FileOutputStream( cachedFilePath );
          GZIPOutputStream gos = new GZIPOutputStream( fos, 1024 * 1024 );
          ObjectOutputStream oos = new ObjectOutputStream( gos ) ) {
      oos.writeObject( dictionary );
      oos.flush();
    }
  }
  
  public static Dictionary readCached() throws IOException, ClassNotFoundException {
    return readCached( "cached" );
  }
  
  public static Dictionary readCached( String cachedFilePath ) throws IOException, ClassNotFoundException {
    try ( FileInputStream fis = new FileInputStream( cachedFilePath );
          GZIPInputStream gis = new GZIPInputStream( fis, 1024 * 1024 );
          ObjectInputStream ois = new ObjectInputStream( gis ) ) {
      return ( Dictionary )ois.readObject();
    }
  }
  
  public static Dictionary readFromXml() throws IOException, XMLStreamException, ParseException {
    try ( FileInputStream fis = new FileInputStream( "dict.opcorpora.xml" ) ) {
      return readFromXml( fis );
    } catch ( FileNotFoundException e ) {
      try ( FileInputStream fis = new FileInputStream( "dict.opcorpora.xml.bz2" ) ) {
        return readFromXml( fis );
      } catch ( FileNotFoundException ex ) {
        try ( FileInputStream fis = new FileInputStream( "dict.opcorpora.xml.zip" ) ) {
          return readFromXml( fis );
        } catch ( FileNotFoundException exc ) {
          try {
            return readFromXml( new URL( "http://opencorpora.org/files/export/dict/dict.opcorpora.xml.bz2" ) );
          } catch ( FileNotFoundException exce ) {
            return readFromXml( new URL( "http://opencorpora.org/files/export/dict/dict.opcorpora.xml.zip" ) );
          }
        }
      }
    }
  }
  
  public static Dictionary readFromXml( URL url ) throws IOException, XMLStreamException, ParseException {
    try ( InputStream is = url.openStream() ) {
      return readFromXml( is );
    }
  }
  
  public static Dictionary readFromXml( InputStream is ) throws IOException, XMLStreamException, ParseException {
    try ( BufferedInputStream bis = new BufferedInputStream( is, 1024 * 1024 ) ) {
      bis.mark( 1024 );
      try ( CompressorInputStream cis = new CompressorStreamFactory().createCompressorInputStream( bis ) ) {
        return readFromXmlRaw( cis );
      } catch ( CompressorException e ) {
      }
      bis.reset();
      try {
        ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream( bis );
        boolean ok;
        try {
          ais.getNextEntry();
          ok = true;
        } catch ( IOException e ) {
          ok = false;
          // Do not close stream on error.
        }
        if ( ok ) {
          try {
            return readFromXmlRaw( ais );
          } finally {
            ais.close();
          }
        }
      } catch ( ArchiveException e ) {
      }
      bis.reset();
      return readFromXmlRaw( bis );
    }
  }
  
  private static Dictionary readFromXmlRaw( InputStream is ) throws IOException, XMLStreamException, ParseException {
    Dictionary dictionary = new Dictionary();
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader( is, StandardCharsets.UTF_8.name() );
    try {
      List< String > hierarchy = new ArrayList<>();
      Object current = null;
      String formName = null;
      Set< Grammeme > grammemesSet = null;
      Map< SuffixParadigm, Integer > paradigms = new LinkedHashMap<>();
      ListValuedMap< String, Grammeme > grammemeParents = new ArrayListValuedHashMap<>();
      while ( reader.hasNext() ) {
        switch ( reader.getEventType() ) {
          case XMLStreamConstants.START_ELEMENT:
            QName name = reader.getName();
            String tag = name.getLocalPart();
            hierarchy.add( tag );
            String section = hierarchy.size() > 1 ? hierarchy.get( 1 ) : "";
            switch ( section ) {
              case "grammemes":
                Grammeme grammeme;
                switch ( tag ) {
                  case "grammeme":
                    current = grammeme = new Grammeme();
                    String parent = reader.getAttributeValue( "", "parent" );
                    if ( parent != null && !parent.isEmpty() )
                      grammemeParents.put( "ANim".equals( parent ) ? "ANIMG" : parent.toUpperCase(), grammeme );
                    break;
                }
                break;
              case "lemmata":
                Lemma lemma;
                switch ( tag ) {
                  case "lemma":
                    current = lemma = new Lemma();
                    String id = reader.getAttributeValue( "", "id" );
                    lemma.id = Integer.parseInt( id );
                    break;
                  default:
                    lemma = ( Lemma )current;
                }
                switch ( tag ) {
                  case "l":
                    String value = reader.getAttributeValue( "", "t" );
                    lemma.name = value;
                    grammemesSet = lemma.grammemes;
                    break;
                  case "f":
                    formName = reader.getAttributeValue( "", "t" );
                    grammemesSet = new HashSet<>();
                    break;
                  case "g":
                    value = reader.getAttributeValue( "", "v" );
                    grammeme = dictionary.grammemes.get( value.toUpperCase() );
                    grammemesSet.add( grammeme );
                    break;
                }
                break;
            }
            break;
          case XMLStreamConstants.END_ELEMENT:
            section = hierarchy.size() > 1 ? hierarchy.get( 1 ) : "";
            tag = hierarchy.remove( hierarchy.size() - 1 );
            switch ( section ) {
              case "lemmata":
                Lemma lemma = ( Lemma )current;
                switch ( tag ) {
                  case "f":
                    lemma.forms.put( grammemesSet, formName );
                    break;
                  case "lemma":
                    SuffixParadigm paradigm = lemma.getSuffixParadigm();
                    Integer pos = paradigms.get( paradigm );
                    if ( pos == null ) {
                      paradigms.put( paradigm, pos = paradigms.size() );
                      dictionary.allParadigms.add( paradigm );
                    }
                    CompressedLemma compressedLemma = new CompressedLemma( lemma.id, lemma.name.substring( 0, lemma.name.length() - paradigm.shift ), pos, dictionary );
                    dictionary.lemmas.put( lemma.id, compressedLemma );
                    break;
                }
                break;
              case "grammemes":
                switch ( tag ) {
                  case "grammemes":
                    for ( Map.Entry< String, Grammeme > e : grammemeParents.entries() )
                      e.getValue().parent = dictionary.grammemes.get( e.getKey() );
                    break;
                }
                break;
            }
            if ( section.equals( tag ) )
              current = null;
            break;
          case XMLStreamConstants.CHARACTERS:
            section = hierarchy.size() > 1 ? hierarchy.get( 1 ) : "";
            tag = hierarchy.get( hierarchy.size() - 1 );
            String value = reader.getText();
            switch ( section ) {
              case "grammemes":
                Grammeme grammeme = ( Grammeme )current;
                switch ( tag ) {
                  case "name":
                    grammeme.name = "ANim".equals( value ) ? "ANIMG" : value.toUpperCase();
                    if ( dictionary.grammemes.put( grammeme.name, grammeme ) != null )
                      throw new ParseException( "Duplicate gemmeme found: " + grammeme.name );
                    break;
                  case "alias":
                    grammeme.alias = value;
                    break;
                  case "description":
                    grammeme.description = value;
                    break;
                }
                break;
            }
            break;
        }
        reader.next();
      }
    } finally {
      reader.close();
    }
    dictionary.init();
    return dictionary;
  }
  
  public static Dictionary read() throws IOException, XMLStreamException, ParseException {
    Dictionary dictionary;
    try {
      dictionary = readCached();
    } catch ( IOException | ClassNotFoundException e ) {
      dictionary = readFromXml();
      writeCached( dictionary );
    }
    return dictionary;
  }
}