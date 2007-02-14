<% // No entry by title %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<ssf:ifadapter>
<body>
</ssf:ifadapter>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<div class="ss_style ss_portlet">
<c:forEach var="title" items="${ssAddEntryTitles}">
  <a href="${ssAddEntryUrls[title.value]}">${title.key}</a><br/>
</c:forEach>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
