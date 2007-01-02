package com.sitescape.ef.module.folder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.ReservedByAnotherUserException;
import com.sitescape.ef.domain.Subscription;
import com.sitescape.ef.domain.User;

import com.sitescape.ef.module.file.WriteFilesException;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.InputDataAccessor;
import com.sitescape.ef.security.AccessControlException;
import com.sitescape.ef.domain.Definition;

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

    public Folder getFolder(Long folderId);
	public Collection getFolders(List folderIds);
    public Long addFolder(Long folderId, String definitionId, InputDataAccessor inputData,
       		Map fileItems, boolean library) throws AccessControlException, WriteFilesException;

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
    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException;
     public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, 
    		WriteFilesException, ReservedByAnotherUserException;
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException, ReservedByAnotherUserException;
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException;
	public void checkTransitionOutStateAllowed(FolderEntry entry, Long stateId) throws AccessControlException;
	public void checkTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState) throws AccessControlException;
	public Map getManualTransitions(FolderEntry entry, Long stateId);
	public Map getWorkflowQuestions(FolderEntry entry, Long stateId);
    public void setWorkflowResponse(Long folderId, Long entryId, Long stateId, InputDataAccessor inputData);
    
   /**
     * Return Dom tree of folders starting at the topFolder of the specified folder
     * @param folderId
     * @return
     */
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper);
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper, int levels);
 	public Map getEntries(Long folderId) throws AccessControlException;
	public Map getEntries(Long folderId, Map options) throws AccessControlException;
	public Map getFullEntries(Long folderId) throws AccessControlException;
	public Map getFullEntries(Long folderId, Map options) throws AccessControlException;
    public Map getUnseenCounts(List folderIds);
    public List getCommunityTags(Long binderId, Long entryId);
    public List getPersonalTags(Long binderId, Long entryId);
    public void setUserRating(Long folderId, Long entryId, long value);
	public void setUserRating(Long folderId, long value);
	public void setUserVisit(FolderEntry entry);
    public void setTag(Long binderId, Long entryId, String tag, boolean community);
    public void setTagDelete(Long binderId, Long entryId, String tagId);
    
    public void addSubscription(Long folderId, Long entryId, int style); 
    public void deleteSubscription(Long folderId, Long entryId);
    public Subscription getSubscription(Long folderId, Long entryId); 
        	  
    public FolderEntry getEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    public void deleteEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public void moveEntry(Long folderId, Long entryId, Long destinationId);
    
    /**
     * Returns a list of ids of all folders that the user has read access to. 
     * 
     * @return
     */
    public List<String> getFolderIds(Integer type);
    
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
     * Finds file folder entry by title. If no matching entry is found
     * it returns <code>null</code>. If matching entry is found but the
     * user has no access to it, it throws <code>AccessControlException</code>.
     *  
     * @param fileFolder
     * @param title
     * @return
     * @throws AccessControlException
     */
    public FolderEntry getFileFolderEntryByTitle(Folder fileFolder, String title)
    	throws AccessControlException;
    
    public Set<String> getSubfoldersTitles(Folder folder);
    
    public Set<Folder> getSubfolders(Folder folder);
    public void checkAccess(Folder folder, String operation) throws AccessControlException;
    public void checkAccess(FolderEntry entry, String operation) throws AccessControlException;

}