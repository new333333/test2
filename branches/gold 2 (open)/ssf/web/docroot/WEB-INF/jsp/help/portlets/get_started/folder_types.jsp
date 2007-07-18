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
<span class="ss_titlebold"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span><br />
<span class="subtitle"><ssf:nlt tag="help.getStartedProduct.subtopic.folders"/></span>
</div>

<p><ssf:nlt tag="help.getStartedProduct.content.folders.listIntro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<ul>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.discussion"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.file"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.calendar"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.photoAlbum"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.guestbook"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.blog"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.wiki"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.survey"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.task"/></li>

<li><ssf:nlt tag="help.getStartedProduct.content.folders.listItem.milestone"/></li>

</ul>

</div>

<div class="ss_help_more_pages_section">
<a href="#skip_nav_panel_numbers" title="<ssf:nlt tag="helpTitleAlt.skipNavPanelNumbers" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel');"><<</a></div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');">1</a></div> 
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/understand_workspaces_folders', 'ss_moreinfo_panel');">2</a></div>
<div class="current_page">3</div>
<div class="not_last_link"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/teaming', 'ss_moreinfo_panel');">4</a></div>
<div><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/teaming', 'ss_moreinfo_panel');">>></a><a id="skip_nav_panel_numbers" /></div>
</div>

</div>
