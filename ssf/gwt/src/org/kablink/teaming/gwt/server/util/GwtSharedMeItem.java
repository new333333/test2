/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.gwt.client.util.ShareRights;

/**
 * Used to track items for the 'Shared by/with Me' collection points.
 *
 * @author drfoster@novell.com
 */
public class GwtSharedMeItem {
	private DefinableEntity			m_item;				//
	private List<GwtPerShareInfo>	m_perShareInfos;	//
	private Long					m_id;				//
	private Long					m_sharerId;			//
	
	/**
	 * Constructor method.
	 * 
	 * @param id
	 */
	public GwtSharedMeItem(Long id, Long sharerId) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		setId(      id      );
		setSharerId(sharerId);
		
		// ...initialize everything else.
		m_perShareInfos = new ArrayList<GwtPerShareInfo>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public DefinableEntity       getItem()           {return m_item;         }
	public List<GwtPerShareInfo> getPerShareInfos()  {return m_perShareInfos;}
	public Long                  getId()             {return m_id;           }
	public Long                  getSharerId()       {return m_sharerId;     }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setItem(    DefinableEntity item)     {m_item     = item;    }
	public void setId(      Long            id)       {m_id       = id;      }
	public void setSharerId(Long            sharerId) {m_sharerId = sharerId;}
	
	/**
	 * Adds a new GwtPerShareInfo object to the List<GwtPerShareInfo>.
	 * 
	 * @param recipientId
	 * @param recipientType
	 * @param shareDate
	 * @param rights
	 * @param rightsExpire
	 * @param comment
	 */
	public void addPerShareInfo(Long recipientId, RecipientType recipientType, Date shareDate, ShareRights rights, Date rightsExpire, String comment) {
		m_perShareInfos.add(
			new GwtPerShareInfo(
				recipientId,
				recipientType,
				shareDate,
				rights,
				rightsExpire,
				comment));
	}
	
	/**
	 * Adds a new GwtPerShareInfo object to the List<GwtPerShareInfo>.
	 * 
	 * @param si
	 */
	public void addPerShareInfo(ShareItem si) {
		// Always use the initial form of the method.
		addPerShareInfo(
			si.getRecipientId(),
			si.getRecipientType(),
			si.getCreation().getDate(),
			null,	//! GwtShareHelper.getShareRightsFromRightSet(si.getRightSet()),
			si.getEndDate(),
			si.getComment());
	}
	
	/**
	 * Searches a List<GwtSharedMeItem> for one referring to the
	 * given DefinableEntity.  If one is found, it's returned.
	 * Otherwise, null is returned.
	 * 
	 * @param item
	 * @param siList
	 * 
	 * @return
	 */
	public static GwtSharedMeItem findItemInList(DefinableEntity item, List<GwtSharedMeItem> siList) {
		// If we have an item to find and there are any share items in
		// the list...
		if ((null != item) && (null != siList) && (!(siList.isEmpty()))) {
			// ...scan them.
			for (GwtSharedMeItem si:  siList) {
				// Is this share item the item in question?
				DefinableEntity siItem = si.getItem(); 
				if ((siItem.getId().equals(item.getId())) && siItem.getEntityType().equals(item.getEntityType())) {
					// Yes!  Return it.
					return si;
				}
			}
		}

		// If we get here, we couldn't find the item in question.
		// Return null.
		return null;
	}

	/**
	 * Searches a List<GwtSharedMeItem> for one referring to a
	 * specific entity based on entity type and ID.  If one is
	 * found, it's returned.  Otherwise, null is returned.
	 * 
	 * @param searchId
	 * @param isEntityFolderEntry
	 * @param docId
	 * @param siList
	 * 
	 * @return
	 */
	public static GwtSharedMeItem findItemInList(Long searchId, boolean isEntityFolderEntry, Long docId, List<GwtSharedMeItem> siList) {
		// Do we have any GwtSharedMeItem's to search?
		if ((null != docId) && (null != siList) && (!(siList.isEmpty()))) {
			// Yes!  Scan them.
			boolean bySearchId = (null != searchId);
			for (GwtSharedMeItem si:  siList) {
				// Were we given a searchId to find?
				if (bySearchId) {
					// Yes!  Is this one were looking for?
					if (searchId.equals(si.getId())) {
						// Yes!  Return it.
						return si;
					}
					
					// Skip it.  With a searchId, we only match by
					// that.
					continue;
				}
				
				// Are we looking for a folder entry?
				DefinableEntity	de   = si.getItem();
				Long			deId = de.getId();
				if (isEntityFolderEntry) {
					// Yes!  Is this the GwtSharedMeItem for it?
					if ((!(de instanceof FolderEntry)) || (!(deId.equals(docId)))) {
						// No!  Skip it.
						continue;
					}
					
					// Yes, this is the GwtSharedMeItem for it!
					// Return it.
					return si;
				}
				
				else {
					// No, we must be looking for a binder!  Is
					// this the GwtSharedMeItem for it?
					if ((de instanceof FolderEntry) || (!(deId.equals(docId)))) {
						// No!  Skip it.
						continue;
					}
					
					// Yes, this is the GwtSharedMeItem for it!
					// Return it.
					return si;
				}
			}
		}

		// If we get here, we couldn't find the GwtSharedMeItem in
		// question.  Return null.
		return null;
	}

	/**
	 * Returns true if this object is tracking any GwtPerShareInfo's and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isShared() {
		return (!(m_perShareInfos.isEmpty()));
	}
}
