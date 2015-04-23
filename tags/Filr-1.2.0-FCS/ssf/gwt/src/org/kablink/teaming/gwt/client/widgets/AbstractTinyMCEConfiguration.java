/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * 
 */
public abstract class AbstractTinyMCEConfiguration
{
	protected boolean m_rpcInProgress;
	protected String m_binderId = null;	// Id of the binder we are dealing with.
	protected AsyncCallback<VibeRpcResponse> m_rpcCallback = null;
	protected ArrayList<String> m_listOfFileAttachments = null;
	
	private String mode = "none";
	private String theme = "advanced";
	private String onPageLoad = "ss_addLanguageChanges";
	protected String language = null;
	protected String contentCss = null;
	private boolean relativeUrls = false;
	private boolean removeScriptHost = false;
	private String width = "100%";
	private boolean accessibilityWarnings = true;
	private boolean accessibilityFocus = true;
	private String entities = "39,#39,34,quot,38,amp,60,lt,62,gt";
	private boolean gecko_spellcheck = true;
	private String[] plugins = new String[] {"pdw", "table", "preelementfix", "ss_addimage", "preview", "paste", "ss_wikilink", "ss_youtube"};
	private String themeAdvancedToolbarLocation = "top";
	private String themeAdvancedToolbarAlign = "left";
	private String themeAdvancedStatusbarLocation = "bottom";
	private boolean themeAdvancedResizing = true;
	private boolean convertFontsToSpans = true;
	private boolean themeAdvancedResizingUseCookie = true;
	private boolean themeAdvancedPath = false;
	private String themeAdvancedDisable = "image,advimage";
	
	private int pdwToggle = 1;
	private String pdwToggleToolbars = "2";
	
	private String documentBaseUrl = "";
	private String skin = "o2k7";
	private String entityEncoding = "raw";
	private String templateExternalListUrl = "editor_stuf/lists/template_list.js";
	private String externalLinkListUrl = "editor_stuf/lists/link_list.js";
	private String externalImageListUrl = "editor_stuf/lists/image_list.js";
	private String mediaExternalListUrl = "editor_stuf/lists/media_list.js";
	
	private String[] themeAdvancedButtons1 = new String[] {"fontselect", "fontsizeselect", "formatselect", "|", "bold", "italic", "underline", "|", "forecolor", "bullist", "numlist", "indent", "outdent", "|", "justifyleft", "justifycenter", "justifyright", "justifyfull", "|", "code", "|", "fullpage", "pdw_toggle"};
	private String[] themeAdvancedButtons1Add = new String[] {""};
	private String[] themeAdvancedButtons2 = new String[] {"hr", "link", "unlink", "backcolor", "|", "table", "col_after", "col_before", "delete_col", "row_after", "row_before", "delete_row", "split_cells", "merge_cells", "row_props", "cell_props", "|", "pastetext", "pasteword", "|", "undo", "redo", "|", "sub", "sup", "strikethrough", "|", "charmap", "removeformat", "anchor", "cleanup"};
	private String[] themeAdvancedButtons2Add = new String[] {"|", "ss_addimage", "ss_wikilink", "ss_youtube"};
	private String[] themeAdvancedButtons3 = new String[] {""};
	private String[] themeAdvancedButtons3Add = new String[] {"tablecontrols"};
	private String[] themeAdvancedButtons4 = new String[] {"insertlayer", "moveforward", "movebackward", "absolute", "|", "styleprops", "|", "cite", "abbr", "acronym", "del", "ins", "attribs", "|", "visualchars", "nonbreaking", "template", "pagebreak"};
	private String[] themeAdvancedBlockFormats= new String[] {"p", "address", "pre", "h1", "h2", "h3", "h4", "h5", "h6"};
	private String themeAdvancedStyles = "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px";
	private String themeAdvancedFonts = "Andale Mono=andale mono,times;Arial=arial,helvetica,sans-serif;Arial Black=arial black,avant garde;Book Antiqua=book antiqua,palatino;Comic Sans MS=comic sans ms,sans-serif;Courier New=courier new,courier;Georgia=georgia,palatino;Helvetica=helvetica;Impact=impact,chicago;Symbol=symbol;Tahoma=tahoma,arial,helvetica,sans-serif;Terminal=terminal,monaco;Times New Roman=times new roman,times;Trebuchet MS=trebuchet ms,geneva;Verdana=verdana,geneva;Webdings=webdings;Wingdings=wingdings,zapf dingbats";
	private int themeAdvancedMoreColors = 1;
	private int themeAdvancedRowHeight = 23;
	private int themeAdvancedResizeHorizontal = 1;
	private String themeAdvancedFontSizes = "1,2,3,4,5,6,7";
		

