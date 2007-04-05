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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>


<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <div id="<c:out value="${ss_divId}"/>" style="padding:2px;margin:2px;">
		<c:if test="${!empty ss_tags}">
          <table cellspacing="0" cellpadding="0">
          <tbody>
          <tr>
          <td colspan="2">
	      <ul>
	      	
		    <c:forEach var="tag" items="${ss_tags}">
		      <c:set var="count" value="${count + 1}"/>

		      	<c:set var="onClickVal" value="ss_findTagSelectItem('${ss_namespace}',this.parentNode);" />
		      	<c:if test="${ss_userGroupType == 'personalTags' || ss_userGroupType == 'communityTags' }">
		      		<c:set var="onClickVal" value="ss_putValueInto('ss_findTag_searchText_${ss_namespace}', '${tag.ssTag}');" />
		      	</c:if>
		      
		      <li id="<c:out value="ss_findTag_id_${tag.ssTag}"/>"><a 
		          href="#" onClick="
		          <c:if test="${ss_userGroupType == 'personalTags' || ss_userGroupType == 'communityTags' }">
		      		ss_putValueInto('ss_findTag_searchText_${ss_namespace}', '${tag.ssTag}');
		      	  </c:if>
  		          <c:if test="${ss_userGroupType != 'personalTags' && ss_userGroupType != 'communityTags' }">
  		          	ss_findTagSelectItem('${ss_namespace}', this.parentNode);
  		          </c:if>"><span style="white-space:nowrap;"><c:out value="${tag.ssTag}"/></span></a></li>
		    </c:forEach>
	      </ul>
	      </td>
	      </tr>
	      <tr><td colspan="2"><br/></td></tr>
	      <tr>
	      <td width="150" nowrap="nowrap" style="white-space:nowrap;">
            <c:if test="${ss_pageNumber > 0}">
              <a href="#" onClick="ss_findTagPrevPage('${ss_namespace}');return false;"
              ><ssf:nlt tag="general.Previous"/>...</a>
            </c:if>
            </td>
           <td nowrap="nowrap" style="white-space:nowrap;">
            <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
              <a href="#" onClick="ss_findTagNextPage('${ss_namespace}');return false;"
              ><ssf:nlt tag="general.Next"/>...</a>
            </c:if>
           </td>
           </tr>
	       <tr><td width="150" align="center">
             <a href="#" onClick="ss_findTagClose('${ss_namespace}');return false;"
             ><ssf:nlt tag="button.close"/></a>
	       </td><td></td>
	       </tr>
           </tbody>
           </table>
		</c:if>
		<c:if test="${empty ss_tags}">
		 <ul>
		  <li>
		    <table cellspacing="0" cellpadding="0"><tbody><tr>
		    <td nowrap="nowrap" style="white-space:nowrap;">
		      <ssf:nlt tag="findUser.noneFound"/>
		    </td></tr>
	        <tr><td align="center">
              <br/>
              <a href="#" onClick="ss_findTagClose('${ss_namespace}');return false;"
              ><ssf:nlt tag="button.close"/></a>
	        </td>
	        </tr>
		    </tbody></table>
		  </li>
		 </ul>
		</c:if>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>
