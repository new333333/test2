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
<jsp:useBean id="ssAdminDomTree" type="org.dom4j.Document" scope="request" />
  <div class="ss_portlet_style ss_portlet">
	<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					<div>
						<ssf:tree treeName="ssAdminDomTree" treeDocument="<%= ssAdminDomTree %>" rootOpen="false" />
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
	<br/>
  </div>
