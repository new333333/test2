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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!ss_searchResultsPage}">
	<c:if test="${ssDefinitionEntry.entityType == 'folder'}">
		<ssf:sidebarPanel title="sidebar.tags.folder" id="ss_placetags_sidebar" divClass="ss_place_tags" initOpen="true" sticky="true">
		
		  <c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
		<%@ page import="com.sitescape.util.BrowserSniffer" %>
		<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
		<c:set var="ss_tagViewNamespace" value="${renderResponse.namespace}" scope="request"/>
		<c:if test="${!empty ss_namespace}"><c:set var="ss_tagViewNamespace" value="${ss_namespace}" scope="request"/></c:if>
		<c:if test="${ssConfigJspStyle != 'template'}">
		<c:if test="${empty ss_tagDivNumber}">
		  <c:set var="ss_tagDivNumber" value="0" scope="request"/>
		</c:if>
		<c:set var="ss_tagDivNumber" value="${ss_tagDivNumber + 1}" scope="request"/>
		<c:if test="${empty ss_tags_loaded}">
		<script type="text/javascript">
			ss_loadJsFile(ss_rootPath, "js/forum/ss_tags.js");
		</script> 
		<c:set var="ss_tags_loaded" value="1" scope="request"/>
		</c:if>
		<c:set var="ssEntry" value="${ss_tagObject}" scope="request"/>
		<%
		boolean isIEtag = BrowserSniffer.is_ie(request);
		%>
		<table class="ss_style" cellspacing="0" cellpadding="0">
		<tbody>
		<tr>
		<td valign="top" style="padding-right:2px;">
		  <ssHelpSpot helpId="workspaces_folders/misc_tools/tags" offsetX="-10" xAlignment="left" 
		    <c:if test="<%= isIEtag %>">
		       offsetY="-3" 
		    </c:if>
		    <c:if test="<%= !isIEtag %>">
		       offsetY="-17" 
		    </c:if>
		     xAlignment="left" title="<ssf:nlt tag="helpSpot.tags" text="Tags"/>">
		  </ssHelpSpot>
		<a href="javascript:;" 
			<ssf:ifaccessible>
		  		onClick="ss_showAccessibleMenu('ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane'); return false;"
			</ssf:ifaccessible>
			<ssf:ifnotaccessible>
				onClick="ss_tagShow('${ss_tagViewNamespace}','${ss_tagDivNumber}'); return false;"
			</ssf:ifnotaccessible>
			<ssf:title tag="title.open.tag.menu" />
		><div class="ss_iconed_label ss_add_tag"><ssf:nlt tag="tags.tags"/></div></a>
		  <div id="ss_tags_anchor${ss_tagViewNamespace}_${ss_tagDivNumber}">
		  </div>
		</td>
		<td width="100%">
		<ssf:ifaccessible>
			<c:set var="ssCloseScript" value="ss_hideAccessibleMenu('ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane'); return false;" scope="request"/>
		</ssf:ifaccessible>
		<ssf:ifnotaccessible>
			<c:set var="ssCloseScript" value="ss_tagHide('${ss_tagViewNamespace}', '${ss_tagDivNumber}');return false;" scope="request"/>
		</ssf:ifnotaccessible>
		
		<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data_cloud.jsp" />
		</td></tr></tbody></table>
		
		<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane" 
			<ssf:ifaccessible>
				style="visibility:hidden;display:none;white-space:nowrap;"  		
			</ssf:ifaccessible>
			<ssf:ifnotaccessible>
				class="ss_tag_pane"
				<ssf:ifadapter>style="position:relative;"</ssf:ifadapter>
				<ssf:ifnotadapter>style="position:absolute;"</ssf:ifnotadapter>
			</ssf:ifnotaccessible>
		>
		
		<ssf:popupPane width="220px" titleTag="tags.manageTags" closeScript="${ssCloseScript}">
		
		<div style="padding:0px 10px;">
		<form class="ss_style ss_form ss_tag_pane_color" 
		  method="post" action=""
		  id="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" 
		  name="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}">
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
		<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="personalTag" type="personalTags" width="70px" singleItem="true" accessibilityText="title.add.personal.tags" />
		    </td><td>
		      <a class="ss_linkButton" href="javascript:;" 
		        onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
		        <ssf:title tag="title.add.personal.tags" />
		      ><ssf:nlt tag="button.add"/></a>
		    </td></tr>
		  </tbody></table>
		</td></tr>
		
		<tr><td></td></tr>
		
		<tr><th align="left"><ssf:nlt tag="tags.communityTags"/></th></tr>
		
		<tr><td>
		<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
		<c:set var="ssTagsType" value="c" scope="request" />
		<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
		</td></tr>
		
		<tr><td>
		  <table class="ss_tag_pane_color"><tbody><tr><td>
		    <!--input type="text" class="ss_text" name="communityTag"/ -->
		   
		<ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">    
		<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="communityTag" type="communityTags" width="70px" singleItem="true" accessibilityText="title.add.community.tags" />
		</ssf:ifAccessAllowed>
		   
		    </td><td style="padding-left:4px;">
		    
		    <ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">  
		    <a class="ss_linkButton" href="javascript:;" 
		      onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');setTimeout('document.ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}.reset()', 100);return false;"
		      <ssf:title tag="title.add.community.tags" />
		    ><ssf:nlt tag="button.add"/></a>
		    </ssf:ifAccessAllowed>  
		
		    </td></tr>
		  </tbody></table>
		</td></tr>
		
		
		<tr><td>
		  <table class="ss_tag_pane_color" colspan="2"><tbody><tr><td>
		    <a class="ss_linkButton" href="javascript:;" title="<ssf:nlt tag="title.closeMenu" />"
		      onClick="ss_hideAccessibleMenu('ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane'); return false;"
		    ><ssf:nlt tag="button.close"/></a>
		    </td></tr>
		  </tbody></table>
		</td></tr>
		
		
		</tbody>
		</table>
		<input type="submit" value="ok" style="height:10px; width:10px; margin-left: -8000px;"
		  onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ss_tagObject.entityType}', '${ss_tagObject.id}');return false;"/>
		</form>
		</div>
		
		</ssf:popupPane>
		
		</div>
		
		</c:if>
		</ssf:sidebarPanel>
	</c:if>
</c:if>
