<% // Timeline view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String displayStyle = ssUser.getDisplayStyle();
	if (displayStyle == null) displayStyle = "";
	
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
<script src="http://simile.mit.edu.nyud.net:8080/timeline/api/scripts/timeline-api.js" type="text/javascript"></script>
<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle %>";
</script>

<div class="ss_folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>

<div style="margin:0px;">
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
<table cellspacing="0" cellpadding="0" width="95%">
<tr><td align="left">
<% // Then include the navigation widgets for this view %>
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

<div id="ss_timeline" style="height: 150px; border: 1px solid #aaa"></div>

<script type="text/javascript">
var tl;
function ss_timeline_onLoad() {
  var eventSource = new Timeline.DefaultEventSource();
  var bandInfos = [
    Timeline.createBandInfo({
        eventSource:    eventSource,
        date:           "Jun 28 2006 00:00:00 GMT",
        width:          "70%", 
        intervalUnit:   Timeline.DateTime.MONTH, 
        intervalPixels: 100
    }),
    Timeline.createBandInfo({
        eventSource:    eventSource,
        date:           "Jun 28 2006 00:00:00 GMT",
        width:          "30%", 
        intervalUnit:   Timeline.DateTime.YEAR, 
        intervalPixels: 200
    })
  ];
  bandInfos[1].syncWith = 0;
  bandInfos[1].highlight = true;
  bandInfos[1].eventPainter.setLayout(bandInfos[0].eventPainter.getLayout());
  
  var tl = Timeline.create(document.getElementById("ss_timeline"), bandInfos);
  Timeline.loadXML("http://mrbig/forum/example1.xml", function(xml, url) { eventSource.loadXML(xml, url); });
}

var resizeTimerID = null;
function ss_timeline_onResize() {
    if (resizeTimerID == null) {
        resizeTimerID = window.setTimeout(function() {
            resizeTimerID = null;
            tl.layout();
        }, 500);
    }
}

ss_createOnLoadObj('ss_timeline_onLoad', ss_timeline_onLoad);
ss_createOnResizeObj('ss_timeline_onResize', ss_timeline_onResize);

</script>

<c:forEach var="entry1" items="${ssFolderEntries}" >
</c:forEach>

</div>

