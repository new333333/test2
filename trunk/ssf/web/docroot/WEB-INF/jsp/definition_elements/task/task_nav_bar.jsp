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
<div class="ss_clear"></div>
<div>
  <ssHelpSpot helpId="workspaces_folders/misc_tools/calendar_tools" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.calendarTools"/>"></ssHelpSpot>
    
	<ul id="ss_calendarNaviBar">
		<li class="ss_calendarNaviBarOption ss_taskViewOptions">
			<ssf:nlt tag="task.navi.chooseView"/>:
			<a class="ss_calDaySelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="view_folder_listing">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="tabId" value="${tab.tabId}"/>
	  				<ssf:param name="ssTaskFilterType" value="CLOSED"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_<portlet:namespace/>_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'CLOSED'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewClosed"/>
			</a>
		
			<a class="ss_cal3DaysSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="view_folder_listing">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="tabId" value="${tab.tabId}"/>
	  				<ssf:param name="ssTaskFilterType" value="DAY"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_<portlet:namespace/>_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'DAY'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewToday"/>
			</a>

			<a class="ss_cal5DaysSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="view_folder_listing">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="tabId" value="${tab.tabId}"/>
	  				<ssf:param name="ssTaskFilterType" value="WEEK"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_<portlet:namespace/>_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'WEEK'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewWeek"/>
			</a>

			<a class="ss_cal7DaysSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="view_folder_listing">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="tabId" value="${tab.tabId}"/>
	  				<ssf:param name="ssTaskFilterType" value="MONTH"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_<portlet:namespace/>_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'MONTH'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewMonth"/>
			</a>

			<a class="ss_calMonthSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="view_folder_listing">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="tabId" value="${tab.tabId}"/>
	  				<ssf:param name="ssTaskFilterType" value="ACTIVE"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_<portlet:namespace/>_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'ACTIVE'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewAllActive"/>
			</a>
			
			<a class="ss_calMonthSelectButton" href="<ssf:url 
  				folderId="${ssBinder.id}" 
  				action="view_folder_listing">
	  				<ssf:param name="binderId" value="${ssBinder.id}"/>
	  				<ssf:param name="tabId" value="${tab.tabId}"/>
	  				<ssf:param name="ssTaskFilterType" value="ALL"/>
  				</ssf:url>">
				<input type="radio" onclick="document.location.href=this.parentNode.href;" name="ss_task_current_filter_<portlet:namespace/>_${ssBinder.id}" <c:if test="${ssCurrentTaskFilterType == 'ALL'}">checked="true"</c:if>/> <ssf:nlt tag="alt.viewAll"/>
			</a>			
		</li>
		<li class="ss_calendarNaviBarOption" />
	</ul>
</div>