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
package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.VibeProduct;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * This class wraps a JavaScript object that holds information about
 * the request we are working with.
 * 
 * @author jwootton@novell.com
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
	 * Return whether the logged in user can see other users in the system.
	 */
	public final native boolean canSeeOtherUsers()
	/*-{
		return this.getBFromS( this.canSeeOtherUsers );
	}-*/;

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
	 * Return the flag that tells us if SharePoint is a valid server type for a net folder server
	 */
	public final native boolean getAllowSharePointAsAServerType()
	/*-{
		return this.getBFromS( this.allowSharePointAsAServerType );
	}-*/;
	
	/**
	 * Return the flag that tells us if SharePoint 2013 is a valid server type for a net folder server
	 */
	public final native boolean getAllowSharePoint2013AsAServerType()
	/*-{
		return this.getBFromS( this.allowSharePoint2013AsAServerType );
	}-*/;
	
	/**
	 * Return the flag that tells us if SharePoint 2010 is a valid server type for a net folder server
	 */
	public final native boolean getAllowSharePoint2010AsAServerType()
	/*-{
		return this.getBFromS( this.allowSharePoint2010AsAServerType );
	}-*/;
	
	/**
	 * Return the flag that tells us if we should show the "show people" link in the mast head.
	 */
	public final native boolean getAllowShowPeople()
	/*-{
		return this.getBFromS( this.allowShowPeople );
	}-*/;
	
	/**
	 * Return the flag that tells us if the logged in user can access
	 * their own personal workspace.
	 * 
	 * @return
	 */
	public final native boolean canAccessOwnWorkspace()
	/*-{
		return this.getBFromS( this.canAccessOwnWorkspace );
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
	 * Return true if the current user has access to the root workspace
	 * and false otherwise. 
	 * 
	 * @return
	 */
	public final native boolean hasRootDirAccess()
	/*-{
		return this.getBFromS( this.hasRootDirAccess );
	}-*/;
	
	/**
	 * Return true if Cloud Folders are enabled and false otherwise. 
	 * 
	 * @return
	 */
	public final native boolean isCloudFoldersEnabled()
	/*-{
		return this.getBFromS( this.cloudFoldersEnabled );
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
	 * Return the current user's decimal separator character as a
	 * string.
	 * 
	 * @return
	 */
	public final native String getDecimalSeparator()
	/*-{
		return this.decimalSeparator;
	}-*/;
	
	/**
	 * Return the default value for jits results max age
	 */
	public final native String getDefaultJitsResultsMaxAgeAsString()
	/*-{
		return this.defaultJitsResultsMaxAge;
	}-*/;
	
	/**
	 * Return the default value for jits results max age
	 */
	public final Long getDefaultJitsResultsMaxAge()
	{
		Long defaultVal;

		try
		{
			String value;

			value = getDefaultJitsResultsMaxAgeAsString();
			defaultVal = Long.parseLong( value );
		}
		catch ( Exception ex )
		{
			defaultVal = 60000L;
		}
		
		return defaultVal;
	}
	
	/**
	 * Return the default value for jits acl max age
	 */
	public final native String getDefaultJitsAclMaxAgeAsString()
	/*-{
		return this.defaultJitsAclMaxAge;
	}-*/;
	
	/**
	 * Return the default value for jits acl max age
	 */
	public final Long getDefaultJitsAclMaxAge()
	{
		Long defaultVal;

		try
		{
			String value;

			value = getDefaultJitsAclMaxAgeAsString();
			defaultVal = Long.parseLong( value );
		}
		catch ( Exception ex )
		{
			defaultVal = 600000L;
		}
		
		return defaultVal;
	}
	
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
	 * Return the Vibe's ssf path.
	 * 
	 * @return
	 */
	public final native String getSSFPath()
	/*-{
		return this.ssfPath;
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
	 * Returns true if password policy is enabled and false
	 * otherwise.
	 * 
	 * @return
	 */
	public final native boolean isPasswordPolicyEnabled()
	/*-{
		return this.getBFromS( this.passwordPolicyEnabled);
	}-*/;

	/**
	 * Return the user's short date pattern.
	 * 
	 * @return
	 */
	public final native String getShortDatePattern()
	/*-{
		return this.shortDatePattern;
	}-*/;

	/**
	 * Return the user's short time pattern.
	 * 
	 * @return
	 */
	public final native String getShortTimePattern()
	/*-{
		return this.shortTimePattern;
	}-*/;

	/**
	 * Return the flag that tells us if we should show the "Synchronize only the directory structure"
	 * ui in the net folder and net folder server dialogs.
	 */
	public final native boolean getShowSyncOnlyDirStructureUI()
	/*-{
		return this.getBFromS( this.showSyncOnlyDirStructureUI );
	}-*/;
	
	
	/**
	 * Return the user's time zone.
	 * 
	 * @return
	 */
	public final native String getTimeZone()
	/*-{
		return this.timeZone;
	}-*/;
	
	/**
	 * Return the abbreviation of the user's time zone.
	 * 
	 * @return
	 */
	public final native String getTimeZoneIdAbrev()
	/*-{
		return this.timeZoneIdAbrev;
	}-*/;
	
	/**
	 * Return the offset in hours of the users timezone
	 */
	public final native int getTimeZoneOffsetHour()
	/*-{
	 	return this.timeZoneOffsetHour;
	}-*/;

	/**
	 * Return the invitation url
	 */
	public final native String getLoginInvitationUrl()
	/*-{
		return this.loginInvitationUrl;
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
	 * Return the external user id
	 * 
	 * @return
	 */
	public final native String getLoginExternalUserId()
	/*-{
		return this.loginExternalUserId;
	}-*/;
	
	/**
	 * Return the external user name
	 * 
	 * @return
	 */
	public final native String getLoginExternalUserName()
	/*-{
		return this.loginExternalUserName;
	}-*/;
	
	/**
	 * Return the name of the open id provider the user can use.
	 */
	public final native String getLoginOpenIdProviderName()
	/*-{
		return this.loginOpenIdProviderName;
	}-*/;
	
	/**
	 * Return the url of the open id provider the user can use.
	 */
	public final native String getLoginOpenIdProviderUrl()
	/*-{
		return this.loginOpenIdProviderUrl;
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
	 * Return the login status.
	 * 
	 * @return
	 */
	public final native String getLoginStatus()
	/*-{
		return this.loginStatus;
	}-*/;
	
	/**
	 * Return the login user ID.
	 * 
	 * @return
	 */
	public final native String getLoginUserId()
	/*-{
		return this.loginUserId;
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
	 * Returns whether the user has access to their own workspace.
	 * 
	 * @return
	 */
	public final native boolean getMyWorkspaceAccessible()
	/*-{
		return this.getBFromS( this.myWSAccessible );
	}-*/;
	
	/**
	 * Return whether the UI should initially show the workspace tree control
	 */
	public final native boolean getShouldShowWSTreeControl()
	/*-{
		return this.getBFromS( this.showWSTreeControl );
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
	 * Return the user's avatar URL.
	 * 
	 * @return
	 */
	public final native String getUserAvatarUrl()
	/*-{
		return this.userAvatarUrl;
	}-*/;

	/**
	 * Stores the user's avatar URL.
	 * 
	 * @param userAvatarUrl
	 */
	public final native String setUserAvatarUrl(String userAvatarUrl)
	/*-{
		this.userAvatarUrl = userAvatarUrl;
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
	 * Return the flag that tells us if we are running in Filr mode.
	 * 
	 * @return
	 */
	public final native boolean isLicenseFilr()
	/*-{
		return (this.getBFromS( this.isLicenseFilr ));
	}-*/;

	/**
	 * Return the flag that tells us if we are running with an expired
	 * license.
	 * 
	 * @return
	 */
	public final native boolean isLicenseExpired()
	/*-{
		return (this.getBFromS( this.isLicenseExpired ));
	}-*/;

	/**
	 * Return the flag that tells us if we are running with a valid
	 * license.
	 * 
	 * @return
	 */
	public final native boolean isLicenseValid()
	/*-{
		return (this.getBFromS( this.isLicenseValid ));
	}-*/;

	/**
	 * Return the flag that tells us if we are running in Filr and Vibe mode.
	 * 
	 * @return
	 */
	public final native boolean isLicenseFilrAndVibe()
	/*-{
		return (this.getBFromS( this.isLicenseFilrAndVibe ));
	}-*/;

	/**
	 * Return the flag that tells us if we are running in Vibe mode.
	 * 
	 * @return
	 */
	public final native boolean isLicenseVibe()
	/*-{
		return (this.getBFromS( this.isLicenseVibe ));
	}-*/;

	/**
	 * Return the flag that tells us if we should expose Filr features.
	 * 
	 * @return
	 */
	public final native boolean showFilrFeatures()
	/*-{
		return (this.getBFromS( this.showFilrFeatures ));
	}-*/;

	/**
	 * Return the flag that tells us if we should expose Vibe features.
	 * 
	 * @return
	 */
	public final native boolean showVibeFeatures()
	/*-{
		return (this.getBFromS( this.showVibeFeatures ));
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
	 * Return the flag that tells us if the logged in user is a site
	 * administrator.
	 * 
	 * @return
	 */
	public final native boolean isSiteAdmin()
	/*-{
		return (this.getBFromS( this.isSiteAdmin ));
	}-*/;

	/**
	 * Return the flag that tells us if the logged in user is the
	 * built-in admin user.
	 * 
	 * @return
	 */
	public final native boolean isBuiltInAdmin()
	/*-{
		return (this.getBFromS( this.isBuiltInAdmin ));
	}-*/;

	/**
	 * Return the flag that tells us if the logged in user is the Guest
	 * user.
	 * 
	 * @return
	 */
	public final native boolean isGuestUser()
	/*-{
		return (this.getBFromS( this.isGuestUser ));
	}-*/;

	/**
	 * Return the flag that tells us if the logged in user is an
	 * external user.
	 * 
	 * @return
	 */
	public final native boolean isExternalUser()
	/*-{
		return (this.getBFromS( this.isExternalUser ));
	}-*/;
	
	/**
	 * Return the flag that tells us if the logged in user is an
	 * LDAP user.
	 * 
	 * @return
	 */
	public final native boolean isLdapUser()
	/*-{
		return (this.getBFromS( this.isLdapUser ));
	}-*/;
	
	/**
	 * Return the flag that tells us if users running on a browser that
	 * doesn't support HTML5 storage should have their history tracked
	 * on the server.
	 * 
	 * @return
	 */
	public final native boolean isTrackNonHTML5HistoryOnServer()
	/*-{
		return (this.getBFromS( this.trackNonHTML5HistoryOnServer ));
	}-*/;
	
	/**
	 * Return the id of the "all external users" group
	 */
	public final native String getAllExternalUsersGroupId()
	/*-{
		return this.allExternalUsersGroupId;
	}-*/;
	
	/**
	 * Return the id of the "all internal users" group
	 */
	public final native String getAllInternalUsersGroupId()
	/*-{
		return this.allInternalUsersGroupId;
	}-*/;
	
	/**
	 * Return the id of the "guest" user
	 */
	public final native String getGuestId()
	/*-{
		return this.guestId;
	}-*/;
	
	/**
	 * Return the flag that tells us if the login dialog can have a cancel button.
	 */
	public final native boolean getLoginCanCancel()
	/*-{
		return (this.getBFromS( this.loginCanCancel ));
	}-*/;
	
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
	 * Returns a collection that's to be shown at login, if one is
	 * required.
	 * 
	 * @return
	 */
	public final native String getShowCollectionOnLogin()
	/*-{
		return this.showCollectionOnLogin;
	}-*/;
	
	/**
	 * Return whether the Public collection should be presented to the
	 * logged in user.
	 * 
	 * @return
	 */
	public final native boolean isShowPublicCollection()
	/*-{
		return this.getBFromS( this.showPublicCollection );
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
	 * Returns true if we should reload the sidebar regardless of an
	 * operation and false otherwise.
	 * 
	 * @return
	 */
	public final native boolean isRefreshSidebarTree()
	/*-{
		return this.getBFromS( this.refreshSidebarTree );
	}-*/;
	
	public final native void clearRefreshSidebarTree()
	/*-{
		this.refreshSidebarTree = 'false';
	}-*/;
	
	public final native void setRefreshSidebarTree()
	/*-{
		this.refreshSidebarTree = 'true';
	}-*/;

	/**
	 * Returns true if we should re-root the sidebar regardless of an
	 * operation and false otherwise.
	 * 
	 * @return
	 */
	public final native boolean isRerootSidebarTree()
	/*-{
		return this.getBFromS( this.rerootSidebarTree );
	}-*/;
	
	public final native void clearRerootSidebarTree()
	/*-{
		this.rerootSidebarTree = 'false';
	}-*/;
	
	public final native void setRerootSidebarTree()
	/*-{
		this.rerootSidebarTree = 'true';
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
	 * Returns the ID of the profile binder.
	 * 
	 * @return
	 */
	public final native String getProfileBinderId()
	/*-{
		return this.profileBinderId;
	}-*/;

	/**
	 * Interacts with the settings dealing with activity stream access
	 * on login.
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

	public final native void setShowWhatsNewOnLogin()
	/*-{
		this.showWhatsNewOnLogin = 'true';
	}-*/;

	/**
	 * Interacts with settings dealing with a specific ActivityStream
	 * being navigated to.
	 * 
	 * @return
	 */
	private final native String getShowSpecificWhatsNewS()
	/*-{
		return this.specificWhatsNew;
	}-*/;
	
	public final ActivityStream getShowSpecificWhatsNew()
	{
		ActivityStream reply;
		try
		{
			String asS = getShowSpecificWhatsNewS();
			reply = ActivityStream.valueOf(Integer.parseInt(asS));
		}
		catch ( Exception ex )
		{
			reply = ActivityStream.UNKNOWN;
		}
		return reply;
	}
	
	public final void setShowSpecificWhatsNew(ActivityStream as)
	{
		setShowSpecificWhatsNewS( String.valueOf( as.getValue() ) );
	}
	
	private final native void setShowSpecificWhatsNewS(String asS)
	/*-{
		this.specificWhatsNew = asS;
	}-*/;
	
	public final void clearShowSpecificWhatsNew()
	{
		setShowSpecificWhatsNewS( String.valueOf( ActivityStream.UNKNOWN.getValue() ) );
		clearShowSpecificWhatsNewHistoryAction();
	}
	
	private final native String getShowSpecificWhatsNewIdS()
	/*-{
		return this.specificWhatsNewId;
	}-*/;
	
	public final void setShowSpecificWhatsNewId( Long id )
	{
		setShowSpecificWhatsNewIdS( String.valueOf( id ) );
	}
	
	private final native void setShowSpecificWhatsNewIdS( String id )
	/*-{
		this.specificWhatsNewId = String( id );
	}-*/;
	
	public final Long getShowSpecificWhatsNewId()
	{
		Long reply;
		try
		{
			String asItemIdS = getShowSpecificWhatsNewIdS();
			reply = Long.parseLong( asItemIdS );
		}
		catch ( Exception ex )
		{
			reply = (-1L);
		}
		return reply;
	}
	
	public final native boolean isShowSpecificWhatsNewHistoryAction()
	/*-{
		return this.getBFromS( this.specificWhatsNewHistoryAction );
	}-*/;
	
	public final native void clearShowSpecificWhatsNewHistoryAction()
	/*-{
		this.specificWhatsNewHistoryAction = 'false';
	}-*/;

	public final native void setShowSpecificWhatsNewHistoryAction()
	/*-{
		this.specificWhatsNewHistoryAction = 'true';
	}-*/;

	/**
	 * Return true if the logged in user has admin rights to the
	 * currently selected binder.
	 * 
	 * @return
	 */
	public final native boolean isBinderAdmin()
	/*-{
		return this.getBFromS( this.isBinderAdmin );
	}-*/;
	
	/**
	 * Interacts with the information dealing with quotas.
	 * 
	 * @return
	 */
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
	
	public final native boolean isDiskQuotaHighWaterMarkExceeded()
	/*-{
		return this.getBFromS( this.isDiskQuotaHighWaterMarkExceeded );
	}-*/;
	
	public final native String getQuotaMessage()
	/*-{
		return this.quotasDiskMessage;
	}-*/;

	/**
	 * ?
	 * 
	 * @return
	 */
	public final native boolean isModifyAllowed()
	/*-{
		return this.getBFromS( this.isModifyAllowed );
	}-*/;
	
	public final native String getModifyUrl()
	/*-{
		return this.modifyUrl;
	}-*/;
	
	/**
	 * ?
	 * 
	 * @return
	 */
	public final native String getDeleteUserUrl()
	/*-{
		return this.deleteUserUrl;
	}-*/;

	/**
	 * ?
	 * 
	 * @return
	 */
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
