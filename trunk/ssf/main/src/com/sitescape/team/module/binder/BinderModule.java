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

package com.sitescape.team.module.binder;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.SortedSet;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.domain.User;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.StatusTicket;
import com.sitescape.team.web.tree.DomTreeBuilder;

/**
 * @author Janet McCann
 *
 */
public interface BinderModule {
	public enum BinderOperation {
		copyBinder,
		deleteBinder,
		indexBinder,
		indexTree,
		manageDefinitions,
		manageMail,
		manageTag,
		manageTeamMembers,
		modifyBinder,
		moveBinder,
		report,
		setProperty,		
	}
	/**
	 * Subscribe to a binder.  Use to request notification of changes.
	 * @param binderId
	 * @param style
	 */
	public void addSubscription(Long binderId, Map<Integer,String[]> styles) 
		throws AccessControlException;
	/**
	 * Copy a binder to another location
	 * @param sourceId
	 * @param destinationId
	 * @param cascade - True to copy child binders
	 * @return
	 */
	public Long copyBinder(Long sourceId, Long destinationId, boolean cascade)
		throws AccessControlException;
	/**
	 * Delete a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.
	 * @param binderId
	 * @return Set of exceptions when deleting child binders
	 * @throws AccessControlException
	 */
	public Set<Exception> deleteBinder(Long binderId) 
		throws AccessControlException;
	/**
	 * Delete a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.
	 * @param binderId
	 * @param deleteMirroredSource indicates whether or not to delete the
	 * corresponding source resources (directories and files) if this binder
	 * or any of the child binders is mirrored.
	 * @return Set of exceptions when deleting child binders
	 * @throws AccessControlException
	 */
	public Set<Exception> deleteBinder(Long binderId, boolean deleteMirroredSource) 
		throws AccessControlException;
		
	/**
	 * Stop receiveing notifications that you have explicity requested.
	 * @param binderId
	 */
	public void deleteSubscription(Long binderId);
	/**
	 * Delete a tag on a binder
	 * @param binderId
	 * @param tagId
	 * @throws AccessControlException
	 */
	public void deleteTag(Long binderId, String tagId) 
		throws AccessControlException;
	/**
	 * Execute a search query.  Read access is automatically checked
	 * @param searchQuery
	 * @return
	 */
    public Map executeSearchQuery(Document searchQuery);
    /**
     * Same as <code>executeSearchQuery</code>
     * @param searchQuery
     * @param options
     * @return
     */
    public Map executeSearchQuery(Document searchQuery, Map options);
    /**
     * Execute a search query using a <code>QueryBuilder</code>-ready <code>Document</code>.
     * @param query
     * @param offset
     * @param maxResults
     * @return
     */
    public Map executeSearchQuery(Document query, int offset, int maxResults);
    /**
     * Get a binder
     * @param binderId
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */
    public Binder getBinder(Long binderId)
		throws NoBinderByTheIdException, AccessControlException;
    /**
     * Check binder access by a user
     * @param binderId
     * @param user
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */
    public boolean checkBinderAccess(Long binderId, User user);
    /**
     * Load binders.
     * @param binderIds
     * @return Binders sorted by title
     */
    public SortedSet<Binder> getBinders(Collection<Long> binderIds);
    /**
     * Search for child binders - 1 level
     * @param binder
     * @param options - search options
     * @return search results
     */
    public Map getBinders(Binder binder, Map options);
    /**
     * Finds a binder by path name. If no binder exists with the path name,
     * it returns <code>null</code>. If a matching binder exists but the
     * user has no access to it, it throws <code>AccessControlException</code>.
     * 
     * @param pathName
     * @return
     * @throws AccessControlException
     */
    public Binder getBinderByPathName(String pathName) 
    	throws AccessControlException;
    /**
     * Orders list
     * @param wordroot
     * @param type
     * @return
     */
    public List<Map> getSearchTags(String wordroot, String type); 
    /**
     * Get your subscription to this binder
     * @param binderId
     * @return
     */
	public Subscription getSubscription(Long binderId);
	/**
	 * Return community tags and the current users personal tags on the binder
	 * @param binder
	 * @return 
	 */
	public Collection<Tag> getTags(Binder binder);
	/**
	 * Get a list of team members for the given binder
	 * @param binder
	 * @param explodeGroups
	 * @return
	 */
	public SortedSet<Principal> getTeamMembers(Binder binder, boolean explodeGroups);
	/**
	 * Return a list of team member ids
	 * @param binderId
	 * @param explodeGroups
	 * @return
	 * @throws AccessControlException
	 */
	public Set<Long> getTeamMemberIds(Long binderId, boolean explodeGroups) throws AccessControlException;
	/**
	 * Ordered list of binders by title
	 * @param id
	 * @return search results
	 */
	public List<Map> getTeamMemberships(Long id);    
	/**
	 * Index only the binder and its attachments.  Do not include entries or sub-binders
	 * @param binderId
	 */
	public void indexBinder(Long binderId) throws AccessControlException;
	/**
	 * Index the binder and its attachments and optionally its entries.  Do not index sub-binders
	 * @param binderId
	 * @param includeEntries
	 */
    public void indexBinder(Long binderId, boolean includeEntries) throws AccessControlException;
    /**
     * Index a binder and its child binders, including all entries
     * @param binderId
     * @return Set of binderIds indexed
     */
    public Set<Long> indexTree(Long binderId) throws AccessControlException;
    /**
     * Same as <code>indexTree</code> except handles a collection of binders.  Use this as a
     * performance optimzation for multiple binders- it handles the index cleanup better
     * @param binderId
     * @return Set of binderIds indexed
     */
     public Set<Long> indexTree(Collection<Long> binderId, StatusTicket statusTicket) throws AccessControlException;
   
