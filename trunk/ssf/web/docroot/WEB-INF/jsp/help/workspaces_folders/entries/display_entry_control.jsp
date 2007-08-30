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
<div class="ss_style">
<div class="ss_help_style">

<div class="ss_help_title">
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.displayEntryControl"/></span>
</div>

<p><ssf:nlt tag="help.displayEntryControl.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<div class="picture">
<img border="0" <ssf:alt tag="helpTitleAlt.displayEntryIcon"/> src="<html:imagesPath/>pics/downarrow.gif" /> 
</div>

<p><ssf:nlt tag="help.displayEntryControl.displayOptions" /></p>

<p><ssf:nlt tag="help.displayEntryControl.contactPeopleIntro" /></p>

<div class="picture">
<img border="0" <ssf:alt tag="helpTitleAlt.presence.online"/> src="<html:imagesPath/>pics/sym_s_green_dude.gif" /> 
<img border="0" <ssf:alt tag="helpTitleAlt.presence.away"/> src="<html:imagesPath/>pics/sym_s_yellow_dude.gif" /> 
<img border="0" <ssf:alt tag="helpTitleAlt.presence.offline"/> src="<html:imagesPath/>pics/sym_s_gray_dude.gif" /> 
<img border="0" <ssf:alt tag="helpTitleAlt.presence.unavailable"/> src="<html:imagesPath/>pics/sym_s_white_dude.gif" /> 
</div>

<p><ssf:nlt tag="help.displayEntryControl.presenceIconDescription"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt> <ssf:nlt tag="help.displayEntryControl.contactPeople"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

</div>
