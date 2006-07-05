<% //view a folder forum with folder on the left and the entry on the right in an iframe %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";

//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<div id="ss_showfolder" class="ss_style ss_portlet">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<c:set var="ss_toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>

<div id="ss_showentrydiv" onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:absolute; visibility:hidden;
  width:600; height:80%; display:none; z-index:50;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_color" value="${ss_entry_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
<div class="ss_entry_border" style="margin:0px;">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</div>
    </ssf:param>
    <ssf:param name="box_show_resize_icon" value="true" />
    <ssf:param name="box_show_resize_routine" value="ss_startDragDiv()" />
    <ssf:param name="box_show_resize_gif" value="box/resize_east_west.gif" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>

<script type="text/javascript">
var ss_iframe_box_div_name = '<portlet:namespace/>_iframe_box_div';
</script>

<jsp:include page="/WEB-INF/jsp/entry/view_iframe_js.jsp" />

<form class="ss_style ss_form" name="ss_saveEntryWidthForm" id="ss_saveEntryWidthForm" >
<input type="hidden" name="entry_width">
</form>
