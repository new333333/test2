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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.teaming.util.NLT" %>

<style type="text/css">
        @import "<html:rootPath />js/dojo/dijit/themes/tundra/tundra.css";
        @import "<html:rootPath />js/dojo/dojo/resources/dojo.css"
</style>
<script type="text/javascript">
	dojo.require("dojo.parser");
	dojo.require("dijit.form.DateTextBox");
	dojo.require("dijit.form.TimeTextBox");
</script>


<ssf:ifadapter>
	<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript" src="<html:rootPath />js/common/ss_event.js"></script>

<c:set var="timeZoneID" value="${ssUser.timeZone.ID}" />
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
		<script type="text/javascript">
			var width = ss_getWindowWidth()/2;
			if (width < 600) width=600;
			var height = ss_getWindowHeight();
			if (height < 700) height=700;
			self.window.resizeTo(width, height);
		</script>

		<div class="ss_style ss_portlet" style="padding:10px;">
		
			<form class="ss_style ss_form" method="post" id="startMeetingForm" name="startMeetingForm">
				<c:if test="${!empty ssBinder}">
					<input type="hidden" name="binderId" value="${ssBinder.id}" />
				</c:if>
				<c:if test="${!empty ssEntry}">
					<input type="hidden" name="entryId" value="${ssEntry.id}" />	
				</c:if>
		
				<span class="ss_bold"><ssf:nlt tag="meeting.add.title"/></span>
				<table class="ss_style"  border="0" cellspacing="0" cellpadding="0" width="95%">
					<tr><td>
						<fieldset class="ss_fieldset">
						  <legend class="ss_legend"><ssf:nlt tag="meeting.invitees" /></legend>
							<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
								<tr>
									<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
									<td valign="top">
									  <ssf:find formName="startMeetingForm" formElement="users" 
									    type="user" userList="${ssUsers}" width="150px" />
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<ssf:clipboard type="user" formElement="users" />
										<c:if test="${!empty ssBinder}">
											<ssf:teamMembers binderId="${ssBinder.id}" formElement="users" appendAll="${appendTeamMembers}"/>
										</c:if>										
									</td>
								</tr>
								<tr>				
									<td>
										<span class="ss_labelAbove ss_bold" valign="top"><ssf:nlt tag="meeting.password"/></span>
										<input class="ss_style" type="text" name="meeting_password" />										
									</td>
								</tr>								
							</table>
						</fieldset>
					</td></tr>
				</table>
			<div>
				<span>
				<a class="ss_linkButton ss_bold ss_smallprint" href="javascript:;"
				  onClick="ss_startMeeting(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:'start_meeting'}), 'startMeetingForm', this.parentNode);"
				><ssf:nlt tag="meeting.start"/></a></span>
			</div>
			<br/>
			<div>
				<fieldset class="ss_fieldset">
				 	<legend class="ss_legend"><ssf:nlt tag="meeting.options"/></legend>
					<div class="ss_event_editor tundra">
						<table class="ss_style">
							<tr>
								<td>
									<span class="ss_labelAbove ss_bold"><ssf:nlt tag="meeting.title"/></span>
									<input type="text" name="meeting_name"></textarea>
								</td>
							</tr>
							<tr>
								<td>
									<span class="ss_labelAbove ss_bold"><ssf:nlt tag="meeting.agenda"/></span>
									<textarea name="meeting_agenda" rows="5"></textarea>
								</td>
							</tr>
							<tr>
								<td>
									<span class="ss_labelAbove ss_bold"><ssf:nlt tag="meeting.start.time"/></span>
									<input type="text" dojoType="dijit.form.DateTextBox" 
										name="meeting_start_date"
										lang="<ssf:convertLocaleToDojoStyle />"
										<c:if test="${!empty startDate}">
											value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd" timeZone="${timeZoneID}"/>"
										</c:if>
									/>
									<span>
										<input type="text" dojoType="dijit.form.TimeTextBox"
											name="meeting_start_time" 
											lang="<ssf:convertLocaleToDojoStyle />" 	
											 <c:if test="${!empty startDate}">
												value="T<fmt:formatDate value="${startDate}" pattern="HH:mm" timeZone="${timeZoneID}"/>"
											</c:if>
										/>
									</span>
								</td>					
							</tr>
							<tr>
								<td>
									<span class="ss_labelAbove ss_bold"><ssf:nlt tag="meeting.length"/></span>
									<select name="meeting_length_hours">
										<option value="0">0</option>
										<option value="1" selected>1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
										<option value="9">9</option>
										<option value="10">10</option>
										<option value="11">11</option>
										<option value="12">12</option>
									</select>
									<span><ssf:nlt tag="meeting.length.hours"/></span>
									<span>
										<select name="meeting_length_minutes">
											<option value="0">0</option>
											<option value="15">15</option>
											<option value="30">30</option>
											<option value="45">45</option>
										</select>
									</span>
									<span><ssf:nlt tag="meeting.length.minutes"/></span>
								</td>
							</tr>
							<!--  
							<tr>
								<td>
									<span class="ss_labelAbove ss_bold"><ssf:nlt tag="meeting.repeat"/></span>
									<select name="meeting_repeat_option">
										<option value="once"><ssf:nlt tag="meeting.repeat.once"/></option>
										<option value="daily"><ssf:nlt tag="meeting.repeat.daily"/></option>
										<option value="weekly"><ssf:nlt tag="meeting.repeat.weekly"/></option>
										<option value="monthly"><ssf:nlt tag="meeting.repeat.monthly"/></option>
									</select>
								</td>
							</tr>
							<tr>
								<td>
									<span class="ss_labelAbove ss_bold"><ssf:nlt tag="meeting.end.date"/></span>	
								</td>
							</tr>
							-->
						</table>
					</div>
				</fieldset>
			</div>
			</form>
			<a class="ss_linkButton ss_bold ss_smallprint" href="javascript:;"
				  onClick="ss_startMeeting(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:'schedule_meeting'}), 'startMeetingForm', this.parentNode);"
				><ssf:nlt tag="meeting.schedule"/></a>
			
			<br/><br/>
			<div class="ss_buttonBarLeft">
				<form class="ss_style ss_form" method="post" 
				  onSubmit="return ss_onSubmit(this);" name="${renderResponse.namespace}fm">
					<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" 
					onClick="ss_cancelButtonCloseWindow();return false;">
				</form>
			</div>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
									dojo.addClass(document.body, "tundra");
							   });
			</script>
		</div>
<ssf:ifadapter>
	</body>
</html>
</ssf:ifadapter>
