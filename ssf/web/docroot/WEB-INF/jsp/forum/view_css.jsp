
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>
<%@ page import="com.sitescape.ef.context.request.RequestContextHolder" %>
<%@ page import="com.sitescape.ef.domain.User" %>
<%
//Set some default colors (alpha: med brownish, beta: light brownish, gamma:sand (?)
String alphaColor = "#775325";
String betaColor = "#B89257";
String gammaColor = "#CCCC99";

//Set up the user object
if(RequestContextHolder.getRequestContext() != null) {
	User user = RequestContextHolder.getRequestContext().getUser();
	request.setAttribute("ssUser", user);
}

boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${empty ssf_support_files_loaded}">

<script language="javascript" type="text/javascript">
function ss_createStyleSheet(url) {
	var styles = "@import url('" + " " + url + " " + "');";
	var newSS = document.createElement('link');
	newSS.rel = 'stylesheet';
	newSS.href = 'data:text/css,' + escape(styles);
	document.getElementsByTagName("head")[0].appendChild(newSS);
}

var ss_urlBase = self.location.protocol + "//" + self.location.host + "/";
var ss_forumCssUrl = ss_urlBase + "<html:rootPath/>css/forum.css";
<c:if test="<%= isIE %>">
var ss_forumCss2Url = ss_urlBase + "<html:rootPath/>css/forum_ie.css";
</c:if>
<c:if test="<%= !isIE %>">
var ss_forumCss2Url = ss_urlBase + "<html:rootPath/>css/forum_nn.css";
</c:if>
var niftyCornersCssUrl = ss_urlBase + "<html:rootPath/>css/nifty_corners.css";
if (document.createStyleSheet) {
	document.createStyleSheet(ss_forumCssUrl);
	document.createStyleSheet(ss_forumCss2Url);
	document.createStyleSheet(niftyCornersCssUrl);
} else {
	ss_createStyleSheet(ss_forumCssUrl);
	ss_createStyleSheet(ss_forumCss2Url);
	ss_createStyleSheet(niftyCornersCssUrl);
}
</script>

<style>
/* Forum toolbar */
div.ss_toolbar {
  width: 100%; 
  border-top: 1px solid #3366cc;
  border-bottom: 1px solid #3366cc;
  background-color: #f7f7f7;
  margin-top: 0px;
  margin-bottom: 8px;
  }
  
/* Forum historybar */
div.ss_historybar {
  width: 100%; 
  background-color: <%= betaColor %>;
  margin-top: 8px;
  margin-bottom: 8px;
  }
  
/* highlights */
.ss_highlight_alpha {
  font-weight: bold;
  background-color: <%= alphaColor %>;
  }
  
.ss_highlight_beta {
  font-weight: bold;
  background-color: <%= betaColor %>;
  }
  
.ss_highlight_gamma {
  font-weight: bold;
  color: <%= betaColor %>;
  }
  
.ss_titlebold {
  font-size: 16px;
  font-weight: bold;
  color: <%= alphaColor %>;  
  }

/* Box styles */
div.ss_box_rounded {
	background-color: #cecece;
}

div.ss_box_bottom_rounded {
	background-color: #cecece;
	height: 1px;
	margin: 0px;
}

div.ssf_box {
	background-color: #FFFFFF;
	height: auto;
<c:if test="<%= !isIE %>">
	height: 100%;
</c:if>
	margin: 2px 2px 0px 2px;
}

div.ss_box_minimum_height {
	height: 1px;
	margin: 1px;
}

ss_box_small_icon_bar {
	background-color: #cecece;
	height: 1em;
	padding-right: 10px;
	position:relative;
	top: 0px;
	margin: 0px;
}

ss_box_small_icon {
	height: 14px;
	margin: 0px;
	width: 14px;
}

ss_box_title {
	background: <%= gammaColor %> url(<html:imagesPath/>box/box_title_bg_gradient.gif) repeat-x;
	color: #4A517D;
	height: 20px;
	padding: 0px 3px 0px 3px;
}

.ssf-box-corner-2-bl {
	background: #FFFFFF url(<html:imagesPath/>box/shadow_left.gif) no-repeat;
	height: 6px;
	left: 0px;
	overflow: hidden;
	position: absolute;
	top: 0px;
	width: 6px;
}

.ssf-box-corner-2-br {
	background: #FFFFFF url(<html:imagesPath/>box/shadow_right.gif) no-repeat;
	height: 6px;
	overflow: hidden;
	position: absolute;
	right: 0px;
	top: 0px;
	width: 6px;
}

.ssf-box-bottom-decoration-2 {
	background: url(<html:imagesPath/>box/shadow_middle.gif) repeat-x;
	height: 6px;
	position: relative;
	top: -5px;
	width: 100%;
    margin-right:2;
    margin-left:2;
	z-index: 2;
}

</style>
<script language="JavaScript" src="<html:rootPath/>js/forum/forum_common.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/nifty_corners.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/ss_drag.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/ss_dragdrop.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/ss_coordinates.js"></script>
<c:if test="0">
<script language="JavaScript" src="<html:rootPath/>js/forum/forum_dragsort.js"></script>
</c:if>
</c:if>

