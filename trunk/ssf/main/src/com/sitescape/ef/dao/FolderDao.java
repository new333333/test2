package com.sitescape.ef.dao;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.dao.util.OrderBy;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.TitleException;

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
  
    public HistoryMap loadHistoryMap(Long userId, Long folderId);
    public void delete(Folder folder);
    public void deleteEntries(Folder folder);
    public void deleteEntries(List entries);
    public void deleteEntryWorkflows(Folder folder);
    public void deleteEntryWorkflows(Folder folder, List ids);
    public void moveEntries(Folder folder);
    public void moveEntries(Folder folder, List ids);
    public void validateTitle(Folder folder, String title) throws TitleException;
    	 
}
