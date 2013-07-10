/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.ClientEventParameter;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Class used to communicate toolbar item information between the
 * client (i.e., the MainMenuControl) and the server (i.e.,
 * GwtRpcServiceImpl.getToolbarItems().)
 * 
 * @author drfoster@novell.com
 *
 */
public class ToolbarItem implements IsSerializable {
	private List<NameValuePair> m_qualifiersAL = new ArrayList<NameValuePair>();	// Qualifier name/value pairs for this toolbar item.
	private List<ToolbarItem> m_nestedItemsAL = new ArrayList<ToolbarItem>();		// Toolbar items nested within this one.
	private String m_name;															// The name of this toolbar item.
	private String m_title;															// The display name for this toolbar item.
	private String m_url;															// The URL to launch for this toolbar item.
	private TeamingEvents m_teamingEvent  = TeamingEvents.UNDEFINED;				// If the toolbar item is to fire is an event.
	
	// The Client*Parameter's can only be specified and used
	// on the client side.
	private transient ClientEventParameter  m_clientEventParameter;					// Optional parameter for the TeamingEvents.

	/**
	 * Inner class used to track name/value pairs.
	 */
	public static class NameValuePair implements IsSerializable {
		private String m_name;	// The name  for this name/value pair.
		private String m_value;	// The value for this name/value pair.

		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public NameValuePair() {
			// Nothing to do.
		}

		/**
		 * Public get'er methods.
		 * 
		 * @return
		 */
		public String getName()  {return m_name; }
		public String getValue() {return m_value;}

		/**
		 * Public set'er methods.
		 * 
		 * @param s
		 */
		public void setName( String s) {m_name  = s;}
		public void setValue(String s) {m_value = s;}
	}
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ToolbarItem() {
		// Nothing to do.
	}

	/**
	 * Adds a nested toolbar item to this one.
	 *  
	 * @param tmi
	 */
	public void addNestedItem(ToolbarItem tmi) {
		m_nestedItemsAL.add(tmi);
	}

	/**
	 * Adds a name/value pair to the qualifier list.
	 *  
	 * @param nvp
	 */
	public void addQualifier(NameValuePair nvp) {
		m_qualifiersAL.add(nvp);
	}

	/**
	 * Adds a name/value pair to the qualifier list.
	 *  
	 * @param name
	 * @param value
	 */
	public void addQualifier(String name, String value) {
		NameValuePair nvp = new NameValuePair();
		nvp.setName(name);
		nvp.setValue(value);
		addQualifier(nvp);
	}

	/**
	 * Returns the name of the toolbar item.
	 * 
	 * @return
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns this toolbar item's nested items list.
	 * 
	 * @return
	 */
	public List<ToolbarItem> getNestedItemsList() {
		return m_nestedItemsAL;
	}

	/**
	 * Returns nested toolbar item based on its name.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public ToolbarItem getNestedToolbarItem(String name) {
		name = ((null == name) ? "" : name.toLowerCase());
		boolean noName = (0 == name.length());
		for (Iterator<ToolbarItem> tbiIT = m_nestedItemsAL.iterator(); tbiIT.hasNext(); ) {
			ToolbarItem tbi = tbiIT.next();
			String tbName = tbi.getName();
			tbName = ((null == tbName) ? "" : tbName.toLowerCase());
			boolean noTBName = (0 == tbName.length());
			if ((noName && noTBName) || ((!noName) && tbName.endsWith(name))) {
				return tbi;
			}
		}
		return null;
	}
	
	/*
	 * Returns the name/value pair for a qualifier based on its name.
	 */
	private NameValuePair getQualifier(String name) {
		name = name.toLowerCase();
		for (Iterator<NameValuePair> qIT = m_qualifiersAL.iterator(); qIT.hasNext(); ) {
			NameValuePair nvp = qIT.next();
			String nvpName = nvp.getName().toLowerCase();
			if (nvpName.endsWith(name)) {
				return nvp;
			}
		}
		return null;
	}
	
	/**
	 * Returns this toolbar item's qualifiers list.
	 * 
	 * @return
	 */
	public List<NameValuePair> getQualifiersList() {
		return m_qualifiersAL;
	}

	/**
	 * Returns the value for a qualifier based on its name.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public String getQualifierValue(String name) {
		NameValuePair nvp = getQualifier(name);
		return ((null == nvp) ? null : nvp.getValue());
	}
	
	/**
	 * Returns the teaming event from a toolbar item.
	 * 
	 * @return
	 */
	public TeamingEvents getTeamingEvent() {
		return m_teamingEvent;
	}
	
	/**
	 * Returns the client event parameter from a toolbar item.
	 * 
	 * @return
	 */
	public ClientEventParameter getClientEventParameter() {
		return m_clientEventParameter;
	}
	
	/**
	 * Returns the title of the toolbar item.
	 * 
	 * @return
	 */
	public String getTitle() {
		return m_title;
	}
	
	/**
	 * Returns the URL of the toolbar item.
	 * 
	 * @return
	 */
	public String getUrl() {
		return m_url;
	}
	
	/**
	 * Returns true if this toolbar item has at least a specified
	 * number of nested toolbar items and false otherwise.
	 * 
	 * @param atLeast
	 * 
	 * @return
	 */
	public boolean hasNestedToolbarItems(int atLeast) {
		int count = ((null == m_nestedItemsAL) ? 0 : m_nestedItemsAL.size());
		return (count >= atLeast);
	}
	
	public boolean hasNestedToolbarItems() {
		return hasNestedToolbarItems(1);
	}
	
	/**
	 * Stores the name of the toolbar item.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Stores a teaming event in the toolbar item.
	 * 
	 * @param teamingEvent
	 */
	public void setTeamingEvent(TeamingEvents teamingEvent) {
		m_teamingEvent = teamingEvent;
	}
	
	/**
	 * Stores a client event parameter in the toolbar item.
	 * 
	 * @param clientEventParameter
	 */
	public void setClientEventParameter(ClientEventParameter clientEventParameter) {
		m_clientEventParameter = clientEventParameter;
	}
	
	/**
	 * Stores the title of the toolbar item.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		m_title = title;
	}
	
	/**
	 * Stores the URL of the toolbar item.
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		m_url = url;
	}
}
