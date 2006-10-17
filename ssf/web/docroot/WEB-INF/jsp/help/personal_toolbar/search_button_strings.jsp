<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.searchButton"/></span> (2/3)<br/>
<span style="font-weight:bold;"><ssf:nlt tag="help.searchicon.subtopic.searchStrings"/></span>

<p><ssf:nlt tag="help.searchicon.searchStrings.content.intro"/></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.case"/></li>

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.severalWords"/></li>

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.quotedPhrase"/></li>

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.excludeWords"/></li>

</ul>


</div>
<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarSearchButton');
</script>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/search_button', 'ss_help_panel', '', '');" style="color:#0000ff;">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/search_button_understand_tab', 'ss_help_panel', '', '');" style="color:#0000ff;"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>