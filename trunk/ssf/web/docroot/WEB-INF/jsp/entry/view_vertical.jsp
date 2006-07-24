<% //view a folder forum with the entry at the bottom in an iframe %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
int sliderDivHeight = 22;
int sliderDivArrowHeight = 17;    //This is the height of pics/sym_s_arrows_northsouth.gif
int sliderDivBlankHeight = sliderDivHeight - sliderDivArrowHeight;
String sliderDivOffset = "-" + String.valueOf(sliderDivHeight + 20);

//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

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
<div id="ss_showfolder_slider" onMousedown="ss_startDragDiv();" 
 onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}" 
 style="position:relative; margin:0px 0px 3px 0px; padding:0px; 
  width:100%; height:<%= String.valueOf(sliderDivHeight) %>px;
  top:<%= sliderDivOffset %>px;">
  <table class="ss_folder_border" width="100%" 
    style="border: 1px solid black; margin:0px;"
    cellpadding="0" cellspacing="0">
    <tr>
      <td align="center" width="100%">
        <div style="display:inline; 
          background-image:url(<html:imagesPath/>pics/sym_s_arrows_northsouth.gif);
          background-repeat:no-repeat;">
        &nbsp;&nbsp;&nbsp;
        </div>
      </td>
    </tr>
  </table>
  <table width="100%" 
    cellpadding="0" cellspacing="0">
    <tr>
      <td width="1"><img src="<html:imagesPath/>pics/1pix.gif" 
        style="height:<%= String.valueOf(sliderDivBlankHeight) %>px;"></td>
      </td>
    </tr>
  </table>
</div>

<div id="ss_showentrydiv_place_holder" width="100%"
 style="position:relative; margin:0px 0px 3px 0px; padding:0px; 
 top:<%= sliderDivOffset %>px;">

<div id="ss_showentrydiv" class="ss_style ss_portlet" 
  onMouseover="if (self.ss_clearMouseOverInfo) {ss_clearMouseOverInfo(null);}"
  style="margin:0px 0px 0px 2px;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_color" value="${ss_folder_border_color}" />
    <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    <ssf:param name="box_title" useBody="true">
      <div class="ss_folder_border">
      <%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
      </div>
    </ssf:param>
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="400" width="100%" 
    onLoad="ss_setEntryDivHeight()" frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
</div>
</div>
<div id="ss_showfolder_bottom" class="ss_style ss_portlet">&nbsp;</div>

<script type="text/javascript">
var ss_iframe_box_div_name = '<portlet:namespace/>_iframe_box_div';
</script>

<jsp:include page="/WEB-INF/jsp/entry/view_vertical_js.jsp" />

<form class="ss_style ss_form" name="ss_saveEntryHeightForm" id="ss_saveEntryHeightForm" >
<input type="hidden" name="entry_height">
</form>
