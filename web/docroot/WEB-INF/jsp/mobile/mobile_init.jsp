<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
%><%--
--%><%@ page import="org.kablink.util.BrowserSniffer" %><%--
--%><%
	boolean isWap = BrowserSniffer.is_wap_xhtml(request);
	boolean isIPhone = BrowserSniffer.is_iphone(request);
	boolean isIPad = BrowserSniffer.is_ipad(request);
%><%--
--%><%@ include file="/WEB-INF/jsp/common/common.jsp" %><%--
--%><c:set var="ssf_snippet" value="1" scope="request"/><%--
--%><%@ page contentType="text/html; charset=UTF-8" %><%--
--%><!DOCTYPE html<% if (org.kablink.teaming.web.util.MiscUtil.isHtmlQuirksMode()) { %> PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"<% } %>>
<html <c:if test="${!empty ssUser && !empty ssUser.locale}"> lang="${ssUser.locale}"</c:if>>
<head>
<meta http-equiv="Content-Script-Type" content="text/javascript"/>
<meta http-equiv="Content-Style-Type" content="text/css"/>
<meta name="viewport" content="initial-scale=1.0; maximum-scale=1.0; user-scalable=0"/>
<jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_mobile_head.jsp" />
<c:if test="${!empty ss_windowTitle}"><title>${ss_windowTitle}</title></c:if>
<script type="text/javascript" 
  src="<html:rootPath/>js/common/ss_mobile.js?startTime=<%= org.kablink.teaming.util.ReleaseInfo.getStartTime() %>"></script>
<script type="text/javascript">	
ss_createOnLoadObj("ss_scrollToTop", function() {
  		setTimeout(function(){window.scrollTo(0, 1);}, 100);
	});
</script>  
<c:set var="ss_isMobile" value="true" scope="request" />
<c:if test="${!empty ss_mashupCss}">
  <link href="<html:rootPath/>${ss_mashupCss}" rel="stylesheet" type="text/css" />
</c:if>
<link href="<html:rootPath/>js/gwt/gwtteaming/LegacyJspSupport.css"          rel="stylesheet" type="text/css" />
<link href="<html:rootPath/>css/ss_mobile_common.css" rel="stylesheet" type="text/css" />
<link href="<html:rootPath/>css/ss_mobile_iphone.css" rel="stylesheet" type="text/css" />
<%
	if (isWap) {
%>
<link href="<html:rootPath/>css/ss_mobile_wap.css" rel="stylesheet" type="text/css" />
<%
	} else {
%>
<link href="<html:rootPath/>css/ss_mobile_browser.css" rel="stylesheet" type="text/css" />
<%
	}
%>
</head>
<body style="margin: 0;">
