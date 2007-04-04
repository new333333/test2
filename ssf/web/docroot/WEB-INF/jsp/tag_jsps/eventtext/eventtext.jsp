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


