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
      
<c:forEach var="entry1" items="${ssFolderEntries}" varStatus="status" >
	<jsp:useBean id="entry1" type="java.util.HashMap" />
	<c:set var="seenStyleburst" value=""/>
	<%
		String folderLineId = "folderLine_" + (String) entry1.get("_docId");
		String seenStyle = "";
		String seenStyleFine = "ss_finePrint";
		if (!ssSeenMap.checkIfSeen(entry1)) {
			seenStyle = "ss_unseen";
			seenStyleFine = "ss_unseen ss_fineprint";
			%><c:set var="seenStyleburst" value="1"/><%
		}
	%>
    <div class="margintop2 marginleft2">
	  <!-- Sunburst -->
	  <c:if test="${!empty seenStyleburst}">
	  	<a id="ss_sunburstDiv${ssBinder.id}_${entry1._docId}" href="javascript: ;" 
	  		title="<ssf:nlt tag="sunburst.click"/>"
	  		onClick="ss_hideSunburst('${entry1._docId}', '${ssBinder.id}');return false;"
		><img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
	  	  align="text-bottom" border="0" <ssf:alt tag="sunburst.click"/> />&nbsp;
	    </a>
	  </c:if>
      <a 
        href="<ssf:url     
          adapter="true" 
          portletName="ss_forum" 
          folderId="${ssFolder.id}" 
          action="view_folder_entry" 
          entryId='<%= entry1.get("_docId").toString() %>' actionUrl="true"><ssf:param
          name="entryViewStyle" value="popup"/><ssf:param
          name="namespace" value="${renderResponse.namespace}"/></ssf:url>" 
        <ssf:title tag="title.open.folderEntry">
	      <ssf:param name="value" useBody="true"><c:choose><c:when 
	        test="${!empty entry1.title}"><c:out value="${entry1.title}" escapeXml="true"/></c:when><c:otherwise>--<ssf:nlt 
	        tag="entry.noTitle"/>--</c:otherwise></c:choose></ssf:param>
        </ssf:title>
        onClick="ss_hideSunburst('${entry1._docId}', '${ssBinder.id}');ss_loadEntry(this, '${entry1._docId}', '${ssFolder.id}', '${entry1._entityType}', '${renderResponse.namespace}', 'no');return false;" 		    	
      ><c:if test="${empty entry1.title}"
      ><span id="folderLineSeen_${entry1._docId}" class="<%= seenStyleFine %>"
        >--<ssf:nlt tag="entry.noTitle"/>--</span
      ></c:if><span id="folderLineSeen_${entry1._docId}" class="<%= seenStyle %>"
        ><c:out value="${entry1.title}" escapeXml="true"/></span></a>
    </div>
    
</c:forEach>
    