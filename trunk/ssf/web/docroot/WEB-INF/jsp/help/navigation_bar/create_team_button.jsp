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
<%@ include file="/WEB-INF/jsp/help/hide_help_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="help.createTeam.title"/></span>

<p><ssf:nlt tag="help.createTeam.content.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.createTeam.content.listItem.clickTeams"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.createTeam.content.listItem.clickMenu"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.createTeam.content.listItem.fillOutForm"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickOK"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickClose"/></li>

</ol>

<p><ssf:nlt tag="help.createTeam.content.pageContent"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.createTeam.content.restrictAccessIntro"/> <ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/></p>

<div style="margin-left:25px;">
<a href="javascript: ss_helpSystem.showMoreInfoPanel('navigation_bar/create_team_restrict_access', 'ss_moreinfo_panel');"><ssf:nlt tag="help.setTeamAccess.title"/></a>
</div>

</div>






