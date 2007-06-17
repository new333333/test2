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
<div class="ss_style">
<div class="ss_help_style">

<div class="ss_help_title">
<span class="ss_titlebold"><ssf:nlt tag="help.searchboxes.subtopic.moreSearchInfo"/></span><br />
<span style="font-weight:bold;"><ssf:nlt tag="help.searchboxes.subtopic.searchResults"/></span>
</div>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.searchResults.intro" /></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.searchResults.saved" /></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.searchResults.rankingsTags" /></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.searchResults.criteria"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>


</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.header" />
<a href="#skip_nav_all" title="<ssf:nlt tag="helpTitleAlt.skipNavAll" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif"/></a>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_advanced', 'ss_moreinfo_panel');"><ssf:nlt tag="help.searchboxes.subtopic.advancedSearch"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_restrict', 'ss_moreinfo_panel');"><ssf:nlt tag="help.searchboxes.subtopic.restrictingSearches"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_strings', 'ss_moreinfo_panel');"><ssf:nlt tag="help.searchboxes.subtopic.searchStrings"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>
</div>

<div class="ss_help_more_pages_section"><a id="skip_nav_titles" />
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="no_prev_page" title="<ssf:nlt tag="helpTitleAlt.noPrevPage" />"><<</div>
<div class="current_page">1</div> 
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_advanced', 'ss_moreinfo_panel');">2</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_restrict', 'ss_moreinfo_panel');">3</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_strings', 'ss_moreinfo_panel');">4</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_advanced', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /><a id="skip_nav_all" /></div>
</div>

</div>

