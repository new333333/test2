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
<script type="text/javascript" src="<html:rootPath />js/binder/ss_access.js"></script>
<script type="text/javascript">
function ss_treeShowIdAccessControl${renderResponse.namespace}(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="configure_access_control"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}
function ss_accessSelectPrincipal${renderResponse.namespace}(id) {
	${renderResponse.namespace}accessObj.selectPrincipals([id]);
}
//doens't work with direct call in accessObj
function ss_accessSelectOwner${renderResponse.namespace}(ownerId, obj) {
	${renderResponse.namespace}accessObj.selectOwnerAjax(ownerId, obj);
}
var ${renderResponse.namespace}accessObj = new ssAccessControl('${renderResponse.namespace}', '${ssBinder.id}');

</script>

<%
	String roleId = (String) request.getAttribute("roleId");
	if (roleId == null) roleId = "";
%>
<c:set var="roleId" value="<%= roleId %>" />
<div class="ss_portlet">
<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
<div style="margin:6px; width:100%;">
<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="access.configure"/></span> <ssf:inlineHelp jsp="workspaces_folders/menus_toolbars/access_control"/>
<br/>
<br/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
</c:if>
<% //need to check tags for templates %>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
<br/>
<form name="${renderResponse.namespace}changeOwnerForm" id="${renderResponse.namespace}changeOwnerForm" 
  class="ss_form" method="post" style="display:inline;" action="" >
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.folderOwner"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.workspaceOwner"/></span>
</c:if>
<span id="ss_accessControlOwner${renderResponse.namespace}"
  class="ss_bold">${ssBinder.owner.title} 
  <span class="ss_normal ss_smallprint ss_italic">(${ssBinder.owner.name})</span></span>&nbsp;&nbsp;
<span class="ss_fineprint"><a href="javascript: ;" 
  onClick="${renderResponse.namespace}accessObj.showChangeOwnerMenu(this, 'ss_changeOwnerMenu${renderResponse.namespace}');return false;">[<ssf:nlt tag="edit"/>]</a></span>
</form>
</td>
<td align="right" valign="top">
<form class="ss_form" method="post" style="display:inline;" 
	action="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_access_control"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</td>
</tr>
</table>

<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdAccessControl${renderResponse.namespace}" 
  scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

<c:if test="${ssBinder.functionMembershipInheritanceSupported}">
  <ssf:box style="rounded">
  <div style="padding:4px 8px;">
  <c:set var="yes_checked" value=""/>
  <c:set var="no_checked" value=""/>
  <c:if test="${ssBinder.functionMembershipInherited}">
    <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.inheriting"/></span>
    <c:set var="yes_checked" value="checked"/>
  </c:if>
  <c:if test="${!ssBinder.functionMembershipInherited}">
    <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.notInheriting" 
    text="This folder is not inheriting its access control settings from its parent folder."/></span>
    <c:set var="no_checked" value="checked"/>
  </c:if>
  <br/><br/>
  <form class="ss_form" name="inheritanceForm" method="post" 
    onSubmit="return ss_onSubmit(this);"
    action="<portlet:actionURL windowState="maximized"><portlet:param 
  		name="action" value="configure_access_control"/><portlet:param 
  		name="binderType" value="${ssBinder.entityType}"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
  <ssf:nlt tag="binder.configure.access_control.inherit"/> <ssf:inlineHelp tag="ihelp.other.inherit_roles"/>
  <br/>
  &nbsp;&nbsp;&nbsp;<input type="radio" name="inherit" value="yes" ${yes_checked}>
  <ssf:nlt tag="general.yes"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <input type="radio" name="inherit" value="no" ${no_checked}>
  <ssf:nlt tag="general.no"/>&nbsp;&nbsp;&nbsp;
  <input type="submit" class="ss_submit" name="inheritanceBtn"
   value="<ssf:nlt tag="button.apply" text="Apply"/>">
  </form>
  </div>
  </ssf:box>
  <br/>
</c:if>



<ssf:box style="rounded">
<div style="padding:4px 8px;">
<c:if test="${ss_accessFunctionsCount <= 0}">
<span class="ss_bold ss_italic"><ssf:nlt tag="access.noRoles"/></span><br/>
</c:if>
<c:if test="${ss_accessFunctionsCount > 0}">
<form class="ss_form" 
  name="${renderResponse.namespace}rolesForm" 
  id="${renderResponse.namespace}rolesForm" 
  method="post" 
  action="<portlet:actionURL><portlet:param 
  		name="action" value="configure_access_control"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/><portlet:param 
  		name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>">

<input type="hidden" name="btnClicked"/>
<input type="hidden" name="roleIdToAdd"/>
<c:if test="${!ssBinder.functionMembershipInherited && !empty ss_accessParent.ssBinder}">
<div>
<img src="<html:imagesPath/>pics/sym_s_checkmark.gif" <ssf:alt tag="alt.checkmark"/>/>&nbsp;
<span class="ss_italic">
<c:if test="${ss_accessParent.ssBinder.entityType == 'folder'}">
  <ssf:nlt tag="access.designatesFolder"/>
</c:if>
<c:if test="${ss_accessParent.ssBinder.entityType != 'folder'}">
  <ssf:nlt tag="access.designatesWorkspace"/>
</c:if>
</span>
<br/>
<br/>
</div>
</c:if>

