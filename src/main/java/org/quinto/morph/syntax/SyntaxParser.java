package org.quinto.morph.syntax;

import java.util.Iterator;
import org.quinto.dawg.util.Objects;
import org.quinto.morph.morphology.Dictionary;
import org.quinto.morph.syntaxengine.ParseException;
import org.quinto.morph.syntaxengine.Parser;
import org.quinto.morph.syntaxengine.Splitter;
import org.quinto.morph.syntaxengine.TreeNode;
import org.quinto.morph.syntaxengine.rules.Rule;
import org.quinto.morph.syntaxengine.util.Variants;

public class SyntaxParser extends Parser {
  public final Dictionary dictionary;
  
  public SyntaxParser( Dictionary dictionary ) {
    this.dictionary = dictionary;
    def( Rule.ROOT, ref( "sentence" ) );
    def( "sentence", ref( "like_noun" ) );
    def( "like_noun", or( ref( "main_gent" ), ref( "noun_adjf" ), hasTag( "NOUN" ) ) );
    def( "main_gent", filter( mapNode( seqUnordered( ref( "like_noun" ), filter( ref( "like_noun" ), node -> node.hasTag( "GENT" ) ) ),
      node -> node.setChildAsMain( 0 ).tagChildFromEnd( 0, "main_gent" ) ), node -> node.children.stream().filter( child -> child.hasTag( "main_gent" ) ).count() == 1L ) );
    def( "noun_adjf", mapNode( filter( seqUnordered( ref( "like_noun" ), ref( "like_adjf" ) ), node -> haveEqualTags( node.children, "NMBR", "CASE", "GNDR", "MS-F" ) ),
      node -> node.setChildAsMain( 0 ).tagChildFromEnd( 0, "noun_adjf" ) ) );
    def( "like_adjf", hasTag( "ADJF" ) );
  }
  
  private boolean haveEqualTags( Iterable< TreeNode > nodes, String... tags ) {
    if ( nodes == null )
      return true;
    Iterator< TreeNode > it = nodes.iterator();
    if ( it == null || !it.hasNext() )
      return true;
    TreeNode first = it.next();
    while ( it.hasNext() ) {
      TreeNode rest = it.next();
      for ( String tag : tags ) {
        if ( first.hasTag( tag ) != rest.hasTag( tag ) )
          return false;
        if ( !Objects.equals( first.tags.get( tag ), rest.tags.get( tag ) ) )
          return false;
      }
    }
    return true;
  }

  public Variants< TreeNode > parse( String sentence ) throws ParseException {
    return parse( dictionary.getWordFormsSequence( Splitter.split( sentence ) ) );
  }
}