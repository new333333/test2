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
<% //Event scheduler widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>

<script type="text/javascript" src="<html:rootPath />js/timeline/timeline-api.js?bundle=false"></script>
<script type="text/javascript" src="<html:rootPath />js/common/ss_event.js"></script>

<script type="text/javascript">
dojo.addOnLoad(function() {
					${prefix}scheduler = ssEventScheduler.create({
							containerId: '${prefix}schedule',
							userListObj: ssFind.getObjectByFormName('${formName}', '${propertyValues_sourceUserListElement[0]}'),
							eventStartObj: dijit.byId('event_start_${propertyValues_sourceEventElement[0]}'),
							eventStartTimeObj: dijit.byId('event_start_time_${propertyValues_sourceEventElement[0]}'),
							eventEndObj: dijit.byId('event_end_${propertyValues_sourceEventElement[0]}'),
							eventEndTimeObj: dijit.byId('event_end_time_${propertyValues_sourceEventElement[0]}'),
							eventAllDayObj: document.getElementById('${propertyValues_sourceEventElement[0]}_allDayEvent'),
							userListDataName: "${propertyValues_sourceUserListElement[0]}",
							binderId: "${ssFolder.id}",
							entryId: "${ssEntry.id}"
					});
				});
ssEventScheduler.locale.allAttendees = "<ssf:nlt tag="event.scheduler.allAttendees" />";
ssEventScheduler.locale.busy = "<ssf:nlt tag="event.scheduler.busy" />";
ssEventScheduler.locale.tentative = "<ssf:nlt tag="event.scheduler.tentative" />";
ssEventScheduler.locale.outOfOffice = "<ssf:nlt tag="event.scheduler.outOfOffice" />";
</script>


<div class="ss_entryContent ${ss_fieldModifyStyle}">
		<a href="javascript: //" onclick="${prefix}scheduler.display();">${property_caption}</a>
	
		<div id="${prefix}schedule" class="ss_event_scheduler"></div>
		<br style="clear: both;">
</div>