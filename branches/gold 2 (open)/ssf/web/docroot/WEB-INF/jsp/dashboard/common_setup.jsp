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
//This file is included for summary portlets, dashboards and ajax callbacks for both
//In the portlet case include.jsp has already been called and include files have been handled
//In the dashboard case if not in an adapter, the variables to skip the includes are setup
//In the ajax callback case, we don't want to send the includes
//So in all cases at this point, all files included from "include.jsp" are handled.
//Set flags to skip them, but still need rest of the setup in "include.jsp"
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ssf_snippet" value="1" scope="request"/>
<c:set var="ssf_support_files_loaded" value="1" scope="request"/>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:if test="${empty ss_namespace}">
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
</c:if>
<c:set var="componentId" value="${ssComponentId}" scope="request"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" scope="request"/>
</c:if>

<c:set var="ss_divId" value="ss_results_${ss_namespace}_${componentId}"  scope="request"/>
