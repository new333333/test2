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
<% // Task view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/common/ss_tasks.js"></script>
<script type="text/javascript">
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="subscribe"/></portlet:actionURL>";
var ss_noEntryTitleLabel = "<ssf:nlt tag="entry.noTitle" />";
</script>

<div style="margin:0px;">

<!------------ STATISTICS EXAMPLE -------------->
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.sitescape.team.domain.Statistics"%>
<jsp:useBean id="ssBinder" type="com.sitescape.team.domain.Folder" scope="request" />
<%
	Map statusStatistics = null;
	Map priorityStatistics = null;
	
	if (ssBinder.getCustomAttribute("statistics") != null) {
		Statistics statistics = (Statistics)ssBinder.getCustomAttribute("statistics").getValue();
		if (statistics!=null) {
			Map allDefinitionStatistics = statistics.getValue();
			if (allDefinitionStatistics != null) {
				Map taskStatistics = (Map)allDefinitionStatistics.get("402883c1129b1f8101129b28bbe50002");
				if (taskStatistics != null) {
					statusStatistics = (Map)taskStatistics.get("status");
					priorityStatistics = (Map)taskStatistics.get("priority");
				}
			}
		}
	}

%>
<c:set var="statusStatistics" value="<%= statusStatistics %>" />
<c:set var="priorityStatistics" value="<%= priorityStatistics %>" />

<c:if test="${!empty statusStatistics}">
	<ssf:drawStatistic statistic="${statusStatistics}"/>
</c:if>
<c:if test="${!empty priorityStatistics}">
	<ssf:drawStatistic statistic="${priorityStatistics}" style="shortColoredBar" showLabel="true" showLegend="false"/>
</c:if>
<!------------- STATISTICS END ------------------>


<div class="ss_folder_border">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</c:if>

</ssf:toolbar>

</div>
</div>
<div class="ss_folder" id="ss_task_folder_div">

<%@ include file="/WEB-INF/jsp/definition_elements/task/task_nav_bar.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/task/task_folder_listing.jsp" %>
</div>
