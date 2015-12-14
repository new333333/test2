<%
/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

/* CSS Document - container for navigation and search */
	
	/* COLUMN CONTAINER */
#ss_dashboard_content {
	vertical-align:top;
 	overflow:hidden; /* This chops off any overhanging divs */
	}

/* COMMON COLUMN SETTINGS */
.ss_colright, .ss_colmid, .ss_colleft{
	float:left;
	width:100%;  /* width of page */
	}
.ss_col1, .ss_col2, .ss_col3 {
	padding-bottom: 10px; 
	margin: 0px 3px;
	overflow: hidden;
	}	
.ss_col1, .ss_col2 {
	float: left;
	}	
.ss_col3 {
	float: right;
	}	
.col-nextback-but {
	padding-left: 5px;
	}
		
/* 3 COLUMN PAGE STYLE SETTINGS */
.ss_tricolumn{	/* 3 Column page style */
	background-color: transparent; /* right column background color */
	} 
.ss_tricolumn .ss_colmid{
	right: 21%;
	/* width of the right column */
	/* Need to change this to change background color */
	background-color: transparent; /* was #FFFFFF */
	}
.ss_tricolumn .ss_colleft {
	right:20%;				/* placement from right of the middle column */
	}
.ss_tricolumn .ss_col1 {
	width:37%;				/* width of center column content (column width minus padding on either side) */
	left:42%;				/* 100% plus left padding of center column */
	<c:if test="<%= isIE %>">
 		width:35%;
 	</c:if>	
	}
.ss_tricolumn .ss_col2 {
	width:37%;				/* Width of left column content (column width minus padding on either side) */
	left:43%;				/* width of (right column) plus (center column left and right padding) plus (left column left padding) */
	}
.ss_tricolumn .ss_col3 {
	width:23%;				/* Width of right column content (column width minus padding on either side) */
	left:44%;				/* (100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	padding: 5px 0px;
	}

div.ss_canvas {
	border-left: solid 1px ${ss_toolbar1_background_color};
	border-right: solid 1px ${ss_toolbar1_background_color};
	border-bottom: solid .3px ${ss_toolbar1_background_color};
	padding-bottom: 0px;
	margin-bottom: 10px;
	background-color: red; /* ${ss_style_background_color_side_panel} */
	}
#ss_wrap{
	margin: auto;
	min-height:100%;
	clear:both;
    width:100%;			/* width of whole page */
	overflow:hidden;	/* This chops off any overhanging divs */
	text-align:left;
	}	
		
/*  THE FOLLOWING DEFINES ALL THE IMAGES FOR THE Title BARS */
	
.ss_tasks_img {
	background-image: url("<html:rootPath/>images/pics/check16x22.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}	
.ss_ping_img {
	background-image: url("<html:rootPath/>images/pics/im_16x16.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}
.ss_cal_img {
	background-image: url("<html:rootPath/>images/pics/calendar16x16_2.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}		
.ss_email_img {
	background-image: url("<html:rootPath/>images/pics/mailicon.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}	
.ss_bookmarks_img {
	background-image: url("<html:rootPath/>images/pics/bookmarks16x16.png");
	background-repeat: no-repeat;
	background-position: 97% center;
	}	
.ss_tracked_icon {
	background-image: url("<html:rootPath/>images/pics/tracking20x16.png");
	background-repeat: no-repeat;
	background-position: 3px 3px;
	padding: 2px 0px 5px 23px !important;
	
	}	
.ss_recentfolder_img {
	background-image: url("<html:rootPath/>images/pics/folder_blue_recent.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}
.display-pointer {
	cursor: pointer;
	}	
			
/* BOX STYLES*/	

#ss_Box{
	padding: 0;
	margin: 4px 0 6px 0;
	}	
	/*SHARED*/
#ss_networkupdates{
	padding: 0;
	margin: 4px 0 4px 0;
	}
#ss_shared{
	margin: 0 0 6px 0;
	<c:if test="<%= isIE %>">
 	margin: 3px 0 6px 0;
 	</c:if>	
	line-height:1.6em;
	}
.ss_shared_para {
	line-height:1.6em;
	}

	/*MINIBLOG*/

#ss_blogs{
	margin: 4px 0 6px 0;
	<c:if test="<%= isIE %>">
 	margin: 3px 0 6px 0;
 	</c:if>	
	color:#444444;
	line-height:1.6em;
	}
 	/*TASKS*/
#ss_tasks{
	margin: 4px 0 6px 0;
	padding: 2px 0 15px;
	line-height: 1.6em;
	}
#ss_tasks_para {
	padding:5px 0 0 15px;
	}
#ss_tasks_para .ss_link_1 a {
	text-decoration:underline!important;
	color: #3e6978 !important; 
	}
#ss_tasks_para .ss_link_2 a {
	text-decoration:underline!important;
	color:#4AAA42!important;
	font-size:10px!important; 
	}
#ss_tasks_para {
	list-style-type: square!important;
	padding-top:5px;
	}
	/*COMMON STYLE ITEMS*/	
#ss_hints{
	font-size: 12px;
	line-height: 13px;
	padding-right: 2px;
	padding-bottom:5px;
	color:#f63;
	margin-right: 5px;
	margin-bottom: 5px;
	font-family: Arial, Helvetica, sans-serif;

	font-style: italic;
	}
