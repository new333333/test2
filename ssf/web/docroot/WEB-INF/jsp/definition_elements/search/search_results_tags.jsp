<% // Search results listing of "tags" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<style>
.labelClass {
	font-size: 1.4em !important; 
	font-weight: bold;
}
.firstRating {
	font-size: 2.0em !important; 
}
.secondRating {
	font-size: 1.7em !important; 
}
.thirdRating {
	font-size: 1.4em !important; 
}
.fourthRating {
	font-size: 1.1em !important; 
}
.fifthRating {
	font-size: 0.8em !important; 
}
</style>

<div id="ss_tags_table" style="position:relative; 
 height:400px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">
<table cellspacing="0" cellpadding="0" width="95%" align="center">
 
 <tr>
  <td class="labelClass"><ssf:nlt tag="tags.community"/>:</td>
 </tr>

 <tr><td>&nbsp;</td></tr>
 
 <tr>
  <td>
   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
   	<a href="#" class="${tag.searchResultsRatingCSS}">${tag.ssTag}</a>&nbsp;&nbsp;
   </c:forEach>
  </td>
 </tr>   

 <tr><td>&nbsp;</td></tr>
 <tr><td>&nbsp;</td></tr>
 <tr><td>&nbsp;</td></tr>

 <tr>
  <td class="labelClass"><ssf:nlt tag="tags.personal"/>:</td>
 </tr>

 <tr>
  <td>&nbsp;</td>
 </tr>

 <tr>
  <td>
   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
   	<a href="#" class="${tag.searchResultsRatingCSS}">${tag.ssTag}</a>&nbsp;&nbsp;
   </c:forEach>
  </td>
 </tr>   

</table>
</div>

