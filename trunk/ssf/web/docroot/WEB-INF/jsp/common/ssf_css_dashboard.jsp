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

#ss_top_nav_wrapper{
	width: 100%;
	background-image: url(<html:rootPath/>images/pics/subbanner.png);
	display: table;
	}
	
/* grey buttons for portal-expanded view */	

#ss_top_nav_button{
	position:relative;
	display:block;
	height:20px;
	background-color:#A7A9AC;
	border:solid;
	padding-top:4px;	
	}
#ss_top_nav_buttontwo a {
	font-family:  Arial, sans-serif;
	font-size: 10px;
	background-color: #A7A9AC;
	font-color: white!important;
	display: block;
	font-weight: bold;
	letter-spacing: 0px;
}
#ss_top_nav_buttontwo ul{
	margin:0px;
	padding:0px;
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
	text-decoration:none;
	padding:6px 20px 0px 20px;
	height:18px;
	}
#ss_top_nav_buttontwo ul li a:hover{
	font-color: #white;
	background-color: #CCC;
	border-style: solid;
	border-color: #white;
	border-width: .01em;
	}	
	
/* blue buttons for navigation Not Used */
	
#ss_top_nav_buttontwo ul li a.current{	
	color:#333;
	background-color: #B2CBE7;
	border-style: solid;
	border-color: #white;
	border-width: .01em;
	}
#ss_top_nav_buttonthree a {
	font-family:  Arial, sans-serif;
	font-size: 10px;
	background-color: #7986A1;
	display: block;
	font-weight: bold;
	letter-spacing: 1px;
	}
#ss_top_nav_buttonthree ul{
	margin:0px;
	padding:0px;
	list-style-type:none;
	width:auto;
	}
#ss_top_nav_buttonthree ul li{
	display:block;
	float:left;
	margin:0px 2px 0px 0px;
	}
#ss_top_nav_buttonthree ul li a{
	display:block;
	float:left;
	color:#fff;
	text-decoration:none;
	padding:6px 20px 0px 20px;
	height:20px;
	}
#ss_top_nav_buttonthree ul li a:hover{
	color:#333;
	background-color: #8B9BBA;
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
	background-color: #FFF;
	min-height:100%;
	position:relative;		/* This fixes the IE7 overflow hidden bug */
	clear:both;
	float:left;
    width:100%;			/* width of whole page */
	overflow:hidden;	/* This chops off any overhanging divs */
	text-align:left;
	color:#666666;
	}	
/* 	THE FOLLOWING DEFINES HORZ RULE LINES  */	
.rule_1 {					/* This defines plain line in entries recently visited */
	border:none;
	color:#3D5FA3!important;
	background-color:#3D5FA3!important;
	height:1px;
	width:100%;
	margin-bottom:5%;
	}
.rule_2 {					/* This defines dashed line in entries recently visited */

	border-color:#3D5FA3!important;
	border-top-style:dashed;
	border-top-width:1px;
	width:100%;
	margin-bottom:5%;
	}
.rule_3 {					/* This defines dotted line in entries recently visited */

	border-color:#3D5FA3!important;
	border-top-style:dotted;
	border-top-width:1px;
	width:100%;
	margin-bottom:5%;
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
	background-position: 96% center;
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
	background-position: 97% center;
	}				
/* Box styles */	

#ss_Box{
	padding: 0;
	margin: 1% 0 2% 0;
	}
#ss_networkupdates{
	padding: 0;
	margin: 2% 0 2% 0;
	color:#333333;
	}
#ss_shared{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#FFF;
	border:1px solid #B2CEE7;	
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
#ss_tasks{
	padding: 0;
	margin: 1% 0 5% 0;
	padding: 0.5% 0 15px;
	color: #666666;
	line-height: 1.5em;
	}
#ss_tasks_para {
	line-height:1.25em;
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
#ss_tasks_para li
{
	list-style-type: square!important;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;
}
#ss_trackedItems
{
	padding: 0;
	margin: 1% 0 2% 0;
	color:#444444;

}
#ss_trackedPeople
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#FFFFFF;
	

}
#ss_hints
{
	font-size: 11px;
	font-style: italic;
	padding-left:2px;
	line-height:14px;
	padding-bottom:5px;
	color:#555555;
}

#ss_calendar
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	background-color:#ffffff;
	padding: 0.5% 0 15px;
	color: #666666;
	line-height: 1.5em;
	

}
#ss_visit_para 
{
	line-height:1.25em;
	padding-top:5px;

}
#ss_visit_para li
{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;

}
#ss_visit_para a					/* link style for active links */
{
	text-decoration:underline;
}

