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

	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
%>

<div class="ss_folder">

<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>

<% // Then include the navigation widgets for this view %>
<div style="margin:0px;">
<div class="ss_folder_border" style="position:relative; top:2px; margin:2px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar">

<ssf:toolbar style="ss_actions_bar" item="true">
<c:set var="ss_history_bar_table_class" value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</ssf:toolbar>

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar" item="true" />
</c:if>

<ssf:toolbar style="ss_actions_bar" item="true" >
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</ssf:toolbar>

</ssf:toolbar>

</div>
</div>

<%
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
<div id="ss_folder_table" 
  style="position:relative; overflow:scroll; height:<%= ssFolderTableHeight %>;
  margin:2px; border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
<%
	} else {
%>
<div id="ss_folder_table" style="margin:2px; border-left:solid #666666 1px;">
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