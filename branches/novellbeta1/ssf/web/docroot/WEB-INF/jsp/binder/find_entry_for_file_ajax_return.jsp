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

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
  <taconite-replace contextNodeID="ss_duplicateFileCheck" 
	parseInBrowser="true">
	<c:if test="${empty ssEntryTitle}">
		<div id="ss_duplicateFileCheck" style="display:none"><span class="ss_formError"></span></div>
	</c:if>
	<c:if test="${!empty ssEntryTitle}">
		<div id="ss_duplicateFileCheck" style="display:block"><span class="ss_formError"><ssf:nlt tag="entry.duplicateFileInLibrary"/> <span class="ss_bold">${ssEntryTitle}</span></span></div>
	</c:if>
  </taconite-replace>

</c:if>
</taconite-root>
