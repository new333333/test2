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

<table class="ss_style" border="0" cellpadding="4">
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
			<a href="<ssf:servletrooturl/>downloadFile?file=C:\liferay-portal-pro-3.6.1-tomcat\RUNNING.txt">Download TEXT File (using pure servlet)</a>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<a href="<portletadapter:renderURL>
			         <portletadapter:param name="action" value="downloadFile"/>
			         <portletadapter:param name="file" value="C:\liferay-portal-pro-3.6.1-tomcat\conf\web.xml"/>
			         </portletadapter:renderURL>" onClick="return popup(this, 'notes')">Download XML File (using portlet adapter)</a>
		</td>
	</tr>
	<tr>
		<td colspan="4">
			<a href="<portletadapter:renderURL>
			         <portletadapter:param name="action" value="showXml"/>
			         </portletadapter:renderURL>" onClick="return popup(this, 'notes')">XML rendered by JSP (using portlet adapter - doesn't work)</a>
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
		<applet archive="sample/ssf-sample-applet.jar" code="com.sitescape.team.applets.sample.HelloWorld" codebase="<html:rootPath/>/applets" height="30" width="130">
		</applet>
	</td>
	</tr>
	<!--<tr>
		<td colspan="4">
			<a href="${forumUrl}">Forum Portlet URL</a>
		</td>
	</tr>-->
		
</table>