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

<script type="text/javascript" src="<html:rootPath/>js/common/ss_common.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/nifty_corners.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_drag.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_dragdrop.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_coordinates.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/dojo.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/src/style.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/src/fx/html.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/src/math/Math.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/src/math/curves.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/dojo/src/lfx/html.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_dashboard_drag_and_drop.js"></script>
<script type="text/javascript">
	dojo.require("dojo.fx.*");
	dojo.require("dojo.math");
	//dojo.require("dojo.lfx.*");
	dojo.require("dojo.dnd.*");
	dojo.require("dojo.event.*");
</script>
  <c:if test="0">
<script type="text/javascript" src="<html:rootPath/>js/common/ss_dragsort.js"></script>
  </c:if>
<script type="text/javascript">

function ss_createStyleSheet(url) {
	var styles = "@import url('" + " " + url + " " + "');";
	var newSS = document.createElement('link');
	newSS.rel = 'stylesheet';
	newSS.href = 'data:text/css,' + escape(styles);
	document.getElementsByTagName("head")[0].appendChild(newSS);
}

var ss_urlBase = self.location.protocol + "//" + self.location.host;
var ss_forumCssUrl = ss_urlBase + "<html:rootPath/>css/forum.css";
var niftyCornersCssUrl = ss_urlBase + "<html:rootPath/>css/nifty_corners.css";
//var htmlareaCssUrl = ss_urlBase + "<html:rootPath/>js/htmleditor/htmlarea.css";
if (document.createStyleSheet) {
	document.createStyleSheet(ss_forumCssUrl);
	document.createStyleSheet(niftyCornersCssUrl);
	//document.createStyleSheet(htmlareaCssUrl);
} else {
	ss_createStyleSheet(ss_forumCssUrl);
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
function ss_notLoggedIn() {
	alert(ss_not_logged_in);
}

</script>

<c:set var="ss_color_theme" value="debug" scope="request"/>
<style>
<jsp:include page="/WEB-INF/jsp/common/ssf_css.jsp" />
</style>

<script type="text/javascript">

//Routine to round the corners of the rounded box tag
function ss_rounded() {
	if(!NiftyCheck()) return;
	Rounded("*.ss_rounded", "all", "${ss_style_background_color}", "${ss_form_background_color}", "smooth");
	Rounded("*.ss_rounded_border", "all", "${ss_style_background_color}", "transparent", "border smooth");
	Rounded("*.ss_rounded_border_form", "all", "${ss_form_background_color}", "transparent", "border smooth");
}
ss_createOnLoadObj('ss_rounded', ss_rounded);

function ss_defineColorValues() {
	ss_style_background_color = '${ss_style_background_color}';
	ss_dashboard_table_border_color = '${ss_dashboard_table_border_color}';
}
ss_createOnLoadObj('ss_defineColorValues', ss_defineColorValues);
</script>

</c:if>

