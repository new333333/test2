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

import java.util.List;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate information about folder columns between
 * the client and server.
 * 
 * @author drfoster@novell.com
 */
public class FolderColumn implements IsSerializable, VibeRpcResponseData {
	private Boolean			m_columnShown;			//
	private boolean			m_columnSortable;		//
	private transient int	m_displayIndex;			// The index of the column in the browser's display (i.e., the index of the column's <TD> in the <TR>.)
	private String			m_columnName;			// For custom attributes this is 'defId,type,eleName,caption'.
	private String			m_columnTitle;			//
	private String			m_columnDefaultTitle;	//
	private String			m_columnCustomTitle;	//
	private String			m_columnSearchKey;		//
	private String			m_columnSortKey;		//
	
	private String			m_columnDefId;			// The definition ID for this column (only used for custom columns.)
	private String			m_columnEleName;		// The element name from the definition (if custom attribute), otherwise, it is the column name.
	private String			m_columnType;			// The type for this column (only used for custom columns.)

	// The following are the various predefined names used for columns.
	public final static String COLUMN_ADMIN_RIGHTS					= "adminRights";
	public final static String COLUMN_ADMINISTRATOR					= "administrator";
	public final static String COLUMN_AUTHOR						= "author";
	public final static String COLUMN_CAN_ONLY_SEE_MEMBERS			= "canOnlySeeMembers";
	public final static String COLUMN_COMMENTS						= "comments";
	public final static String COLUMN_DATE							= "date";
	public final static String COLUMN_DESCRIPTION					= "description";
	public final static String COLUMN_DESCRIPTION_HTML				= "descriptionHtml";
	public final static String COLUMN_DEVICE_DESCRIPTION			= "deviceDescription";
	public final static String COLUMN_DEVICE_LAST_LOGIN				= "deviceLastLogin";
	public final static String COLUMN_DEVICE_USER					= "deviceUser";
	public final static String COLUMN_DEVICE_WIPE_DATE				= "deviceWipeDate";
	public final static String COLUMN_DEVICE_WIPE_SCHEDULED			= "deviceWipeScheduled";
	public final static String COLUMN_DOCNUMBER						= "docNum";
	public final static String COLUMN_DOWNLOAD						= "download";
	public final static String COLUMN_DUE_DATE						= "dueDate";
	public final static String COLUMN_EMAIL_ADDRESS					= "emailAddress";
	public final static String COLUMN_FAMILY						= "family";
	public final static String COLUMN_FULL_NAME						= "fullName";
	public final static String COLUMN_GUEST							= "guest";
	public final static String COLUMN_HTML							= "html";
	public final static String COLUMN_LIMITED_VISIBILITY_USER		= "limitedVisibilityUser";
	public final static String COLUMN_LOCATION						= "location";
	public final static String COLUMN_LOGIN_ID						= "loginId";
	public final static String COLUMN_MOBILE_DEVICES				= "mobileDevices";
	public final static String COLUMN_NETFOLDER_ACCESS				= "netfolder_access";
	public final static String COLUMN_PRINCIPAL_TYPE				= "principalType";
	public final static String COLUMN_PROXY_NAME					= "proxyName";
	public final static String COLUMN_RATING						= "rating";
	public final static String COLUMN_RESPONSIBLE					= "responsible";
	public final static String COLUMN_SHARE_ACCESS					= "share_access";
	public final static String COLUMN_SHARE_DATE					= "share_date";
	public final static String COLUMN_SHARE_EXPIRATION				= "share_expiration";
	public final static String COLUMN_SHARE_MESSAGE					= "share_message";
	public final static String COLUMN_SHARE_SHARED_BY				= "share_sharedBy";
	public final static String COLUMN_SHARE_SHARED_WITH				= "share_sharedWith";
	public final static String COLUMN_SIZE							= "size";
	public final static String COLUMN_STATE							= "state";
	public final static String COLUMN_STATUS						= "status";
	public final static String COLUMN_TASKS							= "tasks";
	public final static String COLUMN_TEAM_MEMBERS					= "teamMembers";
	public final static String COLUMN_TITLE							= "title";
	
