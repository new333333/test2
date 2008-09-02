<%
/* *
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

/* CSS Document - container for toolbars and menu styles */

/* ACTIONS */

.ss_actions_bar {
	list-style-type:none;	
	padding: 0px 0px 0px 0px;
	height: 22px;
	margin: 0px;
	}
.ss_actions_bar1 {
	font-weight: normal;
	font-size: ${ss_style_font_smallprint};
	font-family: ${ss_style_title_font_family};
	}
div.ss_actions_bar1_pane {
	border-bottom: 1px solid;
	border-color: #FFF;
	/* height: 22px; */
	white-space: nowrap;
	}		
table.ss_actions_bar2_pane, table.ss_actions_bar2_pane td {
	/* background-color: ${ss_toolbar2_background_color}; */
	background-color: #666666;
	width: 100%;
	height: 2px;
	padding: 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
	}
.ss_actions_bar2, table.ss_actions_bar2_pane {
	/* background-image: url(<html:imagesPath/>pics/background_toolbar2.gif); 
	background-repeat: repeat-x; */
	background-color: #666666;
	}	
.ss_actions_bar2 {
	font-weight: normal;
	font-size: ${ss_style_font_smallprint};
	font-family: ${ss_style_title_font_family};
	}
.ss_actions_bar3 {
	/* background-color: #AFC8E3; */
		background-color: #FFFFFF;
	font-weight: normal;
	font-size: ${ss_style_font_smallprint};
	font-family: ${ss_style_title_font_family};
	}
	/*ENTRY*/	
table.ss_actions_bar3_pane, table.ss_actions_bar3_pane td {
 	background-color: #AFC8E3; 
	width: 100%;
	height: 20px;
	padding: 4px 2px 0px 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
	}
table.ss_actions_bar4_pane {
    background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
	background-repeat: repeat-x;
	width: 100%;
	height: 20px;
	padding: 4px 0px 0px 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
	}
.ss_actions_bar4 {
	font-weight: normal;
	font-size: ${ss_style_font_smallprint};
	font-family: ${ss_style_title_font_family};
	}
.ss_actions_bar5 {
	background-color:#72AEB6;
	font-family: Arial,Helvetica,sans-serif;
	font-size: 12px;
	font-weight: bold;
	margin:15px 0 0 0;
	padding:5px 0 5px 25px;
	}
ul.ss_actions_bar1  {
	/* height: 22px;	*/
	}
ul.ss_actions_bar1 .ss_actions_bar_submenu {
	height: auto;
	padding: 0px !important;
	}
.ss_action_bar ul, .ss_actions_bar_submenu {
	list-style-type: none;
	}
ul.ss_actions_bar li {
	float:left;
	display: inline;
	margin: 0px;
	white-space: nowrap;
	}
.ss_actions_bar li.ss_toolBarItem {
	border: 0px;
	margin: 0px;
	padding: 0px;
	background-color: inherit;
	}
.ss_actions_bar li.ss_toolBarItem ul {
	float: left;
	display: inline;
	margin: 0px;
	padding: 0px;
	}
.ss_actions_bar li.ss_toolBarItem .ss_toolBarItemTxt {
	float: left;
	margin-top: 0px;
	}
.ss_actions_bar li.ss_actions_bar_separator {
	border-right: 0px;
	border-top: 0px;
	height: 18px;
	margin: 0px 5px 0px 5px;
	width: 0px;
	padding: 0px;
	}
.ss_actions_bar form {
	display: inline;
	background-color: transparent;
	}
.ss_actions_bar form label, ul.ss_actions_bar form input {
	background-color: transparent;
	margin-top: 5px;
	}
.ss_actions_bar li.ss_actions_bar_last-child {
	border-right:none;
	}
.ss_actions_bar li a, .ss_actions_bar li a:visited {
	color: #ffffff !important;
	display:block;
	padding:4px 7px;
	}
.ss_actions_bar li a:hover{
	color: #333333 !important;
	background-image: none !important;
	}
.ss_actions_bar li a.ss_actions_bar_inline, .ss_actions_bar li a.ss_actions_bar_inline:visited  {
	color:${ss_style_text_color} !important;
	display: inline;
	padding:0px 7px;
	}
.ss_actions_bar_background {
	margin:0px;
	padding:0px;
	}
.ss_actions_bar_history_bar {
	margin-top: 5px;
	font-size: 11px;
	font-family: ${ss_style_title_font_family};
	}
.ss_actions_bar_history_bar a {
	padding:0px !important;
	}
.ss_actions_bar_history_bar * {
	color:#0000FF
	}
