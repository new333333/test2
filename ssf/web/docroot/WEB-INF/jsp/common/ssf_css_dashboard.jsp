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

/* CSS Document - container for navigation and search */
	
	/*WRAPPER*/
#ss_top_nav_wrapper{
	width: 100%;
	background-color:#EBEBEA;
	display: table;
	border-color:#CCCCCC;
	border: 1px;
	}
	
	/* COLUMN CONTAINER */
#ss_dashboard_content {
	position:relative; /* This fixes the IE7 overflow hidden bug */
	clear:both;
	float:left;
    width:100%;	/* width of whole page */
	overflow:hidden; /* This chops off any overhanging divs */
	}

/* COMMON COLUMN SETTINGS */
.ss_colright,.ss_colmid,.ss_colleft{
	float:left;
	width:100%; /* width of page */
	position:relative;
	}
.ss_col1,.ss_col2,.ss_col3 {
	float:left;
	position:relative;
	padding:1em 0 1em 0; /* no left and right padding on columns, we just make them narrower instead 
	only padding top and bottom is included here, make it whatever value you need */
	overflow:hidden;
	}	
/* 3 COLUMN PAGE STYLE SETTINGS */
.ss_tricolumn{	/* 3 Column page style */
	background-color: #C9CFE0; /* right column background color */
	} 
.ss_tricolumn .ss_colmid{
	right: 20%;
	/* width of the right column */
	/* center column background color */
	background-color: #FFFFFC;
	}
.ss_tricolumn .ss_colleft {
	right:20%;				/* placement from right of the middle column */
	background:#fff;		/* left column background color */
	}
.ss_tricolumn .ss_col1 {
	width:38%;				/* width of center column content (column width minus padding on either side) */
	left:42%;				/* 100% plus left padding of center column */
	}
.ss_tricolumn .ss_col2 {
	width:38%;				/* Width of left column content (column width minus padding on either side) */
	left:43%;				/* width of (right column) plus (center column left and right padding) plus (left column left padding) */
	}
.ss_tricolumn .ss_col3 {
	width:18%;				/* Width of right column content (column width minus padding on either side) */
	left:45%;				/* (100% - left column width) plus (center column left and right padding) plus (left column left and right padding) plus (right column left padding) */
	}
	/* NAVIGATION BUTTONS -MOVE */	

#ss_top_nav_button{
	position:relative;
	display:block;
	height:16px;
	background-color:#A7A9AC;
	border:solid;
	padding-top:6px;	
	}
#ss_top_nav_buttontwo a {
	font-family:  Arial, sans-serif;
	font-size: 10px;
	background: #8B9BBA;
	color: #FFF!important;
	display: block;
	font-weight: bold;
	letter-spacing: 0px;
}
#ss_top_nav_buttontwo ul{
	margin:0px;
	padding:0px;
	padding-left:10px;
	list-style-type:none;
	width:auto;
	}
#ss_top_nav_buttontwo ul li{
	display:block;
	float:left;
	margin:6px 3px 0px 0px;
	}
#ss_top_nav_buttontwo ul li a{
	display:block;
	float:left;
	color:#333;
	border-style: solid;
	border-color: white;
	border-width: .01em;
	padding:5px 15px 0px 15px;
	height:16px;
	}
#ss_top_nav_buttontwo ul li a:hover{
	color: #FFF;
	background-color: #CCC;
	border-style: solid;
	border-color: #FFF;
	border-width: .01em;
	}	
#ss_top_nav_buttontwo ul li a.current{	
	color:#333;
	background-color: #B2CBE7;
	border-style: solid;
	border-color: #FFF;
	border-width: .01em;
	}
#ss_top_nav_view{					/* "buttons" for portal and expanded view */
	position:relative;
	display:inline;
	height:16px;
	padding-top:6px;	
	}
#ss_top_nav_view a {
	font-family:  Arial, sans-serif;
	font-size: 10px;
	letter-spacing: 0px;
}
#ss_top_nav_view ul{
	margin:0px;
	padding:0px;
	list-style-type:none;
	width:auto;
	}
#ss_top_nav_view ul li{
	display:inline;
	margin:6px 3px 0px 0px;
	width:100%;
	}
#ss_top_nav_view ul li a{
	float:left;
	color:#333;
	padding:5px 15px 0px 15px;
	height:16px;
	}
#ss_top_nav_view ul li a:hover{
	color: #EF2121!important;
	}	
	
#ss_top_nav_view ul li a.current{	
	color:#526394!important;
	}
	
div.ss_canvas {
	border-left: solid 1px ${ss_toolbar1_background_color};
	border-right: solid 1px ${ss_toolbar1_background_color};
	border-bottom: solid .3px ${ss_toolbar1_background_color};
	padding-bottom: 0px;
	margin-bottom: 10px;
	background-color: ${ss_style_background_color_side_panel};
	}
