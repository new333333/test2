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

package org.kablink.teaming.module.binder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.SortedSet;
import java.util.Set;

import org.dom4j.Document;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.Tag;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.search.IndexErrors;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.util.search.Criteria;


/**
 * @author Janet McCann
 *
 */
public interface BinderModule {
	public enum BinderOperation {
		addFolder,
		addWorkspace,
		copyBinder,
		restoreBinder,
		preDeleteBinder,
		deleteBinder,
		indexBinder,
		indexTree,
		manageConfiguration,
		manageMail,
		manageTag,
		manageTeamMembers,
		modifyBinder,
		moveBinder,
		readEntries,
		viewBinderTitle,
		report,
		setProperty,	
		changeEntryTimestamps,
		manageSimpleName,
		export
	}
    /**
     * Add a new <code>Folder</code> or <code>Workspace</code>.  Use definition type to determine which
     * @param parentId
     * @param definitionId
     * @param inputData
     * @param fileItems May be <code>null</code>
     * @param options Additional processing options or null
     * @return
     * @throws AccessControlException
     * @throws WriteFilesException
     * @throws WriteEntryDataException
     */
    public Binder addBinder(Long parentId, String definitionId, InputDataAccessor inputData,
       		Map fileItems, Map options)
    	throws AccessControlException, WriteFilesException, WriteEntryDataException;
	/**
	 * Check access to a binder, throwing an exception if access is denied.
	 * @param binder
	 * @param operation
	 * @throws AccessControlException
	 */
	public void checkAccess(Binder binder, BinderOperation operation)
		throws AccessControlException;
	
    /**
     * Check binder access by a user
     * @param binderId
     * @param user
     * @return
     */
    public boolean checkAccess(Long binderId, User user);
	/**
	 * Copy a binder to another location
	 * @param sourceId
	 * @param destinationId
	 * @param cascade
     * @param options - processing options or null 
	 * @return
	 */
	public Binder copyBinder(Long sourceId, Long destinationId, boolean cascade, Map options)
		throws AccessControlException;
	
	/**
	 * Restores a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.  Mirrored source will be deleted
	 * @param binderId
	 * @param renameData
	 * @param reindex
	 * @throws AccessControlException
	 */
	public void restoreBinder(Long binderId, Object renameData)                  throws AccessControlException, WriteEntryDataException, WriteFilesException;
	public void restoreBinder(Long binderId, Object renameData, boolean reindex) throws AccessControlException, WriteEntryDataException, WriteFilesException;
	/**
	 * Restores a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.
	 * @param binderId
	 * @param renameData
	 * @param deleteMirroredSource indicates whether or not to delete the
	 * corresponding source resources (directories and files) if this binder
	 * or any of the child binders is mirrored.
	 * @param options - processing options or null
	 * @param reindex
	 * @throws AccessControlException
	 */
	public void restoreBinder(Long binderId, Object renameData, boolean deleteMirroredSource, Map options)                  throws AccessControlException, WriteEntryDataException, WriteFilesException;
	public void restoreBinder(Long binderId, Object renameData, boolean deleteMirroredSource, Map options, boolean reindex) throws AccessControlException, WriteEntryDataException, WriteFilesException;
	
	/**
	 * Predelete a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.  Mirrored source will be deleted
	 * @param binderId
	 * @param userId
	 * @param reindex
	 * @throws AccessControlException
	 */
	public void preDeleteBinder(Long binderId, Long userId)                  throws AccessControlException;
	public void preDeleteBinder(Long binderId, Long userId, boolean reindex) throws AccessControlException;
	/**
	 * Predelete a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.
	 * @param binderId
	 * @param userId
	 * @param deleteMirroredSource indicates whether or not to delete the
	 * corresponding source resources (directories and files) if this binder
	 * or any of the child binders is mirrored.
	 * @param options - processing options or null
	 * @param reindex
	 * @throws AccessControlException
	 */
	public void preDeleteBinder(Long binderId, Long userId, boolean deleteMirroredSource, Map options)                  throws AccessControlException;
	public void preDeleteBinder(Long binderId, Long userId, boolean deleteMirroredSource, Map options, boolean reindex) throws AccessControlException;
		
	/**
	 * Delete a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.  Mirrored source will be deleted
	 * @param binderId
	 * @throws AccessControlException
	 */
	public void deleteBinder(Long binderId) 
		throws AccessControlException;
	/**
	 * Delete a binder including any sub-binders and entries.
	 * Any errors deleting child-binders will be returned, but
	 * will continue deleting as much as possible.
	 * @param binderId
	 * @param deleteMirroredSource indicates whether or not to delete the
	 * corresponding source resources (directories and files) if this binder
	 * or any of the child binders is mirrored.
	 * @param options - processing options or null
	 * @param phase1Only - true -> perform only phase 1 of the delete
	 * @throws AccessControlException
	 */
	public void deleteBinder(Long binderId, boolean deleteMirroredSource, Map options) 
		throws AccessControlException;
	public void deleteBinder(Long binderId, boolean deleteMirroredSource, Map options, boolean phase1Only) 
		throws AccessControlException;
	
