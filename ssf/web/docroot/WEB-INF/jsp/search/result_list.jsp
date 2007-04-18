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
<c:if test="${empty isDashboard}">
	<c:set var="isDashboard" value="no"/>
</c:if>
		<ul id="ss_searchResult">
		<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
			<li>
				<c:choose>
		  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'entry'}">
						<div class="ss_thumbnail">
							<img src="<html:imagesPath/>pics/entry_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" isDashboard="${isDashboard}"
									useBinderFunction="no">
				
									<ssf:param name="url" useBody="true">
										<ssf:url folderId="${entry._binderId}" entryId="${entry._docId}" action="view_folder_entry"/>
									</ssf:param>
									<c:out value="${entry.title}"/>
								</ssf:menuLink>

								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
							<c:if test="${!empty entry._workflowStateCaption}">
								<p><span class="ss_label"><ssf:nlt tag="entry.workflowState" />:</span> <c:out value="${entry._workflowStateCaption}" /></p>
							</c:if>
							<c:if test="${!empty entry.binderTitle}">
								<ssf:nlt tag="searchResult.label.binder" />: 
								<a href="<ssf:url folderId="${entry._binderId}" action="view_folder_listing" />" 
								class="ss_parentPointer">
								${entry.binderTitle}
								</a>
							</c:if>
						</div>
			</c:when>
	  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
						<div class="ss_thumbnail">
							<img src="<html:imagesPath/>pics/attachment_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="none" isDashboard="${isDashboard}"
									useBinderFunction="no" isFile="yes">
									
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
								<a href="<ssf:url folderId="${entry._binderId}" entryId="${entry._docId}" action="view_folder_entry"/>"
									class="ss_parentPointer">
									<c:out value="${entry.title}"/>
								</a>
							</p>
							<c:if test="${!empty entry.binderTitle}">
								<ssf:nlt tag="searchResult.label.binder" />: 
								<a href="<ssf:url folderId="${entry._binderId}" action="view_folder_listing" />" 
								class="ss_parentPointer">
								${entry.binderTitle}
								</a>
							</c:if>
							
						</div>
		    </c:when>

			<c:when test="${entry._entityType == 'user' && entry._docType == 'entry'}">
						<div class="ss_thumbnail">
							<c:if test="${!empty entry._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${entry._binderId}" entryId="${entry._docId}" >
												<ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" />
							</c:if>
							<c:if test="${empty entry._fileID}"><img src="<html:imagesPath/>pics/thumbnail_no_photo.jpg"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId="menuimg_${entry._docId}_${renderResponse.namespace}" 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="none" isDashboard="${isDashboard}"
									useBinderFunction="yes">
									
									<ssf:param name="url" useBody="true">
									<ssf:url folderId="${entry._docId}" action="view_ws_listing" actionUrl="true">
										<ssf:param name="binderId" value="${entry._binderId}"/>
										<ssf:param name="entryId" value="${entry._docId}"/>
										<ssf:param name="newTab" value="1"/>
									</ssf:url>
									</ssf:param>
									<c:out value="${entry.title}"/>
								</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
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
							<img src="<html:imagesPath/>pics/attachment_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId="menuimg_${entry._docId}_${entry._fileID}_${renderResponse.namespace}" 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="none" isDashboard="${isDashboard}"
									useBinderFunction="no" isFile="yes">
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
								<a href="<ssf:url folderId="${entry._docId}" action="view_ws_listing" actionUrl="true">
										<ssf:param name="binderId" value="${entry._binderId}"/>
										<ssf:param name="entryId" value="${entry._docId}"/>
										<ssf:param name="newTab" value="1"/>
									</ssf:url>"
									class="ss_parentPointer">
									<c:out value="${entry.title}"/>
								</a>
							</p>
						</div>
			</c:when>				    
			<c:when test="${entry._entityType == 'group'}">
						<div class="ss_thumbnail">
							<c:if test="${empty entry._fileID}"><img src="<html:imagesPath/>pics/group_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<c:out value="${entry.title}"/>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
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
							<c:if test="${!empty entry._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${entry._binderId}" entryId="${entry._docId}" >
												<ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" />
							</c:if>
							<c:if test="${empty entry._fileID}"><img src="<html:imagesPath/>pics/folder_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="none" isDashboard="${isDashboard}"
									useBinderFunction="no">
									<ssf:param name="url" useBody="true">
										<ssf:url folderId="${entry._docId}" action="view_folder_listing" />
									</ssf:param>
									<c:out value="${entry.title}"/>
								</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
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
							<c:if test="${!empty entry._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${entry._binderId}" entryId="${entry._docId}" >
												<ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" />
							</c:if>
							<c:if test="${empty entry._fileID}"><img src="<html:imagesPath/>pics/workspace_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="none" isDashboard="${isDashboard}"
									useBinderFunction="no">
									<ssf:param name="url" useBody="true">
										<ssf:url folderId="${entry._docId}" action="view_ws_listing" />
									</ssf:param>
									<c:out value="${entry.title}"/>
								</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
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
							<c:if test="${!empty entry._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${entry._binderId}" entryId="${entry._docId}" >
												<ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" />
							</c:if>
							<c:if test="${empty entry._fileID}"><img src="<html:imagesPath/>pics/workspace_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
								<ssf:menuLink 
									displayDiv="false" entryId="${entry._docId}" binderId="${entry._binderId}" 
									entityType="${entry._entityType}" imageId='menuimg_${entry._docId}_${renderResponse.namespace}' 
							    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
									namespace="${renderResponse.namespace}" entryCallbackRoutine="none" isDashboard="${isDashboard}"
									useBinderFunction="no">
									<ssf:param name="url" useBody="true">
										<ssf:url folderId="${entry._docId}" binderId="${entry._docId}" action="view_profile_listing" />
									</ssf:param>
									<c:out value="${entry.title}"/>
								</ssf:menuLink>
								</h3>
								<div class="ss_clear">&nbsp;</div>
							</div>
							<p id="summary_${status.count}">
								<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
									${entry._desc}
								</ssf:textFormat>
							</p>
						</div>
						<div class="ss_clear">&nbsp;</div>
										
						<div id="details_${status.count}" class="ss_entryDetails">
							<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" /></p>
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
		    </c:when>
		    <c:otherwise>
			<!--	  other type: ${entry._entityType}
				  entry details: ${entry} -->
		    </c:otherwise>
			</c:choose>	
			</li>
		</c:forEach>
		</ul>
		
<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>
		