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
	/**
	 * This class defines all the possible values of a branding rule.
	 */
	public enum BrandingRule implements IsSerializable
	{
		DISPLAY_SITE_BRANDING_ONLY,
		DISPLAY_BOTH_SITE_AND_BINDER_BRANDING,
		BINDER_BRANDING_OVERRIDES_SITE_BRANDING,
		BRANDING_RULE_UNDEFINED;
	}// end BrandingRule

	public static final String BRANDING_TYPE_ADVANCED = "advanced";
	public static final String BRANDING_TYPE_IMAGE = "image";
	
	private static final long serialVersionUID = -7837015105719505381L;
	private String m_fontColor = null;
	private String m_brandingImgName = null;
	private String m_brandingImgUrl = null;
	private String m_backgroundColor = null;
	private String m_backgroundImgName = null;
	private String m_backgroundImgUrl = null;
	private boolean m_stretchBgImg = false;
	private String m_brandingType = BRANDING_TYPE_ADVANCED;
	private String m_loginDlgImgName = null;
	private String m_loginDlgImgUrl = null;

	// m_brandingRule indicates how site and binder branding are to be displayed
	private BrandingRule m_brandingRule = BrandingRule.BRANDING_RULE_UNDEFINED;
	

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
	 * Return whether or not the background img should be stretched. 
	 */
	public boolean getBackgroundImgStretchValue()
	{
		return m_stretchBgImg;
	}// end getBackgroundImgStretchValue()
	
	
	/**
	 * 
	 */
	public String getBackgroundImgUrl()
	{
		return m_backgroundImgUrl;
	}// end getBackgroundImgUrl()
	

	/**
	 * Return the branding information as an xml string.  The following is an example of what the xml looks like:
	 * 	<brandingData fontColor="" brandingImgName="some name" brandingType="image/advanced" brandingRule="">
	 * 		<background color="" imgName="" stretchImg="true/false" />
	 * 	</brandingData>
	 */
	public String getBrandingExtAsXmlString()
	{
		StringBuffer xml;
		String fontColor;
		String brandingImgName;
		String loginDlgImgName;
		String bgColor;
		String bgImgName;
		String type;
		boolean stretch;

		// Has any branding been defined?
		if ( haveBranding() == false )
		{
			// No, return an empty string.
			return "";
		}
		
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
    	{
    		// Replace '&' with "&amp;"
    		brandingImgName = brandingImgName.replaceAll( "&", "&amp;" );
    		xml.append( brandingImgName );
    	}
    	xml.append( "\"" );
    	
    	// Add the brandingType attribute.
    	xml.append( " brandingType=\"" );
    	type = getBrandingType();
    	if ( type != null && type.length() > 0 )
    		xml.append( type );
    	xml.append( "\"" );
    	
    	// Add the brandingRule attribute.
    	xml.append( " brandingRule=\"" );
    	xml.append( m_brandingRule.toString() );
    	xml.append( "\"" );
    	
    	// Add the loginDlgImgName attribute.
    	xml.append( " loginDlgImgName=\"" );
    	loginDlgImgName = getLoginDlgImgName();
    	if ( loginDlgImgName != null && loginDlgImgName.length() > 0 )
    	{
    		// Replace '&' with "&amp;"
    		loginDlgImgName = loginDlgImgName.replaceAll( "&", "&amp;" );
    		xml.append( loginDlgImgName );
    	}
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
    	{
    		// Replace '&' with "&amp;"
    		bgImgName = bgImgName.replaceAll( "&", "&amp;" );
    		xml.append( bgImgName );
    	}
    	xml.append( "\"" );
    	
    	// Add the stretchImg attribute
    	xml.append( " stretchImg=\"" );
    	stretch = getBackgroundImgStretchValue();
   		xml.append( Boolean.toString( stretch ) );
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
	 * Return the rule for displaying site and binder branding
	 */
	public BrandingRule getBrandingRule()
	{
		return m_brandingRule;
	}// end getBrandingRule()
	
	
	/**
	 * 
	 */
	public String getBrandingType()
	{
		return m_brandingType;
	}// end getBrandingType();
	
	/**
	 * 
	 */
	public String getLoginDlgImgName()
	{
		return m_loginDlgImgName;
	}
	
	/**
	 * Return the url for the image used by the login dialog
	 */
	public String getLoginDlgImgUrl()
	{
		return m_loginDlgImgUrl;
	}
	
	/**
	 * Return whether or not we have any branding data.
	 */
	public boolean haveBranding()
	{
		if ( m_fontColor != null && m_fontColor.length() > 0 )
			return true;
		
		if ( m_brandingImgName != null && m_brandingImgName.length() > 0 )
			return true;
		
		if ( m_backgroundColor != null && m_backgroundColor.length() > 0 )
			return true;
		
		if ( m_backgroundImgName != null && m_backgroundImgName.length() > 0 )
			return true;
		
		if ( m_brandingRule != BrandingRule.BRANDING_RULE_UNDEFINED )
			return true;
		
		// If we get here we don't have any branding data.
		return false;
	}// end haveBranding()
	

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
	 * Set whether or not the background img should be stretched. 
	 */
	public void setBackgroundImgStretchValue( boolean stretch )
	{
		m_stretchBgImg = stretch;
	}// end setBackgroundImgStretchValue()
	
	
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
	 * Set the rule for displaying site and binder branding
	 */
	public void setBrandingRule( BrandingRule brandingRule )
	{
		m_brandingRule = brandingRule;
	}// end setBrandingRule()
	

	/**
	 * 
	 */
	public void setBrandingType( String type )
	{
		m_brandingType = type;
	}// end setBrandingType()

	
	/**
	 * 
	 */
	public void setFontColor( String color )
	{
		m_fontColor = color;
	}// end setFontColor()

	/**
	 * 
	 */
	public void setLoginDlgImgName( String imgName )
	{
		m_loginDlgImgName = imgName;
	}


	/**
	 * 
	 */
	public void setLoginDlgImgUrl( String imgUrl )
	{
		m_loginDlgImgUrl = imgUrl;
	}
	
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