	// The following are the various internal names used for columns.
	public final static String COLUMN_PAD							= "--pad--";
	public final static String COLUMN_PIN							= "--pin--";
	public final static String COLUMN_SELECT						= "--select--";
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public FolderColumn() {
		// Initialize the super class...
		super();
		
		// ...and anything else.
		m_columnSortable = true;
	}

	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 */
	public FolderColumn(String columnName) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setColumnName( columnName );
		setColumnTitle(columnName);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 */
	public FolderColumn(String columnName, String columnTitle) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setColumnName( columnName );
		setColumnTitle(columnTitle);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 * @param columnSearchKey
	 * @param columnSortKey
	 */
	public FolderColumn(String columnName, String columnTitle, String columnSearchKey, String columnSortKey) {
		// Initialize this object...
		this(columnName, columnTitle);
		
		// ...and store the parameters.
		setColumnSearchKey(columnSearchKey);
		setColumnSortKey(  columnSortKey  );
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param columnName
	 * @param columnTitle
	 * @param columnSearchKey
	 * @param columnSortKey
	 * @param columnDefId
	 * @param columnType
	 */
	public FolderColumn(String columnName, String columnTitle, String columnSearchKey, String columnSortKey, String columnDefId, String columnType) {
		// Initialize this object...
		this(columnName, columnTitle, columnSearchKey, columnSortKey);
		
		// ...and store the parameters.
		setColumnDefId(columnDefId);
		setColumnType( columnType );
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Boolean isColumnShown()         {return m_columnShown;       }
	public boolean isColumnSortable()      {return m_columnSortable;    }
	public int     getDisplayIndex()       {return m_displayIndex;      }
	public String  getColumnName()         {return m_columnName;        }
	public String  getColumnTitle()        {return m_columnTitle;       }
	public String  getColumnDefaultTitle() {return m_columnDefaultTitle;}
	public String  getColumnCustomTitle()  {return m_columnCustomTitle; }
	public String  getColumnSearchKey()    {return m_columnSearchKey;   }
	public String  getColumnType()         {return m_columnType;        }
	public String  getColumnDefId()        {return m_columnDefId;       }
	public String  getColumnSortKey() {
		String reply = m_columnSortKey;
		if ((null == reply) || (0 == reply.length())) {
			reply = m_columnSearchKey;
		}
		return reply;
	}
	public String getColumnEleName() {
		if ((null != m_columnEleName) && (0 < m_columnEleName.length())) {
			return m_columnEleName;
		}
		
		String eleName = m_columnName;
		if (eleName.contains(",")) {
			// This is a custom attribute structured as
			// 'defId,type,eleName,caption'.
			String[] colParts = eleName.split(",");
			eleName = colParts[2];
		}
		return eleName;
	}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setColumnShown(       Boolean columnShown)        {m_columnShown        = columnShown;       }
	public void setColumnSortable(    boolean columnSortable)     {m_columnSortable     = columnSortable;    }
	public void setDisplayIndex(      int     displayIndex)       {m_displayIndex       = displayIndex;      }
	public void setColumnName(        String  columnName)      	  {m_columnName         = columnName;        }
	public void setColumnTitle(       String  columnTitle)     	  {m_columnTitle        = columnTitle;       }
	public void setColumnDefaultTitle(String  columnDefaultTitle) {m_columnDefaultTitle = columnDefaultTitle;}
	public void setColumnCustomTitle( String  columnCustomTitle)  {m_columnCustomTitle  = columnCustomTitle; }
	public void setColumnSearchKey(   String  columnSearchKey) 	  {m_columnSearchKey    = columnSearchKey;   }
	public void setColumnSortKey(     String  columnSortKey)   	  {m_columnSortKey      = columnSortKey;     }
	public void setColumnDefId(       String  columnDefId)     	  {m_columnDefId        = columnDefId;       }
	public void setColumnEleName(     String  columnEleName)      {m_columnEleName      = columnEleName;     }
	public void setColumnType(        String  columnType)         {m_columnType         = columnType;        }

	/**
	 * Returns true if the columns is a custom column (i.e., has a
	 * definition ID) and false otherwise.
	 * 
	 * @return
	 */
	public boolean isCustomColumn() {
		return ((null != m_columnDefId) && (0 < m_columnDefId.length()));
	}
	
	/**
	 * Various column type detectors.
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static boolean isColumnAdminRights(          String       columnName) {return columnName.equals(FolderColumn.COLUMN_ADMIN_RIGHTS);           }
	public static boolean isColumnAdministrator(        String       columnName) {return columnName.equals(FolderColumn.COLUMN_ADMINISTRATOR);          }
	public static boolean isColumnAccess(               String       columnName) {return columnName.equals(FolderColumn.COLUMN_SHARE_ACCESS);           }
	public static boolean isColumnCanOnlySeeMembers(    String       columnName) {return columnName.equals(FolderColumn.COLUMN_CAN_ONLY_SEE_MEMBERS);   }
	public static boolean isColumnComments(             String       columnName) {return columnName.equals(FolderColumn.COLUMN_COMMENTS);               }
	public static boolean isColumnCustom(               FolderColumn column)     {return column.isCustomColumn();                                       }
	public static boolean isColumnDescriptionHtml(      String       columnName) {return columnName.equals(FolderColumn.COLUMN_DESCRIPTION_HTML);       }
	public static boolean isColumnDeviceDescription(    String       columnName) {return columnName.equals(FolderColumn.COLUMN_DEVICE_DESCRIPTION);     }
	public static boolean isColumnDeviceLastLogin(      String       columnName) {return columnName.equals(FolderColumn.COLUMN_DEVICE_LAST_LOGIN);      }
	public static boolean isColumnDeviceUser(           String       columnName) {return columnName.equals(FolderColumn.COLUMN_DEVICE_USER);            }
	public static boolean isColumnDeviceWipeDate(       String       columnName) {return columnName.equals(FolderColumn.COLUMN_DEVICE_WIPE_DATE);       }
	public static boolean isColumnDeviceWipeScheduled(  String       columnName) {return columnName.equals(FolderColumn.COLUMN_DEVICE_WIPE_SCHEDULED);  }
	public static boolean isColumnDownload(             String       columnName) {return columnName.equals(FolderColumn.COLUMN_DOWNLOAD);               }
	public static boolean isColumnEmailAddress(         String       columnName) {return columnName.equals(FolderColumn.COLUMN_EMAIL_ADDRESS);          }
	public static boolean isColumnFamily(               String       columnName) {return columnName.equals(FolderColumn.COLUMN_FAMILY);                 }
	public static boolean isColumnFullName(             String       columnName) {return columnName.equals(FolderColumn.COLUMN_FULL_NAME);              }
	public static boolean isColumnGuest(                String       columnName) {return columnName.equals(FolderColumn.COLUMN_GUEST);                  }
	public static boolean isColumnLimitedVisibilityUser(String       columnName) {return columnName.equals(FolderColumn.COLUMN_LIMITED_VISIBILITY_USER);}
	public static boolean isColumnLoginId(              String       columnName) {return columnName.equals(FolderColumn.COLUMN_LOGIN_ID);               }
	public static boolean isColumnMobileDevices(        String       columnName) {return columnName.equals(FolderColumn.COLUMN_MOBILE_DEVICES);         }
	public static boolean isColumnNetFolderAccess(      String       columnName) {return columnName.equals(FolderColumn.COLUMN_NETFOLDER_ACCESS);       }
	public static boolean isColumnRating(               String       columnName) {return columnName.equals(FolderColumn.COLUMN_RATING);                 }
	public static boolean isColumnPresence(             String       columnName) {return columnName.equals(FolderColumn.COLUMN_AUTHOR);                 }
	public static boolean isColumnPrincipalType(        String       columnName) {return columnName.equals(FolderColumn.COLUMN_PRINCIPAL_TYPE);         }
	public static boolean isColumnProxyName(            String       columnName) {return columnName.equals(FolderColumn.COLUMN_PROXY_NAME);             }
	public static boolean isColumnTaskFolders(          String       columnName) {return columnName.equals(FolderColumn.COLUMN_TASKS);                  }
	public static boolean isColumnTeamMembers(          String       columnName) {return columnName.equals(FolderColumn.COLUMN_TEAM_MEMBERS);           }
	public static boolean isColumnTitle(                String       columnName) {return columnName.equals(FolderColumn.COLUMN_TITLE);                  }
	public static boolean isColumnView(                 String       columnName) {return columnName.equals(FolderColumn.COLUMN_HTML);                   }
	public static boolean isColumnSharedBy(             String       columnName) {return columnName.equals(FolderColumn.COLUMN_SHARE_SHARED_BY);        }
	public static boolean isColumnSharedWith(           String       columnName) {return columnName.equals(FolderColumn.COLUMN_SHARE_SHARED_WITH);      }
	public static boolean isColumnShareMessage(         String       columnName) {return columnName.equals(FolderColumn.COLUMN_SHARE_MESSAGE);          }
	public static boolean isColumnShareStringValue(     String       columnName) {
		return
			(columnName.equals(FolderColumn.COLUMN_SHARE_ACCESS)     ||
			 columnName.equals(FolderColumn.COLUMN_SHARE_DATE)       ||
			 columnName.equals(FolderColumn.COLUMN_SHARE_EXPIRATION) ||
			 columnName.equals(FolderColumn.COLUMN_SHARE_MESSAGE));
	}

	/**
	 * Returns the FolderColumn from a List<FolderColumn> that matches
	 * the element name.  If one can't be found, null is returned.
	 * 
	 * @param columns
	 * @param name
	 * 
	 * @return
	 */
	public static FolderColumn getFolderColumnByEleName(List<FolderColumn> columns, String eleName) {
		if ((null != columns) && (!(columns.isEmpty())) && (null != eleName) && (0 < eleName.length())) {
			for (FolderColumn column:  columns) {
				String cen = column.getColumnEleName();
				if ((null != cen) && cen.equals(eleName)) {
					return column;
				}
			}
		}
		return null;
	}
}
