<% // Blog view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />

<script type="text/javascript">
function ss_loadEntry(obj,id) {
	ss_highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
</div>
<br>
<script type="text/javascript">
var rn = Math.round(Math.random()*999999)
function ss_blog_sidebar_date_callback() {
	var url = "<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_folder_listing" >
    </ssf:url>";
	var formObj = document.ss_blog_sidebar_date_form
	url += "\&year=" + formObj.ss_blog_sidebar_date_year.value;
	url += "\&month=" + formObj.ss_blog_sidebar_date_month.value;
	url += "\&day=" + formObj.ss_blog_sidebar_date_date.value;
	url += "\&rn=" + rn++
	self.location.href = url;
}
function ss_showBlogReplies<portlet:namespace/>(id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_replies_' + id)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
		} else {
			targetDiv.innerHTML = "<ssf:nlt tag="Loading"/><br/>";
			targetDiv.style.visibility = 'visible';
			targetDiv.style.display = 'block';
			url = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
				<ssf:param name="binderId" value="${ssBinder.id}" />
				<ssf:param name="operation" value="show_blog_replies" />
		    	</ssf:url>"
			url += "\&entryId=" + id
			url += "\&rn=" + rn++
			ss_fetch_url(url, ss_showBlogRepliesCallback<portlet:namespace/>, id);
		}
	}
}
function ss_showBlogRepliesCallback<portlet:namespace/>(s, id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_replies_' + id)
	if (targetDiv != null) targetDiv.innerHTML = s;
}
function ss_addBlogReply<portlet:namespace/>(obj, id) {
	var showRepliesDiv = document.getElementById('<portlet:namespace/>ss_blog_replies_' + id)
	if (showRepliesDiv != null) {
		if (showRepliesDiv.style.visibility == 'visible') {
			//Hide the list of replies
			ss_showBlogReplies<portlet:namespace/>(id)
		}
	}
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_' + id)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
			return
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_iframe_' + id)
	iframeDiv.src = obj.href;
}
var ss_replyIframeOffset = 50;
function ss_showBlogReplyIframe<portlet:namespace/>(obj, id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_' + id)
	var iframeDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_iframe_' + id)
	if (window.frames['<portlet:namespace/>ss_blog_add_reply_iframe_' + id] != null) {
		eval("var iframeHeight = parseInt(window.<portlet:namespace/>ss_blog_add_reply_iframe_" + id + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_replyIframeOffset + "px"
		}
	}
}
function ss_hideBlogReplyIframe<portlet:namespace/>(id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_' + id)
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
	ss_showBlogReplies<portlet:namespace/>(id);
}
</script>

<div style="margin:0px;">
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
<div class="folder" id="ss_blog_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/blog/blog_folder_listing.jsp" %>
</div>
