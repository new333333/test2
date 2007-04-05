		<ul id="ss_searchResult">
		<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
			<li>
				<c:choose>
		  		<c:when test="${entry._entityType == 'folderEntry'}">
						<div class="ss_thumbnail">
							<img src="<html:imagesPath/>pics/entry_icon.gif"/>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
			    					<a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._docId}"><ssf:param name="entityType" value="folderEntry" /><ssf:param name="newTab" value="1"/></ssf:url>"
									onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${portletNamespace}');">
									<c:out value="${entry.title}"/>
									</a>
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
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${entry._principal.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
							<c:if test="${!empty entry._workflowStateCaption}">
								<p><span class="ss_label"><ssf:nlt tag="entry.workflowState" />:</span> <c:out value="${entry._workflowStateCaption}" /></p>
							</c:if>
							<c:if test="${!empty entry._attachments}"> 
								<p><span class="ss_label"><ssf:nlt tag="searchResult.attachment" />:</span> 
									<ul>
									<c:forEach var="attachment" items="${entry._attachments}">
										<li>
											<c:if test="${!empty attachment._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${attachment._binderId}" entryId="${attachment._docId}">
												<ssf:param name="fileId" value="${attachment._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" class="ss_attachment_thumbnail"/>
											</c:if>
											<a target="_blank" href="<ssf:url webPath="viewFile" binderId="${entry._binderId}"><ssf:param name="entryId" value="${entry._docId}"/><ssf:param name="fileId" value="${attachment._fileID}"/></ssf:url>">
											${attachment._fileName}</a>
										</li>
									</c:forEach>
									</ul>
								</p>
							</c:if>
						</div>
			</c:when>
		    <c:when test="${entry._entityType == 'attachments'}">
ATTACHMENTS ONLY, WITHOUT ENTRY
			<div id="details_${status.count}" class="ss_entryDetails">
				<c:if test="${!empty entry._attachments}"> 
					<p><span class="ss_label"><ssf:nlt tag="searchResult.attachment" />:</span> 
						<ul>
						<c:forEach var="attachment" items="${entry._attachments}">
							<li>
								<c:if test="${!empty attachment._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${attachment._binderId}" entryId="${attachment._docId}">
									<ssf:param name="fileId" value="${attachment._fileID}"/>
								    <ssf:param name="viewType" value="thumbnail"/>
								    </ssf:url>" class="ss_attachment_thumbnail"/>
								</c:if>
								<a target="_blank" href="<ssf:url webPath="viewFile" binderId="${entry._binderId}"><ssf:param name="entryId" value="${entry._docId}"/><ssf:param name="fileId" value="${attachment._fileID}"/></ssf:url>">
								${attachment._fileName}</a>
								${attachment}
							</li>
						</c:forEach>
						</ul>
					</p>
				</c:if>
			</div>

		    </c:when>

			<c:when test="${entry._entityType == 'user'}">

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
									<a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink"	binderId="${entry._principal.workspaceId}"><ssf:param name="entityType" value="${entry._entityType}" /><ssf:param name="newTab" value="1"/></ssf:url>" 
									onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${portletNamespace}');">
									<c:out value="${entry.title}"/>
									</a>
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
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${entry._principal.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
							<c:if test="${!empty entry._attachments}"> 
								<p><span class="ss_label"><ssf:nlt tag="searchResult.attachment" />:</span> 
									<ul>
									<c:forEach var="attachment" items="${entry._attachments}">
										<li><a target="_blank" href="<ssf:url webPath="viewFile" binderId="${entry._binderId}"><ssf:param name="entryId" value="${attachment._docId}"/><ssf:param name="fileId" value="${attachment._fileID}"/></ssf:url>">
										${attachment._fileName}</a></li>
									</c:forEach>
									</ul>
								</p>
							</c:if>
						</div>

			</c:when>
				    
			<c:when test="${entry._entityType == 'group'}">
						<div class="ss_thumbnail">
							<c:if test="${!empty entry._fileID}"><img src="<ssf:url webPath="viewFile" folderId="${entry._binderId}" entryId="${entry._docId}" >
												<ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="viewType" value="thumbnail"/>
											    </ssf:url>" />
							</c:if>
							<c:if test="${empty entry._fileID}"><img src="<html:imagesPath/>pics/group_icon.gif"/></c:if>
						</div>
						<div class="ss_entry">
							<div class="ss_entryHeader">
								<h3 class="ss_entryTitle">
									<a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink"	binderId="${entry._principal.workspaceId}"><ssf:param name="entityType" value="${entry._entityType}" /><ssf:param name="newTab" value="1"/></ssf:url>" 
									onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${portletNamespace}');">
									<c:out value="${entry.title}"/>
									</a>
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
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${entry._principal.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
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
<!-- a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._docId}"><ssf:param name="entityType" value="folderEntry" /><ssf:param name="newTab" value="1"/></ssf:url>"
onClick="return ss_gotoPermalink('${entry._docId}','${entry._docId}', '${entry._entityType}', '${portletNamespace}');" -->
     <a href="<ssf:url
          adapter="false" 
          portletName="ss_forum" 
          folderId="${entry._docId}" 
          action="view_folder_listing"
          actionUrl="true" >
    	  <ssf:param name="binderId" value="${entry._docId}"/>
    	  <ssf:param name="newTab" value="1"/>
    	  </ssf:url>" 
        onClick="return ss_loadBinder(this, '${entry._docId}', '${entry._entityType}');" >
									<c:out value="${entry.title}"/>
									</a>
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
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${entry._principal.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
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
									<a href="<ssf:url adapter="false" portletName="ss_forum" folderId="${entry._docId}" action="view_ws_listing" actionUrl="true" >
							    		<ssf:param name="binderId" value="${entry._docId}"/><ssf:param name="newTab" value="1"/></ssf:url>" 
								    	onClick="return ss_loadBinder(this, '${entry._docId}', '${entry._entityType}');" >
										<c:out value="${entry.title}"/>
									</a>
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
							<p><span class="ss_label"><ssf:nlt tag="entry.modified" />:</span> <fmt:formatDate timeZone="${entry._principal.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></p>
						</div>
		    </c:when>
		    <c:when test="${entry._entityType == 'profiles'}">
				    	PROFILES?
		    </c:when>
			</c:choose>	
			</li>
		</c:forEach>
		</ul>
		
