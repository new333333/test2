<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<% //Title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.teaming.domain.FolderEntry" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<jsp:useBean id="ssDefinitionEntry" type="org.kablink.teaming.domain.DefinableEntity" scope="request" />
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="org.kablink.teaming.domain.FolderEntry" />
<%
	Map ssFolderColumns = (Map) ssUserFolderProperties.get("userFolderColumns");
	if (ssFolderColumns == null) ssFolderColumns = (Map)ssBinder.getProperty("folderColumns");
	if (ssFolderColumns == null) {
		ssFolderColumns = new java.util.HashMap();
	}
	
	boolean entryInTrash = (ssDefinitionEntry instanceof FolderEntry && ((FolderEntry)ssDefinitionEntry).isPreDeleted());
	boolean isIE = BrowserSniffer.is_ie(request);
%>
<c:set var="ssFolderColumns" value="<%= ssFolderColumns %>" scope="request"/>

<c:set var="ss_title_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_title_namespace" value="${ss_namespace}"/></c:if>

<div class="ss_entryContent" <% if (isIE && entryInTrash) { %>style="padding-top: 10px;"<% } %>>

<c:if test="${empty ss_title_breadcrumbs_seen && 
                    ssDefinitionEntry.entityType == 'folderEntry' && 
                    !empty ssDefinitionEntry.parentEntry}">

<div style="padding-bottom:2px;">

	<c:set var="parentEntry" value="${ssDefinitionEntry.parentEntry}"/>
	<jsp:useBean id="parentEntry" type="java.lang.Object" />
	<%
		Stack parentEntryTree = new Stack();
		while (parentEntry != null) {
			parentEntryTree.push(parentEntry);
			parentEntry = ((FolderEntry)parentEntry).getParentEntry();
		}
		while (!parentEntryTree.empty()) {
			FolderEntry nextEntry = (FolderEntry) parentEntryTree.pop();
	%>
	<c:set var="nextEntry" value="<%= nextEntry %>"/>
<span class="ss_comment_breadcrumb">
<a
  href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum"
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  entryId="${nextEntry.id}" 
  action="view_folder_entry"/>"
>
<c:if test="${!empty nextEntry.docNumber && !empty ssFolderColumns['number']}">
${nextEntry.docNumber}.
</c:if>
<c:if test="${empty nextEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextEntry.title}" escapeXml="true" /><img src="<html:rootPath/>images/pics/breadspace.gif" border="0" align="absmiddle"></a>
</span>
	
<%
	}
%>
</c:if>
	<div>
	<!-- 20110812 (DRF):  Eliminated as per bug#703285.
		<c:if test="${!ssSeenEntries[ssDefinitionEntry.id]}">
			<img border="0" align="absbottom" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.png">
		</c:if>
	-->
	<c:set var="ss_title_breadcrumbs_seen" value="1" scope="request"/>
	<span class="ss_entryTitle ss_link_7">
		<c:if test="${(empty property_showDocNumber || property_showDocNumber == 'true') && 
			!empty ssDefinitionEntry.docNumber && !empty ssFolderColumns['number']}">
		  <c:out value="${ssDefinitionEntry.docNumber}"/>.
		</c:if>
		<c:if test="${(empty property_showDocNumber || property_showDocNumber == 'true') && 
			!empty ssDefinitionEntry.docNumber && empty ssFolderColumns['number'] && 
			!empty ssDefinitionEntry.parentEntry}">
		  <%
		  	//This is a reply. Always make sure the reply number is shown
		  	if (ssDefinitionEntry instanceof FolderEntry) {
		  		String docNum = ((FolderEntry)ssDefinitionEntry).getDocNumber();
		  		docNum = docNum.substring(docNum.indexOf(".") + 1, docNum.length());
		  %>
		  <%= docNum %>. 
		  <%
		  	}
		  %>
		</c:if>
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
			<% if (entryInTrash) { %>
			  <div style="display: inline; margin:0px 30px;"><span class="wiki-noentries-panel"><ssf:nlt tag="entry.inTheTrash"/></span></div>
			<% } %>
	</span>
	</div>
</div>

<script type="text/javascript">
if (!ss_baseEntryUrl || !ss_baseBinderUrl) {
	var ss_baseEntryUrl = '';
	var ss_baseBinderUrl = '';
}

//This function just reloads the current link and overloads the ss_common definition
function ss_showForumEntryOverride(url, isDashboard) {
	self.location.href = url;
	return;
}


var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";

</script>