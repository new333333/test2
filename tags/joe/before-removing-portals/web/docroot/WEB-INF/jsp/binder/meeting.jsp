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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>


<ssf:ifadapter>
	<body class="tundra">
</ssf:ifadapter>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
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
				<a class="ss_linkButton ss_bold ss_smallprint" href="javascript:;"
				  onClick="ss_startMeeting(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:'start_meeting'}), 'startMeetingForm', this.parentNode);"
				><ssf:nlt tag="meeting.start"/></a></span>

				<span>
				
				<a class="ss_linkButton ss_bold ss_smallprint" href="javascript:;"
				  onClick="ss_startMeeting(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:'schedule_meeting'}), 'startMeetingForm', this.parentNode);"
				><ssf:nlt tag="meeting.schedule"/></a></span>				
			</div>
		
			<br/><br/>
		
			<div class="ss_buttonBarLeft">
				<form class="ss_style ss_form" method="post" 
				  onSubmit="return ss_onSubmit(this);" name="${renderResponse.namespace}fm">
					<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;">
				</form>
			</div>
		
		</div>
<ssf:ifadapter>
	</body>
</html>
</ssf:ifadapter>
