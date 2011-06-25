<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="tag" value="folder.manageFolderVersionControls"/>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<script type="text/javascript">

function ss_checkIfDecimalNumberValid(s) {
	if (ss_trim(s) == '') return true;   //Blank is ok
	if (!ss_checkIfNumber(s) || s.indexOf("-") >= 0) {
		alert("<ssf:escapeJavaScript><ssf:nlt tag="error.mustBeANumber"/></ssf:escapeJavaScript>");
		return false;
	}
	
	if (ss_trim(s).length >= 12) {
		alert("<ssf:escapeJavaScript><ssf:nlt tag="error.numberTooBig"/></ssf:escapeJavaScript>");
		return false;
	}
	return true;
}

function ss_checkIfNumberValid(s) {
	if (ss_trim(s) == '') return true;   //Blank is ok
	
	var pattern1 = new RegExp("^[0-9]+$");
	if (pattern1.test(ss_trim(s))) {
		if (ss_trim(s).length >= 8) {
			alert("<ssf:escapeJavaScript><ssf:nlt tag="error.numberTooBig"/></ssf:escapeJavaScript>");
			return false;
		}
		return true;
	}
	alert("<ssf:escapeJavaScript><ssf:nlt tag="error.mustBeANumber"/></ssf:escapeJavaScript>");
	return false;
}

</script>

<div class="ss_style ss_portlet">
	<div style="padding:10px;">
		<br>
		
		<c:if test="${!empty ssException}">
		  <font color="red">
		    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
		  </font>
		  <br/>
		</c:if>
	
<c:set var="ss_tab_versionControls" value="on"/>
<%@ include file="/WEB-INF/jsp/binder/configure_tabs.jsp" %>

<div style="display:block;" class="wg-tab-content marginbottom3">
<form class="ss_form" method="post" 
	action="<ssf:url action="manage_version_controls" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>">
<br/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
</c:if>
<% //need to check tags for templates %>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
<div align="right">
  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
  <input type="submit" class="ss_submit" name="closeBtn" 
    value="<ssf:nlt tag="button.close"/>" onClick="ss_cancelButtonCloseWindow();return false;">
</div>

<br/>
	
    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
	    <input type="checkbox" name="enableBinderVersions" 
		  <c:if test="${ss_binder_versions_enabled}">checked=checked</c:if>
		  <c:if test="${ss_binder_versions_inherited}">disabled=disabled</c:if>
		/>
		<span class="ss_bold"><ssf:nlt tag="binder.versions.enableVersionsForFolder" /></span>
		<c:if test="${ss_binder_versions_inherited}">
		  <span style="padding-right:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
	  </legend>
      
       <div style="padding:10px 10px 0px 10px;">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsToKeep"/></span>
          <input type="text" name="versionsToKeep" value="${ss_binder_versions_to_keep}" 
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
		    <c:if test="${ss_binder_versions_inherited}">disabled=disabled</c:if>
          >
        <c:if test="${ss_binder_versions_inherited}">
		  <span style="padding-right:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
       </div>
      
       <div style="padding:10px 10px 0px 10px;">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsMaxAge"/></span>
          <input type="text" name="maxVersionAge" 
            value="${ss_binder_versions_max_age}"
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
		    <c:if test="${ss_binder_versions_inherited}">disabled=disabled</c:if>
          >
        <c:if test="${ss_binder_versions_inherited}">
		  <span style="padding-right:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
       </div>
      
       <div style="padding:10px 10px 0px 10px;">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsMaxFileSize"/></span>
          <input type="text" name="maxFileSize" 
            value="${ss_binder_versions_max_file_size}"
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
		    <c:if test="${ss_binder_versions_inherited}">disabled=disabled</c:if>
          ><ssf:nlt tag="file.sizeMB"/>
        <c:if test="${ss_binder_versions_inherited}">
		  <span style="padding-right:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
       </div>
      
       <div style="padding:10px 10px 0px 10px;">
		<c:if test="${ss_binder_versions_inherited}">
		  <input type="submit" name="stopInheritBtn" 
		    value="<ssf:nlt tag='binder.versions.inheritVersionControlsStop'/>"
		  />
		</c:if>
		<c:if test="${!ss_binder_versions_inherited}">
		  <input type="submit" name="inheritBtn" 
		    value="<ssf:nlt tag='binder.versions.inheritVersionControls'/>"
		  />
		</c:if>
       </div>
      
    </fieldset>
    
    <br/>
    
    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
	    <input type="checkbox" name="enableFileEncryption" 
		  <c:if test="${ss_binder_file_encryption_enabled}">checked=checked</c:if>
		/><span class="ss_bold"><ssf:nlt tag="binder.enableFileEncryption" /></span>
	  </legend>
		<c:if test="${ss_binder_file_encryption_enabled}">
	      <span><ssf:nlt tag="binder.fileEncryptionEnabled"/></span>
	    </c:if>
	</fieldset>
<br/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
  onClick="ss_cancelButtonCloseWindow();return false;">
</form>
</div>
</div>
</div>

</body>
</html>
