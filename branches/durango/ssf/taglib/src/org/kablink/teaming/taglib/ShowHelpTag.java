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

import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Jay Wootton
 *
 */
public class ShowHelpTag extends BodyTagSupport
{
	private String guideName;
	private String pageId;
	private String sectionId;
	private String className;
	private String _bodyContent;
    
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
				
				if ( this.guideName.equalsIgnoreCase( "user" ) )
				{
					// Get the url to the user guide.
					url = SPropsUtil.getString( "help.userGuide", "http://www.novell.com/documentation/vibe_onprem3/vibeprem3_user/data/" );
				}
				else if ( this.guideName.equalsIgnoreCase( "adv_user" ) )
				{
					// Get the url to the advanced user guide.
					url = SPropsUtil.getString( "help.advancedUserGuide", "http://www.novell.com/documentation/vibe_onprem3/vibeprem3_useradv/data/" );
				}
				else if ( this.guideName.equalsIgnoreCase( "admin" ) )
				{
					// Get the url to the administration guide.
					url = SPropsUtil.getString( "help.adminGuide", "http://www.novell.com/documentation/vibe_onprem3/vibeprem3_admin/data/" );
				}
				else
					url = null;
				
				if ( url != null )
				{
					// Do we have a specific page to go to in the documentation?
					if ( this.pageId != null )
					{
						// Yes, each page has its own html file.
						url += this.pageId + ".html";
						
						// Do we have a specific section within the page to go to?
						if ( this.sectionId != null )
						{
							// Yes
							url += "#" + this.sectionId;
						}
					}
					else
					{
						// No, take the user to the start of the guide.
						url += "bookinfo.html";
					}
				}
				else
				{
					// If we get here we didn't recognize the name of the guide.  Take the user
					// to the main Teaming documentation site.
					url = "http://www.novell.com/documentation/vibe_onprem3";
				}

				// If we have a help URL...
				if (MiscUtil.hasString(url)) {
					// ...make sure it contains any required localizations.
					url = MiscUtil.localizeHelpUrl(url);
				}
				
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
