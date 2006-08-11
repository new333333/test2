package com.sitescape.ef.module.folder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.ReservedByAnotherUserException;

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
       		Map fileItems) throws AccessControlException, WriteFilesException;
    public void checkAddFolderAllowed(Folder parentFolder) throws AccessControlException;

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
    public void checkAddEntryAllowed(Folder folder) throws AccessControlException;
    public Long addReply(Long folderId, Long parentId, String definitionId, 
    		InputDataAccessor inputData, Map fileItems) throws AccessControlException, WriteFilesException;
    public void checkAddReplyAllowed(FolderEntry entry) throws AccessControlException;
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection deleteAttachments) throws AccessControlException, WriteFilesException;
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException;
    public void checkModifyEntryAllowed(FolderEntry entry) throws AccessControlException;
    public void modifyWorkflowState(Long folderId, Long entryId, Long stateId, String toState) throws AccessControlException;
	public void checkTransitionOutStateAllowed(FolderEntry entry, Long stateId) throws AccessControlException;
	public void checkTransitionInStateAllowed(FolderEntry entry, Long stateId, String toState) throws AccessControlException;
	public Map getManualTransitions(FolderEntry entry, Long stateId);
	public Map getWorkflowQuestions(FolderEntry entry, Long stateId);
    /**
     * Apply the filter and get back a list of matching entries. 
     * Access control is also applied implicitly.
     * 
     * @param entryFilter
     * @return a list of {@link com.sitescape.ef.domain.Entry}
     */
    public List applyEntryFilter(Definition entryFilter);
    
   /**
     * Return Dom tree of folders starting at the topFolder of the specified folder
     * @param folderId
     * @return
     */
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper);
 	public Map getEntries(Long folderId) throws AccessControlException;
	public Map getEntries(Long folderId, Map options) throws AccessControlException;
	public Map getFullEntries(Long folderId) throws AccessControlException;
	public Map getFullEntries(Long folderId, Map options) throws AccessControlException;
    public Map getUnseenCounts(List folderIds);
    public List getCommunityTags(Long binderId, Long entryId);
    public List getPersonalTags(Long binderId, Long entryId);
    public void setUserRating(Long folderId, Long entryId, long value);
	public void setUserRating(Long folderId, long value);
	public void setUserVisit(Long folderId, Long entryId);
	public void setUserVisit(Long folderId);
	public void setUserVisit(FolderEntry entry);
	public void setUserVisit(Folder folder);
	public void indexFolderTree(Long folderId);
    public void indexEntries(Long folderId);
    public void setTag(Long binderId, Long entryId, String tag, boolean community);
    
    public Map getCommonEntryElements();

   	  
    public FolderEntry getEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    public void deleteEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public void checkDeleteEntryAllowed(FolderEntry entry) throws AccessControlException;
    public void moveEntry(Long folderId, Long entryId, Long destinationId);
    
    /**
     * Returns a list of ids of all folders that the user has read access to. 
     * 
     * @return
     */
    public List<String> getFolderIds(Integer type);
    
    public void reserveEntry(Long folderId, Long entryId)
    	throws AccessControlException, ReservedByAnotherUserException,
    	FilesLockedByOtherUsersException;
    
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
}