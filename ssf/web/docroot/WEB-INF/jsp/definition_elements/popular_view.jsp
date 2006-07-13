<div id="<portlet:namespace/>popular">
<table>
<tr><td>
<span><b><ssf:nlt tag="popularity.rating.average" text="Average Rating"/>:</b></span>
<c:if test="${!empty ssDefinitionEntry.rating}">
<c:out value="${ssDefinitionEntry.rating}"/>
</c:if>
<c:if test="${empty ssDefinitionEntry.rating}">
<ssf:nlt tag="popularity.rating.none" text="--no rating--"/>
</c:if>
</td><td>
  <form class="ss_style ss_form" method="post" action="" style="display:inline;">
		  <input type="hidden" name="replyId" value="${ssDefinitionEntry.id}">
		  <select name="rating">
		    <option value="1">1</option>
		    <option value="2">2</option>
		    <option value="3">3</option>
		    <option value="4">4</option>
		    <option value="5">5</option>
		  </select>
		  <input type="submit" class="ss_submit" name="changeRatingBtn" 
		   value="<ssf:nlt tag="button.ok" text="OK"/>">
   </form>
</td></tr>
<tr><td><span><b><ssf:nlt tag="popularity.visits" text="Visits"/>:</b></span>
<c:if test="${!empty ssDefinitionEntry.popularity}">
<c:out value="${ssDefinitionEntry.popularity}"/>
</c:if>
<c:if test="${empty ssDefinitionEntry.popularity}">
<ssf:nlt tag="popularity.visits.none" text="--no visits--"/>
</c:if>
</td></tr></table>

</div>