<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>

<c:if test="${!empty ss_portletInitialization}">
<div class="ss_style">
<br/>
<span class="ss_italic"><ssf:nlt tag="portlet.reloadNeeded"/></span>
<br/>
<br/>
<a href="${ss_portletInitializationUrl}">
  <ssf:nlt tag="portlet.clickForReload"/>
</a>
<br/>
</div>
</c:if>
<c:if test="${empty ss_portletInitialization}">
<c:if test="${empty ssf_support_files_loaded}">
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>
<%
boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
%>

<script type="text/javascript">
// Dojo configuration
djConfig = { 
	isDebug: false,
	parseWidgets: false, // make dojo startup a little bit faster
	searchIds: []
};
</script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/dojo.js"></script>
<script type="text/javascript">
var ss_scripts_loaded = "no";
var scripts = document.getElementsByTagName("script");
for (var i = 0; i < scripts.length; i++) {
	if (scripts[i].src) {
		if (scripts[i].src.indexOf("/ss_common.js") >= 0) {
			ss_scripts_loaded = "yes";
			break;
		}
	}
}
var undefined;
var ss_urlBase;
var ss_rootPath;
var ss_imagesPath;
var ss_1pix;
var ss_defaultStyleSheet;
var ss_forumCssUrl;
var ss_forumColorsCssUrl;
var ss_helpSystemUrl;
var ss_addTabUrl;
var ss_not_logged_in;
var ss_nlt_navigation_normal;
var ss_nlt_navigation_maximize;


if (ss_scripts_loaded && ss_scripts_loaded == "no") {
	ss_urlBase = self.location.protocol + "//" + self.location.host;
	ss_rootPath = "<html:rootPath/>";
	ss_imagesPath = "<html:imagesPath/>";
	
	ss_defaultStyleSheet = 'blackandwhite';
	ss_forumCssUrl = ss_urlBase + ss_rootPath + "css/forum.css";
	ss_1pix = ss_imagesPath + "pics/1pix.gif";
	ss_forumColorsCssUrl = "<ssf:url
	    webPath="viewCss">
	    <ssf:param name="theme" value=""/>
	    </ssf:url>";
	
	//Help system url (used to request a help panel to be shown).
	ss_helpSystemUrl = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="show_help_panel" />
		<ssf:param name="operation2" value="ss_help_panel_id_place_holder" />
		</ssf:url>";
	
	ss_addTabUrl = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="add_tab" />
		<ssf:param name="binderId" value="ss_binderid_place_holder" />
		<ssf:param name="entryId" value="ss_entryid_place_holder" />
		<ssf:param name="tabId" value="ss_tabid_place_holder" />
		<ssf:param name="type" value="ss_tab_type_place_holder" />
		</ssf:url>";

	ss_deleteTabUrl = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="delete_tab" />
		<ssf:param name="tabId" value="ss_tabid_place_holder" />
		</ssf:url>";

	ss_setCurrentTabUrl = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="set_current_tab" />
		<ssf:param name="tabId" value="ss_tabid_place_holder" />
		</ssf:url>";

	ss_musterUrl = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="ss_operation_place_holder" />
		</ssf:url>";
		
	//Not logged in message
	ss_not_logged_in = "<ssf:nlt tag="general.notLoggedIn"/>";
	
	//Global toolbar text
	ss_nlt_navigation_normal = "<ssf:nlt tag="navigation.normal"/>";
	ss_nlt_navigation_maximize = "<ssf:nlt tag="navigation.maximize"/>";
	
	//Clipboard text
	ss_clipboardTitleText = "<ssf:nlt tag="clipboard.title"/>";
	ss_addContributesToClipboardText = "<ssf:nlt tag="button.add_contributes_to_clipboard"/>";
	ss_addTeamMembersToClipboardText = "<ssf:nlt tag="button.sdd_team_members_to_clipboard"/>";
	ss_clearClipboardText = "<ssf:nlt tag="button.clear_clipboard"/>";
	ss_noUsersOnClipboardText = "<ssf:nlt tag="clipboard.noUsers"/>";
	ss_closeButtonText = "<ssf:nlt tag="button.close"/>";
	ss_selectAllBtnText = "<ssf:nlt tag="button.selectAll"/>";
	ss_clearAllBtnText = "<ssf:nlt tag="button.clearAll"/>";
	
	// Team memmbers text
	ss_noTeamMembersText = "<ssf:nlt tag="teamMembers.noUsers"/>";
}

