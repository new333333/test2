/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
import java.util.HashSet;
import java.util.Set;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.kablink.util.search.Constants;

/**
 * @author jong
 *
 */
public class ThreadLocalAclQueryFilter extends Filter{

	private static final long serialVersionUID = 1L;
	
	private static final ThreadLocal<Set<String>> threadLocal = new ThreadLocal<Set<String>>();
	
	Filter aclQueryFilter; // original ACL query filter
	
	public ThreadLocalAclQueryFilter(Filter aclQueryFilter) {
		if(aclQueryFilter == null)
			throw new IllegalArgumentException("ACL query filter must be specified");
		this.aclQueryFilter = aclQueryFilter;
	}

	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		final long[] entryAclParentIds = FieldCache.DEFAULT.getLongs(reader, Constants.ENTRY_ACL_PARENT_ID_FIELD);
		final String[] entryIdStrs = FieldCache.DEFAULT.getStrings(reader, Constants.DOCID_FIELD);
		
		Set<String> noAclButAccessibleThroughSharingEntryIds = new HashSet<String>();

		final DocIdSet docIdSet = aclQueryFilter.getDocIdSet(reader);
		
		if(docIdSet != null) {
			DocIdSetIterator it = docIdSet.iterator();
			int docId = it.nextDoc();
			while(docId != DocIdSetIterator.NO_MORE_DOCS) {
				if(entryAclParentIds[docId] < 0) {
					// This doc represents an entry/reply/attachment whose intrinsic ACL information is not
					// stored with the search index (e.g. a net folder file). The fact that this doc nevertheless
					// passed the original ACL query filter is a clear indication that there are "additional" 
					// ACL indexed on this doc that made it pass the filter. In the current application, that 
					// should be share-granted ACL. We need to pass this information up to the caller
					noAclButAccessibleThroughSharingEntryIds.add(entryIdStrs[docId]);
				}
				docId = it.nextDoc();
			}
		}
		
		if(noAclButAccessibleThroughSharingEntryIds.isEmpty())
			noAclButAccessibleThroughSharingEntryIds = null;
		
		threadLocal.set(noAclButAccessibleThroughSharingEntryIds);
		
		return docIdSet;
	}

	public static Set<String> getNoAclButAccessibleThroughSharingEntryIds() {
		return threadLocal.get();
	}
	
	public static void clear() {
		threadLocal.set(null);
	}
}