#ss_nextPage{			/* to use when need to display left right arrows */
	padding-left: 10px;
	padding-right: 5px;
	}
#ss_col3_para {			/* to use for 3rd column displays */
	margin: 10px 0px;
	}			
	/*TRACKED ITEMS*/	
#ss_trackedItems{
	margin: 4px 0 6px 0;
	padding: 0 0 2px 0;
	}
#ss_trackedPeople{
	background-color:transparent;
	line-height: 1.6em;
	}
#ss_im_status{
	font-size: 11px;
	line-height: 13px;
	padding: 8px;
	background: #FFF;
	-moz-border-radius: 3px;
	border-radius: 3px;
	-webkit-border-radius: 3px;
	}
.ss_im_status_active{
	font-weight: bold;
	background: #FFFBD6!important;	
	}	
					
	/*CALENDAR*/
#ss_calendar{
	padding: 0;
	margin: 4px 0 6px 0;
	padding: 2px 0 0px;
	line-height: 1.5em;
	}
#ss_visit_para {
	line-height:1em;
	padding-top:5px;
	}
#ss_visit_para li{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #555555;}
	
	/* ACTIVE LINKS  VISITOR */
#ss_visit_para a{
	text-decoration:underline;
}
	
	/*VIEWED ITEMS*/
#ss_viewedItems {
	}
#ss_viewedItems .ss_v_entries ul{
	padding: 0;
	margin-left: 4px;
	}
#ss_viewedItems .ss_v_entries li{
	background-image: url("<html:rootPath/>images/pics/entry_icon.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	padding-left: 30px;
	padding-top: 3px;
	padding-bottom: 1.6em;
	border-bottom:1px dotted #5691A6;
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}
#ss_viewedItems .ss_v_attachments ul {
	padding: 0;
	margin-left: 4px;
	}
#ss_viewedItems .ss_v_attachments li {
	background-image: url("<html:rootPath/>images/pics/attachment_icon.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	padding-left: 30px;
	padding-top: 3px;
	padding-bottom: 1.6em;
	border-bottom:1px dotted #5691A6;
    list-style:none !important;
    list-style-image: url(<html:imagesPath/>pics/1pix.gif) !important;
	}	
	/* ACTIVE LINKS - VIEWED ITEMS*/	
#ss_viewedItems a{
	text-decoration:none;
	}

	/*CALENDAR*/

#ss_cal_para{
	padding: 5px 0 0 15px;
	}
#ss_cal_para li{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #5691A6;
	}
	/* CALENDAR ACTIVE LINKS */
#ss_cal_para a{
	text-decoration:underline;
	}

	/*MY DOCUMENTS*/
	
