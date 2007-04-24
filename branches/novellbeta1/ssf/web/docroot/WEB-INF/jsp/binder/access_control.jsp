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
<script type="text/javascript">
function ss_selectPrincipal<portlet:namespace/>(id) {
	ss_selectPrincipals<portlet:namespace/>([id]);
}

function ss_selectPrincipals<portlet:namespace/>(ids) {
	var formObj = document.getElementById('${renderResponse.namespace}rolesForm');

	for (var i = 0; i < ids.length; i++) {
		var inputObj = document.createElement("input");
		inputObj.setAttribute("type", "hidden");
		inputObj.setAttribute("name", "principalId");
		inputObj.setAttribute("value", ids[i]);
	
		formObj.appendChild(inputObj);
	}

	formObj.btnClicked.value = "addPrincipal";
<c:if test="${ssUser.displayStyle == 'accessible'}" >
	ss_selectPrincipalAccessible<portlet:namespace/>();
</c:if>
<c:if test="${ssUser.displayStyle != 'accessible'}" >
	if (isIE) {
		//IE does not display the table right, so repaint the screen
		ss_selectPrincipalAccessible<portlet:namespace/>();
	} else {
		ss_selectPrincipalAjax<portlet:namespace/>();
	}
</c:if>
}

function ss_selectPrincipalAccessible<portlet:namespace/>() {
	setTimeout("document.forms['${renderResponse.namespace}rolesForm'].submit();", 100)
}
function ss_addAccessControlRole<portlet:namespace/>(id) {
	var formObj = document.getElementById('${renderResponse.namespace}rolesForm');
	formObj.btnClicked.value = "addRole";
	formObj.roleIdToAdd.value = id;
<c:if test="${ssUser.displayStyle == 'accessible'}" >
	ss_selectPrincipalAccessible<portlet:namespace/>();
</c:if>
<c:if test="${ssUser.displayStyle != 'accessible'}" >
	if (isIE) {
		//IE does not display the table right, so repaint the screen
		ss_selectPrincipalAccessible<portlet:namespace/>();
	} else {
		ss_selectPrincipalAjax<portlet:namespace/>();
	}
</c:if>
	ss_hideDiv('ss_addRolesMenu<portlet:namespace/>');
}

function ss_selectOwnerAjax<portlet:namespace/>(ownerId) {
	ss_setupStatusMessageDiv()
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="operation" value="set_binder_owner_id" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("namespace", "${renderResponse.namespace}");
	ajaxRequest.addKeyValue("ownerId", ownerId);
	//ajaxRequest.addKeyValue("random", ss_random++);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postSelectOwner<portlet:namespace/>);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_postSelectOwner<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_hideDiv('ss_changeOwnerMenu<portlet:namespace/>')
}

function ss_selectPrincipalAjax<portlet:namespace/>() {
	ss_setupStatusMessageDiv()
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="operation" value="get_access_control_table" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("namespace", "${renderResponse.namespace}");
	//ajaxRequest.addKeyValue("random", ss_random++);
	ajaxRequest.addFormElements("${renderResponse.namespace}rolesForm");
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postSelectPrincipal<portlet:namespace/>);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
	
	ss_hideDiv('ss_addGroupsMenu<portlet:namespace/>');
	ss_hideDiv('ss_addUsersMenu<portlet:namespace/>');	
}

function ss_postSelectPrincipal<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	var divObj = document.getElementById('ss_accessControlDiv${renderResponse.namespace}');
}

function ss_selectRole<portlet:namespace/>() {
	var formObj = document.getElementById('${renderResponse.namespace}rolesForm');
	formObj.btnClicked.value = "addRole";
	ss_selectPrincipalAjax<portlet:namespace/>();
}

