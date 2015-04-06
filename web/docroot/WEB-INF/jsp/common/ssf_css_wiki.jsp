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

/**********************************/
/**********************************/
/** CSS DOCUMENT FOR WIKIs **/
/**********************************/
/**********************************/

/* Wiki */

.wiki-topics {
    font-size: 13px;
	line-height: 20px;
    background-color: #fff;
    padding: 5px;
    }
.wiki-topic-content {
	border-bottom: 1px solid #c4c4c4;
	}	
.wiki-topic-a {
    padding: 2px 8px;
    white-space: nowrap;
    }
.wiki-topic-selected {
    color: #fff !important;
    background-color: #036f9f;
    font-weight: bold;
    padding: 2px 8px;
    -moz-border-radius: 3px;
    border-radius: 3px;
    -webkit-border-radius: 3px;
    }
.wiki-page {
    font-size: 12px;
    line-height: 12px;
    margin: 2px 8px 10px 10px;
    }
.wiki-page-a {
    }
.wiki-homepage-a {
 	font-weight: bold;
}

.wiki-noentries-panel, .wiki-nohomepage-panel {
	color: #000;
 	font-size: 14px;
	background-color: #f4f5bd;
	border: 1px solid #e0e0e0;
	padding: 2px 10px;
	line-height: 28px;
	z-index: 100;
	-moz-border-radius: 5px;
	border-radius: 5px;
	-webkit-border-radius: 5px;
}

.ss_wiki_search_bar {
	position: absolute;
	top: 5px;
	right: 5px;
	white-space: nowrap;
	}
#ss_findWikiPageForm_ss_forum_ {display: inline;}


/* wiki tabs */
#wiki-tabset span.wiki-tab.on a {color:#353838;}
#wiki-tabset span.wiki-tab a {color:#fff;}
#wiki-tabset span.wiki-tab a:hover {background: transparent;}

.wiki-tabs {
    text-align: center;
    margin-bottom: 6px;
	padding: 3px 6px 0px 10px;
	background-color: #fff;
    border-bottom: 1px solid #ccc;
    -moz-border-radius-topright: 5px;
    -moz-border-radius-topleft: 5px;
    border-top-right-radius: 5px;
    border-top-left-radius: 5px;
    -webkit-border-top-right-radius: 5px;
    -webkit-border-top-left-radius: 5px;
    }
.wiki-tabs table {
	background-color: transparent;
	}	
.wiki-tab {
    font-size: 12px;
    font-weight: normal;
    padding: 3px 8px;
    margin: 2px 0px 4px;
    background: #fff;
   	border-top: 1px solid #949494;
	border-left: 1px solid #949494;
	border-right: 1px solid #949494;
    display:inline-block;
    cursor: pointer;
    color: #fff;
    background: #949494;
    border-radius: 3px;
    -moz-border-radius: 3px;
    -webkit-border-radius: 3px;
    }   
.wiki-tab.on {
    color: #353838;
    background: #fff;
    font-weight: bold;
    cursor: pointer;
    display: inline-block;
    padding: 4px 8px 7px;
    margin: 2px 0px 0px 0px;
	border-top: 1px solid #ccc;
	border-left: 1px solid #ccc;
	border-right: 1px solid #ccc;
    border-bottom-right-radius: 0px;
    border-bottom-left-radius: 0px;
    -moz-border-radius-bottomright: 0px;
    -moz-border-radius-bottomleft: 0px;
    -webkit-border-bottom-right-radius: 0px;
    -webkit-border-bottom-left-radius: 0px;
    }
.wiki-tab:hover, .wiki-tab.on:hover {
    color: #036f9f !important;
    background: #cce9ee;
	border-top: 1px solid #81b2bd;
	border-left: 1px solid #81b2bd;
	border-right: 1px solid #81b2bd;
    }   
.wiki-tab a:hover, .wiki-tab.on a:hover {
    color: #036f9f !important;
	}

.wiki-tabs2 {
	padding-top:6px;
	}
.wiki-actions {
    margin: 0px 0px 15px;
    }

.wiki-menu a {
	color: #1f1f1f !important;
	background-color: #fafafa;
	border: 1px solid #B8B8B8 !important;
	font-weight: normal !important;
	font-size: 11px;
	padding: 2px 8px  !important;
	text-decoration:none;	
  	white-space: nowrap;
  	cursor: pointer !important;
	border-radius: 5px;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
    }
.wiki-menu a:hover {
	color: #036f9f !important;
    text-align: center !important;
    font-size: 11px !important;
    font-family: Arial, sans-serif !important;
    white-space: nowrap !important;
    text-decoration: none !important;
  	padding: 2px 8px !important;
	border: 1px solid #81b2bd !important;
	background: #cce9ee;
	border-radius: 5px;
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
  	<c:if test="<%= isIE %>">
  		padding: 0px;
  		padding-top: -2px;
  		padding-bottom: 1px;
  		padding-left: 1px; 
  		padding-right: 1px;
	</c:if>
    }   
