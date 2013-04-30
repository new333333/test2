/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.lucene.util;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;


/**
 *  Find each token.  If there's uppercase chars in the token, then create two tokens at the same location,
 *  one that has all lowercase chars, and the original.
 *  
 * @author klein
 *
 */
public class SsfTokenFilter extends TokenFilter
{
  private AttributeSource.State _lowercaseToken;
  
  // Lucene allows the same field to be added multiple times to a document.  When 
  // this happens, we need to separate the fields so a phrase search won't match 
  // the end of the first concatenated to the beginning of the second. Simply going 
  // to add an empty location before each new field.
  private boolean _newField;
  
  private CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);

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
  @Override
  public boolean incrementToken() throws IOException {
	    // If an alternate version exists...
	    if (_lowercaseToken != null)
	    {
	      // We'll return it.
	      restoreState(_lowercaseToken);
	      
	      // And signal that we've already returned it.
	      _lowercaseToken = null;

	      return true;
	    }
	    
	    // If there isn't an alternative token, we'll get the next one.
		  if(input.incrementToken()) {
			    // get a lowercase version
			    _lowercaseToken = getLowercaseToken();
			    
			    if (_newField)
			    {
			    	posIncrAtt.setPositionIncrement(2);
			    	
			    	// Done for this field
			    	_newField = false;
			    }
			    // This returns the original.
			    return true;
		  }
		  else {
			  return false;
		  }
  }

  // Get a lowercase instance of a token
  // @param token Token to process
  // @return lowercase version of <tt>token</tt>, or <tt>null</tt> if <tt>token</tt> has no uppercase chars. 
  private AttributeSource.State getLowercaseToken()
  {
	  AttributeSource.State origToken = null;;
	  
      char termBuffer[] = termAtt.buffer();
      final int length = termAtt.length();
      char c;
      for(int i = 0; i < length; i++) {
          c = termBuffer[i];    
          if (Character.isUpperCase(c))
          {
        	  // We encountered a upper case character, which means that we will be generating a lower case token.
        	  if(origToken == null) {
        		  // Save original token temporarily if not done already so.
        		  origToken = captureState();
        	  }
        	  termBuffer[i] = Character.toLowerCase(c);
          }
      }
      if(origToken != null) {
    	  // We have a lower-case version of the token. 
    	  // It should be at the same position as the original one.
    	  posIncrAtt.setPositionIncrement(0);
    	  // Capture it into a state that we can return.
    	  AttributeSource.State lowercaseToken = captureState();
    	  // Restore original token.
    	  restoreState(origToken);
    	  return lowercaseToken;
      }
      else {
    	  // We don't have a lower-case version of the token.
    	  return null;
      }
  }

}
