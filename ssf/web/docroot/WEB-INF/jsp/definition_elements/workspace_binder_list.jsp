<% // Workspace binder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<script type="text/javascript">
function wsTree_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="ssActionPlaceHolder"/>
				<portlet:param name="binderId" value="ssBinderIdPlaceHolder"/>
				</portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

</script>

<ssf:tree treeName="wsTree" treeDocument="${ssWsDomTree}" 
  topId="${ssDefinitionEntry.id}" highlightNode="${ssDefinitionEntry.id}" rootOpen="true" />
<div id="ss_tree_div_status_messagewsTree" style="visibility:hidden; display:none;"></div>
