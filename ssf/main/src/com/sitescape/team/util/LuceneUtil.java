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

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import com.sitescape.team.lucene.CJKAnalyzer;
import com.sitescape.team.lucene.SsfIndexAnalyzer;



public class LuceneUtil {
	
	private static final int READSEARCH = 1;
	private static final int READDELETE = 2;
	private static final int WRITE = 3;
	
	private static Analyzer defaultAnalyzer = new SsfIndexAnalyzer();
	
	private static int prevState = READSEARCH;
	private static IndexWriter indexWriter = null;
	private static IndexReader indexReader = null;
	private static IndexSearcher indexSearcher = null;
	
	public static IndexReader getReader(String indexPath) throws IOException {
		synchronized (LuceneUtil.class) {
			switch(prevState) {
			case (READSEARCH):
				if (indexReader != null) 
					return indexReader;
			case (READDELETE):
			case(WRITE): 
				closeAll();
				indexWriter = null;
				try {
					if (indexReader != null) indexReader.close();
					indexReader = IndexReader.open(indexPath);
				} catch (IOException ioe) {
					if (!indexExists(indexPath)) {
						if (initializeIndex(indexPath)) {
							indexReader =  IndexReader.open(indexPath);
							return indexReader;
						}
					} else {
						try {
							// force unlock of the directory
							IndexReader.unlock(FSDirectory.getDirectory(indexPath));
							indexReader = IndexReader.open(indexPath);
						} catch (IOException e) {
							throw e;
						}
					}
				}
			}
			prevState = READDELETE;
		}
		return indexReader;
	}
	
	public static IndexSearcher getSearcher(String indexPath) throws IOException {
		synchronized (LuceneUtil.class) {
			switch(prevState) {
			case (READSEARCH):
			case (READDELETE):
				if (indexSearcher != null) 
					return indexSearcher;
			case(WRITE): 
				closeAll();
				try {
					indexSearcher = new IndexSearcher(indexPath);
				} catch (IOException ioe) {
					try {
						if (indexExists(indexPath)) {
							// force unlock of the directory
							IndexReader.unlock(FSDirectory.getDirectory(indexPath));
							indexSearcher = new IndexSearcher(indexPath);
						} else {
							if (initializeIndex(indexPath)) {
								indexSearcher = new IndexSearcher(indexPath);
								return indexSearcher;
							} else {
								throw ioe;
							}
						}
					} catch (IOException e) {
						throw e;
					}
				}
			}
			prevState = READSEARCH;
		}
		return indexSearcher;
	}
	
	public static IndexWriter getWriter(String indexPath, boolean create)
			throws IOException {
		synchronized (LuceneUtil.class) {
			switch (prevState) {
			case (WRITE):
				if (indexWriter != null && !create)
					break;
			case (READSEARCH):
			case (READDELETE):
				closeAll();
				try {
					indexWriter = new IndexWriter(indexPath, 
						new SsfIndexAnalyzer(), create);
				} catch (Exception ie) {
					try {
						// force unlock of the directory
						IndexReader.unlock(FSDirectory.getDirectory(indexPath));
						indexWriter = new IndexWriter(indexPath, 
								new SsfIndexAnalyzer(), create);
					} catch (IOException e) {
						throw e;
					}
				}
				indexWriter.setUseCompoundFile(false);
			}
			
			prevState = WRITE;
		}
		return indexWriter;
	}
	
	public static IndexSearcher getSearcher(IndexReader reader) {
		synchronized (LuceneUtil.class) {
			indexSearcher =  new IndexSearcher(reader);
			//prevState = READSEARCH;
		}
		return indexSearcher;
	}

	public static IndexWriter getWriter(String indexPath) throws IOException {
		return getWriter(indexPath, false);
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
				indexWriter.close();
				indexWriter = null;
				return true;
			}
		}
	}
	
	public static boolean indexExists(String indexPath) {
		if (IndexReader.indexExists(indexPath))
			return true;
		else
			return false;
	}
	
	
	public static void closeAll() {
		synchronized(LuceneUtil.class) {
			try {
				indexWriter.close();
			} catch (Exception we) {}
			try {
				indexReader.close();
			} catch (Exception re) {}
			try {
				indexSearcher.close();
			} catch (Exception se) {}
			indexWriter = null;
			indexReader = null;
			indexSearcher = null;
		}
	}
	
	// needed for searchers that are opened on existing readers.
	public static void closeSearcher() {
		try {
			indexSearcher.close();
		} catch (Exception se) {}
		indexSearcher = null;
	}

	// unlock the index
	public static void unlock(String indexPath) {
		try {
			if (indexExists(indexPath)) {
				// force unlock of the directory
				IndexReader.unlock(FSDirectory.getDirectory(indexPath));
			}
		} catch (Exception e) {
		}
	}
	
	//return the correct analyzer based on the text passed in.
	public static Analyzer getAnalyzer(String snippet) {
		// pass the snippet to the language taster and see which 
		// analyzer to use
		String language = LanguageTaster.taste(snippet.toCharArray());
		if (language.equalsIgnoreCase(LanguageTaster.DEFAULT)) {
			return defaultAnalyzer;
		} else if (language.equalsIgnoreCase(LanguageTaster.CJK)) {
			return new CJKAnalyzer();
		} else {
			//return new ARABICAnalyzer;
			return defaultAnalyzer;
		}
	}
	
}
