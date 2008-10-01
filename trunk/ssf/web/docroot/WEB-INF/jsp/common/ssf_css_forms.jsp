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


.ss_form_header {
	font-size:28px !important;
	letter-spacing:-1px;
	font-family:Georgia, "Times New Roman", Times, serif !important;
	font-weight:normal !important;
	color:#5A9A98;
	}
.ss_table_wrap {
	display: table;
	border-top-width: 24px;
	border-right-width: thin;
	border-bottom-width: thin;
	border-left-width: thin;
	border-style: solid;
	border-top-color: #5A9A98;
	border-right-color: #CCCCCC;
	border-bottom-color: #CCCCCC;
	border-left-color: #CCCCCC;
	padding: 0 25px 25px 25px!important;
	}
.ss_form_wrap {
	border-top-width: 24px;
	border-right-width: thin;
	border-bottom-width: thin;
	border-left-width: thin;
	border-style: solid;
	border-top-color: #5A9A98;
	border-right-color: #CCCCCC;
	border-bottom-color: #CCCCCC;
	border-left-color: #CCCCCC;
	padding: 0 25px 25px 25px!important;
	margin: 25px 25px 25px 25px;
	}
.ss_form_subhead {
	 font-weight: bold;
	 color: #333;
	 }
.ss_form_subhead a:hover{
	 color: #555;
	 }	
.ss_table_heading {
	font-size: 10px;
	font-weight: bold;
	color: #333333;
	background-color: #EFEFEF;
	text-align: left;
	letter-spacing: normal;
	padding: 0 0 0 5px;
	}
.ss_table_oddRow {
	background-color: #E4EFEF;
	display: table-row;
	}
.ss_table_data {
	font-family: ${ss_style_font_family};
	font-size: 10px;
	letter-spacing: 1px;
	text-align: left;
	display: inline-table;
	margin: 0px;
	padding: 0 0 0 5px;
	border: 0.1px solid #EFEFEF;
	}
.ss_table_data_TD {
	font-family: ${ss_style_font_family};
	font-size: 10px;
	letter-spacing: normal;
	text-align: left;
	display: table-cell;
	border-bottom-width: thin;
	border-top-style: none;
	border-bottom-style: solid;
	border-bottom-color: #CCCCCC;
	padding: 6px 3px 6px 3px;
	margin: 2px;
	}
.ss_table_data_TD a:hover {
	background-color: #F2F2F2;
	}
.ss_table_data_mid{
 	text-align: center;
 	border-bottom: solid thin #CCCCCC;
	padding: 6px 0 6px 0;
	margin: 2px;
 	}		
.ss_table_data_Mid {
	font-family: ${ss_style_font_family};
	font-size: 10px;
	letter-spacing: normal;
	text-align: center;
	display: inline-table;
	padding-right: 2px;
	padding-left: 2px;
	border-bottom-width: thin;
	border-bottom-style: solid;
	border-bottom-color: #CCCCCC;
	padding-bottom: 4px;
	padding-top: 4px;
	margin: 2px;
	}
.ss_formButton {
	background-position: right;
	text-align: right;
	}

/********* CLIPBOARD POPUP ************/
.ss_stylePopup ul {
	padding-left: 1% !important;
	margin-left: 0px;
	margin-top: 5px;
	margin-bottom: 15px;
	margin-right: 0px;
	}
.ss_stylePopup {
	font-family: ${ss_style_font_family};
	font-weight: inherit;
	font-size: ${ss_style_font_size}; 
	background-color: ${ss_style_background_color};
	color: ${ss_style_text_color}; 
}
/********* LOGIN TEXT BOX ************/

.ss_text_login{
	font-family: ${ss_style_font_family};
	font-size: 10px;
	text-align: left;
	border: dotted 1px #5A9A98;
	padding: 3px;
	margin: 6px;
	}
.ss_text_login a:hover{
	background: #666;
	}
fieldset{
  	margin-top: 40px;
  	margin-bottom: 40px;
    margin-top: 6px;
    margin-bottom: 4px;
    border: 1px solid ${ss_style_border_color_light};
	} 
.ss_fieldset_login {
  	margin-top: 80px;
  	margin-bottom: 100px;
 	padding-bottom:12px;
  	padding-top: 18px;
  	display: block;
  	border: 1px dotted #5A9A98;
  	background-image: url(<html:imagesPath/>pics/login_bk.png);
  	background-repeat: no-repeat;
  	width: 480px;
  	height: 190px;
    -moz-border-radius: 2%;
	} 
fieldset.ss_fieldset_square {
  	border: 1px solid #5A9A98;
  	padding: 5px;
  	margin-top: 12px;
	}
fieldset a{
	border: dotted 1px #000;
	}
fieldset a:hover{
	border: dotted 1px #000;
	}
.ss_legend {
  	font-size:12px !important;
  	}
 /**********LOGIN SCREEN*******/
.ss_legend_login {
  	font-size:24px !important;
	letter-spacing:-1px;
	font-family:Georgia, "Times New Roman", Times, serif !important;
	font-weight:normal !important;
	color:#666;
	}
 /*FORM LABELS (these need to be consolidated */	
.ss_labelAbove {
  	padding-top: 2px;
  	padding-bottom: 2px;
  	display: block;
  	font-weight: bold;
	}
 /**********MINIBLOG******/	
.ss_labelminiBlog {
  	padding-top: 10px;
  	color: #333;
	}	
.ss_list-style-image_miniblog {
	list-style-image: url(/ssf/i/icwg/pics/blog/chatballoon.png)!important;
	padding-top: 10px;
  	color: #555;
	}
/******END******/	
.ss_labelLeft {
  	font-weight: bold;
  	font-size: ${ss_style_font_normalprint};
  	display: inline;
  	padding-right: 2px;
	}
.ss_labelRight {
  	font-weight: bold;
  	display: inline;
  	padding-left: 2px;
  	font-size: ${ss_style_font_normalprint};
	}

/* Text styled as buttons */

.ss_inlineButton {
  cursor: pointer;
 <c:if test="<%= isIE %>">
  height: 20px;
 </c:if>
 <c:if test="<%= !isIE %>">
  /*height: 18px;*/
 </c:if>
  text-align: center;
  padding: 0px 6px 0px 6px;
  font-size: 10px !important;
  font-family: Arial, sans-serif;
  white-space: nowrap;
  text-decoration: none !important;
  border: 1px solid #5A9A98;
  background-image: url(<html:imagesPath/>pics/background_inline_button_blue.gif);
  background-repeat: repeat-x;
}
.ss_inlineButton ss_reset {
	position:relative;
	display:block;
	white-space:nowrap;
	margin:2px 0 0 0;
	}
.ss_inlineButton:hover {
  border: 1px dotted #333;
  color: #999;
}
.ss_inlineButton span {
  padding: 0px 6px 0px 6px;
  text-align: center;
}
.ss_inlineButton img {
  margin: 0px 0px 0px 0px;
}