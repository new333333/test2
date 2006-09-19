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
var ss_defaultStyleSheet;
var ss_forumCssUrl;
var niftyCornersCssUrl;
//var htmlareaCssUrl;
var ss_forumColorsCssUrl;
var ss_forumColorDebugCssUrl;
var ss_forumColorBlackAndWhiteCssUrl;
var ss_helpSystemUrl;
var ss_not_logged_in;

if (ss_scripts_loaded && ss_scripts_loaded == "no") {
	ss_urlBase = self.location.protocol + "//" + self.location.host;
	ss_rootPath = "<html:rootPath/>";
	ss_imagesPath = "<html:imagesPath/>";
	
	ss_defaultStyleSheet = 'blackandwhite';
	ss_forumCssUrl = ss_urlBase + ss_rootPath + "css/forum.css";
	niftyCornersCssUrl = ss_urlBase + ss_rootPath + "css/nifty_corners.css";
	//htmlareaCssUrl = ss_urlBase + ss_rootPath + "js/htmleditor/htmlarea.css";
	ss_forumColorsCssUrl = "<ssf:url
	    webPath="viewCss">
	    <ssf:param name="theme" value=""/>
	    </ssf:url>";
	ss_forumColorDebugCssUrl = "<ssf:url
	    webPath="viewCss">
	    <ssf:param name="theme" value="debug"/>
	    </ssf:url>";
	ss_forumColorBlackAndWhiteCssUrl = "<ssf:url
	    webPath="viewCss">
	    <ssf:param name="theme" value="blackandwhite"/>
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
	
	//Not logged in message
	ss_not_logged_in = "<ssf:nlt tag="general.notLoggedIn"/>";
}

function ss_loadDojoFiles() {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
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
	ss_loadJsFile(ss_rootPath, "js/dojo/dojo.js");
	ss_loadJsFile(ss_rootPath, "js/common/ss_common.js");
}
</script>
<script type="text/javascript">
if (!ss_js_files_loaded || ss_js_files_loaded == undefined || ss_js_files_loaded == "undefined" ) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		//ss_loadJsFile(ss_rootPath, "js/dojo/dojo.js");
		//ss_loadJsFile(ss_rootPath, "js/common/nifty_corners.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_drag.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_dragdrop.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_coordinates.js");
		ss_loadJsFile(ss_rootPath, "js/common/taconite-client.js");
		ss_loadJsFile(ss_rootPath, "js/common/taconite-parser.js");
		ss_loadJsFile(ss_rootPath, "js/dojo/src/style.js");
		ss_loadJsFile(ss_rootPath, "js/dojo/src/fx/html.js");
		ss_loadJsFile(ss_rootPath, "js/dojo/src/math/Math.js");
		ss_loadJsFile(ss_rootPath, "js/dojo/src/math/curves.js");
		ss_loadJsFile(ss_rootPath, "js/dojo/src/lfx/html.js");
		ss_loadJsFile(ss_rootPath, "js/common/ss_dashboard_drag_and_drop.js");
		//ss_loadJsFile(ss_rootPath, "js/common/ss_dragsort.js");
		
		<c:if test="${!empty ssFooterToolbar.RSS.url}">
			//Add the rss feed info
			var linkEle = document.createElement("link");
			linkEle.setAttribute("rel", "alternate");
			linkEle.setAttribute("type", "application/rss+xml");
			linkEle.setAttribute("title", "RSS feed");
			linkEle.setAttribute("src", "${ssFooterToolbar.RSS.url}");
			document.getElementsByTagName("head")[0].appendChild(linkEle);
		</c:if>
	}
}
var ss_js_files_loaded = 1;
</script>

<script type="text/javascript">
if (!ss_css_files_loaded || ss_css_files_loaded == undefined || ss_css_files_loaded == "undefined" ) {
	if (ss_scripts_loaded && ss_scripts_loaded == "no") {
		if (document.createStyleSheet) {
			document.createStyleSheet(ss_forumCssUrl);
			document.createStyleSheet(ss_forumColorsCssUrl);
			document.createStyleSheet(ss_forumColorBlackAndWhiteCssUrl, "blackandwhite", true);
			document.createStyleSheet(ss_forumColorDebugCssUrl, "debug");
			//document.createStyleSheet(niftyCornersCssUrl);
			//document.createStyleSheet(htmlareaCssUrl);
		} else {
			ss_createStyleSheet(ss_forumCssUrl);
			ss_createStyleSheet(ss_forumColorsCssUrl);
			ss_createStyleSheet(ss_forumColorBlackAndWhiteCssUrl, "blackandwhite", true);
			ss_createStyleSheet(ss_forumColorDebugCssUrl, "debug");
			//ss_createStyleSheet(niftyCornersCssUrl);
			//ss_createStyleSheet(htmlareaCssUrl);
		}
	}
}
var ss_css_files_loaded = 1;

<c:set var="ss_color_theme" value="debug" scope="request"/>
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

