/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.binderviews.folderdata;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtProxyIdentity;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.BinderIcons;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.LimitedUserVisibilityInfo;
import org.kablink.teaming.gwt.client.util.MobileDevicesInfo;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.ShareAccessInfo;
import org.kablink.teaming.gwt.client.util.ShareDateInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationInfo;
import org.kablink.teaming.gwt.client.util.ShareMessageInfo;
import org.kablink.teaming.gwt.client.util.ShareStringValue;
import org.kablink.teaming.gwt.client.util.PrincipalAdminType;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.ViewFileInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about a row of data from a
 * folder.
 * 
 * @author drfoster@novell.com
 */
public class FolderRow implements IsSerializable {
	private BinderIcons								m_binderIcons;				// Hold binder icons of various sizes for a binder.
	private boolean									m_homeDir;					// true -> The row is a user's Home             directory folder.  false -> It's not.
	private boolean									m_myFilesDir;				// true -> The row is a user's My Files Storage directory folder.  false -> It's not.
	private boolean									m_pinned;					// true -> The row is pinned.                          false -> It's not.
	private boolean									m_selectionDisabled;			// true -> Selecting the row should be disabled.                   false -> It should be enabled.
	private EntityId								m_entityId;					// The entity ID of the FolderEntry this FolderRow corresponds to.
	private List<FolderColumn>						m_columns;					// The FolderColumns that contribute to this FolderRow.
	private Map<String, Boolean>					m_rowOverdueDates;			// A map of column names to Boolean indicators of an overdue date possibly stored for a column.
	private Map<String, Boolean>					m_rowWipesScheduled;		// A map of column names to Boolean indicators of a wipe schedule possibly stored for a column.
	private Map<String, CommentsInfo>				m_rowComments;				// A map of column names to CommentsInfo's                        possibly stored for a column.
	private Map<String, DescriptionHtml>			m_rowDescriptionHtmls;		// A map of column names to DescriptionHtml's                     possibly stored for a column.
	private Map<String, EmailAddressInfo>			m_rowEmailAddresses;		// A map of column names to EmailAddressInfo's                    possibly stored for a column.
	private Map<String, EntryEventInfo>				m_rowEntryEvents;			// A map of column names to EntryEventInfo's                      possibly stored for a column.
	private Map<String, EntryLinkInfo>				m_rowEntryLinks;			// A map of column names to EntryLinkInfo's                       possibly stored for a column.
	private Map<String, EntryTitleInfo>				m_rowEntryTitles;			// A map of column names to EntryTitleInfo's                      possibly stored for a column.
	private Map<String, GuestInfo>					m_rowGuests;				// A map of column names to GuestInfo's                           possibly stored for a column.
	private Map<String, GwtProxyIdentity>			m_rowProxyIdentities;		// A map of column names to GwtProxyIdentity's                    possibly stored for a column.
	private Map<String, LimitedUserVisibilityInfo>	m_rowLimitedUserVisibility;	// A map of column names to LimitUserVisibilityInfo's             possibly stored for a column.
	private Map<String, List<AssignmentInfo>>		m_rowAssigneeInfos;			// A map of column names to List<AssignmentInfo>'s                possibly stored for a column.
	private Map<String, MobileDevicesInfo>			m_rowMobileDevices;			// A map of column names to MobileDevicesInfo's                   possibly stored for a column.
	private Map<String, PrincipalInfo>				m_rowPrincipals;			// A map of column names to PrincipalInfo's                       possibly stored for a column.
	private Map<String, PrincipalInfoId>			m_rowPrincipalIds;			// A map of column names to PrincipalInfoId's                     possibly stored for a column.
	private Map<String, List<TaskFolderInfo>>		m_rowTaskFolderInfos;		// A map of column names to List<TaskFolderInfo>'s                possibly stored for a column.
	private Map<String, ViewFileInfo>				m_rowViewFiles;				// A map of column names to ViewFileInfo's                        possibly stored for a column.
	private Map<String, List<ShareAccessInfo>>		m_rowShareAccessInfos;		// A map of column names to List<ShareAccessInfo>'s               possibly stored for a column.
	private Map<String, List<ShareDateInfo>>		m_rowShareDateInfos;		// A map of column names to List<ShareDateInfo>'s                 possibly stored for a column.
	private Map<String, List<ShareExpirationInfo>>	m_rowShareExpirationInfos;	// A map of column names to List<ShareExpirationInfo>'s           possibly stored for a column.
	private Map<String, List<ShareMessageInfo>>		m_rowShareMessageInfos;		// A map of column names to List<ShareMessageInfo>'s              possibly stored for a column.
	private Map<String, String>						m_rowStrings;				// A map of column names to String values                         possibly stored for a column.
	private Map<String, PrincipalAdminType>			m_rowPrincipalAdminTypes;	// A map of column names to PrincipalAdminType values             possibly stored for a column.
	private String									m_rowFamily;				// Family type of this row's entity.
	
