<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style">
<div class="ss_help_style">

<div class="ss_help_title">
<span class="ss_titlebold"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></span><br />
<span class="subtitle"><ssf:nlt tag="help.getStartedProduct.subtopic.getStarted"/></span>
</div>

<p><ssf:nlt tag="help.getStartedProduct.getStarted.intro"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.getStarted.team"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.getStartedProduct.getStarted.teamWorkspace"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.header" />
<a href="#skip_nav_all" title="<ssf:nlt tag="helpTitleAlt.skipNavAll" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif"/></a>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale}/pdfs/ICEcore Quick Start Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></a></p>
</div>

<div class="ss_help_more_pages_section"><a id="skip_nav_titles" />
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/folder_types', 'ss_moreinfo_panel');"><<</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');">1</a></div> 
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel');">2</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/folder_types', 'ss_moreinfo_panel');">3</a></div>
<div class="current_page">4</div>
<div class="no_next_page" title="<ssf:nlt tag="helpTitleAlt.noNextPage" />">>></a><a id="skip_nav_panel_numbers" /><a id="skip_nav_all" /></div>
</div>

</div>
