<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${listDivId}"/>" parseInBrowser="true">
	  <ul id="<c:out value="${listDivId}"/>" class="ss_dragable ss_userlist">
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
