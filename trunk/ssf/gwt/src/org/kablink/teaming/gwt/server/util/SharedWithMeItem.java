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
import org.kablink.teaming.domain.ShareItemMember;
import org.kablink.teaming.gwt.client.util.ShareRights;

/**
 * Used to track items for the 'Shared with Me' collection point.
 *
 * @author drfoster@novell.com
 */
public class SharedWithMeItem {
	private Date				m_rightsExpire;	//
	private DefinableEntity		m_item;			//
	private List<SharerInfo>	m_sharerInfos;	//
	private Long				m_id;			//
	private ShareRights			m_rights;		//
	
	/**
	 * Inner class used to track sharers in a SharedWithMeItem.
	 */
	public static class SharerInfo {
		private Date	m_date;		//
		private Long	m_id;		//
		private String	m_comment;	//
		
		/**
		 * Constructor method.
		 * 
		 * @param id
		 * @param date
		 * @param comment
		 */
		public SharerInfo(Long id, Date date, String comment) {
			// Initialize the super class...
			super();
			
			// ...and store the parameters.
			setId(     id     );
			setDate(   date   );
			setComment(comment);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Date   getDate()    {return m_date;   }
		public Long   getId()      {return m_id;     }
		public String getComment() {return m_comment;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setDate(   Date   date)    {m_date    = date;   }
		public void setId(     Long   id)      {m_id      = id;     }
		public void setComment(String comment) {m_comment = comment;}
		
		/**
		 * Returns true if a List<SharerInfo> contains a specific
		 * sharer and false otherwise.
		 * 
		 * @param sharerInfos
		 * @param id
		 * 
		 * @return
		 */
		public static boolean contains(List<SharerInfo> sharerInfos, Long id) {
			if ((null != id) && (null != sharerInfos) && (!(sharerInfos.isEmpty()))) {
				for (SharerInfo si:  sharerInfos) {
					if (si.getId().equals(id)) {
						return true;
					}
				}
			}
			return false;
		}
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param id
	 */
	public SharedWithMeItem(Long id) {
		// Initialize the super class...
		super();
		
		// ...store the parameter...
		setId(id);
		
		// ...initialize everything else.
		m_sharerInfos = new ArrayList<SharerInfo>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Date             getRightsExpire() {return m_rightsExpire;}
	public DefinableEntity  getItem()         {return m_item;        }
	public List<SharerInfo> getSharerInfos()  {return m_sharerInfos; }
	public Long             getId()           {return m_id;          }
	public ShareRights      getRights()       {return m_rights;      }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setRightsExpire(Date            rightsExpire) {m_rightsExpire = rightsExpire;}
	public void setItem(        DefinableEntity item)         {m_item         = item;        }
	public void setId(          Long            id)           {m_id           = id;          }
	public void setRights(      ShareRights     rights)       {m_rights       = rights;      }
	
	/**
	 * Adds a new ShareInfo object to the List<SharerInfo>.
	 * 
	 * @param id
	 * @param date
	 * @param comment
	 */
	public void addSharerInfo(Long id, Date date, String comment) {
		m_sharerInfos.add(new SharerInfo(id, date, comment));
	}
	
	/**
	 * Searches a List<SharedWithMeItem> for one referring to the
	 * given DefinableEntity.  If one is found, it's returned.
	 * Otherwise, null is returned.
	 * 
	 * @param item
	 * @param siList
	 * 
	 * @return
	 */
	public static SharedWithMeItem findItemInList(DefinableEntity item, List<SharedWithMeItem> siList) {
		// If we have an item to find and there are any share items in
		// the list...
		if ((null != item) && (null != siList) && (!(siList.isEmpty()))) {
			// ...scan them.
			for (SharedWithMeItem si:  siList) {
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
	 * Searches a List<SharedWithMeItem> for one referring to a
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
	public static SharedWithMeItem findItemInList(Long searchId, boolean isEntityFolderEntry, Long docId, List<SharedWithMeItem> siList) {
		// Do we have any SharedWithMeItem's to search?
		if ((null != docId) && (null != siList) && (!(siList.isEmpty()))) {
			// Yes!  Scan them.
			boolean bySearchId = (null != searchId);
			for (SharedWithMeItem si:  siList) {
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
					// Yes!  Is this the SharedWithMeItem for it?
					if ((!(de instanceof FolderEntry)) || (!(deId.equals(docId)))) {
						// No!  Skip it.
						continue;
					}
					
					// Yes, this is the SharedWithMeItem for it!
					// Return it.
					return si;
				}
				
				else {
					// No, we must be looking for a binder!  Is
					// this the SharedWithMeItem for it?
					if ((de instanceof FolderEntry) || (!(deId.equals(docId)))) {
						// No!  Skip it.
						continue;
					}
					
					// Yes, this is the SharedWithMeItem for it!
					// Return it.
					return si;
				}
			}
		}

		// If we get here, we couldn't find the SharedWithMeItem in
		// question.  Return null.
		return null;
	}

	/**
	 * Returns true if this object is tracking any rights
	 * information and false otherwise.
	 * 
	 * @return
	 */
	public boolean hasRights() {
		return (null != m_rights);
	}

	/**
	 * Updates the rights on this SharedWithMeItem based on the
	 * rights in a ShareItemMember.
	 * 
	 * The logic is to always track the highest level of rights
	 * granted.
	 * 
	 * @param siMember
	 */
	public void updateRights(ShareItemMember siMember) {
		boolean		storeRights  = false;
		ShareRights rights       = GwtShareHelper.getShareRightsFromRightSet(siMember.getRightSet());
		Date		rightsExpire = siMember.getEndDate();
		if (hasRights()) {
			switch (getRights()) {
			case VIEW:
				switch (rights) {
				default:
				case VIEW:                             break;
				case CONTRIBUTOR:
				case OWNER:        storeRights = true; break;	// Overwrite View with Contributor or Owner.
				}
				break;
				
			case CONTRIBUTOR:
				switch (rights) {
				default:
				case VIEW:
				case CONTRIBUTOR:                      break;
				case OWNER:        storeRights = true; break;	// Overwrite Contributor with Owner.
				}
				break;
				
			case OWNER:
				break;											// Never overwrite Owner.
				
			default:
				storeRights = true;								// Always overwrite what we don't undertand.
				break;
			}
		}
		else {
			storeRights = true;
		}
		
		if (storeRights ){
			setRights(      rights      );
			setRightsExpire(rightsExpire);
		}
	}
}
