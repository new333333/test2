package com.sitescape.team.dao;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryMap;
import com.sitescape.team.domain.NoFolderByTheIdException;

/**
 * @author Jong Kim
 *
 */
public interface FolderDao {
	public FolderEntry loadFolderEntry(Long parentFolderId, Long entryId, Long zoneId) throws DataAccessException;
   /**
     * Return iterator of child entries
     * @param filter
     * @return SFQuery
     * @throws DataAccessException
     */
    public SFQuery queryEntries(FilterControls filter) throws DataAccessException; 
	public SFQuery queryChildEntries(Folder folder) throws DataAccessException;    
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
	
	public Folder loadFolder(Long folderId, Long zoneId) throws DataAccessException,NoFolderByTheIdException;
  
    public HistoryMap loadHistoryMap(Long userId, Long folderId);
    public void delete(Folder folder);
    public void deleteEntries(Folder folder);
    public void deleteEntries(Folder folder, List entries);
    public void deleteEntryWorkflows(Folder folder);
    public void deleteEntryWorkflows(Folder folder, List ids);
    public void moveEntries(Folder folder);
    public void moveEntries(Folder folder, List ids);
    public List loadEntryTags(EntityIdentifier ownerIdentifier, Collection ids);

}
