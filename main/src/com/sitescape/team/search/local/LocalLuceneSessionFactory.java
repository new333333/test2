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
package com.sitescape.team.search.local;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.DisposableBean;

import com.sitescape.team.lucene.LuceneHelper;
import com.sitescape.team.search.AbstractLuceneSessionFactory;
import com.sitescape.team.search.LuceneException;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.FileHelper;

/**
 * @author Jong Kim
 *
 */
public class LocalLuceneSessionFactory extends AbstractLuceneSessionFactory 
implements DisposableBean, LocalLuceneSessionFactoryMBean {
    
	private String indexRootDir;
	private static ConcurrentHashMap<String,String> indexNameMap= new ConcurrentHashMap();
	

	public LuceneSession openSession(String indexName) {
		String indexDirPath = getIndexDirPath(indexName);
		
		try {
			FileHelper.mkdirsIfNecessary(indexDirPath);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		if (!indexNameMap.containsKey(indexName)) {
			indexNameMap.put(indexName, indexDirPath);
			LuceneHelper.unlock(indexDirPath);
		}
        return new LocalLuceneSession(indexDirPath);
    }
	
	public void setIndexRootDir(String indexRootDir) {
		if(indexRootDir.endsWith(Constants.SLASH))
			this.indexRootDir = indexRootDir;
		else
			this.indexRootDir = indexRootDir + Constants.SLASH;
	}
	
	public String getIndexRootDir() {
		return indexRootDir;
	}
	
	private String getIndexDirPath(String indexName) {
		return indexRootDir + indexName;
	}

	public void destroy() throws Exception {
		String indexDirPath = "";
		if (indexNameMap.size() == 0) return;
		Iterator iter = indexNameMap.keySet().iterator();
		while (iter.hasNext()) {
			indexDirPath = indexNameMap.get(iter.next());
			LuceneHelper.closeAll(indexDirPath);
			LuceneHelper.unlock(indexDirPath);
		}
		
	}
}
