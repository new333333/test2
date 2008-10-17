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
<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!property_generated}">

<c:choose>
  <c:when test="${ss_formViewStyle == 'guestbook'}">
		<input type="hidden" name="title" value="<c:out value="${ssUser.firstName}"/> <c:out value="${ssUser.lastName}"/> wrote" />
  </c:when>

  <c:otherwise>
		<%
			String caption = (String) request.getAttribute("property_caption");
			if (caption == null) {caption = "";}
		%>
		<div class="ss_entryContent">
		<div class="ss_labelAbove" id="${property_name}_label">${property_caption}</div>
		<input type="text" class="ss_text" name="title" id="title" size="40"
		 <c:if test="${empty ssDefinitionEntry.title}">
		   <c:if test="${empty ssEntryTitle && !empty ssEntry}">
		     value="<ssf:nlt tag="reply.re.title"><ssf:param 
		       name="value" useBody="true">${ssEntry.title}</ssf:param></ssf:nlt>"
		   </c:if>
		   <c:if test="${!empty ssEntryTitle || empty ssEntry}">
		     value="<c:out value="${ssEntryTitle}"/>"
		   </c:if>
		 </c:if>
		 <c:if test="${!empty ssDefinitionEntry.title}">
		   value="<c:out value="${ssDefinitionEntry.title}"/>"
		 </c:if>
		 >
		</div>
<script type="text/javascript">
function ss_focusOnTitle() {
	var formObj = self.document.getElementById('${ss_form_form_formName}')
	if (formObj != null) {
		if (typeof formObj.title != 'undefined' && 
				typeof formObj.title.type != 'undefined' && 
				formObj.title.type == 'text') {
			formObj.title.focus();
		}
	}
}
ss_focusOnTitle();
</script>
  </c:otherwise>
</c:choose>

</c:if>