<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <ul id="<c:out value="${ss_divId}"/>">
		<c:if test="${!empty ssEntries}">
		<c:forEach var="entry" items="${ssEntries}">
		  <c:set var="count" value="${count + 1}"/>
		  <li id="<c:out value="ss_findPlaces_id_${entry._docId}"/>" 
		    onClick="ss_findPlacesSelectItem(this);" 
		  ><c:out value="${entry.title}"/></li>
		</c:forEach>
	    <li>
	        <div>
	          <c:if test="${ss_pageNumber > 0}">
	            <span style="padding-right:20px;">
	            <a href="#" onClick="ss_findPlacesPrevPage();return false;">
	              <ssf:nlt tag="general.Previous"/>
	            </a>
	            </span>
	          </c:if>
	          <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
	           <span style="padding-left:40px;">
	            <a href="#" onClick="ss_findPlacesNextPage();return false;">
	             <ssf:nlt tag="general.Next"/>
	            </a>
	           </span>
	          </c:if>
	        </div>
	    </li>
		</c:if>
		<c:if test="${empty ssEntries}">
		  <li>
		    <table cellspacing="0" cellpadding="0"><tr><td nowrap="nowrap">
		      <ssf:nlt tag="findUser.noneFound"/>
		    </td></tr></table>
		  </li>
		</c:if>
	  </ul>
	</taconite-replace>
</c:if>
</taconite-root>
