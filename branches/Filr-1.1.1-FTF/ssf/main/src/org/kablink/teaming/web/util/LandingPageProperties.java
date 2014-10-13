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
package org.kablink.teaming.web.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * 
 *
 */
public class LandingPageProperties
{
	private final String ROOT_ELEMENT_NAME = "landingPageData";
	private final String BACKGROUND_ELEMENT_NAME = "background";
	private final String PAGE_LAYOUT_ELEMENT_NAME = "pageLayout";
	private final String HEADER_ELEMENT_NAME = "header";
	private final String CONTENT_ELEMENT_NAME = "content";
	private final String BORDER_ELEMENT_NAME = "border";
	private final String COLOR_ATTRIBUTE_NAME = "color";
	private final String IMAGE_NAME_ATTRIBUTE_NAME = "imgName";
	private final String REPEAT_ATTRIBUTE_NAME = "repeat";
	private final String HIDE_MASTHEAD_ATTRIBUTE_NAME = "hideMasthead";
	private final String HIDE_SIDEBAR_ATTRIBUTE_NAME = "hideSidebar";
	private final String HIDE_FOOTER_ATTRIBUTE_NAME = "hideFooter";
	private final String HIDE_MENU_ATTRIBUTE_NAME = "hideMenu";
	private final String BG_COLOR_ATTRIBUTE_NAME = "bgColor";
	private final String TEXT_COLOR_ATTRIBUTE_NAME = "textColor";
	private final String WIDTH_ATTRIBUTE_NAME = "width";
	private final String PAGE_STYLE_ATTRIBUTE_NAME = "pageStyle";

	private String m_backgroundColor;
	private String m_backgroundImageName;
	private String m_backgroundImageUrl;
	private String m_backgroundRepeat;
	private boolean m_hideMasthead;
	private boolean m_hideSidebar;
	private boolean m_hideFooter;
	private boolean m_hideMenu;
	private String m_headerBgColor;
	private String m_headerTextColor;
	private String m_contentTextColor;
	private String m_borderColor;
	private String m_borderWidth;
	private String m_style;

