<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1><spring:message code="exception.codedError.title"/></h1>

<p>${exception.localizedMessage }<br/></p>

<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
