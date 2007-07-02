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
<c:if test="${empty ss_portletInitialization}">
<div class="ss_portlet_style ss_portlet">
<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td>
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
			    <span><b>Element tests</b></span>
			    <br>
				<a href="<portlet:renderURL windowState="maximized"><portlet:param 
					name="action" value="tree" /></portlet:renderURL>">Tree test</a><br>
				<a href="<portlet:actionURL windowState="maximized"><portlet:param 
					name="action" value="time" /></portlet:actionURL>">Datepicker and timepicker test</a><br>
				<a href="<portlet:renderURL windowState="maximized"><portlet:param 
					name="action" value="htmledit" /></portlet:renderURL>">Html Editor test</a><br>
				<a href="<portlet:actionURL windowState="maximized"><portlet:param 
					name="action" value="event" /></portlet:actionURL>">Event tester</a>
				<br>
				<br><span><b>Servlet tests</b></span>
				<br>
				<a href="<portlet:renderURL windowState="maximized"><portlet:param 
					name="action" value="fragment" /></portlet:renderURL>">Fragment test</a>
				<br>
				<br><span><b>General tests</b></span>
				<br>
				<a href="<portlet:actionURL windowState="normal"><portlet:param 
					name="action" value="flush" /><portlet:param 
					name="operation" value="null" /></portlet:actionURL>">Flush cache</a>
				
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>
</div>

</c:if>
