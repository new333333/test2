<% // Folder listing - select the style that the folder should be displayed in %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ss_folderViewStyle == 'event'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view.jsp" %>
</c:if>
<c:if test="${ss_folderViewStyle == 'file'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/file_library.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'blog'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/blog.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'timeline'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/timeline.jsp" />
</c:if>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/folder_view.jsp" />
</c:if>
