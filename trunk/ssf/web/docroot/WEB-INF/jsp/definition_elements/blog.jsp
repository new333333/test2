<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Blog view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />

<script type="text/javascript">
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="subscribe"/></portlet:actionURL>";		
function ss_loadEntry(obj,id) {
	ss_highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

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
function ss_showBlogReplies<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(id, blogNamespace) {
	var targetDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_replies_' + id)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
		} else {
			targetDiv.innerHTML = "<ssf:nlt tag="Loading"/><br/>";
			targetDiv.style.visibility = 'visible';
			targetDiv.style.display = 'block';
			var urlParams = {binderId : "${ssBinder.id}", operation:"show_blog_replies", entryId:id};
			
			if (blogNamespace && blogNamespace != '') {
				urlParams.namespace=blogNamespace
			}
			
			urlParams.rn=rn++;
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
			ss_fetch_url(url, ss_showBlogRepliesCallback<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>, id);
		}
	}
}
function ss_showBlogRepliesCallback<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(s, id) {
	var targetDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_replies_' + id)
	if (targetDiv != null) targetDiv.innerHTML = s;
}

function ss_addBlogReply<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(obj, id) {
	var showRepliesDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_replies_' + id)
	if (showRepliesDiv != null) {
		if (showRepliesDiv.style.visibility == 'visible') {
			//Hide the list of replies
			ss_showBlogReplies<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(id)
		}
	}
	var targetDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_' + id)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
			return
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_iframe_' + id)
	iframeDiv.src = obj.href;
	iframeDiv.style.border = "1px solid #CCCCCC";
}
var ss_replyIframeOffset = 50;
function ss_showBlogReplyIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(obj, id) {
	var targetDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_' + id)
	var iframeDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_iframe_' + id)
	if (window.frames['<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_iframe_' + id] != null) {
		eval("var iframeHeight = parseInt(window.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_iframe_" + id + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_replyIframeOffset + "px"
		}
	}
}
function ss_hideBlogReplyIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(id, count) {
	var targetDiv = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_' + id)
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
	var replyCountObj = document.getElementById('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_reply_count_' + id)
	if (replyCountObj != null) replyCountObj.innerHTML = count;
	ss_showBlogReplies<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(id);
}
</script>


<div class="ss_folder_border">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
 <% // Entry toolbar %>
 <ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</ssf:toolbar>
<div class="ss_clear"></div>
</div>
<%@ include file="/WEB-INF/jsp/forum/page_navigation_bar.jsp" %>
<div class="ss_folder" id="ss_blog_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/description_view.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/blog/blog_folder_listing.jsp" %>
</div>
