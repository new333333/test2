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

/* CSS Document - container for two column pages */
	

/* 2 COLUMN PAGE STYLE SETTINGS */	

.ss_doublecolumn{
	background-color:#fff;
	text-align:left;
	}
#ss_column_L{
	/* holder for left column */
	vertical-align:top;
	}
#ss_column_R{
	/* holder for left column */
	height:100%;
	vertical-align:top;
	}	
.ss_doublecolumn .ss_dblcolleft{		/* right column width */
	right:49%;		
	}
.ss_doublecolumn .ss_dblcol1{
	width:49%;						/* left column content width (column width minus left and right padding) */
	background-color:#fff;			/* left column background color */
	position:relative;
	float:left;
	margin-right:2%;
	}	
.ss_doublecolumn .ss_dblcol2{
	width:100%;						/* right column content width (column width minus left and right padding) */
	left:51%;						/* (right column column width) plus (left column left and right padding) plus (right column left padding) */
	overflow:hidden;
	}	
	
/* TOP PART OF DISCUSSION TOPIC PAGE */	
	
	#ss_diss_inset{
		width:80%;
		text-align:left;
	}
	#ss_diss_top
	{
		/*clear:both;
		float: left;*/
		width: 100%;
		color: #777;
			}	
	#ss_topic_box { 
		margin: 1% 0 1% 0;
		border: 1px #CCCCCC solid; 
		padding: 0% 5% 3% 3%;
		font-size: 12px;
		font-style: italic;
		font-family: 'Lucida Sans', 'Lucida Grande', sans-serif;
		line-height: 18px;
		text-align:left;
		vertical-align:top;
		background-color:#f2f2f2;
		}
	#ss_topic_box_h1{
		margin: 1% 0 1% 0;
		font-size: 20px;
		font-style: italic;
		text-weight: 200;
		font-family:'Lucida Sans', 'Lucida Grande', sans-serif;
		letter-spacing:-2px;
		color:#5A9A98!important;
	}
	
	#ss_topic_folder
	{
		margin-left:1%;
		padding-top:0px;
		padding-bottom:4px;
		font-size: 16px;
		font-style: italic;
		font-weight:bold;
		color: #00ADef;
		text-align:left;
	}
	
	.ss_topic_folder_thread{	
		color: #526394;
		font-size:14px;
		font-style:normal;
	}

/**********************************/	

/* DISCUSSION TOPIC FOLDER */	

#ss_folder_inset{
	margin:0% 5% 0% 5%;
	width:90%;
	text-align:left;
	}

#ss_topic{
	margin: 2% 25% 2% 5px;
	color:#555;
	font-family: Arial, Helvetica, sans-serif;
	}
#ss_topic_title{
	padding-left: 28px;
	padding-top: 2px;
	padding-bottom: 1px;
	font-size:1.2em;
	font-style:italic;
	font-weight: 200;
	font-family: 'Lucida Sans', 'Lucida Grande', sans-serif;	
	color:#5A9A98!important;
	}
#ss_title_th1{
	color:#5A9A98!important;
	font-size:.89em;
	letter-spacing:-2;
	font-weight:300;
	}
.ss_title_count{
	color:#777!important;
	font-size:10px;
	font-style: normal;
	}
.ss_title_count a{
	color:red!important;
	font-size:10px;
	font-style: normal;
	}
.ss_title_count a:hover{
	color:#777!important;
	font-size:10px;
	font-style: normal;
	}
#ss_topic_title span{
	color:#5A9A98;
}
#ss_topic_nav{
	text-align:left;
	margin:1%;
	}
.ss_disc_next {	
	background-color:#6B737B;
	color:#FFFFFF;
	font-weight:bold;
	font-size:10px;
	}
/* TOPIC FOLDER THEMES */

.ss_disc_th1{
	background-image: url("<html:rootPath/>images/pics/discussion/blue_topic.png");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	color: #00ADef;
}
.ss_disc_sub_th1{
	background-image: url("<html:rootPath/>images/pics/discussion/blue_topic.png");
	background-repeat: no-repeat;
	background-position: 4% 1%!important;
	color: #00ADef;
}
.ss_disc_folder_th1{
	background-image: url("<html:rootPath/>images/icons/folder_blue.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	color: #00ADef!important;
}

.ss_disc_th2
{
	background-image: url("<html:rootPath/>images/novell/icons/workspace.gif");
	background-repeat: no-repeat;
	background-position: 1% 1%;
	color: #EF9418;
}

#ss_topic_desc{
	font-size:11px;
	line-height:13px;
	padding-left:px;
	padding-right:2px;
	padding-bottom:1px;
	color:#777;
	font-style: italic;
	margin-right:5px;
	margin-bottom:5px;
	font-weight: 500;
	}