ul.ss_actions_bar1 li {
	margin-top: 3px;
	padding: 0px;
	}
ul.ss_actions_bar1 li a:hover {
	text-decoration:none;
	background-image: none !important;
	}
.ss_actions_bar2 li a:hover {
	/* background-image: url(<html:imagesPath/>pics/background_actionbar4.gif); */
	text-decoration:none;
	}
.ss_actions_bar3 li a:hover {
	/* background-color: ${ss_style_background_color_side_panel}; */
		background-color: #FFFFFF;
	text-decoration:none;
	}
div.ss_actions_bar_submenu {
/* background-color: ${ss_style_background_color_side_panel}; */
	background-color: transparent;
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display:none;
	z-index:500;
	white-space: nowrap;
	}
div.ss_actions_bar_submenu ul.ss_actions_bar1 {
  /* background-color: ${ss_toolbar1_dropdown_menu_color}; */
  	background-color: #FFFFFF;
  	background-image: none !important;
  	opacity: 0.95;
  	border:0.1em solid #CCCCCC;
  	padding: 0px !important;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if>
	}
div.ss_actions_bar_submenu ul.ss_actions_bar2 {
 /* background-color: #DDE5EF; */
  	background-color: #FFFFFF;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if>
	}
div.ss_actions_bar_submenu ul.ss_actions_bar3 {
 /* background-color: ${ss_toolbar1_background_color}; */
  	background-color: #FFFFFF;
  	background-image: none;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if>
	}
div.ss_actions_bar_submenu ul.ss_actions_bar4 {
 /* background-color: ${ss_toolbar1_background_color}; */
  	background-color: #FFFFFF;
 	background-image: none;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=75);
 </c:if>
 margin: 0px;
	}
.ss_actions_bar_submenu {
 <c:if test="<%= isIE %>">
	top:-12px;
	left:-26px;
 </c:if>
 <c:if test="<%= !isIE %>">
	top:-8px;
	left:-10px;
 </c:if>	
	border:1px solid ${ss_toolbar1_background_color};
	border-top:none;
	padding: 1px 1px;
	}
.ss_actions_bar_submenu li  {
	float:none;
	border-right-style:none;
    line-height:18px;
	}
.ss_actions_bar_submenu ul.ss_actions_bar3 li  {
	float:none;
	border-bottom:1px solid #AFC8E3;
	border-right-style:none;
    line-height:18px;
	}
.ss_actions_bar_submenu li a {
  	display: block;
 	padding-left: 10px;
  	padding-right: 10px;
 	text-decoration: none;
	}
.ss_actions_bar_submenu div {
	background: none;
	}
.ss_actions_bar_filters {
 <c:if test="<%= isIE %>">
	top:-6px;
	left:-45px;
 </c:if>
 <c:if test="<%= !isIE %>">
	top: 5px;
	left: -32px;
 </c:if>
	}
.ss_actions_bar1 li:hover, .ss_actions_bar1 a:hover {
	background-color: ${ss_style_header_bar_background};
	}
.ss_actions_bar2 li:hover, .ss_actions_bar1 a:hover {
	background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
	}
.ss_actions_bar3 li:hover, .ss_actions_bar1 a:hover {
	background-image: url(<html:imagesPath/>pics/background_actionbar4.gif);
	}
.ss_actions_bar_submenu li:hover, .ss_actions_bar_submenu a:hover {
	text-decoration:underline;
	color:${ss_style_text_color};
	}
.ss_actions_bar_submenu a, .ss_actions_bar_submenu a:visited {
	color:${ss_style_text_color};
	}
/* utils bar (dashboard)  */
div.ss_dashboardContainer {
	border-bottom: 1px solid ${ss_generic_border_color};
	width: 100%;
	margin: 0px;
	padding: 0px 0px 0px 0px;
	}
div.ss_utils_bar {
    float:right;		
    text-align:right;	/* this was here */
	margin-bottom:0px;
	background:transparent none repeat 0%;
	background-attachment:scroll;
	width:100%;
	}
div.ss_line {
	border-bottom: 1px solid #666666;
	width: 100%;
	margin: 0px;
	padding: 0px 0px 0px 0px;
	}
ul.ss_utils_bar {
 	float: right;		
	list-style-type:none;
	margin:0px;
	padding:0px;
	text-align:right;	
	/* this was here */
	}
div.ss_utils_bar ul.ss_utils_bar li {
	float:left;			
/*	text-align: left;	 this is new */	
	margin: 0px 5px 0px 0px;
	}
