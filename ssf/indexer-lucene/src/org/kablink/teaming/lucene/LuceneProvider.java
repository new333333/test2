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
package org.kablink.teaming.lucene;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.kablink.teaming.lucene.ChineseAnalyzer;
import org.kablink.util.PropsUtil;
import org.kablink.util.search.Constants;

public class LuceneProvider {
	private static Analyzer defaultAnalyzer = new SsfIndexAnalyzer();

	private static HashMap<String, Object> rwLockTable = new HashMap<String, Object>();
	
	private static HashMap<String, Object> searchLockTable = new HashMap<String, Object>();
	
	private String indexPath;
	
	protected static final Log logger = LogFactory.getLog(LuceneProvider.class);
	protected static boolean debugEnabled = logger.isDebugEnabled();
	
	// Copied from WebKeys
	private static final String FIND_TYPE_PERSONAL_TAGS = "personalTags";
	private static final String FIND_TYPE_COMMUNITY_TAGS = "communityTags";
	
	public LuceneProvider() {}
	
	public LuceneProvider(String indexPath) {
		this.indexPath = indexPath;
	}
	
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}
	public String getIndexPath() {
		return indexPath;
	}
	
	public void addDocuments(ArrayList docs) throws LuceneException {
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
				for (Iterator iter = docs.iterator(); iter.hasNext();) {
					Document doc = (Document) iter.next();
					if (doc.getField(Constants.UID_FIELD) == null)
						throw new IllegalArgumentException(
								"Document must contain a UID with field name "
										+ Constants.UID_FIELD);
					String tastingText = getTastingText(doc);
					indexWriter.addDocument(doc, getAnalyzer(tastingText));
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
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("addDocuments took: " + (endTime - startTime) + " milliseconds");
	}

	public void deleteDocuments(Term term) throws LuceneException {
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
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("deleteDocuments(term) took: " + (endTime - startTime) + " milliseconds");
	}

	public void flush() throws LuceneException {
		// Because Liferay's Lucene functions (on which this implementation
		// is based) are atomic in that it flushes out after each operation,
		// there is no separate flush to perform. Nothing to do.
	}

	public void optimize() throws LuceneException {
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
			logger.debug("optimize took: " + (endTime - startTime) + " milliseconds");
	}
		
	/**
	 * Clear the index, only used at Zone creation
	 * 
	 * @return
	 * @throws LuceneException
	 */
	public void clearIndex() throws LuceneException {
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
			logger.debug("clearIndex took: " + (endTime - startTime) + " milliseconds");
	}

	private String getTastingText(Document doc) {
		String text = "";
		Field allText = doc.getField(Constants.ALL_TEXT_FIELD);
		if (allText != null) 
			text = allText.stringValue();
		if (text.length() == 0) {
			Field title = doc.getField(Constants.TITLE_FIELD);
			if (title != null) 
				text = title.stringValue();
		}
		if (text.length()> 1024) 
			return text.substring(0,1024);
		else return text;
	}
	
	public Analyzer getAnalyzer(String snippet) {
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
			String aName = PropsUtil.getString("lucene.hebrew.analyzer", "");
			if (!aName.equalsIgnoreCase("")) {
				// load the hebrew analyzer here
				try {
					Class hebrewClass = LuceneHelper.classForName(aName);
					analyzer = (Analyzer) hebrewClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize hebrew analyzer class", e);
				}
			}
			return analyzer;
		} else {
			// return new ARABICAnalyzer;
			Analyzer analyzer = defaultAnalyzer;
			String aName = PropsUtil.getString("lucene.arabic.analyzer", "");
			if (!aName.equalsIgnoreCase("")) {
				// load the arabic analyzer here
				try {
					Class arabicClass = LuceneHelper.classForName(aName);
					analyzer = (Analyzer) arabicClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize arabic analyzer class", e);
				}
			}
			return analyzer;
		}
	}
	
	public void backup() {
		IndexWriter indexWriter = null;
		String backupDir = indexPath;
		
		String bDir = PropsUtil.getString("lucene.server.backup.dir","");
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
				logger.warn("Error in backup", ie);
				return;
			}
			indexWriter.setUseCompoundFile(false);
			try {
				indexWriter.addIndexes(new Directory[] { LuceneHelper.getFSDirectory(indexPath)});
				indexWriter.close();
			} catch (Exception ie) {
				logger.warn("Error in backup", ie);		
			} finally {
				try {
					indexWriter.close();
				} catch (Exception e) {
					logger.warn("Error in backup", e);
				}
			}
		}
	}

	public void addDeleteDocuments(ArrayList docsToAddOrDelete) throws LuceneException {
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
				for(Object obj : docsToAddOrDelete) {
					if(obj instanceof Document) {
						Document doc = (Document) obj;
						if (doc.getField(Constants.UID_FIELD) == null)
							throw new IllegalArgumentException(
									"Document must contain a UID with field name "
											+ Constants.UID_FIELD);
						String tastingText = getTastingText(doc);
						indexWriter.addDocument(doc, getAnalyzer(tastingText));						
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
			logger.debug("addDeleteDocuments took: " + (endTime - startTime) + " milliseconds");	
	}

	
	public org.kablink.teaming.lucene.Hits search(Query query) throws LuceneException {
		return this.search(query, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Query query, int offset,
			int size) throws LuceneException {
		IndexSearcher indexSearcher = null;
		long startTime = System.currentTimeMillis();

		synchronized (getSearchLockObject()) {
			try {
				indexSearcher = LuceneHelper.getSearcher(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open searcher on the index ["
								+ this.indexPath + "]", e);
			}

			try {
				org.apache.lucene.search.Hits hits = indexSearcher
						.search(query);
				if (size < 0)
					size = hits.length();
				org.kablink.teaming.lucene.Hits tempHits = org.kablink.teaming.lucene.Hits
						.transfer(hits, offset, size);
				tempHits.setTotalHits(hits.length());
				long endTime = System.currentTimeMillis();
				if (debugEnabled)
					logger.debug("LocalLucene: search took: "
							+ (endTime - startTime) + " milliseconds");
				return tempHits;
			} catch (IOException e) {
				throw new LuceneException("Error searching index [" + indexPath
						+ "]", e);
			} finally {
				/*
				 * 
				 * try { indexSearcher.close(); } catch (IOException e) { }
				 */
			}
		}
	}

	public org.kablink.teaming.lucene.Hits search(Query query, Sort sort) throws LuceneException {
		return this.search(query, sort, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Query query, Sort sort,
			int offset, int size) throws LuceneException {
		long startTime = System.currentTimeMillis();

		Hits hits = null;
		IndexSearcher indexSearcher = null;

		synchronized (getSearchLockObject()) {
			try {

				indexSearcher = LuceneHelper.getSearcher(indexPath);

			} catch (IOException e) {
				throw new LuceneException(
						"Could not open searcher on the index ["
								+ this.indexPath + "]", e);
			}

			try {
				if (sort == null)
					hits = indexSearcher.search(query);
				else
					try {
						hits = indexSearcher.search(query, sort);
					} catch (Exception ex) {
						hits = indexSearcher.search(query);
					}
				if (size < 0)
					size = hits.length();
				org.kablink.teaming.lucene.Hits tempHits = org.kablink.teaming.lucene.Hits
						.transfer(hits, offset, size);
				tempHits.setTotalHits(hits.length());
				long endTime = System.currentTimeMillis();
				if (debugEnabled)
					logger.debug("LocalLucene: search took: "
							+ (endTime - startTime) + " milliseconds");
				return tempHits;
			} catch (IOException e) {
				throw new LuceneException("Error searching index [" + indexPath
						+ "]", e);
			} finally {
				/*
				 * try { indexSearcher.close(); } catch (IOException e) { }
				 */
			}
		}
	}

	public ArrayList getTags(Query query, String tag, String type, String userId, boolean isSuper)
	throws LuceneException {
		ArrayList<String> resultTags = new ArrayList<String>();
		ArrayList tagObjects = getTagsWithFrequency(query, tag, type, userId, isSuper);
		Iterator iter = tagObjects.iterator();
		while (iter.hasNext()) {
			resultTags.add(((TagObject)iter.next()).getTagName());
		}
		return resultTags;
	}	
	
	/**
	 * Get all the unique tags that this user can see, based on the wordroot
	 * passed in.
	 * 
	 * @param query
	 *            can be null for superuser
	 * @param tag
	 * @param type
	 * @return
	 * @throws RemoteException
	 * getTags(Query query, Long id, String tag, String type, boolean isSuper)
	 */
	public ArrayList getTagsWithFrequency(Query query, String tag, String type, String userId, boolean isSuper)
			throws LuceneException {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		int prefixLength = 0;
		tag = tag.toLowerCase();
		
		TreeSet<TagObject> results = new TreeSet<TagObject>();
		ArrayList<TagObject> resultTags = new ArrayList<TagObject>();
		long startTime = System.currentTimeMillis();

		// block until updateDocs is completed
		try {
			synchronized (getRWLockObject()) {

				try {
					indexReader = LuceneHelper.getReader(indexPath);
					indexSearcher = LuceneHelper.getSearcher(indexReader);
				} catch (IOException e) {
					throw new LuceneException(
							"Could not open reader on the index ["
									+ this.indexPath + "]", e);
				}
				try {
					final BitSet userDocIds = new BitSet(indexReader.maxDoc());
					if (!isSuper) {
						indexSearcher.search(query, new HitCollector() {
							public void collect(int doc, float score) {
								userDocIds.set(doc);
							}
						});
					} else {
						userDocIds.set(0, userDocIds.size());
					}
					LuceneHelper.closeSearcher(indexPath);
					String[] fields = null;
					if (type != null
							&& type
									.equals(FIND_TYPE_PERSONAL_TAGS)) {
						fields = new String[1];
						fields[0] = Constants.ACL_TAG_FIELD_TTF;
					} else if (type != null
							&& type
									.equals(FIND_TYPE_COMMUNITY_TAGS)) {
						fields = new String[1];
						fields[0] = Constants.TAG_FIELD_TTF;
					} else {
						fields = new String[2];
						fields[0] = Constants.TAG_FIELD_TTF;
						fields[1] = Constants.ACL_TAG_FIELD_TTF;
					}
					int preTagLength = 0;
					for (int i = 0; i < fields.length; i++) {
						if (fields[i].equalsIgnoreCase(Constants.ACL_TAG_FIELD_TTF)) {
							String preTag = Constants.TAG_ACL_PRE
									+ userId + Constants.TAG;
							preTagLength = preTag.length();
							tag = preTag + tag;
						}
						TermEnum enumerator = indexReader.terms(new Term(
								fields[i], tag));

						TermDocs termDocs = indexReader.termDocs();
						if (enumerator.term() == null) {
							// no matches
							return null;
						}
						do {
							Term term = enumerator.term();
							// stop when the field is no longer the field we're
							// looking
							// for, or, when
							// the term doesn't startwith the string we're
							// matching.
							if (term.field().compareTo(fields[i]) != 0
									|| !term.text().startsWith(tag)) {
								break; // no longer in '_tagField' field
							}
							termDocs.seek(enumerator);
							while (termDocs.next()) {
								if (userDocIds.get((termDocs.doc()))) {
									// Add term.text to results
									prefixLength = preTagLength + term.text().indexOf(":") + 1;
									TagObject tagObj = new TagObject();
									tagObj.setTagName(term.text().substring(prefixLength));
									tagObj.setTagFreq(termDocs.freq());
									results.add(tagObj);
									break;
								}
							}
						} while (enumerator.next());
					}
				} catch (Exception e) {
					logger.warn("Error getting tags", e);
				}

				Iterator iter = results.iterator();
				while (iter.hasNext())
					resultTags.add((TagObject) iter.next());
				long endTime = System.currentTimeMillis();
				if (debugEnabled)
					logger.debug("getTags took: "
							+ (endTime - startTime) + " milliseconds");

				return resultTags;
			}
		} finally {
			LuceneHelper.closeSearcher(indexPath);
		}
	}

	
	/**
	 * Get all the sort titles that this user can see, and return a skip list
	 * 
	 * @param query can be null for superuser
	 * @param start
	 * @param end
	 * @return
	 * @throws LuceneException
	 */
	
	// This returns an arraylist of arraylists.  Each child arraylist has 2 strings, (RangeStart, RangeEnd)
	// i.e. results[0] = {a, c}
	//      results[1] = {d, g}
	
	public String[] getNormTitles(Query query, String start, String end, int skipsize)
			throws LuceneException {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<ArrayList> resultTitles = new ArrayList<ArrayList>();
		int count = 0;
		String lastTerm = "";
		long startTime = System.currentTimeMillis();
		//User user = RequestContextHolder.getRequestContext().getUser();

		// block until updateDocs is completed
		try {
			synchronized (getRWLockObject()) {

				try {
					indexReader = LuceneHelper.getReader(indexPath);
					indexSearcher = LuceneHelper.getSearcher(indexReader);
				} catch (IOException e) {
					throw new LuceneException(
							"Could not open reader on the index ["
									+ this.indexPath + "]", e);
				}
				try {
					final BitSet userDocIds = new BitSet(indexReader.maxDoc());
					
					indexSearcher.search(query, new HitCollector() {
						public void collect(int doc, float score) {
							userDocIds.set(doc);
						}
					});
					LuceneHelper.closeSearcher(indexPath);
					String field = Constants.NORM_TITLE;
						TermEnum enumerator = indexReader.terms(new Term(
								field, start));

						TermDocs termDocs = indexReader.termDocs();
						if (enumerator.term() == null) {
							// no matches
							return null;
						}
						do {
							Term term = enumerator.term();
							// stop when the field is no longer the field we're
							// looking for, or, when the term is beyond the end term
							if (term.field().compareTo(field) != 0)
								break;
							if ((!"".equalsIgnoreCase(end)) && (term.text().compareTo(end) > 0)) {
								break; // no longer in '_tagField' field
							}
							termDocs.seek(enumerator);
							while (termDocs.next()) {
								if (userDocIds.get((termDocs.doc()))) {
									// add terms in ranges, i.e. if the skipsize is 7, add 0,6,7,13,14,20,21
									// so the ranges can be 0-6, 7-13, 14-20, etc
									if ((count == 0) || (count%skipsize == skipsize-1) || (count%skipsize == 0)) {
										titles.add((String)term.text());
										lastTerm = (String) term.text();
										count++;
										break;
									}
									lastTerm = (String) term.text();
									count++;
									break;
								}
							}
						} while (enumerator.next());
						// if the size is odd, then add the final term to the end of the list
						// if the final range is just the last term itself, then drop 
						// the final range, and modify the previous range to include the final
						// term.
						int tsize = titles.size();
						if ((tsize%2 ==1) && (tsize > 1)) {
							if (lastTerm.equals(titles.get(tsize-1))) {
								titles.set(tsize-2, lastTerm);
								titles.remove(tsize-1);
							} else {
								titles.add(lastTerm);
							}
						}

				} catch (Exception e) {
					logger.warn("Error getting titles", e);
				}

				String[] retArray = new String[titles.size()];
			    int i = 0;
			    for ( String s : titles ) {
			        retArray[i] = s;
			        i++;
			    }
			    
				long endTime = System.currentTimeMillis();
				if (debugEnabled)
					logger.debug("getNormTitles took: "
							+ (endTime - startTime) + " milliseconds");
				
				return retArray;
			}
		} finally {
			LuceneHelper.closeSearcher(indexPath);
		}
	}
	
	public ArrayList getNormTitlesAsList(Query query, String start, String end,
			int skipsize) throws LuceneException {
		String[] normResults = getNormTitles(query, start, end, skipsize);
		
		ArrayList<ArrayList> resultTitles = new ArrayList<ArrayList>();
		ArrayList titles =  new ArrayList<String>(Arrays.asList(normResults));
		Iterator iter = titles.iterator();
		while (iter.hasNext()) {
			ArrayList<String> tuple = new ArrayList<String>();
			if (!iter.hasNext())
				break;
			tuple.add((String) iter.next());
			if (!iter.hasNext())
				break;
			tuple.add((String) iter.next());
			resultTitles.add(tuple);
		}	
		return resultTitles;
	}
	
	protected Object getRWLockObject() {
		synchronized(RWLock.class) {
			Object obj = rwLockTable.get(indexPath);
			if (obj == null) {
				obj = new Object();
				rwLockTable.put(indexPath, obj);
			}
			return obj;
		}
	}
	
	protected Object getSearchLockObject () {
		synchronized(SearchLock.class) {
			Object obj = searchLockTable.get(indexPath);
			if (obj == null) {
				obj = new Object();
				searchLockTable.put(indexPath, obj);
			}
			return obj;
		}
	}
}
class RWLock {}
class SearchLock {}
