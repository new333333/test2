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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.searchButton"/></span> (2/3)<br/>
<span style="font-weight:bold;"><ssf:nlt tag="help.searchicon.subtopic.searchStrings"/></span>

<p><ssf:nlt tag="help.searchicon.searchStrings.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.case"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.severalWords"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.quotedPhrase"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.searchicon.searchStrings.content.listItem.excludeWords"/></li>

</ul>


</div>
<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarSearchButton');
</script>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('navigation_bar/search_button', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('navigation_bar/search_button_understand_tab', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>