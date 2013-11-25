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

package org.kablink.teaming.gwt.client.util;

import java.util.Date;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.lpe.LandingPageEditor;
import org.kablink.teaming.gwt.client.profile.widgets.GwtProfilePage;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;


/**
 * Helper methods for the GWT UI client code.
 *
 * @author drfoster@novell.com
 */
public class GwtClientHelper {
	// String used to recognized an '&' formatted URL vs. a '/'
	// formatted permalink URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";
	
	// Marker string used to recognize the format of a URL.
	private final static String PERMALINK_MARKER = "view_permalink";

	// Number of milliseconds in a day.
    public static final long DAY_IN_MS = (24L * 60L * 60L * 1000L);
    
	// Enumeration value used to interact with scroll bars on
	// something.
	public enum ScrollType {
		BOTH,
		HORIZONTAL,
		VERTICAL,
	}

	/*
	 * Constructor method. 
	 */
	private GwtClientHelper() {
		// Inhibits this class from being instantiated.
	}
	
	/**
	 * Appends a parameter to to a URL.
	 * 
	 * @param urlString
	 * @param pName
	 * @param pValue
	 * 
	 * @return
	 */
	public static String appendUrlParam(String urlString, String pName, String pValue) {
		String param;
		boolean useAmpersand = (0 < urlString.indexOf(AMPERSAND_FORMAT_MARKER));
		if (useAmpersand)
			 param = ("&" + pName + "=" + pValue);
		else param = ("/" + pName + "/" + pValue);
		if (0 > urlString.indexOf(param)) {
			urlString += param;
		}
		return urlString;
	}

	/**
	 * Returns the boolean value stored in a string.
	 * 
	 * @param s
	 * @param def
	 * 
	 * @return
	 */
	public static boolean bFromS(String s, boolean def) {
		return (hasString(s) ? Boolean.parseBoolean(s) : def);
	}
	
	public static boolean bFromS(String s) {
		return bFromS(s, false);
	}

	/**
	 * Displays a messages in an 'deferred' alert box.
	 * 
	 * @param msg
	 */
	public static void deferredAlert(final String msg) {
		if (hasString(msg)) {
			ScheduledCommand cmd = new ScheduledCommand() {
				@Override
				public void execute() {
					Window.alert(msg);
				}
			};
			Scheduler.get().scheduleDeferred(cmd);
		}
	}

	/**
	 * Execute the given command via GWT's rpc mechanism
	 */
	public static void executeCommand(VibeRpcCmd cmd, AsyncCallback<VibeRpcResponse> callback) {
		GwtTeaming.getRpcService().executeCommand(HttpRequestInfo.createHttpRequestInfo(), cmd, callback);
	}	
	
	/**
	 * Returns the RequestInfo object from whatever component we're
	 * running as.
	 *
	 * Returns null if a RequestInfo cannot be determined.
	 * 
	 * @return
	 */
	public static RequestInfo getRequestInfo() {
		RequestInfo reply;
		if      (null != GwtMainPage.m_requestInfo)         reply = GwtMainPage.m_requestInfo;
		else if (null != GwtProfilePage.profileRequestInfo) reply = GwtProfilePage.profileRequestInfo;
		else if (null != TaskListing.m_requestInfo)         reply = TaskListing.m_requestInfo;
		else if (null != LandingPageEditor.m_requestInfo)   reply = LandingPageEditor.m_requestInfo;
		else                                                reply = null;
		if (null == reply) {
			// Virtually NOTHING with the GWT code will work without a
			// RequestInfo object.  Tell the user about the problem.
			//
			// Some potential causes of this problem:
			// 1. Missing check in the if/else-if above.
			// 2. The GWT UI component failed to load. 
			Window.alert(GwtTeaming.getMessages().missingRequestInfo());
		}
		return reply;
	}

