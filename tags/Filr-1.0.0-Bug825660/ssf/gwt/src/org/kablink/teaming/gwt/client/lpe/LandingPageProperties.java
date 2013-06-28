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

package org.kablink.teaming.gwt.client.lpe;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;
import org.kablink.teaming.gwt.client.widgets.WidgetStyles;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.google.gwt.xml.client.impl.DOMParseException;

/**
 * This class holds the properties for a landing page, such as the background color
 * and background image.
 * @author jwootton
 *
 */
public class LandingPageProperties
	implements IsSerializable, VibeRpcResponseData
{
	transient public final String ROOT_ELEMENT_NAME = "landingPageData";
	transient public final String BACKGROUND_ELEMENT_NAME = "background";
	transient public final String PAGE_LAYOUT_ELEMENT_NAME = "pageLayout";
	transient public final String HEADER_ELEMENT_NAME = "header";
	transient public final String CONTENT_ELEMENT_NAME = "content";
	transient public final String BORDER_ELEMENT_NAME = "border";
	transient public final String COLOR_ATTRIBUTE_NAME = "color";
	transient public final String IMAGE_NAME_ATTRIBUTE_NAME = "imgName";
	transient public final String REPEAT_ATTRIBUTE_NAME = "repeat";
	transient public final String HIDE_MENU_ATTRIBUTE_NAME = "hideMenu";
	transient public final String BG_COLOR_ATTRIBUTE_NAME = "bgColor";
	transient public final String TEXT_COLOR_ATTRIBUTE_NAME = "textColor";
	transient public final String WIDTH_ATTRIBUTE_NAME = "width";
	
	private String m_backgroundColor;
	private String m_backgroundImageName;
	private String m_backgroundImageUrl;
	private String m_backgroundRepeat;
	private boolean m_hideMenu;
	private boolean m_inheritProperties;
	private WidgetStyles m_widgetStyles;
	
	/**
	 * 
	 */
	public LandingPageProperties()
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_backgroundImageUrl = null;
		m_backgroundRepeat = null;
		m_widgetStyles = new WidgetStyles();
		m_hideMenu = false;
		m_inheritProperties = true;
	}

	/**
	 * 
	 */
	public LandingPageProperties( String propertiesXML )
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_backgroundImageUrl = null;
		m_backgroundRepeat = null;
		m_widgetStyles = new WidgetStyles();
		m_hideMenu = false;
		m_inheritProperties = true;
		
		init( propertiesXML );
	}
	
	/**
	 * 
	 */
	public void copy( LandingPageProperties lpProperties )
	{
		m_backgroundColor = lpProperties.getBackgroundColor();
		m_backgroundImageName = lpProperties.getBackgroundImageName();
		m_backgroundImageUrl = lpProperties.getBackgroundImageUrl();
		m_backgroundRepeat = lpProperties.getBackgroundRepeat();
		m_widgetStyles.setHeaderBgColor( lpProperties.getHeaderBgColor() );
		m_widgetStyles.setHeaderTextColor( lpProperties.getHeaderTextColor() );
		m_widgetStyles.setContentTextColor( lpProperties.getContentTextColor() );
		m_widgetStyles.setBorderColor( lpProperties.getBorderColor() );
		m_widgetStyles.setBorderWidth( lpProperties.getBorderWidth() );
		m_hideMenu = lpProperties.getHideMenu();
		m_inheritProperties = lpProperties.getInheritProperties();
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
		return m_widgetStyles.getBorderColor();
	}
	
	/**
	 * 
	 */
	public String getBorderWidth()
	{
		return m_widgetStyles.getBorderWidth();
	}
	
	/**
	 * 
	 */
	public String getContentTextColor()
	{
		return m_widgetStyles.getContentTextColor();
	}

	/**
	 * 
	 */
	public String getHeaderBgColor()
	{
		return m_widgetStyles.getHeaderBgColor();
	}
	
	/**
	 * 
	 */
	public String getHeaderTextColor()
	{
		return m_widgetStyles.getHeaderTextColor();
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
	public boolean getInheritProperties()
	{
		return m_inheritProperties;
	}
	
	/**
	 * Return the properties as an xml string that looks like the following:
	 *	<landingPageData>
	 * 		<background color="" imgName="" />
	 * 		<pageLayout hideMenu="true | false" />
	 * 		<header bgColor="" textColor="" />
	 * 		<content textColor="" />
	 * 		<border color="" width="" />
	 * 	</landingPageData>
	 */
	public String getPropertiesAsXMLString()
	{
		Document doc;
		Element rootElement;
		String color;
		
		// Are we inheriting the properties?
		if ( m_inheritProperties )
		{
			// Yes, return null to indicate we are inheriting
			return null;
		}
		
		doc = XMLParser.createDocument();
		
		rootElement = doc.createElement( ROOT_ELEMENT_NAME );
		doc.appendChild( rootElement );
		
		// Add the <background color="" imgName="" /> element
		{
			Element backgroundElement;

			backgroundElement = doc.createElement( BACKGROUND_ELEMENT_NAME );
			
			if ( m_backgroundColor != null )
				backgroundElement.setAttribute( COLOR_ATTRIBUTE_NAME, m_backgroundColor );
			
			if ( m_backgroundImageName != null )
			{
				backgroundElement.setAttribute( IMAGE_NAME_ATTRIBUTE_NAME, m_backgroundImageName );
				
				if ( m_backgroundRepeat != null )
					backgroundElement.setAttribute( REPEAT_ATTRIBUTE_NAME, m_backgroundRepeat );
			}
			
			rootElement.appendChild( backgroundElement );
		}
		
		// Add the <pageLayout hideMenu="true | false" /> element
		{
			Element pgLayoutElement;
			
			pgLayoutElement = doc.createElement( PAGE_LAYOUT_ELEMENT_NAME );
			
			pgLayoutElement.setAttribute( HIDE_MENU_ATTRIBUTE_NAME, String.valueOf( m_hideMenu ) );
			
			rootElement.appendChild( pgLayoutElement );
		}
		
		// Add the <header bgColor="" textColor="" /> element
		{
			Element headerElement;
			
			headerElement = doc.createElement( HEADER_ELEMENT_NAME );
			
			color = m_widgetStyles.getHeaderBgColor();
			if ( color != null )
				headerElement.setAttribute( BG_COLOR_ATTRIBUTE_NAME, color );
			
			color = m_widgetStyles.getHeaderTextColor();
			if ( color != null )
				headerElement.setAttribute( TEXT_COLOR_ATTRIBUTE_NAME, color );
			
			rootElement.appendChild( headerElement );
		}
		
		// Add the <content textColor="" /> element
		{
			Element contentElement;
			
			contentElement = doc.createElement( CONTENT_ELEMENT_NAME );
			
			color = m_widgetStyles.getContentTextColor();
			if ( color != null )
				contentElement.setAttribute( TEXT_COLOR_ATTRIBUTE_NAME, color );
			
			rootElement.appendChild( contentElement );
		}
		
		// Add the <border color="" width="" /> element
		{
			Element borderElement;
			String width;
			
			borderElement = doc.createElement( BORDER_ELEMENT_NAME );
			
			color = m_widgetStyles.getBorderColor();
			if ( color != null )
				borderElement.setAttribute( COLOR_ATTRIBUTE_NAME, color );
			
			width = m_widgetStyles.getBorderWidth();
			if ( width != null )
				borderElement.setAttribute( WIDTH_ATTRIBUTE_NAME, width );
			
			rootElement.appendChild( borderElement );
		}
		
		return doc.toString();
	}
	
	/**
	 * 
	 */
	public WidgetStyles getWidgetStyles()
	{
		return m_widgetStyles;
	}
	
	/**
	 * Initialize the properties from the given xml
	 */
	public void init( String xmlStr )
	{
		if ( xmlStr != null && xmlStr.length() > 0 )
		{
			try
			{
				Document doc;
				NodeList nodeList;
				
				doc = XMLParser.parse( xmlStr );
				
				// Get the <background ...> element.
				nodeList = doc.getElementsByTagName( BACKGROUND_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element backgroundElement;
					
					backgroundElement = (Element) nodeList.item( 0 );
					
					m_backgroundColor = backgroundElement.getAttribute( COLOR_ATTRIBUTE_NAME );
					m_backgroundImageName = backgroundElement.getAttribute( IMAGE_NAME_ATTRIBUTE_NAME );
					m_backgroundRepeat = backgroundElement.getAttribute( REPEAT_ATTRIBUTE_NAME );
				}
				
				// Get the <pageLayout ...> element
				nodeList = doc.getElementsByTagName( PAGE_LAYOUT_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element pageLayoutElement;
					
					pageLayoutElement = (Element) nodeList.item( 0 );
					
					m_hideMenu = Boolean.parseBoolean( pageLayoutElement.getAttribute( HIDE_MENU_ATTRIBUTE_NAME ) );
				}
				
				// Get the <header...> element
				nodeList = doc.getElementsByTagName( HEADER_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element headerElement;
					
					headerElement = (Element) nodeList.item( 0 );
					
					m_widgetStyles.setHeaderBgColor( headerElement.getAttribute( BG_COLOR_ATTRIBUTE_NAME ) );
					m_widgetStyles.setHeaderTextColor( headerElement.getAttribute( TEXT_COLOR_ATTRIBUTE_NAME ) );
				}
				
				// Get the <content...> element
				nodeList = doc.getElementsByTagName( CONTENT_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element contentElement;
					
					contentElement = (Element) nodeList.item( 0 );
					
					m_widgetStyles.setContentTextColor( contentElement.getAttribute( TEXT_COLOR_ATTRIBUTE_NAME ) );
				}
				
				// Get the <border...> element
				nodeList = doc.getElementsByTagName( BORDER_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element borderElement;
					
					borderElement = (Element) nodeList.item( 0 );
					
					m_widgetStyles.setBorderColor( borderElement.getAttribute( COLOR_ATTRIBUTE_NAME ) );
					m_widgetStyles.setBorderWidth( borderElement.getAttribute( WIDTH_ATTRIBUTE_NAME ) );
				}
				
				m_inheritProperties = false;
			}
			catch (DOMParseException ex)
			{
			}
		}
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
		m_widgetStyles.setBorderColor( color );
	}
	
	/**
	 * 
	 */
	public void setBorderWidth( String width )
	{
		m_widgetStyles.setBorderWidth( width );
	}
	
	/**
	 * 
	 */
	public void setContentTextColor( String color )
	{
		m_widgetStyles.setContentTextColor( color );
	}
	
	/**
	 * 
	 */
	public void setHeaderBgColor( String color )
	{
		m_widgetStyles.setHeaderBgColor( color );
	}
	
	/**
	 * 
	 */
	public void setHeaderTextColor( String color )
	{
		m_widgetStyles.setHeaderTextColor( color );
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
	public void setInheritProperties( boolean inherit )
	{
		m_inheritProperties = inherit;
	}
}
