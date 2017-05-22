<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<%@ page import="org.kablink.teaming.util.Utils" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.ResolveIds" %>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssWorkArea" type="org.kablink.teaming.security.function.WorkArea" scope="request" />
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("access.configure") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript" src="<html:rootPath />js/binder/ss_access.js"></script>
<script type="text/javascript">

/**
 * 
 */
function handleCloseBtn()
{
<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
		// Tell the Teaming GWT ui to close the administration content panel.
		if ( window.parent.ss_closeAdministrationContentPanel ) {
			window.parent.ss_closeAdministrationContentPanel();
		} else {
			ss_cancelButtonCloseWindow();
		}

		return false;
<% 	}
	else { %>
		ss_cancelButtonCloseWindow();
		return false;
<%	} %>
	
}// end handleCloseBtn()

function ss_clearTextareas() {
	var taList = self.document.getElementsByTagName("textarea");
	for (var i = 0; i < taList.length; i++) {
		if (taList[i].id.indexOf("addPrincipalText${renderResponse.namespace}") >= 0 && 
				taList[i].id.indexOf("${renderResponse.namespace}rolesForm") >= 0) {
			taList[i].value = "";
		}
	}
}
function ss_treeShowIdAccessControl${renderResponse.namespace}(id, obj, action, namespace) {
	ss_treeShowIdNoWS(id, obj, 'configure_access_control', namespace);
}
function ss_accessSelectPrincipal${renderResponse.namespace}(id) {
	${renderResponse.namespace}accessObj.selectPrincipals([id]);
}
//doens't work with direct call in accessObj
function ss_accessSelectOwner${renderResponse.namespace}(obj) {
	var propagateObj = self.document.getElementById('ss_accessPropagate${renderResponse.namespace}');
	var ownerId = ss_accessSelectedOwnerId${renderResponse.namespace};
	${renderResponse.namespace}accessObj.selectOwner(ownerId, propagateObj.checked);
}
var ss_accessSelectedOwnerId${renderResponse.namespace} = null;
function ss_accessSetOwner${renderResponse.namespace}(id, obj) {
	var selectionObj = self.document.getElementById('ss_accessSelectionSpan${renderResponse.namespace}')
	var fc = obj;
	while (fc != null) {
		if (fc.firstChild == null) break;
		fc = fc.firstChild
	}
	if (fc != null) selectionObj.innerHTML = fc.nodeValue;
	ss_accessSelectedOwnerId${renderResponse.namespace} = id;
}
var ${renderResponse.namespace}accessObj = new ssAccessControl('${renderResponse.namespace}', '${ssWorkArea.workAreaId}', '${ssWorkArea.workAreaType}');
var ss_operationSucceeded = "<ssf:nlt tag="general.request.succeeded" text="Request succeeded"/>"
var ss_operationFailed = "<ssf:nlt tag="general.request.failed" text="Request failed"/>"
</script>

<%
	String roleId = (String) request.getAttribute("roleId");
	if (roleId == null) roleId = "";
%>
<c:set var="roleId" value="<%= roleId %>" />
<div class="ss_portlet">
<ssf:form titleTag="access.configure">
<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
<div style="margin:6px; width:100%;">
<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="access.configure"/></span>
<% if ( Utils.checkIfFilr() == false ) { %>
	<ssf:showHelp guideName="adv_user" pageId="access_mngusers" />
<% } else { %>
	<ssf:showHelp guideName="admin" pageId="access_mngusers" sectionId="access_usersgroups" />
<% } %>
<br/>
<br/>
<c:choose>
<c:when test="${ssWorkArea.workAreaType == 'folder'}">
  <c:set var="binderType"><ssf:nlt tag="binder.configure.access_control.sharing.folder"/></c:set>
  <span><ssf:nlt tag="access.currentFolder"/></span>
<span class="ss_bold"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:when>
<c:when test="${ssWorkArea.workAreaType == 'zone'}">
  <c:set var="binderType" value=""/>
  <span><ssf:nlt tag="access.zone"/></span>
</c:when>
<c:otherwise>
  <c:set var="binderType"><ssf:nlt tag="binder.configure.access_control.sharing.workspace"/></c:set>
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
	<% //need to check tags for templates %>
	<span class="ss_bold"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
