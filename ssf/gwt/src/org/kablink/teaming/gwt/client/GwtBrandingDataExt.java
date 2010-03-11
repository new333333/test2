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
package org.kablink.teaming.gwt.client;


import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to hold the data from the "brandingExt" field in the ss_forums table.
 * @author jwootton
 *
 */
public class GwtBrandingDataExt
	implements IsSerializable
{
	private static final long serialVersionUID = -7837015105719505381L;
	private String m_fontColor = null;
	private String m_brandingImgName = null;
	private String m_brandingImgUrl = null;
	private String m_backgroundColor = null;
	private String m_backgroundImgName = null;
	private String m_backgroundImgUrl = null;


	/**
	 * 
	 */
	public GwtBrandingDataExt()
	{
	}// end GwtBrandingDataExt()
	
	
	/**
	 * 
	 */
	public String getBackgroundColor()
	{
		return m_backgroundColor;
	}// end getBackgroundColor()
	

	/**
	 * 
	 */
	public String getBackgroundImgName()
	{
		return m_backgroundImgName;
	}// end getBackgroundImgName()
	

	/**
	 * 
	 */
	public String getBackgroundImgUrl()
	{
		return m_backgroundImgUrl;
	}// end getBackgroundImgUrl()
	

	/**
	 * Return the branding information as an xml string.  The following is an example of what the xml looks like:
	 * 	<brandingData fontColor="" brandingImgName="some name">
	 * 		<background color="" imgName="" />
	 * 	</brandingData>
	 */
	public String getBrandingExtAsXmlString()
	{
		StringBuffer xml;
		String fontColor;
		String brandingImgName;
		String bgColor;
		String bgImgName;

		xml = new StringBuffer();
    	
    	xml.append( "<brandingData" );
    	
    	// Add the fontColor attribute.
    	xml.append( " fontColor=\"" );
    	fontColor = getFontColor();
    	if ( fontColor != null && fontColor.length() > 0 )
    		xml.append( fontColor );
    	xml.append( "\"" );
    	
    	// Add the brandingImgName attribute.
    	xml.append( " brandingImgName=\"" );
    	brandingImgName = getBrandingImgName();
    	if ( brandingImgName != null && brandingImgName.length() > 0 )
    		xml.append( brandingImgName );
    	xml.append( "\"" );
    	
    	// Close the <brandingData tag.
    	xml.append( ">" );
    	
    	// Add the <background tag
    	xml.append( "<background " );
    	
    	// Add the color attribute.
    	xml.append( " color=\"" );
    	bgColor = getBackgroundColor();
    	if ( bgColor != null && bgColor.length() > 0 )
    		xml.append( bgColor );
    	xml.append( "\"" );
    	
    	// Add the imgName attribute
    	xml.append( " imgName=\"" );
    	bgImgName = getBackgroundImgName();
    	if ( bgImgName != null && bgImgName.length() > 0 )
    		xml.append( bgImgName );
    	xml.append( "\"" );
    	
    	// Close the <background tag
    	xml.append( "/>" );
    	
    	// Add the </brandingData> tag.
    	xml.append( "</brandingData>" );

    	return xml.toString();
	}// end getBrandingExtAsXmlString()
	
	
	/**
	 * 
	 */
	public String getBrandingImgName()
	{
		return m_brandingImgName;
	}// end getBrandingImgName()
	

	/**
	 * 
	 */
	public String getBrandingImgUrl()
	{
		return m_brandingImgUrl;
	}// end getBrandingImgUrl()
	

	/**
	 * 
	 */
	public String getFontColor()
	{
		return m_fontColor;
	}// end getFontColor()
	

	/**
	 * 
	 */
	public void setBackgroundColor( String backgroundColor )
	{
		m_backgroundColor = backgroundColor;
	}// end setBackgroundColor()


	/**
	 * 
	 */
	public void setBackgroundImgName( String imgName )
	{
		m_backgroundImgName = imgName;
	}// end setBackgroundImgName()


	/**
	 * 
	 */
	public void setBackgroundImgUrl( String imgUrl )
	{
		m_backgroundImgUrl = imgUrl;
	}// end setBackgroundImgUrl()


	/**
	 * 
	 */
	public void setBrandingImgName( String imgName )
	{
		m_brandingImgName = imgName;
	}// end setBrandingImgName()

	
	/**
	 * 
	 */
	public void setBrandingImgUrl( String imgUrl )
	{
		m_brandingImgUrl = imgUrl;
	}// end setBrandingImgUrl()

	
	/**
	 * 
	 */
	public void setFontColor( String color )
	{
		m_fontColor = color;
	}// end setFontColor()


	/**
	 * Wrap the given text with <![CDATA[ ]]>
	 */
	private String wrapWithCDATA( String str )
	{
		StringBuffer	wrappedStr;
		
		wrappedStr = new StringBuffer( "<![CDATA[" );
		if ( str != null && str.length() > 0 )
			wrappedStr.append( str );
		
		wrappedStr.append( "]]>" );
		
		return wrappedStr.toString();
	}// end wrapWithCDATA()

}// end GwtBrandingDataExt

