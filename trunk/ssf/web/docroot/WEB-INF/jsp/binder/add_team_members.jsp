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

<div class="ss_style ss_portlet">
<br/>

<form class="ss_style ss_form" 
  id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" 
  method="post" onSubmit="return ss_onSubmit(this);">
<span class="ss_titlebold"><ssf:nlt tag="toolbar.teams.addMember"/></span></br></br>

<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
<c:if test="${ssBinder.teamMembershipInherited}">
<tr>
  <td colspan="3"><span class="ss_bold"><ssf:nlt tag="team.inheritingTeamFromParent"/></span></td>
</tr>
<tr>
  <td>&nbsp;&nbsp;&nbsp;</td>
  <td><ssf:nlt tag="team.inheritTeamFromParent.no"/></td>
  <td><input type="submit" class="ss_submit" name="inheritBtnNo" value="<ssf:nlt tag="button.Yes"/>" 
    onClick="ss_buttonSelect('inheritBtnNo');">
  </td>
</tr>
</c:if>
<c:if test="${!ssBinder.teamMembershipInherited}">
<tr>
  <td colspan="3"><span class="ss_bold"><ssf:nlt tag="team.inheritingTeamFromParentNot"/></span></td>
</tr>
<tr>
  <td>&nbsp;&nbsp;&nbsp;</td>
  <td><ssf:nlt tag="team.inheritTeamFromParent.yes"/></td>
  <td><input type="submit" class="ss_submit" name="inheritBtnYes" value="<ssf:nlt tag="button.Yes"/>" 
    onClick="ss_buttonSelect('inheritBtnYes');">
  </td>
</tr>
</c:if>
</table>
<br/>
<br/>

<c:if test="${ssBinder.teamMembershipInherited}">
<span class="ss_bold"><ssf:nlt tag="team.inheritedTeamMembers"/></span><br/>
<ul>
<c:forEach var="teamMember" items="${ssUsers}">
  <li>${teamMember.title} <span class="ss_smallprint">(${teamMember.name})</span></li>
</c:forEach>
<c:forEach var="teamMember" items="${ssGroups}">
  <li>${teamMember.title} <span class="ss_smallprint">(${teamMember.name})</span></li>
</c:forEach>
</ul>
</c:if>

<c:if test="${!ssBinder.teamMembershipInherited}">
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}" binderId="${ssBinder.id}"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}"/>
</td>
</tr>
<tr><td colspan="2">
	<ssf:clipboard type="user" formElement="users" />
</td></tr>
</table>
</c:if>

<br/>  

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" 
  onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" 
  onClick="ss_buttonSelect('closeBtn');">

</form>
</div>