#ss_topic_thread{
	font-size:12px;
	line-height:15px;
	padding-left:15px;
	padding-right:2px;
	font-weight: 800;
	color:#777;
	margin-right:5px;
	margin-bottom:2px;
	}	
#ss_topic_thread a{
	color: #555;
	}		
#ss_topic_thread a:hover{
	color: #CCC !important;
	}
/**********************************/

/*  DISCUSSION TOPIC TABLE STYLES */

.ss_table_data
{
	font-size:10px;
	border-color:#526394;
	border-width:1px;
	border-style:solid;
}
.ss_topic_status {
	font-size:10px;
	font-weight:bold;
	border-style:hidden;
	border:1px solid #ffffff;
	border-bottom:1px solid #526394;
	}	
.ss_topic_table   {
 	border-collapse: collapse;
	font-size:10px;
	border-color:#526394;
	border-width:1px;
	border-style:solid;
	}	
	/* DISCUSSION THREAD LINKS */
.ss_link_6, .ss_link_6 span {
	text-decoration: underline!important;
	color:#333333!important;
	font-size:10px!important;
	}

/****  DISCUSSION THREAD IMAGES ********/
	
	a.ss_new_thread img {
	width: 12px;
	height: 12px;
	margin: 0px;
	padding: 0px 0 5px 3px;
	border: 0px;
	vertical-align: text-bottom;
}

a.ss_new_thread img, a.ss_new_thread:link img , a.ss_new_thread:focus img, a.ss_new_thread:visited img { 
	background: transparent; 
}

a.ss_new_thread:hover img {
    background-position:  left -12px; 
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}
a.ss_new_thread:hover {
<ssf:ifaccessible>
  outline: dotted 1px gray;
</ssf:ifaccessible>  
}
.ss_nowrapFixed {
	white-space:normal!important;
	}
.ss_sliding {
	white-space:nowrap!important;
	}	
#ss_configureCol {
	margin-left:20px;
	padding-bottom:10px;
	}
	
/**** INLINE NAVBAR STYLE ************/

#ss_navbar_inline {
	margin:0px;
	padding:1px 0px 0px 0px;
	<c:if test="<%= isIE %>">
 		padding-top:3px;
 		padding-bottom:3px;
 	</c:if>
	}
#ss_navbar_inline ul{
	margin:0px !important;
	}	
#ss_navbar_inline ul li{
	display: inline!important;
	white-space: nowrap;
	margin-right: 10px!important;
	}
#ss_navbar_inline ul a{
	color:#666666;
	}	
#ss_navbar_inline ul a:hover{
	color:#0C9;
	font-weight:bold;
	}
#ss_navbar_inline ul a:active{
	font-weight:bold;
	color:#C00000;
	}	
.ss_navbar_current {
	font-weight: bold !important;
	border:solid 1px #999999 !important; /* text-decoration: underline; */
	padding:2px 5px 2px 5px;
	}	
.ss_navbar_new {
	text-align: center;
	font-weight: bold !important;
	border:solid 1px #FFFFFF !important; /* text-decoration: underline; */
	padding:2px 5px 2px 5px;
	background-color: #828282 !important;
	
	}
.ss_navbar_new a {
	color: #FFFFFF !important;
	}	
.ss_navbar_new a:hover {
	color: #333333 !important;
	}		

/******* PAGINATION STYLES *************/
.ss_pagination {
	
	font-size: ${ss_style_font_fineprint} !important;
	background-color: #CCDFDE !important;
	height:38px !important;
	margin-bottom: 0px;
	}
/***********This is the style sub-group for the "Go boxes" ***********/
#ss_goBox {
	height:38px !important;
	<c:if test="<%= isIE %>">
 	height:38px !important;
 	</c:if>
	}
.ss_pagination_goTable {
	height:40px !important;
/*	background-color: #CCDFDE !important;
	white-space: nowrap !important; */
}
	
