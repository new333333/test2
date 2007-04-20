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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.searchButton"/></span> (3/3)<br />
<span style="font-weight:bold;"><ssf:nlt tag="help.searchicon.subtopic.understandTab"/></span>

<p><ssf:nlt tag="help.searchicon.understandTab.content.intro"/></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.searchicon.understandTab.content.listItem.people"/></li>

<li><ssf:nlt tag="help.searchicon.understandTab.content.listItem.places"/></li>

<li><ssf:nlt tag="help.searchicon.understandTab.content.listItem.things"/></li>

</ul>

<p><ssf:nlt tag="help.searchicon.understandTab.content.switchResultType"/></p>

</div>

<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarSearchButton');
</script>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/search_button_strings', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showNextHelpSpot('', 'left');"><ssf:nlt tag="helpPanel.button.next"/> 
&gt;&gt;&gt;</a>
</div>
</div>