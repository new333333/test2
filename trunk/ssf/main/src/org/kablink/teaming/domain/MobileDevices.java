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
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Used to encapsulate mobile devices information.  A component of
 * User.
 * 
 * Device information is stored in the database as XML.  An example of
 * devices document would be:
 * 
 *		<devices>
 *			<device id="...long...">
 *				<description>...string...</description>
 *				<pushToken>...string...</pushToken>
 *				<lastLogin>...date...</lastLogin>
 *				<lastActivity>...date...</lastActivity>
 *				<wipeScheduled>...Boolean...</wipeScheduled>
 *				<lastWipe>...date...</lastWipe>
 *			</device>
 *		</devices>
 * 
 * @author drfoster@novell.com
 */
public class MobileDevices {
	private List<MobileDevice>	m_mobileDeviceList;	//
	private SSClobString		m_mobileDevices;	// The XML stored in the database.

	/**
	 * Inner class that encapsulates an individual device.
	 */
	public static class MobileDevice {
		private boolean	m_wipeScheduled;	//
		private Date	m_lastLogin;		//
		private Date	m_lastWipe;			//
		private String	m_description;		//
		private String	m_id;				//
		private String	m_pushToken;		//

		/**
		 * Constructor method.
		 */
		public MobileDevice() {
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public boolean isWipeScheduled() {return m_wipeScheduled;}
		public Date    getLastLogin()    {return m_lastLogin;    }
		public Date    getLastWipe()     {return m_lastWipe;     }
		public String  getDescription()  {return m_description;  }
		public String  getId()           {return m_id;           }
		public String  getPushToken()    {return m_pushToken;    }

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setWipeScheduled(boolean wipeScheduled) {m_wipeScheduled = wipeScheduled;}
		public void setLastLogin(    Date    lastLogin)     {m_lastLogin     = lastLogin;    }
		public void setLastWipe(     Date    lastWipe)      {m_lastWipe      = lastWipe;     }
		public void setDescription(  String  description)   {m_description   = description;  }
		public void setId(           String  id)            {m_id            = id;           }
		public void setPushToken(    String  pushToken)     {m_pushToken     = pushToken;    }

		/**
		 * Constructs a MobileDevice object from a <device> XML node.
		 * 
		 * @param deviceElement
		 * 
		 * @return
		 */
		public static MobileDevice constructMobileDeviceFromXml(Element deviceElement) {
			if (null == deviceElement) {
				return null;
			}
			
			MobileDevice reply = new MobileDevice();
			reply.setId(           deviceElement.attributeValue("id"));
			reply.setDescription(  getElementStringValue( deviceElement.element("description"))  );
			reply.setPushToken(    getElementStringValue( deviceElement.element("pushToken"))    );
			reply.setLastLogin(    getElementDateValue(   deviceElement.element("lastLogin"))    );
			reply.setLastWipe(     getElementDateValue(   deviceElement.element("lastWipe"))     );
			reply.setWipeScheduled(getElementBooleanValue(deviceElement.element("wipeScheduled")));
			return reply;
		}
		
		/**
		 * Constructs the XML string that will be stored in the
		 * database.
		 * 
		 * @return
		 */
		public String constructXmlString() {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("<device id=\""   + getSafeString(getId())               +            "\">"  );
			strBuff.append("<description>"   + getSafeString(    getDescription())  + "</description>"  );
			strBuff.append("<pushToken>"     + getSafeString(    getPushToken())    + "</pushToken>"    );
			strBuff.append("<lastLogin>"     + getSafeDateString(getLastLogin())    + "</lastLogin>"    );
			strBuff.append("<lastWipe>"      + getSafeDateString(getLastWipe())     + "</lastWipe>"     );
			strBuff.append("<wipeScheduled>" + String.valueOf(   isWipeScheduled()) + "</wipeScheduled>");
			strBuff.append("</device>");
			return strBuff.toString();
		}

		/*
		 * Returns the content of an Element as a boolean.
		 */
		private static boolean getElementBooleanValue(Element e) {
			boolean reply = false;
			if (null != e) {
				String bool = e.getText();
				if ((null != bool) && (0 < bool.length())) {
					try {
						reply = Boolean.parseBoolean(bool);
					}
					catch (Exception ex) {}
				}
			}
			return reply;
		}

		/*
		 * Returns the content of an Element as a Date.
		 */
		private static Date getElementDateValue(Element e) {
			Date reply = null;
			if (null != e) {
				String date = e.getText();
				if ((null != date) && (0 < date.length())) {
					try {
						reply = new Date(Long.parseLong(date));
					}
					catch (Exception ex) {}
				}
			}
			return reply;
		}

		/*
		 * Returns the content of an Element as a String.
		 */
		private static String getElementStringValue(Element e) {
			String reply = null;
			if (null != e) {
				reply = e.getText();
			}
			return reply;
		}

		/*
		 * Returns a Date as a string guarding against NPEs.
		 */
		private String getSafeDateString(Date d) {
			if (null == d) {
				return "";
			}
			return String.valueOf(d.getTime());
		}
		
		/*
		 * Returns a string guarding against NPEs.
		 */
		private String getSafeString(String s) {
			if (null == s) {
				s = "";
			}
			return s;
		}
	}