</c:otherwise>
</c:choose>
<br/>
<c:if test="${ssWorkArea.workAreaType != 'zone'}">
<form name="${renderResponse.namespace}changeOwnerForm" id="${renderResponse.namespace}changeOwnerForm" 
  class="ss_form" method="post" style="display:inline;" >
<c:if test="${ssWorkArea.workAreaType == 'folder'}">
  <span><ssf:nlt tag="access.folderOwner"/></span>
</c:if>
<c:if test="${ssWorkArea.workAreaType != 'folder'}">
  <span><ssf:nlt tag="access.workspaceOwner"/></span>
</c:if>
<span id="ss_accessControlOwner${renderResponse.namespace}"
  class="ss_bold"><ssf:userTitle user="${ssWorkArea.owner}"/> 
  <span class="ss_normal ss_smallprint ss_italic">(<ssf:userName user="${ssWorkArea.owner}"/>)</span></span>&nbsp;&nbsp;
<span class="ss_fineprint"><a href="javascript: ;" 
  onClick="${renderResponse.namespace}accessObj.showChangeOwnerMenu(this, 'ss_changeOwnerMenu${renderResponse.namespace}');return false;"
  >[<ssf:nlt tag="edit"/>]</a></span>
<div id="ss_changeOwnerMenuOk${renderResponse.namespace}" 
  style="display:none; border:1px solid black; background-color:#FFFFFF; width:400px">
  <span id="ss_changeOwnerMenuOkSpan${renderResponse.namespace}" 
    class="ss_bold"><ssf:nlt tag="general.request.succeeded"/></span>
</div>
  <sec:csrfInput />
</form>
</c:if>
</td>
<td align="right" valign="top">
<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url ><ssf:param 
	name="action" value="configure_access_control"/><ssf:param 
	name="actionUrl" value="true"/><ssf:param 
	name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
	name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">
<% if (ssWorkArea instanceof org.kablink.teaming.domain.TemplateBinder) { %>
  <input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
<% } else { %>
  <input type="submit" class="ss_submit" name="closeBtn"
  	onClick="return handleCloseBtn();" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
<% } %>
  <sec:csrfInput />
</form>
</td>
</tr>
</table>

<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdAccessControl${renderResponse.namespace}" 
  scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />

<c:if test="${ssWorkArea.functionMembershipInheritanceSupported}">
  <ssf:box style="rounded">
  <div style="padding:4px 8px;">
  <c:set var="yes_checked" value=""/>
  <c:set var="no_checked" value=""/>
  <c:if test="${ssWorkArea.functionMembershipInherited}">
    <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.inheriting"/></span>
    <c:set var="yes_checked" value="checked"/>
  </c:if>
  <c:if test="${!ssWorkArea.functionMembershipInherited}">
    <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.notInheriting" 
    text="This folder is not inheriting its access control settings from its parent folder."/></span>
    <c:set var="no_checked" value="checked"/>
  </c:if>
  <br/><br/>
  <form class="ss_form" name="inheritanceForm" method="post" 
    onSubmit="return ss_onSubmit(this);"
    action="<ssf:url ><ssf:param 
  		name="action" value="configure_access_control"/><ssf:param 
  		name="actionUrl" value="true"/><ssf:param 
  		name="workAreaType" value="${ssWorkArea.workAreaType}"/><ssf:param 
  		name="workAreaId" value="${ssWorkArea.workAreaId}"/></ssf:url>">
  <ssf:nlt tag="binder.configure.access_control.inherit"/>
  <br/>
  &nbsp;&nbsp;&nbsp;<input type="radio" name="inherit" value="yes" id="yes" ${yes_checked}>
  <label for="yes"><ssf:nlt tag="general.yes"/></label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <input type="radio" name="inherit" value="no" id="no" ${no_checked}>
  <label for="no"><ssf:nlt tag="general.no"/></label>&nbsp;&nbsp;&nbsp;
  <input type="submit" class="ss_submit" name="inheritanceBtn"
   value="<ssf:nlt tag="button.apply" text="Apply"/>">
    <sec:csrfInput />
  </form>
  </div>
  </ssf:box>
  <br/>
</c:if>

