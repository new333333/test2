<%@ include file="/WEB-INF/jsp/common/include.jsp" %>


SEARCH RESULT PAGE

<br/>SEARCH QUERY: <c:out value="${query}" />
<br/>SEARCH OPTIONS: <c:out value="${options}" />

<br/>SEARCH TEXT: <c:out value="${searchText}" />
<br/>SEARCH AUTHORS: <c:out value="${options.authors}" />
<br/>SEARCH TAGS: <c:out value="${options.tags}" /> 

<br/><br/>SEARCH RESULTS: <c:out value="${results}" />
