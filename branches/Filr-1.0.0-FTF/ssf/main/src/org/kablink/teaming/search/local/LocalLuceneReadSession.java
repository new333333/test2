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

import java.util.ArrayList;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.lucene.LuceneProvider;
import org.kablink.teaming.search.LuceneReadSession;
import org.kablink.teaming.util.SimpleProfiler;

/**
 * This implementation provides access to local Lucene index.
 * 
 */
public class LocalLuceneReadSession implements LuceneReadSession {

	private LuceneProvider luceneProvider;

	public LocalLuceneReadSession(LuceneProvider luceneProvider) {
		this.luceneProvider = luceneProvider;
	}

	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query) {
		return this.search(contextUserId, aclQueryStr, mode, query, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, int offset,
			int size) {
		SimpleProfiler.start("LocalLuceneReadSession.search(Query,int,int)");
		try {
			return luceneProvider.search(contextUserId, aclQueryStr, mode, query, offset, size);
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.search(Query,int,int)");
		}
	}

	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort) {
		return this.search(contextUserId, aclQueryStr, mode, query, sort, 0, -1);
	}

	public org.kablink.teaming.lucene.Hits search(Long contextUserId, String aclQueryStr, int mode, Query query, Sort sort,
			int offset, int size) {
		SimpleProfiler.start("LocalLuceneReadSession.search(Query,Sort,int,int)");
		try {
			return luceneProvider.search(contextUserId, aclQueryStr, mode, query, sort, offset, size);
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.search(Query,Sort,int,int)");
		}
	}

	public ArrayList getTags(String aclQueryStr, String tag, String type)
	throws LuceneException {
		SimpleProfiler.start("LocalLuceneReadSession.getTags()");
		try {
			return luceneProvider.getTags(aclQueryStr, tag, type, 
				RequestContextHolder.getRequestContext().getUserId().toString(), 
				RequestContextHolder.getRequestContext().getUser().isSuper());
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.getTags()");
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
	public ArrayList getTagsWithFrequency(String aclQueryStr, String tag, String type)
			throws LuceneException {
		SimpleProfiler.start("LocalLuceneReadSession.getTagsWithFrequency()");
		try {
			return luceneProvider.getTagsWithFrequency(aclQueryStr, tag, type, 
				RequestContextHolder.getRequestContext().getUserId().toString(), 
				RequestContextHolder.getRequestContext().getUser().isSuper());
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.getTagsWithFrequency()");
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
	
	public ArrayList getSortedTitles(Query query, String sortTitleFieldName, String start, String end, int skipsize)
			throws LuceneException {
		SimpleProfiler.start("LocalLuceneReadSession.getSortedTitles()");
		try {
			return luceneProvider.getSortedTitlesAsList(query, sortTitleFieldName, start, end, skipsize);
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.getSortedTitles()");	
		}
	}	

	public void close() {
		// luceneProvider is stateless, and there is no resource to release here.
	}

	@Override
	public Hits searchFolderOneLevelWithInferredAccess(Long contextUserId,
			String aclQueryStr, int mode, Query query, Sort sort, int offset,
			int size, Long parentBinderId, String parentBinderPath)
			throws LuceneException {
		SimpleProfiler.start("LocalLuceneReadSession.searchFolderOneLevelWithInferredAccess()");
		try {
			return luceneProvider.searchFolderOneLevelWithInferredAccess(contextUserId, aclQueryStr, mode, query, sort, offset, size, parentBinderId, parentBinderPath);
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.searchFolderOneLevelWithInferredAccess()");
		}
	}

	@Override
	public boolean testInferredAccessToBinder(Long contextUserId,
			String aclQueryStr, String binderPath) throws LuceneException {
		SimpleProfiler.start("LocalLuceneReadSession.testInferredAccessToBinder()");
		try {
			return luceneProvider.testInferredAccessToBinder(contextUserId, aclQueryStr, binderPath);
		}
		finally {
			SimpleProfiler.stop("LocalLuceneReadSession.testInferredAccessToBinder()");
		}
	}
}
