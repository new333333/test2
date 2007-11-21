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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ page import="java.util.HashMap" %>
<script type="text/javascript">

				
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
	
	function ss_getMonthCalendarEvents${prefix}() {
		var formObj = document.getElementById("ssCalNavBar${prefix}");
		if (formObj && formObj.ss_goto${prefix}_year && formObj.ss_goto${prefix}_month && formObj.ss_goto${prefix}_date) {
			ss_calendar_${prefix}.switchView("monthdirect", formObj.ss_goto${prefix}_year.value, formObj.ss_goto${prefix}_month.value - 1, formObj.ss_goto${prefix}_date.value);
		}
	}
	
</script>

<%@ include file="/WEB-INF/jsp/definition_elements/calendar/calendar_nav_bar_accessible.jsp" %>

<%
	Map entriesSeen = new HashMap();
%>
			<ul class="ss_searchResult">
			
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
									if (!ssSeenMap.checkIfSeen(entry)) {
										%><img <ssf:alt tag="alt.unseen"/> border="0" 
										src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
									}
								%>
						
								<c:out value="${entry._docNum}" escapeXml="false"/>.
								<ssf:titleLink 
									action="view_folder_entry" 
									entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" 
									namespace="${prefix}" >
									<ssf:param name="url" useBody="true">
										<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
										action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
									</ssf:param>
									<c:out value="${entry.title}" escapeXml="false"/>
								</ssf:titleLink>
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
			