	/**
	 * Constructor method. 
	 */
	public MobileDevices() {
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param devices
	 */
	public MobileDevices(MobileDevices devices) {
		this();
		
		if (null != devices) {
			m_mobileDevices = devices.getMobileDevices();
		}
	}

	/**
	 * Adds a MobileDevice to the List<MobileDevice>.
	 * 
	 * @param device
	 */
	public void addMobileDevice(MobileDevice device) {
		if (null != device) {
			getMobileDeviceList().add(device);
			constructXmlString();
		}
	}
	
	/*
	 * Constructs the XML string that will be stored in the database. 
	 */
	private void constructXmlString() {
		if ((null == m_mobileDeviceList) || (0 == m_mobileDeviceList.size())) {
			m_mobileDevices = null;
		}
		
		else {
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("<devices>");
			for (MobileDevice d:  m_mobileDeviceList) {
				strBuff.append(d.constructXmlString());
			}
			strBuff.append("</devices>");
			m_mobileDevices = new SSClobString(strBuff.toString());
		}
	}
	
	/**
	 * Returns the current List<MobileDevice>.
	 * 
	 * @return
	 */
	public List<MobileDevice> getMobileDeviceList() {
		if (null == m_mobileDeviceList) {
			m_mobileDeviceList = new ArrayList<MobileDevice>();
		}
		return m_mobileDeviceList;
	}
	
	/**
	 * Return the current <devices> XML string.
	 * 
	 * @return
	 */
	public SSClobString getMobileDevices() {
		return m_mobileDevices;
	}
	
	/*
	 * Extract the List<MobileDevice> from the given <devices> XML
	 * string.
	 */
	@SuppressWarnings("unchecked")
	private void parseXmlString(String xmlString) {
		if ((null == xmlString) || (0 == xmlString.length())) {
			m_mobileDeviceList = null;
			return;
		}
		
		m_mobileDeviceList = new ArrayList<MobileDevice>();
		try {
			Document		doc            = DocumentHelper.parseText(xmlString);
			Element			devicesElement = doc.getRootElement();
			List<Element>	deviceElements = devicesElement.elements("device");
			if (null != deviceElements) {
				for (Element deviceElement:  deviceElements) {
					m_mobileDeviceList.add(
						MobileDevice.constructMobileDeviceFromXml(
							deviceElement));
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
	public void setMobileDevices(List<MobileDevice> mobileDeviceList) {
		// Stores the list and constructs its XML string
		// representation.
		m_mobileDeviceList = mobileDeviceList;
		constructXmlString();
	}
	
	/**
	 * Stores an XML String containing a <devices> Element and parses
	 * it into a List<MobileDevice>.
	 * 
	 * @param xmlString
	 */
	public void setMobileDevices(SSClobString xmlString) {
		// Store the XML, parse it and extract the devices.
		m_mobileDevices = xmlString;
		parseXmlString((null == xmlString) ? null : xmlString.getText());
	}
}
