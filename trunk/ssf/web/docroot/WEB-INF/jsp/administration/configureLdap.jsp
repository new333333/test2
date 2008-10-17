<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>

<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<span class="ss_titlebold"><ssf:nlt tag="ldap.title"/></span><br/><br/>
<c:if test="${!empty ssException}">
<span class="ss_largerprint"><ssf:nlt tag="administration.errors"/> (<c:out value="${ssException}"/>)</span></br>
</c:if>

<form class="ss_style ss_form" name="${renderResponse.namespace}fm" method="post" 
  action="<ssf:url action="configure_ldap" actionUrl="true"/>">
<div class="ss_buttonBarRight">
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
  <div>
    <div>
		<input type="checkbox" id="allowAnonymous" name="allowAnonymous" <c:if test="${ssAuthenticationConfig.allowAnonymousAccess}">checked</c:if>/>
			<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowAnonymous"/></span><br/>
		<input type="checkbox" id="allowLocalLogin" name="allowLocalLogin" <c:if test="${ssAuthenticationConfig.allowLocalLogin}">checked</c:if>/>
			<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowLocalLogin"/></span><br/>
		<input type="checkbox" id="allowSelfRegistration" name="allowSelfRegistration" <c:if test="${ssAuthenticationConfig.allowSelfRegistration}">checked</c:if>/>
			<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.config.allowSelfRegistration"/></span><br/>
    </div>
	<div id="funkyDiv" style="width:500px">
		<ul>
			<li><a href='#wah'>Wah</a></li>
		</ul>
		<div id='wah'></div>
    </div>
    <button id="ldapAddConnection" class="ss_submit"><ssf:nlt tag="ldap.connection.add" /></button>
  </div>
<div class="ss_divider"></div>
<br/>

<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssLdapConfig.enabled}">checked</c:if>/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.enable"/></span><br/>
</td></tr>
<tr><td>
<input type="checkbox" id="runnow" name="runnow" <c:if test="${runnow}"> checked="checked" </c:if>/>
<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.now"/></span><br/>
</td></tr></table>

<br/>
<ssf:expandableArea title='<%= NLT.get("ldap.schedule") %>'>
<c:set var="schedule" value="${ssLdapConfig.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
<div class="ss_divider"></div>
</ssf:expandableArea>
<br/>

<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.users" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
		<tr><td><input type="checkbox" name="userSync" <c:if test="${ssLdapConfig.userSync}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.sync"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userRegister" <c:if test="${ssLdapConfig.userRegister}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.register"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userDelete" <c:if test="${ssLdapConfig.userDelete}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.delete"/></span></input>
	   	</td></tr>
	   	<tr><td><input type="checkbox" name="userWorkspaceDelete" <c:if test="${ssLdapConfig.userWorkspaceDelete}">checked</c:if>>
	   	<span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.user.workspace.delete"/></span></input>
	   	</td></tr>
	 	</td></tr>
</table>
</fieldset>

<br/>
<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.groups" /></legend>
<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3">
	   <tr>
	   <td><input type="checkbox" name="groupRegister" <c:if test="${ssLdapConfig.groupRegister}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.register"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="membershipSync" <c:if test="${ssLdapConfig.membershipSync}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.membership.sync"/></span></input></td>
	   </tr><tr>
	   <td><input type="checkbox" name="groupDelete" <c:if test="${ssLdapConfig.groupDelete}">checked</c:if>>
	   <span class="ss_labelRight ss_normal"><ssf:nlt tag="ldap.schedule.group.delete"/></span></input></td>
	   </tr>
</table>
</fieldset>

<br/>
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
<input type="hidden" name="ldapConfigDoc" id="ldapConfigDoc" value=""/>
</form>
</div>
<script type="text/javascript" src="<html:rootPath/>js/jquery/jquery.js"></script>
<script type="text/javascript">
     jQuery.noConflict();
