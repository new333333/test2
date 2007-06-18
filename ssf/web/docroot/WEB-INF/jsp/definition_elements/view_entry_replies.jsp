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
<%@ page import="com.sitescape.team.ObjectKeys" %>
<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="surveyVoteDefinitionId" value="<%= ObjectKeys.DEFAULT_ENTRY_SURVEY_VOTE_CONFIG  %>" />

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ssEntry == ssDefinitionEntry}" >
  <c:if test="${!empty ssFolderEntryDescendants}">

<div class="ss_entryContent" style="padding-top:15px;">
<c:if test="${!empty property_caption}">
<span class="ss_largerprint ss_bold ss_replies_header"><c:out value="${property_caption}"/></span>
<br/>
</c:if>

<c:set var="noReplies" value="true" />

<c:forEach var="reply" items="${ssFolderEntryDescendants}">
  <jsp:useBean id="reply" type="com.sitescape.team.domain.Entry" />
  
	<c:choose>
		<c:when test="${reply.entryDef.id == surveyVoteDefinitionId}">
			<% // skip survey vote %>
		</c:when>
		<c:otherwise>
			<c:set var="noReplies" value="false" />
			  <div class="ss_replies">
	
			  <c:if test="${!empty reply.entryDef}">
			 	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
					configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView' or @name='profileEntryView' or @name='fileEntryView']") %>" 
					configJspStyle="${ssConfigJspStyle}" 
					processThisItem="false" 
					entry="<%= reply %>" />
			  </c:if>
			  <c:if test="${empty reply.entryDef}">
			 	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
					configElement="${ssConfigElement}" 
					configJspStyle="${ssConfigJspStyle}" 
					processThisItem="false" 
					entry="<%= reply %>" />
			  </c:if>
			 
			  </div>
		</c:otherwise>
	</c:choose>
</c:forEach>

<c:if test="${noReplies}">
	<style>
		.ss_replies_header {
			display: none;
		}
	</style>
</c:if>

</div>
  </c:if>
</c:if>
