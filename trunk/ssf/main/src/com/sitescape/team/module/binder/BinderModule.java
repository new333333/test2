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
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;

/**
 * @author Janet McCann
 *
 */
public interface BinderModule {
	/**
	 * Subscribe to a binder.  Use to request notification of changes.
	 * @param binderId
	 * @param style
	 */
	public void addSubscription(Long binderId, int style);
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
	public void deleteTag(Long binderId, String tagId) throws AccessControlException;
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
     * Get a binder
     * @param binderId
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */
    public Binder getBinder(Long binderId)
		throws NoBinderByTheIdException, AccessControlException;
    /**
     * Load binders,
     * @param binderIds
     * @return Binders sorted by title
     */
    public SortedSet<Binder> getBinders(Collection<Long> binderIds);
    /**
     * Search for child binders - 1 level
     * @param binder
     * @param options
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
    public Binder getBinderByPathName(String pathName) throws AccessControlException;
    /**
     * Get the current schedule information for email notifications on this binder.
     * If the binder is a sub-folder, its schedule is handled by the topFolder
     * @param binderId
     * @return
     */
    public ScheduleInfo getNotificationConfig(Long binderId);
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
	public List<Tag> getTags(Binder binder);
	/**
	 * Get a list of team members for the given binder
	 * @param binderId
	 * @param explodeGroups
	 * @return
	 * @throws AccessControlException
	 */
	public SortedSet<Principal> getTeamMembers(Long binderId, boolean explodeGroups) throws AccessControlException;
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
	public void indexBinder(Long binderId);
	/**
	 * Index the binder and its attachments and optionally its entries.  Do not index sub-binders
	 * @param binderId
	 * @param includeEntries
	 */
    public void indexBinder(Long binderId, boolean includeEntries);
    /**
     * Index a binder and its child binders, including all entries
     * @param binderId
     * @return Set of binderIds indexed
     */
    public Set<Long> indexTree(Long binderId);
    /**
     * Same as <code>indexTree</code> except handles a collection of binders.  Use this as a
     * performance optimzation for multiple binders- it handles the index cleanup better
     * @param binderId
     * @return Set of binderIds indexed
     */
     public Set<Long> indexTree(Collection<Long> binderId);
   
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
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException;
    /**
     * Modify who gets email notifications.  The upates are applied to the <code>NotificationDef</code> for this binder
     * @param binderId
     * @param updates
     * @param principalIds
     */
    public void modifyNotification(Long binderId, Map updates, Collection<Long> principalIds) throws AccessControlException;  
	/**
	 * Move a binder, all of its entries and sub-binders
	 * @param fromId - the binder to move
	 * @param toId - destination id
	 */
	public void moveBinder(Long binderId, Long toId) throws AccessControlException;  
	/**
	 * Set the definition inheritance on a binder
	 * @param binderId
	 * @param inheritFromParent
	 * @return
	 * @throws AccessControlException
	 */
    public Binder setDefinitions(Long binderId, boolean inheritFromParent)  throws AccessControlException;
    /**
     * Modify the list of definitions assocated with a binder
     * @param binderId
     * @param definitionIds
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List<String> definitionIds) throws AccessControlException;
    /**
     * Modify the list of definitions and workflows assocated with a binder
     * @param binderId
     * @param definitionIds
     * @param workflowAssociations
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List<String> definitionIds, Map workflowAssociations) throws AccessControlException;
    /**
     * Set the schedule by which notifications are sent.  Use this to both enable and disable notifications
     * @param binderId
     * @param config
     */
    public void setNotificationConfig(Long binderId, ScheduleInfo config) throws AccessControlException;  
	/**
	 * Update the <code>com.sitescape.team.domain.PostingDef</code> associated with this binder.
	 * If one doesn't exists, create one.  
	 * If emailAddress is null, delete the current <code>PostingDef</code>
	 * @param binderId
	 * @param updates
	 */
    public void setPosting(Long binderId, Map updates) throws AccessControlException;  
    /**
     * Same as <code>setPosting</code>
     * @param binderId
     * @param emailAddress
     */
    public void setPosting(Long binderId, String emailAddress) throws AccessControlException;  
    /**
     * Set a property to be associated with this binder
     * @param binderId
     * @param property
     * @param value
     */
    public void setProperty(Long binderId, String property, Object value) throws AccessControlException;  
    /**
     * Create a new tag for this binder
     * @param binderId
     * @param newtag
     * @param community
     * @throws AccessControlException
     */
	public void setTag(Long binderId, String newtag, boolean community) throws AccessControlException;  
	/**
	 * Set the team members for a binder.  By default inherits from parent
	 * @param binderId
	 * @param membersIds
	 * @throws AccessControlException
	 */
	public void setTeamMembers(Long binderId, Collection<Long> membersIds) throws AccessControlException;  
	/**
	 * Test access to a binder.  The method name to be called is used as the operation.   This
	 * allows the binderModule to check for multiple rights or change requirments in the future.
	 * @param binderId
	 * @param operation - the method name
	 * @return
	 */
	public boolean testAccess(Long binderId, String operation);
		
	/**
	 * Same as <code>testAccess</code> 
	 * @param binder
	 * @param operation
	 * @return
	 */
	public boolean testAccess(Binder binder, String operation);
	
}
