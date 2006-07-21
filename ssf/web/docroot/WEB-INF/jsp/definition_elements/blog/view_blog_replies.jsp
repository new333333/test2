<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript">
var rn = Math.round(Math.random()*999999)
function ss_showBlogReplies(id) {
	url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="show_blog_replies" />
    	</ssf:url>"
	url += "\&rn=" + rn++
	fetch_url(url, ss_showBlogRepliesCallback, id);
}
function ss_showBlogRepliesCallback(s, id) {
	var targetDiv = document.getElementById('ss_blog_replies_' + id)
	if (targetDiv != null) targetDiv.innerHTML = s;
}
</script>

<div>
<a href="javascript: ;" onClick="ss_showBlogReplies('${ssDefinitionEntry.id}');return false;">
<span class="ss_bold"><ssf:nlt tag="blog.replyCount"/>: 
  ${ssDefinitionEntry.totalReplyCount}</span>
</a>
</div>
<div id="ss_blog_replies_${ssDefinitionEntry.id}"></div>
