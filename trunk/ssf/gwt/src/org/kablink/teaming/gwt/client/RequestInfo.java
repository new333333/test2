/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.VibeProduct;

import com.google.gwt.core.client.JavaScriptObject;


/**
 * This class wraps a JavaScript object that holds information about
 * the request we are working with.
 * 
 * @author jwootton
 */
public class RequestInfo extends JavaScriptObject
{
	/**
	 * Overlay types always have a protected, zero argument
	 * constructor.
	 */
	protected RequestInfo()
	{
	}// end RequestInfo()
	
	/**
	 * Return the the session captive state.
	 * 
	 * @return
	 */
	public final native boolean isSessionCaptive()
	/*-{
		return this.getBFromS( this.sessionCaptive );
	}-*/;

	/**
	 * Return the adapted URL.
	 * 
	 * @return
	 */
	public final native String getAdaptedUrl()
	/*-{
		return this.adaptedUrl;
	}-*/;

	/**
	 * Return the base Vibe URL.
	 * 
	 * @return
	 */
	public final native String getBaseVibeUrl()
	/*-{
		return this.baseVibeUrl;
	}-*/;

	/**
	 * Return the binder ID.
	 * 
	 * @return
	 */
	public final native String getBinderId()
	/*-{
		return this.binderId;
	}-*/;
	
	/**
	 * Return the get the current user's workspaceId
	 * 
	 * @return
	 */
	public final native String getCurrentUserWorkspaceId()
	/*-{
		return this.currentUserWorkspaceId;
	}-*/;
	
	/**
	 * Return the URL for the CSS used with the tinyMCE editor.
	 * 
	 * @return
	 */
	public final native String getContentCss()
	/*-{
		return this.contentCss;
	}-*/;

	/**
	 * Return whether the UI should perform extra debug checking
	 * on landing pages.
	 * 
	 * @return
	 */
	public final native boolean isDebugLP()
	/*-{
		return this.getBFromS( this.debugLP );
	}-*/;
	
	/**
	 * Return whether the UI should perform extra debug checking and
	 * debug message displays. 
	 * 
	 * @return
	 */
	public final native boolean isDebugUI()
	/*-{
		return this.getBFromS( this.debugUI );
	}-*/;
	
	/**
	 * Return the error message we should display.
	 * 
	 * @return
	 */
	public final native String getErrMsg()
	/*-{
		return this.errMsg;
	}-*/;
	
	/**
	 * Return the URL used for simple searches.
	 * 
	 * @return
	 */
	public final native String getSimpleSearchUrl()
	/*-{
		return this.simpleSearchUrl;
	}-*/;
	
	/**
	 * Return the URL used for advanced searches.
	 * 
	 * @return
	 */
	public final native String getAdvancedSearchUrl()
	/*-{
		return this.advancedSearchUrl;
	}-*/;
	
	/**
	 * Return the URL used for saved searches.
	 * 
	 * @return
	 */
	public final native String getSavedSearchUrl()
	/*-{
		return this.savedSearchUrl;
	}-*/;
	
	/**
	 * Return the URL used for recent place searches.
	 * 
	 * @return
	 */
	public final native String getRecentPlaceSearchUrl()
	/*-{
		return this.recentPlaceSearchUrl;
	}-*/;
	
	/**
	 * Return the URL used for the help system.
	 * 
	 * @return
	 */
	public final native String getHelpUrl()
	/*-{
		return this.helpUrl;
	}-*/;
	
	/**
	 * Return the path to Teaming's images.
	 * 
	 * @return
	 */
	public final native String getImagesPath()
	/*-{
		return this.imagesPath;
	}-*/;

	/**
	 * Return the path to Teaming's JavaScript.
	 * 
	 * @return
	 */
	public final native String getJSPath()
	/*-{
		return this.jsPath;
	}-*/;

	/**
	 * Return the user's language.
	 * 
	 * @return
	 */
	public final native String getLanguage()
	/*-{
		return this.language;
	}-*/;

