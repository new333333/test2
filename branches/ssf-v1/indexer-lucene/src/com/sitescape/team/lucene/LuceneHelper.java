/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import com.sitescape.util.PropsUtil;


public class LuceneHelper {

	protected static Log logger = LogFactory.getLog(LuceneHelper.class);

	private static final int SEARCH = 1;
	private static final int READ = 2;
	private static final int WRITE = 3;

	private static int maxMerge = 1000;
	private static int mergeFactor = 10;

	static {
		try {
			maxMerge = Integer.parseInt(PropsUtil.getString("lucene.max.merge.docs"));
		} catch (Exception e) {};
		try {
			mergeFactor = Integer.parseInt(PropsUtil.getString("lucene.merge.factor"));
		} catch (Exception e) {};
	}

	private static int prevState = SEARCH;

	private static IndexWriter indexWriter = null;
	private static IndexReader indexReader = null;
	private static IndexSearcher indexSearcher = null;

	
	public static IndexReader getReader(String indexPath) throws IOException {
		synchronized (LuceneHelper.class) {
			switch (prevState) {
			case (READ):
				if (indexReader != null)
					return indexReader;
			case (SEARCH):
				indexReader = getNewReader(indexPath);
				break;
			case (WRITE):
				closeWriter();
				closeReader();
				indexReader = getNewReader(indexPath);
			}
			prevState = READ;
		}
		return indexReader;
	}

	public static IndexSearcher getSearcher(String indexPath)
			throws IOException {
		synchronized (LuceneHelper.class) {
			switch (prevState) {
			case (SEARCH):
				if (indexSearcher != null)
					return indexSearcher;
				indexSearcher = getNewSearcher(indexPath);
				break;
			case (READ):
				closeReader();
				prevState = SEARCH;
				indexSearcher = getNewSearcher(indexPath);
				break;
			case (WRITE):
				if (indexWriter != null) indexWriter.flush();
				if (indexSearcher != null) {
					indexSearcher.close();
				}
				indexSearcher = getNewSearcher(indexPath);
			}
		}
		return indexSearcher;
	}

	public static IndexWriter getWriter(String indexPath, boolean create,
			boolean forOptimize) throws IOException {
		synchronized (LuceneHelper.class) {
			switch (prevState) {
			case (WRITE):
				if (indexWriter != null && !create)
					break;
			case (SEARCH):
				if (indexWriter != null) {
					indexWriter.close();
				}
				indexWriter = getNewWriter(indexPath, create);
				break;
			case (READ):
				closeReader();
				indexWriter = getNewWriter(indexPath, create);
			}
			if (forOptimize) {
				prevState = SEARCH;
			} else {
				prevState = WRITE;
			}
		}
		return indexWriter;
	}

	private static IndexReader getNewReader(String indexPath)
			throws IOException {
		try {
			indexReader = IndexReader.open(indexPath);
		} catch (IOException ioe) {
			if (!indexExists(indexPath)) {
				if (initializeIndex(indexPath)) {
					indexReader = IndexReader.open(indexPath);
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
		return indexReader;

	}

	private static IndexSearcher getNewSearcher(String indexPath)
			throws IOException {
		IndexSearcher indexSearcher = null;
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
		return indexSearcher;
	}

	private static IndexWriter getNewWriter(String indexPath, boolean create)
			throws IOException {
		try {
			indexWriter = new IndexWriter(indexPath, new SsfIndexAnalyzer(),
					create);
		} catch (Exception ie) {
			try {
				// force unlock of the directory
				IndexReader.unlock(FSDirectory.getDirectory(indexPath));
				indexWriter = new IndexWriter(indexPath,
						new SsfIndexAnalyzer(), create);
				indexWriter.setMaxMergeDocs(maxMerge);
				indexWriter.setMergeFactor(mergeFactor);
			} catch (IOException e) {
				throw e;
			}
		}
		indexWriter.setUseCompoundFile(false);
		return indexWriter;
	}

	public static IndexSearcher getSearcher(IndexReader reader) {
		synchronized (LuceneHelper.class) {
			indexSearcher = new IndexSearcher(reader);
			// prevState = READSEARCH;
		}
		return indexSearcher;
	}

	public static IndexWriter getWriterForOptimize(String indexPath) throws IOException {
		return getWriter(indexPath,false,true);
	}
	
	public static IndexWriter getWriter(String indexPath) throws IOException {
		return getWriter(indexPath, false, false);
	}

	private static boolean initializeIndex(String indexPath) throws IOException {
		synchronized (LuceneHelper.class) {
			if (IndexReader.indexExists(indexPath)) {
				// Index already exists at the specified directory.
				// We shouldn't initialize index in this case.
				return false;
			} else {
				// No index exists at the specified directory. Create a new one.
				getWriter(indexPath, true, false);
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
		synchronized (LuceneHelper.class) {
			closeWriter();
			closeReader();
			closeSearcher();
			prevState = WRITE;
		}
	}

	// needed for searchers that are opened on existing readers.
	public static void closeSearcher() {
		try {
			indexSearcher.close();
		} catch (Exception se) {
		}
		indexSearcher = null;
	}

	public static void closeReader() {
		try {
			indexReader.close();
		} catch (Exception se) {
		}
		indexReader = null;
	}

	public static void closeWriter() {
		try {
			indexWriter.close();
		} catch (Exception se) {
		}
		indexWriter = null;
	}

	// unlock the index
	public static void unlock(String indexPath) {
		try {
			closeAll();
		} catch (Exception e) {
		}
		try {
			if (indexExists(indexPath)) {
				// force unlock of the directory
				IndexReader.unlock(FSDirectory.getDirectory(indexPath));
			}
		} catch (Exception e) {
			logger.info("Can't unlock Lucene lockfile: " + e.toString());
		}
		try {
			File lockFile = new File(indexPath + "write.lock");
			if (lockFile.exists()) {
				lockFile.delete();
			}
		} catch (Exception e) {
			logger.info("Can't delete Lucene lockfile: " + e.toString());
		}
	}

	// return the correct analyzer based on the text passed in.

	
	public static Class classForName(String name) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(name);
		}
		catch (Exception e) {
			return Class.forName(name);
		}
	}
}
