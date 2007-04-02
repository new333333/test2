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
<%@ page import="org.dom4j.Element" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
changes
<table class="ss_style" cellpadding="10" width="100%"><tr><td>
<c:forEach var="change" items="${changeLogs}">
	<c:set var="rootElement" value="${change.document.rootElement}" />
	<jsp:useBean id="rootElement" type="org.dom4j.Element" />
	<%
		String text = "";
		Element description = (Element) rootElement.selectSingleNode("//folderEntry/attribute[@name='description']");
		if (description != null) {
			text = description.getText();
		}
	%>
	<%= text %><br/>
</c:forEach>
</td></tr></table>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
