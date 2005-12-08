<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${folderEntry == definitionEntry}" >
<div class="entryContent">
<table width="100%">
<tr>
  <th align="left"><c:out value="${property_caption}"/></th>
</tr>
<c:forEach var="reply" items="${folderEntryDescendants}">
<jsp:useBean id="reply" type="com.sitescape.ef.domain.FolderEntry" />
<tr>
  <td>
    <div class="entryContent">
	  <ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
		configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView']") %>" 
		configJspStyle="<%= configJspStyle %>" 
		processThisItem="false" 
		folderEntry="<%= reply %>" />
    </div>
  </td>
</tr>
</c:forEach>
</table>
</div>
</c:if>
