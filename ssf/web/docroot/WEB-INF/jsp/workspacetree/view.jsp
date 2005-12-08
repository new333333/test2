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


<jsp:useBean id="wsDomTree" type="org.dom4j.Document" scope="request" />

<table width="100%">
	<tr>
		<td>
		</td>
	</tr>
	<tr>
		<td align="left">
			<div>
			<c:choose>
			<c:when test="${renderRequest.windowState == 'normal'}">
				<ssf:tree treeName="wsTree" treeDocument="<%= wsDomTree %>"  rootOpen="false" />
			</c:when>
			<c:when test="${renderRequest.windowState == 'maximized'}">
				<ssf:tree treeName="wsTree" treeDocument="<%= wsDomTree %>"  rootOpen="true" />
			</c:when>
			</c:choose>			

			</div>
		</td>
	</tr>
</table>
