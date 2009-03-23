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
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.createTeam"/></span>
</div>

<p><ssf:nlt tag="help.createTeam.content.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.createTeam.content.listItem.clickTeams" />

<p><ssf:nlt tag="help.createTeam.content.listItem.clickTeams.otherWorkspace"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p></li>

<li><ssf:nlt tag="help.createTeam.content.listItem.clickMenu" /></li>

<li><ssf:nlt tag="help.createTeam.content.listItem.fillOutForm"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></li>


<li><ssf:nlt tag="help.globalStrings.listItem.clickOK"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickClose"/></li>

</ol>

<p><ssf:nlt tag="help.createTeam.content.defaultAccess"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/team_intro', 'ss_moreinfo_panel');"><ssf:nlt tag="help.understandingTeams.topic"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></a></p>
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt></a></p>
<p><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale}/pdfs/ICEcore Quick Start Guide.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt> <ssf:nlt tag="help.globalStrings.newWindow"/></a><a id="skip_nav_titles" /></p>
</div>

</div>






