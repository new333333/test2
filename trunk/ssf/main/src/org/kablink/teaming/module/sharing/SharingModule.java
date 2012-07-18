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
package org.kablink.teaming.module.sharing;

import java.util.Collection;
import java.util.List;

import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItemMember;


/**
 * <code>SharingModule</code> provides "Share with Me" related operations
 * 
 * @author Peter Hurley
 */
public interface SharingModule {

    /**
     * Add a share item.
     * 
     * @param shareItem
     */
    public void addShareItem(ShareItem shareItem);
    
    /**
     * Modify an existing share item.
     * 
     * @param shareItem
     */
    public void modifyShareItem(ShareItem shareItem);
    
    /**
     * Delete an existing share item.
     * 
     * @param shareItemId
     */
    public void deleteShareItem(Long shareItemId);
    
 	/**
 	 * Get a share item by id.
 	 * If the object is not found, it throws <code>NoShareItemByTheIdException</code>.
 	 * 
 	 * @param shareItemId
 	 * @return
 	 * @throws NoShareItemByTheIdException
 	 */
 	public ShareItem getShareItem(Long shareItemId) throws NoShareItemByTheIdException;
 	
 	/**
 	 * Get a list of share items by their ids.
 	 * Unlike <code>getShareItem</code> method, this method does not return error when
 	 * not all of the objects are found by the specified ids. Instead, it will only return
 	 * those objects successfully found.
 	 * 
 	 * @param shareItemIds
 	 * @return
 	 */
 	public List<ShareItem> getShareItems(Collection<Long> shareItemIds); 

 	/**
 	 * Gets a list of <code>ShareItem</code> that were shared explicitly and directly with
 	 * the specified recipient (as opposed to indirectly through another group or team
 	 * membership).
 	 * 
 	 * @param recipientType
 	 * @param recipientId
 	 * @return
 	 */
 	public List<ShareItem> getShareItemsByRecipient(ShareItemMember.RecipientType recipientType, Long recipientId);
 
}