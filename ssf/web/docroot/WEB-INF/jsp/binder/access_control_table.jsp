<%
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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

<div id="${ss_accessControlTableDivId}">
<table class="ss_table">
<thead>
<tr>
  <th rowspan="2" colspan="3"></th>
  <th class="ss_table_paragraph_bld" nowrap="nowrap" colspan="${ss_accessFunctionsCount}"><ssf:nlt tag="access.roles"/>
<c:if test="${!ssBinder.functionMembershipInherited}">
  <br/>
  <select name="roleIds" onChange="ss_selectRole${ss_namespace}();">
    <option value=""><ssf:nlt tag="binder.configure.access_control.selectRole" /></option>
    <c:forEach var="function" items="${ssFunctionMap}">
      <c:set var="includeRole" value="1"/>
      <c:forEach var="sortedFunction" items="${ss_accessSortedFunctions}">
        <c:if test="${sortedFunction.id == function.key.id}">
          <c:set var="includeRole" value="0"/>
        </c:if>
      </c:forEach>
      <c:if test="${includeRole == '1'}">
        <option value="${function.key.id}"><c:out value="${function.key.name}"/></option>
      </c:if>
    </c:forEach>
  </select>
  <c:if test="${ssUser.displayStyle == 'accessible'}" >
    <input type="submit" class="ss_submit" name="addRoleBtn"
      value="<ssf:nlt tag="button.add" />">
  </c:if>
</c:if>
  </th>
</tr>
<tr>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<input type="hidden" name="roleIds" value="${function.id}"/>
<th class="ss_table_smheaders"><span class="ss_table_smalltext">${function.name}</span></th>
</c:forEach>
</tr>
</thead>
<tbody>
<c:set var="ss_groupsProcessed" value="0"/>
<c:forEach var="group" items="${ss_accessSortedGroups}">
<input type="hidden" name="principalIds" value="${group.id}"/>
<tr>
<c:if test="${ss_groupsProcessed == 0}">
  <td class="ss_table_paragraph_bld" rowspan="${ss_accessGroupsCount}"><ssf:nlt tag="access.groups"/></td>
  <c:set var="ss_groupsProcessed" value="1"/>
</c:if>
  <td class="ss_table_paragraph">${group.title}</td><td class="ss_table_paragraph">${group.name}</td>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<td class="ss_table_paragraph" align="center">

<c:if test="${!ssBinder.functionMembershipInherited}">
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssGroups[group.id]}">
    <img height="13" width="13" src="<html:imagesPath/>pics/sym_s_checkmark.gif" />
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssGroups[group.id]}">
    <img height="13" width="13" src="<html:imagesPath/>pics/1pix.gif"/>
  </c:if>
</c:if>

<c:if test="${!empty ssFunctionMap[function].ssGroups[group.id]}">
<input type="checkbox" 
  <c:if test="${ssBinder.functionMembershipInherited}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_${group.id}" 
  checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssGroups[group.id]}">
<input type="checkbox" 
  <c:if test="${ssBinder.functionMembershipInherited}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_${group.id}" />
</c:if>
</td>
</c:forEach>
  
</tr>
</c:forEach>

<c:set var="ss_usersProcessed" value="0"/>
<c:forEach var="user" items="${ss_accessSortedUsers}">
<input type="hidden" name="principalIds" value="${user.id}"/>
<tr>
<c:if test="${ss_usersProcessed == 0}">
  <td class="ss_table_paragraph_bld" rowspan="${ss_accessUsersCount}"><ssf:nlt tag="access.users"/></td>
  <c:set var="ss_usersProcessed" value="1"/>
</c:if>
  <td class="ss_table_paragraph">${user.title}</td><td class="ss_table_paragraph">${user.name}</td>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<td class="ss_table_paragraph" align="center">

<c:if test="${!ssBinder.functionMembershipInherited}">
  <c:if test="${!empty ss_accessParent.ssFunctionMap[function].ssUsers[user.id]}">
    <img height="13" width="13" src="<html:imagesPath/>pics/sym_s_checkmark.gif" style="padding-right:10px;"/>
  </c:if>
  <c:if test="${empty ss_accessParent.ssFunctionMap[function].ssUsers[user.id]}">
    <img height="13" width="13" src="<html:imagesPath/>pics/1pix.gif" style="padding-right:10px;"/>
  </c:if>
</c:if>

<c:if test="${!empty ssFunctionMap[function].ssUsers[user.id]}">
<input type="checkbox" 
  <c:if test="${ssBinder.functionMembershipInherited}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_${user.id}" 
  checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssUsers[user.id]}">
<input type="checkbox" 
  <c:if test="${ssBinder.functionMembershipInherited}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_${user.id}" />
</c:if>
</td>
</c:forEach>
  
</tr>
</c:forEach>

</tbody>
</table>
</div>
