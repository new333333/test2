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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.manageFolderMenu"/></span>  (3/7)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.folderMenu.subtopic.editWorkflows"/></span>

<p><ssf:nlt tag="help.editWorkflows.content.intro"/></p>

<p><ssf:nlt tag="help.editWorkflows.content.workflowParts"/></p>

<p><ssf:nlt tag="help.globalStrings.workflow.onlineConcepts"/></p>

<div style="margin-left:20px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_concepts', 'ss_workflow_concepts', 'right', 'bottom');"><ssf:nlt tag="help.workflowIntro.title"/></a>
</div>

<p><ssf:nlt tag="help.globalStrings.workflow.manual"/></p>

<div style="margin-left:20px;">
<a target="ss_new" href="<html:rootPath/>docs/aspen_workflow.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.workflowTutorial"/></a> <ssf:nlt tag="help.globalStrings.newWindow"/>
</div>

</div>

<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/add_folder', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/edit_forms', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
