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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
function ss_doReload() {
	// need to refresh list? (used on calendar view and task list view)
	if (!window.ssScope) { ssScope = {}; };
	if (self.ssScope && self.ssScope.refreshView) {
		ssScope.refreshViewRoutine = self.ssScope.refreshView;
	} else if (self.opener && self.opener.ssScope && self.opener.ssScope.refreshView) {
		ssScope.refreshViewRoutine = self.opener.ssScope.refreshView;
	} else if (self.opener && self.opener.opener && self.opener.opener.ssScope && self.opener.opener.ssScope.refreshView){
		ssScope.refreshViewRoutine = self.opener.opener.ssScope.refreshView;
	} else if (self.parent && self.parent.ssScope && self.parent.ssScope.refreshView) {
		ssScope.refreshViewRoutine = self.parent.ssScope.refreshView;
	} else if (self.parent && self.parent.parent && self.parent.parent.ssScope && self.parent.parent.ssScope.refreshView){
		ssScope.refreshViewRoutine = self.parent.parent.ssScope.refreshView;
	} else if (self.opener && self.opener.parent && self.opener.parent.ssScope && self.opener.parent.ssScope.refreshView){
		ssScope.refreshViewRoutine = self.opener.parent.ssScope.refreshView;
	} 
	
	if (ssScope.refreshViewRoutine) {
		ssScope.refreshViewRoutine("${ssEntryId}");
	}
	if (self.opener && self.opener.ss_reloadUrl) {
		var url = self.opener.ss_reloadUrl;
		if (typeof self.opener.ss_reloadUrl${ssBinderId} != "undefined") 
			url = self.opener.ss_reloadUrl${ssBinderId};
		ss_random++;
		url = ss_replaceSubStr(url, "ss_entry_id_place_holder", "${ssEntryId}")
		url = ss_replaceSubStr(url, "ss_randomPlaceholder", ss_random)
		self.opener.location.href = url;
		self.opener.focus();
	} else if (self.opener) {
		self.opener.location.reload(true);
		self.opener.focus();
	} else if (self.parent && self.parent.ss_reloadUrl) {
		var url = self.parent.ss_reloadUrl;
		if (typeof self.parent.ss_reloadUrl${ssBinderId} != "undefined") 
			url = self.parent.ss_reloadUrl${ssBinderId};
		ss_random++;
		url = ss_replaceSubStr(url, "ss_entry_id_place_holder", "${ssEntryId}")
		url = ss_replaceSubStr(url, "ss_randomPlaceholder", ss_random)
		self.parent.location.href = url;
		self.parent.focus();
	}
	
	setTimeout("self.window.close();", 500);
}
</script>

<c:if test="${!empty ss_errorMessage}">
<ssf:form titleTag="general.error.anErrorOccurred">
<span class="ss_largestprint"><pre>${ss_errorMessage}</pre></span>
<br/>
<br/>
<input type="button" onClick="ss_doReload();return false;" value="<ssf:nlt tag="button.close"/>">
</ssf:form>
</c:if>
<c:if test="${empty ss_errorMessage}">
<script type="text/javascript">
	ss_doReload();
</script>
</c:if>
