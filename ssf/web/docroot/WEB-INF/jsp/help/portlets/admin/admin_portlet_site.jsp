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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.adminPortletSite"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span>

<p><ssf:nlt tag="help.adminPortlet.roleDefined" /></p>

<p><ssf:nlt tag="help.adminPortletSite.listIntro" /></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.adminPortletSite.listItem.roleDefs" /></li>

<li><ssf:nlt tag="help.adminPortletSite.listItem.configEmail"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.adminPortletSite.listItem.exportImport" /></li>

<li><ssf:nlt tag="help.adminPortletSite.listItem.templates" /></li>

</ul>

<p><ssf:nlt tag="help.adminPortlet.duties"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.adminPortlet.createGroups"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence" /></p>

<div style="margin-left:25px;">

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access', 'ss_moreinfo_panel');"><ssf:nlt tag="help.accessIntro.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow', 'ss_moreinfo_panel');"><ssf:nlt tag="help.workflowIntro.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>

<p><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Installation and Configuration Guide.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.installGuide"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt> <ssf:nlt tag="help.globalStrings.newWindow"/></a></p>

</div>

</div>
