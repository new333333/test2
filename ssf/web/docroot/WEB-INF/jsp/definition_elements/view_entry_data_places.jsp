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
<c:set var="places_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="places_entry" type="com.sitescape.team.domain.Entry" />
<div class="ss_entryContent">
	<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
	<ul class="ss_nobullet">
	<c:forEach var="selection" items="<%= com.sitescape.team.util.ResolveIds.getBinderTitlesAndIcons(places_entry.getCustomAttribute(property_name)) %>" >
		<li><img border="0" <ssf:alt/>
		          src="<html:imagesPath/>${selection.value.iconName}" />
				<a       
		          <ssf:ifadapter>
			          href="<ssf:url adapter="true" portletName="ss_forum" action="view_permalink" binderId="${selection.key}" entryId="${selection.key}">
							<ssf:param name="entityType" value="folder"/><ssf:param name="newTab" value="1"/></ssf:url>"
						onClick="return ss_gotoPermalink('${selection.key}','${selection.key}', 'folder', '${portletNamespace}', 'yes', true);"
		          </ssf:ifadapter>
		          <ssf:ifnotadapter>
				     href="<ssf:url adapter="false" portletName="ss_forum" folderId="${selection.key}" action="view_folder_listing" actionUrl="false" >
		    			<ssf:param name="binderId" value="${selection.key}"/>
						<ssf:param name="newTab" value="1"/>
						</ssf:url>" 		          
		          </ssf:ifnotadapter>
		          class="ss_parentPointer"><c:out value="${selection.value.title}" escapeXml="false"/></a>
		</li>
	</c:forEach>
	</ul>
</div>
