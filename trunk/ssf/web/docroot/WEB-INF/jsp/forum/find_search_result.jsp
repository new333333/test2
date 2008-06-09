<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
		<c:if test="${empty ssEntries}">
			<c:if test="${ssTagLengthWarning != null}">
				<ul>
				<li><c:out value="${ssTagLengthWarning}"/></li>
				</ul>
			</c:if>
		</c:if>	  
		<c:if test="${!empty ssEntries}">
	      <ul>
			<c:forEach var="entry" items="${ssEntries}">
			  <c:set var="count" value="${count + 1}"/>
			  <li id="ss_findUser_id_<c:choose><%--
		          --%><c:when test="${ssFindType == 'personalTags' || ssFindType == 'communityTags' || ssFindType == 'tags' }"><%--
		      		--%><c:out value="${entry.ssTag}"/><%--
		      	  --%></c:when><%--
  		          --%><c:otherwise><%--
  		          	--%><c:out value="${entry._docId}"/><%--
  		          --%></c:otherwise></c:choose>"><a 
			    onClick="<c:choose><%--
		    			--%><c:when test="${ssFindType == 'personalTags' || ssFindType == 'communityTags'}"><%--
		    				--%>ss_putValueInto('ss_findUser_searchText_${ss_namespace}', '${entry.ssTag}');<%--
		    			--%></c:when><%--
						--%><c:otherwise><%--
							--%>window['${findObjectName}']._selectItem(this.parentNode, '${entry._entityType}');return false;<%--
						--%></c:otherwise><%--
			   		 --%></c:choose>" 
			    href="javascript: ;"><span style="white-space:nowrap;"><c:choose><%--
		          --%><c:when test="${!empty entry.ssTag}"><%--
		      		--%><c:out value="${entry.ssTag}"/><%--
		      	  --%></c:when><%--
		          --%><c:when test="${!empty entry._extendedTitle}"><%--
		      		--%><c:out value="${entry._extendedTitle}"/><%--
		      	  --%></c:when><%--		      	  
  		          --%><c:otherwise><%--
  		          	--%><c:out value="${entry.title}"/><%--
  		          --%></c:otherwise></c:choose></span></a></li>
			</c:forEach>			
	      </ul>
          <c:if test="${ss_searchTotalHits > ss_pageSize}">
			<table class="ss_typeToFindNav" cellpadding="0" cellspacing="0" border="0"><tbody>
			<tr><td width="10%">
            <c:if test="${ss_pageNumber > 0}">
              <a href="javascript:;" onClick="window['${findObjectName}'].prevPage();return false;"
              ><img border="0" style="margin-right: 20px;" title="<ssf:nlt tag="general.Previous"/>" src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a>
             </c:if>
             </td><td width="80%"></td><td width="10%">
            <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
              <a href="javascript: ;" onClick="window['${findObjectName}'].nextPage();return false;"
              ><img border="0" style="margin-left: 20px;" title="<ssf:nlt tag="general.Next"/>" src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
            </c:if>
           </td></tr></tbody></table>
           </c:if>
		</c:if>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>