#ss_mydocs_para div.item, .ss_v_entries div.item, .ss_viewedItems div.item, .ss_v_attachments div.item, #ss_hot_para div.item, #ss_tasks_para div.item, .ss_shared_para div.item, .ss_newinbinder div.item {
	background: #fff;
	background-position: top;
	margin-top: 3px;
    min-width: 220px;
	padding: 6px 10px 10px;
	line-height:1.2em;
	border-radius: 3px;
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
	}
div.item-sub a {
	font-weight: normal !important;
	}
div.item-sub {
	position: relative;
	}    
div.item-sub span.ss_entryDate {
	position: absolute;
    top: 1px;
	}    
	/* ACTIVE LINK */
#ss_mydocs_para a {					
	text-decoration:underline;
	}

	/* WHATS HOT */

#ss_whatshot{
	margin: 4px 0 6px 0;
	padding: 0 0 2px 0;
	line-height: 1.6em;
	}
	/* SURVEY*/
#ss_survey{
	margin: 4px 0 6px 0;
	color:#555;
	border:1px solid #5691A6;
	}
	
	/*TAGS*/
#ss_tags{
	font-size: ${ss_style_font_normalprint};
	color: #CCCCCC !important;
	line-height: 18px;
	margin-left: 4px !important;
	margin-bottom: 10px;
	background: #FFF;
	-moz-border-radius: 3px;
	border-radius: 3px;
	-webkit-border-radius: 3px;
	}
	/* NOTES */
#ss_notes{
	margin: 4px 0 6px 0;
	color:#555;
	border:1px solid #5691A6;
	}
	/* BOOKMARKS */
#ss_bookmarks{
	margin: 4px 0 6px 0;
	color:#555;
	}
	/* ACTIVITIES */
.ss_activity{
	margin: 4px 4px 6px 15px;
	color:#555;
	}
	/* WHATS NEW */
#ss_whatsnew{
	margin: 4px 0 6px 0;
	}
	/* PERSONAL TRACKER */
#ss_personaltracker{
	margin: 4px 0 6px 0;
	color:#555;
	border:1px solid #A7A9AC
	}
	/* EMAIL */
#ss_email{
	margin: 4px 0 6px 0;
	color:#555;
	font-family:  Arial, sans serif;
	line-height: 1.5em;
	padding: 0;
	}
	/* PEOPLE */
#ss_people{
	margin: 4px 0 6px 0;
	color:#555;
	}
	/* DOCUMENTS */
#ss_title{
	margin: 0px 0px 2px 0px;
	font-size: 16px;
	font-weight: 500;
	font-family: Arial, Helvetica, sans-serif;
	}
.ss_title_im{
	background: #FFFFFF;
	}	
#ss_subtitle{
	font-size:14px;
	color:#555555;
	padding: 2px 0 4px 6px;
	}

/*  BOX TITLES */
.ss_pt_title{
	font-size:14px;
	padding: 2px 0px 2px 5px;
	width:100%;
	font-family: Arial, Helvetica, sans-serif;
	}
/*  BOX TITLE COLORS   */
.ss_blue{
	background-color: #CCD2E3;
	}
.ss_green{
	background-color: transparent;
	}
.ss_orange{	
	background-color: #F93;
	}

/* ******************************  */

.ss_pt_para { 	
	margin: 4px; 
	}
.ss_pt_para ul{
	color:#555555;
}
.ss_pt_para ul li{
	margin-left: -10px;
	list-style-type: disc;
	border-bottom:1px dotted #5691A6;	
	}
.ss_prioValue{
	font-weight:bold;
	padding:0 3px 0 3px;
	}
/* BACKGROUND COLORS */	
.ss_paraC{
	background-color:transparent;
	padding-left: 	5px;	
	}
.ss_paraD{                    
	background-color:transparent;
	padding-bottom: 15px;
	}	
/* TIMEBLOCK STYLES */	
#ss_today {
	margin: 0 0 0 0;
	}
#ss_yesterday{
	margin: 0 0 0 10px;
	}
