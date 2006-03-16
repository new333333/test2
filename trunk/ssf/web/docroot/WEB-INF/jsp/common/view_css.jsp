
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.ef.ObjectKeys" %>
<%@ page import="com.sitescape.ef.web.WebKeys" %>
<%@ page import="com.sitescape.ef.context.request.RequestContextHolder" %>
<%@ page import="com.sitescape.ef.domain.User" %>
<%

// Color values used in ss styles, highlighting, borders, and headers
%>
<c:set var="ss_portlet_style_background_color" value="#FFFFAA" scope="request"/>
<c:set var="ss_portlet_style_text_color" value="#000099" scope="request"/>
<c:set var="ss_portlet_style_inherit_font_specification" value="false" scope="request"/>

<c:set var="ss_style_background_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_style_text_color" value="#009900" scope="request"/>
<c:set var="ss_style_link_color" value="#009900" scope="request"/>
<c:set var="ss_style_link_hover_color" value="#3333FF" scope="request"/>
<c:set var="ss_style_gray_color" value="#999999" scope="request"/>

<c:set var="ss_folder_border_color" value="#CC6666" scope="request"/>
<c:set var="ss_folder_line_highlight_color" value="#CECECE" scope="request"/>
<c:set var="ss_entry_border_color" value="#CC0000" scope="request"/>

<c:set var="ss_form_background_color" value="#CCFFFF" scope="request"/>
<c:set var="ss_form_text_color" value="#3333FF" scope="request"/>
<c:set var="ss_form_gray_color" value="#CC99CC" scope="request"/>
<c:set var="ss_form_element_color" value="#FFCCFF" scope="request"/>
<c:set var="ss_form_element_header_color" value="#66CCCC" scope="request"/>
<c:set var="ss_form_element_border_color" value="#669966" scope="request"/>
<c:set var="ss_form_element_text_color" value="#0033FF" scope="request"/>

<c:set var="ss_toolbar_background_color" value="#CECECE" scope="request"/>
<c:set var="ss_toolbar_text_color" value="#000000" scope="request"/>
<c:set var="ss_toolbar_link_hover_color" value="${ss_style_link_hover_color}" scope="request"/>
<c:set var="ss_toolbar_border_color" value="#3366CC" scope="request"/>

<c:set var="ss_title_line_color" value="#3333FF" scope="request"/>

<c:set var="ss_tree_highlight_line_color" value="#6666FF" scope="request"/>

<c:set var="ss_box_color" value="#CCCCCC" scope="request"/>
<c:set var="ss_box_canvas_color" value="#FFFFCC" scope="request"/>
<c:set var="ss_box_title_color" value="#009999" scope="request"/>
<c:set var="ss_box_title_text_color" value="#993333" scope="request"/>

<c:set var="ss_sliding_table_background_color" value="#FFFFAA" scope="request"/>
<c:set var="ss_sliding_table_border_color" value="#999999" scope="request"/>
<c:set var="ss_sliding_table_text_color" value="#3333FF" scope="request"/>
<c:set var="ss_sliding_table_link_hover_color" value="#3333FF" scope="request"/>

<c:set var="ss_calendar_today_background_color" value="#ffffe8" scope="request"/>
<c:set var="ss_calendar_notInView_background_color" value="#f7f7f7" scope="request"/>

<%

//Set up the user object
if(RequestContextHolder.getRequestContext() != null) {
	User user = RequestContextHolder.getRequestContext().getUser();
	request.setAttribute("ssUser", user);
}

boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:if test="${empty ssf_support_files_loaded}">
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

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
	Rounded("*.ss_rounded_border", "all", "${ss_style_background_color}", "transparent", "border smooth");
	Rounded("*.ss_rounded_border_form", "all", "${ss_form_background_color}", "transparent", "border smooth");
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
.ss_portlet_style, .ss_portlet_style * {
<c:if test="${!empty ss_portlet_style_background_color}">
  background-color: ${ss_portlet_style_background_color};
</c:if>
<c:if test="${!empty ss_portlet_style_text_color}">
  color: ${ss_portlet_style_text_color};
</c:if>
<c:if test="${ss_portlet_style_inherit_font_specification}">
  font-family: arial, helvetica, sans-serif;
  font-weight: inherit;
  font-size: 12px; 
</c:if>
}

.ss_style, .ss_style table {
  font-family: arial, helvetica, sans-serif;
  background-color: ${ss_style_background_color};
  color: ${ss_style_text_color};
  font-weight: inherit;
  font-size: 12px; 
  }
.ss_style a, .ss_style a:visited {
  color: ${ss_style_link_color};
}
.ss_style a:hover {
  color: ${ss_style_link_hover_color};
}

.ss_gray {
  color: ${ss_style_gray_color};   
  }

