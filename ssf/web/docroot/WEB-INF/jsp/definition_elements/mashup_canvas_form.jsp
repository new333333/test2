<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<% //div %>
<!-- Modified December 7, 2009 1:54pm (GWT) -->

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>
<c:set var="gwtPage" value="landingPageEditor" scope="request"/>	
<%@ include file="/WEB-INF/jsp/common/GwtRequestInfo.jsp" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="org.kablink.teaming.domain.FileAttachment" %>
<%@ page import="org.kablink.teaming.domain.FileItem" %>

<c:set var="ss_mashupItemId" value="0" scope="request"/>
<%  
	Long ss_mashupTableDepth = Long.valueOf(0);
	Long ss_mashupTableNumber = Long.valueOf(0);
	Map ss_mashupTableItemCount = new HashMap(); 
	Map ss_mashupTableItemCount2 = new HashMap(); 
	ss_mashupTableItemCount.put(ss_mashupTableDepth, "");
	ss_mashupTableItemCount2.put(ss_mashupTableDepth, ss_mashupTableNumber);
	request.setAttribute("ss_mashupTableDepth", ss_mashupTableDepth);
	request.setAttribute("ss_mashupTableNumber", ss_mashupTableNumber);
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);
	request.setAttribute("ss_mashupTableItemCount2", ss_mashupTableItemCount2);

	Long ss_mashupListDepth = Long.valueOf(0);
	request.setAttribute("ss_mashupListDepth", ss_mashupListDepth);
%>
<c:set var="ss_mashupPropertyName" value="${property_name}" scope="request"/>
<script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js"></script>

<c:if test="${ssConfigJspStyle == 'form'}">
<script type="text/javascript">
// m_landingPageConfig holds the string that defines the content of this landing page and is referenced by the GWT code.
var m_landingPageConfig = null;

m_landingPageConfig = { configData : '<ssf:escapeJavaScript value="${ssDefinitionEntry.customAttributes[property_name].value}" />', mashupPropertyName: '<ssf:escapeJavaScript value="${ss_mashupPropertyName}" />', binderId: '', language : '${ssUser.locale.language}', contentCss : '<ssf:url webPath="viewCss"><ssf:param name="sheet" value="editor"/></ssf:url>', propertiesXML : '<ssf:escapeJavaScript value="${ss_mashupProperties}" />' };

// Create an array of objects where each object holds the name and id of a file attachment.
<c:if test="${!empty ssBinder}">
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />

<%
	String binderId;
	Long binderIdL;

	binderIdL = ssBinder.getId();
	if ( binderIdL != null )
		binderId = binderIdL.toString();
	else
		binderId = "";
%>
m_landingPageConfig.binderId = '<%= binderId %>';

m_fileAttachments = 
	[
	<%
		int i;
		String seperator;
		String fileName;
		String fileId;
		FileItem fileItem;
		SortedSet<FileAttachment> attachments;
		
        i = 0;
		attachments = ssBinder.getFileAttachments();
        for(FileAttachment fileAttachment : attachments)
        {
           	// If this is not the first file attachment, add a ',' before we add another file attachment.
			if ( i != 0 )
				seperator = ",";
			else
				seperator = "";
			
           	fileItem = fileAttachment.getFileItem();
			fileName = fileItem.getName();
			fileId = fileAttachment.getId();
	%>
			<%= seperator %>{ fileName: '<ssf:escapeJavaScript value="<%= fileName %>" />', fileId: '<ssf:escapeJavaScript value="<%= fileId %>" />' }
	<%
		   	++i;
		}// end for()
	%>
	];
</c:if>

function ss_mashup_deleteAll_${renderResponse.namespace}() {
	if (confirm("<ssf:nlt tag="mashup.deleteEverythingConfirm"/>")) {
		var obj = self.document.getElementById("${ss_mashupPropertyName}__deleteEverything");
		obj.value = "true";
		return true;
	} else {
		return false;
	}
}

// Create an onSubmit handler that will get called when this form gets submitted.
ss_createOnSubmitObj( "addLandingPageEditorDataToFormOnSubmit", "${formName}", addLandingPageEditorDataToFormOnSubmit );

/**
 * This form will call into the GWT Landing Page Editor so that it can add its data to the form.
 */
function addLandingPageEditorDataToFormOnSubmit()
{
	// Call the function that will add the landing page editor data to the form
	if ( window.ss_addLandingPageEditorDataToForm )
	{
		window.ss_addLandingPageEditorDataToForm();
	}
	
	return true;
}

