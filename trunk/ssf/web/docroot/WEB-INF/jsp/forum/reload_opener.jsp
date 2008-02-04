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
<script type="text/javascript">

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

</script>

