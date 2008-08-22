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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="com.sitescape.team.ObjectKeys" %>
<%@ page import="com.sitescape.team.util.NLT" %>

<c:if test="${empty ss_namespace}">
	<c:set var="ss_namespace" value="${renderResponse.namespace}" />
</c:if>

<c:if test="${empty isDashboard}">
	<c:set var="isDashboard" value="no"/>
</c:if>


		<ul class="ss_searchResult">
		<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
		    <c:set var="entryBinderTitle" value="${entry.binderTitle}"/>
		    <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}">
		    	<c:set var="entryBinderTitle" value="${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}"/>
		    </c:if>
		
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
				
			<li <c:if test="${status.last}">class="last"</c:if>>
				<c:choose>
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'entry'}">
							<div class="ss_thumbnail">
								<c:if test="${entry._entryType != 'reply'}">
								  <img alt="<ssf:nlt tag="alt.entry"/>" src="<html:imagesPath/>pics/entry_icon.gif"/>
                                </c:if>
								<c:if test="${entry._entryType == 'reply'}">
								  <img alt="<ssf:nlt tag="alt.comment"/>" src="<html:imagesPath/>pics/entry_comment_icon.gif"/>
                                </c:if>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<h3 class="ss_entryTitle">

	


	   									<% if (!ssSeenMap.checkIfSeen(entry)) { %>
									    
										  <a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
										  title="<ssf:nlt tag="sunburst.click"/>"
										  onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
										><span 
										  style="display:${ss_sunburstVisibilityHide};"
										  id="ss_sunburstShow${renderResponse.namespace}" 
										  class="ss_fineprint">
										  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="text-bottom" <ssf:alt tag="alt.new"/> />&nbsp;
										  </span>
										  </a>
										    
										<% } %>
										
										<ssf:titleLink 
											entryId="${entry._docId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}" 
											useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
											
											<ssf:param name="url" useBody="true">
												<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
												action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
											</ssf:param>
										
										    <c:if test="${entry._entryType == 'reply' && !empty entry._docNum}">
										      ${entry._docNum}&nbsp;&nbsp;
										    </c:if>
										    <c:if test="${empty entry.title}">
										    	(<ssf:nlt tag="entry.noTitle"/>)
										    </c:if>
									    	<c:out value="${entry.title}"/>
										</ssf:titleLink>
	
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
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
							       <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
							       <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
									<c:if test="${!empty entry._totalReplyCount}">
									    <span style="padding-left: 10px;" class="ss_label"><ssf:nlt 
									      tag="popularity.Comments"/>: ${entry._totalReplyCount}</span>
									</c:if>
							    </p>
								<c:if test="${!empty entry._workflowStateCaption}">
									<p><span class="ss_label"><ssf:nlt tag="entry.workflowState" />:</span> <c:out value="${entry._workflowStateCaption}" /></p>
								</c:if>
								<c:if test="${entry._entryType == 'reply' && !empty entry._entryTopEntryId}">
									<p>
									<ssf:nlt tag="searchResult.label.entry" />: 
										<ssf:titleLink 
											entryId="${entry._entryTopEntryId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  hrefClass="ss_parentPointer"
											namespace="${ss_namespace}" 
											useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">											
											<ssf:param name="url" useBody="true">
												<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
						      						action="view_folder_entry" entryId="${entry._entryTopEntryId}" actionUrl="true" />
											</ssf:param>
									    	<c:out value="${entry._entryTopEntryTitle}"/>
										</ssf:titleLink>
									</p>
								</c:if>
								<c:if test="${!empty entryBinderTitle}">
									<p><ssf:nlt tag="searchResult.label.binder" />: <a 
									<c:if test="${isDashboard == 'yes'}">
										href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._binderId}">
											<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
										onClick="return ss_gotoPermalink('${entry._binderId}','${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
									</c:if>
									<c:if test="${empty isDashboard || isDashboard == 'no'}">
								     href="<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._binderId}" action="view_folder_listing" actionUrl="false" >
						  					<ssf:param name="newTab" value="1"/>
	    	  							</ssf:url>" 
	    	  						  onClick="ss_openUrlInWorkarea(this.href, '${entry._binderId}', 'view_folder_listing');return false;"
	    	  						</c:if>
									class="ss_parentPointer">
									${entryBinderTitle}
									</a></p>
								</c:if>
							</div>
				</c:when>
		  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
							<div class="ss_thumbnail">
								<img alt="<ssf:nlt tag="alt.attachment"/>" src="<html:imagesPath/>pics/attachment_icon.gif"/>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<h3 class="ss_entryTitle">
											<ssf:titleLink 
												entryId="${entry._docId}" binderId="${entry._binderId}" 
												entityType="${entry._entityType}"  
												namespace="${ss_namespace}" 
												isDashboard="no" useBinderFunction="<%= strUseBinderMethod %>" isFile="yes">
												
												<ssf:param name="url" useBody="true">
													<ssf:fileUrl search="${entry}"/>
												</ssf:param>
												
										    	<c:out value="${entry._fileName}"/>
											</ssf:titleLink>
											
									</h3>
									<div class="ss_clear">&nbsp;</div>
								</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
								   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
								   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
									<c:if test="${!empty entry._totalReplyCount}">
									    <span style="padding-left: 10px;" class="ss_label"><ssf:nlt 
									      tag="popularity.Comments"/>: ${entry._totalReplyCount}</span>
									</c:if>
								   </p>
								<p><ssf:nlt tag="searchResult.label.entry" />:
										<ssf:titleLink 
											entryId="${entry._docId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}" 
											useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" 
											dashboardType="${ssDashboard.scope}" hrefClass="ss_parentPointer">
											
											<ssf:param name="url" useBody="true">
												<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
						      						action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
											</ssf:param>
									    	<c:out value="${entry.title}"/>
										</ssf:titleLink>
								</p>
								<c:if test="${!empty entryBinderTitle}">
									<p><ssf:nlt tag="searchResult.label.binder" />: <a 
									<c:if test="${isDashboard == 'yes'}">
										href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._binderId}">
											<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
										onClick="return ss_gotoPermalink('${entry._binderId}','${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
									</c:if>
									<c:if test="${empty isDashboard || isDashboard == 'no'}">
								     href="<ssf:url adapter="false" 
								       portletName="ss_forum" 
								       binderId="${entry._binderId}" 
								       action="view_folder_listing" actionUrl="false" >
	    	  							<ssf:param name="newTab" value="1"/>
	    	  							</ssf:url>" 
	    	  						  onClick="ss_openUrlInWorkarea(this.href, '${entry._binderId}', 'view_folder_listing');return false;"
	    	  						</c:if>
									class="ss_parentPointer">
									${entryBinderTitle}
									</a></p>
								</c:if>
								
							</div>
			    </c:when>
	
				<c:when test="${entry._entityType == 'user' && entry._docType == 'entry'}">
							<div class="ss_thumbnail">
								<c:if test="${!empty entry._fileID}"><img alt="<ssf:nlt tag="alt.entry"/>"
								
								  src="<ssf:fileUrl webPath="readThumbnail" search="${entry}"/> />
								</c:if>
								<c:if test="${empty entry._fileID}"><img alt="<ssf:nlt tag="alt.entry"/>"
								  src="<html:brandedImagesPath/>pics/thumbnail_no_photo.jpg"/></c:if>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<h3 class="ss_entryTitle">
	
										<ssf:titleLink 
											entryId="${entry._docId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}" 
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
													<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._binderId}" 
														entryId="${entry._docId}" action="view_ws_listing" actionUrl="false" >
			    	  									<ssf:param name="newTab" value="1"/>
			    	  								</ssf:url>
												</c:if>
												
											</ssf:param>
									    	<c:out value="${entry.title}"/>
										</ssf:titleLink>
	
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
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
								   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
								   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
								</p>
							</div>
				</c:when>
				
				<c:when test="${entry._entityType == 'user' && entry._docType == 'attachment'}">
							<div class="ss_thumbnail">
								<img alt="<ssf:nlt tag="alt.attachment"/>" src="<html:imagesPath/>pics/attachment_icon.gif"/>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<h3 class="ss_entryTitle">
										<ssf:titleLink 
											entryId="${entry._docId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}"  
											isDashboard="no" useBinderFunction="no" isFile="yes">
											
											<ssf:param name="url" useBody="true">
												<ssf:fileUrl search="${entry}"/>
											</ssf:param>
	
									    	<c:out value="${entry._fileName}"/>
										</ssf:titleLink>
									</h3>
									<div class="ss_clear">&nbsp;</div>
								</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
								   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
								   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
								</p>
								<p><ssf:nlt tag="searchResult.label.user" />:
										<c:if test="${isDashboard == 'yes'}">
											<a href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" entryId="${entry._docId}">
												<ssf:param name="entityType" value="${entry._entityType}" /><ssf:param name="newTab" value="1"/></ssf:url>"
											onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${ss_namespace}', 'yes');"
										</c:if>
										<c:if test="${empty isDashboard || isDashboard == 'no'}">
									     <a href="<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._binderId}" entryId="${entry._docId}" action="view_ws_listing" actionUrl="false" >
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
								<img alt="<ssf:nlt tag="alt.group"/>" src="<html:imagesPath/>pics/group_icon.gif"/>
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
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
								   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
								   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
								</p>
							</div>
				</c:when>
			
				<c:when test="${entry._docType == 'binder'}">
							<div class="ss_thumbnail">
								<c:set var="entryBinderId" value="${entry._docId}"/>
								<c:set var="entryDocId" value="${entry._docId}"/>
								<c:if test="${entry._entityType == 'folder'}">
								  <img <ssf:alt tag="general.type.folder"/> 
								    src="<html:imagesPath/>pics/folder_icon.gif"/>
								  <c:set var="actionVar" value="view_folder_listing"/>
								</c:if>
								<c:if test="${entry._entityType == 'workspace'}">
								  <img <ssf:alt tag="general.type.workspace"/> 
								    src="<html:imagesPath/>pics/workspace_icon.gif"/>
								  <c:set var="actionVar" value="view_ws_listing"/>
								</c:if>								
								<c:if test="${entry._entityType == 'profiles'}">
								  <img <ssf:alt tag="general.type.workspace"/> 
								    src="<html:imagesPath/>pics/workspace_icon.gif"/>
								  <c:set var="actionVar" value="view_profile_listing"/>
								</c:if>								
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<h3 class="ss_entryTitle">
										<ssf:titleLink 
											entryId="${entryDocId}" 
											binderId="${entryBinderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}"  
											useBinderFunction="<%= strUseBinderMethod %>" 
											isDashboard="${isDashboard}" 
											dashboardType="${ssDashboard.scope}">
											
											<ssf:param name="url" useBody="true">
											
												<c:if test="${isDashboard == 'yes'}">
													<ssf:url adapter="true" portletName="ss_forum" 
													    action="view_permalink" 
														binderId="${entryBinderId}" 
														entryId="${entryDocId}">
														<ssf:param name="entityType" value="${entry._entityType}"/>
														<ssf:param name="newTab" value="1"/>
													</ssf:url>
												</c:if>
											
												<c:if test="${empty isDashboard || isDashboard == 'no'}">
													<ssf:url adapter="false" portletName="ss_forum" 
													    folderId="${entry._docId}" 
														action="${actionVar}" actionUrl="false" >
						    							<ssf:param name="binderId" value="${entry._docId}"/>
			  											<ssf:param name="newTab" value="1"/>
			  										</ssf:url>
		  										</c:if>
		  										
											</ssf:param>
									    	<c:out value="${entry.title}"/>
										</ssf:titleLink>
									</h3>
									<div class="ss_clear">&nbsp;</div>
								</div>
								<p id="summary_${status.count}">
									<ssf:markup binderId="${entryBinderId}" entryId="${entryDocId}">
										<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
											${entry._desc}
										</ssf:textFormat>
									</ssf:markup>							
								</p>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
								   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
								   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
								</p>
							</div>
			    </c:when>
		  		<c:when test="${entry._docType == 'attachment'}">
							<div class="ss_thumbnail">
								<img alt="<ssf:nlt tag="alt.attachment"/>" src="<html:imagesPath/>pics/attachment_icon.gif"/>
							</div>
								<c:if test="${entry._entityType == 'folder'}">
								<c:set var="actionVar" value="view_folder_listing"/>
								<c:set var="binderLabel" value="<%= NLT.get("general.type.folder") %>"/>
								</c:if>
								<c:if test="${entry._entityType == 'workspace'}">
								<c:set var="actionVar" value="view_ws_listing"/>
								<c:set var="binderLabel" value="<%= NLT.get("general.type.workspace") %>"/>
								</c:if>								
								<c:if test="${entry._entityType == 'profiles'}">
								<c:set var="actionVar" value="view_profile_listing"/>
								<c:set var="binderLabel" value="<%= NLT.get("general.profiles") %>"/>
								</c:if>								
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<h3 class="ss_entryTitle">
											<ssf:titleLink 
												entryId="${entry._docId}" binderId="${entry._docId}" 
												entityType="${entry._entityType}"  
												namespace="${ss_namespace}" 
												isDashboard="no" useBinderFunction="no" isFile="yes">
												
												<ssf:param name="url" useBody="true">
													<ssf:fileUrl search="${entry}"/>
												</ssf:param>
												
										    	<c:out value="${entry._fileName}"/>
											</ssf:titleLink>
											
									</h3>
									<div class="ss_clear">&nbsp;</div>
								</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><span class="ss_label"><ssf:nlt tag="entry.createdBy" />:</span> <ssf:showUser user="${entry._principal}" />
								   <span style="padding-left: 10px;" class="ss_label"><ssf:nlt tag="entry.modified" />:</span> 
								   <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" />
								   </p>
									<p>${binderLabel}: <a 
									<c:if test="${isDashboard == 'yes'}">
										href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}">
											<ssf:param name="entityType" value="${entry._entityType}"/><ssf:param name="newTab" value="1"/></ssf:url>"
										onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${ss_namespace}', 'yes');"
									</c:if>
									<c:if test="${empty isDashboard || isDashboard == 'no'}">
								     href="<ssf:url adapter="false" binderId="${entry._docId}" action="${actionVar}" actionUrl="false" >
	    	  							<ssf:param name="newTab" value="1"/>
	    	  							</ssf:url>" 
	    	  						</c:if>
									class="ss_parentPointer">
									${entry.title}
									</a></p>
								
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
		
