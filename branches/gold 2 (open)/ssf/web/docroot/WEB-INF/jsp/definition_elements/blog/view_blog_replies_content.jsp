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
<% // View blog replies %>
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssEntry.topEntry}" >
<c:if test="${!empty ssFolderEntryDescendants}">
<div class="ss_replies">
<c:forEach var="reply" items="${ssFolderEntryDescendants}">
<jsp:useBean id="reply" type="com.sitescape.team.domain.Entry" />
 <div>
<c:if test="${!empty reply.entryDef}">
 	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
		configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryBlogView']") %>" 
		configJspStyle="view" 
		processThisItem="false" 
		entry="<%= reply %>" />
</c:if>
<c:if test="${empty reply.entryDef}">
 	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
		configElement="${ssConfigElement}" 
		configJspStyle="view" 
		processThisItem="false" 
		entry="<%= reply %>" />
</c:if>
 
 </div>
 <div class="ss_divider"></div>
</c:forEach>
</div>
</c:if>
</c:if>
