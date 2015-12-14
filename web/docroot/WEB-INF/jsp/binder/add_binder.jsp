<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="tag" value="toolbar.menu.addWorkspace"/>
<c:if test="${ssOperation == 'add_folder' || ssOperation == 'add_subFolder'}">
  <c:set var="tag" value="toolbar.menu.addFolder"/>
</c:if>
<c:if test="${ssOperation == 'add_team_workspace'}">
  <c:set var="tag" value="team.addTeam"/>
</c:if>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<body class="ss_style_body tundra">
<c:if test="${!empty ssBinderTemplateName}">
  <%@ include file="/WEB-INF/jsp/binder/add_binder_short_form.jsp" %>
</c:if>
<c:if test="${empty ssBinderTemplateName}">
<%
	String workspaceId = org.kablink.teaming.ObjectKeys.DEFAULT_WORKSPACE_CONFIG;
%>
<c:set var="ss_workspaceId" value="<%= workspaceId %>"/>
<c:set var="ss_workspaceConfigId" value="<%= ObjectKeys.DEFAULT_WORKSPACE_CONFIG %>"/>
<c:set var="ss_teamWorkspaceConfigId" value="<%= ObjectKeys.DEFAULT_TEAM_WORKSPACE_CONFIG %>"/>

<c:set var="ss_stdConfigId_desc" value="<%= ObjectKeys.DEFAULT_FOLDER_CONFIG %>"/>
<c:set var="ss_stdConfigId_blog" value="<%= ObjectKeys.DEFAULT_FOLDER_BLOG_CONFIG %>"/>
<c:set var="ss_stdConfigId_miniblog" value="<%= ObjectKeys.DEFAULT_FOLDER_MINIBLOG_CONFIG %>"/>
<c:set var="ss_stdConfigId_wiki" value="<%= ObjectKeys.DEFAULT_FOLDER_WIKI_CONFIG %>"/>
<c:set var="ss_stdConfigId_cal" value="<%= ObjectKeys.DEFAULT_FOLDER_CALENDAR_CONFIG %>"/>
<c:set var="ss_stdConfigId_guest" value="<%= ObjectKeys.DEFAULT_FOLDER_GUESTBOOK_CONFIG %>"/>
<c:set var="ss_stdConfigId_photo" value="<%= ObjectKeys.DEFAULT_FOLDER_PHOTO_CONFIG %>"/>
<c:set var="ss_stdConfigId_file" value="<%= ObjectKeys.DEFAULT_FOLDER_LIBRARY_CONFIG %>"/>
<c:set var="ss_stdConfigId_mirrored_file" value="<%= ObjectKeys.DEFAULT_FOLDER_MIRRORED_FILE_CONFIG %>"/>
<c:set var="ss_stdConfigId_task" value="<%= ObjectKeys.DEFAULT_FOLDER_TASK_CONFIG %>"/>
<c:set var="ss_stdConfigId_mile" value="<%= ObjectKeys.DEFAULT_FOLDER_MILESTONE_CONFIG %>"/>
<c:set var="ss_stdConfigId_survey" value="<%= ObjectKeys.DEFAULT_FOLDER_SURVEY_CONFIG %>"/>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
var ss_addBinderOperation = "${ssOperation}";
var ss_teamWorkspaceInternalId = '<%= ObjectKeys.DEFAULT_TEAM_WORKSPACE_CONFIG %>';
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);

