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
<%@ page import="java.util.ArrayList" %>
<script type="text/javascript">
dojo.require('dojo.widget.*');
</script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_search.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/WorkflowSelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/EntrySelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/FieldSelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/SelectPageable.js"></script>

<script type="text/javascript">


var ss_AjaxBaseUrl = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
var ss_nlt_searchFormLabelWorkflow = "<ssf:nlt tag="searchForm.label.workflow"/>";
var ss_nlt_searchFormLabelEntry = "<ssf:nlt tag="searchForm.label.entry"/>";
var ss_nlt_tagsCommunityTags = "<ssf:nlt tag="tags.communityTags"/>";
var ss_nlt_tagsPersonalTags = "<ssf:nlt tag="tags.personalTags"/>";
var ss_searchFormLabelAuthor = "<ssf:nlt tag="searchForm.label.author"/>";
var ss_searchFormLabelDate = "<ssf:nlt tag="searchForm.label.date"/>";
var ss_AdvancedSearchURLNoOperation = "<portlet:actionURL windowState="maximized" portletMode="view">
				<portlet:param name="action" value="advanced_search"/>
				<portlet:param name="tabId" value="${tabId}"/>
		</portlet:actionURL>";
var ss_AdvancedSearchURL = ss_AdvancedSearchURLNoOperation + "&operation=viewPage";
var ss_currentTabId = "${tabId}";

var ss_overwriteQuestion = "<ssf:nlt tag="search.save.overwriteQuestion"/>";
var ss_noNameMsg="<ssf:nlt tag="search.save.noName"/>";
</script>
