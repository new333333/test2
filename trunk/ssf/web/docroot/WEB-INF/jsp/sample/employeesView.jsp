<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<SCRIPT TYPE="text/javascript">
<!--
function popup(mylink, windowname)
{
if (! window.focus)return true;
var href;
if (typeof(mylink) == 'string')
   href=mylink;
else
   href=mylink.href;
window.open(href, windowname, 'width=400,height=350,scrollbars=yes');
return false;
}
//-->
</SCRIPT>

<h1>SiteScape Employees</h1>

<table border="0" cellpadding="4">
	<tr><th>First Name</th><th>Last Name</th><th>Salary</th><th></th></tr>
	<c:forEach items="${employees}" var="employee">
		<tr>
			<td>${employee.firstName}</td>
			<td>${employee.lastName}</td>
			<td align="right">${employee.salary}</td>
			<td>
				<a href="<portlet:actionURL>
						<portlet:param name="action" value="incrementSalary"/>
						<portlet:param name="employee" value="${employee.key}"/>
						<portlet:param name="increment" value="1000"/>
					</portlet:actionURL>"><img title="Increase Salary" src="<html:imagesPath/>sample/increase.png" border=0 /></a>
				<a href="<portlet:actionURL>
						<portlet:param name="action" value="incrementSalary"/>
						<portlet:param name="employee" value="${employee.key}"/>
						<portlet:param name="increment" value="-1000"/>
					</portlet:actionURL>"><img title="Decrease Salary" src="<html:imagesPath/>sample/decrease.png" border=0 /></a>
				<a href="<portlet:renderURL>
						<portlet:param name="action" value="editEmployee"/>
						<portlet:param name="employee" value="${employee.key}"/>
					</portlet:renderURL>"><img title="Edit Employee Details" src="<html:imagesPath/>sample/edit.png" border=0 /></a>
				<a href="<portlet:actionURL>
						<portlet:param name="action" value="deleteEmployee"/>
						<portlet:param name="employee" value="${employee.key}"/>
					</portlet:actionURL>"><img title="Delete Employee" src="<html:imagesPath/>sample/delete.png" border=0 /></a>
			</td>
		</tr>
	</c:forEach>
	<tr>
		<td colspan="4">
			<a href="<portlet:renderURL>
					<portlet:param name="action" value="editEmployee"/>
				</portlet:renderURL>"><img title="Add New Employee" src="<html:imagesPath/>sample/new.png" border=0 /> Add New Employee</a>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<a href="<ssf:servletrooturl/>downloadFile?file=C:\liferay-portal-pro-3.5.0-tomcat\RUNNING.txt">Download File (using pure servlet)</a>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<a href="<ssf:servletrooturl/>uploadFile" onClick="return popup(this, 'notes')">Upload File (using pure servlet)</a>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<a href="<portletadapter:renderURL>
			         <portletadapter:param name="action" value="uploadFile"/>
			         </portletadapter:renderURL>" onClick="return popup(this, 'notes')">Upload File (using portlet adapter)</a>
		</td>
	</tr>
	<tr>
	<td align="center">
		<applet archive="sample/sample.jar" code="com.sitescape.ef.applets.sample.HelloWorld" codebase="<html:rootPath/>/applets" height="30" width="130">
		</applet>
	</td>
</tr>
		
</table>