.ss_form, .ss_form table, .ss_style form {
  color: ${ss_form_text_color};
  background-color: ${ss_form_background_color};
  }
    
.ss_form.ss_gray {
  color: ${ss_form_gray_color};
  }

.ss_form select, .ss_form option {
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
    
.ss_form input.ss_submit {}

.ss_form input.ss_submit:hover {}
  
/* Sliding tables */
div.ss_sliding_table_column0 {
  display: block; 
  border: ${ss_sliding_table_border_color} 1px solid;
  margin: 0px;
}
.ss_sliding_table_column0 * {
  background-color: ${ss_sliding_table_background_color}; 
  color: ${ss_sliding_table_text_color};
}
div.ss_sliding_table_column1 {
  position: absolute; 
  visibility: hidden;
  display: block; 
  border-left: #ffffff solid 1px;
  margin: 0px;
}
.ss_sliding_table_column1 * {
  background-color: ${ss_sliding_table_background_color}; 
  color: ${ss_sliding_table_text_color};
}
div.ss_sliding_table_column {
  position: absolute; 
  visibility: hidden;
  display: block; 
  border-left: ${ss_sliding_table_border_color} solid 1px;
  margin: 0px;
}
.ss_sliding_table_column * {
  background-color: ${ss_sliding_table_background_color}; 
  color: ${ss_sliding_table_text_color};
}
div.ss_sliding_table_info_popup {
  position: absolute; 
  visibility: hidden;
  display:block; 
  border-left: ${ss_sliding_table_border_color} solid 1px;
  margin: 0px;
  z-index: 40;
}
.ss_sliding_table_info_popup * {
  background-color: ${ss_sliding_table_background_color}; 
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column a, .ss_sliding_table_column a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column a:hover {
  color: ${ss_sliding_table_link_hover_color};
}
.ss_sliding_table_column0 a, .ss_sliding_table_column0 a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column0 a:hover {
  color: ${ss_sliding_table_link_hover_color};
}
.ss_sliding_table_column1 a, .ss_sliding_table_column1 a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_column1 a:hover {
  color: ${ss_sliding_table_link_hover_color};
}
.ss_sliding_table_info_popup a, .ss_sliding_table_info_popup a:visited {
  color: ${ss_sliding_table_text_color};
}
.ss_sliding_table_info_popup a:hover {
  color: ${ss_sliding_table_link_hover_color} !important;
}


/* Folder */
.ss_folder_border, .ss_folder_border table {
  background-color: ${ss_folder_border_color} !important;
  }

/* Entry */
.ss_entry_border, .ss_entry_border table {
  background-color: ${ss_entry_border_color} !important;
  }

/* Forum toolbar */
.ss_toolbar {
  width: 100%; 
  border-top: 1px solid ${ss_toolbar_border_color};
  border-bottom: 1px solid ${ss_toolbar_border_color};
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  margin-top: 0px;
  margin-bottom: 8px;
  padding-top:2px;
  padding-bottom:2px;
  }
.ss_toolbar * {
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar_color {
  background-color: ${ss_toolbar_background_color};
  color: ${ss_toolbar_text_color};
  }
  
.ss_toolbar_menu {
  position: absolute;
  z-index: 100;
  visibility: hidden;
  background-color: ${ss_toolbar_background_color}; 
  color: ${ss_toolbar_text_color};
  border: 1px ${ss_toolbar_border_color} solid;
  padding: 0px;
  width: 300px;
  }
.ss_toolbar_menu * {
  background-color: ${ss_toolbar_background_color}; 
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar_item * {
  background-color: ${ss_toolbar_background_color}; 
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar_item a {
  background-color: ${ss_toolbar_background_color}; 
  color: ${ss_toolbar_text_color};
  }
.ss_toolbar a, .ss_toolbar a:visited {
  color: ${ss_toolbar_text_color};
}
.ss_toolbar a:hover {
  color: ${ss_toolbar_link_hover_color};
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
.ss_box_rounded {
	background-color: ${ss_box_color};
}

.ss_box_bottom_rounded {
	background-color: ${ss_box_color};
	height: 1px;
	margin: 0px;
}

.ssf_box {
	background-color: ${ss_style_background_color};
	height: auto;
<c:if test="<%= !isIE %>">
	height: 100%;
</c:if>
	margin: 2px 2px 0px 2px;
}

.ss_box_minimum_height {
	height: 1px;
	margin: 0px;
}

.ss_box_small_icon_bar {
	background-color: ${ss_box_color};
	height: 1em;
	position:relative;
	top: 0px;
	margin: 0px;
}

.ss_box_small_icon {
	height: 14px;
	margin: 0px;
	width: 14px;
}

.ss_box_title {
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

