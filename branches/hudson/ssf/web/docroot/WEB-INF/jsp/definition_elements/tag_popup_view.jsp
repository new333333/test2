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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_tagViewNamespace" value="${renderResponse.namespace}" scope="request"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_tagViewNamespace" value="${ss_namespace}" scope="request"/></c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
<c:if test="${empty ss_tagDivNumber}">
  <c:set var="ss_tagDivNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_tagDivNumber" value="${ss_tagDivNumber + 1}" scope="request"/>
<script type="text/javascript">
var ss_tagConfirmBadCharacters = "<ssf:nlt tag="tags.badChars"/>";
var ss_tagConfirmTooLong = "<ssf:nlt tag="tags.tooLong"/>";
var ss_tagConfirmNoPunct = "<ssf:nlt tag="tags.noPunctAllowed"/>";
var ss_tagConfirmNoUnderscore = '<ssf:nlt tag="tags.noUnderscoreAllowed" />';
</script>
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

<table class="ss_style" cellspacing="0" cellpadding="0" style="padding-top:6px;">
 <tbody>
  <tr>
	<th>
	  	<div class="ss_tag_label">
			<span>
				<img border="0" src="<html:imagesPath/>pics/1pix.gif" 
					<ssf:alt tag=""/> />
	  			<ssHelpSpot helpId="workspaces_folders/misc_tools/tags" offsetX="0" offsetY="0" xAlignment="left" 
	     				  	title="<ssf:nlt tag="helpSpot.tags" text="Tags"/>">
	  			</ssHelpSpot>
			</span>
	  		<ssf:nlt tag="tags.tags.colon"/>
		</div>

	  <div id="ss_tags_anchor${ss_tagViewNamespace}_${ss_tagDivNumber}">

	  </div>
	</th>
  </tr>
  <tr>
	<td width="100%" valign="top" >
	  <div class="ss_tag_content">
		<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data_cloud.jsp" />
	  </div>
	</td>
  </tr>
 </tbody>
</table>

<div class="ss_sub_sidebarMenu" style="padding:5px 0px 5px 10px; ">
  <form class="ss_style ss_form ss_tag_pane_color" 
  	method="post"
  	id="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" 
  	name="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}">

		<jsp:include page="/WEB-INF/jsp/definition_elements/tag_table_view.jsp" />

		<input type="submit" value="<ssf:nlt tag="button.ok"/>" style="height:10px; width:10px; margin-left: -8000px;"
  		  onClick="ss_tagAdd('${ss_tagViewNamespace}', '${ss_tagDivNumber}', '${ssBinder.id}', 
  		  '${ss_tagObject.entityType}', '${ss_tagObject.id}');return false;"/>
  </form>
</div>




</c:if>