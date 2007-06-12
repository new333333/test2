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
<% // Places list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String propertyName = (String)request.getAttribute("property_name");
	java.util.List propertyValues = (java.util.List)request.getAttribute("propertyValues_"+propertyName);
	java.util.Set folderIds = new java.util.HashSet();
%>


<c:if test="${! empty ssDefinitionEntry}">
	<c:set var="places_entry" value="${ssDefinitionEntry}"/>
	<jsp:useBean id="places_entry" type="com.sitescape.team.domain.DefinableEntity" />
	<%
		if (propertyName != null && !propertyName.equals("") && places_entry.getCustomAttribute(propertyName) != null) { 
			folderIds = places_entry.getCustomAttribute(propertyName).getValueSet();
		}
		if (folderIds == null) {
			folderIds = new java.util.HashSet();
		}
	%>
</c:if>
<c:set var="propertyName" value="<%= propertyName %>"/>
<div class="ss_entryContent">
	<div class="ss_labelAbove"><c:out value="${property_caption}"/></div>
  
  	<c:choose>
		<c:when test="${!empty ssDomTree}">
			<ssf:tree 
				  treeName="t_searchForm_wsTree"
				  treeDocument="${ssDomTree}"  
				  rootOpen="false" 
				  multiSelect="<%= folderIds %>" 
				  multiSelectPrefix="${propertyName}"
				  fixedMultiSelectParamsMode="true"
				  showIdRoutine="t_advSearchForm_wsTree_showId"/>
		</c:when>
		<c:otherwise>
			<ssf:nlt tag="milestone.entryDesigner.tree.placeholder"/>
		</c:otherwise>
	</c:choose>
</div>
