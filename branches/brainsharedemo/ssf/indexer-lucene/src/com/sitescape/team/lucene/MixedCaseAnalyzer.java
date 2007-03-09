package com.sitescape.team.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**
 * The <tt>MixedCaseAnalyzer</tt> returns a stream of tokens which are composed of 
 * only alpha-numeric characters. The tokens are separated by non-alphanumeric characters.
 * If a token contains any uppercase characters, this analyzer also returns a lowercase 
 * version of the token at the same word position.
 */
public class MixedCaseAnalyzer extends Analyzer
{
  public TokenStream tokenStream(String fieldName, Reader reader) 
  {
    TokenStream result = new SsfTokenFilter(
    
    new CharTokenizer(reader) 
    {
      // Returns true if a c should be included in the current token.
      // Otherwise, false (and a new token begins)
      protected boolean isTokenChar(char c) 
      {
        return Character.isLetterOrDigit(c);
      }
    });
    
    return result;
  }
}