<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/xml" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="${ss_ratingDivId}" parseInBrowser="true">
<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
	</taconite-replace>
</c:if>
</taconite-root>
