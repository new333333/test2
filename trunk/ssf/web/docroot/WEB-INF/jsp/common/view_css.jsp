<c:if test="${empty ssf_support_files_loaded}">
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>
<%@ page import="com.sitescape.ef.context.request.RequestContextHolder" %>
<%@ page import="com.sitescape.ef.domain.User" %>

<%
//Set up the user object
if(RequestContextHolder.getRequestContext() != null) {
	User user = RequestContextHolder.getRequestContext().getUser();
	request.setAttribute("ssUser", user);
}

boolean isIE = BrowserSniffer.is_ie(request);
%>
<script type="text/javascript" src="<html:rootPath/>js/dojo/dojo.js"></script>
<script type="text/javascript">
var undefined;
var ss_urlBase = self.location.protocol + "//" + self.location.host;
var ss_rootPath = "<html:rootPath/>";
var ss_imagesPath = "<html:imagesPath/>";

function ss_loadJsFile(rootPath, jsFile) {
	var spath = rootPath + jsFile;
	try {
		document.writeln("<scr"+"ipt type='text/javascript' src='"+spath+"'></scr"+"ipt>");
	} catch (e) {
		var script = document.createElement("script");
		script.src = spath;
		document.getElementsByTagName("head")[0].appendChild(script);
	}
}

if (!ss_js_files_loaded || ss_js_files_loaded == undefined || ss_js_files_loaded == "undefined" ) {
	ss_loadJsFile(ss_rootPath, "js/common/ss_common.js");
	ss_loadJsFile(ss_rootPath, "js/common/nifty_corners.js");
	ss_loadJsFile(ss_rootPath, "js/common/ss_drag.js");
	ss_loadJsFile(ss_rootPath, "js/common/ss_dragdrop.js");
	ss_loadJsFile(ss_rootPath, "js/common/ss_coordinates.js");
	ss_loadJsFile(ss_rootPath, "js/common/taconite-client.js");
	ss_loadJsFile(ss_rootPath, "js/common/taconite-parser.js");
	//ss_loadJsFile(ss_rootPath, "js/dojo/dojo.js");
	ss_loadJsFile(ss_rootPath, "js/dojo/src/style.js");
	ss_loadJsFile(ss_rootPath, "js/dojo/src/fx/html.js");
	ss_loadJsFile(ss_rootPath, "js/dojo/src/math/Math.js");
	ss_loadJsFile(ss_rootPath, "js/dojo/src/math/curves.js");
	ss_loadJsFile(ss_rootPath, "js/dojo/src/lfx/html.js");
	ss_loadJsFile(ss_rootPath, "js/common/ss_dashboard_drag_and_drop.js");
	//ss_loadJsFile(ss_rootPath, "js/common/ss_dragsort.js");
}
var ss_js_files_loaded = 1;
</script>

<script type="text/javascript">
function ss_loadDojoFiles() {
	dojo.require("dojo.fx.*");
	dojo.require("dojo.math");
	//dojo.require("dojo.lfx.*");
	dojo.require("dojo.dnd.*");
	dojo.require("dojo.event.*");
	//dojo.require("dojo.widget.*");
	//dojo.require("dojo.widget.TaskBar");
	//dojo.require("dojo.widget.LayoutContainer");
	//dojo.require("dojo.widget.FloatingPane");
	//dojo.require("dojo.widget.ResizeHandle");
}
ss_createOnLoadObj('ss_loadDojoFiles', ss_loadDojoFiles);

function ss_createStyleSheet(url, title, enabled) {
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
var ss_defaultStyleSheet = 'blackandwhite';
ss_changeStyles(ss_defaultStyleSheet);
function ss_setDefaultStyleSheet() {
	//Set the user's desired style
	ss_changeStyles(ss_defaultStyleSheet);
}
ss_createOnLoadObj('ss_setDefaultStyleSheet', ss_setDefaultStyleSheet);

var ss_forumCssUrl = ss_urlBase + ss_rootPath + "css/forum.css";
var niftyCornersCssUrl = ss_urlBase + ss_rootPath + "css/nifty_corners.css";
//var htmlareaCssUrl = ss_urlBase + ss_rootPath + "js/htmleditor/htmlarea.css";
var ss_forumColorsCssUrl = "<ssf:url
    webPath="viewCss">
    <ssf:param name="theme" value=""/>
    </ssf:url>"
var ss_forumColorDebugCssUrl = "<ssf:url
    webPath="viewCss">
    <ssf:param name="theme" value="debug"/>
    </ssf:url>"
var ss_forumColorBlackAndWhiteCssUrl = "<ssf:url
    webPath="viewCss">
    <ssf:param name="theme" value="blackandwhite"/>
    </ssf:url>"
if (document.createStyleSheet) {
	document.createStyleSheet(ss_forumCssUrl);
	document.createStyleSheet(ss_forumColorsCssUrl);
	document.createStyleSheet(ss_forumColorBlackAndWhiteCssUrl, "blackandwhite", true);
	document.createStyleSheet(ss_forumColorDebugCssUrl, "debug");
	document.createStyleSheet(niftyCornersCssUrl);
	//document.createStyleSheet(htmlareaCssUrl);
} else {
	ss_createStyleSheet(ss_forumCssUrl);
	ss_createStyleSheet(ss_forumColorsCssUrl);
	ss_createStyleSheet(ss_forumColorBlackAndWhiteCssUrl, "blackandwhite", true);
	ss_createStyleSheet(ss_forumColorDebugCssUrl, "debug");
	ss_createStyleSheet(niftyCornersCssUrl);
	//ss_createStyleSheet(htmlareaCssUrl);
}

//Help system url (used to request a help panel to be shown).
var ss_helpSystemUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="show_help_panel" />
	<ssf:param name="operation2" value="ss_help_panel_id_place_holder" />
	</ssf:url>"

//Not logged in message
var ss_not_logged_in = "<ssf:nlt tag="general.notLoggedIn"/>";

</script>

<c:set var="ss_color_theme" value="debug" scope="request"/>
<c:set var="ss_skipCssStyles" value="true" scope="request"/>
<style>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />
</style>

<script type="text/javascript">

//Routine to round the corners of the rounded box tag
function ss_rounded() {
	if(!NiftyCheck()) return;
	Rounded("div.ss_rounded", "all", "${ss_style_background_color}", "${ss_form_background_color}", "smooth");
	Rounded("div.ss_rounded_border", "all", "${ss_style_background_color}", "transparent", "border smooth");
	Rounded("div.ss_rounded_border_form", "all", "${ss_form_background_color}", "transparent", "border smooth");
}
ss_createOnLoadObj('ss_rounded', ss_rounded);

function ss_defineColorValues() {
	ss_style_background_color = '${ss_style_background_color}';
	ss_dashboard_table_border_color = '${ss_dashboard_table_border_color}';
}
ss_createOnLoadObj('ss_defineColorValues', ss_defineColorValues);
</script>

</c:if>

