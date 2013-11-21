<%
// The dashboard "google widget" component
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
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
<label for="data_google_widget"><span>Paste the widget code below</span></label>
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
<label for="data_height"><span class="ss_bold"><ssf:nlt tag="dashboard.height"/></span><br/></label>
<input type="text" name="data_height" id="data_height" size="6" 
  value="${ssDashboard.dashboard.components[ssComponentId].data.height}"/>
<br/>

</div>
<br/>
