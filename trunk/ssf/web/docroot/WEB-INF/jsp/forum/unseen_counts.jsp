<%@ page session="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page contentType="text/xml" %>
<%@ page import="com.sitescape.ef.domain.Folder" %>
<%@ page import="com.sitescape.ef.util.NLT" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="java.util.Iterator" %>
<jsp:useBean id="ss_unseenCounts" type="java.util.Map" scope="request" />
<jsp:useBean id="ss_unseenStatus" type="java.util.Map" scope="request" />
<%
	//Cannot translate this. User is not logged in. Therefore, we do not have a locale.
	String notLoggedIn = "Your session timed out. Please log in again.";
%>
<taconite-root xml:space="preserve">
<%
	if (ss_unseenStatus.containsKey("ss_unseenNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="status_message" parseInBrowser="true">
		<div id="status_message" class="ss_labelLeftError">
		  <span><%= notLoggedIn %></span>
		</div>
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="status_message" parseInBrowser="true">
		<div id="status_message" ></div>
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
