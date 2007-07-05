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
<span class="ss_titlebold"><ssf:nlt tag="help.accessIntro.title"/></span><br />
<span style="font-weight:bold;"><ssf:nlt tag="help.accessIntro.subTopic.usersGroups"/></span>
</div>

<p><ssf:nlt tag="help.accessIntro.usersGroups.listIntro.users" /></p>

<ul>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.owner"/></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.teamMembers"/></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.allUsers"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.superAdmin"/></li>

</ul>

<p><ssf:nlt tag="help.accessIntro.usersGroups.listIntro.roles" /></p>

<ul>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.visitor"/></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.participant"/></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.teamMember"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.wsFldrAdmin"/></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.siteAdmin"/></li>

<li><ssf:nlt tag="help.accessIntro.usersGroups.listItem.workspaceCreator"/></li>

</ul>

</div>

<div class="ss_help_more_pages_section">
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access', 'ss_moreinfo_panel');"><<</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access', 'ss_moreinfo_panel');">1</a></div> 
<div class="current_page">2</div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_delegating', 'ss_moreinfo_panel');">3</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_new_role', 'ss_moreinfo_panel');">4</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_delegating', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /></div>
</div>

</div>
