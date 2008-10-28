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
<div class="ss_clear"></div>
<script type="text/javascript"> 
	ss_loadJsFile(ss_rootPath, "js/common/ss_calendar.js");
</script>
<div>
  <ssHelpSpot helpId="workspaces_folders/misc_tools/tasks_tools" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.tasksTools"/>"></ssHelpSpot>
    
	<ul class="ss_calendarNaviBar">
		<li class="ss_calendarNaviBarOption ss_taskViewOptions">
			<ssf:nlt tag="task.navi.chooseView"/>:
			<a class="ss_calDaySelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="${action}">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="ssTaskFilterType" value="CLOSED"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'CLOSED'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewClosed"/>
			</a>
		
			<a class="ss_cal3DaysSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="${action}">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="ssTaskFilterType" value="DAY"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'DAY'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewToday"/>
			</a>

			<a class="ss_cal5DaysSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="${action}">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="ssTaskFilterType" value="WEEK"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'WEEK'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewWeek"/>
			</a>

			<a class="ss_cal7DaysSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="${action}">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="ssTaskFilterType" value="MONTH"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'MONTH'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewMonth"/>
			</a>

			<a class="ss_calMonthSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="${action}">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="ssTaskFilterType" value="ACTIVE"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'ACTIVE'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewAllActive"/>
			</a>
			
			<a class="ss_calMonthSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="${action}">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="ssTaskFilterType" value="ALL"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_${renderResponse.namespace}_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'ALL'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewAll"/>
			</a>			
		</li>
	</ul>
</div>