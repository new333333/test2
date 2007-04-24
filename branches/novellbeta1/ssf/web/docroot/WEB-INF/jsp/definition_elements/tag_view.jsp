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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ss_tagDivNumber}">
  <c:set var="ss_tagDivNumber" value="0" scope="request"/>
</c:if>
<c:set var="ss_tagDivNumber" value="${ss_tagDivNumber + 1}" scope="request"/>
<script type="text/javascript">
function ss_showTags<portlet:namespace/>(divNumber, entryId) {
	var divId = 'ss_tags<portlet:namespace/>_' + parseInt(divNumber) + '_pane';
	ss_moveDivToBody(divId);
	var divObj = document.getElementById(divId);
	divObj.style.display = "block";
	divObj.visibility = "visible";
	divObj.style.zIndex = ssMenuZ;
	var anchorObj = document.getElementById('ss_tags_anchor<portlet:namespace/>_'+parseInt(divNumber));
	ss_setObjectTop(divObj, ss_getDivTop('ss_tags_anchor<portlet:namespace/>_'+parseInt(divNumber)) + "px");
	var rightEdge = parseInt(ss_getDivLeft('ss_tags_anchor<portlet:namespace/>_'+parseInt(divNumber)));
	var leftEdge = parseInt(rightEdge - ss_getObjectWidth(divObj));
	if (leftEdge < 0) leftEdge = 0;
	self.parent.ss_debug("top = "+ss_getDivTop('ss_tags_anchor<portlet:namespace/>_'+parseInt(divNumber)) + ", left = " +leftEdge)
	ss_setObjectLeft(divObj, leftEdge + "px")
	ss_showDiv(divId);
	
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}
function ss_hideTags<portlet:namespace/>(divNumber, entryId) {
	var divId = 'ss_tags<portlet:namespace/>_' + parseInt(divNumber) + '_pane';
	ss_hideDiv(divId);
}
function ss_addTag<portlet:namespace/>(divNumber, entryId) {
	ss_modifyTags<portlet:namespace/>('add', '', divNumber, entryId);
	
}
function ss_addTag2<portlet:namespace/>(id, divNumber, entryId) {
	alert(entryId)
	document.forms['ss_modifyTagsForm<portlet:namespace/>_'+divNumber].communityTag.value = id;
	ss_modifyTags<portlet:namespace/>('add', '', divNumber, entryId);
}

function ss_deleteTag<portlet:namespace/>(tagId, divNumber, entryId) {
	ss_modifyTags<portlet:namespace/>('delete', tagId, divNumber, entryId);
}
function ss_modifyTags<portlet:namespace/>(operation2, tagId, divNumber, entryId) {
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
		</ssf:url>";
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("operation2", operation2)
	ajaxRequest.addKeyValue("namespace", "<portlet:namespace/>")
	ajaxRequest.addKeyValue("tagToDelete", tagToDelete)
	ajaxRequest.addKeyValue("tagDivNumber", divNumber)
	ajaxRequest.addKeyValue("entryId", entryId);
	ajaxRequest.addFormElements("ss_modifyTagsForm<portlet:namespace/>_"+divNumber);
	ajaxRequest.setData("divNumber", divNumber);
	ajaxRequest.setData("entryId", entryId);
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
	ss_debug("post: "+obj.getData("divNumber"))
	ss_showTags<portlet:namespace/>(obj.getData("divNumber"), obj.getData("entryId"));
}
</script>
<table cellspacing="0" cellpadding="0">
<tbody>
<tr>
<td valign="top" style="padding-right:2px;">
<a href="#" 
  onClick="ss_showTags<portlet:namespace/>('${ss_tagDivNumber}', '${ssDefinitionEntry.id}'); return false;"
><div class="ss_iconed_label ss_add_tag"><ssf:nlt tag="tags.tags"/></div></a></td>
</tr>
<tr><td colspan="2"></td><td>
  <div id="ss_tags_anchor<portlet:namespace/>_${ss_tagDivNumber}">
  </div>
</td></tr>
</tbody>
</table>

<c:set var="ss_tagViewNamespace" value="${renderResponse.namespace}" scope="request"/>
<c:set var="ssEntryId" value="${ssDefinitionEntry.id}" scope="request"/>
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data_cloud.jsp" />

<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_pane" class="ss_tag_pane">


<ssf:popupPane width="220px" titleTag="tags.manageTags"
     closeScript="ss_hideTags${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ssEntryId}');return false;">

<div style="padding:0px 10px;">
<form class="ss_style ss_form ss_tag_pane_color" 
  method="post" action=""
  id="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" 
  name="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}">
<table class="ss_tag_pane_color">
<tbody>

<tr><th align="left"><ssf:nlt tag="tags.personalTags"/></th></tr>

<tr><td>
<c:set var="ssTags" value="${ssPersonalTags}" scope="request" />
<c:set var="ssTagsType" value="p" scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
</td></tr>

<tr><td>
  <table class="ss_tag_pane_color"><tbody><tr><td>
    <!-- input type="text" class="ss_text" name="personalTag" / -->
<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="personalTag" type="personalTags" width="70px" singleItem="true" />
    </td><td>
      <a class="ss_linkButton" href="#" 
        onClick="ss_addTag${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ssEntryId}');return false;"
      ><ssf:nlt tag="button.add"/></a>
    </td></tr>
  </tbody></table>
</td></tr>

<tr><td></td></tr>

<tr><th align="left"><ssf:nlt tag="tags.communityTags"/></th></tr>

<tr><td>
<c:set var="ssTags" value="${ssCommunityTags}" scope="request" />
<c:set var="ssTagsType" value="c" scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/tag_view_data.jsp" />
</td></tr>

<tr><td>
  <table class="ss_tag_pane_color"><tbody><tr><td>
    <!--input type="text" class="ss_text" name="communityTag"/ -->
<ssf:find formName="ss_modifyTagsForm${ss_tagViewNamespace}_${ss_tagDivNumber}" formElement="communityTag" type="communityTags" width="70px" singleItem="true" />
   
    </td><td style="padding-left:4px;">
    <a class="ss_linkButton" href="#" 
      onClick="ss_addTag${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ssEntryId}');return false;"
    ><ssf:nlt tag="button.add"/></a>
    </td></tr>
  </tbody></table>
</td></tr>

</tbody>
</table>
<input type="submit" value="ok" style="height:10px; width:10px; margin-left: -8000px;"
  onClick="ss_addTag${ss_tagViewNamespace}('${ss_tagDivNumber}', '${ssEntryId}');return false;"/>
</div>
</form>
</div>

</ssf:popupPane>

</div>

