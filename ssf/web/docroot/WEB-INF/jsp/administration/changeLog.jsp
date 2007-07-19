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
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<script type="text/javascript">
function ss_saveChangeLogBinderId(id) {
	var formObj = document.getElementById('ss_changeLogForm')
	formObj['binderId'].value = id;

	var url = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="get_change_log_entry_form" />
		<ssf:param name="binderId" value="ssBinderIdPlaceHolder" />
		<ssf:param name="random" value="ssRandomPlaceHolder" />
		</ssf:url>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssRandomPlaceHolder", ss_random++);
	ifObj = document.getElementById('ss_changeLogIframe');
	ifObj.src = url;
}

function ss_saveChangeLogEntryId(id) {
	var formObj = document.getElementById('ss_changeLogForm')
	formObj['entityId'].value = id;
}

</script>

<div class="ss_portlet_style ss_portlet">

<form class="ss_portlet_style ss_form" 
  id="ss_changeLogForm" 
  name="ss_changeLogForm" method="post" 
  action="<portlet:renderURL/>">

<span class="ss_largerprint ss_bold"><ssf:nlt tag="administration.view_change_log"/></span>
<br/>
<br/>
<table style="background-color: #eeeeee;" border="1">
<tr>
  <td valign="top" width="250">
  <span><ssf:nlt tag="changeLog.findFolder"/></span><br/><br/>
	 <ssf:find formName="ss_changeLogForm" 
	    formElement="binderId2" 
	    type="places"
	    width="140px" 
	    binderId="${binderId}"
	    searchSubFolders="false"
	    foldersOnly="true"
	    singleItem="true"
	    clickRoutine="ss_saveChangeLogBinderId"
	    /> 
  </td>
  <td valign="top" width="250">
    <span><ssf:nlt tag="changeLog.findEntry"/></span>
    <div id="ss_changeLogEntryForm">
      <iframe id="ss_changeLogIframe" frameborder="0" style="height:230px; width:200px;"
      src="<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="get_change_log_entry_form" />
		</ssf:url>">xxx</iframe>
    </div>
  </td>
</tr>
</table>
<table>
<tr>
  <td valign="top" width="250" align="center" nowrap>BinderId: <input type="text" name="binderId" size="8"/></td>
  <td valign="top" width="250" align="center" nowrap>EntityId: <input type="text" name="entityId" size="8"><br/>
  EntityType: 
<select name="entityType">
  <option value="">----</option>
    <option value="folderEntry">folderEntry</option>
    <option value="user">user</option>
    <option value="group">group</option>
    <option value="folder">folder</option>
    <option value="workspace">workspace</option>
    <option value="profiles">profiles</option>
 </select>
</td></tr>
</table>
<br/>
<table>
<tr>
  <td><ssf:nlt tag="changeLogs.filterByOperation"/>
	<select name="operation">
	  <option value=""><ssf:nlt tag="changeLog.showAllChanges"/></option>
	    <option value="addEntry">addEntry</option>
	    <option value="modifyEntry">modifyEntry</option>
	    <option value="deleteEntry">deleteEntry</option>
	    <option value="startWorkflow">startWorkflow</option>
	    <option value="modifyWorkflowState">modifyWorkflowState</option>
	    <option value="addWorkflowResponse">addWorkflowResponse</option>
	    <option value="moveEntry">moveEntry</option>
	    <option value="addFile">addFile</option>
	    <option value="modifyFile">modifyFile</option>
	    <option value="deleteFile">deleteFile</option>
	    <option value="deleteVersion">deleteVersion</option>
	    <option value="renameFile">renameFile</option>
	    <option value="addBinder">addBinder</option>
	    <option value="modifyBinder">modifyBinder</option>
	    <option value="deleteBinder">deleteBinder</option>
	    <option value="moveBinder">moveBinder</option>
	    <option value="modifyAccess">modifyAccess</option>
	    <option value="deleteAccess">deleteAccess</option>
	 </select>
  </td>
</tr>
</table>
<br/>
<div class="ss_buttonBarLeft">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>" 
	  onClick="if (${ssNamespace}_getChanges) {${ssNamespace}_getChanges()};return false;">

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
<br/>
<iframe id="${ssNamespace}_display" name="${ssNamespace}_display" style="width:98%; height:350px; 
    display:block; position:relative; left:5px;"
    src="<html:rootPath/>js/forum/null.html" 
    >xxx</iframe>
</div>
<script type="text/javascript">
var rn = Math.round(Math.random()*999999);

function ${ssNamespace}_getChanges() {
	var url = "<ssf:url adapter="true" 
			portletName="ss_administration" 
			action="view_change_log" 
			actionUrl="false" />";

	var frame = self.document.getElementById('${ssNamespace}_display');
	var myForm = self.document.forms['ss_changeLogForm'];
	if (!myForm.binderId.value && !myForm.entityId.value) {
		return false;
	}
	url += "\&operation=" + myForm.operation.value;
	if (myForm.binderId.value) {
		url += "\&binderId=" + myForm.binderId.value;
	}
	if (myForm.entityId.value) {
		url += "\&entityId=" + myForm.entityId.value;
	}
	url += "\&entityType=" + myForm.entityType.value;
	url += "\&rn=" + rn++;
	frame.src = url;
}
</script>