<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>

<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<portletadapter:defineObjects1/>

<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>
</c:if>
<portletadapter:defineObjects2/>
</ssf:ifadapter>

<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>
<%
//Set some default colors
String alphaColor = "#775325";
String betaColor = "#B89257";
String gammaColor = "#CCCC99";
//Get the Liferay colors
//String alphaColor = GetterUtil.get(request.getParameter("body_background"), skin.getAlpha().getBackground());
//String betaColor = GetterUtil.get(request.getParameter("body_background"), skin.getBeta().getBackground());
//String gammaColor = GetterUtil.get(request.getParameter("body_background"), skin.getGamma().getBackground());

boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${empty ssf_support_files_loaded}">
<link rel="stylesheet" type="text/css" href="<html:rootPath/>css/forum.css">
<c:if test="<%= isIE %>">
<link rel="stylesheet" type="text/css" href="<html:rootPath/>css/forum_ie.css">
</c:if>
<c:if test="<%= !isIE %>">
<link rel="stylesheet" type="text/css" href="<html:rootPath/>css/forum_nn.css">
</c:if>
<style>
/* Forum toolbar */
div.ss_toolbar {
  width: 100%; 
  background-color: <%= gammaColor %>;
  margin-top: 8px;
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
  background-color: <%= alphaColor %>;
  }
  
.ss_highlight_beta {
  background-color: <%= betaColor %>;
  }
  
.ss_highlight_gamma {
  background-color: <%= gammaColor %>;
  }
  
.ss_titlebold {
  font-family: arial, helvetica, sans-serif;
  font-size: 13px;
  font-weight: bold;
  color: <%= alphaColor %>;  
  }

/* Box styles */
.ssf-box-container {
	margin-top: -5px;
}

.ssf-box {
	background-color: #FFFFFF;
	border: 1px solid <%= gammaColor %>;
	height: auto;
<c:if test="<%= !isIE %>">
	height: 100%;
</c:if>
	padding: 15px 0 0 0;
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
	font-family: Tahoma, Arial;
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
</c:if>

<ssf:ifadapter>
<c:if test="${empty ssf_support_files_loaded}">
</head>
</c:if>
</ssf:ifadapter>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

