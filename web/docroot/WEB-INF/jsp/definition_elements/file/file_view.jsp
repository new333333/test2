<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="org.kablink.teaming.domain.DefinableEntity" scope="request" />
<%
	Map<String,Boolean> ss_entryAttributesSeen = new java.util.HashMap<String,Boolean>();
	String thisEntryId = ssDefinitionEntry.getId().toString();
	
	boolean entryInTrash = (ssDefinitionEntry instanceof FolderEntry && ((FolderEntry)ssDefinitionEntry).isPreDeleted());
%>
<c:set var="ss_entryAttributesSeen" value="<%= ss_entryAttributesSeen %>" scope="request" />
<c:set var="thisEntryId" value="<%= thisEntryId %>" />

<div class="ss_style ss_portlet_style ss_portlet">
<jsp:include page="/WEB-INF/jsp/common/help_welcome.jsp" />

<table cellspacing="0" cellpadding="0" width="100%" 
	<c:if test="${ss_parentFolderViewStyle != 'wiki'}"> class="ss_actions_bar13_pane" </c:if>
>
<tr><td width="95%">
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
<td align="right" width="5%" style="padding-top:5px; white-space: nowrap">
  <c:if test="${ss_parentFolderViewStyle != 'wiki'}">
  	<a class="ss_actions_bar13_pane_none" href="javascript: window.print();"><img border="0" 
      align="absmiddle" alt="<ssf:nlt tag="navigation.print"/>" title="<ssf:nlt tag="navigation.print"/>"
      src="<html:rootPath/>images/pics/masthead/masthead_printer.png" /></a>&nbsp;&nbsp;
    <ssf:showHelp className="ss_actions_bar13_pane_none" guideName="user" pageId="entry" />
	&nbsp;&nbsp;
  </c:if>
</td>
</tr>
</table>

<div>
<c:if test="${!empty ssPrimaryFileAttribute}">
  <c:set var="selections" value="${ssDefinitionEntry.customAttributes[ssPrimaryFileAttribute].valueSet}" />
  <c:forEach var="selection" items="${selections}" varStatus="status">
    <jsp:useBean id="selection" type="org.kablink.teaming.domain.FileAttachment" />
    <c:if test="${status.first}">
<%
	ss_entryAttributesSeen.put("title"+ssDefinitionEntry.getId().toString(), true);
%>
      <table cellspacing="0" cellpadding="0" style="margin-left: 5px; margin-top: 10px">
      <tr>
        <td <c:if test="${!empty selection.fileItem.description.text}">rowspan="2" valign="top" </c:if> >
          <div class="ss_thumbnail_tiny ss_thumbnail_standalone">
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_thumbnail.jsp" />
          </div>
        </td>
        <td 
          <% if (!BrowserSniffer.is_ie(request) && selection.getFileItem().getName().length() <= 40) { %> style="white-space: nowrap;" <% } %>
		  <% if (BrowserSniffer.is_ie(request) || selection.getFileItem().getName().length() > 40) { %> style="white-space: normal;" <% } %>
        >
	      <div class="ss_entryTitleFile" <% if (BrowserSniffer.is_ie(request) || selection.getFileItem().getName().length() > 40) { %> style="white-space: normal;" <% } %>>
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_title.jsp" />
          </div>
        </td>
        
        <% if (entryInTrash) { %>
        	<td style = "shite-space: nowrap;">
				<div style="display: inline; margin:0px 30px;"><span class="wiki-noentries-panel"><ssf:nlt tag="entry.inTheTrash"/></span></div>
			</td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		<% } %>
        
        <% if (!entryInTrash) { %>
		    <td  style="white-space: nowrap;">
			  <div class="ss_entryTitleFileVersion"><ssf:nlt tag="file.versionNumber"><ssf:param
				name="value" value="${selection.fileVersion}"/></ssf:nlt></div>
			</td>
			<td style="white-space: nowrap;">
	          <div class="ss_entryTitleFileVersion">
	            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
	            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_status.jsp" />
	          </div>
			</td>
			<td style="white-space: nowrap;">
	          <div class="ss_entryTitleFileVersion">
	            <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
	            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
	            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions.jsp" />
	          </div>
			</td>
			<td style="white-space: nowrap;">
	          <div class="ss_entryTitleFileVersion">
	            <c:set var="ss_attachedFileShowEditButton" value="true" scope="request"/>
	            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions_edit_button.jsp" />
	            <c:set var="ss_attachedFileShowEditButton" value="false" scope="request"/>
	          </div>
			</td>
			<td <% if (!BrowserSniffer.is_ie(request)) { %>width="100%" <% } %> >&nbsp;</td>
	  <% } %>
      </tr>
      <c:if test="${!empty selection.fileItem.description.text}">
      <tr>
        <td colspan="6" valign="top">
          <div class="ss_entryTitleFileDescription">
            <ssf:markup type="view" entity="${ssDefinitionEntry}">${selection.fileItem.description.text}</ssf:markup>
          </div>
        </td>
      </tr>
      </c:if>
      </table>
    </c:if>
  </c:forEach>
</c:if>
<c:set var="thisTitle" value="title${thisEntryId}"/>
<c:if test="${!ss_entryAttributesSeen[thisTitle]}">
  <c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" varStatus="status">
    <c:if test="${status.first}">
<%
	ss_entryAttributesSeen.put("title"+ssDefinitionEntry.getId().toString(), true);
%>
      <table cellspacing="0" cellpadding="0">
      <tr>
      <td>
      <div class="ss_thumbnail_tiny">
       <c:if test="${selection.fileExists}">
        <img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
		  src="<ssf:fileUrl webPath="readThumbnail" file="${selection}" useVersionNumber="${ss_useExplicitFileVersionNumbers}"/>"/>
       </c:if>
      </div>
      </td>
      <td>
	  <div class="ss_entryTitleFile">
	  <c:if test="${selection.fileExists}">
        <a target="_blank" 
          href="<ssf:fileUrl file="${selection}" useVersionNumber="${ss_useExplicitFileVersionNumbers}"/>" 
        >${selection.fileItem.name}
        </a>
      </c:if>
      <c:if test="${!selection.fileExists}">
        <span>${selection.fileItem.name}</span>
      </c:if>
      </div>
      </td>
      </tr>
      </table>
    </c:if>
  </c:forEach>
</c:if>
<c:set var="thisTitle" value="title${thisEntryId}"/>
<c:if test="${!ss_entryAttributesSeen[thisTitle]}">
<%
	ss_entryAttributesSeen.put("title"+ssDefinitionEntry.getId().toString(), true);
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
		  title="<ssf:nlt tag="entry.AttachFilesByWebDav" />" >Novell Vibe</iframe>
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
<c:if test="${ss_parentFolderViewStyle != 'wiki'}">
  <%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</c:if>
