/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.dao;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.OrderBy;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.Tag;
import org.springframework.dao.DataAccessException;


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
	public FolderEntry loadFolderEntry(String sortKey, Long zoneId) throws DataAccessException,NoFolderEntryByTheIdException;
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
