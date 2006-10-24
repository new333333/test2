<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.helpButton"/></span> (1/5)

<p><ssf:nlt tag="help.helpicon.content.intro"/></p>

<p style="margin-bottom:6px;"><ssf:nlt tag="help.helpicon.content.description"/></p>

<p style="margin-bottom:0px;margin-top:0px;"><span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.moreinfo.header"/></span></p>

<ul style="list-style-type:disc;">
<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_icons', 'ss_help_panel', '', '');"><ssf:nlt tag="help.helpicon.subtopic.stepThrough"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_control_panel', 'ss_help_panel', '', '');"><ssf:nlt tag="help.helpicon.subtopic.controlPanel"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_manuals', 'ss_help_panel', '', '');"><ssf:nlt tag="help.helpicon.subtopic.pdfManuals"/></a></li>

<li><a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_exit', 'ss_help_panel', '', '');"><ssf:nlt tag="help.helpicon.subtopic.exit"/></a></li>
</ul>

</div>
<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarHelpButton');
</script>

<br/>

<div align="center">
<div class="ss_style" style="display:inline;margin-right:10px;">
<a href="javascript: ss_helpSystem.showPreviousHelpSpot();">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_icons', 'ss_help_panel', '', '');"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
