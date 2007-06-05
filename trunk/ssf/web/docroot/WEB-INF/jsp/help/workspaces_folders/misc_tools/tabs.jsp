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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.tabs"/></span>

<p><ssf:nlt tag="help.tabs.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.tabs.using"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.tabs.types.listIntro" /></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.tabs.types.listItem.workspaces" /></li>

<li><ssf:nlt tag="help.tabs.types.listItem.folder" /></li>

<li><ssf:nlt tag="help.tabs.types.listItem.quickSearch" /></li>

<li><ssf:nlt tag="help.tabs.types.listItem.advancedSearch" /></li>

</ul>

<p><ssf:nlt tag="help.tabs.entries"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>
