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
package org.kablink.teaming.gwt.client.util;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to bundle information required to view a share item's
 * date through * GWT RPC requests.
 *  
 * @author drfoster
 */
public class ShareDateInfo implements IsSerializable, ShareStringValue {
	private Date	m_shareDate;	//
	private String	m_addedStyle;	//
	private String	m_stringValue;	//
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ShareDateInfo() {
		// Nothing to do.
		super();
	}
	
	/**
	 * Constructor method.
	 *
	 * @param shareDate
	 * @param dateString
	 */
	public ShareDateInfo(Date shareDate, String dateString) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setShareDate( shareDate  );
		setDateString(dateString);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public Date   getShareDate()  {return m_shareDate;}
	public String getDateString() {return getValue(); }
	
	/**
	 * Implements the ShareStringValue.getAddedStyle() method.
	 * 
	 * @return
	 */
	@Override
	public String getAddedStyle() {return m_addedStyle;}
	
	/**
	 * Implements the ShareStringValue.getValue() method.
	 * 
	 * @return
	 */
	@Override
	public String getValue() {return m_stringValue;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setShareDate( Date   shareDate)  {m_shareDate = shareDate;}
	public void setDateString(String dateString) {setValue(dateString);   }
	
	/**
	 * Implements the ShareStringValue.setAddedStyle() method.
	 * 
	 * @param addedStyle
	 */
	@Override
	public void setAddedStyle(String addedStyle) {m_addedStyle = addedStyle;}
	
	/**
	 * Implements the ShareStringValue.setValue() method.
	 * 
	 * @param stringValue
	 */
	@Override
	public void setValue(String stringValue) {m_stringValue = stringValue;}
}
