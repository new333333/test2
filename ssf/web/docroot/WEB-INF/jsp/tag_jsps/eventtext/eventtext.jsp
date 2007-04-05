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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<span>
<c:if test="${hasDuration == 'yes'}">
    <ssf:nlt tag="calendar.start" text="Start"/>: ${startString}<br />
    <ssf:nlt tag="calendar.end" text="End"/>: ${endString}<br />
</c:if>
<c:if test="${hasDuration == 'no'}">
    <ssf:nlt tag="calendar.when" text="When"/>: ${startString}<br />
</c:if>
    <ssf:nlt tag="calendar.frequency" text="Frequency"/>: ${freqString} ${onString} ${untilString}
</span>


