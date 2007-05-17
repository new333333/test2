<%
// The dashboard "google widget" component
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
value="${ssDashboard.dashboard.components[ssComponentId].data.google_widget}"/></textarea>

<br/>
<br/>
<span class="ss_bold">Step 4)</span>
<span>Specify the height (in pixels) of the widget container</span>
<br>
<br>
<span class="ss_bold"><ssf:nlt tag="dashboard.height"/></span><br/>
<input type="text" name="data_height" size="6" 
  value="${ssDashboard.dashboard.components[ssComponentId].data.height}"/>
<br/>

</div>
<br/>
