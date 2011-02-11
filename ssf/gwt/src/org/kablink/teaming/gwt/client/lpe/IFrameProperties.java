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
 * This class holds all of the properties needed to define an IFrame widget in a landing page.
 * @author jwootton
 *
 */
public class IFrameProperties
	implements PropertiesObj
{
	private String m_url;
	private String m_name;
	private int m_height;
	private int m_width;
	private int m_borderWidth;
	private int m_marginHeight;
	private int m_marginWidth;
	private boolean m_haveScrollbars;
	
	/**
	 * 
	 */
	public IFrameProperties()
	{
		m_url = null;
		m_name = "";
		m_height = 0;
		m_width = 0;
		m_borderWidth = 0;
		m_marginHeight = 0;
		m_marginWidth = 0;
		m_haveScrollbars = false;
	}
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof IFrameProperties )
		{
			IFrameProperties iframeProps;
			
			iframeProps = (IFrameProperties) props;
			setUrl( iframeProps.getUrl() );
			setName( iframeProps.getName() );
			setHeight( iframeProps.getHeight() );
			setWidth( iframeProps.getWidth() );
			setBorderWidth( iframeProps.getBorderWidth() );
			setMarginHeight( iframeProps.getMarginHeight() );
			setMarginWidth( iframeProps.getMarginWidth() );
			setHasScrollbars( iframeProps.getHasScrollbars() );
		}
	}
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		
		//!!! Finish
		// The string should look like: "iframe,url=;"
		str = "iframe,url=";
		if ( m_url != null )
			str += ConfigData.encodeConfigData( m_url );
		
		str += ";";

		return str;
	}
	
	
	/**
	 * 
	 */
	public int getBorderWidth()
	{
		return m_borderWidth;
	}
	
	/**
	 * 
	 */
	public boolean getHasScrollbars()
	{
		return m_haveScrollbars;
	}
	
	/**
	 * 
	 */
	public int getHeight()
	{
		return m_height;
	}
	
	/**
	 * 
	 */
	public int getMarginHeight()
	{
		return m_marginHeight;
	}
	
	/**
	 * 
	 */
	public int getMarginWidth()
	{
		return m_marginWidth;
	}
	
	/**
	 * 
	 */
	public String getName()
	{
		return m_name;
	}
	
	
	/**
	 * 
	 */
	public String getUrl()
	{
		return m_url;
	}
	
	
	/**
	 * 
	 */
	public int getWidth()
	{
		return m_width;
	}
	
	/**
	 * 
	 */
	public void setBorderWidth( int width )
	{
		m_borderWidth = width;
	}
	
	/**
	 * 
	 */
	public void setHasScrollbars( boolean hasScrollbars )
	{
		m_haveScrollbars = hasScrollbars;
	}
	
	/**
	 * 
	 */
	public void setHeight( int height )
	{
		m_height = height;
	}
	
	/**
	 * 
	 */
	public void setMarginHeight( int height )
	{
		m_marginHeight = height;
	}
	
	/**
	 * 
	 */
	public void setMarginWidth( int width )
	{
		m_marginWidth = width;
	}
	
	/**
	 * 
	 */
	public void setName( String name )
	{
		if ( name == null )
			name = "";
		
		m_name = name;
	}
	
	
	/**
	 * 
	 */
	public void setUrl( String url )
	{
		m_url = url;
	}
	
	
	/**
	 * 
	 */
	public void setWidth( int width )
	{
		m_width = width;
	}
	
}
