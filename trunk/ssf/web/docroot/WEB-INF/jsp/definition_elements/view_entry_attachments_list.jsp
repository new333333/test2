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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ssfs.util.SsfsUtil" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<div id="${ss_viewEntryAttachmentDivId}">
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
boolean isAppletSupportedCheck = SsfsUtil.supportApplets();
String operatingSystem = BrowserSniffer.getOSInfo(request);
%>

<table class="ss_attachments_list" cellpadding="0" cellspacing="0">
<tbody>
<c:set var="selectionCount" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <jsp:useBean id="selection" type="com.sitescape.team.domain.FileAttachment" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="1"/>
  <c:if test="${versionCount >= 1}">
    <c:set var="thumbRowSpan" value="2"/>
  </c:if>
     <tr><td colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
	  <tr>

<%
	if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
	    //This is IE and a ppt file; use the edit app to launch powerpoint because of bug in IE and/or powerpoint
%>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		  <div class="ss_thumbnail_gallery ss_thumbnail_small"> 
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
				<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>" 
						onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
						    '${ssDefinitionEntry.id}', 
						    '${ss_attachments_namespace}', 
						    '<%= operatingSystem %>', 
							'${selection.id}');
							return false;"
				    	<ssf:title tag="title.open.file">
					      <ssf:param name="value" value="${selection.fileItem.name}" />
				    	</ssf:title>
					><img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
					  src="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    <ssf:param name="viewType" value="thumbnail"/>
					    </ssf:url>"/></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
					  src="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    <ssf:param name="viewType" value="thumbnail"/>
					    </ssf:url>"/></a>
			</ssf:editorTypeToUseForEditInPlace>

		  </div>
		</td>
		<td class="ss_att_title" width="25%">
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
				<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>" 
						onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
						    '${ssDefinitionEntry.id}', 
						    '${ss_attachments_namespace}', 
						    '<%= operatingSystem %>', 
							'${selection.id}');
							return false;"
				    	<ssf:title tag="title.open.file">
					      <ssf:param name="value" value="${selection.fileItem.name}" />
				    	</ssf:title>
					><c:out value="${selection.fileItem.name} "/></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><c:out value="${selection.fileItem.name} "/></a>
			</ssf:editorTypeToUseForEditInPlace>

			<c:if test="${!empty selection.fileLock}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" value="${selection.fileLock.owner.title}"/>
			  </ssf:nlt></span>
			</c:if>
		</td>
<%
	}

	if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
%>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		<div class="ss_thumbnail_gallery ss_thumbnail_small"> 
			<a style="text-decoration: none;" href="<ssf:url 
					    webPath="readFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    <ssf:param name="fileName" value="${selection.fileItem.name}"/>
					    </ssf:url>" 
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					
				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					     ><img border="0" <ssf:alt text="${selection.fileItem.name}"/> src="<ssf:url 
		    webPath="viewFile"
		    folderId="${ssDefinitionEntry.parentBinder.id}"
		    entryId="${ssDefinitionEntry.id}"
		    entityType="${ssDefinitionEntry.entityType}" >
		    <ssf:param name="fileId" value="${selection.id}"/>
		    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
		    <ssf:param name="viewType" value="thumbnail"/>
		    </ssf:url>"/></a>
	    </div>
		</td>
		<td style="height:20px;" class="ss_att_title" width="25%"><a style="text-decoration: none;" 
						href="<ssf:url 
					    webPath="readFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}"
					    entityType="${ssDefinitionEntry.entityType}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
					    <ssf:param name="fileName" value="${selection.fileItem.name}"/>
					    </ssf:url>" 
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"

				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					><c:out value="${selection.fileItem.name} "/></a>
			<c:if test="${!empty selection.fileLock}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" value="${selection.fileLock.owner.title}"/>
			  </ssf:nlt></span>
			</c:if>
		</td>
<%
	}
