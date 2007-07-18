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
import com.sitescape.team.domain.NoFolderByTheIdException;
import com.sitescape.team.domain.Tag;

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
    public List<FolderEntry> loadEntryDescendants(FolderEntry entry) throws DataAccessException;
    public List<FolderEntry> loadEntryAncestors(FolderEntry entry) throws DataAccessException;
    public List<FolderEntry> loadEntryTree(FolderEntry entry) throws DataAccessException;
    
    /**
     * Load changed entries in a folder and its sub-folders
     * @param folder
     * @param since
     * @param before
     * @return
     */
    
	public List<FolderEntry> loadFolderTreeUpdates(Folder folder, Date since, Date before);
	public List<FolderEntry> loadFolderTreeUpdates(Folder folder, Date since, Date before, OrderBy order, int maxResults);
	
	public Folder loadFolder(Long folderId, Long zoneId) throws DataAccessException,NoFolderByTheIdException;
  
    public void delete(Folder folder);
    public void deleteEntries(Folder folder, Collection<FolderEntry> entries);
    public void markEntriesDeleted(Folder folder, Collection<FolderEntry> entries);
    public void move(Folder folder);
    public void moveEntries(Folder folder, List<Long> ids);
    public List<Tag> loadEntryTags(EntityIdentifier ownerIdentifier, Collection<Long> ids);

}
