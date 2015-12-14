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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.kablink.teaming.lucene.util.SsfTokenFilter;
import org.kablink.util.PropsUtil;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Set;

public class VibeIndexAnalyzer extends VibeAnalyzer {
	
	private static Log logger = LogFactory.getLog(VibeIndexAnalyzer.class);
	
	private static boolean inited = false;
	private static Set indexStopWords;
	private static String indexStemmerName;
	private static boolean indexFoldToAscii;
	private static boolean indexFallbackToLegacy;
	private static boolean indexTokenDecomposition = false; // applicable only if indexUseStandard is true
	private static boolean indexUseStandard;

	protected VibeIndexAnalyzer(Set stopWords,
			String stemmerName, boolean foldToAscii, boolean decomposeToken, boolean useStandard) {
		super(stopWords, stemmerName, foldToAscii, decomposeToken, useStandard);
	}

	protected VibeIndexAnalyzer(File stopWords, String charset,
			String stemmerName, boolean foldToAscii, boolean decomposeToken, boolean useStandard) throws IOException {
		super(openStopWordFile(stopWords, charset), stemmerName, foldToAscii, decomposeToken, useStandard);
	}

	protected VibeIndexAnalyzer(Reader stopWords,
			String stemmerName, boolean foldToAscii, boolean decomposeToken, boolean useStandard) throws IOException {
		super(stopWords, stemmerName, foldToAscii, decomposeToken, useStandard);
	}

	public TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream result;
		if(useStandard) {
			result = new StandardTokenizer(VERSION, reader);
			result = new StandardFilter(result);
			if(decomposeToken)
				result = new TokenDecomposingFilter(result);
		}
		else {
			result = new LetterOrDigitTokenizer(reader);
		}
		result = new LowerCaseFilter(result);
		if(foldToAscii)
			result = new VibeASCIIFoldingFilter(result, true);
		if (stopSet != null && stopSet.size() > 0)
			result = new StopFilter(true, result, stopSet);
		if(stemmerName != null && !stemmerName.equals(""))
			result = new VibeSnowballFilter(result, stemmerName, true);
		return result;
	}

	public TokenStream reusableTokenStream(String fieldName, Reader reader)
			throws IOException {
		SavedStreams streams = (SavedStreams) getPreviousTokenStream();
		if (streams == null) {
			streams = new SavedStreams();
			if(useStandard) {
				streams.source = new StandardTokenizer(VERSION, reader);
				streams.result = new StandardFilter(streams.source);
				if(decomposeToken)
					streams.result = new TokenDecomposingFilter(streams.result);
			}
			else {
				streams.source = new LetterOrDigitTokenizer(reader);
			}
			streams.result = new LowerCaseFilter((streams.result == null)? streams.source : streams.result);
			if(foldToAscii)
				streams.result = new VibeASCIIFoldingFilter((streams.result == null)? streams.source : streams.result, true);
			if (stopSet != null && stopSet.size() > 0)
				streams.result = new StopFilter(true, (streams.result == null)? streams.source : streams.result, stopSet);
			if(stemmerName != null && !stemmerName.equals(""))
				streams.result = new VibeSnowballFilter((streams.result == null)? streams.source : streams.result, stemmerName, true);
			setPreviousTokenStream(streams);
		} else {
			streams.source.reset(reader);
		}
		return streams.result;
	}
	
	public static Analyzer getInstance() {
		if(!inited)
			init();
		if(indexFallbackToLegacy)
			return new SsfIndexAnalyzer();
		else 
			return new VibeIndexAnalyzer(indexStopWords, indexStemmerName, indexFoldToAscii, indexTokenDecomposition, indexUseStandard);
	}
	
	private static void init() {
		indexFallbackToLegacy = PropsUtil.getBoolean("lucene.indexing.fallbackto.legacy", false);
		if(indexFallbackToLegacy) {
			logger.info("Fall back to legacy analyzer for indexing");
		}
		else {
			if(PropsUtil.getBoolean("lucene.indexing.stopwords.enable", true)) {
				// Stop-words filtering is enabled.
				String stopWordFilePath = PropsUtil.getString("lucene.indexing.stopwords.file.path", "");
				if(!stopWordFilePath.equals("")) {
					// Stop-words file exists. Load it. 
					String stopWordFileCharset = PropsUtil.getString("lucene.indexing.stopwords.file.charset", "UTF-8");
					if(stopWordFileCharset.equalsIgnoreCase("jvm-default")) {
						stopWordFileCharset = Charset.defaultCharset().name();
					}				
					try {
						logger.info("Loading stopwords from file '" + stopWordFilePath + "' using charset " + stopWordFileCharset); 
						indexStopWords = WordlistLoader.getWordSet(openStopWordFile(new File(stopWordFilePath), stopWordFileCharset));
					} catch (IOException e) {
						logger.error("Error loading stopwords" , e);
						logger.info("Defaulting to common English stopwords");
						indexStopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
					}
				}
				else {
					// Stop-words file is not specified. Use the default English stop-words list.
					logger.info("Defaulting to common English stopwords");
					indexStopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
				}
				logger.info("Stop words filtering is enabled for indexing. List size is " + indexStopWords.size());
			}
			else {
				logger.info("Stop words filtering is disabled for indexing");
			}
	
			if(PropsUtil.getBoolean("lucene.indexing.stemming.enable", true)) {
				indexStemmerName = PropsUtil.getString("lucene.indexing.stemming.stemmer.names", "English");
				logger.info("Stemming is enabled for indexing with stemmer '" + indexStemmerName + "'");
			}
			else {
				logger.info("Stemming is disabled for indexing");
			}
			
			indexFoldToAscii = PropsUtil.getBoolean("lucene.indexing.asciifolding.enable", true);
			logger.info("ASCII folding is " + ((indexFoldToAscii)? "enabled":"disabled") + " for indexing");
			
			indexUseStandard = PropsUtil.getBoolean("lucene.indexing.use.standard", false);
			if(indexUseStandard) {
				logger.info("Using Lucene's standard tokenizer and filter for indexing");
				indexTokenDecomposition = PropsUtil.getBoolean("lucene.indexing.decomposition.enable", false);
				logger.info("Token decomposition is " + ((indexTokenDecomposition)? "enabled":"disabled") + " for indexing");							
			}
			else {
				logger.info("Using Vibe tokenizer for indexing");
			}
		}
		
		inited = true;
	}

}
