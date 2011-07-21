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
	public final String COLOR_ATTRIBUTE_NAME = "color";
	public final String IMAGE_NAME_ATTRIBUTE_NAME = "imgName";
	public final String REPEAT_ATTRIBUTE_NAME = "repeat";
	
	private String m_backgroundColor;
	private String m_backgroundImageName;
	private String m_backgroundRepeat;
	
	/**
	 * 
	 */
	public LandingPageProperties( String propertiesXML )
	{
		m_backgroundColor = null;
		m_backgroundImageName = null;
		m_backgroundRepeat = null;
		
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
					
					backgroundElement = (Element) nodeList.item( 0 );
					
					m_backgroundColor = backgroundElement.getAttribute( COLOR_ATTRIBUTE_NAME );
					m_backgroundImageName = backgroundElement.getAttribute( IMAGE_NAME_ATTRIBUTE_NAME );
					m_backgroundRepeat = backgroundElement.getAttribute( REPEAT_ATTRIBUTE_NAME );
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
		m_backgroundRepeat = lpProperties.getBackgroundRepeat();
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
	public String getBackgroundRepeat()
	{
		return m_backgroundRepeat;
	}

	/**
	 * Return the properties as an xml string that looks like the following:
	 * <landingPageData>
	 * 	<background color="" imgName="" />
	 * </landingPageData>
	 */
	public String getPropertiesAsXMLString()
	{
		Document doc;
		Element rootElement;
		
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
		
		return doc.toString();
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
	public void setBackgroundRepeat( String repeat )
	{
		m_backgroundRepeat = repeat;
	}
}
