<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<h1>${employee.key == null ? 'Add New Employee' : 'Edit Employee Details'}</h1>

<form class="ss_style" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="editEmployee"/>
			<portlet:param name="employee" value="${employee.key}"/>
		</portlet:actionURL>">
	<table class="ss_style" border="0" cellpadding="4">
		<tr>
			<th>First Name</th>
			<td><html:inputText object="employee.firstName" size="30" maxlength="80"/></td>
		</tr>
		<tr>
			<th>Last Name</th>
			<td><html:inputText object="employee.lastName" size="30" maxlength="80"/></td>
		</tr>
		<tr>
			<th>Salary</th>
			<td><html:inputText object="employee.salary" size="30" maxlength="30"/></td>
		</tr>
		<tr>
			<th colspan="2">
				<button type="submit">${employee.key == null ? 'Add' : 'Save'}</button>
			</th>
		</tr>
	</table>
</form>

<spring:hasBindErrors name="employee">
	<p style="color:#A00000">Please fix all errors!</p>
</spring:hasBindErrors>

<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