	/**
	 * Return the user's locale.
	 * 
	 * @return
	 */
	public final native String getLocale()
	/*-{
		return this.locale;
	}-*/;
	
	/**
	 * Return the user's time zone.
	 * 
	 * @return
	 */
	public final native String getTimeSone()
	/*-{
		return this.timeZone;
	}-*/;

	/**
	 * Return the error message from the last login.
	 * 
	 * @return
	 */
	public final native String getLoginError()
	/*-{
		return this.loginError;
	}-*/;
	
	/**
	 * Return the referrer URL that was passed as part of the login
	 * info.
	 * 
	 * @return
	 */
	public final native String getLoginRefererUrl()
	/*-{
		return this.loginRefererUrl;
	}-*/;
	
	/**
	 * Return the URL we should use when we are trying to log in.
	 * 
	 * @return
	 */
	public final native String getLoginUrl()
	/*-{
		return this.loginPostUrl;
	}-*/;

	/**
	 * Return the My Workspace URL.
	 * 
	 * @return
	 */
	public final native String getMyWorkspaceUrl()
	/*-{
		return this.myWSUrl;
	}-*/;
	
	/**
	 * Return the language that the tinyMCE editor should use.
	 * 
	 * @return
	 */
	public final native String getTinyMCELanguage()
	/*-{
		return this.tinyMCELang;
	}-*/;

	/**
	 * Return the user's id.
	 * 
	 * @return
	 */
	public final native String getUserId()
	/*-{
		return this.userId;
	}-*/;

	/**
	 * Return the user's login id.
	 * 
	 * @return
	 */
	public final native String getUserLoginId()
	/*-{
		return this.userLoginId;
	}-*/;

	/**
	 * Return the user's name.
	 * 
	 * @return
	 */
	public final native String getUserName()
	/*-{
		return this.userName;
	}-*/;

	/**
	 * Return the flag that tells us what product we're running.
	 * 
	 * @return
	 */
	public final native String getVibeProductString()
	/*-{
		return this.vibeProduct;
	}-*/;
	
	public final VibeProduct getVibeProduct() {
		VibeProduct reply = VibeProduct.OTHER;
		String vp = getVibeProductString();
		if (GwtClientHelper.hasString(vp)) {
			reply = VibeProduct.valueOf(Integer.parseInt(vp));
		}
		return reply;
	}

	/**
	 * Return the flag that tells us if we are running Novell Vibe.
	 * 
	 * @return
	 */
	public final native boolean isNovellTeaming()
	/*-{
		return this.getBFromS( this.isNovellTeaming );
	}-*/;
	
	public final boolean isNovellVibe() {
		return isNovellTeaming();
	}

	/**
	 * Return the flag that tells us if we are running Vibe Lite.
	 * 
	 * @return
	 */
	public final native boolean isVibeLite()
	/*-{
		return (this.getBFromS( this.isVibeLite ));
	}-*/;

	/**
	 * Return the flag that tells us if we are running Kablink Vibe.
	 * 
	 * @return
	 */
	public final native boolean isKablinkTeaming()
	/*-{
		return (!(this.getBFromS( this.isNovellTeaming )));
	}-*/;
	
	public final boolean isKablinkVibe() {
		return isKablinkTeaming();
	}

	/**
	 * Return the name (Novell vs. Kablink) of the version of Teaming
	 * that's running.
	 * 
	 * @return
	 */
	public final native String getProductName()
	/*-{
		return this.productName;
	}-*/;

	/**
	 * Return the namespace name of Teaming.
	 * 
	 * @return
	 */
	public final native String getNamespace()
	/*-{
		return this.namespace;
	}-*/;

	/**
	 * Returns the source of this RequestInfo object (i.e., main,
	 * profile, taskListing, ...
	 * 
	 * @return
	 */
	public final native String getRequestInfoSource()
	/*-{
		return this.requestInfoSource;
	}-*/;
	
