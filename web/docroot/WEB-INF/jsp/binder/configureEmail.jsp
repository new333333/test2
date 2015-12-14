<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.Utils" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("toolbar.menu.configure_folder_email") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div class="ss_style ss_portlet">
<ssf:form titleTag="toolbar.menu.configure_folder_email">
<br/>
	<c:if test="${!empty ssException}">
		<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span><br>
	</c:if>
	<div class="margintop1" style="font-size: 20px;"><ssf:nlt tag="notify.forum.label"/>&nbsp;${ssBinder.title}</div>

	<c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowIdNoWS" scope="request" />
	<c:set var="ss_breadcrumbsTreeName" value="${renderResponse.namespace}_tree" scope="request" />

	<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
	<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
  			onSubmit="return ss_onSubmit(this);"
    		action="<ssf:url action="config_email" actionUrl="true">
		<ssf:param name="binderId" value="${ssBinder.id}"/></ssf:url>">
	
		<br/>
		<c:if test="${!empty ssScheduleInfo}">
			<table class="ss_style"  border="0" cellspacing="0" cellpadding="3" width="100%">
				<tr>
					<td align="left">
						<span class="ss_bold"><ssf:nlt tag="notify.header"/></span>
						<% if ( Utils.checkIfFilr() == false ) { %>
							<ssf:showHelp guideName="adv_user" pageId="mngfolder_sendnotification" />
						<% } %>
						<% else { %>
							<ssf:showHelp guideName="user" pageId="folderadmin_configurenotifications" />
						<% } %>
					</td>
				</tr>
				<tr>
					<td style="padding-left:15px;">
						<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
							<tr>
								<td>
			    					<br/><span class="ss_bold"><ssf:nlt tag="subscribe.select.type"/></span><br/>
									<div class="ss_indent_medium">
										<div>
											<input type="radio" name="style" value="1" id="notifyType_1"
													<c:if test="${ssBinder.notificationDef.style=='1'}"> checked="checked"</c:if> 
											/>
											<label for="notifyType_1"><ssf:nlt tag="subscribe.digest"/></label>
										</div>
										
										<div class="marginleft2 margintop1">
											<c:if test="${ssScheduleInfo.enabled && ssNotification_ScheduleInfo.enabled}">
												<div style="padding-bottom:5px;">
													<span class="ss_bold"><ssf:nlt tag="notify.alternateScheduleEnabled"/></span>
												</div>
											</c:if>
											<c:if test="${ssScheduleInfo.enabled && !ssNotification_ScheduleInfo.enabled}">
												<div>
													<div>
													  <span class="ss_bold"><ssf:nlt tag="schedule.siteSchedule"/></span>
													</div>
													<div class="margintop1 marginleft1">
														<c:set var="scheduleStringOnly" value="true"/>
														<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
														<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
													</div>
													<div class="margintop2 marginleft1 marginbottom3">
														<a href="javascript: ;" onClick="ss_toggleShowDiv('digestScheduleDiv');return false;">
														  <ssf:nlt tag="schedule.setLocalSchedule"/>
														</a>
													</div>
												</div>
											</c:if>
											<div id="digestScheduleDiv" class="marginbottom3"
											  style='<c:if test="${!ssNotification_ScheduleInfo.enabled}">display:none;</c:if>background-color: #ededed; padding: 10px;'
											>
												<div>
												  <input type="checkbox" class="ss_style" id="enabled" name="enabled" <c:if test="${ssNotification_ScheduleInfo.enabled}">checked</c:if> />
												  <span class="ss_labelRight"><ssf:nlt tag="notify.schedule.add"/> </span>
												</div>
												<c:set var="schedule" value="${ssNotification_ScheduleInfo.schedule}"/>
												<c:set var="schedPrefix" value="notify"/>
												<c:set var="scheduleStringOnly" value="false"/>
												<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
											</div>
										</div>
									
			  							<input type="radio" name="style" value="2" id="notifyType_2" 
										  <c:if test="${ssBinder.notificationDef.style=='2'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_2"><ssf:nlt tag="subscribe.message"/></label><br/>
										
			  							<input type="radio" name="style" value="3" id="notifyType_3"
			  								<c:if test="${ssBinder.notificationDef.style=='3'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_3"><ssf:nlt tag="subscribe.noattachments"/></label><br/><br/>
										
			  							<input type="radio" name="style" value="5" id="notifyType_5"
			  								<c:if test="${ssBinder.notificationDef.style=='5'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_5"><ssf:nlt tag="subscribe.text"/></label> <br/>
									</div>
			 					</td>
							</tr>
			 				<tr>
								<td>
			   						<span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.addresses"/></span>
			   						<input type="text" class="ss_style" name="addresses" id="addresses" size="86" value="${ssBinder.notificationDef.emailAddress}">
			 					</td>
							</tr>
							<% if (!(Utils.checkIfFilr())) { %>
								<tr>
									<td>
										<input type="checkbox" class="ss_style" id="teamMembers" name="teamMembers" <c:if test="${ssBinder.notificationDef.teamOn}">checked</c:if>/>
										<span class="ss_labelRight"><ssf:nlt tag="sendMail.team"/></span>
									</td>
								</tr>
							<% } %>
						</table>
						<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
							<tr>
								<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
								<td valign="top">
			  						<ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
			    						type="user" userList="${ssUsers}" sendingEmail="true" width="200px"/>
								</td>
							</tr>
							<tr>
								<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
								<td valign="top">
			  						<ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
			    						type="group" userList="${ssGroups}" sendingEmail="true" width="200px"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>

		</c:if>

		<c:if test="${!empty ssScheduleInfo2}">
			<table class="ss_style"  border="0" cellspacing="0" cellpadding="3" width="100%">
				<tr>
					<td align="left"><span class="ss_bold"><ssf:nlt tag="incoming.header" /></span> 
						<c:if test="${mail_posting_use_aliases != 'false'}">
							<ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_alias" />
						</c:if>
						<c:if test="${!ssScheduleInfo2.enabled}"><br/>[<ssf:nlt tag="incoming.disabled"/>]</c:if>
						<c:if test="${ssScheduleInfo2.enabled}"><br/>[
							<br/>[
							<c:set var="scheduleStringOnly" value="true"/>
							<c:set var="schedule" value="${ssScheduleInfo2.schedule}"/>
							<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
							]<ssf:inlineHelp jsp="workspaces_folders/misc_tools/emailScheduleInFolder"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<td style="padding-left:15px;">
						<br/>
						<span class="ss_labelAbove">
							<c:if test="${mail_posting_use_aliases == 'false'}"><ssf:nlt tag="incoming.select.user"/></c:if>
							<c:if test="${mail_posting_use_aliases != 'false'}"><ssf:nlt tag="incoming.select.alias"/></c:if>
							<ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_alias" />
						</span>
						<input type="text" name="alias" value="${ssBinder.posting.emailAddress}" size="30"> 
						<c:if test="${mail_posting_use_aliases == 'false'}">
							<br/>
							<span class="ss_labelAbove"><ssf:nlt tag="incoming.password" text="Password"/></span> 
							<c:set var="emailPassword" value=""/>
							<c:if test="${!empty ssBinder.posting.emailAddress}"><c:set var="emailPassword" value="_____"/></c:if>
							<input type="password" name="password" value="${emailPassword}" size="30"> 
						</c:if>
					</td>
				</tr>
			</table>
		</c:if>
		<br/>
	
		<div class="ss_buttonBarLeft">
			<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
			<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
		</div>
	
	</form>
</ssf:form>
</div>

</body>
</html>
