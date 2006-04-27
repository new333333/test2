<% // Folder listing - select the style that the folder should be displayed in %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.ef.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<c:if test="${ss_folderViewStyle == 'event'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view.jsp" %>
</c:if>
<c:if test="${ss_folderViewStyle == 'file'}">
<%@ include file="/WEB-INF/jsp/definition_elements/file_library.jsp" %>
</c:if>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
<%@ include file="/WEB-INF/jsp/definition_elements/folder_view.jsp" %>
</c:if>
