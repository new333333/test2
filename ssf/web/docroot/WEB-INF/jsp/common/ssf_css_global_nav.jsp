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
	/*WRAPPER*/
#ss_top_nav_wrapper{
	width: 100%;
	background-color:#EBEBEA;
	border-color:#CCCCCC;
	border: 1px;
	padding-bottom:2px;
	}
	
	/* NAVIGATION AREA FOR STATUS LINE AND SHARE AND TRACK BUTTONS */

#ss_statusArea{
	float:left;
	width:30%;
	min-width:335px;
	padding-left:10px;
	vertical-align:top;
	display:inline;

	}	
#ss_statusArea li{
	display:inline;
	list-style:none;
	}
.ss_rt_buffer {
	margin-right:10px;
	}	
	
	/* STATUS BAR TEXT */
	
.ss_statusprint	{
	font-size: ${ss_style_font_smallprint} !important; 
	font-weight:bold;
	}
.ss_fullMenuprint	{
	font-size: ${ss_style_font_smallprint} !important; 
	color:#333;
	text-align:center!important;
	}	
	
	/* SHARE AND TRACK BUTTONS */		
	
a.ss_STButton:focus, a.ss_STButton:hover {
  	background-image:url("<html:rootPath/>images/pics/sharetrackbkg_grey.png")!important;   
	background-position: 0px 0px;
	background-repeat:no-repeat;
  color: ${ss_linkbutton_link_hover_color};
  font-family: ${ss_style_folder_view_font_family};
  font-size: ${ss_style_font_fineprint};
  padding: 1px 4px 1px 3px;
  cursor: pointer;
  white-space: nowrap;
  width:35px;
  height:16px;
}
a.ss_STButton {
  	background-image:url("<html:rootPath/>images/pics/sharetrackbkg_blue.png");   
	background-position: 0px 0px;
	background-repeat:no-repeat;
  color: #ffffff!important;
  font-family: ${ss_style_folder_view_font_family};
  font-size: ${ss_style_font_fineprint};
  padding: 1px 4px 1px 3px;
  cursor: pointer;
  white-space: nowrap;
  width:35px;
  height:16px;
}
		

	/* NAVIGATION BUTTONS FOR MY WORKSPACE, MY TEAMS, FAVORITES */	

#ss_top_nav_buttontwo{
	height:16px;
	padding-top:0px;	/* adding in the following to make right sided div */
	vertical-align:top;
	margin-left:40%;	
	width:100%;
	}

#ss_top_nav_buttontwo a {
	font-family:  Arial, sans-serif;
	font-size: 9px;
	color: #FFFFFF;
	font-weight: bold;
	letter-spacing: 0px;
}
#ss_top_nav_buttontwo ul{
	margin:0px;
	padding:0px;
	padding-left:10px;
	list-style:none;
	width:100%;
	text-align:left;
	float:left;
	}
#ss_top_nav_buttontwo ul li{
	display:inline;
	float:left;
	margin:0px 3px 0px 0px;
	}
#ss_top_nav_buttontwo ul li a{
	float:left;
	color:#FFFFFF;
	background-color: #6B737B;
	border-style: solid;
	border-color: white;
	border-width: .01em;
	padding:5px 15px 0px 15px;
	height:16px;
	}
#ss_top_nav_buttontwo ul li a:hover{
	color: #333333;
	background-color: #CCC;
	border-style: solid;
	border-color: #FFF;
	border-width: .01em;
	}	
	
	/* NAVIGATION BUTTONS FOR PORTAL AND EXPANDED VIEW */		
	
#ss_top_nav_view{					
	position:relative;
	display:inline;
	text-align:left;
	height:16px;
	padding-top:5px;
	margin-left:5px;	/* need to keep margin-left to push past logo */
	width:100%;	
	}
#ss_top_nav_view a {
	font-family:  Arial, sans-serif;
	font-size: 9px;
	letter-spacing: 0px;
}
#ss_top_nav_view ul{
	margin:0 0 0 40px;
	list-style:none;
	width:100%;
	}
