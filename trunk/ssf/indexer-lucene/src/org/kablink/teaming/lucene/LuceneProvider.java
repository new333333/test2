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

import gnu.trove.set.hash.TLongHashSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
import org.kablink.teaming.lucene.analyzer.VibeIndexAnalyzer;
import org.kablink.teaming.lucene.util.LanguageTaster;
import org.kablink.teaming.lucene.util.TagObject;
import org.kablink.util.EventsStatistics;
import org.kablink.util.PropsUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

public class LuceneProvider extends IndexSupport implements LuceneProviderMBean {
	
	private static final int UNSPECIFIED_INT = -1;
	
	private static final String FIND_TYPE_PERSONAL_TAGS = "personalTags";
	private static final String FIND_TYPE_COMMUNITY_TAGS = "communityTags";
	
	private static Analyzer defaultAnalyzer;
	
	private static ThreadLocal<QueryParser> queryParserWithWSA = new ThreadLocal<QueryParser>();
	
	private LuceneProviderManager luceneProviderManager;
	
	private String indexDirPath;
	private Directory directory;
	
	private CommitThread commitThread;
	
	// access protected by "this"
	private IndexingResource indexingResource;
	
	private EventsStatistics statistics;

	public LuceneProvider(String indexName, String indexDirPath, LuceneProviderManager luceneProviderManager) throws LuceneException {
		super(indexName);

		if(Validator.isNull(indexDirPath))
			throw new IllegalArgumentException("Index directory path must be specified");
		this.indexDirPath = indexDirPath;
		
		this.luceneProviderManager = luceneProviderManager;
		
		this.statistics = new EventsStatistics();
		
		logDebug("Lucene provider instantiated");
	}
	
	private QueryParser getQueryParserWithWSA() {
		QueryParser qp = queryParserWithWSA.get();
		if(qp == null) {
			qp =  new QueryParser(Version.LUCENE_34, Constants.ALL_TEXT_FIELD, new WhitespaceAnalyzer());
			queryParserWithWSA.set(qp);
		}
		return qp;
	}
	
	public void initialize() {
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
		
		openIndexingResource(false);
		
		commitThread = new CommitThread(indexName, this);
		
		commitThread.start();
		logDebug("Commit thread started");
		
		logInfo("Lucene provider initialized");
	}
	
	public String getIndexDirPath() {
		return this.indexDirPath;
	}
	
	private Analyzer getDefaultAnalyzer() {
		if(defaultAnalyzer == null) {
			Analyzer analyzer = VibeIndexAnalyzer.getInstance();
			if(analyzer instanceof VibeIndexAnalyzer) {
				// For title field, we disable stopword filtering so that type-to-find
				// functionality can work properly.
				((VibeIndexAnalyzer)analyzer).setStopSet(null);
				PerFieldAnalyzerWrapper pfAnalyzer = new PerFieldAnalyzerWrapper(VibeIndexAnalyzer.getInstance());
				pfAnalyzer.addAnalyzer(Constants.TITLE_FIELD, analyzer);
				defaultAnalyzer = pfAnalyzer;
			}
			else {
				defaultAnalyzer = analyzer;
			}
		}
		return defaultAnalyzer;
	}
	
	private void createIndex(Directory dir) throws LockObtainFailedException, IOException {
		// Use IndexWriter to create index
		IndexWriter iw = new IndexWriter(dir, VibeIndexAnalyzer.getInstance(), true, IndexWriter.MaxFieldLength.UNLIMITED);
		iw.close();
		logInfo("Index created");
	}
	
