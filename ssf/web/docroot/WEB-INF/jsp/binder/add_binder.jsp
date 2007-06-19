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
<%
	String workspaceId = com.sitescape.team.ObjectKeys.DEFAULT_WORKSPACE_CONFIG;
%>
<c:set var="ss_workspaceId" value="<%= workspaceId %>"/>

<script type="text/javascript">
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);

function ss_enableDisableFolderOptions(id) {
	var formObj = self.document.getElementById('<portlet:namespace/>fm');
	if (id == '${ss_workspaceId}') {
	    document.getElementById('folderConfigIdTitle').className = "ss_bold"
	    <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
	      formObj['folderConfigId_${config.id}'].checked = false;
	      formObj['folderConfigId_${config.id}'].disabled = false;
	      document.getElementById('folderConfigIdTitle_${config.id}').className = ""
	    </c:forEach>
	} else {
	    document.getElementById('folderConfigIdTitle').className = "ss_light"
	    <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
	      formObj['folderConfigId_${config.id}'].checked = false;
	      formObj['folderConfigId_${config.id}'].disabled = true;
	      document.getElementById('folderConfigIdTitle_${config.id}').className = "ss_light"
	    </c:forEach>
	}
}
 </script>

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  name="<portlet:namespace/>fm" 
  id="<portlet:namespace/>fm" 
  method="post" onSubmit="return ss_onSubmit(this);">
<span class="ss_bold">
  <c:if test="${ssOperation == 'add_workspace'}">
<ssf:nlt tag="binder.add.workspace.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>
<c:if test="${ssOperation == 'add_folder'}">
<ssf:nlt tag="binder.add.folder.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>
<c:if test="${ssOperation == 'add_team_workspace'}">
<ssf:nlt tag="binder.add.team.title"><ssf:param name="value" value="${ssBinder.pathName}"/>
</ssf:nlt>
</c:if>

</span></br></br>
  
<table class="ss_style"  border="0" cellspacing="0" cellpadding="0" width="95%">
<tr><td>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="general.title" /></legend>
	<span class="ss_labelLeft" id="title_label"><label for="title">
	  <c:if test="${ssOperation == 'add_workspace'}"><ssf:nlt tag="workspace.title"/></c:if>
	  <c:if test="${ssOperation == 'add_team_workspace'}"><ssf:nlt tag="workspace.title"/></c:if>
	  <c:if test="${ssOperation == 'add_folder'}"><ssf:nlt tag="folder.title"/></c:if>
	</label></span>
    <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
	<input type="text" class="ss_text" size="70" name="title" id="title" 
	  onchange="ss_ajaxValidate(ss_checkTitleUrl, this,'title_label', 'ss_titleCheck');"><br/><br/>

<c:if test="${!empty ssBinderConfigs}">
  <span class="ss_bold"><ssf:nlt tag="binder.add.binder.select.config"/></span> <ssf:inlineHelp tag="ihelp.other.select_template"/>
  <br/>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <input type="radio" name="binderConfigId" value="${config.id}" 
      <c:if test="${config.internalId == ss_workspaceId}">checked="checked"</c:if>
      onChange="ss_enableDisableFolderOptions('${config.internalId}')"
      ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/><br/>
  </c:forEach>
<br/>  
</c:if>
</fieldset>
</td></tr>

<c:if test="${ssOperation == 'add_team_workspace'}">
 <tr><td>
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="team.members" /></legend>
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
	<c:if test="${!empty ssBinder}">
		<ssf:teamMembers binderId="${ssBinder.id}" formElement="users"/>
	</c:if>
</td></tr>
</table>
<input type="hidden" name="inheritBtnNo" value="inheritBtnNo"/>
</fieldset>
</td></tr>
</c:if>

<c:if test="${ssOperation == 'add_workspace' || ssOperation == 'add_team_workspace'}">
<c:if test="${!empty ssFolderConfigs}">
 <tr><td>
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="workspace.folders" /></legend>
  <span id="folderConfigIdTitle" class="ss_bold"><ssf:nlt tag="binder.add.binder.select.folders"/></span> 
  <br/>
  <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
      <input type="checkbox" name="folderConfigId_${config.id}" /> 
      <span id="folderConfigIdTitle_${config.id}" class="ss_normalprint"
      ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/></span><br/>
  </c:forEach>
<br/>  
</fieldset>
</td></tr>
</c:if>
</c:if>

<c:if test="${!empty ssUser.emailAddress}">
<c:if test="${ssOperation == 'add_team_workspace' || !empty ssBinder.teamMemberIds}">
 <tr><td>
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="workspace.announce" /></legend>
    <input type="checkbox" name="announce"/>
	<span class="ss_labelLeft"><label for="announce">
	  <ssf:nlt tag="workspace.announceToTeam"/>
	</label></span>
	<br/>
	<br/>
	<span class="ss_labelAbove"><label for="announcementText">
	  <ssf:nlt tag="workspace.announcementText"/>
	</label></span>
	<ssf:htmleditor name="announcementText" height="200" />
</fieldset>
</td></tr>
</c:if>
</c:if>

</table>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">

</form>
</div>

