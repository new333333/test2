<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<% // Define the user's choice of skins (right now there is only one) %>
<c:set var="ss_user_skin" value="r1" scope="request"/>
<div class="ss_style" align="left">

<span class="ss_titlebold"><ssf:nlt tag="help.on.help.title"/></span> 

<p><ssf:nlt tag="help.helpicon.content.description"/></p>

<p><ssf:nlt tag="help.on.help.graphicIntro"/></p>

<div align="center">
<img border="0" alt="" src="<html:imagesPath/>skins/${ss_user_skin}/toolbar/help_choices.gif" />
</div>

<p><ssf:nlt tag="help.helpicon.stepThroughIcons.content.intro"/></p>

<p><ssf:nlt tag="help.on.help.toc"/></p>

<p><ssf:nlt tag="help.on.help.buttons"/></p>

<div align="center">
<a class="ss_linkButton ss_smallprint" href="#" onClick="ss_hideDiv('ss_help_on_help'); return false;"><ssf:nlt tag="button.close"/></a>
</div>
</div>