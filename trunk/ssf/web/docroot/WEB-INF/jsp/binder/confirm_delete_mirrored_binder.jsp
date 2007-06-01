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

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  name="ss_confirm_delete_mirrored_binder" 
  id="ss_confirm_delete_mirrored_binder"
  method="post">
<span class="ss_bold">
<ssf:nlt tag="folder.confirmDeleteFolder"><ssf:param name="value" value="${ssBinder.pathName}"/></ssf:nlt>
</span><br/><br/>

<c:set var="cb_checked" value=""/>
<div style="display:block">
<input type="checkbox" name="ss_deleteSource" <c:out value="${cb_checked}"/> onClick="if (document.ss_confirm_delete_mirrored_binder.ss_deleteSource.checked) document.ss_confirm_delete_mirrored_binder.deleteSource.value='true'; else document.ss_confirm_delete_mirrored_binder.deleteSource.value='false';">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="folder.deleteMirroredFolderContents"/></span></input>
</div>
<input type="hidden" name="deleteSource" value="false"/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onclick="ss_startSpinner();">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>

