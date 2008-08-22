<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<div class="ss_style ss_portlet_style ss_portlet">
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
<table cellspacing="0" cellpadding="0" width="100%" class="ss_actions_bar3_pane">
<tr><td valign="top">
  <ssHelpSpot helpId="workspaces_folders/entries/entry_toolbar" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.entryToolbar"/>"></ssHelpSpot>
<ssf:toolbar toolbar="${ssFolderEntryToolbar}" style="ss_actions_bar4 ss_actions_bar" />
</td>
<td valign="top" nowrap><ssf:ifadapter><a href="javascript: window.print();"><img border="0" 
    class="ss_print_button"
    alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
    src="<html:imagesPath/>pics/1pix.gif" /></a>&nbsp;&nbsp;</ssf:ifadapter><a
    href="javascript: ss_helpSystem.run();"><img border="0"
    <ssf:alt tag="navigation.help"/> src="<html:imagesPath/>icons/help.png" /></a></td>
</tr>
</table>
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<td valign="top"><jsp:include page="/WEB-INF/jsp/definition_elements/popular_view.jsp" /></td>
</tr>
</table>

<ssf:ifnotadapter>
<% // Navigation links %>
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
<br/>
</ssf:ifnotadapter>

<c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}" />
  
</div>

<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
