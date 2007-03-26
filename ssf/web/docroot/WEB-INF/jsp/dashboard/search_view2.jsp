<%
// The dashboard "search" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="hitCount" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:set var="ss_pageSize" value="${ssDashboard.beans[componentId].ssSearchFormData.ss_pageSize}" />
<c:set var="summaryWordCount" value="30"/>
<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount[0]}">
  <c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount[0]}"/>
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<%
	Map entriesSeen = new HashMap();
%>
<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}">
<jsp:useBean id="fileEntry" type="java.util.HashMap" />
<%
	if (!entriesSeen.containsKey(fileEntry.get("_docId"))) {
%>
<c:set var="hitCount" value="${hitCount + 1}"/>
	<div class="ss_blog_summary_title">
		<table class="ss_searchviewDashboardContainer" cellspacing="0" cellpadding="0" width="100%" border="0" align="center">
	  	<tr>
			<td valign="top" class="ss_searchviewDashboardContainer">
			  	<c:choose>
				    <c:when test="${fileEntry._entityType == 'user'}">
				    	<img border="0" src="<html:imagesPath/>icons/user_profile.png" alt="<ssf:nlt tag="general.users" />" />
				    </c:when>
				    
				    <c:when test="${fileEntry._entityType == 'group'}">
				    	<img border="0" src="<html:imagesPath/>icons/group.gif" alt="<ssf:nlt tag="general.groups" />"/>
				    </c:when>

				    <c:when test="${fileEntry._entityType == 'folder'}">
				    	<c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}">
				    		<img border="0" src="<html:imagesPath/>${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}" alt="<ssf:nlt tag="general.type.folder" />"/>
				    	</c:if>
				    	<c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}">
				    		<img border="0" src="<html:imagesPath/>icons/folder.gif" alt="<ssf:nlt tag="general.type.folder" />"/>
				    	</c:if>
				    </c:when>

				    <c:when test="${fileEntry._entityType == 'workspace'}">
				    	<c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}">
				    		<img border="0" src="<html:imagesPath/>${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}" alt="<ssf:nlt tag="general.type.workspace" />"/>
				    	</c:if>
				    	<c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}">
				    		<img border="0" src="<html:imagesPath/>icons/workspace.gif" alt="<ssf:nlt tag="general.type.workspace" />"/>
				    	</c:if>
				    </c:when>

				    <c:when test="${fileEntry._entityType == 'profiles'}">
				    	<c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}">
				    		<img border="0" src="<html:imagesPath/>${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}" alt="<ssf:nlt tag="general.profiles" />"/>
				    	</c:if>
				    	<c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._docId].iconName}">
				    		<img border="0" src="<html:imagesPath/>icons/profiles.gif" alt="<ssf:nlt tag="general.profiles" />"/>
				    	</c:if>
				    </c:when>
			 	</c:choose>

				<span class="ss_entryTitle ss_underline">

				<ssf:menuLink 
					displayDiv="false" entryId="${fileEntry._docId}" 
					folderId="${fileEntry._binderId}" binderId="${fileEntry._binderId}" 
					entityType="${fileEntry._entityType}" imageId='menuimg_${fileEntry._docId}_${renderResponse.namespace}' 
			    	menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObj="ss_linkMenu${renderResponse.namespace}" 
					namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" isDashboard="yes">
					
					<ssf:param name="url" useBody="true">
					  	<c:choose>
						  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
								<ssf:url adapter="true" portletName="ss_forum" folderId="${fileEntry._binderId}" 
								action="view_folder_entry" entryId="${fileEntry._docId}" actionUrl="true" />						  	
						    </c:when>
						    
						    <c:when test="${fileEntry._entityType == 'user'}">
								<ssf:url adapter="true" portletName="ss_forum" action="view_permalink"
									binderId="${fileEntry._principal.workspaceId}">
									<ssf:param name="entityType" value="workspace" />
						    	    <ssf:param name="newTab" value="1"/>
								</ssf:url>
						    </c:when>
						    
						    <c:when test="${fileEntry._entityType == 'group'}">
							    <ssf:url action="view_profile_entry" 
						    		folderId="${fileEntry._binderId}"
						    		entryId="${fileEntry._docId}" />
						    </c:when>
						    
						    <c:when test="${fileEntry._entityType == 'folder' || fileEntry._entityType == 'workspace' || fileEntry._entityType == 'profiles'}">
							    <ssf:url adapter="true" portletName="ss_forum"
								    action="view_permalink"
								    binderId="${fileEntry._docId}">
								    <ssf:param name="entityType" value="${fileEntry._entityType}" />
						    	    <ssf:param name="newTab" value="1"/>
								</ssf:url>
						    </c:when>
					 	</c:choose>
					</ssf:param>
					    <c:if test="${empty fileEntry.title}">
					    	(<ssf:nlt tag="entry.noTitle"/>)
					    </c:if>
				    	<c:out value="${fileEntry.title}"/>
				</ssf:menuLink>
				
				</span>
		    	
				<c:if test="${fileEntry._entryHasMetaHit}">
				  <span class="ss_fineprint">[<ssf:nlt tag="search.textWasFoundInTheMetaData"/>]</span>
				</c:if>
				<c:if test="${empty fileEntry._entryHasMetaHit || !fileEntry._entryHasMetaHit}">
				  <span class="ss_fineprint">[<ssf:nlt tag="search.textWasNotFoundInTheMetaData"/>]</span>
				</c:if>
			</td>
		</tr>
		
		<tr>
			<td class="ss_searchviewDashboardContainer">
				<span class="ss_smallprint">
				  	<c:set var="binderId" value=""/>
				  	<c:set var="entryId" value=""/>
				  	<c:choose>
					  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
				  			<c:set var="binderId" value="${fileEntry._binderId}"/>
				  			<c:set var="entryId" value="${fileEntry._docId}"/>
					    </c:when>
					    <c:when test="${fileEntry._entityType == 'user'}">
				  			<c:set var="binderId" value="${fileEntry._principal.workspaceId}"/>
					    </c:when>
					    <c:when test="${fileEntry._entityType == 'group'}">
				  			<c:set var="binderId" value="${fileEntry._binderId}"/>
				  			<c:set var="entryId" value="${fileEntry._docId}"/>
					    </c:when>
					    <c:when test="${fileEntry._entityType == 'folder' || fileEntry._entityType == 'workspace' || fileEntry._entityType == 'profiles'}">
				  			<c:set var="binderId" value="${fileEntry._docId}"/>
					    </c:when>
				 	</c:choose>
					<ssf:markup type="view" binderId="${binderId}" entryId="${entryId}">
						<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
							${fileEntry._desc}
						</ssf:textFormat>
						
						<c:if test="${fileEntry._entityType == 'user'}">
							<ssf:textFormat formatAction="limitedDescription" textMaxWords="${summaryWordCount}">
								${fileEntry._comments}
							</ssf:textFormat>
						</c:if>
					</ssf:markup>
				</span>	
			</td>
		</tr>
		
		<tr>
			<td class="ss_smallprint">
				<table width="100%">
				<tr>
					<td width="50%" class="ss_smallprint">
						<c:if test="${fileEntry._entityType == 'folderEntry' || fileEntry._entityType == 'reply'}">
							<ssf:nlt tag="entry.Folder" />: 
							<a href="<ssf:url adapter="true" portletName="ss_forum" 
						    	action="view_permalink"
						    	binderId="${fileEntry._binderId}">
						    	<ssf:param name="entityType" value="folder" />
				    	    	<ssf:param name="newTab" value="1"/>
								</ssf:url>" 
								onClick="return ss_gotoPermalink('${fileEntry._binderId}', '', 'folder', '${portletNamespace}');">
								<span class="ss_underline">${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].title}</span>
								</a>
						</c:if>
					</td>
					<td width="50%" class="ss_smallprint">
						<c:if test="${!empty fileEntry._workflowStateCaption}">
							<ssf:nlt tag="entry.workflowState" />: <c:out value="${fileEntry._workflowStateCaption}" />
						</c:if>
					</td>
				</tr>
				</table>
			</td>
		</tr>
		
		<tr>
			<td>
				<table width="100%">
				<tr>
					<td width="50%" class="ss_smallprint">
						<ssf:nlt tag="entry.createdBy" />: <ssf:showUser user="${fileEntry._principal}" />
					</td>
					<td width="50%" class="ss_smallprint">
						<ssf:nlt tag="entry.modified" />: <fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
					 		value="${fileEntry._modificationDate}" type="both" 
					 		timeStyle="short" dateStyle="medium" />
					</td>
				</tr>
				</table>
			</td>
		</tr>
		
		<c:if test="${!empty fileEntry._attachments}"> 
			<tr>
				<td>
					<table width="100%">
					<c:forEach var="attachment" items="${fileEntry._attachments}">
					<tr>
						<td>
							<span class="ss_fineprint"><ssf:nlt tag="search.textFoundInFile"/></span>
							<span class="ss_bold"><a target="_blank" href="<ssf:url
							    	webPath="viewFile"
							    	binderId="${fileEntry._binderId}">
					    	    	<ssf:param name="entryId" value="${attachment._docId}"/>
					    	    	<ssf:param name="fileId" value="${attachment._fileID}"/>
									</ssf:url>">${attachment._fileName}</a></span>
						</td>
					</tr>
					</c:forEach>
					</table>
				</td>
			</tr>
		</c:if>
		
		<tr>
			<td><div class="ss_line"></div></td>
		</tr>
		
	  </table>
	</div>
