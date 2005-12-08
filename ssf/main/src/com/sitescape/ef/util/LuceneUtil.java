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

import com.sitescape.ef.lucene.MixedCaseAnalyzer;

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

	public static IndexReader getReader(String indexPath) throws IOException {
		return IndexReader.open(indexPath);
	}

	public static IndexSearcher getSearcher(String indexPath)
		throws IOException {

		return new IndexSearcher(indexPath);
	}

	public static IndexWriter getWriter(String indexPath) throws IOException {
		return getWriter(indexPath, false);
	}

	public static IndexWriter getWriter(String indexPath, boolean create)
		throws IOException {

		return new IndexWriter(indexPath, new MixedCaseAnalyzer(), create);
	}

}