	/**
     * Returns the client's timezone offset.
	 * 
	 * Note:
	 *    We use deprecated APIs here since GWT's client side has no
	 *    GregorianCalendar equivalent.  This is the only way to
	 *    manipulate a date and time.
     * 
	 * @param date
	 * 
     * @return
     */
	@SuppressWarnings("deprecation")
	public static int getTimeZoneOffset(Date date) {
		final int tzo = date.getTimezoneOffset();
		return tzo;
	}
	
	/**
	 * Returns the client's timezone offset in milliseconds.
	 *
	 * @param date
	 * 
	 * @return
	 */
	public static long getTimeZoneOffsetMillis(Date date) {
		return (((long) getTimeZoneOffset(date)) * 60L * 1000L);
	}
	
    /**
     * Converts a time in GMT to a GWT Date object which is in the
     * timezone of the browser.
     * 
     * @param time
     * 
     * @return
     */
    public static final Date gmtToLocal(Date date) {       
        // Add the timezone offset.
        return new Date(date.getTime() + getTimeZoneOffsetMillis(date));
    }

    /**
     * Converts a GWT Date in the timezone of the browser to a time in
     * GMT.
     * 
     * @param date
     * 
     * @return
     */
    public static final Date localToGmt(Date date) {
        // Remove the timezone offset.        
        return new Date(date.getTime() - getTimeZoneOffsetMillis(date));
    }
   
	/*
	 * Applies patches to a message string.
	 */
	private static String patchMessage(String msg, String[] patches) {
		int count = ((null == patches) ? 0 : patches.length);
    	for (int i = 0; i < count; i += 1) {
            String delimiter = ("[" + i + "]");
            String patch = patches[i];
            while(msg.contains(delimiter)) {
                msg = msg.replace(delimiter, patch);
            }
        }
		return msg;
	}
	
	/**
	 * Handles Throwable's received by GWT RPC onFailure() methods.
	 * 
	 * Notes:
	 * 1) Passing no error message string here allows for the proper
	 *    exception handling to occur but will NOT display an error to
	 *    the user.
	 * 2) On 20100803 I (Dennis) discussed this with Jay and we agreed
	 *    that in the Durango release, we'll only display errors here
	 *    for those exceptions that we really know something about.
	 *    The others will be ignored.
	 * 
	 * @param t
	 * @param errorMessage
	 * @param patches
	 */
	public static void handleGwtRPCFailure(Throwable t, String errorMessage, String[] patches) {
		boolean displayAlert = false;
		if (null != t) {
			GwtTeamingMessages messages = GwtTeaming.getMessages();
			String cause;
			if (t instanceof GwtTeamingException) {
				switch (((GwtTeamingException) t).getExceptionType()) {
				case ACCESS_CONTROL_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_AccessToFolderDenied(), patches);
					break;
					
				case FAVORITES_LIMIT_EXCEEDED:
					errorMessage = messages.rpcFailure_AddFavoriteLimitExceeded();
					cause = "";
					displayAlert = true;
					break;
					
				case LDAP_GUID_NOT_CONFIGURED:
					displayAlert = true;
					cause = messages.rpcFailure_LdapGuidNotConfigured();
					break;
					
				case NO_BINDER_BY_THE_ID_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_FolderDoesNotExist(), patches);
					break;
					
				case USER_NOT_LOGGED_IN:
					cause = null;
					GwtTeaming.getMainPage().handleSessionExpired();
					break;
					
				case GROUP_ALREADY_EXISTS:
					displayAlert = true;
					cause = patchMessage( messages.rpcFailure_CreateGroupAlreadyExists(), patches );
					break;
					
				default:
					cause = patchMessage(messages.rpcFailure_UnknownException(), patches);
					break;
				}
			}
			
			else {
				cause = t.getLocalizedMessage();
				if (!(hasString(cause))) {
					cause = t.toString();
				}
			}
			