function ss_treeShowIdAddBinder${renderResponse.namespace}(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<ssf:url action="add_binder" actionUrl="true"><ssf:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
		name="operation" value="add_workspace"/></ssf:url>";
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
<c:if test="${empty ssBinderConfigs}">
ss_binderConfigSubfolderCount['${binderConfigId}'] = 0;
ss_addBinderConfigInternalIds['${binderConfigId}'] = ss_teamWorkspaceInternalId;
</c:if>

function ss_showAddBinderOptions() {
	var formObj = self.document.getElementById('${renderResponse.namespace}fm');
	if (document.getElementById('folderConfigIdTitle') == null) return;
	
	//Default to inheriting from parent
	formObj['inheritFromParent'].value = 'yes';
	
	var configId = "";
	if (typeof(formObj['binderConfigId'].length) != "undefined") {
		for (var i = 0; i < formObj['binderConfigId'].length; i++) {
			if (formObj['binderConfigId'][i].checked) {
				configId = formObj['binderConfigId'][i].value
			}
		}
	} else if (typeof(formObj['binderConfigId'].value) != "undefined") {
		configId = formObj['binderConfigId'].value;
	}
	//If there are sub-binders to be added, turn off the folder selection list
	if (configId != "") {
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
		    document.getElementById('ss_addBinderAddFoldersDiv').focus()
		}
		//Turn on/off the add team members box
		if (ss_addBinderOperation == 'add_team_workspace' ||
				ss_addBinderConfigInternalIds[configId] == ss_teamWorkspaceInternalId) {
			document.getElementById('ss_addBinderAddTeamMemebersDiv').style.display = "block";
			document.getElementById('ss_addBinderAddTeamMemebersDiv').focus();
			formObj['inheritFromParent'].value = 'no'
		} else {
			document.getElementById('ss_addBinderAddTeamMemebersDiv').style.display = "none";
			formObj['inheritFromParent'].value = 'yes'
		}
		//Turn on/off announce div
		var ss_teamMembersListEmpty = "${empty ssBinderTeamMemberIds}";
		var announceDivObj = document.getElementById('ss_addBinderAnnounceDiv');
		if (announceDivObj != null) {
			if (ss_addBinderOperation == 'add_team_workspace' || 
					ss_addBinderConfigInternalIds[configId] == ss_teamWorkspaceInternalId || 
					ss_teamMembersListEmpty == 'false') {
				announceDivObj.style.display = "block";
				try {
					announceDivObj.focus();
				} catch(e) {};
			} else {
				announceDivObj.style.display = "none";
			}
		}
	}
}

function ss_checkForm(obj) {
	if (ss_buttonSelected == "") return false;
	return ss_onSubmit(obj);
}

</script>

<div class="ss_portlet">

<ssf:form titleTag="${tag}">

<form class="ss_style ss_form" 
  action="<ssf:url action="add_binder" actionUrl="true"><ssf:param 
  		name="binderId" value="${ssBinder.id}"/><ssf:param 
  		name="operation" value="${ssOperation}"/></ssf:url>"
  name="${renderResponse.namespace}fm" 
  id="${renderResponse.namespace}fm" 
  method="post" onSubmit="return ss_checkForm(this);">
  
<div style="text-align:right; padding:10px;">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_buttonSelect('okBtn');">
	<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">
</div>
  
<table class="ss_style"  border="0" cellspacing="0" cellpadding="0" width="95%">
<c:if test="${ssOperation != 'add_folder' && ssOperation != 'add_subFolder'}">
<tr><td>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="workspace.location" /></legend>
<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdAddBinder${renderResponse.namespace}" 
  scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
</fieldset>
<br/>
</td></tr>
</c:if>

<tr><td>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="general.title" /></legend>
	<span class="ss_labelLeft" id="title_label"><label for="title">
	  <c:if test="${ssOperation == 'add_workspace'}"><ssf:nlt tag="workspace.title"/></c:if>
	  <c:if test="${ssOperation == 'add_team_workspace'}"><ssf:nlt tag="workspace.title"/></c:if>
	  <c:if test="${ssOperation == 'add_folder' || ssOperation == 'add_subFolder'}"><ssf:nlt tag="folder.title"/></c:if>
	</label></span>
    <div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
	<input type="text" class="ss_text" size="70" name="title" id="title" maxlength="255"
	  onchange="ss_ajaxValidate(ss_checkTitleUrl, this,'title_label', 'ss_titleCheck');"><br/><br/>

<c:if test="${!empty ssBinderConfigs}">
<c:set var="checkedConfig" value=""/>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
      <c:if test="${status.first}"><c:set var="checkedConfig" value="${config.id}"/></c:if>
      <c:if test="${ssOperation != 'add_folder' && ssOperation != 'add_subFolder' && config.internalId == ss_workspaceId}">
        <c:set var="checkedConfig" value="${config.id}"/>
      </c:if>
      <c:if test="${(ssOperation == 'add_folder' || ssOperation == 'add_subFolder') && 
      		ssBinder.entityType == 'folder' && ssBinder.entryDefId == config.entryDefId}">
        <c:set var="checkedConfig" value="${config.id}"/>
      </c:if>
  </c:forEach>
<c:if test="${ssOperation != 'add_folder' && ssOperation != 'add_subFolder'}">
  <span class="ss_bold"><ssf:nlt tag="general.type.workspace"/></span>
</c:if>
<c:if test="${ssOperation == 'add_folder' || ssOperation == 'add_subFolder'}">
  <span class="ss_bold"><ssf:nlt tag="general.type.folder"/></span>
