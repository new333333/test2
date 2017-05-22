<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper"  %>
<%@ page import="org.kablink.teaming.web.util.BinderHelper" %>
<%@ page import="org.kablink.teaming.web.WebKeys" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ssUserProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />

<% // The main forum view - for viewing folder listings and for viewing entries %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />

<%
String op = (String) renderRequest.getAttribute(WebKeys.ACTION);
String displayStyle = ssUser.getCurrentDisplayStyle();

String ssLoadEntryUrl = (String) renderRequest.getAttribute("ssLoadEntryUrl");
if (ssLoadEntryUrl == null) ssLoadEntryUrl = "";
String ssLoadEntryId = (String) renderRequest.getAttribute("ssLoadEntryId");
if (ssLoadEntryId == null) ssLoadEntryId = "";
//this jsp should is not included when reloadurl is set.  This is left here
//until we get after a reply to work - it may be needed then
String ssReloadUrl = (String) renderRequest.getAttribute("ssReloadUrl");
if (ssReloadUrl == null) ssReloadUrl = "";
boolean reloadCaller = false;
if (!ssReloadUrl.equals("")) reloadCaller = true;

boolean isViewEntry = false;
if (op != null && !op.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING) && !op.equals(WebKeys.ACTION_VIEW_PROFILE_LISTING)) {
	isViewEntry = true;
}
	
int entryWindowWidth = 0;
try
{
	if (ssUserProperties.containsKey("folderEntryWidth")) {
		entryWindowWidth = Integer.parseInt((String) ssUserProperties.get("folderEntryWidth"));
	}
}
catch( Exception ex)
{
}

int entryWindowTop = 0;
try
{
	if (ssUserProperties.containsKey("folderEntryTop")) {
		entryWindowTop = Integer.parseInt((String) ssUserProperties.get("folderEntryTop"));
	}
}
catch( Exception ex)
{
}

int entryWindowLeft = 0;
try
{
	if (ssUserProperties.containsKey("folderEntryLeft")) {
		entryWindowLeft = Integer.parseInt((String) ssUserProperties.get("folderEntryLeft"));
	}
}
catch( Exception ex)
{
}

int entryWindowHeight = 0;
try
{
	if (ssUserProperties.containsKey("folderEntryHeight")) {
		entryWindowHeight = Integer.parseInt((String) ssUserProperties.get("folderEntryHeight"));
	}
}
catch( Exception ex)
{
}

String autoScroll = "true";
renderRequest.setAttribute("ss_entryWindowWidth", new Integer(entryWindowWidth));
renderRequest.setAttribute("ss_entryWindowTop", new Integer(entryWindowTop));
renderRequest.setAttribute("ss_entryWindowLeft", new Integer(entryWindowLeft));
renderRequest.setAttribute("ss_entryWindowHeight", new Integer(entryWindowHeight));
%>
<c:if test="<%= !isViewEntry %>">

<script type="text/javascript">
var ss_viewEntryPopupWidth = "<c:out value="${ss_entryWindowWidth}"/>px";
var ss_viewEntryPopupHeight = "<c:out value="${ss_entryWindowHeight}"/>px";

</script>

<script type="text/javascript">

//Define the url of this page in case the entry needs to reload this page
var ss_reloadUrl = "${ss_reloadUrl}";
var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
var ssLoadEntryUrl = "<%= ssLoadEntryUrl %>";
var autoScroll = "<%= autoScroll %>";
<%
	if (!ssLoadEntryUrl.equals("")) {
%>
function ss_showEntryOnLoad() {
	ss_loadEntryUrl("<%= ssLoadEntryUrl %>", "<%= ssLoadEntryId %>");
}
ss_createOnLoadObj('ss_showEntryOnLoad', ss_showEntryOnLoad);
<%
	}
%>


var ss_highlightBgColor = "${ss_folder_line_highlight_color}"
var ss_highlightedLine = null;
var ss_highlightedColLine = null;
var ss_savedHighlightedLineBgColor = null;
var ss_highlightClassName = "ss_highlightEntry";
var ss_savedHighlightClassName = null;
var ss_highlightColClassName = "ss_highlightEntry";
var ss_savedHighlightColClassName = null;

//Called when one of the "Add entry" toolbar menu options is selected
function ss_addEntry(obj) {
	ss_showForumEntry(obj.href);
	return false;
}

