package com.sitescape.ef.util;

import com.sitescape.util.StringPool;
import com.sitescape.util.Validator;
import com.sitescape.util.lucene.KeywordsUtil;

import java.io.IOException;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public class LuceneUtil {

	public static void addTerm(
			BooleanQuery booleanQuery, String field, String text)
		throws ParseException {

		if (Validator.isNotNull(text)) {
			if (text.indexOf(StringPool.SPACE) == -1) {
				text = KeywordsUtil.toWildcard(text);
			}

			Query query = QueryParser.parse(text, field, new SimpleAnalyzer());

			booleanQuery.add(query, false, false);
		}
	}

	public static void addRequiredTerm(
		BooleanQuery booleanQuery, String field, String text) {

		text = KeywordsUtil.escape(text);

		Term term = new Term(field, text);
		TermQuery termQuery = new TermQuery(term);

		booleanQuery.add(termQuery, true, false);
	}

	public static String getLuceneDir(String zoneName) {
		return PropsUtil.get(PropsUtil.LUCENE_DIR) + zoneName +
			StringPool.SLASH;
	}

	public static IndexReader getReader(String zoneName) throws IOException {
		return IndexReader.open(getLuceneDir(zoneName));
	}

	public static IndexSearcher getSearcher(String zoneName)
		throws IOException {

		return new IndexSearcher(getLuceneDir(zoneName));
	}

	public static IndexWriter getWriter(String zoneName) throws IOException {
		return getWriter(zoneName, false);
	}

	public static IndexWriter getWriter(String zoneName, boolean create)
		throws IOException {

		return new IndexWriter(
			getLuceneDir(zoneName), new SimpleAnalyzer(), create);
	}

}
