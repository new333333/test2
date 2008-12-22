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

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_style ss_portlet">
<br/>

<form class="ss_style ss_form" 
  id="${renderResponse.namespace}fm" 
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
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
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
</table>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
<tr><td >
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