.ss_paginationGo {
	font-size: ${ss_style_font_fineprint} !important;
	padding-left: 0px;
	padding-right: 10px;
	white-space: nowrap;
	padding-top: 0px;
	<c:if test="<%= isIE %>">
 		vertical-align:middle !important;
 		padding-top:0px;
 	</c:if>

}
.ss_page_IE {
		<c:if test="<%= isIE %>">
 		vertical-align:middle !important;
 		padding-top:10px;
 	</c:if>
 	}
 .ss_page_IE2 {
 	font-size: ${ss_style_font_fineprint} !important;
 	
 	padding-left: 5px;
	padding-right: 5px;
	padding-top: 2px;
	<c:if test="<%= isIE %>">
  		padding-top: 2px;
  		vertical-align:middle !important;
  		padding-bottom: 10px;
 	</c:if>
 	overflow:hidden;
 	}	
.ss_pTB_no {				/* This greys out the Go Box if only one page */
	line-height: 10px;
	font-size: ${ss_style_font_finestprint} !important;
	width: 22px;
	border-top-width: 0.8px !important;
	border-right-width: 0.4px;
	border-bottom-width: 0.4px;
	border-left-width: 0.4px !important;
	border-top-style: solid !important;
	border-right-style: none;
	border-bottom-style: none;
	border-left-style: solid !important;
	border-top-color: #698F8E !important;
	border-right-color: #698F8E;
	border-bottom-color: #698F8E;
	border-left-color: #698F8E !important;
	padding-top: 1px;
	margin-top: 2px;
	margin-left: 5px;
	background-color:#D9D9D9 !important;
}
/***********This is the style sub-group for Page N of M ***********/
.ss_paginationDiv {
	background-color: #E9F1F1 !important;
	height:40px !important;
}
.ss_pagination_table {
	height:38px !important;
/*	white-space: nowrap !important; */
	}
.ss_pagination_arrows {
	vertical-align: middle;
	padding-top: 1px;
	padding-left: 0px !important;
	padding-right: 0px !important;
	<c:if test="<%= isIE %>">
  		padding-top: 4px;
 	</c:if>
	}
.ss_paginationFont {
	font-size: ${ss_style_font_fineprint} !important;
	padding-left: 2px;
	padding-right: 5px;
	padding-top: 2px;
	<c:if test="<%= isIE %>">
  		margin-top: 5px;
  		padding-top: 4px;
 	</c:if>
 	overflow:hidden;
}

.ss_paginationFont a{
	border:solid 1px #DDDDDD !important;
	margin-right:2px;
}
.ss_pageActive{
	color:#408080!important;
	font-weight:normal;
	vertical-align:middle;
	padding:3px 4px 0px 6px;
	<c:if test="<%= isIE %>">
  		padding:3px 4px 0px 6px;
 	</c:if>
}
.ss_pageNext {
	color:#333333;
	padding:3px 6px 3px 6px;
	text-decoration:none;
	background-color:#E9F1F1 !important;
	background:transparent !important;	
	border:solid 1px #DDDDDD !important;
	margin-right:5px;
}
.ss_paginationFont a:link,
.ss_paginationFont a:visited {
	color:#333333;
	padding:3px 6px 3px 6px;
	text-decoration:none;
	background-color:#E9F1F1 !important;
	background:transparent !important;	
}
.ss_paginationFont a:hover{
	border:solid 1px #666666 !important;
	background-color: #CCDFDE !important;
}
.ss_paginationTextBox {
	line-height: 10px;
	font-size: ${ss_style_font_finestprint} !important;
	width: 22px;
	border-top-width: 0.8px !important;
	border-right-width: 0.4px;
	border-bottom-width: 0.4px;
	border-left-width: 0.4px !important;
	border-top-style: solid !important;
	border-right-style: none;
	border-bottom-style: none;
	border-left-style: solid !important;
	border-top-color: #698F8E !important;
	border-right-color: #698F8E;
	border-bottom-color: #698F8E;
	border-left-color: #698F8E !important;
	padding-top: 1px;
	margin-top: 2px;
	margin-left: 5px;
}

/***** FORMS ATTRIBUTES STYLES ******/

.ss_box_delete {
	border: 0.3px solid #3D3D3D!important;
	}
.ss_attribute {
	background-color: #DBEDFF!important;
	border: 0.3px solid #999999!important;
	margin-bottom: 5px;
	margin-right: 3px;
	}	

/*************************************/