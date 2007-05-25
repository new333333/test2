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
<%@ include file="/WEB-INF/jsp/help/hide_help_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="help.favoritesicon.content.title"/></span> (3/4)<br/>
<span style="font-weight:bold;"><ssf:nlt tag="help.favoritesicon.subtopic.categories"/></span>

<p><ssf:nlt tag="help.favoritesicon.categories.content.introduceList"/></p>

<ol>
<li><ssf:nlt tag="help.favoritesicon.categories.content.listItem.clickAdd"/>

<p><ssf:nlt tag="help.favoritesicon.categories.content.listItem.clickAdd.afterEffects"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p></li>

<li><ssf:nlt tag="help.favoritesicon.categories.content.listItem.typeText"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickOK"/></li>
</ol>

<p><ssf:nlt tag="help.favoritesicon.categories.content.addingAFavorite"/></p>

</div>
<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('navigation_bar/favorites_button_add', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('navigation_bar/favorites_button_delete', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
