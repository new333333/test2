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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.manageFolderMenu"/></span>  (1/7)

<p><ssf:nlt tag="help.folderMenu.content.intro"/></p>

<p><ssf:nlt tag="help.folderMenu.content.listIntro"/></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/add_folder', 'ss_help_panel', '', '');"><ssf:nlt tag="help.folderMenu.subtopic.addFolder"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/edit_workflows', 'ss_help_panel', '', '');"><ssf:nlt tag="help.folderMenu.subtopic.editWorkflows"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/edit_forms', 'ss_help_panel', '', '');"><ssf:nlt tag="help.folderMenu.subtopic.editForms"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/move_folder', 'ss_help_panel', '', '');"><ssf:nlt tag="help.folderMenu.subtopic.moveThisFolder"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/configure_folder', 'ss_help_panel', '', '');"><ssf:nlt tag="help.folderMenu.subtopic.configureThisFolder"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/folder_acls', 'ss_help_panel', '', '');"><ssf:nlt tag="help.folderMenu.subtopic.accessControl"/></a></li>

</ul>

<p><ssf:nlt tag="help.folderMenu.content.adminGetStartBookIntro"/></p>

<div style="margin-left:20px;">
<a target="ss_new" href="<html:rootPath/>docs/aspen_manager_guide.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.adminGetStart"/></a>
</div>

</div>

<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showPreviousHelpSpot();">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/add_folder', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
