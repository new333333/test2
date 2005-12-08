<% // View entry replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ss_forum_entry == ss_definition_folder_entry}" >
<div class="entryContent">
<table width="100%">
<tr>
  <th align="left"><c:out value="${property_caption}"/></th>
</tr>
<c:forEach var="reply" items="${ss_forum_entry_descendants}">
<jsp:useBean id="reply" type="com.sitescape.ef.domain.FolderEntry" />
<tr>
  <td>
    <div class="entryContent">
	  <ssf:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
		configElement="<%= (Element) reply.getEntryDef().getDefinition().getRootElement().selectSingleNode("//item[@name='entryView']") %>" 
		configJspStyle="<%= ss_forum_configJspStyle %>" 
		processThisItem="false" 
		folderEntry="<%= reply %>" />
    </div>
  </td>
</tr>
</c:forEach>
</table>
</div>
</c:if>
