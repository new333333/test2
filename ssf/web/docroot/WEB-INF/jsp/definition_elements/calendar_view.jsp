<% // Calendar view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.ef.domain.Entry" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.ef.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<jsp:useBean id="ssEventDates" type="java.util.HashMap" scope="request" />
<jsp:useBean id="ssCalendarViewMode" type="java.lang.String" scope="request" />

<%
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<c:set var="ss_toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>

<script type="text/javascript">
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

<div class="ss_folder">

<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>

<% // Then include the navigation widgets for this view %>
<div style="margin:0px;">
<div class="ss_folder_border" style="position:relative; top:2px; margin:0px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
<table cellspacing="0" cellpadding="0" width="95%">
<tr><td align="left">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</td>
<td>
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</td>
<td align="right">&nbsp;</td>
</tr>
</table>
</div>
</div>

<%
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
<div id="ss_folder_table" 
  style="position:relative; overflow:scroll; height:<%= ssFolderTableHeight %>;">
<%
	} else {
%>
<div id="ss_folder_table">
<%
	}
%>

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
</div>