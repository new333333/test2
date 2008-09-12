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
	background-color:#EBEBEA;*/bleeds through breadcrumbs*/
	border-color:#CCCCCC;
	border: 1px;
	padding-bottom:0px;	
	}
	/*BRANDING*/
#ss_branding{
	width:100%;
	margin-top: 0px !important;
	padding: 0px !important;
	height:60px;       /* 60 px for Boulder Need to define specific height for graphics */
	overflow:hidden;	/* This chops off any overhanging divs */
	}	
		
	/* MASTHEAD CODE */
	
.ss_search_bar{
	background-color: #5A9A98;
	border-bottom: 2px solid #5A9A98;
	}
.ss_searchtext {
	font-family: Arial, Helvetica, sans-serif !important;
	font-size: 10px !important;
	color: #FFFFFF !important;
	background-color: #5A9A98;		/* boulder: #5A9A98; kablink: #449EFF */
	text-align: left;
	margin:0px;
	padding-left:3px;
	white-space:nowrap !important;
	}
.ss_searchtext a{
	color: #FFFFFF !important;
}
.ss_searchtext a:hover{
	color: #F47400!important;
}
.ss_search_title {
	font-family: Arial, Helvetica, sans-serif !important;
	font-size: 1.0em !important;
	font-weight: 200;
	color: #FFFFFF !important;
	background-color: #5A9A98;		/* boulder: #5A9A98; kablink: #449EFF */
	padding-top: 10px;
	padding-right: 4px;
	white-space:nowrap !important;
	padding-left:5px;
	}
.ss_find {
	height:30px;
	}
.ss_workspace {
	font-family: Arial, snas serif;
	background-position: left center;
	font-size: 1em !important;
	font-weight: 200;
	font-style: bold;
	color: #FFFFFF !important;
	white-space:nowrap;
	}
.ss_workspace a{
	color: #FFFFFF !important;
	}
.ss_workspace a:hover{
	color: #F47400!important;
	}
.ss_masthead_top {
	background-image: url("<html:rootPath/>images/pics/masthead/masthead_bg.png");  /* kablink: masthead_ka.png */
	background-repeat: repeat-y;
	background-position: right;
	background-color: #FFF;
	}
	
/***********THIS SHOULD BE MOVED TO A STYLE SHEET FOR CUSTOMIZATION************/
.ss_companyName {	
	text-align: left;
	vertical-align: middle;
	font-family: Arial, Helvetica, sans-serif !important;
	font-weight: bold;
	overflow: hidden;
	font-size: 36px !important;
	color: #999999 !important;
	text-indent: 5px;
	}
.ss_masthead_top div {
	clear: none;
	float: none;
	height: 54px;
	width: 92px;
	position: relative;
	}
.ss_mastheadName {
	font-family: ${ss_style_font_family};
	font-size: 14px;
	font-weight: normal;
	color: #FFFFFF;
	font-style: oblique;
	line-height: 16px;
	padding-right: 10px;
	white-space: nowrap;
	}
.ss_mastheadName a{
	color:#FFFFFF !important;
	}
.ss_mastheadName a:hover{
	color:#555555 !important;
	}
.ss_mastheadtoplinks {
	background-position: left center;
	font-size: 11px !important;
	font-weight: bold;
	color: #5A9A98 !important;		/* boulder: #5A9A98; kablink: #449EFF */
	white-space:nowrap !important;
	}
.ss_masthead_fullscreen {
	background-position: left center;
	font-size: 11px;
	font-weight: normal;
	color: #FFFFFF;
	}
.ss_masthead_portals {
	color: #375E5C !important;		/* boulder: #375E5C; kablink: #374C5D */
	}
.ss_masthead_portals a{
color: #375E5C !important;		/* boulder: #375E5C; kablink: #374C5D */
	}
.ss_masthead_portals a:hover{
	color: #F47400 !important;
	}	
.ss_masthead_favorites a{
	color: #444 !important;		/* boulder: #5A9A98; kablink: #449EFF */
	}
ss_masthead_favorites a:hover{
	color: #F47400 !important;
	}
	/* LOGO DIVS */		
  .ss_logo1 {   
	background-position: 0px 0px;
	background-repeat:no-repeat;
	margin-left: -43px;
	margin-right: -20px;
	padding-top: 0px;
	padding-bottom: 0px;
	}			
	
		
	