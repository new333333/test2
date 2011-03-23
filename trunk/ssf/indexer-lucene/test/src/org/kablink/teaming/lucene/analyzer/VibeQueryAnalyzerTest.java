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
		
		Analyzer analyzer = new VibeQueryAnalyzer();
		String text = "vibe_onprem a.b. test.doc a-b end. 30-12 vibe_onprem@novell.com";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"vibe", "onprem", "ab", "test.doc", "a", "b", "end", "30-12", "vibe_onprem@novell.com"});
	}
	
	public void testCases() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer();
		String text = "Novell nOvell XY&Z NOVELL novell Runs RUNS";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"Novell", "nOvell", "XY&Z", "NOVELL", "novell", "Runs", "RUNS"});
		
		text = "the The tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"the", "The", "tHe", "thE", "THE"});
	}
	
	
	public void testEnglishStemming() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer("English");
		String text = "stemming algorithms Algorithmic breathing breathes runs Runs RUNS ran running";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"stem", "algorithm", "Algorithm", "breath", "breath", "run", "Run", "RUNS", "ran", "run"});
	}
	
	public void testStopWords() throws Exception {
		// Apply stop words case insensitively.
		Analyzer analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), Charset.defaultCharset().name(), true, null, false);
		String text = "the The tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {});	

		// Apply stop words case sensitively.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), Charset.defaultCharset().name(), false, null, false);
		text = "the The Then tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {"The", "thE", "THE"});	

		// Apply Western European language (specifically, German and French) stop words by
		// reading them from a file previously encoded in windows-1252 using system default 
		// character encoding (which is windows-1252 on Windows and probably ISO-8859-1 on Linux).
		// This should work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), Charset.defaultCharset().name(), true, null, false);
		text = "L'�ph�m�ride G�terzug novell �berfuhr by d�nemark Caract�re to br�lante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"L'�ph�m�ride", "novell", "d�nemark", "vibe"});	

		// Apply Western European language (specifically, German and French) stop words by
		// reading them using UTF-8 charset from a file previously encoded also in UTF-8.
		// This should work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.utf8.txt"), "UTF-8", true, null, false);
		text = "L'�ph�m�ride G�terzug novell �berfuhr by d�nemark Caract�re to br�lante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"L'�ph�m�ride", "novell", "d�nemark", "vibe"});	
		
		// Apply Western European language (specifically, German and French) stop words by
		// reading them from a file previously encoded in UTF-8 using system default 
		// character encoding (which is windows-1252 on Windows and probably ISO-8859-1 on Linux).
		// This should NOT work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.utf8.txt"), Charset.defaultCharset().name(), true, null, false);
		text = "L'�ph�m�ride G�terzug novell �berfuhr by d�nemark Caract�re to br�lante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesNotTo(analyzer, text, 
				new String[] {"L'�ph�m�ride", "novell", "d�nemark", "vibe"});	

		// Apply Western European language (specifically, German and French) stop words by
		// reading them using UTF-8 charset from a file previously encoded in windows-1252.
		// This should NOT work properly.
		analyzer = new VibeQueryAnalyzer(new File("C:/junk/stop_words.txt"), "UTF-8", true, null, false);
		text = "L'�ph�m�ride G�terzug novell �berfuhr by d�nemark Caract�re to br�lante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesNotTo(analyzer, text, 
				new String[] {"L'�ph�m�ride", "novell", "d�nemark", "vibe"});		
	}
	
	public void testFoldingToAscii() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer((Set)null, true, null, true);
		String text = "L'�ph�m�ride G�terzug novell �berfuhr by d�nemark Caract�re to br�lante vibe �v�nement";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"L'ephemeride", "Guterzug", "novell", "uberfuhr", "by", "danemark", "Caractere", "to", "brulante", "vibe", "evenement"}); 
	}
	
	public void testTokenDecomposition() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer((Set)null, true, null, false, false);
		String text = "debug.doc foo.bar() www.novell.com";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"debug.doc", "foo.bar", "www.novell.com"}); 
		
		analyzer = new VibeQueryAnalyzer((Set)null, true, null, false, true);
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"debug.doc", "debug", "doc", "foo.bar", "foo", "bar", "www.novell.com", "www", "novell", "com"}); 
	}
	
	public void testDefaultConfiguration() throws Exception {
		Analyzer analyzer = new VibeQueryAnalyzer(StopAnalyzer.ENGLISH_STOP_WORDS_SET, 
				true, 
				"English", 
				true, 
				false);
		String text = "Kunde Karlsruhe Update von IBM";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
	}
}

