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

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

/**
 * Title: SsfIndexInterface Description: This is the interface definition to be
 * implemented by the server. Copyright: Copyright (c) 2005 Company: SiteScape,
 * Inc.
 * 
 * @author Roy Klein
 * @version 1.0
 */

public interface SsfIndexInterface extends java.rmi.Remote {
	public void addDocuments(String indexname, ArrayList documents)
	throws RemoteException;

	public void deleteDocuments(String indexname, Term term)
			throws RemoteException;

	public void addDeleteDocuments(String indexname, ArrayList docsToAddOrDelete) throws RemoteException;
	
	public void commit(String indexname) throws RemoteException;

	public void optimize(String indexname) throws RemoteException;

	public org.kablink.teaming.lucene.Hits search(String indexname, Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query,
			List<String> fieldNames, Sort sort, int offset, int size) throws RemoteException;

	public org.kablink.teaming.lucene.Hits searchNonNetFolderOneLevelWithInferredAccess(String indexname, Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query,
			Sort sort, int offset, int size, Long parentBinderId, String parentBinderPath) throws RemoteException;

	public org.kablink.teaming.lucene.Hits searchNetFolderOneLevel(String indexname, Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, List<String> titles, Query query, Sort sort, int offset, int size) 
			throws RemoteException;

	public boolean testInferredAccessToNonNetFolder(String indexname, Long contextUserId,  String aclQueryStr, String binderPath) throws RemoteException;

	public ArrayList getTags(String indexName, String aclQueryStr, Long id, String tag, String type, boolean isSuper)
	throws RemoteException;

	public ArrayList getTagsWithFrequency(String indexName, String aclQueryStr, Long id, String tag, String type, boolean isSuper)
	throws RemoteException;
	
	public String[] getSortedTitles(String indexName, Query query, String sortTitleFieldName, String start, String end,
			int skipsize) throws RemoteException;
	
	public Map<String,Object> getNetFolderInfo(String indexName, List<Long> netFolderTopFolderIds) throws RemoteException;
	
	public void clearIndex(String indexname) throws RemoteException;
	
	public void shutdown() throws RemoteException;
	
	public void test() throws RemoteException;
}
