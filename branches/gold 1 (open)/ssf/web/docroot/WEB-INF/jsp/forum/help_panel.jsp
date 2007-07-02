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
<%@ page contentType="text/html; charset=UTF-8" %>
<c:if test="${ss_help_panel_jsp != '/WEB-INF/jsp/help/welcome_panel.jsp'}">
<%@ include file="/WEB-INF/jsp/help/help_popup_panel_top.jsp" %>
</c:if>
<jsp:include page="${ss_help_panel_jsp}" />
<c:if test="${ss_help_panel_jsp != '/WEB-INF/jsp/help/welcome_panel.jsp'}">
<%@ include file="/WEB-INF/jsp/help/help_popup_panel_bottom.jsp" %>
</c:if>
