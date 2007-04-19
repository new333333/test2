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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.manageFolderMenu"/></span>  (2/4)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.folderMenu.subtopic.addNewFolder"/></span>

<p><ssf:nlt tag="help.addNewFolder.content.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.addNewFolder.listItem.config"/></li>

<li><ssf:nlt tag="help.addNewFolder.listItem.provideInfo"/></li>

</ol>

<p><ssf:nlt tag="help.globalStrings.configurations.moreInfo"/></p>

<div style="margin-left:20px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/config_concepts', 'ss_config_concepts', 'right', 'bottom');"><ssf:nlt tag="help.configurationOverview.title"/></a>
</div>

</div>

<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/manage_folder_menu', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('folder_menu/configure_folder', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
