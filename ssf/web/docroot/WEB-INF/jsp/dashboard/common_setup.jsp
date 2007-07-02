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
%>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifnotadapter>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
</ssf:ifnotadapter>
<c:set var="componentId" value="${ssComponentId}" scope="request"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" scope="request"/>
</c:if>

<c:if test="${empty ss_divId}">
<c:set var="ss_divId" value="ss_results_${ss_namespace}_${componentId}"  scope="request"/>
</c:if>