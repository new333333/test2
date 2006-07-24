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
  name="<portlet:namespace/>fm" 
  method="post" >
<input type="hidden" name="_operation" value="${operation}"/>
<input type="hidden" name="binderDefinitionType" value="${ssBinderDefinitionType}"/>
<%@ include file="/WEB-INF/jsp/forum/add_binder_definition_common.jsp" %>
<br/>
	
<input type="submit" class="ss_submit" name="selectDefBtn" value="<ssf:nlt tag="button.ok"/>">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">

</form>
</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
