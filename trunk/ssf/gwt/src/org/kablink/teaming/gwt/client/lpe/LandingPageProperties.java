package org.kablink.teaming.gwt.client.lpe;

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
{
	public final String ROOT_ELEMENT_NAME = "landingPageData";
	public final String BACKGROUND_ELEMENT_NAME = "background";
	public final String FONT_ELEMENT_NAME = "font";
	public final String COLOR_ATTRIBUTE_NAME = "color";
	public final String IMAGE_NAME_ATTRIBUTE_NAME = "imgName";
	public final String STRETCH_IMAGE_ATTRIBUTE_NAME = "stretchImg";
	
	private String m_backgroundColor;
	private String m_backgroundImageName;
	private String m_fontColor;
	private boolean m_stretchImg;
	
	/**
	 * 
	 */
	public LandingPageProperties( String propertiesXML )
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_fontColor = null;
		m_stretchImg = false;
		
		if ( propertiesXML != null )
		{
			try
			{
				Document doc;
				NodeList nodeList;
				
				doc = XMLParser.parse( propertiesXML );
				
				// Get the <background ...> element.
				nodeList = doc.getElementsByTagName( BACKGROUND_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element backgroundElement;
					String value;
					
					backgroundElement = (Element) nodeList.item( 0 );
					
					m_backgroundColor = backgroundElement.getAttribute( COLOR_ATTRIBUTE_NAME );
					m_backgroundImageName = backgroundElement.getAttribute( IMAGE_NAME_ATTRIBUTE_NAME );
					
					value = backgroundElement.getAttribute( STRETCH_IMAGE_ATTRIBUTE_NAME );
					if ( value != null && value.equalsIgnoreCase( "true" ) )
						m_stretchImg = true;
				}

				// Get the <font ...> element.
				nodeList = doc.getElementsByTagName( FONT_ELEMENT_NAME );
				if ( nodeList != null && nodeList.getLength() == 1 )
				{
					Element fontElement;
					
					fontElement = (Element) nodeList.item( 0 );
					
					m_fontColor = fontElement.getAttribute( COLOR_ATTRIBUTE_NAME );
				}
			}
			catch (DOMParseException ex)
			{
				
			}
		}
	}
	
	/**
	 * 
	 */
	public void copy( LandingPageProperties lpProperties )
	{
		m_backgroundColor = lpProperties.getBackgroundColor();
		m_backgroundImageName = lpProperties.getBackgroundImageName();
		m_fontColor = lpProperties.getFontColor();
		m_stretchImg = lpProperties.getStretchImg();
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
	public String getFontColor()
	{
		return m_fontColor;
	}
	
	/**
	 * Return the properties as an xml string that looks like the following:
	 * <landingPageData>
	 * 	<background color="" imgName="" stretchImg="true | false" />
	 * </landingPageData>
	 */
	public String getPropertiesAsXMLString()
	{
		Document doc;
		Element rootElement;
		
		doc = XMLParser.createDocument();
		
		rootElement = doc.createElement( ROOT_ELEMENT_NAME );
		doc.appendChild( rootElement );
		
		// Add the <background color="" imgName="" stretchImg=""> element
		{
			Element backgroundElement;

			backgroundElement = doc.createElement( BACKGROUND_ELEMENT_NAME );
			
			if ( m_backgroundColor != null )
				backgroundElement.setAttribute( COLOR_ATTRIBUTE_NAME, m_backgroundColor );
			
			if ( m_backgroundImageName != null )
				backgroundElement.setAttribute( IMAGE_NAME_ATTRIBUTE_NAME, m_backgroundImageName );
			
			backgroundElement.setAttribute( this.STRETCH_IMAGE_ATTRIBUTE_NAME, Boolean.toString( m_stretchImg ) );
			
			rootElement.appendChild( backgroundElement );
		}
		
		// Add the <font color="" /> element
		{
			Element fontElement;
			
			fontElement = doc.createElement( FONT_ELEMENT_NAME );
			
			if ( m_fontColor != null )
				fontElement.setAttribute( COLOR_ATTRIBUTE_NAME, m_fontColor );
			
			rootElement.appendChild( fontElement );
		}
		return doc.toString();
	}
	
	/**
	 * 
	 */
	public boolean getStretchImg()
	{
		return m_stretchImg;
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
	public void setFontColor( String color )
	{
		m_fontColor = color;
	}
	
	/**
	 * 
	 */
	public void setStretchImg( boolean stretch )
	{
		m_stretchImg = stretch;
	}
}