</c:if>
  <br/>
  <table>
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
    <c:if test="${config.internalId == ss_workspaceConfigId}">
	  <tr><td valign="top"><input type="radio" name="binderConfigId" value="${config.id}" 
	  <c:if test="${checkedConfig == config.id}">checked="checked"</c:if>
	  onClick="ss_showAddBinderOptions()"
	  > <ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/>&nbsp;&nbsp;&nbsp;</td>
	  <td valign="top" style="padding-bottom:6px;"><span class="ss_smallprint">
	    <ssf:nlt tag="${config.templateDescription}" checkIfTag="true"/>
	  </span></td>
	  </tr>
	</c:if>
  </c:forEach>
  
  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
    <c:if test="${config.internalId == ss_teamWorkspaceConfigId}">
	  <tr><td valign="top"><input type="radio" name="binderConfigId" value="${config.id}" 
	  <c:if test="${checkedConfig == config.id}">checked="checked"</c:if>
	  onClick="ss_showAddBinderOptions()"
	  > <ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/>&nbsp;&nbsp;&nbsp;</td>
	  <td valign="top" style="padding-bottom:6px;"><span class="ss_smallprint">
	    <ssf:nlt tag="${config.templateDescription}" checkIfTag="true"/>
	  </span></td>
	  </tr>
	</c:if>
  </c:forEach>

  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
    <c:if test="${!empty config.templateTitle && !empty config.internalId && !empty config.id}">
    <c:if test="${config.internalId != ss_workspaceConfigId && config.internalId != ss_teamWorkspaceConfigId}">
      <tr><td valign="top" nowrap><input type="radio" name="binderConfigId" value="${config.id}" 
      <c:if test="${checkedConfig == config.id}">checked="checked"</c:if>
      onClick="ss_showAddBinderOptions()"
      > <ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/>&nbsp;&nbsp;&nbsp;</td>
      <td valign="top" style="padding-bottom:6px;"><span class="ss_smallprint">
        <ssf:nlt tag="${config.templateDescription}" checkIfTag="true"/>
      </span></td>
      </tr>
    </c:if>
    </c:if>
  </c:forEach>

  <c:forEach var="config" items="${ssBinderConfigs}" varStatus="status">
    <c:if test="${!empty config.templateTitle && empty config.internalId && !empty config.id}">
      <tr><td valign="top" nowrap><input type="radio" name="binderConfigId" value="${config.id}" 
      <c:if test="${checkedConfig == config.id}">checked="checked"</c:if>
      onClick="ss_showAddBinderOptions()"
      > <ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/>&nbsp;&nbsp;&nbsp;</td>
      <td valign="top" style="padding-bottom:6px;"><span class="ss_smallprint">
        <ssf:nlt tag="${config.templateDescription}" checkIfTag="true"/>
      </span></td>
      </tr>
    </c:if>
  </c:forEach>
</table>
<br/>  
</c:if>
<c:if test="${empty ssBinderConfigs}">
<input type="hidden" name="binderConfigId" value="${binderConfigId}"/>
<ssf:nlt tag="__template_team_workspace" checkIfTag="true"/>
<br/>
<span class="ss_smallprint">
  <ssf:nlt tag="__template_team_workspace_description" checkIfTag="true"/>
</span>
<br/>
</c:if>
</fieldset>
</td></tr>

<tr><td>
<div id="ss_addBinderAddTeamMemebersDiv" style="display:none;">
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="team.members" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
	<tr style="margin-bottom: 10px;">
		<td valign="top" colspan="2">
			<input type="checkbox" name="allowExternalUsers" id="allowExternalUsersId" value="true" style="margin-bottom: 30px;"/>
			<span class="ss_normalprint"><ssf:nlt tag="allowExternalUsers"/></span>
		</td>
	</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
    type="user" userList="${ssUsers}" binderId="${ssBinder.id}" width="200px"/>
</td>
</tr>
<tr>
<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
<td valign="top">
  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
    type="group" userList="${ssGroups}" width="200px"/>
</td>
</tr>
<tr><td colspan="2">
	<ssf:clipboard type="user" formElement="users" />
	<c:if test="${!empty ssBinder}">
		<ssf:teamMembers binderId="${ssBinder.id}" formElement="users"/>
	</c:if>
