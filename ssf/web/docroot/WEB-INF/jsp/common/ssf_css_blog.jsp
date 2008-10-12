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
/** CSS DOCUMENT FOR BLOGS **/
/**********************************/
/**********************************/

/* Blogs */

/* 2 COLUMN PAGE STYLE FOR BLOGS */	
#ss_blogNav_wrap{
	background: #FFF !important;
	width: 198px !important;
	margin-right: 6px;
	}
#ss_blogContent_wrap {
	vertical-align:top;
	}
.ss_blogLeftCol {
	width: 500px !important;
    }
.ss_blogRightCol {
	width: 198px !important;
	padding-left: 20px;
    }
    
.ss_blog {
 
}
div.ss_blog_content {
	padding-bottom: 30px;
}
div.ss_blog_sidebar {
    margin-top: 5%;
    padding-left:5px;
 	padding-right: 5px;
    padding-top: 10px;
    padding-bottom: 10px;
    background-color:${ss_style_background_color_side_panel_featured};
}
.ss_blog_sidebar_subhead {
	font-weight: bold;
	font-style: italic;
	font-size: ${ss_style_font_largeprint} !important;
	color: #0066CC !important; /* ${ss_style_muted_label_color}; */
	border-bottom: 1px solid #0066CC;
	padding-top: 2px;
	padding-bottom: 2px;
}
div.ss_blog_sidebar_box {
/*	background-color: #FFFFFF;
	border: 1px solid ${ss_blog_sidebar_box_outline}; */
	margin-bottom: 10px;
	margin-top: 2px;
	padding: 2px 5px;
}
a.ss_displaytag {
	color: ${ss_style_metadata_color};
	font-size: ${ss_style_font_smallprint} !important;
	}
.ss_blog_sidebar table {
	background-color: transparent;
}
.ss_blog_content, .ss_blog_content table {
  background-color:${ss_blog_content_background_color};
}
.ss_blog_summary_title, .ss_blog_summary_title table {
  background-color:${ss_blog_summary_title_background_color};
}
span.ss_blog_summary_title_text {
  font-family: ${ss_style_title_font_family};
  font-weight: bold;
  font-size: 13px;
}
table.ss_blog_title table {
  background-color:${ss_style_header_bar_background};
}
div.ss_blog_title  {

  padding-top: 12px;
  padding-bottom: 3px;
  padding-left: 2px;
  padding-right: 5px;
}
div.ss_header_bar_timestamp {
	font-family: ${ss_style_title_font_family};
	font-size: 11px;
	color: ${ss_style_metadata_color};
	margin-top: 3px;
	margin-right: 5px;
}
.ss_blog_footer {
	background-color:${ss_blog_footer_color};
	padding-bottom: 5px; 
	padding-top: 4px;
	padding-left: 22px
}
	
div.ss_header_bar_timestamp a, div.ss_header_bar_timestamp a:visited {
	color: ${ss_style_metadata_color};
	}
div.ss_header_bar_timestamp a:hover, div.ss_header_bar_timestamp a:visited:hover {
	color: ${ss_style_metadata_color};
	text-decoration: underline;
	}
div.ss_header_bar_burst {
    display: inline;
	padding-left: 3px;
	}
div.ss_replies div.ss_header_bar_burst {
    display: inline;
	padding-left: 30px;
	}
div.ss_header_bar_title_text {
    display: inline;
	}
span.ss_header_bar_title_text {
    font-family: ${ss_style_title_font_family};
    font-weight: bold;
	color: ${ss_style_header_bar_title_color};
	font-size: 15px;
	margin-left: 5px;
	}
a.ss_header_bar_title_link {
	color: ${ss_style_header_bar_title_link_color};
	text-decoration: none;
	}
a.ss_header_bar_title_link:visited {
	color: ${ss_style_header_bar_title_link_color};
	text-decoration: none;
	}
a.ss_header_bar_title_link:hover, a.ss_header_bar_title_link:visited:hover {
	color: ${ss_style_header_bar_title_link_color};
	text-decoration: underline;
	}
	