#ss_wrap{
	margin: auto;
	background-color: #FFFFFF;
	min-height:100%;
	position:relative;		/* This fixes the IE7 overflow hidden bug */
	clear:both;
	float:left;
    width:100%;			/* width of whole page */
	overflow:hidden;	/* This chops off any overhanging divs */
	text-align:left;
	color:#666666;
	}	
		
/*  THE FOLLOWING DEFINES ALL THE IMAGES FOR THE BLUE BARS */
	
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
/* BOX STYLES*/	

#ss_Box{
	padding: 0;
	margin: 1% 0 2% 0;
	}	
	/*SHARED*/
#ss_networkupdates{
	padding: 0;
	margin: 2% 0 2% 0;
	color:#333333;
	}
#ss_shared{
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#FFFFFF;
	border:1px solid #A7A0AC;	
	line-height:1.25em;
	}
.ss_shared_para {
	line-height:1.25em;
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
 	/*TASKS*/
#ss_tasks{
	margin: 1% 0 5% 0;
	padding: 0.5% 0 15px;
	color: #666666;
	line-height: 1.6em;
	}
#ss_tasks_para {
	line-height:1.6em;
	padding-top:5px;
	}
#ss_tasks_para .ss_link_1 a:{
	text-decoration:underline!important;
	color: #00AEEF!important; 
	}
#ss_tasks_para .ss_link_2 a:{
	text-decoration:underline!important;
	color:#4AAA42!important;
	font-size:10px!important; 
	}
#ss_tasks_para {
	list-style-type: square!important;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;
	}
	/*TRACKED ITEMS*/
#ss_trackedItems{
	margin: 1% 0 2% 0;
	color:#444444;
	}
#ss_trackedPeople{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#CCD2E3;
	line-height: 1.6em;
	}
#ss_hints{
	font-size: 11px;
	font-style: italic;
	padding-left:2px;
	line-height:14px;
	padding-bottom:5px;
	color:#555555;
	}		
	/*CALENDAR*/
#ss_calendar{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#ffffff;
	padding: 0.5% 0 0px;
	color: #666666;
	line-height: 1.5em;
	}
#ss_visit_para {
	line-height:1.25em;
	padding-top:5px;
	}
#ss_visit_para li{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;}
	
	/* ACTIVE LINKS - VISITOR */
#ss_visit_para a{
	text-decoration:underline;
}
	/* HOVER LINKS */
#ss_visit_para a:hover{
	text-decoration:underline;
	color:#464E42; !important;
	background-color:#CCCCCC !important;
	}
	
	/*VIEWED ITEMS*/
#ss_viewedItems {
	line-height:1.4em;
	}
#ss_viewedItems .ss_v_entries ul{
	list-style-type: none;
	padding: 0;
	margin-left: 4px;
	}
#ss_viewedItems .ss_v_entries li{
	background-image: url("<html:rootPath/>images/pics/entry_icon.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	padding-left: 30px;
	padding-top: 3px;
	padding-bottom: 3px;
	border-bottom:1px dotted #666666;}
	}
	/* ACTIVE LINKS - VIEWED ITEMS*/	
#ss_viewedItems a{
	text-decoration:underline;
	}
	/* HOVER LINKS */
#ss_viewedItems a:hover{
	text-decoration:underline;
	color:#464E42;!important;
	background-color:#CCCCCC !important;
	}
	/*CALENDAR*/
#ss_cal_para{
	padding-top:5px;
	}
#ss_cal_para li{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;
	}
	/* CALENDAR ACTIVE LINKS */
#ss_cal_para a{
	text-decoration:underline;
	}
	/* HOVER LINKS */
#ss_cal_para a:hover{
	text-decoration:underline;
	color:#464E42; !important;
	background-color:#CCCCCC !important;
	}
	/*MY DOCUMENTS*/
#ss_mydocs_para {
	line-height:1.5em;
	padding-top:5px;
	}
#ss_mydocs_para li{
	list-style-type: square;
	padding-bottom:0px;
	border-bottom:1px dotted #0C4E84;
	}
	/* ACTIVE LINK */
	#ss_mydocs_para a					
	text-decoration:underline;{
	}
	/* HOVER LINK */
#ss_mydocs_para a:hover{
	text-decoration:underline;
	color:#464E42; !important;
	background-color:#CCCCCC !important;
	}
	/* WHATS HOT */
#ss_whatshot{
	margin: 1% 0 5% 0;
	background-color:#FFFFFF;
	padding: 0.5% 0 1% 5px;
	color: #666666;
	line-height: 1.6em;
	}
#ss_hot_para {
	line-height:1.5em;
	padding-top:5px;
	}
#ss_hot_para li{
	list-style-type: square;
	padding-top:3px;	
	padding-bottom:3px;
	border-bottom:1px dotted #0C4E84;	
	}
	/* SURVEY*/
#ss_survey{
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #A7A9AC;
	background-color:#ffffff;
	}
	/* NOTES */
#ss_notes{
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #A7A9AC;
	background-color:#ffffff;
	}
	/* BOOKMARKS */
#ss_bookmarks{
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#ffffff;
	}
	/* WHATS NEW */
