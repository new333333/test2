<%
// The dashboard "search" component
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
	<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:set var="ss_pageSize" value="${ssDashboard.beans[componentId].ssSearchFormData.ss_pageSize}" />
<c:set var="summaryWordCount" value="30"/>
<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}">
	<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}"/>
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<c:if test="${empty ss_namespace}">
<c:set var="ss_namespace" value="${portletNamespace}_${componentId}" />
</c:if>



<c:set var="ssFolderEntries" value="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}"/>
<c:set var="ssTotalRecords" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}" />
<c:set var="ssPageEndIndex" value="${ss_pageNumber * ss_pageSize + ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchRecordReturned}" />
<c:set var="ssPageStartIndex" value="${ss_pageNumber * ss_pageSize + 1}" />
<c:set var="isDashboard" value="yes"/>

<%@ include file="/WEB-INF/jsp/search/result_header.jsp" %>

<%@ include file="/WEB-INF/jsp/search/result_list.jsp" %>
