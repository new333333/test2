<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1><spring:message code="exception.notAuthorized.title"/></h1>

<p><spring:message code="exception.notAuthorized.message"/><br>

<spring:message code="exception.contactAdmin"/></p>

<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
