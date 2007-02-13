<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssDefinitionEntry.topEntry}" >

<c:set var="ssPersonalTags" value="${ssBlogEntries[ss_blog_docId].ssPersonalTags}" scope="request"/>
<c:set var="ssCommunityTags" value="${ssBlogEntries[ss_blog_docId].ssCommunityTags}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
<br/>


<div style="padding-bottom: 2px;">

<c:if test="${!empty ss_blog_reply_url}">
<a href="${ss_blog_reply_url}" 
  onClick="ss_addBlogReply<portlet:namespace/>(this, '${ssDefinitionEntry.id}');return false;">
<div class="ss_iconed_label ss_add_comment"><ssf:nlt tag="blog.addComment"/></div>
</a>
</c:if>

<a href="javascript: ;" onClick="ss_showBlogReplies<portlet:namespace/>('${ssDefinitionEntry.id}');return false;">
<div class="ss_iconed_label ss_view_something">
<ssf:nlt tag="blog.viewComments"/> [<span id="<portlet:namespace/>ss_blog_reply_count_${ssDefinitionEntry.id}">${ssDefinitionEntry.totalReplyCount}</span>]
</div>
</a>

</div>


<div id="<portlet:namespace/>ss_blog_replies_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;"></div>
<div id="<portlet:namespace/>ss_blog_add_reply_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;">
<iframe 
  id="<portlet:namespace/>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  name="<portlet:namespace/>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  onLoad="ss_showBlogReplyIframe<portlet:namespace/>(this, '${ssDefinitionEntry.id}');" 
  width="100%">xxx</iframe>
</div>
</c:if>
