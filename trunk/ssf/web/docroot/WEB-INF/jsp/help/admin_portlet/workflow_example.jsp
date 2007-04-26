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

<span class="ss_titlebold"><ssf:nlt tag="help.workflowIntro.title"/></span> (3/4)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.exampleSubtopic"/></span>


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

</div>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_more_defs', 'ss_workflow_concepts', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:4px;">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_workflow_concepts'); return false;"><ssf:nlt tag="button.close"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('admin_portlet/workflow_associating', 'ss_workflow_concepts', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
