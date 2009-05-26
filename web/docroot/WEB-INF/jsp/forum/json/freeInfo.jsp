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
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%
	pageContext.setAttribute("tentative", org.kablink.teaming.domain.Event.FreeBusyType.tentative);
	pageContext.setAttribute("busy", org.kablink.teaming.domain.Event.FreeBusyType.busy);
	pageContext.setAttribute("outOfOffice", org.kablink.teaming.domain.Event.FreeBusyType.outOfOffice);	
%>
<% // This is JSON type AJAX response  %>
{<%--
	--%>'dateTimeFormat': 'iso8601',<%--
	--%>'events' : [<%--
		--%><c:forEach var="freeBusyInfo" items="${ssCalendarFreeBusyInfo}" varStatus="status"><%--
			--%><c:forEach var="userEvents" items="${freeBusyInfo.value}" varStatus="statusUserEvents"><%--
				--%><c:forEach var="event" items="${userEvents.value}" varStatus="statusEvents"><%--			
					--%>{<%--
					--%><c:choose><%--
						--%><c:when test="${event.allDay}"><%--
							--%>'start': '<fmt:formatDate value="${event.start}" pattern="yyyyMMdd"/>',<%--
					        --%>'end': '<fmt:formatDate value="${event.end}" pattern="yyyyMMdd"/>',<%--
						--%></c:when><%--
						--%><c:otherwise><%--
						  	--%><c:set var="timeZone" value="${ssUser.timeZone.ID}"/><%--
						  	--%><c:if test="${!event.timeZoneSensitive}"><%--
						  		--%><c:set var="timeZone" value="GMT"/><%--
						  	--%></c:if><%--						
							--%>'start': '<fmt:formatDate value="${event.start}" timeZone="${timeZone}" pattern="yyyyMMdd'T'HHmm"/>',<%--
					        --%>'end': '<fmt:formatDate value="${event.end}" timeZone="${timeZone}" pattern="yyyyMMdd'T'HHmm"/>',<%--						
						--%></c:otherwise><%--
			        --%></c:choose><%--
			        --%>'title': '',<%--
					--%>'top': '${status.index * 2 + 0.5}',<%--
					--%>'color': <%--
						--%><c:choose><%--
							--%><c:when test="${userEvents.key == outOfOffice}"><%--
								--%>'#FFCC00'<%--
							--%></c:when><%--
							--%><c:when test="${userEvents.key == tentative}"><%--
								--%>'#FF3300'<%--
							--%></c:when><%--
							--%><c:when test="${userEvents.key == busy}"><%--
								--%>'#990000'<%--
							--%></c:when><%--
						--%></c:choose><%--			
			        --%>}<c:if test="${!status.last || !statusUserEvents.last || !statusEvents.last}">,</c:if><%--
		        --%></c:forEach><%--		        
	        --%></c:forEach><%--
		--%></c:forEach><%--
	--%>]<%--
--%>}