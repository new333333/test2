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

package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
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

	public enum ScrollType {
		BOTH,
		HORIZONTAL,
		VERTICAL,
	}

	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtClientHelper() {
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
			DeferredCommand.addCommand( new Command() {
				public void execute() {
					Window.alert(msg);
				}
			});
		}
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
					
				case NO_BINDER_BY_THE_ID_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_FolderDoesNotExist(), patches);
					break;
					
				case USER_NOT_LOGGED_IN:
					cause = null;
					GwtTeaming.getMainPage().handleSessionExpired();
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
		
		if (hasString(errorMessage) && displayAlert) {
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
	 * Appends an HTML element to the top document.
	 * 
	 * @param htmlElement
	 */
	public static native void jsAppendDocumentElement(Element htmlElement) /*-{
		window.top.document.documentElement.appendChild(htmlElement);
	}-*/;

	/*
	 * Returns the URL to launch a search for the given tag.
	 */
	public static native String jsBuildTagSearchUrl(String tag) /*-{
		// Find the base tag search result URL...
		var searchUrl;
	   	                      try {searchUrl =                             ss_tagSearchResultUrl;} catch(e) {searchUrl="";}
		if (searchUrl == "") {try {searchUrl =                 self.parent.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}
		if (searchUrl == "") {try {searchUrl =                 self.opener.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}
		if (searchUrl == "") {try {searchUrl = window.top.gwtContentIframe.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}

		// ...and return it with the tag patched in.
		searchUrl = window.top.gwtContentIframe.ss_replaceSubStrAll(searchUrl, "ss_tagPlaceHolder", tag);
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
		window.top.jsEvalStringImpl(url, jsString);
	}-*/;

	
	/**
	 * Search for <script type="text/javascript"> elements found in the given html element
	 * and execute the javascript.
	 * 
	 * @param htmlElement
	 */
	public static native void jsExecuteJavaScript( Element htmlElement ) /*-{
		window.parent.ss_executeJavascript( htmlElement );
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
		if (window.top.ss_getUserDisplayStyle() == "newpage") {
			if (typeof window.top.ss_hideEntryDivOnLoad != "undefined") {
				window.top.ss_hideEntryDivOnLoad();
			}
		}
	}-*/;
	
	/**
	 * Invoke the "define editor overrides" dialog.
	 */
	public static native void jsInvokeDefineEditorOverridesDlg() /*-{
		window.top.ss_editAppConfig();
	}-*/;

	/**
	 * Returns true if we're running in any flavor of IE and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsIsIE() /*-{
		return( navigator.userAgent.toLowerCase().indexOf("msie") > -1 );
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
		window.top.ss_openUrlInWindow({href: url}, windowName, windowWidth, windowHeight);
	}-*/;

	/**
	 * Loads a URL into the GWT UI's content frame.
	 * 
	 * @param url
	 */
	public static void loadUrlInContentFrame(String url)
	{
		GwtMainPage.m_contentCtrl.setUrl( url );
	}

	/**
	 * Loads a URL into the top window.
	 * 
	 * @param url
	 */
	public static native void jsLoadUrlInTopWindow(String url) /*-{
		window.top.location.href = url;
	}-*/;
	
	/**
	 * Use Teaming's existing JavaScript to logout of Teaming.
	 */
	public static native void jsLogout() /*-{
		if ( window.top.ss_logoff != null )
			window.top.ss_logoff();
	}-*/;

	/**
	 * Use to register as an ActionRequestor to the GwtMainPage
	 * @param requestor
	 */
	public static native void jsRegisterActionHandler( ActionRequestor requestor ) /*-{
		window.top.ss_registerActionHandler( requestor );
	}-*/;

	/**
	 * Called to force the GWT UI content area to resize itself based
	 * on its current content.
	 * 
	 * @param reason
	 */
	public static native void jsResizeGwtContent(String reason) /*-{
		window.top.resizeGwtContent(reason);
	}-*/;


	/**
	 * Set the javascript variable, ss_userDisplayStyle, to the given value.
	 */
	public static native void jsSetEntryDisplayStyle( String style ) /*-{
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
				((null == s2) ? "" : s2) );
	}
	
	/**
	 * For the given list box, select the item in the list box that has
	 * the given value.
	 * 
	 * @param listbox
	 * @param value
	 */
	public static int selectListboxItemByValue( ListBox listbox, String value )
	{
		int i;
		
		for (i = 0; i < listbox.getItemCount(); ++i)
		{
			String tmp;
			
			tmp = listbox.getValue( i );
			if ( tmp != null && tmp.equalsIgnoreCase( value ) )
			{
				listbox.setSelectedIndex( i );
				return i;
			}
		}
		
		// If we get here it means we did not find an item in the listbox with the given value.
		return -1;
	}// end selectListboxItemByValue()
	
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
	private static native void jsFixFirstCol(Element eTD) /*-{
		eTD.colSpan = 2;
		eTD.width = "100%";
	}-*/;


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
}
