<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ssEntry == ssDefinitionEntry}" >
 <c:if test="${!empty ssFolderEntryDescendants}">
<div class="ss_replies">
<span class="ss_largerprint ss_bold"><c:out value="${property_caption}"/></span>

<c:forEach var="reply" items="${ssFolderEntryDescendants}">
<jsp:useBean id="reply" type="com.sitescape.ef.domain.Entry" />
 <div>
<c:if test="${!empty reply.entryDef}">
 	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
		configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView']") %>" 
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
 <div class="ss_divider"></div>
</c:forEach>
</div>
</c:if>
</c:if>
