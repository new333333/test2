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

/**********************************/
/**********************************/
/** CSS DOCUMENT FOR THE SIDEBAR **/
/**********************************/
/**********************************/

/*********  SIDEBAR WRAP **********/
/*This is the wrap for the navigation */

#ss_sideNav_wrap{
	background: #FFF !important;
	border-color: #CCCCCC;
	border-style: solid;
	border-width: 0;
	width: 198px;
	height:auto;
	margin-right: 6px;
	overflow:hidden;
	}
#ss_sideNav_wrap, #ss_sideNav_wrap *{	
	font-family: Arial;
	font-size:${ss_style_font_smallprint};
	color: #333333
	}	
	
/*********  STATUS BOX **********/	
.ss_myStatus{
	background: transparent;
	width: 176px;
	height: auto;
	margin: 8px 8px 3px 8px;
	padding: 4px 4px 4px 4px;
	background-color: #EFEFEF;
	}
/* .ss_statusMouseOver {		***DOESN'T WORK YET.  ADD IN SIDEBAR_STATUS.JSP WHEN DOES***
	background-color: #CCFFFF;
	width: 176px;
	margin: 8px 8px 3px 8px;
	padding: 4px 4px 4px 4px;
		}	*/ 
.ss_status_textarea{
	font-size: 10px;
	background-color:#E9F1F1!important;
	}
.ss_input_myStatus{
	background-color: #F3E9C5;
	border-color: #5A9A98;
	width: 160px;
	height: 42px;
	margin: 8px 0px 8px 8px;
	<c:if test="<%= isIE %>">
  		margin: 6px 0px 0px 0px;
 	</c:if>
	padding: 8px 0px 8px 10px;	
	}
/*This is the border for all of the boxes*/
.ss_setStatusBackground{
	background-color: #5A9A98;
	}
	
/*This is the border for all of the boxes*/
.ss_myStatus, .ss_input_myStatus, .ss_sidebarMenu, .ss_sub_sidebarMenu, .ss_mouseOver{
	border-color: #5A9A98;
	border-style: solid;
	border-width: .01em;
	}
/*This is the title of each link box*/
.ss_sidebarTitle{
	text-indent: 2px;
	margin-top:12px;		
	}

/*This is the title of each link in Recent Places */	
.ss_tabs_title {
	font-size: ${ss_style_font_smallprint}!important;
	margin-left: 11px;
}	
	
/*This is the box style for the drop down menu open*/
.ss_sidebarMenu{
	/* text-indent: 5px; */
	margin: 3px 8px 3px 8px;
	width: 176px;
	<c:if test="<%= isIE %>">
		width: 174px;
 	</c:if>	
 	padding: 8px 4px 8px 4px;
	}	
.ss_menuOpen {
	background-image:  url("<html:rootPath/>images/pics/sidebar/backgrounddropdowns.png");
	background-position: 1px -57px;
	background-color: transparent;
	background-repeat: no-repeat;
	padding-left: 27px;
	}	
.ss_menuClosed {
	background-image: url("<html:rootPath/>images/pics/sidebar/backgrounddropdowns.png");
	background-position: 1px -31px;
	background-color: transparent;
	background-repeat: no-repeat;
	padding-left: 27px;
	}	
.ss_mouseOver {
	background-color: #CCFFFF;
	margin: 3px 8px 3px 8px;
	width: 176px;
	<c:if test="<%= isIE %>">
		width: 174px;
 	</c:if>	
 	padding: 8px 4px 8px 4px;
		}	
	
/* .ss_sidebarMenu .ss_menuClosed a:hover {
    Color: #5A9A98;
	background-color: #CCFFFF;
	border-color:#85D1D1;
	border-width: .2px;
    padding: 8px 0 0 35px;
	margin-bottom:3px;
	}	
.ss_sidebarMenu a:hover {
    Color: #5A9A98;
	background-color: #CCFFFF;

	} */
.ss_sidebarTitle{
	text-indent: 8px;
	margin:3px 0px 1px 0px;
	}
	
