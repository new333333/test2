<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<script type="text/javascript">
function ss_showTags<portlet:namespace/>() {
	var divId = 'ss_tags<portlet:namespace/>';
	ss_moveDivToBody(divId);
	var divObj = document.getElementById(divId);
	divObj.style.display = "block";
	divObj.visibility = "visible";
	var anchorObj = document.getElementById('ss_tags_anchor<portlet:namespace/>');
	ss_setObjectTop(divObj, ss_getDivTop('ss_tags_anchor<portlet:namespace/>'));
	var rightEdge = parseInt(ss_getDivLeft('ss_tags_anchor<portlet:namespace/>'));
	var leftEdge = parseInt(rightEdge - ss_getObjectWidth(divObj));
	if (leftEdge < 0) leftEdge = 0;
	//self.parent.ss_debug("top = "+ss_getDivTop('ss_tags_anchor<portlet:namespace/>') + ", left = " +leftEdge)
	ss_setObjectLeft(divObj, leftEdge)
	ss_showDiv(divId);
}
function ss_hideTags<portlet:namespace/>() {
	var divId = 'ss_tags<portlet:namespace/>';
	ss_hideDiv(divId);
}
function ss_addTag<portlet:namespace/>() {
	ss_modifyTags<portlet:namespace/>('add');
	
}
function ss_deleteTag<portlet:namespace/>(tagId) {
	ss_modifyTags<portlet:namespace/>('delete', tagId);
}
function ss_modifyTags<portlet:namespace/>(operation2, tagId) {
	ss_setupStatusMessageDiv();
	var tagToDelete = "";
	if (operation2 == 'delete') tagToDelete = tagId;
	var url = "<ssf:url 
		adapter="true" 
		portletName="ss_forum" 
		action="__ajax_request" 
		actionUrl="true" >
		<ssf:param name="operation" value="modify_tags" />
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
		</ssf:url>";
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("operation2", operation2)
	ajaxRequest.addKeyValue("namespace", "<portlet:namespace/>")
	ajaxRequest.addKeyValue("tagToDelete", tagToDelete)
	ajaxRequest.addFormElements("ss_modifyTagsForm<portlet:namespace/>");
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postModifyTags<portlet:namespace/>);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postModifyTags<portlet:namespace/>(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_showTags<portlet:namespace/>()
}
</script>
<table cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td valign="top" style="padding-right:2px;">
<a href="#" onClick="ss_showTags<portlet:namespace/>(); return false;"
><span class="ss_fineprint"><ssf:nlt tag="tags.tags"/></span></a></td>
<td valign="top"><a href="#" onClick="ss_showTags<portlet:namespace/>(); return false;"
><img border="0" src="<html:imagesPath/>pics/red_tag.gif"></a></td><td></td>
</tr>
<tr><td colspan="2"></td><td>
  <div id="ss_tags_anchor<portlet:namespace/>">
  </div>
</td></tr>
</tbody>
</table>

<c:set var="ss_tagViewNamespace" value="${renderResponse.namespace}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
