<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.Utils" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="tag" value="folder.manageFolderVersionControls"/>
<jsp:useBean id="tag" type="String" />
<c:set var="binder" value="${ssBinder}"/>
<jsp:useBean id="binder" type="org.kablink.teaming.domain.Binder" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="isFilr" value="<%= Utils.checkIfFilr() %>"/>

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

function ss_validateFileSizeLimitDefault(s) {
	<c:if test="${!empty ss_fileSizeLimitUserDefault}">
		if (s && (0 < s.length)) {
			var fsDefault = Number("${ss_fileSizeLimitUserDefault}");
			var fsNew     = Number(s);
			if (fsNew > fsDefault) {
				alert("<ssf:escapeJavaScript><ssf:nlt tag="error.fileSizeLimitExceeded"><ssf:param name="value" value="${ss_fileSizeLimitUserDefault}" /></ssf:nlt></ssf:escapeJavaScript>");
				return false;
			}
		}
	</c:if>
	return true;
}

function ss_confirmEncryption(cbObj) {
	if (cbObj.checked) {
		<c:if test="${!ss_binder_file_encryption_enabled}">
		  if (confirm("<ssf:escapeQuotes><ssf:nlt tag="binder.fileEncryptionInheritanceHint2"/></ssf:escapeQuotes>")) {
			  var formObj = document.forms['form1'];
			  ss_startSpinner(cbObj);
			  setTimeout("document.forms['form1'].applyBtn.click();", 100);
			  return true;
		  } else {
			  cbObj.checked = false;
		  }
		</c:if>
	}
	return false;
}

function ss_confirmEncryptAll() {
	if (confirm("<ssf:escapeQuotes><ssf:nlt tag="binder.fileEncryptionInheritanceHint2"/></ssf:escapeQuotes>")) {
		var formObj = document.forms['form1'];
		ss_startSpinner(cbObj);
		setTimeout("document.forms['form1'].applyBtn.click();", 100);
		return true;
	}
	return false;
}

</script>

<div class="ss_style ss_portlet">
	<div style="padding:10px;">		
		<c:if test="${!empty ssException}">
		<br>
		  <font color="red">
		    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
		  </font>
		<br/>
		</c:if>
	
<c:set var="ss_tab_versionControls" value="on"/>
<%@ include file="/WEB-INF/jsp/binder/configure_tabs.jsp" %>

<div style="display:block;" class="wg-tab-content marginbottom3">
<form class="ss_form" method="post" name="form1"
	action="<ssf:url action="manage_version_controls" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>">

	<div class="marginbottom3">
		<c:if test="${ssBinder.entityType == 'folder'}">
		  <span><ssf:nlt tag="access.currentFolder"/></span>
		</c:if>
		<c:if test="${ssBinder.entityType != 'folder'}">
		  <span><ssf:nlt tag="access.currentWorkspace"/></span>
		</c:if>
		<% //need to check tags for templates %>
			<span class="ss_bold ss_largestprint"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>		
	</div>

    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
<c:if test="${!isFilr}">
	    <input type="checkbox" name="enableBinderVersions" 
		  <c:if test="${ss_binder_versions_enabled}">checked=checked</c:if>
		  <c:if test="${isFilr || ss_binder_versions_inherited}">disabled=disabled</c:if>
		/>
</c:if>
		<span class="ss_bold"><ssf:nlt tag="binder.versions.enableVersionsForFolder" /></span>
	  </legend>
      
<c:if test="${!isFilr}">
       <div style="padding:10px 10px 0px 10px;">
	   	<div><ssf:nlt tag="binder.versions.versionsToKeep1"/></div>
	   	<div class="marginbottom2"><ssf:nlt tag="binder.versions.versionsToKeep2"/></div>
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsToKeep3"/></span>
          <input type="text" name="versionsToKeep" value="${ss_binder_versions_to_keep}" 
            style="width:40px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
          >
        <c:if test="${ss_binder_versions_inherited}">
		  <span class="ss_smallprint" style="padding-left:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
      
       <span style="padding-left:10px;">
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
       </span>
       </div>
