<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

<%@ page import="org.kablink.teaming.util.Utils" %>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("access.configure") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript" src="<html:rootPath />js/binder/ss_access.js"></script>
<script type="text/javascript">
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
<div class="ss_portlet diag_modal2">
	<ssf:form titleTag="access.configure">

<% if ( Utils.checkIfFilr() == false ) { %>
	<ssf:showHelp guideName="adv_user" pageId="access_mngusers" sectionId="access_mngusers_entry" />
<% } %>
<% else { %>
	<ssf:showHelp guideName="admin" pageId="access_mngusers" sectionId="access_usersgroups-access_mngusers_entry" />
<% } %>

	<div class="ss_style ss_form" style="margin:0px; padding:10px 16px 10px 10px;">
		<div>
			<table cellpadding="0" cellspacing="0" width="100%">
				<tr>
				<td valign="top">
					<table cellpadding="0" cellspacing="5" class="margintop3">
						<tr>
							<td><span><ssf:nlt tag="access.currentEntry"/></span></td>
							<td style="padding-left:10px;">
								<span class="ss_bold"><ssf:nlt tag="${ssWorkArea.title}" checkIfTag="true"/></span>
							</td>
						</tr>
						<tr>
							<td>
								<span><ssf:nlt tag="access.entryOwner"/></span>
							</td>
							<td style="padding-left:10px;">
								<span class="ss_bold"><ssf:userTitle user="${ssWorkArea.owner}"/> 
								<span class="ss_normal ss_smallprint ss_italic">(<ssf:userName user="${ssWorkArea.owner}"/>)</span></span>
							</td>
						</tr>
					</table>
				</td>
				<td align="right" valign="top">
					<form class="ss_form" method="post" style="display:inline;" 
						action="<ssf:url ><ssf:param 
						name="action" value="configure_access_control"/><ssf:param 
						name="actionUrl" value="true"/><ssf:param 
						name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
						name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">
					  <input type="button" class="ss_submit" name="closeBtn" 
						value="<ssf:nlt tag="button.close" text="Close"/>"
						onClick="ss_cancelButtonCloseWindow();return false;"/>
						<sec:csrfInput />
					</form>
				</td>
			</tr>
		</table>

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

  <div class="margintop3 ss_newpage_box" style="padding:10px; background-color: #f6f6f6;">
    <div>
      <span class="ss_bold"><ssf:nlt tag="binder.configure.access_control.sharing"/></span>
    </div>
    <c:if test="${accessControlShareItemCount == 0}">
      <div style="padding-top:6px;">
		<c:if test="${empty binderType}">
	        <span><ssf:nlt tag="binder.configure.access_control.sharing.none.entry"><ssf:param
	          name="value" value="${binderType}"/></ssf:nlt></span>
        </c:if>
		<c:if test="${!empty binderType}">
	        <span><ssf:nlt tag="binder.configure.access_control.sharing.none"><ssf:param
	          name="value" value="${binderType}"/></ssf:nlt></span>
        </c:if>
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
		
		<div class="margintop3 ss_newpage_box" style="padding:10px; background-color: #f6f6f6;">
			<c:if test="${ssEntryHasEntryAcl && !ss_accessControlConfigureAllowed}">
			<div>
  				<span class="ss_italic">[<ssf:nlt tag="access.mode.readonly"/>]</span>
			</div>
			</c:if>

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
			
			<div>
			  <c:if test="${!ssEntryHasEntryAcl}"><span class="ss_bold">
				<ssf:nlt tag="access.noEntryAclSet"/></span>
				<span class="ss_smallprint" style="padding-left:20px;">
				  [<a href="<ssf:url action="configure_access_control"><ssf:param
					  name="workAreaId" value="${ssWorkArea.parentBinder.id}"/><ssf:param
					  name="workAreaType" value="${ssWorkArea.parentBinder.workAreaType}"/><ssf:param
					  name="operation" value="view_access"/></ssf:url>"
					onClick="ss_openUrlInPortlet(this.href, true, '600', '700');return false;"
				  ><ssf:nlt tag="access.viewWhoHasFolderAccess"/></a>]
				</span>
			  </c:if>
			  <c:if test="${ssEntryHasEntryAcl}"><span class="ss_bold"><ssf:nlt tag="access.entryAclSet"/></span></c:if>
			  <div class="margintop3" style="padding:4px 0px 10px 10px;">
			   <c:if test="${ss_accessControlConfigureAllowed}">
				<input type="radio" name="aclSelection" value="folder"  
				  <c:if test="${!ssEntryHasEntryAcl}"> checked </c:if>
				  <c:if test="${!ss_accessControlConfigureAllowed}"> disabled </c:if>
				/>
				<span><ssf:nlt tag="access.entry.useFolderACL"/></span>
				<br/>
				<input type="radio" name="aclSelection" value="entry" 
				  <c:if test="${ssEntryHasEntryAcl}"> checked </c:if>
				  <c:if test="${!ss_accessControlConfigureAllowed}"> disabled </c:if>
				/>
				<span><ssf:nlt tag="access.entry.specifyEntryACL"/></span>
				<div class="margintop2" style="padding:4px 0px 20px 10px;">
					<input type="submit" class="ss_submit" name="aclSelectionBtn" 
					  value="<ssf:nlt tag="button.apply" />">
				</div>
			   </c:if>
			  </div>
			</div>
			
			<div id="ss_accessControlTable" 
			  <c:if test="${!ssEntryHasEntryAcl && empty ss_accessSortedGroups && empty ss_accessSortedUsers}">style="display:none;"</c:if>
			>
			<c:if test="${ss_accessFunctionsCount <= 0}">
			<span class="ss_bold ss_italic"><ssf:nlt tag="access.noRoles"/></span><br/>
			</c:if>
			<c:if test="${ss_accessFunctionsCount > 0}">
			
			<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
			
			<c:if test="${!ssWorkArea.functionMembershipInherited}">
			
			<div id="ss_addGroupsMenu${renderResponse.namespace}" 
			  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
			  <div align="right">
				<a href="javascript:;" onClick="ss_hideDiv('ss_addGroupsMenu${renderResponse.namespace}');return false;">
				  <img border="0" src="<html:imagesPath/>icons/close_gray16.png" <ssf:alt tag="alt.hideThisMenu"/>/>
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
				  <img border="0" src="<html:imagesPath/>icons/close_gray16.png" <ssf:alt tag="alt.hideThisMenu"/>/>
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
			
			<div id="ss_addApplicationGroupsMenu${renderResponse.namespace}" 
			  style="position:absolute; display:none; border:1px solid black; background-color:#FFFFFF;">
			  <div align="right">
				<a href="javascript:;" onClick="ss_hideDiv('ss_addApplicationGroupsMenu${renderResponse.namespace}');return false;">
				  <img border="0" src="<html:imagesPath/>icons/close_gray16.png" <ssf:alt tag="alt.hideThisMenu"/>/>
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
				  <img border="0" src="<html:imagesPath/>icons/close_gray16.png" <ssf:alt tag="alt.hideThisMenu"/>/>
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
			
			<c:if test="${ss_accessControlConfigureAllowed}">
			<div style="padding:4px;">
			<span>
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
					<c:forEach var="function" items="${ssFunctions}">
					 <c:if test="${function.scope == 'entry'}">
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
					 </c:if>
					</c:forEach>
					</ul>
				</div>
			  
			</c:if>
			
			
			<c:set var="ss_accessControlTableDivId" value="ss_accessControlDiv${renderResponse.namespace}" scope="request"/>
			<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
			<%@ include file="/WEB-INF/jsp/binder/access_control_table.jsp" %>
			
			<br/>
			<div style="padding:8px 0px 14px 10px;">
			<table>
			<tr>
			<td valign="bottom">
			<input type="checkbox" 
			  <c:if test="${ssWorkArea.includeFolderAcl}">
				checked="checked"
			  </c:if>
			  <c:if test="${!ss_accessControlConfigureAllowed}">
				disabled="disabled"
			  </c:if>
			  name="includeFolderAcl"
			  title="<ssf:nlt tag="access.select"/>" /><span style="padding-left:4px;"><ssf:nlt tag="access.includeFolderAcl"/></span>
			</td>
			<td valign="bottom" style="padding-left:20px;">
			<span class="ss_smallprint">[<a href="<ssf:url action="configure_access_control"><ssf:param
					  name="workAreaId" value="${ssWorkArea.parentBinder.id}"/><ssf:param
					  name="workAreaType" value="${ssWorkArea.parentBinder.workAreaType}"/><ssf:param
					  name="operation" value="view_access"/></ssf:url>"
			  onClick="ss_openUrlInPortlet(this.href, true, '600', '700');return false;"
			><ssf:nlt tag="access.viewWhoHasFolderAccess"/></a>]</span>
			</td>
			</tr>
			</table>
			</div>
			<c:if test="${ss_accessControlConfigureAllowed}">
			<input type="submit" class="ss_submit" name="okBtn" 
			 onClick="ss_startSpinner();"
			 value="<ssf:nlt tag="button.saveChanges" />">
			</c:if>
			</c:if>
					<sec:csrfInput />
			</form>

	<div class="ss_italic ss_small margintop3">[<ssf:nlt tag="access.superUser">
	  <ssf:param name="value" useBody="true"><ssf:userTitle user="${ss_superUser}"/></ssf:param>
	  <ssf:param name="value" useBody="true"><ssf:userName user="${ss_superUser}"/></ssf:param>
	  </ssf:nlt>]
	</div>

