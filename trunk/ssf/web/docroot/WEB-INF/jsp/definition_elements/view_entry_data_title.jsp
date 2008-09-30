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
<% //Title view %>

<c:set var="ss_title_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_title_namespace" value="${ss_namespace}"/></c:if>

<div class="ss_entryContent">

<c:if test="${empty ss_title_breadcrumbs_seen && 
                    ssDefinitionEntry.entityType == 'folderEntry' && 
                    !empty ssDefinitionEntry.parentEntry}">
<div style="padding-bottom:10px;">
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
<div style="padding-bottom:10px;">
<span style="ss_fineprint ss_light">
<a
  href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum"
  folderId="${ssDefinitionEntry.parentBinder.id}" 
  entryId="${nextEntry.id}" 
  action="view_folder_entry"/>"
>
<c:if test="${!empty nextEntry.docNumber}">
${nextEntry.docNumber}.
</c:if>
<c:if test="${empty nextEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${nextEntry.title}" /><img border="0" <ssf:alt/>
  style="width:1px;height:14px;" src="<html:imagesPath/>pics/1pix.gif"/></a>
</span>
<br/>
<%
	}
%>
</div>
</c:if>
<c:set var="ss_title_breadcrumbs_seen" value="1" scope="request"/>
<span class="ss_entryTitle   ss_link_7 ss_link_7 span ">
	<c:if test="${!empty ssDefinitionEntry.docNumber}">
	  <c:out value="${ssDefinitionEntry.docNumber}"/>.
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
			<c:out value="${ssDefinitionEntry.title}"/>
		</ssf:titleLink>

</span>

</div>

<script type="text/javascript">
if (!ss_baseEntryUrl || !ss_baseBinderUrl) {
	var ss_baseEntryUrl = '';
	var ss_baseBinderUrl = '';
}

//This function just reloads the current link and overloads the ss_common definition
function ss_showForumEntry(url, isDashboard) {
	self.location.href = url;
	return;
}


var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";

</script>