/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.lucene;

import java.io.IOException;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;


/**
 *  Find each token.  If there's uppercase chars in the token, then create two tokens at the same location,
 *  one that has all lowercase chars, and the original.
 *  
 * @author klein
 *
 */
public class SsfTokenFilter extends TokenFilter
{
  private Token _lowercaseToken;
  
  // Lucene allows the same field to be added multiple times to a document.  When 
  // this happens, we need to separate the fields so a phrase search won't match 
  // the end of the first concatenated to the beginning of the second. Simply going 
  // to add an empty location before each new field.
  private boolean _newField;
  
  public SsfTokenFilter(TokenStream input)
  {
    super(input);
    _newField = true;
    _lowercaseToken = null;
  }

  /**
   * Returns each token found as well as a lowercase version of the token if the original token contains any uppercase characters.
   * The lowercase version of the token is stored in the same position as the original. 
   */
  public Token next() throws IOException
  {
    Token t;
    
    // If an alternate version exists...
    if (_lowercaseToken != null)
    {
      // We'll return it.
      t = _lowercaseToken;
      
      // And signal that we've already returned it.
      _lowercaseToken = null;

      return t;
    }
    
    // If there isn't an alternative token, we'll get the next one.
    Token token = input.next();
    
    // No more...
    if (token == null)
    {
      // We're done
      return null;
    }


    // get a lowercase version
    _lowercaseToken = getLowercaseToken(token);
    
    if (_newField)
    {
    	token.setPositionIncrement(2);
    	
    	// Done for this field
    	_newField = false;
    }
    // Return the original.
    return token;
  }

  // Get a lowercase instance of a token
  // @param token Token to process
  // @return lowercase version of <tt>token</tt>, or <tt>null</tt> if <tt>token</tt> has no uppercase chars. 
  private Token getLowercaseToken(Token token)
  {
    Token newToken;
    String term = token.termText();
    char c;
    
    for (int i=0; i < term.length(); ++i)
    {
      c = term.charAt(i);
      
      if (Character.isUpperCase(c))
      {
        newToken = new Token(term.toLowerCase(), token.startOffset(), token.endOffset());
        newToken.setPositionIncrement(0);
        
        return newToken;
      }
    }
    
    return (Token)null;
  }
}
