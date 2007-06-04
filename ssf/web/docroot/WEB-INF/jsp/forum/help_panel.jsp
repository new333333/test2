<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page session="false" %>
<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
	<taconite-replace contextNodeID="${ss_help_panel_id}" parseInBrowser="true">
<div id="${ss_help_panel_id}" class="ss_helpPanel">
<%@ include file="/WEB-INF/jsp/help/help_popup_panel_top.jsp" %>
<jsp:include page="${ss_help_panel_jsp}" />
<%@ include file="/WEB-INF/jsp/help/help_popup_panel_bottom.jsp" %>
</div>
	</taconite-replace>
</c:if>
</taconite-root>
