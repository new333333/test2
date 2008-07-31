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
	background: #FFF;
	border-color: #CCCCCC;
	border-style: solid;
	border-width: .01em;
	width: 198px;
	height:auto;
	margin-right: 6px;
	}
#ss_sideNav_wrap, #ss_sideNav_wrap *{	
	font-family: Arial;
	font-size:12px;
	color: #333333
	}	
	
/*********  STATUS BOX **********/	
.ss_myStatus{
	background: transparent;
	width: 172px;
	height: auto;
	margin: 12px 8px 6px 8px;
	padding: 4px 4px 8px 4px;
	}
.ss_input_myStatus{
	background-color: #C90000;		/* #F3E9C5 */
	border-color: #5A9A98;
	width: 146px;
	height: 46px;
	margin: 10px 0px 0px 8px;
	<c:if test="<%= isIE %>">
  		margin: 8px 0px 0px 0px;
 	</c:if>
	padding: 12px 0px 12px 10px;	
	}
	
/*This is the border for all of the boxes*/
.ss_myStatus, .ss_input_myStatus, .ss_dropdownMenu, .ss_sub_dropdownMenu{
	border-color: #5A9A98;
	border-style: solid;
	border-width: .01em;
	}
	
/*This is the title of each link box*/
.ss_dropdownTitle{
	text-indent: 2px;
	margin-top:12px;		
	}
	
/*This is the box style for the drop down menu open*/
.ss_dropdownMenu{
	text-indent: 22px;
	margin: 8px 8px 12px 8px;
	margin-top: 8px;
	padding-top: 8px;
	padding-right: 4px;
	padding-bottom: 8px;
	padding-left: 4px;
	}
.ss_menuOpen {
	background-image:  url("<html:rootPath/>images/pics/sidebar/backgrounddropdowns.png");
	background-position: 0px -57px;
	background-color: transparent;
	background-repeat: no-repeat;
	}	
.ss_menuClosed {
	background-image: url("<html:rootPath/>images/pics/sidebar/backgrounddropdowns.png");
	background-position: 0px -30px;
	background-color: transparent;
	background-repeat: no-repeat;
	}	
.ss_dropdownMenu .ss_menuClosed a:hover {
    Color: #5A9A98;
	background-color: #CCFFFF;
	border-color:#85D1D1;
	border-width: .2px;
    padding: 8px 0 0 35px;
	margin-bottom:3px;
	}	

.ss_dropdownTitle{
	text-indent: 8px;
	margin:12px 0px 1px 0px;
	}
	
/*This is the box style for the blue menu boxes*/		
.ss_sub_dropdownMenu {
	background-color: #DBE9E8;
	width: 150px;
	height: auto;
	line-height: 170%;
	padding: 8px 2px 12px 3px;
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
.ss_sub_dropdownMenu a:hover {
	background-color: #ADD8E6;
	}
.ss_sub_dropdownMenu a{
	text-decoration: underline;	
	color:#003399;			
	}

/* This is for the box style links */

#ss_leftNav {
      width: 172px;
      margin: 8px;
      margin-bottom: 2px;
        }
#ss_leftNav ul {
    text-decoration: none;
    margin: 0px;
    padding: 0px;
         }
        
#ss_leftNav li a {
	height: 24px;
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #5A9A98;
	border-width: 0.2px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_bgd.png") 5px 2px;
    padding: 8px 0 0 30px;
	margin-bottom: 3px;
    }		
#ss_leftNav a:visited {
	color:#990099;
	}
#ss_leftNav li a:hover {
    color: #5A9A98;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_bgd.png") 5px -24px;
	background-color: #CCFFFF;
	border-color:#85D1D1;
	border-width: .01em;
    padding: 8px 0 0 35px;
	margin-bottom:3px;
     }
#ss_leftNav li a:active {
    color: #fff;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_bgd.png") 5px -50px;
	background-color: #5A9A98;
    padding: 8px 0 0 35px;
	margin-bottom: 3px;
    }
.ss_menuSubTitle {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 11px;
	line-height: 16px;
	color: #333333;
	padding: 5px;
}

/**********************************/
/*This is the box style for the drop down menus (LEFTOVER CODE-- NOT USED)
#ss_dropdownMenu a {
    width: 180px;
    margin: 8px;
    text-decoration: none;
    height: 24px;
	}
#ss_dropdownmenu a a:link, a:visited {	      
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #5A9A98;
	border-width: .2px;
	background-color: #FFFFFF;
    background: url(backgrounddropdowns.png)5px 2px;
    padding: 8px 0 0 30px;
	margin-bottom: 3px;
	}
#ss_dropdownMenu a:hover #current;
	color: #5A9A98;
    background: url(backgrounddropdowns.png) 5px -24px;
	background-color: #CCFFFF;
	border-color:#85D1D1;
	border-width: .2px;
    padding: 8px 0 0 35px;
	margin-bottom:3px;
	}
  #ss_dropdown a:active {
    color: #fff;
    background: url(backgrounddropdowns.png) 5px -50px;
	background-color: #5A9A98;
    padding: 8px 0 0 35px;
	margin-bottom: 3px;
	}*/
	
/**********************************/