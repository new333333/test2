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
<% //Mashup folder view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "folder");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<c:set var="mashupBinder" value="${ss_mashupBinders[mashup_attributes['folderId']]}"/>
<c:set var="mashupBinderIdIndex" value="${mashup_attributes['folderId']}"/>
<c:if test="${!empty mashup_attributes['zoneUUID']}">
  <c:set var="zoneBinderId" value="${mashup_attributes['zoneUUID']}.${mashup_attributes['folderId']}" />
  <c:if test="${!empty ss_mashupBinders[zoneBinderId]}">
    <c:set var="mashupBinder" value="${ss_mashupBinders[zoneBinderId]}"/>
    <c:set var="mashupBinderIdIndex" value="${zoneBinderId}"/>
  </c:if>
</c:if>
<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
<li>
</c:if>
<% } %>

<c:if test="${ssConfigJspStyle == 'form'}">
	<script type="text/javascript">
	//Routine called when "Delete entry" is clicked
	function ss_mashup_deleteFolder${ss_mashupItemId}_${renderResponse.namespace}() {
		var formObj = self.document.forms['${ss_form_form_formName}'];
		formObj['${ss_mashupPropertyName}__${ss_mashupItemId}'].value = "";
	}
	</script>
</c:if>
<div class="ss_mashup_element">
  <c:if test="${!empty mashupBinder}">
  <div class="ss_mashup_round_top"><div></div></div>

	  <!-- Should we show the title or the description? -->
	  <c:if test="${ssConfigJspStyle != 'form' && (!empty mashup_attributes['showTitle'] || !empty mashup_attributes['showFolderDescription'])}">
	  	  <!-- Yes -->
		  <c:set var="divClass" value="ss_mashup_folder_header_view" />
		  <div class="${divClass}">
		  	<!-- Should we show the title? -->
			<c:if test="${!empty mashup_attributes['showTitle']}">
				<!-- Yes -->
				<a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
				  action="view_permalink" 
				  binderId="${mashupBinder.id}">
				  <ssf:param name="entityType" value="${mashupBinder.entityType}"/>
				  <ssf:param name="seen_by_gwt" value="1" />
				  </ssf:url>"><span>${mashupBinder.title}</span>
				</a>
			</c:if>
			
			<!--  Should we show the description? -->
			<c:if test="${!empty mashup_attributes['showFolderDescription']}">
				<!--  Yes -->
				<div class="ss_mashup_folder_description">
					<ssf:markup entity="${mashupBinder}">${mashupBinder.description.text}</ssf:markup>
					<div class="ss_clear"></div>
			  	</div>
			</c:if>
		</c:if>
	  </div>

	<c:if test="${ssConfigJspStyle != 'form' && empty mashup_attributes['showEntriesOpened']}">
	<div class="ss_mashup_folder_list_closed">
	  <c:forEach var="entry" items="${ss_mashupBinderEntries[mashupBinderIdIndex]}" varStatus="status">
	    <c:if test="${empty mashup_attributes['entriesToShow'] || status.count <= mashup_attributes['entriesToShow']}">
	      <div style="padding-left:6px;">
	        <ssf:titleLink action="view_permalink" entryId="${entry._docId}" 
				binderId="${entry._binderId}" entityType="folderEntry">
				<ssf:param name="url" useBody="true">
					<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
						action="view_permalink" 
						binderId="${entry._binderId}"
						entryId="${entry._docId}">
						<ssf:param name="entityType" value="folderEntry"/>
						<ssf:param name="seen_by_gwt" value="1" />
					</ssf:url>
				</ssf:param>
				<c:out value="${entry.title}"/>
			</ssf:titleLink>
	      </div>
		</c:if>
	  </c:forEach>
	</div>
	</c:if>
	<c:if test="${ssConfigJspStyle != 'form' && !empty mashup_attributes['showEntriesOpened']}">
	  <c:forEach var="entry" items="${ss_mashupBinderEntries[mashupBinderIdIndex]}" varStatus="status">
	    <c:if test="${empty mashup_attributes['entriesToShow'] || status.count <= mashup_attributes['entriesToShow']}">
	      <div class="ss_mashup_folder_list_open">
			<div class="ss_mashup_folder_list_open_title">
		        <ssf:titleLink action="view_permalink" entryId="${entry._docId}" 
					binderId="${entry._binderId}" entityType="folderEntry">
					<ssf:param name="url" useBody="true">
						<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
							action="view_permalink" 
							binderId="${entry._binderId}"
							entryId="${entry._docId}">
							<ssf:param name="entityType" value="folderEntry"/>
							<ssf:param name="seen_by_gwt" value="1" />
						</ssf:url>
					</ssf:param>
					<c:out value="${entry.title}"/>
				</ssf:titleLink>
			</div>
			<c:if test="${!empty entry._desc}">
			  <div class="ss_mashup_folder_list_open_entry">
			    <ssf:markup search="${entry}">${entry._desc}</ssf:markup>
			    <div class="ss_clear"></div>
			  </div>
			</c:if>
		  </div>
		</c:if>
	  </c:forEach>
	</c:if>
	<c:if test="${ssConfigJspStyle == 'form'}">
	  <div>
	    <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	      class="ss_linkButton ss_fineprint"
	      onClick="ss_mashup_deleteFolder${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
	  </div>
    </c:if>
  <div class="ss_mashup_round_bottom"><div></div></div>
  </c:if>

  <c:if test="${empty mashupBinder}">
	<c:if test="${ssConfigJspStyle == 'form'}">
	   <% if (ss_mashupListDepth > 0) { %>
	    <li>
	   <% } %>
      <div class="ss_mashup_round_top"><div></div></div>
	  <div class="ss_mashup_element">
	    <span class="ss_largeprint ss_bold"><ssf:nlt tag="mashup.type.folder"/></span>
	  </div>
      <div class="ss_mashup_round_bottom"><div></div></div>
	  <div>
	    <input type="submit" name="applyBtn" value="<ssf:nlt tag="button.delete"/>" 
	      class="ss_linkButton ss_fineprint"
	      onClick="ss_mashup_deleteFolder${ss_mashupItemId}_${renderResponse.namespace}();return true;"/>
	  </div>
	   <% if (ss_mashupListDepth > 0) { %>
	    </li>
	   <% } %>
    </c:if>
  </c:if>
</div>
<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
</li>
</c:if>
<% } %>
