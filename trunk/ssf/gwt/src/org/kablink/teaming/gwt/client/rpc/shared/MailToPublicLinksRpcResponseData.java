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
 * This class holds the response data for the 'get mail to public
 * links' command.
 * 
 * @author drfoster@novell.com
 */
public class MailToPublicLinksRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private String						m_error;				//
	private String						m_subject;				//
	private List<MailToPublicLinkInfo>	m_mailToPublicLinks;	//

	/**
	 * Inner class used to encapsulate the public links of a file.
	 */
	public static class MailToPublicLinkInfo implements IsSerializable {
		private String	m_comment;		//
		private String	m_downloadUrl;	//
		private String	m_expiration;	//
		private String	m_sharedOn;		//
		private String	m_viewUrl;		//

		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public MailToPublicLinkInfo() {
			// Initialize the super class.
			super();
		}

		/**
		 * Constructor method.
		 *
		 * @param downloadUrl
		 * @param viewUrl
		 * @param comment
		 * @param sharedOn
		 * @param expiration
		 */
		public MailToPublicLinkInfo(String downloadUrl, String viewUrl, String comment, String sharedOn, String expiration) {
			// Initialize the this object...
			this();
			
			// ...and store the parameters.
			setDownloadUrl(downloadUrl);
			setViewUrl(    viewUrl    );
			setComment(    comment    );
			setExpiration( expiration );
			setSharedOn(   sharedOn   );
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getComment()     {return m_comment;    }
		public String getDownloadUrl() {return m_downloadUrl;}
		public String getExpiration()  {return m_expiration; }
		public String getSharedOn()    {return m_sharedOn;   }
		public String getViewUrl()     {return m_viewUrl;    }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setComment(    String comment)     {m_comment     = comment;    }
		public void setDownloadUrl(String downloadUrl) {m_downloadUrl = downloadUrl;}
		public void setExpiration( String expiration)  {m_expiration  = expiration; }
		public void setSharedOn(   String sharedOn)    {m_sharedOn    = sharedOn;   }
		public void setViewUrl(    String viewUrl)     {m_viewUrl     = viewUrl;    }
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public MailToPublicLinksRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
		m_mailToPublicLinks = new ArrayList<MailToPublicLinkInfo>();
	}

	/**
	 * Constructor method.
	 * 
	 * @param subject
	 */
	public MailToPublicLinksRpcResponseData(String subject) {
		// Initialize this object...
		this();

		// ...and store the parameter.
		setSubject(subject);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                    hasError()             {return ((null != m_error) && (0 < m_error.length()));}
	public List<MailToPublicLinkInfo> getMailToPublicLinks() {return m_mailToPublicLinks;                          }
	public String                     getError()             {return m_error;                                      }
	public String                     getSubject()           {return m_subject;                                    }

	/**
	 * Set'er methods.
	 */
	public void setError(  String error)   {m_error   = error;  }
	public void setSubject(String subject) {m_subject = subject;}
	
	/**
	 * Add'er methods.
	 * 
	 * @param
	 */
	public void addMailToPublicLink(MailToPublicLinkInfo pl) {m_mailToPublicLinks.add(pl);}
}