</script>
  <table style="padding: 20px 0px 20px 0px;">
  	<tr>
		<td style="vertical-align:top; padding-right: 40px;">
			<span class="ss_bold">${property_caption}</span>
		
			<!-- This div holds the checkboxes for hiding/showing the various parts of the page, masthead, sidebar, etc. -->
			<div style="padding:6px 0px 6px 20px;">
				<div class="ss_nowrap">
				  <input type="checkbox" name="${ss_mashupPropertyName}__hideMasthead"
					id="${ss_mashupPropertyName}__hideMasthead"
					<c:if test="${ss_mashupHideMasthead}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__hideMasthead">
					<span class="ss_labelRight ss_nowrap"><ssf:nlt tag="mashup.hideMasthead"/></span>
				  </label>
				</div>
				<div class="ss_nowrap">
				  <input type="checkbox" name="${ss_mashupPropertyName}__hideSidebar"
					id="${ss_mashupPropertyName}__hideSidebar"
					<c:if test="${ss_mashupHideSidebar}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__hideSidebar">
					<span class="ss_labelRight ss_nowrap"><ssf:nlt tag="mashup.hideSidebar"/></span>
				  </label>
				</div>
		
		<% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
				<div>
				  <input type="checkbox" name="${ss_mashupPropertyName}__hideToolbar"
					id="${ss_mashupPropertyName}__hideToolbar"
					<c:if test="${ss_mashupHideToolbar}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__hideToolbar">
					<span class="ss_labelRight ss_nowrap"><ssf:nlt tag="mashup.hideToolbar"/></span>
				  </label>
				</div>
		<% } %>
				<div class="ss_nowrap">
				  <input type="checkbox" name="${ss_mashupPropertyName}__hideFooter"
					id="${ss_mashupPropertyName}__hideFooter"
					<c:if test="${ss_mashupHideFooter}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__hideFooter">
					<span class="ss_labelRight"><ssf:nlt tag="mashup.hideFooter"/></span>
				  </label>
				</div>
		
				<div class="ss_nowrap">
				  <input type="checkbox" name="${ss_mashupPropertyName}__hideMenu"
					id="${ss_mashupPropertyName}__hideMenu"
					<c:if test="${ss_mashupHideMenu}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__hideMenu">
					<span class="ss_labelRight"><ssf:nlt tag="mashup.hideMenu"/></span>
				  </label>
				</div>
		
		<% if (!(GwtUIHelper.isGwtUIActive(request))) { %>
				<div>
				  <input type="checkbox" name="${ss_mashupPropertyName}__showBranding"
					id="${ss_mashupPropertyName}__showBranding"
					<c:if test="${ss_mashupShowBranding}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__showBranding">
					<span class="ss_labelRight"><ssf:nlt tag="mashup.showBranding"/></span>
				  </label>
				</div>
				<div>
				  <input type="checkbox" name="${ss_mashupPropertyName}__showFavoritesAndTeams"
					id="${ss_mashupPropertyName}__showFavoritesAndTeams"
					<c:if test="${ss_mashupShowFavoritesAndTeams}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__showFavoritesAndTeams">
					<span class="ss_labelRight"><ssf:nlt tag="mashup.showFavoritesAndTeams"/></span>
				  </label>
				</div>
				<div>
				  <input type="checkbox" name="${ss_mashupPropertyName}__showNavigation"
					id="${ss_mashupPropertyName}__showNavigation"
					<c:if test="${ss_mashupShowNavigation}">checked</c:if> >
				  <label for="${ss_mashupPropertyName}__showNavigation">
					<span class="ss_labelRight"><ssf:nlt tag="mashup.showNavigation"/></span>
				  </label>
				</div>
		<% } %>
		    </div>
	</td>
	<td style="vertical-align:top;">	
		  <span class="ss_labelAbove" style="padding-top: 0px;"><ssf:nlt tag="mashup.style"/></span>
		  <div class="margintop2 marginleft1">
			  <input type="radio" name="${ss_mashupPropertyName}__style" value="mashup.css"
				id="${ss_mashupPropertyName}__style"
				<c:if test="${ss_mashupStyle == 'mashup.css'}">checked</c:if> >
				<span class="ss_labelRight"><ssf:nlt tag="mashup.style_light"/></span>
		  </div>
		  <div class="margintop1 marginleft1">
			  <input type="radio" name="${ss_mashupPropertyName}__style" value="mashup_dark.css"
				id="${ss_mashupPropertyName}__style_dark"
				<c:if test="${ss_mashupStyle == 'mashup_dark.css'}">checked</c:if> >
				<span class="ss_labelRight"><ssf:nlt tag="mashup.style_dark"/></span>
		  </div>
    </div>
	</td>
	</tr>
  </table>	

	<!-- This div will hold the Landing Page editor implemented in gwt.  See GwtTeaming.java -->
	<div id="gwtLandingPageEditorDiv">
	</div>
</c:if>
