/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtMainPage;

import com.google.gwt.user.client.Window;

/**
 * 
 */
public abstract class AbstractTinyMCEConfiguration
{
	private String mode = "none";
	private String theme = "advanced";
	private String language = GwtMainPage.m_requestInfo.getLanguage();
	private String contentCss = GwtMainPage.m_requestInfo.getContentCss();
	private String relativeUrls = "false";
	private String removeScriptHost = "false";
	private String width = "100%";
	private String accessibilityWarnings = "true";
	private String accessibilityFocus = "true";
	private String entities = "39,#39,34,quot,38,amp,60,lt,62,gt";
	private String gecko_spellcheck = "true";
	private String themeAdvancedToolbarLocation = "top";
	private String themeAdvancedToolbarAlign = "left";
	private String themeAdvancedStatusbarLocation = "bottom";
	private String themeAdvancedResizing = "true";
	private String themeAdvancedResizingUseCookie = "true";
	private String themeAdvancedPath = "false";
	private String themeAdvancedDisable = "image,advimage";
	private String convertFontsToSpans = "true";

	private String documentBaseUrl = "";
	private String skin = "o2k7";
	private String entityEncoding = "raw";
	private String templateExternalListUrl = "editor_stuf/lists/template_list.js";
	private String externalLinkListUrl = "editor_stuf/lists/link_list.js";
	private String externalImageListUrl = "editor_stuf/lists/image_list.js";
	private String mediaExternalListUrl = "editor_stuf/lists/media_list.js";
	
	private String[] plugins = new String[] {"compat2x", "table", "ss_addimage", "preview", "paste", "ss_wikilink", "ss_youtube"};
	
	private String[] themeAdvancedButtons1 = new String[] {"mymenubutton" , "newdocument" , "|", "bold", "italic", "underline", "strikethrough", "|", "justifyleft", "justifycenter", "justifyright", "justifyfull", "formatselect"};
	private String[] themeAdvancedButtons1Add = new String[] {"forecolor" , "backcolor"};
	private String[] themeAdvancedButtons2 = new String[] {"cut", "copy", "paste", "pastetext", "pasteword", "|", "search,replace", "|", "bullist", "numlist", "|", "outdent", "indent", "blockquote", "|", "undo", "redo", "|", "link", "unlink", "anchor", "cleanup", "code", "|", "insertdate", "inserttime"};
	private String[] themeAdvancedButtons2Add = new String[] {"pastetext", "pasteword", "ss_addimage", "ss_wikilink", "ss_youtube"};
	private String[] themeAdvancedButtons3 = new String[] {"hr", "removeformat", "visualaid", "|", "sub", "sup", "|", "charmap", "iespell", "advhr", "|", "ltr", "rtl", "|", "fullscreen"};
	private String[] themeAdvancedButtons3Add = new String[] {"tablecontrols"};
	private String[] themeAdvancedButtons4 = new String[] {"insertlayer", "moveforward", "movebackward", "absolute", "|", "styleprops", "|", "cite", "abbr", "acronym", "del", "ins", "attribs", "|", "visualchars", "nonbreaking", "template", "pagebreak"};
	private String themeAdvancedStyles = "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px";
	

	/**
	 * Configurations that need to add a language pack to the tinyMCE editor should implement this method.
	 */
	public abstract void addLanguagePacks();
	
	/**
	 * 
	 */
	public String getAccessibilityFocus()
	{
		return accessibilityFocus;
	}
	public void setAccessibilityFocus( String accessibilityFocus )
	{
		this.accessibilityFocus = accessibilityFocus;
	}
	
	
	/**
	 * 
	 */
	public String getAccessibilityWarnings()
	{
		return accessibilityWarnings;
	}
	public void setAccessibilityWarnings( String accessibilityWarnings )
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
	public String getConvertFontsToSpans()
	{
		return convertFontsToSpans;
	}
	public void setConvertFontsToSpans( String convertFontsToSpans )
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
	public String getGeckoSpellCheck()
	{
		return gecko_spellcheck;
	}
	public void setGeckoSpellCheck( String geckoSpellCheck )
	{
		this.gecko_spellcheck = geckoSpellCheck;
	}
	
	
	/**
	 * 
	 */
	public String getLanguage()
	{
		return language;
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
	public String getRelativeUrls()
	{
		return relativeUrls;
	}
	public void setRelativeUrls( String relativeUrls )
	{
		this.relativeUrls = relativeUrls;
	}
	
	
	/**
	 * 
	 */
	public String getRemoveScriptHost()
	{
		return removeScriptHost;
	}
	public void setRemoveScriptHost( String removeScriptHost )
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
	
	
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
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
	public String getThemeAdvancedPath()
	{
		return themeAdvancedPath;
	}
	public void setThemeAdvancedPath( String themeAdvancedPath )
	{
		this.themeAdvancedPath = themeAdvancedPath;
	}
	
	
	public String getThemeAdvancedResizing() {
		return themeAdvancedResizing;
	}
	public void setThemeAdvancedResizing(String theme_advanced_resizing) {
		this.themeAdvancedResizing = theme_advanced_resizing;
	}
	

	/**
	 * 
	 */
	public String getThemeAdvancedResizingUseCookie()
	{
		return themeAdvancedResizingUseCookie;
	}
	public void setThemeAdvancedResizingUseCookie(String themeAdvancedResizingUseCookie )
	{
		this.themeAdvancedResizingUseCookie = themeAdvancedResizingUseCookie;
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
	
}// end AbstractTinyMCEConfiguration
