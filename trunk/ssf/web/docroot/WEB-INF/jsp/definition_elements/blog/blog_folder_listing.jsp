<% //View the listing part of a blog folder %>

  <table class="ss_blog" width="100%">
    <tr>
      <td class="ss_blog_content" width="80%" valign="top">
		  <c:forEach var="entry" items="${ssFolderEntries}" >
			<div class="ss_blog_content" style="margin:2px 8px 20px 2px;">
			  <c:set var="ss_blog_docId" value="${entry._docId}" scope="request"/>
			  <c:set var="ss_blog_workflowStateCaption" value="" scope="request"/>
			  <c:if test="${!empty entry._workflowStateCaption}">
			  <c:set var="ss_blog_workflowStateCaption" value="${entry._workflowStateCaption}" scope="request"/>
			  </c:if>
			  <ssf:displayConfiguration 
			    configDefinition="${ssBlogEntries[entry._docId].ssConfigDefinition}" 
			    configElement="${ssBlogEntries[entry._docId].ssConfigElement}" 
			    configJspStyle="view"
			    processThisItem="true" 
			    entry="${ssBlogEntries[entry._docId].entry}" />
			</div>
		  </c:forEach>
	  </td>
	  <td class="ss_blog_sidebar" width="20%" valign="top">
		<span class="ss_bold"><ssf:nlt tag="blog.calendar"/></span>
		<br>
		<div id="ss_blog_sidebar_date_popup"></div>
		<form name="ss_blog_sidebar_date_form" style="display:inline;">
		  <ssf:datepicker id="ss_blog_sidebar_date" 
            calendarDivId="ss_blog_sidebar_date_popup"
            formName="ss_blog_sidebar_date_form"
            immediateMode="true" initDate="${ssFolderEndDate}"
			callbackRoutine="ss_blog_sidebar_date_callback" />
        </form>
        
        <br>
        <br>
		<span class="ss_bold ss_underline"><ssf:nlt tag="blog.archives"/></span>
		<br/>
		<table>
		<c:forEach var="monthYear" items="${ssBlogMonthHits}">
		  <tr>
		  <td><a href="${ssBlogMonthUrls[monthYear.key]}"><c:out value="${ssBlogMonthTitles[monthYear.key]}"/></a></td>
		  <td align="right">(<c:out value="${monthYear.value}"/>)</td>
		  </tr>
		</c:forEach>
		</table>
		
        <br>
        <br>
		<span class="ss_bold ss_underline"><ssf:nlt tag="tags.community"/></span>
		<br/>
		   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
		   
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="cTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="${tag.searchResultsRatingCSS}">${tag.ssTag}</a>&nbsp;&nbsp;
		   	
		   </c:forEach>
		
        <br>
        <br>
		<span class="ss_bold ss_underline"><ssf:nlt tag="tags.personal"/></span>
		<br/>
		   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
		
		   	<a href="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_folder_listing"/><portlet:param 
				name="binderId" value="${ssBinder.id}"/><portlet:param 
				name="pTag" value="${tag.ssTag}"/></portlet:actionURL>" 
				class="${tag.searchResultsRatingCSS}">${tag.ssTag}</a>&nbsp;&nbsp;
						
		   </c:forEach>
	  </td>
    </tr>
  </table>
