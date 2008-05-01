<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_portlet">
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

<c:set var="cb_checked" value=""/>
<div style="display:block">
<input type="checkbox" name="ss_deleteSource" <c:out value="${cb_checked}"/> 
  onClick="if (document.ss_confirm_delete_mirrored_binder.ss_deleteSource.checked) document.ss_confirm_delete_mirrored_binder.deleteSource.value='true'; else document.ss_confirm_delete_mirrored_binder.deleteSource.value='false';">
&nbsp;<span class="ss_labelRight">
<c:if test="${ssBinder.entityType == 'folder'}">
<ssf:nlt tag="folder.deleteMirroredFolderContents"/>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
<ssf:nlt tag="workspace.deleteMirroredFolderContents"/>
</c:if>
</span></input>
</div>
<input type="hidden" name="deleteSource" value="false"/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" 
  onclick="ss_startSpinner();">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>

