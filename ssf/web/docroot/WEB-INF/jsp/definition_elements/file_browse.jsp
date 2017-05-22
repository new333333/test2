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

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@page import="org.kablink.teaming.util.SPropsUtil"%>
<c:set var="ss_fieldModifyDisabled" value=""/>
<%
	String[] createFileTypes = org.kablink.teaming.util.SPropsUtil.getStringArray("file.createFileTypes", ",");
%>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
    <c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
    <c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>
<c:if test="${ss_binderQuotasEnabled && empty ss_quotaMessage && 
		ss_binderHighWaterMarkExceeded && !ss_binderQuotasExceeded && !ss_isBinderMirroredFolder}">
    <c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.binder.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${ss_binderMinQuotaLeft/1048576}" 
	    maxFractionDigits="2"/></ssf:param><ssf:param name="value" useBody="true"
	    >${ss_binderMinQuotaLeftBinder.title}</ssf:param>
	    </ssf:nlt></c:set>
</c:if>
<c:if test="${ss_binderQuotasEnabled && empty ss_quotaMessage && 
		ss_binderQuotasExceeded && !ss_isBinderMirroredFolder}">
    <c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskBinderQuotaExceeded"/></c:set>
</c:if>
<script type="text/javascript">
function ss_showMoreFiles${property_name}() {
	document.getElementById('ss_extraFiles_${property_name}').style.display='block';
	document.getElementById('ss_extraFilesClick_${property_name}').style.display='none';
}
function ss_showCreateFile${property_name}() {
	document.getElementById('ss_createFileDiv_${property_name}').style.display='block';
	document.getElementById('ss_createFileClick_${property_name}').style.display='none';
}
function ss_hideCreateFileLink${property_name}() {
	<c:if test="${property_name != 'ss_attachFile'}">
	  var createFileDivObj = document.getElementById('ss_createFileDiv_${property_name}');
	  if (createFileDivObj != null) createFileDivObj.style.visibility='hidden';
	  var createFileClickObj = document.getElementById('ss_createFileClick_${property_name}');
	  if (createFileClickObj != null) createFileClickObj.style.visibility='hidden';
	</c:if>
}
function ss_hideUploadFileDiv${property_name}() {
	<c:if test="${property_name != 'ss_attachFile'}">
	  var createFileNameObj = document.getElementById('ss_createFileNameElement_${property_name}');
	  if (createFileNameObj != null && createFileNameObj.value != '') {
		  document.getElementById('ss_fileBroseButtonDiv_${property_name}').style.visibility='hidden';
	  } else {
	  	  document.getElementById('ss_fileBroseButtonDiv_${property_name}').style.visibility='visible';
	  }
	</c:if>
}
</script>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>
<c:set var="elementName" value="${property_name}"/>
<c:set var="caption" value="${property_caption}"/>
<c:set var="repositoryName" value="${property_storage}"/>

<c:set var="width" value=""/>
<c:if test='${! empty property_width}'>
<c:set var="width" value="size='${property_width}'"/>
</c:if>

<c:set var="required" value=""/>
<c:if test="${property_required}">
<c:set var="caption3" value="<%= caption2 %>"/>
<c:set var="required" value="<span id=\"ss_required_${property_name}\" title=\"${caption3}\" class=\"ss_required\">*</span>"/>
</c:if>

<c:set var="countFb" value="1"/>
<c:if test='${! empty property_number}'><c:set var="countFb" value="${property_number}"/></c:if>
<c:set var="countFb2" value="5"/>
<c:if test="${countFb > 1}"><c:set var="countFb2" value="${countFb}"/></c:if>
<c:if test="${empty ss_fileBrowseOfferMoreFiles || ss_fileBrowseOfferMoreFiles != 'true'}"><c:set var="countFb2" value="1"/></c:if>

<c:set var="eName" value="${elementName}"/>