function ss_treeShowIdAccessControl<portlet:namespace/>(id, obj, action) {
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

function ss_showChangeOwnerMenu<portlet:namespace/>(obj) {
	var divObj = document.getElementById('ss_changeOwnerMenu<portlet:namespace/>');
	ss_moveObjectToBody(divObj)
	var objTopOffset = 10;
	var objLeftOffset = -10;
	ss_setObjectTop(divObj, parseInt(ss_getClickPositionY() + objTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getClickPositionX(obj) + objLeftOffset))
	if (divObj.style.display == 'block' && divObj.style.visibility == 'visible') {
		ss_hideDiv('ss_changeOwnerMenu<portlet:namespace/>')
	} else {
		ss_showDiv('ss_changeOwnerMenu<portlet:namespace/>')
	}
}

function ss_showAddRolesMenu<portlet:namespace/>(obj) {
	var divObj = document.getElementById('ss_addRolesMenu<portlet:namespace/>');
	ss_moveObjectToBody(divObj)
	var objTopOffset = 40
	var objLeftOffset = 40
	ss_setObjectTop(divObj, parseInt(ss_getObjectTopAbs(obj) + objTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getObjectLeftAbs(obj) + objLeftOffset))
	if (divObj.style.display == 'block' && divObj.style.visibility == 'visible') {
		ss_hideDiv('ss_addRolesMenu<portlet:namespace/>')
	} else {
		ss_showDiv('ss_addRolesMenu<portlet:namespace/>')
	}
}

function ss_showAddGroupsMenu<portlet:namespace/>(obj) {
	var divObj = document.getElementById('ss_addGroupsMenu<portlet:namespace/>');
	ss_moveObjectToBody(divObj)
	var objTopOffset = 40
	var objLeftOffset = 40
	ss_setObjectTop(divObj, parseInt(ss_getObjectTopAbs(obj) + objTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getObjectLeftAbs(obj) + objLeftOffset))
	if (divObj.style.display == 'block' && divObj.style.visibility == 'visible') {
		ss_hideDiv('ss_addGroupsMenu<portlet:namespace/>')
	} else {
		ss_showDiv('ss_addGroupsMenu<portlet:namespace/>')
	}
}

function ss_showAddUsersMenu<portlet:namespace/>(obj) {
	var divObj = document.getElementById('ss_addUsersMenu<portlet:namespace/>');
	ss_moveObjectToBody(divObj)
	var objTopOffset = 40
	var objLeftOffset = 40
	ss_setObjectTop(divObj, parseInt(ss_getObjectTopAbs(obj) + objTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getObjectLeftAbs(obj) + objLeftOffset))
	if (divObj.style.display == 'block' && divObj.style.visibility == 'visible') {
		ss_hideDiv('ss_addUsersMenu<portlet:namespace/>')
	} else {
		ss_showDiv('ss_addUsersMenu<portlet:namespace/>')
	}
}

function ss_addClipboardUsersToAccessControlList<portlet:namespace/>() {
	var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"><ssf:param name="operation" value="get_clipboard_users" /></ssf:url>";
	url += "\&randomNumber="+ss_random++;
	var bindArgs = {
    	url: url,
		error: function(type, data, evt) {
			
		},
		load: function(type, data, evt) {
			var userIds = new Array();
			for (var i = 0; i < data.length; i++) {
				userIds.push(data[i][0]);
			}
			ss_selectPrincipals<portlet:namespace/>(userIds);		
		},
		mimetype: "text/json",
		method: "get"
	};
   
	dojo.io.bind(bindArgs);
}

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
<span class="ss_bold ss_largerprint"><ssf:nlt tag="access.configure"/></span>
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
<form name="<portlet:namespace/>changeOwnerForm" id="<portlet:namespace/>changeOwnerForm" 
  class="ss_form" method="post" style="display:inline;" action="" >
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.folderOwner"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.workspaceOwner"/></span>
</c:if>
<span id="ss_accessControlOwner<portlet:namespace/>"
  class="ss_bold">${ssBinder.owner.title} 
  <span class="ss_normal ss_smallprint ss_italic">(${ssBinder.owner.name})</span></span>&nbsp;&nbsp;
<span class="ss_fineprint"><a href="javascript: ;" 
  onClick="ss_showChangeOwnerMenu<portlet:namespace/>(this);return false;">[<ssf:nlt tag="edit"/>]</a></span>
</form>
</td>
<td align="right" valign="top">
<form class="ss_form" method="post" style="display:inline;" 
	action="<portlet:actionURL><portlet:param 
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
    action="<portlet:actionURL><portlet:param 
  		name="action" value="configure_access_control"/><portlet:param 
  		name="binderType" value="${ssBinder.entityType}"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
  <ssf:nlt tag="binder.configure.access_control.inherit"/>
  <br/>
  &nbsp;&nbsp;&nbsp;<input type="radio" name="inherit" value="yes" ${yes_checked}>
  <ssf:nlt tag="answer.yes"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <input type="radio" name="inherit" value="no" ${no_checked}>
  <ssf:nlt tag="answer.no"/>&nbsp;&nbsp;&nbsp;
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
<img src="<html:imagesPath/>pics/sym_s_checkmark.gif"/>&nbsp;
<span class="ss_italic">
<c:if test="${ss_accessParent.ssBinder.entityType == 'folder'}">
  <ssf:nlt tag="access.designatesFolder"/>
</c:if>
<c:if test="${ss_accessParent.ssBinder.entityType != 'folder'}">
  <ssf:nlt tag="access.designatesWorkspace"/>
</c:if>
</span>
<br/>
*&nbsp;&nbsp;&nbsp;
<span class="ss_italic"><ssf:nlt tag="access.cannotChangeRoleMembership"/></span>
<br/>
<br/>
</div>
</c:if>

<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>

<div id="ss_changeOwnerMenu<portlet:namespace/>" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
  <div align="right">
    <a href="#" onClick="ss_hideDiv('ss_changeOwnerMenu<portlet:namespace/>');return false;">
      <img border="0" src="<html:imagesPath/>box/close_off.gif"/>
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
    clickRoutine="ss_selectOwnerAjax${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
  </div>
</div>

<c:if test="${!ssBinder.functionMembershipInherited}">

<div id="ss_addGroupsMenu<portlet:namespace/>" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
  <div align="right">
    <a href="#" onClick="ss_hideDiv('ss_addGroupsMenu<portlet:namespace/>');return false;">
      <img border="0" src="<html:imagesPath/>box/close_off.gif"/>
    </a>
  </div>
  <div style="padding:0px 10px 10px 10px;">
  <span class="ss_bold"><ssf:nlt tag="access.addGroup"/></span><br/>
  <ssf:find formName="${renderResponse.namespace}rolesForm" 
    formElement="addPrincipalText${renderResponse.namespace}" 
    type="group"
    leaveResultsVisible="false"
    clickRoutine="ss_selectPrincipal${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
  </div>
</div>

<div id="ss_addUsersMenu<portlet:namespace/>" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
  <div align="right">
    <a href="#" onClick="ss_hideDiv('ss_addUsersMenu<portlet:namespace/>');return false;">
      <img border="0" src="<html:imagesPath/>box/close_off.gif"/>
    </a>
  </div>
  <div style="padding:0px 10px 10px 10px;">
  <span class="ss_bold"><ssf:nlt tag="access.addUser"/></span><br/>
  <ssf:find formName="${renderResponse.namespace}rolesForm" 
    formElement="addPrincipalText${renderResponse.namespace}" 
    type="user"
    leaveResultsVisible="false"
    clickRoutine="ss_selectPrincipal${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
  </div>
</div>

<span class="ss_bold"><a href="javascript: ss_addClipboardUsersToAccessControlList${ss_namespace}();"><ssf:nlt tag="access.addClipboardUsers"/></a></span><br/>

  <c:if test="${ssUser.displayStyle != 'accessible'}" >
  
	<div id="ss_addRolesMenu<portlet:namespace/>" class="ss_actions_bar2 ss_actions_bar_submenu" >
		<ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
		<span class="ss_bold"><ssf:nlt tag="access.addRole"/></span><br/><br/>
	    <c:forEach var="function" items="${ssFunctionMap}">
	      <c:set var="includeRole" value="1"/>
	      <c:forEach var="sortedFunction" items="${ss_accessSortedFunctions}">
	        <c:if test="${sortedFunction.id == function.key.id}">
	          <c:set var="includeRole" value="0"/>
	        </c:if>
	      </c:forEach>
	      <c:if test="${includeRole == '1'}">
	        <li>
	          <a href="javascript: ;" 
	          onClick="ss_addAccessControlRole${ss_namespace}('${function.key.id}');"
	          ><c:out value="${function.key.name}"/></a>
	        </li>
	      </c:if>
	    </c:forEach>
		</ul>
	</div>
  </c:if>
  
</c:if>


<c:set var="ss_accessControlTableDivId" value="ss_accessControlDiv${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<%@ include file="/WEB-INF/jsp/binder/access_control_table.jsp" %>

<br/>
<c:if test="${!ssBinder.functionMembershipInherited}">
<br/>
<input type="submit" class="ss_submit" name="okBtn"
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
	action="<portlet:actionURL><portlet:param 
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
<div id="<portlet:namespace/>ss_operations${function.id}" class="ss_style ss_portlet"
  style="position:absolute; display:none; width:300px; border:1px solid #000000; 
  margin-bottom:10px; padding:4px; background-color:#ffffff;">
  <div align="right">
    <a href="#" onClick="ss_hideDiv('<portlet:namespace/>ss_operations${function.id}');return false;">
      <img border="0" src="<html:imagesPath/>box/close_off.gif"/>
    </a>
  </div>
  <div>
	<span class="ss_bold">${function.name}</span><br/>
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function.operations}">
			<c:if test="${roleOperation.name == operation.key}">
				<c:out value="${operation.value}"/><br>
			</c:if>
		</c:forEach>
	</c:forEach>	
  </div>	
</div>
</c:forEach>

</div>

