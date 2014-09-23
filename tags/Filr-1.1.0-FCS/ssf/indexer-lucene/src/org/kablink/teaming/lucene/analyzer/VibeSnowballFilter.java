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

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.KeywordAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tr.TurkishLowerCaseFilter; // javadoc @link
import org.apache.lucene.analysis.LowerCaseFilter; // javadoc @link
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;
import org.tartarus.snowball.SnowballProgram;

/**
 * A filter that stems words using a Snowball-generated stemmer.
 *
 * Available stemmers are listed in {@link org.tartarus.snowball.ext}.
 * <p><b>NOTE</b>: SnowballFilter expects lowercased text.
 * <ul>
 *  <li>For the Turkish language, see {@link TurkishLowerCaseFilter}.
 *  <li>For other languages, see {@link LowerCaseFilter}.
 * </ul>
 * </p>
 */
public final class VibeSnowballFilter extends TokenFilter {

  private final SnowballProgram stemmer;

  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final KeywordAttribute keywordAttr = addAttribute(KeywordAttribute.class);
  
  private AttributeSource.State current;
  
  private PositionIncrementAttribute posIncrAtt;
  
  private boolean inAdditionToOriginal;
  
  private char[] output = new char[512];// initial size
  private int outputLength;
  
  public VibeSnowballFilter(TokenStream input, SnowballProgram stemmer, boolean inAdditionToOriginal) {
    super(input);
    this.stemmer = stemmer;
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
      Class<?> stemClass = Class.forName("org.tartarus.snowball.ext." + name + "Stemmer");
      stemmer = (SnowballProgram) stemClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e.toString());
    }
    this.inAdditionToOriginal = inAdditionToOriginal;
    this.posIncrAtt = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);
  }

  	/** Returns the next input Token, after being stemmed */
    @Override
  	public boolean incrementToken() throws IOException {
		if (inAdditionToOriginal) {
			if(outputLength > 0) {
				restoreState(current);
				termAtt.copyBuffer(output, 0, outputLength);
				posIncrAtt.setPositionIncrement(0);
				outputLength = 0;
				return true;
			}
			else { // no saved stem
				if(input.incrementToken()) {
			      if (!keywordAttr.isKeyword()) {
			          char termBuffer[] = termAtt.buffer();
			          final int length = termAtt.length();
			    	  copyToOutput(termBuffer, length);
			          stemmer.setCurrent(output, outputLength);
			          stemmer.stem();
			          final char finalTerm[] = stemmer.getCurrentBuffer();
			          final int newLength = stemmer.getCurrentBufferLength();
			          if(!equalArrays(termBuffer, length, finalTerm, newLength)) {
			        	  // The original token and the output of the stemming differs. We need to put out both.
			        	  // Copy the result of stemming to the output buffer.
				          if (finalTerm != output)
				        	  copyToOutput(finalTerm, newLength);
					      else
					    	  outputLength = newLength;
				          current = captureState();
			          }
			          else {
			        	  // The original token is identical to the result of the stemming. Discard the stemming output.
			        	  outputLength = 0;
			          }
			        }
					return true;					
				}
				else {
					return false;
				}
			}
		} else { // in place of original
			if (input.incrementToken()) {
		      if (!keywordAttr.isKeyword()) {
		          char termBuffer[] = termAtt.buffer();
		          final int length = termAtt.length();
		          stemmer.setCurrent(termBuffer, length);
		          stemmer.stem();
		          final char finalTerm[] = stemmer.getCurrentBuffer();
		          final int newLength = stemmer.getCurrentBufferLength();
		          if (finalTerm != termBuffer)
		            termAtt.copyBuffer(finalTerm, 0, newLength);
		          else
		            termAtt.setLength(newLength);
		        }
				return true;
			} else {
				return false;
			}
		}
	}
    
  private void copyToOutput(char[] src, int srcLen) {
      if(output.length < srcLen) {
    	  output = new char[srcLen]; // make it big enough
      }
      System.arraycopy(src, 0, output, 0, srcLen);
      outputLength = srcLen;
  }
  
  private boolean equalArrays(char[] a1, int l1, char[] a2, int l2) {
	  if(l1 != l2)
		  return false;
	  for(int i = 0; i < l1; i++) {
		  if(a1[i] != a2[i])
			  return false;
	  }
	  return true;
  }

}
