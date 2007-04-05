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

<span class="ss_titlebold"><ssf:nlt tag="help.accessControlOverview.title"/></span> (2/3)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.accessControlOverview.subtopic.definingRoles"/></span>

<p><ssf:nlt tag="help.accessControlOverview.roleDefs.content.intro"/></p>

<p><ssf:nlt tag="help.accessControlOverview.roleDefs.content.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.accessControlOverview.roleDefs.content.listItem.adminPortlet"/></li>

<li><ssf:nlt tag="help.accessControlOverview.roleDefs.content.listItem.clickConfigLink"/></li>

<li><ssf:nlt tag="help.accessControlOverview.roleDefs.content.listItem.clickRoleName"/></li>

<li><ssf:nlt tag="help.accessControlOverview.roleDefs.content.listItem.alterTasks"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickAdd"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickClose"/></li>

</ol>

<p><ssf:nlt tag="help.accessControlOverview.roleDefs.content.addNewRole"/></p>

</div>
<br />

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/acls_concepts', 'ss_acls_concepts', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_acls_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/acls_default_roles', 'ss_acls_concepts', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