	/**
	 * Configurations that need to add a language pack to the tinyMCE editor should implement this method.
	 */
	public abstract void addLanguagePacks();
	
	/**
	 * 
	 */
	public boolean getAccessibilityFocus()
	{
		return accessibilityFocus;
	}
	public void setAccessibilityFocus( boolean accessibilityFocus )
	{
		this.accessibilityFocus = accessibilityFocus;
	}
	
	
	/**
	 * 
	 */
	public boolean getAccessibilityWarnings()
	{
		return accessibilityWarnings;
	}
	public void setAccessibilityWarnings( boolean accessibilityWarnings )
	{
		this.accessibilityWarnings = accessibilityWarnings;
	}
	
	
	public String getContentCss() {
		return contentCss;
	}
	public void setContentCss(String content_css) {
		this.contentCss = content_css;
	}
	
	
	/**
	 * 
	 */
	public boolean getConvertFontsToSpans()
	{
		return convertFontsToSpans;
	}
	public void setConvertFontsToSpans( boolean convertFontsToSpans )
	{
		this.convertFontsToSpans = convertFontsToSpans;
	}
	
	
	/**
	 * 
	 */
	public String getDocumentBaseUrl()
	{
		return documentBaseUrl;
	}
	public void setDocumentBaseUrl( String documentBaseUrl )
	{
		this.documentBaseUrl = documentBaseUrl;
	}
	
	
	/**
	 * 
	 */
	public String getEntities()
	{
		return entities;
	}
	public void setEntities( String entities )
	{
		this.entities = entities;
	}
	
	
	public String getEntityEncoding() {
		return entityEncoding;
	}
	public void setEntityEncoding(String entity_encoding) {
		this.entityEncoding = entity_encoding;
	}
	
	
	public String getExternalImageListUrl() {
		return externalImageListUrl;
	}
	public void setExternalImageListUrl(String external_image_list_url) {
		this.externalImageListUrl = external_image_list_url;
	}
	
	
	public String getExternalLinkListUrl() {
		return externalLinkListUrl;
	}
	public void setExternalLinkListUrl(String external_link_list_url) {
		this.externalLinkListUrl = external_link_list_url;
	}
	
	
	/**
	 * 
	 */
	public boolean getGeckoSpellCheck()
	{
		return gecko_spellcheck;
	}
	public void setGeckoSpellCheck( boolean geckoSpellCheck )
	{
		this.gecko_spellcheck = geckoSpellCheck;
	}
	
	
	/**
	 * 
	 */
	public String getLanguage()
	{
		String locale = "";
		String lang = language;
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		if ( ri != null ) {
			locale = ri.getLocale();
		}
		//Special case: If this is Taiwan, set the language to "tw"
		if ("zh_tw".equals(locale.toLowerCase())) {
			lang = "tw";
		}
		return lang;
	}// end getLanguage()
	
	
	public String getMediaExternalListUrl() {
		return mediaExternalListUrl;
	}
	public void setMediaExternalListUrl(String media_external_list_url) {
		this.mediaExternalListUrl = media_external_list_url;
	}
	
	
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	
	/**
	 * 
	 */
	public int getPdwToggle()
	{
		return pdwToggle;
	}
	public void setPdwToggle( int pdwToggle )
	{
		this.pdwToggle = pdwToggle;
	}
	
	
	/**
	 * 
	 */
	public String getPdwToggleToolbars()
	{
		return pdwToggleToolbars;
	}
	public void setPdwToggleToolbars( String pdwToggleToolbars )
	{
		this.pdwToggleToolbars = pdwToggleToolbars;
	}
	
	
	public String getPlugins() {
		String plug = "";
		int c = 1;
		for (String p : plugins) {
			plug += p;
			if (c < plugins.length) {
				plug += ", ";
			}
			++c;
		}
		return plug;
	}
	public void setPlugins(String[] plugins) {
		this.plugins = plugins;
	}
	
	
	/**
	 * 
	 */
	public boolean getRelativeUrls()
	{
		return relativeUrls;
	}
	public void setRelativeUrls( boolean relativeUrls )
	{
		this.relativeUrls = relativeUrls;
	}
	
	
	/**
	 * 
	 */
	public boolean getRemoveScriptHost()
	{
		return removeScriptHost;
	}
	public void setRemoveScriptHost( boolean removeScriptHost )
	{
		this.removeScriptHost = removeScriptHost;
	}
	
	
	public String getSkin() {
		return skin;
	}
	public void setSkin(String skin) {
		this.skin = skin;
	}
	
	
	public String getTemplateExternalListUrl() {
		return templateExternalListUrl;
	}
	public void setTemplateExternalListUrl(String template_external_list_url) {
		this.templateExternalListUrl = template_external_list_url;
	}
	
	
	public String getOnPageLoad() {
		return onPageLoad;
	}
	public void setOnPageLoad(String onPageLoad) {
		this.onPageLoad = onPageLoad;
	}

	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	
	public String getThemeAdvancedBlockFormats() {
		String button1 = "";
		int c = 1;
		for (String p : themeAdvancedBlockFormats) {
			button1 += p;
			if (c < themeAdvancedBlockFormats.length) {
				button1 += ", ";
			}
			++c;
		}
		return button1;
	}
	public void setThemeAdvancedBlockFormats(String[] theme_advanced_blockformats) {
		this.themeAdvancedBlockFormats = theme_advanced_blockformats;
	}
	
	
	public String getThemeAdvancedButtons1() {
		String button1 = "";
		int c = 1;
		for (String p : themeAdvancedButtons1) {
			button1 += p;
			if (c < themeAdvancedButtons1.length) {
				button1 += ", ";
			}
			++c;
		}
		return button1;
	}
	public void setThemeAdvancedButtons1(String[] theme_advanced_buttons1) {
		this.themeAdvancedButtons1 = theme_advanced_buttons1;
	}
	
	
	/**
	 * 
	 */
	public String getThemeAdvancedButtons1Add()
	{
		String button1 = "";
		int c = 1;
		for (String p : themeAdvancedButtons1Add)
		{
			button1 += p;
			if (c < themeAdvancedButtons1Add.length)
			{
				button1 += ", ";
			}
			++c;
		}
		return button1;
	}
	public void setThemeAdvancedButtons1Add( String[] themeAdvancedButtons1Add )
	{
		this.themeAdvancedButtons1Add = themeAdvancedButtons1Add;
	}
	
	
	public String getThemeAdvancedButtons2() {
		String button2 = "";
		int c = 1;
		for (String p : themeAdvancedButtons2) {
			button2 += p;
			if (c < themeAdvancedButtons2.length) {
				button2 += ", ";
			}
			++c;
		}
		return button2;
	}
	public void setThemeAdvancedButtons2(String[] theme_advanced_buttons2) {
		this.themeAdvancedButtons2 = theme_advanced_buttons2;
	}
	
	
	/**
	 * 
	 */
	public String getThemeAdvancedButtons2Add()
	{
		String button2 = "";
		int c = 1;
		for (String p : themeAdvancedButtons2Add)
		{
			button2 += p;
			if (c < themeAdvancedButtons2Add.length)
			{
				button2 += ", ";
			}
			++c;
		}
		return button2;
	}
	public void setThemeAdvancedButtons2Add( String[] themeAdvancedButtons2Add )
	{
		this.themeAdvancedButtons2Add = themeAdvancedButtons2Add;
	}
	
	
	public String getThemeAdvancedButtons3() {
		String button3 = "";
		int c = 1;
		for (String p : themeAdvancedButtons3) {
			button3 += p;
			if (c < themeAdvancedButtons3.length) {
				button3 += ", ";
			}
			++c;
		}
		return button3;
	}
	public void setThemeAdvancedButtons3(String[] theme_advanced_buttons3) {
		this.themeAdvancedButtons3 = theme_advanced_buttons3;
	}
	
	
	/**
	 * 
	 */
	public String getThemeAdvancedButtons3Add()
	{
		String button3 = "";
		int c = 1;
		for (String p : themeAdvancedButtons3Add)
		{
			button3 += p;
			if (c < themeAdvancedButtons3Add.length)
			{
				button3 += ", ";
			}
			++c;
		}
		return button3;
	}
	public void setThemeAdvancedButtons3Add( String[] themeAdvancedButtons3Add )
	{
		this.themeAdvancedButtons3Add = themeAdvancedButtons3Add;
	}
	
	
	public String getThemeAdvancedButtons4() {
		String button4 = "";
		int c = 1;
		for (String p : themeAdvancedButtons4) {
			button4 += p;
			if (c < themeAdvancedButtons4.length) {
				button4 += ", ";
			}
			++c;
		}
		return button4;
	}
	public void setThemeAdvancedButtons4(String[] theme_advanced_buttons4) {
		this.themeAdvancedButtons4 = theme_advanced_buttons4;
	}
	
	
	/**
	 * 
	 */
	public String getThemeAdvancedDisable()
	{
		return themeAdvancedDisable;
	}
	public void setThemeAdvancedDisable( String themeAdvancedDisable )
	{
		this.themeAdvancedDisable = themeAdvancedDisable;
	}
	
	
	/**
	 * 
	 */
	public String getThemeAdvancedFonts()
	{
		return themeAdvancedFonts;
	}
	public void setThemeAdvancedFonts( String themeAdvancedFonts )
	{
		this.themeAdvancedFonts = themeAdvancedFonts;
	}
	
	
	/**
	 * 
	 */
	public String getThemeAdvancedFontSizes()
	{
		return themeAdvancedFontSizes;
	}
	public void setThemeAdvancedFontSizes( String themeAdvancedFontSizes )
	{
		this.themeAdvancedFontSizes = themeAdvancedFontSizes;
	}
	
	
	/**
	 * 
	 */
	public int getThemeAdvancedMoreColors()
	{
		return themeAdvancedMoreColors;
	}
	public void setThemeAdvancedMoreColors( int themeAdvancedMoreColors )
	{
		this.themeAdvancedMoreColors = themeAdvancedMoreColors;
	}
	
	
	/**
	 * 
	 */
	public boolean getThemeAdvancedPath()
	{
		return themeAdvancedPath;
	}
	public void setThemeAdvancedPath( boolean themeAdvancedPath )
	{
		this.themeAdvancedPath = themeAdvancedPath;
	}
	
	
	/**
	 * 
	 */
	public int getThemeAdvancedResizeHorizontal()
	{
		return themeAdvancedResizeHorizontal;
	}
	public void setThemeAdvancedResizeHorizontal( int themeAdvancedResizeHorizontal )
	{
		this.themeAdvancedResizeHorizontal = themeAdvancedResizeHorizontal;
	}
	
	
	public boolean getThemeAdvancedResizing() {
		return themeAdvancedResizing;
	}
	public void setThemeAdvancedResizing( boolean theme_advanced_resizing) {
		this.themeAdvancedResizing = theme_advanced_resizing;
	}
	

