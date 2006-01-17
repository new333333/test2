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

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.DocumentType" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ListIterator" %>


<jsp:useBean id="wsTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ws2Tree" type="org.dom4j.Document" scope="request" />

<script language="javascript">

function t_<portlet:namespace/>_wsTree_showId(id, obj) {
	alert('You selected "'+obj.innerText+' (id: '+id+') from the first widget.')
	return false
}

function t_<portlet:namespace/>_ws2Tree_showId(id, obj) {
	alert('You selected "'+obj.innerText+' (id: '+id+') from the second widget.')
	return false
}

</script>

<span align="left">SiteScape Forum Widget Tester -  Tree Widget</span>
<br />
<br />

	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					First tree:
					<div>
						<ssf:tree treeName="wsTree" treeDocument="<%= wsTree %>" sortable="true" />
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
	<br>
	<br>
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					Second tree:
					<div>
						<c:if test="true">
						<ssf:tree treeName="ws2Tree" treeDocument="<%= ws2Tree %>" rootOpen="false" />
						</c:if>
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
<br />
<br />
End of page

