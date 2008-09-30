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

var rn = Math.round(Math.random()*999999)
function ss_blog_sidebar_date_callback${renderResponse.namespace}() {
	var url = "<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_folder_listing" >
    </ssf:url>";
	var formObj = document.ss_blog_sidebar_date_form${renderResponse.namespace}
	url += "\&year=" + formObj.ss_blog_sidebar_date_year.value;
	url += "\&month=" + formObj.ss_blog_sidebar_date_month.value;
	url += "\&day=" + formObj.ss_blog_sidebar_date_date.value;
	url += "\&rn=" + rn++
	self.location.href = url;
}
function ss_showBlogReplies(blogNamespace, binderId, entryId) {
	var targetDiv = document.getElementById(blogNamespace + 'ss_blog_replies_' + entryId);
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
		} else {
			targetDiv.innerHTML = "<ssf:nlt tag="Loading"/><br/>";
			targetDiv.style.visibility = 'visible';
			targetDiv.style.display = 'block';
			var urlParams = {binderId :binderId, operation:"show_blog_replies", entryId:entryId, namespace:blogNamespace};
						
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
			ss_fetch_div(url, blogNamespace + 'ss_blog_replies_' + entryId, "false");
		}
	}
}

function ss_addBlogReply(obj, blogNamespace, binderId, entryId) {
	var showRepliesDiv = document.getElementById(blogNamespace + 'ss_blog_replies_' + entryId)
	if (showRepliesDiv != null) {
		if (showRepliesDiv.style.visibility == 'visible') {
			//Hide the list of replies
			ss_showBlogReplies(blogNamespace, binderId, entryId)
		}
	}
	var targetDiv = document.getElementById(blogNamespace + 'ss_blog_add_reply_' + entryId)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
			return
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById(blogNamespace + 'ss_blog_add_reply_iframe_' + entryId)
	iframeDiv.src = obj.href;
	iframeDiv.style.border = "1px solid #CCCCCC";
}
var ss_replyIframeOffset = 50;
function ss_showBlogReplyIframe(obj, blogNamespace, binderId, entryId) {
	var targetDiv = document.getElementById(blogNamespace + 'ss_blog_add_reply_' + entryId);
	var iframeDiv = document.getElementById(blogNamespace + 'ss_blog_add_reply_iframe_' + entryId);
	if (window.frames[blogNamespace + 'ss_blog_add_reply_iframe_' + entryId] != null) {
		eval("var iframeHeight = parseInt(window." + blogNamespace + "ss_blog_add_reply_iframe_" + entryId + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_replyIframeOffset + "px"
		}
	}
}
function ss_hideBlogReplyIframe(blogNamespace, binderId, entryId, count) {
	var targetDiv = document.getElementById(blogNamespace + 'ss_blog_add_reply_' + entryId)
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
	var replyCountObj = document.getElementById(blogNamespace + 'ss_blog_reply_count_' + entryId)
	if (replyCountObj != null) replyCountObj.innerHTML = count;
	ss_showBlogReplies(blogNamespace, binderId, entryId);
}
</script>

<div class="ss_folder_border">
<% // Add the toolbar with the navigation widgets, commands  %>
<div class="ss_clear"></div>
</div>
<jsp:include page="/WEB-INF/jsp/forum/add_files_to_folder.jsp" />
<% // Begins blog page %>
<div id="ss_blogContent_wrap" >
<c:set var="currentPage" value="${ssBinder.id}"/>
<table>
	<tbody>
	 <tr>
	   <td width="50%"></td>
	   <td class="ss_blogLeftCol"><img class="ss_blogLeftCol" border="0" alt="" style="height:1px;"
	     src="<html:imagesPath/>pics/1pix.gif"/></td>
	   <td class="ss_blogRightCol"><img class="ss_blogRightCol" border="0" alt="" style="height:1px;"
	     src="<html:imagesPath/>pics/1pix.gif"/></td>
	   <td width="50%"></td>
	 </tr>
	 <tr>
	   <td colspan="4" align="right">
	     <div class="ss_navbar_inline">
	     <ul>
	     <c:forEach var="blogPage" items="${ssBlogPages}">
	       <li>
	         <a class="ss_link_8
	         		<c:if test="${blogPage.id == currentPage}"> ss_navbar_current</c:if>
					<c:if test="${blogPage.id != currentPage}"></c:if>
	         	" 
	         	href="<ssf:url action="view_folder_listing" binderId="${blogPage.id}"><ssf:param 
	         	name="yearMonth" value="${ss_yearMonth}" /></ssf:url>"
	         >${blogPage.title}</a>
	       </li>
	     </c:forEach>
	     </ul>
	     </div>
	   </td>
	 </tr>
	 <tr>
	   <td width="50%">
	   </td>
	   <td class="ss_blogLeftCol" valign="top">
       <!-- Start Left Column -->
      <div class="ss_folder">
<%@ include file="/WEB-INF/jsp/definition_elements/blog/blog_folder_listing.jsp" %>
</div>
       </td><!-- end of left col -->
        <!-- Start Right Column -->
      	<td class="ss_blogRightCol" valign="top">
      	<div id="ss_blogNav_wrap">
<jsp:include page="/WEB-INF/jsp/sidebars/blog.jsp" />
</div>
      	</td><!-- end of Right Column -->
	   <td width="50%">
	   </td>
    </tr>
</tbody>
</table>
</div>
<div class="ss_clear_float"></div>
<c:if test="${!empty ssPageCount && ssPageCount > 1.0}">
<jsp:include page="/WEB-INF/jsp/forum/page_navigation_bar.jsp" />
</c:if>

