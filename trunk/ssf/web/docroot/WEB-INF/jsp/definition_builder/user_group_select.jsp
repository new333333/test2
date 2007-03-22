<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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





