<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ page session="false" %>
<%@ page contentType="text/html; charset=UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>
<c:if test="${aclMap[entity.id]['modifyEntry']}">
<div style="float: right; margin-left: 5px;"><a href="#"
  onClick="ss_editablePopUp('${editUrl}', '<portlet:namespace/>ss_editableDiv${ss_editableDivIdNumber}');return false;"
  ><span class="ss_fineprint">[<ssf:nlt tag="Edit"/>]</span></a>
  <a style="padding-left:6px;" href="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_editable_history" 
		actionUrl="false">
		<ssf:param name="entityId" value="${ssDefinitionEntry.id}" />
		<ssf:param name="operation" value="modifyEntry" />
		</ssf:url>"
     onClick="ss_openUrlInWindow(this, '_blank');return false;">
  <span class="ss_fineprint">[<ssf:nlt tag="entry.versionHistory"/>]</span></a>
</div>
<c:set var="ss_editableDivIdNumber" value="${ss_editableDivIdNumber + 1}" scope="request"/>
</c:if>
</div>
<div class="ss_clear"></div>
