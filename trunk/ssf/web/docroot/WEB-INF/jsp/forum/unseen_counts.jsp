<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<c:forEach var="entry" items="${ss_unseenCounts}">
	<taconite-replace contextNodeID="${ssNamespace}_count_${entry.key.id}" 
	parseInBrowser="true"><span id="${ssNamespace}_count_${entry.key.id}" 
	>${entry.value}</span></taconite-replace>
	</c:forEach>
</c:if>
</taconite-root>
