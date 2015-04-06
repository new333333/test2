<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<c:set var="isEntryACL" value="false" scope="request"/>
<c:if test="${ssWorkArea.workAreaType == 'folderEntry'}">
  <c:set var="ss_hideApplications" value="1" scope="request"/>
  <c:set var="isEntryACL" value="true" scope="request"/>
</c:if>
<c:set var="isFilr" value="false"/>
<ssf:ifFilr>
  <c:set var="isFilr" value="true"/>
</ssf:ifFilr>
<div id="${ss_accessControlTableDivId}" class="ss_portlet ss_style ss_form">
<TABLE class="ss_table">
<THEAD>
<TR>
  <TH rowSpan="2" colSpan="3"></TH>
  <TH class="ss_table_paragraph_bld" noWrap="noWrap" colSpan="${ss_accessFunctionsCount}">
<c:if test="${ssWorkArea.functionMembershipInherited}">
  <ssf:nlt tag="access.roles"/>
</c:if>
<c:if test="${!ssWorkArea.functionMembershipInherited && !isEntryACL && ss_accessControlConfigureAllowed}">
  <a href="javascript:;" onClick="${ss_namespace}accessObj.showMenu(this, 'ss_addRolesMenu${ss_namespace}',40, 40);return false;"
  ><ssf:nlt tag="access.addRole"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
  src="<html:imagesPath/>pics/menudown.gif"/></a>
</c:if>
  </TH>
</TR>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<input type="hidden" name="roleIds" value="${function.id}"/>
</c:forEach>

<c:if test="${ssWorkArea.workAreaType != 'zone'}">
<TR>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TH class="ss_table_smheaders"><a href="javascript:;" 
  onClick="ss_showDivAtXY('${ss_namespace}ss_operations${function.id}');return false;"
  ><span class="ss_table_smalltext"><ssf:nlt tag="${function.name}" checkIfTag="true"/>
</span></a></TH>
</c:forEach>
</TR>
</c:if>
</THEAD>

<input type="hidden" name="principalIds" value="-1"/>
<c:if test="${ssWorkArea.workAreaType != 'zone'}">
<TBODY>

<TR>
  <TD class="ss_table_paragraph"></TD>
  <TD colSpan="2" class="ss_table_paragraph">
    <c:if test="${!isEntryACL}"><ssf:nlt tag="access.ownerOfBinder"/></c:if>
    <c:if test="${isEntryACL}"><ssf:nlt tag="access.ownerOfEntry"/></c:if>
  </TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">

<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <img height="13" width="13" <ssf:alt tag="alt.selectedByParent"/>
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssOwner}">
    src="<html:imagesPath/>pics/sym_s_checkmark.gif"
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssOwner}">
    src="<html:imagesPath/>pics/1pix.gif"
  </c:if>
  style="padding-right:10px;"/>
</c:if>

<c:if test="${!empty ssFunctionMap[function].ssOwner || isEntryACL}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
  		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_owner"
  title="<ssf:nlt tag="access.select"/>" 
  <c:if test="${ssWorkArea.functionMembershipInherited || 
  		!isEntryACL || (isEntryACL && function.scope != 'filr')}">
    checked="checked" 
  </c:if>
  <c:if test="${isEntryACL}">
    onClick="alert(ss_escapeSQ('<ssf:nlt tag="access.cannotChangeEntryOwnerAccess"/>'));return false;"
    onChange="if(!this.checked)this.click();"
  </c:if>
/>
</c:if>
<c:if test="${empty ssFunctionMap[function].ssOwner && !isEntryACL}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] ||
  		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_owner"
  title="<ssf:nlt tag="access.select"/>" />
</c:if>
</TD>
</c:forEach>
  
</TR>

<c:if test="${!isFilr || !empty ssBinderTeamMemberIds}">
<TR>
  <TD class="ss_table_paragraph"></TD>
  <TD colSpan="2" class="ss_table_paragraph"><ssf:nlt tag="access.teamMembers"/></TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">

<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <img height="13" width="13" <ssf:alt tag="alt.selectedByParent"/>
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssTeamMember}">
    src="<html:imagesPath/>pics/sym_s_checkmark.gif"
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssTeamMember}">
    src="<html:imagesPath/>pics/1pix.gif"
  </c:if>
  style="padding-right:10px;"/>