</script>
<script type="text/javascript" src="<html:rootPath/>js/jquery/jquery-ui-personalized.js"></script>

<style type="text/css">
.ui-tabs-hide { display: none; }
.invalid .errorMessage { display: inline; }
.errorMessage { display: none; color: red; }
.invalid label { color: red; }
@import "flora.css";

/* Caution! Ensure accessibility in print and other media types... */
@media projection, screen { /* Use class for showing/hiding tab content, so that visibility can be better controlled in different media types... */
    .ui-tabs-hide {
        display: none;
    }
}

/* Hide useless elements in print layouts... */
@media print {
    .ui-tabs-nav {
        display: none;
    }
}

/* Skin */
.ui-tabs-nav, .ui-tabs-panel {
}
.ui-tabs-nav {
    list-style: none;
    margin: 0;
    padding: 0 0 0 3px;
}
.ui-tabs-nav:after { /* clearing without presentational markup, IE gets extra treatment */
    display: block;
    clear: both;
    content: " ";
}
.ui-tabs-nav li {
    float: left;
    margin: 0 0 0 2px;
    font-weight: bold;
}
.ui-tabs-nav a, .ui-tabs-nav a span {
    float: left; /* fixes dir=ltr problem and other quirks IE */
    padding: 0 12px;
}
.ui-tabs-nav a {
    margin: 5px 0 0; /* position: relative makes opacity fail for disabled tab in IE */
    padding-left: 0;
    background-position: 100% 0;
    text-decoration: none;
    white-space: nowrap; /* @ IE 6 */
    outline: 0; /* @ Firefox, prevent dotted border after click */    
	font-size: 12px;
}
.ui-tabs-nav a:link, .ui-tabs-nav a:visited {
}
.ui-tabs-nav .ui-tabs-selected a {
    position: relative;
    z-index: 2;
	font-size: 14px;
}
.ui-tabs-nav a span {
    padding-top: 1px;
    padding-right: 0;
    height: 20px;
    line-height: 20px;
	font-size: 12px;
	color: #666;
}
.ui-tabs-nav .ui-tabs-selected a span {
    padding-top: 0;
	color: #000;
}

.ui-tabs-nav .ui-tabs-selected a:link, .ui-tabs-nav .ui-tabs-selected a:visited { /* @ Opera, use pseudo classes otherwise it confuses cursor... */
    cursor: text;
}
.ui-tabs-nav a:hover, .ui-tabs-nav a:focus, .ui-tabs-nav a:active,
.ui-tabs-nav .ui-tabs-unselect a:hover, .ui-tabs-nav .ui-tabs-unselect a:focus, .ui-tabs-nav .ui-tabs-unselect a:active { /* @ Opera, we need to be explicit again here now... */
    cursor: pointer;
}
.ui-tabs-panel {
    border: 1px solid #519e2d;
    padding: 10px;
    background: #fff; /* declare background color for container to avoid distorted fonts in IE while fading */
}


/* Additional IE specific bug fixes... */
* html .ui-tabs-nav { /* auto clear @ IE 6 & IE 7 Quirks Mode */
    display: inline-block;
}
*:first-child+html .ui-tabs-nav  { /* auto clear @ IE 7 Standards Mode - do not group selectors, otherwise IE 6 will ignore complete rule (because of the unknown + combinator)... */
    display: inline-block;
}

</style>
  
<script type="text/javascript">