	/**
	 * Return whether logging in is allowed from our standard login dialog.  This will be
	 * disallowed if we are running behind a single-sign on product such as NAM.
	 * 
	 * @return
	 */
	public final native boolean isFormLoginAllowed()
	/*-{
		return this.getBFromS( this.isFormLoginAllowed );
	}-*/;

	/**
	 * Return the flag that tells us if the user is logged in.
	 * 
	 * @return
	 */
	public final native boolean isUserLoggedIn()
	/*-{
		return this.getBFromS( this.isUserLoggedIn );
	}-*/;

	
	/**
	 * Return whether the tinyMCE editor can run on the device the user is running on. 
	 * 
	 * @return
	 */
	public final native boolean isTinyMCECapable()
	/*-{
		return this.getBFromS( this.isTinyMCECapable );
	}-*/;
	
	/**
	 * Return the flag that tells us if we should prompt for login
	 * 
	 * @return
	 */
	public final native boolean promptForLogin()
	/*-{
		return this.getBFromS( this.promptForLogin );
	}-*/;

	/**
	 * Return the URL needed to invoke the Teaming Feed page.
	 * 
	 * @return
	 */
	public final native String getTeamingFeedUrl()
	/*-{
		return this.teamingFeedUrl;
	}-*/;

	/**
	 * Returns true if we should false the sidebar to reload regardless
	 * of an operation and false otherwise.
	 * 
	 * @return
	 */
	public final native boolean forceSidebarReload()
	/*-{
		return this.getBFromS( this.forceSidebarReload );
	}-*/;
	
	public final native void    clearSidebarReload()
	/*-{
		this.forceSidebarReload = 'false';
	}-*/;
	
	public final native void    setSidebarReload()
	/*-{
		this.forceSidebarReload = 'true';
	}-*/;

	/**
	 * Returns the ID of the top workspace.
	 * 
	 * @return
	 */
	public final native String getTopWSId()
	/*-{
		return this.topWSId;
	}-*/;

	/**
	 * Returns true if we should be showing the site wide activity
	 * stream and false otherwise.
	 * 
	 * @return
	 */
	public final native boolean isShowWhatsNewOnLogin()
	/*-{
		return this.getBFromS( this.showWhatsNewOnLogin );
	}-*/;
	
	public final native void clearShowWhatsNewOnLogin()
	/*-{
		this.showWhatsNewOnLogin = 'false';
	}-*/;
	
	public final native boolean isBinderAdmin()
	/*-{
		return this.getBFromS( this.isBinderAdmin );
	}-*/;
	
	public final native boolean isQuotasEnabled()
	/*-{
		return this.getBFromS( this.isQuotasEnabled );
	}-*/;
	
	public final native String getQuotasUserMaximum()
	/*-{
		return this.quotasUserMaximum;
	}-*/;
	
	public final native String getQuotasDiskSpacedUsed()
	/*-{
		return this.quotasDiskSpacedUsed;
	}-*/;
	
	public final native boolean isDiskQuotaExceeded()
	/*-{
		return this.getBFromS( this.isQuotasDiskQuotaExceeded );
	}-*/;
	
	public final native boolean isModifyAllowed()
	/*-{
		return this.getBFromS( this.isModifyAllowed );
	}-*/;
	
	public final native String getModifyUrl()
	/*-{
		return this.modifyUrl;
	}-*/;
	
	public final native boolean isDiskQuotaHighWaterMarkExceeded()
	/*-{
		return this.getBFromS( this.isDiskQuotaHighWaterMarkExceeded );
	}-*/;

	public final native String getDeleteUserUrl()
	/*-{
		return this.deleteUserUrl;
	}-*/;

	public final native String getQuotaMessage()
	/*-{
		return this.quotasDiskMessage;
	}-*/;
	public final native String getUserDescription()
	/*-{
		return this.userDescription;
	}-*/;	
	
	/**
	 * Is the workspace being referenced owned by the current user.
	 * 
	 * @return
	 */
	public final boolean isOwner()
	{
		if ( getCurrentUserWorkspaceId() == getBinderId() )
		{
			return true;
		} 
		return false;
	}// end isOwner()
}// end RequestInfo
