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

<div style="margin:6px; width:100%;">
	<h3><ssf:nlt tag="presence.configure" text="Configure buddy list"/></h3>
	<fieldset class="ss_fieldset">
		<legend class="ss_legend"><ssf:nlt tag="presence.configure.buddies" text="Buddies"/></legend>

		<table cellspacing="10px" cellpadding="10px" width="100%">
			<tr>
				<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
				<td valign="top">
					<ssf:find formName="${ss_dashboard_config_form_name}" formElement="data_users" 
    					type="user" userList="${ssDashboard.beans[ssComponentId].ssUsers}" 
    					binderId="${ssBinder.id}"/>
				</td>
			</tr>
			<tr>
				<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
				<td valign="top">
					<ssf:find formName="${ss_dashboard_config_form_name}" formElement="data_groups" 
						type="group" userList="${ssDashboard.beans[ssComponentId].ssGroups}"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<ssf:clipboard type="user" formElement="data_users" />
					<c:if test="${!empty ssBinder}">
						<ssf:teamMembers binderId="${ssBinder.id}" formElement="data_users"/>
						<br/>
						<input type="checkbox" name="data_teamOn" <c:if test="${!empty ssDashboard.beans[ssComponentId].data.teamOn}"> checked="checked" </c:if>/>
						<span class="ss_labelLeft"><ssf:nlt tag="presense.include.teamMembers"/></span>
					</c:if>						
				</td>
			</tr>
		</table>

	</fieldset>
	
</div>
