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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<table class="ss_tag_pane_color">
 <tbody>

  <tr><th align="left"><ssf:nlt tag="tags.personalTags"/></th></tr>

  <tr><td>
	<c:set var="ssTags" value="${ssPersonalTags}" scope="request" />
	<c:set var="ssTagsType" value="p" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </td></tr>

  <tr><td>
  	<table class="ss_tag_pane_color"><tbody><tr><td>
      <!-- input type="text" class="ss_text" name="personalTag" / -->
		<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="personalTag" type="personalTags" displayValueOnly="true" width="70px" singleItem="true" accessibilityText="title.add.personal.tags" />
      </td><td>
      	<a class="ss_linkButton" href="javascript:;" 
          onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
          <ssf:title tag="title.add.personal.tags" />
        >
        	<ssf:nlt tag="button.add"/>
        </a>
      </td></tr>
    </tbody></table>
  </td></tr>

  <tr><th align="left"><ssf:nlt tag="tags.communityTags"/></th></tr>

  <tr><td>
	<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
	<c:set var="ssTagsType" value="c" scope="request" />
	<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
  </td></tr>

  <tr><td>
  	<table class="ss_tag_pane_color"><tbody>
  	  <tr><td>
    	<!--input type="text" class="ss_text" name="communityTag"/ -->
   
		<ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">    
		<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="communityTag" type="communityTags" displayValueOnly="true" width="70px" singleItem="true" accessibilityText="title.add.community.tags" />
		</ssf:ifAccessAllowed>
   
      </td><td style="padding-left:4px;">
    
    	<ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">  
    	<a class="ss_linkButton" href="javascript:;" 
       	  onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
       	  <ssf:title tag="title.add.community.tags" />
    	>
    		<ssf:nlt tag="button.add"/>
    	</a>
    	</ssf:ifAccessAllowed>  

      </td></tr>
    </tbody></table>
  </td></tr>
</tbody></table>    
