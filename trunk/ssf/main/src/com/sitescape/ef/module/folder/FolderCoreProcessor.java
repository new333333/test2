package com.sitescape.ef.module.folder;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.lucene.Hits;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.security.AccessControlException;

/**
 * <code>ForumCoreProcessor</code> is a model processor for forum, which
 * defines a set of core operations around forum. 
 * 
 * @author Jong Kim
 */
public interface FolderCoreProcessor {

    /**
     * This key is used to uniquely identify a type of processor (ie, a 
     * concrete class implementing this interface).
     */
    public static final String PROCESSOR_KEY = "processorKey_folderCoreProcessor";

    /**
     * Create an entry object from the input data and add it to the specified
     * forum.  
     * 
     * @param forum
     * @param def
     * @param inputData
     * @return
     * @throws AccessControlException
     */
    public Long addEntry(Folder forum, Definition def, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public Long addFileEntry(Folder forum, Definition def, Map inputData, Map fileItems) throws AccessControlException;
    public Long addReply(FolderEntry parent, Definition def, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public void modifyEntry(Folder parentFolder, Long entryId, Map inputData, Map fileItems) 
    	throws AccessControlException, WriteFilesException;
    public Document getDomFolderTree(Folder folder, DomTreeBuilder domTreeHelper);
	public Map getFolderEntries(Folder folder, int maxNumEntries) throws AccessControlException;
	public void indexFolder(Folder folder);
	
	public Long addFolder(Folder parentFolder, Folder folder) throws AccessControlException;
    	  
    public FolderEntry getEntry(Folder parentFolderId, Long entryId, int type) throws AccessControlException;
    public Map getEntryTree(Folder parentFolderId, Long entryId, int type) throws AccessControlException;
    public void deleteEntry(Folder parentFolder, Long entryId) throws AccessControlException;
}
