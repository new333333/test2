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
<div class="ss_style">
<div class="ss_help_style">

<div class="ss_help_title">
<span class="ss_titlebold"><ssf:nlt tag="help.workflowIntro.title"/></span>
</div>

<p><ssf:nlt tag="help.workflowIntro.content.businessProcesses"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.tasks"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.status"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.workflowDef"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.header" />
<a href="#skip_nav_all" title="<ssf:nlt tag="helpTitleAlt.skipNavAll" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif"/></a>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showHelpPanel('portlets/admin/workflow_more_defs', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.workflowIntro.subtopic.moreDefs"/></a></p>
<p><a href="javascript: ss_helpSystem.showHelpPanel('portlets/admin/workflow_example', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.workflowIntro.subtopic.example"/></a></p>
<p><a href="javascript: ss_helpSystem.showHelpPanel('portlets/admin/workflow_associating', 'ss_moreinfo_panel', '', '');"><ssf:nlt tag="help.workflowIntro.subtopic.associatingWorkflows"/></a></p>
</div>

<div class="ss_help_more_pages_section"><a id="skip_nav_titles" />
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="no_prev_page" title="<ssf:nlt tag="helpTitleAlt.noPrevPage" />"><<</div>
<div class="current_page">1</div> 
<div class="no_prev_page"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_more_defs', 'ss_moreinfo_panel');">2</a></div>
<div class="no_prev_page"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_example', 'ss_moreinfo_panel');">3</a></div>
<div class="no_prev_page"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_associating', 'ss_moreinfo_panel');">4</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_more_defs', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /><a id="skip_nav_all" /></div>
</div>

</div>

