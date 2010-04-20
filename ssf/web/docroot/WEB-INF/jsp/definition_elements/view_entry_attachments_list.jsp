<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>
<c:set var="owningBinder" value="${ssBinder}"/>
<jsp:useBean id="owningBinder" type="org.kablink.teaming.domain.Binder" />

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<div id="${ss_viewEntryAttachmentDivId}">
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
boolean isAppletSupportedCheck = SsfsUtil.supportApplets(request);
String operatingSystem = BrowserSniffer.getOSInfo(request);
%>

<table class="ss_attachments_list" cellpadding="0" cellspacing="0" width="100%">
<tbody>
<c:set var="selectionCount" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <jsp:useBean id="selection" type="org.kablink.teaming.domain.FileAttachment" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
	String fnBr = "";
	int cCount = 0;
	for (int i = 0; i < fn.length(); i++) {
		String c = String.valueOf(fn.charAt(i));
		cCount++;
		if (c.matches("[\\W_]?") || cCount > 15) {
			fnBr += c + "<wbr/>";
			cCount = 0;
		} else {
			fnBr += c;
		}
	}
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="2"/>
  <c:if test="${versionCount >= 1}">
    <c:set var="thumbRowSpan" value="2"/>
  </c:if>
     <tr><td valign="top" colspan="7"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
	  <tr>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		<div class="ss_thumbnail_gallery ss_thumbnail_tiny"> 
		<%
			if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
		%>
			<a style="text-decoration: none;" href="<ssf:fileUrl file="${selection}"/>" 
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					
				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					     ><img border="0" <ssf:alt text="${selection.fileItem.name}"/> src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/></a>

		<%  }
			if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
		%>
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
					  src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
					  src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/></a>
			</ssf:editorTypeToUseForEditInPlace>
		<%  }  %>

	    </div>
		</td>
		
		<td valign="top" style="height:20px;" class="ss_att_title" width="30%">
		<%
			if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
		%>
		    <a style="text-decoration: none;" 
						href="<ssf:fileUrl file="${selection}"/>" 
					    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"

				    <ssf:title tag="title.open.file">
					    <ssf:param name="value" value="${selection.fileItem.name}" />
				    </ssf:title>
					><%= fnBr %><span style="padding-left:8px;"><ssf:nlt tag="file.versionNumber"><ssf:param
					name="value" value="${selection.fileVersion}"/></ssf:nlt></span></a>

		<%  }
			if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
		%>
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
					><%= fnBr %><span style="padding-left:8px;"><ssf:nlt tag="file.versionNumber"><ssf:param
					name="value" value="${selection.fileVersion}"/></ssf:nlt></span></a>
				</ssf:isFileEditorConfiguredForOS>
			</ssf:editorTypeToUseForEditInPlace>
			
			<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
				<a href="<ssf:ssfsInternalAttachmentUrl 
						binder="${ssDefinitionEntry.parentBinder}"
						entity="${ssDefinitionEntry}"
						fileAttachment="${selection}"/>"
				><%= fnBr %><span style="padding-left:8px;"><ssf:nlt tag="file.versionNumber"><ssf:param
					name="value" value="${selection.fileVersion}"/></ssf:nlt></span></a>
			</ssf:editorTypeToUseForEditInPlace>
		<%  }  %>

			<c:if test="${selection.currentlyLocked}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" value="${selection.fileLock.owner.title}"/>
			  </ssf:nlt></span>
			  <c:if test="${ss_canForceFileUnlock}">
			    <div>
			      <a href="<ssf:url     
				    adapter="true" 
				    portletName="ss_forum" 
				    binderId="${ssDefinitionEntry.parentBinder.id}" 
				    action="view_folder_entry" 
				    entryId="${ssDefinitionEntry.id}" actionUrl="true">
				    <ssf:param name="operation" value="force_unlock_file"/>
				    <ssf:param name="fileId" value="${selection.id}"/></ssf:url>"
				    onclick='if(confirm("<ssf:escapeJavaScript><ssf:nlt tag="entry.forceUnlockFileConfirm"/></ssf:escapeJavaScript>")){ss_postToThisUrl(this.href);return false;}else{return false};'
				    style="padding-left:10px;"
			        >
			        <span class="ss_fineprint"><ssf:nlt tag="entry.forceUnlockFile"/></span>
			      </a>
			    </div>
			  </c:if>
			</c:if>
		</td>

		<td valign="top" class="ss_att_meta" nowrap width="5%">
		 <c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
		  <div>
		    <a href="javascript: ;" onClick="ss_showHide('ss_fileStatusMenu_${selection.id}');return false;"
		    ><span id="fileStatus_${selection.id}">
		      <c:if test="${selection.fileStatus != 0}">${selection.fileStatusText}</c:if>
		      <c:if test="${selection.fileStatus == 0}"><ssf:nlt tag="file.statusNoStatus"/></c:if>
		      </span><img style="padding:0px 4px;" src="<html:imagesPath/>pics/menudown.gif" /></a>
		  </div>
		  <div id="ss_fileStatusMenu_${selection.id}" 
		    style="display:none; background:#fff; border:1px #ccc solid;">
		    <div><span class="ss_bold"><ssf:nlt tag="file.setStatus"/></span></div>
		    <ul style="margin:0px;padding:0px 10px 0px 10px;">
			  <li>
			    <a href="javascript: ;" 
			      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${selection.id}', 'fileStatus_${selection.id}', '0');return false;">
			      <ssf:nlt tag="file.statusNone"/>
			    </a>
			  </li>
			  <li>
			    <a href="javascript: ;" 
			      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${selection.id}', 'fileStatus_${selection.id}', '1');return false;">
			      <ssf:nlt tag="file.status1"/>
			    </a>
			  </li>
			  <li>
			    <a href="javascript: ;" 
			      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${selection.id}', 'fileStatus_${selection.id}', '2');return false;">
			      <ssf:nlt tag="file.status2"/>
			    </a>
			  </li>
			  <li>
			    <a href="javascript: ;" 
			      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${selection.id}', 'fileStatus_${selection.id}', '3');return false;">
			      <ssf:nlt tag="file.status3"/>
			    </a>
			  </li>
			</ul>
		  </div>
		 </c:if>
		 <c:if test="${!ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
		  <div>
		    <span>${selection.fileStatusText}</span>
		  </div>
		 </c:if>
		</td>
		
		<td valign="top" width="15%"><span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="date" 
					 dateStyle="medium" /></span> <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="time" 
					 timeStyle="short"/></span></td>
		<td valign="top" class="ss_att_meta" nowrap width="5%">
		  <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber value="${selection.fileItem.lengthKB}"/> 
		  <ssf:nlt tag="file.sizeKB" text="KB"/>
		</td>
		
		<td valign="top" class="ss_att_meta_wrap ss_att_space" width="25%">${selection.modification.principal.title}</td>
		<td valign="top" class="ss_att_meta" width="20%">
		  <a class="ss_tinyButton ss_fineprint" href="javascript: ;" 
		    onClick="ss_showHide('ss_fileActionsMenu_${selection.id}');return false;"
		  ><ssf:nlt tag="file.actions"/></a>
		  <div id="ss_fileActionsMenu_${selection.id}" 
		    style="display:none; background:#fff; border:1px #ccc solid;">
		    <ul style="margin:0px;padding:0px 10px 0px 10px;">
			  <li>
				<%
					if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
				%>
					<a style="text-decoration: none;" href="<ssf:fileUrl file="${selection}"/>" 
					  onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${selection.fileItem.name}"/>');"
					><span><ssf:nlt tag="file.view"/></span></a>
		
				<%  }
					if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
				%>
					<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
						<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" 
						  operatingSystem="<%= operatingSystem %>">
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
							><span><ssf:nlt tag="file.view"/></span></a>
						</ssf:isFileEditorConfiguredForOS>
					</ssf:editorTypeToUseForEditInPlace>
					
					<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
						<a href="<ssf:ssfsInternalAttachmentUrl 
								binder="${ssDefinitionEntry.parentBinder}"
								entity="${ssDefinitionEntry}"
								fileAttachment="${selection}"/>"
						><span><ssf:nlt tag="file.view"/></span></a>
					</ssf:editorTypeToUseForEditInPlace>
				<%  }  %>
			  </li>

			  <ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
				<li>
				  <a target="_blank" style="text-decoration: none;" href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
			   	 	entryId="${ssDefinitionEntry.id}"
				    entityType="${ssDefinitionEntry.entityType}" >
			    	<ssf:param name="fileId" value="${selection.id}"/>
			    	<ssf:param name="fileTime" value="${selection.modification.date.time}"/>
			    	<ssf:param name="viewType" value="html"/>
			    	</ssf:url>" title="<ssf:nlt tag="title.open.file.in.html.format" />" 
			       ><span><ssf:nlt tag="file.viewAsHtml" /></span></a>
			    </li>
			  </ssf:ifSupportsViewAsHtml>

			  <ssf:ifnotaccessible>
				<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
					<ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
						<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
							<ssf:isFileEditorConfiguredForOS relativeFilePath="${selection.fileItem.name}" operatingSystem="<%= operatingSystem %>">
								<c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
								  <li>
								    <a href="javascript: ;" 
									  onClick='javascript:<c:if test="${!empty ss_quotaMessage}">alert("${ss_quotaMessage}");</c:if>
									    ss_openWebDAVFile("${ssDefinitionEntry.parentBinder.id}", "${ssDefinitionEntry.id}", "${ss_attachments_namespace}", "<%= operatingSystem %>", 
										"${selection.id}");
										return false;'
								    ><span><ssf:nlt tag="file.editFile"/></span></a>
								  </li>
								</c:if>
								<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
								  <li>
								  <a href="javascript: ;" 
									onClick='alert("${ss_quotaMessage}");return false;'
								  ><span><ssf:nlt tag="file.editFile"/></span></a>
								  </li>
								</c:if>
							</ssf:isFileEditorConfiguredForOS>
						</ssf:editorTypeToUseForEditInPlace>
							
						<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
							  <c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
							    <li>
							      <a href="<ssf:ssfsInternalAttachmentUrl 
									binder="${ssDefinitionEntry.parentBinder}"
									entity="${ssDefinitionEntry}"
									fileAttachment="${selection}"/>"
								  <c:if test="${!empty ss_quotaMessage}">onClick='alert("${ss_quotaMessage}");'</c:if>
								  ><span><ssf:nlt tag="file.editFile"/></span></a>
								</li>
							  </c:if>
							  <c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
							    <li>
								  <a href="javascript: ;" 
									onClick='alert("${ss_quotaMessage}");return false;'
								  ><span><ssf:nlt tag="file.editFile"/></span></a>
								</li>
							  </c:if>
						</ssf:editorTypeToUseForEditInPlace>
					
					</ssf:ifSupportsEditInPlace>
				  </c:if>	
				</ssf:ifnotaccessible>
				
				<li>
				  <a target="_blank" style="text-decoration: none;" 
				    href="<ssf:fileUrl zipUrl="true" entity="${ssDefinitionEntry}" fileId="${selection.id}" />" 
			       ><span><ssf:nlt tag="file.downloadAsZip" /></span></a>
				</li>
				
				<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
				  <li>
				    <a href="<ssf:url
					    adapter="true" 
					    portletName="ss_forum" 
					    action="modify_file" 
					    actionUrl="false" 
					    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
					    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
					    name="fileId" value="${selection.id}"/><ssf:param 
					    name="operation" value="modify_file_description"/></ssf:url>"
				      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
					><span><ssf:nlt tag="file.editFileComment"/></span></a>
				  </li>
				</c:if>

				<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['deleteEntry']}">
				  <li>
				    <a href="<ssf:url
					    adapter="true" 
					    portletName="ss_forum" 
					    action="modify_file" 
					    actionUrl="false" 
					    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
					    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
					    name="fileId" value="${selection.id}"/><ssf:param 
					    name="operation" value="delete"/></ssf:url>"
				      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
					><span><ssf:nlt tag="file.deleteVersion"/></span></a>
				  </li>
				</c:if>

			</ul>
		  </div>
		</td>
	</tr>
	<tr>
	  <td valign="top" colspan="6" class="ss_att_description" width="100%">
	    <div><ssf:markup type="view" entity="${ssDefinitionEntry}">${selection.fileItem.description.text}</ssf:markup></div>
	  </td>
	</tr>	
	<c:if test="${!empty selection.fileVersions && versionCount > 1}">
        <tr><td valign="top" style="height:10px;" class="ss_att_title" colspan="7">
          <hr class="ss_att_divider" noshade="noshade" /></td></tr>
		<tr>
		  <td valign="top" class="ss_att_title ss_subhead2" colspan="7">
		    <c:set var="previousVersionsText" value='<%= NLT.get("entry.PreviousVersions", new String[] {String.valueOf(selection.getFileVersions().size()-1)}) %>'/>
		    <c:if test="<%= owningBinder.isMirrored() %>">
		      <c:set var="previousVersionsText" value='<%= NLT.get("entry.PreviousVersionsMirrored", new String[] {String.valueOf(selection.getFileVersions().size()-1)}) %>'/>
		    </c:if>
		    <ssf:expandableArea title="${previousVersionsText}">
			  <table class="ss_attachments_list" cellpadding="0" cellspacing="0" width="100%">
			  <c:forEach var="fileVersion" items="${selection.fileVersions}" begin="1" varStatus="status">
