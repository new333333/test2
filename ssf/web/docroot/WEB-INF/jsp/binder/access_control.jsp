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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%
	String roleId = (String) request.getAttribute("roleId");
	if (roleId == null) roleId = "";
%>
<c:set var="roleId" value="<%= roleId %>" />
<div class="ss_style ss_form ss_portlet">
<div class="ss_form">
<h3><ssf:nlt tag="binder.configure.access_control" text="Configure access control"/></h3>

<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.configure.access_control.inheritance" 
    text="Role membership inheritance"/></legend>
<br>
<c:set var="yes_checked" value=""/>
<c:set var="no_checked" value=""/>
<c:if test="${ssBinder.functionMembershipInherited}">
<span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.inheriting" 
 text="This folder is inheriting its access control settings from its parent folder."/></span>
<c:set var="yes_checked" value="checked"/>
</c:if>
<c:if test="${!ssBinder.functionMembershipInherited}">
<span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.notInheriting" 
 text="This folder is not inheriting its access control settings from its parent folder."/></span>
<c:set var="no_checked" value="checked"/>
</c:if>
<br><br>
<form class="ss_style" name="inheritanceForm" method="post" 
  onSubmit="return ssf_onSubmit(this);"
  action="<portlet:actionURL>
		  <portlet:param name="action" value="configure_access_control"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  </portlet:actionURL>">
<ssf:nlt tag="binder.configure.access_control.inherit"
 text="Inherit role membership from the parent folder or workspace:"/>
<br>
&nbsp;&nbsp;&nbsp;<input type="radio" name="inherit" value="yes" checked="${yes_checked}">
<ssf:nlt tag="yes" text="yes"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="radio" name="inherit" value="no" checked="${no_checked}">
<ssf:nlt tag="no" text="no"/>&nbsp;&nbsp;&nbsp;
<input type="submit" name="inheritanceBtn"
 value="<ssf:nlt tag="button.apply" text="Apply"/>">
</form>
</fieldset>
<br>
<c:if test="${!ssBinder.functionMembershipInherited}">
<c:if test="${empty roleId}">
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.configure.access_control.currentMembershipSettings" 
    text="Current membership settings"/></legend>
  <c:set var="foundOne" value="0"/>
  <c:forEach var="function" items="${ssFunctionMap}">
	<c:if test="${!empty function.value.ssUsers || !empty function.value.ssGroups}">
	  <c:set var="foundOne" value="1"/>
	</c:if>
  </c:forEach>
  <c:if test="${foundOne == '1'}">
<table class="ss_style" cellspacing="10px" cellpadding="4px" width="100%">
 <tr>
  <th align="left"><ssf:nlt tag="binder.configure.access_control.role" text="Role"/></th>
  <th align="left"><ssf:nlt tag="binder.configure.access_control.users" text="Users"/></th>
  <th align="left"><ssf:nlt tag="binder.configure.access_control.groups" text="Groups"/></th>
  <th></th>
 </tr>
	<c:forEach var="function" items="${ssFunctionMap}">
	  <c:if test="${!empty function.value.ssUsers || !empty function.value.ssGroups}">
 <tr>
  <td valign="top">
	<span calss="ss_bold"><c:out value="${function.key.name}"/></span>
  </td>
  <td valign="top">
	<c:if test="${!empty function.value.ssUsers}">
	<ul class="ss_nobullet">
	  <c:forEach var="user" items="${function.value.ssUsers}">
		<li class="ss_nobullet"><c:out value="${user.title}"/></li>
	  </c:forEach>
	</ul>
	</c:if>
	<c:if test="${empty function.value.ssUsers}">
	  <span class="ss_italic ss_gray ss_smallprint">[<ssf:nlt tag="none"/>]</span>
	</c:if>
  </td>
  <td valign="top">
	<c:if test="${!empty function.value.ssGroups}">
	<ul class="ss_nobullet">
	  <c:forEach var="user" items="${function.value.ssGroups}">
		<li class="ss_nobullet"><c:out value="${user.title}"/></li>
	  </c:forEach>
	</ul>
	</c:if>
	<c:if test="${empty function.value.ssGroups}">
	  <span class="ss_italic ss_gray ss_smallprint">[<ssf:nlt tag="none"/>]</span>
	</c:if>
  </td>
  <td valign="top">
    <form class="ss_style" style="display:inline;" method="post"
	  action="<portlet:actionURL>
			  <portlet:param name="action" value="configure_access_control"/>
			  <portlet:param name="binderId" value="${ssBinder.id}"/>
			  </portlet:actionURL>">

      <input type="hidden" name="roleId" value="${function.key.id}">
      <input type="submit" name="modifyBtn" 
        value="<ssf:nlt tag="button.modify" text="Modify"/>">&nbsp;&nbsp;
      <input type="submit" name="deleteBtn" 
        value="<ssf:nlt tag="button.delete" text="Delete"/>">
    </form>
  </td>
 </tr>
	  </c:if>
	</c:forEach>