	private IndexWriter openIndexWriter(Directory dir, boolean create) throws IOException {
		// Make sure that there is no existing lock on the directory so that we can create a new IndexWriter.
		if(IndexWriter.isLocked(dir)) {
			logWarn("The index is already locked. Forcibly unlocking it.");
			IndexWriter.unlock(dir);
		}
		
		int maxFields = PropsUtil.getInt("lucene.max.fieldlength", 100000);
		int maxMerge = PropsUtil.getInt("lucene.max.merge.docs", UNSPECIFIED_INT);
		int mergeFactor = PropsUtil.getInt("lucene.merge.factor", 10);
		int ramBufferSizeMb = PropsUtil.getInt("lucene.ram.buffer.size.mb", 256);
		boolean useCompoundFile = PropsUtil.getBoolean("lucene.use.compound.file", false);

		IndexWriter writer = new IndexWriter(dir, VibeIndexAnalyzer.getInstance(), create, new MaxFieldLength(maxFields));
		
		if(maxMerge >= 0)
			writer.setMaxMergeDocs(maxMerge);
		writer.setMergeFactor(mergeFactor);
		writer.setUseCompoundFile(useCompoundFile);
		writer.setRAMBufferSizeMB(ramBufferSizeMb);

		logInfo("Opened index writer: create=" + create + 
				", maxFields=" + maxFields + 
				", maxMerge=" + ((maxMerge >= 0)? maxMerge : "Unspecified") + 
				", mergeFactor=" + mergeFactor + 
				", ramBufferSizeMb=" + ramBufferSizeMb + 
				", useCompoundFile=" + useCompoundFile);
		
		return writer;
	}
	
	public void addDocuments(ArrayList docs) throws LuceneException {
		long startTime = System.nanoTime();

		try {
			for (Iterator iter = docs.iterator(); iter.hasNext();) {
				Document doc = (Document) iter.next();
				if (doc.getFieldable(Constants.UID_FIELD) == null)
					throw new IllegalArgumentException(
							"Document must contain a UID with field name "
									+ Constants.UID_FIELD);
				String tastingText = getTastingText(doc);
				getIndexingResource().getIndexWriter().addDocument(doc, getAnalyzer(tastingText));
				if(logger.isTraceEnabled())
					logTrace("Called addDocument on writer with doc [" + doc.toString() + "]");
			}
		} catch (IOException e) {
			throw newLuceneException("Could not add document to the index", e);
		} catch (OutOfMemoryError e) {
			getIndexingResource().closeOOM(e);
			throw e;
		}

		getIndexingResource().getCommitStat().update(docs.size());
		
		commitThread.someDocsProcessed();
		
		end(startTime, "addDocuments()", docs.size());
	}

	public void deleteDocuments(Term term) throws LuceneException {
		long startTime = System.nanoTime();

		try {
			getIndexingResource().getIndexWriter().deleteDocuments(term);
			if(logger.isTraceEnabled())
				logTrace("Called deleteDocuments on writer with term [" + term.toString() + "]");
		} catch (IOException e) {
			throw newLuceneException(
					"Could not delete documents from the index", e);
		} catch (OutOfMemoryError e) {
			getIndexingResource().closeOOM(e);
			throw e;
		}

		getIndexingResource().getCommitStat().update(1);
		commitThread.someDocsProcessed();

		end(startTime, "deleteDocuments(Term)");
	}

	public void addDeleteDocuments(ArrayList docsToAddOrDelete) throws LuceneException {
		long startTime = System.nanoTime();

		for(Object obj : docsToAddOrDelete) {
			if(obj instanceof Document) {
				Document doc = (Document) obj;
				if (doc.getFieldable(Constants.UID_FIELD) == null)
					throw new IllegalArgumentException(
							"Document must contain a UID with field name "
									+ Constants.UID_FIELD);
				String tastingText = getTastingText(doc);
				try {
					getIndexingResource().getIndexWriter().addDocument(doc, getAnalyzer(tastingText));
				} catch (IOException e) {
					throw newLuceneException("Could not add document to the index", e);					
				} catch (OutOfMemoryError e) {
					getIndexingResource().closeOOM(e);
					throw e;
				}						
				if(logger.isTraceEnabled())
					logTrace("Called addDocument on writer with doc [" + doc.toString() + "]");
			}
			else if(obj instanceof Term) {
				Term term = (Term) obj;
				try {
					getIndexingResource().getIndexWriter().deleteDocuments(term);
				} catch (IOException e) {
					throw newLuceneException(
							"Could not delete documents from the index", e);
				} catch (OutOfMemoryError e) {
					getIndexingResource().closeOOM(e);
					throw e;
				}
				if(logger.isTraceEnabled())
					logTrace("Called deleteDocuments on writer with term [" + term.toString() + "]");
			}
			else {
				throw new IllegalArgumentException("Invalid object type for indexing: " + obj.getClass().getName());
			}
		}

		getIndexingResource().getCommitStat().update(docsToAddOrDelete.size());
		commitThread.someDocsProcessed();

		end(startTime, "addDeleteDocuments()", docsToAddOrDelete.size());
	}

