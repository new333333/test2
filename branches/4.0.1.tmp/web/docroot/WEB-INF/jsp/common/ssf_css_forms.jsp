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


/* General CSS */

input[type="submit"] {
	<c:choose>
		<c:when test="<%= isIE %>">
			/* IE does not support the min-width attribute. */
		</c:when>
		<c:otherwise>
			min-width: 50px;
		</c:otherwise>
	</c:choose>
}

td.ss_cellvalign {
	vertical-align: top;
	padding-top: 3px;
	}

/* Object List Table styles */

.objlist  /*   apply to the object list table tag  */ { margin-bottom: 1em; }
.objlist td  /* applies a "nowrap" style to all TD cells in a object list table */ {
	white-space: nowrap;
	}
.objlist td.wrapOK  /* Apply this class to a TD to let the TD wrap normally. */ {
	white-space: normal;
	}
.objlist tr.title td  	 /* apply to the first row in table */
	 { color: #353838; 
	 font-size: 1.1em;
	 font-weight: bold;
	 background-color: #ccc;
	 text-align: left; 
	 text-indent: 0.15em;
	 letter-spacing: 1px; 
	 padding: 3px;
	-moz-border-radius-topleft: 3px;
	-moz-border-radius-topright: 3px;
	border-top-left-radius: 3px;
	border-top-right-radius: 3px;
	-webkit-border-top-left-radius: 3px;
	-webkit-border-top-right-radius: 3px;
	}
	 
