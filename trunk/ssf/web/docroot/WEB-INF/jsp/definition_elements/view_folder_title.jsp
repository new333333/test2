<% //Title view for folders %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_entryContent">
<span class="ss_entryTitle">
<a style="text-decoration: none;" href="<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_folder_listing" />">
<c:if test="${empty ssDefinitionEntry.title}" >
--<ssf:nlt tag="entry.noTitle" />--
</c:if>
<c:out value="${ssDefinitionEntry.title}"/></a></span>
</div>