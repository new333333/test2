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
	  <ssf:showUser user="${user.ssUser}" /> 
 	</td>
 </tr>
</c:forEach>
</table>

<br/>
<br/>
<br/>

</div>