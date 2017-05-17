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
	public enum ScrollbarValue
	{
		ALWAYS,
		NEVER,
		AUTO
	}
	
	private String m_url;
	private String m_name;
	private String m_title;
	private int m_height;
	private Long m_width;
	private boolean m_showBorder;
	private ScrollbarValue m_scrollbarValue;
	
	/**
	 * 
	 */
	public IFrameProperties()
	{
		m_url = null;
		m_name = "";
		m_title = null;
		m_height = 200;
		m_width = null;
		m_showBorder = false;
		m_scrollbarValue = ScrollbarValue.AUTO;
	}
	
	
	/**
	 * 
	 */
	@Override
	public void copy( PropertiesObj props )
	{
		if ( props instanceof IFrameProperties )
		{
			IFrameProperties iframeProps;
			
			iframeProps = (IFrameProperties) props;
			setUrl( iframeProps.getUrl() );
			setName( iframeProps.getName() );
			setHeight( iframeProps.getHeight() );
			setWidthLong( iframeProps.getWidthLong() );
			setShowBorder( iframeProps.getShowBorder() );
			setScrollbarValue( iframeProps.getScrollbarValue() );
			setTitle( iframeProps.getTitle() );
		}
	}
	

	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	@Override
	public String createConfigString()
	{
		String str;
		
		// The string should look like: "iframe,url=;"
		str = "iframe,url=";
		if ( m_url != null )
			str += ConfigData.encodeConfigData( m_url );
		
		if ( m_name != null )
			str += ",name=" + m_name;
		
		str += ",height=" + String.valueOf( m_height );
		str += ",width=";
		if ( m_width != null )
			str += String.valueOf( m_width );
		
		if ( m_title != null && m_title.length() > 0 )
		{
			str += ",title=" + ConfigData.encodeConfigData( m_title );
		}
		
		str += ",frameBorder=";
		if ( m_showBorder )
			str += "1";
		else
			str += "0";
		
		if ( m_scrollbarValue == ScrollbarValue.ALWAYS )
			str += ",scrolling=yes";
		else if ( m_scrollbarValue == ScrollbarValue.NEVER )
			str += ",scrolling=no";
		
		str += ";";

		return str;
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
	public String getHeightAsString()
	{
		return String.valueOf( m_height );
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
	public ScrollbarValue getScrollbarValue()
	{
		return m_scrollbarValue;
	}
	
	/**
	 * 
	 */
	public String getScrollbarValueAsString()
	{
		if ( m_scrollbarValue == ScrollbarValue.ALWAYS )
			return "yes";
		
		if ( m_scrollbarValue == ScrollbarValue.NEVER )
			return "no";
		
		return "auto";
	}
	
	/**
	 * 
	 */
	public boolean getShowBorder()
	{
		return m_showBorder;
	}
	
	/**
	 * 
	 */
	public String getTitle()
	{
		return m_title;
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
	public Long getWidthLong()
	{
		return m_width;
	}
	
	/**
	 * 
	 */
	public String getWidthAsString()
	{
		if ( m_width != null )
			return String.valueOf( m_width );
		
		return "";
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
	public void setHeight( String height )
	{
		try
		{
			setHeight( Integer.parseInt( height ) );
		}
		catch (Exception ex)
		{
			setHeight( 200 );
		}
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
	public void setScrollbarValue( ScrollbarValue scrollbarValue )
	{
		m_scrollbarValue = scrollbarValue;
	}
	
	/**
	 * 
	 */
	public void setScrollbarValue( String scrollbarValue )
	{
		ScrollbarValue value;
		
		value = ScrollbarValue.AUTO;
		
		if ( scrollbarValue != null )
		{
			if ( scrollbarValue.equalsIgnoreCase( "yes" ) )
				value = ScrollbarValue.ALWAYS;
			else if ( scrollbarValue.equalsIgnoreCase( "no" ) )
				value = ScrollbarValue.NEVER;
		}
		
		setScrollbarValue( value );
	}
	
	/**
	 * 
	 */
	public void setShowBorder( boolean showBorder )
	{
		m_showBorder = showBorder;
	}
	
	/**
	 * 
	 */
	public void setShowBorder( String showBorder )
	{
		if ( showBorder != null && showBorder.equalsIgnoreCase( "1" ) )
			setShowBorder( true );
		else
			setShowBorder( false );
	}
	
	/**
	 * 
	 */
	public void setTitle( String title )
	{
		m_title = title;
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
	public void setWidthLong( Long width )
	{
		m_width = width;
	}
	
	/**
	 * 
	 */
	public void setWidth( String width )
	{
		if ( width == null || width.length() == 0 )
		{
			setWidthLong( null );
			return;
		}
		
		try
		{
			setWidthLong( Long.parseLong( width ) );
		}
		catch ( Exception ex )
		{
			setWidthLong( null );
		}
	}
	
}
