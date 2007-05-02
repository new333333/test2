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
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_user.js"></script>

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
		    onClick="parent.ss_findUserSelectItem('${ss_namespace}', this.parentNode);" 
		    href="#"><span style="white-space:nowrap;"><c:out value="${entry.title}"/></span></a></li>
		</c:forEach>
      </ul>
      </td>
      </tr>
      <tr><td colspan="2"><br/></td></tr>
      <tr>
      <td width="100" nowrap="nowrap" style="white-space:nowrap;">
        <c:if test="${ss_pageNumber > 0}">
          <a href="#" onClick="parent.ss_findUserPrevPage('${ss_namespace}');return false;"
          ><ssf:nlt tag="general.Previous"/>...</a>
        </c:if>
        </td>
       <td style="white-space:nowrap;">
        <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
          <a href="#" onClick="parent.ss_findUserNextPage('${ss_namespace}');return false;"
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
	    </td></tr>
        <tr><td align="center">
          <br/>
          <a href="#" onClick="parent.ss_findUserClose('${ss_namespace}');return false;"
          ><ssf:nlt tag="button.close"/></a>
        </td>
        </tr>
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

