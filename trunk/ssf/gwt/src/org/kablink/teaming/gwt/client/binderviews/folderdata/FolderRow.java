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
package org.kablink.teaming.gwt.client.binderviews.folderdata;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.util.EmailAddressInfo;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
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
	private boolean								m_canModify;			//
	private boolean								m_canPurge;				//
	private boolean								m_canTrash;				//
	private boolean								m_pinned;				//
	private EntityId							m_entityId;				// The entity ID of the FolderEntry this FolderRow corresponds to.
	private List<FolderColumn>					m_columns;				// The FolderColumns that contribute to this FolderRow.
	private Map<String, Boolean>				m_rowOverdueDates;		// A map of column names to Boolean indicators of an overdue date possibly stored for a column.
	private Map<String, DescriptionHtml>		m_rowDescriptionHtmls;	// A map of column names to DescriptionHtml's                     possibly stored for a column.
	private Map<String, EmailAddressInfo>		m_rowEmailAddresses;	// A map of column names to EmailAddressInfo's                    possibly stored for a column.
	private Map<String, EntryEventInfo>			m_rowEntryEvents;		// A map of column names to EntryEventInfo's                      possibly stored for a column.
	private Map<String, EntryLinkInfo>			m_rowEntryLinks;		// A map of column names to EntryLinkInfo's                       possibly stored for a column.
	private Map<String, EntryTitleInfo>			m_rowEntryTitles;		// A map of column names to EntryTitleInfo's                      possibly stored for a column.
	private Map<String, GuestInfo>				m_rowGuests;			// A map of column names to GuestInfo's                           possibly stored for a column.
	private Map<String, List<AssignmentInfo>>	m_rowAssigneeInfos;		// A map of column names to List<AssignmentInfo>'s                possibly stored for a column.
	private Map<String, PrincipalInfo>			m_rowPrincipals;		// A map of column names to PrincipalInfo's                       possibly stored for a column.
	private Map<String, List<TaskFolderInfo>>	m_rowTaskFolderInfos;	// A map of column names to List<TaskFolderInfo>'s                possibly stored for a column.
	private Map<String, ViewFileInfo>			m_rowViewFiles;			// A map of column names to ViewFileInfo's                        possibly stored for a column.
	private Map<String, String>					m_rowStrings;			// A map of column names to String values                         possible stored for a column.
	private String								m_binderIconName;		// Only when the entity type is 'folder' or 'workspace'.
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderRow() {
		// Simply initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param columns
	 */
	public FolderRow(EntityId entityId, List<FolderColumn> columns) {
		// Initialize the class...
		this();

		// ...and store the parameters.
		m_entityId = entityId;
		m_columns  = columns;
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                           getCanModify()                 {                               return m_canModify;          }
	public boolean                           getCanPurge()                  {                               return m_canPurge;           }
	public boolean                           getCanTrash()                  {                               return m_canTrash;           }
	public boolean                           getPinned()                    {                               return m_pinned;             }
	public EntityId                          getEntityId()                  {                               return m_entityId;           }
	public List<FolderColumn>                getColumns()                   {                               return m_columns;            }
	public Map<String, Boolean>              getRowOverdueDates()           {validateMapOverdueDates();     return m_rowOverdueDates;    }
	public Map<String, DescriptionHtml>      getRowDescriptionHtmlMap()     {validateMapDescriptionHtmls(); return m_rowDescriptionHtmls;}
	public Map<String, EmailAddressInfo>     getRowEmailAddressMap()        {validateMapEmailAddresses();   return m_rowEmailAddresses;  }
	public Map<String, EntryEventInfo>       getRowEntryEventMap()          {validateMapEvents();           return m_rowEntryEvents;     }
	public Map<String, EntryLinkInfo>        getRowEntryLinkMap()           {validateMapLinks();            return m_rowEntryLinks;      }
	public Map<String, EntryTitleInfo>       getRowEntryTitlesMap()         {validateMapTitles();           return m_rowEntryTitles;     }
	public Map<String, GuestInfo>            getRowGuestsMap()              {validateMapGuests();           return m_rowGuests;          }
	public Map<String, List<AssignmentInfo>> getRowAssigneeInfoListsMap()   {validateMapAssignees();        return m_rowAssigneeInfos;   }
	public Map<String, PrincipalInfo>        getRowPrincipalsMap()          {validateMapPrincipals();       return m_rowPrincipals;      }
	public Map<String, List<TaskFolderInfo>> getRowTaskFolderInfoListsMap() {validateMapTaskFolders();      return m_rowTaskFolderInfos; } 
	public Map<String, ViewFileInfo>         getRowViewFilesMap()           {validateMapViews();            return m_rowViewFiles;       } 
	public Map<String, String>               getRowStringsMap()             {validateMapStrings();          return m_rowStrings;         }
	public String                            getBinderIconName()            {                               return m_binderIconName;     }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setCanModify(     boolean canModify)      {m_canModify      = canModify;     }
	public void setCanPurge(      boolean canPurge)       {m_canPurge       = canPurge;      }
	public void setCanTrash(      boolean canTrash)       {m_canTrash       = canTrash;      }
	public void setPinned(        boolean pinned)         {m_pinned         = pinned;        }
	public void setBinderIconName(String  binderIconName) {m_binderIconName = binderIconName;}
	
	/**
	 * Stores the value for a specific column.
	 * 
	 * @param fc
	 * @param v
	 */
	public void setColumnValue(FolderColumn fc, Object v) {
		String vk = getValueKey(fc);
		if      (v instanceof String)           {validateMapStrings();          m_rowStrings.put(         vk, ((String)           v));}
		else if (v instanceof DescriptionHtml)  {validateMapDescriptionHtmls(); m_rowDescriptionHtmls.put(vk, ((DescriptionHtml)  v));}
		else if (v instanceof EmailAddressInfo) {validateMapEmailAddresses();   m_rowEmailAddresses.put(  vk, ((EmailAddressInfo) v));}
		else if (v instanceof EntryEventInfo)   {validateMapEvents();           m_rowEntryEvents.put(     vk, ((EntryEventInfo)   v));}
		else if (v instanceof EntryLinkInfo)    {validateMapLinks();            m_rowEntryLinks.put(      vk, ((EntryLinkInfo)    v));}
		else if (v instanceof EntryTitleInfo)   {validateMapTitles();           m_rowEntryTitles.put(     vk, ((EntryTitleInfo)   v));}
		else if (v instanceof GuestInfo)        {validateMapGuests();           m_rowGuests.put(          vk, ((GuestInfo)        v));}
		else if (v instanceof PrincipalInfo)    {validateMapPrincipals();       m_rowPrincipals.put(      vk, ((PrincipalInfo)    v));}
		else if (v instanceof ViewFileInfo)     {validateMapViews();            m_rowViewFiles.put(       vk, ((ViewFileInfo)     v));}
		else                                    {validateMapStrings();          m_rowStrings.put(         vk, v.toString());          }
	}
	
	public void setColumnValue_AssignmentInfos(FolderColumn fc, List<AssignmentInfo> aiList) {
		validateMapAssignees(); 
		m_rowAssigneeInfos.put(getValueKey(fc), aiList);		
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
					DescriptionHtml dh = ((null == m_rowDescriptionHtmls) ? null : m_rowDescriptionHtmls.get(vk));
					if (null != dh) {
						reply = dh.getDescription();
					}
					else {
						EmailAddressInfo emai = ((null == m_rowEmailAddresses) ? null : m_rowEmailAddresses.get(vk));
						if (null != emai) {
							reply = emai.getEmailAddress(); 
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
	
	/*
	 * Validates that the various Maps have been defined.
	 */
	private void validateMapAssignees()        {if (null == m_rowAssigneeInfos)		m_rowAssigneeInfos		= new HashMap<String, List<AssignmentInfo>>();}
	private void validateMapOverdueDates()     {if (null == m_rowOverdueDates)  	m_rowOverdueDates		= new HashMap<String, Boolean>();             }
	private void validateMapDescriptionHtmls() {if (null == m_rowDescriptionHtmls)	m_rowDescriptionHtmls	= new HashMap<String, DescriptionHtml>();     }
	private void validateMapEmailAddresses()   {if (null == m_rowEmailAddresses)	m_rowEmailAddresses		= new HashMap<String, EmailAddressInfo>();    }
	private void validateMapEvents()           {if (null == m_rowEntryEvents)		m_rowEntryEvents		= new HashMap<String, EntryEventInfo>();      }
	private void validateMapGuests()           {if (null == m_rowGuests)		    m_rowGuests			    = new HashMap<String, GuestInfo>();           }
	private void validateMapLinks()            {if (null == m_rowEntryLinks)		m_rowEntryLinks			= new HashMap<String, EntryLinkInfo>();       }
	private void validateMapPrincipals()       {if (null == m_rowPrincipals)		m_rowPrincipals			= new HashMap<String, PrincipalInfo>();       }
	private void validateMapTaskFolders()      {if (null == m_rowTaskFolderInfos)	m_rowTaskFolderInfos	= new HashMap<String, List<TaskFolderInfo>>();}
	private void validateMapTitles()           {if (null == m_rowEntryTitles)		m_rowEntryTitles		= new HashMap<String, EntryTitleInfo>();      }
	private void validateMapStrings()          {if (null == m_rowStrings)			m_rowStrings			= new HashMap<String, String>();              }
	private void validateMapViews()            {if (null == m_rowViewFiles)			m_rowViewFiles			= new HashMap<String, ViewFileInfo>();        }
}
