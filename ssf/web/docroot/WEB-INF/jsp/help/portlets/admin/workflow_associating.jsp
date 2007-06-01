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
<%@ include file="/WEB-INF/jsp/help/hide_moreinfo_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="help.workflowIntro.title"/></span> (4/4)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.workflowIntro.subtopic.associatingWorkflows"/></span>


<p><ssf:nlt tag="help.workflowIntro.associatingWorkflows.intro"/></p>

<p><ssf:nlt tag="help.workflowIntro.associatingWorkflows.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.workflowIntro.associatingWorkflows.listItem.viewFolder"/></li>

<li><ssf:nlt tag="help.workflowIntro.associatingWorkflows.listItem.configureMenuItem"/>

<p><ssf:nlt tag="help.workflowIntro.associatingWorkflows.listItem.configureMenuItem.moreInfo"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>
</li>

<li><ssf:nlt tag="help.workflowIntro.associatingWorkflows.listItem.selectWorkflow"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickApply"/></li>

</ol>

<p><ssf:nlt tag="help.workflowIntro.associatingWorkflows.autoStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('portlets/workflow_example', 'ss_workflow_concepts', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_workflow_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<span class="ss_gray"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</span>
</div>
</div>
