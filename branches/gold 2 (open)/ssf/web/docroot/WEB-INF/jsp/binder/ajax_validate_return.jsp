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
  <c:if test="${empty ssAjaxErrorMessage}">
    <taconite-replace contextNodeID="${ss_ajaxMsgId}" 
	  parseInBrowser="true">
		<div id="${ss_ajaxMsgId}" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div>
    </taconite-replace>
    <taconite-set-attributes contextNodeID="${ss_ajaxLabelId}" 
	  parseInBrowser="true" style="color:black"/>
  </c:if>
  <c:if test="${!empty ssAjaxErrorMessage}">
    <taconite-replace contextNodeID="${ss_ajaxMsgId}" 
	  parseInBrowser="true">
		<div id="${ss_ajaxMsgId}" style="display:block; visibility:visible;" ss_ajaxResult="error"><span class="ss_formError"><ssf:nlt tag="${ssAjaxErrorMessage}"/> <span class="ss_bold">${ssAjaxErrorDetail}</span></span></div>
    </taconite-replace>
    <taconite-set-attributes contextNodeID="${ss_ajaxLabelId}" 
	  parseInBrowser="true" style="color:red"/>
  </c:if>
</c:if>
</taconite-root>