<%
	String vfn = selection.getFileItem().getName();
	String vext = "";
	if (vfn.lastIndexOf(".") >= 0) vext = vfn.substring(vfn.lastIndexOf("."));
	String vfnBr = "";
	int vcCount = 0;
	for (int i = 0; i < vfn.length(); i++) {
		String c = String.valueOf(vfn.charAt(i));
		vcCount++;
		if (c.matches("[\\W_]?") || vcCount > 15) {
			vfnBr += c + "<wbr/>";
			vcCount = 0;
		} else {
			vfnBr += c;
		}
	}
%>
	          	<c:choose>
		          	<c:when test="${status.count == 4}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n"
						   style="display: block; visibility: visible; ">
						    <td width="80">
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="6" style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							    onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 4, 9)" 
							    class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>
		          	<c:when test="${status.count == 10}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" 
						   style="display: none; visibility: hidden; ">
						    <td width="80">
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="6" style="padding-left: 5px; font-weight: normal;">
							<a href="javascript: // " 
							  onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 10, 20)" 
							  class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>	
		          	<c:when test="${status.count == 21}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
						    <td width="80">
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="6" style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							    onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 21, 40)" 
							    class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>	
		          	<c:when test="${status.count == 41}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
						    <td width="80">
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="6" 
							  style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							  onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 41, 80)" 
							  class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>
		          	<c:when test="${status.count == 81}">
						 <tr id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
						    <td width="80">
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="6" style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							    onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 81)" 
							    class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>				 	    
		 	    </c:choose>	 	    
		 	    
				<c:choose>
					<c:when test="${status.count <= 3}">
						<tr style="display: block; visibility: visible;">
					</c:when>	
					<c:otherwise>						
						<tr id="${ss_attachments_namespace}att_row${status.count}" style="display: none; visibility: hidden; ">
					</c:otherwise>
				</c:choose>						
						
				<td width="80">
				  <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
				    <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
				      src="<html:imagesPath/>pics/1pix.gif"/>
				  </div>
				</td>
				<td valign="top" class="ss_att_title" width="30%" style="font-weight: normal;">
				<c:if test="<%= !owningBinder.isMirrored() %>">
					<a style="text-decoration: none;"
					  href="<ssf:fileUrl file="${fileVersion}"/>" 
						    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${fileVersion.fileItem.name}"/>');"
						
					    <ssf:title tag="title.open.file">
						    <ssf:param name="value" value="${fileVersion.fileItem.name}" />
					    </ssf:title>
						><%= vfnBr %><span style="padding-left:8px;"><ssf:nlt tag="file.versionNumber"><ssf:param
						name="value" value="${fileVersion.fileVersion}"/></ssf:nlt></span></a>
				</c:if>
				<c:if test="<%= owningBinder.isMirrored() %>">
					<span><ssf:nlt tag="entry.Version"/> ${fileVersion.fileVersion}</span>
				</c:if>
				</td>

				<td valign="top" class="ss_att_meta" nowrap width="5%">
				 <c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
				  <div>
				    <a href="javascript: ;" onClick="ss_showHide('ss_fileStatusMenu_${fileVersion.id}');return false;"
				    ><span id="fileStatus_${fileVersion.id}">
				      <c:if test="${fileVersion.fileStatus != 0}">${fileVersion.fileStatusText}</c:if>
				      <c:if test="${fileVersion.fileStatus == 0}"><ssf:nlt tag="file.statusNoStatus"/></c:if>
				      </span>
				      <img style="padding:0px 4px;" src="<html:imagesPath/>pics/menudown.gif" /></a>
				  </div>
				  <div id="ss_fileStatusMenu_${fileVersion.id}" 
				    style="display:none; background:#fff; border:1px #ccc solid;">
		    		<div><span class="ss_bold"><ssf:nlt tag="file.setStatus"/></span></div>
				    <ul style="margin:0px;padding:0px 10px 0px 10px;">
					  <li>
					    <a href="javascript: ;" 
					      onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${fileVersion.id}', 'fileStatus_${fileVersion.id}', '0');return false;">
					      <ssf:nlt tag="file.statusNone"/>
					    </a>
					  </li>
					  <li>
					    <a href="javascript: ;" 
					    onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${fileVersion.id}', 'fileStatus_${fileVersion.id}', '1');return false;">
					      <ssf:nlt tag="file.status1"/>
					    </a>
					  </li>
					  <li>
					    <a href="javascript: ;" 
					    onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${fileVersion.id}', 'fileStatus_${fileVersion.id}', '2');return false;">
					      <ssf:nlt tag="file.status2"/>
					    </a>
					  </li>
					  <li>
					    <a href="javascript: ;" 
					    onClick="ss_setFileStatus('${ssDefinitionEntry.id}', '${ssDefinitionEntry.entityType}', '${fileVersion.id}', 'fileStatus_${fileVersion.id}', '3');return false;">
					      <ssf:nlt tag="file.status3"/>
					    </a>
					  </li>
					</ul>
				  </div>
				 </c:if>
				 <c:if test="${!ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
				  <div>
				    <span>${fileVersion.fileStatusText}</span>
				  </div>
				 </c:if>
				</td>
		
				<td valign="top" width="15%">
				  <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="date" 
					 dateStyle="medium" /></span> <span class="ss_att_meta"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="time" 
					 timeStyle="short" /></span></td>
				<td valign="top" class="ss_att_meta" nowrap width="5%"><fmt:setLocale value="${ssUser.locale}"/><fmt:formatNumber value="${fileVersion.fileItem.lengthKB}"/> <ssf:nlt tag="file.sizeKB" text="KB"/></td>
				<td valign="top" class="ss_att_meta_wrap ss_att_space" width="25%">${fileVersion.modification.principal.title}</td>
				<td valign="top" class="ss_att_meta" width="20%">
				  <a class="ss_tinyButton ss_fineprint" href="javascript: ;" 
				    onClick="ss_showHide('ss_fileActionsMenu_${fileVersion.versionNumber}');return false;"
				  ><ssf:nlt tag="file.actions"/></a>
				  <div id="ss_fileActionsMenu_${fileVersion.versionNumber}" 
				     style="display:none; background:#fff; border:1px #ccc solid;">
		    		<ul style="margin:0px;padding:0px 10px 0px 10px;">
						<c:if test="<%= !owningBinder.isMirrored() %>">
		    		      <li>
							<a style="text-decoration: none;"
							  href="<ssf:fileUrl file="${fileVersion}"/>" 
								    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${fileVersion.fileItem.name}"/>');"
							><span><ssf:nlt tag="file.view"/></span></a>
		    		      </li>
						</c:if>

						<li>
						  <a target="_blank" style="text-decoration: none;" 
						    href="<ssf:fileUrl zipUrl="true" entity="${ssDefinitionEntry}" fileId="${fileVersion.id}" />" 
					       ><span><ssf:nlt tag="file.downloadAsZip" /></span></a>
						</li>

						<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
						  <li>
						    <a href="<ssf:url
							    adapter="true" 
							    portletName="ss_forum" 
							    action="modify_file" 
							    actionUrl="false" 
							    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
							    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
							    name="fileId" value="${fileVersion.id}"/><ssf:param 
							    name="operation" value="modify_file_description"/></ssf:url>"
						      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
							><span><ssf:nlt tag="file.editFileComment"/></span></a>
						  </li>
						</c:if>

						<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['deleteEntry']}">
						  <li>
						    <a href="<ssf:url
							    adapter="true" 
							    portletName="ss_forum" 
							    action="modify_file" 
							    actionUrl="false" 
							    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
							    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
							    name="fileId" value="${fileVersion.id}"/><ssf:param 
							    name="operation" value="delete"/></ssf:url>"
						      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
							><span><ssf:nlt tag="file.deleteVersion"/></span></a>
						  </li>
						</c:if>

						<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
						  <li>
						    <a href="<ssf:url
							    adapter="true" 
							    portletName="ss_forum" 
							    action="modify_file" 
							    actionUrl="false" 
							    ><ssf:param name="entityId" value="${ssDefinitionEntry.id}"/><ssf:param 
							    name="entityType" value="${ssDefinitionEntry.entityType}"/><ssf:param 
							    name="fileId" value="${fileVersion.id}"/><ssf:param 
							    name="operation" value="modify_file_revert"/></ssf:url>"
						      onClick="ss_openUrlInPortlet(this.href, true, '500', '400');return false;"
							><span><ssf:nlt tag="file.revertVersion"/></span></a>
						  </li>
						</c:if>

				    </ul>
				  </div>
				</td>	
			  </tr>	
			  <tr style="display: block; visibility: visible;">
				<td width="80">
				  <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
					<img border="0" style="border:0px none #fff; width:35px;height:1px;" 
					  src="<html:imagesPath/>pics/1pix.gif"/>
				  </div>
				</td>
			    <td valign="top" width="100%" colspan="6" class="ss_att_description">
			      <div><ssf:markup type="view" entity="${ssDefinitionEntry}">${fileVersion.fileItem.description.text}</ssf:markup></div>
			    </td>
			  </tr>	
				
 	    	</c:forEach>
 	    	</table>
 	    	</ssf:expandableArea>
		  </td>
		</tr>
	</c:if>
</c:forEach>
<c:if test="${selectionCount > 0}">
     <tr><td valign="top" colspan="7"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>

</div>