.wiki-menu {
    padding: 2px 0px;
    }
.wiki-menu a:visited {
	color: #fff;
	}

/* Wiki Entry styles */
/*-------------------------------------------*/

.wiki-entry-wrap {
	position: relative;
	padding:4px;
	}
	
.wiki-entry-wrap #ss_profile_box_h1, #ss_folder_type_wiki #ss_profile_box_h1 { margin-right: 300px;}
.wiki-entry-wrap .ss_treeWidget, .ss_folder_type_wiki .ss_treeWidget { margin-top: 0px; margin-left: 5px;}
#ss_folder_type_wiki .ss_treeWidget { margin-top: 0px;}
.wiki-entry-wrap #tag_button {display: none;}
.wiki-entry-wrap .ss_entryDescription {border-top: 0px;}	
.wiki-entry-wrap .wg-comment .ss_entryDescription {border-top: 1px solid #c4c4c4;}	
	
.wiki-entry-title {
    font-size: 15px;
    line-height: 18px;
    padding-right: 25px;
    }
.wiki-entry-content {
    font-size: 13px;
    white-space: normal;
    overflow: auto;
    height: auto;
    }
.wiki-entry-actions {
    position: absolute;
    top: 1px;
    right: 5px;
    background: #ededed;
    padding: 3px 8px;
    z-index: 1000;
    -moz-border-radius: 3px;
    border-radius: 3px;
    -webkit-border-radius: 3px;
    }   
.wiki-entry-actions img {
    cursor: pointer;
    padding-right: 5px;
    }
.wiki-entry-author {
    font-size: 11px;
    font-weight: bold;
    padding-top: 2px;
    padding-right: 10px;
    }
.wiki-entry-author a, a.wiki-entry-author, span.wiki-entry-author {
    color: #353838;
    }
.wiki-entry-locale a {
    color: #666;
    }   
.wiki-entry-locale a:hover,
.wiki-entry-author a:hover {
    color: #135c8f;
    }   
.wiki-entry-date {
    font-size: 10px;
    color: #666;
    padding-left: 8px;
    padding-right: 8px;
    white-space: nowrap;
    }
.wiki-entry-locale {
    font-size: 10px;
    color: #666;
    padding-left: 8px;
    padding-right: 8px;
    white-space: nowrap;
    }
#descriptionRegion1 .ss_entryDescription {
	background-color: #fff;
	padding: 5px 20px 0px 5px;
	color: #353838;
	font-size: 1.15em;
	border: 0px;
	}	


.nv-footer-wiki     { border-top: 1px solid #e0e0e0; padding-bottom:10px; padding-top: 5px;}
.nv-footer-wiki .ss_muster_users { font-size: 11px;}
.nv-footer          { position: relative; background-color: #e6e6e4; height: 25px; vertical-align:middle; text-align: center; margin-top: .5em; padding: 0.5em; border-top: 1px solid #babdb6; }
.nv-footer-r        { position: relative; background-color: #e6e6e4; height: 25px; vertical-align:middle; text-align: right; margin-top: .5em; padding: 0.5em; border-top: 1px solid #babdb6; }
.nv-footer .buttons { position: absolute; right: 10px; top: 10px; min-width: 80px; }
#opaque-div         { position: absolute; top: 0px; left: 0px; background: url(../common/images/trans30_black.png) repeat; width: 100%; height: 100%; overflow: hidden; z-index: 999;  }

.ws-nw td { white-space: nowrap; }
  
div.ss_wiki_content {
	padding-bottom: 30px;
}
div.ss_wiki_sidebar {
    padding-left:5px;
 	padding-right: 5px;
    padding-top: 10px;
    padding-bottom: 10px;
    background-color:${ss_style_background_color_side_panel_featured};
}
.ss_wiki_sidebar_subhead {
	font-weight: bold;
	font-style: italic;
	font-size: ${ss_style_font_largeprint} !important;
	color: #0066CC !important; /* ${ss_style_muted_label_color}; */
	border-bottom: 1px solid #0066CC;
	padding-top: 2px;
	padding-bottom: 2px;
}
div.ss_wiki_sidebar_box {
/*	background-color: #FFFFFF;
	border: 1px solid ${ss_blog_sidebar_box_outline}; */
	margin-bottom: 10px;
	margin-top: 2px;
	padding: 2px 5px;
}
.ss_wiki_sidebar table {
	background-color: transparent;
}

.ss_wiki_folder_list {
	font-size: 12px !important;
	border-top: 1px solid #D2D5D1;
	border-bottom: 1px solid #D2D5D1;
	padding: 5px 0px 10px 0px;
	}