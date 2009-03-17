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
package org.kablink.teaming.lucene;

import java.rmi.RemoteException;
import java.util.ArrayList;

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

	public void stop(String indexname) throws RemoteException;
	
	public void stop() throws RemoteException;

	public void optimize(String indexname) throws RemoteException;

	public org.kablink.teaming.lucene.Hits search(String indexname, Query query)
			throws RemoteException;

	public org.kablink.teaming.lucene.Hits search(String indexname, Query query,
			int offset, int size) throws RemoteException;

	public org.kablink.teaming.lucene.Hits search(String indexname, Query query,
			Sort sort) throws RemoteException;

	public org.kablink.teaming.lucene.Hits search(String indexname, Query query,
			Sort sort, int offset, int size) throws RemoteException;

	public void updateDocument(String indexname, String uid, String fieldname,
			String fieldvalue) throws RemoteException;

	public void updateDocuments(String indexname, Query query,
			String fieldname, String fieldvalue) throws RemoteException;
		
	public ArrayList getTags(String indexName, Query query, Long id, String tag, String type, boolean isSuper)
	throws RemoteException;

	public ArrayList getTagsWithFrequency(String indexName, Query query, Long id, String tag, String type, boolean isSuper)
	throws RemoteException;
	
	public String[] getNormTitles(String indexName, Query query, String start, String end,
			int skipsize) throws RemoteException;
	
	public void clearIndex(String indexname) throws RemoteException;
}
