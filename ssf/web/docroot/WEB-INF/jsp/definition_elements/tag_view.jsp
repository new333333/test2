<div align="right">
<table>
<tr><td align="right">
<span><b><ssf:nlt tag="tags.tag" text="Community Tags"/>:</b></span>
<c:if test="${!empty ssCommunityTags}">
<b><font color="blue">
<c:forEach var="tag" items="${ssCommunityTags}">
<jsp:useBean id="tag" type="com.sitescape.ef.domain.Tag" />
<c:out value="${tag.name}"/>
</c:forEach>
</c:if>
<c:if test="${empty ssCommunityTags}">
<ssf:nlt tag="tags.none" text="--none--"/>
</c:if>
</font></b>
</td><td rowspan="2" >
  <form class="ss_style ss_form" method="post" action="" style="display:inline;">
		  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		  <table><tr><td>
		  <input type="text" class="ss_text" name="tag">
		  </td><td rowspan=2>
		  <input TYPE="radio" VALUE="Community" NAME="scope" CHECKED>   Community<br>
   	      <input TYPE="radio" VALUE="Personal" NAME="scope">   Personal<br>
   	      </td></tr>
   	      <tr><td>
		  <input type="submit" class="ss_submit" name="changeTags" 
		   value="<ssf:nlt tag="button.add" text="Add"/>">
		   </td></tr></table>
   </form>
</td></tr>
<tr><td>
<span><b><ssf:nlt tag="tags.tag" text="Personal Tags"/>:</b></span>
<c:if test="${!empty ssPersonalTags}">
<b><font color="blue">
<c:forEach var="ptag" items="${ssPersonalTags}">
<jsp:useBean id="ptag" type="com.sitescape.ef.domain.Tag" />
<c:out value="${ptag.name}"/>
</c:forEach>
</c:if>
<c:if test="${empty ssPersonalTags}">
<ssf:nlt tag="tags.none" text="--none--"/>
</c:if>
</font></b>
</td></tr></table>

</div>