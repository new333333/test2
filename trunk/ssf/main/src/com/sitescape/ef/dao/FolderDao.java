package com.sitescape.ef.dao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.UserProperties;

import org.springframework.dao.DataAccessException;

import com.sitescape.ef.domain.NoFolderByTheIdException;

import java.util.Iterator;
import java.util.List;

/**
 * @author Jong Kim
 *
 */
public interface FolderDao {
	public Folder loadFolder(Long folderId, String zoneId) throws DataAccessException,NoFolderByTheIdException;
	public FolderEntry loadFolderEntry(Long parentFolderId, Long entryId, String zoneId) throws DataAccessException;
    /**
     * Return iterator of child entries
     * @param filter
     * @return Iterator
     * @throws DataAccessException
     */
    public Iterator queryEntries(FilterControls filter) throws DataAccessException; 
	public Iterator queryChildEntries(Folder folder) throws DataAccessException;    
    /**
     * 
     * @param parentFolder
     * @param entry
     * @return
     * @throws DataAccessException
     */
    public List loadEntryDescendants(FolderEntry entry) throws DataAccessException;
    public List loadEntryAncestors(FolderEntry entry) throws DataAccessException;
    public List loadEntryTree(FolderEntry entry) throws DataAccessException;
           
    
    /**
     * Load a folder and subFolders 
     * @param folderId
     * @return Folder
     */
    public Folder loadFolders(Long folderId, String zoneId) throws DataAccessException; 
    public List loadFolderAncestors(Folder folder) throws DataAccessException;
    public int allocateEntryNumbers(Folder folder, int count);
    public int allocateFolderNumbers(Folder folder, int count);

    public UserProperties loadUserFolderProperties(Long userId, Long folderId);
    public SeenMap loadSeenMap(Long userId, Long folderId);
    public HistoryMap loadHistoryMap(Long userId, Long folderId);

}
