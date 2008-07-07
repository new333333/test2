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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style">
<div class="ss_help_style">

<div class="ss_help_title">
<span class="ss_titlebold"><ssf:nlt tag="help.understandingTeams.topic"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></span><br />
<span class="subtitle"><ssf:nlt tag="help.createTeam.subTopic.grantAccess"/></span>
</div>

<p><ssf:nlt tag="help.setTeamAccess.content.intro"/></p>

<p><ssf:nlt tag="help.setTeamAccess.content.listIntro"/></p>

<ol>
<li><ssf:nlt tag="help.setTeamAccess.content.listItem.viewPage"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.aclMenu"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.noInherit"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.addUser"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.enableUser"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.repeat"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.saveChanges"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickClose"/></li>

</ol>

</div>

<div class="ss_help_more_pages_section">
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/team_intro', 'ss_moreinfo_panel');"><<</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/team_intro', 'ss_moreinfo_panel');">1</a></div> 
<div class="current_page">2</div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/site_admin_notes', 'ss_moreinfo_panel');">3</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/site_admin_notes', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /></div>
</div>

</div>


