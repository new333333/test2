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
<% //Mashup entry view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "entry");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<% if (ss_mashupListDepth > 0) { %>
<li>
<% } %>

<c:if test="${ssConfigJspStyle == 'form'}">
	<script type="text/javascript">
	//Routine called when "Delete entry" is clicked
	function ss_mashup_deleteEntry${ss_mashupItemId}_${renderResponse.namespace}() {
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "";
	}
	</script>
</c:if>
<c:set var="mashupEntry" value="${ss_mashupEntries[mashup_attributes['entryId']]}"/>
<c:if test="${!empty mashupEntry}">
  <div class="ss_mashup_element">
  <div class="ss_mashup_round_top"><div></div></div>
	<c:if test="${ssConfigJspStyle == 'form' || !empty mashup_attributes['showTitle']}">
	  <div class="ss_mashup_entry_header">
		<c:if test="${ssConfigJspStyle == 'form'}">
		  <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.entry"/>: </span>
		</c:if>
		<c:set var="spanClass" value="" />
		<c:if test="${ssConfigJspStyle != 'form'}">
		  <c:set var="spanClass" value="ss_largeprint ss_bold" />
		</c:if>
		<a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
		  action="view_folder_entry" 
		  folderId="${mashupEntry.parentBinder.id}"
		  entryId="${mashupEntry.id}">
		  <ssf:param name="entryViewStyle" value="full"/>
		  <ssf:param name="newTab" value="1"/>
		  </ssf:url>"><span class="${spanClass}">${mashupEntry.title}</span></a>
		  <c:if test="${ssConfigJspStyle == 'form'}">
		    <span class="ss_italic ss_smallprint">(${mashupEntry.parentBinder.title})</span>
		  </c:if>
	  </div>
	</c:if>
	<c:if test="${ssConfigJspStyle != 'form'}">
	  <div class="ss_mashup_entry_content">
	    <ssf:markup entity="${mashupEntry}">${mashupEntry.description.text}</ssf:markup>
	  </div>
	</c:if>
  <div class="ss_mashup_round_bottom"><div></div></div>
	<c:if test="${ssConfigJspStyle == 'form'}">
	  <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	    class="ss_linkButton ss_fineprint"
	    onClick="ss_mashup_deleteEntry${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
    </c:if>
  </div>
</c:if>

<c:if test="${empty mashupEntry}">
  <c:if test="${ssConfigJspStyle == 'form'}">
   <div class="ss_mashup_element">
	<div class="ss_mashup_entry_header">
	  <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.entry"/></span>
	    <br/>
	    <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	      class="ss_linkButton ss_fineprint"
	      onClick="ss_mashup_deleteEntry${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
	</div>
   </div>
  </c:if>
</c:if>

<% if (ss_mashupListDepth > 0) { %>
</li>
<% } %>
