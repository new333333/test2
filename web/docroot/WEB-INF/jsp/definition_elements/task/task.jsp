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
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>
<%@ page import="java.util.Date"                                         %>
<%@ page import="org.kablink.teaming.ObjectKeys"                         %>
<%@ page import="org.kablink.teaming.util.SPropsUtil"                    %>
<%@ page import="org.kablink.teaming.web.util.ListFolderHelper.ModeType" %>
<jsp:useBean id="ssSeenMap"               type="org.kablink.teaming.domain.SeenMap"                     scope="request" />
<jsp:useBean id="ssCurrentFolderModeType" type="org.kablink.teaming.web.util.ListFolderHelper.ModeType" scope="request" />
<jsp:useBean id="ss_searchTotalHits"      type="java.lang.Integer"                                      scope="request" />
<jsp:useBean id="ssBinder"                type="org.kablink.teaming.domain.Binder"                      scope="request" />
<jsp:useBean id="ssUserFolderPropertyObj" type="org.kablink.teaming.domain.UserProperties"              scope="request" />
<%
	// Is the GWT based subtasks feature enabled?
	boolean subtasksEnabled = SPropsUtil.getBoolean("subtasks.enabled", true);
	if (subtasksEnabled) {
		// Yes!  Is the number of items that we're working with
		// within our supported limits?
		subtasksEnabled =
			((ss_searchTotalHits != null) &&
			 (ss_searchTotalHits <= SPropsUtil.getInt("subtasks.max.items", Integer.MAX_VALUE)));
	}
	
	String taskChange = ((String) ssUserFolderPropertyObj.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_CHANGE));
//	String taskChange = ((String) ssBinder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_CHANGE));
	if (null == taskChange) taskChange = "";
	boolean updateCalculatedDates = (0 < taskChange.length());

	String taskId = ((String) ssUserFolderPropertyObj.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_ID));
//	String taskId = ((String) ssBinder.getProperty(ObjectKeys.BINDER_PROPERTY_TASK_ID));
	if (null == taskId) taskId = "";
%>

<c:set var="gwtPage" value="taskListing" scope="request"/>	
<% if (subtasksEnabled) { %>
	<% // The DlgBox class uses JQuery to make dialogs draggable. %>
	<script type="text/javascript" src="<html:rootPath/>js/jquery/jquery-1.3.2.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/jquery/ui.core.js"></script>
	<script type="text/javascript" src="<html:rootPath/>js/jquery/ui.draggable.js"></script>
	<script type="text/javascript">
		// Relinquish jQuery's control of the $ variable.
	    jQuery.noConflict();
	</script>

	<%@ include file="/WEB-INF/jsp/common/GwtRequestInfo.jsp" %>
	<script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js?<%= org.kablink.teaming.util.ReleaseInfo.getContentVersion() %>"></script>
<% } %>
	
<script type="text/javascript" src="<html:rootPath/>js/common/ss_tasks.js"></script>
<script type="text/javascript">
	var ss_noEntryTitleLabel = "<ssf:nlt tag="entry.noTitle" />";
	
	function toggleGraphs() {
		var ss_taskGraphExpander  = "<ssf:escapeJavaScript><html:imagesPath/>pics/sym_s_expand.gif</ssf:escapeJavaScript>";
		var ss_taskGraphCollapser = "<ssf:escapeJavaScript><html:imagesPath/>pics/sym_s_collapse.gif</ssf:escapeJavaScript>";

		// Are we showing the graphs?
		var ePriorities = document.getElementById("taskPrioritiesGraph");
		var eRefresh    = document.getElementById("taskRefreshGraph"   );
		var eStatus     = document.getElementById("taskStatusGraph"    );		
		var eIMG        = document.getElementById("graph-toggle-img"   );
		if (0 < eIMG.src.indexOf(ss_taskGraphExpander)) {
			// Yes!  Make the expander a collapser...
			eIMG.src   = ss_taskGraphCollapser;
			eIMG.title = "<ssf:escapeJavaScript><ssf:nlt tag="task.graphsHideAlt"/></ssf:escapeJavaScript>";
			
			// ...and show the graphs.
			if (null != ePriorities) ePriorities.className = "";
			if (null != eRefresh)    eRefresh.className    = "";
			if (null != eStatus)     eStatus.className     = "";
		}
		else {
			// No, we must be hiding the graphcs!  Make the collapser
			// and expander...
			eIMG.src   = ss_taskGraphExpander;
			eIMG.title = "<ssf:escapeJavaScript><ssf:nlt tag="task.graphsShowAlt"/></ssf:escapeJavaScript>";
			
			// ...and hide the graphs.
			if (null != ePriorities) ePriorities.className = "ss_taskGraphsHidden";
			if (null != eRefresh)    eRefresh.className    = "ss_taskGraphsHidden";
			if (null != eStatus)     eStatus.className     = "ss_taskGraphsHidden";
		}

		// In either case, we need to tell GWT to relayout things so
		// that the task listing table properly shows a scroll bar
		// when needed.
		window.top.ss_gwtRelayoutPage();
	}
	
	function ss_doShowTaskGraphs() {
		// If the task graphs are supposed to be shown...
		if (window.top.ss_showTaskGraphs) {
			// ...toggle their visibility state.
			window.top.ss_showTaskGraphs = false;
			toggleGraphs();
		}
	}
	
	function ss_refreshTaskGraphs() {		
		window.top.ss_showTaskGraphs = true;
		self.location.reload(true);
	}
	
	<% if (subtasksEnabled) { %>
		ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");		
	<% } %>
	
	ss_createOnLoadObj( "tasksLoaded", ss_doShowTaskGraphs);
