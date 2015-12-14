<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved. 
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>

/* CSS Document - container for toolbars and menu styles */

/* General CSS */

.ss_relDiv {
	position: relative;
	} 
	
/* positioned inside of a relative DIV (can use .ss_relDiv) to keep with content */	
.ss_diagSmallDiv { 
	position: absolute;
	background-color: #f6f6f6;
	border: 1px solid #babdb6;
	min-width: 300px;
	}
.ss_diagDivTitle {
	color: #fff;
	font-size: 13px;
	font-weight: bold;
	letter-spacing: .1em;
	background: #5691A6 url(<html:imagesPath/>pics/H_bg_blend_teal.png) repeat-x;
	padding: 4px;
	}
.ss_diagDivContent {
	padding: 10px;
	background-color: #fff;
	}		
.ss_diagDivFooter {
	position: relative;
	background-color: #efeeec;
	text-align: right;
	padding: 0.5em;
	border-top: 1px solid #babdb6;
	}	

/* ACTIONS */

.ss_actions_bar {
	list-style-type:none;	
	height: 22px;	
	padding: 0px 0px 0px 0px;
	margin: 0px;
	}
.ss_action_bar ul, .ss_actions_bar_submenu {
	list-style-type: none;
	}
ul.ss_actions_bar li {
	float:left;
	display: inline;
	margin: 0px;
	white-space: nowrap;
	padding: 2px 0px 4px 0px;
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
li.ss_actions_bar_separator {
    list-style-type: none;
	margin: 0px 5px 0px 5px;
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
	color: #1f1f1f !important;
	margin-right: 2px;
	display:block;
	border: 1px solid #B8B8B8;
	font-weight: normal !important;
	font-size: 11px;
	padding: 2px 6px  !important;
	text-decoration:none;	
	background-color: #fafafa;
	border-radius: 5px;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
/*	background: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#E0E0E0));
	background: -moz-linear-gradient(center top , #ffffff, #E0E0E0) repeat scroll 0 0;       */
	}
.ss_actions_bar li span {
	padding: 2px 5px;
	}
.ss_actions_bar li a:hover{
	color: #036f9f !important;
	border: 1px solid #81b2bd;
	border-radius: 5px;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
/*	background: linear-gradient(bottom, #A8D4DD 47%, #D5EDF1 53%, #C8E4E9 100%);
	background: -o-linear-gradient(bottom, #A8D4DD 47%, #D5EDF1 53%, #C8E4E9 100%);
	background: -moz-linear-gradient(bottom, #A8D4DD 47%, #D5EDF1 53%, #C8E4E9 100%);
	background: -webkit-linear-gradient(bottom, #A8D4DD 47%, #D5EDF1 53%, #C8E4E9 100%);
	background: -ms-linear-gradient(bottom, #A8D4DD 47%, #D5EDF1 53%, #C8E4E9 100%);
	background: -webkit-gradient(
		linear,
		left bottom,
		left top,
		color-stop(0.47, #A8D4DD),
		color-stop(0.53, #D5EDF1),
		color-stop(1, #C8E4E9));   */
	}
.ss_actions_bar li a.ss_actions_bar_inline, .ss_actions_bar li a.ss_actions_bar_inline:visited  {
	color:${ss_style_text_color} ;
	display: inline;
	padding:0px 7px;
	}
.ss_actions_bar1 {
	font-size: ${ss_style_font_smallprint};
	font-family: ${ss_style_title_font_family};
	}
div.ss_actions_bar1_pane {
	white-space: nowrap;
	}		
table.ss_actions_bar2_pane, table.ss_actions_bar2_pane td {
	width: 100%;
	padding: 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
	}
.ss_actions_bar2, table.ss_actions_bar2_pane {
	/* background-image: url(<html:imagesPath/>pics/background_toolbar2.gif); 
	background-repeat: repeat-x; */
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
 	/* background-color: #AFC8E3; */
	width: 100%;
	/* height: 20px; */
	padding: 4px 2px 0px 0px;
	margin: 0px;
	border-collapse: collapse;
	border-spacing: 0px;
	} 
table.ss_actions_bar4_pane {
    /* background-image: url(<html:imagesPath/>pics/background_actionbar4.gif); */
	/* background-repeat: repeat-x; */
	width: 100%;
	/* height: 20px; */
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
	font-family: Arial,Helvetica,sans-serif;
	font-size: 12px;
	margin:5px 0 0 0;
	padding:5px 0 5px 5px;
	}
.ss_actions_bar5 li a {color: #fff}
	
.ss_actions_bar6 {
	font-family: Arial,Helvetica,sans-serif;
	font-size: 12px;
	margin:0;
	padding: 0 4px;
    float:right;		
	}
ul.ss_actions_bar1  {
	/* height: 22px;	*/
	}
ul.ss_actions_bar1 .ss_actions_bar_submenu, ul.ss_actions_bar_submenu li {
	height: auto;
	padding: 0px !important;
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
	margin-top: 1px;
	padding: 5px 0px 0px 0px;
	}
ul.ss_actions_bar1 li a:hover {
	text-decoration:none;
	background-image: none !important;
	}
.ss_actions_bar2 li a:hover {
	text-decoration:none;
	}
.ss_actions_bar3 li a:hover {
	text-decoration:none;
	}
ul.ss_actions_bar4 li {
	list-style-type:none;
	}
div.ss_actions_bar_submenu {
	margin: 0;
	padding: 5px;
	text-align:left;
	position:absolute;
	display:none;
	z-index:500;
	white-space: nowrap;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
	}
div.ss_actions_bar_submenu ul.ss_actions_bar1 {
  	background-color: #505354;
  	background-image: none !important;
  	opacity: 0.95;
  	padding: 0px !important;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if>
	}
div.ss_actions_bar_submenu ul.ss_actions_bar2 {
  	background-color: #505354;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if>
	}
div.ss_actions_bar_submenu ul.ss_actions_bar3 {
  	background-color: #505354;
  	background-image: none;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=95);
 </c:if>
	}
div.ss_actions_bar_submenu ul.ss_actions_bar4 {
  	background-color: #505354;
 	background-image: none;
  	opacity: 0.95;
 <c:if test="<%= isIE %>">
  	filter: alpha(opacity=75);
 </c:if>
 	margin: 0px;
 	border: 1px solid ${ss_toolbar1_background_color};
	}
.ss_actions_bar_submenu {
 <c:if test="<%= isIE %>">
	margin-top:-12px;
	margin-left:-26px;
 </c:if>
	margin-top:0px;
	margin-left:0px;	
	background-color: #505354;
	border:none;	
	padding: 1px 1px;
	}
.ss_actions_bar_submenu li  {
	float:none;
	border-right-style:none;
    line-height: 22px;
	}
.ss_actions_bar_submenu ul.ss_actions_bar3 li  {
	float:none;
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
.ss_actions_bar_submenu li:hover, .ss_actions_bar_submenu a:hover, .ss_inline_menu a:hover {
	color: #81B2BD !important;
	background-color: #353838;
	-moz-border-radius: 3px;
	border-radius: 3px;
	-webkit-border-radius: 3px;
	}
.ss_inline_menu {
	font-weight: normal;
	padding: 3px 5px;
	}
div.ss_inline_menu a {
	color: #fff !important;
	padding: 3px 5px;
	}
.ss_inline_menu a:hover {
	background-image: none;
	}
.ss_actions_bar_submenu a, .ss_actions_bar_submenu a:visited {
/*	color:${ss_box_title_color}; */
	color: #fff;
	}
/* utils bar (dashboard)  */
div.ss_dashboardContainer {
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
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}
div.ss_utils_bar ul.ss_utils_bar li a, div.ss_utils_bar ul.ss_utils_bar li a:visited {
	color:#036f9f;
	display:block;
	margin:0px;
	border:0px;	
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}
div.ss_utils_bar ul.ss_utils_bar li a span {
	padding: 0px;
	margin: 0px;
	font-size:${ss_style_font_smallprint};
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
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
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
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
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}
ul.ss_utils_bar_submenu li a:hover {
	text-decoration:underline;
	background-color:transparent;
	color:#666666;
	font-size:${ss_style_font_smallprint};	
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
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
  	background-color: transparent !important;
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
  	/* height:20px; */
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
	
.ss_dropdownmenu_spacer {
	text-align: center !important;
	font-size: 8px;
	color: #CCC !important;
	padding-left: 10px;
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
table.ss_popup, div.ss_popup {
	position: relative;
	background: url(<html:imagesPath/>pics/trans30_black.png) repeat;
	padding: 6px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
	}
div.ss_popup {
	position: relative;
	background: url(<html:imagesPath/>pics/trans30_black.png) repeat;
	padding: 6px;
  	margin: 0px;
  	text-align: left;
  	width: 300px;	
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
	}	
div.ss_popup_top {
  	position: relative;
  	background-color: #fff; 
  	height: 26px;
	border-bottom: 1px solid #e0e0e0;
	}
div.ss_popup_topLg {
  	position: relative;
  	background-image: url(<html:imagesPath/>pics/dialog_header_tile.png);
  	background-repeat: repeat-x;
  	height: 25px; 
  	width: 100%!important;
  	vertical-align:top;
	<c:if test="<%= isIE %>">
  		margin-top: 0px!important; 
	</c:if>  	
	}	
div.ss_popup_title {
   font-family: ${ss_style_title_font_family};
   font-size: 14px;
   font-weight: normal;
   color: #036f9f;
   padding-top: 10px;
   position: relative;
	}
div.ss_popup_close {
  	position: relative;
  	background-image: url(<html:imagesPath/>icons/close_gray16.png);
 	background-repeat: no-repeat;
  	width: 18px;
  	height: 20px;
  	top: 2px;
  	left: 0px;
	}
div.ss_popup_body {
  	position: relative;
 /* background-color: ${ss_toolbar4_background_color}; */
  	background-color: #FFFFFF;
  	padding: 10px;
    font-family: ${ss_style_title_font_family}; 
    font-size: ${ss_style_font_smallprint};
	-moz-border-radius-bottomleft: 3px;
	-moz-border-radius-bottomright: 3px;
	border-bottom-left-radius: 3px;
	border-bottom-right-radius: 3px;
	-webkit-border-bottom-left-radius: 3px;
	-webkit-border-bottom-right-radius: 3px;
	}
.ss_popup ul{
	text-decoration: none;
	}	
.ss_popupMenu {
  	position:absolute;
  	border:1px solid black;
  	margin:2px;
  	padding:2px;
  	background-color: ${ss_style_background_color_opaque};
  	}
.ss_popupDiv {
  	position:absolute;
  	margin:2px;
  	padding:0 2px 2px 2px;
  	background-color: transparent !important;
  	}  
.ss_popupDiv input[type="submit"]:hover {	
  	padding: 0px 7px 0px 7px;
	<c:if test="<%= isIE %>">
  		padding: 0px;
  		padding-top: -2px;
  		padding-bottom: 1px;
  		padding-left: 1px; 
  		padding-right: 0px;
	</c:if>  	
	}  
.ss_popupDiv input[type="submit"] {	
	<c:if test="<%= isIE %>">
  		margin-left: 1px; 
  		margin-right: 1px;
	</c:if>  	
	}		
.ss_popupMenuClose {
  	padding:0px 8px 0px 0px;
  	text-align:right;
	}
.ss_valignTop {
	vertical-align: top;
	}
.ss_popup_topright {
	text-align: right!important;
	}
.ss_popup_topcenter {
	text-align: center!important;
	}
.ss_popup_topleft {
	text-align: left!important;
	}			
	
/* THEME MENU STYLES */

div.ss_themeMenu {
  position:absolute;
  border:1px solid ${ss_toolbar1_background_color};
  margin: 0px;
  background-color:${ss_style_background_color_opaque};
  text-align: left;
  width: 25%;
  min-width: 150px;
}
.ss_themeMenu_top {
	text-align: left;
	padding: 0px 15px;
	white-space: nowrap;
}
div.ss_themeMenu a {
  font-family: ${ss_style_font_family};
  font-size: ${ss_style_font_size}; 
  text-decoration: underline;
}
div.ss_themeMenu a:hover {
	color: #333333 !important;
}

div.ss_themeMenu ul {
	list-style-type: none;
	padding-left: 0px;
	margin-left: 20px;
}
		
/* EDITOR APPLICATION CONFIGURATION MENU STYLES */

div.ss_appConfigMenu {
  position:absolute;
  border:0px solid ${ss_toolbar1_background_color};
  margin: 0px;
  background-color:${ss_style_background_color_opaque};
  text-align: left;
}
.ss_appConfigMenu_top {
	text-align: center;
	padding: 0px;
	white-space: nowrap;
}
div.ss_appConfigMenu a {
  font-family: ${ss_style_font_family};
  font-size: ${ss_style_font_size};
  color: #036f9f; 
}

div.ss_appConfigMenu ul {
	list-style-type: none;
	padding-left: 0px;
	margin-left: 20px;
}
		
/* ENTRY VIEW TOOLBAR STYLES */
/* FAMILY TYPES = (10)unspecified (11)blog (12)calendar (13)discussion (14)photo (15)wiki (16)task (17)file */

table.ss_actions_bar13_pane, table.ss_actions_bar13_pane td {
	width: 100%;
	padding: 0px 2px 0px 0px;
	margin: 0px;
	}
.ss_actions_bar13_pane a {
	-moz-border-radius:10px 10px 10px 10px;
	border-radius:10px 10px 10px 10px;
	-webkit-border-radius:10px 10px 10px 10px;
	background:none repeat scroll 0 0 #949494;
	color:#FFFFFF;
	margin-right:3px;
	padding:2px 10px;
	text-decoration:none;
}	
.ss_actions_bar13_pane_none {
	-moz-border-radius:0px !important;
	border-radius:0px !important;
	-webkit-border-radius:0px !important;
	background:#FFFFFF !important;
	color:#000000 !important;
	margin:0px !important;
	padding:0px !important;
	text-decoration:none;
}
.ss_actions_bar13_pane ul {
	height:auto;
}
.ss_actions_bar13 {
	font-size: ${ss_style_font_normalprint};
	font-family: ${ss_style_title_font_family};
	padding: 0px 3px;
	}
.ss_toolbarDeleteBtn {
    background-color: #949494 !important;
    }
.ss_toolbarDeleteBtnDisabled {
    background-color: #cecece !important;
    }

/* Description region */
.wg-description-content {
	display: block;
	margin: 0px;
	padding: 0px;
	}	
.wg-description-content-clipped {
	height: 200px;
	overflow: auto;
	display: block;
	margin: 0px;
	padding: 0px;
	}
.wg-comment {
	background-color: #fff;
	padding: 7px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
	}	
.wg-comment .ss_entryTitle {
	margin-left: 0px;
	font-size: 14px;
	color: #036f9f !important;
	}

/* Teaming - Tabs */

.wg-tabs {
	text-align: center;
	border-bottom: 1px solid #ededed;
	}
.wg-tab, .wg-tab-b {
	background:none repeat scroll 0 0 #FFFFFF;
	font-size: 11px;
	font-weight: normal;
	padding: 3px 8px;
	margin: 2px 5px 2px 0px;
	display:inline-block;
	cursor: pointer;
	}
.wg-tab {
	color: #fff;
	background:none repeat scroll 0 0 #949494;
	}	

.wg-tab a {
	color: #fff !important;
	}
.wg-tab a:hover {
	color: #fff !important;
	background: transparent url(<html:rootPath/>images/pics/1pix.gif) no-repeat;
	}
.wg-tab.on a {
	color: #1f1f1f !important;
	}
.wg-tab:hover, .selected-menu {
	color: #036f9f;
	background: #cce9ee;
	}	
.wg-tab.on, .wg-tab-b.on {
	color: #1f1f1f;
	font-size: 12px;
	font-weight: bold;
	background:none repeat scroll 0 0 #ededed;
	cursor: pointer;
	display: inline;
	padding-bottom: 15px;
	margin-right: 5px;
	}
.wg-tab-content {
	background:none repeat scroll 0 0 #ededed;
	border: 0px;
	display: block;
	margin: 0px 0px 10px 0px;
	padding: 10px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
	}	
.wg-tab-content-clipped {
	background:none repeat scroll 0 0 #ededed;
	border: 0px;
	border-bottom: 1px solid #bbbbb9;
	height: 300px;
	overflow: auto;
	display: block;
	margin: 0px 0px 10px 0px;
	padding: 10px;
	-moz-border-radius-topleft: 5px;
	border-top-left-radius: 5px;
	-webkit-border-top-left-radius: 5px;
	}	
.wg-tab-iframe {
	height: 290px;
	width: 100%;
	border: 0px;
	}
.tabactions {
	font-size: 12px;
	text-align: right;
	margin: 5px;
	}
.inactive  { color: #6c8899 }
.margintop1 { margin-top: 0.3em }
.margintop2 { margin-top: 0.5em }
.margintop3 { margin-top: 1em }
.marginleft1 { margin-left: 1em }
.marginleft1b { font-size: 0.85em; margin-left: 1em }
.marginleft2 { margin-left: 2em }
.marginleft3 { margin-left: 3em }
.marginleft4 { margin-left: 4em }
.marginbottom1 { margin-bottom: 0.3em }
.marginbottom2 { margin-bottom: 0.5em }
.marginbottom3 { margin-bottom: 1em }
.roundcorner {
	-moz-border-radius: 10px;
	border-radius: 10px;
	-webkit-border-radius: 10px;
	}
.roundcornerSM {
	-moz-border-radius: 3px;
	border-radius: 3px;
	-webkit-border-radius: 3px;
	}	
.roundcornerSM-bottom {
	-moz-border-radius-bottomleft: 3px;
	-moz-border-radius-bottomright: 3px;
	border-bottom-left-radius: 3px;
	border-bottom-right-radius: 3px;
	-webkit-border-bottom-left-radius: 3px;
	-webkit-border-bottom-right-radius: 3px;
	}	
.roundcornerSM-top {
	-moz-border-radius-topleft: 3px;
	-moz-border-radius-topright: 3px;
	border-top-left-radius: 3px;
	border-top-right-radius: 3px;
	-webkit-border-top-left-radius: 3px;
	-webkit-border-top-right-radius: 3px;
	}	
.roundcornerLG {
	-moz-border-radius: 40px;
	border-radius: 40px;
	-webkit-border-radius: 40px;
	}
	
	
/* Styles for dialog boxes */
.teamingDlgBox {
	font-family: Arial,Helvetica,sans-serif;
	background: url(<html:imagesPath/>pics/trans30_black.png) repeat;
	padding: 5px;
	-moz-border-radius: 8px;
	border-radius: 8px;
	-webkit-border-radius: 8px;
	z-index: 1001;
	}
.teamingDlgBox_Glass {
	background-color: #000;
	filter: alpha(opacity=50);
	opacity: 0.5;
	overflow: hidden;
	z-index: 1000;
	}
.teamingDlgBox .popupContent {
	background-color: #f6f6f6;
	-moz-border-radius: 3px;
	border-radius: 3px;
	-webkit-border-radius: 3px;
	}
/* Styles for the content of a dialog box. */
.teamingDlgBoxContent{
	margin: 10px;
	}
/* Styles for the footer of a dialog box. */
.teamingDlgBoxFooter {
	background-color: #EAEBE8;
	border-top: 1px solid #BABDB6;
	margin-top: 15px;
	padding: 10px 0px 5px 10px;
	text-align: right;
	vertical-align: middle;
	-moz-border-radius-bottomleft: 3px;
	-moz-border-radius-bottomright: 3px;
	border-bottom-left-radius: 3px;
	border-bottom-right-radius: 3px;
	-webkit-border-bottom-left-radius: 3px;
	-webkit-border-bottom-right-radius: 3px;
	}
/* Styles for the header in a dialog box. */
.teamingDlgBoxHeader {
	color: #fff;
	font-size: 14px;
	font-weight: bold;
	background: transparent url(<html:imagesPath/>pics/dialog_header_tile.png) repeat-x;
	padding: 5px;
	-moz-border-radius-topleft: 3px;
	-moz-border-radius-topright: 3px;
	border-top-left-radius: 3px;
	border-top-right-radius: 3px;
	-webkit-border-top-left-radius: 3px;
	-webkit-border-top-right-radius: 3px;
	}
.teamingDlgBoxHeader .closebutton {
	position: absolute;
	top: 8px;
	right: 10px;
	}	
	
.diag_modal {
	position: relative;
	background-color: #eaebe8;
	margin: -10px 50px 20px;
	padding: 1px 20px 10px;
	-moz-border-radius-bottomleft: 10px;
	-moz-border-radius-bottomright: 10px;
	border-bottom-right-radius: 10px;
	border-bottom-left-radius: 10px;
	-webkit-border-bottom-right-radius: 10px;
	-webkit-border-bottom-left-radius: 10px;
	}
.diag_modal2 {
	position: relative;
	background-color: #f6f6f6;
	margin: -10px 50px 20px;
	padding: 15px 10px 10px ;
	min-width: 700px;
	-moz-border-radius-bottomleft: 10px;
	-moz-border-radius-bottomright: 10px;
	border-bottom-right-radius: 10px;
	border-bottom-left-radius: 10px;
	-webkit-border-bottom-right-radius: 10px;
	-webkit-border-bottom-left-radius: 10px;
	}
.diag_modal2 table div.ss_form_wrap {
	background: transparent;
	padding: 0px;
	}
.diag_modal2 table div.ss_form_header, .diag_modal2 div.ss_form_header table td {
	color: #333 !important;
	font-size: 14px !important;
	font-weight: bold !important;
	}
	
.diag_modal2 table div.ss_form_wrap	.ss_buttonBarLeft {
	display: block;
	text-align: right;
	}	
	
#ss_permalink_display_div {
	background-color: #eaebe8;
	padding: 10px;
	margin: 20px;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
 	}	

/* Action Menu Styles */
.action-anchor {
    padding: 2px 4px;
    margin-left: 5px;
    }   
a.action-anchor:hover { 
    color: #036f9f;
    background: #81b2bd url(<html:imagesPath/>pics/blends/teal_slice.png) repeat;
    background-position:center;
    -moz-border-radius: 3px;
    border-radius: 3px;
    -webkit-border-radius: 3px;
}
 
/*  Menu CSS  */
/*-------------------------------------------*/

.menudetail {
    position: relative; 
    background-color: #3a3e40; /* Gray Blend 2 base*/
    text-align: left; 
/*  border: solid 1px #000;  */
    top: 0px;
    width: 200px;
    z-index: 1000;
}

.entry-menu-list {
    position: absolute;
    font-size: .8em;
    }   
.entry-status-actions {
    position: absolute;
    top: 25px;
    right: 3px;
    }       
    
.menutitle {
    color: #F6F6F6;
    font-size: 1em;
    font-weight: bold;
    background: #3a3e40 url(../../../1Graphics/GraphicsDB/Branding/NL2010/gray_blend2_15.png) repeat-x; 
    background-position: bottom;
    margin-bottom: 0.5em;
    padding: 5px; 
}
   tr.rule2 div.menutitle { font-size: 1em; }
   td div.menutitle { font-size: 1em; }
   tr.columnhead div.menutitle { font-size: 1em; background-color: #e0e1df; }
   span div.menutitle { font-size: 1em; }

.menutitle img, .infoboxtitle img 
    {float: right}

tr.columnhead div.menudetail { background-color: #ffffff; text-align: left; position: absolute; z-index: 10; top: 0; width: 200px; border: solid 1px #000; font-weight: normal; font-size: 1em;}

.menuitem1 a:link { 
	text-decoration: none;
	}

.menuitem1 a, menuitem1 a:link, menuitem1 a:visited {
    color: #fff !important;
    padding: 2px 2px 2px 0;
    text-decoration: none;
    }

.menuitem1 { 
    margin-left: 8px;
    padding: 4px; 
    line-height: 1.1em; 
}

.menuitem2 {   /* disabled menu item (disabled link) */ 
    color: gray; 
    padding: 0.3em; 
    margin-left: 0.5em}

.menuitemspacer {    /* to add a spacer between a section or group within a menu */ 
    border-top: 1px solid #353838;
    border-bottom: 1px solid #505354;
    margin-top: 4px;
    margin-bottom: 4px;
    }

.menusubhead  {  /* used within a menu */ 
    color: gray;
    font-size: 0.75em;
    padding: 6px 4px 4px 6px;
    background-color: #F6F6F6;
    text-transform:uppercase;
    letter-spacing: 0.1em;
    vertical-align: middle;
    white-space: normal;
    }

.menubottom  { margin-bottom: 0px; padding-bottom: 0px; padding-top: 5px; }

#ss_folder_column_menu, #ss_subscription_menu {
	background-color: transparent !important;
	}
	
.n_date_picker img {
	vertical-align: middle;
	}	

/*-------------------------------------------*/
