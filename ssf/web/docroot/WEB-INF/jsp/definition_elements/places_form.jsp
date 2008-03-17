<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Places list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String propertyName = (String)request.getAttribute("property_name");
	java.util.List propertyValues = (java.util.List)request.getAttribute("propertyValues_"+propertyName);
	java.util.Set folderIds = new java.util.HashSet();
	String multiple = (String) request.getAttribute("property_multipleAllowed");
	boolean multipleAllowed = false;
	if (multiple != null && "true".equals(multiple)) {
		multipleAllowed = true;
	}
	String folderId = null;
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
		if (!multipleAllowed && folderIds.iterator().hasNext()) {
			folderId = (String)folderIds.iterator().next();
		}
		if (folderId == null) {
			folderId = "-1";
		}
	%>
</c:if>
<c:set var="propertyName" value="<%= propertyName %>"/>
<c:set var="multipleAllowed" value="<%= multipleAllowed %>"/>
<c:set var="folderId" value="<%= folderId %>"/>
<div class="ss_entryContent">
	<div class="ss_labelAbove"><c:out value="${property_caption}"/></div>
  
  	<c:choose>
		<c:when test="${!empty ssDomTree}">
			<c:set var="treeName" value="t_searchForm_wsTree${response.namespace}${propertyName}" />
			<script type="text/javascript">
				/* check/uncheck checkboxes in tree on click place name */
				function ${treeName}_showId(forum, obj) {
					if (obj.ownerDocument) {
						var cDocument = obj.ownerDocument;
					} else if (obj.document) {
						cDocument = obj.document;
					}
					if (cDocument) {
				<c:choose>
					<c:when test="${multipleAllowed}">
						var r = cDocument.getElementById("ss_tree_checkbox${treeName}${propertyName}" + forum);
					</c:when>
					<c:otherwise>
						var r = cDocument.getElementById("ss_tree_radio${treeName}${propertyName}" + forum);
					</c:otherwise>
				</c:choose>
						if (r) {
							if (r.checked !== undefined) {
								<c:choose>
									<c:when test="${multipleAllowed}">
										r.checked = !r.checked;
									</c:when>
									<c:otherwise>
										r.checked = true;
									</c:otherwise>
								</c:choose>							
							}
							if (r.onclick !== undefined) {
								r.onclick();
							}
						}
					}
					return false;
				}
			</script>

			<c:choose>
				<c:when test="${multipleAllowed}">
					<ssf:tree 
						  treeName="${treeName}"
						  treeDocument="${ssDomTree}"  
						  rootOpen="false" 
						  multiSelect="<%= folderIds %>" 
						  multiSelectPrefix="${propertyName}"/>				
				</c:when>
				<c:otherwise>
					<ssf:tree 
						  treeName="${treeName}"
						  treeDocument="${ssDomTree}"  
						  rootOpen="false" 
							singleSelect="<%= folderId %>"
			  				singleSelectName="${propertyName}"/>
				</c:otherwise>
			</c:choose>

  
		</c:when>
		<c:otherwise>
			<ssf:nlt tag="milestone.entryDesigner.tree.placeholder"/>
		</c:otherwise>
	</c:choose>
</div>
