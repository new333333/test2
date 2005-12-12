<% // Calendar view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.ef.domain.Entry" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssEventDates" type="java.util.HashMap" scope="request" />
<jsp:useBean id="ssCalendarViewMode" type="java.lang.String" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="java.lang.String" scope="request" />

<script language="javascript">
function highlightLineById(id) {
    if (id == "") {return;}
    var obj = null
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(id)
    } else {
        obj = self.document.all[id]
    }
 
	if (highlightedLine != null) {
		highlightedLine.className = savedHighlightClassName;
	}
	if (obj != null) {
		highlightedLine = obj;
		savedHighlightedLineClassName = highlightClassName;
		highlightedLine.className = highlightClassName;
	}
}
</script>

<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
<% // get the folder tree %>
<div class="ss_folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
<% // Then include the navigation widgets for this view %>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>

<%@ include file="/WEB-INF/jsp/definition_elements/calendar_nav_bar.jsp" %></td>

<c:choose>
<c:when test="${ssCalendarViewMode == 'day'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view_day.jsp" %>
</c:when>

<c:when test="${ssCalendarViewMode == 'week'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view_week.jsp" %>
</c:when>

<c:when test="${ssCalendarViewMode == 'month'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view_month.jsp" %>
</c:when>

<c:otherwise>
Unknown view mode: ${ssCalendarViewMode}
</c:otherwise>
</c:choose>

</div>