#ss_lastweek{
	margin: 0 0 0 10px;
	}
/* COMPRESSED BOX*/	
 .ss_closed	{
	height:20px;
	}
 /* when a milestone is overdue */				
 .ss_overdue{
	color: #CC0000;
	font-style: italic;
	}	
	
	/* HIDE TOGGLE*/

.ss_toggle{
	font-size: ${ss_style_font_smallprint};
	display: block;
	padding-right: 5px;
	}
/* =================THESE NEED TO BE CONSOLIDATED======== */


/* to use when need to display summary of an entry */

.ss_summary	{
	font-size: 11px;
	line-height: 13px;
	padding: 0;
	}
	
/* to use when need to display twitter status of person */
.ss_status{						
	font-style: italic;
	font-size: 11px;				
	line-height: 1.6em;
	padding: 5px 0 15px 20px;
}
#ss_para {
	}
.list-indent {
	margin-left: 19px;
	}	

#ss_para ul{
	color:#555555;
	}
#ss_para ul li{
	list-style-type: square;
	margin-left:-10px;
	}
#ss_para_activity {
	border: 1px dotted #5691A6;
	padding-left: 15px;
	padding-bottom: 15px;
	color: #555555;
	margin-top: 4px;
	line-height: 1.6em;
	list-style-type: square;
	}
	
/* =========== LINK STYLES===================== */	

/* BACKGROUND HOVER IS FROM THIS CODE */

#ss_para a{
	text-decoration:none!important;
	font-weight: bold;
	}
#ss_para a:visited{
	}	
#ss_para .ss_link_3 a:visited{
	}	
	/* PEOPLE LINKS */
.ss_link_1, .ss_link_1 span { 
	font-size:${ss_style_font_smallprint};
	letter-spacing: -.25px;
	}
	/* PLACES LINKS*/	
.ss_link_2, .ss_link_2 span	{
	font-size:${ss_style_font_smallprint}!important;
	}
	/* ENTRY LINKS*/	
.ss_link_3, .ss_link_3 span	{
	color:#555!important;
	font-size:${ss_style_font_normalprint}!important;
	font-weight: bold !important;
	}
.ss_link_4, .ss_link_4 span{
	text-decoration: none!important;
	color:#888!important;
	font-size:${ss_style_font_smallprint}!important;
	padding-left:6px;
	}
.ss_link_5, .ss_link_5 span	{
	text-decoration: underline!important;
	color:#888!important;
	font-size:${ss_style_font_smallprint}!important;
	line-height:14px;
	}
/*======THIS IS FOR THE DISCUSSION PAGE STYLE========*/
.ss_link_7, .ss_link_7 span {
	font-weight: 400;
	font-family: Arial, Helvetica, sans-serif;
	color:#036f9f !important;
	font-size:1.3em;
	}
.wg-comment .ss_entryContent .ss_link_7 span {
	color:#036f9f !important;
	font-size:15px !important;	
	}	
.ss_link_7 a:hover {
	text-decoration: none;
	color:#036f9f !important;
	}		
.ss_link_8, .ss_link_8 span{
	font-family: ${ss_style_folder_view_font_family};
	color:#036f9f !important;
	font-size:${ss_style_font_normalprint};
	}
				
/* ============ End link styles =========================== */		


/* HEADER STYLES*/
  	
#ss_topper {
	height:150px;
	width:100%;
	}
#ss_BC_nav {
	background-color:#FFF;
	text-align:left;
	padding:3px 0px 3px 20px;
	width:100%;
	position:relative;
	float:left;			
	}
#ss_BC_nav li{
	text-decoration: none;
	display: inline;
	}	
#ss_toolbar_basic{
	position: relative;
	float: right;		
	text-align: right;
	background-color: #cccccc;
	padding: 7px 20px 3px 0px;
	width: 100%;
	height:18px;
	vertical-align: middle;
	color:#FFFFFF;
	}

/* - MENU TABS FOR RELEVANCE--------------------------- */

.ss_tertiaryTabs, #ss_dashboard_content {
	background-color: #f6f6f6;
	border-radius: 5px;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	}
