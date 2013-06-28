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
<c:set var="mashupEntry" value="${ss_mashupEntries[mashup_attributes['entryId']]}"/>
<c:if test="${!empty mashup_attributes['zoneUUID']}">
  <c:set var="zoneEntryId" value="${mashup_attributes['zoneUUID']}.${mashup_attributes['entryId']}" />
  <c:if test="${!empty ss_mashupEntries[zoneEntryId]}">
    <c:set var="mashupEntry" value="${ss_mashupEntries[zoneEntryId]}"/>
  </c:if>
</c:if>
<c:set var="mWidth" value="100%" />
<c:set var="mHeight" value="100%" />
<c:set var="mOverflow" value="hidden" />
<c:if test="${!empty mashup_attributes['width']}">
  <c:set var="mWidth" value="${mashup_attributes['width']}" />
</c:if>
<c:if test="${!empty mashup_attributes['height']}">
  <c:set var="mHeight" value="${mashup_attributes['height']}" />
</c:if>
<c:if test="${!empty mashup_attributes['overflow']}">
  <c:set var="mOverflow" value="${mashup_attributes['overflow']}" />
</c:if>

<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupEntry}">
<li>
</c:if>
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
<c:if test="${!empty mashupEntry}">
  <div class="ss_mashup_element"
    <c:if test="${ssConfigJspStyle != 'form'}">
      style="width: ${mWidth}; overflow: hidden;"
    </c:if>
  >
  <div class="ss_mashup_round_top"><div></div></div>
	<c:if test="${ssConfigJspStyle == 'form' || !empty mashup_attributes['showTitle']}">
	  <div class="ss_mashup_entry_header">
		<c:if test="${ssConfigJspStyle == 'form'}">
		  <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.entry"/>: </span>
		</c:if>
		<c:set var="spanClass" value="" />
		<c:if test="${ssConfigJspStyle != 'form'}">
		  <c:set var="spanClass" value="ss_size_20px ss_bold" />
		</c:if>
		<ssf:titleLink action="view_folder_entry" entryId="${mashupEntry.id}" 
			binderId="${mashupEntry.parentBinder.id}" entityType="folderEntry"
			seenStyle="class='${spanClass}'" seenStyleFine="class='${spanClass}'">
			<ssf:param name="url" useBody="true">
				<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
					action="view_permalink" 
					binderId="${mashupEntry.parentBinder.id}"
					entryId="${mashupEntry.id}">
					<ssf:param name="entityType" value="folderEntry"/>
					<ssf:param name="seen_by_gwt" value="1" />
				</ssf:url>
			</ssf:param>
			<c:out value="${mashupEntry.title}"/>
		</ssf:titleLink>
		
		<c:if test="${ssConfigJspStyle == 'form'}">
		    <span class="ss_italic ss_smallprint">(${mashupEntry.parentBinder.pathName})</span>
		</c:if>
	  </div>
	</c:if>
	<c:if test="${ssConfigJspStyle != 'form'}">
  	  <div class="ss_mashup_folder_list_open" style="height: ${mHeight}; overflow: ${mOverflow};">
	    <div class="ss_mashup_entry_content">
		  <c:set var="textFormat" value=""/>
		  <c:if test="${!empty mashupEntry.description.format}">
			  <c:set var="textFormat" value="${mashupEntry.description.format}"/>
		  </c:if>
		  <ssf:editable entity="${mashupEntry}" element="description">
			<c:if test="${textFormat == '2'}">
			  <ssf:textFormat formatAction="textToHtml">${mashupEntry.description.text}</ssf:textFormat>
			</c:if>
			<c:if test="${textFormat != '2'}">
			  <div>
				<ssf:markup entity="${mashupEntry}" leaveSectionsUnchanged="true" 
				>${mashupEntry.description.text}</ssf:markup>
			  </div>
			</c:if>
			<div class="ss_clear"></div>
		  </ssf:editable>
	    </div>
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

<c:if test="${ssConfigJspStyle == 'form' && empty mashupEntry}">
   <% if (ss_mashupListDepth > 0) { %>
    <li>
   <% } %>
   <div class="ss_mashup_element">
	 <div class="ss_mashup_entry_header">
	    <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.entry"/></span>
	    <br/>
	    <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	      class="ss_linkButton ss_fineprint"
	      onClick="ss_mashup_deleteEntry${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
	 </div>
   </div>
   <% if (ss_mashupListDepth > 0) { %>
    </li>
   <% } %>
</c:if>

<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupEntry}">
</li>
</c:if>
<% } %>
