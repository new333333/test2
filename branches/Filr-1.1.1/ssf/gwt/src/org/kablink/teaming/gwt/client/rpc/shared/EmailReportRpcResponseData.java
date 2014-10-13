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
 * This class holds the response data for the RPCs that return
 * an email report.
 * 
 * @author drfoster@novell.com
 */
public class EmailReportRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<EmailItem> m_emailItems;	//
	
	/**
	 * Inner class used to track each reported item. 
	 */
	public static class EmailItem implements IsSerializable {
		private String m_attachedFiles;	//
		private String m_comment;		//
		private String m_from;			//
		private String m_logStatus;		//
		private String m_logType;		//
		private String m_sendDate;		//
		private String m_subject;		//
		private String m_toAddresses;	//
		
		/**
		 * Constructor method. 
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		public EmailItem() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getAttachedFiles() {return m_attachedFiles;}
		public String getComment()       {return m_comment;      }
		public String getFrom()          {return m_from;         }
		public String getLogStatus()     {return m_logStatus;    }
		public String getLogType()       {return m_logType;      }
		public String getSendDate()      {return m_sendDate;     }
		public String getSubject()       {return m_subject;      }
		public String getToAddresses()   {return m_toAddresses;  }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setAttachedFiles(String attachedFiles) {m_attachedFiles = attachedFiles;}
		public void setComment(      String comment)       {m_comment       = comment;      }
		public void setFrom(         String from)          {m_from          = from;         }
		public void setLogStatus(    String logStatus)     {m_logStatus     = logStatus;    }
		public void setLogType(      String logType)       {m_logType       = logType;      }
		public void setSendDate(     String sendDate)      {m_sendDate      = sendDate;     }
		public void setSubject(      String subject)       {m_subject       = subject;      }
		public void setToAddresses(  String toAddresses)   {m_toAddresses   = toAddresses;  }
	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public EmailReportRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_emailItems = new ArrayList<EmailItem>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public List<EmailItem> getEmailItems() {return m_emailItems;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEmailItems(List<EmailItem> emailItems) {m_emailItems = emailItems;}
	
	/**
	 * Adds an EmailItem to the list of items being tracked.
	 * 
	 * @param emailItem
	 */
	public void addEmailItem(EmailItem emailItem) {
		m_emailItems.add(emailItem);
	}
}
