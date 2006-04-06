package com.sitescape.ef.dao;
import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.UserProperties;

import org.springframework.dao.DataAccessException;

import com.sitescape.ef.domain.NoFolderByTheIdException;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Jong Kim
 *
 */
public interface FolderDao {
	public FolderEntry loadFolderEntry(Long parentFolderId, Long entryId, String zoneName) throws DataAccessException;
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
     * Load changed entries in a folder
     * @param folder
     * @param since
     * @param before
     * @return
     */
	public List loadFolderUpdates(Folder folder, Date since, Date before);
	public List loadFolderUpdates(Folder folder, Date since, Date before, OrderBy order);
    
	public List loadFolderTreeUpdates(Folder folder, Date since, Date before);
	public List loadFolderTreeUpdates(Folder folder, Date since, Date before, OrderBy order);
	
	public Folder loadFolder(Long folderId, String zoneName) throws DataAccessException,NoFolderByTheIdException;
    /**
     * Load a folder and subFolders 
     * @param folder
     * @return List
     */
    public List loadFolderTree(Folder folder) throws DataAccessException; 
    public List loadFolderAncestors(Folder folder) throws DataAccessException;
 
    public UserProperties loadUserFolderProperties(Long userId, Long folderId);
    public HistoryMap loadHistoryMap(Long userId, Long folderId);
    public void delete(Folder folder);
    public void deleteEntries(Folder folder);
    public void deleteEntries(List entries);
    public void deleteEntryWorkflows(Folder folder);
    public void deleteEntryWorkflows(List entries);
    
}
