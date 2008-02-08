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
package com.sitescape.team.search;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.sitescape.team.lucene.Hits;
import com.sitescape.team.search.LuceneException;

/**
 * The main runtime interface between application and Lucene service. 
 * <p>
 * It is not intended that implementors be threadsafe. Instead each thread
 * should obtain its own instance from a <code>LuceneSessionFactory</code>.
 * <p>
 * Important semantics regarding the API:
 * <p>
 * 1) The API does not guarantee that changes made to the index through
 * a method call is flushed out to disk immediately. This implies that
 * the subsequent call may or may not see the changes made by the 
 * previous calls even when made through the same session. 
 * If the application requires the changes to be synchronized to disk
 * immediately, it must call <code>flush</code> immediately after the
 * operation. Then it is guaranteed that the next call will see the changes.
 * <p> 
 * 2) The API says nothing about when, how often, or under what circumstances
 * the Lucene service actually flushes changes out to the persistent store
 * OTHER THAN the fact that it guarantees synchronization when the application 
 * explicitly calls <code>flush</code> method. Other details are left to
 * the service implementation.
 * <p>
 * 3) There is no client-controlled rollback semantics supported.
 * 
 * @author Jong Kim
 *
 */
public interface LuceneSession {
	/**
	 * Add a document.
	 * 
	 * @param doc
	 * @throws LuceneException
	 */
	public void addDocument(Document doc) throws LuceneException;

	/**
	 * Add a collection of documents.
	 * 
	 * @param doc
	 * @throws LuceneException
	 */
	public void addDocuments(Collection docs) throws LuceneException;

	/**
	 * Update the field in the document identified by the uid. 
	 * 
	 * @param uid
	 * @param fieldname
	 * @param fieldvalue
	 * @throws LuceneException
	 */
	//public void updateDocument(String uid, String fieldname, String fieldvalue)
	//		throws LuceneException;

	/**
	 * Update all documents matching the query.
	 * 
	 * @param query
	 * @param fieldname
	 * @param fieldvalue
	 * @throws LuceneException
	 */
	public void updateDocuments(Query query, String fieldname, String fieldvalue)
			throws LuceneException;
	
	/**
	 * Delete the document identified by the uid. 
	 * 
	 * @param uid
	 * @throws LuceneException
	 */
	public void deleteDocument(String uid) throws LuceneException;

	/**
	 * Delete all documents matching the term.
	 * 
	 * @param term
	 * @throws LuceneException
	 */
	public void deleteDocuments(Term term) throws LuceneException;

	/**
	 * Apply the query and delete all matching documents.
	 * 
	 * @param query
	 * @throws LuceneException
	 */
	public void deleteDocuments(Query query) throws LuceneException;

	/**
	 * Search and return the entire result set.
	 * 
	 * @throws LuceneException
	 */
	public Hits search(Query query) throws LuceneException;

	/**
	 * Search and return only the portion of the result specified.
	 * 
	 * @param query
	 * @param offset
	 * @param size
	 * @return
	 * @throws LuceneException
	 */
	public Hits search(Query query, int offset, int size)
			throws LuceneException;

	/**
	 * Force the <code>LuceneSession</code> to flush.
	 * <p>
	 * Flushing is the process of synchronizing the underlying persistent store
	 * (ie, index files on disk) with persistable state held in memory.
	 * 
	 * @throws LuceneException
	 *
	 */

	/**
	 * Search and return the entire result set.
	 * 
	 * @throws LuceneException
	 */
	public Hits search(Query query, Sort sort) throws LuceneException;

	/**
	 * Search and return only the portion of the result specified.
	 * 
	 * @param searchobject
	 * @param offset
	 * @param size
	 * @return
	 * @throws LuceneException
	 */
	public Hits search(Query query, Sort sort, int offset, int size)
			throws LuceneException;
	
	/**
	 * Get all the unique tags that this user can see, based on the wordroot passed in.
	 * 
	 * @param query
	 * @param wordroot
	 * @param type  - if null or tags - all tags will be returnet, can be personalTags or communityTags
	 * @return
	 * @throws LuceneException
	 */
	public ArrayList getTags(Query query, String tag, String type)
			throws LuceneException;

	/**
	 * Get all the unique sortTitles that this user can see, based on the range passed in.
	 * 
	 * @param query
	 * @param wordroot
	 * @param type  - if null or tags - all tags will be returnet, can be personalTags or communityTags
	 * @return
	 * @throws LuceneException
	 */
	public ArrayList getNormTitles(Query query, String start, String end, int skipsize)
			throws LuceneException;

	
	/**
	 * Force the <code>LuceneSession</code> to flush.
	 * <p>
	 * Flushing is the process of synchronizing the underlying persistent store
	 * (ie, index files on disk) with persistable state held in memory.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void flush() throws LuceneException;

	/**
	 * Force the <code>LuceneSession</code> to optimize the index. This helps reduce
	 * the size of the index by removing deleted docs and renumbering the inner docs.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void optimize() throws LuceneException;
	
	/**
	 * Force the <code>LuceneSession</code> to clear the contents of the index.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void clearIndex() throws LuceneException;
	
	/**
	 * End the <code>LuceneSession</code> by disconnecting from the Lucene
	 * service and cleaning up. Note that this does NOT implicitly perform
	 * <code>flush</code> operation. In other words, <code>flush</code>
	 * must be invoked explicitly by the caller before closing the session
	 * if that was intended. Once <code>close</code> method is called, the 
	 * session object is no longer usable.
	 * 
	 * @throws LuceneException
	 *
	 */
	public void close() throws LuceneException;
}
