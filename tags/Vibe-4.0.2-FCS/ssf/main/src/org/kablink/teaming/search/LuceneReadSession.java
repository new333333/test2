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
package org.kablink.teaming.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.kablink.teaming.lucene.Hits;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.search.postfilter.PostFilterCallback;


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
public interface LuceneReadSession extends LuceneSession {
		
	/*
	 * Search and return the entire result set.
	 * 
	 * @throws LuceneException
	 */
	public Hits search(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query, List<String> fieldNames) throws LuceneException;

	/**
	 * Search and return only the portion of the result specified.
	 * 
	 * @param searchobject
	 * @param offset
	 * @param size
	 * @return
	 * @throws LuceneException
	 */
	public Hits search(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query, List<String> fieldNames, Sort sort, int offset, int size)
			throws LuceneException;
	
	public Hits search(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query, List<String> fieldNames, Sort sort, int offset, int size, PostFilterCallback callback)
			throws LuceneException;
	
	/**
	 * Return immediate children entities (entries and binders) of the specified parent
	 * binder (which is not a net folder) where those entities are accessible/visible 
	 * because the user has either direct/explicit access or inferred/implicit access to those.
	 *
	 * This method differs from other general purpose search method in that this implementation
	 * takes into account implicit/inferred accesses. 
	 * 
	 * NOTE: This method is to be used on a binder where the hierarchy below the binder has 
	 * all required ACLs indexed with them (e.g. adhoc folder, cloud folder, etc.).
	 * This method should NEVER be used on a net folder.
	 * 
	 * @param contextUserId
	 * @param aclQueryStr
	 * @param mode
	 * @param query
	 * @param sort
	 * @param offset
	 * @param size
	 * @param parentBinderId ID of the parent binder that is not a net folder
	 * @param parentBinderPath
	 * @return
	 * @throws LuceneException
	 */
	public Hits searchNonNetFolderOneLevelWithInferredAccess(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, int mode, Query query, Sort sort, int offset, int size, 
			Long parentBinderId, String parentBinderPath) throws LuceneException;

	/**
	 * Return immediate children entities (entries and binders) of the specified
	 * parent net folder where those entities are accessible/visible to the user.
	 * 
	 * NOTE: This method is to be used only on a binder where the entries below the 
	 * binder have no ACLs indexed with them (e.g. net folder exposed through FAMT).
	 * 
	 * @param contextUserId
	 * @param aclQueryStr
	 * @param titles
	 * @param query
	 * @param sort
	 * @param offset
	 * @param size
	 * @return
	 * @throws LuceneException
	 */
	public Hits searchNetFolderOneLevel(Long contextUserId, String baseAclQueryStr, String extendedAclQueryStr, List<String> titles, Query query, Sort sort, int offset, int size) throws LuceneException;
	
	/**
     * Return whether or not the calling user can gain inferred access to the specified
     * binder because the user has explicit access to at least one descendant binder of
     * the specified binder. 
     * 
     * Note: This method does not take into account whether or not the user has explicit
     * access to the specified binder. That is something that the caller hast to check
     * separately before invoking this method. 
     * 
	 * @param contextUserId
	 * @param aclQueryStr
	 * @param binderPath
	 * @return
	 * @throws LuceneException
	 */
	public boolean testInferredAccessToNonNetFolder(Long contextUserId,  String aclQueryStr, String binderPath) throws LuceneException;

	/**
	 * Get all the unique tags that this user can see, based on the wordroot passed in.
	 * 
	 * @param aclQueryStr
	 * @param wordroot
	 * @param type  - if null or tags - all tags will be returnet, can be personalTags or communityTags
	 * @return
	 * @throws LuceneException
	 */
	public ArrayList getTags(String aclQueryStr, String tag, String type)
			throws LuceneException;
	
	/**
	 * Get all the unique tags and their frequencies that this user can see, based on the wordroot passed in.
	 * 
	 * @param aclQueryStr
	 * @param wordroot
	 * @param type  - if null or tags - all tags will be returnet, can be personalTags or communityTags
	 * @return
	 * @throws LuceneException
	 */
	public ArrayList getTagsWithFrequency(String aclQueryStr, String tag, String type)
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
	public ArrayList getSortedTitles(Query query, String sortTitleFieldName, String start, String end, int skipsize)
			throws LuceneException;
	
	/**
	 * Get information about the net folders identified by the corresponding top folder IDs.
	 * 
	 * @param netFolderTopFolderIds
	 * @return
	 * @throws LuceneException
	 */
	public Map<String,Object> getNetFolderInfo(List<Long> netFolderTopFolderIds) throws LuceneException;
}