ssPage = {
	nextId : 1,
	currentTab : 0,
	defaultUserFilter: "${ssDefaultUserFilter}",
	defaultGroupFilter: "${ssDefaultGroupFilter}",
	
	createBindings : function($container)
	{
		jQuery(".ldapUrl", $container).change(function() {
			jQuery(this).parent().parent().find(".ldapTitle span").text(jQuery(this).val());
			var id = jQuery(this).parent().parent().attr("id");
			jQuery("#funkyDiv > ul li > a[href='#" + id + "']").text(jQuery(this).val());
		});
		jQuery(".ldapDelete", $container).click(function() {
			var title = jQuery("#funkyDiv > ul li").eq(ssPage.currentTab).find("span").text();
			if(ss_confirm("Really delete configuration for", title)) {
				var id = jQuery(this).parent().parent().attr("id");
				jQuery("#funkyDiv > ul").tabs("remove", ssPage.currentTab);
				jQuery(this).parent().parent().remove();
			}
			return false;
		});	
		jQuery(".ldapUserSearches button.addSearch", $container).click(function() {
			ssPage.createSearchEntry(jQuery(this).prev(), "", ssPage.defaultUserFilter, "true");
			return false;
		});
		jQuery(".ldapGroupSearches button.addSearch", $container).click(function() {
			ssPage.createSearchEntry(jQuery(this).prev(), "", ssPage.defaultGroupFilter, "false");
			return false;
		});
	},

	createSearchEntry : function($listDiv, baseDn, filter, ss)
	{
		var $newSearch = jQuery('#ldapSearchTemplate').children().clone();
		$newSearch
			.find('.ldapBaseDn').val(baseDn).end()
			.find('.ldapFilter').val(filter).end()
			.find('.ldapSearchSubtree').val([ss]).end();
		jQuery("button.deleteSearch", $newSearch).click(function() {
			if(ss_confirm("Really delete this search specification")) {
				jQuery(this).parent().remove();
			}
			return false;
		});	

		$listDiv.append($newSearch);
	},

	createConnection : function(url, userIdAttribute, mappings, userSearches, groupSearches, principal, credentials)
	{
		var label = "New LDAP connection";
		if(url != "") { label = url; }
		var id = "ldapConn" + ssPage.nextId++;
		var $pane = jQuery('#ldapTemplate').children().clone().hide().attr("id", id);
		$pane.find(".ldapTitle span").text(label).end()
			 .find(".ldapUrl").val(url).end()
			 .find(".ldapUserIdAttribute").val(userIdAttribute).end()
			 .find(".ldapMappings").val(mappings).end()
			 .find(".ldapPrincipal").val(principal).end()
			 .find(".ldapCredentials").val(credentials).end();
		jQuery.each(userSearches, function() {
			ssPage.createSearchEntry($pane.find(".ldapUserSearches .ldapSearchList"), this.baseDn, this.filter, this.searchSubtree);
		});
		jQuery.each(groupSearches, function() {
			ssPage.createSearchEntry($pane.find(".ldapGroupSearches .ldapSearchList"), this.baseDn, this.filter, this.searchSubtree);
		});
		if(userSearches.length == 0 && groupSearches.length == 0) {
			ssPage.createSearchEntry($pane.find(".ldapUserSearches .ldapSearchList"), "", ssPage.defaultUserFilter, "true");
		}
		jQuery('#funkyDiv').append($pane);

		var index = jQuery("#funkyDiv > ul").tabs("length");
		jQuery("#funkyDiv > ul").tabs("add", '#'+ id, label);
		jQuery("#funkyDiv > ul").tabs("select", index);

		ssPage.createBindings($pane);
		$pane.show();
		return $pane;
	},
	
	addConnection : function() {
		var $pane = ssPage.createConnection("", "uid", ssPage.defaultUserMappings, [], [], "", "");
		return false;
	},
	
	balanced : function(input)
	{
		var length = input.length;
		var count = 0;
		var c = 0;
		
		for (var i = 0; i < length; ++i)
		{
		   c = input.charAt(i);
		   if (c == '(') {
			  ++count;
		   } else if (c == ')') {
			  --count;
			  if (count < 0) {
				 break;
			  }
		   }
		}
		
		return count == 0;
	},

	validQuery : function(query) {
		return (query=="")||ssPage.balanced(query);
	},
	
	validDn : function(dn) {
		return true;
	}

};

