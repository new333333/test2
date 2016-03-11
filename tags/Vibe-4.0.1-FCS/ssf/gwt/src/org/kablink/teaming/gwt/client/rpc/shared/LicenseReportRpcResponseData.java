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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return a
 * license report.
 * 
 * @author drfoster@novell.com
 */
public class LicenseReportRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private LicenseReleaseInfo		m_releaseInfo;		//
	private List<LicenseItem>		m_licenses;			//
	private List<LicenseStatsItem>	m_licenseStats;		//
	private long					m_externalUsers;	//
	private long					m_registeredUsers;	//
	private long					m_externalDevices;	//
	private long					m_internalDevices;	//
	private String					m_licenseKey;		//
	private String					m_beginDate;		//
	private String					m_endDate;			//
	private String					m_reportDate;		//

	/**
	 * Inner class used to represent an instance of a license.
	 */
	public static class LicenseItem implements IsSerializable {
		private String	m_contact;			//
		private String	m_effectiveEnd;		//
		private String	m_effectiveStart;	//
		private String	m_issued;			//
		private String	m_productTitle;		//
		private String	m_productVersion;	//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public LicenseItem() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getContact()        {return m_contact;       }
		public String getEffectiveEnd()   {return m_effectiveEnd;  }
		public String getEffectiveStart() {return m_effectiveStart;}
		public String getIssued()         {return m_issued;        }
		public String getProductTitle()   {return m_productTitle;  }
		public String getProductVersion() {return m_productVersion;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setContact(       String contact)        {m_contact        = contact;       }
		public void setEffectiveEnd(  String effectiveEnd)   {m_effectiveEnd   = effectiveEnd;  }
		public void setEffectiveStart(String effectiveStart) {m_effectiveStart = effectiveStart;}
		public void setIssued(        String issued)         {m_issued         = issued;        }
		public void setProductTitle(  String productTitle)   {m_productTitle   = productTitle;  }
		public void setProductVersion(String productVersion) {m_productVersion = productVersion;}
	}

	/**
	 * Inner class used to represent release information.
	 */
	public static class LicenseReleaseInfo implements IsSerializable {
		private String m_name;			//
		private String m_releaseInfo;	//
		private String m_version;		//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public LicenseReleaseInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getName()        {return m_name;       }
		public String getReleaseInfo() {return m_releaseInfo;}
		public String getVersion()     {return m_version;    }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setName(       String name)        {m_name        = name;       }
		public void setReleaseInfo(String releaseInfo) {m_releaseInfo = releaseInfo;}
		public void setVersion(    String version)     {m_version     = version;    }
	}
	
	/**
	 * Inner class used to represent an instance of a license
	 * statistic.
	 */
	public static class LicenseStatsItem implements IsSerializable {
		private long	m_checksum;					//
		private long	m_externalUserCount;		//LDAP users
		private long	m_internalUserCount;		//Local internal
		private Long	m_activeUserCount;			//
		private Long	m_openIdUserCount;			//OpenId users
		private Long	m_otherExtUserCount;	    //Self-registered users
		private Boolean	m_guestAccessEnabled;	    //Guest Access Enabled (0 or 1)
		private String	m_id;						//
	    private String	m_snapshotDate;				//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public LicenseStatsItem() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public long   getCheckSum()          		{return m_checksum;         }
		public long   getExternalUserCount() 		{return m_externalUserCount;}
		public long   getInternalUserCount() 		{return m_internalUserCount;}
		public Long   getActiveUserCount()   		{return m_activeUserCount;  }
		public Long   getOpenIdUserCount()   		{return m_openIdUserCount;  }
		public Long   getOtherExtUserCount()        {return m_otherExtUserCount;}
		public Boolean getGuestAccessEnabled()      {return m_guestAccessEnabled;}
		public String getId()                		{return m_id;               } 
		public String getSnapshotDate()      		{return m_snapshotDate;     }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setCheckSum(         long   checksum)          {m_checksum               = checksum;         }
		public void setExternalUserCount(long   externalUserCount) {m_externalUserCount      = externalUserCount;}
		public void setInternalUserCount(long   internalUserCount) {m_internalUserCount      = internalUserCount;}
		public void setActiveUserCount(  Long   activeUserCount)   {m_activeUserCount        = activeUserCount;  }
		public void setOpenIdUserCount(  Long   openIdUserCount)   {m_openIdUserCount        = openIdUserCount;  }
		public void setOtherExtUserCount( Long otherExtUserCount)  {m_otherExtUserCount      = otherExtUserCount;}
		public void setGuestAccessEnabled(Boolean guestAccessEnabled) {m_guestAccessEnabled  = guestAccessEnabled;}
		public void setId(               String id)                {m_id                     = id;               }
		public void setSnapshotDate(     String snapshotDate)      {m_snapshotDate           = snapshotDate;     }
	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public LicenseReportRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_licenses     = new ArrayList<LicenseItem>();
		m_licenseStats = new ArrayList<LicenseStatsItem>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public LicenseReleaseInfo     getReleaseInfo()     {return m_releaseInfo;    }
	public List<LicenseItem>      getLicenses()        {return m_licenses;       }
	public List<LicenseStatsItem> getLicenseStats()    {return m_licenseStats;   }
	public long                   getExternalUsers()   {return m_externalUsers;  }
	public long                   getRegisteredUsers() {return m_registeredUsers;}
	public long                   getExternalDevices() {return m_externalDevices;  }
	public long                   getInternalDevices() {return m_internalDevices;}
	public String                 getLicenseKey()      {return m_licenseKey;     }
	public String                 getBeginDate()       {return m_beginDate;      }
	public String                 getEndDate()         {return m_endDate;        }
	public String                 getReportDate()      {return m_reportDate;     }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setReleaseInfo(    LicenseReleaseInfo     releaseInfo)     {m_releaseInfo     = releaseInfo;    }
	public void setLicenses(       List<LicenseItem>      licenses)        {m_licenses        = licenses;       }
	public void setLicenseStats(   List<LicenseStatsItem> licenseStats)    {m_licenseStats    = licenseStats;   }
	public void setExternalUsers(  long                   externalUsers)   {m_externalUsers   = externalUsers;  }
	public void setRegisteredUsers(long                   registeredUsers) {m_registeredUsers = registeredUsers;}
	public void setLicenseKey(     String                 licenseKey)      {m_licenseKey      = licenseKey;     }
	public void setBeginDate(      String                 beginDate)       {m_beginDate       = beginDate;      }
	public void setEndDate(        String                 endDate)         {m_endDate         = endDate;        }
	public void setReportDate(     String                 reportDate)      {m_reportDate      = reportDate;     }

	/**
	 * Adds a LicenseItem to the list of them being tracked.
	 * 
	 * @param license
	 */
	public void addLicense(LicenseItem license) {
		m_licenses.add(license);
	}

	/**
	 * Adds a LicenseStatsItem to the list of them being tracked.
	 * 
	 * @param licenseStats
	 */
	public void addLicenseStats(LicenseStatsItem licenseStats) {
		m_licenseStats.add(licenseStats);
	}
}
