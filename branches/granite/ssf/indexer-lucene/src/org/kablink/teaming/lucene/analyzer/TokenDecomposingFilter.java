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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.AttributeSource;
import org.kablink.util.StringUtil;

public class TokenDecomposingFilter extends TokenFilter {

	//private Token[] tokens;
	private AttributeSource.State[] savedStates;
	private int index;
	
	private final TermAttribute termAtt = addAttribute(TermAttribute.class);
	private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

	public TokenDecomposingFilter(TokenStream input) {
		super(input);
	}

	public boolean incrementToken()
    throws IOException {
		AttributeSource.State state;
		if(savedStates != null && index < savedStates.length) {
			// We have decomposed tokens and haven't returned them all yet.
			state = savedStates[index++];
			if(index == savedStates.length) {
				// The token being returned is the last one in the set of decomposed
				// tokens we save.
				savedStates = null;
				index = 0;
			}
			restoreState(state);
			return true;
		}
		else {
			// We don't have decomposed tokens. Let's get the next regular token.
			if(input.incrementToken()) {
				state = captureState(); // Save current state, i.e., original token.
				// Get a list of decomposed tokens from the original token.
				savedStates = getDecomposedTokens();
				index = 0;
				// Return the original by restoring the saved state.
				restoreState(state);
				return true;
			}
			else {
				return false; // No more token
			}
		}
	}

	private AttributeSource.State[] getDecomposedTokens() {
		AttributeSource.State[] dStates = null;
		String term = termAtt.term();
		String[] dTerms = StringUtil.split(term, ".");
		if(dTerms != null && dTerms.length > 1) {
			dStates = new AttributeSource.State[dTerms.length];
			for(int i=0; i<dTerms.length; i++) {
				// All of the decomposed tokens share the same location and character
				// offsets as the original token. Purely from technical point of view,
				// this is not correct, and can use re-design in a future release.
				
				// Use current buffer (attributes) to hold the state of each decomposed token temporarily.
				termAtt.setTermBuffer(dTerms[i]);
				posIncrAtt.setPositionIncrement(0);
				
				// Copy current state into the array element. The state of current buffer will be
				// taken care of by the caller.
				dStates[i] = captureState();
			}
		}
		return dStates;
	}
}
