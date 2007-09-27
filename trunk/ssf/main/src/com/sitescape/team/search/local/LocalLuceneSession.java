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
package com.sitescape.team.search.local;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.AclUpdater;
import org.apache.lucene.index.DocumentSelection;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexUpdater;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.lucene.ChineseAnalyzer;
import com.sitescape.team.lucene.LanguageTaster;
import com.sitescape.team.lucene.LuceneHelper;
import com.sitescape.team.lucene.SsfIndexAnalyzer;
import com.sitescape.team.lucene.SsfQueryAnalyzer;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.LuceneException;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.WebKeys;

/**
 * This implementation provides access to local Lucene index.
 * 
 * @author Jong Kim
 * 
 */
public class LocalLuceneSession implements LuceneSession {
	Object SyncObj = new Object();
	
	private static Analyzer defaultAnalyzer = new SsfIndexAnalyzer();
	
	private static final Log logger = LogFactory
			.getLog(LocalLuceneSession.class);

	private static boolean debugEnabled = logger.isDebugEnabled();
	
	private String indexPath;

	public LocalLuceneSession(String indexPath) {
		this.indexPath = indexPath;		
	}

	public void addDocument(Document doc) {

		long startTime = System.currentTimeMillis();

		SimpleProfiler.startProfiler("LocalLuceneSession.addDocument");

		IndexWriter indexWriter;
		if (doc.getField(BasicIndexUtils.UID_FIELD) == null)
			throw new LuceneException(
					"Document must contain a UID with field name "
							+ BasicIndexUtils.UID_FIELD);
		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
			indexWriter = null;
			String tastingText = getTastingText(doc);
			try {
				indexWriter = LuceneHelper.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}

			try {
					indexWriter.addDocument(doc, getAnalyzer(tastingText));
			} catch (IOException e) {
				throw new LuceneException(
						"Could not add document to the index [" + indexPath
								+ "]", e);
			} finally {
				try {
					indexWriter.flush();
				} catch (Exception e) {}
				/* try {
					indexWriter.close();
				} catch (IOException e) {
				}
				*/
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: addDocument took: " + (endTime - startTime) + " milliseconds");

		SimpleProfiler.stopProfiler("LocalLuceneSession.addDocument");

	}

	public void addDocuments(Collection docs) {
		SimpleProfiler.startProfiler("LocalLuceneSession.addDocuments");

		IndexWriter indexWriter;
		long startTime = System.currentTimeMillis();
		//Runtime rt = Runtime.getRuntime();
		//rt.gc();
     	//rt.gc();
    	//System.out.println("adddoc START Heap size( " + docs.size() + " docs): " + (rt.totalMemory() - rt.freeMemory()));

		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
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
					if (doc.getField(BasicIndexUtils.UID_FIELD) == null)
						throw new LuceneException(
								"Document must contain a UID with field name "
										+ BasicIndexUtils.UID_FIELD);
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
				} catch (Exception e) {}

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

	public void deleteDocument(String uid) {
		deleteDocuments(new Term(BasicIndexUtils.UID_FIELD, uid));
	}

	public void deleteDocuments(Term term) {
		SimpleProfiler.startProfiler("LocalLuceneSession.deleteDocuments(Term)");
		IndexWriter indexWriter;

		long startTime = System.currentTimeMillis();

		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
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
				} catch (Exception e) {}
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

	public void deleteDocuments(Query query) {
		SimpleProfiler.startProfiler("LocalLuceneSession.deleteDocuments(Query)");
		IndexSearcher indexSearcher;

		long startTime = System.currentTimeMillis();
		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
			indexSearcher = null;

			try {
				indexSearcher = LuceneHelper.getSearcher(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open searcher on the index ["
								+ this.indexPath + "]", e);
			}

			try {
				deleteDocs(indexSearcher.search(query));
			} catch (IOException e) {
				throw new LuceneException("Error searching index [" + indexPath
						+ "]", e);
			} finally {
				/*
				try {
					indexSearcher.close();
				} catch (IOException e) {
				}
				*/
			}
		}

		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: deleteDocuments(query) took: " + (endTime - startTime) + " milliseconds");

		SimpleProfiler.stopProfiler("LocalLuceneSession.deleteDocuments");

	}

	public void updateDocument(String uid, String fieldname, String fieldvalue) {
		// build the query
		Query q = null;
		QueryParser qp = new QueryParser(BasicIndexUtils.ALL_TEXT_FIELD,
				new SsfQueryAnalyzer());
		try {
			q = qp.parse(BasicIndexUtils.UID_FIELD + ":" + uid);
		} catch (ParseException pe) {
			throw new LuceneException(pe.toString());
		}
		updateDocuments(q, fieldname, fieldvalue);
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
	
	public void updateDocuments(ArrayList<Query> queries, String fieldname, ArrayList<String> values) {
		SimpleProfiler.startProfiler("LocalLuceneSession.updateDocuments(ArrayList,String,ArrayList");
		
		try {
			updateDocs(queries, fieldname, values);
		} catch (Exception e) {
			throw new LuceneException("Error updating index [" + indexPath
					+ "]", e);
		}
		SimpleProfiler.stopProfiler("LocalLuceneSession.updateDocs(ArrayList,String,ArrayList");
	}

	public com.sitescape.team.lucene.Hits search(Query query) {
		return this.search(query, 0, -1);
	}

	public com.sitescape.team.lucene.Hits search(Query query, int offset, int size) {
		IndexSearcher indexSearcher = null;

		long startTime = System.currentTimeMillis();

		try {
			indexSearcher = LuceneHelper.getSearcher(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open searcher on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			org.apache.lucene.search.Hits hits = indexSearcher.search(query);
			if (size < 0)
				size = hits.length();
			com.sitescape.team.lucene.Hits tempHits = com.sitescape.team.lucene.Hits
					.transfer(hits, offset, size);
			tempHits.setTotalHits(hits.length());
			long endTime = System.currentTimeMillis();
			if(debugEnabled)
				logger.debug("LocalLucene: search took: " + (endTime - startTime) + " milliseconds");
			return tempHits;
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			/*

			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
			*/
		}
	}

	public com.sitescape.team.lucene.Hits search(Query query, Sort sort) {
		return this.search(query, sort, 0, -1);
	}

	public com.sitescape.team.lucene.Hits search(Query query, Sort sort,
			int offset, int size) {
		long startTime = System.currentTimeMillis();

		Hits hits = null;
		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = LuceneHelper.getSearcher(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open searcher on the index ["
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
			com.sitescape.team.lucene.Hits tempHits = com.sitescape.team.lucene.Hits
					.transfer(hits, offset, size);
			tempHits.setTotalHits(hits.length());
			long endTime = System.currentTimeMillis();
			if(debugEnabled)
				logger.debug("LocalLucene: search took: " + (endTime - startTime) + " milliseconds");
			return tempHits;
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			/*
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
			*/
		}		
	}
	/**
	 * Get all the unique tags that this user can see, based on the wordroot passed in.
	 * 
	 * @param query can be null for superuser
	 * @param tag
	 * @param type 
	 * @return
	 * @throws LuceneException
	 */
	public ArrayList getTags(Query query, String tag, String type)
			throws LuceneException {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		;
		TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		ArrayList<String> resultTags = new ArrayList<String>();
		User user = RequestContextHolder.getRequestContext().getUser();
		long startTime = System.currentTimeMillis();
		int prefixLength = 0;

		tag = tag.toLowerCase();
		// block until updateDocs is completed
		try {
			synchronized (LocalLuceneSession.class) {

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
					if (!user.isSuper()) {
						indexSearcher.search(query, new HitCollector() {
							public void collect(int doc, float score) {
								userDocIds.set(doc);
							}
						});
					} else {
						userDocIds.set(0, userDocIds.size());
					}
					LuceneHelper.closeSearcher();
					String[] fields = null;
					if (type != null
							&& type
									.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PERSONAL_TAGS)) {
						fields = new String[1];
						fields[0] = BasicIndexUtils.ACL_TAG_FIELD_TTF;
					} else if (type != null
							&& type
									.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_COMMUNITY_TAGS)) {
						fields = new String[1];
						fields[0] = BasicIndexUtils.TAG_FIELD_TTF;
					} else {
						fields = new String[2];
						fields[0] = BasicIndexUtils.TAG_FIELD_TTF;
						fields[1] = BasicIndexUtils.ACL_TAG_FIELD_TTF;
					}
					int preTagLength = 0;
					for (int i = 0; i < fields.length; i++) {
						if (fields[i].equalsIgnoreCase(BasicIndexUtils.ACL_TAG_FIELD_TTF)) {
							String preTag = BasicIndexUtils.TAG_ACL_PRE
									+ RequestContextHolder.getRequestContext()
											.getUserId().toString()
									+ BasicIndexUtils.TAG;
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
									results.add(term.text().substring(prefixLength));
									break;
								}
							}
						} while (enumerator.next());
					}
				} catch (Exception e) {
					System.out.println(e.toString());
				}

				Iterator iter = results.iterator();
				while (iter.hasNext())
					resultTags.add((String)iter.next());
				long endTime = System.currentTimeMillis();
				if(debugEnabled)
					logger.debug("LocalLucene: getTags took: " + (endTime - startTime) + " milliseconds");

				return resultTags;
			}
		} finally {
			LuceneHelper.closeSearcher();
		}
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
			indexWriter = LuceneHelper.getWriter(indexPath);
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

