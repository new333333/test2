/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.lucene;

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
	public void addDocument(String indexname, String UID, Document document)
			throws RemoteException;

	public void deleteDocument(String indexname, String uid)
			throws RemoteException;

	public int deleteDocuments(String indexname, Term term)
			throws RemoteException;

	public int deleteDocuments(String indexname, Query query)
			throws RemoteException;

	public void commit(String indexname) throws RemoteException;

	public void stop(String indexname) throws RemoteException;

	public void optimize(String indexname) throws RemoteException;

	public com.sitescape.team.lucene.Hits search(String indexname, Query query)
			throws RemoteException;

	public com.sitescape.team.lucene.Hits search(String indexname, Query query,
			int offset, int size) throws RemoteException;

	public com.sitescape.team.lucene.Hits search(String indexname, Query query,
			Sort sort) throws RemoteException;

	public com.sitescape.team.lucene.Hits search(String indexname, Query query,
			Sort sort, int offset, int size) throws RemoteException;

	public void updateDocument(String indexname, String uid, String fieldname,
			String fieldvalue) throws RemoteException;

	public void updateDocuments(String indexname, Query query,
			String fieldname, String fieldvalue) throws RemoteException;
	
	public void updateDocuments(String indexname, ArrayList<Query> queries,
			String fieldname, ArrayList<String> values) throws RemoteException;
	
	public ArrayList getTags(String indexName, Query query, Long id, String tag, String type, boolean isSuper)
		throws RemoteException;
	public ArrayList getSortTitles(String indexName, Query query, String start, String end,
			int skipsize) throws RemoteException;
}
