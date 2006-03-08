
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>
<%@ page import="com.sitescape.ef.context.request.RequestContextHolder" %>
<%@ page import="com.sitescape.ef.domain.User" %>
<%

//Set some color values used in styles, highlighting, borders, and headers
%>
<c:set var="ss_style_background_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_style_text_color" value="#009900" scope="request"/>
<c:set var="ss_style_gray_color" value="#999999" scope="request"/>

<c:set var="ss_folder_border_color" value="#CC6666" scope="request"/>
<c:set var="ss_folder_line_highlight_color" value="#dddddd" scope="request"/>
<c:set var="ss_entry_border_color" value="#CC0000" scope="request"/>

<c:set var="ss_form_background_color" value="#CCFFFF" scope="request"/>
<c:set var="ss_form_text_color" value="3333FF" scope="request"/>
<c:set var="ss_form_gray_color" value="CC99CC" scope="request"/>
<c:set var="ss_form_element_color" value="#FFCCFF" scope="request"/>
<c:set var="ss_form_element_header_color" value="#66CCCC" scope="request"/>
<c:set var="ss_form_element_border_color" value="#669966" scope="request"/>
<c:set var="ss_form_element_text_color" value="#0033FF" scope="request"/>

<c:set var="ss_toolbar_color" value="#f7f7f7" scope="request"/>
<c:set var="ss_toolbar_border_color" value="#3366cc" scope="request"/>

<c:set var="ss_title_line_color" value="#3333FF" scope="request"/>

<c:set var="ss_tree_highlight_line_color" value="#6666FF" scope="request"/>

<c:set var="ss_box_color" value="#CCCCCC" scope="request"/>
<c:set var="ss_box_canvas_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_box_title_color" value="#009999" scope="request"/>
<c:set var="ss_box_title_text_color" value="#993333" scope="request"/>
<%

//Set up the user object
if(RequestContextHolder.getRequestContext() != null) {
	User user = RequestContextHolder.getRequestContext().getUser();
	request.setAttribute("ssUser", user);
}

boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${empty ssf_support_files_loaded}">

<script type="text/javascript" src="<html:rootPath/>js/common/ss_common.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/nifty_corners.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_drag.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_dragdrop.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_coordinates.js"></script>
  <c:if test="0">
<script type="text/javascript" src="<html:rootPath/>js/common/ss_dragsort.js"></script>
  </c:if>
<script type="text/javascript">

//Routine to round the corners of the rounded box tag
function ss_rounded() {
	if(!NiftyCheck()) return;
	Rounded("*.ss_rounded", "all", "${ss_style_background_color}", "${ss_form_background_color}", "smooth");
}
ss_createOnLoadObj('ss_rounded', ss_rounded);

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

var ss_forumCssUrl = ss_urlBase + "<html:rootPath/>css/forum.css";
var niftyCornersCssUrl = ss_urlBase + "<html:rootPath/>css/nifty_corners.css";
var htmlareaCssUrl = ss_urlBase + "<html:rootPath/>js/htmlarea/htmlarea.css";
if (document.createStyleSheet) {
	document.createStyleSheet(ss_forumCssUrl);
	document.createStyleSheet(ss_forumCss2Url);
	document.createStyleSheet(niftyCornersCssUrl);
	document.createStyleSheet(htmlareaCssUrl);
} else {
	ss_createStyleSheet(ss_forumCssUrl);
	ss_createStyleSheet(ss_forumCss2Url);
	ss_createStyleSheet(niftyCornersCssUrl);
	ss_createStyleSheet(htmlareaCssUrl);
}

</script>

<style>
.ss_style, .ss_style table {
  font-family: arial, helvetica, sans-serif;
  background-color: ${ss_style_background_color};
  color: ${ss_style_text_color};
  font-weight: inherit;
  font-size: 12px; 
  }

.ss_gray {
  color: ${ss_style_gray_color};   
  }

.ss_form, .ss_form table, .ss_style form {
  color: ${ss_form_text_color};
  background-color: ${ss_form_background_color};
  margin:6px;
  }
    
.ss_form.ss_gray {
  color: ${ss_form_gray_color};
  }

.ss_form select {
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }
  
.ss_form textarea {
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }

.ss_form input.ss_text { 
  background-color: ${ss_form_element_color};
  color: ${ss_form_element_text_color};
  }
    
.ss_form input.ss_submit { 
  /*
  background-color: #e5e5e5;
  font-size: x-small; 
  color: #3366cc;
  font-weight: bold;
  padding-left, padding-right: 0px;
  border-top: 2px solid #e6e6e6;
  border-left: 2px solid #e6e6e6;
  border-right: 2px solid #8f8f8f;
  border-bottom: 2px solid #8f8f8f;
  */
  }

.ss_form input.ss_submit:hover { 
  /*
  background-color: #e5e5e5;
  font-size: x-small; 
  color: #3366cc;
  font-weight: bold;
  padding-left, padding-right: 0px;
  border-top: 2px solid #8f8f8f;
  border-left: 2px solid #8f8f8f;
  border-right: 2px solid #e6e6e6;
  border-bottom: 2px solid #e6e6e6;
  */
  }

/* Folder */
.ss_folder_border, .ss_folder_border table {
  background-color: ${ss_folder_border_color} !important;
  }

/* Forum toolbar */
.ss_toolbar {
  width: 100%; 
  border-top: 1px solid ${ss_toolbar_border_color};
  border-bottom: 1px solid ${ss_toolbar_border_color};
  background-color: ${ss_toolbar_color};
  margin-top: 0px;
  margin-bottom: 8px;
  }
  
.ss_toolbar_menu {
  position: absolute;
  z-index: 100;
  visibility: hidden;
  background-color: ${ss_toolbar_color}; 
  color: ${ss_style_text_color};
  border: 1px #cfcfcf solid;
  padding: 0px;
  width: 300px;
  }

  
/* highlights */
.ss_highlightEntry {
  background-color: ${ss_folder_line_highlight_color};
  }

.ss_tree_highlight {
  font-weight: bold;
  color: ${ss_tree_highlight_line_color};
  }
  
.ss_titlebold {
  font-size: 16px;
  font-weight: bold;
  color: ${ss_title_line_color};  
  }

/* Box styles */
div.ss_box_rounded {
	background-color: ${ss_box_color};
}

div.ss_box_bottom_rounded {
	background-color: ${ss_box_color};
	height: 1px;
	margin: 0px;
}

div.ssf_box {
	background-color: ${ss_style_background_color};
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
	background-color: ${ss_box_color};
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
	background: ${ss_box_title_color} url(<html:imagesPath/>box/box_title_bg_gradient.gif) repeat-x;
	color: ${ss_box_title_text_color};
	height: 20px;
	margin:0px;
	padding: 0px 3px 0px 3px;
}

/* htmlarea overrides */
.htmlarea { 
    background: ${ss_form_element_color}; 
}

</style>
</c:if>

