<%
// The dashboard "search" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div>
<c:set var="hitCount" value="0"/>
<c:forEach var="fileEntry" items="${ssDashboard.beans[ssComponentId].ssSearchFormData.searchResults}" >
  <c:set var="hitCount" value="${hitCount + 1}"/>
  
<div class="ss_blog_title">
<span class="ss_bold ss_largerprint">
 <a style="text-decoration: none;" href="<ssf:url 
            adapter="true" 
            portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>">
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_light">
    --<ssf:nlt tag="entry.noTitle"/>--
  </span>
</c:if>
<c:out value="${fileEntry.title}"/></a>
</span>
</div>
<br>
<span class="ss_italic ss_smallprint">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${fileEntry._creationDate}" type="both" 
	 timeStyle="short" dateStyle="medium" />
 by <c:out value="${fileEntry.creation.principal.title}"/>
</span>

  <div style="padding-bottom:6px;">
    <div>
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>">
    </c:when>
    <c:when test="${fileEntry._entityType == 'user'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
			action="view_permalink"
			binderId="${fileEntry._principal.workspaceId}">
			<ssf:param name="entityType" value="workspace" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'group'}">
    <a target="_blank" href="<ssf:url action="view_profile_entry" 
    		folderId="${fileEntry._binderId}"
    		entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'folder' || fileEntry._entityType == 'workspace' || fileEntry._entityType == 'profiles'}">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" >
    </c:when>
 	</c:choose>
    <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(no title)</i></span>
    </c:if>
    <span class="ss_bold ss_underline"><c:out value="${fileEntry.title}"/></span></a>

    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <span class="ss_smallprint">
      <c:out value="${fileEntry._principal.title}"/>,&nbsp;&nbsp;
	<fmt:formatDate timeZone="${fileEntry._principal.timeZone.ID}"
      value="${fileEntry._modificationDate}" type="both" 
	  timeStyle="short" dateStyle="short" /></span>
    
    
    &nbsp;&nbsp;&nbsp;
    <c:if test="${fileEntry._entityType == 'folderEntry' || 
      		fileEntry._entityType == 'reply'}">
      <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}">
		    <ssf:param name="entityType" value="folder" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>" 
    	onMouseover="ss_showObjInline('ss_folderName_${hitCount}_${ssComponentId}_<portlet:namespace/>');"
    	onMouseout="ss_hideObj('ss_folderName_${hitCount}_${ssComponentId}_<portlet:namespace/>');"
      >
      <c:if test="${empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
        <img src="<html:imagesPath/>icons/folder.gif"/>
      </c:if>
      <c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
        <img src="<html:imagesPath/>${ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}" />
      </c:if>
       <div id="ss_folderName_${hitCount}_${ssComponentId}_<portlet:namespace/>" 
       style="position:absolute; display:none;">${ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderData[fileEntry._binderId].title}</div></a>
    </c:if>

    </div>
  
<jsp:useBean id="fileEntry" type="java.util.Map" />
<%
	if (fileEntry.containsKey("_desc")) {
		String[] words = ((String)fileEntry.get("_desc")).split(" ");
		String summary = "";
		for (int i = 0; i < words.length; i++) {
			summary = summary + " " + words[i];
			//Limit the summary to 30 words
			if (i >= 30) {
				if (i < words.length - 1) summary = summary + "...";
				break;
			}
		}
%>
    <div class="ss_smallprint ss_indent_medium">  
      <c:out value="<%= summary %>" escapeXml="false"/>
    </div>
<%
	}
%>  
  </div>
</c:forEach>
<c:if test="${hitCount > 0}">
  <div align="right">
    <span class="ss_light ss_fineprint">
	[<ssf:nlt tag="search.results">
	<ssf:param name="value" value="1"/>
	<ssf:param name="value" value="${hitCount}"/>
	<ssf:param name="value" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssEntrySearchCount}"/>
	</ssf:nlt>]
	</span>
  </div>
</c:if>
<c:if test="${hitCount == 0}">
  <div>
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="search.noneFound"/>]
	</span>
  </div>
</c:if>
</div>
