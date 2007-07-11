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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="java.util.HashMap" %>
<script type="text/javascript">

	var ss_findEventsUrl = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="operation" value="find_calendar_events" />
		    	</ssf:url>";

	var ss_viewEventUrl = ss_viewEntryURL + "&binderId=${ssFolder.id}";
				
	var ss_stickyCalendarDisplaySettings =  "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="operation" value="sticky_calendar_display_settings" />
		    	</ssf:url>";				
	
	var ss_addCalendarEntryUrl = "${addDefaultEntryURL}";
	if (ss_addCalendarEntryUrl.indexOf("addEntryFromIFrame=1&") > -1) {
		ss_addCalendarEntryUrl = ss_addCalendarEntryUrl.replace("addEntryFromIFrame=1&", "");
	}
					
	var ss_calendarWorkDayGridTitle = "<ssf:nlt tag="calendar.hours.workday"/>";
	var ss_calendarFullDayGridTitle = "<ssf:nlt tag="calendar.hours.fullday"/>";
	
	function ss_getMonthCalendarEvents() {
		var formObj = document.getElementById("ssCalNavBar");
		if (formObj && formObj.ss_goto_year && formObj.ss_goto_month && formObj.ss_goto_date) {
			ss_calendar_${renderResponse.namespace}.ss_cal_Events.switchView("monthdirect", formObj.ss_goto_year.value, formObj.ss_goto_month.value - 1, formObj.ss_goto_date.value);
		}
	}
	
</script>

<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_nav_bar_accessible.jsp" %>

<%
	Map entriesSeen = new HashMap();
%>
			<ul id="ss_searchResult">
			
				<c:if test="${empty ssFolderEntries}">
					<span class="ssVisibleEntryNumbers"><ssf:nlt tag="folder.NoResults" /></span>
				</c:if>
			
				<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
					<jsp:useBean id="entry" type="java.util.HashMap" />
					<li>
					<%
						if (!entriesSeen.containsKey(entry.get("_docId"))) {
					%>
					<c:set var="entryBinderId" value="${entry._binderId}"/>
					<c:set var="entryDocId" value="${entry._docId}"/>	
					<c:if test="${entry._entityType == 'folder' || entry._entityType == 'workspace'}">
					  <c:set var="entryBinderId" value="${entry._docId}"/>
					  <c:set var="entryDocId" value=""/>
					</c:if>
					
					<div class="ss_thumbnail">
						<img <ssf:alt tag="alt.entry"/> src="<html:imagesPath/>pics/entry_icon.gif"/>
					</div>
					<div class="ss_entry_folderListView">
						<div class="ss_entryHeader">
							<h3 class="ss_entryTitle">
							
								<%
									if (!ssSeenMap.checkAndSetSeen(entry, true)) {
										%><img <ssf:alt tag="alt.unseen"/> border="0" 
										src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
									}
								%>
						
								<c:out value="${entry._docNum}" escapeXml="false"/>.
								<ssf:menuLink 
									displayDiv="false" action="view_folder_entry" 
									adapter="true" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}">
									<ssf:param name="url" useBody="true">
										<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
										action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
									</ssf:param>
									<c:out value="${entry.title}" escapeXml="false"/>
								</ssf:menuLink>
							</h3>
							<div class="ss_clear">&nbsp;</div>
						</div>
			
						<p id="summary_${status.count}">
							<c:if test="${!empty entry._desc}">
								<ssf:markup type="view" binderId="${entryBinderId}" entryId="${entryDocId}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="100">
										${entry._desc}
									</ssf:textFormat>
								</ssf:markup>
							</c:if>
						</p>
					</div>
					<div class="ss_clear">&nbsp;</div>
													
					<div id="details_${status.count}" class="ss_entryDetails">
						<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
						<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						<c:if test="${!empty entry._workflowStateCaption}">
							<p><span class="ss_label"><ssf:nlt tag="entry.workflowState" />:</span> <c:out value="${entry._workflowStateCaption}" /></p>
						</c:if>
					</div>
					<%
						}
						entriesSeen.put(entry.get("_docId"), "1");
					%>
					</li>
				</c:forEach>
			</ul>
			

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>