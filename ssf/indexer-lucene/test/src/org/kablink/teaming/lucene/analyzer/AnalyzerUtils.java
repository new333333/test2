package org.kablink.teaming.lucene.analyzer;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.Assert;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

public class AnalyzerUtils {

	public static void displayTokens(Analyzer analyzer, String text) throws IOException {
			displayTokens(analyzer.tokenStream("contents", new StringReader(text)));
	}

	public static void displayTokens(TokenStream stream) throws IOException {
		TermAttribute term = (TermAttribute) stream.addAttribute(TermAttribute.class);
		while (stream.incrementToken()) {
			System.out.print("[" + term.term() + "] ");
		}
	}
	
	public static void assertAnalyzesTo(Analyzer analyzer, String input,
			String[] output) throws Exception {
		TokenStream stream = analyzer.tokenStream("field", new StringReader(input));
		TermAttribute termAttr = (TermAttribute) stream.addAttribute(TermAttribute.class);
		for (String expected : output) {
			Assert.assertTrue(stream.incrementToken());
			Assert.assertEquals(expected, termAttr.term());
		}
		Assert.assertFalse(stream.incrementToken());
		stream.close();
	}
}