<c:set var="accessControlShareItemCount" value="0"/>
<c:forEach var="shareItem" items="${ss_accessControlShareItems}">
	<jsp:useBean id="shareItem" type="org.kablink.teaming.domain.ShareItem" />
	<%
	if (shareItem.isLatest()) {
		%>
		<c:set var="accessControlShareItemCount" value="${accessControlShareItemCount + 1}"/>
		<%
	}
	%>
</c:forEach>

<c:if test="${ssWorkArea.workAreaType != 'zone'}">
<ssf:box style="rounded">
  <div style="padding:4px 8px;">
    <div>
      <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.sharing"/></span>
    </div>
    <c:if test="${accessControlShareItemCount == 0}">
      <div style="padding-top:6px;">
        <span><ssf:nlt tag="binder.configure.access_control.sharing.none"><ssf:param
          name="value" value="${binderType}"/></ssf:nlt></span>
      </div>
    </c:if>
    <c:if test="${accessControlShareItemCount gt 0}">
      <div style="padding-top:6px;">
        <c:if test="${accessControlShareItemCount == 1}">
          <span><ssf:nlt tag="binder.configure.access_control.sharing.one"><ssf:param
          name="value" value="${binderType}"/></ssf:nlt></span>
        </c:if>
        <c:if test="${accessControlShareItemCount gt 1}">
          <span><ssf:nlt tag="binder.configure.access_control.sharing.more"><ssf:param
          name="value" value="${binderType}"/><ssf:param 
            name="value" value="${accessControlShareItemCount}"/></ssf:nlt>
          </span>
        </c:if>
        <span style="padding-left:10px;">
          <a class="ss_button" href="<ssf:url><ssf:param 
	  		name="action" value="configure_access_control"/><ssf:param 
	  		name="actionUrl" value="true"/><ssf:param 
	  		name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
	  		name="workAreaType" value="${ssWorkArea.workAreaType}"/><ssf:param
	  		name="operation" value="manage_sharing"/></ssf:url>"
          ><ssf:nlt tag="binder.configure.access_control.sharing.manageShares"/></a>
        </span>
      </div>
    </c:if>
  </div>
</ssf:box>
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
  action="<ssf:url><ssf:param 
  		name="action" value="configure_access_control"/><ssf:param 
  		name="actionUrl" value="true"/><ssf:param 
  		name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
  		name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">

<input type="hidden" name="btnClicked"/>
<input type="hidden" name="roleIdToAdd"/>
<c:if test="${!ssWorkArea.functionMembershipInherited && !empty ss_accessParent.ssWorkArea}">
<div>
<img src="<html:imagesPath/>pics/sym_s_checkmark.gif" <ssf:alt tag="alt.checkmark"/>/>&nbsp;
<span class="ss_italic">
<c:if test="${ss_accessParent.ssWorkArea.workAreaType == 'folder'}">
  <ssf:nlt tag="access.designatesFolder"/>
</c:if>
<c:if test="${ss_accessParent.ssWorkArea.workAreaType != 'folder'}">
  <ssf:nlt tag="access.designatesWorkspace"/>
</c:if>
</span>
<br/>
<br/>
</div>
</c:if>

<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>

<div id="ss_changeOwnerMenu${renderResponse.namespace}" 
  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF; z-index: 10;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('ss_changeOwnerMenu${renderResponse.namespace}');return false;">
      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
    </a>
  </div>
  <div style="padding:0px 10px 10px 10px;">
  <c:if test="${ssWorkArea.workAreaType == 'folder'}">
    <span class="ss_bold"><ssf:nlt tag="access.changeFolderOwner"/></span>
  </c:if>
  <c:if test="${ssWorkArea.workAreaType != 'folder'}">
    <span class="ss_bold"><ssf:nlt tag="access.changeWorkspaceOwner"/></span>
  </c:if>
  <br/>
  <table>
  <tr><td>
  <ssf:find formName="${renderResponse.namespace}changeOwnerForm" 
    formElement="changeOwnerText${renderResponse.namespace}" 
    type="user"
    clickRoutine="ss_accessSetOwner${renderResponse.namespace}"
    leaveResultsVisible="false"
    width="250px" singleItem="true"/> 
  </td><td valign="top">
  <span class="ss_bold" id="ss_accessSelectionSpan${renderResponse.namespace}"></span>
  </td></tr>
  </table>
  <input type="checkbox" name="propagate" 
    id="ss_accessPropagate${renderResponse.namespace}"/><label for="ss_accessPropagate${renderResponse.namespace}"><span style="padding-left:4px;"
  ><ssf:nlt tag="access.propagateChangeOwner" 
    text="Propagate this change to all folders beneath this one?"/></span>
  <br/></label>
  <input type="submit" value="<ssf:nlt tag="button.ok"/>"
  onClick="ss_accessSelectOwner${renderResponse.namespace}(this);return false;"/>
  <br/>
  <span class="ss_smallprint ss_italic"><ssf:nlt tag="access.noLongerCaution"/></span>
  </div>
