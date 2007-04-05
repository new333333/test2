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
<% // Multiple entries by title %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<div class="ss_style ss_portlet">
<span class="ss_entryTitle">${ssEntryTitle}</span>
<br/>
<br/>
<span class="ss_smallprint ss_italic">[<ssf:nlt tag="entry.multipleEntriesByThisTitle"/>]</span>
<br/>
<br/>

<c:forEach var="ssDefinitionEntry" items="${ssFolderEntries}">
  <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_title.jsp" %>
  <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_signature.jsp" %>
</c:forEach>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
