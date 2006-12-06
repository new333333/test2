package com.sitescape.ef.lucene;

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
  private Token _alternateToken;
  
  public SsfTokenFilter(TokenStream input)
  {
    super(input);
  
    _alternateToken = null;
  }

  /**
   * Returns each token found as well as a lowercase version of the token if the original token contains any uppercase characters.
   * The lowercase version of the token is stored in the same position as the original. 
   */
  public Token next() throws IOException
  {
    Token t;
    
    // If an alternate version exists...
    if (_alternateToken != null)
    {
      // We'll return it.
      t = _alternateToken;
      
      // And signal that we've already returned it.
      _alternateToken = null;

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

//    System.out.println("Token: " + token.termText() + " startOffset: " + token.startOffset() + " positionIncrement: " + token.getPositionIncrement());

    // See if the current token needs an alternate.
    _alternateToken = getAlternateToken(token);
    
    // But return the original for now.
    return token;
  }

  // Get an alternate version of a token, if necessary.
  // @param token Token to process
  // @return lowercase version of <tt>token</tt>, or <tt>null</tt> if <tt>token</tt> has not uppercase characters. 
  private Token getAlternateToken(Token token)
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
