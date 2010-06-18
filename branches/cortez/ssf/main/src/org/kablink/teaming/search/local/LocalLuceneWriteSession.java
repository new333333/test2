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
package org.kablink.teaming.search.local;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.AclUpdater;
import org.apache.lucene.index.DocumentSelection;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kablink.teaming.lucene.ChineseAnalyzer;
import org.kablink.teaming.lucene.LanguageTaster;
import org.kablink.teaming.lucene.LuceneHelper;
import org.kablink.teaming.lucene.SsfIndexAnalyzer;
import org.kablink.teaming.search.LuceneException;
import org.kablink.teaming.search.LuceneWriteSession;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;


public class LocalLuceneWriteSession extends LocalLuceneSession implements LuceneWriteSession {

	private static final Log logger = LogFactory.getLog(LocalLuceneWriteSession.class);
	
	private static boolean debugEnabled = logger.isDebugEnabled();

	public LocalLuceneWriteSession(String indexPath) {
		this.indexPath = indexPath;		
	}

	public void addDocuments(ArrayList docs) {
		SimpleProfiler.startProfiler("LocalLuceneSession.addDocuments");

		IndexWriter indexWriter;
		long startTime = System.currentTimeMillis();
		//Runtime rt = Runtime.getRuntime();
		//rt.gc();
     	//rt.gc();
    	//System.out.println("adddoc START Heap size( " + docs.size() + " docs): " + (rt.totalMemory() - rt.freeMemory()));

		// block until updateDocs is completed
		synchronized (getRWLockObject()) {
			indexWriter = null;
			
			try {
				indexWriter = LuceneHelper.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				for (Iterator iter = docs.iterator(); iter.hasNext();) {
					Document doc = (Document) iter.next();
					if (doc.getField(Constants.UID_FIELD) == null)
						throw new IllegalArgumentException(
								"Document must contain a UID with field name "
										+ Constants.UID_FIELD);
					String tastingText = getTastingText(doc);
					try {
						SimpleProfiler.startProfiler("LocalLuceneSession.single_document");
						indexWriter.addDocument(doc, getAnalyzer(tastingText));
					} finally {
						SimpleProfiler.stopProfiler("LocalLuceneSession.single_document");
					}
				}
			} catch (IOException e) {
				throw new LuceneException(
						"Could not add document to the index [" + indexPath
								+ "]", e);
			} finally {
				try {
					indexWriter.flush();
				} catch (Exception e) {
					logger.warn(e);
				}

				/* 
				 try {
					indexWriter.close();
				} catch (IOException e) {
				}
				*/
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: addDocuments took: " + (endTime - startTime) + " milliseconds");

		SimpleProfiler.stopProfiler("LocalLuceneSession.addDocuments");
		//rt.gc();
     	//rt.gc();
    	//System.out.println("adddoc END Heap size: " + (rt.totalMemory() - rt.freeMemory()));
	}

	public void deleteDocuments(Term term) {
		SimpleProfiler.startProfiler("LocalLuceneSession.deleteDocuments(Term)");
		IndexWriter indexWriter;

		long startTime = System.currentTimeMillis();

		// block until updateDocs is completed
		synchronized (getRWLockObject()) {
			indexWriter = null;

			try {
				indexWriter = LuceneHelper.getWriter(indexPath); 
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				indexWriter.deleteDocuments(term);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not delete documents from the index ["
								+ indexPath + "]", e);
			} finally {
				try {
					indexWriter.flush();
				} catch (Exception e) {
					logger.warn(e);
				}
				/*
				try {
					indexReader.close();
				} catch (IOException e) {
				}
				*/
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: deleteDocuments(term) took: " + (endTime - startTime) + " milliseconds");

		SimpleProfiler.stopProfiler("LocalLuceneSession.deleteDocuments(Term)");

	}

	public void updateDocuments(Query query, String fieldname, String fieldvalue) {
		SimpleProfiler.startProfiler("LocalLuceneSession.updateDocuments(Query,String,String");
		
		try {
			updateDocs(query, fieldname, fieldvalue);
		} catch (Exception e) {
			throw new LuceneException("Error updating index [" + indexPath
					+ "]", e);
		}
		SimpleProfiler.stopProfiler("LocalLuceneSession.updateDocs(Query,String,String");
	}
		
	public void flush() {
		// Because Liferay's Lucene functions (on which this implementation
		// is based) are atomic in that it flushes out after each operation,
		// there is no separate flush to perform. Nothing to do.
	}

	public void optimize() {
		long startTime = System.currentTimeMillis();

		IndexWriter indexWriter = null;
		try {
			indexWriter = LuceneHelper.getWriterForOptimize(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open writer on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			indexWriter.optimize();
		} catch (IOException e) {
			throw new LuceneException("Could not add document to the index ["
					+ indexPath + "]", e);
		} finally {
			/*
			try {
				indexWriter.close();
			} catch (IOException e) {
			}
			*/
		}
		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: optimize took: " + (endTime - startTime) + " milliseconds");
	}
	
	private void updateDocs(Query q, String fieldname, String fieldvalue) {
		SimpleProfiler.startProfiler("localLucene_updateDocs");
		long start = 0L;
		// block every read/write while updateDocs is in progress
		synchronized (getRWLockObject()) {
			// first Optimize the index.
			IndexWriter indexWriter = null;

			//LuceneHelper.closeAll();
			LuceneHelper.closeWriter(indexPath);
			LuceneHelper.closeReader(indexPath);
			try {
				// need this writer to check for segment count and optimization of the index.
				// This allows searchers to remain open during optimize
				indexWriter = LuceneHelper.getWriterForOptimize(indexPath);
			} catch (IOException e) {
				throw new LuceneException("Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}
			
			try {
				// open a searcher for use by other threads while we're updating
				// the index.
				//LuceneHelper.getSearcher(indexPath);
				if (indexWriter.getSegmentCount() > 1) {
					start = System.currentTimeMillis();
					indexWriter.optimize();
					if(debugEnabled)
						logger.debug("LocalLucene: updateDocs(after optimize) took: "
													+ (System.currentTimeMillis() - start)
													+ " milliseconds");
				
				} 
				indexWriter.close();
				start = System.currentTimeMillis();
				doUpdate(q, fieldname, fieldvalue);
				if(debugEnabled)
					logger.debug("LocalLucene: updateDocs(doUpdate) took: "
													+ (System.currentTimeMillis() - start)
													+ " milliseconds");
			} catch (IOException ioe) {
				throw new LuceneException(
						"Could not update fields on the index ["
								+ this.indexPath + " ], query is: "
								+ q.toString() + " field: " + fieldname);
			} finally {
				SimpleProfiler.stopProfiler("localLucene_updateDocs");
			}

		}
	}
	

	private void doUpdate(Query q, String fieldname, String fieldvalue) {
		AclUpdater updater = null;
		long startTime = System.currentTimeMillis();			
		try {
			Directory indDir = LuceneHelper.getFSDirectory(indexPath);
			updater = new AclUpdater(indDir);
			DocumentSelection docsel = updater.createDocSelection(q);
			if (Validator.isNull(fieldvalue)) {
				fieldvalue = "xx";
				logger.error("Null acl value: " + q.toString());
			}
			if (docsel.size() != 0)
				updater.addField(new Field(fieldname, fieldvalue,
						Field.Store.NO, Field.Index.TOKENIZED),
						new SsfIndexAnalyzer(), docsel);
			synchronized (getSearchLockObject()) {
				LuceneHelper.closeSearcher(indexPath);
				updater.close();
			}
			updater = null;
		} catch (IOException ioe) {
			throw new LuceneException("Could not update fields on the index ["
					+ this.indexPath + " ], query is: " + q.toString()
					+ " field: " + fieldname);
		} finally {
			try {
				if (updater != null)
					updater.close();
			} catch (Exception e) {
				logger.warn(e);
			}
		}
		
		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: doUpdate took: " + (endTime - startTime) + " milliseconds");
	}
	
	/**
	 * Clear the index, only used at Zone creation
	 * 
	 * @return
	 * @throws LuceneException
	 */
	public void clearIndex() {
		long startTime = System.currentTimeMillis();

		LuceneHelper.closeAll(indexPath);
		
		try {
			LuceneHelper.getWriter(indexPath, true, false);
		} catch (IOException e) {
			throw new LuceneException(
					"Could not open writer on the index [" + this.indexPath
							+ "]", e);
		}
		/*
		try {
			indexWriter.close();
		} catch (Exception e) {}
		*/
		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: clearIndex took: " + (endTime - startTime) + " milliseconds");
	}
	
	private String getTastingText(Document doc) {
		String text = getTastingTextFromAllTextField(doc);
		if (text == null || text.length() == 0) {
			Field title = doc.getField(Constants.TITLE_FIELD);
			if (title != null) 
				text = title.stringValue();
		}
		if(text == null)
			text = "";
		if (text.length()> 1024) 
			return text.substring(0,1024);
		else 
			return text;
	}
	
	private String getTastingTextFromAllTextField(Document doc) {
		StringBuilder sb = new StringBuilder();
		Field[] allTextFields = doc.getFields(Constants.ALL_TEXT_FIELD);
		String piece;
		for(Field allTextField:allTextFields) {
			piece = allTextField.stringValue();
			if(piece != null && piece.length()>0) {
				if(sb.length() > 0)
					sb.append(" ");
				sb.append(piece);
			}
		}
		return sb.toString();
	}
	
	public static Analyzer getAnalyzer(String snippet) {
		// pass the snippet to the language taster and see which
		// analyzer to use
		String language = LanguageTaster.taste(snippet.toCharArray());
		if (language.equalsIgnoreCase(LanguageTaster.DEFAULT)) {
			return defaultAnalyzer;
		} else if (language.equalsIgnoreCase(LanguageTaster.CJK)) {
			PerFieldAnalyzerWrapper retAnalyzer = new PerFieldAnalyzerWrapper(new SsfIndexAnalyzer());
			retAnalyzer.addAnalyzer(Constants.ALL_TEXT_FIELD, new ChineseAnalyzer());
			retAnalyzer.addAnalyzer(Constants.DESC_FIELD, new ChineseAnalyzer());
			retAnalyzer.addAnalyzer(Constants.TITLE_FIELD, new ChineseAnalyzer());
			return retAnalyzer;
		} else if (language.equalsIgnoreCase(LanguageTaster.HEBREW)) {
			// return new HEBREWAnalyzer;
			Analyzer analyzer = defaultAnalyzer;
			String aName = SPropsUtil.getString("lucene.hebrew.analyzer", "");
			if (!aName.equalsIgnoreCase("")) {
				// load the hebrew analyzer here
				try {
					Class hebrewClass = ReflectHelper.classForName(aName);
					analyzer = (Analyzer) hebrewClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize hebrew analyzer class: "
							+ e.toString());
				}
			}
			return analyzer;
		} else {
			// return new ARABICAnalyzer;
			Analyzer analyzer = defaultAnalyzer;
			String aName = SPropsUtil.getString("lucene.arabic.analyzer", "");
			if (!aName.equalsIgnoreCase("")) {
				// load the arabic analyzer here
				try {
					Class arabicClass = ReflectHelper.classForName(aName);
					analyzer = (Analyzer) arabicClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize arabic analyzer class: "
							+ e.toString());
				}
			}
			return analyzer;
		}
	}
	
	public void backup() {
		IndexWriter indexWriter = null;
		String backupDir = indexPath;
		
		String bDir = SPropsUtil.getString("lucene.server.backup.dir","");
		if (!bDir.equalsIgnoreCase("")) {
			backupDir = bDir;
		}
		
		synchronized (getRWLockObject()) {
			LuceneHelper.closeAll(indexPath);
			// create a name for the copy directory
			SimpleDateFormat formatter =
			new SimpleDateFormat (".yyyy.MM.dd.HH.mm.ss");
			Date currentTime = new Date();
			String dateString = formatter.format(currentTime);
			String indexCopyPath = backupDir + dateString;
			// merge to that dir
			try {
				indexWriter = new IndexWriter(indexCopyPath, new SsfIndexAnalyzer(),
						true);
			} catch (Exception ie) {
				if (debugEnabled)
					logger.debug(ie);
				return;
			}
			indexWriter.setUseCompoundFile(false);
			try {
				indexWriter.addIndexes(new Directory[] { LuceneHelper.getFSDirectory(indexPath)});
				indexWriter.close();
			} catch (Exception ie) {
				if (debugEnabled)
					logger.debug(ie);	
			} finally {
				try {
					indexWriter.close();
				} catch (Exception e) {
					if (debugEnabled) {
						logger.debug(e);
					}
				}
			}
		}
	}

	public void addDeleteDocuments(ArrayList docsToAddOrDelete) throws LuceneException {
		SimpleProfiler.startProfiler("LocalLuceneSession.addDeleteDocuments");

		IndexWriter indexWriter;
		long startTime = System.currentTimeMillis();
		//Runtime rt = Runtime.getRuntime();
		//rt.gc();
     	//rt.gc();
    	//System.out.println("adddoc START Heap size( " + docs.size() + " docs): " + (rt.totalMemory() - rt.freeMemory()));

		// block until updateDocs is completed
		synchronized (getRWLockObject()) {
			indexWriter = null;
			
			try {
				indexWriter = LuceneHelper.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				for(Object obj : docsToAddOrDelete) {
					if(obj instanceof Document) {
						Document doc = (Document) obj;
						if (doc.getField(Constants.UID_FIELD) == null)
							throw new IllegalArgumentException(
									"Document must contain a UID with field name "
											+ Constants.UID_FIELD);
						String tastingText = getTastingText(doc);
						try {
							SimpleProfiler.startProfiler("LocalLuceneSession.single_add");
							indexWriter.addDocument(doc, getAnalyzer(tastingText));
						} finally {
							SimpleProfiler.stopProfiler("LocalLuceneSession.single_add");
						}						
					}
					else if(obj instanceof Term) {
						indexWriter.deleteDocuments((Term) obj);
					}
					else {
						throw new IllegalArgumentException("Invalid object type for indexing: " + obj.getClass().getName());
					}
				}
			} catch (IOException e) {
				throw new LuceneException(
						"Could not add document to the index [" + indexPath
								+ "]", e);
			} finally {
				try {
					indexWriter.flush();
				} catch (Exception e) {
					logger.warn(e);
				}

				/* 
				 try {
					indexWriter.close();
				} catch (IOException e) {
				}
				*/
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: addDeleteDocuments took: " + (endTime - startTime) + " milliseconds");

		SimpleProfiler.stopProfiler("LocalLuceneSession.addDeleteDocuments");
		//rt.gc();
     	//rt.gc();
    	//System.out.println("adddoc END Heap size: " + (rt.totalMemory() - rt.freeMemory()));	
	}

}
