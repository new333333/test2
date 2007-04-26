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

<span class="ss_titlebold"><ssf:nlt tag="helpSpot.myWorkspaceButton"/></span> (1/1)

<p><ssf:nlt tag="help.myWorkspaceIcon.intro.explainIcon"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="help.myWorkspaceIcon.intro.listIntro"/></p>

<ul style="list-style-type:disc;">

<li><ssf:nlt tag="help.myWorkspaceIcon.intro.listItem.picture"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.intro.listItem.pictureGallery"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.intro.listItem.contactInfo"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.intro.listItem.blog"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.intro.listItem.guestbook"/></li>

<li><ssf:nlt tag="help.myWorkspaceIcon.intro.listItem.more"/></li>

</ul>

<p><ssf:nlt tag="help.myWorkspaceIcon.intro.alterAccess"/></p>

</div>
<br/>

<script type="text/javascript">
ss_helpSystem.highlight('ss_navbarMyWorkspaceButton');
</script>

