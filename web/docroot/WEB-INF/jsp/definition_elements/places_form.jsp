<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // Places list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>
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
	
	// We use propertyName to construct JavaScript methods, ... so that
	// can't contain '-'s as that would generate an invalid name.  If
	// propertyName contains '-'s...
	String propertyName4Tree = propertyName;
	if ((null != propertyName) && (0 < propertyName.length() && propertyName.contains("-"))) {
		// ...replace them with somthing that would constitute a valid
		// ...name than we'll use instead.
		propertyName4Tree = org.kablink.util.StringUtil.replace(propertyName, '-', "_dash_");
	}
%>

<c:if test="${! empty ssDefinitionEntry}">
	<c:set var="places_entry" value="${ssDefinitionEntry}"/>
	<jsp:useBean id="places_entry" type="org.kablink.teaming.domain.DefinableEntity" />
	<%
		if (propertyName != null && !propertyName.equals("") && places_entry.getCustomAttribute(propertyName) != null) { 
			folderIds = places_entry.getCustomAttribute(propertyName).getValueSet();
		}
		if (folderIds == null) {
			folderIds = new java.util.HashSet();
		}
		if (!multipleAllowed && folderIds.iterator().hasNext()) {
			folderId = (String)folderIds.iterator().next();
			// to show current
			folderIds = java.util.Collections.singleton(folderId);
		}
	%>
</c:if>
<c:set var="propertyName" value="<%= propertyName %>"/>
<c:set var="propertyName4Tree" value="<%= propertyName4Tree %>"/>
<c:set var="multipleAllowed" value="<%= multipleAllowed %>"/>
<c:set var="folderId" value="<%= folderId %>"/>
<c:set var="folderIds" value="<%= folderIds %>"/>
<div class="ss_entryContent ${ss_fieldModifyStyle}">
	<div class="ss_labelAbove">${property_caption}<c:if test="${property_required}"><span 
  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if></div>
  
  	<c:choose>
		<c:when test="${!empty ssDomTree}">
			<c:set var="treeName" value="t_searchForm_wsTree${response.namespace}${propertyName4Tree}" />
			<script type="text/javascript">
				/* check/uncheck checkboxes in tree on click place name */
				function ${treeName}_showId(forum, obj) {
				<c:choose>
					<c:when test="${multipleAllowed}">
						return ss_checkTree(obj, "ss_tree_checkbox${treeName}${propertyName}" + forum);
					</c:when>
					<c:otherwise>
						return ss_checkTree(obj, "ss_tree_radio${treeName}${propertyName}" + forum);
					</c:otherwise>
				</c:choose>
				}
			</script>
			
			
			<c:if test="${!empty folderIds}">
				<ssf:nlt tag="places.delete.select.forums"/>
				<ul class="placesForm">
					<c:forEach var="folder" items="<%= org.kablink.teaming.util.ResolveIds.getBinderTitlesAndIcons(folderIds) %>">
						<li>
							<input type="checkbox" name="idChoicesRemove_${propertyName}" id="${treeName}${propertyName}del_${folder.key}"
								<c:if test="${folder.value.deleted}">
									checked="true"
								</c:if> value="${folder.key}" ${ss_fieldModifyInputAttribute} />
							<label for="${treeName}${propertyName}del_${folder.key}">${folder.value.title} (${folder.value.parentTitle})</label>
			  				<c:if test="${folder.value.deleted}">
				  				<span class="ss_fineprint ss_light"><ssf:nlt tag="milestone.folder.deleted"/></span>
			  				</c:if>
						</li>
					</c:forEach>	
				</ul>
			</c:if>
			
		  <c:if test="${empty ss_fieldModifyDisabled || ss_fieldModificationsAllowed}">
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
		  </c:if>

  
		</c:when>
		<c:otherwise>
			<ssf:nlt tag="milestone.entryDesigner.tree.placeholder"/>
		</c:otherwise>
	</c:choose>
</div>
