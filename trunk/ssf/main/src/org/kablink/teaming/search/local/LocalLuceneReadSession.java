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
package org.kablink.teaming.search.local;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.lucene.LuceneHelper;
import org.kablink.teaming.lucene.TagObject;
import org.kablink.teaming.search.LuceneException;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.search.Constants;

/**
 * This implementation provides access to local Lucene index.
 * 
 */
public class LocalLuceneReadSession extends LocalLuceneSession implements LuceneReadSession {

	private static final Log logger = LogFactory.getLog(LocalLuceneReadSession.class);

	private static boolean debugEnabled = logger.isDebugEnabled();
	
	public LocalLuceneReadSession(String indexPath) {
		this.indexPath = indexPath;		
	}

	public org.kablink.teaming.lucene.Hits search(Query query) {
		return this.search(query, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Query query, int offset,
			int size) {
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

	public org.kablink.teaming.lucene.Hits search(Query query, Sort sort) {
		return this.search(query, sort, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Query query, Sort sort,
			int offset, int size) {
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

	public ArrayList getTags(Query query, String tag, String type)
	throws LuceneException {
		ArrayList<String> resultTags = new ArrayList<String>();
		ArrayList tagObjects = getTagsWithFrequency(query, tag, type);
		Iterator iter = tagObjects.iterator();
		while (iter.hasNext()) {
			resultTags.add(((TagObject)iter.next()).getTagName());
		}
		return resultTags;
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
	public ArrayList getTagsWithFrequency(Query query, String tag, String type)
			throws LuceneException {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		;
		TreeSet<TagObject> results = new TreeSet<TagObject>();
		ArrayList<TagObject> resultTags = new ArrayList<TagObject>();
		User user = RequestContextHolder.getRequestContext().getUser();
		long startTime = System.currentTimeMillis();
		int prefixLength = 0;

		tag = tag.toLowerCase();
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
					if (!user.isSuper()) {
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
									.equals(WebKeys.FIND_TYPE_PERSONAL_TAGS)) {
						fields = new String[1];
						fields[0] = Constants.ACL_TAG_FIELD_TTF;
					} else if (type != null
							&& type
									.equals(WebKeys.FIND_TYPE_COMMUNITY_TAGS)) {
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
									+ RequestContextHolder.getRequestContext()
											.getUserId().toString()
									+ Constants.TAG;
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
					logger.warn(e);
				}

				Iterator iter = results.iterator();
				while (iter.hasNext())
					resultTags.add((TagObject)iter.next());
				long endTime = System.currentTimeMillis();
				if(debugEnabled)
					logger.debug("LocalLucene: getTags took: " + (endTime - startTime) + " milliseconds");

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
					logger.warn(e);
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
			LuceneHelper.closeSearcher(indexPath);
		}
	}
	
}