#ss_tabsC {
    width:100%;
    background:transparent;
  /*  line-height:normal; */
  	margin-left: 10px;
    margin-top: 10px;
 	<c:if test="<%= isIE %>">
 	    margin-top:20px !important;
 	</c:if>	    
      }
#ss_tabsC div { 
	margin-right: 4px;
	margin-bottom: 4px;
	}
#ss_tabsC a {
	color: #fff !important;
      }
#ss_tabsC a span {
    display:block;
    background-color: #949494; /* gray */
    padding: 4px 8px;
	font-size: 11px;
    white-space:nowrap !important;
	border-radius: 3px;
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
      }

	 /* Commented Backslash Hack hides rule from IE5-Mac \ */ 
#ss_tabsC a span {float:none;}
    /* End IE5-Mac hack */

#ss_tabsC a:hover {
	color: #036f9f;
	background: #cce9ee;
      }
#ss_tabsC a:hover span {
	color: #036f9f;
	background: #cce9ee;
      }  
#ss_tabsC .ss_tabsC_other {
	margin-bottom: 4px;
	}
#ss_tabsC .ss_tabsCCurrent a {
	color: #000 !important;
	font-weight: bold;
    font-size:100%;
	}
#ss_tabsC .ss_tabsCCurrent {
	margin-bottom: 2px;
	}
#ss_tabsC .ss_tabsCCurrent a span {
	border-bottom: 10px solid #f6f6f6;
	background-color: #f6f6f6;
	-moz-border-radius-topleft: 5px;
	-moz-border-radius-topright: 5px;
	-moz-border-radius-bottomleft: 0px;
	-moz-border-radius-bottomright: 0px;
	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
	border-bottom-left-radius: 0px;
	border-bottom-right-radius: 0px;
	-webkit-border-top-left-radius: 5px;
	-webkit-border-top-right-radius: 5px;
	-webkit-border-bottom-left-radius: 0px;
	-webkit-border-bottom-right-radius: 0px;
        }	
/* PROFILE STYLES*/

.ss_profileData{
	vertical-align: bottom;	
	}
.ss_profileTitle{
	width: 40%;	
	}	

/* FOOTER STYLES*/

#ss_footer{
	float:left;
	overflow:hidden;
	background: #fff;
	padding-bottom: 5px;
	width: 100%;
	margin: 10px auto auto auto;
	height: 60px;
	}	
		
/* HEIGHT OF THE FOOTER */
	
#ss_footer li{
	text-decoration: none;
	display: inline;
	}
#ss_footer a { 
	color: #C00;
	text-decoration:none;
	}
#ss_footer a:hover {
	text-decoration:none;
	background-color:#fff;
	}		
#ss_task_list{
	padding: 0;
	margin: 4px 0 6px 6px;
	color:#333333;
	border:1px solid #A7A9AC;
	}
#ss_task_list p{
	border-top: 1px solid #A7A9AC;
	padding:4px 5px 7px 5px;
	line-height:2em;
	}
#ss_task_list_hdr{
	background-color: #A7A9AC;
	color: #000000;
	font-size:12px;
	padding: 5px 5px 3px 2px;
	width:100%;
	border-right:1px solid #777777;
	margin:0 auto;
	}	
.ss_para_lists{
	margin-top: 4px;
	padding: 2px 4px 4px 5px;
	border: 1px dotted #cccccc;
	}	
.ss_para_lists ul{
	color:#555555;
	}	
.ss_para_lists ul li{
	list-style-type: square;
	margin-left:-10px;
	border-bottom:1px dashed #444444;
	line-height:2.5em;
	padding-bottom:10px;
	padding-right:4px;
	}
#ss_tracker{
	position:absolute;
	display:none;
	visibility:hidden;
	border:1px solid black;
	padding:10px;
	}
/* Calendar need some other settings */
#ss_calendar #ss_para, #ss_calendar .ss_paraC, #ss_calendar #ss_cal_para {
	padding: 0;
}
