<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // Task Folder Listing View %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date"                                         %>
<%@ page import="org.kablink.teaming.util.SPropsUtil"                    %>
<%@ page import="org.kablink.teaming.web.util.ListFolderHelper.ModeType" %>
<jsp:useBean id="ssSeenMap"               type="org.kablink.teaming.domain.SeenMap"                     scope="request" />
<jsp:useBean id="ssCurrentFolderModeType" type="org.kablink.teaming.web.util.ListFolderHelper.ModeType" scope="request" />
<jsp:useBean id="ss_searchTotalHits"      type="java.lang.Integer"                                      scope="request" />
<%
	// Is the GWT based subtasks feature enabled?
	boolean subtasksEnabled = SPropsUtil.getBoolean("subtasks.enabled", true);
	if (subtasksEnabled) {
		// Yes!  Are we viewing the task list as 'Entries from Folder'?
		subtasksEnabled = (ssCurrentFolderModeType == ModeType.PHYSICAL);
		if (subtasksEnabled) {
			// Yes!  Is the number of items that we're working with
			// within our supported limits?
			subtasksEnabled =
				((ss_searchTotalHits != null) &&
				 (ss_searchTotalHits <= SPropsUtil.getInt("subtasks.max.items", 1000)));
		}
	}
%>

<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp" %>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_tasks.js?<%= org.kablink.teaming.util.ReleaseInfo.getContentVersion() %>"></script>
<script type="text/javascript">
	var ss_noEntryTitleLabel = "<ssf:nlt tag="entry.noTitle" />";
</script>
<c:if test="${ ssCurrentFolderModeType != 'VIRTUAL' }">
	<table class="ss_statisticTable"><tr>
	<c:set var="colCount" value="0"/>
	<c:if test="${!empty ssBinder &&
	              !empty ssBinder.customAttributes['statistics'] && 
	              !empty ssBinder.customAttributes['statistics'].value && 
	              !empty ssBinder.customAttributes['statistics'].value.value}">		
		<c:forEach var="definition" items="${ssBinder.customAttributes['statistics'].value.value}">
			<c:if test="${!empty definition.value}">
				<c:if test="${!empty definition.value.priority}">
					<td><ssf:drawStatistic statistic="${definition.value.priority}" style="coloredBar" showLabel="true" showLegend="true" labelAll="true"/></td>
					<c:set var="colCount" value="${colCount + 1}"/>
				</c:if>
	
				<c:if test="${!empty definition.value.status}">
					<td><ssf:drawStatistic statistic="${definition.value.status}" style="coloredBar ss_statusBar" showLabel="true" showLegend="true" labelAll="true"/></td>
					<c:set var="colCount" value="${colCount + 1}"/>
				</c:if>
			</c:if>
		</c:forEach>
	</c:if>
	</tr>
	<c:if test="${colCount > 0}">
	<tr>
	<td colspan="${colCount}" align="right">
	<input type="button" class="ss_linkButton ss_fineprint" onClick="self.location.reload(true);"
	 value="<%= NLT.get("task.refreshChart") %>" />
	</td>
	</tr>
	</c:if>
	</table>
</c:if>

<jsp:include page="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" />
<div class="ss_folder_border">
	<% // Add the toolbar with the navigation widgets, commands and filter %>
	<ssf:toolbar style="ss_actions_bar5 ss_actions_bar">
		<ssHelpSpot 
			helpId="workspaces_folders/menus_toolbars/folder_toolbar"
			offsetX="0"
			offsetY="0" 
		  	title="<ssf:nlt tag="helpSpot.folderControlAndFiltering"/>"></ssHelpSpot>
		<% // Entry toolbar %>
		<c:if test="${!empty ssEntryToolbar}">
			<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar5 ss_actions_bar" item="true" />
		 </c:if>
	</ssf:toolbar>
	<div class="ss_clear"></div>
</div>

<jsp:include page="/WEB-INF/jsp/forum/add_files_to_folder.jsp" />
<% if (subtasksEnabled) { %>
	<div class="gwtTaskTools" id="ss_gwtTaskToolsDIV"></div>
<% } else {%>
	<jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
<% } %>

<div class="ss_folder" id="ss_task_folder_div">
	<%@ include file="/WEB-INF/jsp/definition_elements/task/task_nav_bar.jsp" %>
	<% if (subtasksEnabled) { %>
		<% // Generate the GWT UI. %>
		<div class="gwtTaskListing" id="ss_gwtTaskListingDIV"><br /><span class="wiki-noentries-panel"><%= NLT.get("task.loadingPleaseWait") %></span></div>
		<script type="text/javascript">
			function ss_initGwtTaskListing() {
				if ((typeof window.top.ss_initGwtTaskListing != "undefined") &&
						(window.name == "gwtContentIframe")) {
					window.top.ss_initGwtTaskListing("${ssBinder.id}", "${ssCurrentTaskFilterType}", "${ssCurrentFolderModeType}", "${ssFolderSortBy}", "${ssFolderSortDescend}");
				}
				else {
					alert("*Internal Error* - The GWT Task UI code is missing!!!");
				}
			}
			ss_createOnLoadObj('ss_initGwtTaskListing', ss_initGwtTaskListing());
		</script>
	<% } else { %>
		<% // Generate the old. JSP based UI. %>
		<%@ include file="/WEB-INF/jsp/definition_elements/task/task_folder_listing.jsp" %>
	<% } %>
</div>
