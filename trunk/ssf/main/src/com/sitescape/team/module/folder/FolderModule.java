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
package com.sitescape.team.module.folder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.ReservedByAnotherUserException;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.Tag;
import com.sitescape.team.fi.FIException;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.util.StatusTicket;
import com.sitescape.team.web.tree.DomTreeBuilder;

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
	   addFolder,
	   addEntryWorkflow,
	   addReply,
	   deleteEntry,
	   deleteEntryWorkflow,
	   manageTag,
	   modifyEntry,
	   modifyWorkflowState,
	   moveEntry,
	   report,
	   reserveEntry,
	   overrideReserveEntry,
	   setWorkflowResponse, 
	   synchronize
   }

	   /**
     * Create an entry object from the input data and add it to the specified
     * folder.  
     * 
     * @param folder
     * @param inputData raw input data
     * @return
     * @throws AccessControlException
     */
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems) throws AccessControlException, WriteFilesException;
    public Long addEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Boolean filesFromApplet) throws AccessControlException, WriteFilesException;
    public void addEntryWorkflow(Long folderId, Long entryId, String definitionId) throws AccessControlException;
    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException;

    public Long addFolder(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems) throws AccessControlException, WriteFilesException;
    public void addSubscription(Long folderId, Long entryId, int style); 

    /**
     * Complete deletion of folders previously marked for delete
     * Used by a scheduled job to finish the delete in the background
     *
     */
    public void cleanupFolders();
    /**
     * Delete a FolderEntry and all of its replies
     * @param parentFolderId
     * @param entryId
     * @throws AccessControlException
     */
    public void deleteEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public void deleteEntry(Long parentFolderId, Long entryId, boolean deleteMirroredSource) throws AccessControlException;
    public void deleteEntryWorkflow(Long parentFolderId, Long entryId, String definitionId) throws AccessControlException;
    public void deleteSubscription(Long folderId, Long entryId);
    public void deleteTag(Long binderId, Long entryId, String tagId) throws AccessControlException;
    
	  /**
     * Return Dom tree of folders starting at the topFolder of the specified folder
     * @param folderId
     * @return
     */
	public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper);
	public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper, int levels);
	public Map getEntries(Long folderId) throws AccessControlException;
	/**
	 * Return entries
	 * @param folderId
	 * @param options
	 * @return search results
	 * @throws AccessControlException
	 */
	public Map getEntries(Long folderId, Map options) throws AccessControlException;
    public FolderEntry getEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    public Folder getFolder(Long folderId) throws AccessControlException;
	public Set<Folder> getFolders(Collection<Long> folderIds);
    public Set<FolderEntry> getFolderEntryByNormalizedTitle(Long folderId, String title) throws AccessControlException;
	public Map getFullEntries(Long folderId) throws AccessControlException;
	public Map getFullEntries(Long folderId, Map options) throws AccessControlException;
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
    
    
	public Map getManualTransitions(FolderEntry entry, Long stateId);
    public Set<Folder> getSubfolders(Folder folder);
    public Set<String> getSubfoldersTitles(Folder folder);
	public Subscription getSubscription(FolderEntry entry); 
	/**
	 * Return community tags and the current users personal tags on the entry
	 * @param entry
	 * @return 
	 */
	public List<Tag> getTags(FolderEntry entry);
 	  
	public Map getUnseenCounts(Collection<Long> folderIds);
	public Map getWorkflowQuestions(FolderEntry entry, Long stateId);
	public void indexEntry(FolderEntry entry, boolean includeReplies);
    /**
     * 
     * @param folderId
     * @param entryId
     * @param inputData
     * @param fileItems
     * @param deleteAttachments A collection of either <code>java.lang.String</code>
     * representing database id of each attachment or 
     * {@link com.sitescape.team.domain.Attachment Attachment}.
     * @throws AccessControlException
     * @throws WriteFilesException
     * @throws ReservedByAnotherUserException
     */
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo) 
    	throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Boolean filesFromApplet) 
    	throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException;

    public void moveEntry(Long folderId, Long entryId, Long destinationId) throws AccessControlException;

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
    
    
	public void setTag(Long binderId, Long entryId, String tag, boolean community) throws AccessControlException;
    public void setUserRating(Long folderId, Long entryId, long value) throws AccessControlException;
	public void setUserRating(Long folderId, long value) throws AccessControlException;
	public void setUserVisit(FolderEntry entry);
    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData) throws AccessControlException;
    
    public boolean testAccess(Folder folder, FolderOperation operation);
    public void checkAccess(Folder folder, FolderOperation operation) throws AccessControlException;
    public boolean testAccess(FolderEntry entry, FolderOperation operation);
    public void checkAccess(FolderEntry entry, FolderOperation operation) throws AccessControlException;
    public boolean testTransitionOutStateAllowed(FolderEntry entry, Long stateId);
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
	 * @throws FIException
	 */
	public boolean synchronize(Long folderId, StatusTicket statusTicket) throws AccessControlException, FIException, UncheckedIOException;
}