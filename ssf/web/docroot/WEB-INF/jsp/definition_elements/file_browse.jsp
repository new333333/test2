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

<c:set var="count" value="1"/>
<c:if test='${! empty property_number}'>
<c:set var="count" value="${property_number}"/>
</c:if>

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
  <c:forEach var="i" begin="1" end="${count}">
   <c:if test='${! empty property_number}'>
	<c:set var="eName" value="${elementName}${i}"/>
   </c:if>
ss_addValidator("ss_duplicateFileCheck_${eName}", ss_ajax_result_validator);
var ${eName}_ok = 1;
  </c:forEach>
 </script>
</c:if>

<span class="ss_labelAbove" id="${elementName}_label">${caption}${required}</span>
<c:forEach var="i" begin="1" end="${count}">
 <c:if test='${! empty property_number}'>
	<c:set var="eName" value="${elementName}${i}"/>
 </c:if>
 <c:if test='${! empty ssFolder}'>
  <div class="needed-because-of-ie-bug"><div id="ss_duplicateFileCheck_${eName}" style="display:none; visibility:hidden;" ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
  <input type="file" class="ss_text" 
    name="${eName}" id="${eName}" ${width} 
    onkeyup="this.click();return false;"
    onchange="ss_ajaxValidate(ss_findEntryForFileUrl, this,'${elementName}_label', 'ss_duplicateFileCheck_${eName}', '${repositoryName}');"
  /><br/>
  <input type="hidden" name="ss_upload_request_uid" />
 </c:if>
 <c:if test='${empty ssFolder}'>
  <input type="file" class="ss_text" name="${eName}" id="${eName}" ${width}/><br/>
 </c:if>
	<script type="text/javascript">	
		function ${eName}_onAtatchmentFormSubmit(formObj) {
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
			return true;			
		}
	
		ss_createOnSubmitObj('${eName}onsub', '${formName}', ${eName}_onAtatchmentFormSubmit);
		 
	</script> 
</c:forEach>

