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
<span class="ss_titlebold"><ssf:nlt tag="help.configIntro.title"/></span>
</div>

<p><ssf:nlt tag="help.configIntro.content.listIntro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<ul>

<li><ssf:nlt tag="help.configIntro.content.listItem.initial"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.prepopulation" /></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.placeEnhancement"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.useEnhancement" /></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.user" /></li>

</ul>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.header" />
<a href="#skip_nav_all" title="<ssf:nlt tag="helpTitleAlt.skipNavAll" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif"/></a>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_defs', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.definitions" /></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_designers', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.designers" /></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_apply_defs', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.applyDefs" /></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_dedicate_apps', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.dedicatedApps" /></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_templates', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.templates" /></a></p>
</div>

<div class="ss_help_more_pages_section"><a id="skip_nav_titles" />
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="no_prev_page" title="<ssf:nlt tag="helpTitleAlt.noPrevPage" />"><<</div>
<div class="current_page">1</div> 
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_defs', 'ss_moreinfo_panel');">2</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_designers', 'ss_moreinfo_panel');">3</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_apply_defs', 'ss_moreinfo_panel');">4</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_dedicate_apps', 'ss_moreinfo_panel');">5</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_templates', 'ss_moreinfo_panel');">6</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_defs', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /><a id="skip_nav_all" /></div>
</div>

</div>
