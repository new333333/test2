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
<%@ include file="/WEB-INF/jsp/help/hide_moreinfo_panel_button.jsp" %>

<span class="ss_titlebold"><ssf:nlt tag="help.configIntro.title"/></span>

<p><ssf:nlt tag="help.configIntro.content.listIntro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.configIntro.content.listItem.initial"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.prepopulation" /></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.placeEnhancement"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.useEnhancement" /></li>

<li><ssf:nlt tag="help.configIntro.content.listItem.user" /></li>

</ul>

<p><span style="font-weight:bold;"><ssf:nlt tag="help.globalStrings.moreinfo.header" /></span></p>

<div style="margin-left:25px;">

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_defs', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.definitions" /></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_designers', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.designers" /></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_apply_defs', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.applyDefs" /></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_dedicate_apps', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.dedicatedApps" /></a></p>

<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_templates', 'ss_moreinfo_panel');"><ssf:nlt tag="help.configIntro.subTopic.templates" /></a></p>

</div>

</div>

<br/>

<div align="center" style="margin-bottom:5px;" title="This Help topic has more than one page of information">
<div style="display:inline;margin-right:10px;"><img border="0" <ssf:alt tag="general.previous"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></div>
<div style="display:inline;margin-right:10px;">1</div> 
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_defs', 'ss_moreinfo_panel');">2</a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_designers', 'ss_moreinfo_panel');">3</a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_apply_defs', 'ss_moreinfo_panel');">4</a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_dedicate_apps', 'ss_moreinfo_panel');">5</a></div>
<div style="display:inline;margin-right:10px;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_templates', 'ss_moreinfo_panel');">6</a></div>
<div style="display:inline;"><a href="javascript: ss_helpSystem.showMoreInfoPanel('portlets/admin/config_defs', 'ss_moreinfo_panel');"><img border="0" <ssf:alt tag="general.next"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a></div>
</div>
