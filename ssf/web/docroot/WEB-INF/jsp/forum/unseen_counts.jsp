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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<c:set var="ss_showUnseenNote" value="0"/>
	<c:forEach var="folderId" items="${ss_unseenCountsBinderIds}">
	  <c:set var="folderIdFound" value="0"/>
	  <c:forEach var="entry" items="${ss_unseenCounts}">
	    <c:if test="${entry.key.id == folderId}">
	      <c:set var="folderIdFound" value="1"/>
	    </c:if>
	  </c:forEach>
	  <c:if test="${folderIdFound == '0'}">
	    <taconite-replace contextNodeID="${ssNamespace}_count_${folderId}" 
	    parseInBrowser="true"><span id="${ssNamespace}_count_${folderId}" 
	    >*</span></taconite-replace>
	    <c:set var="ss_showUnseenNote" value="1"/>
	  </c:if>
	</c:forEach>
	
	<c:forEach var="entry" items="${ss_unseenCounts}">
	<taconite-replace contextNodeID="${ssNamespace}_count_${entry.key.id}" 
	parseInBrowser="true"><span id="${ssNamespace}_count_${entry.key.id}" 
	>${entry.value}</span></taconite-replace>
	</c:forEach>
	<c:if test="${ss_showUnseenNote == '1'}">
	  <taconite-replace contextNodeID="${ssNamespace}_note" parseInBrowser="true">
	    <div id="${ssNamespace}_note" class="ss_indent_large">
	    <span>* <ssf:nlt tag="unseen.foldersOnly"/></span>
	    </div>
	  </taconite-replace>
	</c:if>
</c:if>
</taconite-root>
