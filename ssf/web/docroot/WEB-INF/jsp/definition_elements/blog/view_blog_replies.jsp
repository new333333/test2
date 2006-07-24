<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssDefinitionEntry.topEntry}" >
<c:if test="${ssDefinitionEntry.totalReplyCount > 0}">
<div>
<a href="javascript: ;" onClick="ss_showBlogReplies('${ssDefinitionEntry.id}');return false;">
<span class="ss_bold"><ssf:nlt tag="blog.replyCount"/>: 
  ${ssDefinitionEntry.totalReplyCount}</span>
</a>
</div>
<div id="ss_blog_replies_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;"></div>
</c:if>
<c:if test="${ssDefinitionEntry.totalReplyCount <= 0}">
<div>
<span class="ss_bold"><ssf:nlt tag="blog.replyCount"/>: 
  ${ssDefinitionEntry.totalReplyCount}</span>
</div>
</c:if>
</c:if>