var ss_currentEntryId = "";



function ss_loadEntryUrl(url,id) {
	if (ss_getUserDisplayStyle() == "accessible") {
		self.location.href = url;
		return false;
	}
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	if (window.ss_highlightLineById) {
		ss_highlightLineById(folderLine);
	}
	
	ss_showForumEntry(url);
	return false;
}
</script>
</c:if>

<c:if test="<%= reloadCaller %>">
<script type="text/javascript">
	//Open the current url in the opener window
	ss_reloadOpener('<c:out value="${ssReloadUrl}" escapeXml="false"/>')
</script>
</c:if>


<% // View the entry  %>

<c:if test="<%= !reloadCaller %>">
  <c:if test="<%= isViewEntry %>">
	<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
	<div id="ss_entryTop_${renderResponse.namespace}"></div>
	<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
	<script type="text/javascript">
	
	try {
		if (self.parent && self.parent.ss_highlightLineById) {
			self.parent.ss_highlightLineById("folderLine_<c:out value="${ssEntry.id}"/>");
		}
	} catch(e) {}
	
	//Define the url of this page in case the entry needs to reload this page
	var ss_reloadUrl = "${ss_reloadUrl}";
	var ss_reloadUrl${ssBinder.id} = ss_reloadUrl;
	
	ss_createOnLayoutChangeObj("ss_setCurrentIframeHeight", ss_setCurrentIframeHeight);
	</script>
	<c:set var="ss_viewEntryNavbar" value="false"/>
	<ssf:ifnotadapter><c:set var="ss_viewEntryNavbar" value="true"/></ssf:ifnotadapter>
	<c:if test="${ss_entryViewStyle == 'full'}"><c:set var="ss_viewEntryNavbar" value="true"/></c:if>
	<ssf:ifaccessible><c:set var="ss_viewEntryNavbar" value="true"/></ssf:ifaccessible>
	<c:set var="ss_entryViewStyle2" value="${ss_folderViewStyle}" scope="request"/>
	
	<c:if test="${ss_viewEntryNavbar}">
		<c:set var="ss_sidebarVisibility" value="${ssUserProperties.sidebarVisibility}" scope="request"/>
		<c:if test="${empty ss_sidebarVisibility}"><c:set var="ss_sidebarVisibility" value="block" scope="request"/></c:if>
		<c:if test="${ss_sidebarVisibility == 'none'}">
		  <c:set var="ss_sidebarVisibilityShow" value="block"/>
		  <c:set var="ss_sidebarVisibilityHide" value="none"/>
		  <c:set var="ss_sidebarTdStyle" value=""/>
		</c:if>
		<c:if test="${ss_sidebarVisibility != 'none'}">
		  <c:set var="ss_sidebarVisibilityShow" value="none"/>
		  <c:set var="ss_sidebarVisibilityHide" value="block"/>
		  <c:set var="ss_sidebarTdStyle" value="ss_view_sidebar"/>
		</c:if>
<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
  linkOnly="true"/>
		<div id="ss_portlet_content" class="ss_style ss_portlet ss_content_outer">
		<jsp:include page="/WEB-INF/jsp/forum/view_workarea_navbar.jsp" />
		
<ssf:skipLink tag='<%= NLT.get("skip.header.toContent") %>' id="headerToContent_${renderResponse.namespace}"
  anchorOnly="true"/>
		<div class="ss_style_color">
	</c:if>
	<c:if test="${!ss_viewEntryNavbar}">
		<div class="ss_entryPopupWrapper">
	</c:if>
		
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${ssConfigElement}" 
	  configJspStyle="${ssConfigJspStyle}"
	  processThisItem="true" 
	  entry="${ssEntry}" />
	
	<c:if test="${ss_viewEntryNavbar}">
		</div>
	</c:if>
	<c:if test="${!ss_viewEntryNavbar}">
		</div>
	</c:if>
	
	<% // Footer toolbar %>
	<jsp:include page="/WEB-INF/jsp/definition_elements/footer_toolbar.jsp" />
		
	<c:if test="${ss_viewEntryNavbar}">
			</td>
			</tr>
			</tbody>
			</table>
		
	</div>
	</c:if>
	
  </c:if>
</c:if>