	/**
	 * 
	 */
	public LandingPageProperties()
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_backgroundImageUrl = null;
		m_backgroundRepeat = null;
		m_hideMasthead = false;
		m_hideSidebar = false;
		m_hideFooter = false;
		m_hideMenu = false;
		m_headerBgColor = null;
		m_headerTextColor = null;
		m_contentTextColor = null;
		m_borderColor = null;
		m_borderWidth = null;
		m_style = "mashup_dark.css"; 
	}

	/**
	 * 
	 */
	public String getBackgroundColor()
	{
		return m_backgroundColor;
	}
	
	/**
	 * 
	 */
	public String getBackgroundImageName()
	{
		return m_backgroundImageName;
	}
	
	/**
	 * 
	 */
	public String getBackgroundImageUrl()
	{
		return m_backgroundImageUrl;
	}
	
	/**
	 * 
	 */
	public String getBackgroundRepeat()
	{
		return m_backgroundRepeat;
	}
	
	/**
	 * 
	 */
	public String getBorderColor()
	{
		return m_borderColor;
	}
	
	/**
	 * 
	 */
	public String getBorderWidth()
	{
		return m_borderWidth;
	}
	
	/**
	 * 
	 */
	public String getContentTextColor()
	{
		return m_contentTextColor;
	}

	/**
	 * 
	 */
	public String getHeaderBgColor()
	{
		return m_headerBgColor;
	}
	
	/**
	 * 
	 */
	public String getHeaderTextColor()
	{
		return m_headerTextColor;
	}

	/**
	 * 
	 */
	public boolean getHideFooter()
	{
		return m_hideFooter;
	}
	
	/**
	 * 
	 */
	public boolean getHideMasthead()
	{
		return m_hideMasthead;
	}
	
	/**
	 * 
	 */
	public boolean getHideMenu()
	{
		return m_hideMenu;
	}
	
	/**
	 * 
	 */
	public boolean getHideSidebar()
	{
		return m_hideSidebar;
	}
	
	/**
	 * Return the properties as an xml string that looks like the following:
	 *	<landingPageData>
	 * 		<background color="" imgName="" />
	 * 		<pageLayout hideMasthead="true | false" hideSidebar="true | false" hideFooter="true | false" hideMenu="true | false" pageStyle="mashup_dark.css | mashup_light.css" />
	 * 		<header bgColor="" textColor="" />
	 * 		<content textColor="" />
	 * 		<border color="" width="" />
	 * 	</landingPageData>
	 */
	public String getPropertiesAsXMLString()
	{
		Document doc;
		Element rootElement;
		
		doc = DocumentHelper.createDocument();
		
		rootElement = doc.addElement( ROOT_ELEMENT_NAME );
		
		// Add the <background color="" imgName="" /> element
		{
			Element backgroundElement;

			backgroundElement = rootElement.addElement( BACKGROUND_ELEMENT_NAME );
			
			if ( m_backgroundColor != null )
				backgroundElement.addAttribute( COLOR_ATTRIBUTE_NAME, m_backgroundColor );
			
			if ( m_backgroundImageName != null )
				backgroundElement.addAttribute( IMAGE_NAME_ATTRIBUTE_NAME, m_backgroundImageName );
			
			if ( m_backgroundRepeat != null )
				backgroundElement.addAttribute( REPEAT_ATTRIBUTE_NAME, m_backgroundRepeat );
		}
		
		// Add the <pageLayout /> element
		{
			Element pgLayoutElement;
			
			pgLayoutElement = rootElement.addElement( PAGE_LAYOUT_ELEMENT_NAME );
			
			pgLayoutElement.addAttribute( HIDE_MASTHEAD_ATTRIBUTE_NAME, String.valueOf( m_hideMasthead ) );
			pgLayoutElement.addAttribute( HIDE_SIDEBAR_ATTRIBUTE_NAME, String.valueOf( m_hideSidebar ) );
			pgLayoutElement.addAttribute( HIDE_FOOTER_ATTRIBUTE_NAME, String.valueOf( m_hideFooter ) );
			pgLayoutElement.addAttribute( HIDE_MENU_ATTRIBUTE_NAME, String.valueOf( m_hideMenu ) );
			pgLayoutElement.addAttribute( PAGE_STYLE_ATTRIBUTE_NAME, m_style );
		}
		
		// Add the <header bgColor="" textColor="" /> element
		{
			Element headerElement;
			
			headerElement = rootElement.addElement( HEADER_ELEMENT_NAME );
			
			if ( m_headerBgColor != null )
				headerElement.addAttribute( BG_COLOR_ATTRIBUTE_NAME, m_headerBgColor );
			
			if ( m_headerTextColor != null )
				headerElement.addAttribute( TEXT_COLOR_ATTRIBUTE_NAME, m_headerTextColor );
		}
		
		// Add the <content textColor="" /> element
		{
			Element contentElement;
			
			contentElement = rootElement.addElement( CONTENT_ELEMENT_NAME );
			
			if ( m_contentTextColor != null )
				contentElement.addAttribute( TEXT_COLOR_ATTRIBUTE_NAME, m_contentTextColor );
		}
		
		// Add the <border color="" width="" /> element
		{
			Element borderElement;
			
			borderElement = rootElement.addElement( BORDER_ELEMENT_NAME );
			
			if ( m_borderColor != null )
				borderElement.addAttribute( COLOR_ATTRIBUTE_NAME, m_borderColor );
			
			if ( m_borderWidth != null )
				borderElement.addAttribute( WIDTH_ATTRIBUTE_NAME, m_borderWidth );
		}
		
		return doc.asXML();
	}
	
	/**
	 * 
	 */
	public String getStyle()
	{
		return m_style;
	}
	
	/**
	 * 
	 */
	public void setBackgroundColor( String color )
	{
		m_backgroundColor = color;
	}
	
	/**
	 * 
	 */
	public void setBackgroundImgName( String imgName )
	{
		m_backgroundImageName = imgName;
	}
	
	/**
	 * 
	 */
	public void setBackgroundImgUrl( String url )
	{
		m_backgroundImageUrl = url;
	}
	
	/**
	 * 
	 */
	public void setBackgroundRepeat( String repeat )
	{
		m_backgroundRepeat = repeat;
	}
	
	/**
	 * 
	 */
	public void setBorderColor( String color )
	{
		m_borderColor = color;
	}
	
	/**
	 * 
	 */
	public void setBorderWidth( String width )
	{
		m_borderWidth = width;
	}
	
	/**
	 * 
	 */
	public void setContentTextColor( String color )
	{
		m_contentTextColor = color;
	}
	
	/**
	 * 
	 */
	public void setHeaderBgColor( String color )
	{
		m_headerBgColor = color;
	}
	
	/**
	 * 
	 */
	public void setHeaderTextColor( String color )
	{
		m_headerTextColor = color;
	}
	/**
	 * 
	 */
	public void setHideFooter( boolean hide )
	{
		m_hideFooter = hide;
	}
	
	/**
	 * 
	 */
	public void setHideMasthead( boolean hide )
	{
		m_hideMasthead = hide;
	}
	
	/**
	 * 
	 */
	public void setHideMenu( boolean hide )
	{
		m_hideMenu = hide;
	}
	
	/**
	 * 
	 */
	public void setHideSidebar( boolean hide )
	{
		m_hideSidebar = hide;
	}
	
	/**
	 * 
	 */
	public void setStyle( String style )
	{
		m_style = style;
	}
}
