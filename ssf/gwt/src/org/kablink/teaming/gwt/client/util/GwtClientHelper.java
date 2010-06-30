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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;


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
		if ( window.top.gwtContentIframe.ss_logoff != null )
			window.top.gwtContentIframe.ss_logoff();;
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
	 * Sets a PopupPanel to use roll-down animation to open.  Currently
	 * the GWT API for this is inaccessible.  Once it becomes
	 * available, this method needs to be changed to enable animations
	 * and set their type to ROLL_DOWN.
	 * 
	 * @param popup
	 */
	public static void rollDownPopup(PopupPanel popup) {
		popup.setAnimationEnabled(false);
//!		popup.setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
	}
	
	/**
	 * For the given listbox, select the item in the listbox that has the given value.
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
}
