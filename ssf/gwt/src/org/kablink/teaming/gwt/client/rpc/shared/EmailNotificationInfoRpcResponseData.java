/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
 * This class holds the response data for a request for email
 * notification information.
 * 
 * @author drfoster@novell.com
 */
public class EmailNotificationInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean					m_overridePresets;	// true -> These settings override the presets.  false -> They don't.
	private List<EmailAddressInfo>	m_emailAddresses;	// The list of email addresses defined for the current user.
	private String					m_bannerHelpUrl;	// 
	private String					m_digestAddress;	// User's digest                                  email address, if any.
	private String					m_msgAddress;		// User's individual messages                     email address, if any.
	private String					m_msgNoAttAddress;	// User's individual messages without attachments email address, if any.
	private String					m_overrideHelpUrl;	//
	private String					m_textAddress;		// User's text messaging                          email address, if any.
	
	/**
	 * Inner class used to represent an email address.
	 */
	public static class EmailAddressInfo implements IsSerializable {
		private String m_type;		//
		private String m_address;	//
		
		/**
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter
		 * constructor.
		 */
		public EmailAddressInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param type
		 * @param address
		 */
		public EmailAddressInfo(String type, String address) {
			// Initialize this object...
			this();

			// ...and store the parameters.
			setType(   type   );
			setAddress(address);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getType()    {return m_type;   }
		public String getAddress() {return m_address;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setType(   String type)    {m_type    = type;   }
		public void setAddress(String address) {m_address = address;}
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param emailAddresses
	 */
	public EmailNotificationInfoRpcResponseData(List<EmailAddressInfo> emailAddresses) {
		// Initialize the superclass..
		super();
		
		// ...and store the parameters.
		setEmailAddresses(emailAddresses);
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public EmailNotificationInfoRpcResponseData() {
		// Always use the initial form of the constructor.
		this(new ArrayList<EmailAddressInfo>());
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                getOverridePresets() {return m_overridePresets;}
	public List<EmailAddressInfo> getEmailAddresses()  {return m_emailAddresses; }
	public String                 getBannerHelpUrl()   {return m_bannerHelpUrl;  }
	public String                 getDigestAddress()   {return m_digestAddress;  }
	public String                 getMsgAddress()      {return m_msgAddress;     }
	public String                 getMsgNoAttAddress() {return m_msgNoAttAddress;}
	public String                 getOverrideHelpUrl() {return m_overrideHelpUrl;}
	public String                 getTextAddress()     {return m_textAddress;    }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setOverridePresets( boolean                overridePresets) {m_overridePresets = overridePresets;}
	public void setEmailAddresses(  List<EmailAddressInfo> emailAddresses)  {m_emailAddresses  = emailAddresses; }
	public void setBannerHelpUrl(   String                 bannerHelpUrl)   {m_bannerHelpUrl   = bannerHelpUrl;  }
	public void setDigestAddress(   String                 digestAddress)   {m_digestAddress   = digestAddress;  }
	public void setMsgAddress(      String                 msgAddress)      {m_msgAddress      = msgAddress;     }
	public void setMsgNoAttAddress( String                 msgNoAttAddress) {m_msgNoAttAddress = msgNoAttAddress;}
	public void setOverrideHelpUrl( String                 overrideHelpUrl) {m_overrideHelpUrl = overrideHelpUrl;}
	public void setTextAddress(     String                 textAddress)     {m_textAddress     = textAddress;    }

	/**
	 * Adds an email address to the list of email addresses.
	 * 
	 * @param emailAddress
	 */
	public void addEmailAddress(EmailAddressInfo emailAddress) {
		m_emailAddresses.add(emailAddress);
	}
	
	/**
	 * Adds an email address to the list of email addresses.
	 * 
	 * @param type
	 * @param address
	 */
	public void addEmailAddress(String type, String address) {
		m_emailAddresses.add(new EmailAddressInfo(type, address));
	}
}
