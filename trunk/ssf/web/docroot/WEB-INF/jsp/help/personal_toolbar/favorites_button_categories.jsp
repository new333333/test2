<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">

<span class="ss_titlebold"><ssf:nlt tag="help.favoritesicon.content.title"/></span> (3/4)<br/>
<span style="font-weight:bold;"><ssf:nlt tag="help.favoritesicon.subtopic.categories"/></span>

<p><ssf:nlt tag="help.favoritesicon.categories.content.introduceList"/></p>

<ol>
<li><ssf:nlt tag="help.favoritesicon.categories.content.listItem.clickAdd"/>

<p><ssf:nlt tag="help.favoritesicon.categories.content.listItem.clickAdd.afterEffects"/></p></li>

<li><ssf:nlt tag="help.favoritesicon.categories.content.listItem.typeText"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickOK"/></li>
</ol>

<p><ssf:nlt tag="help.favoritesicon.categories.content.addingAFavorite"/></p>

<p style="text-align:center;"><a href="javascript: ss_hideFavoritesPane();" style="color:#0000ff;"><ssf:nlt tag="help.favoritesicon.content.intro.closeFavoritesPanel"/></a></p>
</div>
<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_add', 'ss_help_panel', '', '');" style="color:#0000ff;">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_delete', 'ss_help_panel', '', '');" style="color:#0000ff;"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
