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

<span class="ss_titlebold"><ssf:nlt tag="help.createTeam.title"/></span><br/>
<span style="font-weight:bold;"><ssf:nlt tag="help.setTeamAccess.title"/></span>

<p><ssf:nlt tag="help.setTeamAccess.content.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.viewPage"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.aclMenu"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.noInherit"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.addGroup"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.enableGroup"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.removeAllUsers"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.saveChanges"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickClose"/></li>

</ol>

</div>

<br/>

<div align="center" style="margin-bottom:5px;" title="This Help topic has more than one page of information">
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showHelpPanel('navigation_bar/create_team_button', 'ss_help_panel', '', '');"><img border="0" <ssf:alt tag="general.previous"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showHelpPanel('navigation_bar/create_team_button', 'ss_help_panel', '', '');">1</a></div> 
<div style="display:inline;margin-right:10px;">2</div>
<div style="display:inline;"><img border="0" <ssf:alt tag="general.next"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></div>
</div>