/*This is the box style for the blue menu boxes*/		
.ss_sub_sidebarMenu {
	background-color: #DBE9E8;
	width: 150px;
	height: auto;
	line-height: 170%;
	padding: 6px 2px 8px 3px;
	margin: 3px 0px 3px 8px;
	text-decoration: none;
	}
	
/*This is the rollover style for the links in the blue boxes*/
.ss_rollover a{
	color:#000099!important;
	}		
.ss_rollover a:hover {
	background-color: #ADD8E6;
	color:#003399;
	text-decoration: none;
	}
.ss_sub_sidebarMenu a:hover {
	background-color: #ADD8E6;
	}
.ss_sub_sidebarMenu a{
	text-decoration: underline;	
	color:#003399;			
	}

/* This is for the box style links */

#ss_leftNav {
      width: 184px;
      margin-left: 8px;
      margin-bottom: 2px;
        }
#ss_leftNav ul {
    text-decoration: none;
    margin: 0px;
    padding: 0px;
         }
        
#ss_leftNav li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #5A9A98;
	border-width: .01em;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_bgd.png") 5px 2px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }		
#ss_leftNav a:visited {
	color:#990099;
	}
#ss_leftNav li a:hover {
    color: #5A9A98;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_bgd.png") 5px -24px;
    background-repeat: no-repeat !important;
	background-color: #CCFFFF;
	border-color:#85D1D1;
	border-width: .01em;
    padding: 7px 0 7px 30px;
	margin-bottom:3px;
     }
#ss_leftNav li a:active {
    color: #fff;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_bgd.png") 5px -50px;
    background-repeat: no-repeat !important;
	background-color: #5A9A98;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }
.ss_menuSubTitle {
	font-family: Arial, Helvetica, sans-serif;
	font-size: ${ss_style_font_fineprint};
	line-height: 16px;
	color: #333333;
	padding: 1px;
}
/* TOOLBAR STYLES (change heights to 22px and no ka for boulder */

.ss_sidebarImage {
	background-image: url("<html:rootPath/>images/pics/navbar/bg_toolbar.gif");  
	background-repeat: repeat-x;
}
.ss_sidebarSlide {
	background-image: url("<html:rootPath/>images/pics/navbar/toolbar_slide_lg.png"); 
	background-position:top left;
	background-repeat: no-repeat;
	height:22px;		/* boulder 22px */
	color:#FFF;
	}
.ss_sidebarSlidesm {
	background-image: url("<html:rootPath/>images/pics/navbar/toolbar_slide_sm.png"); 
	background-position:top left;
	background-repeat: no-repeat;
	height:22px;		/* boulder 22px */
	color:#FFF;
	}
.ss_hideShow {
	font-size: ${ss_style_font_smallprint};
	padding-left:20px;
	padding-top: 5px;
	}
	
button { 
  border:0; 
  cursor:pointer; 
  padding:0 20px 0 0; 
  text-align:center; 
}
button span {
	position:relative;
	display:block;
	white-space:nowrap;
	padding:0 0 0 25px;
}

/*AQUA buttons*/
.ss_toolbar_submitBtn {
	font-size:.8em;
	background-color: transparent !important;
	background-image: url("<html:rootPath/>images/pics/navbar/btn_aqua_right.png");
	background-repeat: no-repeat;
	background-position: right;
	 <c:if test="<%= isIE %>">
  		background-position: top right!important;
 	</c:if>
	
}
.ss_toolbar_submitBtn span {
	line-height:20px;
	height: 20px;
	color:#fff;
	background-color: transparent !important;
	background-image: url("<html:rootPath/>images/pics/navbar/btn_aqua_left.png");
	background-repeat: no-repeat;
	background-position: left;
		 <c:if test="<%= isIE %>">
  		background-position: top left!important;
 	</c:if>
}
.ss_toolbar_submitBtn:hover {
	background-color: transparent !important;
	background-image: url("<html:rootPath/>images/pics/navbar/btn_aqua_right_hover.png");
	background-repeat: no-repeat;
	background-position: right;
}
.ss_toolbar_submitBtn:hover span {
	background-color: transparent !important;
	background-image: url("<html:rootPath/>images/pics/navbar/btn_aqua_left_hover.png");
	background-repeat: no-repeat;
	background-position: left;
}

