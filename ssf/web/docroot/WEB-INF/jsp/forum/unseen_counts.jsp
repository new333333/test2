<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="com.sitescape.ef.domain.Folder" %>
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="java.util.Iterator" %>
<jsp:useBean id="ss_unseenCounts" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<taconite-root xml:space="preserve">
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">error</div
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
<%
	}
	
	for (Iterator iter=ss_unseenCounts.entrySet().iterator(); iter.hasNext();) {
		Map.Entry entry = (Map.Entry)iter.next();
		Folder forum = (Folder)entry.getKey();
%>
	<taconite-replace contextNodeID="count_<%= forum.getId().toString() %>" parseInBrowser="true">
		<span id="count_<%= forum.getId().toString() %>"><%= entry.getValue() %></span>
	</taconite-replace>
<%
	}
%>
</taconite-root>
