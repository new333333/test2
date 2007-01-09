<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssDefinitionEntry.topEntry}" >
<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td valign="top">
<c:if test="${ssDefinitionEntry.totalReplyCount > 0}">
<div>
<a href="#" onClick="ss_showBlogReplies<portlet:namespace/>('${ssDefinitionEntry.id}');return false;">
<span class="ss_bold"><ssf:nlt tag="blog.replyCount"/>: 
  ${ssDefinitionEntry.totalReplyCount}</span>
</a>
</div>
<div id="<portlet:namespace/>ss_blog_replies_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;"></div>
</c:if>
<c:if test="${ssDefinitionEntry.totalReplyCount <= 0}">
<div>
<span class="ss_bold"><ssf:nlt tag="blog.replyCount"/>: 
  ${ssDefinitionEntry.totalReplyCount}</span>
</div>
</c:if>
</td>
<td align="right" valign="top">
<c:if test="${!empty ss_blog_reply_url}">
<a href="${ss_blog_reply_url}" 
  onClick="ss_addBlogReply<portlet:namespace/>(this, '${ssDefinitionEntry.id}');return false;">
<span class="ss_bold"><ssf:nlt tag="blog.addReply"/></span>
</a>
</c:if>
</td>
</tr>
</table>
<div id="<portlet:namespace/>ss_blog_add_reply_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;">
<iframe 
  id="<portlet:namespace/>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  name="<portlet:namespace/>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  onLoad="ss_showBlogReplyIframe<portlet:namespace/>(this, '${ssDefinitionEntry.id}');" 
  width="100%">xxx</iframe>
</div>
</c:if>
