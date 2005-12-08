<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ssFolderEntry == ssDefinitionEntry}" >
 <c:if test="${!empty ssFolderEntryDescendants}">
<h1 class="ss_entryTitle"><c:out value="${property_caption}"/></h1>

<c:forEach var="reply" items="${ssFolderEntryDescendants}">
<jsp:useBean id="reply" type="com.sitescape.ef.domain.FolderEntry" />
 <div class="ss_contentEntry">
	  <ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
		configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView']") %>" 
		configJspStyle="<%= ssConfigJspStyle %>" 
		processThisItem="false" 
		folderEntry="<%= reply %>" />
 </div>
 <div class="ss_divider"></div>
</c:forEach>
</c:if>
</c:if>
