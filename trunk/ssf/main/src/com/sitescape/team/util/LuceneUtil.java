/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import com.sitescape.team.lucene.SsfIndexAnalyzer;
import com.sitescape.team.search.LuceneException;
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
import org.apache.lucene.store.FSDirectory;


public class LuceneUtil {

/* These methods are no longer supported
 * 
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
*/
	
	public static IndexReader getReader(String indexPath) throws IOException {
		try {
			return IndexReader.open(indexPath);
		}
		catch(IOException ioe) {
			if(initializeIndex(indexPath)) {
				return IndexReader.open(indexPath);
			} else {
				try {
					// force unlock of the directory
					IndexReader.unlock(FSDirectory.getDirectory(indexPath, false));
					return IndexReader.open(indexPath);
				} catch (IOException e) {
					throw e;
				}
			}
		}
	}

	public static IndexSearcher getSearcher(String indexPath)
			throws IOException {

		try {
			return new IndexSearcher(indexPath);
		} catch (IOException e) {
			if (initializeIndex(indexPath)) {
				return new IndexSearcher(indexPath);
			} else {
				throw e;
			}
		}
	}

	public static IndexSearcher getSearcher(IndexReader reader) {
		return new IndexSearcher(reader);
	}

	public static IndexWriter getWriter(String indexPath) throws IOException {
		return getWriter(indexPath, false);
	}

	public static IndexWriter getWriter(String indexPath, boolean create) throws IOException {
		IndexWriter iw = new IndexWriter(indexPath, new SsfIndexAnalyzer(), create);
		iw.setUseCompoundFile(false);
		return iw;
	}
	
	private static boolean initializeIndex(String indexPath) throws IOException {
		synchronized(LuceneUtil.class) {
			if(IndexReader.indexExists(indexPath)) {
				// Index already exists at the specified directory. 
				// We shouldn't initialize index in this case.
				return false;
			}
			else {
				// No index exists at the specified directory. Create a new one.
				getWriter(indexPath, true);
				return true;
			}
		}
	}
}
