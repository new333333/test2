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
 * This class is used to hold branding data.
 * @author jwootton
 *
 */
public class GwtBrandingData
	implements IsSerializable
{
	// The binder this branding data is from.  If m_binderId is null then the branding data is global
	private String m_binderId = null;
	
	// Branding data read from the db.  The data is html.
	private String m_html = null;
	
	// Color of the font to be used to display the user's name in the masthead.
	private String m_fontColor = "";
	
	// Color to be used as the background color of the masthead.
	private String m_bgColor = "";
	
	// Image to be used as the background of the masthead.
	private String m_bgImg = null;
	
	
	/**
	 * 
	 */
	public GwtBrandingData()
	{
		m_binderId = null;
		m_html = null;
	}// end GwtBrandingData()
	
	
	/**
	 * Return the color to be used as the background color of the masthead. 
	 */
	public String getBgColor()
	{
		return m_bgColor;
	}// end getBgColor()
	
	/**
	 * Return the image to be used as the background of the masthead. 
	 */
	public String getBgImage()
	{
		return m_bgImg;
	}// end getBgImg()
	
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
	 * Return the color of the font the be used to display the user's name in the masthead.
	 */
	public String getFontColor()
	{
		return m_fontColor;
	}// end getFontColor()
	
	
	/**
	 * Set the color to be used as the background color in the masthead.
	 */
	public void setBgColor( String color )
	{
		m_bgColor = color;
	}// end setBgColor()
	

	/**
	 * Set the image to be used as the background image in the masthead.
	 */
	public void setBgImage( String bgImg )
	{
		m_bgImg = bgImg;
	}// end setBgImage()
	

	/**
	 * Set the branding html.
	 */
	public void setBranding( String html )
	{
		m_html = html;
	}// end setBranding()
	

	/**
	 * Set the color of the font to be used to display the user's name in the masthead.
	 */
	public void setFontColor( String fontColor )
	{
		m_fontColor = fontColor;
	}// end setFontColor()
	
	
}// end GwtBrandingData
