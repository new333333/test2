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
	private boolean					m_overridePresets;		// true -> These settings override the presets.  false -> They don't.
	private List<EmailAddressInfo>	m_emailAddresses;		// The list of email addresses defined for the current user.
	private List<String>			m_digestAddresses;		// List of email addresses used for digests,                                 if any.
	private List<String>			m_msgAddresses;			// List of email addresses used for individual messages,                     if any.
	private List<String>			m_msgNoAttAddresses;	// List of email addresses used for individual messages without attachments, if any.
	private List<String>			m_textAddresses;		// List of email addresses used for text messaging,                          if any.
	private String					m_bannerHelpUrl;		// URL for the help to launch with the button on the dialog's banner. 
	private String					m_overrideHelpUrl;		// URL for the help to launch with the button next to the override checkbox.
	private String					m_singleEntityIconUrl;	//
	private String					m_singleEntityPath;		//
	private String					m_singleEntityTitle;	//
	
	/**
	 * Inner class used to represent an email address.
	 */
	public static class EmailAddressInfo implements IsSerializable {
		private String m_ema;	// The email address itself.
		private String m_type;	// The type of this email address.
		
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
		 * @param ema
		 */
		public EmailAddressInfo(String type, String ema) {
			// Initialize this object...
			this();

			// ...and store the parameters.
			setType(   type);
			setAddress(ema );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getAddress() {return m_ema; }
		public String getType()    {return m_type;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setAddress(String ema)  {m_ema  = ema; }
		public void setType(   String type) {m_type = type;}
	}
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public EmailNotificationInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_emailAddresses    = new ArrayList<EmailAddressInfo>();
		m_digestAddresses   = new ArrayList<String>();
		m_msgAddresses      = new ArrayList<String>();
		m_msgNoAttAddresses = new ArrayList<String>();
		m_textAddresses     = new ArrayList<String>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                getOverridePresets()     {return m_overridePresets;    }
	public List<EmailAddressInfo> getEmailAddresses()      {return m_emailAddresses;     }
	public List<String>           getDigestAddresses()     {return m_digestAddresses;    }
	public List<String>           getMsgAddresses()        {return m_msgAddresses;       }
	public List<String>           getMsgNoAttAddresses()   {return m_msgNoAttAddresses;  }
	public List<String>           getTextAddresses()       {return m_textAddresses;      }
	public String                 getBannerHelpUrl()       {return m_bannerHelpUrl;      }
	public String                 getOverrideHelpUrl()     {return m_overrideHelpUrl;    }
	public String                 getSingleEntityIconUrl() {return m_singleEntityIconUrl;}
	public String                 getSingleEntityPath()    {return m_singleEntityPath;   }
	public String                 getSingleEntityTitle()   {return m_singleEntityTitle;  }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setOverridePresets(    boolean                overridePresets)     {m_overridePresets     = overridePresets;    }
	public void setEmailAddresses(     List<EmailAddressInfo> emailAddresses)      {m_emailAddresses      = emailAddresses;     }
	public void setDigestAddresses(    List<String>           digestAddresses)     {m_digestAddresses     = digestAddresses;    }
	public void setMsgAddresses(       List<String>           msgAddresses)        {m_msgAddresses        = msgAddresses;       }
	public void setMsgNoAttAddresses(  List<String>           msgNoAttAddresses)   {m_msgNoAttAddresses   = msgNoAttAddresses;  }
	public void setTextAddresses(      List<String>           textAddresses)       {m_textAddresses       = textAddresses;      }
	public void setBannerHelpUrl(      String                 bannerHelpUrl)       {m_bannerHelpUrl       = bannerHelpUrl;      }
	public void setOverrideHelpUrl(    String                 overrideHelpUrl)     {m_overrideHelpUrl     = overrideHelpUrl;    }
	public void setSingleEntityIconUrl(String                 singleEntityIconUrl) {m_singleEntityIconUrl = singleEntityIconUrl;}
	public void setSingleEntityPath(   String                 singleEntityPath)    {m_singleEntityPath    = singleEntityPath;   }
	public void setSingleEntityTitle(  String                 singleEntityTitle)   {m_singleEntityTitle   = singleEntityTitle;  }

	/**
	 * Adds an email address to the list of email addresses.
	 * 
	 * @param type
	 * @param ema
	 */
	public void addEmailAddress(String type, String ema) {
		// Always use the alternate form of the method.
		addEmailAddress(new EmailAddressInfo(type, ema));
	}

	/**
	 * Adds an email address to a list of email addresses.
	 * 
	 * @param
	 */
	public void addEmailAddress(   EmailAddressInfo ema) {m_emailAddresses.add(   ema);}
	public void addDigestAddress(  String           ema) {m_digestAddresses.add(  ema);}
	public void addMsgAddress(     String           ema) {m_msgAddresses.add(     ema);}
	public void addMsgNoAttAddress(String           ema) {m_msgNoAttAddresses.add(ema);}
	public void addTextAddress(    String           ema) {m_textAddresses.add(    ema);}
}
