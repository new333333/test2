<% //view a folder forum with the entry at the bottom in an iframe %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
//int sliderDivHeight = 18;
int sliderDivHeight = 6;
String sliderDivOffset = "-20";

//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<script type="text/javascript">
//Set up variables needed by the javascript routines
var ss_saveEntryHeightUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_entry_height" />
	<ssf:param name="binderId" value="${ssFolder.id}" />
	</ssf:url>";
var ss_folderTableId = 'ss_folder_table';
var ss_iframe_box_div_name = '<portlet:namespace/>_iframe_box_div';
</script>
<script type="text/javascript" src="<html:rootPath/>js/forum/view_vertical.js"></script>

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<c:set var="ss_toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>

<div id="ss_folder">
  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
    configElement="${ssConfigElement}" 
    configJspStyle="${ssConfigJspStyle}" />
</div>
<div id="ss_showfolder_slider" align="center" onMousedown="ss_startDragDiv();"
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}" 
  style="position:relative; margin:0px 2px 0px 2px; padding:0px; 
    border-top:1px solid #666666; 
    background-color:${ss_style_background_color};
    cursor:n-resize; top:<%= sliderDivOffset %>px;"
><table cellspacing="0" cellpadding="0" style="width:95%">
<tr>
<td><img style="visibility:hidden; height:<%= String.valueOf(sliderDivHeight) %>px;" src="<html:imagesPath/>pics/1pix.gif"></td>
</tr></table></div>

<div id="ss_showentrydiv" class="ss_style ss_portlet" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:relative; margin:0px 0px 0px 2px; 
    top:<%= sliderDivOffset %>px;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_class" value="ss_style" />
    <ssf:param name="box_color" value="${ss_folder_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
      <div class="ss_folder_border">
      <%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
      </div>
    </ssf:param>
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="400" width="100%" 
    onLoad="if (self.ss_setEntryDivHeight) ss_setEntryDivHeight();" frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
</div>
<div id="ss_showfolder_bottom" class="ss_style ss_portlet">&nbsp;</div>

<form class="ss_style ss_form" name="ss_saveEntryHeightForm" id="ss_saveEntryHeightForm" >
<input type="hidden" name="entry_height">
</form>
