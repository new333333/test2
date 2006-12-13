<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${listDivId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <ul id="<c:out value="${listDivId}"/>">
		<c:if test="${!empty ssUsers}">
		<c:forEach var="entry" items="${ssUsers}">
		  <c:set var="count" value="${count + 1}"/>
		  <li id="<c:out value="ss_findUser_id_${entry._docId}"/>" 
		    onClick="ss_findUserSelectItem(this);" 
		  ><c:out value="${entry.title}"/></li>
		</c:forEach>
	    <c:if test="${count < ss_searchTotalHits}">
	    <li>
	      <div align="right">
	        <table cellspacing="0" cellpadding="0"><tr><td nowrap="nowrap">
	          <a href="#" onClick="ss_findUserNextPage();return false;">
	            <ssf:nlt tag="general.more"/>
	          </a>
	        </td></tr></table>
	      </div>
	    </li>
	  </c:if>
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
