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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%
	// Bugzilla 566967:
	//    Addresses button truncation issue.  IE doesn't honor padding in an
	//    anchor's background image until the anchor is display:  block or
	//    display:  inline-block.
	String addAnchorStyle;
	if (BrowserSniffer.is_ie(request)) {
		addAnchorStyle = "style=\"display:  inline-block;\"";
	} else {
		addAnchorStyle = "";
	}
%>

<table class="ss_tag_pane_color">
 <tbody>

  <tr><th align="left"><ssf:nlt tag="tags.personalTags"/></th></tr>

  <tr><td>
  	<div class="ss_tag_pane_color">
      <!-- input type="text" class="ss_text" name="personalTag" / -->
		<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="personalTag" type="personalTags" displayValueOnly="true" width="130px" singleItem="true" accessibilityText="title.add.personal.tags" />
      
      	<a  <%= addAnchorStyle %> href="javascript:;" 
          onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
          <ssf:title tag="title.add.personal.tags" />
    	><img src="<html:rootPath/>images/pics/add_btn.png" border="0" align="absmiddle"></a>

	</div>
  </td></tr>

  <tr><td>
	<c:set var="ssTags" value="${ssPersonalTags}" scope="request" />
	<c:set var="ssTagsType" value="p" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </td></tr>

  <tr><th align="left" style="padding-top:15px;"><ssf:nlt tag="tags.communityTags"/></th></tr>

  <tr><td>
  	<div class="ss_tag_pane_color">
    	<!--input type="text" class="ss_text" name="communityTag"/ -->
   
		<c:set var="manageTagsAllowed" value="false"/>
		<ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">
		  <c:set var="manageTagsAllowed" value="true"/>
		</ssf:ifAccessAllowed>
		<ssf:ifAccessAllowed entity="${ssEntry}" operation = "manageTag">
		  <c:set var="manageTagsAllowed" value="true"/>
		</ssf:ifAccessAllowed>
		<c:if test="${manageTagsAllowed}">
		<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="communityTag" type="communityTags" displayValueOnly="true" width="130px" singleItem="true" accessibilityText="title.add.community.tags" />
    	<a  <%= addAnchorStyle %> href="javascript:;" 
       	  onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
       	  <ssf:title tag="title.add.community.tags" />
    	><img src="<html:rootPath/>images/pics/add_btn.png" border="0" align="absmiddle"></a>
    	</c:if>  
    </div>
  </td></tr>


  <tr><td>
	<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
	<c:set var="ssTagsType" value="c" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </td></tr>


</tbody></table>    
