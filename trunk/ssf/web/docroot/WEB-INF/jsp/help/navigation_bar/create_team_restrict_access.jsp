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
<span class="ss_titlebold"><ssf:nlt tag="help.createTeam.subTopic.restrictAccess"/></span>
</div>

<p><ssf:nlt tag="help.setTeamAccess.content.listIntro"/></p>

<ol>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.viewPage"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.aclMenu"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.noInherit"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.addGroup"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.enableGroup"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.removeAllUsers"/></li>

<li><ssf:nlt tag="help.setTeamAccess.content.listItem.saveChanges"/></li>

<li><ssf:nlt tag="help.globalStrings.listItem.clickClose"/></li>

</ol>

</div>
</div>