	public void optimize() throws LuceneException {
		long startTime = System.nanoTime();
		getIndexingResource().optimize();
		endStat(startTime, "optimize()");
	}
			
	public void expungeDeletes() throws LuceneException {
		long startTime = System.nanoTime();
		getIndexingResource().expungeDeletes();
		endStat(startTime, "expungeDeletes()");
	}
			
	public void optimize(int maxNumSegments) throws LuceneException {
		long startTime = System.nanoTime();
		getIndexingResource().optimize(maxNumSegments);
		endStat(startTime, "optimize(int)");
	}
			
	private void closeIndexingResource() {
		getIndexingResource().close();
	}
	
	private void openIndexingResource(boolean create) throws LuceneException {
		IndexWriter writer = null;
		SearcherManager manager = null;
		
		// Open index writer
		try {
			writer = openIndexWriter(directory, create);
		} catch (IOException e) {
			if(create)
				throw newLuceneException("Could not create index writer", e);
			else
				throw newLuceneException("Could not open index writer", e);
		}
		
		// Open searcher manager
		try {
			manager = new SearcherManager(indexName, writer);
		} catch (IOException e) {
			throw newLuceneException("Could not open searcher manager", e);
		}
		
		setIndexingResource(new IndexingResource(writer, manager, new CommitStat()));
	}
	
	/**
	 * Clear the index, only used at Zone deletion or re-indexing from the top workspace
	 * 
	 * @return
	 * @throws LuceneException
	 */
	public void clearIndex() throws LuceneException {
		long startTime = System.nanoTime();

		closeIndexingResource();
		
		// Open a new index writer overwriting the existing index
		openIndexingResource(true);
				
		endStat(startTime, "clearIndex()");
		logInfo("clearIndex completed. It took " + elapsedTimeInMs(startTime) + " milliseconds");
	}

	private double elapsedTimeInMs(long startTimeInNanoseconds) {
		return (System.nanoTime() - startTimeInNanoseconds)/1000000.0;
	}
	
