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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String workspaceId = com.sitescape.team.ObjectKeys.DEFAULT_WORKSPACE_CONFIG;
%>
<c:set var="ss_workspaceId" value="<%= workspaceId %>"/>

<script type="text/javascript">
var ss_teamWorkspaceInternalId = '<%= ObjectKeys.DEFAULT_TEAM_WORKSPACE_CONFIG %>';
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);

function ss_treeShowIdAddBinder<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="add_binder"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="operation" value="add_workspace"/></portlet:renderURL>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}

var ss_binderConfigSubfolderCount = new Object();
var ss_addBinderConfigInternalIds = new Object();
<c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
ss_binderConfigSubfolderCount['${config.id}'] = ${config.binderCount};
ss_addBinderConfigInternalIds['${config.id}'] = "${config.internalId}";
</c:forEach>

function ss_showAddBinderOptions() {
	var formObj = self.document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm');
	if (document.getElementById('folderConfigIdTitle') == null) return;
	
	//If there are sub-binders to be added, turn off the folder selection list
	for (var i = 0; i < formObj['binderConfigId'].length; i++) {
		var configId = formObj['binderConfigId'][i].value
		if (formObj['binderConfigId'][i].checked) {
			//Turn on or off the ability to add folders
			if (ss_binderConfigSubfolderCount[configId] > 0) {
			    document.getElementById('folderConfigIdTitle').className = "ss_light"
			    <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
		          <c:if test="${!empty config.id && !empty config.templateTitle}">
			        formObj['folderConfigId_${config.id}'].checked = false;
			        formObj['folderConfigId_${config.id}'].disabled = true;
			        document.getElementById('folderConfigIdTitle_${config.id}').className = "ss_light"
			      </c:if>
			    </c:forEach>
			    document.getElementById('ss_addBinderAddFoldersDiv').style.display = "none"
			} else {
			    document.getElementById('folderConfigIdTitle').className = "ss_bold"
			    <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
		          <c:if test="${!empty config.id && !empty config.templateTitle}">
			        formObj['folderConfigId_${config.id}'].checked = false;
			        formObj['folderConfigId_${config.id}'].disabled = false;
			        document.getElementById('folderConfigIdTitle_${config.id}').className = ""
			      </c:if>
			    </c:forEach>
			    document.getElementById('ss_addBinderAddFoldersDiv').style.display = "block"
			}
			//Turn on/off the add team members box
			if (ss_addBinderConfigInternalIds[configId] == ss_teamWorkspaceInternalId) {
				document.getElementById('ss_addBinderAddTeamMemebersDiv').style.display = "block";
				formObj['inheritBtnNo'].value = 'inheritBtnNo'
			} else {
				document.getElementById('ss_addBinderAddTeamMemebersDiv').style.display = "none";
				formObj['inheritBtnNo'].value = 'inheritBtnYes'
			}
			//Turn on/off announce div
			var ss_teamMembersListEmpty = "${empty ssBinder.teamMemberIds}";
			if (ss_addBinderConfigInternalIds[configId] == ss_teamWorkspaceInternalId || ss_teamMembersListEmpty == 'false') {
				document.getElementById('ss_addBinderAnnounceDiv').style.display = "block";
			} else {
				document.getElementById('ss_addBinderAnnounceDiv').style.display = "none";
			}
		}
	}
}

</script>

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  action="<portlet:actionURL windowState="maximized"><portlet:param 
  		name="action" value="add_binder"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/><portlet:param 
  		name="operation" value="${ssOperation}"/></portlet:actionURL>"
  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" 
  id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" 
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
  <legend class="ss_legend"><ssf:nlt tag="workspace.location" /></legend>
    <span class="ss_bold"><ssf:nlt tag="workspace.selectLocation"/>:</span>
    <br/>
<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdAddBinder${renderResponse.namespace}" 
  scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
</fieldset>
<br/>
</td></tr>

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
<c:set var="checkedConfig" value=""/>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <c:if test="${status.first}"><c:set var="checkedConfig" value="${config.id}"/></c:if>
      <c:if test="${config.internalId == ss_workspaceId}"><c:set var="checkedConfig" value="${config.id}"/></c:if>
  </c:forEach>
  <span class="ss_bold"><ssf:nlt tag="binder.add.binder.select.config"/></span> <ssf:inlineHelp tag="ihelp.other.select_template"/>
  <br/>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <input type="radio" name="binderConfigId" value="${config.id}" 
      <c:if test="${checkedConfig == config.id}">checked="checked"</c:if>
      onClick="ss_showAddBinderOptions()"
      ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/><br/>
  </c:forEach>
<br/>  
</c:if>
<c:if test="${empty ssBinderConfigs}">
<input type="hidden" name="binderConfigId" value="${binderConfigId}">
</c:if>
</fieldset>
</td></tr>

<tr><td>
<div id="ss_addBinderAddTeamMemebersDiv" style="display:none;">
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
</div>
</td></tr>

<c:if test="${!empty ssFolderConfigs}">
<tr><td>
<div id="ss_addBinderAddFoldersDiv" style="display:none;">
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="workspace.folders" /></legend>
  <span id="folderConfigIdTitle" class="ss_bold"><ssf:nlt tag="binder.add.binder.select.folders"/></span> 
  <br/>
  <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
    <c:if test="${!empty config.id && !empty config.templateTitle}">
      <input type="checkbox" name="folderConfigId_${config.id}" /> 
      <span id="folderConfigIdTitle_${config.id}" class="ss_normalprint"
      ><ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/></span><br/>
    </c:if>
  </c:forEach>
<br/>  
</fieldset>
</div>
</td></tr>
</c:if>

<c:if test="${!empty ssUser.emailAddress}">
 <tr><td>
<div id="ss_addBinderAnnounceDiv" style="display:none;">
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="workspace.announce" /></legend>
    <input type="checkbox" name="announce"/>
	<span class="ss_labelLeft"><label for="announce">
	
	<c:if test="${ssOperation == 'add_workspace'}">
	  <ssf:nlt tag="workspace.announceToTeam"/>
	</c:if>
	<c:if test="${ssOperation == 'add_folder'}">
	  <ssf:nlt tag="folder.announceToTeam"/>
	</c:if>
	  
	</label></span>
	<br/>
	<br/>
	<span class="ss_labelAbove"><label for="announcementText">
	  <ssf:nlt tag="workspace.announcementText"/>
	</label></span>
	<ssf:htmleditor name="announcementText" height="200" />
</fieldset>
</div>
</td></tr>
</c:if>

</table>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_buttonSelect('okBtn');">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">

</form>
</div>

<script type="text/javascript">
ss_createOnLoadObj("ss_showAddBinderOptions", ss_showAddBinderOptions);
</script>
