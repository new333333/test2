<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // Timeline view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
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
<div style="margin:0px;">
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
  
  	<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
	
		<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true">
			<c:set var="ss_history_bar_table_class" value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
			<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
		</ssf:toolbar>
		
		<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" >
			<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
		</ssf:toolbar>
	
	</ssf:toolbar>

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

