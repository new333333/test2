<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ssEntry == ssDefinitionEntry}" >
  <c:if test="${!empty ssFolderEntryDescendants}">

<div class="ss_entryContent" style="padding-top:15px;">
<c:if test="${!empty property_caption}">
<span class="ss_largerprint ss_bold"><c:out value="${property_caption}"/></span>
<br/>
</c:if>

<c:forEach var="reply" items="${ssFolderEntryDescendants}">
  <jsp:useBean id="reply" type="com.sitescape.team.domain.Entry" />
  <ssf:box style="rounded">
  <ssf:param name="backgroundClass" value="ss_replies_background"/>
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
  </ssf:box>

</c:forEach>

</div>
  </c:if>
</c:if>
