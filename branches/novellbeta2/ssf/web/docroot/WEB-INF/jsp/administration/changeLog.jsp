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
<div class="ss_portlet_style ss_portlet">

<form class="ss_portlet_style ss_form" id="${ssNamespace}_logForm" 
  name="${ssNamespace}_logForm" method="post"  action="<portlet:renderURL/>">
Enter a binderId to list changes to entries in a binder or <br/>
enter an entityId and entityType to list only changes for a specific entry.
<table>
<tr><td>BinderId:</td><td><input type="text" name="binderId"></td>
</tr>
<tr><td>EntityId:</td><td><input type="text" name="entityId"></td>
<td>EntityType:</td><td>
<select name="entityType">
  <option value="">--none--</option>
    <option value="folderEntry">folderEntry</option>
    <option value="user">user</option>
    <option value="group">group</option>
    <option value="folder">folder</option>
    <option value="workspace">workspace</option>
    <option value="profiles">profiles</option>
 </select>
</td></tr>
<tr><td>Filter by operation:</td><td>
<select name="operation">
  <option value="">--none--</option>
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
</td></tr>
</table>
<div class="ss_buttonBarLeft">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>" 
	  onClick="if (${ssNamespace}_getChanges) {${ssNamespace}_getChanges()};return false;">

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
<br/>
<iframe id="${ssNamespace}_display" name="${ssNamespace}_display" style="width:100%; height:150%; 
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
	var myForm = self.document.forms['${ssNamespace}_logForm'];
	if (!myForm.binderId.value && !myForm.entityId.value) {
		alert("Enter binderId or entityId");
		return false;
	}
	url += "\&operation=" + myForm.operation.value;
	if (myForm.binderId.value) {
		url += "\&binderId=" + myForm.binderId.value;
	} else {
		url += "\&entityId=" + myForm.entityId.value;
	}
	url += "\&entityType=" + myForm.entityType.value;
	url += "\&rn=" + rn++;
	frame.src = url;
}
</script>