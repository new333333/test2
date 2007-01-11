<% //Entry creator view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent ss_entrySignature">
  <c:out value="${property_caption}" />
<c:if test="${ssConfigJspStyle != 'mail'}">
  <ssf:presenceInfo user="${ssDefinitionEntry.creation.principal}" showTitle="true"/>
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
</div>
