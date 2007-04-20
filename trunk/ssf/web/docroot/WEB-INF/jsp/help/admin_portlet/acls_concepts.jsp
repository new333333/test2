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

<span class="ss_titlebold"><ssf:nlt tag="help.accessControlOverview.title"/></span> (1/3)

<p><ssf:nlt tag="help.accessControlOverview.content.intro"/></p>

<p><ssf:nlt tag="help.accessControlOverview.content.membership"/></p>

<p><ssf:nlt tag="help.accessControlOverview.content.benefits"/></p>

<p><ssf:nlt tag="help.globalStrings.moreinfo.header"/></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/acls_role_defs', 'ss_acls_concepts', '', '');"><ssf:nlt tag="help.accessControlOverview.subtopic.definingRoles"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/acls_default_roles', 'ss_acls_concepts', '', '');"><ssf:nlt tag="help.accessControlOverview.subtopic.defaultRoles"/></a></li>

</ul>

</div>
<br />

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<span class="ss_gray">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></span>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_acls_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_more_defs', 'ss_acls_concepts', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
