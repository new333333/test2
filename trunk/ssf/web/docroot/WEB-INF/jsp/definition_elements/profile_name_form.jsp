<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<% //Name form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<c:if test="${empty ss_profile_entry_form || not ss_profile_entry_form}">
<script type="text/javascript">
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheckName", ss_ajax_result_validator);

<% // Are we editing the name of the built-in admin user? %>
<c:if test="${ssUser.id == ssDefinitionEntry.id && ssUser.admin}">
	<% // Yes!  We need to confirm that if the name changes, the %>
	<% // user is OK with restarting the servers.                %>
	ss_addValidator("ss_titleCheckName", ss_validateBuiltInAdminName, null, null, ss_rejectBuiltInAdminName);
	function ss_validateBuiltInAdminName() {
		// Did the name change?
		var origName = '<ssf:escapeJavaScript><c:out value="${ssUser.name}"/></ssf:escapeJavaScript>';
		var newName  = document.getElementById('name').value;
		var reply = (newName == origName);
		if (!reply) {
			// Yes!  Is the user sure they want to restart the servers?
			reply = confirm("<ssf:escapeJavaScript><ssf:nlt tag='validate.adminNameChange.confirmRestart'/></ssf:escapeJavaScript>");
		}
		
		// If we get here, reply is true if the form is valid and false
		// otherwise.  Return it.
		return reply;
	}
	
	<% // Called if the user rejects changing the name of the %>
	<% // built-in admin.                                     %>
	function ss_rejectBuiltInAdminName(vObj, isError) {
		window.setTimeout(function(){ss_cancelPopupDiv('ss_validation_errors_div')}, 1);
	}
</c:if>
</script>
<div class="ss_entryContent">
<div class="needed-because-of-ie-bug"><div id="ss_titleCheckName" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
<div class="ss_labelAbove"><%= caption %></div>
<input type="text" size="40" name="name" id="name" value="<c:out value="${ssDefinitionEntry.name}"/>"
	onchange="ss_ajaxValidate(ss_checkTitleUrl, this, 'name', 'ss_titleCheckName');"
	<c:if test="${(empty ssDefinitionEntry.name) || (ssUser.id == ssDefinitionEntry.id && ssUser.admin)}">
	  <% // It's editable when the built-in admin is editing their %>
	  <% // own profile.                                           %>
	  class="ss_text"
	</c:if>
	<c:if test="${(!empty ssDefinitionEntry.name) && (ssUser.id != ssDefinitionEntry.id || (!ssUser.admin))}">
	  class="ss_text ss_readonly" READONLY="true" 
	</c:if>
>
</div>
</c:if>