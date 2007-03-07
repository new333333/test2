<% // Search results listing of "people" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_people_table" class="ss_search_results_pane">

<table cellspacing="0" cellpadding="0">
  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Rating"/> </th>
  <th align="left" style="padding-left:20px;"><ssf:nlt tag="search.Contributors"/></th>

 <c:forEach var="user" items="${ssFolderEntryPeople}">
 <tr>
 	<td style="padding-left:20px;"><img class="${user.searchResultsRatingCSS}" src="<html:imagesPath/>pics/sym_m_star.gif"/></td>
 	<td style="padding-left:20px;">
	  <ssf:showUser user="${user.ssUser}" /> 
 	</td>
 </tr>
</c:forEach>
</table>
</div>