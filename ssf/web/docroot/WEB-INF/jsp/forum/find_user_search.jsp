<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <div id="<c:out value="${ss_divId}"/>" style="padding:2px;margin:2px;">
		<c:if test="${!empty ssUsers}">
          <table cellspacing="0" cellpadding="0">
          <tbody>
          <tr>
          <td colspan="2">
	      <ul>
			<c:forEach var="entry" items="${ssUsers}">
			  <c:set var="count" value="${count + 1}"/>
			  <li id="<c:out value="ss_findUser_id_${entry._docId}"/>"><a 
			    onClick="ss_findUserSelectItem(this.parentNode);" 
			    href="#"><span style="white-space:nowrap;"><c:out value="${entry.title}"/></span></a></li>
			</c:forEach>
	      </ul>
	      </td>
	      </tr>
	      <tr><td colspan="2"><br/></td></tr>
	      <tr>
	      <td width="100" nowrap="nowrap" style="white-space:nowrap;">
            <c:if test="${ss_pageNumber > 0}">
              <a href="#" onClick="ss_findUserPrevPage();return false;"
              ><ssf:nlt tag="general.Previous"/>...</a>
            </c:if>
            </td>
           <td style="white-space:nowrap;">
            <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
              <a href="#" onClick="ss_findUserNextPage();return false;"
              ><ssf:nlt tag="general.Next"/>...</a>
            </c:if>
           </td>
           </tr>
           </tbody>
           </table>
		</c:if>
		<c:if test="${empty ssUsers}">
		 <ul>
		  <li>
		    <table cellspacing="0" cellpadding="0"><tbody><tr>
		    <td nowrap="nowrap" style="white-space:nowrap;">
		      <ssf:nlt tag="findUser.noneFound"/>
		    </td></tr></tbody></table>
		  </li>
		 </ul>
		</c:if>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>
