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

<div>
<input type="hidden" class="ss_user_group_results" id="propertyId_${propertyId}" 
  name="propertyId_${propertyId}"
  value="${propertyValue}" />
<table cellspacing="0px" cellpadding="0px" width="100%">
  <tbody>
	<tr>
		<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
	</tr>
	<tr>
		<td valign="top">
			<ssf:find formName="definitionbuilder" formElement="data_users" 
				type="user" userList="${ss_userList}"
				clickRoutine="ss_ug_saveResults"/>
		</td>
	</tr>
	<tr>
		<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
	</tr>
	<tr>
		<td valign="top">
			<ssf:find formName="definitionbuilder" formElement="data_groups" 
				type="group" userList="${ss_groupList}"
				clickRoutine="ss_ug_saveResults"/>
		</td>
	</tr>
  </tbody>
</table>
</div>





