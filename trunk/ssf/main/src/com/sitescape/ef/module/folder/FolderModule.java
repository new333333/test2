package com.sitescape.ef.module.folder;

import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.lucene.Hits;
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
	public static int NEXT_ENTRY=1;
	public static int PREVIOUS_ENTRY=2;
    public static int CURRENT_ENTRY=3;
 
    public Folder getFolder(Long folderId);
	public List getFolders(List folderIds);
	public List getSortedFolderList(List folderIds);

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
    		Map fileItems) throws AccessControlException, WriteFilesException;
    public void modifyEntry(Long folderId, Long entryId, InputDataAccessor inputData) 
    	throws AccessControlException, WriteFilesException;
    public void checkModifyEntryAllowed(FolderEntry entry) throws AccessControlException;
    public void modifyWorkflowState(Long folderId, Long entryId, Long tokenId, String toState) throws AccessControlException;
    
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
 	public Map getFolderEntries(Long folderId) throws AccessControlException;
	public Map getFolderEntries(Long folderId, int maxNumEntries) throws AccessControlException;
	public Map getFolderEntries(Long folderId, int maxNumEntries, Document searchFilter) throws AccessControlException;
    public Map getUnseenCounts(List folderIds);
    public void indexFolderTree(Long folderId);
    public void indexFolder(Long folderId);
    public Map getCommonEntryElements(Long folderId);

	public Long addFolder(Long parentFolderId, Map input) throws AccessControlException;
    public void checkAddFolderAllowed(Folder parentFolder) throws AccessControlException;
   	  
    public FolderEntry getEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public FolderEntry getEntry(Long parentFolderId, Long entryId, int type) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId, int type) throws AccessControlException;
    public void deleteEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public void checkDeleteEntryAllowed(FolderEntry entry) throws AccessControlException;

}