<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
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
<script type="text/javascript">
function ss_showGroupList<portlet:namespace/>(obj, id) {
	var iframeDiv = document.getElementById('ss_group_iframe<portlet:namespace/>')
	iframeDiv.src = obj.href;
	return false;
}
var ss_groupIframeOffset = 20;
var ss_groupIframeYOffset = -20;
function ss_size_group_iframe<portlet:namespace/>(obj) {
	var targetDiv = document.getElementById('ss_groupsDiv<portlet:namespace/>')
	ss_moveObjectToBody(targetDiv);
	targetDiv.style.display = "block";
	targetDiv.style.visibility = "visible";
	var iframeDiv = document.getElementById('ss_group_iframe<portlet:namespace/>')
	if (iframeDiv.src) {
		if (window.frames['ss_group_iframe<portlet:namespace/>'] != null) {
			eval("var iframeHeight = parseInt(window.ss_group_iframe<portlet:namespace/>.document.body.scrollHeight);")
			if (iframeHeight > 0) {
				iframeDiv.style.height = iframeHeight + ss_groupIframeOffset + "px"
			}
		}
		ss_setObjectTop(targetDiv, parseInt(ss_getClickPositionY() + ss_groupIframeYOffset)+"px")
		var objLeft = parseInt(ss_getWindowWidth() / 3);
		ss_setObjectLeft(targetDiv, objLeft+"px")
	}
}
</script>

<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.groups" /></span>
<br>
<br>
<div>
<form class="ss_style ss_form" method="post" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="manage_groups"/></portlet:actionURL>">
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.group"/></span>
	<input type="text" class="ss_text" size="70" name="groupName"><br>
		
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</div>
<br/>
<br/>

<span class="ss_bold"><ssf:nlt tag="administration.selectGroupToManage"/></span>
<br/>
<div class="ss_indent_medium">
  <c:forEach var="group" items="${ss_groupList}">
  	<a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_request"
		    binderId="${ssBinder.id}"
		    entryId="${group._docId}">
    	    <ssf:param name="operation" value="manage_group"/>
			</ssf:url>" 
  	  onClick="ss_showGroupList<portlet:namespace/>(this, '${group._docId}');return false;"><span>${group.title}</span> <span class="ss_smallprint">(${group._name})</span></a><br/>
  </c:forEach>
</div>
<br/>

<div class="ss_formBreak"/>

<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<portlet:actionURL>
		 <portlet:param name="action" value="manage_groups"/>
		 <portlet:param name="binderId" value="${ssBinder.id}"/>
		 </portlet:actionURL>" name="<portlet:namespace />fm">
<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</form>
</div>

<div class="ss_popupMenu" style="visibility:hidden; display:none;" id="ss_groupsDiv<portlet:namespace/>">
<div align="right"><a href="#" 
  onClick="ss_hideDivNone('ss_groupsDiv<portlet:namespace/>');return false;"><img 
  border="0" alt="<ssf:nlt tag="button.close"/>" 
  src="<html:imagesPath/>pics/sym_s_delete.gif"></a></div>
<iframe frameBorder="0"
  id="ss_group_iframe<portlet:namespace/>"
  name="ss_group_iframe<portlet:namespace/>"
  onLoad="if (parent.ss_size_group_iframe<portlet:namespace/>) parent.ss_size_group_iframe<portlet:namespace/>(this);" 
  width="100%">xxx</iframe>
</div>

</div>

