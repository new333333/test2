<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
<li><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale}/pdfs/ICEcore Quick Start Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a>

<p><ssf:nlt tag="help.viewBooks.content.quickTips.explain"/></p>
</li>

<li><a target="ss_new" href="<html:rootPath/>help/ref/pdfs/ICEcore User Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.userGetStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a> <ssf:nlt tag="help.globalStrings.englishOnly"/>

<p><ssf:nlt tag="help.viewBooks.content.userGuide.explain"/></p>
</li>

</ul>

<p><span class="header"><ssf:nlt tag="help.viewBooks.content.header.administrators"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span></p>

<ul>
<li><a target="ss_new" href="<html:rootPath/>help/ref/pdfs/ICEcore Administration Guide.pdf">
  <span class="document_title"><ssf:nlt tag="help.viewBooks.content.listItem.administrationGuide"
    text="${ssProductName} Administration Guide"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span></a> 
  <ssf:nlt tag="help.globalStrings.englishOnly"/>
</li>

<li><a target="ss_new" href="<html:rootPath/>help/ref/pdfs/ICEcore Installation and Configuration Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.installGuide"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a> <ssf:nlt tag="help.globalStrings.englishOnly"/>

<p><ssf:nlt tag="help.viewBooks.content.installConfigGuide.explain"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>
</li>

</ul>

</div>

</div>