	private transient Object						m_clientEntryPinInfo;		// Used on the client side to refer to an EntryPinInfo object created for the row.
	private transient Object						m_serverMobileDevice;		// Used on the server side to refer to a  MobileDevice object when that's what the row represents.

	/**
	 * Inner class used to wrap a long for use as a specific object
	 * for resolving a PrincipalInfo. 
	 */
	public static class PrincipalInfoId implements IsSerializable {
		private Long	m_id;	//
		
		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public PrincipalInfoId() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param id
		 */
		public PrincipalInfoId(Long id) {
			// Initialize this object...
			this();
			
			// ...and store the parameter.
			setId(id);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Long getId() {return m_id;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setId(Long id) {m_id = id;}
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderRow() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that needs it.
		m_binderIcons = new BinderIcons();
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param columns
	 */
	public FolderRow(EntityId entityId, List<FolderColumn> columns) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setEntityId(entityId);
		setColumns( columns );
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean								  isHomeDir()                            {                                    return m_homeDir;                 }
	public boolean								  isMyFilesDir()                         {                                    return m_myFilesDir;              }
	public boolean								  isPinned()                             {                                    return m_pinned;                  }
	public boolean								  isSelectionDisabled()                  {                                    return m_selectionDisabled;       }
	public EntityId								  getEntityId()                          {                                    return m_entityId;                }
	public List<FolderColumn>					  getColumns()                           {                                    return m_columns;                 }
	public Map<String, Boolean>					  getRowOverdueDates()                   {validateMapOverdueDates();          return m_rowOverdueDates;         }
	public Map<String, Boolean>					  getRowWipesScheduled()                 {validateMapWipesScheduled();        return m_rowWipesScheduled;       }
	public Map<String, CommentsInfo>			  getRowCommentsMap()                    {validateMapComments();              return m_rowComments;             } 
	public Map<String, DescriptionHtml>			  getRowDescriptionHtmlMap()             {validateMapDescriptionHtmls();      return m_rowDescriptionHtmls;     }
	public Map<String, EmailAddressInfo>		  getRowEmailAddressMap()                {validateMapEmailAddresses();        return m_rowEmailAddresses;       }
	public Map<String, EntryEventInfo>			  getRowEntryEventMap()                  {validateMapEvents();                return m_rowEntryEvents;          }
	public Map<String, EntryLinkInfo>			  getRowEntryLinkMap()                   {validateMapLinks();                 return m_rowEntryLinks;           }
	public Map<String, EntryTitleInfo>			  getRowEntryTitlesMap()                 {validateMapTitles();                return m_rowEntryTitles;          }
	public Map<String, GuestInfo>				  getRowGuestsMap()                      {validateMapGuests();                return m_rowGuests;               }
	public Map<String, GwtProxyIdentity>		  getRowProxyIdentitiesMap()             {validateMapProxyIdentities();       return m_rowProxyIdentities;      }
	public Map<String, LimitedUserVisibilityInfo> getRowLimitedUserVisibilityMap()       {validateMapLimitedUserVisibility(); return m_rowLimitedUserVisibility;} 
	public Map<String, List<AssignmentInfo>>	  getRowAssigneeInfoListsMap()           {validateMapAssignees();             return m_rowAssigneeInfos;        }
	public Map<String, MobileDevicesInfo>		  getRowMobileDevicesMap()               {validateMapMobileDevices();         return m_rowMobileDevices;        } 
	public Map<String, PrincipalInfo>			  getRowPrincipalsMap()                  {validateMapPrincipals();            return m_rowPrincipals;           }
	public Map<String, PrincipalInfoId>			  getRowPrincipalIdsMap()                {validateMapPrincipalIds();          return m_rowPrincipalIds;         }
	public Map<String, List<TaskFolderInfo>>	  getRowTaskFolderInfoListsMap()         {validateMapTaskFolders();           return m_rowTaskFolderInfos;      } 
	public Map<String, ViewFileInfo>			  getRowViewFilesMap()                   {validateMapViews();                 return m_rowViewFiles;            } 
	public Map<String, String>					  getRowStringsMap()                     {validateMapStrings();               return m_rowStrings;              }
	public Map<String, PrincipalAdminType>		  getRowPrincipalAdminTypesMap()         {validateMapPrincipalAdminTypes();   return m_rowPrincipalAdminTypes;  }
	public Object								  getClientEntryPinInfo()                {                                    return m_clientEntryPinInfo;      }
	public Object								  getServerMobileDevice()                {                                    return m_serverMobileDevice;      }
	public String								  getBinderIcon(BinderIconSize iconSize) {return m_binderIcons.getBinderIcon(iconSize);                         }
	public String								  getRowFamily()                         {return m_rowFamily;                                                   }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setColumns(           List<FolderColumn> columns)                             {m_columns            = columns;                   }
	public void setEntityId(          EntityId           entityId)                            {m_entityId           = entityId;                  }
	public void setHomeDir(           boolean            homeDir)                             {m_homeDir            = homeDir;                   }
	public void setMyFilesDir(        boolean            myFilesDir)                          {m_myFilesDir         = myFilesDir;                }
	public void setPinned(            boolean            pinned)                              {m_pinned             = pinned;                    }
	public void setSelectionDisabled( boolean            selectionDisabled)                   {m_selectionDisabled  = selectionDisabled;         }
	public void setClientEntryPinInfo(Object             clientEntryPinInfo)                  {m_clientEntryPinInfo = clientEntryPinInfo;        }
	public void setServerMobileDevice(Object             serverMobileDevice)                  {m_serverMobileDevice = serverMobileDevice;        }
	public void setBinderIcon(        String             binderIcon, BinderIconSize iconSize) {m_binderIcons.setBinderIcon(binderIcon, iconSize);}
	public void setRowFamily(         String             rowFamily)                           {m_rowFamily          = rowFamily;                 }
	
	/**
	 * Clears the binder icons being tracked in this TreeInfo.
	 */
	public void clearBinderIcons() {
		m_binderIcons.clearBinderIcons();
	}
	
	/**
	 * Stores the value for a specific column.
	 * 
	 * @param fc
	 * @param v
	 */
	public void setColumnValue(FolderColumn fc, Object v) {
		String vk = getValueKey(fc);
		if      (v instanceof String)                    {validateMapStrings();               m_rowStrings.put(              vk, ((String)                    v));}
		else if (v instanceof CommentsInfo)              {validateMapComments();              m_rowComments.put(             vk, ((CommentsInfo)              v));}
		else if (v instanceof DescriptionHtml)           {validateMapDescriptionHtmls();      m_rowDescriptionHtmls.put(     vk, ((DescriptionHtml)           v));}
		else if (v instanceof EmailAddressInfo)          {validateMapEmailAddresses();        m_rowEmailAddresses.put(       vk, ((EmailAddressInfo)          v));}
		else if (v instanceof EntryEventInfo)            {validateMapEvents();                m_rowEntryEvents.put(          vk, ((EntryEventInfo)            v));}
		else if (v instanceof EntryLinkInfo)             {validateMapLinks();                 m_rowEntryLinks.put(           vk, ((EntryLinkInfo)             v));}
		else if (v instanceof EntryTitleInfo)            {validateMapTitles();                m_rowEntryTitles.put(          vk, ((EntryTitleInfo)            v));}
		else if (v instanceof GuestInfo)                 {validateMapGuests();                m_rowGuests.put(               vk, ((GuestInfo)                 v));}
		else if (v instanceof GwtProxyIdentity)          {validateMapProxyIdentities();       m_rowProxyIdentities.put(      vk, ((GwtProxyIdentity)          v));}
		else if (v instanceof LimitedUserVisibilityInfo) {validateMapLimitedUserVisibility(); m_rowLimitedUserVisibility.put(vk, ((LimitedUserVisibilityInfo) v));}
		else if (v instanceof MobileDevicesInfo)         {validateMapMobileDevices();         m_rowMobileDevices.put(        vk, ((MobileDevicesInfo)         v));}
		else if (v instanceof PrincipalInfo)             {validateMapPrincipals();            m_rowPrincipals.put(           vk, ((PrincipalInfo)             v));}
		else if (v instanceof PrincipalInfoId)           {validateMapPrincipalIds();          m_rowPrincipalIds.put(         vk, ((PrincipalInfoId)           v));}
		else if (v instanceof PrincipalAdminType)        {validateMapPrincipalAdminTypes();   m_rowPrincipalAdminTypes.put(  vk, ((PrincipalAdminType)        v));}
		else if (v instanceof ViewFileInfo)              {validateMapViews();                 m_rowViewFiles.put(            vk, ((ViewFileInfo)              v));}
		else                                             {validateMapStrings();               m_rowStrings.put(              vk, v.toString());                   }
	}
	
	public void setColumnValue_AssignmentInfos(FolderColumn fc, List<AssignmentInfo> aiList) {
		validateMapAssignees(); 
		m_rowAssigneeInfos.put(getValueKey(fc), aiList);		
	}
	
	public void setColumnValue_ShareAccessInfos(FolderColumn fc, List<ShareAccessInfo> saiList) {
		validateMapShareAccesses(); 
		m_rowShareAccessInfos.put(getValueKey(fc), saiList);		
	}
	
	public void setColumnValue_ShareDateInfos(FolderColumn fc, List<ShareDateInfo> sdiList) {
		validateMapShareDates(); 
		m_rowShareDateInfos.put(getValueKey(fc), sdiList);		
	}
	
	public void setColumnValue_ShareExpirationInfos(FolderColumn fc, List<ShareExpirationInfo> seiList) {
		validateMapShareExpirations(); 
		m_rowShareExpirationInfos.put(getValueKey(fc), seiList);		
	}
	
	public void setColumnValue_ShareMessageInfos(FolderColumn fc, List<ShareMessageInfo> smiList) {
		validateMapShareMessages(); 
		m_rowShareMessageInfos.put(getValueKey(fc), smiList);		
	}
	
	public void setColumnValue_TaskFolderInfos(FolderColumn fc, List<TaskFolderInfo> tfiList) {
		validateMapTaskFolders(); 
		m_rowTaskFolderInfos.put(getValueKey(fc), tfiList);		
	}

	/**
	 * Stores an overdue date status for a specific column.
	 * 
	 * @param fc
	 * 
	 * @param overdueDate
	 */
	public void setColumnOverdueDate(FolderColumn fc, Boolean overdueDate) {
		validateMapOverdueDates();
		m_rowOverdueDates.put(getValueKey(fc), overdueDate);
	}

	/**
	 * Stores a wipe scheduled flag for a specific column.
	 * 
	 * @param fc
	 * 
	 * @param wipeScheduled
	 */
	public void setColumnWipeScheduled(FolderColumn fc, Boolean wipeScheduled) {
		validateMapWipesScheduled();
		m_rowWipesScheduled.put(getValueKey(fc), wipeScheduled);
	}

	/**
	 * Returns the List<AssignmentInfo> value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public List<AssignmentInfo> getColumnValueAsAssignmentInfos(FolderColumn fc) {
		return ((null == m_rowAssigneeInfos) ? null : m_rowAssigneeInfos.get(getValueKey(fc)));
	}

	/**
	 * Returns the CommentsInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public CommentsInfo getColumnValueAsComments(FolderColumn fc) {
		return ((null == m_rowComments) ? null : m_rowComments.get(getValueKey(fc)));
	}

	/**
	 * Returns the DescriptionHtml value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public DescriptionHtml getColumnValueAsDescriptionHtml(FolderColumn fc) {
		return ((null == m_rowDescriptionHtmls) ? null : m_rowDescriptionHtmls.get(getValueKey(fc)));
	}

	/**
	 * Returns the EmailAddressInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public EmailAddressInfo getColumnValueAsEmailAddress(FolderColumn fc) {
		return ((null == m_rowEmailAddresses) ? null : m_rowEmailAddresses.get(getValueKey(fc)));
	}

	/**
	 * Returns the EntryEventInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public EntryEventInfo getColumnValueAsEntryEvent(FolderColumn fc) {
		return ((null == m_rowEntryEvents) ? null : m_rowEntryEvents.get(getValueKey(fc)));
	}

	/**
	 * Returns the LimitedUserVisibilityInfo value for a specific
	 * column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public LimitedUserVisibilityInfo getColumnValueAsLimitedUserVisibility(FolderColumn fc) {
		return ((null == m_rowLimitedUserVisibility) ? null : m_rowLimitedUserVisibility.get(getValueKey(fc)));
	}

	/**
	 * Returns the EntryLinkInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public EntryLinkInfo getColumnValueAsEntryLink(FolderColumn fc) {
		return ((null == m_rowEntryLinks) ? null : m_rowEntryLinks.get(getValueKey(fc)));
	}

	/**
	 * Returns the EntryTitleInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public EntryTitleInfo getColumnValueAsEntryTitle(FolderColumn fc) {
		return ((null == m_rowEntryTitles) ? null : m_rowEntryTitles.get(getValueKey(fc)));
	}

	/**
	 * Returns the GuestInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public GuestInfo getColumnValueAsGuestInfo(FolderColumn fc) {
		return ((null == m_rowGuests) ? null : m_rowGuests.get(getValueKey(fc)));
	}

	/**
	 * Returns the GwtProxyIdentity value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public GwtProxyIdentity getColumnValueAsProxyIdentity(FolderColumn fc) {
		return ((null == m_rowProxyIdentities) ? null : m_rowProxyIdentities.get(getValueKey(fc)));
	}

	/**
	 * Returns the MobileDevicesInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public MobileDevicesInfo getColumnValueAsMobileDevices(FolderColumn fc) {
		return ((null == m_rowMobileDevices) ? null : m_rowMobileDevices.get(getValueKey(fc)));
	}

	/**
	 * Returns the PrincipalInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public PrincipalInfo getColumnValueAsPrincipalInfo(FolderColumn fc) {
		return ((null == m_rowPrincipals) ? null : m_rowPrincipals.get(getValueKey(fc)));
	}

	/**
	 * Returns the PrincipalInfoId value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public PrincipalInfoId getColumnValueAsPrincipalInfoId(FolderColumn fc) {
		return ((null == m_rowPrincipalIds) ? null : m_rowPrincipalIds.get(getValueKey(fc)));
	}

	/**
	 * Returns the List<TaskFolderInfo> value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public List<TaskFolderInfo> getColumnValueAsTaskFolderInfos(FolderColumn fc) {
		return ((null == m_rowTaskFolderInfos) ? null : m_rowTaskFolderInfos.get(getValueKey(fc)));
	}

	/**
	 * Returns the List<ShareStringValue> value for a share access
	 * column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public List<ShareStringValue> getColumnValueAsShareAccessInfos(FolderColumn fc) {
		if (null == m_rowShareAccessInfos) {
			return null;
		}
		List<ShareStringValue> reply = new ArrayList<ShareStringValue>();
		for (ShareAccessInfo sai:  m_rowShareAccessInfos.get(getValueKey(fc))) {
			reply.add(sai);
		}
		return reply;
	}

	/**
	 * Returns the List<ShareStringValue> value for a share date
	 * column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public List<ShareStringValue> getColumnValueAsShareDateInfos(FolderColumn fc) {
		if (null == m_rowShareDateInfos) {
			return null;
		}
		List<ShareStringValue> reply = new ArrayList<ShareStringValue>();
		for (ShareDateInfo sai:  m_rowShareDateInfos.get(getValueKey(fc))) {
			reply.add(sai);
		}
		return reply;
	}

	/**
	 * Returns the List<ShareStringValue> value for a share expiration
	 * column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public List<ShareStringValue> getColumnValueAsShareExpirationInfos(FolderColumn fc) {
		if (null == m_rowShareExpirationInfos) {
			return null;
		}
		List<ShareStringValue> reply = new ArrayList<ShareStringValue>();
		for (ShareExpirationInfo sai:  m_rowShareExpirationInfos.get(getValueKey(fc))) {
			reply.add(sai);
		}
		return reply;
	}

	/**
	 * Returns the List<ShareStringValue> value for a share message
	 * column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public List<ShareStringValue> getColumnValueAsShareMessageInfos(FolderColumn fc) {
		if (null == m_rowShareMessageInfos) {
			return null;
		}
		List<ShareStringValue> reply = new ArrayList<ShareStringValue>();
		for (ShareMessageInfo sai:  m_rowShareMessageInfos.get(getValueKey(fc))) {
			reply.add(sai);
		}
		return reply;
	}

	/**
	 * Returns the PrincipalAdminType value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public PrincipalAdminType getColumnValueAsPrincipalAdminType(FolderColumn fc) {
		return ((null == m_rowPrincipalAdminTypes) ? null : m_rowPrincipalAdminTypes.get(getValueKey(fc)));
	}

	/**
	 * Returns the ViewFileInfo value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public ViewFileInfo getColumnValueAsViewFile(FolderColumn fc) {
		return ((null == m_rowViewFiles) ? null : m_rowViewFiles.get(getValueKey(fc)));
	}

	/**
	 * Returns the String value for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public String getColumnValueAsString(FolderColumn fc) {
		String vk = getValueKey(fc);
		String reply = ((null == m_rowStrings) ? null : m_rowStrings.get(vk));
		if (null == reply) {
			PrincipalInfo pi = ((null == m_rowPrincipals) ? null : m_rowPrincipals.get(vk));
			if (null != pi) {
				reply = pi.getTitle();
			}
			else {
				GuestInfo gi = ((null == m_rowGuests) ? null : m_rowGuests.get(vk));
				if (null != gi) {
					reply = gi.getTitle();
				}
				else {
					GwtProxyIdentity gpi = ((null == m_rowProxyIdentities) ? null : m_rowProxyIdentities.get(vk));
					if (null != gpi) {
						reply = gpi.getTitle();
					}
					else {
						DescriptionHtml dh = ((null == m_rowDescriptionHtmls) ? null : m_rowDescriptionHtmls.get(vk));
						if (null != dh) {
							reply = dh.getDescription();
						}
						else {
							EmailAddressInfo emai = ((null == m_rowEmailAddresses) ? null : m_rowEmailAddresses.get(vk));
							if (null != emai) {
								reply = emai.getEmailAddress(); 
							}
							
							else {
								Boolean wipeScheduled = ((null == m_rowWipesScheduled) ? null : m_rowWipesScheduled.get(vk));
								if (null == wipeScheduled) {
									wipeScheduled = Boolean.FALSE;
								}
								reply = (wipeScheduled ? GwtTeaming.getMessages().yes() : GwtTeaming.getMessages().no());
							}
						}
					}
				}
			}
		}
		return ((null == reply) ? "" : reply); 
	}

	/**
	 * Returns the Boolean overdue date status for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public Boolean getColumnOverdueDate(FolderColumn fc) {
		return ((null == m_rowOverdueDates) ? null : m_rowOverdueDates.get(getValueKey(fc)));
	}
	
	/**
	 * Returns the Boolean wipe scheduled status for a specific column.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public Boolean getColumnWipeScheduled(FolderColumn fc) {
		return ((null == m_rowWipesScheduled) ? null : m_rowWipesScheduled.get(getValueKey(fc)));
	}
	
	/*
	 * Returns the key that we should use into the data map to
	 * determine a column's value.
	 */
	private String getValueKey(FolderColumn fc) {
		return fc.getColumnEleName().toLowerCase();
	}

	/**
	 * Returns true if this row refers to a binder and false otherwise.
	 * 
	 * @return
	 */
	public boolean isBinder() {
		return ((null == m_entityId) ? false : m_entityId.isBinder());
	}
	
	/**
	 * Returns true if a column's value is a List<AssignmenInfo> and
	 * false otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueAssigneeInfos(FolderColumn fc) {
		return ((null != m_rowAssigneeInfos) && (null != m_rowAssigneeInfos.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is a DescriptionHtml and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueDescriptionHtml(FolderColumn fc) {
		return ((null != m_rowDescriptionHtmls) && (null != m_rowDescriptionHtmls.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is an EmailAddressInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueEmailAddressInfo(FolderColumn fc) {
		return ((null != m_rowEmailAddresses) && (null != m_rowEmailAddresses.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is an EntryEventInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueEntryEventInfo(FolderColumn fc) {
		return ((null != m_rowEntryEvents) && (null != m_rowEntryEvents.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is an EntryLinkInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueEntryLinkInfo(FolderColumn fc) {
		return ((null != m_rowEntryLinks) && (null != m_rowEntryLinks.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is an EntryTitleInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueEntryTitleInfo(FolderColumn fc) {
		return ((null != m_rowEntryTitles) && (null != m_rowEntryTitles.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is a GuestInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueGuestInfo(FolderColumn fc) {
		return ((null != m_rowGuests) && (null != m_rowGuests.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is a GwtProxyIdentity and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueProxyIdentity(FolderColumn fc) {
		return ((null != m_rowProxyIdentities) && (null != m_rowProxyIdentities.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is a PrincipalInfo and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValuePrincipalInfo(FolderColumn fc) {
		return ((null != m_rowPrincipals) && (null != m_rowPrincipals.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is a PrincipalInfoId and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValuePrincipalInfoId(FolderColumn fc) {
		return ((null != m_rowPrincipalIds) && (null != m_rowPrincipalIds.get(getValueKey(fc))));
	}

	/**
	 * Returns true if a column's value is a String and false
	 * otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValueString(FolderColumn fc) {
		return ((null != m_rowStrings) && (null != m_rowStrings.get(getValueKey(fc))));
	}
	
	/**
	 * Returns true if a column's value is a PrincipalAdminType and
	 * false otherwise.
	 * 
	 * @param fc
	 * 
	 * @return
	 */
	public boolean isColumnValuePrincipalAdminType(FolderColumn fc) {
		return ((null != m_rowPrincipalAdminTypes) && (null != m_rowPrincipalAdminTypes.get(getValueKey(fc))));
	}

	/**
	 * Returns true if this row corresponds to a file entity and false
	 * otherwise.
	 *
	 * @param isFilr
	 * 
	 * @return
	 */
	public boolean isRowFile(boolean isFilr) {
		boolean reply = (null != m_rowFamily);
		if (reply) {
			reply = m_rowFamily.equalsIgnoreCase("file");
			if ((!reply) && (!isFilr)) {
				reply = m_rowFamily.equalsIgnoreCase("photo");
			}
		}
		return reply;
	}

	/*
	 * Validates that the various Maps have been defined.
	 */
	private void validateMapAssignees()             {if (null == m_rowAssigneeInfos)		 m_rowAssigneeInfos			= new HashMap<String, List<AssignmentInfo>>();     }
	private void validateMapOverdueDates()          {if (null == m_rowOverdueDates)  		 m_rowOverdueDates			= new HashMap<String, Boolean>();                  }
	private void validateMapWipesScheduled()        {if (null == m_rowWipesScheduled)  		 m_rowWipesScheduled		= new HashMap<String, Boolean>();                  }
	private void validateMapComments()              {if (null == m_rowComments)				 m_rowComments				= new HashMap<String, CommentsInfo>();             }
	private void validateMapDescriptionHtmls()      {if (null == m_rowDescriptionHtmls)		 m_rowDescriptionHtmls		= new HashMap<String, DescriptionHtml>();          }
	private void validateMapEmailAddresses()        {if (null == m_rowEmailAddresses)		 m_rowEmailAddresses		= new HashMap<String, EmailAddressInfo>();         }
	private void validateMapEvents()                {if (null == m_rowEntryEvents)			 m_rowEntryEvents			= new HashMap<String, EntryEventInfo>();           }
	private void validateMapGuests()                {if (null == m_rowGuests)		   		 m_rowGuests			    = new HashMap<String, GuestInfo>();                }
	private void validateMapProxyIdentities()       {if (null == m_rowProxyIdentities)		 m_rowProxyIdentities       = new HashMap<String, GwtProxyIdentity>();         }
	private void validateMapLimitedUserVisibility() {if (null == m_rowLimitedUserVisibility) m_rowLimitedUserVisibility = new HashMap<String, LimitedUserVisibilityInfo>();}
	private void validateMapLinks()                 {if (null == m_rowEntryLinks)			 m_rowEntryLinks			= new HashMap<String, EntryLinkInfo>();            }
	private void validateMapMobileDevices()         {if (null == m_rowMobileDevices)		 m_rowMobileDevices			= new HashMap<String, MobileDevicesInfo>();        }
	private void validateMapPrincipals()            {if (null == m_rowPrincipals)			 m_rowPrincipals			= new HashMap<String, PrincipalInfo>();            }
	private void validateMapPrincipalIds()          {if (null == m_rowPrincipalIds)			 m_rowPrincipalIds			= new HashMap<String, PrincipalInfoId>();          }
	private void validateMapTaskFolders()           {if (null == m_rowTaskFolderInfos)		 m_rowTaskFolderInfos		= new HashMap<String, List<TaskFolderInfo>>();     }
	private void validateMapTitles()                {if (null == m_rowEntryTitles)			 m_rowEntryTitles			= new HashMap<String, EntryTitleInfo>();           }
	private void validateMapShareAccesses()         {if (null == m_rowShareAccessInfos)		 m_rowShareAccessInfos		= new HashMap<String, List<ShareAccessInfo>>();    }
	private void validateMapShareDates()            {if (null == m_rowShareDateInfos)		 m_rowShareDateInfos		= new HashMap<String, List<ShareDateInfo>>();      }
	private void validateMapShareExpirations()      {if (null == m_rowShareExpirationInfos)	 m_rowShareExpirationInfos	= new HashMap<String, List<ShareExpirationInfo>>();}
	private void validateMapShareMessages()         {if (null == m_rowShareMessageInfos)	 m_rowShareMessageInfos		= new HashMap<String, List<ShareMessageInfo>>();   }
	private void validateMapStrings()               {if (null == m_rowStrings)				 m_rowStrings				= new HashMap<String, String>();                   }
	private void validateMapPrincipalAdminTypes()   {if (null == m_rowPrincipalAdminTypes)	 m_rowPrincipalAdminTypes	= new HashMap<String, PrincipalAdminType>();       }
	private void validateMapViews()                 {if (null == m_rowViewFiles)			 m_rowViewFiles				= new HashMap<String, ViewFileInfo>();             }
}
