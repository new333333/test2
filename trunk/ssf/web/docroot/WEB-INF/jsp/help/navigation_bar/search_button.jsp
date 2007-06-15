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
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.searchButton"/></span>
</div>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<div class="picture">
<img border="0" <ssf:alt tag="alt.exitHelp"/> src="<html:imagesPath/>pics/search_icon.gif" />
</div>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.content.results"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.content.typeFewLetters"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchboxes.content.peoplePlacesTags"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/search_results', 'ss_moreinfo_panel');"><ssf:nlt tag="help.searchboxes.subtopic.moreSearchInfo"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a><a id="skip_nav_titles"/></p>
</div>

</div>
