<% // Search results listing of "places" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<style>
.firstRating {
	opacity: 1;
	filter: alpha(opacity=100);
}
.secondRating {
	opacity: .8;
	filter: alpha(opacity=90);
}
.thirdRating {
	opacity: .7;
	filter: alpha(opacity=80);
}
.fourthRating {
	opacity: .6;
	filter: alpha(opacity=70);
}
.fifthRating {
	opacity: .4;
	filter: alpha(opacity=40);
}
</style>

<div id="ss_places_table" style="position:relative; 
 height:400px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">
<table cellspacing="0" cellpadding="0">

  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Rating"/> </th>
  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Folders"/></th>
  
   <c:forEach var="place" items="${ssFolderEntryPlaces}">
     <tr>
		<td style="padding-left:20px;">
		  <img class="${place.searchResultsRatingCSS}" src="<html:imagesPath/>pics/sym_m_star.gif"/>
		</td>
        <td style="padding-left:20px;">
         <a href="<portlet:renderURL>
		  <portlet:param name="action" value="view_folder_listing"/>
		  <portlet:param name="binderId" value="${place.ssBinder.id}"/>
		  <portlet:param name="binderType" value="${place.ssBinder.entityIdentifier.entityType}"/>
		  </portlet:renderURL>">${place.ssBinder.title}
		 </a>
	    </td>
     </tr>
   </c:forEach>
</table>
</div>

