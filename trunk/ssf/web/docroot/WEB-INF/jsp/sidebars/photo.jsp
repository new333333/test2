<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<ssf:sidebarPanel title="__definition_default_folder_photo" id="ss_folder_sidebar" divClass="ss_blog_sidebar"
    initOpen="true" sticky="true">

		  <div style="margin-top: 15px;">
		  <c:if test="${ssConfigJspStyle != 'template'}">
		  <a class="ss_linkButton" href="<portlet:actionURL windowState="maximized" portletMode="view">
			<portlet:param name="action" value="${action}"/>
			<portlet:param name="operation" value="view_folder_listing"/>
			<portlet:param name="binderId" value="${ssBinder.id}"/>
			</portlet:actionURL>"
		  ><ssf:nlt tag="photo.showAll"/></a>
		  </c:if>
		  <c:if test="${ssConfigJspStyle == 'template'}">
		    <ssf:nlt tag="photo.showAll"/>
	      </c:if>
		  </div>

		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="photo.findPage"/></div>
	    <form method="post" name="ss_findWikiPageForm<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>"
	    	action="<portlet:actionURL 
	                windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
		 <ssf:find formName="ss_findWikiPageForm${renderResponse.namespace}" 
		    formElement="searchTitle" 
		    type="entries"
		    width="160px" 
		    binderId="${ssBinder.id}"
		    searchSubFolders="false"
		    singleItem="true"
		    clickRoutine="ss_loadPhotoEntryId${renderResponse.namespace}"/> 
	    <input type="hidden" name="searchTitle"/>
	    </form>
		
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="blog.archives"/></div>
        <div class="ss_blog_sidebar_box">		
		<table>
		<c:forEach var="monthYear" items="${ssBlogMonthHits}">
		  <tr>
		  <td><a href="${ssBlogMonthUrls[monthYear.key]}" 
		  class="<c:if test="${!empty selectedYearMonth && selectedYearMonth == ssBlogMonthTitles[monthYear.key]}">ss_bold</c:if>">
		  <c:out value="${ssBlogMonthTitles[monthYear.key]}"/></a></td>
		  <td align="right">(<c:out value="${monthYear.value}"/>)</td>
		  </tr>
		</c:forEach>
		</table>
        </div>		
		
       <c:if test="${!empty ssFolderEntryCommunityTags}"> 
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
        <div class="ss_blog_sidebar_box">		
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
			   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
					name="action" value="view_folder_listing"/><portlet:param 
					name="binderId" value="${ssBinder.id}"/><portlet:param 
					name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
					class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
		   </c:forEach>
        </div>		
       </c:if>
       <c:if test="${!empty ssFolderEntryPersonalTags}"> 
		<div class="ss_blog_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
        <div class="ss_blog_sidebar_box">		
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">		
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;						
		   </c:forEach>
		</div>
	   </c:if>
	   	   
</ssf:sidebarPanel>
<%@ include file="/WEB-INF/jsp/sidebars/folder_tags.jsp" %>