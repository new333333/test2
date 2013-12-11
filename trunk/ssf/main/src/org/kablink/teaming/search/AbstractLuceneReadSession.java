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
package org.kablink.teaming.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.fi.FIException;
import org.kablink.teaming.fi.connection.acl.AclItemPrincipalMappingException;
import org.kablink.teaming.fi.connection.acl.AclResourceSession;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.search.postfilter.PostFilterCallback;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

public abstract class AbstractLuceneReadSession extends AbstractLuceneSession implements LuceneReadSession {

	protected AbstractLuceneReadSession(Log logger) {
		super(logger);
	}

	@Override
	public Hits search(Long contextUserId, String aclQueryStr, int mode, Query query) throws LuceneException {
		return search(contextUserId, aclQueryStr, mode, query, null, 0, -1);
	}

	@Override
	public Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort, int offset, int size)
			throws LuceneException {
		return search(contextUserId, aclQueryStr, mode, query, sort, offset, size, getPostFilterCallback());
	}

	@Override
	public Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort, int offset, int size, PostFilterCallback callback)
			throws LuceneException {
		SimpleProfiler.start("search()");
		long begin = System.nanoTime();
		PostFilteringStats stats = new PostFilteringStats();
		Hits hits = searchWithPostFiltering(contextUserId, aclQueryStr, mode, query, sort, offset, size, callback, stats);
		SimpleProfiler.stop("search()");
		endRead(begin, "search", contextUserId, aclQueryStr, mode, query, sort, offset, size, hits.length(), stats.sucessCount, stats.failureCount, stats.serviceCallCount);
		return hits;
	}

	@Override
	public ArrayList getTags(String aclQueryStr, String tag, String type) throws LuceneException {
		SimpleProfiler.start("getTags()");
		long begin = System.nanoTime();
		ArrayList results = invokeGetTags(aclQueryStr, tag, type);
		SimpleProfiler.stop("getTags()");
		endRead(begin, "getTags", aclQueryStr, results.size());
		return results;	
	}
	
	protected abstract ArrayList invokeGetTags(String aclQueryStr, String tag, String type) throws LuceneException;

	@Override
	public ArrayList getTagsWithFrequency(String aclQueryStr, String tag, String type) throws LuceneException {
		SimpleProfiler.start("getTagsWithFrequency()");
		long begin = System.nanoTime();
		ArrayList results = invokeGetTagsWithFrequency(aclQueryStr, tag, type);
		SimpleProfiler.stop("getTagsWithFrequency()");
		endRead(begin, "getTagsWithFrequency", aclQueryStr, results.size());
		return results;	
	}
	
	protected abstract ArrayList invokeGetTagsWithFrequency(String aclQueryStr, String tag, String type) throws LuceneException;
	
	@Override
	public ArrayList getSortedTitles(Query query, String sortTitleFieldName, String start, String end,
			int skipsize) throws LuceneException {
		SimpleProfiler.start("getSortedTitles()");
		long begin = System.nanoTime();
		String[] normResults;
		ArrayList resultTitles = invokeGetSortedTitles(query, sortTitleFieldName, start, end, skipsize);
		SimpleProfiler.stop("getSortedTitles()");
		endRead(begin, "getSortedTitles", query, resultTitles.size());		
		return resultTitles;
	}

	protected abstract ArrayList invokeGetSortedTitles(Query query, String sortTitleFieldName, String start, String end,
			int skipsize) throws LuceneException;

	@Override
	public Hits searchNonNetFolderOneLevelWithInferredAccess(Long contextUserId,
			String aclQueryStr, int mode, Query query, Sort sort, int offset,
			int size, Long parentBinderId, String parentBinderPath)
			throws LuceneException {
		SimpleProfiler.start("searchFolderOneLevelWithInferredAccess()");
		long begin = System.nanoTime();
		Hits hits = invokeSearchNonNetFolderOneLevelWithInferredAccess(contextUserId, aclQueryStr, mode, query, sort, offset, size, parentBinderId, parentBinderPath);
		hits = setClientSideFields(hits, offset, size);
		SimpleProfiler.stop("searchFolderOneLevelWithInferredAccess()");
		endRead(begin, "searchFolderOneLevelWithInferredAccess", query, hits.length());
		return hits;
	}

	protected abstract Hits invokeSearchNonNetFolderOneLevelWithInferredAccess(Long contextUserId,
			String aclQueryStr, int mode, Query query, Sort sort, int offset,
			int size, Long parentBinderId, String parentBinderPath)
			throws LuceneException;
	
	@Override
	public boolean testInferredAccessToNonNetFolder(Long contextUserId,
			String aclQueryStr, String binderPath) throws LuceneException {
		SimpleProfiler.start("testInferredAccessToNonNetFolder()");
		long begin = System.nanoTime();
		boolean result = invokeTestInferredAccessToBinder(contextUserId, aclQueryStr, binderPath);
		SimpleProfiler.stop("testInferredAccessToNonNetFolder()");
		endRead(begin, "testInferredAccessToNonNetFolder", aclQueryStr, binderPath, result);
		return result;
	}

	protected abstract boolean invokeTestInferredAccessToBinder(Long contextUserId,
			String aclQueryStr, String binderPath) throws LuceneException;
	
	@Override
	public Hits searchNetFolderOneLevel(Long contextUserId, String aclQueryStr,
			List<String> titles, Query query, Sort sort, int offset, int size)
			throws LuceneException {
		SimpleProfiler.start("searchFolderOneLevel()");
		long begin = System.nanoTime();
		Hits hits = setClientSideFields(invokeSearchNetFolderOneLevel(contextUserId, aclQueryStr, titles, query, sort, offset, size), offset, size);
		SimpleProfiler.stop("searchFolderOneLevelWithInferredAccess()");
		endRead(begin, "searchFolderOneLevel", query, hits.length());
		return hits;
	}
	
	protected abstract Hits invokeSearchNetFolderOneLevel(Long contextUserId, String aclQueryStr,
			List<String> titles, Query query, Sort sort, int offset, int size)
			throws LuceneException;
	
	protected int adjustSearchSizeToFindOutIfThereIsMoreWhenPostFilteringInvolved(int size) {
		if(size < 0 || size == Integer.MAX_VALUE) // unbounded search
			return Integer.MAX_VALUE;
		else // bounded search - get one more than requested
			return size + 1;
	}
	
	protected Hits setClientSideFields(Hits hits, int offset, int origSize) {
		if(origSize > 0 && origSize < Integer.MAX_VALUE) { // bounded
			if(hits.isTotalHitsApproximate()) {
				// The search total is approximate. Use look-ahead element to determine whether there's at least one more match.
				if(hits.length() > origSize) {
					hits.setLength(origSize); // Do not return the look-ahead element in this current result
					hits.setThereIsMore(true); // Indicate that there is at least one more match
				}				
			}
			else {
				// The search total is exact. In this case, there is no need for look-ahead, since whether or not there's at least 
				// one more match is easily computable from the information returned from the server.
				if(hits.getTotalHits() > offset + origSize)
					hits.setThereIsMore(true); // 
			}			
		}
		return hits;
	}
	
	protected PostFilterCallback getPostFilterCallback() {
		return new PostFilterCallback() {
			public boolean doFilter(Document doc, boolean noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl) {
				if(noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl)
					return true; // This doc represents an entry the user has access via sharing. Need not consult file system for access test.
				NumericField field = (NumericField) doc.getFieldable(Constants.ENTRY_ACL_PARENT_ID_FIELD);
				if(field == null)
					return true; // doesn't require access check
				if(field.getNumericValue().longValue() >= 0)
					return true; // doesn't require access check
				String resourceDriverName = doc.get(Constants.RESOURCE_DRIVER_NAME_FIELD);
				if(Validator.isNull(resourceDriverName)) {
					logger.warn("Can not perform access check because resource driver name is missing on this doc: " + doc.toString());
					return false; // fails the test
				}
				String resourcePath = doc.get(Constants.RESOURCE_PATH_FIELD);
				if(resourcePath == null)
					resourcePath = "";
				AclResourceSession session = org.kablink.teaming.module.shared.SearchUtils.openAclResourceSession(resourceDriverName);				
				if(session == null)
					return false; // can not perform access check on this
				try {
					String docType = doc.get(Constants.DOC_TYPE_FIELD);
					session.setPath(resourcePath, (docType != null && docType.equals(Constants.DOC_TYPE_BINDER))? Boolean.TRUE : Boolean.FALSE);
					try {
						return session.isVisible(AccessUtils.getFileSystemGroupIds(resourceDriverName));
					} catch (Exception e) {
						logger.error("Error checking visibility on resource [" + resourcePath + "]", e);
						return false; // fails the test
					}
				}
				finally {
					session.close();
				}
			}
		};
	}
	
	protected Hits searchWithPostFiltering(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
			int offset, int size, PostFilterCallback callback, PostFilteringStats stats) throws LuceneException {
		Hits hits = doSearchWithPostFiltering(contextUserId, aclQueryStr, mode, query, sort, offset, adjustSearchSizeToFindOutIfThereIsMoreWhenPostFilteringInvolved(size), callback, stats);
		return setClientSideFields(hits, offset, size);
	}
	
	private Hits doSearchWithPostFiltering(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
			int offset, int size, PostFilterCallback callback, PostFilteringStats stats) throws LuceneException {
		if(size <= 0)
			throw new IllegalArgumentException("Size must be positive number");
		
		if(Validator.isNull(aclQueryStr)) {
			// The user is not restricted by access check in the first place. So there is no point doing post filtering either.
			return invokeSearch(contextUserId, aclQueryStr, mode, query, sort, offset, size);
		}
		
		int filterPredictedSuccessPercentage = SPropsUtil.getInt("search.post.filtering.predicted.success.rate.percentage", 80);
		
		if(filterPredictedSuccessPercentage == 0) {
			// We don't expect any of the items to pass post filtering. Then there is no point in even trying searching.
			// NOTE: This value is for development use only and unsupported for production system.
			return new Hits(0); 
		}
		else if(filterPredictedSuccessPercentage == 100) {
			// We expect all items to pass post filtering. Then there is no point in applying post filtering.
			// NOTE: This value is for development use only and unsupported for production system.
			return invokeSearch(contextUserId, aclQueryStr, mode, query, sort, offset, size);			
		}
		else if(filterPredictedSuccessPercentage < 0 || filterPredictedSuccessPercentage > 100) {
			throw new ConfigurationException("The value of search.post.filtering.predicted.success.rate.percentage property must be between 0 and 100 non-inclusive"); 
		}
		
		int filterSuccessCount = 0;
		int filterFailureCount = 0;

		SearchServiceIterator searchServiceIterator = new SearchServiceIterator(contextUserId, aclQueryStr, mode, query, sort, offset, size, filterPredictedSuccessPercentage);
		
		// First, skip as many effective matches as offset
		int skipCount = 0;
		Hit hit;
		while(skipCount < offset && searchServiceIterator.hasNext()) {
			hit = searchServiceIterator.next();
			if(callback.doFilter(hit.doc, hit.noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl)) {
				// Effective match. This item counts;
				skipCount++; 
				filterSuccessCount++;
			}
			else {
				// Ineffective match. This item doesn't count.
				filterFailureCount++;
			}
		}
		if(skipCount < offset) {
			// There are not enough matching items to even get to the offset point.
			return new Hits(0);
		}
		
		List<Document> result = new ArrayList<Document>();		

		// Second, gather as many effective matches as size
		while(result.size() < size && searchServiceIterator.hasNext()) {
			hit = searchServiceIterator.next();
			if(callback.doFilter(hit.doc, hit.noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl)) {
				// Effective match. Put it in the result.
				result.add(hit.doc); 
				filterSuccessCount++;
			}
			else {
				// Ineffective match. Throw this out.
				filterFailureCount++;
			}
		}
		
		Hits hits = new Hits(result.size());
		for(int i = 0; i < result.size(); i++)
			hits.setDoc(result.get(i), i);
		// Adjust the raw total hits count by subtracting the number of items that failed the post 
		// filtering so far. In other word we factor in what we've learned by now. 
		// This adjustment may not amount to much, but still better than nothing.
		int adjustedTotalHits = searchServiceIterator.getTotalHits() - filterFailureCount;
		if(adjustedTotalHits < 0)
			adjustedTotalHits = 0;
		if(result.size() > 0 && (adjustedTotalHits < offset + result.size()))
			adjustedTotalHits = offset + result.size();
		hits.setTotalHits(adjustedTotalHits);
		
		stats.sucessCount = filterSuccessCount;
		stats.failureCount = filterFailureCount;
		stats.serviceCallCount = searchServiceIterator.getServiceCallCount();
		
		return hits;
	}
	
	protected abstract Hits invokeSearch(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort, int offset, int size) throws LuceneException;
	
	class SearchServiceIterator implements Iterator<Hit> {

		private static final int BEFORE_START = 1;
		private static final int IN_PROGRESS = 2;
		private static final int ENDED = 3;
		
		// Caller parameters
		private Long contextUserId;
		private String aclQueryStr;
		private int mode;
		private Query query;
		private Sort sort;
		private int offset;
		private int size;
		private int filterPredictedSuccessPercentage;
		
		// Service parameters
		private int state = BEFORE_START; // indicates initial state
		private int serviceOffset;	// offset for service call
		private int serviceSize; // size for service call
		private Hits serviceHits;   // Hits objects from the last call to service
		private int totalHits; // total hits count from the first hits object obtained
		private int hitsPosition; // position on current hits object representing the state of the iterator
		
		private int serviceCallCount;
		
		
		SearchServiceIterator(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
				int offset, int size, int filterPredictedSuccessPercentage) {
			this.contextUserId = contextUserId;
			this.aclQueryStr = aclQueryStr;
			this.mode = mode;
			this.query = query;
			this.sort = sort;
			this.offset = offset;
			this.size = size;
			this.filterPredictedSuccessPercentage = filterPredictedSuccessPercentage;
		}
		
		@Override
		public boolean hasNext() {
			if(state == BEFORE_START) { // Service call never made yet
				serviceOffset = 0; // Always start from offset of zero because we can't jump into middle due to post filtering requirement
				serviceSize = computeServiceSizeInitial(offset, size, filterPredictedSuccessPercentage); // Current batch size
				serviceHits = invokeSearch(contextUserId, aclQueryStr, mode, query, sort, serviceOffset, serviceSize);
				serviceCallCount++;
				totalHits = serviceHits.getTotalHits();
				hitsPosition = -1;
				if(serviceHits.length() > 0) {
					state = IN_PROGRESS;
					return true;
				}
				else {
					state = ENDED;
					return false;
				}
			}
			else if(state == IN_PROGRESS) {
				if(serviceHits.length() > hitsPosition + 1) {
					// There's still more element in the current hits object
					return true;
				}
				else {
					// There's no more element in the current hits object.
					if(serviceHits.length() < serviceSize) {
						// The current hits object returned less than we asked, which means there's no point in asking the index for more.
						state = ENDED;
						return false;
					}
					else if (serviceHits.getTotalHits() > serviceOffset + serviceSize) {
						// According to the current hits object, the index contains more items. Let's get it.
						serviceOffset += serviceSize;
						serviceSize = computeServiceSizeInitial();
						serviceHits = invokeSearch(contextUserId, aclQueryStr, mode, query, sort, serviceOffset, serviceSize);
						serviceCallCount++;
						hitsPosition = -1;
						if(serviceHits.length() > 0) {
							return true;
						}
						else {
							state = ENDED;
							return false;
						}						
					}
					else {
						// According to the current hits object, the index doesn't contain any more than we already retrieved.
						state = ENDED;
						return false;
					}
				}
			}
			else if (state == ENDED ) {
				return false;
			}
			else {
				return false;
			}
		}

		@Override
		public Hit next() {
			if(state == IN_PROGRESS) {
				hitsPosition++;
				return new Hit(serviceHits.doc(hitsPosition), serviceHits.noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl(hitsPosition));
			}
			else {
				throw new NoSuchElementException("Must be a bug in the code");
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public int getTotalHits() {
			return totalHits;
		}
		
		
		public int getServiceCallCount() {
			return serviceCallCount;
		}
		
		private int computeServiceSizeInitial(int offset, int size, int filterPredictedSuccessPercentage) {
			if(size == Integer.MAX_VALUE)
				return size;
			double serviceSize = ((double) (offset + size)) / ((double) filterPredictedSuccessPercentage) * 100.0;
			return (int) Math.ceil(serviceSize);
		}
		
		private int computeServiceSizeInitial() {	
			if(size == Integer.MAX_VALUE)
				throw new IllegalStateException("Shouldn't call this method with unbounded size");
			int incrSizePercentage = SPropsUtil.getInt("search.post.filtering.incr.size.percentage", 50);		
			double dsize = (((double) size) * incrSizePercentage) / 100.0;
			return (int) Math.ceil(dsize);
		}
	}
	
	static class Hit {
		Document doc;
		boolean noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl;
		
		Hit(Document doc, boolean noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl) {
			this.doc = doc;
			this.noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl = noIntrinsicAclStoredButAccessibleThroughFilrGrantedAcl;
		}
	}

	static class PostFilteringStats {
		int sucessCount;
		int failureCount;
		int serviceCallCount;
	}
}
