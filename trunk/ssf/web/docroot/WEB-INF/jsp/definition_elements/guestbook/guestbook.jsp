<% // Guestbook view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript">

var ss_signGuestbookIframeOffset = 50;
function ss_showSignGuestbookIframe<portlet:namespace/>(obj) {
	var targetDiv = document.getElementById('<portlet:namespace/>_add_entry_from_iframe');
	var iframeDiv = document.getElementById('<portlet:namespace/>_new_guestbook_entry_iframe');
	if (window.frames['<portlet:namespace/>_new_guestbook_entry_iframe'] != null) {
		eval("var iframeHeight = parseInt(window.<portlet:namespace/>_new_guestbook_entry_iframe.document.body.scrollHeight);");
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_signGuestbookIframeOffset + "px"
		}
	}
}
function ss_signGuestbook<portlet:namespace/>(obj) {

	var targetDiv = document.getElementById('<portlet:namespace/>_add_entry_from_iframe');
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden';
			targetDiv.style.display = 'none';
			return;
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById('<portlet:namespace/>_new_guestbook_entry_iframe');
	iframeDiv.src = obj.href;
}

function ss_hideAddEntryIframe<portlet:namespace/>() {
	var targetDiv = document.getElementById('<portlet:namespace/>_add_entry_from_iframe');
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
}

</script>
<div class="folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
</div>
<br>
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
<ssf:toolbar style="ss_actions_bar">

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar" item="true" />
</c:if>

</ssf:toolbar>

</div>
</div>
<div class="folder" id="ss_guestbook_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/guestbook/guestbook_folder_listing.jsp" %>
</div>
