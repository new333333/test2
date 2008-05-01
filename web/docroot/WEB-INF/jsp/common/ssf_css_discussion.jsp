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
	background-color:#eee;
	text-align:left;
	}
#ss_column_L{
	/* holder for left column */
	}
#ss_column_R{
	/* holder for left column */
	height:100%;
	}	
.ss_doublecolumn .ss_colleft{		/* right column width */
	right:50%;		
	}
.ss_doublecolumn .ss_col1{
	width:49%;						/* left column content width (column width minus left and right padding) */
	left:50%;						/* (right column width) plus (left column padding) */
	background-color:#fff;			/* left column background color */
	}	
.ss_doublecolumn .ss_col2{
	width:49%;						/* right column content width (column width minus left and right padding) */
	left:51%;						/* (right column column width) plus (left column left and right padding) plus (right column left padding) */
	overflow:hidden;
	}	
		
