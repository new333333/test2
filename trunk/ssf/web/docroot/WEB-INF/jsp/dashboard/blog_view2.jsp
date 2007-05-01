<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:set var="hitCount" value="0"/>
<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>

<c:forEach var="fileEntry" items="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}" >
  <c:set var="hitCount" value="${hitCount + 1}"/>
  
<div class="ss_blog_summary_title">
  <table cellspacing="0" cellpadding="0" width="100%">
  <tr>
  <td valign="top"><span class="ss_blog_summary_title_text">
    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${fileEntry._binderId}"
		    entryId="${fileEntry._docId}">
		    <ssf:param name="entityType" value="${fileEntry._entityType}" />
    	    <ssf:param name="newTab" value="1"/>
			</ssf:url>"
			onClick="return ss_gotoPermalink('${fileEntry._binderId}','${fileEntry._docId}', '${fileEntry._entityType}', '', 'yes');">

     <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(no title)</i></span>
    </c:if>
    <span class="ss_bold ss_underline"><c:out value="${fileEntry.title}"/></span></a>
	</td>
	<td align="right" nowrap valign="top"><span class="ss_italic ss_smallprint">
	<c:if test="${ssDashboard.scope != 'portlet'}">
	    <c:if test="${fileEntry._entityType == 'folderEntry' || 
      		fileEntry._entityType == 'reply'}">
		<ssf:menu titleId="ss_folderName_${hitCount}_${componentId}_<portlet:namespace/>" 
		    menuClass="ss_actions_bar2 ss_actions_bar_submenu">
		  <ssf:param name="title" useBody="true">
		      <c:if test="${empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
		        <img border="0" src="<html:imagesPath/>icons/folder.gif" <ssf:alt/> />
		      </c:if>
		      <c:if test="${!empty ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}">
		        <img border="0" <ssf:alt/>
		          src="<html:imagesPath/>${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].iconName}" />
		      </c:if>
		  </ssf:param>
		  <ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
		  <li><a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="view_permalink"
				    binderId="${fileEntry._binderId}">
				    <ssf:param name="entityType" value="folder" />
		    	    <ssf:param name="newTab" value="1"/>
					</ssf:url>" 
					onClick="return ss_gotoPermalink('${fileEntry._binderId}', '${fileEntry._binderId}', 'folder', '', 'yes');">
					
		  ${ssDashboard.beans[componentId].ssSearchFormData.ssBinderData[fileEntry._binderId].title}</a></li>
		  </ul>
		</ssf:menu>
     </c:if>
    &nbsp;&nbsp;
    </c:if>
    <c:out value="${fileEntry._principal.title}"/>,&nbsp;&nbsp;
	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      value="${fileEntry._modificationDate}" type="both" 
	  timeStyle="short" dateStyle="short" /></span>&nbsp;&nbsp;
	</td>
	</tr>
	</table>
</div>

<div style="padding-bottom:10px;">
<jsp:useBean id="fileEntry" type="java.util.Map" />
<%
	if (fileEntry.containsKey("_desc")) {
		String[] words = ((String)fileEntry.get("_desc")).split(" ");
		String summary = "";
		for (int i = 0; i < words.length; i++) {
			summary = summary + " " + words[i];
			//Limit the summary to 200 words
			if (i >= 200) {
				if (i < words.length - 1) summary = summary + "...";
				break;
			}
		}
%>
    <div class="ss_smallprint ss_indent_medium">  
      <ssf:markup binderId="${fileEntry._binderId}" entryId="${fileEntry._docId}"
      ><c:out value="<%= summary %>" escapeXml="false"/></ssf:markup>
    </div>
<%
	}
%>  
</div>
</c:forEach>

<div>
  <table width="100%">
   <tr>
    <td>
<c:if test="${hitCount > 0}">
      <span class="ss_light ss_fineprint">
	    [<ssf:nlt tag="folder.Results">
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + 1}"/>
	    <ssf:param name="value" value="${ss_pageNumber * ss_pageSize + hitCount}"/>
	    <ssf:param name="value" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}"/>
	    </ssf:nlt>]
	  </span>
</c:if>
<c:if test="${hitCount == 0}">
    <span class="ss_light ss_fineprint">
	  [<ssf:nlt tag="dashboard.noEntriesFound"/>]
	</span>
</c:if>
	</td>
	<c:if test="${ssDashboard.scope != 'portlet'}">
		<c:set var="binderId" value="${ssBinder.id}"/>
	</c:if>
	<c:if test="${ssDashboard.scope == 'portlet'}">
		<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
	</c:if>
	
	<td align="right">
	  <c:if test="${ss_pageNumber > 0}">
	    <span>
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'blog'); return false;"
	        href="#" >&lt;&lt;&lt;&nbsp;<ssf:nlt tag="general.previousPage"/></a>&nbsp;&nbsp;&nbsp;
	    </span>
	  </c:if>
	  <c:if test="${(ss_pageNumber * ss_pageSize + hitCount) < ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}">
	    <span>&nbsp;&nbsp;
	      <a onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'blog'); return false;"
	        href="#" ><ssf:nlt tag="general.nextPage"/>&nbsp;&gt;&gt;&gt;</a>
	    </span>
	  </c:if>
    </td>
   </tr>
  </table>
</div>
