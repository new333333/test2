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

<span class="ss_titlebold"><ssf:nlt tag="help.viewBooks.title"/></span> 

<p><ssf:nlt tag="help.viewBooks.content.intro"/></p>

<ul style="list-style-type:disc;">
<!--<li><a target="ss_new" href="<html:rootPath/>docs/aspen_install.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.installGuide"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>
</li>-->

<li><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcorps Quick Tips.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>
</li>

<li><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcorps QuickStart User Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.userGetStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>
</li>

<!--<li><a target="ss_new" href="<html:rootPath/>docs/aspen_manager_guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.adminGetStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>
</li>-->

<!--<li><a target="ss_new" href="<html:rootPath/>docs/aspen_workflow.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.workflowTutorial"/></a>
</li>-->
</ul>

<div align="center">
<a class="ss_linkButton ss_smallprint" href="#" 
  onClick="ss_hideDiv('ss_help_print_manuals'); return false;"><ssf:nlt tag="button.close"/></a>
</div>
</div>