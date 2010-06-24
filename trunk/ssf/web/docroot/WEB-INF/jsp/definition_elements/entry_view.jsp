<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:choose>
  <c:when test="${propertyValues_type[0] == 'file'}">
	<jsp:include page="/WEB-INF/jsp/definition_elements/file/file_view.jsp" />
  </c:when>
  <c:otherwise>
<div class="ss_style ss_portlet_style ss_portlet">
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
<table cellspacing="0" cellpadding="0" width="100%" class="ss_actions_bar13_pane">
<tr><td valign="top">
  <ssHelpSpot helpId="workspaces_folders/entries/entry_toolbar" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.entryToolbar"/>"></ssHelpSpot>
<ssf:toolbar toolbar="${ssFolderEntryToolbar}" style="ss_actions_bar13 ss_actions_bar" />
</td>
<td valign="top" style="white-space: nowrap">
  <ssf:ifadapter>
  	<a class="ss_actions_bar13_pane_none" href="javascript: window.print();"><img border="0" 
      alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
      src="<html:rootPath/>images/pics/masthead/masthead_printer.png" width="23" height="21"/></a>&nbsp;&nbsp;
  </ssf:ifadapter>
  <ssf:showHelp className="ss_actions_bar13_pane_none" guideName="user" pageId="entry" />
</td>
</tr>
</table>
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<td valign="top">
<c:set var="ss_defFam" value="entry" scope="request"/>

<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view.jsp" /></td>
</tr>
</table>

<% // Navigation links %>
<div id="ss_entry_view_navigation_links" style="display:none">
  <jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
</div>

<c:if test="${empty ss_entryLinkCounter}"><c:set var="ss_entryLinkCounter" value="0" scope="request"/></c:if>
<c:set var="ss_entryLinkCounter" value="${ss_entryLinkCounter + 1}" scope="request"/>
<div id="ss_entryTop${ss_entryLinkCounter}" >
<c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}" />
  
</div>
</div>
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view.jsp" />
  </c:otherwise>
</c:choose>
<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<c:if test="${ss_accessControlMap[ssEntry.id]['modifyEntry']}">
  <div id="ss_div_fileopen${ssEntry.id}${ss_attachments_namespace}" 
    name="ss_div_fileopen${ssEntry.id}${ss_attachments_namespace}" 
    style="visibility:visible;display:block; width:1px; height:1px;">
	<div align="right">
		<iframe frameborder="0" 
		  id="ss_iframe_fileopen${ssEntry.id}${ss_attachments_namespace}" 
		  name="ss_iframe_fileopen${ssEntry.id}${ss_attachments_namespace}" 
		  src="<html:rootPath/>js/forum/null.html" 
		  height="1" width="1"
		  title="<ssf:nlt tag="entry.AttachFilesByWebDav" />" >xxx</iframe>
	</div>
  </div>
</c:if>
<script type="text/javascript">
function ss_focusOnEntry() {
	ss_setFocusToFirstA("ss_entryTop${ss_entryLinkCounter}");
}
ss_createOnLoadObj("ss_focusOnEntry", ss_focusOnEntry);
</script>
