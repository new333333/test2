<% //Title view for folders %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_entryContent">
<span class="ss_entryTitle">
<a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.parentFolder.id}" 
    action="view_folder_entry"
    entryId="${ssDefinitionEntry.id}" />">
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.title}"/></a></span>
</div>