			if (!(hasString(cause))) {
				cause = messages.rpcFailure_UnknownCause();
			}
			patches = new String[]{cause};
		}
		
		if (hasString(errorMessage) && (displayAlert || isDebugUI())) {
			errorMessage = patchMessage(errorMessage, patches);
			Window.alert(errorMessage);
		}
	}
	
	public static void handleGwtRPCFailure(Throwable t, String errorMessage, String patch) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, errorMessage, new String[]{patch});
	}
	
	public static void handleGwtRPCFailure(Throwable t, String errorMessage) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, errorMessage, ((String[]) null));
	}
	
	public static void handleGwtRPCFailure(Throwable t) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, null, ((String[]) null));
	}
	
	/**
	 * Returns true is s refers to a non null, non 0 length String and
	 * false otherwise.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean hasString(String s) {
		return ((null != s) && (0 < s.length()));
	}

	/**
	 * Returns the integer value stored in a string.
	 * 
	 * @param s
	 * @param def
	 * 
	 * @return
	 */
	public static int iFromS(String s, int def) {
		return (hasString(s) ? Integer.parseInt(s) : def);
	}
	
	public static int iFromS(String s) {
		return iFromS(s, (-1));
	}

	/**
	 * Open a window with the url that points to the appropriate help
	 * documentation.
	 */
	public static void invokeHelp(HelpData helpData) {
		if (null != helpData) {
			// Get the URL that points to the appropriate help
			// documentation.
			String url = helpData.getUrl();
			if (hasString(url)) {
				Window.open(url, "teaming_help_window", "resizeable,scrollbars");
			}
		}
	}	

	/**
	 * Returns true if the UI is in debug mode and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isDebugUI() {
		RequestInfo ri = getRequestInfo();
		if (null == ri) {
			return false;
		}
		return ri.isDebugUI();
	}
	
	/**
	 * Returns true if a URL is a permalink URL and false otherwise.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isPermalinkUrl(String url) {
		boolean reply = hasString(url);
		if (reply) {
			reply = (0 < url.indexOf(PERMALINK_MARKER));
		}
		return reply;
	}

	/**
	 * Invokes the simple profile dialog off an HTML Element.
	 * 
	 * @param htmlElement
	 * @param binderId
	 * @param userName
	 */
	public static native void invokeSimpleProfile(Element htmlElement, String binderId, String userName) /*-{
		$wnd.top.ss_invokeSimpleProfile(htmlElement, binderId, userName);
	}-*/;
	
	/**
	 * Appends an HTML element to the top document.
	 * 
	 * @param htmlElement
	 */
	public static native void jsAppendDocumentElement(Element htmlElement) /*-{
		$wnd.top.document.documentElement.appendChild(htmlElement);
	}-*/;

	/*
	 * Returns the URL to launch a search for the given tag.
	 */
	public static native String jsBuildTagSearchUrl(String tag) /*-{
		// Find the base tag search result URL...
		var searchUrl;
	   	                      try {searchUrl =                           ss_tagSearchResultUrl;} catch(e) {searchUrl="";}
		if (searchUrl == "") {try {searchUrl =               self.parent.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}
		if (searchUrl == "") {try {searchUrl =               self.opener.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}
		if (searchUrl == "") {try {searchUrl = $wnd.top.gwtContentIframe.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}

		// ...and return it with the tag patched in.
		searchUrl = $wnd.top.gwtContentIframe.ss_replaceSubStrAll(searchUrl, "ss_tagPlaceHolder", tag);
		return searchUrl;
	}-*/;
	
	/**
	 * Uses JavaScript native method to URI encode a string.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static native String jsEncodeURIComponent(String s) /*-{
		return encodeURIComponent(s);
	}-*/;
	
	/**
	 * Evaluates a JavaScript string containing embedded
	 * JavaScript. 
	 * 
	 * Note:  The code that's now in GwtMainPage.jsEvalStringImpl() was
	 *    originally inside this method but GWT's obfuscation used for
	 *    our production compiles broke it.
	 * 
	 * @param url
	 * @param jsString
	 */
	public static native void jsEvalString(String url, String jsString) /*-{
		$wnd.top.jsEvalStringImpl(url, jsString);
	}-*/;
	
	/*
	 * ?
	 */
	private static native void jsFixFirstCol(Element eTD) /*-{
		eTD.colSpan = 2;
		eTD.width = "100%";
	}-*/;

	/**
	 * Search for <script type="text/javascript"> elements found in the given html element
	 * and execute the javascript.
	 * 
	 * @param htmlElement
	 */
	public static native void jsExecuteJavaScript(Element htmlElement) /*-{
		$wnd.parent.ss_executeJavascript(htmlElement);
	}-*/;

	/**
	 * Returns the left position of the content <IFRAME>'s <DIV>.
	 * 
	 * @return
	 */
	public static native int jsGetContentIFrameLeft() /*-{
		var iFrameDIV = $wnd.top.document.getElementById('contentControl');
		return $wnd.top.ss_getObjectLeft(iFrameDIV);
	}-*/;

	/**
	 * Returns the top position of the content <IFRAME>'s <DIV>.
	 * 
	 * @return
	 */
	public static native int jsGetContentIFrameTop() /*-{
		var iFrameDIV = $wnd.top.document.getElementById('contentControl');
		return $wnd.top.ss_getObjectTop(iFrameDIV);
	}-*/;

	/**
	 * Returns the view type of what's being viewed in the content
	 * panel.
	 * 
	 * @return
	 */
	public static native String jsGetViewType() /*-{
		return $wnd.top.gwtContentIframe.ss_viewType;
	}-*/;
	
	/**
	 * Returns true if the GWT simple search form is available and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsHasSimpleSearchForm() /*-{
		var reply = false;
		var contentIFrame = $wnd.top.gwtContentIframe;
		if (null != contentIFrame) {
			var contentDoc = contentIFrame.document;
			if (null != contentDoc) {
				reply = (null != contentDoc.getElementById("gwtSimpleSearchForm"));
			}
		}
		return reply;
	}-*/;
	
	/**
	 * Hides the popup entry iframe div if one exists.
	 */
	public static native void jsHideEntryPopupDiv() /*-{
		if ($wnd.ss_hideEntryDivOnLoad !== undefined) {
			$wnd.ss_hideEntryDivOnLoad();
		}
	}-*/;

	/**
	 * Called to hide any open entry view DIV that's in new page mode.
	 */
	public static native void jsHideNewPageEntryViewDIV() /*-{
		if ($wnd.top.ss_getUserDisplayStyle() == "newpage") {
			if (typeof $wnd.top.ss_hideEntryDivOnLoad != "undefined") {
				$wnd.top.ss_hideEntryDivOnLoad();
			}
		}
	}-*/;
	
	/**
	 * Invoke the "define editor overrides" dialog.
	 */
	public static native void jsInvokeDefineEditorOverridesDlg() /*-{
		$wnd.top.ss_editAppConfig();
	}-*/;

	/**
	 * Invokes a simple search on the given string.
	 * 
	 * @param searchForThis
	 */
	public static native void jsInvokeSimpleSearch(String searchFor) /*-{
		var contentDoc = $wnd.top.gwtContentIframe.document;
		contentDoc.getElementById("gwtSimpleSearchText").value = searchFor;
		contentDoc.getElementById("gwtSimpleSearchForm").submit();
	}-*/;
	
	/**
	 * Returns true if we're running in any flavor of IE and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsIsIE() /*-{
		return(navigator.userAgent.toLowerCase().indexOf("msie") > -1);
	}-*/;
	
	/**
	 * Uses Teaming's existing ss_common JavaScript to launch a toolbar
	 * popup URL.
	 * 
	 * @param url
	 */
	public static native void jsLaunchToolbarPopupUrl(String url) /*-{
		$wnd.ss_toolbarPopupUrl(url, '_blank', '', '');
	}-*/;

	/**
	 * Uses Teaming's existing ss_common JavaScript to launch a URL in
	 * a new window.
	 * 
	 * @param url
	 * @param windowName
	 * @param windowHeight
	 * @param windowWidth
	 */
	public static native void jsLaunchUrlInWindow(String url, String windowName, int windowHeight, int windowWidth) /*-{
		$wnd.top.ss_openUrlInWindow({href: url}, windowName, windowWidth, windowHeight);
	}-*/;

	/**
	 * Loads a URL into the current window.
	 * 
	 * @param url
	 */
	public static native void jsLoadUrlInCurrentWindow(String url) /*-{
		$wnd.location.href = url;
	}-*/;
	
	/**
	 * Loads a URL into the top window.
	 * 
	 * @param url
	 */
	public static native void jsLoadUrlInTopWindow(String url) /*-{
		$wnd.top.location.href = url;
	}-*/;
	
	/**
	 * Use Teaming's existing JavaScript to logout of Teaming.
	 */
	public static native void jsLogout() /*-{
		if ($wnd.top.ss_logoff != null) {
			$wnd.top.ss_logoff();
		}
	}-*/;

	/**
	 * Used to fire a simple Vibe event to the outer most GwtMainpage's
	 * event bus from anywhere within the application.
	 * 
	 * @param event
	 */
	public static native void jsFireVibeEventOnMainEventBus(TeamingEvents eventEnum) /*-{
		$wnd.top.ss_fireVibeEventOnMainEventBus(eventEnum);
	}-*/;

	/**
	 * Called to force the GWT UI content area to resize itself based
	 * on its current content.
	 * 
	 * @param reason
	 */
	public static native void jsResizeGwtContent(String reason) /*-{
		$wnd.top.resizeGwtContent(reason);
	}-*/;

	/**
	 * Set the JavaScript variable, ss_userDisplayStyle, to the given
	 * value.
	 */
	public static native void jsSetEntryDisplayStyle(String style) /*-{
		$wnd.top.ss_userDisplayStyle = style;
	}-*/;

	/**
	 * Call ss_setEntryPopupIframeSize() to set the size and position of the view entry popup div.
	 */
	public static native void jsSetEntryPopupIframeSize() /*-{
		$wnd.top.ss_setEntryPopupIframeSize();
	}-*/;

	/**
	 * Sets the text on the main GWT page's <title>.
	 */
	public static native void jsSetMainTitle(String title) /*-{
		$wnd.top.document.title = title;
	}-*/;

	/**
	 * Runs an entry view URL in the content frame.
	 * 
	 * @param url
	 */
	public static native void jsShowForumEntry(String entryUrl) /*-{
		$wnd.top.gwtContentIframe.ss_showForumEntry(entryUrl);
	}-*/;
	
	/**
	 * Compares two strings by collation.
	 * 
	 * Returns:
	 *    -1 if s1 <  s2;
	 *     0 if s1 == s2; and
	 *     1 if s1 >  s2.
	 *     
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static native int jsStringCompare(String s1, String s2) /*-{
		return s1.localeCompare(s2);
	}-*/;

	/**
	 * Removes all the child Node's from a DOM Element.
	 *  
	 * @param e
	 */
	public static void removeAllChildren(Element e) {
		// If we have a DOM Element... 
		if (null != e) {
			// ...scan its child Node's...
			Node child = e.getFirstChild(); 
			while (null != child) {
				// ...removing each from the Element.
				e.removeChild(child);
				child = e.getFirstChild();
			}
		}
	}
	
	/**
	 * Sets a TeamingPopupPanel to use roll-down animation to open.
	 * 
	 * @param popup
	 */
	public static void rollDownPopup(TeamingPopupPanel popup) {
		popup.setAnimationEnabled(true);
		popup.setAnimationTypeToRollDown();
	}
	
	/**
	 * Performs a collated compare on two strings without generating any
	 * exceptions.
	 * 
	 * Returns:
	 *    -1 if s1 <  s2;
	 *     0 if s1 == s2; and
	 *     1 if s1 >  s2.
	 *     
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static int safeSColatedCompare(String s1, String s2) {
		return
			jsStringCompare(
				((null == s1) ? "" : s1),
				((null == s2) ? "" : s2));
	}
	
	/**
	 * For the given list box, select the item in the list box that has
	 * the given value.
	 * 
	 * @param listbox
	 * @param value
	 */
	public static int selectListboxItemByValue(ListBox listbox, String value) {
		int i;
		
		for (i = 0; i < listbox.getItemCount(); i += 1) {
			String tmp = listbox.getValue(i);
			if (tmp != null && tmp.equalsIgnoreCase(value)) {
				listbox.setSelectedIndex(i);
				return i;
			}
		}
		
		// If we get here it means we did not find an item in the
		// list box with the given value.
		return -1;
	}
	
	/**
	 * Sets up a colspan="span" that spans the cells of a row in a
	 * Grid beginning at index start in a way that works on IE, FF, ...
	 *
	 * Note:  This is a hack to get around the inability to natively
	 *        set a colspan on cells of GWT Grid.
	 *
	 * @param grid
	 * @param row
	 * @param cell
	 */
	public static void setGridColSpan(Grid grid, int row, int start, int span) {
		jsFixFirstCol(grid.getCellFormatter().getElement(row, start));
		for (int i = 1; i < span; i += 1) {
			start += 1;
			grid.getCellFormatter().getElement(row, start).addClassName("grid_HideCell");
		}
		
	}
	
	/**
	 * Replaces all occurrences of oldSub with newSub in s.
	 * 
	 * The implementation was copied from StringUtil.replace().
	 * 
	 * @param s
	 * @param oldSub
	 * @param newSub
	 * 
	 * @return
	 */
	public static String replace(String s, String oldSub, String newSub) {
		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		int y = s.indexOf(oldSub);

		if (y >= 0) {
			StringBuffer sb = new StringBuffer();
			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);
				x = y + length;
				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}
		else {
			return s;
		}
	}
	
	/**
	 * Adds scroll bars to the main content panel for the duration of a
	 * PopupPanel.
	 *  
	 * @param popup
	 * @param scrollType
	 */	
	public static void scrollUIForPopup(PopupPanel popup, ScrollType scrollType) {
		// What class name do we use for the requested scroll type?
		final String scrollClass;
		switch (scrollType) {
		default:
		case VERTICAL:    scrollClass = "gwtUI_ss_forceVerticalScroller";   break;
		case BOTH:        scrollClass = "gwtUI_ss_forceBothScrollers";      break;
		case HORIZONTAL:  scrollClass = "gwtUI_ss_forceHorizontalScroller"; break;
		}

		// If the <body> doesn't currently have this class...
		final Element bodyElement = RootPanel.getBodyElement();
		String className = bodyElement.getClassName();
		if ((!(hasString(className))) || (0 > className.indexOf(scrollClass))) {
			// ...add it...
			bodyElement.addClassName(scrollClass);

			// ...and when the popup closes...
			popup.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					// ...remove it.
					bodyElement.removeClassName(scrollClass);
				}
			});
		}
	}

	public static void scrollUIForPopup(PopupPanel popup) {
		// Always use the initial form of the method, defaulting to a
		// vertical scroll bar only.
		scrollUIForPopup(popup, ScrollType.VERTICAL);
	}

	/**
	 * Validates we have a URL in an OnSelectBinderInfo object.
	 * 
	 * Optionally displays an error if there isn't and returns false.
	 * Otherwise, returns true.
	 * 
	 * @param osbi
	 * @param displayError
	 * 
	 * @return
	 */
	public static boolean validateOSBI(OnSelectBinderInfo osbi, boolean displayError) {
		// If we the OnSelectBinderInfo doesn't have a permalink to the
		// binder...
		if (!(GwtClientHelper.hasString(osbi.getBinderUrl()))) {
			// ...tell the user and return false.
			if (displayError) {
				deferredAlert(GwtTeaming.getMessages().cantAccessFolder());
			}
			return false;
		}
		
		// If we get here, the OnSelectBinderInfo has a permalink to
		// the binder.  Return true.
		return true;
	}
	
	public static boolean validateOSBI(OnSelectBinderInfo osbi) {
		// Always use the initial form of the method.
		return validateOSBI(osbi, true);
	}
}
