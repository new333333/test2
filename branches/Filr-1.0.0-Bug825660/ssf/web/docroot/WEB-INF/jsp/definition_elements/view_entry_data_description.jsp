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
<% //Description view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="margintop1"></div>
<c:set var="ss_divCounter" value="${ss_divCounter + 1}" scope="request" />
<%
	//Get the user's desired region view (if set)
	String regionView_descriptionRegion = "expanded";
	Map userProperties = (Map)  request.getAttribute("ssUserProperties");
	if (userProperties != null && userProperties.containsKey("regionView.descriptionRegion")) {
		regionView_descriptionRegion = (String) userProperties.get("regionView.descriptionRegion");
	}
	if ("collapsed".equals(regionView_descriptionRegion)) {
		%><c:set var="regionClass" value="wg-description-content-clipped"/><c:set var="regionImg" value="expand_16_yellow.png"/><%
	} else {
		%><c:set var="regionClass" value="wg-description-content"/><c:set var="regionImg" value="collapse_16_yellow.png"/><%
	}
%>
<c:set var="textFormat" value=""/>
<c:if test="${!empty ssDefinitionEntry.description.format}">
  <c:set var="textFormat" value="${ssDefinitionEntry.description.format}"/>
</c:if>
<c:if test="${!empty ssDefinitionEntry.description.text}">
    <c:if test="${ssDefinitionEntry.entityType != 'folderEntry' || ssDefinitionEntry.top}">
    <div id="descriptionRegionImg${ss_divCounter}" class="margintop2 descriptionRegionBut">
      <a href="javascript: ;" 
        onClick="ss_toggleRegion(this, 'descriptionRegion${ss_divCounter}', 'descriptionRegion', 'wg-description-content', 200);return false;" 
        alt="<ssf:nlt tag="general.expandCollapseRegion"/>" title="<ssf:nlt tag="general.expandCollapseRegion"/>"
      ><img src="<html:rootPath/>images/pics/${regionImg}"/></a>
    </div>
    </c:if>
    <div id="descriptionRegion${ss_divCounter}" style="width:100%;">
	  <ssf:editable entity="${ssDefinitionEntry}" element="description" aclMap="${ss_accessControlMap}">
		<c:if test="${textFormat == '2'}">
		  <ssf:textFormat formatAction="textToHtml">${ssDefinitionEntry.description.text}</ssf:textFormat>
		</c:if>
		<c:if test="${textFormat != '2'}">
		  <div>
			<ssf:markup entity="${ssDefinitionEntry}" leaveSectionsUnchanged="true" 
			>${ssDefinitionEntry.description.text}</ssf:markup>
		  </div>
		</c:if>
		<div class="ss_clear"></div>
	  </ssf:editable>
    </div>
<script type="text/javascript">
ss_createOnLoadObj("descriptionRegion${ss_divCounter}", function() {
	ss_toggleRegionInit('descriptionRegion${ss_divCounter}', 'descriptionRegionImg${ss_divCounter}', 200, '${regionClass}');
});
</script>
</c:if>
