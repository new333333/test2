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
	
		<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
		  class="wg-tabs margintop3 marginbottom2">
		  <table>
		    <tr>
			  <td>
				  <div class="wg-tab roundcornerSM">
					  <a href="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="binderType" value="${ssBinder.entityType}"/></ssf:url>"
					  >${ss_windowTitle}</a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM">
					  <a href="<ssf:url action="configure_definitions" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="binderType" value="${ssBinder.entityType}"/><ssf:param 
						name="operation" value="simpleUrls"/></ssf:url>"
					  ><ssf:nlt tag="binder.configure.definitions.simpleUrls"/></a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM on" >
					  <a href="<ssf:url action="manage_version_controls" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/></ssf:url>"
					  ><ssf:nlt tag="folder.manageFolderVersionControls"/></a>
				  </div>
			  </td>
		    </tr>
		  </table>
		</div>
		<div class="ss_clear"></div>

<div id="manageIndexDiv" style="display:block;" class="wg-tab-content marginbottom3">
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
		/>
		<span class="ss_bold"><ssf:nlt tag="binder.versions.enableVersionsForFolder" /></span>
	  </legend>
      
      <c:if test="${ss_binder_versions_enabled}">
       <div style="padding:10px 10px 0px 10px;">
        <c:if test="${empty ss_binder_versions_to_keep}">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsToKeep"/></span>
          <input type="text" name="versionsToKeep" value="" 
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
          >
        </c:if>
        <c:if test="${!empty ss_binder_versions_to_keep}">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsToKeep"/></span>
          <input type="text" name="versionsToKeep" value="${ss_binder_versions_to_keep}" 
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
            />
        </c:if>
       </div>
      
       <div style="padding:10px 10px 0px 10px;">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsMaxAge"/></span>
          <input type="text" name="maxVersionAge" 
            value="${ss_binder_versions_max_age}"
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
          >
       </div>
      
       <div style="padding:10px 10px 0px 10px;">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsMaxFileSize"/></span>
          <input type="text" name="maxFileSize" 
            value="${ss_binder_versions_max_file_size}"
            style="width:80px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
          ><ssf:nlt tag="file.sizeMB"/>
       </div>
      
       <div style="padding:10px 10px 0px 10px;">
		<input type="checkbox" name="inheritBinderVersionControls" 
		  <c:if test="${ss_binder_version_controls_inherited}">checked=checked</c:if>
		/>
		<span class="ss_bold"><ssf:nlt tag="binder.versions.inheritVersionControls" /></span>
       </div>
      </c:if>

      <c:if test="${!ss_binder_versions_enabled}">
       <div style="padding:10px 10px 0px 10px;">
        <span><ssf:nlt tag="binder.versions.versionsDisabled"/></span>
       </div>
      </c:if>
      
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

</body>
</html>
