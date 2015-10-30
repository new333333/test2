/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;

/**
 * This class is used to define what help documentation to display.
 * @author jwootton
 *
 */
public class HelpData
{
	public static final String USER_GUIDE = "user";
	public static final String ADV_USER_GUIDE = "adv_user";
	public static final String ADMIN_GUIDE = "admin";
	
	private String m_guideName;
	private String m_pageId;
	private String m_sectionId;

	// The following holds the language codes for all the languages that the online
	// documentation supports (except English, English does not need a language code)
	private final static String[] DOC_LANGS =
	{
		"cs-cz",
		"da-dk",
		"de-de",
		"es-es",
		"fr-fr",
		"hu-hu",
		"it-it",
		"ja-jp",
		"nl-nl",
		"pl-pl",
		"pt-br",
		"ru-ru",
		"sv-se",
		"zh-cn",
		"zh-tw",		
	};
	
	
	/**
	 * 
	 */
	public HelpData()
	{
		m_guideName = null;
		m_pageId = null;
		m_sectionId = null;
	}
	
	/**
	 * 
	 */
	public void setGuideName( String guideName )
	{
		m_guideName = guideName;
	}
	
	/**
	 * 
	 */
	public void setPageId( String pageId )
	{
		m_pageId = pageId;
	}
	
	/**
	 * 
	 */
	public void setSectionId( String sectionId )
	{
		m_sectionId = sectionId;
	}
	

	/**
	 * Return the language code that should be put on the help url.
	 */
	private String getLangCode()
	{
		String lang;
		String originalLang;
		int i;
		
		lang = null;
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		if ( ri != null )
			lang = ri.getLanguage();

		originalLang = lang;
		
		// Do we know the language? 
		if ( lang == null || lang.length() == 0 )
		{
			// No
			return null;
		}
		
		// Is the language English?
		if ( lang.indexOf( "en" ) == 0 )
		{
			// Yes, we don't need to put a language code on the url.
			return null;
		}

		// We only need the first two characters of the language to
		// localize the documentation URLs.
		if ( lang.length() > 2 )
		{
			lang = lang.substring( 0, 2 ); 
		}

		// Is the language Chinese?
		if ( lang.equalsIgnoreCase( "zh" ) )
		{
			// Yes, use the full language string of zh-tw or zh-cn
			lang = originalLang.toLowerCase();
			lang = lang.replace( '_', '-' );
		}

		// Look for the appropriate language code.
		for (i = 0; i < DOC_LANGS.length; ++i)
		{
			if ( DOC_LANGS[i].indexOf( lang ) == 0 )
			{
				break;
			}
		}		

		// Do we have a language code for this language?
		if ( i == DOC_LANGS.length )
		{
			// No
			return null;
		}

		return DOC_LANGS[i];
	}
	
	
	/**
	 * Return the url that points to the appropriate help documentation.
	 */
	public String getUrl()
	{
		String url;
		String lang;
		String guideComponent = null;
		String product;
		
		//~JW:  Get the base help url from ssf-ext.properties.
		url = "http://www.novell.com";
		
		// Do we have a language code to put on the url?
		lang = getLangCode();
		if ( lang != null && lang.length() > 0 )
		{
			// Yes
			url +=  "/" + lang;
		}
		
		url += "/documentation";

		product = "/vibe4";
		
		// Are we running Filr?
		boolean isFilr = GwtTeaming.m_requestInfo.isLicenseFilr();
		if ( isFilr )
		{
			// Yes
			url += "/novell-filr-2";
			product = "/filr-2";
		}
		// Are we running Novell Teaming?
		else if ( GwtMainPage.m_requestInfo.isNovellTeaming() )
		{
			// Yes
			url += "/vibe4";
		}
		else
			url += "/kablinkvibe4";
		
		String guideSeparator = (isFilr ? "-" : "_");
		if ( m_guideName != null && m_guideName.length() > 0 )
		{
			if ( m_guideName.equalsIgnoreCase( USER_GUIDE ) )
			{
				// Get the url to the user guide.
				guideComponent = product + guideSeparator + "user/data/";
			}
			else if ( m_guideName.equalsIgnoreCase( ADV_USER_GUIDE ) )
			{
				// Get the url to the advanced user guide.
				guideComponent = product + guideSeparator + "useradv/data/";
			}
			else if ( m_guideName.equalsIgnoreCase( ADMIN_GUIDE ) )
			{
				// Get the url to the administration guide.
				guideComponent = product + guideSeparator + "admin/data/";
			}
			else
				guideComponent = null;
			
			// Did we recognize the name of the guide?
			if ( guideComponent != null )
			{
				// Yes, add the guide component to the url.
				url += guideComponent;
				
				// Do we have a specific page to go to in the documentation?
				if ( m_pageId != null )
				{
					// Yes, each page has its own html file.
					url += m_pageId + ".html";
					
					// Do we have a specific section within the page to go to?
					if ( m_sectionId != null )
					{
						// Yes
						url += "#" + m_sectionId;
					}
				}
				else
				{
					// No, take the user to the start of the guide.
					url += "bookinfo.html";
				}
			}
		}

		return url;
	}
}
