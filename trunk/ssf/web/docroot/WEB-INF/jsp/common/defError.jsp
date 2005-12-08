<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1><spring:message code="exception.generalError.title"/></h1>

<p>${exception.localizedMessage == null ? exception : exception.localizedMessage }<br/>
<spring:message code="exception.contactAdmin"/></p>

<p>${exception.class}</p>

<p><% ((Exception)request.getAttribute("exception")).printStackTrace(); %>

<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
