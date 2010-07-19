<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.view_change_log") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

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
				self.window.close();
			}

			return false;
	<% 	}
		else { %>
			self.window.close();
			return true;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.view_change_log">
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<script type="text/javascript">
function ss_saveChangeLogBinderId(id) {
	var formObj = document.getElementById('ss_changeLogForm')
	formObj['binderId'].value = id;
	var urlParams = {operation:"get_change_log_entry_form", binderId:id, random:ss_random++};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
	ifObj = document.getElementById('ss_changeLogIframe');
	ifObj.src = url;
}

function ss_saveChangeLogEntryId(id) {
	var formObj = document.getElementById('ss_changeLogForm')
	formObj['entityId'].value = id;
}

</script>

<div class="ss_style ss_portlet">

<form class="ss_portlet_style ss_form" 
  id="ss_changeLogForm" 
  name="ss_changeLogForm" method="post" 
  action="<ssf:url action="view_change_log" actionUrl="true"/>">

<br/>
<br/>
<table style="background-color: #eeeeee;" border="1">
<tr>
  <td valign="top" width="350">
  <span><ssf:nlt tag="changeLog.findFolder"/></span><br/><br/>
	 <ssf:find formName="ss_changeLogForm" 
	    formElement="binderId2" 
	    type="places"
	    width="300px" 
	    binderId="${binderId}"
	    searchSubFolders="false"
	    foldersOnly="false"
	    singleItem="true"
	    clickRoutine="ss_saveChangeLogBinderId"
	    /> 
  </td>
  <td valign="top" width="350">
    <span><ssf:nlt tag="changeLog.findEntry"/></span>
    <div id="ss_changeLogEntryForm">
      <iframe id="ss_changeLogIframe" title="<ssf:nlt tag="changeLog.findEntry"/>" frameborder="0" 
        style="height:230px; width:330px; padding-top:9px;"
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
  <td valign="top" width="250" align="center" nowrap><label for="binderId">BinderId: </label><input type="text" name="binderId" id="binderId" size="8"/></td>
  <td valign="top" width="250" align="center" nowrap><label for="entityId">EntityId: </label><input type="text" name="entityId" id="entityId" size="8"><br/>
  <label for="entityType">EntityType: </label>
<select name="entityType" id="entityType">
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
  <td><label for="operation"><ssf:nlt tag="changeLogs.filterByOperation"/></label>
	<select name="operation" id="operation">
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

<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
	onClick="return handleCloseBtn();"/>
</div>
</form>
<br/>
<iframe id="${ssNamespace}_display" name="${ssNamespace}_display" style="width:98%; height:350px; 
    display:block; position:relative; left:5px;" title="<ssf:nlt tag="administration.view_change_log"/>"
    src="<html:rootPath/>js/forum/null.html" 
    >xxx</iframe>
</div>
<script type="text/javascript">
var rn = Math.round(Math.random()*999999);

function ${ssNamespace}_getChanges() {

	var frame = self.document.getElementById('${ssNamespace}_display');
	var myForm = self.document.forms['ss_changeLogForm'];
	if (!myForm.binderId.value && !myForm.entityId.value) {
		return false;
	}
	var urlParams={operation:myForm.operation.value, rn:rn++};
	if (myForm.binderId.value) {
		urlParams['binderId'] = myForm.binderId.value;
	}
	if (myForm.entityId.value) {
		urlParams['entityId'] = myForm.entityId.value;
	}
	urlParams['entityType'] = myForm.entityType.value;
	frame.src = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "view_change_log"); 

}
</script>

</ssf:form>
</div>
</div>
</body>
</html>