#ss_visit_para a:hover					/* link style to hover links */
{
	text-decoration:underline;
	color:#E67814 !important;
	background-color:#CCCCCC !important;
}
#ss_cal_para 
{
	line-height:1.25em;
	padding-top:5px;
	
}
#ss_cal_para li
{

	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #666666;

}
#ss_cal_para a					/* link style for active links */
{
	text-decoration:underline;
}

#ss_cal_para a:hover					/* link style to hover links */
{
	text-decoration:underline;
	color:#E67814 !important;
	background-color:#CCCCCC !important;
}
#ss_mydocs_para 
{
	line-height:1.5em;
	padding-top:5px;

}
#ss_mydocs_para li
{
	list-style-type: square;
	padding-top:3px;
	padding-bottom:3px;
	border-bottom:1px dotted #0C4E84;	/* some kind of blue */

}
#ss_mydocs_para a					/* link style for active links */
{
	text-decoration:underline;
}

#ss_mydocs_para a:hover					/* link style to hover links */
{
	text-decoration:underline;
	color:#E67814 !important;
	background-color:#CCCCCC !important;
}
#ss_whatshot
{
	padding: 0;
	margin: 1% 0 5% 0;
	background-color:#ffffff;
	padding: 0.5% 0 1% 5px;
	color: #666666;
	line-height: 1.5em;
}
#ss_hot_para 
{
	line-height:1.5em;
	padding-top:5px;
}
#ss_hot_para li
{
	list-style-type: square;
	padding-top:3px;	
	padding-bottom:3px;
	border-bottom:1px dotted #0C4E84;	/* some kind of blue */
}


#ss_survey
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #B2CEE7;
	background-color:#ffffff;

}
#ss_notes
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #B2CEE7;
	background-color:#ffffff;

}
#ss_bookmarks
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #B2CEE7;
	background-color:#ffffff;

}
#ss_whatsnew
{
	padding: 0;
	margin: 2% 0 5% 0;
	color:#333333;

}
#ss_personaltracker
{
	padding: 0%;
	margin: 1% 0 5% 0;
	color:#444444;
	border:1px solid #B2CEE7;
	background-color:#ffffff;

}

#ss_email
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#333333;
	font-style:  arial, sans serif;
	font-color: #5799CB;
	line-height: 1.5em;
	

}
#ss_documents
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#333333;

}

#ss_Box_8
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;

}
#ss_Box_9
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;

}
#ss_Box_10
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;

}
#ss_Box_11
{
	padding: 0;
	margin: 1% 0 5% 0;
	color:#444444;

}
#ss_Box_12
{
	padding: 0px;
	margin: 1% 0% 5% 0%;
	color:#444444;

}

#ss_title
{
	
	margin: 0px 0px 1% 0px;
	font-size: 16px;
	font-weight: 400;
	color: #333;
}

#ss_subtitle
{
	padding: 0.5% 0px 1% 6px;
	font-size:14px;
	color:#555555;

}
#ss_para 
{
	margin-top: 1%;
	padding: 0.5% 0 1% 5px;
	border: 1px dotted #cccccc;
	color: #666666;
	line-height: 1.8em;
}
#ss_para a					/* link style for active links */
{
	text-decoration:underline !important;
}

#ss_para a:hover					/* link style to hover links */
{
	text-decoration:underline!important;
	color:#E67814 !important;
	background-color:#CCCCCC!important;
}

#ss_para ul
{
	
	color:#555555;
}
#ss_para ul li
{

	list-style-type: square;
	margin-left:-10px;
}
.ss_pt_title
{

	color: #000000;
	font-size:14px;
	padding: 2px 0px 2px 5px;
	width:100%;
}
.ss_blue
{
	background-color: #B2CEE7;
}
.ss_orange
{
	background-color: #F93;
}

.ss_pt_para 
{ 	margin: 1%; 
}
.ss_pt_para ul
{
	
	color:#555555;
}
.ss_pt_para ul li
{
	margin-left: -10px;
	list-style-type: disc;
	border-bottom:1px dotted #666666;
	
}
.ss_prioValue
{
	font-weight:bold;
	padding:0 3px 0 3px;
}


/* time block styles */	
#ss_today 
{
	margin: 0px 0px 10px 10px;
}

#ss_yesterday
{
	margin: 0px 0px 0px 10px;
}
#ss_todayC                     /* this style used to add background color to tasks and calendar */
{
	margin: 0px 0px 0px 10px;	

}
#ss_lastweek
{
	margin: 0px 0px 0px 10px;
}
 .ss_closed				/* use when a box is compressed */	
{
	height:20px;
}
 .ss_overdue				/* when a milestone is overdue */	
{
	color: #CC0000;
	font-style: italic;
}	


