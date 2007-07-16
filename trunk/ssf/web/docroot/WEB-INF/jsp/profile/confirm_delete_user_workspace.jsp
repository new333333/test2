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
  method="post">
<span class="ss_bold">
<ssf:nlt tag="profile.confirmDeleteUser"><ssf:param name="value" value="${ssEntry.title}"/></ssf:nlt>
</span><br/><br/>

<div style="display:block">
<input type="checkbox" name="deleteWs">&nbsp;<span class="ss_labelRight"><ssf:nlt tag="profile.deleteUserWorkspace"/></span></input>
</div>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onclick="ss_startSpinner();">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>

