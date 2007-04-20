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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.helpButton"/></span> 
<div style="display:inline;" id="ss_multiple_pages">(2/5)</div><br />
<span style="font-weight:bold;"><ssf:nlt tag="help.helpicon.subtopic.stepThrough"/></span>

<p><ssf:nlt tag="help.helpicon.stepThroughIcons.content.intro"/></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.helpicon.stepThroughIcons.content.multiplePages"/></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.helpicon.stepThroughIcons.content.navigating"/></p>

</div>

<br/>

<div align="center" id="ss_next_and_previous">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button', 'ss_help_panel', '', '');">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_control_panel', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
<script type="text/javascript">
ss_helpSystem.highlight('ss_multiple_pages');ss_helpSystem.highlight('ss_next_and_previous');
</script>