	/**
	 * 
	 */
	public boolean getThemeAdvancedResizingUseCookie()
	{
		return themeAdvancedResizingUseCookie;
	}
	public void setThemeAdvancedResizingUseCookie(boolean themeAdvancedResizingUseCookie )
	{
		this.themeAdvancedResizingUseCookie = themeAdvancedResizingUseCookie;
	}
	
	
	/**
	 * 
	 */
	public int getThemeAdvancedRowHeight()
	{
		return themeAdvancedRowHeight;
	}
	public void setThemeAdvancedRowHeight( int themeAdvancedRowHeight )
	{
		this.themeAdvancedRowHeight = themeAdvancedRowHeight;
	}
	
	
	public String getThemeAdvancedStatusbarLocation() {
		return themeAdvancedStatusbarLocation;
	}
	public void setThemeAdvancedStatusbarLocation(String theme_advanced_statusbar_location) {
		this.themeAdvancedStatusbarLocation = theme_advanced_statusbar_location;
	}
	

	/**
	 * 
	 */
	public String getThemeAdvancedStyles()
	{
		return themeAdvancedStyles;
	}
	public void setThemeAdvancedStyles( String themeAdvancedStyles )
	{
		this.themeAdvancedStyles = themeAdvancedStyles;
	}
	
	
	public String getThemeAdvancedToolbarAlign() {
		return themeAdvancedToolbarAlign;
	}
	public void setThemeAdvancedToolbarAlign(String theme_advanced_toolbar_align) {
		this.themeAdvancedToolbarAlign = theme_advanced_toolbar_align;
	}
	
	
	public String getThemeAdvancedToolbarLocation() {
		return themeAdvancedToolbarLocation;
	}
	public void setThemeAdvancedToolbarLocation(String theme_advanced_toolbar_location) {
		this.themeAdvancedToolbarLocation = theme_advanced_toolbar_location;
	}
	
	
	/**
	 * 
	 */
	public String getWidth()
	{
		return width;
	}
	public void setWidth( String width )
	{
		this.width = width;
	}


	/**
	 * Set the content css we should use.
	 */
	public abstract void setContentCss();
	
	
	/**
	 * Set the language we are running in.
	 */
	public abstract void setLanguage();
	
	protected abstract void getDocumentBaseUrlFromServer();
	
	/**
	 * This method should be called to notify us that we are now working with a new binder.
	 */
	public void setBinderId( String binderId )
	{
		// Is the binder id changing?
		if ( m_binderId == null || m_binderId.equalsIgnoreCase( binderId ) == false )
		{
			// Yes
			// Issue an ajax request to get the document base url for this binder.
			m_binderId = binderId;
			getDocumentBaseUrlFromServer();
		}
	}
	
	/**
	 * Set the list of file attachments the user can choose from when they invoke the "Add image" dialog.
	 */
	public void setListOfFileAttachments( 	ArrayList<String> listOfFileAttachments )
	{
		m_listOfFileAttachments = listOfFileAttachments;
	}// end setListOfFileAttachments()
	
	/**
	 * Return whether we are waiting for an rpc request to finish.
	 */
	public boolean isRpcInProgress()
	{
		return m_rpcInProgress;
	}// end isRpcInProgress()
}// end AbstractTinyMCEConfiguration
