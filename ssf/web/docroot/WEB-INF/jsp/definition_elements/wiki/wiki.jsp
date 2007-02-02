<% // Wiki view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript">
var ss_columnCount = 0;
function ss_loadEntry(obj,id) {
	ss_highlightLineById('folderLine_' + id);
	var iframeDiv = document.getElementById('ss_wikiIframe<portlet:namespace/>')
	iframeDiv.src = obj.href;
	return false;
}
var ss_wikiIframeOffset = 20;
function ss_setWikiIframeSize<portlet:namespace/>() {
	var targetDiv = document.getElementById('ss_wikiEntryDiv<portlet:namespace/>')
	var iframeDiv = document.getElementById('ss_wikiIframe<portlet:namespace/>')
	if (window.frames['ss_wikiIframe<portlet:namespace/>'] != null) {
		eval("var iframeHeight = parseInt(window.ss_wikiIframe<portlet:namespace/>.document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_wikiIframeOffset + "px"
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
<ssf:toolbar style="ss_actions_bar">

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar" item="true" />
</c:if>

<ssf:toolbar style="ss_actions_bar" item="true" >
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</ssf:toolbar>

</ssf:toolbar>

</div>
</div>
<div class="folder" id="ss_wiki_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_listing.jsp" %>
</div>
