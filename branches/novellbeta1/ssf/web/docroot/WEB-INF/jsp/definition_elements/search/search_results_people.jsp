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