<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>

<div id="ss_changeOwnerMenu${renderResponse.namespace}" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('ss_changeOwnerMenu${renderResponse.namespace}');return false;">
      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
    </a>
  </div>
  <div style="padding:0px 10px 10px 10px;">
  <c:if test="${ssBinder.entityType == 'folder'}">
    <span class="ss_bold"><ssf:nlt tag="access.changeFolderOwner"/></span>
  </c:if>
  <c:if test="${ssBinder.entityType != 'folder'}">
    <span class="ss_bold"><ssf:nlt tag="access.changeWorkspaceOwner"/></span>
  </c:if>
  <br/>
  <ssf:find formName="${renderResponse.namespace}changeOwnerForm" 
    formElement="changeOwnerText${renderResponse.namespace}" 
    type="user"
    leaveResultsVisible="false"
    clickRoutine="ss_accessSelectOwner${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
  </div>
</div>

<c:if test="${!ssBinder.functionMembershipInherited}">

<div id="ss_addGroupsMenu${renderResponse.namespace}" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('ss_addGroupsMenu${renderResponse.namespace}');return false;">
      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
    </a>
  </div>
  <div style="padding:0px 10px 10px 10px;">
  <span class="ss_bold"><ssf:nlt tag="access.addGroup"/></span><br/>
  <ssf:find formName="${renderResponse.namespace}rolesForm" 
    formElement="addPrincipalText${renderResponse.namespace}" 
    type="group"
    leaveResultsVisible="false"
    clickRoutine="ss_accessSelectPrincipal${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
  </div>
</div>

<div id="ss_addUsersMenu${renderResponse.namespace}" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('ss_addUsersMenu${renderResponse.namespace}');return false;">
      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
    </a>
  </div>
  <div style="padding:0px 10px 10px 10px;">
  <span class="ss_bold"><ssf:nlt tag="access.addUser"/></span><br/>
  <ssf:find formName="${renderResponse.namespace}rolesForm" 
    formElement="addPrincipalText${renderResponse.namespace}" 
    type="user"
    leaveResultsVisible="false"
    clickRoutine="ss_accessSelectPrincipal${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
  </div>
</div>

<span class="ss_bold"><a href="javascript: ${renderResponse.namespace}accessObj.addClipboardUsers();"><ssf:nlt tag="access.addClipboardUsers"/></a></span><br/>

  <ssf:ifnotaccessible>
  
	<div id="ss_addRolesMenu${renderResponse.namespace}" class="ss_actions_bar2 ss_actions_bar_submenu" >
		<ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
		  <div align="right">
		    <a href="javascript:;" onClick="ss_hideDiv('ss_addRolesMenu${renderResponse.namespace}');return false;">
		      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
		    </a>
		  </div>
		<span class="ss_bold"><ssf:nlt tag="access.addRole"/></span><br/><br/>
	    <c:forEach var="function" items="${ssFunctions}">
	      <c:set var="includeRole" value="1"/>
	      <c:forEach var="sortedFunction" items="${ss_accessSortedFunctions}">
	        <c:if test="${sortedFunction.id == function.id}">
	          <c:set var="includeRole" value="0"/>
	        </c:if>
	      </c:forEach>
	      <c:if test="${includeRole == '1'}">
	        <li>
	          <a href="javascript: ;" 
	          onClick="${renderResponse.namespace}accessObj.addAccessControlRole('${function.id}');"
	          ><ssf:nlt tag="${function.name}" checkIfTag="true"/></a>
	        </li>
	      </c:if>
	    </c:forEach>
		</ul>
	</div>
  </ssf:ifnotaccessible>
  
</c:if>


<c:set var="ss_accessControlTableDivId" value="ss_accessControlDiv${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<%@ include file="/WEB-INF/jsp/binder/access_control_table.jsp" %>

<br/>
<c:if test="${!ssBinder.functionMembershipInherited}">
<br/>
<input type="submit" class="ss_submit" name="okBtn" 
 onClick="ss_startSpinner();"
 value="<ssf:nlt tag="button.saveChanges" />">
</c:if>


</form>
</c:if>
<br/>
<br/>
<span class="ss_italic ss_small">[<ssf:nlt tag="access.superUser">
  <ssf:param name="value" value="${ss_superUser.title}"/>
  <ssf:param name="value" value="${ss_superUser.name}"/>
  </ssf:nlt>]</span><br/>
</div>
</ssf:box>


<br/>
<br/>

<form class="ss_form" method="post" style="display:inline;" 
	action="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_access_control"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
</div>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="com.sitescape.team.security.function.Function" />
<div id="${renderResponse.namespace}ss_operations${function.id}" class="ss_style ss_portlet"
  style="position:absolute; display:none; width:300px; border:1px solid #000000; 
  margin-bottom:10px; padding:4px; background-color:#ffffff;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('${renderResponse.namespace}ss_operations${function.id}');return false;">
      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
    </a>
  </div>
  <div>
	<span class="ss_bold"><%= NLT.getDef(function.getName()) %></span><br/>
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function.operations}">
			<c:if test="${roleOperation.name == operation.value}">
				<c:out value="${operation.key}"/><br>
			</c:if>
		</c:forEach>
	</c:forEach>	
  </div>	
</div>
</c:forEach>

</div>

