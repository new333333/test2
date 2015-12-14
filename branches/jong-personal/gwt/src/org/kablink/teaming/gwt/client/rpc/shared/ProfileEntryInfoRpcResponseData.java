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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.LinkedHashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for commands that expect profile
 * entry information.
 * 
 * @author drfoster@novell.com
 */
public class ProfileEntryInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private LinkedHashMap<String, ProfileAttribute>	m_profileEntryInfo;	// A map of profile entry attribute names to ProfileAttribute's.
	private String									m_aboutMeHtml;		// The user's 'About Me' HTML.
	private String									m_avatarUrl;		// The URL of the user's avatar, if they've defined one.
	private String									m_modifyUrl;		// The URL to use to modify the user's profile.
	
	/**
	 * Inner class that encapsulates information about a profile
	 * attribute.
	 */
	public static class ProfileAttribute implements IsSerializable {
		private String	m_attributeCaption;	//
		private String	m_attributeValue;	//
		
		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public ProfileAttribute() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param attributeCaption
		 * @param attributeValue
		 */
		public ProfileAttribute(String attributeCaption, String attributeValue) {
			// Initialize this object...
			this();

			// ...and store the parameters.
			setAttributeCaption(attributeCaption);
			setAttributeValue(  attributeValue  );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getAttributeCaption() {return m_attributeCaption;}
		public String getAttributeValue()   {return m_attributeValue;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setAttributeCaption(String attributeCaption) {m_attributeCaption = attributeCaption;}
		public void setAttributeValue(  String attributeValue)   {m_attributeValue   = attributeValue;  }
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public ProfileEntryInfoRpcResponseData() {
		// Initialize the this object.
		this(new LinkedHashMap<String, ProfileAttribute>());
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param profileEntryInfo
	 */
	public ProfileEntryInfoRpcResponseData(LinkedHashMap<String, ProfileAttribute> profileEntryInfo) {
		// Initialize the super class...
		super();
		
		// ...and store the parameter.
		setProfileEntryInfo(profileEntryInfo);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public LinkedHashMap<String, ProfileAttribute> getProfileEntryInfo() {return m_profileEntryInfo;}
	public String                                  getAboutMeHtml()      {return m_aboutMeHtml;     }
	public String                                  getAvatarUrl()        {return m_avatarUrl;       }
	public String                                  getModifyUrl()        {return m_modifyUrl;       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setProfileEntryInfo(LinkedHashMap<String, ProfileAttribute> profileEntryInfo) {m_profileEntryInfo = profileEntryInfo;}
	public void setAboutMeHtml(     String                                  aboutMeHtml)      {m_aboutMeHtml      = aboutMeHtml;     }
	public void setAvatarUrl(       String                                  avatarUrl)        {m_avatarUrl        = avatarUrl;       }
	public void setModifyUrl(       String                                  modifyUrl)        {m_modifyUrl        = modifyUrl;       }
	
	/**
	 * Adds a ProfileAttribute to the map.
	 * 
	 * @param attributeName
	 * @param pa
	 */
	public void addProfileAttribute(String attributeName, ProfileAttribute pa) {
		m_profileEntryInfo.put(attributeName, pa);
	}
}