function ss_loadDojoFiles() {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		//dojo.require("dojo.fx.*");
		dojo.require("dojo.html.*");
		//dojo.require("dojo.math.*");
		//dojo.require("dojo.math.curves");
		dojo.require("dojo.lfx.*");
		dojo.require("dojo.event.*");
		dojo.require("dojo.lang.*");
		//dojo.require("dojo.string.*");
		dojo.require("dojo.dnd.*");
		//dojo.require("dojo.widget.*");
		//dojo.require("dojo.widget.TaskBar");
		//dojo.require("dojo.widget.LayoutContainer");
		//dojo.require("dojo.widget.FloatingPane");
		//dojo.require("dojo.widget.ResizeHandle");
		//ss_loadJsFile(ss_rootPath, "js/common/ss_dashboard_drag_and_drop.js");
	}
}

function ss_createStyleSheet(url, title, enabled) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		if (enabled == null || enabled == "") enabled = false;
		var styles = "@import url('" + " " + url + " " + "');";
		var newSS = document.createElement('link');
		newSS.rel = 'stylesheet';
		if (title != null && title != "") {
			newSS.setAttribute("title", title);
			newSS.disabled = true;
			if (enabled == true) {
				newSS.disabled = false;
			}
		}
		newSS.href = 'data:text/css,' + escape(styles);
		//newSS.href = url;
		document.getElementsByTagName("head")[0].appendChild(newSS);
	}
}
function ss_changeStyles(title) {
	var i, a, main;
	for (i=0; (a = document.getElementsByTagName("link")[i]); i++) {
		if (a.getAttribute("rel").indexOf("style") != -1 && 
				a.getAttribute("title") != null && a.getAttribute("title") != "" ) {
			a.disabled = true;
			if (a.getAttribute("title") == title) {
				a.disabled = false;
			}
		}
	}
}
function ss_setDefaultStyleSheet() {
	//Set the user's desired style
	ss_changeStyles(ss_defaultStyleSheet);
}

function ss_loadJsFile(rootPath, jsFile) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		var spath = rootPath + jsFile;
		var scripts = document.getElementsByTagName("script");
		for (var i = 0; i < scripts.length; i++) {
			if (scripts[i].src && scripts[i].src == spath) return;
		}
		try {
			document.writeln("<scr"+"ipt type='text/javascript' src='"+spath+"'></scr"+"ipt>");
		} catch (e) {
			var script = document.createElement("script");
			script.src = spath;
			document.getElementsByTagName("head")[0].appendChild(script);
		}
	}
}
if (ss_scripts_loaded && ss_scripts_loaded == "no") {
	ss_loadJsFile(ss_rootPath, "js/common/ss_common.js");
}
</script>
<script type="text/javascript">
if (!ss_js_files_loaded || ss_js_files_loaded == undefined || ss_js_files_loaded == "undefined" ) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		//ss_loadJsFile(ss_rootPath, "js/common/ss_drag.js");
		//ss_loadJsFile(ss_rootPath, "js/common/ss_dragdrop.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_coordinates.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
		ss_loadJsFile(ss_rootPath, "js/common/taconite-client.js");
		ss_loadJsFile(ss_rootPath, "js/common/taconite-parser.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_dashboard_drag_and_drop.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_dashboard_drag_and_drop.js");
		
		<c:if test="${!empty ssFooterToolbar.RSS.url}">
			//Add the rss feed info
			var linkEle = document.createElement("link");
			linkEle.setAttribute("rel", "alternate");
			linkEle.setAttribute("type", "application/rss+xml");
			linkEle.setAttribute("title", "RSS feed");
			linkEle.setAttribute("href", "${ssFooterToolbar.RSS.url}");
			document.getElementsByTagName("head")[0].appendChild(linkEle);
		</c:if>
	}
}
var ss_js_files_loaded = 1;

<c:if test="${empty ss_notAdapter}">
<ssf:ifnotadapter>
var ss_baseEntryUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
	name="action" value="ssActionPlaceHolder"/><portlet:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
	name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
	name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>';
var ss_baseEntryUrl<portlet:namespace/> = ss_baseEntryUrl;
	
var ss_baseBinderUrl = '<portlet:renderURL windowState="maximized"><portlet:param 
	name="action" value="ssActionPlaceHolder"/><portlet:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
	name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>';
