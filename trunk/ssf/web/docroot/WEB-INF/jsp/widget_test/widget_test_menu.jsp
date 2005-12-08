<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr>
	<td align="center">
		<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td valign="top">
				<font class="portlet-section-body" style="font-size:100%">
				<a href="<portlet:renderURL windowState="maximized">
						<portlet:param name="action" value="tree" />
					</portlet:renderURL>">Tree test</a><br>
				<a href="<portlet:renderURL windowState="maximized">
						<portlet:param name="action" value="time" />
					</portlet:renderURL>">Datepicker and timepicker test</a><br>
				<a href="<portlet:renderURL windowState="maximized">
						<portlet:param name="action" value="htmledit" />
					</portlet:renderURL>">Html Editor test</a><br>
				<a href="<portlet:renderURL windowState="maximized">
						<portlet:param name="action" value="event" />
					</portlet:renderURL>">Event tester</a><br>
				</font>
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>

