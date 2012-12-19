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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.PerUserShareRightsInfo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for commands that return
 * the properties required to manage a user.
 * 
 * @author drfoster@novell.com
 */
public class UserPropertiesRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private AccountInfo						m_accountInfo;		//
	private HomeInfo						m_homeInfo;			//
	private NetFoldersInfo					m_netFoldersInfo;	//
	private PerUserShareRightsInfo			m_sharingRights;	//
	private ProfileEntryInfoRpcResponseData	m_profile;			//
	private QuotaInfo						m_quotaInfo;		//

	/**
	 * Inner class used to encapsulate information about the user's
	 * account.
	 */
	public static class AccountInfo implements IsSerializable {
		private boolean	m_fromLdap;			//
		private boolean	m_fromLocal;		//
		private boolean	m_fromOpenId;		//
		private boolean	m_hasAdHocFolders;	//
		private boolean	m_internal;			//
		private String	m_eDirContainer;	//
		private String	m_lastLogin;		//
		private String	m_ldapDN;			//
		private String	m_loginId;			//
		
		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public AccountInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isFromLdap()       {return m_fromLdap;       }
		public boolean isFromLocal()      {return m_fromLocal;      }
		public boolean isFromOpenId()     {return m_fromOpenId;     }
		public boolean hasAdHocFolders()  {return m_hasAdHocFolders;}
		public boolean isInternal()       {return m_internal;       }
		public String  getEDirContainer() {return m_eDirContainer;  }
		public String  getLastLogin()     {return m_lastLogin;      }
		public String  getLdapDN()        {return m_ldapDN;         }
		public String  getLoginId()       {return m_loginId;        }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setFromLdap(       boolean fromLdap)        {m_fromLdap        = fromLdap;       }
		public void setFromLocal(      boolean fromLocal)       {m_fromLocal       = fromLocal;      }
		public void setFromOpenId(     boolean fromOpenId)      {m_fromOpenId      = fromOpenId;     }
		public void setHasAdHocFolders(boolean hasAdHocFolders) {m_hasAdHocFolders = hasAdHocFolders;}
		public void setInternal(       boolean internal)        {m_internal        = internal;       }
		public void setEDirContainer(  String  eDirContainer)   {m_eDirContainer   = eDirContainer;  }
		public void setLastLogin(      String  lastLogin)       {m_lastLogin       = lastLogin;      }
		public void setLdapDN(         String  ldapDN)          {m_ldapDN          = ldapDN;         }
		public void setLoginId(        String  loginId)         {m_loginId         = loginId;        }
	}
	
	/**
	 * Inner class used to encapsulate information about a user's home
	 * folder.
	 */
	public static class HomeInfo implements IsSerializable {
		private Long	m_id;			//
		private String	m_relativePath;	//
		private	String	m_rootPath;		//
		
		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public HomeInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Long   getId()           {return m_id;          }
		public String getRelativePath() {return m_relativePath;}
		public String getRootPath()     {return m_rootPath;    }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setId(          Long   id)           {m_id           = id;          }
		public void setRelativePath(String relativePath) {m_relativePath = relativePath;}
		public void setRootPath(    String path)         {m_rootPath     = path;        }
	}

	/**
	 * Inner class used to encapsulate information about the user's net
	 * folders.
	 */
	public static class NetFoldersInfo implements IsSerializable {
		private boolean					m_canManageNetFolders;	//
		private List<EntryTitleInfo>	m_netFolders;			//
		
		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public NetFoldersInfo() {
			// Initialize the super class...
			super();
			
			// ...and initialize anything else that requires it.
			setNetFolders(new ArrayList<EntryTitleInfo>());
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean              canManageNetFolders() {return m_canManageNetFolders;}
		public List<EntryTitleInfo> getNetFolders()       {return m_netFolders;         }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setCanManageNetFolders(boolean              canManageNetFolders) {m_canManageNetFolders = canManageNetFolders;}
		public void setNetFolders(         List<EntryTitleInfo> netFolders)          {m_netFolders          = netFolders;         }

		/**
		 * Adds an EntryTitleInfo for a Net Folder to the list of those
		 * being tracked.
		 * 
		 * @param netFolder
		 */
		public void addNetFolder(EntryTitleInfo netFolder) {
			m_netFolders.add(netFolder);
		}
	}
	
	/**
	 * Inner class used to encapsulate information about a user's quota
	 * settings.
	 */
	public static class QuotaInfo implements IsSerializable {
		private boolean m_groupQuota;		//
		private boolean m_zoneQuota;		//
		private long	m_userQuota;		//
		private String	m_manageQuotasUrl;	//

		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public QuotaInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isGroupQuota()       {return m_groupQuota;     }
		public boolean isZoneQuota()        {return m_zoneQuota;      }
		public long    getUserQuota()       {return m_userQuota;      }
		public String  getManageQuotasUrl() {return m_manageQuotasUrl;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setGroupQuota(     boolean groupQuota)      {m_groupQuota      = groupQuota;     }
		public void setZoneQuota(      boolean zoneQuota)       {m_zoneQuota       = zoneQuota;      }
		public void setUserQuota(      long    userQuota)       {m_userQuota       = userQuota;      }
		public void setManageQuotasUrl(String  manageQuotasUrl) {m_manageQuotasUrl = manageQuotasUrl;}
	}

	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public UserPropertiesRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param profile
	 */
	public UserPropertiesRpcResponseData(ProfileEntryInfoRpcResponseData profile) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setProfile(profile);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public AccountInfo                     getAccountInfo()    {return m_accountInfo;        }
	public boolean                         hasHome()           {return (null != m_homeInfo); }
	public boolean                         hasQuota()          {return (null != m_quotaInfo);}
	public HomeInfo                        getHomeInfo()       {return m_homeInfo;           }
	public NetFoldersInfo                  getNetFoldersInfo() {return m_netFoldersInfo;     }
	public PerUserShareRightsInfo          getSharingRights()  {return m_sharingRights;      }
	public ProfileEntryInfoRpcResponseData getProfile()        {return m_profile;            }
	public QuotaInfo                       getQuotaInfo()      {return m_quotaInfo;          }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setAccountInfo(    AccountInfo                     accountInfo)    {m_accountInfo    = accountInfo;   }
	public void setHomeInfo(       HomeInfo                        homeInfo)       {m_homeInfo       = homeInfo;      }
	public void setNetFoldersInfo( NetFoldersInfo                  netFoldersInfo) {m_netFoldersInfo = netFoldersInfo;}
	public void setSharingRights(  PerUserShareRightsInfo          sharingRights)  {m_sharingRights  = sharingRights; }
	public void setProfile(        ProfileEntryInfoRpcResponseData profile)        {m_profile        = profile;       }
	public void setQuotaInfo(      QuotaInfo                       quotaInfo)      {m_quotaInfo      = quotaInfo;     }
}
