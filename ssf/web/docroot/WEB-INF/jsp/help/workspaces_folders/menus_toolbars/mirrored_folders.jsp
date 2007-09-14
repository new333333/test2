<%
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div class="ss_style">
<div class="ss_help_style">

<p><ssf:nlt tag="ihelp.folderManagement.mirroredFiles.def"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></p>

<p><ssf:nlt tag="ihelp.folderManagement.mirroredFiles.serverFile" /></p>

<p class="ss_help_moreinfo"><ssf:nlt tag="help.globalStrings.moreinfo.leadInSentence"/>
<a href="#skip_nav_titles" title="<ssf:nlt tag="helpTitleAlt.skipNavTitles" />"><img border="0" alt="" src="<html:imagesPath/>pics/1pix.gif" /></a>
</p>

<div class="ss_help_moreinfo">
<p><a target="ss_new" href="<html:rootPath/>help/ref/pdfs/ICEcore Installation and Configuration Guide.pdf">
  <ssf:nlt tag="help.viewBooks.content.listItem.installGuide"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></a> <ssf:nlt tag="help.globalStrings.englishOnly"/><a id="skip_nav_titles"/></p>
</div>

</div>

</div>
