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

import gnu.trove.set.hash.TLongHashSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Filter;
import org.kablink.util.search.Constants;

/**
 * @author jong
 *
 */
public class AclInheritingAccessibleEntriesFilter extends Filter {

	private static final long serialVersionUID = 1L;
	
	Filter aclInheritingEntriesFilter;
	TLongHashSet baseAccessibleFolderIds;
	TLongHashSet extendedAccessibleFolderIds;
	
	TLongHashSet noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds = new TLongHashSet();
	
	public AclInheritingAccessibleEntriesFilter(Filter aclInheritingEntriesFilter, TLongHashSet baseAccessibleFolderIds, TLongHashSet extendedAccessibleFolderIds) {
		this.aclInheritingEntriesFilter = aclInheritingEntriesFilter;
		this.baseAccessibleFolderIds = baseAccessibleFolderIds;
		this.extendedAccessibleFolderIds = extendedAccessibleFolderIds;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Filter#getDocIdSet(org.apache.lucene.index.IndexReader)
	 */
	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		/*
		 * This numeric field - Constants.ENTRY_ACL_PARENT_ID_FIELD - is only used for two specific purposes:
		 *  
		 * Case 1: If the value is positive number, it means that this entry/file inherits its ACL from
		 *         the parent folder and the value represents the ID of the parent folder.
		 *         
		 * Case 2: If the value is negative number, it means that the system does not store the ACL of the
		 *         particular entry/file in the search index, and consequently the search engine is unable 
		 *         to make final determination as to whether the user has access to this entry/file or not.
		 *         It is the caller's responsibility to perform access check and filter out those entries/files
		 *         that the user doesn't have access to after the search result is returned to the caller. 
		 *         The negative value represents the ID of the parent folder multiplied by -1.
		 */
		final long[] entryAclParentIds = FieldCache.DEFAULT.getLongs(reader, Constants.ENTRY_ACL_PARENT_ID_FIELD);
		final long[] entityIds = FieldCache.DEFAULT.getLongs(reader, Constants.ENTITY_ID_FIELD);
		
		final DocIdSet innerSet = aclInheritingEntriesFilter.getDocIdSet(reader);
		
		return new NullSafeFilteredDocIdSet(innerSet) {
			@Override
			protected boolean match(int docid) throws IOException {
				long entryAclParentId = entryAclParentIds[docid];
				if(entryAclParentId > 0) {
					// This doc represents an entry (or an attachment within an entry) that inherits ACL
					// from its parent folder. We need to check if the user has access to the parent folder.
					if(baseAccessibleFolderIds.contains(entryAclParentId) || extendedAccessibleFolderIds.contains(entryAclParentId))
						return true; // The user has access to parent folder. Grant access to this entry/attachment.
					else
						return false; // The user has no access to parent folder. Deny access to this entry/attachment.
				}
				else if(entryAclParentId < 0) {
					// This doc represents an entry (or an attachment within an entry) whose ACL information
					// is not stored with the search index, which means that the search index does not know
					// whether the user has access to this entry or not. We include this entry in the result
					// AS LONG AS it is in a folder that the user has access to.
					if(baseAccessibleFolderIds.contains(entryAclParentId * -1)) {
						// The user has access to parent folder through base ACL. 
						// Include this entry/attachment as a candidate in the result for now, 
						// so that the caller can check if the user has access to this file or not.
						return true; 
					}
					else if(extendedAccessibleFolderIds.contains(entryAclParentId * -1)) {
						// The user has access to parent folder through extended ACL such as sharing. 
						// Include this entry/attachment in the result. Also, add the entry ID of this
						// document into a set, which can be used to instruct the caller that it doesn't
						// have to perform post-filtering on this entry.
						noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds.add(entityIds[docid]);
						return true;
					}
					else {
						return false; // The user has no access to parent folder. Include this entry/attachment in the result for now.
					}
				}
				else {
					// In the current usage, this can not occur, because previous filter in the chain
					// (aclInheritingEntriesFilter) will have already excluded this possibility.
					// That is, the filter would have already filtered out all documents that do not
					// contain Constants.ENTRY_ACL_PARENT_ID_FIELD field, and this field can only
					// have non-zero values.
					return false;
				}
			}			
		};
	}
	
	/*
	 * Return a set of IDs of file entries (from net folders) whose parent folders are accessible to the user due to sharing.
	 */
	public TLongHashSet getNoIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds() {
		return noIntrinsicAclStoredButAccessibleThroughExtendedAclOnParentFolder_entryIds;
	}

}
