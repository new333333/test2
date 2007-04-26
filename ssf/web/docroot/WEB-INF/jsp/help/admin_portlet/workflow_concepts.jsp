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

<span class="ss_titlebold"><ssf:nlt tag="help.workflowIntro.title"/></span> (1/4)

<p><ssf:nlt tag="help.workflowIntro.content.businessProcesses"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.tasks"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.status"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.security"/></p>

<p><ssf:nlt tag="help.workflowIntro.content.workflowDef"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.globalStrings.moreinfo.header"/></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_more_defs', 'ss_workflow_concepts', '', '');"><ssf:nlt tag="help.workflowIntro.subtopic.moreDefs"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_example', 'ss_workflow_concepts', '', '');"><ssf:nlt tag="help.globalStrings.exampleSubtopic"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_associating', 'ss_workflow_concepts', '', '');"><ssf:nlt tag="help.workflowIntro.subtopic.associatingWorkflows"/></a></li>

</ul>
</div>
<br />

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<span class="ss_gray">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></span>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_workflow_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_more_defs', 'ss_workflow_concepts', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
