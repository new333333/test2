<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="${ssNamespace}_refreshDate" 
	parseInBrowser="true"><div id="${ssNamespace}_refreshDate">
<span class="ss_smallprint ss_gray"><ssf:nlt 
tag="presence.last.refresh"/> <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
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
