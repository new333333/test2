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
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.util.XmlUtil;

/**
 * Used to encapsulate mobile 'Open In' white lists.  A component of
 * ZoneConfig.
 * 
 * White list information is stored in the database as XML.  An example of
 * white lists document would be:
 * 
 *		<whiteLists>
 *			<android>
 *				<application>...string...</application>
 *			</android>
 *			<iOS>
 *				<application>...string...</application>
 *			</iOS>
 *		</whiteLists>
 * 
 * @author drfoster@novell.com
 */
public class MobileOpenInWhiteLists {
	private List<String>	m_androidApplications;		//
	private List<String>	m_iosApplications;			//
	private SSClobString	m_mobileOpenInWhiteLists;	// The XML stored in the database.

	/**
	 * Constructor method. 
	 */
	public MobileOpenInWhiteLists() {
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param whiteLists
	 */
	public MobileOpenInWhiteLists(MobileOpenInWhiteLists whiteLists) {
		this();
		
		if (null != whiteLists) {
			m_mobileOpenInWhiteLists = whiteLists.getMobileOpenInWhiteLists();
		}
	}

	/*
	 * Constructs the XML string that will be stored in the database. 
	 */
	private void constructXmlString() {
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<whiteList>");
		strBuff.append("<android>"  );
		if (null != m_androidApplications) {
			for (String app:  m_androidApplications) {
				strBuff.append("<application>" );
				strBuff.append(app             );
				strBuff.append("</application>");
			}
		}
		strBuff.append("</android>");
		strBuff.append("<iOS>");
		if (null != m_iosApplications) {
			for (String app:  m_iosApplications) {
				strBuff.append("<application>" );
				strBuff.append(app             );
				strBuff.append("</application>");
			}
		}
		strBuff.append("</iOS>"      );
		strBuff.append("</whiteList>");
		m_mobileOpenInWhiteLists = new SSClobString(strBuff.toString());
	}
	
	/**
	 * Returns the current List<String> of Android applications.
	 * 
	 * @return
	 */
	public List<String> getAndroidApplications() {
		if (null == m_androidApplications) {
			m_androidApplications = new ArrayList<String>();
		}
		return m_androidApplications;
	}
	
	/**
	 * Returns the current List<String> of iOS applications.
	 * 
	 * @return
	 */
	public List<String> getIosApplications() {
		if (null == m_iosApplications) {
			m_iosApplications = new ArrayList<String>();
		}
		return m_iosApplications;
	}
	
	/**
	 * Return the current <whiteLists> XML string.
	 * 
	 * @return
	 */
	public SSClobString getMobileOpenInWhiteLists() {
		return m_mobileOpenInWhiteLists;
	}
	
	/*
	 * Extract the List<String>'s from the given <whiteLists> XML
	 * string.
	 */
	@SuppressWarnings("unchecked")
	private void parseXmlString(String xmlString) {
		if ((null == xmlString) || (0 == xmlString.length())) {
			m_androidApplications =
			m_iosApplications     = null;
			return;
		}
		
		m_androidApplications = new ArrayList<String>();
		m_iosApplications     = new ArrayList<String>();
		
		try {
			Document doc               = XmlUtil.parseText(xmlString);
			Element  whiteListsElement = doc.getRootElement();
			
			Element androidElement = whiteListsElement.element("android");
			if (null != androidElement) {
				List<Element> applications = androidElement.elements("application");
				if (null != applications) {
					for (Element application:  applications) {
						m_androidApplications.add(application.getText());
					}
				}
			}
			
			Element iosElement = whiteListsElement.element("iOS");
			if (null != iosElement) {
				List<Element> applications = iosElement.elements("application");
				if (null != applications) {
					for (Element application:  applications) {
						m_iosApplications.add(application.getText());
					}
				}
			}
		}
		catch (DocumentException ex) {}
	}
	
	/**
	 * Stores a List<MobileDevice> as the current device list.
	 * 
	 * @param mobileDeviceList 
	 */
	public void setMobileOpenInWhiteLists(List<String> androidApplications, List<String> iosApplications) {
		// Stores the lists and constructs their XML string
		// representation.
		m_androidApplications = androidApplications;
		m_iosApplications     = iosApplications;
		constructXmlString();
	}
	
	/**
	 * Stores an XML String containing a <devices> Element and parses
	 * it into a List<MobileDevice>.
	 * 
	 * @param xmlString
	 */
	public void setMobileOpenInWhiteLists(SSClobString xmlString) {
		// Store the XML, parse it and extract the applications.
		m_mobileOpenInWhiteLists = xmlString;
		parseXmlString((null == xmlString) ? null : xmlString.getText());
	}
}
