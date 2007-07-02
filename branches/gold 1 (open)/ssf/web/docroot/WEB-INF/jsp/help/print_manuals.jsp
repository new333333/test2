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
<span class="ss_titlebold"><ssf:nlt tag="help.viewBooks.title"/></span> 
</div>

<p><ssf:nlt tag="help.viewBooks.content.intro"/></p>

<p><span class="header"><ssf:nlt tag="help.viewBooks.content.header.users"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span></p>

<ul>
<li><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Quick Start Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>

<p><ssf:nlt tag="help.viewBooks.content.quickTips.explain"/></p>
</li>

<li><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore User Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.userGetStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a> <ssf:nlt tag="help.globalStrings.englishOnly"/>

<p><ssf:nlt tag="help.viewBooks.content.userGuide.explain"/></p>
</li>

</ul>

<p><span class="header"><ssf:nlt tag="help.viewBooks.content.header.administrators"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span></p>

<ul>
<li><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Installation and Configuration Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.installGuide"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a> <ssf:nlt tag="help.globalStrings.englishOnly"/>

<p><ssf:nlt tag="help.viewBooks.content.installConfigGuide.explain"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>
</li>

</ul>

</div>

</div>