%>

		<td class="ss_att_meta" width="10%"></td>
		<td class="ss_att_meta">
		<ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
				<a target="_blank" style="text-decoration: none;" href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
			   	 	entryId="${ssDefinitionEntry.id}"
				    entityType="${ssDefinitionEntry.entityType}" >
			    	<ssf:param name="fileId" value="${selection.id}"/>
			    	<ssf:param name="fileTime" value="${selection.modification.date.time}"/>
			    	<ssf:param name="viewType" value="html"/>
			    	</ssf:url>" <ssf:title tag="title.open.file.in.html.format" /> ><span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="entry.HTML" />]</span></a>
		</ssf:ifSupportsViewAsHtml>
		</td>
		<td class="ss_att_meta">
		
		<ssf:ifnotaccessible>
		
			<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
				<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
					
					<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
					
						<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
					
							<a href="javascript: ;" 
								onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}', '<%= operatingSystem %>', 
									'${selection.id}');
									return false;">
								<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>

						</ssf:isFileEditorConfiguredForOS>
							
					</ssf:editorTypeToUseForEditInPlace>
					
					<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
						<a href="<ssf:ssfsInternalAttachmentUrl 
								binder="${ssDefinitionEntry.parentBinder}"
								entity="${ssDefinitionEntry}"
								fileAttachment="${selection}"/>">
								<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="EDIT"/>]</span></a>
					</ssf:editorTypeToUseForEditInPlace>
				
				</ssf:ifSupportsEditInPlace>
			</c:if>	
			
		</ssf:ifnotaccessible>
			
		</td>
		<td class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="both" 
					 timeStyle="medium" dateStyle="medium" /></td>
		<td class="ss_att_meta">${selection.fileItem.lengthKB}KB</td>
		<td class="ss_att_meta ss_att_space">${selection.modification.principal.title}</td>
		<td class="ss_att_meta" width="15%"></td>
	</tr>
	<c:if test="${!empty selection.fileVersions && versionCount > 1}">
        <tr><td style="height:10px;" class="ss_att_title" colspan="8"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
		<tr>
		  <td class="ss_att_title ss_subhead2" colspan="8">
		    <c:set var="previousVersionsText" value="<%= NLT.get("entry.PreviousVersions") %>"/>
		    <ssf:expandableArea title="${previousVersionsText}">
			  <table>
			  <c:forEach var="fileVersion" items="${selection.fileVersions}" begin="1" varStatus="status">
	          	<c:choose>
		          	<c:when test="${status.count == 4}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n">
							<td colspan="8" style="padding-left: 5px; font-weight: normal;"><a href="javascript: // " onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 4, 9)" class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>
		          	<c:when test="${status.count == 10}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
							<td colspan="8" style="padding-left: 5px; font-weight: normal;"><a href="javascript: // " onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 10, 20)" class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>	
		          	<c:when test="${status.count == 21}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
							<td colspan="8" style="padding-left: 5px; font-weight: normal;"><a href="javascript: // " onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 21, 40)" class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>	
		          	<c:when test="${status.count == 41}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
							<td colspan="8" style="padding-left: 5px; font-weight: normal;"><a href="javascript: // " onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 41, 80)" class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>
		          	<c:when test="${status.count == 81}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
							<td colspan="8" style="padding-left: 5px; font-weight: normal;"><a href="javascript: // " onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 81)" class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>				 	    
		 	    </c:choose>	 	    
		 	    
				<c:choose>
					<c:when test="${status.count <= 3}">
						<tr>
					</c:when>	
					<c:otherwise>						
						<tr id="${ss_attachments_namespace}att_row${status.count}" style="display: none; visibility: hidden; ">
					</c:otherwise>
				</c:choose>						
						
				<td class="ss_att_title" width="25%" style="padding-left: 5px; font-weight: normal;">
				<a style="text-decoration: none;"
				  href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
				    entryId="${ssDefinitionEntry.id}"
				    entityType="${ssDefinitionEntry.entityType}" >
				    <ssf:param name="fileId" value="${selection.id}"/>
				    <ssf:param name="versionId" value="${fileVersion.id}"/>
				    <ssf:param name="fileTime" value="${fileVersion.modification.date.time}"/>
				    </ssf:url>"
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					
				    <ssf:title tag="title.open.file.version">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
					    <ssf:param name="value" value="${fileVersion.versionNumber}" />
				    </ssf:title>
					
				    ><ssf:nlt tag="entry.Version"/> ${fileVersion.versionNumber}</a></td>
				<td class="ss_att_meta" width="10%"></td>
				<td class="ss_att_meta"></td>
				<td class="ss_att_meta"></td>    
				<td class="ss_att_meta ss_att_space"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="both" 
					 timeStyle="medium" dateStyle="medium" /></td>
				<td class="ss_att_meta">${fileVersion.fileItem.lengthKB}KB</td>
				<td width="25%" class="ss_att_meta ss_att_space">${fileVersion.modification.principal.title}</td>
				<td class="ss_att_meta" width="15%"></td>	
			  </tr>				
				
 	    	</c:forEach>
 	    	</table>
 	    	</ssf:expandableArea>
		  </td>
		</tr>
	</c:if>
</c:forEach>
<c:if test="${selectionCount > 0}">
     <tr><td colspan="9"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>

</div>