</td></tr>
</table>
<input type="hidden" name="inheritFromParent"/>
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
  <div class="ss_indent_medium">
  <span class="ss_light"><ssf:nlt tag="administration.configure_cfg.standardTemplates"/></span>
  <br/>
  <div class="ss_indent_medium">
  <table>
  <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
   <c:if test="${config.internalId == ss_stdConfigId_desc || 
                 config.internalId == ss_stdConfigId_blog ||  
                 config.internalId == ss_stdConfigId_miniblog ||  
                 config.internalId == ss_stdConfigId_wiki ||  
                 config.internalId == ss_stdConfigId_cal ||  
                 config.internalId == ss_stdConfigId_guest ||  
                 config.internalId == ss_stdConfigId_photo ||  
                 config.internalId == ss_stdConfigId_file ||  
                 config.internalId == ss_stdConfigId_mirrored_file ||  
                 config.internalId == ss_stdConfigId_task ||  
                 config.internalId == ss_stdConfigId_mile ||  
                 config.internalId == ss_stdConfigId_survey}">
    <c:if test="${!empty config.id && !empty config.templateTitle}">
      <tr><td valign="top" nowrap><input type="checkbox" name="folderConfigId_${config.id}" /> 
      <span id="folderConfigIdTitle_${config.id}" class="ss_normalprint"
      > <ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/></span>&nbsp;&nbsp;&nbsp;</td>
      <td valign="top" style="padding-bottom:6px;"><span class="ss_smallprint"><ssf:nlt tag="${config.templateDescription}" checkIfTag="true"/></span></td>
      </tr>
    </c:if>
   </c:if>
  </c:forEach>
  </table>
  </div>
  </div>
  <br/>
  <div class="ss_indent_medium">
  <span class="ss_light"><ssf:nlt tag="administration.configure_cfg.customTemplates"/></span>
  <br/>
  <div class="ss_indent_medium">
  <table>
  <c:forEach var="config" items="${ssFolderConfigs}" varStatus="status">
   <c:if test="${config.internalId != ss_stdConfigId_desc && 
                 config.internalId != ss_stdConfigId_blog &&  
                 config.internalId != ss_stdConfigId_miniblog &&  
                 config.internalId != ss_stdConfigId_wiki &&  
                 config.internalId != ss_stdConfigId_cal &&  
                 config.internalId != ss_stdConfigId_guest &&  
                 config.internalId != ss_stdConfigId_photo &&  
                 config.internalId != ss_stdConfigId_file &&  
                 config.internalId != ss_stdConfigId_mirrored_file &&  
                 config.internalId != ss_stdConfigId_task &&  
                 config.internalId != ss_stdConfigId_mile &&  
                 config.internalId != ss_stdConfigId_survey}">
    <c:if test="${!empty config.id && !empty config.templateTitle}">
      <tr><td valign="top" nowrap><input type="checkbox" name="folderConfigId_${config.id}" /> 
      <span id="folderConfigIdTitle_${config.id}" class="ss_normalprint"
      > <ssf:nlt tag="${config.templateTitle}" checkIfTag="true"/></span>&nbsp;&nbsp;&nbsp;</td>
      <td valign="top" style="padding-bottom:6px;"><span class="ss_smallprint"><ssf:nlt tag="${config.templateDescription}" checkIfTag="true"/></span></td>
      </tr>
    </c:if>
   </c:if>
  </c:forEach>
  </table>
  </div>
  </div>
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
	
	<c:if test="${ssOperation == 'add_workspace' || ssOperation == 'add_team_workspace'}">
	  <ssf:nlt tag="workspace.announceToTeam"/>
	</c:if>
	<c:if test="${ssOperation != 'add_workspace' && ssOperation != 'add_team_workspace'}">
	  <ssf:nlt tag="folder.announceToTeam"/>
	</c:if>
	  
	</label></span>
	<br/>
	<br/>
	<span class="ss_labelAbove"><label for="announcementText">
	  <ssf:nlt tag="workspace.announcementText"/>
	</label></span>
	<div>
	  <ssf:htmleditor name="announcementText" height="150" toolbar="minimal" />
	</div>
</fieldset>
</div>
</td></tr>
</c:if>

</table>
<div style="text-align: right; margin: 20px;">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_buttonSelect('okBtn');">
	<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="ss_buttonSelect('cancelBtn');">
</div>
</form>
</ssf:form>
</div>

<script type="text/javascript">
ss_createOnLoadObj("ss_showAddBinderOptions", ss_showAddBinderOptions);
</script>
</c:if>

</body>
</html>
