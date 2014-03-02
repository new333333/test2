<%
/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
boolean isIE6 = BrowserSniffer.is_ie_6(request);
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
	}	
.ss_sidebarTree {
	overflow-x:auto;
	padding-bottom:4px;
	}	
.ss_sidebarTree_IE {
	overflow-x:scroll;
	overflow-y:hidden;
	padding-bottom:4px;
	}	
.ss_sidebarTree_IE6 {
	overflow:hidden;
	width:180px;
	}	
/*********  STATUS BOX **********/	
.ss_myStatusOuter{
	background: transparent;
	height: auto;
	margin: 8px 0px 0px;
	<c:if test="<%= isIE %>">
		margin-right: 2px !important;
 	</c:if>	 	
	}
.ss_myStatusInner{
	padding:0px;
	<c:if test="<%= isIE %>">
		margin: 0px !important;
 	</c:if>	 	
	}
.ss_status_textarea{
	font-size: 10px;
	}
.ss_status_header{
	font-size: 12px;
	font-weight: bold;
	}
div.ss_input_myStatus {
	border-color: #9EC8CD;
	padding: 2px 2px 0px 2px;	
	}
textarea.ss_input_myStatus {
	border-color: #9EC8CD;
	width: 152px;
	height: 42px;
	margin: 0px 0px 8px 1px;
	<c:if test="<%= isIE %>">
	    width: 160px;
  		margin: 0px 8px 4px -16px;
 	</c:if>
	<c:if test="<%= isIE6 %>">
	    width: 160px;
  		margin: 0px 8px 4px -10px;
 	</c:if>
	padding: 0px 0px 3px 2px;	
	}
.ss_setStatusBackground{
	background-color: #9EC8CD !important;
	}
	
/*This is the border for all of the boxes*/
.ss_myStatus, .ss_input_myStatus, .ss_sidebarMenu, .ss_sub_sidebarMenu, .ss_mouseOver{
	border-color: #9EC8CD;
	border-style: solid;
	border-width: 1px;
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
	margin: 3px 8px 3px 8px;
	padding: 8px 4px 8px 4px;	
	<c:if test="<%= isIE %>">
		padding: 8px 2px 8px 4px
 	</c:if>				
	}	
.ss_menuOpen {
	background-image:  url("<html:rootPath/>images/pics/sidebar/backgrounddropdowns.png");
	background-position: 1px -57px;
	background-color: inherit;
	background-repeat: no-repeat;
	padding-left: 27px;
	cursor: pointer;
	}	
.ss_menuClosed {
	background-image: url("<html:rootPath/>images/pics/sidebar/backgrounddropdowns.png");
	background-position: 1px -31px;
	background-color: inherit;
	background-repeat: no-repeat;
	padding-left: 27px;
	cursor: pointer;
	}
.ss_mouseOver {
	background-color: #CCFFFF;
	margin: 3px 8px 3px 8px;
 	padding: 8px 4px 8px 4px;
 	<c:if test="<%= isIE %>">
		padding: 8px 2px 8px 4px
 	</c:if>		
	}		
.ss_sidebarTitle{
	margin:3px 0px 1px 8px;
	}
	
/*This is the box style for the blue menu boxes*/		
.ss_sub_sidebarMenu {
	background-color: #DBE9E8;
	width: 140px;
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
	background-color: #CCCCCC;
	}
.ss_sideLink {
	text-decoration: none;	
	color:#003399;	
	background-image: url("<html:rootPath/>images/pics/sidebar/menu_click.gif");
	background-position: 1px 3px;
	background-repeat: no-repeat !important;
	padding-left: 10px;		
	}

/* This is for the box style links */

.ss_leftNav, .ss_sideShare, .ss_sideTrack {
      margin-left: 8px;
      margin-right: 8px;
	<c:if test="<%= isIE %>">
		margin-right: 2px !important;
 	</c:if>	      
      margin-bottom: 2px;
      }     
.ss_leftNav ul, .ss_sideShare ul, .ss_sideTrack ul {
    text-decoration: none;
    margin: 0px;
    padding: 0px;
      }
.ss_leftNav ul li, .ss_sideShare ul li, .ss_sideTrack ul li {
    list-style-type: none !important;
    list-style-position: outside !important;
    list-style-image: none !important;
    text-decoration: none;
    margin: 0px;
    padding: 0px;
      }
.ss_leftNav a:visited {
	color:#333333;	/* #990099 (magenta) */
	}
/* .ss_leftNav li a:hover {
    color: #333333;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_hover.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #CCFFFF;
	border-color:#9EC8CD;
	border-width: 1px;
    padding: 7px 0 7px 30px;
	margin-bottom:3px;	
     }
.ss_leftNav li a:active {
    color: #fff;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_a.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #9EC8CD;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }      */
.ss_sidePlus li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_a.png") 5px 2px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }	
.ss_clipBd li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/footer/sidebar_clip.png") 8px 5px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }    
.ss_sideEmail li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/footer/sidebar_mail.png") 8px 5px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }
.ss_sideTrash li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/sidebar/sidebar_trash.png") 8px 5px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }
.ss_sideMeet li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/footer/sidebar_meet.png") 8px 5px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }      
.ss_sideEmail li a:hover, .ss_sideMeet li a:hover, .ss_clipBd li a:hover, .ss_sideTrash li a:hover {
    color: #333333;
	background-color: #CCFFFF;	
     }          
