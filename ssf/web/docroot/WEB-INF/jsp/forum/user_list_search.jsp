<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="com.sitescape.ef.domain.Folder" %>
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="java.util.Iterator" %>
<jsp:useBean id="ssUsers" type="java.util.List" scope="request" />
<jsp:useBean id="ssUserIdsToSkip" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<taconite-root xml:space="preserve">
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" 
		 style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
	
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
<%
	}
%>
</taconite-root>
