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
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.myWorkspaceButton"/></span>
</div>

<p><ssf:nlt tag="help.myWorkspaceIcon.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<ul>

<li><ssf:nlt tag="help.myWorkspaceIcon.content.listItem.pictures"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.content.listItem.contactInfo"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.content.listItem.blog"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.content.listItem.task"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.content.listItem.guestbook"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.content.listItem.more"/></li>

</ul>

<p><ssf:nlt tag="help.myWorkspaceIcon.content.ifNew"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/get_started/product_intro', 'ss_moreinfo_panel');"><ssf:nlt tag="help.getStartedProduct.title"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a></p>
<p><a target="ss_new" href="<html:rootPath/>help/${ssUser.locale.language}/pdfs/ICEcore Quick Start Guide.pdf"><ssf:nlt tag="help.viewBooks.content.listItem.quickStart"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt> <ssf:nlt tag="help.globalStrings.newWindow"/></a><a id="skip_nav_titles"/></p>
</div>

</div>



