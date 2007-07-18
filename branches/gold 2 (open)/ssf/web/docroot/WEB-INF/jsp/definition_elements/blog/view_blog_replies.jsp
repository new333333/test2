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
<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssDefinitionEntry.topEntry}" >

<c:set var="ssPersonalTags" value="${ssBlogEntries[ss_blog_docId].ssPersonalTags}" scope="request"/>
<c:set var="ssCommunityTags" value="${ssBlogEntries[ss_blog_docId].ssCommunityTags}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
<br/>


<div style="padding-bottom: 14px;">

<c:if test="${!empty ss_blog_reply_url}">
<a href="${ss_blog_reply_url}" 
  onClick="ss_addBlogReply<portlet:namespace/>(this, '${ssDefinitionEntry.id}');return false;"
  <ssf:title tag="title.add.comment" />
  >
<div class="ss_iconed_label ss_add_comment"><ssf:nlt tag="blog.addComment"/></div>
</a>
</c:if>

<a href="javascript: ;" onClick="ss_showBlogReplies<portlet:namespace/>('${ssDefinitionEntry.id}', '<portlet:namespace/>');return false;"
<ssf:title tag="title.view.comments">
	<ssf:param name="value" value="${ssDefinitionEntry.totalReplyCount}" />
</ssf:title>
>
<div class="ss_iconed_label ss_view_something">
<ssf:nlt tag="blog.viewComments"/> [<span id="<portlet:namespace/>ss_blog_reply_count_${ssDefinitionEntry.id}">${ssDefinitionEntry.totalReplyCount}</span>]
</div>
</a>

</div>


<div id="<portlet:namespace/>ss_blog_replies_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;"></div>
<div id="<portlet:namespace/>ss_blog_add_reply_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;">
<iframe <ssf:title tag="title.add.reply" />
  id="<portlet:namespace/>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  name="<portlet:namespace/>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  onLoad="if (parent.ss_showBlogReplyIframe<portlet:namespace/>) parent.ss_showBlogReplyIframe<portlet:namespace/>(this, '${ssDefinitionEntry.id}');" 
  width="100%" frameBorder="0">xxx</iframe>
</div>
</c:if>