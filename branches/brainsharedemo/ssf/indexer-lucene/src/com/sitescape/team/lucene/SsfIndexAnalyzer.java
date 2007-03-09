package com.sitescape.team.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**
 * The SsfIndexAnalyzer returns a stream of tokens which are composed of 
 *  alpha-numeric characters. Tokens are separated by non-alphanumeric chars.
 * If a token contains any uppercase characters, this analyzer returns the 
 * original token as well as a lowercase version of the token at the same 
 * word position.
 */
public class SsfIndexAnalyzer extends Analyzer
{
  public TokenStream tokenStream(String fieldName, Reader reader) 
  {
    TokenStream result = new SsfTokenFilter(
    
    new CharTokenizer(reader) 
    {
     // Returns true if this character should be included in the current token.
     // Otherwise, return false - signalling that a new token should begin
      protected boolean isTokenChar(char c) 
      {
        return Character.isLetterOrDigit(c);
      }
    });
    
    return result;
  }
}