</c:if>
<c:if test="${isFilr}">
       <div style="padding:10px 10px 0px 10px;">
          <div style="padding:0px 0px 6px 0px;">
            <span class="ss_bold"><ssf:nlt tag="binder.file.versionNotSupportedInFilr"/></span>
          </div>
      </div>
</c:if>      
    </fieldset>
    <br/>

<c:if test="${!isFilr}">
    <c:if test="${ssBinder.entityType == 'folder'}">
     <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
	    <input type="checkbox" name="enableBinderVersionAging" 
		  <c:if test="${ss_binder_version_aging_enabled}">checked=checked</c:if>
		/>
	    <span class="ss_bold"><ssf:nlt tag="binder.file.versionAgingEnable" /></span>
	  </legend>
       <div style="padding:10px 10px 0px 10px;">
          <c:if test="${!ss_binder_version_aging_enabled}">
          <div style="padding:0px 0px 6px 0px;">
            <span class="ss_bold"><ssf:nlt tag="binder.file.versionAgingDisabled1"/></span>
            <span><ssf:nlt tag="binder.file.versionAgingDisabled2"/></span>
          </div>
          </c:if>
		  <div><ssf:nlt tag="binder.versions.agingDays.hint1" />&nbsp;<ssf:nlt tag="binder.versions.agingDays.hint2" />&nbsp;<ssf:nlt tag="binder.versions.agingDays.hint3" />
		    <c:if test="${empty ss_fileVersionMaximumAge}">
		       <div><b><ssf:nlt tag="binder.versions.agingDays.hint4" /></b></div>
		    </c:if>
		    <c:if test="${!empty ss_fileVersionMaximumAge}">
		       <div><b><ssf:nlt tag="binder.versions.agingDays.hint5"></b><ssf:param 
		         name="value" value="${ss_fileVersionMaximumAge}"/></ssf:nlt></div>
		    </c:if>
		  </div>
		  <div class="margintop3">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.agingDays"/>&nbsp;</span>
          <input type="text" name="versionAgingDays" 
            value="${ss_binder_version_aging_days}"
            style="width:40px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}'
          >&nbsp;<ssf:nlt tag="smallWords.days"/>
		  </div>
       </div>
	 </fieldset>
     <br/>
    </c:if>
</c:if>

    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
	    <span class="ss_bold"><ssf:nlt tag="binder.file.uploadSizeLimit" /></span>
	  </legend>
       <div style="padding:10px 10px 0px 10px;">
        	<span><ssf:nlt tag="binder.versions.versionsMaxFileSize1"/></span>
			<div><ssf:nlt tag="binder.versions.leaveBlankToInherit"/></div>
			<div><ssf:nlt tag="binder.file.uploadSizeLimit.hint1"/></div>
		<c:if test="${!empty ss_fileSizeLimitUserDefault}">
		  <div class="margintop2">
		    <ssf:nlt tag="binder.file.uploadSizeLimit.hint2"><ssf:param 
		    name="value" value="${ss_fileSizeLimitUserDefault}"/></ssf:nlt>
		  </div>
		</c:if>
  		</div>
       <div style="padding:10px 10px 0px 10px;">
          <span class="ss_bold"><ssf:nlt tag="binder.versions.versionsMaxFileSize2"/></span>
          <input type="text" name="maxFileSize" 
            value="${ss_binder_versions_max_file_size}"
            style="width:50px; text-align:right;"
            onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";} if (!ss_validateFileSizeLimitDefault(this.value)){this.value="${ss_fileSizeLimitUserDefault}";}'
          >&nbsp;<ssf:nlt tag="file.sizeMB"/>
        <c:if test="${ss_binder_file_size_limit_inherited}">
		  <span class="ss_smallprint" style="padding-left:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
		<br/>
       </div>
	</fieldset>
<br/>

<c:if test="${(ss_binder_file_encryption_enabled || ss_binder_file_encryption_allowed) && 
		!ss_file_encryption_enabled_all && !ssBinder.mirrored}">
