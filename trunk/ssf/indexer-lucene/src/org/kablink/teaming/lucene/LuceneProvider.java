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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.kablink.teaming.lucene.ChineseAnalyzer;
import org.kablink.teaming.lucene.analyzer.SsfIndexAnalyzer;
import org.kablink.teaming.lucene.util.LanguageTaster;
import org.kablink.teaming.lucene.util.TagObject;
import org.kablink.util.PropsUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

public class LuceneProvider extends IndexSupport {
	
	private static final String FIND_TYPE_PERSONAL_TAGS = "personalTags";
	private static final String FIND_TYPE_COMMUNITY_TAGS = "communityTags";
	
	private static Analyzer defaultAnalyzer = new SsfIndexAnalyzer();
	
	private LuceneProviderManager luceneProviderManager;
	
	private Directory directory;
	private IndexWriter indexWriter;
	private SearcherManager searcherManager;
	
	private CommitThread commitThread;
	
	private CommitStat commitStat;
	
	public LuceneProvider(String indexName, String indexDirPath, LuceneProviderManager luceneProviderManager) throws LuceneException {
		super(indexName);

		this.luceneProviderManager = luceneProviderManager;
		
		if(Validator.isNull(indexDirPath))
			throw new IllegalArgumentException("Index directory path must be specified");
		
		// Make sure that the index directory exists
		File indexDir = new File(indexDirPath);
		if(indexDir.exists()) {
			if (!indexDir.isDirectory()) {
				throw newLuceneException("The index directory path [" + indexDirPath + "] exists, but is not a directory");
			}
			else {
				logInfo("The index directory path [" + indexDirPath + "] exists, and is a directory");
			}
		}
		else {
			// Create the directory
			if(!indexDir.mkdir()) {
				throw newLuceneException("Could not create index directory [" + indexDirPath + "]");
			}
			else {
				logInfo("The index directory [" + indexDirPath + "] is created");
			}
		}
		
		// Create index if necessary
		try {
			directory = FSDirectory.open(new File(indexDirPath));
			logInfo("Created FSDirectory instance of type " + directory.getClass().getName());
		} catch (IOException e) {
			throw newLuceneException("Could not create FSDirectory instance", e);
		}
		
		try {
			if(!IndexReader.indexExists(directory)) {
				createIndex(directory);
			}
		}
		catch(Exception e) {
			throw newLuceneException("Could not create index", e);
		}
		
		// Open index writer
		try {
			indexWriter = openIndexWriter(directory, false);
		} catch (IOException e) {
			throw newLuceneException("Could not open index writer", e);
		}
		
		// Open searcher manager
		try {
			searcherManager = new SearcherManager(indexName, indexWriter);
		} catch (IOException e) {
			throw newLuceneException("Could not open searcher manager", e);
		}
		
		commitStat = new CommitStat();
		
		commitThread = new CommitThread(indexName, this);
		// Don't start the thread here yet.
		
		logInfo("Lucene provider instantiated");
	}
	
	public void open() {
		// The sole purpose of this method is to start the thread that was created in
		// the constructor. Since the thread refers to "this" object, starting it before
		// "this" object is fully instantiated is a BAD practice for concurrent programming.
		commitThread.start();
		logDebug("Commit thread started");
	}
	
	private void createIndex(Directory dir) throws LockObtainFailedException, IOException {
		// Use IndexWriter to create index
		IndexWriter iw = new IndexWriter(dir, new SsfIndexAnalyzer(), true, IndexWriter.MaxFieldLength.UNLIMITED);
		iw.close();
		logInfo("Index created");
	}
	
