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
<% //Mashup utility view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssConfigJspStyle != 'form'}">
<div>
  <c:if test="${mashup_attributes['element'] == 'gettingStarted'}">
    <jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/utility/getting_started.jsp" />
  </c:if>
  <c:if test="${mashup_attributes['element'] == 'signInForm'}">
    <jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/utility/signin_form.jsp" />
  </c:if>
  <c:if test="${mashup_attributes['element'] == 'siteAdmin'}">
    <jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/utility/site_admin.jsp" />
  </c:if>
  <c:if test="${mashup_attributes['element'] == 'myWorkspace'}">
    <jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/utility/my_workspace.jsp" />
  </c:if>
  <c:if test="${mashup_attributes['element'] == 'workspaceTree'}">
    <jsp:include page="/WEB-INF/jsp/tag_jsps/mashup/utility/workspace_tree.jsp" />
  </c:if>
</div>
</c:if>

<c:if test="${ssConfigJspStyle == 'form'}">
	<script type="text/javascript">
	//Routine called when "Delete utility" is clicked
	function ss_mashup_deleteUtility${ss_mashupItemId}_${renderResponse.namespace}() {
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "";
	}
	</script>

   <div style="padding:10px;" width="100%">
	 <div style="border:1px solid #cecece; background-color:#e5e5e5; padding:6px;">
	    <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.utility"/></span>
		  &nbsp;-&nbsp;
		  <c:if test="${mashup_attributes['element'] == 'gettingStarted'}">
		    <span><ssf:nlt tag="mashup.gettingStarted"/></span>
		  </c:if>
		  <c:if test="${mashup_attributes['element'] == 'signInForm'}">
		    <span><ssf:nlt tag="mashup.signInForm"/></span>
		  </c:if>
		  <c:if test="${mashup_attributes['element'] == 'siteAdmin'}">
		    <span><ssf:nlt tag="mashup.siteAdmin"/></span>
		  </c:if>
		  <c:if test="${mashup_attributes['element'] == 'myWorkspace'}">
		    <span><ssf:nlt tag="mashup.myWorkspace"/></span>
		  </c:if>
		  <c:if test="${mashup_attributes['element'] == 'workspaceTree'}">
		    <span><ssf:nlt tag="mashup.workspaceTree"/></span>
		  </c:if>
	    <br/>
	    <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	      class="ss_linkButton ss_fineprint"
	      onClick="ss_mashup_deleteUtility${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
	 </div>
   </div>
</c:if>

