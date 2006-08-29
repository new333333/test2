<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">

<span class="ss_titlebold"><ssf:nlt tag="help.favoritesicon.content.title"/></span> (1/4)

<p><ssf:nlt tag="help.favoritesicon.content.intro.explainIcon"/></p>

<p><ssf:nlt tag="help.favoritesicon.content.intro.introduceList"/></p>

<ul style="list-style-type:disc;">
<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_add', 'ss_help_panel', '', '');"
style="color:#0000ff;"><ssf:nlt tag="help.favoritesicon.content.intro.subtopicone"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_categories', 'ss_help_panel', '', '');"
style="color:#0000ff;"><ssf:nlt tag="help.favoritesicon.content.intro.subtopictwo"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_delete', 'ss_help_panel', '', '');"
style="color:#0000ff;"><ssf:nlt tag="help.favoritesicon.content.intro.subtopicthree"/></a></li>
</ul>

</div>
<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarFavoritesButton');
</script>
<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<span class="ss_gray">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></span>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/favorites_button_add', 'ss_help_panel', '', '');"
style="color:#0000ff;"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
