<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ssFolderEntry == ssDefinitionEntry}" >
<div class="ss_entryContent">
<table width="100%">
<tr>
  <th align="left"><c:out value="${property_caption}"/></th>
</tr>
<c:forEach var="reply" items="${ssFolderEntryDescendants}">
<jsp:useBean id="reply" type="com.sitescape.ef.domain.FolderEntry" />
<tr>
  <td>
    <div class="ss_entryContent">
	  <ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
		configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView']") %>" 
		configJspStyle="<%= ssConfigJspStyle %>" 
		processThisItem="false" 
		folderEntry="<%= reply %>" />
    </div>
  </td>
</tr>
</c:forEach>
</table>
</div>
</c:if>
