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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<div class="ss_photo_sidebar">
	<div class="ss_photo_sidebar_subhead">
	  <ssf:expandableArea title='<%= NLT.get("photo.albums") %>' titleClass="ss_photo_title" 
	  	action="wipe" initOpen="true">
	  <div class="ss_photo_sidebar_box">
        <table cellspacing="0" cellpadding="0">
	     <c:forEach var="blogPage" items="${ssBlogPages}">
 		   <tr><td>
 		     <div style="padding:0px 4px 4px 15px;">
	           <a class="<c:if test="${blogPage.id == ssBinder.id}"> ss_navbar_current</c:if>
					   <c:if test="${blogPage.id != ssBinder.id}"></c:if>" 
				href="<ssf:url action="view_folder_listing" binderId="${blogPage.id}"/>"
	         >${blogPage.title}</a></div>
	       </td></tr>
	     </c:forEach>
	    </table>
       </div>
      </ssf:expandableArea>
	</div>
	<div class="ss_photo_sidebar_subhead">	
	  <ssf:expandableArea title='<%= NLT.get("blog.archives") %>' titleClass="ss_photo_title" 
	    action="wipe" initOpen="true">
	      <div class="ss_blog_sidebar_box marginleft2">		
			<table>
			  <tr>
			   <td colspan="2">
			      <a href="<ssf:url action="${action}" actionUrl="true">
				    <ssf:param name="operation" value="view_folder_listing"/>
				    <ssf:param name="binderId" value="${ssBinder.id}"/>
				    </ssf:url>"
				    <c:if test="${empty ss_yearMonth}">
				      class="ss_navbar_current" 
				    </c:if>
			      ><ssf:nlt tag="photo.showCurrentAlbum"/></a>
			   </td>
			  </tr>
			  <c:forEach var="monthYear" items="${ssBlogMonthHits}">
		  	  <tr>
		  	   <td style="padding-top: 5px;">
		  	    <a href="${ssBlogMonthUrls[monthYear.key]}" 
		  		  <c:if test="${monthYear.key == ss_yearMonth}">
	        		class="ss_navbar_current" 
	      		  </c:if>
	      		>
		  		<c:out value="${ssBlogMonthTitles[monthYear.key]}"/></a>
		  	   </td>
		  	   <td align="right">(<c:out value="${monthYear.value}"/>)</td>
		  	  </tr>
	
		  	  <c:if test="${monthYear.key == ss_yearMonth}">
		       <c:forEach var="blogPage" items="${ssBlogPages}">
		        <c:set var="monthFolder" value="${monthYear.key}/${blogPage.id}"/>
		        <c:if test="${!empty ssBlogMonthFolderHits[monthFolder]}">
		         <tr>
		          <td style="padding-left:10px; padding-top: 3px;">
		           <a href="<ssf:url action="view_folder_listing" binderId="${blogPage.id}"><ssf:param 
		             name="yearMonth" value="${ss_yearMonth}" /></ssf:url>"
		           >${blogPage.title}</a>
		          </td>
		          <td align="right">(<c:out value="${ssBlogMonthFolderHits[monthFolder]}"/>)</td>
		         </tr>
		        </c:if>
		       </c:forEach>
		  	  </c:if>
		    </c:forEach>
		   </table>
	      </div>		
      </ssf:expandableArea>
  	</div>
   <c:if test="${!empty ssFolderEntryCommunityTags}"> 
	<div class="ss_photo_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
    <div class="ss_photo_sidebar_box">		
	   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
		   	<a href="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
				name="binderId" value="${ssBinder.id}"/><ssf:param 
				name="cTag" value="${tag.ssTag}"/></ssf:url>" 
				class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;
	   </c:forEach>
    </div>		
   </c:if>
   <c:if test="${!empty ssFolderEntryPersonalTags}"> 
	<div class="ss_photo_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
    <div class="ss_photo_sidebar_box">		
	   <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">		
	   	<a href="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
			name="binderId" value="${ssBinder.id}"/><ssf:param 
			name="pTag" value="${tag.ssTag}"/></ssf:url>" 
			class="ss_displaytag ${tag.searchResultsRatingCSS} <c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>">${tag.ssTag}</a>&nbsp;&nbsp;						
	   </c:forEach>
	</div>
   </c:if>
	   	   
</div>
