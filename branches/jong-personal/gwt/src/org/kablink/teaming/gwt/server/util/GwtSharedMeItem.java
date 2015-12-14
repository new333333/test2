/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.server.util.GwtPerShareInfo.PerShareInfoComparator;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.search.Constants;

/**
 * Used to track items for the 'Shared by/with Me' collection points.
 *
 * @author drfoster@novell.com
 */
public class GwtSharedMeItem {
	private boolean					m_hidden;			//
	private DefinableEntity			m_entity;			//
	private List<GwtPerShareInfo>	m_perShareInfos;	//
	private String					m_entityFamily;		//
	
	/**
	 * Inner class used to compare two Map's of 'Shared by/with Me'
	 * entry maps.
	 */
	@SuppressWarnings("unchecked")
	public static class SharedMeEntriesMapComparator implements Comparator<Map> {
		private boolean 				m_sortDescend;	//
		private List<GwtSharedMeItem>	m_shareItems;	//
		private String					m_sortBy;		//
		
		/**
		 * Constructor method.
		 *
		 * @param shareItems
		 * @param sortKey
		 * @param sortDescend
		 */
		public SharedMeEntriesMapComparator(List<GwtSharedMeItem> shareItems, String sortBy, boolean sortDescend) {
			// Initialize the super class...
			super();

			// ...and store the parameters.
			m_shareItems  = shareItems;
			m_sortBy      = sortBy;
			m_sortDescend = sortDescend;
		}

