<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1>Employees Portlet</h1>

<p>This portlet is an example of a slightly more complicated portlet
that use multiple pages within various modes.  You can view the list
of employees, edit a given employee, delete a given employee, and add 
new employees.</p>

<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