.objlist tr.menu td	/* apply to the row after the title */ 
	{ background-color: #efeeec; padding-top: 3px;  padding-left: 3px; padding-bottom: 5px; font-size: 1em;}
	
.objlist tr.menu a	/* menu style will automaticallly apply to all links within the table row*/ 
	{ text-decoration: none; padding: 2px 5px; padding-bottom: 1px !important; }

.objlist div.actions a:hover { color: white; background-color: #5e919e;}

.objlist tr.columnhead td, .objlist2 tr.columnhead td	/* Apply to the column headings row following the menu */ {
	background: #ededed;
	border-bottom: 1px solid #D2D5D1;
	border-right: 1px solid #D2D5D1;
	font-size: 11px !important;
	text-align: left;
	padding: 3px 5px;
	white-space: nowrap;
	overflow: visible;
	color: #505354 !important;
	}

.objlist tr.columnhead a { text-decoration: none; }
.objlist tr.columnhead a.nv-sortedcol { font-weight: bold; }
.objlist tr.columnhead a.nv-sortcol { font-weight: normal; }
.objlist tr.columnhead td.nv-nonsortcol { color: #666666; font-weight: normal; }


.objlist tr.regrow	/*Apply to all the content rows.  Style will automatically flow to each cell td. If a cell is empty, place a &nbsp; (none braking space) in the cell so the line style will continue across all cells in the row. */ 
	{ background-color: white; } /* #babdb6 */

.objlist tr.regrow td	/*Apply to all the content rows.  Style will automatically flow to each cell td. If a cell is empty, place a &nbsp; (none braking space) in the cell so the line style will continue across all cells in the row. */ 
	{ font-size: 1em; padding: 3px 5px; border-bottom: 1px solid #dbdbd3; } /* #babdb6 */

.objlist tr.no-regrow td	/* use to override regrow border */ 
	{ font-size: 1em; padding: 3px 5px; border-bottom: 0px; } 
		
.objlist tr.regrow a {	color: ${ss_style_link_hover_color}; }

.objlist tr.overrow 	/*roll over a row to change the background color*/ 
	{ background-color: #F6F6F6; }

.objlist tr.selectrow 	/*select a row to change the background color*/ 
	{ background-color: #CCDAE8; }

.objlist tr.footrow td	/*Apply to all the content rows.  Style will automatically flow to each cell <td>. If a cell is empty, place a &nbsp; (none braking space) in the cell so the line style will continue across all cells in the row. */ 
	{ background-color: #f6f6f6; color: #666666; letter-spacing: .1em; font-size:0.7em; padding: 3px 5px; border-bottom: 1px solid #bbbbb9; } /* #babdb6 */

tr.ends td   
	{ border-right: 1px solid #bbbbb9; 
	border-left: 1px solid #bbbbb9}	
			
tr.titleends td   
	{ border-right: 1px solid #173751; 
	border-left: 1px solid #173751}		
		
td.leftend    
	{ border-left: 1px solid #bbbbb9; }
				
td.rightend   
	{ border-right: 1px solid #bbbbb9; }
.objlist tr.regrow td.righton { color: black; font-weight: 700; text-transform: uppercase; cursor: help; }
.objlist tr.regrow td.rightoff { color: #bbbbb9; font-weight: normal; text-transform: uppercase; cursor: help; }
.objlist tr.regrow td span.instructions { font-size: 0.95em; }




.ss_form_header {
	color:#036f9f;
	font-family: ${ss_style_font_family};
	font-size: 15px !important;
	font-weight: normal;
	letter-spacing: .03em;
	padding-top: 2px;
	margin-bottom: 10px;
	border-bottom: 1px solid #e0e0e0;
	}
div.ss_form_header table td {
	color:#036f9f;
	font-family: ${ss_style_font_family};
	font-size: 15px !important;
	font-weight: normal;
	letter-spacing: .03em;
	padding-top: 2px;
	margin-bottom: 10px;
	}
.ss_table_wrap {
	display: table;
	border-top-width: 24px;
	border-right-width: thin;
	border-bottom-width: thin;
	border-left-width: thin;
	border-style: solid;
	border-top-color: #5691A6;
	border-right-color: #CCCCCC;
	border-bottom-color: #CCCCCC;
	border-left-color: #CCCCCC;
	padding: 0 25px 25px 25px!important;
	}
.ss_form_wrap {
/*	background: transparent url(<html:imagesPath/>pics/dialog_header_tile.png) repeat-x;  */
/*	background-position: top; */
	background-color: #fff;
	padding: 0 15px 15px 15px!important;
/*	border-top-left-radius: 5px;
	border-top-right-radius: 5px;
	-moz-border-radius-topleft: 5px;
	-moz-border-radius-topright: 5px;
	-webkit-border-top-left-radius: 5px;
	-webkit-border-top-right-radius: 5px;  */
	}
.ss_form_element {
	padding-top: 10px;
}
.tab_form .ss_form_wrap {
	border-top-width: 0px;
	border-right-width: 0px;
	border-bottom-width: 0px;
	border-left-width: 0px;
	padding: 0px 15px 0px 0px !important;
	background: transparent !important;
	}	
	
.ss_form_wrap_compact {
	border-top-width: 24px;
	border-right-width: thin;
	border-bottom-width: thin;
	border-left-width: thin;
	border-style: solid;
	border-top-color: #5691A6;
	border-right-color: #CCCCCC;
	border-bottom-color: #CCCCCC;
	border-left-color: #CCCCCC;
	padding: 0 6px 6px 6px!important;
	margin: 6px 6px 6px 6px;
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
.ss_formButtonRight {
	background-position: right;
	text-align: right;
	}
.ss_formButtonLeft {
	background-position: left;
	text-align: left;
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
	padding: 3px;
	margin: 6px;
	}
.ss_text_login a:hover{
	background: #666;
	}
.ss_text_mashup{
	font-family: ${ss_style_font_family};
	font-size: 10px;
	text-align: left;
	padding: 3px;
	margin: 6px;
	}
.ss_text_mashup a:hover{
	background: #666;
	}
fieldset{
  	margin-top: 40px !important;
  	margin-bottom: 40px !important;
    margin-top: 6px !important;
    margin-bottom: 4px !important;
    padding: 10px !important;
    border: 1px solid ${ss_style_border_color_light} !important;
	background-color: #f6f6f6;
	} 
.ss_fieldset_login {
  	margin-top: 80px;
  	margin-bottom: 100px;
 	padding-bottom:12px;
  	padding-top: 18px;
  	display: block;
  	background-image: url(<html:imagesPath/>pics/login_bk.png);
  	background-repeat: repeat;
  	width: 480px;
  	height: 190px;
    -moz-border-radius: 2%;
	} 
.ss_fieldset_mashup {
  	display: inline;
	} 
fieldset.ss_fieldset_square {
  	border: 1px solid #5691A6;
  	padding: 5px;
  	margin-top: 12px;
	}
fieldset a {
	}
fieldset a:hover {
	}
.ss_legend {
  	font-size:12px !important;
	font-weight: bold;
  	}
 /**********LOGIN SCREEN*******/
.ss_legend_login {
  	font-size:24px !important;
	letter-spacing:-1px;
	font-family:Georgia, "Times New Roman", Times, serif !important;
	font-weight:normal !important;
	color:#666;
	}
.ss_legend_mashup {
  	font-size:16px !important;
	letter-spacing:-1px;
	font-family:Georgia, "Times New Roman", Times, serif !important;
	font-weight:normal !important;
	color:#666;
	}
 /*FORM LABELS (these need to be consolidated */	
.ss_labelAbove {
  	padding-top: 8px;
  	padding-bottom: 2px;
  	display: block;
  	font-weight: bold;
	}
	
.ss_surveyForm {
	margin: 5px 5px 20px 5px;
}	
 /**********MINIBLOG******/	
.ss_labelminiBlog {
  	padding-top: 10px;
  	color: #333;
	}	
.ss_list-style-image_miniblog {
	list-style-image: url(<html:imagesPath/>pics/blog/chatballoon.png)!important;
	padding-top: 10px;
  	color: #555;
	}
/******END******/	
.ss_labelLeft {
  	font-weight: bold;
  	font-size: ${ss_style_font_normalprint};
  	display: inline-block;
  	padding-right: 5px;
	padding-top: 10px;
	}
.ss_labelRight {
  	font-weight: bold;
  	display: inline;
  	padding-left: 2px;
  	font-size: ${ss_style_font_normalprint};
	}

.ss_upButton {
	display: block;
	width: 16px;
	height: 16px;
	background: url(<html:imagesPath/>icons/arrow_up.png) no-repeat 0 0;
	}
.ss_upButton:hover {
	cursor: pointer;
	background: url(<html:imagesPath/>icons/arrow_up_over.png) no-repeat 0 0;
	}	
.ss_downButton {
	display: block;
	width: 16px;
	height: 16px;
	background: url(<html:imagesPath/>icons/arrow_down.png) no-repeat 0 0;
	}
.ss_downButton:hover {
	cursor: pointer;
	background: url(<html:imagesPath/>icons/arrow_down_over.png) no-repeat 0 0;
	}	

/* Text styled as buttons */

.ss_inlineButton, input[type="reset"].ss_inlineButton, input[type="button"].ss_inlineButton, input[type="submit"].ss_inlineButton, button.ss_inlineButton {
  cursor: pointer;
  text-align: center;
  padding: 0px 6px 0px 6px;
 <c:if test="<%= isIE %>">
  	height: 20px;
  	padding: 0px 1px 0px 1px;
 </c:if>  
  font-size: 10px !important;
  font-family: Arial, sans-serif;
  white-space: nowrap;
  text-decoration: none !important;
  border: 1px solid #5691A6;
  background-image: url(<html:imagesPath/>pics/background_inline_button_blue.gif);
  background-repeat: repeat-x;
}
.ss_inlineButton ss_reset {
	position:relative;
	display:block;
	white-space:nowrap;
	margin:2px 0 0 0;
	}
.ss_inlineButton:hover, input[type="reset"].ss_inlineButton:hover, input[type="button"].ss_inlineButton:hover, input[type="submit"].ss_inlineButton:hover, button.ss_inlineButton:hover {
  color: #135c8f;
}
.ss_inlineButton span {
  padding: 0px 6px 0px 6px;
  text-align: center;
}
.ss_inlineButton img {
  margin: 0px 0px 0px 0px;
}

.ss_inlineButtonSmall, input[type="reset"].ss_inlineButtonSmall, input[type="button"].ss_inlineButtonSmall, input[type="submit"].ss_inlineButtonSmall, button.ss_inlineButtonSmall {
  cursor: pointer;
  text-align: center;
  padding: 0px 4px 0px 4px;
 <c:if test="<%= isIE %>">
  	height: 16px;
  	padding: 0px 1px 0px 1px;
 </c:if>  
  font-size: 9px !important;
  font-family: Arial, sans-serif;
  white-space: nowrap;
  text-decoration: none !important;
  border: 1px solid #5691A6;
  background-image: url(<html:imagesPath/>pics/background_inline_button_blue.gif);
  background-repeat: repeat-x;
}
.ss_inlineButtonSmall ss_reset {
	position:relative;
	display:block;
	white-space:nowrap;
	margin:2px 0 0 0;
	}
.ss_inlineButtonSmall:hover, input[type="reset"].ss_inlineButtonSmall:hover, input[type="button"].ss_inlineButtonSmall:hover, input[type="submit"].ss_inlineButtonSmall:hover, button.ss_inlineButtonSmall:hover {
  color: #135c8f;
}
.ss_inlineButtonSmall span {
  padding: 0px 4px 0px 4px;
  text-align: center;
}
.ss_inlineButtonSmall img {
  margin: 0px 0px 0px 0px;
}

.designer_dialog_title {
	color: #fff;
	display: block;
	position: absolute;
	top: 7px;
	padding-right: 15px;
	width: 220px;
	overflow: hidden;
	white-space: nowrap;
	}