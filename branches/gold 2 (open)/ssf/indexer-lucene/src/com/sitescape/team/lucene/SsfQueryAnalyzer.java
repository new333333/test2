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

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**
 * The SsfQueryAnalyzer returns a case preserved stream of tokens.
 * Tokens are separated by non-alphanumeric characters.  
 */
public class SsfQueryAnalyzer extends Analyzer
{
  public TokenStream tokenStream(String fieldName, Reader reader) 
  {
    return new CharTokenizer(reader) 
    {     
      // Returns true if this character should be included in the current token.
      // Otherwise, return false - signalling that a new token should begin
      protected boolean isTokenChar(char c) 
      {
        return Character.isLetterOrDigit(c);
      }
    };
  }
}