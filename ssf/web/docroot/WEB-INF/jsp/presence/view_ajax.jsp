<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<taconite-root xml:space="preserve">
<c:if test="${!empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">

	<taconite-replace contextNodeID="ss_presence_status_message" parseInBrowser="true">
		<div id="ss_presence_status_message" style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
</c:if>
<c:if test="${empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">
	<taconite-replace contextNodeID="ss_presence_status_message" parseInBrowser="true">
		<div id="ss_presence_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
</c:if>
	
<c:forEach var="user" items="${ssUsers}">
<jsp:useBean id="user" type="com.sitescape.ef.domain.User" />

	<taconite-replace contextNodeID="count_${user.id}" 
	parseInBrowser="true"><span id="count_${user.id}">
	<ssf:presenceInfo user="<%=user%>"/></span></taconite-replace>
</c:forEach>
</taconite-root>
