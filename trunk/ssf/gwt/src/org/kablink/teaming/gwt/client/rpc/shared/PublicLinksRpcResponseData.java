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
import java.util.HashMap;
import java.util.Map;

import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.EntityId;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'get public links'
 * command.
 * 
 * @author drfoster@novell.com
 */
public class PublicLinksRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private ErrorListRpcResponseData		m_errors;		//
	private Map<EntityId, PublicLinkInfo>	m_publicLinks;	//

	/**
	 * Inner class used to encapsulate the public links of a file.
	 */
	public static class PublicLinkInfo implements IsSerializable {
		private String	m_downloadUrl;	//
		private String	m_imageUrl;		//
		private String	m_title;		//
		private String	m_viewUrl;		//

		/**
		 * Constructor method.
		 * 
		 * Zero parameter constructor as per GWT serialization
		 * requirements.
		 */
		public PublicLinkInfo() {
			// Initialize the super class.
			super();
		}

		/**
		 * Constructor method.
		 *
		 * @param title
		 * @param imageUrl
		 * @param downloadUrl
		 * @param viewUrl
		 */
		public PublicLinkInfo(String title, String imageUrl, String downloadUrl, String viewUrl) {
			// Initialize the this object...
			this();
			
			// ...and store the parameters.
			setTitle(      title      );
			setImageUrl   (imageUrl   );
			setDownloadUrl(downloadUrl);
			setViewUrl(    viewUrl    );
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param title
		 * @param imageUrl
		 * @param downloadUrl
		 */
		public PublicLinkInfo(String title, String imageUrl, String downloadUrl) {
			// Initialize the this object.
			this(title, imageUrl, downloadUrl, null);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getDownloadUrl() {return m_downloadUrl;}
		public String getImageUrl()    {return m_imageUrl;   }
		public String getTitle()       {return m_title;      }
		public String getViewUrl()     {return m_viewUrl;    }
		
		/**
		 * Set'er methods.
		 * 
		 * @param downloadUrl
		 */
		public void setDownloadUrl(String downloadUrl) {m_downloadUrl = downloadUrl;}
		public void setImageUrl(   String imageUrl)    {m_imageUrl    = imageUrl;   }
		public void setTitle(      String title)       {m_title       = title;      }
		public void setViewUrl(    String viewUrl)     {m_viewUrl     = viewUrl;    }
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public PublicLinksRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and initialize anything else that requires it.
		m_errors      = new ErrorListRpcResponseData(new ArrayList<ErrorInfo>());
		m_publicLinks = new HashMap<EntityId, PublicLinkInfo>();
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean                       hasErrors()                  {return m_errors.hasErrors();           }
	public ErrorListRpcResponseData      getErrors()                  {return m_errors;                       }
	public int                           getErrorCount()              {return m_errors.getErrorCount();       }
	public int                           getTotalMessageCount()       {return m_errors.getTotalMessageCount();}
	public int                           getWarningCount()            {return m_errors.getWarningCount();     }
	public List<ErrorInfo>               getErrorList()               {return m_errors.getErrorList();        }
	public PublicLinkInfo                getPublicLinks(EntityId eid) {return m_publicLinks.get(eid);         }
	public Map<EntityId, PublicLinkInfo> getPublicLinksMap()          {return m_publicLinks;                  }
	
	/**
	 * Add'er methods.
	 * 
	 * @param
	 */
	public void addError(     String   error)                                                                  {m_errors.addError(  error  );                                                 }
	public void addWarning(   String   warning)                                                                {m_errors.addWarning(warning);                                                 }
	public void addPublicLink(EntityId eid, PublicLinkInfo pl)                                                 {m_publicLinks.put(eid, pl);                                                   }
	public void addPublicLink(EntityId eid, String title, String imageUrl, String downloadUrl)                 {addPublicLink(eid,                    title, imageUrl, downloadUrl, null);    }
	public void addPublicLink(EntityId eid, String title, String imageUrl, String downloadUrl, String viewUrl) {addPublicLink(eid, new PublicLinkInfo(title, imageUrl, downloadUrl, viewUrl));}
}
