package org.kablink.teaming.lucene.analyzer;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.AttributeSource;
import org.tartarus.snowball.SnowballProgram;

/**
 * A filter that stems words using a Snowball-generated stemmer.
 *
 * Available stemmers are listed in {@link org.tartarus.snowball.ext}.
 */
public class VibeSnowballFilter extends TokenFilter {

  private SnowballProgram stemmer;

  private TermAttribute termAtt;
  
  private AttributeSource.State current;
  
  private PositionIncrementAttribute posIncrAtt;
  
  private boolean inAdditionToOriginal;
  
  private String stem;
  
  public VibeSnowballFilter(TokenStream input, SnowballProgram stemmer, boolean inAdditionToOriginal) {
    super(input);
    this.stemmer = stemmer;
    termAtt = (TermAttribute) addAttribute(TermAttribute.class);
    this.inAdditionToOriginal = inAdditionToOriginal;
    this.posIncrAtt = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);
  }

  /**
   * Construct the named stemming filter.
   *
   * Available stemmers are listed in {@link org.tartarus.snowball.ext}.
   * The name of a stemmer is the part of the class name before "Stemmer",
   * e.g., the stemmer in {@link org.tartarus.snowball.ext.EnglishStemmer} is named "English".
   *
   * @param in the input tokens to stem
   * @param name the name of a stemmer
   */
  public VibeSnowballFilter(TokenStream in, String name, boolean inAdditionToOriginal) {
    super(in);
    try {      
      Class stemClass = Class.forName("org.tartarus.snowball.ext." + name + "Stemmer");
      stemmer = (SnowballProgram) stemClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
    termAtt = (TermAttribute) addAttribute(TermAttribute.class);
    this.inAdditionToOriginal = inAdditionToOriginal;
    this.posIncrAtt = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);
  }

  	/** Returns the next input Token, after being stemmed */
  	public boolean incrementToken() throws IOException {
		if (inAdditionToOriginal) {
			if(stem != null) {
				restoreState(current);
				termAtt.setTermBuffer(stem);
				posIncrAtt.setPositionIncrement(0);
				stem = null;
				return true;
			}
			else { // no saved stem
				if(input.incrementToken()) {
					String originalTerm = termAtt.term();
					stemmer.setCurrent(originalTerm);
					stemmer.stem();
					String finalTerm = stemmer.getCurrent();
					// Don't bother updating, if it is unchanged.
					if (!originalTerm.equals(finalTerm)) {
						stem = finalTerm;
						current = captureState();
					}
					return true;					
				}
				else {
					return false;
				}
			}
		} else { // in place of original
			if (input.incrementToken()) {
				String originalTerm = termAtt.term();
				stemmer.setCurrent(originalTerm);
				stemmer.stem();
				String finalTerm = stemmer.getCurrent();
				// Don't bother updating, if it is unchanged.
				if (!originalTerm.equals(finalTerm))
					termAtt.setTermBuffer(finalTerm);
				return true;
			} else {
				return false;
			}
		}
	}
  
  /** @deprecated Will be removed in Lucene 3.0. This method is final, as it should
   * not be overridden. Delegates to the backwards compatibility layer. */
  public final Token next(final Token reusableToken) throws java.io.IOException {
    return super.next(reusableToken);
  }

  /** @deprecated Will be removed in Lucene 3.0. This method is final, as it should
   * not be overridden. Delegates to the backwards compatibility layer. */
  public final Token next() throws java.io.IOException {
    return super.next();
  }
}