<c:if test="${ssBinder.entityType == 'folder'}">
    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
	    <input type="checkbox" 
	      name="enableFileEncryption" 
	      onChange="return ss_confirmEncryption(this);"
		  <c:if test="${ss_binder_file_encryption_enabled_inherited || ss_binder_file_encryption_enabled}">
		    checked=checked
		  </c:if>
		/><span class="ss_bold"><ssf:nlt tag="binder.enableFileEncryption" /></span>
		<% if (binder.getFileEncryptionEnabled() == null) { %>
	    <c:if test="${ss_binder_file_encryption_enabled_inherited}">
		  <span class="ss_smallprint" style="padding-left:10px;">(<ssf:nlt tag="general.Inherited" />)</span>
		</c:if>
		<% } %>
	  </legend>
	  <div style="padding:10px;">
		<c:if test="${ss_binder_file_encryption_enabled || ss_binder_file_encryption_enabled_inherited}">
	      <span><ssf:nlt tag="binder.fileEncryptionEnabled"/></span>
	      <br/>
	      <br/>
	      <div class="ss_smallprint">
	        <ssf:nlt tag="binder.fileEncryptionInheritanceHint"/>
	      </div>
	    </c:if>
		<c:if test="${!ss_binder_file_encryption_enabled && !ss_binder_file_encryption_enabled_inherited}">
	      <span><ssf:nlt tag="binder.fileEncryptionEnabled2"/></span>
	      <br/>
	    </c:if>
	  </div>
  <%
  		if (binder.getParentBinder() != null && 
  			binder.getParentBinder() instanceof org.kablink.teaming.domain.Folder) {
  %>
       <div style="padding:4px 10px 0px 10px;">
		<% if (binder.getFileEncryptionEnabled() == null) { %>
		  <c:if test="${ss_binder_file_encryption_enabled_inherited}">
		    <input type="submit" name="stopInheritEncryptionBtn" 
		      value="<ssf:nlt tag='binder.versions.inheritVersionControlsStop'/>"
		    />
		  </c:if>
		<% } %>
		<% if (binder.getFileEncryptionEnabled() != null) { %>
		  <input type="submit" name="inheritEncryptionBtn" 
		    value="<ssf:nlt tag='binder.versions.inheritVersionControls'/>"
		  />
		<% } %>
       </div>
   <%
		} 
   %>
	</fieldset>
<br/>
</c:if>
</c:if>

<c:if test="${ss_file_encryption_enabled_all && !ssBinder.mirrored}">
<c:if test="${ss_binder_files_not_encrypted == 0}">
    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
		<span class="ss_bold"><ssf:nlt tag="binder.fileEncryption" /></span>
	  </legend>
	  <div style="padding:10px;">
	      <span>
	        <ssf:nlt tag="binder.fileEncryptionEnabledAllHint"/>
	      </span>
	      <br/>
	      <br/>
	  </div>
	</fieldset>
</c:if>
<c:if test="${ssBinder.entityType == 'folder' && ss_binder_files_not_encrypted > 0 && !ssBinder.mirrored}">
    <fieldset class="ss_fieldset">
	  <legend class="ss_legend">
		<span class="ss_bold"><ssf:nlt tag="binder.enableFileEncryption" /></span>
	  </legend>
	  <div style="padding:10px;">
	      <span>
	        <ssf:nlt tag="binder.fileEncryptionEnabledAllHint"/>
	      </span>
	      <br/>
	      <br/>
	      <span>
	        <c:if test="${ss_binder_files_not_encrypted == 1}">
	          <ssf:nlt tag="binder.fileNeedsToBeEncrypted"/>
	        </c:if>
	        <c:if test="${ss_binder_files_not_encrypted > 1}">
	          <ssf:nlt tag="binder.filesNeedToBeEncrypted">
	            <ssf:param name="value" value="${ss_binder_files_not_encrypted}"/>
	          </ssf:nlt>
	        </c:if>
	      </span>
	      <br/>
	      <br/>
	      <input type="submit" name="encryptAllFiles" 
		    value="<ssf:nlt tag='binder.filesEncryptNow'/>" 
		    onClick="return(ss_confirmEncryptAll());" />
	  </div>
	</fieldset>
</c:if>
</c:if>

	<div class="margintop3" style="text-align: right;">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
		<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" />" >
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="ss_cancelButtonCloseWindow();return false;">
	</div>

</form>
</div>
</div>
</div>

</body>
</html>
