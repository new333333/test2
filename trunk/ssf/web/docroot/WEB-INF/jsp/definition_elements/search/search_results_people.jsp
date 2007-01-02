<% // Search results listing of "people" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_people_table" style="position:relative; 
 height:400px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">

<table cellspacing="0" cellpadding="0">
  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Rating"/> </th>
  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Contributors"/></th>

 <c:forEach var="user" items="${ssFolderEntryPeople}">
 <tr>
 	<td style="padding-left:20px;"><img class="${user.searchResultsRatingCSS}" src="<html:imagesPath/>pics/sym_m_star.gif"/></td>
 	<td style="padding-left:20px;">
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_ws_listing"/><portlet:param 
	  	name="binderId" value="${user.ssUser.parentBinder.id}"/><portlet:param 
	  	name="entryId" value="${user.ssUser.id}"/><portlet:param 
	  	name="newTab" value="1"/></portlet:renderURL>">
	  	 <c:out value="${user.ssUser.title}"/>
	  </a>
 	</td>
 </tr>
</c:forEach>
</table>

</div>