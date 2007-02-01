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

<div id="${ss_accessControlTableDivId}" class="ss_portlet ss_style ss_form">
<TABLE class="ss_table">
<THEAD>
<TR>
  <TH rowSpan="2" colSpan="3"></TH>
  <TH class="ss_table_paragraph_bld" noWrap="noWrap" colSpan="${ss_accessFunctionsCount}">
<c:if test="${ssBinder.functionMembershipInherited}">
  <ssf:nlt tag="access.roles"/>
</c:if>
<c:if test="${!ssBinder.functionMembershipInherited}">
  <c:if test="${ssUser.displayStyle != 'accessible'}" >
  <a href="#" onClick="ss_showAddRolesMenu${ss_namespace}(this);return false;"
  ><ssf:nlt tag="access.roles"/><img style="margin-left:4px;"
  src="<html:imagesPath/>pics/sym_s_down.gif"/></a>
  </c:if>
  
  <c:if test="${ssUser.displayStyle == 'accessible'}" >
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
    <input type="submit" class="ss_submit" name="addRoleBtn"
      value="<ssf:nlt tag="button.add" />">
  </c:if>
</c:if>
  </TH>
</TR>
<TR>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<input type="hidden" name="roleIds" value="${function.id}"/>
<TH class="ss_table_smheaders"><span class="ss_table_smalltext">${function.name}</span></TH>
</c:forEach>
</TR>
</THEAD>

<TBODY>

<input type="hidden" name="principalIds" value="-1"/>
<TR>
  <TD class="ss_table_paragraph"></TD>
  <TD colSpan="2" class="ss_table_paragraph"><ssf:nlt tag="access.ownerOfBinder"/></TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">

<c:if test="${!ssBinder.functionMembershipInherited}">
  <img height="13" width="13" 
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
  <c:if test="${ssBinder.functionMembershipInherited}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_owner" 
  checked="checked" />
</c:if>
<c:if test="${empty ssFunctionMap[function].ssOwner}">
<input type="checkbox" 
  <c:if test="${ssBinder.functionMembershipInherited}">
    disabled="disabled"
  </c:if>
  name="role_id${function.id}_owner" />
</c:if>
</TD>
</c:forEach>
  
</TR>
</TBODY>

<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld"><ssf:nlt tag="access.groups"/>
	<c:if test="${!ssBinder.functionMembershipInherited}">
	  <c:if test="${ssUser.displayStyle != 'accessible'}" >
	    <a href="#" onClick="ss_showAddGroupsMenu${ss_namespace}(this);return false;">
	      <img style="margin-left:4px;"
	        src="<html:imagesPath/>pics/sym_s_down.gif"/>
	    </a>
	  </c:if>
  
      <c:if test="${ssUser.displayStyle == 'accessible'}" >
      </c:if>
    </c:if>
  </TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.groupTitle"/></TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.groupName"/></TH>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
  <TH class="ss_table_smheaders"><span class="ss_table_smalltext">${function.name}</span></TH>
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
  <TD class="ss_table_paragraph">${group.title}</TD>
  <TD class="ss_table_paragraph">${group.name}</TD>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
<TD class="ss_table_paragraph" align="center" noWrap="noWrap">
<c:if test="${!ssBinder.functionMembershipInherited}">
  <img height="13" width="13" 
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
</TD>
</c:forEach>
  
</TR>
</c:forEach>
</TBODY>

<THEAD>
<TR>
  <TH class="ss_table_paragraph_bld"><ssf:nlt tag="access.users"/>
	<c:if test="${!ssBinder.functionMembershipInherited}">
	  <c:if test="${ssUser.displayStyle != 'accessible'}" >
	    <a href="#" onClick="ss_showAddUsersMenu${ss_namespace}(this);return false;">
	      <img style="margin-left:4px;"
	      src="<html:imagesPath/>pics/sym_s_down.gif"/></a>
	  </c:if>
  
      <c:if test="${ssUser.displayStyle == 'accessible'}" >
      </c:if>
    </c:if>
  </TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.userTitle"/></TH>
  <TH class="ss_table_smheaders"><ssf:nlt tag="access.userName"/></TH>
<c:forEach var="function" items="${ss_accessSortedFunctions}">
  <TH class="ss_table_smheaders"><span class="ss_table_smalltext">${function.name}</span></TH>
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
<c:if test="${!ssBinder.functionMembershipInherited}">
  <img height="13" width="13" 
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
</TD>
</c:forEach>
  
</TR>
</c:forEach>

</TBODY>
</TABLE>
</div>
