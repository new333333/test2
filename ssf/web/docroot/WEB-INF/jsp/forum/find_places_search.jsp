<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <div id="<c:out value="${ss_divId}"/>">
		<c:if test="${!empty ssEntries}">
          <table cellspacing="0" cellpadding="0">
          <tr>
          <td colspan="2">
	      <ul>
		    <c:forEach var="entry" items="${ssEntries}">
		      <c:set var="count" value="${count + 1}"/>
		        <li><div style="display:inline; white-space:nowrap;">
		          <a href="#" id="<c:out value="ss_findPlaces_id_${entry._docId}"/>" 
		            onClick="ss_findPlacesSelectItem(this);" 
		          ><c:out value="${entry.title}"/></a></div></li>
		    </c:forEach>
	      </ul>
	      </td>
	      </tr>
	      <tr><td colspan="2"><br/></td></tr>
	      <tr>
	      <td width="150">
            <c:if test="${ss_pageNumber > 0}">
              <a href="#" onClick="ss_findPlacesPrevPage();return false;">
                <ssf:nlt tag="general.Previous"/>...
              </a>
            </c:if>
            </td>
           <td>
            <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
              <a href="#" onClick="ss_findPlacesNextPage();return false;">
               <ssf:nlt tag="general.Next"/>...
              </a>
            </c:if>
           </td>
           </tr>
           </table>
		</c:if>
		<c:if test="${empty ssEntries}">
		 <ul>
		  <li>
		    <table cellspacing="0" cellpadding="0"><tr><td nowrap="nowrap">
		      <ssf:nlt tag="findUser.noneFound"/>
		    </td></tr></table>
		  </li>
		 </ul>
		</c:if>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>
