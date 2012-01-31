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

import java.io.File;
import java.nio.charset.Charset;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;

public class VibeQueryAnalyzerTest extends TestCase {
	
	public void testPuntuationAndEmailAddress() throws Exception {
		System.out.println(Charset.defaultCharset());
		
		Analyzer analyzer = new VibeQueryAnalyzer((Set)null, null, false, false, false);
		String text = "vibe_onprem a.b. test.doc a-b end. 30-12 vibe3_onprem@novell.com 3A";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"vibe", "onprem", "a", "b", "test", "doc", "a", "b", "end", "30", "12", "vibe3", "onprem", "novell", "com", "3a"});
	}
	
	public void testCases() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer((Set)null, null, false, false, false);
		String text = "Novell nOvell XY&Z NOVELL novell Runs RUNS";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"novell", "novell", "xy", "z", "novell", "novell", "runs", "runs"});
		
		text = "the The tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"the", "the", "the", "the", "the"});
	}
	
	
	public void testEnglishStemming() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer((Set)null, "English", false, false, false);
		String text = "stemming algorithms Algorithmic breathing breathes runs Runs RUNS ran running";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"stem", "algorithm", "algorithm", "breath", "breath", "run", "run", "run", "ran", "run"});
	}
	
	public void testStopWords() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), Charset.defaultCharset().name(), null, false, false, false);
		String text = "the The tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {});	

		// Vibe performs both indexing and search in lowercase. As such, for efficiency reason,
		// Vibe utilizes case sensitive match when filtering against stop word list.
		// Consequently, it is crucial that ALL words in the stopword file must be in lowercase.
		// Otherwise, the filtering will fail on such word. This example demonstrates such case.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), Charset.defaultCharset().name(), null, false, false, false);
		text = "the The Then tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {"then"});	

		// Apply Western European language (specifically, German and French) stop words by
		// reading them from a file previously encoded in windows-1252 using system default 
		// character encoding (which is windows-1252 on Windows and probably ISO-8859-1 on Linux).
		// This should work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), Charset.defaultCharset().name(), null, false, false, false);
		text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"l", "éphéméride", "güterzug", "novell", "dänemark", "caractère", "vibe"});	

		// Apply Western European language (specifically, German and French) stop words by
		// reading them using UTF-8 charset from a file previously encoded also in UTF-8.
		// This should work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.utf8.txt"), "UTF-8", null, false, false, false);
		text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"l", "éphéméride", "güterzug", "novell", "dänemark", "caractère", "vibe"});	
		
		// Apply Western European language (specifically, German and French) stop words by
		// reading them from a file previously encoded in UTF-8 using system default 
		// character encoding (which is windows-1252 on Windows and probably ISO-8859-1 on Linux).
		// This should NOT work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.utf8.txt"), Charset.defaultCharset().name(), null, false, false, false);
		text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesNotTo(analyzer, text, 
				new String[] {"l", "éphéméride", "güterzug", "novell", "dänemark", "caractère", "vibe"});		

		// Apply Western European language (specifically, German and French) stop words by
		// reading them using UTF-8 charset from a file previously encoded in windows-1252.
		// This should NOT work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), "UTF-8", null, false, false, false);
		text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesNotTo(analyzer, text, 
				new String[] {"l", "éphéméride", "güterzug", "novell", "dänemark", "caractère", "vibe"});		
	}
	
	public void testFoldingToAscii() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer((Set)null, null, true, false, false);
		String text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe évènement";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"l", "éphéméride", "ephemeride", "güterzug", "guterzug", "novell", "überfuhr", "uberfuhr", "by", "dänemark", "danemark", "caractère", "caractere", "to", "brûlante", "brulante", "vibe", "évènement", "evenement"}); 
	}
	
	public void testDefaultConfiguration() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer(StopAnalyzer.ENGLISH_STOP_WORDS_SET,  
				"English", 
				true, 
				false,
				false);
		String text = "Kunde Karlsruhe Update update von IBM";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"kund", "karlsruh", "updat", "updat", "von", "ibm"}); 
	}

	public void testFrenchStemmer() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer(StopAnalyzer.ENGLISH_STOP_WORDS_SET,  
				"French", 
				true, 
				false,
				false);
		String text = "technicité";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"techniqu", "technicit"}); 
	}

	public void testGermanStemmer() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer(StopAnalyzer.ENGLISH_STOP_WORDS_SET,  
				"German", 
				true, 
				false,
				false);
		String text = "gegessen";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"gegess"}); 
	}

}

