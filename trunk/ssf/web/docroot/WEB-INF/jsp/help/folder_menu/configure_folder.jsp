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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.manageFolderMenu"/></span>  (3/4)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.menuItem.configure"/></span>

<p><ssf:nlt tag="help.configureFolder.content.listIntro"/></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.configureFolder.content.listItem.folderViews"/></li>

<li><ssf:nlt tag="help.configureFolder.content.listItem.defaultView"/></li>

<li><ssf:nlt tag="help.configureFolder.content.listItem.defaultEntry"/></li>

<li><ssf:nlt tag="help.configureFolder.content.listItem.workflowAssociations"/></li>

</ul>

<p><ssf:nlt tag="help.configureFolder.content.moreInfo.designs"/></p>

<p><ssf:nlt tag="help.configureFolder.content.moreInfo.formInstructions"/></p>

<p><ssf:nlt tag="help.globalStrings.moreinfo.header"/></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/config_concepts', 'ss_config_concepts', 'right', 'bottom');"><ssf:nlt tag="help.configurationOverview.title"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_concepts', 'ss_workflow_concepts', 'right', 'bottom');"><ssf:nlt tag="help.workflowIntro.title"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_associating', 'ss_workflow_concepts', 'right', 'bottom');"><ssf:nlt tag="help.workflowIntro.subtopic.associatingWorkflows"/></a></li>

</ul>
</div>

<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/add_folder', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/move_folder', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
