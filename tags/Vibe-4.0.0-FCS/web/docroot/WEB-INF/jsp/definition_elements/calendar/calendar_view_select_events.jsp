<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

<%@ page import="org.kablink.util.BrowserSniffer" %>
<div class="ss_style">
<script type="text/javascript"> 
		function setModeCheck(eSel) {
			var mode = eSel.options[eSel.selectedIndex].value;
			var bChecked = (("creation" == mode) || ("activity" == mode));
			document.getElementById("ss_calendarEventsTypeChoose${prefix}").checked = bChecked;
		}
	</script>
	<form style="display: inline;">
		<%
			boolean isIE = BrowserSniffer.is_ie(request);
		%>
		<input type="hidden" id="ss_calendarEventsTypeChoose${prefix}" />
		<span class="ss_actions_bar6 ss_actions_bar">
			<span>
				<img border="0" src="<html:imagesPath/>pics/1pix.gif" />
				<ssHelpSpot helpId="workspaces_folders/misc_tools/calendar_entry_control" offsetX="-16" offsetY="-4" 
				    title="<ssf:nlt tag="helpSpot.calendarEntryControl"/>">
				</ssHelpSpot>
			</span>

			<label for="ss_calendarEventsTypeSelect${prefix}"><ssf:nlt tag="calendar.navi.chooseMode"/></label>
			<select id="ss_calendarEventsTypeSelect${prefix}" onchange="setModeCheck(this); ss_calendar_${prefix}.changeEventType();">
				<c:if test="${ !empty ssShowFolderModeSelect && ssShowFolderModeSelect }">
					<option value="virtual"><ssf:nlt tag="calendar.navi.mode.alt.virtual"/></option>
				</c:if>
				<option value="event"><ssf:nlt tag="calendar.navi.mode.alt.physical"/></option>
				<option value="creation"><ssf:nlt tag="calendar.navi.mode.alt.physical.byCreation"/></option>
				<option value="activity"><ssf:nlt tag="calendar.navi.mode.alt.physical.byActivity"/></option>
			</select>
		</span>
	</form>
</div>
