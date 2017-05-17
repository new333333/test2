/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.widgets.PropertiesObj;



/**
 * This class holds all of the properties needed to define a "Link to url" widget in a landing page.
 * @author jwootton
 *
 */
public class LinkToUrlProperties
	implements PropertiesObj
{
	private String		m_title;
	private String		m_url;
	private boolean	m_openInNewWindow;
	
	/**
	 * 
	 */
	public LinkToUrlProperties()
	{
		m_title = null;
		m_url = null;
		m_openInNewWindow = false;
	}// end LinkToUrlProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof LinkToUrlProperties )
		{
			LinkToUrlProperties linkProps;
			
			linkProps = (LinkToUrlProperties) props;
			setTitle( linkProps.getTitle() );
			setUrl( linkProps.getUrl() );
			setOpenInNewWindow( linkProps.getOpenInNewWindow() );
		}
	}// end copy()
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "url,title=something,href=something,popup=1;"
		str = "url,title=";
		if ( m_title != null )
			str += ConfigData.encodeConfigData( m_title );
		str += ",";
		
		str += "href=";
		if ( m_url != null )
			str += ConfigData.encodeConfigData( m_url );
		
		if ( m_openInNewWindow )
			str += ",popup=1";
		str += ";";

		return str;
	}// end createConfigString()
	
	
	/**
	 * Return the value of the "Open the url in a new window" property
	 */
	public boolean getOpenInNewWindow()
	{
		return m_openInNewWindow;
	}// getOpenInNewWindow()
	
	
	/**
	 * Return the value of the title property
	 */
	public String getTitle()
	{
		return m_title;
	}// end getTitle()
	
	
	/**
	 * Return the value of the url property
	 */
	public String getUrl()
	{
		return m_url;
	}// end getUrl()
	

	/**
	 * 
	 */
	public void setOpenInNewWindow( boolean value )
	{
		m_openInNewWindow = value;
	}// end setOpenInNewWindow()
	
	
	/**
	 * 
	 */
	public void setTitle( String title )
	{
		m_title = title;
	}// end setTitle()
	
	
	/**
	 * 
	 */
	public void setUrl( String url )
	{
		m_url = url;
	}// end setUrl()
}// end LinkToUrlProperties
