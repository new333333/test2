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
package com.sitescape.team.module.folder;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.StatusTicket;

/**
 * <code>FolderModule</code> provides folder-related operations that the caller
 * can invoke without concern for the specific type of the folder. 
 * Consequently, only those generic operations that are meaningful across 
 * many different folder types are defined in this module interface.
 * Additional type-specific operations must be defined in the respective module.   
 * 
 * @author Jong Kim
 */
public interface FolderModule {
	//these are input to the testAccess methods
	//usefull for UI's.  Use to determine which operations are allowed
	//if not listed, you have the entry and the operation only needs read access
   public enum FolderOperation {
	   addEntry,
	   addEntryWorkflow,
	   addReply,
	   copyEntry,
	   deleteEntry,
	   deleteEntryWorkflow,
	   manageTag,
	   modifyEntry,
	   moveEntry,
	   report,
	   reserveEntry,
	   overrideReserveEntry,
	   synchronize,
	   changeEntryTimestamps
   }
 
   /**
    * Create an <code>FolderEntry</code> from the input data and add it to the specified
    * <code>Folder</code>.  
    * 
    * @param folderId
    * @param definitionId
    * @param inputData
    * @param fileItems - may be null
    * @param options - additional processing options or null
    * @return
    * @throws AccessControlException
    * @throws WriteFilesException
    */
     public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException;
     /**
      * Start a workflow on a <code>FolderEntry</code>
      * @param folderId
      * @param entryId
      * @param definitionId
      * @throws AccessControlException
      */
     public void addEntryWorkflow(Long folderId, Long entryId, String definitionId) 
     	throws AccessControlException;
     /**
     * Start a workflow on a <code>FolderEntry</code> and put it in a state.  Used when entries are being
     * added to the system from another source.
     * @param folderId
     * @param entryId
     * @param definitionId
     * @param startState
     * @throws AccessControlException
     */
    public void addEntryWorkflow(Long folderId, Long entryId, String definitionId, String startState) 
    	throws AccessControlException;
    /**
     * Add a reply to the specified <code>FolderEntry</code>
     * @param folderId
     * @param parentId
     * @param definitionId
     * @param inputData
     * @param fileItems- may be null
     * @param options - additional processing options or null
     * @return
     * @throws AccessControlException
     * @throws WriteFilesException
     */
    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems, Map options) 
    	throws AccessControlException, WriteFilesException;
    /**
     * Subscribe to an entry.  Multiple styles can be specified and multiple address/style are permitted
     * @param folderId
     * @param entryId
     * @param styles
     */
    public void addSubscription(Long folderId, Long entryId, Map<Integer,String[]> styles); 
    /**
     * Add a vote to a survey entry.
     * @param folderId
     * @param entryId
     * @param inputData
     * @param options additional processing options or null
     * @throws AccessControlException
     */
    public void addVote(Long folderId, Long entryId, InputDataAccessor inputData, Map options) 
		throws AccessControlException;

    public void changeEntryTimstamps(Long folderId, Long entryId,
			 HistoryStamp creation, HistoryStamp modification) throws AccessControlException, WriteFilesException;

    /**
     * Check access to a <code>Folder</code> throwing an exception if access is denied
     * @param folder
     * @param operation
     * @throws AccessControlException
     */
    public void checkAccess(Folder folder, FolderOperation operation) 
    	throws AccessControlException;
    /**
     * Check access to a <code>FolderEntry</code> throwing an exception if access is denied
     * @param entry
     * @param operation
     * @throws AccessControlException
     */
    public void checkAccess(FolderEntry entry, FolderOperation operation) 
    	throws AccessControlException;
    /**
     * Complete deletion of folders previously marked for delete
     * Used by a scheduled job to finish the delete in the background
     *
     */
    public void cleanupFolders();
    /**
     * Copy a <code>FolderEntry</code> to another <code>Folder</code>.  Workflows and subsciptions will not be copied.
     * @param folderId
     * @param entryId
     * @param destinationId
     * @param options additional processing options or null
     * @throws AccessControlException
     */
    public void copyEntry(Long folderId, Long entryId, Long destinationId, Map options)
    	throws AccessControlException;
   /**
     * Delete a <code>FolderEntry</code> and all of its replies.  Deleted mirrored resources also.
     * @param parentFolderId
     * @param entryId
     * @throws AccessControlException
     */
    public void deleteEntry(Long parentFolderId, Long entryId) 
    throws AccessControlException;
    /**
     * Delete a <code>FolderEntry</code> and all of its replies.
     * @param parentFolderId
     * @param entryId
     * @param deleteMirroredSource
     * @param options - processing options or null
     * @throws AccessControlException
     */
    public void deleteEntry(Long parentFolderId, Long entryId, boolean deleteMirroredSource, Map options) 
    	throws AccessControlException;
    /**
     * Stop a running workflow and delete it from the states of the <code>FolderEntry</code>.
     * @param parentFolderId
     * @param entryId
     * @param definitionId
     * @throws AccessControlException
     */
    public void deleteEntryWorkflow(Long parentFolderId, Long entryId, String definitionId)
    	throws AccessControlException;
    /**
     * Unsubscribe to a <code>FolderEntry</code>
     * @param folderId
     * @param entryId
     */
    public void deleteSubscription(Long folderId, Long entryId);
    /**
     * Delete a <code>Tag</code> associated with a <code>FolderEntry</code>
     * @param binderId
     * @param entryId
     * @param tagId
     * @throws AccessControlException
     */
    public void deleteTag(Long binderId, Long entryId, String tagId) 
    	throws AccessControlException;
	/**
	 * Search for entries in the <code>Folder</code>.  Optionally specify search options
	 * @param folderId
	 * @param searchOptions - may be null
	 * @return Map containing 5 Map.entry
	 *        ObjectKeys.BINDER, Folder
	 *        ObjectKeys.SEARCH_ENTRIES, childEntries: List
	 *        ObjectKeys.SEARCH_COUNT_TOTAL, Integer
	 *        ObjectKeys.TOTAL_SEARCH_COUNT, Integer
	 *        ObjectKeys.TOTAL_SEARCH_RECORDS_RETURNED, Integer
	 * @throws AccessControlException
	 */
	public Map getEntries(Long folderId, Map searchOptions) 
		throws AccessControlException;
	/**
	 * Get the <code>FolderEntry</code> with the specified id and parent
	 * @param parentFolderId
	 * @param entryId
	 * @return
	 * @throws AccessControlException
	 */
    public FolderEntry getEntry(Long parentFolderId, Long entryId) 
    	throws AccessControlException;
    /**
     * Get the  <code>FolderEntries</code> 
     * @param ids
     * @return
     */
    public SortedSet<FolderEntry>getEntries(Collection<Long>ids);
    /**
     * 
     * @param parentFolderId
     * @param entryId
     * @return Map containing 3 items:   
     * 			<code>ObjectKeys.FOLDER_ENTRY</code>, entry
     * 			<code>ObjectKeys.FOLDER_ENTRY_ANCESTORS</code>, List<FolderEntry> ordered by sortKey
     * 			<code>ObjectKeys.FOLDER_ENTRY_DESCENDANTS</code>, List<FolderEntry> ordered by sortKey
     * @throws AccessControlException
     */
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    /**
     * Get the <code>Folder</code> with the specified id
     * @param folderId
     * @return
     * @throws AccessControlException
     */
    public Folder getFolder(Long folderId) 
    	throws AccessControlException;
    /**
     * Get set of folders sorted by title
     * @param folderIds
     * @return
     */
	public SortedSet<Folder> getFolders(Collection<Long> folderIds);
	/**
	 * Return a set of folderEntrys, doing the lookup by normalized title.
	 * This is for wiki links where normalize title is used
	 * @param folderId
	 * @param title
	 * @return
	 * @throws AccessControlException
	 */
    public Set<FolderEntry> getFolderEntryByNormalizedTitle(Long folderId, String title)
    	throws AccessControlException;
 	/**
	 * Search for entries in a folder and additionally return the folderEntry and its tags.
	 * @param folderId
	 * @param searchOptions - may be null
	 * @return Map containing {@link #getEntries getEntries} results plus
	 * 			<code>ObjectKeys.FULL_ENTRIES</code>, List<FolderEntry>
	 * 			<code>ObjectKeys.COMMUNITY_ENTITY_TAGS</code>, Map<Long, Collection<Tag>>
	 * 			<code>ObjectKeys.PERSONAL_ENTITY_TAGS</code>, Map<Long, Collection<Tag>>
	 * @throws AccessControlException
	 */
	public Map getFullEntries(Long folderId,
				Map searchOptions) throws AccessControlException;
	/**
     * Finds library folder entry by the file name. If no matching entry is 
     * found it returns <code>null</code>. If matching entry is found but the
     * user has no access to it, it throws <code>AccessControlException</code>.
     *  
     * @param libraryFolder
     * @param fileName
     * @return
     * @throws AccessControlException
     */
    public FolderEntry getLibraryFolderEntryByFileName(Folder libraryFolder, String fileName)
    	throws AccessControlException;
    
    /**
     * Return the manual transitions currently available for an entry in the specified state.
     * @param entry
     * @param stateId
     * @return Map where each key is a stateName and each value is the untranslated state caption
     */
	public Map<String, String> getManualTransitions(FolderEntry entry, Long stateId);
	/**
	 * Return sub-folders of this folder, sorted by title (1-level)
	 * @param folder
	 * @return
	 */
    public SortedSet<Folder> getSubfolders(Folder folder);
    /**
     * Return the sorted titles of this folders sub-folders (1-level)
     * @param folder
     * @return
     */
    public SortedSet<String> getSubfoldersTitles(Folder folder);
    /**
     * Return the current users subscription the the folderEntry
     * @param entry
     * @return
     */
	public Subscription getSubscription(FolderEntry entry); 
	/**
	 * Return community tags and the current users personal tags on the entry
	 * @param entry
	 * @return 
	 */
	public Collection<Tag> getTags(FolderEntry entry);
	/**
	 * Get count of entries unseen for each folder.  Unseen counts are only kept for a short time. Refer to ObjectKeys.SEEN_MAP_TIMEOUT
	 * @param folderIds
	 * @return
	 */
	public Map<Folder, Long> getUnseenCounts(Collection<Long> folderIds);
	/**
	 * Return the workflow questions currently available for an entry in the specified state.
	 * @param entry
	 * @param stateId
	 * @return Map where each key is a questionName and each value is a map.  The value map contains
	 * 2 entries:  (Key:ObjectKeys.WORKFLOW_QUESTION_TEXT, Value: untranslated text) and (Key:ObjectKeys.WORKFLOW_QUESTION_RESPONSES,Value: maap of responses).  The response map contains key:responseName and value:untranslated responseText
	 */
	public Map<String, Map> getWorkflowQuestions(FolderEntry entry, Long stateId);
	/**
	 * Re-index an existing entry.  Should be handled internally
	 * @param entry
	 * @param includeReplies
	 */
	public void indexEntry(FolderEntry entry, boolean includeReplies);
	/**
	 * Return the parent Folder of the entry.  Useful if an entry has moved.
	 * @param entryId
	 * @return
	 */
	public Folder locateEntry(Long entryId);
    /**
     * Modify an existing <code>FolderEntry</code> with inputData.
     * @param folderId ID of the folder
     * @param entryId ID of the entry
     * @param inputData attributes to be modified
     * @param fileItems - may be null
     * @param deleteAttachments - A collection of <code>String</code>
     * representing database id of each attachment or null
     * @param fileRenamesTo  may be null
     * @param options  processing options or null
     * @throws AccessControlException
     * @throws WriteFilesException
     * @throws ReservedByAnotherUserException
     */
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
    	throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    
    /**
     * Add a file to the entry.
     * <p>
     * The same can be achieved by calling {{@link #modifyEntry(Long, Long, InputDataAccessor, Map, Collection, Map)}.
     * But this is more convenient to call when you have a single file to attach to an existing entry.
     * 
     * @param folderId ID of the folder
     * @param entryId ID of the entry
     * @param fileDataItemName name of the data item defined in the definition 
     * object of the entry. Pass <code>null</code> to store the file through
     * the default attachment element.
     * @param fileName Name of the file
     * @param content content of the file as an input stream
     * @throws AccessControlException
     * @throws WriteFilesException
     * @throws ReservedByAnotherUserException
     */
    public void modifyEntry(Long folderId, Long entryId, String fileDataItemName, String fileName, InputStream content)
		throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    /**
     * Move an entry workflow from the state identified as stateId to a new state.
     * @param folderId
     * @param entryId
     * @param stateId
     * @param toState
     * @throws AccessControlException
     */
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) 
    	throws AccessControlException;
    /**
     * Move a top level entry to another folder.  Replies are moved with the entry.
     * @param folderId
     * @param entryId
     * @param destinationId
     * @param options - processing options or null
     * @throws AccessControlException
     */
    public void moveEntry(Long folderId, Long entryId, Long destinationId, Map options) 
    	throws AccessControlException;

    /**
     * Reserve the entry.
     * 
     * @param folderId
     * @param entryId
     * @throws AccessControlException
     * @throws ReservedByAnotherUserException
     * @throws FilesLockedByOtherUsersException
     */
    public void reserveEntry(Long folderId, Long entryId)
    	throws AccessControlException, ReservedByAnotherUserException,
    	FilesLockedByOtherUsersException;
    
    /**
     * Add a tag to a folderEntry
     * @param binderId
     * @param entryId
     * @param tag
     * @param community
     * @throws AccessControlException
     */
	public void setTag(Long binderId, Long entryId, String tag, boolean community) 
		throws AccessControlException;
	/**
	 * Rate a folderEntry
	 * @param folderId
	 * @param entryId
	 * @param value
	 * @throws AccessControlException
	 */
    public void setUserRating(Long folderId, Long entryId, long value) 
    	throws AccessControlException;
    /**
     * Rate a folder
     * @param folderId
     * @param value
     * @throws AccessControlException
     */
	public void setUserRating(Long folderId, long value) 
		throws AccessControlException;
	/**
	 * Mark the current user has visited the entry.  A running count is kept
	 * @param entry
	 */
	public void setUserVisit(FolderEntry entry);
	/**
	 * Record responses to workflow questions
	 * @param folderId
	 * @param entryId
	 * @param stateId
	 * @param inputData
	 * @throws AccessControlException
	 */
    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) 
    	throws AccessControlException;
    /**
     * 	Test access to a binder. 
     * @param folder
     * @param operation
     * @return
     */
    public boolean testAccess(Folder folder, FolderOperation operation);
    /**
     * Test access to a folderEntry.  Access may be effected by workflows
     * @param entry
     * @param operation
     * @return
     */
    public boolean testAccess(FolderEntry entry, FolderOperation operation);
    /**
     * Test if current user is allowed to transition out of  a workflow state
     * @param entry
     * @param stateId
     * @return
     */
    public boolean testTransitionOutStateAllowed(FolderEntry entry, Long stateId);
    /**
     * Test if currnet user is allowed to transition into the specified stae
     * @param entry
     * @param stateId
     * @param toState
     * @return
     */
    public boolean testTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState);
   
    
    /**
     * Cancel reservation on the entry.
     * 
     * @param folderId
     * @param entryId
     * @throws AccessControlException
     * @throws ReservedByAnotherUserException
     */
    public void unreserveEntry(Long folderId, Long entryId)
		throws AccessControlException, ReservedByAnotherUserException;
    
	/**
	 * Synchronize the mirrored folder. 
	 * This initiates the work and returns immediately without waiting for
	 * the work to finish, hence working asynchronously. 
	 * 
	 * @param folderId this should be a mirrored folder; note that mirrored 
	 * folder is always a library folder (but not the other way around)
	 * @param statusTicket
	 * @return returns <code>false</code> if the folder represented by the
	 * <code>folderId</code> has been deleted as result of the synchronization.
	 * Otherwise returns <code>true</code>
	 * @throws AccessControlException
	 * @throws FIException
	 * @throws UncheckedIOException
	 */
	public boolean synchronize(Long folderId, StatusTicket statusTicket)
		throws AccessControlException, FIException, UncheckedIOException;
}