		/**
		 * Compares two search entry Map's based on the criteria passed
		 * into the constructor.
		 * 
		 * Implements the Comparator.compare() method.
		 * 
		 * @param em1
		 * @param em2
		 * 
		 * @return
		 */
		@Override
		public int compare(Map em1, Map em2) {
			int reply = MiscUtil.COMPARE_EQUAL;

			// Do the entry maps refer to different entity types?
			String  et1 = getSafeStringFromEntryMap(em1, Constants.ENTITY_FIELD);
			String  et2 = getSafeStringFromEntryMap(em2, Constants.ENTITY_FIELD);
			if (!(et1.equals(et2))) {
				// Yes!  Simply sort them, that's all we need to apply.
				if (et1.equals(EntityType.folder.name()))
				     reply = MiscUtil.COMPARE_LESS;
				else reply = MiscUtil.COMPARE_GREATER;
			}
			
			else {
				// No, the entry maps refer to the same entity types!
				// What field are we sorting on?
				if (m_sortBy.equalsIgnoreCase(Constants.SORT_TITLE_FIELD)) {
					String s1 = getSafeStringFromEntryMap(em1, Constants.TITLE_FIELD);
					String s2 = getSafeStringFromEntryMap(em2, Constants.TITLE_FIELD);
					reply = MiscUtil.safeSColatedCompare(s1, s2);
				}
				
				else if (m_sortBy.equalsIgnoreCase(Constants.TOTALREPLYCOUNT_FIELD)) {
					int i1 = getSafeIntFromEntryMap(em1, Constants.TOTALREPLYCOUNT_FIELD);
					int i2 = getSafeIntFromEntryMap(em2, Constants.TOTALREPLYCOUNT_FIELD);
					if      (i1 == i2) reply = MiscUtil.COMPARE_EQUAL;
					else if (i1 <  i2) reply = MiscUtil.COMPARE_LESS;
					else               reply = MiscUtil.COMPARE_GREATER;
				}
				
				else if (MiscUtil.hasItems(m_shareItems)){
					GwtPerShareInfo psi1 = getPSI(em1);
					GwtPerShareInfo psi2 = getPSI(em2);
					
					if     ((null == psi1) && (null == psi2)) reply = MiscUtil.COMPARE_EQUAL;
					else if (null == psi1)                    reply = MiscUtil.COMPARE_LESS;
					else if (null == psi2)                    reply = MiscUtil.COMPARE_GREATER;
					else {
						if      (m_sortBy.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_ACCESS))      {reply = PerShareInfoComparator.compareByRights(                                  psi1, psi2);}
						else if (m_sortBy.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_DATE))        {reply = PerShareInfoComparator.compareByShareDates(                              psi1, psi2);}
						else if (m_sortBy.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_EXPIRATION))  {reply = PerShareInfoComparator.compareByExpirationDates(                         psi1, psi2);}
						else if (m_sortBy.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_MESSAGE))     {reply = PerShareInfoComparator.compareByMessage(                                 psi1, psi2);}
						else if (m_sortBy.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_SHARED_BY))   {reply = PerShareInfoComparator.compareByRecipient(CollectionType.SHARED_WITH_ME, psi1, psi2);}
						else if (m_sortBy.equalsIgnoreCase(FolderColumn.COLUMN_SHARE_SHARED_WITH)) {reply = PerShareInfoComparator.compareByRecipient(CollectionType.SHARED_BY_ME,   psi1, psi2);}
					}
				}
				
				// Sort on any other columns that make sense here.
			}

			// If we're doing a descending sort...
			if (m_sortDescend) {
				// ...invert the reply.
				reply = (-reply);
			}

			// If we get here, reply contains the appropriate value for
			// the compare.  Return it.
			return reply;
		}
		
		/*
		 * Returns the GwtPerShareInfo from an entry map.
		 */
		private GwtPerShareInfo getPSI(Map entryMap) {
			String					docIdS     = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.DOCID_FIELD);
			Long					docId      = Long.parseLong(docIdS);
			String					entityType = GwtServerHelper.getStringFromEntryMap(entryMap, Constants.ENTITY_FIELD);
			GwtSharedMeItem			si         = GwtSharedMeItem.findShareMeInList(docId, entityType, m_shareItems);
			List<GwtPerShareInfo>	psiList    = si.getPerShareInfos();
			return (MiscUtil.hasItems(psiList) ? psiList.get(0) : null);
		}
		
		/*
		 * Returns an integer from a string in an entry map.
		 */
		private static int getSafeIntFromEntryMap(Map map, String key) {
			String reply = getSafeStringFromEntryMap(map, key);
			if (0 == reply.length()) {
				reply = "-1";
			}
			return Integer.parseInt(reply);
		}
		
		/*
		 * Returns a non-null string from an entry map.
		 */
		private static String getSafeStringFromEntryMap(Map map, String key) {
			String reply = GwtServerHelper.getStringFromEntryMap(map, key);
			if (null == reply) {
				reply = "";
			}
			return reply;
		}
	}

	/**
	 * Constructor method.
	 * 
	 * @param hidden
	 * @param entity
	 * @param entityFamily
	 */
	public GwtSharedMeItem(boolean hidden, DefinableEntity entity, String entityFamily) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		setHidden(      hidden      );
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
	public boolean               isHidden()          {return m_hidden;       }
	public DefinableEntity       getEntity()         {return m_entity;       }
	public List<GwtPerShareInfo> getPerShareInfos()  {return m_perShareInfos;}
	public String                getEntityFamily()   {return m_entityFamily; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setHidden(      boolean         hidden)       {m_hidden       = hidden;      }
	public void setEntity(      DefinableEntity entity)       {m_entity       = entity;      }
	public void setEntityFamily(String          entityFamily) {m_entityFamily = entityFamily;}
	
	/**
	 * Adds a new GwtPerShareInfo object to the List<GwtPerShareInfo>.
	 * 
	 * @param si
	 */
	public void addPerShareInfo(ShareItem si, String recipientTitle, String sharerTitle) {
		// Is this part of a public share?
		boolean sharePublic = si.getIsPartOfPublicShare();
		if (sharePublic) {
			// Yes!  Are we already tracking a public share?
			if (isSharedWithPublic()) {
				// Yes!  Then we don't need to return another.
				return;
			}
		}
		
		// Construct a new per share info for the share item...
		GwtPerShareInfo psi = new GwtPerShareInfo(
			si.getId(),														// Share  ID.
			si.getSharerId(),												// Sharer ID.
			sharerTitle,													// Sharer title.
			si.getRecipientId(),											// Recipient ID.
			si.getRecipientType(),											// Recipient type (user, group or team.)
			sharePublic,													// true -> Shared with Public (recipient ID and recipient type will be ignored.)  false -> Not shared with Public. 
			recipientTitle,													// Recipient title.
			si.getStartDate(),												// Time/date stamp the share was created.
			GwtShareHelper.getShareRightsFromRightSet(si.getRightSet()),	// The rights granted by the share.
			si.getEndDate(),												// Time/date stamp the share expires.
			si.isExpired(),													// true -> The share is expired.  false -> It's not.
			si.getComment());												// Comment associated with the share.
		
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
		if ((null != shareId) && (null != siList) && (!(siList.isEmpty()))) {
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
		return ((null != m_perShareInfos) && (!(m_perShareInfos.isEmpty())));
	}

	/**
	 * Returns true if this share contains a share public item and
	 * false otherwise.
	 * 
	 * @return
	 */
	public boolean isSharedWithPublic() {
		if (isShared()) {
			for (GwtPerShareInfo psi:  m_perShareInfos) {
				if (psi.isRecipientPublic()) {
					return true;
				}
			}
		}
		return false;
	}
}
