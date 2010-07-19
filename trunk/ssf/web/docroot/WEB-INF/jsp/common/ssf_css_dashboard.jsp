<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
	float: left;
	padding: 0 0 1em 0; 
	margin: 0px 2px 0px 2px;
	overflow: hidden;
	}
.col-nextback-but {
	text-align: right;
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
	width:20%;				/* Width of right column content (column width minus padding on either side) */
	left:44%;				/* (100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	padding: 5px 5px 0px 5px;
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
.ss_tracked_img {
	background-image: url("<html:rootPath/>images/pics/tracking20x16.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}	
.ss_recentfolder_img {
	background-image: url("<html:rootPath/>images/pics/folder_blue_recent.png");
	background-repeat: no-repeat;
	background-position: 95% center;
	}			
/* BOX STYLES*/	

#ss_Box{
	padding: 0;
	margin: 1% 0 2% 0;
	}	
	/*SHARED*/
#ss_networkupdates{
	padding: 0;
	margin: 2% 0 2% 0;
	color:#444444;
	}
#ss_shared{
	margin: 0% 0 5% 0;
	<c:if test="<%= isIE %>">
 	margin: 3px 0 5% 0;
 	</c:if>	
	color:#444444;
	line-height:1.6em;
	}
.ss_shared_para {
	line-height:1.6em;
	padding-top:5px;
	padding-left:1%;
	}
 .ss_shared_para li{
	list-style-type: square;
	padding-bottom:5px;
	}
#ss_shared_para li{
	list-style-type: square;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;	
	}
	/*MINIBLOG*/
#ss_blogs{
	margin: 0% 0 5% 0;
	<c:if test="<%= isIE %>">
 	margin: 3px 0 5% 0;
 	</c:if>	
	color:#444444;
	line-height:1.6em;
	}
 	/*TASKS*/
#ss_tasks{
	margin: 1% 0 5% 0;
	padding: 0.5% 0 15px;
	color: #666666;
	line-height: 1.6em;
	}
#ss_tasks_para {
	line-height:1.6em;
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
	border-bottom:dotted 1px #5691A6;
	margin:5px;
	padding-right: 5px;
	
	}
#ss_col3_para {			/* to use for 3rd column displays */
	padding-top:5px;
	margin:5px 0 5px 4px;
	}			
	/*TRACKED ITEMS*/	
#ss_trackedItems{
	margin: 1% 0 5% 0;
	padding: 0% 0 1% 0;
	color:#555;
	}
#ss_trackedPeople{
	color:#555;
	background-color:transparent;
	line-height: 1.6em;
	}
#ss_im_status{
	font-size: 11px;
	line-height: 13px;
	padding: 5px;
	background: #FFF;
	-moz-border-radius: 3px;
	-webkit-border-radius: 3px;
	}
.ss_im_status_active{
	font-weight: bold;
	background: #FFFBD6!important;	
	}					
	/*CALENDAR*/
#ss_calendar{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	padding: 0.5% 0 0px;
	color: #555555;
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
	/* HOVER LINKS */
#ss_visit_para a:hover{
	text-decoration:underline;
	color:#464E42 !important;
	background-color:#CCCCCC !important;
	}
	
	/*VIEWED ITEMS*/
#ss_viewedItems {
	line-height:1.2em;
	padding: 5px;
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
	/* ACTIVE HOVER LINKS */
#ss_viewedItems a:hover{
	text-decoration:underline;
	color:#555 !important;
	background-color:#CCCCCC !important;
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
	/* CALENDAR HOVER LINKS */
#ss_cal_para a:hover{
	text-decoration:underline;
	color:#464E42 !important;
	background-color:#CCCCCC !important;
	}
	/*MY DOCUMENTS*/
#ss_mydocs_para {
	line-height:1.2em;
	}
#ss_mydocs_para div.item, .ss_v_entries div.item, .ss_viewedItems div.item {
	padding-top: 2px;
	padding-bottom: 8px;
	border-top: 2px solid #fff;
	border-bottom: 1px solid #e0e0e0;
	}
div.item-sub a {
	font-weight: normal !important;
	}	
	/* ACTIVE LINK */
#ss_mydocs_para a {					
	text-decoration:underline;
	}
	/* HOVER LINK */
#ss_mydocs_para a:hover{
	text-decoration:underline;
	color:#464E42 !important;
	background-color:#CCCCCC !important;
	}
	/* WHATS HOT */
#ss_whatshot{
	margin: 1% 0 5% 0;
	padding: 0% 0 1% 0;
	color: #666666;
	line-height: 1.6em;
	}
#ss_hot_para {
	line-height:1.2em;
	padding-top:1px;
	}
#ss_hot_para li{
	list-style-type: square;	
	border-bottom:1px dotted #5691A6;
	margin:0pt 0pt 0pt 15px;
	padding-bottom: 1.6em;
	}	
	
	/* SURVEY*/
#ss_survey{
	margin: 1% 0 5% 0;
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
	-webkit-border-radius: 3px;
	}
	/* NOTES */
