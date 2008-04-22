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
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.adminPortlet"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></span>
</div>

<p><ssf:nlt tag="help.adminPortlet.roleDefined" /></p>

<p><ssf:nlt tag="help.adminPortlet.duties"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.adminPortlet.createGroups"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.title"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/access', 'ss_moreinfo_panel');"><ssf:nlt tag="help.accessIntro.title"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/workflow', 'ss_moreinfo_panel');"><ssf:nlt tag="help.workflowIntro.title"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></a></p>
<p><a target="ss_new" href="<html:rootPath/>help/ref/pdfs/ICEcore Installation and Configuration Guide.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.installGuide"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt> <ssf:nlt tag="help.globalStrings.newWindow"/></a></p>
</div>

</div>
