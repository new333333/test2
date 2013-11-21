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
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;
import org.kablink.util.search.Constants;

/**
 * @author jong
 *
 */
public class ImplicitlyAccessibleSubFoldersCollector extends Collector {

	private String parentBinderPath;
	private Set<String> implicitlyAccessibleSubFolderPaths;
	
	private String[] binderPaths; 
	
	public ImplicitlyAccessibleSubFoldersCollector(String parentBinderPath) {
		this.parentBinderPath = parentBinderPath.toLowerCase();
		implicitlyAccessibleSubFolderPaths = new HashSet<String>();
	}
	
	public Set<String> getImplicitlyAccessibleSubFolderPaths() {
		return implicitlyAccessibleSubFolderPaths;
	}
	
	@Override
	public void setScorer(Scorer scorer) throws IOException {
		// We don't need score.
	}

	@Override
	public void collect(int doc) throws IOException {
		String binderPath = binderPaths[doc];

		if(binderPath != null) {
			int index = binderPath.indexOf('/', parentBinderPath.length()+1);
			if(index < 0) {
				// This binder path itself represents an immediate child of the specified parent binder.
				implicitlyAccessibleSubFolderPaths.add(binderPath);				
			}
			else {
				// This binder path represents a descendant that is not an immediate child.
				// Get the immediate child of the specified parent binder based on this path. 
				implicitlyAccessibleSubFolderPaths.add(binderPath.substring(0, index));
			}
		}
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase)
			throws IOException {
		binderPaths = FieldCache.DEFAULT.getStrings(reader, Constants.SORT_ENTITY_PATH);
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

}
