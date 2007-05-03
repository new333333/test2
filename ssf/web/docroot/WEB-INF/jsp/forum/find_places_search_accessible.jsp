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
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body>

  <c:set var="count" value="0"/>
  <div id="<c:out value="${ss_divId}"/>" style="padding:2px;margin:2px;">
	<c:if test="${!empty ssEntries}">
      <table cellspacing="0" cellpadding="0">
      <tbody>
      <tr>
      <td colspan="2">
      <ul>
	    <c:forEach var="entry" items="${ssEntries}">
	      <c:set var="count" value="${count + 1}"/>
	      <li id="<c:out value="ss_findPlaces_id_${entry._docId}"/>"><a 
	          href="#" onClick="ss_findPlacesSelectItem${ss_namespace}(this.parentNode, '${entry._entityType}');" 
	          ><span style="white-space:nowrap;"><c:out value="${entry._extendedTitle}"/></span></a></li>
	    </c:forEach>
      </ul>
      </td>
      </tr>
      <tr><td colspan="2"><br/></td></tr>
      <tr>
      <td width="150" nowrap="nowrap" style="white-space:nowrap;">
        <c:if test="${ss_pageNumber > 0}">
          <a href="#" onClick="parent.ss_findPlacesPrevPage${ss_namespace}();return false;"
          ><ssf:nlt tag="general.Previous"/>...</a>
        </c:if>
        </td>
       <td nowrap="nowrap" style="white-space:nowrap;">
        <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
          <a href="#" onClick="parent.ss_findPlacesNextPage${ss_namespace}();return false;"
          ><ssf:nlt tag="general.Next"/>...</a>
        </c:if>
       </td>
       </tr>
      </tbody>
      </table>
	</c:if>
	<c:if test="${empty ssEntries}">
	 <ul>
	  <li>
	    <table cellspacing="0" cellpadding="0"><tbody><tr>
	    <td nowrap="nowrap" style="white-space:nowrap;">
	      <ssf:nlt tag="findUser.noneFound"/>
	    </td></tr>
	    </tbody></table>
	  </li>
	 </ul>
	</c:if>
  </div>

<script type="text/javascript">
self.window.focus();
</script>

</body>
</html>
  