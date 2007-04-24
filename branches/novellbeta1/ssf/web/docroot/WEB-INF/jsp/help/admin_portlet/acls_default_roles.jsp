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

<span class="ss_titlebold"><ssf:nlt tag="help.accessControlOverview.title"/></span> (2/3)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.accessControlOverview.subtopic.defaultRoles"/></span>


<p><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listIntro"/></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listItem.visitors"/></li>

<li><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listItem.participants"/></li>

<li><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listItem.teamMembers"/></li>

<li><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listItem.moderators"/></li>

<li><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listItem.admin"/></li>

<li><ssf:nlt tag="help.accessControlOverview.defaultRoles.content.listItem.siteAdmin"/></li>

</ul>

</div>
<br />

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/acls_role_defs', 'ss_acls_concepts', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_acls_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<span class="ss_gray"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</span>
</div>
</div>
