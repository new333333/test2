<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Task view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/common/ss_tasks.js"></script>
<script type="text/javascript">
var ss_noEntryTitleLabel = "<ssf:nlt tag="entry.noTitle" />";
</script>

<table class="ss_statisticTable"><tr>
<c:if test="${!empty ssBinder &&
				!empty ssBinder.customAttributes['statistics'] && 
				!empty ssBinder.customAttributes['statistics'].value && 
				!empty ssBinder.customAttributes['statistics'].value.value}">		
	<c:forEach var="definition" items="${ssBinder.customAttributes['statistics'].value.value}">
		<c:if test="${!empty definition.value}">
			<c:if test="${!empty definition.value.priority}">
				<td><ssf:drawStatistic statistic="${definition.value.priority}" style="coloredBar" showLabel="true" showLegend="true" labelAll="true"/></td>
			</c:if>

			<c:if test="${!empty definition.value.status}">
				<td><ssf:drawStatistic statistic="${definition.value.status}" style="coloredBar ss_statusBar" showLabel="true" showLegend="true" labelAll="true"/></td>
			</c:if>
		</c:if>
	</c:forEach>
</c:if>
</tr></table>


<%@ include file="/WEB-INF/jsp/definition_elements/description_view.jsp" %>
<div class="ss_folder_border">
<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</c:if>
</ssf:toolbar>
<div class="ss_clear"></div>
</div>
<jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
<div class="ss_folder" id="ss_task_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/task/task_nav_bar.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/task/task_folder_listing.jsp" %>
</div>
