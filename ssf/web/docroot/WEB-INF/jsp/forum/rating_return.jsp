<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<taconite-root xml:space="preserve">
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
	<taconite-replace contextNodeID="${ss_ratingDivId}" parseInBrowser="true">
<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
	</taconite-replace>
<%
	}
%>	
</taconite-root>
