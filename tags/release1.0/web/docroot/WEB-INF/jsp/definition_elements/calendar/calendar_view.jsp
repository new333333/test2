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

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>