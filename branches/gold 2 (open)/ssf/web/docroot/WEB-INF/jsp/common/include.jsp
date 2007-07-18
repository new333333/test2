<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%><%--

--%><%@ include file="/WEB-INF/jsp/common/common.jsp" %><%--
--%><%@ page contentType="text/html; charset=UTF-8" %><%--

--%><%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %><%--
--%><%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %><%--

--%><portletadapter:defineObjects1/><%--
--%><%

//Set up the user object
if (com.sitescape.team.context.request.RequestContextHolder.getRequestContext() != null) {
	com.sitescape.team.domain.User user = com.sitescape.team.context.request.RequestContextHolder.getRequestContext().getUser();
	request.setAttribute("ssUser", user);
}

%><%--
--%><c:set var="ssf_support_files_loaded_flag" value=""/><%--
--%><ssf:ifadapter><%--
	--%><c:if test="${empty ssf_support_files_loaded}"><%--
	    --%><c:set var="ssf_support_files_loaded_flag" value="1"/><%--
	    --%><c:if test="${empty ssf_snippet}"><%--
	        --%><html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable"><%--
	        --%><head><%--
	    --%></c:if><%--
	--%></c:if><%--
	--%><portletadapter:defineObjects2/><%--
--%></ssf:ifadapter><%--

--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter><%--

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