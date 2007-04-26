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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.searchButton"/></span> (1/3)

<p><ssf:nlt tag="help.searchicon.content.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.searchicon.content.howto"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p style="margin-bottom:0px;margin-top:0px;"><span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.moreinfo.header"/></span></p>

<ul style="list-style-type:disc;">

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/search_button_strings', 'ss_help_panel', '', '');"><ssf:nlt tag="help.searchicon.subtopic.searchStrings"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/search_button_understand_tab', 'ss_help_panel', '', '');"><ssf:nlt tag="help.searchicon.subtopic.understandTab"/></a></li>

</ul>

</div>
<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarSearchButton');
</script>


<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showPreviousHelpSpot();">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>

</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/search_button_strings', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>