/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
import com.sitescape.team.domain.NoFolderEntryByTheIdException;
import com.sitescape.team.domain.Tag;

/**
 * @author Jong Kim
 *
 */
public interface FolderDao {
	/**
	 * Load folder entry
	 * @param entryId
	 * @param zoneId
	 * @return
	 * @throws DataAccessException
	 * @throws NoFolderEntryByTheIdException
	 */
	public FolderEntry loadFolderEntry(Long entryId, Long zoneId) throws DataAccessException,NoFolderEntryByTheIdException;
	/**
	 * Same as {@link #loadFolderEntry(Long,Long) loadFolderEntry} except validate entry belongs to the specified folder.
	 * @param parentFolderId
	 * @param entryId
	 * @param zoneId
	 * @return
	 * @throws DataAccessException
	 * @throws NoFolderEntryByTheIdException
	 */
	public FolderEntry loadFolderEntry(Long parentFolderId, Long entryId, Long zoneId) throws DataAccessException,NoFolderEntryByTheIdException;
   /**
     * Return iterator of child entries
     * @param parentFolder
     * @param filter
     * @return SFQuery
     * @throws DataAccessException
     */
    public SFQuery queryEntries(Folder folder, FilterControls filter) throws DataAccessException; 
    public List<FolderEntry> loadEntries(final Folder folder, FilterControls filter) throws DataAccessException ;
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
