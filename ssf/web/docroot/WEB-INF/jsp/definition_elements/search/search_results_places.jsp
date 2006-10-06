<% // Search results listing of "places" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_places_table" style="position:relative; 
 height:400px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">
<table>
  <th align="left"><ssf:nlt tag="search.References"/></th>
  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Folders"/></th>
   <c:forEach var="place" items="${ssFolderEntryPlaces}">
     <tr>
       <td style="padding-left:20px;">(${place.searchResultsCount})</td>
       <td style="padding-left:20px;"><a href="<portlet:renderURL>
				  <portlet:param name="action" value="view_folder_listing"/>
				  <portlet:param name="binderId" value="${place.ssBinder.id}"/>
				  <portlet:param name="binderType" value="${place.ssBinder.entityIdentifier.entityType}"/>
				  </portlet:renderURL>">${place.ssBinder.title}</a></td>
     </tr>
   </c:forEach>
</table>
</div>