</c:if>

<c:if test="${!empty ssFunctionMap[function].ssTeamMember}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] ||
  		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_teamMember" 
  title="<ssf:nlt tag="access.select"/>" 
  checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssTeamMember}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
  		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_teamMember"
  title="<ssf:nlt tag="access.select"/>"  />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:if>

</TBODY>
</c:if>
<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.groups"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited && ss_accessControlConfigureAllowed}">
	    <a href="javascript:;" onClick="ss_clearTextareas();${ss_namespace}accessObj.showMenu(this, 'ss_addGroupsMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addGroup"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	        src="<html:imagesPath/>pics/menudown.gif"/>
	    </a>
    </c:if>
  </TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.groupTitle"/></TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.groupName"/></TH>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
  <TH class="ss_table_smheaders"><a href="javascript:;" 
  onClick="ss_showDivAtXY('${ss_namespace}ss_operations${function.id}');return false;"
  ><span class="ss_table_smalltext"><ssf:nlt tag="${function.name}" checkIfTag="true"/>
  <c:if test="${empty ssFunctionsAllowed[function.id]}">
  *
  </c:if>
  </span></a></TH>
</c:forEach>
</TR>
</THEAD>

<TBODY>
<c:set var="counter" value="0"/>
<c:forEach var="group" items="${ss_accessSortedGroups}">
<c:set var="rowClass" value="ss_table_tr_even"/>
<c:if test="${counter%2 != 0}"><c:set var="rowClass" value="ss_table_tr_odd"/></c:if>
<c:set var="counter" value="${counter + 1}"/>
<input type="hidden" name="principalIds" value="${group.id}"/>
<TR class="${rowClass}">
  <TD class="ss_table_paragraph"></TD>
  <TD class="ss_table_paragraph"><a 
    href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="groupId" value="${group.id}"/></ssf:url>"
    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.title}
    <c:if test="${ss_accessWorkareaIsPersonal && group.id == ss_accessAllUsersGroup}">
      <span class="ss_fineprint" style="vertical-align: super;">1</span>
      <c:set var="allUsersGroupSeen" value="true"/>
    </c:if></a></TD>
  <TD class="ss_table_paragraph"><a href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="groupId" value="${group.id}"/></ssf:url>"
	onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.name}
	<c:if test="${ss_accessWorkareaIsPersonal && group.id == ss_accessAllUsersGroup}">
	  <span class="ss_fineprint" style="vertical-align: super;">1</span>
	  <c:set var="allUsersGroupSeen" value="true"/>
	</c:if></a></TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">
<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <img height="13" width="13" <ssf:alt tag="alt.selectedByParent"/>
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssGroups[group.id]}">
    src="<html:imagesPath/>pics/sym_s_checkmark.gif"
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssGroups[group.id]}">
    src="<html:imagesPath/>pics/1pix.gif"
  </c:if>
  style="padding-right:10px;"/>
</c:if>
<c:if test="${!empty ssFunctionMap[function].ssGroups[group.id] && 
		(ssWorkArea.workAreaType != 'zone' || function.zoneWide)}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] ||
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}" 
    title="<ssf:nlt tag="access.select"/>" 
   checked="checked" /> 
</c:if>
<c:if test="${empty ssFunctionMap[function].ssGroups[group.id] && 
		(ssWorkArea.workAreaType != 'zone' || function.zoneWide)}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}"
    title="<ssf:nlt tag="access.select"/>"  />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>
<c:if test="${empty ss_accessSortedGroups}">
<TR>
<TD class="ss_table_paragraph" colspan="3" align="center"><ssf:nlt tag="access.no.groups"/></TD>
<c:forEach var="f" items="${ss_accessSortedFunctions}"><TD class="ss_table_paragraph"></TD></c:forEach>
</TR>
</c:if>
</TBODY>

<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.users"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited && ss_accessControlConfigureAllowed}">
	    <a href="javascript:;" onClick="ss_clearTextareas();${ss_namespace}accessObj.showMenu(this, 'ss_addUsersMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addUser"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	      src="<html:imagesPath/>pics/menudown.gif"/></a>
    </c:if>
  </TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.userTitle"/></TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.userName"/></TH>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
  <TH class="ss_table_smheaders"><a href="javascript:;" 
  onClick="ss_showDivAtXY('${ss_namespace}ss_operations${function.id}');return false;"
  ><span class="ss_table_smalltext"><ssf:nlt tag="${function.name}" checkIfTag="true"/>
  <c:if test="${empty ssFunctionsAllowed[function.id]}">
  *
  </c:if>
  </span></a></TH>