</table>
  </c:if>

  <c:if test="${foundOne == '0'}">
[<span class="ss_italic"><ssf:nlt tag="binder.configure.access_control.nosettings"
 text="No access controls have been set."/></span>]
  </c:if>
</fieldset>
<br>
</c:if>

<c:if test="${empty roleId}">
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.configure.access_control.addRole" 
    text="Define role membership"/></legend>
  <c:if test="${empty ssFunctionMap}">
    [<span class="ss_italic"><ssf:nlt tag="binder.configure.access_control.nofunctions"
      text="No roles have been defined. Please contact the zone administrator to define "/></span>]
  </c:if>
  <c:if test="${!empty ssFunctionMap}">
<form class="ss_style" name="rolesForm" method="post" 
  onSubmit="return ssf_onSubmit(this);"
  action="<portlet:actionURL>
		  <portlet:param name="action" value="configure_access_control"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  </portlet:actionURL>">
<table class="ss_style" cellspacing="10px" cellpadding="10px">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="binder.configure.access_control.role" 
 text="Role"/></td>
<td valign="top">
<select name="roleId" >
  <option value=""><ssf:nlt tag="binder.configure.access_control.selectRole"
    text="--select the role to be added--"/></option>
<c:forEach var="function" items="${ssFunctionMap}">
  <c:if test="${empty function.value.ssUsers && empty function.value.ssGroups}">
    <option value="${function.key.id}"><c:out value="${function.key.name}"/></option>
  </c:if>
</c:forEach>
</select>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="binder.configure.access_control.users" 
 text="Users"/></td>
<td valign="top">
  <ssf:findUsers formName="rolesForm" formElement="users" type="user"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="binder.configure.access_control.groups" 
 text="Groups"/></td>
<td valign="top">
  <ssf:findUsers formName="rolesForm" formElement="groups" type="group"/>
</td>
</tr>
</table>

<input type="submit" name="addBtn"
 value="<ssf:nlt tag="button.add" text="Add"/>"
 onClick="return ss_checkRoleIdField(this)">
<br/>

</form>
</c:if>
</fieldset>
</c:if>

<c:if test="${!empty roleId}">
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.configure.access_control.modifyRole" 
    text="Modify role membership"/></legend>
  <c:if test="${!empty ssFunctionMap}">
<form class="ss_style" name="rolesForm" method="post" 
  onSubmit="return ssf_onSubmit(this);"
  action="<portlet:actionURL>
		  <portlet:param name="action" value="configure_access_control"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  </portlet:actionURL>">

<c:forEach var="function" items="${ssFunctionMap}">
  <c:if test="${roleId == function.key.id}">
<table class="ss_style" cellspacing="10px" cellpadding="10px">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="binder.configure.access_control.role" 
 text="Role"/></td>
<td valign="top">
    <c:out value="${function.key.name}"/>
    <input type="hidden" name="roleId" value="${function.key.id}">
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="binder.configure.access_control.users" 
 text="Users"/></td>
<td valign="top">
  <ssf:findUsers formName="rolesForm" formElement="users" 
    type="user" userList="${function.value.ssUsers}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="binder.configure.access_control.groups" 
 text="Groups"/></td>
<td valign="top">
  <ssf:findUsers formName="rolesForm" formElement="groups" 
    type="group" userList="${function.value.ssGroups}"/>
</td>
</tr>
</table>
  </c:if>
</c:forEach>

<input type="submit" name="addBtn"
 value="<ssf:nlt tag="button.modify" text="Modify"/>"
 onClick="return ss_checkRoleIdField(this)">&nbsp;&nbsp;
<input type="submit" name="cancelModifyBtn"
 value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
<br/>

</form>
</c:if>
</fieldset>
</c:if>

<script type="text/javascript">
function ss_checkRoleIdField(btnObj) {
	if (btnObj.form.roleId.value == "") {
		alert("<ssf:nlt tag="binder.configure.access_control.selectRoleWarning" 
		  text="Please select a role."/>")
		return false;
	}
	return true;
}
</script>

</c:if>

<c:if test="${empty roleId}">
<br/>
<form class="ss_style" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_access_control"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
		</portlet:actionURL>">

	<input type="submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</c:if>
</div>
</div>
