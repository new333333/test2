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

import org.apache.lucene.analysis.Analyzer;

import junit.framework.TestCase;

public class VibeIndexAnalyzerTest extends TestCase {
	
	public void testPuntuationAndEmailAddress() throws Exception {
		System.out.println(Charset.defaultCharset());
		
		Analyzer analyzer = new VibeIndexAnalyzer();
		String text = "vibe_onprem a.b. a.b a-b end. 30-12 vibe_onprem@novell.com";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"vibe", "onprem", "ab", "a.b", "a", "b", "end", "30-12", "vibe_onprem@novell.com"});
	}
	
	public void testCases() throws Exception {
		Analyzer analyzer = new VibeIndexAnalyzer();
		String text = "Novell nOvell XY&Z NOVELL novell Runs RUNS";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"Novell", "novell", "nOvell", "novell", "XY&Z", "xy&z", "NOVELL", "novell", "novell", "Runs", "runs", "RUNS", "runs"});
		
		text = "the The tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"the", "The", "the", "tHe", "the", "thE", "the", "THE", "the"});
	}
	
	
	public void testEnglishStemming() throws Exception {
		Analyzer analyzer = new VibeIndexAnalyzer("English");
		String text = "stemming algorithms Algorithmic breathing breathes runs Runs RUNS ran running";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"stem", "algorithm", "Algorithm", "algorithm", "breath", "breath", "run", "Run", "run", "RUNS", "run", "ran", "run"});

		// The following test shows that English stemmers do NOT necessarily leave non-English words intact.
		// Pay attention to Caractère and brûlante.
		text = "L'éphéméride Güterzug überfuhr working dänemark Caractère brûlante évènement";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"L'éphéméride", "l'éphéméride", "Güterzug", "güterzug", "überfuhr", "work", "dänemark", "Caractèr", "caractèr", "brûlant", "évènement"}); 
	}
	
	public void testStopWords() throws Exception {
		// Apply stop words case insensitively.
		Analyzer analyzer = new VibeIndexAnalyzer(new File("C:/junk/stop_words.txt"), true, null, false);
		String text = "the The tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {});	

		// Apply stop words case sensitively.
		analyzer = new VibeIndexAnalyzer(new File("C:/junk/stop_words.txt"), false, null, false);
		text = "the The Then tHe thE THE";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {"The", "then", "thE", "THE"});	

		// Apply non-English Latin-1 (Western European languages) stop words.
		analyzer = new VibeIndexAnalyzer(new File("C:/junk/stop_words.txt"), true, null, false);
		text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"L'éphéméride", "l'éphéméride", "novell", "dänemark", "vibe"});	
	}
	
	public void testFoldingToAscii() throws Exception {
		Analyzer analyzer = new VibeIndexAnalyzer((Set)null, true, null, true);
		String text = "L'éphéméride Güterzug novell überfuhr by dänemark Caractère to brûlante vibe évènement";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"L'ephemeride", "l'ephemeride", "Guterzug", "guterzug", "novell", "uberfuhr", "by", "danemark", "Caractere", "caractere", "to", "brulante", "vibe", "evenement"}); 
	}
}
