<% // Blog view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />

<script type="text/javascript">
function ss_loadEntry(obj,id) {
	ss_highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
</div>
<br>
<script type="text/javascript">
var rn = Math.round(Math.random()*999999)
function ss_blog_sidebar_date_callback() {
	var url = "<ssf:url 
    folderId="${ssDefinitionEntry.id}" 
    action="view_folder_listing" >
    </ssf:url>";
	var formObj = document.ss_blog_sidebar_date_form
	url += "\&year=" + formObj.ss_blog_sidebar_date_year.value;
	url += "\&month=" + formObj.ss_blog_sidebar_date_month.value;
	url += "\&day=" + formObj.ss_blog_sidebar_date_date.value;
	url += "\&rn=" + rn++
	self.location.href = url;
}
function ss_showBlogReplies<portlet:namespace/>(id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_replies_' + id)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
		} else {
			targetDiv.innerHTML = "<ssf:nlt tag="Loading"/><br/>";
			targetDiv.style.visibility = 'visible';
			targetDiv.style.display = 'block';
			url = "<ssf:url 
		    	adapter="true" 
		    	portletName="ss_forum" 
		    	action="__ajax_request" 
		    	actionUrl="true" >
				<ssf:param name="binderId" value="${ssBinder.id}" />
				<ssf:param name="operation" value="show_blog_replies" />
		    	</ssf:url>"
			url += "\&entryId=" + id
			url += "\&rn=" + rn++
			ss_fetch_url(url, ss_showBlogRepliesCallback<portlet:namespace/>, id);
		}
	}
}
function ss_showBlogRepliesCallback<portlet:namespace/>(s, id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_replies_' + id)
	if (targetDiv != null) targetDiv.innerHTML = s;
}
//Check the Page Number Before Submission
function goToPage_<portlet:namespace/>(formObj) {
	var strGoToPage = formObj.ssGoToPage.value;
	var pageCount = <c:out value="${ssPageCount}"/>;
	
	if (strGoToPage == "") {
		alert("<ssf:nlt tag="folder.enterPage" />");
		return false;	
	}
	if (strGoToPage == "0") {
		alert("<ssf:nlt tag="folder.enterValidPage" />");
		return false;
	}
	var blnValueCheck = _isInteger(strGoToPage);
	if (!blnValueCheck) {
		alert("<ssf:nlt tag="folder.enterValidPage" />");
		return false;
	}
	if (strGoToPage > pageCount) {
		formObj.ssGoToPage.value = pageCount;
	}
	return true;
}

function submitPage_<portlet:namespace/>(formObj) {
	return (goToPage_<portlet:namespace/>(formObj));
}

function clickGoToPage_<portlet:namespace/>(strFormName) {
	var formObj = document.getElementById(strFormName);
	if (goToPage_<portlet:namespace/>(formObj)) {
		formObj.submit();
	}
}

//Change the number of entries to be displayed in a page
function changePageEntriesCount_<portlet:namespace/>(strFormName, pageCountValue) {
	var formObj = document.getElementById(strFormName);
	formObj.ssEntriesPerPage.value = pageCountValue;
	formObj.submit();
}

function ss_addBlogReply<portlet:namespace/>(obj, id) {
	var showRepliesDiv = document.getElementById('<portlet:namespace/>ss_blog_replies_' + id)
	if (showRepliesDiv != null) {
		if (showRepliesDiv.style.visibility == 'visible') {
			//Hide the list of replies
			ss_showBlogReplies<portlet:namespace/>(id)
		}
	}
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_' + id)
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden'
			targetDiv.style.display = 'none'
			return
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_iframe_' + id)
	iframeDiv.src = obj.href;
}
var ss_replyIframeOffset = 50;
function ss_showBlogReplyIframe<portlet:namespace/>(obj, id) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_' + id)
	var iframeDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_iframe_' + id)
	if (window.frames['<portlet:namespace/>ss_blog_add_reply_iframe_' + id] != null) {
		eval("var iframeHeight = parseInt(window.<portlet:namespace/>ss_blog_add_reply_iframe_" + id + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_replyIframeOffset + "px"
		}
	}
}
function ss_hideBlogReplyIframe<portlet:namespace/>(id, count) {
	var targetDiv = document.getElementById('<portlet:namespace/>ss_blog_add_reply_' + id)
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
	var replyCountObj = document.getElementById('<portlet:namespace/>ss_blog_reply_count_' + id)
	if (replyCountObj != null) replyCountObj.innerHTML = count;
	ss_showBlogReplies<portlet:namespace/>(id);
}
</script>

<div style="margin:0px;">