	public void close() {
		// Nothing to do
	}

	private int deleteDocs(org.apache.lucene.search.Hits hits) {
		int length = hits.length();
		long startTime = System.currentTimeMillis();

		if (length > 0) {
			IndexReader indexReader = null;
			try {
				indexReader = LuceneHelper.getReader(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open reader on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				for (int i = 0; i < length; i++) {
					int docId = hits.id(i);
					indexReader.deleteDocument(docId);
				}
			} catch (IOException e) {
				throw new LuceneException(
						"Could not delete documents from the index ["
								+ indexPath + "]", e);
			} finally {
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
			logger.debug("LocalLucene: deleteDocs took: " + (endTime - startTime) + " milliseconds");

		return length;
	}

	private void updateDocs(Query q, String fieldname, String fieldvalue) {
		long startTime = System.currentTimeMillis();
		//block every read/write while updateDocs is in progress
		synchronized (LocalLuceneSession.class) {
			// first Optimize the index.
			IndexWriter indexWriter = null;

			try {
				indexWriter = LuceneHelper.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}
			try {
				indexWriter.optimize();
				/*ROY indexWriter.close();
				 * 
				 */
				doUpdate(q,fieldname,fieldvalue);
			} catch (IOException ioe) {
				throw new LuceneException(
						"Could not update fields on the index ["
								+ this.indexPath + " ], query is: "
								+ q.toString() + " field: " + fieldname);
			} 
		}
		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: updateDocs(query) took: " + (endTime - startTime) + " milliseconds");
	}

	private void updateDocs(ArrayList<Query> queries, String fieldname, ArrayList<String> values) {
		long startTime = System.currentTimeMillis();
		//block every read/write while updateDocs is in progress
		int count = 0;
		synchronized (LocalLuceneSession.class) {
			// first Optimize the index.
			IndexWriter indexWriter = null;

			try {
				indexWriter = LuceneHelper.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}
			try {
				indexWriter.optimize();
				LuceneHelper.closeAll();
				/*ROY indexWriter.close();*/
				for (count = 0; count < queries.size(); count++)
					doUpdate(queries.get(count),fieldname,values.get(count));
			} catch (IOException ioe) {
				throw new LuceneException(
						"Could not update fields on the index ["
								+ this.indexPath + " ], query is: "
								+ queries.get(count).toString() + " field: " + fieldname);
			} 
		}
		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: updateDocs(list) took: " + (endTime - startTime) + " milliseconds");
	}

	private void doUpdate(Query q, String fieldname, String fieldvalue) {
		AclUpdater updater = null;
		long startTime = System.currentTimeMillis();

		try {
			Directory indDir = FSDirectory.getDirectory(indexPath);
			updater = new AclUpdater(indDir);
			DocumentSelection docsel = updater.createDocSelection(q);
			if (fieldvalue.equalsIgnoreCase("")) fieldvalue = "xx";
			if (docsel.size() != 0)
				updater.addField(new Field(fieldname, fieldvalue,
						Field.Store.NO, Field.Index.TOKENIZED),
						new SsfIndexAnalyzer(), docsel);
			updater.close();
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
			}
		}
		long endTime = System.currentTimeMillis();
		if(debugEnabled)
			logger.debug("LocalLucene: doUpdate took: " + (endTime - startTime) + " milliseconds");
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
	
	public ArrayList getNormTitles(Query query, String start, String end, int skipsize)
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
			synchronized (LocalLuceneSession.class) {

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
					LuceneHelper.closeSearcher();
					String field = EntityIndexUtils.NORM_TITLE;
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
							if ((end != "") && (term.text().compareTo(end) > 0)) {
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
					System.out.println(e.toString());
				}

				Iterator iter = titles.iterator();
				while (iter.hasNext()) {
					ArrayList<String> tuple= new ArrayList<String>();
					if (!iter.hasNext()) break;
					tuple.add((String)iter.next());
					if (!iter.hasNext()) break;
					tuple.add((String)iter.next());
					resultTitles.add(tuple);
				}
				long endTime = System.currentTimeMillis();
				if(debugEnabled)
					logger.debug("LocalLucene: getNormTitles took: " + (endTime - startTime) + " milliseconds");

				return resultTitles;
			}
		} finally {
			LuceneHelper.closeSearcher();
		}
	}
	
	/**
	 * Clear the index, only used at Zone creation
	 * 
	 * @return
	 * @throws LuceneException
	 */
	public void clearIndex() {
		long startTime = System.currentTimeMillis();

		LuceneHelper.closeAll();
		
		try {
			LuceneHelper.getWriter(indexPath, true);
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
		String text = doc.getField(BasicIndexUtils.ALL_TEXT_FIELD).stringValue();
		if (text.length()> 1024) 
			return text.substring(0,1024);
		else return text;
	}
	
	public static Analyzer getAnalyzer(String snippet) {
		// pass the snippet to the language taster and see which
		// analyzer to use
		String language = LanguageTaster.taste(snippet.toCharArray());
		if (language.equalsIgnoreCase(LanguageTaster.DEFAULT)) {
			return defaultAnalyzer;
		} else if (language.equalsIgnoreCase(LanguageTaster.CJK)) {
			PerFieldAnalyzerWrapper retAnalyzer = new PerFieldAnalyzerWrapper(new ChineseAnalyzer());
			retAnalyzer.addAnalyzer(BasicIndexUtils.FOLDER_ACL_FIELD, new SsfIndexAnalyzer());
			retAnalyzer.addAnalyzer(BasicIndexUtils.ENTRY_ACL_FIELD, new SsfIndexAnalyzer());
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
		
		synchronized (LocalLuceneSession.class) {
			LuceneHelper.closeAll();
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
				indexWriter.addIndexes(new Directory[] { FSDirectory.getDirectory(indexPath)});
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
}
