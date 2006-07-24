<% // View blog replies %>
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssEntry.topEntry}" >
<c:if test="${!empty ssFolderEntryDescendants}">
<div class="ss_replies">
<c:forEach var="reply" items="${ssFolderEntryDescendants}">
<jsp:useBean id="reply" type="com.sitescape.ef.domain.Entry" />
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
