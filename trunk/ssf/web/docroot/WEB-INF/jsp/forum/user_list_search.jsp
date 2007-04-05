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

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <ul id="<c:out value="${ss_divId}"/>" class="ss_dragable ss_userlist">
		<c:forEach var="entry" items="${ssUsers}">
		  <c:if test="${empty ssUserIdsToSkip[entry._docId]}">
		    <li id="<c:out value="${entry._docId}"/>" 
		      onDblClick="ss_userListMoveItem(this);" 
		      class="ss_dragable ss_userlist"><c:out value="${entry.title}"/></li>
		  </c:if>
		</c:forEach>
	  </ul>
	</taconite-replace>
</c:if>
</taconite-root>
