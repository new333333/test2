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
package com.sitescape.team.search.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import org.apache.lucene.search.TermQuery;
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
	private Query warmingQuery = new TermQuery(new Term(BasicIndexUtils.ALL_TEXT_FIELD, "sitescape"));
	// Note: I'm not convinced that this implementation makes good use of
	// Lucene,
	// primarily due to my lack of intimiate knowledge of Lucene.
	// Two major implementation questions:
	// 1) Is it allowed to open more than one IndexWriter (using multiple
	// instances of sessions in multiple threads) in a program?
	// What are the ramifications when we do that?
	// 2) What is the best practise around using IndexSearcher? Specifically,
	// should it be opened and closed for each query or re-used for multiple
	// queries? If re-used for extended period of time, then I assume that
	// the index snapshot represented by the handle won't incorporate the
	// changes that may have been made to the index since the handle was
	// obtained. Is that right?

	// Updated Note: This implementation is rewritten to simply use Liferay's
	// local index support, which is not very scalable. Since local index
	// configuration should only be used for testing and/or demo installation,
	// I wouldn't bother with making this a production quality service.

	private static final Log logger = LogFactory
			.getLog(LocalLuceneSession.class);

	private static boolean debugEnabled = logger.isDebugEnabled();
	
	private String indexPath;

	public LocalLuceneSession(String indexPath) {
		this.indexPath = indexPath;
		// WARM UP the index by issuing a single query
		search(warmingQuery);
		
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
					String[] fields = null;
					if (type != null
							&& type
									.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_PERSONAL_TAGS)) {
						fields = new String[1];
						fields[0] = BasicIndexUtils.ACL_TAG_FIELD;
					} else if (type != null
							&& type
									.equals(WebKeys.USER_SEARCH_USER_GROUP_TYPE_COMMUNITY_TAGS)) {
						fields = new String[1];
						fields[0] = BasicIndexUtils.TAG_FIELD;
					} else {
						fields = new String[2];
						fields[0] = BasicIndexUtils.TAG_FIELD;
						fields[1] = BasicIndexUtils.ACL_TAG_FIELD;
					}
					int preTagLength = 0;
					for (int i = 0; i < fields.length; i++) {
						if (fields[i].equalsIgnoreCase("_aclTagField")) {
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
									results.add(term.text().substring(preTagLength));
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
		logger.info("LocalLucene: optimize took: " + (endTime - startTime) + " milliseconds");
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
		IndexUpdater updater = null;
		long startTime = System.currentTimeMillis();

		try {
			Directory indDir = FSDirectory.getDirectory(indexPath);
			updater = new IndexUpdater(indDir);
			DocumentSelection docsel = updater.createDocSelection(q);
			if (docsel.size() != 0)
				updater.updateField(new Field(fieldname, fieldvalue,
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
		
		IndexWriter indexWriter = null;
		try {
			indexWriter = LuceneHelper.getWriter(indexPath, true);
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
		logger.info("LocalLucene: clearIndex took: " + (endTime - startTime) + " milliseconds");
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
			return new ChineseAnalyzer();
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
}
