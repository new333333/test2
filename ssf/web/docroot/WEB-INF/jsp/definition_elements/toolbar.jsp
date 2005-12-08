<% // Toolbar %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_toolbar" type="java.util.SortedMap" scope="request" />
<c:set var="toolbar" value="${ss_forum_toolbar}" scope="request" />
<%@ include file="/jsp/definition_elements/toolbar_view.jsp" %>
