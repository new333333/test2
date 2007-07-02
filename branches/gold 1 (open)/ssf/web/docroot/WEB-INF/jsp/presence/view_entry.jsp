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
<taconite-root xml:space="preserve">
<c:if test="${!empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">

	<taconite-replace contextNodeID="ss_presence_status_message${ssComponentId}" 
	  parseInBrowser="true">
		<div id="ss_presence_status_message${ssComponentId}" 
		  style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
</c:if>
<c:if test="${empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">
	<taconite-replace contextNodeID="ss_presence_status_message${ssComponentId}" 
	  parseInBrowser="true">
		<div id="ss_presence_status_message${ssComponentId}" 
		  style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
</c:if>
	<taconite-replace contextNodeID="ss_presence_view_entry" 
	parseInBrowser="true">
	<div id="ss_presence_view_entry" style="visibility:visible display:block">
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  processThisItem="true" 
  entry="${ssEntry}" />
	
</div>
 </taconite-replace>
 </taconite-root>