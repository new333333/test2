<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style" align="left">

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
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button', 'ss_help_panel', '', '');" style="color:#0000ff;">&lt;&lt;&lt; <ssf:nlt tag="helpPanel.button.previous"/></a>
</div>

<div class="ss_style" style="display:inline;margin-right:6px;">
<a href="javascript: ss_helpSystem.showHelpPanel('personal_toolbar/help_button_control_panel', 'ss_help_panel', '', '');" style="color:#0000ff;"><ssf:nlt tag="helpPanel.button.next"/> &gt;&gt;&gt;</a>
</div>
</div>
<script type="text/javascript">
ss_helpSystem.highlight('ss_multiple_pages');ss_helpSystem.highlight('ss_next_and_previous');
</script>

