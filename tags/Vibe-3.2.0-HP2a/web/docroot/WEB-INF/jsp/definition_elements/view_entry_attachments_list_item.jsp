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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_divCounter" value="${ss_divCounter + 1}" scope="request" />

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
boolean isAppletSupportedCheck = SsfsUtil.supportApplets(request);
String operatingSystem = BrowserSniffer.getOSInfo(request);
%>
  <c:set var="selection" value="${ss_attachedFileSelection}" />
  <jsp:useBean id="selection" type="org.kablink.teaming.domain.FileAttachment" />
  <c:set var="ss_attachedFile" value="${selection}" scope="request" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${selection.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

<c:set var="owningBinder" value="${ssBinder}"/>
<c:if test="${empty owningBinder}">
<c:set var="owningBinder" value="${selection.owner.entity.parentBinder}"/>
</c:if>
<jsp:useBean id="owningBinder" type="org.kablink.teaming.domain.Binder" />

  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="2"/>
  <c:if test="${versionCount >= 1}">
    <c:set var="thumbRowSpan" value="2"/>
  </c:if>
	  <tr class="ss_tab_table_row">
		<td rowspan="${thumbRowSpan}">
		<div class="ss_thumbnail_gallery ss_thumbnail_tiny"> 
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <c:if test="${selection.fileExists && (!ss_pseudoEntity || !ss_isBinderMirroredFolder)}">
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_thumbnail.jsp" />
          </c:if>
          <c:if test="${selection.fileExists && ss_pseudoEntity && ss_isBinderMirroredFolder}">
            <img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
			       src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/>
          </c:if>
          <c:if test="${!selection.fileExists}">
            <span class="ss_fineprint"><ssf:nlt tag="milestone.folder.deleted"/></span>
          </c:if>
	    </div>
		</td>
		
		<td class="ss_att_title" style="border: 0px; 
		  <% if (!BrowserSniffer.is_ie(request) && selection.getFileItem().getName().length() <= 80) { %> white-space: nowrap; <% } %>
		  <% if (BrowserSniffer.is_ie(request) || selection.getFileItem().getName().length() > 80) { %> white-space: normal; <% } %>
		  ">
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <c:if test="${selection.fileExists && (!ss_pseudoEntity || !ss_isBinderMirroredFolder)}">
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_title.jsp" />
          </c:if>
          <c:if test="${!selection.fileExists || (ss_pseudoEntity && ss_isBinderMirroredFolder)}">
            ${selection.fileItem.name}
          </c:if>
		</td>
		<td class="ss_att_meta" style="border: 0px;">
		  <ssf:nlt tag="file.versionNumber"><ssf:param name="value" value="${selection.fileVersion}"/></ssf:nlt>
		</td>

		<td class="ss_att_meta" style="border: 0px;">
          <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_status.jsp" />
		</td>
		
		<td class="ss_att_meta" style="border: 0px;"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="date" 
					 dateStyle="medium" />&nbsp;&nbsp;<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${selection.modification.date}" type="time" 
					 timeStyle="short"/>
		</td>
		<td class="ss_att_meta" style="border: 0px;">
		  <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber value="${selection.fileItem.lengthKB}"/> 
		  <ssf:nlt tag="file.sizeKB" text="KB"/>
		</td>
		
		<td class="ss_att_meta_wrap" style="border: 0px;"><ssf:showUser user="${selection.modification.principal}"/></td>
		<td class="ss_att_meta" style="border: 0px;">
          <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
          <c:set var="ss_attachedFile" value="${selection}" scope="request" />
          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions.jsp" />
		</td>
		<td class="ss_att_meta" style="border: 0px;">
            <c:set var="ss_attachedFileIsVersion" value="false" scope="request" />
            <c:set var="ss_attachedFile" value="${selection}" scope="request" />
            <c:set var="ss_attachedFileShowEditButton" value="true" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions_edit_button.jsp" />
            <c:set var="ss_attachedFileShowEditButton" value="false" scope="request"/>
		</td>
		<td width="100%" style="border: 0px;">&nbsp;</td>
	</tr>

	<tr class="ss_tab_table_row">
	  <td colspan="9" class="ss_att_description" style="white-space: normal;">
	    <ssf:markup type="view" entity="${ssDefinitionEntry}">${selection.fileItem.description.text}</ssf:markup>
	  </td>
	</tr>	

	<c:if test="${!ss_showPrimaryFileAttachmentOnly}">
	<c:if test="${!empty selection.fileVersions && versionCount > 1}">
        <tr class="ss_tab_table_row">
			<td class="ss_att_title" style="border: 0px;" colspan="9"></td>
		</tr>
		<tr class="ss_tab_table_row">
		  <td class="ss_att_title ss_subhead2" style="border: 0px;" colspan="9">
		    <c:set var="previousVersionsText" value='<%= NLT.get("entry.PreviousVersions", new String[] {String.valueOf(selection.getFileVersions().size()-1)}) %>'/>
		    <c:if test="<%= owningBinder.isMirrored() %>">
		      <c:set var="previousVersionsText" value='<%= NLT.get("entry.PreviousVersionsMirrored", new String[] {String.valueOf(selection.getFileVersions().size()-1)}) %>'/>
		    </c:if>
		    <ssf:expandableArea title="${previousVersionsText}" titleClass="ss_normal" 
		      toggleClass="ss_expandable_area_toggle_indent20">
			  <table class="ss_attachments_list" cellpadding="0" cellspacing="0">
			  <c:forEach var="fileVersion" items="${selection.fileVersions}" begin="1" varStatus="status">
<%
	String vfn = selection.getFileItem().getName();
	String vext = "";
	if (vfn.lastIndexOf(".") >= 0) vext = vfn.substring(vfn.lastIndexOf("."));
	String vfnBr = "";
	int vcCount = 0;
	if (vfn.length() > 40) {
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
	} else {
		vfnBr = vfn;
	}
%>
	          	<c:choose>
		          	<c:when test="${status.count == 4}">
						 <tr class="ss_tab_table_row" id="${ss_attachments_namespace}att_row${status.count}n"
						   style="display: block; visibility: visible; ">
						    <td>
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="9" style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							    onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 4, 9);ss_showAttachmentVersions('${ss_attachments_namespace}att_desc_row', 4, 9);" 
							    class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>
		          	<c:when test="${status.count == 10}">
						 <tr class="ss_tab_table_row" id="${ss_attachments_namespace}att_row${status.count}n" 
						   style="display: none; visibility: hidden; ">
						    <td>
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="9" style="padding-left: 5px; font-weight: normal;">
							<a href="javascript: // " 
							  onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 10, 20);ss_showAttachmentVersions('${ss_attachments_namespace}att_desc_row', 10, 20);" 
							  class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>	
		          	<c:when test="${status.count == 21}">
						 <tr class="ss_tab_table_row" id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
						    <td>
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="9" style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							    onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 21, 40);ss_showAttachmentVersions('${ss_attachments_namespace}att_desc_row', 21, 40);" 
							    class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>	
		          	<c:when test="${status.count == 41}">
						 <tr class="ss_tab_table_row" id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
						    <td>
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="9" 
							  style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							  onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 41, 80);ss_showAttachmentVersions('${ss_attachments_namespace}att_desc_row', 41, 80);" 
							  class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>
		          	<c:when test="${status.count == 81}">
						 <tr class="ss_tab_table_row" id="${ss_attachments_namespace}att_row${status.count}n" style="display: none; visibility: hidden; ">
						    <td>
						      <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
						        <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
						          src="<html:imagesPath/>pics/1pix.gif"/>
						      </div>
						    </td>
							<td valign="top" colspan="9" style="padding-left: 5px; font-weight: normal;">
							  <a href="javascript: // " 
							    onclick="ss_showAttachmentVersions('${ss_attachments_namespace}att_row', 81);ss_showAttachmentVersions('${ss_attachments_namespace}att_desc_row', 81);" 
							    class="ss_light ss_fineprint"><ssf:nlt tag="entry.ShowOlderVersions"/></a></td>
						 </tr>
			 	    </c:when>				 	    
		 	    </c:choose>	 	    
		 	    
				<tr class="ss_tab_table_row" 
				<c:choose>
					<c:when test="${status.count <= 3}">
						style="display: block; visibility: visible;">
					</c:when>	
					<c:otherwise>						
						id="${ss_attachments_namespace}att_row${status.count}" style="display: none; visibility: hidden; ">
					</c:otherwise>
				</c:choose>	
				>					
						
				<td>
				  <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
				    <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
				      src="<html:imagesPath/>pics/1pix.gif"/>
				  </div>
				</td>
				<td class="ss_att_title" style="font-weight: normal; border: 0px;">
				<c:if test="<%= !owningBinder.isMirrored() %>">
					<a style="text-decoration: none;"
					  href="<ssf:fileUrl file="${fileVersion}"/>" 
						    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${fileVersion.fileItem.name}"/>');"
						
					    <ssf:title tag="title.open.file">
						    <ssf:param name="value" value="${fileVersion.fileItem.name}" />
					    </ssf:title>
						><%= vfnBr %></a>
						<c:if test="${fileVersion.encrypted}">
					        <img src="<html:imagesPath/>pics/encrypted.png" 
					          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
						</c:if>
				</c:if>
				</td>
				<td class="ss_att_meta" style="border: 0px;"><ssf:nlt tag="file.versionNumber"><ssf:param
						name="value" value="${fileVersion.fileVersion}"/></ssf:nlt>
				</td>

				<td class="ss_att_meta" style="border: 0px;">
		          <c:set var="ss_attachedFileIsVersion" value="true" scope="request" />
		          <c:set var="ss_attachedFile" value="${fileVersion}" scope="request" />
		          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_status.jsp" />
				</td>
		
				<td class="ss_att_meta" style="border: 0px;">
				  <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="date" 
					 dateStyle="medium" />&nbsp;&nbsp;<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				     value="${fileVersion.modification.date}" type="time" 
					 timeStyle="short" />
				</td>
				<td class="ss_att_meta" style="border: 0px;">
					<fmt:setLocale value="${ssUser.locale}"/><fmt:formatNumber value="${fileVersion.fileItem.lengthKB}"/> <ssf:nlt tag="file.sizeKB" text="KB"/>
				</td>
				<td class="ss_att_meta_wrap" style="border: 0px;">
				  <ssf:showUser user="${fileVersion.modification.principal}"/>
				 </td>
				<td class="ss_att_meta" style="border: 0px;">
		          <c:set var="ss_attachedFileIsVersion" value="true" scope="request" />
		          <c:set var="ss_attachedFile" value="${fileVersion}" scope="request" />
		          <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_attachment_actions.jsp" />
				</td>
				<td class="ss_att_meta" style="border: 0px;">
				</td>
				<td width="100%" style="border: 0px">&nbsp;</td>	
			  </tr>	
			  <tr class="ss_tab_table_row" 
			  <c:choose>
				<c:when test="${status.count <= 3}">
					style="display: block; visibility: visible;"
				</c:when>	
				<c:otherwise>						
					id="${ss_attachments_namespace}att_desc_row${status.count}" style="display: none; visibility: hidden; "
				</c:otherwise>
			  </c:choose>
			  >						
				<td>
				  <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
					<img border="0" style="border:0px none #fff; width:35px;height:1px;" 
					  src="<html:imagesPath/>pics/1pix.gif"/>
				  </div>
				</td>
			    <td colspan="9" class="ss_att_description" style="white-space: normal;">
			      <ssf:markup type="view" entity="${ssDefinitionEntry}">${fileVersion.fileItem.description.text}</ssf:markup>
			    </td>
			  </tr>	
				
 	    	</c:forEach>
 	    	</table>
 	    	</ssf:expandableArea>
		  </td>
		</tr>
	</c:if>
	</c:if>
