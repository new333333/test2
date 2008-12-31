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

--%><%@ include file="/WEB-INF/jsp/common/common.jsp" %><%--
--%><%@ page contentType="text/html; charset=UTF-8" %><%--

--%><c:set var="ssf_support_files_loaded_flag" value=""/><%--
--%><ssf:ifadapter><%--
	--%><c:if test="${empty ssf_support_files_loaded}"><%--
	    --%><c:set var="ssf_support_files_loaded_flag" value="1"/><%--
--%><c:if test="${empty ssf_snippet}"><%--
    --%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html <c:if test="${!empty ssUser && !empty ssUser.locale}"> lang="${ssUser.locale}"</c:if>>
<head>
<jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_head.jsp" />
<meta http-equiv="Content-Script-Type" content="text/javascript"/>
<meta http-equiv="Content-Style-Type" content="text/css"/>
<c:if test="${!empty ss_windowTitle}"><title>${ss_windowTitle}</title></c:if>
</c:if><%--
	--%></c:if><%--
--%></ssf:ifadapter><%--

--%><c:if test="${empty ssf_snippet}"><%--
	--%><%@ include file="/WEB-INF/jsp/common/view_css.jsp" %><%--
--%></c:if><%--

--%><ssf:ifadapter><%--
	--%><c:if test="${ssf_support_files_loaded_flag == '1'}"><%--
		--%><c:if test="${empty ssf_snippet}"><%--
			--%></head><%--
		--%></c:if><%--
	--%></c:if><%--
--%></ssf:ifadapter><%--

--%><c:set var="ssf_support_files_loaded" value="1" scope="request"/><%--

--%><c:if test="${!empty ssDownloadURL}"><%--
	--%><script type="text/javascript"><%--
	--%>window.open("${ssDownloadURL}", "session", "directories=no,height=10,location=no,menubar=no,resizable=no,scrollbars=no,status=no,toolbar=no,width=10")<%--
	--%></script><%--
--%></c:if>