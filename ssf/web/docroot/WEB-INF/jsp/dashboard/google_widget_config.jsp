<%
// The dashboard "google widget" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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
<div class="ss_form_color" style="border:1px solid #CECECE; height:150px;">
<span class="ss_bold ss_largerprint">The Google Widget component</span>
<br/>
<br/>
<span class="ss_bold">Step 1)</span>
<span>Visit the <a style="text-decoration:underline;"target="_blank" 
href="http://www.google.com/ig/directory?synd=open"
>Google widget</a> page to select the widget
<br/>
<br/>
<span class="ss_bold">Step 2)</span>
<span>Configure the widget and then "Get the code"</span>
<br/>
<br/>
<span class="ss_bold">Step 3)</span>
<span>Paste the widget code below</span>
<br/>
<br/>
<textarea id="data_google_widget" name="data_google_widget" rows="3" cols="100"><c:out 
value="${ssDashboard.dashboard.components[ssComponentId].data.google_widget[0]}"/></textarea>

<br/>
<br/>
<span class="ss_bold">Step 4)</span>
<span>Specify the height (in pixels) of the widget container</span>
<br>
<br>
<span class="ss_bold"><ssf:nlt tag="dashboard.height"/></span><br/>
<input type="text" name="data_height" size="6" 
  value="${ssDashboard.dashboard.components[ssComponentId].data.height[0]}"/>
<br/>

</div>
<br/>