div.ss_utils_bar ul.ss_utils_bar li a, div.ss_utils_bar ul.ss_utils_bar li a:visited {
	color:#003782;
	display:block;
	margin:0px;
	border:0px;	
	}
div.ss_utils_bar ul.ss_utils_bar li a span {
	padding: 0px;
	margin: 0px;
	font-size:${ss_style_font_smallprint};
	}
.ss_utils_bar li a:hover {
	text-decoration:underline;
	}
div.ss_utils_bar_submenu {
	/* background-color: #EFEFEF; */
		background-color: #FFFFFF;
	margin:0px;
	padding:0px;
	text-align:left;
	position:absolute;
	display: none;
	visibility: hidden;
	z-index:500;
	border: 1px solid #CCCCCC;
	white-space: nowrap;
	}
ul.ss_utils_bar_submenu {
	margin: 0px 5px 0px 0px;
/* background-color: #ECECEC; */
	background-color: #FFFFFF;
	font-family: ${ss_style_title_font_family};
	font-size: ${ss_style_font_fineprint};
	list-style: none;
	padding: 3px;
	} 
ul.ss_utils_bar_submenu li  {
	float:none;
	padding:0px;
	font-weight:normal;
	border-right-style:none;
    line-height:20px;
	}
ul.ss_utils_bar_submenu li a:hover {
	text-decoration:underline;
	background-color:transparent;
	color:#666666;
	font-size:${ss_style_font_smallprint};	
	}
ul.ss_utils_bar_submenu a, .ss_utils_bar_submenu a:visited {
	text-decoration:none;
	color:#666666;
	font-size:${ss_style_font_smallprint};
	}

/* TAG MENU PANES AND DROP DOWNS */

div.ss_folder_tags {
	width: 100%;
	margin-top: 0px;
	}
.ss_tags {
  	color:${ss_tag_color};
  	font-weight:bold;
	}
.ss_tag_pane {
  	display:none; 
  	visibility:hidden; 
  	margin:0px;
  	padding:2px;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if> 
	}
.ss_tag_pane_color {
 /* background-color: ${ss_tag_pane_background_color} !important; */
  	background-color: transparent;
  	color: ${ss_style_text_color} !important;
	}
table.ss_tag_pane_color th {
	border: none;
	}
.ss_tag_pane_ok_cover {
  	position:relative; 
  	top:-14px;
<c:if test="<%= !isIE %>">
  	left: 35%; 
</c:if>
  	height:20px; 
  	width:50px; 
 /* background-color:${ss_tag_pane_background_color}; */
  	background-color: #FFFFFF;
	}
.ss_link_menu {
  	position:absolute;
  	visibility:hidden; 
  	text-align:left; 
 /* background-color: ${ss_style_background_color}; */
  	background-color: #FFFFFF;
  	color: ${ss_style_text_color};
  	border: 1px ${ss_style_border_color} solid;
  	padding: 0px 4px 0px 10px;
	}
ul.ss_dropdownmenu {
  	list-style: outside;
  	text-align:left;
  	margin:2px 2px 2px 13px;
  	padding: 2px;
	}
ul.ss_dropdownmenu li {
  	list-style-type: square;
  	margin-left: 0px;
  	margin-bottom: 2px;
  	padding-left: 2px;
	} 
/* POPUP PANES */

.ss_popupTitleOptions {
  	position:absolute;
  	border:1px solid black;
  	margin:4px;
  	padding:2px;
 /* background-color:${ss_style_background_color}; */
  	background-color: #FFFFFF;
  	}
table.ss_popup {
	position: relative;
	background-color: transparent;
	border: 1px solid ${ss_toolbar1_background_color};
	}
div.ss_popup_top {
  	position: relative;
  	background-image: url(<html:imagesPath/>pics/background_base_title_bar.jpg);
  	background-repeat: repeat-x;
  	height: 14px;
  padding: 0px;
	}
div.ss_popup_title {
   font-family: ${ss_style_title_font_family};
   font-size: 11px;
   font-weight: bold;
   color: ${ss_style_header_bar_title_color};
   position: relative;
   text-align: center;
	}
div.ss_popup_close {
  	position: relative;
  	background-image: url(<html:imagesPath/>pics/popup_close_box.gif);
 	background-repeat: no-repeat;
  	width: 12px;
  	height: 13px;
  	top: 1px;
  	left: -1px;
	}
div.ss_popup_body {
  	position: relative;
 /* background-color: ${ss_toolbar4_background_color}; */
  	background-color: #FFFFFF;
  	padding: 10px;
	}
