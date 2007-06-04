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
<%@ include file="/WEB-INF/jsp/help/hide_moreinfo_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="help.accessIntro.title"/></span>

<p><ssf:nlt tag="help.accessIntro.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.accessIntro.content.roles"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.accessIntro.content.inheritance" /></p>

<p><ssf:nlt tag="help.accessIntro.content.multiplePlaces"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.moreinfo.header"/></span></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('portlets/admin/access_users_groups', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.accessIntro.subTopic.usersGroups"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('portlets/admin/access_delegating', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.accessIntro.subTopic.delegating"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('portlets/admin/access_new_role', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.accessIntro.subTopic.defRoles"/></a></li>

</ul>
</div>

<br/>

<div align="center" style="margin-bottom:5px;" title="This Help topic has more than one page of information">
<div style="display:inline;margin-right:10px;"><img border="0" <ssf:alt tag="general.previous"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></div>
<div style="display:inline;margin-right:10px;">1</div> 
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_users_groups', 'ss_moreinfo_panel');">2</a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_delegating', 'ss_moreinfo_panel');">3</a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_new_role', 'ss_moreinfo_panel');">4</a></div>
<div style="display:inline;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access_users_groups', 'ss_moreinfo_panel');"><img border="0" <ssf:alt tag="general.next"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a></div>
</div>