</c:forEach>
</TR>
</THEAD>

<TBODY>
<c:set var="counter" value="0"/>
<c:forEach var="user" items="${ss_accessSortedUsers}">
<c:set var="rowClass" value="ss_table_tr_even"/>
<c:if test="${counter%2 != 0}"><c:set var="rowClass" value="ss_table_tr_odd"/></c:if>
<c:set var="counter" value="${counter + 1}"/>
<input type="hidden" name="principalIds" value="${user.id}"/>
<TR class="${rowClass}">
  <TD class="ss_table_paragraph"></TD>
  <TD class="ss_table_paragraph"><ssf:userTitle user="${user}"/></TD>
  <TD class="ss_table_paragraph"><ssf:userName user="${user}"/></TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">
<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <img height="13" width="13" <ssf:alt tag="alt.selectedByParent"/> 
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssUsers[user.id]}">
    src="<html:imagesPath/>pics/sym_s_checkmark.gif"
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssUsers[user.id]}">
    src="<html:imagesPath/>pics/1pix.gif"
  </c:if>
    style="padding-right:10px;"/>
</c:if>
<c:if test="${!empty ssFunctionMap[function].ssUsers[user.id] && 
		(ssWorkArea.workAreaType != 'zone' || function.zoneWide)}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${user.id}"
    title="<ssf:nlt tag="access.select"/>" 
    checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssUsers[user.id] && 
		(ssWorkArea.workAreaType != 'zone' || function.zoneWide)}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${user.id}"
    title="<ssf:nlt tag="access.select"/>"  />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>
<c:if test="${empty ss_accessSortedUsers}">
<TR>
<TD class="ss_table_paragraph" colspan="3" align="center"><ssf:nlt tag="access.no.users"/></TD>
<c:forEach var="f" items="${ss_accessSortedFunctions}"><TD class="ss_table_paragraph"></TD></c:forEach>
</TR>
</c:if>

</TBODY>

<c:if test="${empty ss_hideApplications}">
<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.application.groups"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited && ss_accessControlConfigureAllowed}">
	    <a href="javascript:;" onClick="ss_clearTextareas();${ss_namespace}accessObj.showMenu(this, 'ss_addApplicationGroupsMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addApplicationGroup"/>
	      <c:if test="${ssWorkArea.workAreaType == 'zone'}">
	        <span class="ss_smallprint" style="vertical-align: super;">*</span>
	      </c:if>
	      <img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	        src="<html:imagesPath/>pics/menudown.gif"/>
	    </a>
    </c:if>
  </TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.application.groupTitle"/></TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.application.groupName"/></TH>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
  <TH class="ss_table_smheaders"><a href="javascript:;" 
  onClick="ss_showDivAtXY('${ss_namespace}ss_operations${function.id}');return false;"
  ><span class="ss_table_smalltext"><ssf:nlt tag="${function.name}" checkIfTag="true"/>
  <c:if test="${empty ssFunctionsAllowed[function.id]}">
  *
  </c:if>
  </span></a></TH>
</c:forEach>
</TR>
</THEAD>
</c:if>

<c:if test="${empty ss_hideApplications}">
<TBODY>
<c:set var="counter" value="0"/>
<c:forEach var="group" items="${ss_accessSortedApplicationGroups}">
<c:set var="rowClass" value="ss_table_tr_even"/>
<c:if test="${counter%2 != 0}"><c:set var="rowClass" value="ss_table_tr_odd"/></c:if>
<c:set var="counter" value="${counter + 1}"/>
<input type="hidden" name="principalIds" value="${group.id}"/>
<TR class="${rowClass}">
  <TD class="ss_table_paragraph"></TD>
  <TD class="ss_table_paragraph"><a 
    href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="applicationGroupName" value="${group.name}"/></ssf:url>"
    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.title}</a></TD>
  <TD class="ss_table_paragraph"><a href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="applicationGroupName" value="${group.name}"/></ssf:url>"
	onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.name}</a></TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">
