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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
function ss_showGroupList<portlet:namespace/>(obj, id) {
	var iframeDiv = document.getElementById('ss_group_iframe<portlet:namespace/>')
	iframeDiv.src = obj.href;
	return false;
}
var ss_groupIframeWidthOffset = -40;
var ss_groupIframeHeightOffset = 300;
var ss_groupIframeYOffset = 0;
function ss_size_group_iframe() {
	var targetDiv = document.getElementById('ss_groupsDiv<portlet:namespace/>')
	ss_moveObjectToBody(targetDiv);
	var iframeDiv = document.getElementById('ss_group_iframe<portlet:namespace/>')
	var anchorDiv = document.getElementById('ss_modifyGroups');
	if (iframeDiv.src) {
		targetDiv.style.display = "block";
		targetDiv.style.visibility = "visible";
		if (window.frames['ss_group_iframe<portlet:namespace/>'] != null) {
			eval("var iframeHeight = parseInt(window.ss_group_iframe<portlet:namespace/>.document.body.scrollHeight);")
			if (iframeHeight > 0) {
				iframeDiv.style.height = iframeHeight + ss_groupIframeHeightOffset + "px"
			}
			iframeDiv.style.width = parseInt(2 * ss_getDivWidth('ss_manageGroups') / 3) + ss_groupIframeWidthOffset + "px"
		}
		ss_setObjectTop(targetDiv, parseInt(dojo.html.getAbsolutePosition(anchorDiv, true).y + ss_groupIframeYOffset)+"px");
		var objLeft = parseInt(dojo.html.getAbsolutePosition(anchorDiv, true).x + ss_getDivWidth('ss_manageGroups') / 3);
		ss_setObjectLeft(targetDiv, objLeft+"px")
	}
}
</script>

<div class="ss_style ss_portlet">
<div style="padding:10px;" id="ss_manageGroups">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.groups" /></span>
<br>
<br>
<ssf:expandableArea title="<%= NLT.get("administration.add.group") %>">
<form class="ss_style ss_form" method="post" 
	action="<portlet:actionURL><portlet:param 
	name="binderId" value="${ssBinder.id}"/><portlet:param 
	name="action" value="manage_groups"/></portlet:actionURL>">
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupName"/></span><br/>
	<input type="text" class="ss_text" size="70" name="name"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupTitle"/></span><br/>
	<input type="text" class="ss_text" size="70" name="title"><br/><br/>
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.groupDescription"/></span><br/>
	<textarea name="description" wrap="virtual" rows="4" cols="80"></textarea><br/><br/>
		
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>
<br/>
<br/>

<span class="ss_bold"><ssf:nlt tag="administration.selectGroupToManage"/></span>
<br/>
<div class="ss_indent_medium" id="ss_modifyGroups">
  <c:forEach var="group" items="${ss_groupList}">
  	<a href="<ssf:url adapter="true" portletName="ss_forum" 
		    action="__ajax_request"
		    actionUrl="false"
		    binderId="${ssBinder.id}"
		    entryId="${group._docId}">
    	    <ssf:param name="operation" value="modify_group"/>
    	    <ssf:param name="namespace" value="${renderResponse.namespace}"/>
			</ssf:url>" 
  	  onClick="ss_showGroupList<portlet:namespace/>(this, '${group._docId}');return false;"><span>${group.title}</span> <span class="ss_smallprint">(${group._groupName})</span></a><br/>
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
  onLoad="if (parent.ss_size_group_iframe) parent.ss_size_group_iframe();" 
  width="100%">xxx</iframe>
</div>

</div>
</div>

