
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
var ss_urlBase = self.location.protocol + "//" + self.location.host + "/";
var ss_forumCssUrl = ss_urlBase + "<html:rootPath/>css/forum.css";
<c:if test="<%= isIE %>">
var ss_forumCss2Url = ss_urlBase + "<html:rootPath/>css/forum_ie.css";
</c:if>
<c:if test="<%= !isIE %>">
var ss_forumCss2Url = ss_urlBase + "<html:rootPath/>css/forum_nn.css";
</c:if>
if (document.createStyleSheet) {
	document.createStyleSheet(ss_forumCssUrl);
	document.createStyleSheet(ss_forumCss2Url);
} else {
	var styles = "@import url('" + " " + ss_forumCssUrl + " " + "');";
	var newSS = document.createElement('link');
	newSS.rel = 'stylesheet';
	newSS.href = 'data:text/css,' + escape(styles);
	document.getElementsByTagName("head")[0].appendChild(newSS);
	
	var styles2 = "@import url('" + " " + ss_forumCss2Url + " " + "');";
	var newSS2 = document.createElement('link');
	newSS2.rel = 'stylesheet';
	newSS2.href = 'data:text/css,' + escape(styles2);	
	document.getElementsByTagName("head")[0].appendChild(newSS2);
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
  font-size: smaller;
  font-family: arial, helvetica, sans-serif;
  font-weight: bold;
  background-color: <%= alphaColor %>;
  }
  
.ss_highlight_beta {
  font-size: smaller;
  font-family: arial, helvetica, sans-serif;
  font-weight: bold;
  background-color: <%= betaColor %>;
  }
  
.ss_highlight_gamma {
  font-size: smaller;
  font-family: arial, helvetica, sans-serif;
  font-weight: bold;
  color: <%= betaColor %>;
  }
  
.ss_titlebold {
  font-family: arial, helvetica, sans-serif;
  font-size: medium;
  font-weight: bold;
  color: <%= alphaColor %>;  
  }

/* Box styles */
.ssf-box-container {
	margin-top: -5px;
}

.ssf-box {
	background-color: #FFFFFF;
	border: 1px solid #3366cc;
	height: auto;
<c:if test="<%= !isIE %>">
	height: 100%;
</c:if>
	padding: 10px 0 0 0;
}

.ssf-box-minimum-height {
	height: 1px;
}

.ssf-box-header-bar {
	background-color: none;
	position: relative;
	width:100%;
	top: 9px;
	z-index: 2;
}

.ssf-box-title {
	background: <%= gammaColor %> url(<html:imagesPath/>box/box_title_bg_gradient.gif) repeat-x;
	color: #4A517D;
	font-family: arial, helvetica, sans-serif;
	font-size: smaller;
	height: 27px;
	left: 10px;
	padding: 0px 3px 0px 3px;
	position: absolute;
}

.ssf-box-small-icon-bar {
	height: 1em;
	padding-right: 10px;
	position:relative;
    setObjectWidth(wObj1, ss_entryWindowWidth);
	text-align: right;
	top: 8px;
}

.ssf-box-small-icon {
	height: 14px;
	margin: -1px;
	width: 14px;
}

.ssf-box-corner-ul {
	background: #FFFFFF url(<html:imagesPath/>box/box_corner_ul.gif) no-repeat;
	height: 5px;
	left: 0px;
	overflow: hidden;
	position: absolute;
	top: 0px;
	width: 5px;
}

.ssf-box-corner-ur {
	background: #FFFFFF url(<html:imagesPath/>box/box_corner_ur.gif) no-repeat;
	height: 5px;
	overflow: hidden;
	position: absolute;
	right: 0px;
	top: 0px;
	width: 5px;
}

.ssf-box-corner-bl {
	background: #FFFFFF url(<html:imagesPath/>box/box_corner_bl.gif) no-repeat;
	height: 5px;
	left: 0px;
	overflow: hidden;
	position: absolute;
	top: 0px;
	width: 5px;
}

.ssf-box-corner-br {
	background: #FFFFFF url(<html:imagesPath/>box/box_corner_br.gif) no-repeat;
	height: 5px;
	overflow: hidden;
	position: absolute;
	right: 0px;
	top: 0px;
	width: 5px;
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

.ssf-box-top-decoration {
	height: 5px;
	position: relative;
	top: 5px;
	width: 100%;
	z-index: 1;
}

.ssf-box-bottom-decoration {
	height: 5px;
	position: relative;
	top: -5px;
	width: 100%;
	z-index: 2;
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
<script language="JavaScript" src="<html:rootPath/>js/forum/forum_dragsort.js"></script>
</c:if>