</div>
<c:if test="${!ssWorkArea.functionMembershipInherited}">

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
    width="400px" singleItem="true"/> 
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
    width="400px" singleItem="true"/> 
  </div>
</div>
<c:if test="${ssWorkArea.workAreaType == 'zone'}">
	<c:set var="ss_hideApplications" value='true' scope="request"/>
</c:if>
<c:if test="${ssWorkArea.workAreaType != 'zone'}">
	<div id="ss_addApplicationGroupsMenu${renderResponse.namespace}" 
	  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
	  <div align="right">
	    <a href="javascript:;" onClick="ss_hideDiv('ss_addApplicationGroupsMenu${renderResponse.namespace}');return false;">
	      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
	    </a>
	  </div>
	  <div style="padding:0px 10px 10px 10px;">
	  <span class="ss_bold"><ssf:nlt tag="access.addApplicationGroup"/></span><br/>
	  <ssf:find formName="${renderResponse.namespace}rolesForm" 
	    formElement="addPrincipalText${renderResponse.namespace}" 
	    type="applicationGroup"
	    leaveResultsVisible="false"
	    clickRoutine="ss_accessSelectPrincipal${renderResponse.namespace}"
	    width="250px" singleItem="true"/> 
	  </div>
	</div>
	
	<div id="ss_addApplicationsMenu${renderResponse.namespace}" 
	  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
	  <div align="right">
	    <a href="javascript:;" onClick="ss_hideDiv('ss_addApplicationsMenu${renderResponse.namespace}');return false;">
	      <img border="0" src="<html:imagesPath/>icons/close_off.gif" <ssf:alt tag="alt.hideThisMenu"/>/>
	    </a>
	  </div>
	  <div style="padding:0px 10px 10px 10px;">
	  <span class="ss_bold"><ssf:nlt tag="access.addApplication"/></span><br/>
	  <ssf:find formName="${renderResponse.namespace}rolesForm" 
	    formElement="addPrincipalText${renderResponse.namespace}" 
	    type="application"
	    leaveResultsVisible="false"
	    clickRoutine="ss_accessSelectPrincipal${renderResponse.namespace}"
	    width="250px" singleItem="true"/>
	  </div>
	</div>
