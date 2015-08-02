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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.lucene.LuceneProvider;
import org.kablink.teaming.search.AbstractLuceneReadSession;
import org.kablink.teaming.search.LuceneReadSession;

/**
 * This implementation provides access to local Lucene index.
 * 
 */
public class LocalLuceneReadSession extends AbstractLuceneReadSession implements LuceneReadSession {

	private static Log logger = LogFactory.getLog(LocalLuceneReadSession.class);
	
	private LuceneProvider luceneProvider;

	public LocalLuceneReadSession(LuceneProvider luceneProvider) {
		super(logger);
		this.luceneProvider = luceneProvider;
	}

	@Override
	protected Hits invokeSearch(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query, List<String> fieldNames, Sort sort, int offset, int size) {
		return luceneProvider.search(contextUserId, baseAclQueryStr, extendedAclQueryStr, mode, query, fieldNames, sort, offset, size);
	}
	
	@Override
	protected ArrayList invokeGetTags(String aclQueryStr, String tag, String type)
	throws LuceneException {
		return luceneProvider.getTags(aclQueryStr, tag, type, 
			RequestContextHolder.getRequestContext().getUserId().toString(), 
			RequestContextHolder.getRequestContext().getUser().isSuper());
	}
	
	@Override
	protected ArrayList invokeGetTagsWithFrequency(String aclQueryStr, String tag, String type)
			throws LuceneException {
		return luceneProvider.getTagsWithFrequency(aclQueryStr, tag, type, 
			RequestContextHolder.getRequestContext().getUserId().toString(), 
			RequestContextHolder.getRequestContext().getUser().isSuper());
	}
		
	// This returns an arraylist of arraylists.  Each child arraylist has 2 strings, (RangeStart, RangeEnd)
	// i.e. results[0] = {a, c}
	//      results[1] = {d, g}
	
	@Override
	protected ArrayList invokeGetSortedTitles(Query query, String sortTitleFieldName, String start, String end, int skipsize)
			throws LuceneException {
		return luceneProvider.getSortedTitlesAsList(query, sortTitleFieldName, start, end, skipsize);
	}	

	@Override
	public void close() {
		// luceneProvider is stateless, and there is no resource to release here.
	}

	@Override
	protected Hits invokeSearchNonNetFolderOneLevelWithInferredAccess(Long contextUserId,
			String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query, Sort sort, int offset,
			int size, Long parentBinderId, String parentBinderPath)
			throws LuceneException {
		return luceneProvider.searchNonNetFolderOneLevelWithInferredAccess(contextUserId, baseAclQueryStr, extendedAclQueryStr, mode, query, sort, offset, size, parentBinderId, parentBinderPath);
	}

	@Override
	protected boolean invokeTestInferredAccessToBinder(Long contextUserId,
			String aclQueryStr, String binderPath) throws LuceneException {
		return luceneProvider.testInferredAccessToNonNetFolder(contextUserId, aclQueryStr, binderPath);
	}

	@Override
	protected Hits invokeSearchNetFolderOneLevel(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr,
			List<String> titles, Query query, Sort sort, int offset, int size)
			throws LuceneException {
			return luceneProvider.searchNetFolderOneLevel(contextUserId, baseAclQueryStr, extendedAclQueryStr, titles, query, sort, offset, size);
	}
	
	@Override
	protected Map<String,Object> invokeGetNetFolderInfo(List<Long> netFolderTopFolderIds)
	throws LuceneException {
		return luceneProvider.getNetFolderInfo(netFolderTopFolderIds);
	}
	
}
