<div id="<portlet:namespace/>tags">
<table>
<tr><td>
<span><b><ssf:nlt tag="tags.tag" text="Tags"/>:</b></span>
<c:if test="${!empty ssCommunityTags}">
<b><font color="blue">
<c:forEach var="tag" items="${ssCommunityTags}">
<jsp:useBean id="tag" type="com.sitescape.ef.domain.Tag" />
<c:out value="${tag.name}"/>
</c:forEach>
</c:if>
<c:if test="${empty ssCommunityTags}">
<ssf:nlt tag="tags.none" text="untagged"/>
</c:if>
</font></b>
</td><td>
  <form class="ss_style ss_form" method="post" action="" style="display:inline;">
		  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		  <input type="text" class="ss_text" name="tag">
		  <input type="submit" class="ss_submit" name="changeTags" 
		   value="<ssf:nlt tag="button.ok" text="Add"/>">
   </form>
</td></tr>
</table>

</div>