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
dojo.require('sitescape.widget.WorkflowSelect');
dojo.require('sitescape.widget.EntrySelect');
dojo.require('sitescape.widget.FieldSelect');
dojo.require('sitescape.widget.SelectPageable');
dojo.require('sitescape.widget.DropdownDatePickerActivateByInput');
</script>
<script type="text/javascript" src="<html:rootPath/>js/common/ss_search.js"></script>

<script type="text/javascript">

var ss_nlt_searchFormLabelWorkflow = "<ssf:nlt tag="searchForm.label.workflow"/>";
var ss_nlt_searchFormLabelEntry = "<ssf:nlt tag="searchForm.label.entry"/>";
var ss_nlt_tagsCommunityTags = "<ssf:nlt tag="tags.communityTags"/>";
var ss_nlt_tagsPersonalTags = "<ssf:nlt tag="tags.personalTags"/>";
var ss_searchFormLabelAuthor = "<ssf:nlt tag="searchForm.label.author"/>";
var ss_searchFormLabelDate = "<ssf:nlt tag="searchForm.label.date"/>";
var ss_searchFormLabelLastActivity = "<ssf:nlt tag="searchForm.label.lastActivity"/>";
var ss_AdvancedSearchURLNoOperation = "<portlet:actionURL windowState="maximized" 
		portletMode="view"><portlet:param 
		name="action" value="advanced_search"/><portlet:param 
		name="tabId" value="${tabId}"/></portlet:actionURL>";
var ss_AdvancedSearchURL = ss_AdvancedSearchURLNoOperation + "&operation=viewPage";
var ss_currentTabId = "${tabId}";

var ss_overwriteQuestion = "<ssf:nlt tag="search.save.overwriteQuestion"/>";
var ss_noNameMsg="<ssf:nlt tag="search.save.noName"/>";
var ss_days_0 = "<ssf:nlt tag="searchForm.lastActivity.0"/>"
var ss_days_1 = "<ssf:nlt tag="searchForm.lastActivity.1"/>"
var ss_days_3 = "<ssf:nlt tag="searchForm.lastActivity.3"/>"
var ss_days_7 = "<ssf:nlt tag="searchForm.lastActivity.7"/>"
var ss_days_30 = "<ssf:nlt tag="searchForm.lastActivity.30"/>"
var ss_days_90 = "<ssf:nlt tag="searchForm.lastActivity.90"/>"
var ss_searchFormMoreOptionsShowLabel = "<ssf:nlt tag="searchForm.advanced.moreOptions"/>";
var ss_searchFormMoreOptionsHideLabel = "<ssf:nlt tag="searchForm.advanced.moreOptions.hide"/>";

var ss_searchResultSavedSearchInputLegend = "<ssf:nlt tag="searchResult.savedSearch.input.legend"/>";
</script>
