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

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.DocumentType" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ListIterator" %>


<jsp:useBean id="wsTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ws2Tree" type="org.dom4j.Document" scope="request" />

<script type="text/javascript">

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
						<ssf:tree treeName="wsTree" treeDocument="<%= wsTree %>" />
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

