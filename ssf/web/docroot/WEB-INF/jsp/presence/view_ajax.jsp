<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<taconite-root xml:space="preserve">
<c:if test="${!empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">

	<taconite-replace contextNodeID="ss_status_message" 
	  parseInBrowser="true">
		<div id="ss_status_message" 
		  style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
</c:if>
<c:if test="${empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">
	<taconite-replace contextNodeID="ss_status_message" 
	  parseInBrowser="true">
		<div id="ss_status_message" 
		  style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
	<taconite-replace contextNodeID="${ssNamespace}_refreshDate" 
	parseInBrowser="true"><div id="${ssNamespace}_refreshDate">
<span class="ss_smallprint ss_gray"><ssf:nlt 
tag="presence.last.refresh"/> <fmt:formatDate 
value="<%= new java.util.Date() %>" type="time" /></span>
</div></taconite-replace>

<c:forEach var="user" items="${ssUsers}">
<jsp:useBean id="user" type="com.sitescape.ef.domain.User" />

	<taconite-replace contextNodeID="${ssNamespace}_user_${user.id}" 
	parseInBrowser="true"><span id="${ssNamespace}_user_${user.id}"
	><ssf:presenceInfo user="<%=user%>" componentId="${ssDashboardId}" 
	/></span></taconite-replace>
</c:forEach>
</c:if>
</taconite-root>