<div align="right" style="margin:0px 4px 0px 0px;">
    
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">

	<tr>
		<td align="left" width="55%">
		
		<table border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td>
			    <span class="ss_light ss_fineprint">
				[<ssf:nlt tag="folder.Results">
				<ssf:param name="value" value="${ssPageStartIndex}"/>
				<ssf:param name="value" value="${ssPageEndIndex}"/>
				<ssf:param name="value" value="${ssTotalRecords}"/>
				</ssf:nlt>]
			    </span>
				&nbsp;&nbsp;
			</td>

			<form name="ss_recordsPerPage_<portlet:namespace/>" id="ss_recordsPerPage_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="tabId" value="${tabId}"/><c:if test="${!empty cTag}"><portlet:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty day}"><portlet:param 
				name="day" value="${day}"/></c:if><c:if test="${!empty month}"><portlet:param 
				name="month" value="${month}"/></c:if><c:if test="${!empty year}"><portlet:param 
				name="year" value="${year}"/></c:if><portlet:param 
				name="operation" value="change_entries_on_page"/></portlet:actionURL>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
			
			<td>
				<div style="position:relative; top:2; margin:2px; padding:2px; border-top:solid #666666 1px; border-bottom:solid #666666 1px;  border-right:solid #666666 1px;  border-left:solid #666666 1px;">
				<span class="ss_light ss_fineprint">
	
				<ssf:menu title="${ssPageMenuControlTitle}" titleId="ss_selectEntriesTitle" titleClass="ss_compact" menuClass="ss_actions_bar_submenu" menuImage="pics/sym_s_down.gif">
					<ul class="ss_actions_bar_submenu" style="width:250px;">
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '5');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
						</a>
					</li>
					<li>	
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '10');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '25');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '50');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '100');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
						</a>
					</li>
					</ul>
				</ssf:menu>

			    </span>
			    </div>
			</td>

			</form>
			
			<form name="ss_goToPageForm_<portlet:namespace/>" id="ss_goToPageForm_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="tabId" value="${tabId}"/><c:if test="${!empty cTag}"><portlet:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty day}"><portlet:param 
				name="day" value="${day}"/></c:if><c:if test="${!empty month}"><portlet:param 
				name="month" value="${month}"/></c:if><c:if test="${!empty year}"><portlet:param 
				name="year" value="${year}"/></c:if><portlet:param 
				name="operation" value="save_folder_goto_page_info"/></portlet:actionURL>" onSubmit="return(submitPage_<portlet:namespace/>(this))">
			<td>
				&nbsp;&nbsp;
			    <span class="ss_light ss_fineprint"><ssf:nlt tag="folder.GoToPage"/></span>
			    <input name="ssGoToPage" size="1" type="text" class="form-text" />
				<a class="ss_linkButton ss_smallprint" href="javascript: ;" onClick="clickGoToPage_<portlet:namespace/>('ss_goToPageForm_<portlet:namespace/>'); return false;"><ssf:nlt tag="button.go"/></a>
			</td>

			</form>
		
		</tr>
		</table>
		
		</td>
		
		<td align="center" width="25%">

		<table width="100%" border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td width="15%">
				<c:choose>
				  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}">
					<img src="<html:imagesPath/>pics/sym_s_arrow_left.gif"/>
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" portletMode="view">
							<portlet:param name="action" value="${action}"/>
							<portlet:param name="operation" value="save_folder_page_info"/>
							<portlet:param name="binderId" value="${ssFolder.id}"/>
							<portlet:param name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/>
							<portlet:param name="tabId" value="${tabId}"/>
							<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
							<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
							<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
							<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
							<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
							<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
					</portlet:actionURL>"><img src="<html:imagesPath/>pics/sym_s_arrow_left.gif"/>
					</a>
				  </c:otherwise>
				</c:choose>
			</td>
			<td width="70%" align="center">
				<c:forEach var="entryPage" items="${ssPageNumbers}" >
				<jsp:useBean id="entryPage" type="java.util.HashMap" />
					<c:if test="${!empty entryPage.ssPageIsCurrent && entryPage.ssPageIsCurrent == 'true'}">
						<span class="font-small">
							<c:out value="${entryPage.ssPageDisplayValue}"/>
						</span>
					</c:if>
					
					<c:if test="${empty entryPage.ssPageIsCurrent}">
						<a href="<portlet:actionURL windowState="maximized" portletMode="view">
								<portlet:param name="action" value="${action}"/>
								<portlet:param name="operation" value="save_folder_page_info"/>
								<portlet:param name="binderId" value="${ssFolder.id}"/>
								<portlet:param name="ssPageStartIndex" value="${entryPage.ssPageInternalValue}"/>
								<portlet:param name="tabId" value="${tabId}"/>
								<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
								<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
								<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
								<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
								<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
								<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
						</portlet:actionURL>">
						<span class="ss_fineprint ss_light"><c:out value="${entryPage.ssPageDisplayValue}"/></span>
						</a>
					</c:if>
				</c:forEach>
			</td>
			<td width="15%" align="right">
				<c:choose>
				  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
					<img src="<html:imagesPath/>pics/sym_s_arrow_right.gif"/>
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" portletMode="view">
							<portlet:param name="action" value="${action}"/>
							<portlet:param name="operation" value="save_folder_page_info"/>
							<portlet:param name="binderId" value="${ssFolder.id}"/>
							<portlet:param name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/>
							<portlet:param name="tabId" value="${tabId}"/>
							<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
							<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
							<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
							<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
							<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
							<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
					</portlet:actionURL>"><img src="<html:imagesPath/>pics/sym_s_arrow_right.gif"/>
					</a>
				  </c:otherwise>
				</c:choose>
			</td>
		</tr>
		</table>

		</td>

		<td align="right" width="20%">
			&nbsp;
		</td>
	</tr>
</table>

</div>





<div class="ss_folder_border" style="position:relative; top:2; margin:0px; padding:2px 0px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar">

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar" item="true" />
</c:if>

<ssf:toolbar style="ss_actions_bar" item="true" >
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</ssf:toolbar>

</ssf:toolbar>

</div>
</div>
<div class="folder" id="ss_blog_folder_div">
<%@ include file="/WEB-INF/jsp/definition_elements/blog/blog_folder_listing.jsp" %>
</div>
