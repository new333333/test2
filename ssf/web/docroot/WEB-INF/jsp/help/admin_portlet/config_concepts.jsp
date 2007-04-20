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

<span class="ss_titlebold"><ssf:nlt tag="help.configurationOverview.title"/></span> (1/2)

<p><ssf:nlt tag="help.configurationOverview.content.commonConfigTasks.listIntro"/></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.configurationOverview.content.listItem.ldap"/></li>

<li><ssf:nlt tag="help.configurationOverview.content.listItem.roleDefs"/></li>

<li><ssf:nlt tag="help.configurationOverview.content.listItem.notifications"/></li>

<li><ssf:nlt tag="help.configurationOverview.content.listItem.posting"/></li>

</ul>

<p><ssf:nlt tag="help.configurationOverview.content.configAdminPortlet"/></p>

<p><ssf:nlt tag="help.configurationOverview.content.designingExampleIntro"/></p>

<div style="margin-left:20px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/config_concepts_example', 'ss_config_concepts', '', '');"><ssf:nlt tag="help.configurationOverview.subtopic.designer"/></a>
</div>
</div>
<br />

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<span class="ss_gray">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></span>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_config_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/config_concepts_example', 'ss_config_concepts', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
