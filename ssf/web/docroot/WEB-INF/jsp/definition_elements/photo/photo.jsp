<% // Photo album folder view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript">
var ss_columnCount = 0;
function ss_loadEntry(obj,id) {
	ss_highlightLineById('folderLine_' + id);
	var iframeDiv = document.getElementById('ss_photoIframe<portlet:namespace/>')
	iframeDiv.src = obj.href;
	return false;
}
var ss_photoIframeOffset = 20;
function ss_setPhotoIframeSize<portlet:namespace/>() {
	var targetDiv = document.getElementById('ss_photoEntryDiv<portlet:namespace/>')
	var iframeDiv = document.getElementById('ss_photoIframe<portlet:namespace/>')
	if (window.frames['ss_photoIframe<portlet:namespace/>'] != null) {
		eval("var iframeHeight = parseInt(window.ss_photoIframe<portlet:namespace/>.document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_photoIframeOffset + "px"
		}
	}
}

</script>
<div style="margin:0px;">

<div align="right" style="margin:0px 4px 0px 0px;">
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">
	<tr>
		<td align="left" width="55%">
<%@ include file="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" %>
		</td>
		<td align="right" width="20%">
			&nbsp;
		</td>
	</tr>
</table>
</div>

<div class="ss_folder_border" style="position:relative; top:2; margin:0px; padding:2px 0px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">

<% // Entry toolbar %>
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />

<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" >
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</ssf:toolbar>

</ssf:toolbar>

</div>
</div>
<div class="folder" id="ss_photo_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/photo/photo_folder_listing.jsp" %>
</div>
