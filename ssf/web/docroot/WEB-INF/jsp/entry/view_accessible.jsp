<% //view a folder forum in accessible mode %>

<%
//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = "folder";
Element folderViewTypeEle = (Element)ssConfigElement.selectSingleNode("properties/property[@name='type']");
if (folderViewTypeEle != null) folderViewStyle = folderViewTypeEle.attributeValue("value", "folder");
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />

<div id="ss_showfolder" class="ss_style ss_portlet ss_content_outer" style="display:block; margin:2;">

<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Tabs %>
<%@ include file="/WEB-INF/jsp/definition_elements/tabbar.jsp" %>

<!-- Rounded box surrounding entire page (continuation of tabs metaphor) -->
	<div class="ss_decor-border3">
		<div class="ss_decor-border4">
			<div class="ss_rounden-content">

<% // Folder toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<div class="ss_content_inner">
<ssf:toolbar toolbar="${ssFolderToolbar}" style="ss_actions_bar"/>
</div>
</c:if>

<div class="ss_content_inner">
<% // Navigation links %>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<br/>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>

<script type="text/javascript">
function ss_showForumEntryInIframe(url) {
    self.location.href = url;
    return false;
}
</script>

<% // Footer toolbar %>
<c:if test="${!empty ssFooterToolbar}">
<c:set var="ss_toolbar" value="${ssFooterToolbar}" scope="request" />
<c:set var="ss_toolbar_style" value="ss_footer_toolbar" scope="request" />
<br/>
<ssf:toolbar toolbar="${ssFooterToolbar}" style="ss_bottomlinks" />
</c:if>

			</div>
		</div>
	</div>
	<div class="ss_decor-round-corners-bottom1"><div><div></div></div></div>

</div>

