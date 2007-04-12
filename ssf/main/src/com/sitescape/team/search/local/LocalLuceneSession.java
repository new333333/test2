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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.lucene.SsfIndexAnalyzer;
import com.sitescape.team.lucene.SsfQueryAnalyzer;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.LuceneException;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.util.LuceneUtil;
import com.sitescape.team.web.WebKeys;

/**
 * This implementation provides access to local Lucene index.
 * 
 * @author Jong Kim
 * 
 */
public class LocalLuceneSession implements LuceneSession {
	Object SyncObj = new Object();
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

	private String indexPath;

	public LocalLuceneSession(String indexPath) {
		this.indexPath = indexPath;
	}

	public void addDocument(Document doc) {
		IndexWriter indexWriter;
		if (doc.getField(BasicIndexUtils.UID_FIELD) == null)
			throw new LuceneException(
					"Document must contain a UID with field name "
							+ BasicIndexUtils.UID_FIELD);
		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
			indexWriter = null;

			try {
				indexWriter = LuceneUtil.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				indexWriter.addDocument(doc);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not add document to the index [" + indexPath
								+ "]", e);
			} finally {
				try {
					indexWriter.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void addDocuments(Collection docs) {

		IndexWriter indexWriter;

		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
			indexWriter = null;

			try {
				indexWriter = LuceneUtil.getWriter(indexPath);
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
					indexWriter.addDocument(doc);
				}
			} catch (IOException e) {
				throw new LuceneException(
						"Could not add document to the index [" + indexPath
								+ "]", e);
			} finally {
				try {
					indexWriter.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void deleteDocument(String uid) {
		deleteDocuments(new Term(BasicIndexUtils.UID_FIELD, uid));
	}

	public void deleteDocuments(Term term) {
		IndexReader indexReader;

		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
			indexReader = null;

			try {
				indexReader = LuceneUtil.getReader(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open reader on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				indexReader.deleteDocuments(term);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not delete documents from the index ["
								+ indexPath + "]", e);
			} finally {
				try {
					indexReader.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void deleteDocuments(Query query) {
		IndexSearcher indexSearcher;

		// block until updateDocs is completed
		synchronized (LocalLuceneSession.class) {
			indexSearcher = null;

			try {
				indexSearcher = LuceneUtil.getSearcher(indexPath);
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
				try {
					indexSearcher.close();
				} catch (IOException e) {
				}
			}
		}
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

		try {
			updateDocs(query, fieldname, fieldvalue);
		} catch (Exception e) {
			throw new LuceneException("Error updating index [" + indexPath
					+ "]", e);
		}
	}
	
	public void updateDocuments(ArrayList<Query> queries, String fieldname, ArrayList<String> values) {

		try {
			updateDocs(queries, fieldname, values);
		} catch (Exception e) {
			throw new LuceneException("Error updating index [" + indexPath
					+ "]", e);
		}
	}

	public com.sitescape.team.lucene.Hits search(Query query) {
		return this.search(query, 0, -1);
	}

	public com.sitescape.team.lucene.Hits search(Query query, int offset, int size) {
		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = LuceneUtil.getSearcher(indexPath);
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
			return tempHits;
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
		}
	}

	public com.sitescape.team.lucene.Hits search(Query query, Sort sort) {
		return this.search(query, sort, 0, -1);
	}

	public com.sitescape.team.lucene.Hits search(Query query, Sort sort,
			int offset, int size) {
		Hits hits = null;
		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = LuceneUtil.getSearcher(indexPath);
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
			return tempHits;
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
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
		TreeSet<String> results = new TreeSet<String>();
		ArrayList<String> resultTags = new ArrayList<String>();
		User user = RequestContextHolder.getRequestContext().getUser();

		// block until updateDocs is completed
		try {
			synchronized (LocalLuceneSession.class) {

				try {
					indexReader = LuceneUtil.getReader(indexPath);
					indexSearcher = LuceneUtil.getSearcher(indexReader);
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

				return resultTags;
			}
		} finally {
			try {
				indexReader.close();
				indexSearcher.close();
			} catch (Exception e) {
			}

		}
	}
	
	
	public void flush() {
		// Because Liferay's Lucene functions (on which this implementation
		// is based) are atomic in that it flushes out after each operation,
		// there is no separate flush to perform. Nothing to do.
	}

	public void optimize() {
		IndexWriter indexWriter = null;
		try {
			indexWriter = LuceneUtil.getWriter(indexPath);
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
			try {
				indexWriter.close();
			} catch (IOException e) {
			}
		}

	}

	public void close() {
		// Nothing to do
	}

	private int deleteDocs(org.apache.lucene.search.Hits hits) {
		int length = hits.length();

		if (length > 0) {
			IndexReader indexReader = null;
			try {
				indexReader = LuceneUtil.getReader(indexPath);
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
				try {
					indexReader.close();
				} catch (IOException e) {
				}
			}
		}

		return length;
	}

	private void updateDocs(Query q, String fieldname, String fieldvalue) {
		
		//block every read/write while updateDocs is in progress
		synchronized (LocalLuceneSession.class) {
			// first Optimize the index.
			IndexWriter indexWriter = null;

			try {
				indexWriter = LuceneUtil.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}
			try {
				indexWriter.optimize();
				indexWriter.close();
				doUpdate(q,fieldname,fieldvalue);
			} catch (IOException ioe) {
				throw new LuceneException(
						"Could not update fields on the index ["
								+ this.indexPath + " ], query is: "
								+ q.toString() + " field: " + fieldname);
			} 
		}
	}

	private void updateDocs(ArrayList<Query> queries, String fieldname, ArrayList<String> values) {
		
		//block every read/write while updateDocs is in progress
		int count = 0;
		synchronized (LocalLuceneSession.class) {
			// first Optimize the index.
			IndexWriter indexWriter = null;

			try {
				indexWriter = LuceneUtil.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}
			try {
				indexWriter.optimize();
				indexWriter.close();
				for (count = 0; count < queries.size(); count++)
					doUpdate(queries.get(count),fieldname,values.get(count));
			} catch (IOException ioe) {
				throw new LuceneException(
						"Could not update fields on the index ["
								+ this.indexPath + " ], query is: "
								+ queries.get(count).toString() + " field: " + fieldname);
			} 
		}
	}

	private void doUpdate(Query q, String fieldname, String fieldvalue) {
		IndexUpdater updater = null;
		try {
			Directory indDir = FSDirectory.getDirectory(indexPath, false);
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
		//User user = RequestContextHolder.getRequestContext().getUser();

		// block until updateDocs is completed
		try {
			synchronized (LocalLuceneSession.class) {

				try {
					indexReader = LuceneUtil.getReader(indexPath);
					indexSearcher = LuceneUtil.getSearcher(indexReader);
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
				return resultTitles;
			}
		} finally {
			try {
				indexReader.close();
				indexSearcher.close();
			} catch (Exception e) {
			}

		}
	}
}
