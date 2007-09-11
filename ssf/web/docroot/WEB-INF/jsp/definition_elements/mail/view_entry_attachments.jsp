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
<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<div class="ss_entryContent">

<span class="ss_labelLeft"><c:out value="${property_caption}"/></span><br/>

<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${renderResponse.namespace}" scope="request"/>
<c:if test="${!empty ss_namespace}">
<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${ss_namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${ss_namespace}" scope="request"/>
</c:if>

<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list.jsp" %>

</div>
</c:if>