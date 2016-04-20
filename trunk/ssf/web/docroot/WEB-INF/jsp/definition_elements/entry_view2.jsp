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
<c:set var="binderDefinition" value="${ssBinder.entryDef.definition}" />
<jsp:useBean id="binderDefinition" type="org.dom4j.Document" />

<c:if test="${propertyValues_type[0] != 'file'}">
	<div class="ss_style ss_portlet_style ss_portlet">
	<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />
	<table cellspacing="0" cellpadding="0" width="100%" 
		<c:if test="${ss_parentFolderViewStyle != 'wiki'}"> class="ss_actions_bar13_pane" </c:if>
	>
	<tr><td valign="top">
	  <ssHelpSpot helpId="workspaces_folders/entries/entry_toolbar" offsetX="0" 
	    title="<ssf:nlt tag="helpSpot.entryToolbar"/>"></ssHelpSpot>
	  <c:choose>
	    <c:when test="${ss_parentFolderViewStyle == 'wiki'}">
			<ssf:toolbar toolbar="${ssFolderEntryToolbar}" format="wiki" style="ss_actions_bar13 ss_actions_bar" />
	    </c:when>
	    <c:otherwise>
			<ssf:toolbar toolbar="${ssFolderEntryToolbar}" style="ss_actions_bar13 ss_actions_bar" />
	    </c:otherwise>
	  </c:choose>
	</td>
	  <td valign="top" style="white-space: nowrap">
	  	<a class="ss_actions_bar13_pane_none" href="javascript: window.print();"><img style="vertical-align:middle;" 
	  	  border="0" alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
	      src="<html:rootPath/>images/pics/masthead/masthead_printer.png" /></a>&nbsp;&nbsp;
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
</c:if>
<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<c:if test="${!ss_fileopenDivSeen}">
  <div id="ss_div_fileopen${ss_attachments_namespace}" 
    name="ss_div_fileopen${ss_attachments_namespace}" 
    style="visibility:visible;display:block; width:1px; height:1px;">
	<div align="right">
		<iframe frameborder="0" 
		  id="ss_iframe_fileopen${ss_attachments_namespace}" 
		  name="ss_iframe_fileopen${ss_attachments_namespace}" 
		  src="<html:rootPath/>js/forum/null.html" 
		  height="1" width="1"
		  title="<ssf:nlt tag="entry.AttachFilesByWebDav" />" >Micro Focus Vibe</iframe>
	</div>
  </div>
  <c:set var="ss_fileopenDivSeen" value="true" scope="request"/>
</c:if>
<script type="text/javascript">
function ss_focusOnEntry() {
	ss_setFocusToFirstA("ss_entryTop${ss_entryLinkCounter}");
}
ss_createOnLoadObj("ss_focusOnEntry", ss_focusOnEntry);
</script>