	/**
	 * Performs phase2 of deleting a binder.  Must be called after calling
	 * deleteBinder(...) one or more times and specifying to only do phase
	 * one.
	 */
	public void deleteBinderFinish();
		
	/**
	 * Check if the binder quota has been exceeded
	 * @param binder
	 */
	public boolean isBinderDiskHighWaterMarkExceeded(Binder binder);
	/**
	 * Check if the binder quota is enabled
	 * @param binder
	 */
	public boolean isBinderDiskQuotaEnabled();
	/**
	 * Check if the binder quota has been exceeded
	 * @param binder
	 */
	public boolean isBinderDiskQuotaExceeded(Binder binder);
	/**
	 * Check if adding a file would exceed the binder quota
	 * @param binder
	 * @param file size
	 */
	public boolean isBinderDiskQuotaOk(Binder binder, long fileSize);
	/**
	 * Get the minimum amount left for this binder (or any parent)
	 * @param binder
	 */
	public Long getMinBinderQuotaLeft(Binder binder);
	/**
	 * Get the binder with the minimum quota amount left
	 * @param binder
	 */
	public Binder getMinBinderQuotaLeftBinder(Binder binder);
	/**
	 * Get the parent binder with the lowest quota
	 * @param binder
	 */
	public Long getMinParentBinderQuota(Binder binder);
	/**
	 * Increment the disk space used in a binder
	 * @param binder
	 * @param file size
	 */
	public void incrementDiskSpaceUsed(Binder binder, long fileSize);
	/**
	 * Decrement the disk space used in a binder
	 * @param binder
	 * @param file size
	 */
	public void decrementDiskSpaceUsed(Binder binder, long fileSize);

