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
<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<script type="text/javascript">
//Define the variables needed by the javascript routines
var ss_iframe_box_div_name = '<portlet:namespace/>_iframe_box_div';

<c:if test="${!empty ss_entryWindowTop && !empty ss_entryWindowLeft}">
var ss_entryWindowTopOriginal = ${ss_entryWindowTop};
var ss_entryWindowTop = ${ss_entryWindowTop};
var ss_entryWindowLeft = ${ss_entryWindowLeft};
</c:if>
<c:if test="${empty ss_entryWindowTop || empty ss_entryWindowLeft}">
var ss_entryWindowTopOriginal = -1;
var ss_entryWindowTop = -1;
var ss_entryWindowLeft = -1;
</c:if>

var ss_saveEntryWidthUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_entry_width" />
	</ssf:url>"

var ss_forumRefreshUrl = "<html:rootPath/>js/forum/refresh.html";
var ss_entryWindowWidth = ${ss_entryWindowWidth};
var ss_entryBackgroundColor = "${ss_style_background_color}";
</script>
<script type="text/javascript" src="<html:rootPath/>js/forum/view_iframe.js"></script>

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Tabs %>
<%@ include file="/WEB-INF/jsp/definition_elements/tabbar.jsp" %>

<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">
				
<% // Folder toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<div class="ss_content_inner">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar"/>
</div>
</c:if>

<div class="ss_content_inner">
<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<br/>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>
			</div>
		</div>
	</div>
	<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>

<% // Footer toolbar %>
<%@ include file="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" %>

</div>

<div id="ss_showentrydiv" onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="position:absolute; visibility:hidden;
  width:600; height:80%; display:none;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_color" value="${ss_entry_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
      <div style="position:relative; top:5px;">
      <c:set var="ss_history_bar_table_class" value="ss_title_bar_history_bar" scope="request"/>
      <%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
      </div>
    </ssf:param>
    <ssf:param name="box_show_resize_icon" value="true" />
    <ssf:param name="box_show_resize_routine" value="ss_startDragDiv('resize')" />
    <ssf:param name="box_show_resize_gif" value="box/resize_east_west.gif" />
    <ssf:param name="box_show_move_icon" value="true" />
    <ssf:param name="box_show_move_routine" value="ss_startDragDiv('move')" />
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; 
    display:block; position:relative; left:5px;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    onLoad="if (self.ss_setEntryDivHeight) ss_setEntryDivHeight();" frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>

<form class="ss_style ss_form" name="ss_saveEntryWidthForm" id="ss_saveEntryWidthForm" >
<input type="hidden" name="entry_width">
<input type="hidden" name="entry_top">
<input type="hidden" name="entry_left">
</form>