#ss_top_nav_view ul li{
	display:inline;
	padding:0px 2px 0px 0px;
	width:100%;
	}
#ss_top_nav_view ul li a{
	background-image:url("<html:rootPath/>images/pics/portalepand_grey.png");   
	background-position: 0px 0px;
	background-repeat:no-repeat;
	color:#333;
	padding:0px 5px 0px 13px;		/* need to keep padding-left to push past box */
	}
#ss_top_nav_view ul li a:hover{
	background-image:url("<html:rootPath/>images/pics/portalepand_red.png");   
	background-position: 0px 0px;
	background-repeat:no-repeat;
	color: #EF2121!important;
	}	
	
	/* LOGO DIVS */	
	
.ss_logo1 {
	background-image:url("<html:rootPath/>images/pics/black_n.png");   
	background-position: 0px 0px;
	background-repeat:no-repeat;
	margin-left: -20px;
	margin-right: 20px;
	padding-top: 0px;
	padding-bottom: 0px;

	}		
	
	/* 5 COLUMN CONTAINER */
	
	.ss_5colmask {
	    position:relative;		/* This fixes the IE7 overflow hidden bug */
	    clear:both;
	    float:right;
        width:100%;			/* width of whole page */
		overflow:hidden;	/* This chops off any overhanging divs */
		height:40px;
	}
	/* 5 COLUMN SETTINGS */
	
	.ss_5colmidright,
	.ss_5colleftctr,
	.ss_5colright,
	.ss_5colmid,
	.ss_5colleft {
		float:left;
		width:100%;				/* width of page */
		position:relative;
	}
	.ss_5col1,
	.ss_5col2,
	.ss_5col3,
	.ss_5col4,
	.ss_5col5 {
		float:left;
		position:relative;
		padding:0px 0 1em 0;	/* no left and right padding on columns, we just make them narrower instead 
								only padding top and bottom is included here, make it whatever value you need */
		overflow:hidden;
	}
	
	.ss_5col2,					
	.ss_5col3,
	.ss_5col4 {
		padding-top:2px;		/* this lines up find divs with search div */
	}	
	/* 5 Column settings */
	.ss_fivecol {
	/*	background:#f0caaa;		 right column background colour */
	}
	.ss_fivecol .ss_5colmidright {
		right:24%;				/* width of the right column */
	/*	background:#c00000;		 center column background colour */
	}
	.ss_fivecol .ss_5colmid {
		right:14%;				/* width of the find tags column */
	/*	background:#ffff99;		 center column background colour */
	}
	.ss_fivecol .ss_5colleft {
		right:14%;				/* width of the find places column */
	/*	background:#B7C9E0;		 left column background colour */
	}
	.ss_fivecol .ss_5colleftctr {
		right:14%;				/* width of the find people column */
	/*	background:#dddeee;		 left column background colour */
	}	
	.ss_fivecol .ss_5col1 {
		width:34%;				/* width of portal/expanded view column  */
		left:70%;				/* 100% plus left padding of column */
	}
	.ss_fivecol .ss_5col2 {
		width:12%;				/*  Width of find people column content (column width minus padding on either side) */
		left:70%;				/*  width of (right column) plus (center column left and right padding) plus (left column left padding) */
		min-width:75px!important;
	}
	.ss_fivecol .ss_5col3 {
		width:12%;				 			/* Width of find places column content (column width minus padding on either side) */
		left:71%;				
		min-width:75px!important;			/* Please make note of the brackets here:
								(100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	}
	.ss_fivecol .ss_5col4 {
		width:12%;							/*  Width of find tags column content (column width minus padding on either side) */
		left:72%;	
		min-width:75px!important;			/* Please make note of the brackets here:
								(100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	}
	.ss_fivecol .ss_5col5 {
		width:22%;				 			/* 	Width of right column content (column width minus padding on either side) */
		left:73%;	
		min-width:150px!important;			/* Please make note of the brackets here:
											(100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	}	

	/* DIV FOR LINE ACROSS GLOBAL NAV */	
		
	.ss_darkline {
		position:relative;
		float:left;
		height:10px;
		background-color:#969696;
		width:100%;
		}
		
	/* FULL MENU BUTTON */	
		
a.ss_fullMenuButton img {
	width: 15px;
	height: 20px;
	margin: 0px;
	padding: 0px;
	border: 0px;
	vertical-align: middle;
}

a.ss_fullMenuButton img, a.ss_fullMenuButton:link img , a.ss_fullMenuButton:focus img, a.ss_fullMenuButton:visited img { 
	background: transparent url(<html:rootPath/>images/pics/fullmenubkg.png) no-repeat top left; 
}

a.ss_fullMenuButton:hover img {
    background-position:  left -20px; 
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}		
		
	/* LEGACY GLOBAL TOOLBAR CSS */				

div.ss_global_toolbar_in_portlet {
	background-color: transparent;
	height: auto;
}
div.ss_global_toolbar_maximized {
    position: relative;
	padding-top:2px;
}
div.ss_global_findbar {
    position: relative;
	padding-top:2px;
}
div.ss_global_toolbar_maximized div.ss_global_toolbar_myworkspace, div.ss_global_toolbar_maximized div.ss_global_toolbar_favs,
div.ss_global_toolbar_maximized div.ss_global_toolbar_myteams, 
 div.ss_global_toolbar_maximized div.ss_global_toolbar_divider, div.ss_global_toolbar_maximized div.ss_global_toolbar_quick,
 div.ss_global_toolbar_maximized .ss_global_toolbar_findUser, div.ss_global_toolbar_maximized .ss_global_toolbar_help,
 div.ss_global_toolbar_maximized .ss_global_toolbar_accessible {
	white-space: nowrap;
}

div.ss_global_toolbar_help {
	text-align: center;
    <c:if test="<%= !isIE %>">
    padding-top: 15px;
    </c:if>
}

div#ss_navbarHelpButton {
    margin-left: 5px;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_divider {
	width: 4px;
	height: 43px;
	background: transparent url(<html:imagesPath/>pics/divider_global_toolbar.gif) no-repeat;	
	margin: 0px 5px 0px 5px;	
}

div.ss_global_toolbar_maximized .ss_global_toolbar_accessible {
	vertical-align: top;
	padding-top: 3px;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_findUser {
	vertical-align: top;
	padding-top: 3px;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_quick {
	padding-top: 0px;
	padding-bottom: 2px;
	border-top: 1px dashed #c00000;
	margin-top: 8px
}

div.ss_global_toolbar_maximized .ss_global_toolbar_quick .ss_global_toolbar_label_text {
	text-align: left;	
	vertical-align: top;
}

div.ss_global_toolbar_maximized .ss_global_toolbar_quick .ss_global_toolbar_quick_advanced {
	text-align: right;
	font-size: 5px;
	padding-left: 5px;
	vertical-align: top;
}

div.ss_global_toolbar table {
	background-color: transparent;
}
div.ss_global_toolbar table tr.ss_row_txt td {
	padding-top: 10px;
}
div.ss_global_toolbar table tr.ss_row_last td {
	height: 29px;
}
div.ss_global_toolbar table td {
	white-space: nowrap;
	padding-left: 5px;
	padding-right: 5px;
}

div.ss_global_toolbar_maximized table.ss_global_toolbar_maximized td {
	white-space: nowrap;
	padding-left: 2px;
	padding-right: 0px;
}
.ss_global_toolbar_portlet_box {
	background-color: ${ss_toolbar4_background_color} !important;
}

.ss_global_toolbar a span, .ss_global_toolbar div span {
	background-color:transparent;
}



.ss_global_toolbar_myworkspace div, .ss_global_toolbar_favs div,
.ss_global_toolbar_myteams div  {
	padding: 0px 5px 0px 5px;
	vertical-align: middle;
}

.ss_global_toolbar_favs div {
	background:url(<html:brandedImagesPath/>icons/toolbar_favorites.gif) no-repeat top;
	background-position: left 0px;
	width: 49px;
	height: 42px;	
}

.ss_global_toolbar_favs div:hover {
	background:url(<html:brandedImagesPath/>icons/toolbar_favorites.gif) no-repeat top;
	background-position: left -42px;
	width: 49px;
	height: 42px;	
}

.ss_global_toolbar_myworkspace div {
	background:url(<html:brandedImagesPath/>icons/toolbar_myworkspace.gif) no-repeat top;
	background-position: left 0px;
	width: 61px;
	height: 42px;	
}

.ss_global_toolbar_myworkspace div:hover {
	background:url(<html:brandedImagesPath/>icons/toolbar_myworkspace.gif) no-repeat top;
	width: 61px;
	height: 42px;
	background-position: left -42px;
}

.ss_global_toolbar_myteams div {
	background:url(<html:brandedImagesPath/>icons/toolbar_workspace_teaming.gif) no-repeat top;
	background-position: left 0px;
	width: 46px;
	height: 42px;	
}

.ss_global_toolbar_myteams div:hover {
	background:url(<html:brandedImagesPath/>icons/toolbar_workspace_teaming.gif) no-repeat top;
	background-position: left -42px;
	width: 46px;
	height: 42px;	
}

.ss_global_toolbar_favs_big div {
	background:url(<html:brandedImagesPath/>icons/toolbar_favorites_big.jpg) no-repeat top;
	width: 75px;
	padding-top: 60px;
	text-align: center;
}
.ss_global_toolbar_myworkspace_big div {
	background:url(<html:brandedImagesPath/>icons/toolbar_myworkspace_big.jpg) no-repeat top;
	width: 85px;
	padding-top: 60px;
	text-align: center;
}

div.ss_global_toolbar_show_portal {
	background:url(<html:imagesPath/>icons/toolbar_show_portal.jpg) no-repeat top;
}
div.ss_global_toolbar_hide_portal {
	background:url(<html:imagesPath/>icons/toolbar_hide_portal.jpg) no-repeat top;
}
div.ss_global_toolbar_help {
	background:url(<html:imagesPath/>icons/help.png) no-repeat top center;
	width: 30px;
	padding-right: 4px;
	margin-top: 5px;
	text-align: center;
}
.ss_global_toolbar_accessible {
	margin-top: 0px;
	background-color: transparent;
}
.ss_global_toolbar_accessible form {
	background-color:transparent;
}
.ss_global_toolbar_accessible a {
    background-color:transparent;
}
.ss_global_toolbar_accessible img {
	background-color: transparent;
}
.ss_global_toolbar_findUser {
	margin-top: 0px;
	background-color: transparent;
}
.ss_global_toolbar_findUser form {
	background-color:transparent;
}
.ss_global_toolbar_findUser .form-text {
	width:40px;
}
span.ss_global_toolbar_label_text, span.ss_global_toolbar_label_text_quickSearch {
	color:#333333;
	background-color: transparent;
	white-space: nowrap;
}

span.ss_global_toolbar_label_text_quickSearch {
	font-size: ${ss_style_font_fineprint};
	font-family: Arial, sans serif;
}
.ss_global_toolbar_label_text {
	font-size: 10px;
	font-family: Arial, sans serif;
}
.ss_label_text {
	font-size: 10px;
	font-family: Arial, sans serif;
	color:#c00000;
}
div.ss_global_toolbar_findUser_text span {
}
.ss_global_toolbar_findUser a {
}
.ss_global_toolbar_search {
	margin-top: 0px;
	background-color: transparent;
	vertical-align: bottom;
}
.ss_global_toolbar_search form {
	background-color:transparent;
}

.ss_global_toolbar_quick input {
	width: 115px;
	background-color: #FFFFFF;
	border: 1px solid #7F9DB9;
}
div.ss_global_toolbar_search_text {
    width:100px;
	color:#484848;
	text-align:center;
	padding:0px !important;
}
div.ss_global_toolbar_search_text span {
	background-color:inherit !important;
}	

