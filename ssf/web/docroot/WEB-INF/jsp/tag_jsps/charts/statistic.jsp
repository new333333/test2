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
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.lang.Boolean" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%
	Map percentStatistic = (Map) request.getAttribute("percentStatistic"); 
	String statisticLabel = (String) request.getAttribute("statisticLabel"); 
	Boolean showLabel = (Boolean) request.getAttribute("showLabel");
	Boolean labelAll = (Boolean) request.getAttribute("labelAll");
	Boolean showLegend = (Boolean) request.getAttribute("showLegend");
	String barStyle = (String) request.getAttribute("barStyle"); 
%>
<c:set var="percentStatistic" value="<%= percentStatistic%>" scope="request"/>
<c:set var="showLegend" value="<%= showLegend%>" scope="request"/>
<c:set var="showLabel" value="<%= showLabel%>" scope="request"/>
<c:set var="barStyle" value="<%= barStyle%>" scope="request"/>
<c:set var="labelAll" value="<%= labelAll%>" scope="request"/>

<c:if test="${!empty percentStatistic}">
	
	<c:if test="${showLabel}">
		<h5 class="ss_statisticLabel"><ssf:nlt tag="<%=statisticLabel%>"/><c:if test="${labelAll}"> (<ssf:nlt tag="alt.viewAll"/>)</c:if>:</h5>
	</c:if>
	
	<table class="ss_statisticContainer ${barStyle}"><tr>
		<c:forEach var="singleValue" items="${percentStatistic}" varStatus="status">
			<c:if test="${singleValue.value.percent > 0}">
				<td class="ss_statisticBar statistic${status.index mod 8}" style="width:${singleValue.value.percent}%;" title="<ssf:nlt tag="${singleValue.value.label}" /> - ${singleValue.value.percent}% (${singleValue.value.value} <ssf:nlt tag="statistic.unity" />)"><span>${singleValue.value.percent}%</span></td>
			</c:if>
		</c:forEach>
	</tr></table>
	
	<c:if test="${showLegend}">
	<div class="${barStyle}">
		<ul class="ss_statisticLegend">
		<c:forEach var="singleValue" items="${percentStatistic}" varStatus="status">
			<li><div class="statistic${status.index mod 8} ss_statisticLegend">&nbsp;</div>
			  <ssf:nlt tag="general.a.colon.b"><ssf:param name="value" 
			    useBody="true"><ssf:nlt tag="${singleValue.value.label}" /></ssf:param><ssf:param
			    name="value" useBody="true">${singleValue.value.percent}% (${singleValue.value.value} 
			    <c:choose><c:when test="${singleValue.value.value == 1}"><ssf:nlt tag="statistic.unity" /></c:when>
			    <c:otherwise><ssf:nlt tag="statistic.unity.plural" /></c:otherwise>
			    </c:choose>)</ssf:param></ssf:nlt>
			  <div class="ss_clear_float"></div></li>
		</c:forEach>
		</ul>
	</div>
	</c:if>

</c:if>