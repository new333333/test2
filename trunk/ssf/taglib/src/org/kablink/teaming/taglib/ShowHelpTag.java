/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Jay Wootton
 */
@SuppressWarnings("serial")
public class ShowHelpTag extends BodyTagSupport
{
	private String guideName;
	private String pageId;
	private String sectionId;
	private String className;
	@SuppressWarnings("unused")
	private String _bodyContent;

	public static final String USER_GUIDE = "user";
	public static final String ADV_USER_GUIDE = "adv_user";
	public static final String ADMIN_GUIDE = "admin";

	// The following holds the language codes for all the languages that the online
	// documentation supports (except English, English does not need a language code)
	private final static String[] DOC_LANGS =
	{
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
	public int doStartTag()
	{
		return EVAL_BODY_BUFFERED;
	}// end doStartTag()

	/**
	 * 
	 */
	public int doAfterBody()
	{
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}// end doAfterBody()

	/**
	 * 
	 */
	public int doEndTag() throws JspException
	{
		try
		{
			HttpServletRequest httpReq;
			HttpServletResponse httpRes;
			
			httpReq = (HttpServletRequest) pageContext.getRequest();
			httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if ( this.guideName != null )
			{
				String url;
				String jsp;				
				RequestDispatcher rd;	
				ServletRequest req;	
				StringServletResponse res;	
				
				// Construct a url that points to the appropriate documentation for the
				// given guide name, page id and section id.
				url = getHelpUrl( guideName, pageId, sectionId );
				
				jsp = "/WEB-INF/jsp/tag_jsps/inline_help/show_help.jsp";				
				rd = httpReq.getRequestDispatcher( jsp );	
				req = pageContext.getRequest();	
				res = new StringServletResponse( httpRes );	
				req.setAttribute( "helpUrl", url );
				req.setAttribute( "className", this.className );
				rd.include( req, res );	
				pageContext.getOut().print( res.getString() );
			}

			return EVAL_PAGE;
		}
	    catch(Exception e)
	    {
	        throw new JspException(e); 
	    }
		finally
		{
			guideName = null;
			pageId = null;
			sectionId = null;
		}
	}// end doEndTag()

	/**
	 * Return the url that points to the appropriate help documentation.
	 */
	public String getHelpUrl( String guideName, String pageId, String sectionId )
	{
		String url;
		String lang;
		String guideComponent = null;
		
		// Get the base help url from ssf-ext.properties.
		url = SPropsUtil.getString( "help.hostName", "http://www.novell.com" );
		
		// Do we have a language code to put on the url?
		lang = getHelpLangCode();
		if ( lang != null && lang.length() > 0 )
		{
			// Yes
			url +=  "/" + lang;
		}
		
		url += "/documentation";
		
		// Are we running Novell Teaming?
		if ( ReleaseInfo.isLicenseRequiredEdition())
		{
			// Yes
			url += "/vibe_onprem31";
		}
		else
			url += "/kablinkvibe_onprem31";
		
		if ( guideName != null && guideName.length() > 0 )
		{
			if ( guideName.equalsIgnoreCase( USER_GUIDE ) )
			{
				// Get the url to the user guide.
				guideComponent = "/vibeprem31_user/data/";
			}
			else if ( guideName.equalsIgnoreCase( ADV_USER_GUIDE ) )
			{
				// Get the url to the advanced user guide.
				guideComponent = "/vibeprem31_useradv/data/";
			}
			else if ( guideName.equalsIgnoreCase( ADMIN_GUIDE ) )
			{
				// Get the url to the administration guide.
				guideComponent = "/vibeprem31_admin/data/";
			}
			else
				guideComponent = null;
			
			// Did we recognize the name of the guide?
			if ( guideComponent != null )
			{
				// Yes, add the guide component to the url.
				url += guideComponent;
				
				// Do we have a specific page to go to in the documentation?
				if ( pageId != null )
				{
					// Yes, each page has its own html file.
					url += pageId + ".html";
					
					// Do we have a specific section within the page to go to?
					if ( sectionId != null )
					{
						// Yes
						url += "#" + sectionId;
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

	/**
	 * Return the language code that should be put on the help url.
	 */
	private String getHelpLangCode()
	{
		String lang;
		int i;
		
		// Get the language the user is running in
		lang = NLT.get( "Teaming.Lang", "" );
		
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
	 * 
	 */
	public void setClassName( String className )
	{
	    this.className = className;
	}// end setClassName()


	/**
	 * 
	 */
	public void setGuideName( String guideName )
	{
	    this.guideName = guideName;
	}// end setGuideName()


	/**
	 * 
	 */
	public void setPageId( String pageId )
	{
	    this.pageId = pageId;
	}// end setPageId()

	/**
	 * 
	 */
	public void setSectionId( String sectionId )
	{
	    this.sectionId = sectionId;
	}// end setSectionId
}// end ShowHelpTag
