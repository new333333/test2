<% //view a folder forum with the entry at the bottom in an iframe %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
//int sliderDivHeight = 18;
int sliderDivHeight = 7;
String sliderDivOffset = "-20";

//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">

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
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />

<% // Tabs %>
<jsp:include page="/WEB-INF/jsp/definition_elements/tabbar.jsp" />
<div class="ss_clear"></div>

<div class="ss_tab_canvas">
<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
			  <div class="ss_style_color" id="ss_tab_data_${ss_tabs.current_tab}">

<% // Folder toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<div class="ss_content_inner">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar"/>
</div>
</c:if>

<div class="ss_content_inner">
<c:if test="${!ss_showSearchResults}">
<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>

<div id="ss_folder">
  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
    configElement="${ssConfigElement}" 
    configJspStyle="${ssConfigJspStyle}" />
</div>
</c:if>
<c:if test="${ss_showSearchResults}">
<%@ include file="/WEB-INF/jsp/definition_elements/search/search_results_view.jsp" %>
</c:if>
<c:if test="${ss_folderViewStyle != 'blog'}">
<div id="ss_showfolder_slider" align="center" onMousedown="ss_startDragDiv();"
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}" 
  style="position:relative; margin:0px 2px 0px 2px; padding:0px; 
    border-top:1px solid #666666; 
    background-color:${ss_style_background_color};
    cursor:n-resize; top:<%= sliderDivOffset %>px;"
><table cellspacing="0" cellpadding="0" style="width:95%">
<tr>
<td style="background:url(<html:imagesPath/>skins/${ss_user_skin}/uparrows.gif) center no-repeat;"><img 
  border="0" style="height:<%= String.valueOf(sliderDivHeight) %>px;" 
  src="<html:imagesPath/>pics/1pix.gif"></td>
</tr></table></div>

<div id="ss_showentrydiv" class="ss_style ss_portlet" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:relative; padding:0px 0px 20px 2px; left:-1px;
    top:<%= sliderDivOffset %>px;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_class" value="ss_style" />
    <ssf:param name="box_style" value="margin:0px;" />
    <ssf:param name="box_color" value="${ss_folder_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
    <!-- Set width to 0 to indicate "100%" -->
    <ssf:param name="box_width" value="0" />
      <div style="position:relative; top:5px;">
      <c:set var="ss_history_bar_table_class" value="ss_title_bar_history_bar" scope="request"/>
      <%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
      </div>
    </ssf:param>
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; 
    display:block; position:relative; left:5px;"
    src="<html:rootPath/>js/forum/null.html" height="100" width="100%" 
    onLoad="if (self.ss_setEntryDivHeight) ss_setEntryDivHeight();" frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
</c:if>
</div>

<form class="ss_style ss_form" name="ss_saveEntryHeightForm" id="ss_saveEntryHeightForm" >
<input type="hidden" name="entry_height">
</form>

			  </div>
			</div>
		</div>
	</div>
	<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>

<% // Footer toolbar %>
<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />

</div>
</div>