/* ======================BACKGROUND HOVER IS FROM THIS CODE======================== */

#ss_para a{
	text-decoration:underline!important;
	}
#ss_para a:hover{
	text-decoration:underline!important;
	color:#E67814 !important;
	background-color:#CCC!important;
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
.ss_link_1, .ss_link_1 span 	/* link_1 Link style for people links */ 
	{ 
	color: #00AEEF!important;
	text-decoration: underline !important;
	font-style: bold !important;
	text-background: #444444 !important; 
	}

	
.ss_link_2, .ss_link_2 span		/* link_2 Link style for places links */	
	{
	text-decoration: underline!important;
	color:#204E1D!important;
	font-size:10px!important;
	}
.ss_link_3, .ss_link_3 span		/* link_3 Link style for entry links */	
	{
	text-decoration: underline!important;
	color:#333333!important;
	font-size:12px!important;
	}
.ss_link_4, .ss_link_4 span		/* link_4 Link style for entry links */	
	{
	text-decoration: none!important;
	color:#333333!important;
	font-size:9px!important;
	padding-left:6px;
	}		
	
/* ============ End link styles =========================== */		


/* Header styles */
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
	
	#ss_BC_nav li
	{
		text-decoration: none;
		display: inline;
	
	}	
	#ss_toolbar_basic
	{
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

/* - Menu Tabs for Relevance--------------------------- */

    #ss_tabsC {
      float:left;
      width:100%;
      background:#EDF7E7;
      font-size:93%;
      line-height:normal;
      border-bottom: 1px #A7A9AC solid;
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
      background:url("<html:rootPath/>images/pics/tableftC.gif") no-repeat left top;
      margin:0;
      padding:0 0 0 4px;
      text-decoration:none;
      }
    #ss_tabsC a span {
      float:left;
      display:block;
      background:url("<html:rootPath/>images/pics/tabrightC.gif") no-repeat right top;
      padding:5px 15px 4px 6px;
      color:#464E42;
      }
    /* Commented Backslash Hack hides rule from IE5-Mac \ */
    #ss_tabsC a span {float:none;}
    /* End IE5-Mac hack */
    #ss_tabsC a:hover span {
      color:#FFF;
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
		

	
	/* column container */
#ss_dashboard_content {
	    position:relative;		/* This fixes the IE7 overflow hidden bug */
	    clear:both;
	    float:left;
        width:100%;				/* width of whole page */
		overflow:hidden;		/* This chops off any overhanging divs */
	}
	/* common column settings */
	.ss_colright,
	.ss_colmid,
	.ss_colleft {
		float:left;
		width:100%;				/* width of page */
		position:relative;
	}
	.ss_col1,
	.ss_col2,
	.ss_col3 {
		float:left;
		position:relative;
		padding:1em 0 1em 0;		/* no left and right padding on columns, we just make them narrower instead 
								only padding top and bottom is included here, make it whatever value you need */
		overflow:hidden;
	}
	/* 3 Column page style settings */
	.ss_tricolumn				/* 3 Column page style */
	{
		background-color: #EDF7E7;
		/* right column background color */

	} 
	.ss_tricolumn .ss_colmid
	{
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
	/* Footer styles */

	#ss_footer
	{
		float:left;
		overflow:hidden;
		background: #fff;
		position: relative;
		padding-bottom: 5px;
		width: 100%;
		margin: 10px auto auto auto;
		height: 60px;
		/* Height of the footer */
	}
	#ss_footer li
	{
		text-decoration: none;
		display: inline;
	
	}
	#ss_footer a 
	{ 
	color: #C00;
	text-decoration:none;
	}
	
	#ss_footer a:hover {
		color:#444444;
		text-decoration:none;
		background-color:#fff;
	}	
	
	#ss_task_list
{
	padding: 0;
	margin: 1% 0 2% 2%;
	color:#333333;
	background-color:#FFF;
	border:1px solid #B2CEE7;
}

	#ss_task_list p
{
	border-top: 1px solid #B2CEE7;
	padding:4px 5px 7px 5px;
	line-height:2em;

}
	#ss_task_list_hdr
{
	background-color: #B2CEE7;
	color: #000000;
	font-size:12px;
	padding: 5px 5px 3px 2px;
	width:100%;
	border-right:1px solid #777777;
	margin:0 auto;

}

	.ss_para_lists
{
	margin-top: 1%;
	padding: 0.5% 1% 1% 5px;
	border: 1px dotted #cccccc;

}
	.ss_para_lists ul
{
	
	color:#555555;
}
	.ss_para_lists ul li
{

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