	private IndexWriter openIndexWriter(Directory dir, boolean create) throws IOException {
		// Make sure that there is no existing lock on the directory so that we can create a new IndexWriter.
		if(IndexWriter.isLocked(dir)) {
			logWarn("The index is already locked. Forcibly unlocking it.");
			IndexWriter.unlock(dir);
		}
		
		int maxFields = PropsUtil.getInt("lucene.max.fieldlength", 10000);
		int maxMerge = PropsUtil.getInt("lucene.max.merge.docs", 1000);
		int mergeFactor = PropsUtil.getInt("lucene.merge.factor", 10);

		IndexWriter writer = new IndexWriter(dir, new SsfIndexAnalyzer(), create, new MaxFieldLength(maxFields));
		
		writer.setMaxMergeDocs(maxMerge);
		writer.setMergeFactor(mergeFactor);
		writer.setUseCompoundFile(false);

		logInfo("Opened index writer: create=" + create + ", maxMerge=" + maxMerge + ", mergeFactor=" + mergeFactor + ", maxFields=" + maxFields);
		
		return writer;
	}
	
	public void addDocuments(ArrayList docs) throws LuceneException {
		long startTime = System.currentTimeMillis();

		try {
			for (Iterator iter = docs.iterator(); iter.hasNext();) {
				Document doc = (Document) iter.next();
				if (doc.getField(Constants.UID_FIELD) == null)
					throw new IllegalArgumentException(
							"Document must contain a UID with field name "
									+ Constants.UID_FIELD);
				String tastingText = getTastingText(doc);
				indexWriter.addDocument(doc, getAnalyzer(tastingText));
				if(logger.isTraceEnabled())
					logTrace("Called addDocument on writer with doc [" + doc.toString() + "]");
			}
		} catch (IOException e) {
			throw newLuceneException("Could not add document to the index", e);
		}

		commitStat.update(docs.size());
		
		commitThread.someDocsProcessed();
		
		end(startTime, "addDocuments", docs.size());
	}

	public void deleteDocuments(Term term) throws LuceneException {
		long startTime = System.currentTimeMillis();

		try {
			indexWriter.deleteDocuments(term);
			if(logger.isTraceEnabled())
				logTrace("Called deleteDocuments on writer with term [" + term.toString() + "]");
		} catch (IOException e) {
			throw newLuceneException(
					"Could not delete documents from the index", e);
		}

		commitStat.update(1);
		commitThread.someDocsProcessed();

		end(startTime, "deleteDocuments(Term)");
	}

	public void addDeleteDocuments(ArrayList docsToAddOrDelete) throws LuceneException {
		long startTime = System.currentTimeMillis();

		for(Object obj : docsToAddOrDelete) {
			if(obj instanceof Document) {
				Document doc = (Document) obj;
				if (doc.getField(Constants.UID_FIELD) == null)
					throw new IllegalArgumentException(
							"Document must contain a UID with field name "
									+ Constants.UID_FIELD);
				String tastingText = getTastingText(doc);
				try {
					indexWriter.addDocument(doc, getAnalyzer(tastingText));
				} catch (IOException e) {
					throw newLuceneException("Could not add document to the index", e);					
				}						
				if(logger.isTraceEnabled())
					logTrace("Called addDocument on writer with doc [" + doc.toString() + "]");
			}
			else if(obj instanceof Term) {
				Term term = (Term) obj;
				try {
					indexWriter.deleteDocuments(term);
				} catch (IOException e) {
					throw newLuceneException(
							"Could not delete documents from the index", e);
				}
				if(logger.isTraceEnabled())
					logTrace("Called deleteDocuments on writer with term [" + term.toString() + "]");
			}
			else {
				throw new IllegalArgumentException("Invalid object type for indexing: " + obj.getClass().getName());
			}
		}

		commitStat.update(docsToAddOrDelete.size());
		commitThread.someDocsProcessed();

		end(startTime, "addDeleteDocuments", docsToAddOrDelete.size());
	}

	public void optimize() throws LuceneException {
		// optimize and commit are independent of each other.
		
		long startTime = System.currentTimeMillis();

		try {
			indexWriter.optimize();
			if(logger.isTraceEnabled())
				logTrace("Called optimize on writer");
		} catch (IOException e) {
			throw new LuceneException("Could not optimize the index", e);
		} 
		
		end(startTime, "optimize");
	}
			