<c:if test='${! empty ssFolder}'>
 <c:set var="entryId" value=""/>
 <c:if test="${!empty ssEntry}">
  <c:set var="entryId" value="${ssEntry.id}"/>
 </c:if>
 <script type="text/javascript">
var ss_findEntryForFileUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="find_entry_for_file" />
	<ssf:param name="folderId" value="${ssFolder.id}" />
	<ssf:param name="entryId" value="${entryId}" />
	</ssf:url>";
  <c:forEach var="i" begin="1" end="${countFb}">
   <c:if test='${!empty property_number}'>
	<c:set var="eName" value="${elementName}${i}"/>
   </c:if>
ss_addValidator("ss_duplicateFileCheck_${eName}", ss_ajax_result_validator);
var ${eName}_ok = 1;
  </c:forEach>
  </script>
</c:if>

<label for="${eName}">
	<span class="ss_labelAbove ${ss_fieldModifyStyle}" id="${elementName}_label">${caption}${required}</span>
</label>

<table cellspacing="0" cellpadding="0">
<tr>
<td>
<div id="ss_fileBroseButtonDiv_${property_name}" style="display:block;">
<c:forEach var="i" begin="1" end="${countFb2}">
 <c:if test='${! empty property_number}'>
	<c:set var="eName" value="${elementName}${i}"/>
 </c:if>
 <c:if test='${! empty ssFolder}'>
  <div class="needed-because-of-ie-bug"><div id="ss_duplicateFileCheck_${eName}" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
  <input type="file" class="ss_text ${ss_fieldModifyStyle}" ${ss_fieldModifyInputAttribute}
    name="${eName}" id="${eName}" ${width} 
	<c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
      onkeyup="if(window.event && window.event.keyCode!=9 && window.event.keyCode!=16)this.click();return false;"
      onchange="ss_hideCreateFileLink${property_name}();ss_ajaxValidate(ss_findEntryForFileUrl, this,'${elementName}_label', 'ss_duplicateFileCheck_${eName}', '${repositoryName}');"
	</c:if>
	<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
	  onClick='alert("<ssf:escapeJavaScript>${ss_quotaMessage}</ssf:escapeJavaScript>");return false;'
	</c:if>
  /> 
  <a href="javascript: ;" onClick="ss_showHide('addCommentDiv_${eName}');return false;">
    <span class="ss_fineprint"><ssf:nlt tag="file.addComment"/></span>
  </a>
  <div id="addCommentDiv_${eName}" style="display:none;">
    <ssf:htmleditor name="${eName}.description" height="100" toolbar="minimal"/>
  </div>
  <div align="left"><span class="ss_smallprint" style="color:red;">${ss_quotaMessage}</span></div>
  <input type="hidden" name="ss_upload_request_uid" />
 </c:if>
 <c:if test='${empty ssFolder}'>
  <label for="${eName}">&nbsp;</label>
  <input type="file" class="ss_text ${ss_fieldModifyStyle}" ${ss_fieldModifyInputAttribute} 
    name="${eName}" id="${eName}" 
	<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
	  onClick='alert("<ssf:escapeJavaScript>${ss_quotaMessage}</ssf:escapeJavaScript>");return false;'
	</c:if>
    ${width}/>
  <a href="javascript: ;" onClick="ss_showHide('addCommentDiv_${eName}');return false;">
    <span class="ss_fineprint"><ssf:nlt tag="file.addComment"/></span>
  </a>
  <div id="addCommentDiv_${eName}" style="display:none;">
    <ssf:htmleditor name="${eName}.description" height="100" toolbar="minimal"/>
  </div>
  <div align="left"><span class="ss_smallprint" style="color:red;">${ss_quotaMessage}</span></div>
 </c:if>
	<script type="text/javascript">	
		function ${eName}_onAtatchmentFormSubmit(formObj) {
			var eNameObj = formObj['${eName}'];
			if (eNameObj != null) {
				var sTitle = ss_validateEntryTextFieldLength(eNameObj.value);
				if (sTitle != eNameObj.value) {
					alert("<ssf:nlt tag="error.fileNameTooLong"/>");
					return false;
				}
			}
			/**
			//This code was turned off because it causes some browsers to terminate the upload when the progress bar starts
			if (!window.uploadProgressBar) {// prevents many progress bars on one page
			
				// the uid binds upload request with upload status check request
				var uid = new Date().valueOf();
				var formAction = formObj.action;
				if (!formAction || formAction.length == 0) {
					formAction = document.location.href;
				}
				formObj.action = formAction + "&ss_upload_request_uid=" + uid;
				
				var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, 
											{operation:"get_upload_progress_status", 
											ss_upload_request_uid: uid});
				window.uploadProgressBar = new ss_FileUploadProgressBar();
			  	setTimeout(function() {
		  		  	ss_FileUploadProgressBar.reloadProgressStatus(window.uploadProgressBar, url);
			  	}, 1500);
			}
			*/
			return true;			
		}
	
		ss_createOnSubmitObj('${eName}onsub', '${formName}', ${eName}_onAtatchmentFormSubmit);
		 
	</script> 

