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
<% // htmlarea editor bottom
%><%@ include file="/WEB-INF/jsp/common/include.jsp" 
%></textarea>
</div><%--
--%><c:if test="${!empty wikiLinkBinderId}"><%--
   --%><div class="ss_editorHints"><%--
   --%><ssf:nlt tag="editor.wikilink.tip"><%--
       --%><ssf:param name="value" useBody="true"><%--
           --%><img align="absmiddle" src="<html:brandedImagesPath/>icons/wikilink.gif" alt="Wikilink" title="" /><%--
       --%></ssf:param><%--   
   --%></ssf:nlt>  <%--
   --%><ssf:inlineHelp jsp="workspaces_folders/entries/link_to_entry_markup"/><%--
   --%></div><%--
--%></c:if>