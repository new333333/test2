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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.manageWorkspaceMenu"/></span>

<p><ssf:nlt tag="help.manageMenuBar.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.manageMenuBar.menuDescription" /></p>

<p><ssf:nlt tag="help.manageMenuBar.aclsTeams" /></p>

<p><ssf:nlt tag="help.manageMenuBar.adminMoreInfo" /></p>

<p><span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.moreinfo.header" /></span></p>

<div style="margin-left:25px;">

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access', 'ss_moreinfo_panel');"><ssf:nlt tag="help.accessIntro.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow', 'ss_moreinfo_panel');"><ssf:nlt tag="help.workflowIntro.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

</div>

</div>