	/**
	 * Clear the index, only used at Zone deletion or re-indexing from the top workspace
	 * 
	 * @return
	 * @throws LuceneException
	 */
	public void clearIndex() throws LuceneException {
		long startTime = System.currentTimeMillis();

		// Close existing searcher manager
		try {
			searcherManager.close();
		} catch (IOException e) {
			logError("Error closing searcher manager", e);
		}
		
		// Close existing index writer
		try {
			closeIndexWriter();
		} catch (IOException e) {
			logError("Error closing index writer", e);
		}
		
		// Open a new index writer overwriting the existing index
		try {
			indexWriter = openIndexWriter(directory, true);
		} catch (IOException e) {
			throw newLuceneException("Could not create index writer", e);
		}
		
		// Open a new searcher manager
		try {
			searcherManager = new SearcherManager(indexName, indexWriter);
		} catch (IOException e) {
			throw newLuceneException("Could not open searcher manager", e);
		}

		// As result of clearing the index, the in-memory and the disk states are in synch.
		// This call helps prevent the background thread from unnecessarily and prematurely
		// attempting to commit changes to the index.
		commitStat.reset();
		
		end(startTime, "clearIndex");
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
	
	protected Analyzer getAnalyzer(String snippet) {
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
					Class hebrewClass = classForName(aName);
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
					Class arabicClass = classForName(aName);
					analyzer = (Analyzer) arabicClass.newInstance();
				} catch (Exception e) {
					logger.error("Could not initialize arabic analyzer class", e);
				}
			}
			return analyzer;
		}
	}
	
	public org.kablink.teaming.lucene.util.Hits search(Query query) throws LuceneException {
		return this.search(query, 0, -1);
	}

	private IndexSearcher getIndexSearcher() throws LuceneException {
		try {
			searcherManager.maybeReopen();
		} catch (InterruptedException e) {
			// In the current code base, this can not and should not occur. So we will simply throw a regular exception.
			// In the future when we may add sophistication to the search manager an interrupt may actually mean something.
			throw newLuceneException("Error getting index searcher", e);
		} catch (IOException e) {
			throw newLuceneException("Error getting index searcher", e);
		}
		return searcherManager.get();
	}
	
	private void releaseIndexSearcher(IndexSearcher indexSearcher) {
		try {
			searcherManager.release(indexSearcher);
		} catch (IOException e) {
			logError("Error releasing index searcher", e);
		}	
	}
	
	public org.kablink.teaming.lucene.util.Hits search(Query query, int offset,
			int size) throws LuceneException {
		long startTime = System.currentTimeMillis();

		IndexSearcher indexSearcher = getIndexSearcher();

		try {
			org.apache.lucene.search.Hits hits = indexSearcher
					.search(query);
			if (size < 0)
				size = hits.length();
			org.kablink.teaming.lucene.util.Hits tempHits = org.kablink.teaming.lucene.util.Hits
					.transfer(hits, offset, size);
			tempHits.setTotalHits(hits.length());

			end(startTime, "search(Query,int,int)", query, tempHits.length());
			
			return tempHits;
		} catch (IOException e) {
			throw newLuceneException("Error searching index", e);
		} finally {
			releaseIndexSearcher(indexSearcher);
		}
	}

	public org.kablink.teaming.lucene.util.Hits search(Query query, Sort sort) throws LuceneException {
		return this.search(query, sort, 0, -1);
	}

	public org.kablink.teaming.lucene.util.Hits search(Query query, Sort sort,
			int offset, int size) throws LuceneException {
		long startTime = System.currentTimeMillis();

		IndexSearcher indexSearcher = getIndexSearcher();

		Hits hits = null;

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
			org.kablink.teaming.lucene.util.Hits tempHits = org.kablink.teaming.lucene.util.Hits
					.transfer(hits, offset, size);
			tempHits.setTotalHits(hits.length());

			end(startTime, "search(Query,Sort,int,int)", query, tempHits.length());
			
			return tempHits;
		} catch (IOException e) {
			throw newLuceneException("Error searching index", e);
		} finally {
			releaseIndexSearcher(indexSearcher);
		}
	}

	public ArrayList getTags(Query query, String tag, String type, String userId, boolean isSuper)
	throws LuceneException {
		long startTime = System.currentTimeMillis();
		
		ArrayList<String> resultTags = new ArrayList<String>();
		ArrayList tagObjects = getTagsWithFrequency(query, tag, type, userId, isSuper);
		Iterator iter = tagObjects.iterator();
		while (iter.hasNext()) {
			resultTags.add(((TagObject)iter.next()).getTagName());
		}
		
		end(startTime, "getTags", query, resultTags.size());
		
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
	 * getTags(Query query, Long id, String tag, String type, boolean isSuper)
	 */
	public ArrayList getTagsWithFrequency(Query query, String tag, String type, String userId, boolean isSuper)
			throws LuceneException {
		long startTime = System.currentTimeMillis();
		
		int prefixLength = 0;
		tag = tag.toLowerCase();
		
		TreeSet<TagObject> results = new TreeSet<TagObject>();
		ArrayList<TagObject> resultTags = new ArrayList<TagObject>();

		IndexSearcher indexSearcher = getIndexSearcher();

		try {
			final BitSet userDocIds = new BitSet(indexSearcher.getIndexReader().maxDoc());
			if (!isSuper) {
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
				TermEnum enumerator = indexSearcher.getIndexReader().terms(new Term(
						fields[i], tag));

				TermDocs termDocs = indexSearcher.getIndexReader().termDocs();
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
		} catch (IOException e) {
			// Instead of throwing the error, log it and continue. This way, we can
			// return partial result if any.
			// Note: I don't know why/if this is the right thing. But I'm leaving the
			// logic as is, since that's the way it was previously written.
			logError("Error getting tags", e);
		} finally {
			releaseIndexSearcher(indexSearcher);
		}

		resultTags.addAll(results);

		end(startTime, "getTagsWithFrequency", query, resultTags.size());

		return resultTags;

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
		long startTime = System.currentTimeMillis();
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<ArrayList> resultTitles = new ArrayList<ArrayList>();
		int count = 0;
		String lastTerm = "";

		IndexSearcher indexSearcher = getIndexSearcher();

		try {
			final BitSet userDocIds = new BitSet(indexSearcher.getIndexReader().maxDoc());
			
			indexSearcher.search(query, new HitCollector() {
				public void collect(int doc, float score) {
					userDocIds.set(doc);
				}
			});
			String field = Constants.NORM_TITLE;
				TermEnum enumerator = indexSearcher.getIndexReader().terms(new Term(
						field, start));

				TermDocs termDocs = indexSearcher.getIndexReader().termDocs();
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

		} catch (IOException e) {
			// Instead of throwing the error, log it and continue. This way, we can
			// return partial result if any.
			// Note: I don't know why/if this is the right thing. But I'm leaving the
			// logic as is, since that's the way it was previously written.
			logError("Error getting titles", e);
		} finally {
			releaseIndexSearcher(indexSearcher);
		}

		String[] retArray = new String[titles.size()];
		retArray = titles.toArray(retArray);
	    
	    end(startTime, "getNormTitles", query, retArray.length);
		
		return retArray;

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
	
	private void commitIndexWriter() throws LuceneException {
		try {
			indexWriter.commit();
		} catch (IOException e) {
			throw newLuceneException("Error committing index writer", e);		
		}
		logInfo("Index writer committed");
	}
	
	private void reopenIndexSearcher() throws LuceneException {
		try {
			searcherManager.forceReopen();
		} catch (InterruptedException e) {
			// In the current code base, this can not and should not occur. So we will simply throw a regular exception.
			// In the future when we may add sophistication to the search manager an interrupt may actually mean something.
			throw newLuceneException("Error reopening index searcher", e);
		} catch (IOException e) {
			throw newLuceneException("Error reopening index searcher", e);
		}
	}
	
	public void commit() throws LuceneException {
		long startTime = System.currentTimeMillis();

		commitStat.reset();
		
		commitIndexWriter();
		
		// The IndexReader.isCurrent() method that SearcherManager relies on to detect changes
		// returns true if the changes are already committed from the writer. So, it fails to
		// recognize the need to reopen the searcher at the time of search. So, we need to
		// force the searcher manager to reopen the searcher whenever a commit is performed
		// on the writer. 
		reopenIndexSearcher();
		
		end(startTime, "commit");
	}

	private void shutdownCommitThread() throws InterruptedException {
		commitThread.setStop();
		commitThread.join();
		logDebug("Commit thread shut down successfully");
	}
	
	public void close() {
		long startTime = System.currentTimeMillis();

		// Shutdown commit thread
		try {
			shutdownCommitThread();
		} catch (InterruptedException e) {
			logWarn("This shouldn't happen", e);
		}
		
		// Close searcher manager
		try {
			searcherManager.close();
		} catch (IOException e) {
			logError("Error closing searcher manager", e);
		}
		
		// Close index writer
		try {
			closeIndexWriter();
		} catch (IOException e) {
			logError("Error closing index writer", e);
		}
		
		// Close directory
		try {
			closeDirectory();
		} catch (IOException e) {
			logError("Error closing directory", e);
		}
		
		end(startTime, "close");
	}
		
	private void closeDirectory() throws IOException {
		directory.close();
		logInfo("Directory closed");
	}
	
	private void closeIndexWriter() throws IOException {
		indexWriter.close();
		logInfo("Index writer closed");
	}
	
	private void end(long begin, String methodName) {
		if(logger.isDebugEnabled()) {
			logDebug((System.currentTimeMillis()-begin) + " ms, " + methodName);
		}
	}
	
	private void end(long begin, String methodName, int length) {
		if(logger.isDebugEnabled()) {
			logDebug((System.currentTimeMillis()-begin) + " ms, " + methodName + ", input=" + length);
		}
	}
	
	private void end(long begin, String methodName, Query query, int length) {
		if(logger.isTraceEnabled()) {
			logTrace((System.currentTimeMillis()-begin) + " ms, " + methodName + ", result=" + length + 
					", query=[" + ((query==null)? "" : query.toString()) + "]");			
		}
		else if(logger.isDebugEnabled()) {
			logDebug((System.currentTimeMillis()-begin) + " ms, " + methodName + ", result=" + length);
		}
	}
	
	private LuceneException newLuceneException(String msg) {
		return new LuceneException("(" + indexName + ") " + msg);
	}
	
	private LuceneException newLuceneException(String msg, Throwable t) {
		return new LuceneException("(" + indexName + ") " + msg, t);
	}
	
	private Class classForName(String name) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(name);
		}
		catch (Exception e) {
			return Class.forName(name);
		}
	}
	
	LuceneProviderManager getLuceneProviderManager() {
		return luceneProviderManager;
	}
	
	CommitStat getCommitStat() {
		// Returns a copy, which is read-only from the caller's point of view.
		return commitStat.copy();
	}
	
	class CommitStat {
		// Time of first add/delete operation since the beginning of last commit or synch
	    private long firstOpTimeSinceLastCommit; 
	    // Number of add/delete operations since beginning of latest commit or synch
	    private int numberOfOpsSinceLastCommit; 
		
	    // requires monitor so that the two variables can be modified atomically
	    private synchronized void reset() {
			logTrace("Called CommitStat.reset()");
			firstOpTimeSinceLastCommit = 0;
			numberOfOpsSinceLastCommit = 0;	
	    }
	    
	    // requires monitor so that the two variables can be modified atomically
	    private synchronized void update(int opsCount) {
			logTrace("Called CommitStat.update() with opsCount=" + opsCount);
			numberOfOpsSinceLastCommit += opsCount;
			if(firstOpTimeSinceLastCommit == 0)
				firstOpTimeSinceLastCommit = System.currentTimeMillis();
		}
		
	    // requires monitor so that the copy contains consistent values between the two variables. 
	    private synchronized CommitStat copy() {
	    	CommitStat copy = new CommitStat();
	    	copy.firstOpTimeSinceLastCommit = this.firstOpTimeSinceLastCommit;
	    	copy.numberOfOpsSinceLastCommit = this.numberOfOpsSinceLastCommit;
	    	return copy;
	    }
	    
		long getFirstOpTimeSinceLastCommit() {
			return firstOpTimeSinceLastCommit;
		}
		
		int getNumberOfOpsSinceLastCommit() {
			return numberOfOpsSinceLastCommit;
		}
	}
}
