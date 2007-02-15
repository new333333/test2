<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">

<span class="ss_labelLeft"><c:out value="${property_caption}"/></span><br/>

<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${renderResponse.namespace}" scope="request"/>

<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list.jsp" %>

</div>
