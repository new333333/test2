<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page session="false" %>
<%@ page contentType="text/xml" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
	<taconite-replace contextNodeID="${ss_help_panel_id}" parseInBrowser="true">
<div id="${ss_help_panel_id}" class="ss_helpPanel">
<jsp:include page="${ss_help_panel_jsp}" />
</div>
	</taconite-replace>
</c:if>
</taconite-root>
