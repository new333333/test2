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
<% //Mashup URL view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "url");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<% if (ss_mashupListDepth > 0) { %>
<li>
<% } %>

<c:if test="${ssConfigJspStyle != 'form'}">
<div class="ss_mashup_url_content">
  <c:if test="${!empty mashup_attributes['title']}">
    <a 
      <c:if test="${!empty mashup_attributes['popup']}"> target="_blank" </c:if>
      href="${mashup_attributes['href']}">
      <span class="ss_title_link">${mashup_attributes['title']}</span>
    </a>
  </c:if>
</div>
</c:if>

<c:if test="${ssConfigJspStyle == 'form'}">
	<script type="text/javascript">
	//Routine called when "Delete url" is clicked
	function ss_mashup_deleteUrl${ss_mashupItemId}_${renderResponse.namespace}() {
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "";
	}
	</script>

  <div class="ss_mashup_element">
    <div class="ss_mashup_form_element_header">
      <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.url"/>: </span>
      <span class="ss_title_link">${mashup_attributes['title']}</span>
      <span class="ss_italic ss_smallprint">(${mashup_attributes['href']})</span>
    </div>
    <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
      class="ss_linkButton ss_fineprint"
      onClick="ss_mashup_deleteUrl${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
  </div>
</c:if>
<% if (ss_mashupListDepth > 0) { %>
</li>
<% } %>
