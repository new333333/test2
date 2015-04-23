<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div>
	<c:choose>
		<c:when test="${(allDayEvent && startString == endString) || !hasDuration}">
			<div><ssf:nlt tag="calendar.allDay" />&nbsp;${startString}</div>
		</c:when>
		<c:when test="${(allDayEvent && startString != endString)}">
		    <div><ssf:nlt tag="calendar.start" text="Start"/> ${startString}&nbsp;&nbsp;
		    (<ssf:nlt tag="calendar.allDay"/><c:if test="${durationDays > 1}">&nbsp;<ssf:nlt tag="event.duration"><ssf:param 
		    name="value" value="${durationDays}"/></ssf:nlt></c:if>)</div>
		    <div class="margintop1"><ssf:nlt tag="calendar.end" text="End"/> ${endString}</div>			
		</c:when>
		<c:otherwise>
		    <div><ssf:nlt tag="calendar.start" text="Start"/>&nbsp;&nbsp;${startString}</div>
		    <div class="margintop1"><ssf:nlt tag="calendar.end" text="End"/>&nbsp;&nbsp;${endString}</div>			
		</c:otherwise>
	</c:choose>
	<c:if test="${!allDayEvent && durationDaysOnly > 0}">
		<nobr><ssf:nlt tag="calendar.duration" text="Duration"/> ${durationDaysOnly}&nbsp;<ssf:nlt tag="calendar.duration.hint" text="(in days)"/></nobr>
	</c:if>
    <c:if test="${!empty repeatString}"><ssf:nlt tag="calendar.frequency" text="Frequency"/> ${repeatString}</c:if>
</div>


