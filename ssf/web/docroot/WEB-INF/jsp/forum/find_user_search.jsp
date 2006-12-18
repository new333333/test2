<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <ul id="<c:out value="${ss_divId}"/>">
		<c:if test="${!empty ssUsers}">
		<c:forEach var="entry" items="${ssUsers}">
		  <c:set var="count" value="${count + 1}"/>
		  <li>
		   <a id="<c:out value="ss_findUser_id_${entry._docId}"/>" 
		    href="#" onClick="ss_findUserSelectItem(this);" 
		  ><c:out value="${entry.title}"/></a></li>
		</c:forEach>
	    <li>
	        <div>
	          <c:if test="${ss_pageNumber > 0}">
	            <span style="padding-right:20px;">
	            <a href="#" onClick="ss_findUserPrevPage();return false;">
	              <ssf:nlt tag="general.Previous"/>
	            </a>
	            </span>
	          </c:if>
	          <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
	           <span style="padding-left:40px;">
	           <a href="#" onClick="ss_findUserNextPage();return false;">
	             <ssf:nlt tag="general.Next"/>
	           </a>
	           </span>
	          </c:if>
	        </div>
	    </li>
		</c:if>
		<c:if test="${empty ssUsers}">
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
