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
package org.kablink.teaming.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * This class wraps a JavaScript object that holds information about the request we are working with.
 * @author jwootton
 *
 */
public class RequestInfo extends JavaScriptObject
{
	/**
	 * Overlay types always have a protected, zero-arg constructors.
	 */
	protected RequestInfo()
	{
	}// end RequestInfo()

	
	/**
	 * Return the adapted url.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getAdaptedUrl() /*-{ return this.adaptedUrl; }-*/;


	/**
	 * Return the binder id.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getBinderId() /*-{ return this.binderId; }-*/;
	
	
	/**
	 * Return the url for the css used with the tinyMCE editor.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getContentCss() /*-{ return this.contentCss; }-*/;
	
	
	/**
	 * Return the error message we should display.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getErrMsg() /*-{ return this.errMsg; }-*/;
	
	
	/**
	 * Return the url used for simple searches.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getSimpleSearchUrl() /*-{ return this.simpleSearchUrl; }-*/;
	
	
	/**
	 * Return the url used for advanced searches.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getAdvancedSearchUrl() /*-{ return this.advancedSearchUrl; }-*/;
	
	
	/**
	 * Return the url used for saved searches.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getSavedSearchUrl() /*-{ return this.savedSearchUrl; }-*/;
	
	
	/**
	 * Return the url used for recent place searches.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getRecentPlaceSearchUrl() /*-{ return this.recentPlaceSearchUrl; }-*/;
	
	
	/**
	 * Return the path to Teaming's images.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getImagesPath() /*-{ return this.imagesPath; }-*/;

	
	/**
	 * Return the user's language.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getLanguage() /*-{ return this.language; }-*/;

	
	/**
	 * Return the error message from the last login.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getLoginError() /*-{ return this.loginError; }-*/;
	
	
	/**
	 * Return the referer url that was passed as part of the login info.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getLoginRefererUrl() /*-{ return this.loginRefererUrl; }-*/;
	
	
	/**
	 * Return the url we should use when we are trying to log in.
	 */
	public final native String getLoginUrl() /*-{ return this.loginPostUrl; }-*/;


	/**
	 * Return the "my workspace" url.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getMyWorkspaceUrl() /*-{ return this.myWSUrl; }-*/;

	
	/**
	 * Return the user's name.  This class is an overlay on the JavaScript object called m_requestInfo.
	 */
	public final native String getUserName() /*-{ return this.userName; }-*/;


	/**
	 * Return the flag that tells us if we are running Novell Teaming.
	 */
	public final native boolean isNovellTeaming() /*-{ return this.isNovellTeaming; }-*/;


	/**
	 * Return the flag that tells us if the user is logged in.
	 */
	public final native boolean isUserLoggedIn() /*-{ return this.isUserLoggedIn; }-*/;


	/**
	 * Return the flag that tells us if we should prompt for login
	 */
	public final native boolean promptForLogin() /*-{ return this.promptForLogin; }-*/;

	/**
	 * Return the url needed to invoke the "Teaming Feed" page.
	 */
	public final native String getTeamingFeedUrl() /*-{ return this.teamingFeedUrl; }-*/;
	
}// end RequestInfo
