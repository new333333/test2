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

<div id="${ss_accessControlTableDivId}" class="ss_portlet ss_style ss_form">
<TABLE class="ss_table">
<THEAD>
<TR>
  <TH rowSpan="2" colSpan="3"></TH>
  <TH class="ss_table_paragraph_bld" noWrap="noWrap" colSpan="${ss_accessFunctionsCount}">
<c:if test="${ssWorkArea.functionMembershipInherited}">
  <ssf:nlt tag="access.roles"/>
</c:if>
<c:if test="${!ssWorkArea.functionMembershipInherited}">
  <ssf:ifnotaccessible>
  <a href="javascript:;" onClick="${ss_namespace}accessObj.showMenu(this, 'ss_addRolesMenu${ss_namespace}',40, 40);return false;"
  ><ssf:nlt tag="access.addRole"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
  src="<html:imagesPath/>pics/menudown.gif"/></a>
  </ssf:ifnotaccessible>
  
  <ssf:ifaccessible>
  <select name="roleIds" onChange="${ss_namespace}accessObj.selectRole();">
    <option value=""><ssf:nlt tag="binder.configure.access_control.selectRole" /></option>
    <c:forEach var="function" items="${ssFunctions}">
      <c:set var="includeRole" value="1"/>
      <c:forEach var="sortedFunction" items="${ss_accessSortedFunctions}">
        <c:if test="${sortedFunction.id == function.id}">
          <c:set var="includeRole" value="0"/>
        </c:if>
      </c:forEach>
      <c:if test="${includeRole == '1'}">
        <option value="${function.id}"><ssf:nlt tag="${function.name}" checkIfTag="true"/></option>
      </c:if>
    </c:forEach>
  </select>
    <input type="submit" class="ss_submit" name="addRoleBtn"
      value="<ssf:nlt tag="button.add" />">
  </ssf:ifaccessible>
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
  <TD colSpan="2" class="ss_table_paragraph"><ssf:nlt tag="access.ownerOfBinder"/></TD>
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

<c:if test="${!empty ssFunctionMap[function].ssOwner}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_owner" 
  checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssOwner}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_owner" />
</c:if>
</TD>
</c:forEach>
  
</TR>

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
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_teamMember" 
  checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssTeamMember}">
<input type="checkbox" 
  <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_teamMember" />
</c:if>
</TD>
</c:forEach>
  
</TR>

</TBODY>
</c:if>
<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.groups"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited}">
	  <ssf:ifnotaccessible>
	    <a href="javascript:;" onClick="${ss_namespace}accessObj.showMenu(this, 'ss_addGroupsMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addGroup"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	        src="<html:imagesPath/>pics/menudown.gif"/>
	    </a>
	  </ssf:ifnotaccessible>
  
      <ssf:ifaccessible>
        <ssf:nlt tag="access.groups"/>
      </ssf:ifaccessible>
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
    onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.title}</a></TD>
  <TD class="ss_table_paragraph"><a href="<ssf:url
		adapter="true" 
		crawlable="true"
		portletName="ss_forum" 
		action="__ajax_request"
		actionUrl="false"><ssf:param 
		name="operation" value="get_group_list"/><ssf:param 
		name="groupId" value="${group.id}"/></ssf:url>"
	onClick="ss_openUrlInWindow(this, '_blank', 400, 600);return false;">${group.name}</a></TD>
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
<c:if test="${!empty ssFunctionMap[function].ssGroups[group.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}" 
   checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssGroups[group.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}" />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>
</TBODY>

<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.users"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited}">
	  <ssf:ifnotaccessible>
	    <a href="javascript:;" onClick="${ss_namespace}accessObj.showMenu(this, 'ss_addUsersMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addUser"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	      src="<html:imagesPath/>pics/menudown.gif"/></a>
	  </ssf:ifnotaccessible>
  
      <ssf:ifaccessible>
        <ssf:nlt tag="access.users"/>
      </ssf:ifaccessible>
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
  <TD class="ss_table_paragraph">${user.title}</TD>
  <TD class="ss_table_paragraph">${user.name}</TD>
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
<c:if test="${!empty ssFunctionMap[function].ssUsers[user.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${user.id}" 
    checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssUsers[user.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${user.id}" />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>

</TBODY>

<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.application.groups"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited}">
	  <ssf:ifnotaccessible>
	    <a href="javascript:;" onClick="${ss_namespace}accessObj.showMenu(this, 'ss_addApplicationGroupsMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addApplicationGroup"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	        src="<html:imagesPath/>pics/menudown.gif"/>
	    </a>
	  </ssf:ifnotaccessible>
  
      <ssf:ifaccessible>
        <ssf:nlt tag="access.application.groups"/>
      </ssf:ifaccessible>
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
<c:if test="${!empty ssFunctionMap[function].ssApplicationGroups[group.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}" 
   checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssApplicationGroups[group.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${group.id}" />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>
</TBODY>

<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld">
	<c:if test="${ssWorkArea.functionMembershipInherited}">
	  <ssf:nlt tag="access.applications"/>
	</c:if>
	<c:if test="${!ssWorkArea.functionMembershipInherited}">
	  <ssf:ifnotaccessible>
	    <a href="javascript:;" onClick="${ss_namespace}accessObj.showMenu(this, 'ss_addApplicationsMenu${ss_namespace}', 40, 40);return false;">
	      <ssf:nlt tag="access.addApplication"/><img style="margin-left:4px;" <ssf:alt tag="alt.showMenu"/>
	      src="<html:imagesPath/>pics/menudown.gif"/></a>
	  </ssf:ifnotaccessible>
  
      <ssf:ifaccessible>
        <ssf:nlt tag="access.applications"/>
      </ssf:ifaccessible>
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
<c:if test="${!empty ssFunctionMap[function].ssApplications[application.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${application.id}" 
    checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssApplications[application.id]}">
    <input type="checkbox" 
    <c:if test="${ssWorkArea.functionMembershipInherited || empty ssFunctionsAllowed[function.id]}">
      disabled="disabled"
    </c:if>
    name="role_id${function.id}_${application.id}" />
</c:if>
</TD>
</c:forEach>
  
</TR>
</c:forEach>

</TBODY>

</TABLE>
</div>