jQuery(document).ready(function() {
	jQuery('#ldapAddConnection').click(ssPage.addConnection);
	
	jQuery("form").submit(function() {
		var ldapDoc="<ldapConfigs>";
		var valid = true;
		var updateError = function($field, fieldValid)
		{
			if(fieldValid) {
				$field.parent().removeClass('invalid');
			} else {
				$field.parent().addClass('invalid');
			}
			return fieldValid;
		};
		var makeSearch = function()
		{
			var $this = jQuery(this);
			var $baseDn = jQuery('.ldapBaseDn', $this);
			var baseDn = $baseDn.val();
			var $filter = jQuery('.ldapFilter', $this);
			var filter = $filter.val();
			if((baseDn && baseDn != "") || (filter && filter != "")) {
				valid = updateError($baseDn, ssPage.validDn(baseDn)) && valid;
				valid = updateError($filter, ssPage.validQuery(filter)) && valid;
				ldapDoc += "<search>" +
					"<baseDn>" + baseDn + "</baseDn>" +
					"<filter>" +  filter + "</filter>";
				ldapDoc += "<searchSubtree>";
				if(jQuery('.ldapSearchSubtree:checked', $this).length > 0) {
					ldapDoc += "true";
				} else {
					ldapDoc += "false";
				}
				ldapDoc += "</searchSubtree>";

				ldapDoc += "</search>";
			}
		};
		jQuery('#funkyDiv .ldapConfig').each(function() {
			var $this = jQuery(this);
			ldapDoc += "<ldapConfig>";
			var $id = jQuery(".ldapId", $this);
			if($id.length > 0) {
				ldapDoc += "<id>" + $id.text() + "</id>";
			}
			ldapDoc += "<url>"+$this.find('.ldapUrl').val()+"</url>" +
						"<userIdAttribute>"+$this.find('.ldapUserIdAttribute').val()+"</userIdAttribute>" +
						"<mappings>"+$this.find('.ldapMappings').val()+"</mappings>";
			ldapDoc += "<principal>"+$this.find('.ldapPrincipal').val()+"</principal><credentials>" +
						$this.find('.ldapCredentials').val()+"</credentials>";
			ldapDoc += "<userSearches>";
			jQuery('.ldapUserSearches .ldapSearch', $this).each(makeSearch);
			ldapDoc += "</userSearches>";
			ldapDoc += "<groupSearches>";
			jQuery('.ldapGroupSearches .ldapSearch', $this).each(makeSearch);
			ldapDoc += "</groupSearches>";
			ldapDoc += "</ldapConfig>";
		});
		ldapDoc += "</ldapConfigs>";
		jQuery('#ldapConfigDoc').val(ldapDoc);
		
		return valid;
	});
});
</script>
<script type="text/javascript">
	jQuery(document).ready(function() {
		<c:set var="defmappings" value="${ssUserAttributes}"/>
		<jsp:useBean id="defmappings" type="java.util.Map"/>
		<%
		StringBuffer mapText = new StringBuffer();
		for (java.util.Iterator iter=defmappings.entrySet().iterator(); iter.hasNext();) {
			java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
			mapText.append(me.getValue() + "=" + me.getKey() + "\\n");
		}
		%>
		ssPage.defaultUserMappings = "<%=mapText.toString()%>";
	
		jQuery("#funkyDiv > ul").tabs();
	<c:forEach var="config" items="${ssLdapConnectionConfigs}">
		<c:set var="mappings" value="${config.mappings}"/>
		<jsp:useBean id="mappings" type="java.util.Map"/>
		<%
		mapText = new StringBuffer();
		for (java.util.Iterator iter=mappings.entrySet().iterator(); iter.hasNext();) {
			java.util.Map.Entry me = (java.util.Map.Entry)iter.next();
			mapText.append(me.getValue() + "=" + me.getKey() + "\\n");
		}
		%>
		var initialUserSearches = [ ];
		<c:forEach var="userSearch" items="${config.userSearches}">
			initialUserSearches.push({ baseDn: "${userSearch.baseDn}", filter: "${userSearch.filter}", searchSubtree: "${userSearch.searchSubtree}" });
		</c:forEach>
		var initialGroupSearches = [ ];
		<c:forEach var="groupSearch" items="${config.groupSearches}">
			initialGroupSearches.push({ baseDn: "${groupSearch.baseDn}", filter: "${groupSearch.filter}", searchSubtree: "${groupSearch.searchSubtree}" });
		</c:forEach>
		var $pane = ssPage.createConnection("${config.url}", "${config.userIdAttribute}",
										    "<%=mapText.toString()%>", initialUserSearches, initialGroupSearches,
											"${config.principal}", "${config.credentials}");
		$pane.append(jQuery('<span style="display:none" class="ldapId">${config.id}</span>'));
	</c:forEach>
	
		jQuery('#funkyDiv > ul').bind('tabsshow', function(event, ui) {
			ssPage.currentTab = ui.index;
		});
		jQuery("#funkyDiv > ul").tabs("select", 0);
		jQuery("#funkyDiv > ul").tabs("remove", 0);
	});
