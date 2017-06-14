<%
/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.WebHelper" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ page import="org.kablink.teaming.util.FileIconsHelper" %>
<%@ page import="org.kablink.teaming.util.IconSize" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>

<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />

<%
boolean isFilr = org.kablink.teaming.util.Utils.checkIfFilr();
isFilr = false;		//I turned off the way Filr sorted its hits. (pmh)
					//This caused the pages to have fewer than 10 hits per page
					//Customers complained this was confusing. See bug #804179

boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "";
if (isIECheck) strBrowserType = "ie";
%>

<c:if test="${empty ss_namespace}">
	<c:set var="ss_namespace" value="${renderResponse.namespace}" />
</c:if>

<c:if test="${empty isDashboard}">
	<c:set var="isDashboard" value="no"/>
</c:if>

<%
	Map entriesSeen = new HashMap();
%>

		<ul class="ss_searchResult ss_nobullet">
		<c:forEach var="entry" items="${ssFolderEntries}" varStatus="status">
			<jsp:useBean id="entry" type="java.util.HashMap" />
			<%
			if (!isFilr || (isFilr && !entriesSeen.containsKey(entry.get("_docId")))) {

				String parentBinderId = "";
				if (entry.containsKey("_binderId")) parentBinderId = (String)entry.get("_binderId");
				boolean parentBinderPreDeleted = true;
				try {
					if (!parentBinderId.equals("")) {
						parentBinderPreDeleted = WebHelper.isBinderPreDeleted(Long.valueOf(parentBinderId));
					} else {
						parentBinderPreDeleted = false;
					}
				} catch(Exception e) {}
			%>
		    <c:set var="entryBinderTitle" value="${entry.binderTitle}"/>
		    <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}">
		    	<c:set var="entryBinderTitle" value="${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].title}"/>
		    </c:if>
		    <c:set var="entryBinderPathName" value="${entry.binderPathName}"/>
		    <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].pathName}">
		    	<c:set var="entryBinderPathName" value="${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[entry._binderId].pathName}"/>
		    </c:if>
			<c:set var="supportsViewAsHtml" value="0"/>
			<ssf:ifSupportsViewAsHtml relativeFilePath="${entry._fileName}" browserType="<%=strBrowserType%>">
			  <c:set var="supportsViewAsHtml" value="1"/>
			</ssf:ifSupportsViewAsHtml>
			<c:set var="fileLinkAction" value="VIEW_DETAILS"/>
			<c:if test="${ssEffectiveFileLinkAction == 'DOWNLOAD' || ssEffectiveFileLinkAction == 'VIEW_HTML_ELSE_DOWNLOAD'}">
			  <c:set var="fileLinkAction" value="DOWNLOAD"/>
			</c:if>
			<c:if test="${supportsViewAsHtml == 1 && (ssEffectiveFileLinkAction == 'VIEW_HTML_ELSE_DOWNLOAD' || ssEffectiveFileLinkAction == 'VIEW_HTML_ELSE_DETAILS')}">
			  <c:set var="fileLinkAction" value="HTML"/>
			</c:if>
			
			<jsp:useBean id="isDashboard" type="java.lang.String" />
			
			<%
				String strUseBinderMethod = "yes";
				String strEntityType = (String) entry.get("_entityType");
				if (strEntityType == null) strEntityType = "";
				if ( strEntityType.equals("folderEntry") || strEntityType.equals("reply") ) {
					strUseBinderMethod = "no";
				} else if ( (isDashboard.equals("yes") || GwtUIHelper.isGwtUIActive(request)) && (strEntityType.equals("folder") || strEntityType.equals("workspace") || strEntityType.equals("profiles")) ) {
					strUseBinderMethod = "permalink";
				}
			%>
				
			<li <c:if test="${status.last}">class="last"</c:if>>
				<c:choose>
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'entry' && entry._entryType != 'reply'}">
							<div class="ss_thumbnail ss_search_hit">
								<img alt="<ssf:nlt tag="alt.entry"/>" src="<html:imagesPath/>pics/entry_24.png"/>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
	   									<% if (!ssSeenMap.checkIfSeen(entry)) { %>
									    
										  <a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
										  title="<ssf:nlt tag="sunburst.click"/>"
										  onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
										><span 
										  style="display:${ss_sunburstVisibilityHide};"
										  id="ss_sunburstShow${renderResponse.namespace}" 
										  class="ss_fineprint">
										  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0" <ssf:alt tag="alt.new"/> />
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
									    	
										     <c:if test="${!empty entry._rating}">				    	
												<span class="ss_nowrap marginleft1">
													<%
														String iRating = String.valueOf(java.lang.Math.round(Float.valueOf(entry.get("_rating").toString())));
													%>
													<c:set var="sRating" value="<%= iRating %>"/>
													<c:if test="${sRating > 0}">
														<c:forEach var="i" begin="0" end="${sRating - 1}" step="1">

														  <img border="0" 
														    <ssf:alt tag="alt.goldStar"/>
														    src="<html:imagesPath/>pics/star_gold.png"/>
														</c:forEach>
													</c:if>
													<c:if test="${sRating < 5}">
														<c:forEach var="i" begin="${sRating}" end="4" step="1">
														  <img <ssf:alt tag="alt.grayStar"/> border="0" 
															    src="<html:imagesPath/>pics/star_gray.png" />
														</c:forEach>
													</c:if>
												</span>
										     </c:if>
										     
										</ssf:titleLink>
	
									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
								<div class="ss_entryDetails">
								<c:if test="${!empty entryBinderTitle}">
									<p>
									<%if (!parentBinderPreDeleted) { %>
										<a 
											<c:if test="${isDashboard == 'yes'}">
												href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" >
													<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
												onClick="return ss_gotoPermalink('${entry._binderId}','${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
											</c:if>
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
										     href="<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._binderId}" action="view_folder_listing" actionUrl="false" >
								  					<ssf:param name="newTab" value="1"/>
			    	  							</ssf:url>" 
			    	  						  onClick="ss_openUrlInWorkarea(this.href, '${entry._binderId}', 'view_folder_listing');return false;"
			    	  						</c:if>
											class="ss_parentPointer" title="${entryBinderPathName}">
											${entryBinderTitle}
										</a>
									<% } else { %>
										${entryBinderTitle}
									<% } %>
									</p>
								</c:if>
								<p><ssf:showUser user="${entry._principal}" />
							       <span class="ss_search_results_entry_date"> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
							    </p>
							    <c:if test="${!empty entry._desc}">
									<p id="summary_${status.count}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										<ssf:markup search="${entry}">${entry._desc}</ssf:markup>
										</ssf:textFormat>
										<div class="ss_clear"></div>
									</p>
								</c:if>
							</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
				    </c:when>
				
			  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'entry' && entry._entryType == 'reply'}">
							<div class="ss_thumbnail">
								<img alt="<ssf:nlt tag="alt.entry"/>" src="<html:imagesPath/>pics/entry_24.png"/>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader">
									<div class="ss_entryTitleSearchResults">
										<ssf:titleLink 
											entryId="${entry._entryTopEntryId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}" 
											useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
											
											<ssf:param name="url" useBody="true">
												<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
												action="view_folder_entry" entryId="${entry._entryTopEntryId}" actionUrl="true" />
											</ssf:param>
										
										    <c:if test="${empty entry._entryTopEntryTitle}">
										    	(<ssf:nlt tag="entry.noTitle"/>)
										    </c:if>
									    	<c:out value="${entry._entryTopEntryTitle}"/>
										</ssf:titleLink>
									</div>
								</div>
								<div class="ss_clear"></div>
								<div class="ss_entryDetails2">
								<c:if test="${!empty entryBinderTitle}">
									<p>
									<%if (!parentBinderPreDeleted) { %>
										<a 
											<c:if test="${isDashboard == 'yes'}">
												href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" >
													<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
												onClick="return ss_gotoPermalink('${entry._binderId}','${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
											</c:if>
											<c:if test="${empty isDashboard || isDashboard == 'no'}">
										     href="<ssf:url adapter="false" portletName="ss_forum" binderId="${entry._binderId}" action="view_folder_listing" actionUrl="false" >
								  					<ssf:param name="newTab" value="1"/>
			    	  							</ssf:url>" 
			    	  						  onClick="ss_openUrlInWorkarea(this.href, '${entry._binderId}', 'view_folder_listing');return false;"
			    	  						</c:if>
											class="ss_parentPointer" title="${entryBinderPathName}">
											${entryBinderTitle}
										</a>
									<% } else { %>
										${entryBinderTitle}
									<% } %>
									</p>
								</c:if>
								<p><ssf:showUser user="${entry._principal}" />
							       <span class="ss_search_results_entry_date"> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
							    </p>
							</div>
							</div>
							<div class="ss_clear"></div>

							<div style="padding:6px 0px 0px 16px;">
							<div class="ss_thumbnail ss_search_hit">
								<img alt="<ssf:nlt tag="alt.comment"/>" src="<html:imagesPath/>pics/comment_24.png"/>
							</div>
							<div class="ss_reply">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
	   									<% if (!ssSeenMap.checkIfSeen(entry)) { %>
									    
										  <a id="ss_sunburstDiv${entry._binderId}_${entry._docId}" href="javascript: ;" 
										  title="<ssf:nlt tag="sunburst.click"/>"
										  onClick="ss_hideSunburst('${entry._docId}', '${entry._binderId}');return false;"
										><span 
										  style="display:${ss_sunburstVisibilityHide};"
										  id="ss_sunburstShow${renderResponse.namespace}" 
										  class="ss_fineprint">
										  	<img src="<html:rootPath/>images/pics/discussion/sunburst.png" align="absmiddle" border="0" <ssf:alt tag="alt.new"/> />
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
										      <%
										      	String docNum = (String)entry.get("_docNum");
										      	if (docNum.indexOf(".") > 0) {
										    		docNum = docNum.substring(docNum.indexOf(".")+1);
										    	}
										      %>
										      <%= docNum %>&nbsp;&nbsp;
										    </c:if>
										    <c:if test="${empty entry.title && !empty entry._entryTopEntryTitle}">
										        <ssf:nlt tag="reply.re.title"><ssf:param
										    	  name="value" useBody="true">${entry._entryTopEntryTitle}</ssf:param></ssf:nlt>
										    </c:if>
										    <c:if test="${empty entry.title && empty entry._entryTopEntryTitle}">
										        (<ssf:nlt tag="entry.noTitle"/>)
										    </c:if>
									    	<c:out value="${entry.title}"/>
									    	
										     <c:if test="${!empty entry._rating}">				    	
												<span class="ss_nowrap marginleft1">
													<%
														String iRating = String.valueOf(java.lang.Math.round(Float.valueOf(entry.get("_rating").toString())));
													%>
													<c:set var="sRating" value="<%= iRating %>"/>
													<c:if test="${sRating > 0}">
														<c:forEach var="i" begin="0" end="${sRating - 1}" step="1">

														  <img border="0" 
														    <ssf:alt tag="alt.goldStar"/>
														    src="<html:imagesPath/>pics/star_gold.png"/>
														</c:forEach>
													</c:if>
													<c:if test="${sRating < 5}">
														<c:forEach var="i" begin="${sRating}" end="4" step="1">
														  <img <ssf:alt tag="alt.grayStar"/> border="0" 
															    src="<html:imagesPath/>pics/star_gray.png" />
														</c:forEach>
													</c:if>
												</span>
										     </c:if>
										     
										</ssf:titleLink>
	
									</div>
									<div class="ss_clear"></div>
								</div>
								<div class="ss_entryDetails">
								<p><ssf:showUser user="${entry._principal}" />
							       <span class="ss_search_results_entry_date"> <fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
							    </p>
								<c:if test="${!empty entry._desc}">
									<p id="summary_${status.count}">
									<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
										<ssf:markup search="${entry}">${entry._desc}</ssf:markup>
										</ssf:textFormat>
										<div class="ss_clear"></div>
									</p>
								</c:if>
								</div>
								<div class="ss_clear"></div>
							</div>
							<div class="ss_clear">&nbsp;</div>
						</div>
											
				</c:when>

		  		<c:when test="${entry._entityType == 'folderEntry' && entry._docType == 'attachment'}">
		  				<c:set var="fileIcon" value='<%= FileIconsHelper.getFileIconFromFileName((String)entry.get("_fileName"), IconSize.MEDIUM) %>'/>
		  				<c:if test="${empty fileIcon}">
		  				  <c:set var="fileIcon" value="pics/attachment_24.png"/>
		  				</c:if>
							<div class="ss_thumbnail ss_search_hit">
							  <c:if test="${!empty fileIcon}">
								<img alt="<ssf:nlt tag="alt.attachment"/>" src="<html:imagesPath/>${fileIcon}"/>
							  </c:if>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
										<c:if test="${fileLinkAction == 'VIEW_DETAILS' }">
											<ssf:titleLink 
												entryId="${entry._docId}" binderId="${entry._binderId}" 
												entityType="${entry._entityType}"  
												namespace="${ss_namespace}" 
												isDashboard="no" useBinderFunction="<%= strUseBinderMethod %>" isFile="no">
												
											<ssf:param name="url" useBody="true">
											    <ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
												action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
											</ssf:param>
												
										    	<c:out value="${entry._fileName}"/>
											</ssf:titleLink>
										</c:if>
										<c:if test="${fileLinkAction == 'DOWNLOAD' }">
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
										</c:if>
										<c:if test="${fileLinkAction == 'HTML'}">
											<ssf:titleLink 
												entryId="${entry._docId}" binderId="${entry._binderId}" 
												entityType="${entry._entityType}"  
												namespace="${ss_namespace}" 
												isDashboard="no" useBinderFunction="<%= strUseBinderMethod %>" isFile="yes">
											<ssf:param name="url" useBody="true">	
												<ssf:url 
												webPath="viewFile"
											    folderId="${entry._binderId}"
											    entryId="${entry._docId}" >
												<ssf:param name="entityType" value="${entry._entityType}"/>
											    <ssf:param name="fileId" value="${entry._fileID}"/>
											    <ssf:param name="fileTime" value="${entry._fileTime}"/>
											    <ssf:param name="viewType" value="html"/>
											    </ssf:url>
											</ssf:param>
										    	<c:out value="${entry._fileName}"/>
											</ssf:titleLink>
										  </c:if>

									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<div>
									<ssf:titleLink 
										entryId="${entry._docId}" 
										binderId="${entry._binderId}" 
										entityType="${entry._entityType}"  
										namespace="${ss_namespace}" 
										useBinderFunction="<%= strUseBinderMethod %>" isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
										
										<ssf:param name="url" useBody="true">
											<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
											action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
										</ssf:param>
									
									    <c:if test="${empty entry.title}">
									    	(<ssf:nlt tag="entry.noTitle"/>)
									    </c:if>
								    	<c:out value="${entry.title}"/>
									</ssf:titleLink>
								</div>
								<c:if test="${!empty entryBinderTitle}">
									<p>
										<a 
											<c:if test="${isDashboard == 'yes'}">
												href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${entry._binderId}" >
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
											class="ss_parentPointer" title="${entryBinderPathName}">
											${entryBinderTitle}
										</a>
									</p>
								</c:if>
								<p><ssf:showUser user="${entry._principal}" />
								   <span class="ss_search_results_entry_date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
								   </p>
								
							</div>
			    </c:when>
	
				<c:when test="${entry._entityType == 'user' && entry._docType == 'entry'}">
							<div class="ss_thumbnail ss_search_hit">
								<c:if test="${!empty entry._fileID}"><img alt="<ssf:nlt tag="alt.entry"/>"
								
								  src="<ssf:fileUrl webPath="readThumbnail" search="${entry}"/>" />
								</c:if>
								<c:if test="${empty entry._fileID}"><img alt="<ssf:nlt tag="alt.entry"/>"
								  src="<html:brandedImagesPath/>pics/UserPhoto.png"/></c:if>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
	
										<ssf:titleLink 
											entryId="${entry._docId}" binderId="${entry._binderId}" 
											entityType="${entry._entityType}"  
											namespace="${ss_namespace}" 
											useBinderFunction="<%= strUseBinderMethod %>" 
											isDashboard="${isDashboard}" 
											dashboardType="${ssDashboard.scope}">
											
											<ssf:param name="url" useBody="true">
												<c:if test="${isDashboard == 'yes'}">
													<ssf:permalink search="${entry}"/>
												</c:if>
												<c:if test="${empty isDashboard || isDashboard == 'no'}">
												  <c:if test="${!empty entry._workspaceId}">
													<ssf:url adapter="false" portletName="ss_forum" 
													    binderId="${entry._workspaceId}" 
														action="view_ws_listing" actionUrl="false" >
			    	  									<ssf:param name="profile" value="1"/>
			    	  								</ssf:url>
			    	  							  </c:if>
												</c:if>
												
											</ssf:param>
											<c:if test="${empty entry._workspaceId}">
											  <ssf:param name="onClick" 
											    useBody="true">alert('<ssf:escapeJavaScript><ssf:nlt 
												  tag="workspace.noUserWorkspace"/></ssf:escapeJavaScript>');return false;</ssf:param>
		    	  							</c:if>
									    	<c:out value="${entry.title}"/>
										</ssf:titleLink>
	
									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
							    <c:if test="${!empty entry._desc}">
									<p id="summary_${status.count}">	
										<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
												<ssf:markup search="${entry}">${entry._desc}</ssf:markup>
											</ssf:textFormat>
											<div class="ss_clear"></div>
									</p>
								</c:if>
							</div>
							<div class="ss_clear">&nbsp;</div>
							<div id="details_${status.count}" class="ss_entryDetails">
								<p>
								<ssf:showUser user="${entry._principal}" />
								   <span class="ss_search_results_entry_date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
								</p>
							</div>
				</c:when>
				
				<c:when test="${entry._entityType == 'user' && entry._docType == 'attachment'}">
							<div class="ss_thumbnail ss_search_hit">
								<img alt="<ssf:nlt tag="alt.attachment"/>" src="<html:imagesPath/>pics/attachment_24.png"/>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
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
									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><ssf:showUser user="${entry._principal}" />
								   <span class="ss_search_results_entry_date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
								</p>
								<p><ssf:nlt tag="searchResult.label.user" />:
									<c:if test="${isDashboard == 'yes'}">
										<a href="<ssf:permalink search="${entry}"/>"
											onClick="return ss_gotoPermalink('${entry._binderId}','${entry._docId}', '${entry._entityType}', '${ss_namespace}', 'yes');"
											class="ss_parentPointer"
										>
											<c:out value="${entry.title}"/>
										</a>
									</c:if>
									<c:if test="${empty isDashboard || isDashboard == 'no'}">
								      <a href="<ssf:url adapter="false" 
								        	portletName="ss_forum" 
									        binderId="${entry._binderId}" 
									        entryId="${entry._docId}" action="view_ws_listing" actionUrl="false" >
		    	  							<ssf:param name="newTab" value="1"/></ssf:url>"
										class="ss_parentPointer"
									  >
											<c:out value="${entry.title}"/>
									  </a>
	    	  						</c:if>
								</p>
							</div>
				</c:when>
				
				<c:when test="${entry._entityType == 'group'}">
							<div class="ss_thumbnail ss_search_hit">
								<img alt="<ssf:nlt tag="alt.group"/>" src="<html:imagesPath/>pics/group_20.png"/>
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
										<c:out value="${entry.title}"/>
									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
								<c:if test="${!empty entry._desc}">
									<p id="summary_${status.count}">
										<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
											<ssf:markup search="${entry}">${entry._desc}</ssf:markup>
										</ssf:textFormat>
										<div class="ss_clear"></div>
									</p>
								</c:if>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><ssf:showUser user="${entry._principal}" />
								   <span class="ss_search_results_entry_date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
								</p>
							</div>
				</c:when>
			
				<c:when test="${entry._docType == 'binder'}">
							<div class="ss_thumbnail ss_search_hit">
								<c:set var="entryBinderId" value="${entry._docId}"/>
								<c:set var="entryDocId" value="${entry._docId}"/>
								<c:if test="${entry._entityType == 'folder'}">
								  <img <ssf:alt tag="general.type.folder"/> 
								    src="<html:imagesPath/>pics/folder_24.png"/>
								  <c:set var="actionVar" value="view_folder_listing"/>
								</c:if>
								<c:if test="${entry._entityType == 'workspace'}">
								  <img <ssf:alt tag="general.type.workspace"/> 
								    src="<html:imagesPath/>pics/workspace_24.png"/>
								  <c:set var="actionVar" value="view_ws_listing"/>
								</c:if>								
								<c:if test="${entry._entityType == 'profiles'}">
								  <img <ssf:alt tag="general.type.workspace"/> 
								    src="<html:imagesPath/>pics/profile_24.png"/>
								  <c:set var="actionVar" value="view_profile_listing"/>
								</c:if>								
							</div>
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
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
													<ssf:permalink search="${entry}"/> 
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
									    	<c:out value="${entry._extendedTitle}"/>
										</ssf:titleLink>
									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
								<c:if test="${!empty entry._desc}">
									<p id="summary_${status.count}">
										<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
												<ssf:markup search="${entry}">${entry._desc}</ssf:markup>	
										</ssf:textFormat>
										<div class="ss_clear"></div>
									</p>
								</c:if>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<c:set var="createdByText" value='<%= NLT.get("access.folderOwner") %>'/>
							<c:set var="entryPrincipal" value="${entry._principal}"/>
							<c:if test="${entry._entityType == 'workspace'}">
							  <c:set var="createdByText" value='<%= NLT.get("access.workspaceOwner") %>'/>
							</c:if>
							<c:if test="${!empty entry._principalOwner}">
							  <c:set var="entryPrincipal" value="${entry._principalOwner}"/>
							</c:if>
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><ssf:showUser user="${entryPrincipal}" />
								   <span class="ss_search_results_entry_date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
								</p>
							</div>
			    </c:when>
		  		<c:when test="${entry._docType == 'attachment'}">
							<div class="ss_thumbnail ss_search_hit">
								<img alt="<ssf:nlt tag="alt.attachment"/>" src="<html:imagesPath/>pics/attachment_24.png"/>
							</div>
								<c:if test="${entry._entityType == 'folder'}">
								<c:set var="actionVar" value="view_folder_listing"/>
								<c:set var="binderLabel" value='<%= NLT.get("general.type.folder") %>'/>
								</c:if>
								<c:if test="${entry._entityType == 'workspace'}">
								<c:set var="actionVar" value="view_ws_listing"/>
								<c:set var="binderLabel" value='<%= NLT.get("general.type.workspace") %>'/>
								</c:if>								
								<c:if test="${entry._entityType == 'profiles'}">
								<c:set var="actionVar" value="view_profile_listing"/>
								<c:set var="binderLabel" value='<%= NLT.get("general.profiles") %>'/>
								</c:if>								
							<div class="ss_entry">
								<div class="ss_entryHeader ss_search_hit">
									<div class="ss_entryTitleSearchResults">
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
											
									</div>
									<div class="ss_clear">&nbsp;</div>
								</div>
							</div>
							<div class="ss_clear">&nbsp;</div>
											
							<div id="details_${status.count}" class="ss_entryDetails">
								<p><ssf:showUser user="${entry._principal}" />
								   <span class="ss_search_results_entry_date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${entry._modificationDate}" type="both" timeStyle="short" dateStyle="medium" /></span>
								   </p>
									<p>${binderLabel}: <a 
									<c:if test="${isDashboard == 'yes'}">
										href="<ssf:permalink search="${entry}"/>"
										onClick="return ss_gotoPermalink('${entry._docId}','${entry._docId}', '${entry._entityType}', '${ss_namespace}', 'yes');"
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
		  <%
			entriesSeen.put(entry.get("_docId"), "1");
		  }
		  %>
		</c:forEach>
		</ul>
		
