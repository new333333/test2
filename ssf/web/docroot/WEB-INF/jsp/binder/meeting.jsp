<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
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
									    type="user" userList="${ssUsers}" showClipboard="true"
									    showTeamMembers="true" binderId="${ssBinder.id}" />
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
