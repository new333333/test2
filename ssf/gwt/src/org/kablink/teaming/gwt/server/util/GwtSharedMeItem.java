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
import java.util.List;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Used to track items for the 'Shared by/with Me' collection points.
 *
 * @author drfoster@novell.com
 */
public class GwtSharedMeItem {
	private DefinableEntity			m_entity;			//
	private List<GwtPerShareInfo>	m_perShareInfos;	//
	private String					m_entityFamily;		//
	
	/**
	 * Constructor method.
	 * 
	 * @param entity
	 * @param entityFamily
	 */
	public GwtSharedMeItem(DefinableEntity entity, String entityFamily) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		setEntity(      entity      );
		setEntityFamily(entityFamily);
		
		// ...initialize everything else.
		m_perShareInfos = new ArrayList<GwtPerShareInfo>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public DefinableEntity       getEntity()         {return m_entity;       }
	public List<GwtPerShareInfo> getPerShareInfos()  {return m_perShareInfos;}
	public String                getEntityFamily()   {return m_entityFamily; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEntity(      DefinableEntity entity)       {m_entity       = entity;      }
	public void setEntityFamily(String          entityFamily) {m_entityFamily = entityFamily;}
	
	/**
	 * Adds a new GwtPerShareInfo object to the List<GwtPerShareInfo>.
	 * 
	 * @param si
	 */
	public void addPerShareInfo(ShareItem si) {
		// Construct a new per share info for the share item...
		GwtPerShareInfo psi = new GwtPerShareInfo(
			si.getId(),
			si.getModification().getPrincipal().getId(),
			si.getRecipientId(),
			si.getRecipientType(),
			si.getCreation().getDate(),
			GwtShareHelper.getShareRightsFromRightSet(si.getRightSet()),
			si.getEndDate(),
			si.isExpired(),
			si.getComment());
		
		// ...and add it to the list.
		m_perShareInfos.add(psi);
	}
	
	/**
	 * Searches a List<GwtSharedMeItem> for one referring to the
	 * given DefinableEntity.  If one is found, it's returned.
	 * Otherwise, null is returned.
	 * 
	 * @param entityId
	 * @param entityType
	 * @param siList
	 * 
	 * @return
	 */
	public static GwtSharedMeItem findShareMeInList(Long entityId, String entityType, List<GwtSharedMeItem> siList) {
		// If we have an entity to find and there are any share
		// items in the list...
		if ((null != entityId) && MiscUtil.hasString(entityType) && (null != siList) && (!(siList.isEmpty()))) {
			// ...scan them.
			for (GwtSharedMeItem si:  siList) {
				// Is this share item the entity in question?
				DefinableEntity siEntity = si.getEntity(); 
				if ((siEntity.getId().equals(entityId)) && siEntity.getEntityType().name().equalsIgnoreCase(entityType)) {
					// Yes!  Return it.
					return si;
				}
			}
		}

		// If we get here, we couldn't find the entity in question.
		// Return null.
		return null;
	}

	/**
	 * Searches a List<GwtSharedMeItem> for one referring to the
	 * given DefinableEntity.  If one is found, it's returned.
	 * Otherwise, null is returned.
	 * 
	 * @param entity
	 * @param siList
	 * 
	 * @return
	 */
	public static GwtSharedMeItem findShareMeInList(DefinableEntity entity, List<GwtSharedMeItem> siList) {
		// If we have an entity to find...
		if (null != entity) {
			// ...always use the initial form of the method.
			return findShareMeInList(entity.getId(), entity.getEntityType().name(), siList);
		}

		// If we get here, we couldn't find the entity in question.
		// Return null.
		return null;
	}

	/**
	 * Searches a List<GwtSharedMeItem> for one referring to a
	 * specific share item.  If one is found, it's returned.
	 * Otherwise, null is returned.
	 * 
	 * @param shareId
	 * @param siList
	 * 
	 * @return
	 */
	public static GwtSharedMeItem findShareMeInList(Long shareId, List<GwtSharedMeItem> siList) {
		// Do we have any GwtSharedMeItem's to search?
		if ((null != siList) && (!(siList.isEmpty()))) {
			// Yes!  Scan them.
			for (GwtSharedMeItem si:  siList) {
				// Scan this GwtSharedMeItem's GwtPerShareInfo's.
				for (GwtPerShareInfo siPSI:  si.getPerShareInfos()) {
					// Is this one were looking for?
					if (shareId.equals(siPSI.getShareId())) {
						// Yes!  Return its GwtSharedMeItem.
						return si;
					}
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
