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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">
<span class="ss_titlebold"><ssf:nlt tag="help.searchboxes.subtopic.moreSearchInfo"/></span><br />
<span style="font-weight:bold;"><ssf:nlt tag="help.searchboxes.subtopic.restrictingSearches"/></span>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.restrictingSearches.listIntro" /></p>

<ol>
<li><ssf:nlt tag="help.searchboxes.restrictingSearches.listItem.clickAdvanced" /></li>

<li><ssf:nlt tag="help.searchboxes.restrictingSearches.listItem.workspaceTree" /></li>

<li><ssf:nlt tag="help.searchboxes.restrictingSearches.listItem.searchSubs"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.searchboxes.restrictingSearches.listItem.moreCriteria" /></li>

<li><ssf:nlt tag="help.searchboxes.restrictingSearches.listItem.clickSearch" /></li>
</ol>

</div>

<br/>

<div align="center" style="margin-bottom:5px;" title="This Help topic has more than one page of information">
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_advanced', 'ss_moreinfo_panel');"><img border="0" <ssf:alt tag="general.previous"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_results', 'ss_moreinfo_panel');">1</a></div> 
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_advanced', 'ss_moreinfo_panel');">2</a></div>
<div style="display:inline;margin-right:10px;">3</div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_strings', 'ss_moreinfo_panel');">4</a></div>
<div style="display:inline;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_strings', 'ss_moreinfo_panel');"><img border="0" <ssf:alt tag="general.next"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a></div>
</div>