</script>
<c:if test="${ ssCurrentFolderModeType != 'VIRTUAL' }">
	<table class="ss_statisticTable"><tr>
	<c:set var="colCount" value="0"/>
	<c:if test="${!empty ssBinder &&
	              !empty ssBinder.customAttributes['statistics'] && 
	              !empty ssBinder.customAttributes['statistics'].value && 
	              !empty ssBinder.customAttributes['statistics'].value.value}">
	    <td>
	    	<h5 class="ss_statisticLabel"><a id="graph-toggle" href="javascript: ;" onClick="javascript:toggleGraphs();">
	    		<%= NLT.get("task.graphs") %>
	    		<img
	    			id="graph-toggle-img"
	    			border="0"
	    			align="absmiddle"
	    			src="<html:imagesPath/>pics/sym_s_expand.gif"
	    			title="<%= NLT.get("task.graphsShowAlt") %>" />
	    	</a></h5>
	    </td>		
		<c:set var="colCount" value="${colCount + 1}"/>
		<c:forEach var="definition" items="${ssBinder.customAttributes['statistics'].value.value}">
			<c:if test="${!empty definition.value}">
				<c:if test="${!empty definition.value.priority}">
					<td><div id="taskPrioritiesGraph" class="ss_taskGraphsHidden"><ssf:drawStatistic statistic="${definition.value.priority}" style="coloredBar" showLabel="true" showLegend="true" labelAll="true"/></div></td>
					<c:set var="colCount" value="${colCount + 1}"/>
				</c:if>
	
				<c:if test="${!empty definition.value.status}">
					<td><div id="taskStatusGraph" class="ss_taskGraphsHidden"><ssf:drawStatistic statistic="${definition.value.status}" style="coloredBar ss_statusBar" showLabel="true" showLegend="true" labelAll="true"/></div></td>
					<c:set var="colCount" value="${colCount + 1}"/>
				</c:if>
			</c:if>
		</c:forEach>
	</c:if>
	</tr>
	<c:if test="${colCount > 0}">
	<tr>
	<td colspan="${colCount}" align="right">
	<div id="taskRefreshGraph" class="ss_taskGraphsHidden"><input type="button" class="ss_linkButton ss_smallprint" onClick="ss_refreshTaskGraphs();"
	 value="<%= NLT.get("task.refreshChart") %>" /></div>
	</td>
	</tr>
	</c:if>
	</table>
</c:if>

<jsp:include page="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" />
<div class="ss_folder_border <% if (subtasksEnabled) { %>gwtTaskFilter_toolbar<% } %>">
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
	<% if (subtasksEnabled) { %>
		<div class="gwtTaskFilter_div" id="gwtTaskFilter"></div>
	<% } %>
	<div class="ss_clear"></div>
</div>

<jsp:include page="/WEB-INF/jsp/forum/add_files_to_folder.jsp" />
<% if (!subtasksEnabled) { %>
	<jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
<% } %>

<div class="ss_folder" id="ss_task_folder_div">
	<% if (subtasksEnabled) { %>
		<% // Generate the GWT UI. %>
		<div class="gwtTasks" id="gwtTasks">
			<input type="hidden" id="ssCurrentFolderModeType" value="${ssCurrentFolderModeType}"   />
			<input type="hidden" id="ssCurrentTaskFilterType" value="${ssCurrentTaskFilterType}"   />
			<input type="hidden" id="ssFolderSortBy"          value="${ssFolderSortBy}"            />
			<input type="hidden" id="ssFolderSortDescend"     value="${ssFolderSortDescend}"       />
			<input type="hidden" id="ssShowFolderModeSelect"  value="${ssShowFolderModeSelect}"    />
			<input type="hidden" id="taskChange"              value="<%= taskChange            %>" />
			<input type="hidden" id="taskId"                  value="<%= taskId                %>" />
			<input type="hidden" id="updateCalculatedDates"   value="<%= updateCalculatedDates %>" />
		</div>
		<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
	<% } else { %>
		<% // Generate the old. JSP based UI. %>
		<%@ include file="/WEB-INF/jsp/definition_elements/task/task_nav_bar.jsp"        %>
		<%@ include file="/WEB-INF/jsp/definition_elements/task/task_folder_listing.jsp" %>
	<% } %>
</div>