</script>

</div>

<div id="ldapTemplate" style="display:none">
	<fieldset class="ldapConfig ss_fieldset">
		<legend class="ldapTitle ss_legend"><ssf:nlt tag="ldap.connection.title" /> <span class="ldapTitle"></span></legend>
		<div>
			<button class="ldapDelete ss_submit"><ssf:nlt tag="ldap.connection.delete" /></button><br/>
			<ssf:nlt tag="ldap.user.url" /><input class="ldapUrl" type="text" value=""/>
			<div>
				<ssf:nlt tag="ldap.user.principal" /><input class="ldapPrincipal" type="text" value=""/><br/>
				<ssf:nlt tag="ldap.user.credential" /><input class="ldapCredentials" type="password" value=""/>
			</div>
			<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.users" /></legend>
				<ssf:nlt tag="ldap.user.idmapping" /><input class="ldapUserIdAttribute" type="text" value=""/><br/>
				<ssf:nlt tag="ldap.user.mappings" />
				<textarea class="ldapMappings" style="height: 100px; width: 400px" wrap="hard"></textarea>
				<div class="ldapUserSearches">
					<div class="ldapSearchList">
					</div>
					<button class="addSearch ss_submit"><ssf:nlt tag="ldap.search.add" /></button>
				</div>
			</fieldset>
			<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt tag="ldap.groups" /></legend>
				<div class="ldapGroupSearches">
					<div class="ldapSearchList">
					</div>
					<button class="addSearch ss_submit"><ssf:nlt tag="ldap.search.add" /></button>
				</div>
			</fieldset>
		</div>
	</fieldset>
</div>

<div id="ldapSearchTemplate" style="display:none">
	<div class="ldapSearch">
		<div>
			<div class="errorMessage"><ssf:nlt tag="ldap.error.invalid"><ssf:param name="value" value='<%=NLT.get("ldap.error.baseDn")%>'/></ssf:nlt></div>
			<label><ssf:nlt tag="ldap.search.baseDn" /></label><input class="ldapBaseDn" value=""/>
		</div>
		<div>
			<div class="errorMessage"><ssf:nlt tag="ldap.error.invalid"><ssf:param name="value" value='<%=NLT.get("ldap.error.filter")%>'/></ssf:nlt></div>
			<label><ssf:nlt tag="ldap.search.filter" /></label><input class="ldapFilter" value=""/>
		</div>
		<input type="checkbox" class="ldapSearchSubtree" value="true"/><span><ssf:nlt tag="ldap.search.searchSubtree" /></span>
		<button class="deleteSearch ss_submit"><ssf:nlt tag="ldap.search.delete" /></button>
	</div>
</div>
</body>
</html>