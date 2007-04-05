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

<span class="ss_titlebold"><ssf:nlt tag="help.favoritesicon.content.title"/></span> (1/4)

<p><ssf:nlt tag="help.favoritesicon.content.intro.explainIcon"/></p>

<p><a href="javascript: ss_showFavoritesPane('<portlet:namespace/>');"><ssf:nlt tag="help.favoritesicon.content.intro.viewFavoritesPanel"/></a></p>

<p><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button', 'ss_help_panel', '', '', 'left');"><ssf:nlt tag="help.favoritesicon.content.intro.moveHelpPanel"/></a></p>

<p><ssf:nlt tag="help.favoritesicon.content.intro.listIntro"/></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_add', 'ss_help_panel', '', '');"><ssf:nlt tag="help.favoritesicon.subtopic.add.current.page"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_categories', 'ss_help_panel', '', '');"><ssf:nlt tag="help.favoritesicon.subtopic.categories"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_delete', 'ss_help_panel', '', '');"><ssf:nlt tag="help.favoritesicon.subtopic.deleting"/></a></li>
</ul>

<p style="text-align:center;"><a href="javascript: ss_hideFavoritesPane();"><ssf:nlt tag="help.favoritesicon.content.intro.closeFavoritesPanel"/></a></p>

</div>
<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarFavoritesButton<portlet:namespace/>');
</script>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showPreviousHelpSpot();" style="color:#0000ff;">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_add', 'ss_help_panel', '', '');" style="color:#0000ff;"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
