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
<%@ page import="org.kablink.teaming.util.NLT" %>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div class="ss_style ss_portlet">

	<c:if test="${!empty ssException}">
		<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
	</c:if>
	<br/>
	<br/>
	<span class="ss_bold"><ssf:nlt tag="notify.forum.label"/>&nbsp;${ssBinder.title}</span><br/>

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
						<ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_notifications_folder_settings"/>
					</td>
				</tr>
				<tr>
					<td style="padding-left:15px;">
						<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
							<tr>
								<td>
			    					<br/><span class="ss_bold"><ssf:nlt tag="subscribe.select.type"/></span><br/>
									<div class="ss_indent_medium">
			  							<input type="radio" name="style" value="1" id="notifyType_1"
			  									<c:if test="${ssBinder.notificationDef.style=='1'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_1"><ssf:nlt tag="subscribe.digest"/></label>&nbsp;&nbsp;[
										<c:if test="${ssScheduleInfo.enabled}">
											<c:set var="scheduleStringOnly" value="true"/>
											<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
											<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
										</c:if>
										<c:if test="${!ssScheduleInfo.enabled}">
											<ssf:nlt tag="administration.notify.nodefault.schedule"/>
										</c:if>
										]<br/>
			  							<input type="radio" name="style" value="2" 
										  <c:if test="${ssBinder.notificationDef.style=='2'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_2"><ssf:nlt tag="subscribe.message"/></label> <br/>
			  							<input type="radio" name="style" value="3" 
			  								<c:if test="${ssBinder.notificationDef.style=='3'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_3"><ssf:nlt tag="subscribe.noattachments"/></label><br/>
			  							<input type="radio" name="style" value="5" 
			  								<c:if test="${ssBinder.notificationDef.style=='5'}"> checked="checked"</c:if> 
			  							/>
										<label for="notifyType_3"><ssf:nlt tag="subscribe.text"/></label><br/><br/>
									</div>
			 					</td>
							</tr>
			 				<tr>
								<td>
			   						<span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.addresses"/></span>
			   						<input type="text" class="ss_style" name="addresses" id="addresses" size="86" value="${ssBinder.notificationDef.emailAddress}">
			 					</td>
							</tr>
							<tr>
								<td>
									<input type="checkbox" class="ss_style" id="teamMembers" name="teamMembers" <c:if test="${ssBinder.notificationDef.teamOn}">checked</c:if>/>
									<span class="ss_labelRight"><ssf:nlt tag="sendMail.team"/></span>
								</td>
							</tr>
						</table>
						<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
							<tr>
								<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
								<td valign="top">
			  						<ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
			    						type="user" userList="${ssUsers}"/>
								</td>
							</tr>
							<tr>
								<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
								<td valign="top">
			  						<ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
			    						type="group" userList="${ssGroups}"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<hr/>
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
</div>