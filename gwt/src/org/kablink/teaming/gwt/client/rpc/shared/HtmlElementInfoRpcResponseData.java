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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the RPCs that return
 * information about folder view's HTML element <item>'s.
 * 
 * @author drfoster@novell.com
 */
public class HtmlElementInfoRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private List<HtmlElementInfo>	m_userListInfoList;	//
	
	/**
	 * Inner class used to represent an instance of a user_list <item>.
	 */
	public static class HtmlElementInfo implements IsSerializable {
		private String  m_rootTagName;
		private Map<String, String> m_rootAttributes;
		private String	m_caption;			//
		private String	m_customJspHtml;	//
		private String	m_dataName;			//
		private String	m_htmlBottom;		//
		private String	m_htmlTop;			//
		
		/*
		 * Zero parameter constructor method required for GWT
		 * serialization.
		 */
		private HtmlElementInfo() {
			// Initialize the super class.
			super();
		}

		/**
		 * Constructor method.
		 * 
		 * @param caption
		 * @param dataName
		 * @param htmlTop
		 * @param htmlBottom
		 * @param customJspHtml
		 */
		public HtmlElementInfo(String caption, String dataName, String htmlTop, String htmlBottom, String customJspHtml) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setCaption(      caption      );
			setDataName(     dataName     );
			setHtmlTop(      htmlTop      );
			setHtmlBottom(   htmlBottom   );
			setCustomJspHtml(customJspHtml);
		}

		public HtmlElementInfo(String rootTagName, Map<String, String> rootAttributes, String html) {
			this.m_rootTagName = rootTagName;
			this.m_rootAttributes = rootAttributes;
			this.m_htmlTop = html;
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getCaption()       {return m_caption;      }
		public String getCustomJspHtml() {return m_customJspHtml;}
		public String getDataName()      {return m_dataName;     }
		public String getHtmlBottom()    {return m_htmlBottom;   }
		public String getHtmlTop()       {return m_htmlTop;      }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setCaption(      String caption)       {m_caption       = caption;      }
		public void setCustomJspHtml(String customJspHtml) {m_customJspHtml = customJspHtml;}
		public void setDataName(     String dataName)      {m_dataName      = dataName;     }
		public void setHtmlBottom(   String htmlBottom)    {m_htmlBottom    = htmlBottom;   }
		public void setHtmlTop(      String htmlTop)       {m_htmlTop       = htmlTop;      }

		public String getRootTagName() {
			return m_rootTagName;
		}

		public void setRootTagName(String m_rootTagName) {
			this.m_rootTagName = m_rootTagName;
		}

		public Map<String, String> getRootAttributes() {
			return m_rootAttributes;
		}

		public void setRootAttributes(Map<String, String> m_rootAttributes) {
			this.m_rootAttributes = m_rootAttributes;
		}


	}
	
	/**
	 * Constructor method. 
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public HtmlElementInfoRpcResponseData() {
		// Initialize the super class...
		super();
		
		// ...and allocate the List<HtmlElementInfo>.
		setHtmlElementInfoList(new ArrayList<HtmlElementInfo>());
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int                   getHtmlElementInfoListCount() {return m_userListInfoList.size();}
	public List<HtmlElementInfo> getHtmlElementInfoList()      {return m_userListInfoList;       }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setHtmlElementInfoList(List<HtmlElementInfo> userListInfoList) {m_userListInfoList = userListInfoList;}
	
	/**
	 * Adds a HtmlElementInfo object to the List<HtmlElementInfo>'s.
	 * 
	 * @param userListInfo
	 */
	public void addHtmlElementInfo(HtmlElementInfo userListInfo) {
		m_userListInfoList.add(userListInfo);
	}
}