	/**
	 * Delete a tag on a binder
	 * @param binderId
	 * @param tagId
	 * @throws AccessControlException
	 */
	public void deleteTag(Long binderId, String tagId) 
		throws AccessControlException;
   /**
	 * Execute a search query using a <code>Criteria</code>.
     * @param crit
     * @param offset
     * @param maxResults
     * @param preDeleted
     * @return
     */
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults);
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults, boolean preDeleted);
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults, boolean preDeleted, boolean ignoreAcls);
    /**
	 * Execute a search query using a <code>Criteria</code>. Limit results to those of a different user
     * @param crit
     * @param offset
     * @param maxResults
     * @param asUserId
     * @param preDeleted
     * @return
     */
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults, Long asUserId);
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults, Long asUserId, boolean preDeleted);
    public Map executeSearchQuery(Criteria crit, int offset, int maxResults, Long asUserId, boolean preDeleted, boolean ignoreAcls);
    /**
	 * Execute a search query using a <code>QueryBuilder</code>-ready <code>Document</code>.
     * @param query
     * @param offset
     * @param maxResults
     * @param preDeleted
     * @return
     */
    public Map executeSearchQuery(Document query, int offset, int maxResults);
    public Map executeSearchQuery(Document query, int offset, int maxResults, boolean preDeleted);
    public Map executeSearchQuery(Document query, int offset, int maxResults, boolean preDeleted, boolean ignoreAcls);
    /**
	 * Execute a search query using a <code>QueryBuilder</code>-ready <code>Document</code>.
     * @param query
     * @param offset
     * @param maxResults
     * @param asUserId
     * @param preDeleted
     * @return
     */
    public Map executeSearchQuery(Document query, int offset, int maxResults, Long asUserId);
    public Map executeSearchQuery(Document query, int offset, int maxResults, Long asUserId, boolean preDeleted);
    public Map executeSearchQuery(Document query, int offset, int maxResults, Long asUserId, boolean preDeleted, boolean ignoreAcls);
    /**
 	 * Execute a search query using a <code>QueryBuilder</code>-ready <code>Document</code>.
     * Optionally provide additional searchOptions.
     * @param searchQuery
     * @param searchOptions
     * @param preDeleted
     * @return
     */
    public Map executeSearchQuery(Document searchQuery, Map searchOptions);
    /**
     * Get a binder
     * @param binderId
     * @return
     * @throws NoBinderByTheIdException
     * @throws AccessControlException
     */
    public Binder getBinder(Long binderId)
		throws NoBinderByTheIdException, AccessControlException;
    
    public Binder getBinderWithoutAccessCheck(Long binderId) throws NoBinderByTheIdException;
    
    /**
     * Load binders.
     * @param binderIds
     * @return Binders sorted by title
     */
    public SortedSet<Binder> getBinders(Collection<Long> binderIds);
    /**
     * Search for child binders - 1 level
     * @param binder
     * @param searchOptions - search options
     * @return search results
     */
    public Map getBinders(Binder binder, Map searchOptions);
    public Map getBinders(Binder binder, List binderIds, Map searchOptions);
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
	/**
     * Traverse one path of the binder tree returing a DOM structure containing workspaces and
     * folders starting at topId and ending at bottomId.
	 * @param topId
	 * @param bottonId
	 * @param domTreeHelper
	 * @return
	 * @throws AccessControlException
	 */
 	public Document getDomBinderTree(Long topId, Long bottonId, DomTreeBuilder domTreeHelper) 
 		throws AccessControlException;
    /**
     * Orders list
     * @param wordroot
     * @param type
     * @return
     */
    public List<Map> getSearchTags(String wordroot, String type); 
    /**
     * Orders list
     * @param wordroot
     * @param type
     * @return
     */
    public List<Map> getSearchTagsWithFrequencies(String wordroot, String type); 
    /**
     * Get your subscription to this binder
     * @param binder
     * @return
     */
	public Subscription getSubscription(Binder binder);
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
	public IndexErrors indexBinder(Long binderId) throws AccessControlException;
	/**
	 * Index the binder and its attachments and optionally its entries.  Do not index sub-binders
	 * @param binderId
	 * @param includeEntries
	 */
    public IndexErrors indexBinder(Long binderId, boolean includeEntries) throws AccessControlException;
    /**
	 * Index the binder and its attachments and optionally its entries.  Do not index sub-binders
	 * @param binderId
	 * @param includeEntries
	 */
    public IndexErrors indexBinderIncremental(Long binderId, boolean includeEntries) throws AccessControlException;
    /**
     * Index a binder and its child binders, including all entries
     * @param binderId
     * @return Set of binderIds indexed
     */
    public Set<Long> indexTree(Long binderId) throws AccessControlException;
    /**
     * Same as {@link #indexTree(Long) indexTree} except handles a collection of binders.  Use this as a
     * performance optimzation for multiple binders- it handles the index cleanup better
     * @param binderId
     * @return Set of binderIds indexed
     */
     public Set<Long> indexTree(Collection<Long> binderId, StatusTicket statusTicket, String[] nodeNames) throws AccessControlException;
     /**
      * Same as {@link #indexTree(Long) indexTree} except handles a collection of binders.  Use this as a
      * performance optimzation for multiple binders- it handles the index cleanup better
      * Also passes along an error count to track the number of failures that occurred during indexing
      * @param binderId
      * @return Set of binderIds indexed
      */
	public Set<Long> indexTree(Collection<Long> binderId, StatusTicket statusTicket, String[] nodeNames, IndexErrors errors) throws AccessControlException;
   
    /**
     * Validate the binder quota values for a set of binder trees
     * @param binderIds
     * @param status ticket
     * @param error list
     * @return Set of binderIds indexed
     */
	public Set<Long> validateBinderQuotaTree(Binder binder, StatusTicket statusTicket, List<Long> errorIds) 
		throws AccessControlException;

	/**
     * Attach a single file to a binder.  
     * @param binderId
     * @param fileDataItemName name of the data item defined in the definition 
     *   object of the binder. Pass <code>null</code> to store the file through
     *   the default attachment element.
     * @param fileName Name of the file
     * @param content content of the file as an input stream
     * @throws AccessControlException
     * @throws WriteFilesException
	 * @throws WriteEntryDataException 
     */
	public void modifyBinder(Long binderId, String fileDataItemName, String fileName, InputStream content)
		throws AccessControlException, WriteFilesException, WriteEntryDataException;
    /**
     * Modify a binder.  Optionally include files to add and attachments to delete 
     * @param binderId
     * @param inputData
     * @param fileItems - may be null
     * @param deleteAttachments - may be null
     * @param options - processing options or null
     * @throws AccessControlException
     * @throws WriteFilesException
     * @throws WriteEntryDataException
     */
    public void modifyBinder(Long binderId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map options)
    	throws AccessControlException, WriteFilesException, WriteEntryDataException;
    /**
     * Modify who gets email notifications. 
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
     * @param options - processing options or null
	 */
	public void moveBinder(Long binderId, Long toId, Map options)
		throws AccessControlException;  
    /**
     * Modify the list of definitions and workflows assocated with a binder
     * @param binderId
     * @param definitionIds
     * @param workflowAssociations Map entryDefinitionId to workflowDefinitionId
     * @throws AccessControlException
     */
    public Binder setDefinitions(Long binderId, List<String> definitionIds, Map<String,String> workflowAssociations)
    	throws AccessControlException;
	/**
	 * Set the definition inheritance on a binder
	 * @param binderId
	 * @param inheritFromParent
	 * @return
	 * @throws AccessControlException
	 */
    public Binder setDefinitionsInherited(Long binderId, boolean inheritFromParent)
    	throws AccessControlException;

	/**
     * Set this inherited.
     * 
     * @param binderVersionsInherited
     * @throws AccessControlException
     */
    public void setBinderVersionsInherited(Long binderId, final Boolean binderVersionsInherited)
		throws AccessControlException;

	/**
     * Set the binderVersionsEnabled flag.
     * 
     * @param versionsEnabled
     * @throws AccessControlException
     */
    public void setBinderVersionsEnabled(Long binderId, final Boolean binderVersionsEnabled)
		throws AccessControlException;
	//Get the versionsEnabled setting from the first binder it is set in up the ancestor chain
    public Boolean getBinderVersionsEnabled(Binder binder);
	/**
     * Set versionsToKeep.
     * 
     * @param versionsToKeep
     * @throws AccessControlException
     */
    public void setBinderVersionsToKeep(Long binderId, final Integer binderVersionsToKeep)
		throws AccessControlException;
    //Get the versionsToKeep setting from the first binder it is set in up the ancestor chain
    public Integer getBinderVersionsToKeep(Binder binder);
	/**
     * Set maxVersionAge.
     * 
     * @param maxVersionAge
     * @throws AccessControlException
     */
    public void setBinderMaxVersionAge(Long binderId, final Integer maxVersionAge)
		throws AccessControlException;
	//Get the maxVersionAge setting from the first binder it is set in up the ancestor chain
    public Integer getBinderMaxVersionAge(Binder binder);
	/**
     * Set maxFileSize.
     * 
     * @param maxFileSize
     * @throws AccessControlException
     */
    public void setBinderMaxFileSize(Long binderId, final Integer maxFileSize)
		throws AccessControlException;
	//Get the maxFileSize setting from the first binder it is set in up the ancestor chain
    public Integer getBinderMaxFileSize(Binder binder);

	/**
     * Set the fileEncryptionEnabled flag.
     * 
     * @param fileEncryptionEnabled
     * @throws AccessControlException
     */
    public void setBinderFileEncryptionEnabled(Long binderId, final Boolean fileEncryptionEnabled)
		throws AccessControlException;

    /**
     * Set the postingEnabled flag.
     * 
     * @param postingEnabled
     * @throws AccessControlException
     */
    public void setPostingEnabled(Long folderId, Boolean postingEnabled) throws AccessControlException;
    
    /**
     * Add emailAddress and (options password) for posting to a binder
     * @param binderId
     * @param emailAddress
     * @param password - null if using aliases
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
	 * Subscribe to a binder.  Use to request notification of changes.
	 * @param binderId
	 * @param style = null or empty to delete
	 */
	public void setSubscription(Long binderId, Map<Integer,String[]> styles) 
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
	 * Returns <code>SimpleName</code> object matching the name.
	 * Returns <code>null</code> if no match is found.
	 * 
	 * @param name
	 * @return
	 */
	public SimpleName getSimpleName(String name);
	
	/**
	 * Returns <code>SimpleName</code> object matching the email address.
	 * Returns <code>null</code> if no match is found.
	 * 
	 * @param emailAddress
	 * @return
	 */
	public SimpleName getSimpleNameByEmailAddress(String emailAddress);
	
	/**
	 * Returns a list of <code>SimpleName</code> objects of given type for the 
	 * binder sorted by the name. Returns an empty list if no match is found.
	 *  
	 * @param binderId
	 * @return
	 */
	public List<SimpleName> getSimpleNames(Long binderId);
	
	/**
	 * Add a simple name for the binder.
	 * 
	 * @param simpleName
	 */
	public void addSimpleName(String name, Long binderId, String binderType);
	
	/**
	 * Delete the simple name.
	 * 
	 * @param simpleName
	 */
	public void deleteSimpleName(String name);

	public void export(Long binderId, Long entityId, OutputStream out, Map options, 
			Collection<Long> binderIds, Boolean noSubBinders, StatusTicket statusTicket, 
			Map reportMap) throws Exception;
	
	public String filename8BitSingleByteOnly(FileAttachment attachment, boolean _8BitSingleByteOnly);
	public String filename8BitSingleByteOnly(String fileName, String fallBackName, boolean _8BitSingleByteOnly);
	
	public Long getZoneBinderId(Long binderId, String zoneUUID, String entityType);
}
