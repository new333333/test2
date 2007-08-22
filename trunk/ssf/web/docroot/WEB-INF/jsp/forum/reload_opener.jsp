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
	ss_random++;
	url = ss_replaceSubStr(url, "ss_entry_id_place_holder", "${ssEntryId}")
	url = ss_replaceSubStr(url, "ss_randomPlaceholder", ss_random)
	self.parent.location.href = url;
	self.parent.focus();
}

setTimeout("self.window.close();", 500);

</script>

