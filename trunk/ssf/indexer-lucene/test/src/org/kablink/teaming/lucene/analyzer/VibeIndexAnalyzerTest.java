package org.kablink.teaming.lucene.analyzer;

import org.apache.lucene.analysis.Analyzer;

import junit.framework.TestCase;

public class VibeIndexAnalyzerTest extends TestCase {
	
	public void testPuntuationAndEmailAddress() throws Exception {
		Analyzer analyzer = new VibeIndexAnalyzer();
		String text = "vibe_onprem a.b. a.b a-b end. 30-12 vibe_onprem@novell.com";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"vibe", "onprem", "ab", "a.b", "a", "b", "end", "30-12", "vibe_onprem@novell.com"});
	}
	
	public void testCases() throws Exception {
		Analyzer analyzer = new VibeIndexAnalyzer();
		String text = "Novell nOvell XY&Z NOVELL novell";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, 
				new String[] {"Novell", "novell", "nOvell", "novell", "XY&Z", "xy&z", "NOVELL", "novell", "novell"});
	}
	
	
	public void testEnglishStemming() throws Exception {
		Analyzer analyzer = new VibeIndexAnalyzer("English");
		String text = "stemming algorithms breathing breathes runs ran running";
		AnalyzerUtils.displayTokens(analyzer, text);
		System.out.println();
		AnalyzerUtils.assertAnalyzesTo(analyzer, text, new String[] {"stem", "algorithm", "breath", "breath", "run", "ran", "run"});
	}
	
	
}
