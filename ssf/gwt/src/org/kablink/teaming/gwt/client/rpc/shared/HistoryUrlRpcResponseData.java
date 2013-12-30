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

import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for retrieving a URL from the
 * tracked history.
 * 
 * @author drfoster@novell.com
 */
public class HistoryUrlRpcResponseData implements IsSerializable, VibeRpcResponseData {
	public final static boolean	ENABLE_BROWSER_HISTORY	= false;	//! DRF (20131227):  Leave false on checkin until it's all working.

	private CollectionType	m_selectedMastheadCollection;	//
	private Instigator		m_instigator;					//
	private String			m_url;							//
	
	public final static String	HISTORY_MARKER			= "history";				// Marker appended to a URL with a history token so that we can relocate the URL during browser navigations.
	public final static int		HISTORY_MARKER_LENGTH	= HISTORY_MARKER.length();	// Length of HISTORY_MARKER.
	
	/*
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	private HistoryUrlRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param url
	 * @param instigator
	 * @param selectedMastheadCollection
	 */
	public HistoryUrlRpcResponseData(String url, Instigator instigator, CollectionType selectedMastheadCollection) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setUrl(                       url                       );
		setInstigator(                instigator                );
		setSelectedMastheadCollection(selectedMastheadCollection);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public CollectionType getSelectedMastheadCollection() {return m_selectedMastheadCollection;}
	public Instigator     getInstigator()                 {return m_instigator;                }
	public String         getUrl()                        {return m_url;                       }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setSelectedMastheadCollection(CollectionType selectedMastheadCollection) {m_selectedMastheadCollection = selectedMastheadCollection;}
	public void setInstigator(                Instigator     instigator)                 {m_instigator                 = instigator;                }
	public void setUrl(                       String         url)                        {m_url                        = url;                       }
}
