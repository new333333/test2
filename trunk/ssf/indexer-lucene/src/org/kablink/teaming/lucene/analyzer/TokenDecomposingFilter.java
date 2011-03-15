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
package org.kablink.teaming.lucene.analyzer;

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.kablink.util.StringUtil;

public class TokenDecomposingFilter extends TokenFilter {

	private Token[] tokens;
	private int index;
	
	public TokenDecomposingFilter(TokenStream input) {
		super(input);
	}

	public Token next() throws IOException {
		Token t;
		if(tokens != null && index < tokens.length) {
			// We have decomposed tokens and haven't returned them all yet.
			t = tokens[index++];
			if(index == tokens.length) {
				// The token being returned is the last one in the set of decomposed
				// tokens we save.
				tokens = null;
				index = 0;
			}
			return t;
		}
		else {
			// We don't have decomposed tokens. Let's get the next regular token.
			t = input.next();
			if(t != null) {
				// Get a list of decomposed tokens from the original token.
				tokens = getDecomposedTokens(t);
				index = 0;
				// Return the original
				return t;
			}
			else {
				// No more token
				return null;
			}
		}
	}
	
	private Token[] getDecomposedTokens(Token token) {
		Token[] dTokens = null;
		String term = token.termText();
		String[] dTerms = StringUtil.split(term, ".");
		if(dTerms != null && dTerms.length > 1) {
			dTokens = new Token[dTerms.length];
			for(int i=0; i<dTerms.length; i++) {
				// All of the decomposed tokens share the same location and character
				// offsets as the original token. Purely from technical point of view,
				// this is not correct, and can use re-design in a future release.
				dTokens[i] = new Token(dTerms[i], token.startOffset(), token.endOffset());
				dTokens[i].setPositionIncrement(0);
			}
		}
		return dTokens;
	}
}