	private String getTastingText(Document doc) {
		String text = getTastingTextFromAllTextField(doc);
		if (text == null || text.length() == 0) {
			Fieldable title = doc.getFieldable(Constants.TITLE_FIELD);
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
		Fieldable[] allTextFields = doc.getFieldables(Constants.ALL_TEXT_FIELD);
		String piece;
		for(Fieldable allTextField:allTextFields) {
			piece = allTextField.stringValue();
			if(piece != null && piece.length()>0) {
				if(sb.length() > 0)
					sb.append(" ");
				sb.append(piece);
			}
		}
		return sb.toString();
	}
	
	protected Analyzer getAnalyzer(String snippet) {
		// pass the snippet to the language taster and see which
		// analyzer to use
		String language = LanguageTaster.taste(snippet.toCharArray());
		if (language.equalsIgnoreCase(LanguageTaster.DEFAULT)) {
			return getDefaultAnalyzer();
		} else if (language.equalsIgnoreCase(LanguageTaster.CJK)) {
			PerFieldAnalyzerWrapper retAnalyzer = new PerFieldAnalyzerWrapper(VibeIndexAnalyzer.getInstance());
			retAnalyzer.addAnalyzer(Constants.ALL_TEXT_FIELD, new ChineseAnalyzer());
			retAnalyzer.addAnalyzer(Constants.DESC_TEXT_FIELD, new ChineseAnalyzer());
			retAnalyzer.addAnalyzer(Constants.TITLE_FIELD, new ChineseAnalyzer());
			return retAnalyzer;
		} else if (language.equalsIgnoreCase(LanguageTaster.HEBREW)) {
			// return new HEBREWAnalyzer;
			Analyzer analyzer = getDefaultAnalyzer();
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
			Analyzer analyzer = getDefaultAnalyzer();
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
	
	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query) throws LuceneException {
		return this.search(contextUserId, aclQueryStr, mode, query, null, 0, -1);
	}

	private IndexSearcherHandle getIndexSearcherHandle() throws LuceneException {
		SearcherManager manager = getIndexingResource().getSearcherManager();
		try {
			manager.maybeReopen();
		} catch (InterruptedException e) {
			// In the current code base, this can not and should not occur. So we will simply throw a regular exception.
			// In the future when we may add sophistication to the search manager an interrupt may actually mean something.
			throw newLuceneException("Error getting index searcher", e);
		} catch (IOException e) {
			throw newLuceneException("Error getting index searcher", e);
		}
		return new IndexSearcherHandle(manager.get(), manager);
	}
	
	private void releaseIndexSearcherHandle(IndexSearcherHandle indexSearcherHandle) {
		try {
			indexSearcherHandle.getSearcherManager().release(indexSearcherHandle.getIndexSearcher());
		} catch (IOException e) {
			logError("Error releasing index searcher", e);
		}	
	}
	
	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, int offset, int size) throws LuceneException {
		return search(contextUserId, aclQueryStr, mode, query, null, offset, size);
	}
	
	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort) throws LuceneException {
		return this.search(contextUserId, aclQueryStr, mode, query, sort, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
			int offset, int size) throws LuceneException {
		long startTime = System.nanoTime();

		IndexSearcherHandle indexSearcherHandle = getIndexSearcherHandle();

		TopDocs topDocs = null;
		
		Filter aclFilter = null;
		
		try {
			if(aclQueryStr != null && aclQueryStr.length() > 0) {
				// The query result must be filtered by ACL restriction.
				if(mode == Constants.SEARCH_MODE_NORMAL) {
					Query aclInheritingEntriesPermissibleAclQuery = makeAclInheritingEntriesPermissibleAclQuery(parseAclQueryStr(aclQueryStr));
					
					QueryWrapperFilter aclInheritingEntriesPermissibleAclFilter = new QueryWrapperFilter(aclInheritingEntriesPermissibleAclQuery);
					
					TLongHashSet accessibleFolderIds = null;
					
					Query accessibleFoldersAclQuery = makeAccessibleFoldersAclQuery(parseAclQueryStr(aclQueryStr));
						
					accessibleFolderIds = obtainAccessibleFolderIds(indexSearcherHandle.getIndexSearcher(), contextUserId, accessibleFoldersAclQuery);

					aclFilter = new AclFilter(aclInheritingEntriesPermissibleAclFilter, accessibleFolderIds);			
				}
				else if(mode == Constants.SEARCH_MODE_SELF_CONTAINED_ONLY) {
					aclFilter = new QueryWrapperFilter(parseAclQueryStr(aclQueryStr));
				}
				else if(mode == Constants.SEARCH_MODE_PREAPPROVED_PARENTS) {
					Query aclInheritingEntriesPermissibleAclQuery = makeAclInheritingEntriesPermissibleAclQuery(parseAclQueryStr(aclQueryStr));
					
					QueryWrapperFilter aclInheritingEntriesPermissibleAclFilter = new QueryWrapperFilter(aclInheritingEntriesPermissibleAclQuery);

					aclFilter = aclInheritingEntriesPermissibleAclFilter;
				}
				else {
					throw newLuceneException("Invalid search mode: " + mode);
				}				
			}
			
			if (size < 0)
				size = Integer.MAX_VALUE;
			
			if (sort == null) {
				topDocs = indexSearcherHandle.getIndexSearcher().search(query, aclFilter, Integer.MAX_VALUE);
			}
			else {
				try {
					topDocs = indexSearcherHandle.getIndexSearcher().search(query, aclFilter, Integer.MAX_VALUE, sort);
				} catch (Exception ex) {
					topDocs = indexSearcherHandle.getIndexSearcher().search(query, aclFilter, Integer.MAX_VALUE);
				}
			}
			
			org.kablink.teaming.lucene.Hits tempHits = org.kablink.teaming.lucene.Hits
					.transfer(indexSearcherHandle.getIndexSearcher(), topDocs, offset, size);

			end(startTime, "search", contextUserId, aclQueryStr, mode, query, sort, offset, size, tempHits.length());
			
			return tempHits;
		} catch (IOException e) {
			throw newLuceneException("Error searching index", e);
		} catch (OutOfMemoryError e) {
			getIndexingResource().closeOOM(e);
			throw e;
		} catch (ParseException e) {
			throw newLuceneException("Error parsing query", e);
		} finally {
			releaseIndexSearcherHandle(indexSearcherHandle);
		}
	}

	public ArrayList getTags(Query query, String tag, String type, String userId, boolean isSuper)
	throws LuceneException {
		long startTime = System.nanoTime();
		
		ArrayList<String> resultTags = new ArrayList<String>();
		ArrayList tagObjects = getTagsWithFrequency(query, tag, type, userId, isSuper);
		Iterator iter = tagObjects.iterator();
		while (iter.hasNext()) {
			resultTags.add(((TagObject)iter.next()).getTagName());
		}
		
		end(startTime, "getTags(Query,String,String,String,boolean)", query, resultTags.size());
		
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
		long startTime = System.nanoTime();
		String tagOrig = tag;
		int prefixLength = 0;
		tag = tag.toLowerCase();
		
		TreeSet<TagObject> results = new TreeSet<TagObject>();
		ArrayList<TagObject> resultTags = new ArrayList<TagObject>();

		IndexSearcherHandle indexSearcherHandle = getIndexSearcherHandle();

		try {
			final BitSet userDocIds = new BitSet(indexSearcherHandle.getIndexSearcher().getIndexReader().maxDoc());
			if (!isSuper) {
				indexSearcherHandle.getIndexSearcher().search(query, new Collector() {
					@Override
					public void collect(int doc) {
						userDocIds.set(doc);
					}

					@Override
					public boolean acceptsDocsOutOfOrder() {
						return true;
					}

					@Override
					public void setNextReader(IndexReader arg0, int arg1)
							throws IOException {
					}

					@Override
					public void setScorer(Scorer arg0) throws IOException {
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
				TermEnum enumerator = indexSearcherHandle.getIndexSearcher().getIndexReader().terms(new Term(
						fields[i], tag));

				TermDocs termDocs = indexSearcherHandle.getIndexSearcher().getIndexReader().termDocs();
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
		} catch (OutOfMemoryError e) {
			getIndexingResource().closeOOM(e);
			throw e;
		} finally {
			releaseIndexSearcherHandle(indexSearcherHandle);
		}

		resultTags.addAll(results);

		end(startTime, "getTagsWithFrequency(Query,String,String,String,boolean)", query, tagOrig, tag, resultTags.size());

		return resultTags;
	}
	
	/**
	 * Get all the sort titles that this user can see, and return a skip list
	 * 
	 * @param query can be null for superuser
	 * @param sortTitleFieldName
	 * @param start
	 * @param end
	 * @return
	 * @throws LuceneException
	 */
	
	// This returns an arraylist of arraylists.  Each child arraylist has 2 strings, (RangeStart, RangeEnd)
	// i.e. results[0] = {a, c}
	//      results[1] = {d, g}
	
	public String[] getSortedTitles(Query query, String sortTitleFieldName, String start, String end, int skipsize)
			throws LuceneException {
		long startTime = System.nanoTime();
		
		ArrayList<String> titles = new ArrayList<String>();
		ArrayList<ArrayList> resultTitles = new ArrayList<ArrayList>();
		int count = 0;
		String lastTerm = "";

		IndexSearcherHandle indexSearcherHandle = getIndexSearcherHandle();

		try {
			final BitSet userDocIds = new BitSet(indexSearcherHandle.getIndexSearcher().getIndexReader().maxDoc());
			
			indexSearcherHandle.getIndexSearcher().search(query, new Collector() {
				@Override
				public void collect(int doc) {
					userDocIds.set(doc);
				}

				@Override
				public boolean acceptsDocsOutOfOrder() {
					return true;
				}

				@Override
				public void setNextReader(IndexReader arg0, int arg1)
						throws IOException {
				}

				@Override
				public void setScorer(Scorer arg0) throws IOException {
				}
			});
			if(sortTitleFieldName == null)
				sortTitleFieldName = Constants.NORM_TITLE; // default
				TermEnum enumerator = indexSearcherHandle.getIndexSearcher().getIndexReader().terms(new Term(
						sortTitleFieldName, start));

				TermDocs termDocs = indexSearcherHandle.getIndexSearcher().getIndexReader().termDocs();
				if (enumerator.term() == null) {
					// no matches
					return null;
				}
				do {
					Term term = enumerator.term();
					// stop when the field is no longer the field we're
					// looking for, or, when the term is beyond the end term
					if (term.field().compareTo(sortTitleFieldName) != 0)
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
		} catch (OutOfMemoryError e) {
			getIndexingResource().closeOOM(e);
			throw e;
		} finally {
			releaseIndexSearcherHandle(indexSearcherHandle);
		}

		String[] retArray = new String[titles.size()];
		retArray = titles.toArray(retArray);
	    
	    end(startTime, "getSortedTitles(Query,String,String,int)", query, retArray.length);
		
		return retArray;

	}
	
	public ArrayList getSortedTitlesAsList(Query query, String sortTitleFieldName, String start, String end,
			int skipsize) throws LuceneException {
		String[] normResults = getSortedTitles(query, sortTitleFieldName, start, end, skipsize);
		
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
	
	public void commit() throws LuceneException {
		long startTime = System.nanoTime();
		getIndexingResource().commit();
		endStat(startTime, "commit()");
	}

	public void close() {
		long startTime = System.nanoTime();

		// Shutdown commit thread
		try {
			commitThread.setStop();
			commitThread.join();
			logDebug("Commit thread shut down successfully");
		} catch (InterruptedException e) {
			logWarn("This shouldn't happen", e);
		}
		
		closeIndexingResource();
		
		// Close directory
		try {
			directory.close();
			logDebug("Directory closed");
		} catch (IOException e) {
			logError("Error closing directory", e);
		}
		
		endStat(startTime, "close()");
		logInfo("Closed. It took " + elapsedTimeInMs(startTime) + " milliseconds");
	}
	
	private void endStat(long begin, String methodName) {
		if(statistics.isEnabled())
			statistics.addEvent(methodName, System.nanoTime()-begin);		
	}
	
	private void end(long begin, String methodName) {
		endStat(begin, methodName);
		if(logger.isDebugEnabled()) {
			logDebug(elapsedTimeInMs(begin) + " ms, " + methodName);
		}
	}
	
	private void end(long begin, String methodName, int length) {
		endStat(begin, methodName);
		if(logger.isDebugEnabled()) {
			logDebug(elapsedTimeInMs(begin) + " ms, " + methodName + ", input=" + length);
		}
	}
	
	private void end(long begin, String methodName, Long contextUserId, int length) {
		endStat(begin, methodName);
		if(logger.isDebugEnabled()) {
			logDebug(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + length +
					", contextUserId=" + contextUserId);
		}
	}
	
	private void end(long begin, String methodName, Query query, int length) {
		endStat(begin, methodName);
		if(logger.isTraceEnabled()) {
			logTrace(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + length + 
					", query=[" + ((query==null)? "" : query.toString()) + "]");			
		}
		else if(logger.isDebugEnabled()) {
			logDebug(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + length);
		}
	}

	private void end(long begin, String methodName, Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort, int offset, int size, int resultLength) {
		endStat(begin, methodName);
		if(logger.isTraceEnabled()) {
			logTrace(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + resultLength + 
					", contextUserId=" + contextUserId + 
					", aclQueryStr=[" + aclQueryStr + 
					"], mode=" + mode + 
					", query=[" + ((query==null)? null : query.toString()) + 
					"], sort=[" + ((sort==null)? null : sort.toString()) + 
					"], offset=" + offset +
					", size=" + size);
		}
		else if(logger.isDebugEnabled()) {
			logDebug(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + resultLength);
		}
	}
	
	private void end(long begin, String methodName, Query query, String tagBefore, String tagAfter, int length) {
		endStat(begin, methodName);
		if(logger.isTraceEnabled()) {
			logTrace(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + length + 
					", query=[" + ((query==null)? "" : query.toString()) + 
					"], tag-before=[" + ((tagBefore==null)? "" : tagBefore) + 
					"], tag-after=[" + ((tagAfter==null)? "" : tagAfter) + "]");
		}
		else if(logger.isDebugEnabled()) {
			logDebug(elapsedTimeInMs(begin) + " ms, " + methodName + ", result=" + length);
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
	
	synchronized IndexingResource getIndexingResource() {
		return indexingResource;
	}
	synchronized void setIndexingResource(IndexingResource resource) {
		this.indexingResource = resource;
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

	class IndexingResource {
		// This is immutable class, so these fields do not require monitor protection.
		private IndexWriter indexWriter;
		private SearcherManager searcherManager;
		private CommitStat commitStat;
		
		IndexingResource(IndexWriter writer, SearcherManager manager, CommitStat stat) {
			this.indexWriter = writer;
			this.searcherManager = manager;
			this.commitStat = stat;
		}		
		IndexWriter getIndexWriter() {
			return indexWriter;
		}
		SearcherManager getSearcherManager() {
			return searcherManager;
		}
		CommitStat getCommitStat() {
			return commitStat;
		}
		
		void commit() throws LuceneException {
			long startTime = System.nanoTime();

			// save these values before resetting it
			long firstOpTimeSinceLastCommit = this.getCommitStat().getFirstOpTimeSinceLastCommit();
			int numberOfOpsSinceLastCommit = this.getCommitStat().getNumberOfOpsSinceLastCommit();
			
			this.getCommitStat().reset();
			
			try {
				this.getIndexWriter().commit();
				logDebug("Index writer committed");
			} catch (IOException e) {
				throw newLuceneException("Error committing index writer", e);		
			} catch (OutOfMemoryError e) {
				getIndexingResource().closeOOM(e);
				throw e;
			}
			
			// The IndexReader.isCurrent() method that SearcherManager relies on to detect changes
			// returns true if the changes are already committed from the writer. So, it fails to
			// recognize the need to reopen the searcher at the time of search. So, we need to
			// force the searcher manager to reopen the searcher whenever a commit is performed
			// on the writer. 
			try {
				this.getSearcherManager().forceReopen();
			} catch (InterruptedException e) {
				// In the current code base, this can not and should not occur. So we will simply throw a regular exception.
				// In the future when we may add sophistication to the search manager an interrupt may actually mean something.
				throw newLuceneException("Error reopening index searcher", e);
			} catch (IOException e) {
				throw newLuceneException("Error reopening index searcher", e);
			}
			
			logInfo("Committed, firstOpTimeSinceLastCommit=" + firstOpTimeSinceLastCommit + 
					", numberOfOpsSinceLastCommit=" + numberOfOpsSinceLastCommit + 
					". It took " + elapsedTimeInMs(startTime) + " milliseconds");		
		}
		
		void optimize() throws LuceneException {
			// optimize and commit are independent of each other.
			
			long startTime = System.nanoTime();
			logInfo("Optimize started...");

			try {
				getIndexingResource().getIndexWriter().optimize();
				if(logger.isTraceEnabled())
					logTrace("Called optimize on writer");
			} catch (IOException e) {
				throw new LuceneException("Could not optimize the index", e);
			} catch (OutOfMemoryError e) {
				getIndexingResource().closeOOM(e);
				throw e;
			} 
			
			logInfo("Optimize completed. It took " + elapsedTimeInMs(startTime) + " milliseconds");
		}
		
		void optimize(int maxNumSegments) throws LuceneException {
			// optimize and commit are independent of each other.
			
			long startTime = System.nanoTime();
			logInfo("Optimize(" + maxNumSegments + ") started...");

			try {
				getIndexingResource().getIndexWriter().optimize(maxNumSegments);
				if(logger.isTraceEnabled())
					logTrace("Called optimize on writer");
			} catch (IOException e) {
				throw new LuceneException("Could not optimize the index", e);
			} catch (OutOfMemoryError e) {
				getIndexingResource().closeOOM(e);
				throw e;
			} 
			
			logInfo("Optimize completed. It took " + elapsedTimeInMs(startTime) + " milliseconds");
		}
		
		void expungeDeletes() throws LuceneException {
			long startTime = System.nanoTime();
			logInfo("ExpungeDeletes started...");

			try {
				getIndexingResource().getIndexWriter().expungeDeletes();
				if(logger.isTraceEnabled())
					logTrace("Called expungeDeletes on writer");
			} catch (IOException e) {
				throw new LuceneException("Could not expunge deletes from the index", e);
			} catch (OutOfMemoryError e) {
				getIndexingResource().closeOOM(e);
				throw e;
			} 
			
			logInfo("ExpungeDeletes completed. It took " + elapsedTimeInMs(startTime) + " milliseconds");
		}
		
		void close() {
			// Close existing searcher manager
			try {
				this.getSearcherManager().close();
			} catch (IOException e) {
				logError("Error closing searcher manager", e);
			}
			
			// Close existing index writer
			try {
				this.getIndexWriter().close();
				logDebug("Index writer closed");
			} catch (IOException e) {
				logError("Error closing index writer", e);
			}

			this.getCommitStat().reset();
		}
		
		void closeOOM(OutOfMemoryError e) {
			// Close the writer immediately so that we can prevent index corruption.
			// Should only be used for emergency closing upon OOM error.
			try {
				this.getIndexWriter().close();
			} catch (Throwable t) {
				try {
					logError("Error closing index writer upon OOM", t);
				}
				catch(Throwable ignore) {}
			}
			try {
				logError("Fatal OOM error occurred", e);
			}
			catch(Throwable ignore) {}
		}
	}
	
	// This class is used to help keep track of which SearcherManager that a particular
	// IndexSearcher instance originated from. 
	class IndexSearcherHandle {
		private IndexSearcher searcher;
		private SearcherManager manager;
		IndexSearcherHandle(IndexSearcher searcher, SearcherManager manager) {
			this.searcher = searcher;
			this.manager = manager;
		}
		IndexSearcher getIndexSearcher() {
			return searcher;
		}
		SearcherManager getSearcherManager() {
			return manager;
		}
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.lucene.LuceneProviderMBean#isStatisticsEnabled()
	 */
	@Override
	public boolean isStatisticsEnabled() {
		return statistics.isEnabled();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.lucene.LuceneProviderMBean#setStatisticsEnabled(boolean)
	 */
	@Override
	public void setStatisticsEnabled(boolean statisticsEnabled) {
		if(statisticsEnabled)
			statistics.enable();
		else
			statistics.disable();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.lucene.LuceneProviderMBean#clearStatistics()
	 */
	@Override
	public void clearStatistics() {
		statistics.clear();
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.lucene.LuceneProviderMBean#dumpStatisticsToLog()
	 */
	@Override
	public void dumpStatisticsToLog() {
		if(logger.isInfoEnabled())
			logger.info("Indexing and Search Statistics (" + indexName + ")" + System.getProperty("line.separator") + statistics.asString());
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.lucene.LuceneProviderMBean#dumpStatisticsAsString()
	 */
	@Override
	public String dumpStatisticsAsString() {
		return "Indexing and Search Statistics (" + indexName +"), " + statistics.asString();
	}

	private Query parseAclQueryStr(String aclQueryStr) throws ParseException {
		return getQueryParserWithWSA().parse(aclQueryStr);
	}
	
	private TLongHashSet obtainAccessibleFolderIds(IndexSearcher searcher, Long contextUserId, Query accessibleFoldersAclQuery) throws IOException {
		long startTime = System.nanoTime();
		
		AccessibleFoldersCollector collector = new AccessibleFoldersCollector();
		
		searcher.search(accessibleFoldersAclQuery, collector);
		
		TLongHashSet result = collector.getAccessibleFolderIds();
		
		end(startTime, "obtainAccessibleFolderIds", contextUserId, result.size());
		
		return result;
	}
	
	private Query makeAclInheritingEntriesPermissibleAclQuery(Query aclQuery) {
		NumericRangeQuery<Long> nrq = NumericRangeQuery.newLongRange(Constants.ENTRY_ACL_PARENT_ID_FIELD, 1L, Long.MAX_VALUE, true, true);
		
		if(!(aclQuery instanceof BooleanQuery)) {
			BooleanQuery bq = new BooleanQuery();
			bq.add(aclQuery, BooleanClause.Occur.SHOULD);
			aclQuery = bq;
		}
		
		((BooleanQuery)aclQuery).add(nrq, BooleanClause.Occur.SHOULD);
		
		return aclQuery;
	}
	
	private Query makeAccessibleFoldersAclQuery(Query aclQuery) {
		TermQuery tq = new TermQuery(new Term(Constants.ENTITY_FIELD, Constants.ENTITY_TYPE_FOLDER));
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(tq, BooleanClause.Occur.MUST);
		bq.add(aclQuery, BooleanClause.Occur.MUST);
		
		return bq;
	}
}
