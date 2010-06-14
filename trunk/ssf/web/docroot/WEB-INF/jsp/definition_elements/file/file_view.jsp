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
<%
	Map<String,Boolean> ss_entryAttributesSeen = new java.util.HashMap<String,Boolean>();
%>
<c:set var="ss_entryAttributesSeen" value="<%= ss_entryAttributesSeen %>" scope="request" />

<div class="ss_style ss_portlet_style ss_portlet">
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />

<% // Navigation links %>
<div id="ss_entry_view_navigation_links" style="display:none">
  <jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />
</div>

<table cellspacing="0" cellpadding="0" width="100%" class="ss_actions_bar13_pane">
<tr><td valign="top" width="95%">
  <ssHelpSpot helpId="workspaces_folders/entries/entry_toolbar" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.entryToolbar"/>"></ssHelpSpot>
<ssf:toolbar toolbar="${ssFolderEntryToolbar}" style="ss_actions_bar13 ss_actions_bar" />
</td>
<td valign="top" align="right" width="5%" nowrap style="padding-top:5px">
  <ssf:ifadapter>
  	<a class="ss_actions_bar13_pane_none" href="javascript: window.print();"><img border="0" 
      alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
      src="<html:rootPath/>images/pics/masthead/masthead_printer.png" width="23" height="21"/></a>&nbsp;&nbsp;
  </ssf:ifadapter>
    <a class="ss_actions_bar13_pane_none"
      href="javascript: ss_helpSystem.run();"><img border="0"
      <ssf:alt tag="navigation.help"/> src="<html:imagesPath/>icons/help.png" />
    </a>&nbsp;&nbsp;
</td>
</tr>
</table>

<div style="padding: 0.4em;">
<c:if test="${!empty ssPrimaryFileAttribute}">
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes[ssPrimaryFileAttribute].valueSet}" />
  <c:forEach var="selection" items="${selections}" varStatus="status">
    <c:if test="${status.first}">
<%
	ss_entryAttributesSeen.put("title", true);
%>
      <table cellspacing="0" cellpadding="0">
      <tr>
        <td valign="bottom">
          <div class="ss_thumbnail_tiny ss_thumbnail_standalone">
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_thumbnail.jsp" />
          </div>
        </td>
        <td valign="bottom">
	      <div class="ss_entryTitleFile">
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_title.jsp" />
          </div>
        </td>
	    <td valign="bottom" nowrap>
		  <div class="ss_entryTitleFileVersion"><ssf:nlt tag="file.versionNumber"><ssf:param
			name="value" value="${selection.fileVersion}"/></ssf:nlt></div>
		</td>
		<td valign="bottom" nowrap>
          <div class="ss_entryTitleFileVersion">
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_status.jsp" />
          </div>
		</td>
		<td valign="bottom" nowrap>
          <div class="ss_entryTitleFileVersion">
            <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions.jsp" />
          </div>
		</td>
      </tr>
      </table>
    </c:if>
  </c:forEach>
</c:if>
<c:if test="${!ss_entryAttributesSeen['title']}">
  <c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" varStatus="status">
    <c:if test="${status.first}">
<%
	ss_entryAttributesSeen.put("title", true);
%>
      <table cellspacing="0" cellpadding="0">
      <tr>
      <td valign="bottom">
      <div class="ss_thumbnail_tiny">
        <img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
		  src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/>
      </div>
      </td>
      <td valign="bottom">
	  <div class="ss_entryTitleFile">
      <a target="_blank" 
      href="<ssf:fileUrl file="${selection}"/>" 
      >${selection.fileItem.name}
      </a>
      </div>
      </td>
      </tr>
      </table>
    </c:if>
  </c:forEach>
</c:if>
<c:if test="${!ss_entryAttributesSeen['title']}">
<%
	ss_entryAttributesSeen.put("title", true);
%>
	<div class="ss_entryTitleFile">
	<ssf:titleLink action="view_folder_entry" entryId="${ssDefinitionEntry.id}" 
		binderId="${ssDefinitionEntry.parentFolder.id}" entityType="${ssDefinitionEntry.entityType}"
		namespace="${renderResponse.namespace}_${ssDefinitionEntry.id}">
			<ssf:param name="url" useBody="true">
				<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
				folderId="${ssDefinitionEntry.parentFolder.id}" 
				action="view_folder_entry" entryId="${ssDefinitionEntry.id}" actionUrl="true" ><ssf:param
				name="namespace" value="${ss_title_namespace}"/></ssf:url>
			</ssf:param>
			<c:out value="${ssDefinitionEntry.title}" escapeXml="true"/>
	</ssf:titleLink>
	</div>
</c:if>

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
</div>
<script type="text/javascript">
function ss_focusOnEntry() {
	ss_setFocusToFirstA("ss_entryTop${ss_entryLinkCounter}");
}
ss_createOnLoadObj("ss_focusOnEntry", ss_focusOnEntry);
</script>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