</div>
</div>


		<form class="ss_form" method="post" style="display:inline;" 
			action="<ssf:url ><ssf:param 
			name="action" value="configure_access_control"/><ssf:param 
			name="actionUrl" value="true"/><ssf:param 
			name="workAreaId" value="${ssWorkArea.workAreaId}"/><ssf:param 
			name="workAreaType" value="${ssWorkArea.workAreaType}"/></ssf:url>">
		  <input type="button" class="ss_submit" name="closeBtn" style="margin-top: 15px;" 
			value="<ssf:nlt tag="button.close" text="Close"/>"
			onClick="ss_cancelButtonCloseWindow();return false;"/>
			<sec:csrfInput />
		</form>
</div>

<c:forEach var="function" items="${ssFunctions}">
<jsp:useBean id="function" type="org.kablink.teaming.security.function.Function" />
<div id="${renderResponse.namespace}ss_operations${function.id}" class="ss_style ss_portlet"
  style="position:absolute; display:none; white-space:nowrap; border:1px solid #000000; 
  margin-bottom:10px; padding:4px; background-color:#ffffff;">
  <div align="right">
    <a href="javascript:;" onClick="ss_hideDiv('${renderResponse.namespace}ss_operations${function.id}');return false;">
      <img border="0" align="absmiddle" src="<html:imagesPath/>icons/close_gray16.png" <ssf:alt tag="alt.hideThisMenu"/>/>
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

</ssf:form>
</div>

</body>
</html>
