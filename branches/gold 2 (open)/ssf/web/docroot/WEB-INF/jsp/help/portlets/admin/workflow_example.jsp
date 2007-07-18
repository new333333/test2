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
<span class="ss_titlebold"><ssf:nlt tag="help.workflowIntro.title"/></span><br />
<span style="font-weight:bold;"><ssf:nlt tag="help.workflowIntro.subtopic.example"/></span>
</div>

<p><ssf:nlt tag="help.workflowIntro.example.content.listIntro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<ol>

<li><ssf:nlt tag="help.workflowIntro.example.listItem.submit"/>

<p><ssf:nlt tag="help.workflowIntro.example.listItem.submit.moreInfo"/></p>

<p><ssf:nlt tag="help.workflowIntro.example.listItem.submit.moreInfo2"/></p>
</li>

<li><ssf:nlt tag="help.workflowIntro.example.listItem.managerQuestion"/></li>

<li><ssf:nlt tag="help.workflowIntro.example.listItem.denied"/></li>

<li><ssf:nlt tag="help.workflowIntro.example.listItem.approved"/></li>

<li><ssf:nlt tag="help.workflowIntro.example.listItem.recorded"/></li>

</ol>

<div class="footnote_text">
<sup><span class="ss_largerprint">*</span></sup> <ssf:nlt tag="help.globalStrings.addIn"/>
</div>

</div>

<div class="ss_help_more_pages_section">
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_more_defs', 'ss_moreinfo_panel');"><<</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow', 'ss_moreinfo_panel');">1</a></div> 
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_more_defs', 'ss_moreinfo_panel');">2</a></div>
<div class="current_page">3</div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_associating', 'ss_moreinfo_panel');">4</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow_associating', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /></div>
</div>

</div>