</c:if>
<c:if test="${ss_accessControlConfigureAllowed}">
<div style="padding:4px;">
<span class="ss_bold">
<a href="javascript: ${renderResponse.namespace}accessObj.addClipboardUsers();"><ssf:nlt tag="access.addClipboardUsers"/></a>
</span>
</div>
</c:if>

	<div id="ss_addRolesMenu${renderResponse.namespace}" class="ss_actions_bar5 ss_actions_bar_submenu" >
		  <div align="right">
		    <a href="javascript:;" onClick="ss_hideDiv('ss_addRolesMenu${renderResponse.namespace}');return false;">
		      <img border="0" src="<html:imagesPath/>icons/close_gray16.png" <ssf:alt tag="alt.hideThisMenu"/>/>
		    </a>
		  </div>
		<span class="ss_bold" style="color:#fff;"><ssf:nlt tag="access.addRole"/></span><br/><br/>
		<ul class="ss_actions_bar5 ss_actions_bar_submenu" style="white-space:nowrap;">
		<c:set var="ss_roleWasAdded" value="false"/>
	    <c:forEach var="function" items="${ssFunctions}">
	     <c:if test="${(function.scope == ssWorkArea.workAreaType && !ssWorkAreaIsExternalAcls) || 
	     		(function.scope == 'binder' && ssWorkArea.workAreaType == 'workspace' && !ssWorkAreaIsExternalAcls) || 
	     		(function.scope == 'binder' && ssWorkArea.workAreaType == 'folder' && !ssWorkAreaIsExternalAcls) || 
	     		(function.scope == 'binder' && ssWorkArea.workAreaType == 'profiles' && !ssWorkAreaIsExternalAcls) || 
	     		(ssWorkAreaIsExternalAcls && function.scope == 'filr') ||
	     		(!empty ssFunctionsAllowed[function.id])}">
	      <c:set var="includeRole" value="1"/>
	      <c:if test="${function.scope == 'filr'}">
	        <c:set var="includeRole" value="0"/>
	      </c:if>
	      <c:forEach var="sortedFunction" items="${ss_accessSortedFunctions}">
	        <c:if test="${sortedFunction.id == function.id}">
	          <c:set var="includeRole" value="0"/>
	        </c:if>
	      </c:forEach>
	      <c:if test="${includeRole == '1'}">
	        <c:set var="ss_roleWasAdded" value="true"/>
	        <li>
	          <a href="javascript: ;" 
	          onClick="${renderResponse.namespace}accessObj.addAccessControlRole('${function.id}');"
	          ><ssf:nlt tag="${function.name}" checkIfTag="true"/></a>
	        </li>
	      </c:if>
	     </c:if>
	    </c:forEach>
	    <c:if test="${!ss_roleWasAdded}">
	      <li><ssf:nlt tag="access.noMoreRoles"/></li>
	    </c:if>
		</ul>
	</div>
  
</c:if>


<c:set var="ss_accessControlTableDivId" value="ss_accessControlDiv${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<%@ include file="/WEB-INF/jsp/binder/access_control_table.jsp" %>

<br/>
<c:if test="${!ssWorkArea.functionMembershipInherited}">
<br/>
<input type="submit" class="ss_submit" name="okBtn" 
 onClick="ss_startSpinner();"
 value="<ssf:nlt tag="button.saveChanges" />">
</c:if>
  <sec:csrfInput />

</form>
</c:if>
<br/>
<br/>

<c:if test="${ssWorkArea.workAreaType == 'zone'}">
<div style="padding-bottom:10px;">
	<ssf:nlt tag="access.zone.applicationsFiltering"/>
</div>
</c:if>

<span class="ss_small"><ssf:nlt tag="access.superUser">
  <ssf:param name="value" useBody="true"><ssf:userTitle user="${ss_superUser}"/></ssf:param>
  <ssf:param name="value" useBody="true"><ssf:userName user="${ss_superUser}"/></ssf:param>
  </ssf:nlt></span><br/>
</div>
</ssf:box>

<c:if test="${!empty ss_accessNetFolderUrl}">
  <br/>
  <br/>

  <ssf:box style="rounded">
    <a href="${ss_accessNetFolderUrl}">View Net Folder Access Information</a>
  </ssf:box>
</c:if>

<br/>
<br/>

<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url ><ssf:param 
	name="action" value="configure_access_control"/><ssf:param 
	name="actionUrl" value="true"/><ssf:param 
	name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
	name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">
<% if (ssWorkArea instanceof org.kablink.teaming.domain.TemplateBinder) { %>
  <input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
<% } else { %>
  <input type="submit" class="ss_submit" name="closeBtn"
  	onClick="return handleCloseBtn();" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
<% } %>
  <sec:csrfInput />
</form>
</div>
</div>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="org.kablink.teaming.security.function.Function" />
<div id="${renderResponse.namespace}ss_operations${function.id}" class="ss_style ss_portlet"
  style="position:absolute; display:none; white-space:nowrap; border:1px solid #000000; 
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
  <c:if test="${function.conditional}">
    <div style="padding-top:10px;">
      <span class="ss_bold"><ssf:nlt tag="access.subjectToConditions"/></span><br>
      <c:forEach var="conditionalClause" items="${function.conditionalClauses}">
      <span style="padding-left:10px;">${conditionalClause.condition.title}</span><br/>
      </c:forEach>
    </div>
  </c:if>
</div>
</c:forEach>

</ssf:form>
</div>

</body>
</html>
