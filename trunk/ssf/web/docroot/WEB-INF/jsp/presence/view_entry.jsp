<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<taconite-root xml:space="preserve">
<c:if test="${!empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">

	<taconite-replace contextNodeID="ss_presence_status_message${ssDashboardId}" 
	  parseInBrowser="true">
		<div id="ss_presence_status_message${ssDashboardId}" 
		  style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
</c:if>
<c:if test="${empty ss_ajaxStatus[ss_ajaxNotLoggedIn]}">
	<taconite-replace contextNodeID="ss_presence_status_message${ssDashboardId}" 
	  parseInBrowser="true">
		<div id="ss_presence_status_message${ssDashboardId}" 
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