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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class holds the response data for the 'Get Click on Title
 * Action' RPC command.
 * 
 * @author drfoster@novell.com
 */
public class ClickOnTitleActionRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private ClickAction	m_clickAction;	//
	private String		m_url;			//
	
	/**
	 * Enumeration to specific the action to take on the click.
	 * 
	 * Note that certain of these actions may require a URL to
	 * implement which will be provide with the response data.
	 */
	public enum ClickAction implements IsSerializable {
		DESCEND_INTO_BINDER,	// No URL.
		
		DOWNLOAD_FILE,			// URL required.
		VIEW_AS_HTML,			// URL required.
		VIEW_DETAILS,			// URL required.
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization requirements.
	 */
	public ClickOnTitleActionRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * Used for ClickActions that require a URL.
	 * 
	 * @param clickAction
	 * @param url
	 */
	public ClickOnTitleActionRpcResponseData(ClickAction clickAction, String url) {
		// Initialize the this object...
		this();
		
		// ..and store the parameters.
		setClickAction(clickAction);
		setUrl(        url        );
	}
	
	/**
	 * Constructor method.
	 *
	 * Used for ClickActions that don't require a URL.
	 * 
	 * @param clickAction
	 */
	public ClickOnTitleActionRpcResponseData(ClickAction clickAction) {
		// Always use the previous form of the constructor.
		this(clickAction, null);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ClickAction getClickAction() {return m_clickAction;}
	public String      getUrl()         {return m_url;        }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setClickAction(ClickAction clickAction) {m_clickAction = clickAction;}
	public void setUrl(        String      url)         {m_url         = url;        }
}