<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <img height="13" width="13" <ssf:alt tag="alt.selectedByParent"/>
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssApplicationGroups[group.id]}">
    src="<html:imagesPath/>pics/sym_s_checkmark.gif"
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssApplicationGroups[group.id]}">
    src="<html:imagesPath/>pics/1pix.gif"
  </c:if>
  style="padding-right:10px;"/>
</c:if>
<c:if test="${!empty ssFunctionMap[function].ssApplicationGroups[group.id] && !function.zoneWide}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}" 
    title="<ssf:nlt tag="access.select"/>" 
   checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssApplicationGroups[group.id] && !function.zoneWide}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}"
    title="<ssf:nlt tag="access.select"/>"  />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>
</TBODY>
<c:if test="${empty ss_accessSortedApplicationGroups}">
<TR>
<TD class="ss_table_paragraph" colspan="3" align="center"><ssf:nlt tag="access.no.applicationGroups"/></TD>
<c:forEach var="f" items="${ss_accessSortedFunctions}"><TD class="ss_table_paragraph"></TD></c:forEach>
</TR>
</c:if>

</c:if>

<c:if test="${empty ss_hideApplications}">
<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.applications"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited && ss_accessControlConfigureAllowed}">
	    <a href="javascript:;" onClick="ss_clearTextareas();${ss_namespace}accessObj.showMenu(this, 'ss_addApplicationsMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addApplication"/>
	      <c:if test="${ssWorkArea.workAreaType == 'zone'}">
	        <span class="ss_smallprint" style="vertical-align: super;">*</span>
	      </c:if>
	      <img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	      src="<html:imagesPath/>pics/menudown.gif"/></a>
    </c:if>
  </TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.applicationTitle"/></TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.applicationName"/></TH>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
  <TH class="ss_table_smheaders"><a href="javascript:;" 
  onClick="ss_showDivAtXY('${ss_namespace}ss_operations${function.id}');return false;"
  ><span class="ss_table_smalltext"><ssf:nlt tag="${function.name}" checkIfTag="true"/>
  <c:if test="${empty ssFunctionsAllowed[function.id]}">
  *
  </c:if>
  </span></a></TH>
</c:forEach>
</TR>
</THEAD>
</c:if>

<c:if test="${empty ss_hideApplications}">
<TBODY>
<c:set var="counter" value="0"/>
<c:forEach var="application" items="${ss_accessSortedApplications}">
<c:set var="rowClass" value="ss_table_tr_even"/>
<c:if test="${counter%2 != 0}"><c:set var="rowClass" value="ss_table_tr_odd"/></c:if>
<c:set var="counter" value="${counter + 1}"/>
<input type="hidden" name="principalIds" value="${application.id}"/>
<TR class="${rowClass}">
  <TD class="ss_table_paragraph"></TD>
  <TD class="ss_table_paragraph">${application.title}</TD>
  <TD class="ss_table_paragraph">${application.name}</TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">
<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <img height="13" width="13" <ssf:alt tag="alt.selectedByParent"/> 
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssApplications[application.id]}">
    src="<html:imagesPath/>pics/sym_s_checkmark.gif"
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssApplications[application.id]}">
    src="<html:imagesPath/>pics/1pix.gif"
  </c:if>
    style="padding-right:10px;"/>
</c:if>
<c:if test="${!empty ssFunctionMap[function].ssApplications[application.id] && !function.zoneWide}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${application.id}" 
    checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssApplications[application.id] && !function.zoneWide}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id] || 
    		!ss_accessControlConfigureAllowed || function.scope == 'filr'}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${application.id}" />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>
<c:if test="${empty ss_accessSortedApplications}">
<TR>
<TD class="ss_table_paragraph" colspan="3" align="center"><ssf:nlt tag="access.no.applications"/></TD>
<c:forEach var="f" items="${ss_accessSortedFunctions}"><TD class="ss_table_paragraph"></TD></c:forEach>
</TR>
</c:if>

</TBODY>
</c:if>

</TABLE>
<c:if test="${allUsersGroupSeen}">
<br>
<div>
<span class="ss_smallprint" style="vertical-align: super;">1</span> 
<span><ssf:nlt tag="access.allUsersNote"/></span>
</div>
</c:if>
</div>