#ss_whatsnew{
	margin: 2% 0 5% 0;
	color:#333333;
	}
	/* PERSONAL TRACKER */
#ss_personaltracker{
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #A7A9AC;
	background-color:#ffffff;
	}
	/* EMAIL */
#ss_email{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#333333;
	font-family:  Arial, sans serif;
	color: #5799CB;
	line-height: 1.5em;
	}
	/* DOCUMENTS */
#ss_documents{
	margin: 1% 0 5% 0;
	color:#333;
	}
#ss_title{
	margin: 0px 0px 2px 0px;
	font-size: 14px;
	font-weight: 400;
	color: #333;
	}
#ss_subtitle{
	padding: 0.5% 0px 1% 6px;
	font-size:14px;
	color:#555555;
	}
#ss_para {
	margin-top: 1%;
	padding-left: 15px;
	border: 1px dotted #cccccc;
	color: #666666;
	line-height: 1.6em;
	}
#ss_para ul{
	color:#555555;
	}
#ss_para ul li{
	list-style-type: square;
	margin-left:-10px;
	}
	
/*  BOX TITLES */
.ss_pt_title{
	color: #000000;
	font-size:14px;
	padding: 2px 0px 2px 5px;
	width:100%;
	}
/*  BOX TITLE COLORS   */
.ss_blue{
	background-color: #CCD2E3;
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
	border-bottom:1px dotted #666666;	
	}
.ss_prioValue{
	font-weight:bold;
	padding:0 3px 0 3px;
	}
/* BACKGROUND COLORS */	
.ss_paraC                     {
	background-color:#DEE7EE;
	}	
/* time block styles */	
#ss_today {
	margin: 0px 0px 10px 10px;
	}
#ss_yesterday{
	margin: 0px 0px 0px 10px;
	}
#ss_lastweek{
	margin: 0px 0px 0px 10px;
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

/* ======================BACKGROUND HOVER IS FROM THIS CODE======================== */

#ss_para a{
	text-decoration:underline!important;
	}
#ss_para a:hover{
	text-decoration:underline!important;
	color:#ECB456 !important;
	background:#CCC!important;
	}	
#ss_para .ss_link_3 a:visited
	{
	text-decoration:underline!important;
	color:#666666 !important;
	}	
	
/* =================THESE NEED TO BE CONSOLIDATED======== */


/* to use when need to display summary of an entry */

.ss_summary	{
	padding: 0px 0px 0px 0px;
	font-style: italic;
	color: #555555;
	font-size: 11px;
	line-height: 12px;
	}
.ss_status						/* to use when need to display twitter status of person */
{
	padding: 0px 0px 0px 15px;
	font-style: italic;
}

	
/* =========== LINK STYLES===================== */	
	/* PEOPLE LINKS */
.ss_link_1, .ss_link_1 span { 
	text-decoration: underline;
	color: #00AEEF!important;
	font-background: #444444 !important; 
	}
	/* PLACES LINKS*/	
.ss_link_2, .ss_link_2 span	{
	text-decoration: underline!important;
	color:#4AAA42!important;
	font-size:10px!important;
	}
	/* ENTRY LINKS*/	
.ss_link_3, .ss_link_3 span	{
	text-decoration: underline!important;
	color:#333333!important;
	font-size:12px!important;
	font-weight: bold !important;
	}
.ss_link_4, .ss_link_4 span{
	text-decoration: none!important;
	color:#999999!important;
	font-size:9px!important;
	padding-left:6px;
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

#ss_tabsC {
    float:left;
    width:100%;
    background:#D4DAE7;
    font-size:93%;
    line-height:normal;
    border-bottom: 1px solid #CCCCCC;
      }
 #ss_tabsC ul {
    margin:0;
    padding:10px 10px 0 20px;
    list-style:none;
      }
#ss_tabsC li {
    display:inline;
    margin:0;
   	padding:0;
      }
 #ss_tabsC a {
    float:left;
    background:url("<html:rootPath/>images/pics/tableft7.gif") no-repeat left top;
    margin:0;
    padding:0 0 0 4px;
      }
 #ss_tabsC a span {
    float:left;
    display:block;
    background:url("<html:rootPath/>images/pics/tabright7.gif") no-repeat right top;
    padding:5px 15px 4px 6px;
    color:#464E42;
      }
 /* Commented Backslash Hack hides rule from IE5-Mac \ */
 
 #ss_tabsC a span {float:none;}
    /* End IE5-Mac hack */
#ss_tabsC a:hover span {
    color:#ECB546;
      }
#ss_tabsC a:hover {   
      background-position:0% -42px;
      }
#ss_tabsC a:hover span {
      background-position:100% -42px;
      }
#ss_tabsC .ss_tabsCCurrent a {
      background-position:0% -42px;
        }
#ss_tabsC .ss_tabsCCurrent a span {
     background-position:100% -42px;
        }
	
	
/* FOOTER STYLES*/

#ss_footer{
	float:left;
	overflow:hidden;
	background: #fff;
	position: relative;
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
	background-color:#FFF;
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
	background-color:#ffffff;
}

