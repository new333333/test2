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
/*
 * NOTICE: This file has been changed: the package of this class has been changed.
 */
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Set;

/**
 * Filters {@link StandardTokenizer} with {@link StandardFilter} and 
 * {@link StopFilter}, using a list of English stop words.
 *
 * <a name="version"/>
 * <p>You must specify the required {@link Version}
 * compatibility when creating StandardAnalyzer:
 * <ul>
 *   <li> As of 2.9, StopFilter preserves position
 *        increments
 *   <li> As of 2.4, Tokens incorrectly identified as acronyms
 *        are corrected (see <a href="https://issues.apache.org/jira/browse/LUCENE-1068">LUCENE-1608</a>
 * </ul>
 *
 * @version $Id: StandardAnalyzer.java 829134 2009-10-23 17:18:53Z mikemccand $
 */
public class StandardQueryAnalyzer extends Analyzer {
  private Set stopSet;

  /**
   * Specifies whether deprecated acronyms should be replaced with HOST type.
   * This is false by default to support backward compatibility.
   * 
   * @deprecated this should be removed in the next release (3.0).
   *
   * See https://issues.apache.org/jira/browse/LUCENE-1068
   */
  private boolean replaceInvalidAcronym = defaultReplaceInvalidAcronym;

  private static boolean defaultReplaceInvalidAcronym;
  private boolean enableStopPositionIncrements;

  // @deprecated
  private boolean useDefaultStopPositionIncrements;
  
  private boolean ignoreCaseForStop = true;

  // Default to true (fixed the bug), unless the system prop is set
  static {
    final String v = System.getProperty("org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym");
    if (v == null || v.equals("true"))
      defaultReplaceInvalidAcronym = true;
    else
      defaultReplaceInvalidAcronym = false;
  }

  /**
   *
   * @return true if new instances of StandardTokenizer will
   * replace mischaracterized acronyms
   *
   * See https://issues.apache.org/jira/browse/LUCENE-1068
   * @deprecated This will be removed (hardwired to true) in 3.0
   */
  public static boolean getDefaultReplaceInvalidAcronym() {
    return defaultReplaceInvalidAcronym;
  }

