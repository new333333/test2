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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("toolbar.delete") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<div class="ss_portlet">
<c:set var="tag" value="toolbar.menu.delete_folder"/>
<c:if test="${ssBinder.entityType != 'folder'}">
  <c:set var="tag" value="toolbar.menu.delete_workspace"/>
</c:if>
<ssf:form titleTag="${tag}">
<br/>

<form class="ss_style ss_form" 
  action="<ssf:url windowState="maximized" actionUrl="true"><ssf:param 
	name="action" value="modify_binder"/><ssf:param 
	name="operation" value="delete"/><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
  name="ss_confirm_delete_mirrored_binder" 
  id="ss_confirm_delete_mirrored_binder"
  method="post">
<span class="ss_bold">
<c:if test="${ssBinder.entityType == 'folder'}">
<ssf:nlt tag="folder.confirmDeleteFolder"><ssf:param 
  name="value" value="${ssBinder.pathName}"/></ssf:nlt>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
<ssf:nlt tag="workspace.confirmDeleteWorkspace"><ssf:param 
  name="value" value="${ssBinder.pathName}"/></ssf:nlt>
</c:if>
</span><br/><br/>

<%
	// Since we simply move non-mirrored binders to the trash, there's
	// no reason to prompt if the folder being deleted is not mirrored.
	// In the case, the user will be prompted if the item gets purged
	// from the trash.
	//
	// If the binder is mirrored, it will be purged immediately so we
	// do need to prompt the user in that case.
%>
<c:if test="${ssBinder.mirrored}">
</c:if>
<c:if test="${!ssBinder.mirrored}">
	<c:set var="cb_checked" value=""/>
	<div style="display:block">
		<input type="checkbox" name="ss_purgeImmediately" id="ss_purgeImmediately" <c:out value="${cb_checked}"/> 
		  onClick="if (document.ss_confirm_delete_mirrored_binder.ss_purgeImmediately.checked) {document.ss_confirm_delete_mirrored_binder.purgeImmediately.value='true'; document.getElementById('ss_deleteSourceDIV').style.visibility='';} else {document.ss_confirm_delete_mirrored_binder.purgeImmediately.value='false'; document.getElementById('ss_deleteSourceDIV').style.visibility='hidden';}"/>
		  &nbsp;<label for="ss_purgeImmediately"><span class="ss_labelRight">
		<c:if test="${ssBinder.entityType == 'folder'}">
			<ssf:nlt tag="trash.confirm.Purge.immediately.folder"/>
		</c:if>
		<c:if test="${ssBinder.entityType != 'folder'}">
			<ssf:nlt tag="trash.confirm.Purge.immediately.workspace"/>
		</c:if>
		</span></label>
	</div>
</c:if>


<c:if test="${!ssBinder.mirrored}">
	<div id="ss_deleteSourceDIV" style="visibility:hidden">
</c:if>
		<c:set var="cb_checked" value=""/>
		<div style="display:block">
			<input type="checkbox" name="ss_deleteSource" id="ss_deleteSource" <c:out value="${cb_checked}"/> 
			  onClick="if (document.ss_confirm_delete_mirrored_binder.ss_deleteSource.checked) document.ss_confirm_delete_mirrored_binder.deleteSource.value='true'; else document.ss_confirm_delete_mirrored_binder.deleteSource.value='false';"/>
			  &nbsp;<label for="ss_deleteSource"><span class="ss_labelRight">
			<c:if test="${ssBinder.entityType == 'folder'}">
				<ssf:nlt tag="folder.deleteMirroredFolderContents"/>
			</c:if>
			<c:if test="${ssBinder.entityType != 'folder'}">
				<ssf:nlt tag="workspace.deleteMirroredFolderContents"/>
			</c:if>
			</span></label>
		</div>
<c:if test="${!ssBinder.mirrored}">
	</div>
</c:if>

<input type="hidden" name="purgeImmediately" value="false"/>
<input type="hidden" name="deleteSource"     value="false"/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" 
  onclick="if (!ss_confirmIfCBChecked('ss_purgeImmediately', '<ssf:escapeJavaScript><c:if test="${ssBinder.entityType == 'folder'}"><ssf:nlt tag="trash.confirm.Purge.immediately.folder.verify"/></c:if><c:if test="${ssBinder.entityType != 'folder'}"><ssf:nlt tag="trash.confirm.Purge.immediately.workspace.verify"/></c:if></ssf:escapeJavaScript>')) {return false;} else {ss_startSpinner(); return true;}">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</ssf:form>
</div>

</body>
</html>
