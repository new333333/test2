<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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
<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<div class="ss_portlet">
<br/>

<form class="ss_style ss_form" 
  id="<portlet:namespace/>fm" 
  method="post" >
<input type="hidden" name="_operation" value="${operation}"/>

<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="binder.add.folder.type.legend" 
    text="Folder type"/></legend>
  <br/>
  <span class="ss_bold"><ssf:nlt tag="binder.add.folder.select.type" 
  text="Select the type of folder:"/></span>
  <br/>
      <input type="radio" name="binderDefinitionType" value="5" onClick="if (<portlet:namespace/>_getDefinitions) {<portlet:namespace/>_getDefinitions('5')};" ><ssf:nlt tag="binder.add.folder.select.type.folder" 
		  text="Folder" /><br/>
      <input type="radio" name="binderDefinitionType" value="9" onClick="if (<portlet:namespace/>_getDefinitions) {<portlet:namespace/>_getDefinitions('9')};" ><ssf:nlt tag="binder.add.folder.select.type.file" 
		  text="File" />

</fieldset>
<br/>  
<div id="ss_definitions"></div>

<br/>

	
<input type="submit" class="ss_submit" name="selectDefBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>
<script type="text/javascript">
function <portlet:namespace/>_getDefinitions(type) {
	ss_setupStatusMessageDiv()
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="add_binder"
    	actionUrl="false" >
    	<ssf:param name="binderId" value="${ssBinder.id}"/>
    	<ssf:param name="operation" value="${operation}"/>
    	<ssf:param name="ajax" value="true"/> 
    	</ssf:url>";
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setQueryString("binderDefinitionType=" + type);
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postRequest);
	ajaxRequest.sendRequest();  //Send the request
}

</script>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
