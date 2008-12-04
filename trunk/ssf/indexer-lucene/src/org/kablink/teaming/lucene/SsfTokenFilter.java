/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.lucene;

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
