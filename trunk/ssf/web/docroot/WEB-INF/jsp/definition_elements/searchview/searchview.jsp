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
<% // Search view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript">
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="subscribe"/></portlet:actionURL>";
</script>

<div style="margin:0px;">

<div align="right" style="margin:0px 4px 0px 0px;">
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">
	<tr>
		<td align="left" width="55%">
<%@ include file="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" %>
		</td>
		<td align="right" width="20%">
			&nbsp;
		</td>
	</tr>
</table>
</div>

<div class="ss_folder_border" style="position:relative; top:2; margin:0px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
	<% // Add the toolbar with the navigation widgets, commands and filter %>
	<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">

		<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true">
			<c:set var="ss_history_bar_table_class" 
			  value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
			<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
		</ssf:toolbar>

		<% // Entry toolbar %>
		<c:if test="${!empty ssEntryToolbar}">
			<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
		</c:if>
		<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" skipSeparator="true">
			<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
		</ssf:toolbar>
	</ssf:toolbar>
</div>
</div>
<div class="ss_folder" id="ss_guestbook_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/searchview/searchview_folder_listing.jsp" %>
</div>
