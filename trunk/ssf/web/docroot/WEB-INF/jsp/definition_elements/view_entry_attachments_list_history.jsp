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
<c:set var="owningBinder" value="${ssBinder}"/>
<jsp:useBean id="owningBinder" type="org.kablink.teaming.domain.Binder" />

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<div>

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
  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="2"/>
  <c:if test="${versionCount >= 1}">
    <c:set var="thumbRowSpan" value="2"/>
  </c:if>
     <tr><td valign="top" colspan="8"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
	  <tr>
	    <td valign="top" class="ss_att_meta" nowrap width="5%">
	      <c:if test="${!empty ss_pseudoEntityRevert}">
	        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);" checked />
	      </c:if>
	    </td>
		<td valign="top" width="80" rowspan="${thumbRowSpan}">
		<div class="ss_thumbnail_gallery ss_thumbnail_tiny"> 
			<img border="0" <ssf:alt text="${selection.fileItem.name}"/> 
			  src="<ssf:fileUrl webPath="readThumbnail" file="${selection}"/>"/>
	    </div>
		</td>
		
		<td valign="top" style="height:20px;" class="ss_att_title" width="30%">
		    <%= fnBr %><span style="padding-left:8px;"><ssf:nlt tag="file.versionNumber"><ssf:param
					name="value" value="${selection.fileVersion}"/></ssf:nlt></span>
		</td>

		<td valign="top" class="ss_att_meta" nowrap width="5%">
		  <div>
		    <span>${selection.fileStatusText}</span>
		  </div>
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
		</td>
	</tr>
	<tr>
	  <td valign="top" colspan="7" class="ss_att_description" width="100%">
	    <div><ssf:markup type="view" entity="${ssDefinitionEntry}">${selection.fileItem.description.text}</ssf:markup></div>
	  </td>
	</tr>	
	<c:if test="${!empty selection.fileVersions && versionCount > 1}">
        <tr><td valign="top" style="height:10px;" class="ss_att_title" colspan="8">
          <hr class="ss_att_divider" noshade="noshade" /></td></tr>
		<tr>
		  <td valign="top" class="ss_att_title ss_subhead2" colspan="8">
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
						    <td valign="top" class="ss_att_meta" nowrap width="5%">
	      					  <c:if test="${!empty ss_pseudoEntityRevert}">
						        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
						      </c:if>
						    </td>
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
						    <td valign="top" class="ss_att_meta" nowrap width="5%">
						      <c:if test="${!empty ss_pseudoEntityRevert}">
						        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
						      </c:if>
						    </td>
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
						    <td valign="top" class="ss_att_meta" nowrap width="5%">
						      <c:if test="${!empty ss_pseudoEntityRevert}">
						        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
						      </c:if>
						    </td>
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
						    <td valign="top" class="ss_att_meta" nowrap width="5%">
						      <c:if test="${!empty ss_pseudoEntityRevert}">
						        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
						      </c:if>
						    </td>
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
						    <td valign="top" class="ss_att_meta" nowrap width="5%">
						      <c:if test="${!empty ss_pseudoEntityRevert}">
						        <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
						      </c:if>
						    </td>
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
						
				<td valign="top" class="ss_att_meta" nowrap width="5%">
				  <c:if test="${!empty ss_pseudoEntityRevert}">
				    <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
				  </c:if>
				</td>
				<td width="80">
				  <div class="ss_thumbnail_gallery ss_thumbnail_tiny">
				    <img border="0" style="border:0px none #fff; width:35px;height:1px;" 
				      src="<html:imagesPath/>pics/1pix.gif"/>
				  </div>
				</td>
				<td valign="top" class="ss_att_title" width="30%" style="font-weight: normal;">
				<c:if test="<%= !owningBinder.isMirrored() %>">
					<%= vfnBr %><span style="padding-left:8px;"><ssf:nlt tag="file.versionNumber"><ssf:param
						name="value" value="${fileVersion.fileVersion}"/></ssf:nlt></span>
				</c:if>
				<c:if test="<%= owningBinder.isMirrored() %>">
					<span><ssf:nlt tag="entry.Version"/> ${fileVersion.fileVersion}</span>
				</c:if>
				</td>

				<td valign="top" class="ss_att_meta" nowrap width="5%">
				  <div>
				    <span>${fileVersion.fileStatusText}</span>
				  </div>
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
				</td>	
			  </tr>	
			  <tr style="display: block; visibility: visible;">
				<td valign="top" class="ss_att_meta" nowrap width="5%">
				  <c:if test="${!empty ss_pseudoEntityRevert}">
				    <input type="checkbox" name="file_revert_${selection.id}" onChange="saveFileId(this);"/>
				  </c:if>
				</td>
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
     <tr><td valign="top" colspan="8"><hr class="ss_att_divider" noshade="noshade" /></td></tr>
</c:if>
</tbody>
</table>

</div>
