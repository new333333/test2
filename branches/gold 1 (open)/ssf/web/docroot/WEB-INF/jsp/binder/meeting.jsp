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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>


<ssf:ifadapter>
	<body>
</ssf:ifadapter>
		<script type="text/javascript">
			var width = ss_getWindowWidth()/2;
			if (width < 700) width=700;
			var height = ss_getWindowHeight();
			if (height < 600) height=600;
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
									    type="user" userList="${ssUsers}" />
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
							</table>
						</fieldset>
					</td></tr>
				</table>
			</form>
		
			<div>
				<span>
				<a class="ss_linkButton ss_bold ss_smallprint" href="#"
				  onClick="ss_startMeeting(ss_ostatus_start_meeting_url, 'startMeetingForm', this.parentNode);"
				><ssf:nlt tag="meeting.start"/></a></span>

				<span>
				<a class="ss_linkButton ss_bold ss_smallprint" href="#"
				  onClick="ss_startMeeting(ss_ostatus_schedule_meeting_url, 'startMeetingForm', this.parentNode);"
				><ssf:nlt tag="meeting.schedule"/></a></span>				
			</div>
		
			<br/><br/>
		
			<div class="ss_buttonBarLeft">
				<form class="ss_style ss_form" method="post" 
				  onSubmit="return ss_onSubmit(this);" name="<portlet:namespace />fm">
					<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;">
				</form>
			</div>
		
		</div>
<ssf:ifadapter>
	</body>
</html>
</ssf:ifadapter>
