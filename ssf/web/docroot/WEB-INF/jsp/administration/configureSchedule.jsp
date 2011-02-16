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
<%@ page import="java.util.Calendar"                       %>
<%@ page import="java.util.List"                           %>
<%@ page import="org.kablink.teaming.util.NLT"             %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<jsp:useBean id="ssWeekendsAndHolidaysConfig" type="org.kablink.teaming.domain.WeekendsAndHolidaysConfig" scope="request" />

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure.schedule.caption") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%
	boolean sunday    = false;
	boolean monday    = false;
	boolean tuesday   = false;
	boolean wednesday = false;
	boolean thursday  = false;
	boolean friday    = false;
	boolean saturday  = false;
	
	List<Integer> weekendDays = ssWeekendsAndHolidaysConfig.getWeekendDaysList();
	for (Integer weekendDay:  weekendDays) {
		switch (weekendDay.intValue()) {
		case Calendar.SUNDAY:     sunday    = true; break;
		case Calendar.MONDAY:     monday    = true; break;
		case Calendar.TUESDAY:    tuesday   = true; break;
		case Calendar.WEDNESDAY:  wednesday = true; break;
		case Calendar.THURSDAY:   thursday  = true; break;
		case Calendar.FRIDAY:     friday    = true; break;
		case Calendar.SATURDAY:   saturday  = true; break;
		}
	}
%>

<body class="ss_style_body tundra">
	<script type="text/javascript">
		/*
		 * Called when the user presses the 'Apply' push button.
		 *
		 * Bundles the selections the user made into two hidden <INPUT>'s
		 * used by the controller.
		 */
		function bundleSelections() {
			// Bundle the weekend days...
			var weekendDays = new Array();
			if (document.getElementById("sunday"   ).checked) weekendDays[weekendDays.length] = "<%= Calendar.SUNDAY    %>";
			if (document.getElementById("monday"   ).checked) weekendDays[weekendDays.length] = "<%= Calendar.MONDAY    %>";
			if (document.getElementById("tuesday"  ).checked) weekendDays[weekendDays.length] = "<%= Calendar.TUESDAY   %>";
			if (document.getElementById("wednesday").checked) weekendDays[weekendDays.length] = "<%= Calendar.WEDNESDAY %>";
			if (document.getElementById("thursday" ).checked) weekendDays[weekendDays.length] = "<%= Calendar.THURSDAY  %>";
			if (document.getElementById("friday"   ).checked) weekendDays[weekendDays.length] = "<%= Calendar.FRIDAY    %>";
			if (document.getElementById("saturday" ).checked) weekendDays[weekendDays.length] = "<%= Calendar.SATURDAY  %>";
			document.getElementById("weekendDays").value = ss_pack(weekendDays);
			
			// ...bundle the holidays...
//!			...this needs to be implemented...
			
			// ...and return true so that the ok to goes through.
			return true;
		}
		
		/*
		 * Called when the user presses the 'Close' push button.
		 */
		function handleCloseBtn() {
			<% 	if (GwtUIHelper.isGwtUIActive(request)) { %>
					// Tell the Teaming GWT ui to close the administration content panel.
					if ( window.parent.ss_closeAdministrationContentPanel ) {
						window.parent.ss_closeAdministrationContentPanel();
						
					} else {
						ss_cancelButtonCloseWindow();
					}
					return false;
			<% 	}
				else { %>
					ss_cancelButtonCloseWindow();
					return false;
			<%	} %>
				
		}
	</script>
	
	<div class="ss_style ss_portlet">
		<c:set var="titleTag" value='<%= NLT.get("administration.configure.schedule.caption") %>'/>
		<ssf:form title="${titleTag}">
			<c:if test="${!empty ssException}">
				<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span><br>
			</c:if>
		
			<br/>
			<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" action="<ssf:url action="configure_schedule" actionUrl="true"/>">
			    <div>
					<fieldset class="ss_fieldset">
						<legend class="ss_legend"><ssf:nlt tag="administration.configure.schedule.legend.weekends" /></legend>
						<input type="hidden" id="weekendDays" name="weekendDays" value="PP" />
						<nobr>
							<input type="checkbox" id="sunday" name="sunday" <% if (sunday) { %>checked<% } %> />
							<label for="sunday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.su"/></span>
							</label>
	
							&nbsp;							
							<input type="checkbox" id="monday" name="monday" <% if (monday) { %>checked<% } %> />
							<label for="monday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.mo"/></span>
							</label>
							
							&nbsp;							
							<input type="checkbox" id="tuesday" name="tuesday" <% if (tuesday) { %>checked<% } %> />
							<label for="tuesday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.tu"/></span>
							</label>
							
							&nbsp;							
							<input type="checkbox" id="wednesday" name="wednesday" <% if (wednesday) { %>checked<% } %> />
							<label for="wednesday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.we"/></span>
							</label>
							
							&nbsp;							
							<input type="checkbox" id="thursday" name="thursday" <% if (thursday) { %>checked<% } %> />
							<label for="thursday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.th"/></span>
							</label>
							
							&nbsp;							
							<input type="checkbox" id="friday" name="friday" <% if (friday) { %>checked<% } %> />
							<label for="friday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.fr"/></span>
							</label>
							
							&nbsp;							
							<input type="checkbox" id="saturday" name="saturday" <% if (saturday) { %>checked<% } %> />
							<label for="saturday">
								<span class="ss_labelRight ss_normal"><ssf:nlt tag="calendar.day.names.sa"/></span>
							</label>
						</nobr>			
					</fieldset>
				</div>
				
				<br/>
			    <div>
					<fieldset class="ss_fieldset">
						<input type="hidden" id="holidays" name="holidays" value="PP" />
						<legend class="ss_legend"><ssf:nlt tag="administration.configure.schedule.legend.holidays" /></legend>
						<span class="ss_labelAbove">
					    	...this needs to be implemented...
						</span>
					</fieldset>
				</div>
				
				<br/>
				<div class="ss_buttonBarLeft">
					<input type="submit" class="ss_submit" name="okBtn"    value="<ssf:nlt tag="button.apply"/>"              onClick="return bundleSelections();" />
					<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>" onClick="return handleCloseBtn();"   />
				</div>
			</form>
		</ssf:form>
	</div>
</body>
</html>
