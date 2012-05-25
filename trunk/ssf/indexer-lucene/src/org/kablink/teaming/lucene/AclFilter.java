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
public class AclFilter extends Filter {

	private static final long serialVersionUID = 1L;
	
	Filter aclInheritingEntriesPermissibleAclFilter;
	TLongHashSet accessibleFolderIds;
	
	public AclFilter(Filter aclInheritingEntriesPermissibleAclFilter, TLongHashSet accessibleFolderIds) {
		this.aclInheritingEntriesPermissibleAclFilter = aclInheritingEntriesPermissibleAclFilter;
		this.accessibleFolderIds = accessibleFolderIds;
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.search.Filter#getDocIdSet(org.apache.lucene.index.IndexReader)
	 */
	@Override
	public DocIdSet getDocIdSet(IndexReader reader) throws IOException {
		final long[] entryAclParentIds = FieldCache.DEFAULT.getLongs(reader, Constants.ENTRY_ACL_PARENT_ID_FIELD);
		
		final DocIdSet innerSet = aclInheritingEntriesPermissibleAclFilter.getDocIdSet(reader);
		
		return new NullSafeFilteredDocIdSet(innerSet, accessibleFolderIds, entryAclParentIds) {
			@Override
			protected boolean match(int docid) throws IOException {
				long entryAclParentId = entryAclParentIds[docid];
				if(entryAclParentId > 0) {
					// This doc represents an entry (or an attachment within that entry) that inherits ACL
					// from its parent folder. We need to check if the user has access to the parent folder.
					if(accessibleFolderIds.contains(entryAclParentId))
						return true; // The user has access to parent folder. Grant access to this entry.
					else
						return false; // The user has no access to parent folder. Deny access to this entry.
				}
				else {
					// This doc does not represent an entry that inherits ACL from its parent folder,
					// which means that this doc represent either binder or entry that has its own
					// set of ACL (such as folder entries with entry-level ACLs, or user objects).
					// The previous filter in the chain (aclInheritingEntriesPermissibleAclFilter)
					// has already validated this doc, so simply pass it on.
					return true;
				}
			}			
		};
	}

}
