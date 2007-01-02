<% // Search results listing of "tags" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div id="ss_tags_table" style="position:relative; 
 height:400px; overflow:scroll; 
 margin:2px; border: #666666 1px solid;">
<table cellspacing="0" cellpadding="0" width="95%" align="center">
 
 <tr>
  <td class="ss_bold ss_largerprint"><ssf:nlt tag="tags.community"/>:</td>
 </tr>

 <tr><td>&nbsp;</td></tr>
 
 <tr>
  <td>
   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
   
   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="search"/><portlet:param 
				name="searchText" value="${tag.ssTagSearchText}"/><portlet:param 
				name="searchBtn" value="searchBtn"/></portlet:actionURL>" class="${tag.searchResultsRatingCSS}">${tag.ssTagSign}</a>
   
   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="search"/><portlet:param 
				name="searchText" value="${tag.ssTag}"/><portlet:param 
				name="searchBtn" value="searchBtn"/></portlet:actionURL>" class="${tag.searchResultsRatingCSS}">${tag.ssTag}</a>&nbsp;&nbsp;
   	
   </c:forEach>
  </td>
 </tr>   

 <tr><td>&nbsp;</td></tr>
 <tr><td>&nbsp;</td></tr>
 <tr><td>&nbsp;</td></tr>

 <tr>
  <td class="ss_bold ss_largerprint"><ssf:nlt tag="tags.personal"/>:</td>
 </tr>

 <tr>
  <td>&nbsp;</td>
 </tr>

 <tr>
  <td>
   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">

   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="search"/><portlet:param 
				name="searchText" value="${tag.ssTagSearchText}"/><portlet:param 
				name="searchBtn" value="searchBtn"/></portlet:actionURL>" class="${tag.searchResultsRatingCSS}">+</a>   
   
   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="search"/><portlet:param 
				name="searchText" value="${tag.ssTag}"/><portlet:param 
				name="searchBtn" value="searchBtn"/></portlet:actionURL>" class="${tag.searchResultsRatingCSS}">${tag.ssTag}</a>&nbsp;&nbsp;
				
   </c:forEach>
  </td>
 </tr>   

</table>
</div>