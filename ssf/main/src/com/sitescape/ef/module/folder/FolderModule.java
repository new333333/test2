package com.sitescape.ef.module.folder;

import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.shared.DomTreeBuilder;
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
    public Long addEntry(Long folderId, String definitionId, Map inputData, Map fileItems) throws AccessControlException;
    public Long addReply(Long folderId, Long parentId, String definitionId, Map inputData, Map fileItems) throws AccessControlException;
    public void modifyEntry(Long folderId, Long entryId, Map inputData, Map fileItems) throws AccessControlException;
    /**
     * Apply the filter and get back a list of matching entries. 
     * Access control is also applied implicitly.
     * 
     * @param entryFilter
     * @return a list of {@link com.sitescape.ef.domain.Entry}
     */
    public List applyEntryFilter(Definition entryFilter);
    
    /**
     * Modify the folder configuration from a Map of input data
      * 
     * @param folderId
     * @param inputData raw input data
     * @return
     * @throws AccessControlException
     */
    public void modifyFolderConfiguration(Long folderId, List definitionIds) throws AccessControlException;
    /**
     * Return Dom tree of folders starting at the topFolder of the specified folder
     * @param folderId
     * @return
     */
    public Document getDomFolderTree(Long folderId, DomTreeBuilder domTreeHelper);
 	public Map getFolderEntries(Long folderId) throws AccessControlException;
	public Map getFolderEntries(Long folderId, int maxNumEntries) throws AccessControlException;
	public Hits getRecentEntries(List folders, Map seenMaps);
	
	public Long addFolder(Long folderId, Folder folder) throws AccessControlException;
    	  
    public FolderEntry getEntry(Long parentFolderId, Long entryId) throws AccessControlException;
    public FolderEntry getEntry(Long parentFolderId, Long entryId, int type) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId) throws AccessControlException;
    public Map getEntryTree(Long parentFolderId, Long entryId, int type) throws AccessControlException;
    public void deleteEntry(Long parentFolderId, Long entryId) throws AccessControlException;

}