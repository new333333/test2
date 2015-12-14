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
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body>
  <c:set var="count" value="0"/>
  <div id="${ss_divId}" style="padding:2px;margin:2px;">
	<c:if test="${!empty ssEntries}">
      <table cellspacing="0" cellpadding="0">
      <tbody>
      <tr>
      <td colspan="2">
      <ul>
      
		<c:forEach var="entry" items="${ssEntries}">  
		  <c:set var="count" value="${count + 1}"/>
		  <li id="ss_find_id_<c:choose><%--
		          --%><c:when test="${ssFindType == 'personalTags' || ssFindType == 'communityTags' || ssFindType == 'tags' }"><%--
		      		--%>${entry.ssTag}<%--
		      	  --%></c:when><%--
  		          --%><c:otherwise><%--
  		          	--%>${entry._docId}<%--
  		          --%></c:otherwise></c:choose>"><a 
		    onClick="<c:choose><%--
		    			--%><c:when test="${ssFindType == 'personalTags' || ssFindType == 'communityTags' }"><%--
		    				--%>parent.ss_putValueInto('ss_combobox_autocomplete_${ss_namespace}', '${entry.ssTag}');return false;<%--
		    			--%></c:when><%--
						--%><c:otherwise><%--
							--%>parent['${findObjectName}'].selectItemAccessible(this.parentNode, '${entry._entityType}');return false;<%--
						--%></c:otherwise><%--
			   		 --%></c:choose>" 
		    href="javascript: ;"
		    <c:if test="${ssFindType == 'entries'}">
			    <ssf:title tag="title.open.folderEntry">
					<ssf:param name="value" value="${entry.title}" />
				</ssf:title>
		    </c:if>
		    ><span style="white-space:nowrap;"><c:choose><%--
		          --%><c:when test="${!empty entry.ssTag}"><%--
		      		--%>${entry.ssTag}<%--
		      	  --%></c:when><%--
		          --%><c:when test="${!empty entry._extendedTitle}"><%--
		      		--%>${entry._extendedTitle}<%--
		      	  --%></c:when><%--		      	  
  		          --%><c:otherwise><%--
  		          	--%>${entry.title}<%--
  		          --%></c:otherwise></c:choose></span></a></li>
		</c:forEach>
      </ul>
      </td>
      </tr>
      <tr><td colspan="2"><br/></td></tr>
      <tr>
      <td width="100" nowrap="nowrap" style="white-space:nowrap;">
        <c:if test="${ss_pageNumber > 0}">
          <a href="javascript:;" onClick="parent['${findObjectName}'].prevPage();return false;"
          ><ssf:nlt tag="general.Previous"/>...</a>
        </c:if>
        </td>
       <td style="white-space:nowrap;">
        <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
          <a href="javascript:;" onClick="parent['${findObjectName}'].nextPage();return false;"
          ><ssf:nlt tag="general.Next"/>...</a>
        </c:if>
       </td>
       </tr>
       </tbody>
       </table>
	</c:if>
	<c:if test="${empty ssEntries}">
	 <ul>
	  <li>
	    <table cellspacing="0" cellpadding="0"><tbody><tr>
	    <td nowrap="nowrap" style="white-space:nowrap;">
	      <ssf:nlt tag="findUser.noneFound"/>
	    </td></tr>
	    </tbody></table>
	  </li>
	 </ul>
	</c:if>
  </div>

<script type="text/javascript">
self.window.focus();
</script>

</body>
</html>

