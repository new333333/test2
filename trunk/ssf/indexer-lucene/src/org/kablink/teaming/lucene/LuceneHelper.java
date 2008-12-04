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
package org.kablink.teaming.lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.kablink.util.PropsUtil;



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

	private static HashMap<String, Object> readers = new HashMap<String, Object>();
	private static HashMap<String, Object> writers = new HashMap<String, Object>();
	private static HashMap<String, Object> searchers = new HashMap<String, Object>();
	
	
	private static IndexReader getZonedReader(String indexPath) {
		synchronized(readers) {
			IndexReader reader = (IndexReader)readers.get(indexPath);
			return reader;
		}
	}

	private static void putZonedReader(IndexReader reader, String indexPath) {
		synchronized(readers) {
			readers.put(indexPath, reader);
		}
	}
	
	private static IndexSearcher getZonedSearcher(String indexPath) {
		synchronized(searchers) {
			IndexSearcher searcher = (IndexSearcher)searchers.get(indexPath);
			return searcher;
		}
	}
	private static void putZonedSearcher(IndexSearcher searcher, String indexPath) {
		synchronized(searchers) {
			searchers.put(indexPath, searcher);
		}
	}
	
	private static IndexWriter getZonedWriter(String indexPath) {
		synchronized(writers) {
			IndexWriter writer = (IndexWriter)writers.get(indexPath);
			return writer;
		}
	}

	private static void putZonedWriter(IndexWriter writer, String indexPath) {
		synchronized(writers) {
			writers.put(indexPath, writer);
		}
	}
	
	public static IndexReader getReader(String indexPath) throws IOException {
		synchronized (LuceneHelper.class) {
			switch (prevState) {
			case (READ):
				if (getZonedReader(indexPath) != null)
					return (IndexReader)getZonedReader(indexPath);
			case (SEARCH):
				putZonedReader(getNewReader(indexPath), indexPath);
				break;
			case (WRITE):
				closeWriter(indexPath);
				closeReader(indexPath);
				putZonedReader(getNewReader(indexPath), indexPath);
			}
			prevState = READ;
		}
		return (IndexReader)getZonedReader(indexPath);
	}

	public static IndexSearcher getSearcher(String indexPath)
			throws IOException {
		synchronized (LuceneHelper.class) {
			switch (prevState) {
			case (SEARCH):
				if (getZonedSearcher(indexPath) != null)
					return getZonedSearcher(indexPath);
				putZonedSearcher(getNewSearcher(indexPath), indexPath);
				break;
			case (READ):
				closeReader(indexPath);
				prevState = SEARCH;
				putZonedSearcher(getNewSearcher(indexPath), indexPath);
				break;
			case (WRITE):
				if (getZonedWriter(indexPath) != null) getZonedWriter(indexPath).flush();
				if (getZonedSearcher(indexPath) != null) {
					getZonedSearcher(indexPath).close();
				}
				putZonedSearcher(getNewSearcher(indexPath), indexPath);
			}
		}
		return getZonedSearcher(indexPath);
	}

	public static IndexWriter getWriter(String indexPath, boolean create,
			boolean forOptimize) throws IOException {
		synchronized (LuceneHelper.class) {
			switch (prevState) {
			case (WRITE):
				if (getZonedWriter(indexPath) != null && !create)
					break;
			case (SEARCH):
				if (getZonedWriter(indexPath) != null) {
					getZonedWriter(indexPath).close();
				}
				putZonedWriter(getNewWriter(indexPath, create), indexPath);
				break;
			case (READ):
				closeReader(indexPath);
			putZonedWriter(getNewWriter(indexPath, create), indexPath);
			}
			if (forOptimize) {
				prevState = SEARCH;
			} else {
				prevState = WRITE;
			}
		}
		return getZonedWriter(indexPath);
	}

	private static IndexReader getNewReader(String indexPath)
			throws IOException {
		IndexReader indexReader = null;
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
		IndexWriter indexWriter = null;
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
		IndexSearcher indexSearcher = null;
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
				IndexWriter indexWriter = getWriter(indexPath, true, false);
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

	public static void closeAll(String indexPath) {
		synchronized (LuceneHelper.class) {
			closeWriter(indexPath);
			closeReader(indexPath);
			closeSearcher(indexPath);
			prevState = WRITE;
		}
	}

	// needed for searchers that are opened on existing readers.
	public static void closeSearcher(String indexPath) {
		try {
			getZonedSearcher(indexPath).close();
		} catch (Exception se) {
		}
		putZonedSearcher(null,indexPath);
	}

	public static void closeReader(String indexPath) {
		try {
			getZonedReader(indexPath).close();
		} catch (Exception se) {
		}
		putZonedReader(null,indexPath);
	}

	public static void closeWriter(String indexPath) {
		try {
			getZonedWriter(indexPath).close();
		} catch (Exception se) {
		}
		putZonedWriter(null,indexPath);
	}

	// unlock the index
	public static void unlock(String indexPath) {
		try {
			closeAll(indexPath);
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
