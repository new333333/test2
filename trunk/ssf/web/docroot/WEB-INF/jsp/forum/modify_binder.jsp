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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<div class="ss_portlet"> 

<% // Show the workspace according to its definition %>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  processThisItem="true"
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssBinder}"/>

</div>
