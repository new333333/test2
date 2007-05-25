<%
// The dashboard "search" component
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
	<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:set var="ss_pageSize" value="${ssDashboard.beans[componentId].ssSearchFormData.ss_pageSize}" />
<c:set var="summaryWordCount" value="30"/>
<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}">
	<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount}"/>
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<c:if test="${empty ss_namespace}">
<c:set var="ss_namespace" value="${portletNamespace}_${componentId}" />
</c:if>



<c:set var="ssResultEntries" value="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}"/>
<c:set var="ssResultTotalRecords" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}" />
<c:set var="ssPageEndIndex" value="${ss_pageNumber * ss_pageSize + ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchRecordReturned}" />
<c:set var="ssPageStartIndex" value="${ss_pageNumber * ss_pageSize + 1}" />
<c:set var="isDashboard" value="yes"/>

		<div id="ss_searchResult_header">
			<div id="ss_searchResult_numbers">			
				<c:choose>
				  <c:when test="${ssResultTotalRecords == '0'}">
					<ssf:nlt tag="search.NoResults" />
				  </c:when>
				  <c:otherwise>
					<ssf:nlt tag="search.results">
					<ssf:param name="value" value="${ssPageStartIndex}"/>
					<ssf:param name="value" value="${ssPageEndIndex}"/>
					<ssf:param name="value" value="${ssResultTotalRecords}"/>
					</ssf:nlt>
				  </c:otherwise>
				</c:choose>
			</div>
			<div id="ss_paginator"> 
			
			<c:if test="${empty isDashboard || isDashboard == 'no'}">
				<c:if test="${ss_pageNumber > 1}">
					<img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
					  onClick="ss_goToSearchResultPage(${ss_pageNumber-1});" />
				</c:if>
				<span class="ss_pageNumber">${ss_pageNumber}</span>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
					  onClick="ss_goToSearchResultPage(${ss_pageNumber+1});" />
				</c:if>
			</c:if>
			<c:if test="${isDashboard == 'yes'}">
				<c:if test="${ssDashboard.scope != 'portlet'}">
					<c:set var="binderId" value="${ssBinder.id}"/>
				</c:if>
				<c:if test="${ssDashboard.scope == 'portlet'}">
					<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
				</c:if>
				<c:if test="${ss_pageNumber > 0}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" /></a>
				</c:if>
				<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
				</c:if>
			</c:if>
			</div>
			<div class="ss_clear"></div>
		</div>
		
		
		
		<ul id="ss_searchResult">
		<c:forEach var="entry" items="${ssResultEntries}" varStatus="status">
		
			<jsp:useBean id="entry" type="java.util.HashMap" />
			
			<jsp:useBean id="isDashboard" type="java.lang.String" />
			
			<%
				String strUseBinderMethod = "yes";
				String strEntityType = (String) entry.get("_entityType");
				if (strEntityType == null) strEntityType = "";
				if ( strEntityType.equals("folderEntry") || strEntityType.equals("reply") ) {
					strUseBinderMethod = "no";
				} else if ( isDashboard.equals("yes") && (strEntityType.equals("user") || strEntityType.equals("folder") || strEntityType.equals("workspace") || strEntityType.equals("profiles")) ) {
					strUseBinderMethod = "permalink";
				}
			%>
				
			<li>
				<c:choose>
		  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'entry'}">
						<div class="ss_thumbnail">
							<img <ssf:alt tag="alt.entry"/> src="<html:imagesPath/>pics/entry_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">

									<ssf:menuLink 
										displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
										entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
								    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
										namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
										useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										
										<ssf:param name="url" useBody="true">
											<c:if test="${isDashboard == 'yes'}">
												<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
												action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
											</c:if>
											
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
												<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
					      						action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
											</c:if>
										</ssf:param>
									
									    <c:if test="${empty entry.title}">
									    	(<ssf:nlt tag="entry.noTitle"/>)
									    </c:if>
								    	<c:out value="${entry.title}"/>
									</ssf:menuLink>

								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
							<ssf:markup binderId="${entry._binderId}" entryId="${entry._docId}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
							</ssf:markup>
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
							<c:if test="${!empty entry._workflowStateCaption}">
								<p><span class="ss_label"><ssf:nlt tag="entry.workflowState" />:</span> <c:out value="${entry._workflowStateCaption}" /></p>
							</c:if>
							
							<c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}">
								<ssf:nlt tag="searchResult.label.binder" />: <a 
								<c:if test="${isDashboard == 'yes'}">
									href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._binderId}">
										<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
									onClick="return ss_gotoPermalink('${entry._binderId}','${entry._binderId}', 'folder', '${portletNamespace}', 'yes');"
								</c:if>
								<c:if test="${empty isDashboard || isDashboard == 'no'}">
							     href="<ssf:url adapter="false" portletName="ss_forum" folderId="${entry._binderId}" action="view_folder_listing" actionUrl="false" >
					    			<ssf:param name="binderId" value="${entry._binderId}"/>
    	  							<ssf:param name="newTab" value="1"/>
    	  							</ssf:url>" 
    	  						</c:if>
								class="ss_parentPointer">
								<c:out value="${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}"/>
								</a>
							</c:if>
						</div>
			</c:when>
	  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
						<div class="ss_thumbnail">
							<img <ssf:alt tag="alt.attachment"/> src="<html:imagesPath/>pics/attachment_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
										<ssf:menuLink 
											displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
									    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
											namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
											isDashboard="no" useBinderFunction="<%= strUseBinderMethod %>" isFile="yes">
											
											<ssf:param name="url" useBody="true">
												<ssf:url webPath="viewFile" binderId="${entry._binderId}">
													<ssf:param name="entryId" value="${entry._docId}"/>
													<ssf:param name="fileId" value="${entry._fileID}"/>
												</ssf:url>
											</ssf:param>
											
									    	<c:out value="${entry._fileName}"/>
										</ssf:menuLink>
										
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
							<p><ssf:nlt tag="searchResult.label.entry" />:
									<c:if test="${isDashboard == 'yes'}">
										<a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._docId}">
											<ssf:param name="entityType" value="${entry._entityType}"/><ssf:param name="newTab" value="1"/></ssf:url>"
										onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${portletNamespace}', 'yes');"
									</c:if>
									<c:if test="${empty isDashboard || isDashboard == 'no'}">
								     <a href="<ssf:url adapter="false" portletName="ss_forum" entryId="${entry._docId}" action="view_folder_entry" actionUrl="false" >
						    			<ssf:param name="binderId" value="${entry._binderId}"/>
	    	  							<ssf:param name="newTab" value="1"/>
	    	  							</ssf:url>" 
	    	  						</c:if>
									class="ss_parentPointer">
									<c:out value="${entry.title}"/>
								</a>
							</p>
							<c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}">
								<ssf:nlt tag="searchResult.label.binder" />: 
								<a 
								<c:if test="${isDashboard == 'yes'}">
									href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._binderId}">
										<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
									onClick="return ss_gotoPermalink('${entry._binderId}','${entry._binderId}', 'folder', '${portletNamespace}', 'yes');"
								</c:if>
								<c:if test="${empty isDashboard || isDashboard == 'no'}">
							     href="<ssf:url adapter="false" portletName="ss_forum" folderId="${entry._binderId}" action="view_folder_listing" actionUrl="false" >
					    			<ssf:param name="binderId" value="${entry._binderId}"/>
    	  							<ssf:param name="newTab" value="1"/>
    	  							</ssf:url>" 
    	  						</c:if>
								class="ss_parentPointer">
								<c:out value="${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}"/>
								</a>
							</c:if>
							
						</div>
		    </c:when>

			<c:when test="${entry._entityType == 'user' && entry._docType == 'entry'}">
						<div class="ss_thumbnail">
							<c:if test="${!empty entry._fileID}"><img <ssf:alt tag="alt.entry"/>
							  src="<ssf:url webPath="viewFile" folderId="${entry._binderId}" entryId="${entry._docId}" >
												<ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" />
							</c:if>
							<c:if test="${empty entry._fileID}"><img <ssf:alt tag="alt.entry"/>
							  src="<html:imagesPath/>pics/thumbnail_no_photo.jpg"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">

									<ssf:menuLink 
										displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
										entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
								    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
										namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
										useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										
										<ssf:param name="url" useBody="true">
										
											<c:if test="${isDashboard == 'yes'}">
												<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" 
													binderId="${entry._principal.workspaceId}" entryId="${entry._principal.workspaceId}">
													<ssf:param name="entityType" value="workspace" />
													<ssf:param name="newTab" value="1"/>
												</ssf:url>
											</c:if>
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
												<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._principal.workspaceId}" 
													entryId="${entry._docId}" action="view_ws_listing" actionUrl="false" >
							    					<ssf:param name="binderId" value="${entry._binderId}"/>
		    	  									<ssf:param name="newTab" value="1"/>
		    	  								</ssf:url>
											</c:if>
											
										</ssf:param>
									
									    <c:if test="${empty entry.title}">
									    	(<ssf:nlt tag="entry.noTitle"/>)
									    </c:if>
								    	<c:out value="${entry.title}"/>
									</ssf:menuLink>

								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
							
								<ssf:markup binderId="${entry._binderId}" entryId="${entry._docId}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										${entry._desc}
									</ssf:textFormat>
								</ssf:markup>

							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
			</c:when>
			
			<c:when test="${entry._entityType == 'user' && entry._docType == 'attachment'}">
						<div class="ss_thumbnail">
							<img <ssf:alt tag="alt.attachment"/> src="<html:imagesPath/>pics/attachment_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<ssf:menuLink 
										displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
										entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
								    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
										namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
										isDashboard="no" useBinderFunction="<%= strUseBinderMethod %>" isFile="yes">
										
										<ssf:param name="url" useBody="true">
											<ssf:url webPath="viewFile" binderId="${entry._binderId}">
												<ssf:param name="entryId" value="${entry._docId}"/>
												<ssf:param name="fileId" value="${entry._fileID}"/>
											</ssf:url>
										</ssf:param>

								    	<c:out value="${entry._fileName}"/>
									</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
							<p><ssf:nlt tag="searchResult.label.user" />:
									<c:if test="${isDashboard == 'yes'}">
										<a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._principal.workspaceId}" entryId="${entry._principal.workspaceId}">
											<ssf:param name="entityType" value="workspace" /><ssf:param name="newTab" value="1"/></ssf:url>"
										onClick="return ss_gotoPermalink('${entry._principal.workspaceId}','${entry._principal.workspaceId}', 'workspace', '${portletNamespace}', 'yes');"
									</c:if>
									<c:if test="${empty isDashboard || isDashboard == 'no'}">
								     <a href="<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._principal.workspaceId}" entryId="${entry._docId}" action="view_ws_listing" actionUrl="false" >
						    			<ssf:param name="binderId" value="${entry._binderId}"/>
	    	  							<ssf:param name="newTab" value="1"/>
	    	  							</ssf:url>"
	    	  						</c:if>
									class="ss_parentPointer">
									<c:out value="${entry.title}"/>
								</a>
							</p>
						</div>
			</c:when>
			
			<c:when test="${entry._entityType == 'group'}">
						<div class="ss_thumbnail">
							<c:if test="${empty entry._fileID}"><img 
							<ssf:alt tag="alt.group"/> src="<html:imagesPath/>pics/group_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<c:out value="${entry.title}"/>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:markup binderId="${entry._binderId}" entryId="${entry._docId}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										${entry._desc}
									</ssf:textFormat>
								</ssf:markup>
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
			</c:when>
		
			<c:when test="${entry._entityType == 'folder'}">
						<div class="ss_thumbnail">
							<c:if test="${empty entry._fileID}"><img 
							<ssf:alt tag="general.type.folder"/> src="<html:imagesPath/>pics/folder_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<ssf:menuLink 
										displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
										entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
								    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
										namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
										useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										
										<ssf:param name="url" useBody="true">
										
											<c:if test="${isDashboard == 'yes'}">
												<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" 
													binderId="${entry._binderId}" entryId="${entry._docId}">
													<ssf:param name="entityType" value="${entry._entityType}"/>
													<ssf:param name="newTab" value="1"/>
												</ssf:url>
											</c:if>
										
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
												<ssf:url adapter="false" portletName="ss_forum" folderId="${entry._docId}" 
													action="view_folder_listing" actionUrl="false" >
					    							<ssf:param name="binderId" value="${entry._docId}"/>
		  											<ssf:param name="newTab" value="1"/>
		  										</ssf:url>
	  										</c:if>
	  										
										</ssf:param>
									
									    <c:if test="${empty entry.title}">
									    	(<ssf:nlt tag="entry.noTitle"/>)
									    </c:if>
								    	<c:out value="${entry.title}"/>
									</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:markup binderId="${entry._binderId}" entryId="${entry._docId}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										${entry._desc}
									</ssf:textFormat>
								</ssf:markup>							
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
		    </c:when>
		    <c:when test="${entry._entityType == 'workspace'}">
						<div class="ss_thumbnail">
							<c:if test="${empty entry._fileID}"><img 
							<ssf:alt tag="general.type.workspace"/> src="<html:imagesPath/>pics/workspace_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<ssf:menuLink 
										displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
										entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
								    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
										namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
										useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										
										<ssf:param name="url" useBody="true">
										
											<c:if test="${isDashboard == 'yes'}">
												<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" 
													binderId="${entry._binderId}" entryId="${entry._docId}">
													<ssf:param name="entityType" value="${entry._entityType}" />
													<ssf:param name="newTab" value="1"/>
												</ssf:url>
											</c:if>
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
												<ssf:url adapter="false" portletName="ss_forum" folderId="${entry._docId}" 
								     				action="view_ws_listing" actionUrl="false" >
						    						<ssf:param name="binderId" value="${entry._docId}"/>
			  										<ssf:param name="newTab" value="1"/>
			  									</ssf:url>
		  									</c:if>
		  									
										</ssf:param>
									
									    <c:if test="${empty entry.title}">
									    	(<ssf:nlt tag="entry.noTitle"/>)
									    </c:if>
								    	<c:out value="${entry.title}"/>
									</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:markup binderId="${entry._binderId}" entryId="${entry._docId}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										${entry._desc}
									</ssf:textFormat>
								</ssf:markup>							
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
		    </c:when>
		    <c:when test="${entry._entityType == 'profiles'}">
						<div class="ss_thumbnail">
							<c:if test="${empty entry._fileID}"><img 
							<ssf:alt tag="general.profiles"/> src="<html:imagesPath/>pics/workspace_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<ssf:menuLink 
										displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
										entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
								    	menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
										namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" 
										useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										
										<ssf:param name="url" useBody="true">
										
											<c:if test="${isDashboard == 'yes'}">
												<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" 
													binderId="${entry._binderId}" entryId="${entry._docId}">
													<ssf:param name="entityType" value="${entry._entityType}" />
													<ssf:param name="newTab" value="1"/>
												</ssf:url>
											</c:if>
										
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
												<ssf:url folderId="${entry._docId}" binderId="${entry._docId}" action="view_profile_listing">
													<ssf:param name="newTab" value="1"/> 
												</ssf:url>
											</c:if>
											
										</ssf:param>
									
									    <c:if test="${empty entry.title}">
									    	(<ssf:nlt tag="entry.noTitle"/>)
									    </c:if>
								    	<c:out value="${entry.title}"/>
									</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:markup binderId="${entry._binderId}" entryId="${entry._docId}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										${entry._desc}
									</ssf:textFormat>
								</ssf:markup>							
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
		    </c:when>
		    <c:otherwise>
		    </c:otherwise>
			</c:choose>	
			</li>
		</c:forEach>
		</ul>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}_${componentId}" 
	linkMenuObjIdx="${renderResponse.namespace}_${componentId}" 
	namespace="${renderResponse.namespace}" dashboardType="${ssDashboard.scope}">
</ssf:menuLink>		