<%	
	}
	entriesSeen.put(fileEntry.get("_docId"), "1");
%>
</c:forEach>

<div>
	<table width="98%">
	<tr>
		<td>
			<c:if test="${hitCount > 0}">
				<span class="ss_light ss_fineprint">
					[<ssf:nlt tag="search.results">
				    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
				    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
				    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
				    </ssf:nlt>]
				</span>
			</c:if>
			<c:if test="${hitCount == 0}">
				<span class="ss_light ss_fineprint">
				  [<ssf:nlt tag="search.noneFound"/>]
				</span>
			</c:if>
		</td>

		<td align="right">
			<c:if test="${ssDashboard.scope != 'portlet'}">
				<c:set var="binderId" value="${ssBinder.id}"/>
			</c:if>
			<c:if test="${ssDashboard.scope == 'portlet'}">
				<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
			</c:if>
			<c:if test="${ss_pageNumber > 0}">
				<span class="ss_light ss_fineprint">
				  <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'search'); return false;"
				    href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
				</span>
			</c:if>
			<c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
				<span class="ss_light ss_fineprint">&nbsp;&nbsp;
				  <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'search'); return false;"
				    href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
				</span>
			</c:if>
		</td>
	</tr>
	</table>
</div>

<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObj="ss_linkMenu${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>

<script type="text/javascript">
var ss_linkMenu${renderResponse.namespace} = new ss_linkMenuObj();
</script>