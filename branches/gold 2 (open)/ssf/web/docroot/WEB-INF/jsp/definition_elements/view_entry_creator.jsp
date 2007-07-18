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
<% //Entry creator view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent ss_entrySignature">
  <c:out value="${property_caption}" />
<c:if test="${!empty ssDefinitionEntry.creation.principal}">
  <c:if test="${ssConfigJspStyle != 'mail'}">
    <ssf:showUser user="${ssDefinitionEntry.creation.principal}"/>
  </c:if>
  <c:if test="${ssConfigJspStyle == 'mail'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ssDefinitionEntry.creation.principal.parentBinder.id}"
	    entryId="${ssDefinitionEntry.creation.principal.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"><c:out value="${ssDefinitionEntry.creation.principal.title}"/></a>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.postedBy}">
    (<ssf:nlt tag="entry.postedBy"/>&nbsp;<c:out value="${ssDefinitionEntry.postedBy}"/>)
  </c:if>
</c:if>
</div>