#ss_notes{
	margin: 1% 0 5% 0;
	color:#555;
	border:1px solid #5691A6;
	}
	/* BOOKMARKS */
#ss_bookmarks{
	margin: 1% 0 5% 0;
	color:#555;
	}
	/* ACTIVITIES */
.ss_activity{
	margin: 1% 1% 5% 15px;
	color:#555;
	}
	/* WHATS NEW */
#ss_whatsnew{
	margin: 1% 0 5% 0;
	color:#555;
	}
	/* PERSONAL TRACKER */
#ss_personaltracker{
	margin: 1% 0 5% 0;
	color:#555;
	border:1px solid #A7A9AC
	}
	/* EMAIL */
#ss_email{
	margin: 1% 0 5% 0;
	color:#555;
	font-family:  Arial, sans serif;
	line-height: 1.5em;
	padding: 0;
	}
	/* PEOPLE */
#ss_people{
	margin: 1% 0 5% 0;
	color:#555;
	}
	/* DOCUMENTS */
#ss_documents{
	margin: 1% 0 5% 0;
	color:#555;
	}
#ss_title{
	margin: 0px 0px 2px 0px;
	font-size: 16px;
	font-weight: 500;
	color: #555;
	font-family: Arial, Helvetica, sans-serif;
	}
.ss_title_im{
	background: #FFFFFF;
	}	
#ss_subtitle{
	font-size:14px;
	color:#555555;
	padding: 0.5% 0 1% 6px;
	}

/*  BOX TITLES */
.ss_pt_title{
	color: #555;
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
	margin: 1%; 
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
	padding-left: 15px;	
	}
.ss_paraD{                    
	background-color:transparent;
	padding-bottom: 15px;
	}	
/* TIMEBLOCK STYLES */	
#ss_today {
	margin: 0 0 0 0;
	padding: 5px;
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
	line-height: 12px;
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
	padding-left: 6px;
	padding-bottom: 15px;
	color: #555555;
	margin-top: 2px;
	margin-right: 6px;
	line-height: 1.2em;
	background-color: #f6f6f6;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	}
	
.list-indent {
	margin-left: 22px;
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
	margin-top: 1%;
	line-height: 1.6em;
	list-style-type: square;
	}
	
/* =========== LINK STYLES===================== */	

/* BACKGROUND HOVER IS FROM THIS CODE */

#ss_para a{
	text-decoration:none!important;
	font-weight: bold;
	}
#ss_para a:hover{
	color:#555!important;
	background:#CCC!important;
	}
#ss_para a:visited{
	}	
#ss_para .ss_link_3 a:visited{
	}	
	/* PEOPLE LINKS */
.ss_link_1, .ss_link_1 span { 
	font-size:${ss_style_font_normalprint};
	letter-spacing: -.25px;
	}
	/* PLACES LINKS*/	
.ss_link_2, .ss_link_2 span	{
	font-size:${ss_style_font_smallprint}!important;
	text-decoration: underline!important;
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
	color:#5691A6 !important;
	font-size:18px;
	}
.ss_link_7 a:hover {
	text-decoration: none;
	color:#135c8f !important;
	}		
.ss_link_8, .ss_link_8 span{
	font-family: ${ss_style_folder_view_font_family};
	color:#5691A6!important;
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

.ss_tertiaryTabs {
	background-color: #eaebe8; /* Gray 8 */
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
	}
#ss_tabsC a {
	color: #fff;
      }
#ss_tabsC a span {
    display:block;
    background-color: #949494; /* gray */
    padding: 5px 10px;
    white-space:nowrap !important;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
      }

	 /* Commented Backslash Hack hides rule from IE5-Mac \ */ 
#ss_tabsC a span {float:none;}
    /* End IE5-Mac hack */

#ss_tabsC a:hover {
	color: #fff;
	background: #81b2bd url(<html:rootPath/>css/images/main/slice_blend_teal_27.png) repeat-x;
      }
#ss_tabsC a:hover span {
	color: #fff;
	background: #81b2bd url(<html:rootPath/>css/images/main/slice_blend_teal_27.png) repeat-x;
      }  
#ss_tabsC .ss_tabsC_other {
	margin-bottom: 5px;
	}
#ss_tabsC .ss_tabsCCurrent a {
	color: #000;
	font-weight: bold;
    font-size:100%;
	}
#ss_tabsC .ss_tabsCCurrent a span {
	border-bottom: 10px solid #eaebe8;
	background-color: #eaebe8;
	-moz-border-radius-topleft: 5px;
	-moz-border-radius-topright: 5px;
	-moz-border-radius-bottomleft: 0px;
	-moz-border-radius-bottomright: 0px;
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
	color:#444444;
	text-decoration:none;
	background-color:#fff;
	}		
#ss_task_list{
	padding: 0;
	margin: 1% 0 2% 2%;
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
	margin-top: 1%;
	padding: 0.5% 1% 1% 5px;
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
	padding-right:1%;
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

