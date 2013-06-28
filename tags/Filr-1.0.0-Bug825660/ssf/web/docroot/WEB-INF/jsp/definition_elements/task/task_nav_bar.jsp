<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<div class="ss_clear"></div>
<script type="text/javascript"> 
	ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
</script>
<div>
  <ssHelpSpot helpId="workspaces_folders/misc_tools/tasks_tools" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.tasksTools"/>"></ssHelpSpot>
    
	<ul class="ss_calendarNaviBar">
		<% /* Task Folder:  View Options. */ %>
		<li class="ss_calendarNaviBarOption ss_taskViewOptions">
			<ssf:nlt tag="task.navi.chooseView"/>:
			<input id="taskClosed" type="radio" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'CLOSED'}">checked="true"</c:if>
				onclick="window.location='<ssf:url 
	  				folderId="${ssBinder.id}" 
	  				action="${action}">
		  				<ssf:param name="binderId" value="${ssBinder.id}"/>
		  				<ssf:param name="ssTaskFilterType" value="CLOSED"/>
	  				</ssf:url>'"/> <label for="taskClosed"><ssf:nlt tag="alt.viewClosed"/></label>
		
			<input id="taskDay" type="radio" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'DAY'}">checked="true"</c:if>
				onclick="window.location='<ssf:url 
	  				folderId="${ssBinder.id}" 
	  				action="${action}">
		  				<ssf:param name="binderId" value="${ssBinder.id}"/>
		  				<ssf:param name="ssTaskFilterType" value="DAY"/>
	  				</ssf:url>'"/> <label for="taskDay"><ssf:nlt tag="alt.viewToday"/></label>

			<input id="taskWeek" type="radio" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'WEEK'}">checked="true"</c:if>
				onclick="window.location='<ssf:url 
	  				folderId="${ssBinder.id}" 
	  				action="${action}">
		  				<ssf:param name="binderId" value="${ssBinder.id}"/>
		  				<ssf:param name="ssTaskFilterType" value="WEEK"/>
	  				</ssf:url>'"/> <label for="taskWeek"><ssf:nlt tag="alt.viewWeek"/></label>

			<input id="taskMonth" type="radio" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'MONTH'}">checked="true"</c:if>
				onclick="window.location='<ssf:url 
	  				folderId="${ssBinder.id}" 
	  				action="${action}">
		  				<ssf:param name="binderId" value="${ssBinder.id}"/>
		  				<ssf:param name="ssTaskFilterType" value="MONTH"/>
	  				</ssf:url>'"/> <label for="taskMonth"><ssf:nlt tag="alt.viewMonth"/></label>

			<input id="taskAllActive" type="radio" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'ACTIVE'}">checked="true"</c:if>
				onclick="window.location='<ssf:url 
	  				folderId="${ssBinder.id}" 
	  				action="${action}">
		  				<ssf:param name="binderId" value="${ssBinder.id}"/>
		  				<ssf:param name="ssTaskFilterType" value="ACTIVE"/>
	  				</ssf:url>'"/> <label for="taskAllActive"><ssf:nlt tag="alt.viewAllActive"/></label>
			
			<input id="taskAllEntries" type="radio" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'ALL'}">checked="true"</c:if>
				onclick="window.location='<ssf:url 
	  				folderId="${ssBinder.id}" 
	  				action="${action}">
		  				<ssf:param name="binderId" value="${ssBinder.id}"/>
		  				<ssf:param name="ssTaskFilterType" value="ALL"/>
	  				</ssf:url>'"/> <label for="taskAllEntries"><ssf:nlt tag="alt.viewAll"/></label>
		</li>

		<c:if test="${ !empty ssShowFolderModeSelect && ssShowFolderModeSelect }">
			<% /* Task Folder:  Mode Options. */ %>
			<li class="ss_calendarNaviBarOption ss_taskViewOptions">
				<ssf:nlt tag="task.navi.chooseMode"/>:
				<input id="taskVirtual" type="radio" name="ss_folder_current_mode_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentFolderModeType == 'VIRTUAL'}">checked="true"</c:if>
					onclick="window.location='<ssf:url 
		  				folderId="${ssBinder.id}" 
		  				action="${action}">
			  				<ssf:param name="binderId" value="${ssBinder.id}"/>
			  				<ssf:param name="ssFolderModeType" value="VIRTUAL"/>
		  				</ssf:url>'"/> <label for="taskVirtual"><ssf:nlt tag="task.navi.mode.alt.virtual"/></label>

				<input id="taskPhysical" type="radio" name="ss_folder_current_mode_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentFolderModeType == 'PHYSICAL'}">checked="true"</c:if>
					onclick="window.location='<ssf:url 
		  				folderId="${ssBinder.id}" 
		  				action="${action}">
			  				<ssf:param name="binderId" value="${ssBinder.id}"/>
			  				<ssf:param name="ssFolderModeType" value="PHYSICAL"/>
		  				</ssf:url>'"/> <label for="taskPhysical"><ssf:nlt tag="task.navi.mode.alt.physical"/></label>
			</li>
		</c:if>
	</ul>
</div>
