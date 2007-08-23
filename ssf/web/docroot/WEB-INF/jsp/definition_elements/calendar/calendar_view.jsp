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
<% // Calendar view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.team.domain.Entry" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.team.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />


<%
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";

	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
%>

<script type="text/javascript">
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="subscribe"/></portlet:actionURL>";
</script>

<% // Then include the navigation widgets for this view %>
<c:set var="prefix" value="${renderResponse.namespace}" />
			<% // Add the toolbar with the navigation widgets, commands and filter %>
			<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
			
				<% // Entry toolbar %>
				<c:if test="${!empty ssEntryToolbar}">
					<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
				</c:if>
				
				<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" >
					<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
				</ssf:toolbar>

				<ssf:ifnotaccessible>
					<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" skipSeparator="true">
						<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_view_select_events.jsp" %>
					</ssf:toolbar>
				</ssf:ifnotaccessible>

			</ssf:toolbar>

<script type="text/javascript">

	var ss_findEventsUrl${prefix} = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="binderIds" value="${ssBinder.id}" />
					<ssf:param name="operation" value="find_calendar_events" />
		    	</ssf:url>";

</script>		    	

<ssf:ifaccessible>
<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_view_content_accessible.jsp" %>
</ssf:ifaccessible>

<ssf:ifnotaccessible>
<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_view_content.jsp" %>
</ssf:ifnotaccessible>