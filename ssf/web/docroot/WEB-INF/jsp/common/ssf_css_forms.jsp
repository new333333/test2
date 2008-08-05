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



form_header {
	font-size:28px;
	letter-spacing:-1px;
	font-family:Georgia, "Times New Roman", Times, serif;
	font-weight:normal;
	color:#666;
	letter-spacing: -1px;
	}
ss_h2 {
	font-size: 24px;
	letter-spacing: -1px;
}


.ss_formWrap {
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
	padding-right: 25px;
	padding-bottom: 25px;
	padding-left: 25px;
}

.ss_FormTableHeading {
	font-size: 10px;
	font-weight: bold;
	color: #333333;
	background-color: #cccccc;
	text-align: center;
	letter-spacing: normal;
	border-right-style: none;
	border-top-style: none;
	border-bottom-style: none;
	border-left-style: none;
}
.ss_oddRow {
	background-color: #E4EFEF;
	display: table-row;
}
.ss_dataTable {
	font-family: ${ss_style_font_family};
	font-size: 10px;
	letter-spacing: 1px;
	text-align: left;
	display: inline-table;
	margin: 0px;
	padding-top: 0px;
	padding-right: 0px;
	padding-bottom: 0px;
	padding-left: 0px;
	border: 0.1px solid #EFEFEF;
}
.ss_dataTableTD {
	font-family: ${ss_style_font_family};
	font-size: 10px;
	letter-spacing: normal;
	text-align: left;
	display: table-cell;
	border-top-width: thin;
	border-right-width: thin;
	border-bottom-width: thin;
	border-left-width: thin;
	border-top-style: none;
	border-right-style: none;
	border-bottom-style: solid;
	border-left-style: none;
	border-top-color: #999999;
	border-right-color: #999999;
	border-bottom-color: #999999;
	border-left-color: #999999;
	padding-top: 6px;
	padding-right: 0px;
	padding-bottom: 6px;
	padding-left: 0px;
	margin: 2px;
}
.ss_dataTableMid {
	font-family: ${ss_style_font_family};
	font-size: 10px;
	letter-spacing: normal;
	text-align: center;
	display: inline-table;
	padding-right: 2px;
	padding-left: 2px;
	border-bottom-width: thin;
	border-bottom-style: solid;
	border-bottom-color: #999999;
	padding-bottom: 4px;
	padding-top: 4px;
	margin: 2px;
}
.ss_formButton {
	background-position: right;
	text-align: right;
}
