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

<div class="ss_portlet">
<span class="ss_titlebold"><c:out value="${ssFolder.title}"/>&nbsp;-&nbsp;<ssf:nlt tag="folder.add.title"/></span><br/><br/>

<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="add_binder"/>
			<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:actionURL>">

<span class="ss_labelLeft"><ssf:nlt tag="folder.label.name"/></span>
<br/>
<input type="text" class="ss_text" name="name">
<br/>

<span class="ss_labelLeft"><ssf:nlt tag="folder.label.title"/></span>
<br/><input type="text" class="ss_text" name="title">
<br/>

<span class="ss_labelLeft"><ssf:nlt tag="folder.label.description"/></span>
<br/>
    <ssf:htmleditor id="description" 
      formName="<%= renderResponse.getNamespace() + "fm" %>" height="200" color="${ss_form_element_header_color}"
      initText="" />
<br/>
	
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">

</form>
</div>
