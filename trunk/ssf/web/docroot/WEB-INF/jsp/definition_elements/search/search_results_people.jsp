<% // Search results listing of "people" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_people_table" style="position:relative; 
 height:400px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">

<table width="100%">
 <th align="left"><ssf:nlt tag="search.UserNames"/>
   <br>
   <span class="ss_normal ss_finestprint"><ssf:nlt tag="search.Found"/>: ${ssPeopleResultsCount}</span>
 </th>
 <th align="left"><ssf:nlt tag="search.Contributors"/></th>
 <tr>
   <td width="50%">
     <table>
       <c:forEach var="user" items="${ssPeopleResults}">
         <tr><td>${user.title}</td></tr>
       </c:forEach>
     </table>
   </td>
   <td width="50%">
     <c:forEach var="user" items="${ssFolderEntryPeople}">
       <table>
         <tr><td>${user.title}</td></tr>
       </table>
     </c:forEach>
   </td>
 </tr>
</table>

</div>

