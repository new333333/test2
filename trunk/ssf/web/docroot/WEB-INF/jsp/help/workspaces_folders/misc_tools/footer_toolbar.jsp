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
<span class="ss_titlebold"><ssf:nlt tag="helpSpot.bottomLinks"/></span>
</div>

<p><ssf:nlt tag="help.bottomLinks.intro"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.bottomLinks.exceptions"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.bottomLinks.clipboard"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.bottomLinks.iCal"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.bottomLinks.webDAV"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

</div>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a href="javascript: ss_helpSystem.showMoreInfoPanel('workspaces_folders/misc_tools/webdav', 'ss_moreinfo_panel');"><ssf:nlt tag="help.webDAV.subtopic" /></a></p>
</div>

</div>
