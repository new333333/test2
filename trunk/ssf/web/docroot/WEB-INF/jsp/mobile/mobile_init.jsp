<%
/**
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
%><%--
--%><%@ page import="com.sitescape.util.BrowserSniffer" %><%--
--%><%
	boolean isWap = BrowserSniffer.is_wap_xhtml(request);
%><%--
--%><%@ include file="/WEB-INF/jsp/common/common.jsp" %><%--
--%><c:set var="ssf_snippet" value="1" scope="request"/><%--
--%><%@ page contentType="text/html; charset=UTF-8" %><%--
--%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html <c:if test="${!empty ssUser && !empty ssUser.locale}"> lang="${ssUser.locale}"</c:if>>
<head>
<META http-equiv="Content-Script-Type" content="text/javascript">
<META http-equiv="Content-Style-Type" content="text/css">
<jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_mobile_head.jsp" />
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
<script type="text/javascript">
var ss_tryToResizeLater = 1;
function ss_resizeMobileIframe() {
	if (typeof self.parent != 'undefined' && typeof parent.ss_setMobileIframeSize != 'undefined') {
		setTimeout("self.parent.ss_setMobileIframeSize();", 200)
	} else {
		if (ss_tryToResizeLater == 1) {
			ss_tryToResizeLater = 0
			setTimeout("self.ss_resizeMobileIframe();", 500)
		}
	}
}
</script>
</head>
<body onLoad="ss_resizeMobileIframe();">