<c:if test="${i == '1'}">
<div id="ss_extraFiles_${property_name}" 
  <c:if test="${countFb == '1'}"> style="display:none;" </c:if>
>
</c:if>
</c:forEach>
</div>
</div>
</td>
</tr>
<c:if test="${countFb == '1' && ss_fileBrowseOfferMoreFiles == 'true'}">
<tr>
<td align="right">
<div id="ss_extraFilesClick_${property_name}" class="${ss_fieldModifyStyle}">
<a href="javascript: ;" 
onClick="ss_showMoreFiles${property_name}();return false;"
><span class="smallprint"><ssf:nlt tag="entry.attachMore"/></span></a>
</div>
</td>
</tr>
</c:if>

<c:if test="<%= createFileTypes.length > 0 %>">
  <c:if test="${property_name != 'ss_attachFile'}">
    <c:set var="ss_createFileNameName" value="createFileName_${property_name}" />
    <c:set var="ss_createFileTypeName" value="createFileType_${property_name}" />
  </c:if>
  <c:if test="${property_name == 'ss_attachFile'}">
    <c:set var="ss_createFileNameName" value="createFileName_ss_attachFile0" />
    <c:set var="ss_createFileTypeName" value="createFileType_ss_attachFile0" />
  </c:if>
<tr>
  <td>
    <div style="padding:10px 0px;">
	<div id="ss_createFileClick_${property_name}" class="${ss_fieldModifyStyle}">
	  <a href="javascript: ;" 
	    onClick="ss_showCreateFile${property_name}();return false;"
	    title="<ssf:escapeJavaScript><ssf:nlt tag="entry.createFileHint"/></ssf:escapeJavaScript>"
	  ><span class="smallprint"><ssf:nlt tag="entry.createFile"/></span>
	  </a>
	</div>
	<div id="ss_createFileDiv_${property_name}" style="display:none;">
		<table>
		  <tr>
		    <td>
		      <ssf:nlt tag="file.name"/>
		    </td>
		    <td>
		      <input type="text" style="width: 200px;" 
		          name="${ss_createFileNameName}" 
		          id="ss_createFileNameElement_${property_name}" 
		          onChange="ss_hideUploadFileDiv${property_name}();"
		      />
		    </td>
		  </tr>
		  <tr>
		    <td>
		      <ssf:nlt tag="file.type"/>
		    </td>
		    <td>
		      <select name="${ss_createFileTypeName}">
		        <% 
		          for (int i = 0; i < createFileTypes.length; i++) {
		        	  %>
		        	    <option value="<%= createFileTypes[i] %>" 
		        	      <c:if test='<%= createFileTypes[i].equals(".docx") %>'> selected </c:if>
		        	    ><%= createFileTypes[i] %></option>
		        	  <%
		          }
		        %>
		      </select>
		    </td>
		  </tr>
		</table>
	</div>
	</div>
  </td>
</tr>
</c:if>
</table>