    /**
     * Modify a binder
     * @param binderId
     * @param inputData
     * @throws AccessControlException
     * @throws WriteFilesException
     */
    public void modifyBinder(Long binderId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException;
    /**
     * Same as <code>modifyBinder</code>.  Optionally include files to add and attachments to delete 
     * @param binderId
     * @param inputData
     * @param fileItems
     * @param deleteAttachments
     * @throws AccessControlException
     * @throws WriteFilesException
     */
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments)
    	throws AccessControlException, WriteFilesException;
    /**
     * Modify who gets email notifications.  The updates are applied to the <code>NotificationDef</code> for this binder
     * @param binderId
     * @param updates
     * @param principalIds
     */
    public void modifyNotification(Long binderId, Collection<Long> principalIds, Map updates)
    	throws AccessControlException;  
	/**
	 * Move a binder, all of its entries and sub-binders
	 * @param fromId - the binder to move
	 * @param toId - destination id
	 */
	public void moveBinder(Long binderId, Long toId)
		throws AccessControlException;  
	/**
	 * Set the definition inheritance on a binder
	 * @param binderId
	 * @param inheritFromParent
	 * @return
	 * @throws AccessControlException
	 */
    public Binder setDefinitions(Long binderId, boolean inheritFromParent)
    	throws AccessControlException;
    /**
     * Modify the list of definitions assocated with a binder
     * @param binderId
     * @param definitionIds
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List<String> definitionIds)
    	throws AccessControlException;
    /**
     * Modify the list of definitions and workflows assocated with a binder
     * @param binderId
     * @param definitionIds
     * @param workflowAssociations
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List<String> definitionIds, Map workflowAssociations)
    	throws AccessControlException;
    /**
     * Same as <code>setPosting</code>
     * @param binderId
     * @param emailAddress
     */
    public void setPosting(Long binderId, String emailAddress) 
    	throws AccessControlException;  
    /**
     * Same as <code>setPosting</code>
     * @param binderId
     * @param emailAddress
     * @param password
     */
    public void setPosting(Long binderId, String emailAddress, String password)
    	throws AccessControlException;  
    /**
     * Set a property to be associated with this binder
     * @param binderId
     * @param property
     * @param value
     */
    public void setProperty(Long binderId, String property, Object value)
    	throws AccessControlException;  
    /**
     * Create a new tag for this binder
     * @param binderId
     * @param newtag
     * @param community
     * @throws AccessControlException
     */
	public void setTag(Long binderId, String newtag, boolean community) 
		throws AccessControlException;  
	/**
	 * Set the team members for a binder.  By default inherits from parent
	 * @param binderId
	 * @param membersIds
	 * @throws AccessControlException
	 */
	public void setTeamMembers(Long binderId, Collection<Long> membersIds) 
		throws AccessControlException; 
	/**
	 * Enable or disable inheritting team membership
	 * @param binderId
	 * @param inherit
	 * @throws AccessControlException
	 */
	public void setTeamMembershipInherited(Long binderId, boolean inherit)
		throws AccessControlException;
	/**
	 * Test access to a binder. 
	 * @param binder
	 * @param operation 
	 * @return
	 */
	public boolean testAccess(Binder binder, BinderOperation operation);
	/**
	 * Same as <code>testAccess</code>, except an exception is thrown if access is denied
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess(Binder binder, BinderOperation operation)
		throws AccessControlException;
	
    /**
     * Traverse the binder tree returing a DOM structure containing workspaces and
     * folders
     * @param binderId
     * @param domTreeHelper
     * @param levels = depth to return.  -1 means all
     * @return
     * @throws AccessControlException
     */
	public Document getDomBinderTree(Long binderId, DomTreeBuilder domTreeHelper, int levels)
		throws AccessControlException;
 	public Document getDomBinderTree(Long topId, Long bottonId, DomTreeBuilder domTreeHelper) 
 		throws AccessControlException;

}