.ss_sidePlus li a:hover {
    color: #333333;
    background: url("<html:rootPath/>images/pics/sidebar/sidebarmenu_btn_hover.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #CCFFFF;
	border-color:#9EC8CD;
	border-width: 1px;
    padding: 7px 0 7px 30px;
	margin-bottom:3px;	
     }    	
.ss_sideShare li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/sidebar/background_share.png") 5px 2px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }		
.ss_sideShare a:visited {
	color:#333333;	/* #990099 (magenta) */
	}
.ss_sideShare li a:hover {
    color: #333333;
    background: url("<html:rootPath/>images/pics/sidebar/background_share_f.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #CCFFFF;
	border-color:#9EC8CD;
	border-width: 1px;
    padding: 7px 0 7px 30px;
	margin-bottom:3px;	
     }
.ss_sideShare li a:active {
    color: #fff;
    background: url("<html:rootPath/>images/pics/sidebar/background_share.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #9EC8CD;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }   
.ss_sideTrack li a {
    color: #666666;
    display: block;
	border-style: solid;
	border-color: #9EC8CD;
	border-width: 1px;
	background-color: #FFFFFF;
    background: url("<html:rootPath/>images/pics/sidebar/background_tracked.png") 5px 2px;
    background-repeat: no-repeat !important;
    padding: 7px 0 7px 30px;
	margin-bottom: 3px;
    }		
.ss_sideTrack a:visited {
	color:#333333;	/* #990099 (magenta) */
	}
.ss_sideTrack li a:hover {
    color: #333333;
    background: url("<html:rootPath/>images/pics/sidebar/background_tracked_f.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #CCFFFF;
	border-color:#9EC8CD;
	border-width: 1px;
    padding: 7px 0 7px 30px;
	margin-bottom:3px;	
     }
.ss_sideTrack li a:active {
    color: #fff;
    background: url("<html:rootPath/>images/pics/sidebar/background_tracked.png") 5px 2px;
    background-repeat: no-repeat !important;
	background-color: #9EC8CD;
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
	<c:if test="<%= !isIE6 %>">
	  height:18px;	
	</c:if>
	<c:if test="<%= isIE6 %>">
	  height:26px;	
	</c:if>
	color:#FFF;
	cursor: pointer;
	width: 210px;
	}
.ss_sidebarSlide span{
	vertical-align: middle !important;
	color: #c00000 !important;
	}	
.ss_sidebarSlidesm {
	background-image: url("<html:rootPath/>images/pics/navbar/toolbar_slide_sm.png"); 
	background-position:top left;
	background-repeat: no-repeat;
	<c:if test="<%= !isIE6 %>">
	  height:18px;	
	</c:if>
	<c:if test="<%= isIE6 %>">
	  height:26px;	
	</c:if>
	color:#FFF;
	cursor: pointer;	
	width: 120px;
	}
.ss_sidebarSlidetext {
	padding-top: 4px;
	padding-bottom: 0px;
	}	
.ss_hideShow {
	font-size: ${ss_style_font_smallprint};
	padding-left:20px;
	padding-top: 5px;
	}
button { 
  	border:0; 
  	cursor:pointer; 
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


/********************* OBJECT LIST TABLE **********************/
/* Object List Table styles as used by sidebar_appConfig.jsp. */

.ss_objlist_table_columnhead	{color: #505354; font-size: 0.75em; background-color: #ededed; border-bottom: 1px solid #cccccc}
.ss_objlist_table_footer		{margin-top: 1em; padding: 0.5em; border-top: 1px solid #cccccc;}
.ss_objlist_table_instructions	{color: #505354; font-size: 0.8em}
.ss_objlist_table_mediumtext	{color: black; font-size: 0.85em; line-height: 1.1em}
.ss_objlist_table_smalltext		{color: black; font-size: 0.75em; line-height: 1em}
.ss_objlist_table_tablehead	td	{color: #505354; font-weight: bold; font-size: 13px; background-color: #ededed; text-align: left; text-indent: 5px; padding: 5px}
.ss_objlist_table_top			{}
#emptyRowI						{color: #949494;}

.ss_objlist_menu_bottomDIV	{margin-bottom: 0px; padding-bottom: 0px; border-bottom: 5px solid #458ab9}
.ss_objlist_menu_itemDIV	{text-decoration: none; white-space: nowrap}
.ss_objlist_menu_margin		{margin-left: 5px}
.ss_objlist_menu_popupDIV	{line-height: 1.5em; background-color: #ffffff; border: solid 1px #000; position: absolute; z-index: 4; top: 0px; left: 0px} 
.ss_objlist_menu_titleDIV	{background-color: #E0E1DF; font-weight: bold; margin-bottom: 0.5em; padding: 0.5em; white-space: nowrap;}
.ss_objlist_menu_titleIMG	{position: absolute; right: 5px}
