<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // Search results listing of "places" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_places_table" class="ss_search_results_pane">
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
		  <portlet:param name="binderType" value="${place.ssBinder.entityType}"/>
		  </portlet:renderURL>">${place.ssBinder.title}
		 </a>
	    </td>
     </tr>
   </c:forEach>
</table>
</div>

