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
<% //Mashup folder view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "folder");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);
%>

<c:set var="mashupBinder" value="${ss_mashupBinders[mashup_value1]}"/>
<div style="margin:10px; padding:10px; " width="100%">
  <c:if test="${!empty mashupBinder}">
	<div style="border:1px solid #cecece; background-color:#e5e5e5; padding:6px;">
		<a href="<ssf:url action="view_folder_listing" 
		  folderId="${mashupBinder.id}">
		  <ssf:param name="newTab" value="1"/>
		  </ssf:url>"><span class="ss_largeprint ss_bold">${mashupBinder.title}</span></a>
	</div>
	<c:if test="${ssConfigJspStyle != 'form'}">
	<div style="border:1px solid #cecece;padding:6px;">
	  <c:forEach var="entry" items="${ss_mashupBinderEntries[mashup_value1]}" >
	    <div style="padding-left:6px;">
	      <a href="<ssf:url     
		    action="view_folder_entry" 
		    binderId="${entry._binderId}"
		    entryId="${entry._docId}"
		    ><ssf:param name="entryViewStyle" value="full"/>
		    </ssf:url>">
	        <span>${entry.title}</span>
	      </a>
	    </div>
	  </c:forEach>
	</div>
	</c:if>
  </c:if>

  <c:if test="${empty mashupBinder}">
	<script type="text/javascript">
	//Routine called when "Delete entry" is clicked
	function ss_mashup_deleteFolder${ss_mashupItemId}_${renderResponse.namespace}() {
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "";
	}
	</script>
	<div style="border:1px solid #cecece; background-color:#e5e5e5; padding:6px;">
	  <a href="<ssf:url action="view_folder_listing" 
		  folderId="${mashupBinder.id}">
		  <ssf:param name="newTab" value="1"/>
		  </ssf:url>"
	  ><span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.folder"/></span></a>
	  <br/>
	  <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	    class="ss_linkButton ss_fineprint"
	    onClick="ss_mashup_deleteFolder${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
	</div>
  </c:if>
</div>
