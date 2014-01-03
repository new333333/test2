/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for retrieving history
 * information from the tracked history cache.
 * 
 * @author drfoster@novell.com
 */
public class HistoryInfo implements IsSerializable, VibeRpcResponseData {
	private CollectionType				m_selectedMastheadCollection;	// The collection type selected in the masthead when the HistoryInfo was created.
	private HistoryActivityStreamInfo	m_asInfo;						// Refers to the HistoryActivityStreamInfo when m_itemType is ACTIVITY_STREAM.
	private HistoryAdminActionInfo		m_aaInfo;						// Refers to the HistoryAdminActionInfo when m_itemType is ADMIN_ACTION.
	private HistoryItemType				m_itemType;						// The type of HistoryInfo this object represents.
	private HistoryUrlInfo				m_urlInfo;						// Refers to the HistoryUrlInfo when m_itemType is URL.

	/**
	 * Inner class used to describe an activity stream based history
	 * item. 
	 */
	public static class HistoryActivityStreamInfo implements IsSerializable {
		private ActivityStreamDataType	m_showSetting;	//
		private ActivityStreamInfo		m_asi;			//
		
		/*
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		private HistoryActivityStreamInfo() {
			// Initialize the super class...
			super();
			
			// ...and initialize anything else that requires it.
			setShowSetting(ActivityStreamDataType.OTHER);
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param asi
		 * @param showSetting
		 */
		public HistoryActivityStreamInfo(ActivityStreamInfo asi, ActivityStreamDataType showSetting) {
			// Initialize the object...
			this();

			// ...and store the parameters.
			setActivityStreamInfo(asi        );
			setShowSetting(       showSetting);
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param asi
		 */
		public HistoryActivityStreamInfo(ActivityStreamInfo asi) {
			// Always use one of the previous forms of the constructor.
			this(asi, ActivityStreamDataType.OTHER);
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param showSetting
		 */
		public HistoryActivityStreamInfo(ActivityStreamDataType showSetting) {
			// Always use one of the previous forms of the constructor.
			this(null, showSetting);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public ActivityStreamDataType getShowSetting()        {return m_showSetting;}
		public ActivityStreamInfo     getActivityStreamInfo() {return m_asi;        }
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setShowSetting(       ActivityStreamDataType showSetting) {m_showSetting = showSetting;}
		public void setActivityStreamInfo(ActivityStreamInfo     asi)         {m_asi         = asi;        }
	}

	/**
	 * Inner class used to describe an administrative action based
	 * history item. 
	 */
	public static class HistoryAdminActionInfo implements IsSerializable {
		private GwtAdminAction	m_adminAction;	//
		
		/*
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		private HistoryAdminActionInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Class constructor.
		 * 
		 * @param adminAction
		 */
		public HistoryAdminActionInfo(GwtAdminAction adminAction) {
			// Initialize this object...
			this();

			// ...and store the parameter.
			setAdminAction(adminAction);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public GwtAdminAction getAdminAction() {return m_adminAction;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setAdminAction(GwtAdminAction adminAction) {m_adminAction = adminAction;}
	}
	
	/**
	 * Enumeration that specifies the type of item a HistoryInfo
	 * describes. 
	 */
	public enum HistoryItemType implements IsSerializable {
		ACTIVITY_STREAM,
		ADMIN_ACTION,
		URL;
		
		/**
		 * Get'er methods.
		 */
		public boolean isActivityStream() {return this.equals(ACTIVITY_STREAM);}
		public boolean isAdminAction()    {return this.equals(ADMIN_ACTION);   } 
		public boolean isUrl()            {return this.equals(URL);            }
	}

	/**
	 * Inner class used to describe a URL based history item. 
	 */
	public static class HistoryUrlInfo implements IsSerializable {
		private Instigator	m_instigator;	//
		private String		m_url;			//
		
		/*
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		private HistoryUrlInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 *
		 * @param url
		 * @param instigator
		 */
		public HistoryUrlInfo(String url, Instigator instigator) {
			// Initialize this object...
			this();

			// ...and store the parameters.
			setUrl(       url       );
			setInstigator(instigator);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public Instigator getInstigator() {return m_instigator;}
		public String     getUrl()        {return m_url;       }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setInstigator(Instigator instigator) {m_instigator = instigator;}
		public void setUrl(       String     url)        {m_url        = url;       }
	}
	
	/*
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	private HistoryInfo() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method for an activity stream based HistoryInfo.
	 *
	 * @param selectedMastheadCollection
	 * @param asi
	 * @param showSetting
	 */
	public HistoryInfo(CollectionType selectedMastheadCollection, ActivityStreamInfo asi, ActivityStreamDataType showSetting) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setSelectedMastheadCollection(selectedMastheadCollection                     );
		setItemType(                  HistoryItemType.ACTIVITY_STREAM                );
		setActivityStreamInfo(        new HistoryActivityStreamInfo(asi, showSetting));
	}
	
	/**
	 * Constructor method for an administrative action based
	 * HistoryInfo.
	 *
	 * @param selectedMastheadCollection
	 * @param adminAction
	 */
	public HistoryInfo(CollectionType selectedMastheadCollection, GwtAdminAction adminAction) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setSelectedMastheadCollection(selectedMastheadCollection             );
		setItemType(                  HistoryItemType.ADMIN_ACTION           );
		setAdminActionInfo(           new HistoryAdminActionInfo(adminAction));
	}
	
	/**
	 * Constructor method for a URL based HistoryInfo.
	 *
	 * @param selectedMastheadCollection
	 * @param url
	 * @param instigator
	 */
	public HistoryInfo(CollectionType selectedMastheadCollection, String url, Instigator instigator) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setSelectedMastheadCollection(selectedMastheadCollection         );
		setItemType(                  HistoryItemType.URL                );
		setUrlInfo(                   new HistoryUrlInfo(url, instigator));
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public CollectionType            getSelectedMastheadCollection() {return m_selectedMastheadCollection;}
	public HistoryActivityStreamInfo getActivityStreamInfo()         {return m_asInfo;                    }
	public HistoryAdminActionInfo    getAdminActionInfo()            {return m_aaInfo;                    }
	public HistoryItemType           getItemType()                   {return m_itemType;                  }
	public HistoryUrlInfo            getUrlInfo()                    {return m_urlInfo;                   }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setSelectedMastheadCollection(CollectionType            selectedMastheadCollection) {m_selectedMastheadCollection = selectedMastheadCollection;}
	public void setActivityStreamInfo(        HistoryActivityStreamInfo asInfo)                     {m_asInfo                     = asInfo;                    }
	public void setAdminActionInfo(           HistoryAdminActionInfo    aaInfo)                     {m_aaInfo                     = aaInfo;                    }
	public void setItemType(                  HistoryItemType           itemType)                   {m_itemType                   = itemType;                  }
	public void setUrlInfo(                   HistoryUrlInfo            urlInfo)                    {m_urlInfo                    = urlInfo;                   }
}
