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
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.moreFolderNavigation"/>
</span></div>

<p><ssf:nlt tag="help.moreFolderNavigation.listIntro" /></p>

<ul>

<li><ssf:nlt tag="help.moreFolderNavigation.listItem.entryNumbers"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.moreFolderNavigation.listItem.howManyEntries" /></li>

<li><ssf:nlt tag="help.moreFolderNavigation.listItem.goToPage"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.moreFolderNavigation.listItem.goToPageNumber" /></li>

<li><ssf:nlt tag="help.moreFolderNavigation.listItem.configureColumns" /></li>

</ul>

</div>

</div>