  /**
   *
   * @param replaceInvalidAcronym Set to true to have new
   * instances of StandardTokenizer replace mischaracterized
   * acronyms by default.  Set to false to preserve the
   * previous (before 2.4) buggy behavior.  Alternatively,
   * set the system property
   * org.apache.lucene.analysis.standard.StandardAnalyzer.replaceInvalidAcronym
   * to false.
   *
   * See https://issues.apache.org/jira/browse/LUCENE-1068
   * @deprecated This will be removed (hardwired to true) in 3.0
   */
  public static void setDefaultReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
    defaultReplaceInvalidAcronym = replaceInvalidAcronym;
  }


  /** An array containing some common English words that are usually not
  useful for searching. 
  @deprecated Use {@link #STOP_WORDS_SET} instead */
  public static final String[] STOP_WORDS = StopAnalyzer.ENGLISH_STOP_WORDS;
  
  /** An unmodifiable set containing some common English words that are usually not
  useful for searching. */
  public static final Set/*<String>*/ STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET; 

  /** Builds an analyzer with the default stop words ({@link
   * #STOP_WORDS}).
   * @param matchVersion Lucene version to match See {@link
   * <a href="#version">above</a>}
   */
  public StandardQueryAnalyzer(Version matchVersion, boolean ignoreCaseForStop) {
    this(matchVersion, STOP_WORDS_SET, ignoreCaseForStop);
  }

  /** Builds an analyzer with the given stop words.
   * @param matchVersion Lucene version to match See {@link
   * <a href="#version">above</a>}
   * @param stopWords stop words */
  public StandardQueryAnalyzer(Version matchVersion, Set stopWords, boolean ignoreCaseForStop) {
    stopSet = stopWords;
    this.ignoreCaseForStop = ignoreCaseForStop;
    init(matchVersion);
  }

  /** Builds an analyzer with the stop words from the given file.
   * @see WordlistLoader#getWordSet(File)
   * @param matchVersion Lucene version to match See {@link
   * <a href="#version">above</a>}
   * @param stopwords File to read stop words from */
  public StandardQueryAnalyzer(Version matchVersion, File stopwords, boolean ignoreCaseForStop) throws IOException {
    stopSet = WordlistLoader.getWordSet(stopwords);
    this.ignoreCaseForStop = ignoreCaseForStop;
    init(matchVersion);
  }

  /** Builds an analyzer with the stop words from the given reader.
   * @see WordlistLoader#getWordSet(Reader)
   * @param matchVersion Lucene version to match See {@link
   * <a href="#version">above</a>}
   * @param stopwords Reader to read stop words from */
  public StandardQueryAnalyzer(Version matchVersion, Reader stopwords, boolean ignoreCaseForStop) throws IOException {
    stopSet = WordlistLoader.getWordSet(stopwords);
    this.ignoreCaseForStop = ignoreCaseForStop;
    init(matchVersion);
  }

  private final void init(Version matchVersion) {
    setOverridesTokenStreamMethod(StandardQueryAnalyzer.class);
    if (matchVersion.onOrAfter(Version.LUCENE_29)) {
      enableStopPositionIncrements = true;
    } else {
      useDefaultStopPositionIncrements = true;
    }
    if (matchVersion.onOrAfter(Version.LUCENE_24)) {
      replaceInvalidAcronym = defaultReplaceInvalidAcronym;
    } else {
      replaceInvalidAcronym = false;
    }
  }

  /** Constructs a {@link StandardTokenizer} filtered by a {@link
  StandardFilter} and a {@link StopFilter}. */
  public TokenStream tokenStream(String fieldName, Reader reader) {
    StandardTokenizer tokenStream = new StandardTokenizer(reader, replaceInvalidAcronym);
    tokenStream.setMaxTokenLength(maxTokenLength);
    TokenStream result = new StandardFilter(tokenStream);
    if (useDefaultStopPositionIncrements) {
      result = new StopFilter(result, stopSet, ignoreCaseForStop);
    } else {
      result = new StopFilter(enableStopPositionIncrements, result, stopSet, ignoreCaseForStop);
    }
    return result;
  }

  private static final class SavedStreams {
    StandardTokenizer tokenStream;
    TokenStream filteredTokenStream;
  }

  /** Default maximum allowed token length */
  public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

  private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;

  /**
   * Set maximum allowed token length.  If a token is seen
   * that exceeds this length then it is discarded.  This
   * setting only takes effect the next time tokenStream or
   * reusableTokenStream is called.
   */
  public void setMaxTokenLength(int length) {
    maxTokenLength = length;
  }
    
  /**
   * @see #setMaxTokenLength
   */
  public int getMaxTokenLength() {
    return maxTokenLength;
  }

  /** @deprecated Use {@link #tokenStream} instead */
  public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
    if (overridesTokenStreamMethod) {
      // LUCENE-1678: force fallback to tokenStream() if we
      // have been subclassed and that subclass overrides
      // tokenStream but not reusableTokenStream
      return tokenStream(fieldName, reader);
    }
    SavedStreams streams = (SavedStreams) getPreviousTokenStream();
    if (streams == null) {
      streams = new SavedStreams();
      setPreviousTokenStream(streams);
      streams.tokenStream = new StandardTokenizer(reader);
      streams.filteredTokenStream = new StandardFilter(streams.tokenStream);
      if (useDefaultStopPositionIncrements) {
        streams.filteredTokenStream = new StopFilter(streams.filteredTokenStream, stopSet, ignoreCaseForStop);
      } else {
        streams.filteredTokenStream = new StopFilter(enableStopPositionIncrements, streams.filteredTokenStream, stopSet, ignoreCaseForStop);
      }
    } else {
      streams.tokenStream.reset(reader);
    }
    streams.tokenStream.setMaxTokenLength(maxTokenLength);
    
    streams.tokenStream.setReplaceInvalidAcronym(replaceInvalidAcronym);

    return streams.filteredTokenStream;
  }

  /**
   *
   * @return true if this Analyzer is replacing mischaracterized acronyms in the StandardTokenizer
   *
   * See https://issues.apache.org/jira/browse/LUCENE-1068
   * @deprecated This will be removed (hardwired to true) in 3.0
   */
  public boolean isReplaceInvalidAcronym() {
    return replaceInvalidAcronym;
  }

  /**
   *
   * @param replaceInvalidAcronym Set to true if this Analyzer is replacing mischaracterized acronyms in the StandardTokenizer
   *
   * See https://issues.apache.org/jira/browse/LUCENE-1068
   * @deprecated This will be removed (hardwired to true) in 3.0
   */
  public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
    this.replaceInvalidAcronym = replaceInvalidAcronym;
  }
}
