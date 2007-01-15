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
<script type="text/javascript">
function ss_selectPrincipal<portlet:namespace/>(id) {
	var formObj = document.getElementById('${renderResponse.namespace}rolesForm');
	formObj.principalId.value = id
	formObj.btnClicked.value = "addPrincipal";
<c:if test="${ssUser.displayStyle == 'accessible'}" >
	ss_selectPrincipalAccessible<portlet:namespace/>();
</c:if>
<c:if test="${ssUser.displayStyle != 'accessible'}" >
	ss_selectPrincipalAjax<portlet:namespace/>();
</c:if>
}

function ss_selectPrincipalAccessible<portlet:namespace/>() {
	setTimeout("document.forms['${renderResponse.namespace}rolesForm'].submit();", 100)
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
	ajaxRequest.addFormElements("${renderResponse.namespace}rolesForm");
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postSelectPrincipal<portlet:namespace/>);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_postSelectPrincipal<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
}

function ss_selectRole<portlet:namespace/>() {
	var formObj = document.getElementById('${renderResponse.namespace}rolesForm');
	formObj.btnClicked.value = "addPrincipal";
	ss_selectPrincipalAjax<portlet:namespace/>();
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
<c:if test="${ssBinder.entityIdentifier.entityType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
</c:if>
<c:if test="${ssBinder.entityIdentifier.entityType != 'folder'}">
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
</c:if>
<span class="ss_bold">${ssBinder.title}</span>
</td>
<td align="right" valign="top">
<form class="ss_form" method="post" style="display:inline;" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="configure_access_control"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityIdentifier.entityType}"/></portlet:actionURL>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</td>
</tr>
</table>

<c:if test="${!empty ssBinder.parentWorkArea}">
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
  		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/><portlet:param 
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
  		name="binderType" value="${ssBinder.entityIdentifier.entityType}"/></portlet:actionURL>">
<input type="hidden" name="principalId"/>
<input type="hidden" name="btnClicked"/>

<c:if test="${!ssBinder.functionMembershipInherited && !empty ss_accessParent.ssBinder}">
<div>
<img src="<html:imagesPath/>pics/sym_s_checkmark.gif"/> 
<span class="ss_italic">
<c:if test="${ss_accessParent.ssBinder.entityIdentifier.entityType == 'folder'}">
  <ssf:nlt tag="access.designatesFolder"/>
</c:if>
<c:if test="${ss_accessParent.ssBinder.entityIdentifier.entityType != 'folder'}">
  <ssf:nlt tag="access.designatesWorkspace"/>
</c:if>
</span>
<br/>
<br/>
</div>
</c:if>

<c:set var="ss_accessControlTableDivId" value="ss_accessControlDiv${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace" value="${renderResponse.namespace}" scope="request"/>
<%@ include file="/WEB-INF/jsp/binder/access_control_table.jsp" %>

<table cellspacing="4px" cellpadding="4px">
<tr>
<td valign="top" style="padding-left:10px;">
  <span class="ss_bold"><ssf:nlt tag="access.addGroup"/></span>
  <br/>
  <ssf:find formName="${renderResponse.namespace}rolesForm" 
    formElement="addPrincipalText${renderResponse.namespace}" 
    type="group"
    leaveResultsVisible="true"
    clickRoutine="ss_selectPrincipal${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
</td>

<td valign="top" style="padding-left:40px;">
  <span class="ss_bold"><ssf:nlt tag="access.addUser"/></span>
  <br/>
  <ssf:find formName="${renderResponse.namespace}rolesForm" 
    formElement="addPrincipalText${renderResponse.namespace}" 
    type="user"
    leaveResultsVisible="true"
    clickRoutine="ss_selectPrincipal${renderResponse.namespace}"
    width="100px" singleItem="true"/> 
</td>
</tr>
</table>

<br/>
<c:if test="${!ssBinder.functionMembershipInherited}">
<br/>
<input type="submit" class="ss_submit" name="okBtn"
 value="<ssf:nlt tag="button.saveChanges" />">
</c:if>


</form>
</c:if>

</div>
</ssf:box>


<br/>
<br/>

<form class="ss_form" method="post" style="display:inline;" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="configure_access_control"/><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="binderType" value="${ssBinder.entityIdentifier.entityType}"/></portlet:actionURL>">
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>
</div>
</div>
