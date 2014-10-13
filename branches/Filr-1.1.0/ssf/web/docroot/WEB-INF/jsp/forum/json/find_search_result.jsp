<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ page contentType="text/json-comment-filtered; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%--
* The following line is used to call customer supplied customizations.
* This callout can be used to add info to the hover-over text in the type-to-find search results
* See /WEB-INF/jsp/custom_jsps/samples/ss_call_out_find_search_results.jsp for an example of how to do this
--%><jsp:include page="/WEB-INF/jsp/custom_jsps/ss_call_out_find_search_results.jsp" />
/*
{
<c:choose>
	<c:when test="${ss_ajaxStatus.ss_ajaxNotLoggedIn}">
		notLoggedIn : ${ss_ajaxStatus.ss_ajaxNotLoggedIn} 
	</c:when>
	<c:otherwise>
		<c:set var="count" value="0"/>
		<c:if test="${empty ssEntries}">
			<c:if test="${ssTagLengthWarning != null}">
				'items': [{id: 'error', 
					name: '<ssf:escapeJavaScript value="${ssTagLengthWarning}"/>'}]
			</c:if>
		</c:if>	
		  
		<c:if test="${!empty ssEntries}">
	      'items': [
			<c:forEach var="entry" items="${ssEntries}" varStatus="status">
			  <c:set var="count" value="${count + 1}"/>
			  {'id': '<c:choose><%--
		          --%><c:when test="${!empty entry.ssTag}"><%--
		      		--%><c:out value="${entry.ssTag}"/><%--
		      	  --%></c:when><%--
  		          --%><c:otherwise><%--
  		          	--%><c:out value="${entry._docId}"/><%--
  		          --%></c:otherwise></c:choose>',
  		        'name': '<c:choose><%--
		          --%><c:when test="${!empty entry.ssTag}"><%--
		      		--%><ssf:escapeJavaScript value="${entry.ssTag}"/><%--
		      	  --%></c:when><%--
		          --%><c:when test="${!empty entry._extendedTitle}"><%--
		      		--%><ssf:escapeJavaScript value="${entry._extendedTitle}"/><%--
		      	  --%></c:when><%--		      	  
  		          --%><c:otherwise><%--
  		          	--%><ssf:escapeJavaScript value="${entry.title}"/><%--
  		          	--%><c:if test="${ssShowUserTitleOnly != 'true' && !empty entry._loginName}"> (<ssf:escapeJavaScript value="${entry._loginName}"/>)</c:if><%--
  		          	--%><c:if test="${ssShowFolderTitles == 'true' && !empty entry.binderTitle}"> (<ssf:escapeJavaScript value="${entry.binderTitle}"/>)</c:if><%--
  		          --%></c:otherwise></c:choose>',
  		          'type': '${entry._entityType}',
  		          'title': '<c:choose><%--
		          --%><c:when test="${!empty entry._loginName}"><%--
		      		--%><c:if test="${!empty entry.title}"><ssf:escapeJavaScript value="${entry.title}"/></c:if><%--
		      		--%> (<ssf:escapeJavaScript value="${entry._loginName}"/>)<%--
		      		--%><c:if test="${!empty entry.emailAddress}">, <ssf:escapeJavaScript value="${entry.emailAddress}"/></c:if><%--
		      		--%><c:if test="${!empty entry.phone}">, <ssf:escapeJavaScript value="${entry.phone}"/></c:if><%--
		      		--%><c:if test="${!empty user_elements}"><%--
		      			--%><c:forEach var="user_element" items="${user_elements}"><%--
		      				--%><c:if test="${!empty entry[user_element]}">, <ssf:escapeJavaScript value="${entry[user_element]}"/></c:if><%--
		      			--%></c:forEach><%--
		      		--%></c:if><%--
		      	  --%></c:when><%--
		          --%><c:when test="${!empty entry._entityPath}"><%--
		      		--%><ssf:escapeJavaScript value="${entry._entityPath}"/><%--
		      	  --%></c:when><%--
  		          --%><c:otherwise><%--
  		          --%></c:otherwise></c:choose>'
  		    }<c:if test="${!status.last}">,</c:if>
			</c:forEach>			
	      ],
	      
	      'pageNumber': ${ss_pageNumber},
	      'count': ${count},
	      'pageSize': ${ss_pageSize},
	      'totalHits': ${ss_searchTotalHits},

	      'prevLabel': '<ssf:nlt tag="general.Previous"/>',
	      'nextLabel': '<ssf:nlt tag="general.Next"/>'
		</c:if>
	</c:otherwise>
</c:choose>
}
*/
