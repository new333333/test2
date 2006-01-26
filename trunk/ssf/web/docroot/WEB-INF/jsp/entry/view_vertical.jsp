<% //view a folder forum with the entry at the bottom in an iframe %>
<%
String iframeBoxId = renderResponse.getNamespace() + "_iframe_box_div";
%>
<a name="ss_top_of_folder"></a>
<div id="ss_showfolder" class="ss_style ss_portlet">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="ss_showentrydiv" style="position:absolute; visibility:hidden; x:0; y:0;
  width:600; height:80%; display:none; z-index:50;">
  <ssf:box>
    <ssf:param name="box_id" value="<%= iframeBoxId %>" />
    <ssf:param name="box_width" value="400" />
    <ssf:param name="box_title" useBody="true">
<div style="margin:0px; background-color: #cecece; border:solid #cccccc 1px;">
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</div>
    </ssf:param>
    <ssf:param name="box_show_close_icon" value="true" />
    <ssf:param name="box_show_close_routine" value="ss_hideEntryDiv()" />
  <iframe id="ss_showentryframe" name="ss_showentryframe" style="width:100%; display:block;"
    src="<html:rootPath/>js/forum/null.html" height="95%" width="100%" 
    frameBorder="no" >xxx</iframe>
  </ssf:box>
</div>
<div id="ss_showfolder_scroll_link" class="ss_style ss_portlet" style="display:none; visibility:hidden;">
<br>
<br>
<a href="#ss_top_of_folder" onClick="smoothScrollInTime(0 , 0, 8);return false;">
<ssf:nlt tag="folder.scrollToTop" text="Scroll back to the top..."/>
</a>
<br>
</div>
<div id="ss_showfolder_bottom" class="ss_style ss_portlet">
</div>

<script type="text/javascript">
var ss_entryWindowWidth = <%= ss_entryWindowWidth %>;
var ss_minEntryWindowWidth = 200;
var ss_scrollbarWidth = 25;
var ss_entryDivTopDelta = 25;
var ss_entryDivBottomDelta = 50;
var ss_scrollTopOffset = 4;

function ss_showForumEntryInIframe(url) {
	ss_positionEntryDiv();
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')

    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";

    if (wObj.src && wObj.src == url) {
    	wObj.src = "_blank";
    }
    wObj.style.height = parseInt(wObj1.style.height) - ss_entryDivBottomDelta + "px";
    wObj.src = url

    //Scroll to the bottom of the window
	smoothScrollInTime(0 , parseInt(ss_getWindowHeight()), 8)

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_positionEntryDiv() {
    var wObjSL = self.document.getElementById('ss_showfolder_scroll_link')
    wObjSL.style.display = "block";
    wObjSL.style.visibility = "visible";

    var wObj = self.document.getElementById('ss_showfolder')

	var marginLeft = 2
	var marginRight = 2
	
    var width = parseInt(parseInt(ss_getObjectWidth(wObj)) - marginLeft - marginRight);
    ss_entryWindowWidth = parseInt(width);

    var wObj1 = self.document.getElementById('ss_showentrydiv')
    var wObj2 = self.document.getElementById('<portlet:namespace/>_iframe_box_div')
    var wObj3 = self.document.getElementById('ss_showentryframe')

    var top = parseInt(getDivTop('ss_showfolder_bottom'));
    var left = parseInt(parseInt(getDivLeft('ss_showfolder')) + marginLeft);
    var height = parseInt(ss_getWindowHeight() - ss_entryDivBottomDelta);
    setObjectTop(wObj1, top)
    setObjectLeft(wObj1, left);
    setObjectWidth(wObj1, ss_entryWindowWidth);
    setObjectWidth(wObj2, ss_entryWindowWidth);
    //setObjectWidth(wObj3, ss_entryWindowWidth);
    setObjectHeight(wObj1, height);
    wObj1.style.background = "#ffffff"
    wObj1.style.display = "block";
    wObj1.style.visibility = "visible";
}

function ss_hideEntryDiv() {
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    wObj1.style.display = "none";
    wObj1.style.visibility = "hidden";
    var wObjSL = self.document.getElementById('ss_showfolder_scroll_link')
    wObjSL.style.display = "none";
    wObjSL.style.visibility = "hidden";
}

function ss_repositionEntryDiv() {
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    if (wObj1.style.visibility == "visible") {
    	//The entry div is visible, so reposition it to the new size
    	ss_positionEntryDiv();
    }
}

//createOnLoadObj('ss_positionEntryDiv', ss_positionEntryDiv)
createOnResizeObj('ss_repositionEntryDiv', ss_repositionEntryDiv)

</script>
<form class="ss_style" name="ss_saveEntryWidthForm" id="ss_saveEntryWidthForm" >
<input type="hidden" name="entry_width">
</form>
<div id="ss_entry_width_status_message" style="visibility:hidden; display:none;"></div>
