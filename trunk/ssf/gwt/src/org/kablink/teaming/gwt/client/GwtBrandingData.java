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


import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class is used to hold branding data.
 * @author jwootton
 *
 */
public class GwtBrandingData
	implements IsSerializable, VibeRpcResponseData
{
	// The binder this branding data is from.
	private String m_binderId = null;
	
	// Branding data read from the db.  The data is html.
	private String m_html = null;
	
	// Additional branding information such as font color, branding image url, background color and background image url.
	private GwtBrandingDataExt m_brandingExt = null;
	
	// Flag that indicates whether this branding is the site branding (came from the "home workspace")
	private boolean m_isSiteBranding = false;
	
	
	/**
	 * 
	 */
	public GwtBrandingData()
	{
		m_binderId = null;
		m_html = null;
		m_brandingExt = new GwtBrandingDataExt();
	}// end GwtBrandingData()
	
	
	/**
	 * Return the color to be used as the background color of the masthead. 
	 */
	public String getBgColor()
	{
		return m_brandingExt.getBackgroundColor();
	}// end getBgColor()
	
	/**
	 * Return the name of the image to be used as the background of the masthead. 
	 */
	public String getBgImageName()
	{
		return m_brandingExt.getBackgroundImgName();
	}// end getBgImageName()
	

	/**
	 * Return whether or not to stretch the background image.
	 */
	public boolean getBgImageStretchValue()
	{
		return m_brandingExt.getBackgroundImgStretchValue();
	}// end getBgImageStretchValue()
	
	
	/**
	 * Return the image to be used as the background of the masthead. 
	 */
	public String getBgImageUrl()
	{
		return m_brandingExt.getBackgroundImgUrl();
	}// end getBgImageUrl()
	
	/**
	 * 
	 */
	public String getBinderId()
	{
		return m_binderId;
	}// end getBinderId()
	
	
	/**
	 * Return the branding as html.
	 */
	public String getBranding()
	{
		return m_html;
	}// end setBranding()
	
	
	/**
	 * Return the extened branding as an xml string.
	 */
	public String getBrandingAsXmlString()
	{
		return m_brandingExt.getBrandingExtAsXmlString();
	}// end getBrandingAsXmlString()
	
	
	/**
	 * Return the name of the image to be used as the branding. 
	 */
	public String getBrandingImageName()
	{
		return m_brandingExt.getBrandingImgName();
	}// end getBrandingImgName()
	
	/**
	 * Return the image to be used as the branding. 
	 */
	public String getBrandingImageUrl()
	{
		return m_brandingExt.getBrandingImgUrl();
	}// end getBrandingImgUrl()
	
	/**
	 * Return the rule for displaying site and binder branding
	 */
	public GwtBrandingDataExt.BrandingRule getBrandingRule()
	{
		return m_brandingExt.getBrandingRule();
	}// end getBrandingRule()
	
	
	/**
	 * Return the type of branding, "advanced" or "image" 
	 */
	public String getBrandingType()
	{
		return m_brandingExt.getBrandingType();
	}// end getBrandingType()
	
	/**
	 * Return the color of the font the be used to display the user's name in the masthead.
	 */
	public String getFontColor()
	{
		return m_brandingExt.getFontColor();
	}// end getFontColor()
	
	
	/**
	 * Return the name of the image to be used in the login dialog. 
	 */
	public String getLoginDlgImageName()
	{
		return m_brandingExt.getLoginDlgImgName();
	}
	
	/**
	 * Return the url for the image to be used in the login dialog. 
	 */
	public String getLoginDlgImageUrl()
	{
		return m_brandingExt.getLoginDlgImgUrl();
	}
	
	/**
	 * Return whether or not we have any branding data.
	 */
	public boolean haveBranding()
	{
		if ( m_html != null && m_html.length() > 0 )
			return true;
		
		if ( m_brandingExt != null && m_brandingExt.haveBranding() )
			return true;
		
		// If we get here we don't have any branding data.
		return false;
	}// haveBranding()
	
	
	/**
	 * Return the flag that indicates whether this branding is the "site" branding.
	 */
	public boolean isSiteBranding()
	{
		return m_isSiteBranding;
	}// end isSiteBranding()
	
	
	/**
	 * Set the color to be used as the background color in the masthead.
	 */
	public void setBgColor( String color )
	{
		m_brandingExt.setBackgroundColor( color );
	}// end setBgColor()
	

	/**
	 * Set the image to be used as the background image in the masthead.
	 */
	public void setBgImageUrl( String bgImgUrl )
	{
		m_brandingExt.setBackgroundImgUrl( bgImgUrl );
	}// end setBgImageUrl()
	
	
	/**
	 * Set the name of the image to be used as the background image in the masthead.
	 */
	public void setBgImageName( String bgImgName )
	{
		m_brandingExt.setBackgroundImgName( bgImgName );
	}// end setBgImageName()
	
	
	/**
	 * Set whether or not to stretch the background image.
	 */
	public void setBgImageStretchValue( boolean stretch )
	{
		m_brandingExt.setBackgroundImgStretchValue( stretch );
	}// end setBgImageStretchValue()
	
	
	/**
	 * Set the binder id we are working with.
	 */
	public void setBinderId( String binderId )
	{
		m_binderId = binderId;
	}// end setBinderId()
	

	/**
	 * Set the branding html.
	 */
	public void setBranding( String html )
	{
		m_html = html;
	}// end setBranding()
	

	/**
	 * 
	 */
	public void setBrandingExt( GwtBrandingDataExt brandingExt )
	{
		m_brandingExt = brandingExt;
	}// end setBrandingExt()
	
	
	/**
	 * Set the name of the image to be used as the branding. 
	 */
	public void setBrandingImageName( String imgName )
	{
		m_brandingExt.setBrandingImgName( imgName );
	}// end setBrandingImgName()
	
	/**
	 * Set the image to be used as the branding.
	 */
	public void setBrandingImageUrl( String brandingImgUrl )
	{
		m_brandingExt.setBrandingImgUrl( brandingImgUrl );
	}// end setBrandingImageUrl()
	

	/**
	 * Set the rule for displaying site and binder branding
	 */
	public void setBrandingRule( GwtBrandingDataExt.BrandingRule brandingRule )
	{
		m_brandingExt.setBrandingRule( brandingRule );
	}// end setBrandingRule()
	
	/**
	 * Set the type of branding, "advanced" or "image" 
	 */
	public void setBrandingType( String type )
	{
		m_brandingExt.setBrandingType( type );
	}// end setBrandingType()
	
	/**
	 * Set the color of the font to be used to display the user's name in the masthead.
	 */
	public void setFontColor( String fontColor )
	{
		m_brandingExt.setFontColor( fontColor );
	}// end setFontColor()
	
	/**
	 * Set the flag that indicates whether this branding is used for the "site" branding.
	 */
	public void setIsSiteBranding( boolean isSiteBranding )
	{
		m_isSiteBranding = isSiteBranding;
	}// end setIsSiteBranding()

	/**
	 * Set the name of the image to be used in the login dialog. 
	 */
	public void setLoginDlgImageName( String imgName )
	{
		m_brandingExt.setLoginDlgImgName( imgName );
	}
	
	/**
	 * Set the url of the image to be used login dialog.
	 */
	public void setLoginDlgImageUrl( String loginDlgImgUrl )
	{
		m_brandingExt.setLoginDlgImgUrl( loginDlgImgUrl );
	}
	
}// end GwtBrandingData