var ss_baseBinderUrl<portlet:namespace/> = ss_baseBinderUrl;
var ss_baseFileUrl = '<ssf:url webPath="viewFile" folderId="ssBinderIdPlaceHolder" 
   	entryId="ssEntryIdPlaceHolder"></ssf:url>';
var ss_baseFileUrl<portlet:namespace/> = ss_baseFileUrl;
</ssf:ifnotadapter>
</c:if>

var ss_baseAppletFileUploadURL = "<ssf:url adapter="true" portletName="ss_forum" action="add_entry_attachment" actionUrl="true" >
		<ssf:param name="binderId" value="ssBinderIdPlaceHolder" />
		<ssf:param name="entryId" value="ssEntryIdPlaceHolder" />
		<ssf:param name="operation" value="add_files_by_browse_for_entry" />
    </ssf:url>";

var ss_baseAjaxRequest = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="false">
		<ssf:param name="binderId" value="ssBinderIdPlaceHolder" />
		<ssf:param name="entryId" value="ssEntryIdPlaceHolder" />
		<ssf:param name="operation" value="ssOperationPlaceHolder" />
		<ssf:param name="namespace" value="ssNameSpacePlaceHolder" />
    </ssf:url>";

var ss_baseAjaxRequestWithOS = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="false" >
		<ssf:param name="binderId" value="ssBinderIdPlaceHolder" />
		<ssf:param name="entryId" value="ssEntryIdPlaceHolder" />
		<ssf:param name="operation" value="ssOperationPlaceHolder" />
		<ssf:param name="namespace" value="ssNameSpacePlaceHolder" />
		<ssf:param name="ssOSInfo" value="ssOSPlaceHolder" />
    </ssf:url>";

var ss_labelButtonOK = "<ssf:nlt tag="button.ok"/>";
var ss_labelButtonCancel = "<ssf:nlt tag="button.cancel"/>";
var ss_labelEntryChooseFileWarning = "<ssf:nlt tag="entry.chooseFileWarningMessage"/>";
var ss_labelEntryBrowseAddAttachmentHelpText = "<ssf:nlt tag="entry.browseAddAttachmentHelpText"/>";
var ss_htmlRootPath = "<html:rootPath/>";
</script>

<c:if test="${!empty ss_servlet && ss_servlet == 'true'}">
<link href="<html:rootPath/>css/forum.css" rel="stylesheet" type="text/css" />
<link href="<ssf:url
	    webPath="viewCss">
	    <ssf:param name="theme" value=""/>
	    </ssf:url>" rel="stylesheet" type="text/css" />
</c:if>

<script type="text/javascript">
<c:if test="${empty ss_servlet || ss_servlet != 'true'}">
if (!ss_css_files_loaded || ss_css_files_loaded == undefined || ss_css_files_loaded == "undefined" ) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		if (document.createStyleSheet) {
			document.createStyleSheet(ss_forumCssUrl);
			document.createStyleSheet(ss_forumColorsCssUrl);
		} else {
			ss_createStyleSheet(ss_forumCssUrl);
			ss_createStyleSheet(ss_forumColorsCssUrl);
		}
	}
}
var ss_css_files_loaded = 1;
</c:if>

<c:set var="ss_loadCssStylesInline" value="true" scope="request"/>
<c:set var="ss_skipCssStyles" value="true" scope="request"/>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />

//Routine to round the corners of the rounded box tag
function ss_rounded() {
	return
	if (self.NiftyCheck != null && self.NiftyCheck()) {
		Rounded("div.ss_rounded", "all", "${ss_style_background_color}", "${ss_form_background_color}", "smooth");
		Rounded("div.ss_rounded_border", "all", "${ss_style_background_color}", "transparent", "border smooth");
		Rounded("div.ss_rounded_border_form", "all", "${ss_form_background_color}", "transparent", "border smooth");
	}
}
//ss_createOnLoadObj('ss_rounded', ss_rounded);

function ss_defineColorValues() {
	ss_style_background_color = '${ss_style_background_color}';
	ss_dashboard_table_border_color = '${ss_dashboard_table_border_color}';
}

if (ss_scripts_loaded && ss_scripts_loaded == "no") {
	ss_createOnLoadObj('ss_loadDojoFiles', ss_loadDojoFiles);
	ss_createOnLoadObj('ss_setDefaultStyleSheet', ss_setDefaultStyleSheet);
	ss_createOnLoadObj('ss_defineColorValues', ss_defineColorValues);
}
</script>
</c:if>
</c:if>
