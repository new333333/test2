<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page contentType="text/xml" %>
<%@ page import="com.sitescape.ef.domain.Folder" %>
<jsp:useBean id="forums" type="java.util.List" scope="request" />
<jsp:useBean id="unseenCounts" type="java.util.Map" scope="request" />
<taconite-root xml:space="preserve">
<c:forEach var="f" items="${forums}" >
<c:set var="fid" value="${f.id}" />
<c:set var="fid2" value="this is good" />
<jsp:useBean id="fid" type="java.lang.String" />
<jsp:useBean id="fid2" type="java.lang.String" />
	<span>This is bogus: <%= fid %></span>
	<span>This is good: <%= fid2 %></span>
</c:forEach >
<%
	for (int i = 0; i < forums.size(); i++) {
		Folder forum = (Folder)forums.get(i);
%>
	<taconite-replace contextNodeID="count_<%= forum.getId().toString() %>" parseInBrowser="true">
		<span id="count_<%= forum.getId().toString() %>"><%= unseenCounts.get(forum.getId().toString()) %></span>
	</taconite-replace>
<%
	}
%>
</taconite-root>
