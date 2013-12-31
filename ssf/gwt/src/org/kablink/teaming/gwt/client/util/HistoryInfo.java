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
package org.kablink.teaming.gwt.client.util;

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
	private CollectionType	m_selectedMastheadCollection;	// The collection type selected in the masthead when the HistoryInfo was created.
	private ItemType		m_itemType;						//
	private UrlInfo			m_urlInfo;						// Refers to the UrlInfo when m_itemType is URL.
	
	/**
	 * Enumeration that specifies the type of item a HistoryInfo
	 * describes. 
	 */
	public enum ItemType implements IsSerializable {
		ACTIVITY_STREAM,
		URL;
		
		/**
		 * Get'er methods.
		 */
		public boolean isActivityStream() {return this.equals(ACTIVITY_STREAM);}
		public boolean isUrl()            {return this.equals(URL);            }
	}

	/**
	 * Inner class used to describe a URL based history item. 
	 */
	public static class UrlInfo implements IsSerializable {
		private Instigator	m_instigator;	//
		private String		m_url;			//
		
		/*
		 * Constructor method.
		 * 
		 * For GWT serialization, must have a zero parameter constructor.
		 */
		private UrlInfo() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Constructor method.
		 *
		 * @param url
		 * @param instigator
		 */
		public UrlInfo(String url, Instigator instigator) {
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
		setSelectedMastheadCollection(selectedMastheadCollection  );
		setItemType(                  ItemType.URL                );
		setUrlInfo(                   new UrlInfo(url, instigator));
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public CollectionType getSelectedMastheadCollection() {return m_selectedMastheadCollection;}
	public ItemType       getItemType()                   {return m_itemType;                  }
	public UrlInfo        getUrlInfo()                    {return m_urlInfo;                   }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setSelectedMastheadCollection(CollectionType selectedMastheadCollection) {m_selectedMastheadCollection = selectedMastheadCollection;}
	public void setItemType(                  ItemType       itemType)                   {m_itemType                   = itemType;                  }
	public void setUrlInfo(                   UrlInfo        urlInfo)                    {m_urlInfo                    = urlInfo;                   }
}
