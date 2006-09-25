<div id="ss_tabbar" class="ss_tabs">
<!-- Start of tabs -->

<script type="text/javascript">
var ss_tabs_delete_icon = "<html:imagesPath/>skins/${ss_user_skin}/iconset/delete.gif";
var ss_currentTab = "${ss_tabs.current_tab}";
</script>
<table cellspacing="0" cellpadding="0" style="background:transparent;">
<tbody>
<tr id="ss_tabbar_tr">
<c:set var="tabNum" value="0"/>
<c:forEach var="tab" items="${ss_tabs.tablist}">
  <c:set var="active" value=""/>
  <c:if test="${ss_tabs.current_tab == tab.tabId}">
    <c:set var="active" value="_active"/>
  </c:if>
	<td>
	  <table cellspacing="0" cellpadding="0" style="background:transparent;">
	  <tbody>
	  <tr>
	  <td valign="middle" class="ss_tabs_td_left${active}"><img 
	    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner"/></td>
	  <td valign="middle" class="ss_tabs_td${active}" nowrap="true">
		<a id="ss_tabbar_td${tab.tabId}" 
		  <c:if test="${tab.type == 'binder'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_folder_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'workspace'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_ws_listing">
  				<ssf:param name="binderId" value="${tab.binderId}"/>
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'entry'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				entryId="${tab.entryId}" 
  				action="view_folder_entry">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'user'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				entryId="${tab.entryId}" 
  				action="view_profile_entry">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'profiles'}">
		    href="<ssf:url 
  				folderId="${tab.binderId}" 
  				action="view_profile_listing">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  <c:if test="${tab.type == 'query'}">
		    href="<ssf:url 
  				action="view_search_results">
  				<ssf:param name="tabId" value="${tab.tabId}"/>
  				</ssf:url>" 
		  </c:if>
		  onClick="return ss_showTab('${tab.tabId}', true);">
	<c:if test="${!empty tab.icon}">
		   <img src="<html:imagesPath/>${tab.icon}"/>
	</c:if>
		   <span>${tab.title}</span></a>
	<c:if test="${tabNum > 0}">
		<a href="#" onClick="ss_deleteTab(this, '${tab.tabId}');return false;">
		  <img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/delete.gif"/>
		</a>
	</c:if>
	  </td>
	  <td valign="middle" class="ss_tabs_td_right${active}"><img 
	    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner"/></td>
	  </tr>
	  </tbody>
	  </table>
	</td>

  <c:set var="tabNum" value="${tabNum + 1}"/>
</c:forEach>

</tr>
</tbody>
</table>
<script type="text/javascript">
var ss_nextTabNumber = "${ss_tabs.next_tab_id}";
</script>

<!-- End of tabs -->
</div>

