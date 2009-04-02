
<%
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
<%@ page import="org.kablink.teaming.util.NLT"%>
<%@ page import="java.util.ArrayList"%>
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<c:set var="ss_windowTitle"
	value='<%= NLT.get("administration.configure_ldap") %>' scope="request" />
<%@ include file="/WEB-INF/jsp/common/include.jsp"%>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet"><ssf:form titleTag="ldap.title">

	<c:if test="${!empty ssException}">
		<span class="ss_largerprint"><ssf:nlt
			tag="administration.errors" /> (<c:out value="${ssException}" />)</span>
		</br>
	</c:if>

	<form class="ss_style ss_form" name="${renderResponse.namespace}fm"
		method="post"
		action="<ssf:url action="configure_ldap" actionUrl="true"/>">
	<div class="ss_buttonBarRight"><br />
	<input type="submit" class="ss_submit" name="okBtn"
		value="<ssf:nlt tag="button.apply"/>"> <input type="button"
		class="ss_submit" name="closeBtn"
		value="<ssf:nlt tag="button.close" text="Close"/>"
		onClick="self.window.close();return false;" /></div>
	<div>
	<div id="funkyDiv" style="width: 500px">
	<ul>
		<li><a href='#wah'>Wah</a></li>
	</ul>
	<div id='wah'></div>
	</div>
	<button id="ldapAddConnection" class="ss_submit"><ssf:nlt
		tag="ldap.connection.add" /></button>
	</div>
	<div class="ss_divider"></div>
	<br />

	<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
		<tr>
			<td><input type="checkbox" id="enabled" name="enabled"
				<c:if test="${ssLdapConfig.enabled}">checked</c:if> /> <label
				for="enabled"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.enable" /></span><br />
			</label></td>
		</tr>
		<tr>
			<td><input type="checkbox" id="runnow" name="runnow"
				<c:if test="${runnow}"> checked="checked" </c:if> /> <label
				for="runnow"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.now" /></span><br />
			</label></td>
		</tr>
	</table>

	<br />
	<ssf:expandableArea title='<%= NLT.get("ldap.schedule") %>'
		initOpen="true">
		<c:set var="schedule" value="${ssLdapConfig.schedule}" />
		<%@ include file="/WEB-INF/jsp/administration/schedule.jsp"%>
		<div class="ss_divider"></div>
	</ssf:expandableArea> <br />

	<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
		tag="ldap.users" /></legend>
	<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
		<tr>
			<td><input type="checkbox" name="userSync" id="userSync"
				<c:if test="${ssLdapConfig.userSync}">checked</c:if> /> <label
				for="userSync"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.user.sync" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="userRegister" id="userRegister"
				<c:if test="${ssLdapConfig.userRegister}">checked</c:if> /> <label
				for="userRegister"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.user.register" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="userDelete" id="userDelete"
				<c:if test="${ssLdapConfig.userDelete}">checked</c:if> /> <label
				for="userDelete"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.user.delete" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="userWorkspaceDelete"
				id="userWorkspaceDelete"
				<c:if test="${ssLdapConfig.userWorkspaceDelete}">checked</c:if> /> <label
				for="userWorkspaceDelete"><span
				class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.user.workspace.delete" /></span></label></td>
		</tr>
	</table>
	</fieldset>

	<br />
	<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
		tag="ldap.groups" /></legend>
	<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
		<tr>
			<td><input type="checkbox" name="groupRegister"
				id="groupRegister"
				<c:if test="${ssLdapConfig.groupRegister}">checked</c:if> /> <label
				for="groupRegister"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.group.register" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="membershipSync"
				id="membershipSync"
				<c:if test="${ssLdapConfig.membershipSync}">checked</c:if> /> <label
				for="membershipSync"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.membership.sync" /></span></label></td>
		</tr>
		<tr>
			<td><input type="checkbox" name="groupDelete" id="groupDelete"
				<c:if test="${ssLdapConfig.groupDelete}">checked</c:if> /> <label
				for="groupDelete"><span class="ss_labelRight ss_normal"><ssf:nlt
				tag="ldap.schedule.group.delete" /></span></label></td>
		</tr>
	</table>
	</fieldset>

	<br />
	<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
		tag="ldap.localLogin" /></legend> <input type="checkbox" id="allowLocalLogin"
		name="allowLocalLogin"
		<c:if test="${ssAuthenticationConfig.allowLocalLogin}">checked</c:if> />
	<label for="allowLocalLogin"><span
		class="ss_labelRight ss_normal"><ssf:nlt
		tag="ldap.config.allowLocalLogin" /></span><br />
	</label></fieldset>

	<br />
	<div class="ss_buttonBarLeft"><input type="submit"
		class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn"
		value="<ssf:nlt tag="button.close" text="Close"/>"
		onClick="self.window.close();return false;" /></div>
	<input type="hidden" name="ldapConfigDoc" id="ldapConfigDoc" value="" />
	</form>
</ssf:form></div>
<script type="text/javascript" src="<html:rootPath/>js/jquery/jquery.js"></script>
<script type="text/javascript">
     jQuery.noConflict();
</script> <script type="text/javascript"
	src="<html:rootPath/>js/jquery/jquery-ui-personalized.js"></script>

<style type="text/css">
.ui-tabs-hide {
	display: none;
}

.invalid .errorMessage {
	display: inline;
}

.errorMessage {
	display: none;
	color: red;
}

.invalid label {
	color: red;
}

@import "flora.css";

/* Caution! Ensure accessibility in print and other media types... */
@media projection , screen {
	/* Use class for showing/hiding tab content, so that visibility can be better controlled in different media types... */
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
.ui-tabs-nav,.ui-tabs-panel {
	
}

.ui-tabs-nav {
	list-style: none;
	margin: 0;
	padding: 0 0 0 3px;
}

.ui-tabs-nav:after {
	/* clearing without presentational markup, IE gets extra treatment */
	display: block;
	clear: both;
	content: " ";
}

.ui-tabs-nav li {
	float: left;
	margin: 0 0 0 2px;
	font-weight: bold;
	list-style: none !important;
}

.ui-tabs-nav a,.ui-tabs-nav a span {
	float: left; /* fixes dir=ltr problem and other quirks IE */
	padding: 0 12px;
}

.ui-tabs-nav a {
	margin: 5px 0 0;
	/* position: relative makes opacity fail for disabled tab in IE */
	padding-left: 0;
	background-position: 100% 0;
	text-decoration: none;
	white-space: nowrap; /* @ IE 6 */
	outline: 0; /* @ Firefox, prevent dotted border after click */
	font-size: 12px;
}

.ui-tabs-nav a:link,.ui-tabs-nav a:visited {
	
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

.ui-tabs-nav .ui-tabs-selected a:link,.ui-tabs-nav .ui-tabs-selected a:visited
	{ /* @ Opera, use pseudo classes otherwise it confuses cursor... */
	cursor: text;
}

.ui-tabs-nav a:hover,.ui-tabs-nav a:focus,.ui-tabs-nav a:active,.ui-tabs-nav .ui-tabs-unselect a:hover,.ui-tabs-nav .ui-tabs-unselect a:focus,.ui-tabs-nav .ui-tabs-unselect a:active
	{ /* @ Opera, we need to be explicit again here now... */
	cursor: pointer;
}

.ui-tabs-panel {
	border: 1px solid #519e2d;
	padding: 10px;
	background: #fff;
	/* declare background color for container to avoid distorted fonts in IE while fading */
}

/* Additional IE specific bug fixes... */
* html .ui-tabs-nav { /* auto clear @ IE 6 & IE 7 Quirks Mode */
	display: inline-block;
}

* :first-child+html .ui-tabs-nav {
	/* auto clear @ IE 7 Standards Mode - do not group selectors, otherwise IE 6 will ignore complete rule (because of the unknown + combinator)... */
	display: inline-block;
}
</style>

<style media="screen" type="text/css">
.margintop3
{
	margin-top: 1em;
}

.rowaltcolor {
	background-color: #f4f4f4;
}

.syncResultsDialog
{
	background-color: #ffffff;
	border: solid 1px black;
	font-family: arial, sans-serif;
	height: auto !important;
	position: absolute;
	z-index: 75;
}

.syncResultsTitle
{
	color: white;
	font-weight: bold;
	font-size: 1em;
	background-color: #458ab9;
	text-align: left;
	text-indent: 0.2em;
	padding: 0.3em;
}

.syncResultsSection
{
	margin-bottom: .75em;
}

.syncResultsSectionHeaderTR
{
	background-color: #efeeec;
}

.syncResultsSectionHeaderTD
{
	border-bottom: 1px solid #dfddd5;
}

.syncResultsSectionHeaderText
{
	color: black;
	font-size: 0.85em;
	text-align: left;
	margin-left: 0.4em;
	margin-top: 0.2em;
	margin-bottom: 0.2em;
}

.syncResultsSectionItem
{
	margin-left: 1.1em;
	white-space: nowrap;
}

.syncResultsSectionBottomSpace
{
	font-size: 0.85em;
}
</style>

<script type="text/javascript">
var	m_ldapConfigId	= null;

/**
 * This function will close the "Sync Results" dialog.
 */
function closeSyncResultsDlg()
{
	var	div;

	div = document.getElementById( 'syncResultsDlg' );
	div.style.display = 'none';
}// end closeSyncResultsDlg()


/**
 * This function will display the "Sync Results" dialog.
 */
function showSyncResultsDlg()
{
	var	div;
	var	height;

	// Get the height of the <div> that holds all the sync results.
	div = document.getElementById( 'allSyncResultsDiv' );
	height = div.offsetHeight;

	// If the height is > 500 set the height to 500.
	if ( height > 500 )
		div.style.height = '500px';
	
	div = document.getElementById( 'syncResultsDlg' );
	div.style.top = '200px';
	div.style.display = '';
}// end showSyncResultsDlg()


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
			var prompt;

			prompt = "<ssf:nlt tag="ldap.connection.delete.confirm" quoteDoubleQuote="true" />";
			prompt = prompt.replace( '{0}', title );
			if( ss_confirm( prompt, null )) {
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
			var		msg;

			msg = "<ssf:nlt tag="ldap.search.delete.confirm" quoteDoubleQuote="true" />";
			if(ss_confirm( msg )) {
				jQuery(this).parent().remove();
			}
			return false;
		});	

		$listDiv.append($newSearch);
	},

	createConnection : function(url, userIdAttribute, mappings, userSearches, groupSearches, principal, credentials)
	{
		var label = "<ssf:nlt tag="ldap.connection.newConnection"/>";
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

	/**
	 * Select the tab for the ldap config with the id found in m_ldapConfigId.
	 */
	showLdapConfig : function()
	{
		if ( m_ldapConfigId != null && m_ldapConfigId.length > 0 )
		{
			var	span;

			// Get the <span> whose id matches the given ldap config id.
			span = document.getElementById( m_ldapConfigId );
			if ( span != null )
			{
				var	fldset;

				// Get the <fieldset> that holds the ldap configuration.
				fldset = span.parentNode;
				if ( fldset != null && fldset.id != null )
				{
					// Select the tab associated with this ldap configuration.
					jQuery( "#funkyDiv > ul").tabs( "select", '#' + fldset.id );
				}
			}
		}
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
		var wrapWithCDATA = function( str )
		{
			var		newStr;
			
			newStr = '<![CDATA[';
			if ( str != null && str.length > 0 )
				newStr += str;
			newStr += ']]>';

			return newStr;
		};
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
					"<baseDn>" + wrapWithCDATA( baseDn ) + "</baseDn>" +
					"<filter>" +  wrapWithCDATA( filter ) + "</filter>";
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
			ldapDoc += "<url>" + wrapWithCDATA( $this.find('.ldapUrl').val() ) + "</url>" +
						"<userIdAttribute>" + wrapWithCDATA( $this.find('.ldapUserIdAttribute').val() ) + "</userIdAttribute>" +
						"<mappings>" + wrapWithCDATA( $this.find('.ldapMappings').val() ) + "</mappings>";
			ldapDoc += "<principal>" + wrapWithCDATA( $this.find('.ldapPrincipal').val() ) + "</principal><credentials>" +
						wrapWithCDATA( $this.find('.ldapCredentials').val() ) + "</credentials>";
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
</script> <script type="text/javascript">
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
		$pane.append(jQuery('<span id="${config.id}" style="display:none" class="ldapId">${config.id}</span>'));
	</c:forEach>
	
		jQuery('#funkyDiv > ul').bind('tabsshow', function(event, ui) {
			ssPage.currentTab = ui.index;
		});
		jQuery("#funkyDiv > ul").tabs("select", 0);
		jQuery("#funkyDiv > ul").tabs("remove", 0);

		// Did an error happen while doing an ldap sync?
		<c:if test="${!empty ssException}">
			var errMsg;

			// Yes, tell the user about it.
			errMsg = '<ssf:escapeJavaScript><ssf:nlt tag="administration.errors"/>${ssException}</ssf:escapeJavaScript>';
			alert( errMsg );

			// Do we have the id of the ldap configuration that had the error?
			m_ldapConfigId = '${ssErrLdapConfigId}';
			if ( m_ldapConfigId != null && m_ldapConfigId.length > 0 )
			{
				// Yes, Show the ldap configuration that had the error.
				// We can't call ssPage.showLdapConfig() right now.  Wait for .5 second.
				setTimeout( ssPage.showLdapConfig, 500 );
			}
		</c:if>

		// Do we have any sync results?
		<c:if test="${!empty ssSyncResults}">
			// Yes
			// Invoke the Sync Results dialog.
			showSyncResultsDlg();
		</c:if>
	});
</script></div>

<!-- Do we have sync results? -->
<c:if test="${!empty ssSyncResults}">
	<!-- Yes, create a dialog that will display the sync results. -->

	<jsp:useBean id="ssSyncResults"
		type="org.kablink.teaming.module.ldap.LdapSyncResults" scope="request" />
	<%
	ArrayList<String>	syncResults;
	String				name;
	int					i;
%>

	<!-- The following <div> is the Sync Results dialog.  This dialog will display -->
	<!-- all of the results from the ldap sync -->
	<div id="syncResultsDlg" class="syncResultsDialog" style="left: 200px; top: 6000px;">
		<div class="syncResultsTitle"><ssf:nlt tag="ldap.syncResults.title" /></div>

		<!-- This section holds all of the results. -->
		<div id="allSyncResultsDiv" style="width: 400px; overflow: auto;">
			<table width="100%" cellspacing="0">
				<!-- This section holds the list of users that were added -->
				<tr class="syncResultsSectionHeaderTR">
					<td class="syncResultsSectionHeaderTD">
						<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.addedUsers" /></span>
					</td>
				</tr>
			<%
				// Get the list of users that were added.
				syncResults = ssSyncResults.getAddedUsers();
			
				// Were any users added?
				if ( syncResults != null && syncResults.size() > 0 )
				{
					// Yes
					for (i = 0; i < syncResults.size(); ++i)
					{
						name = (String)syncResults.get( i );
			%>
						<tr>
							<td>
								<span class="syncResultsSectionItem"><%= name %></span>
							</td>
						</tr>
			<%
					}
				}
				else
				{
					// No users were added.
			%>
					<tr>
						<td><span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span></td>
					</tr>
			<%
				}
			%>
				<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>


				<!-- This section holds the list of users that were modified -->
				<tr class="syncResultsSectionHeaderTR">
					<td class="syncResultsSectionHeaderTD">
						<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.modifiedUsers" /></span>
					</td>
				</tr>
			<%
				// Get the list of users that were modified.
				syncResults = ssSyncResults.getModifiedUsers();
			
				// Were any users modified?
				if ( syncResults != null && syncResults.size() > 0 )
				{
					// Yes
					for (i = 0; i < syncResults.size(); ++i)
					{
						name = (String)syncResults.get( i );
			%>
						<tr>
							<td>
								<span class="syncResultsSectionItem"><%= name %></span>
							</td>
						</tr>
			<%
					}
				}
				else
				{
					// No users were modified.
			%>
					<tr>
						<td>
							<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
						</td>
					</tr>
			<%
				}
			%>
				<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>


				<!-- This section holds the list of users that were deleted -->
				<tr class="syncResultsSectionHeaderTR">
					<td class="syncResultsSectionHeaderTD">
						<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.deletedUsers" /></span>
					</td>
				</tr>
			<%
				// Get the list of users that were deleted.
				syncResults = ssSyncResults.getDeletedUsers();
			
				// Were any users deleted?
				if ( syncResults != null && syncResults.size() > 0 )
				{
					// Yes
					for (i = 0; i < syncResults.size(); ++i)
					{
						name = (String)syncResults.get( i );
			%>
						<tr>
							<td>
								<span class="syncResultsSectionItem"><%= name %></span>
							</td>
						</tr>
			<%
					}
				}
				else
				{
					// No users were deleted.
			%>
					<tr>
						<td>
							<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
						</td>
					</tr>
			<%
				}
			%>
				<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>


				<!-- This section holds the list of groups that were added -->
				<tr class="syncResultsSectionHeaderTR">
					<td class="syncResultsSectionHeaderTD">
						<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.addedGroups" /></span>
					</td>
				</tr>
			<%
				// Get the list of groups that were added.
				syncResults = ssSyncResults.getAddedGroups();
			
				// Were any groups added?
				if ( syncResults != null && syncResults.size() > 0 )
				{
					// Yes
					for (i = 0; i < syncResults.size(); ++i)
					{
						name = (String)syncResults.get( i );
			%>
						<tr>
							<td>
								<span class="syncResultsSectionItem"><%= name %></span>
							</td>
						</tr>
			<%
					}
				}
				else
				{
					// No groups were added.
			%>
					<tr>
						<td>
							<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
						</td>
					</tr>
			<%
				}
			%>
				<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>


				<!-- This section holds the list of groups that were modified -->
				<tr class="syncResultsSectionHeaderTR">
					<td class="syncResultsSectionHeaderTD">
						<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.modifiedGroups" /></span>
					</td>
				</tr>
			<%
				// Get the list of groups that were modified.
				syncResults = ssSyncResults.getModifiedGroups();
			
				// Were any groups modified?
				if ( syncResults != null && syncResults.size() > 0 )
				{
					// Yes
					for (i = 0; i < syncResults.size(); ++i)
					{
						name = (String)syncResults.get( i );
			%>
						<tr>
							<td>
								<span class="syncResultsSectionItem"><%= name %></span>
							</td>
						</tr>
			<%
					}
				}
				else
				{
					// No groups were modified.
			%>
					<tr>
						<td>
							<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
						</td>
					</tr>
			<%
				}
			%>
				<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>


				<!-- This section holds the list of groups that were deleted -->
				<tr class="syncResultsSectionHeaderTR">
					<td class="syncResultsSectionHeaderTD">
						<span class="syncResultsSectionHeaderText"><ssf:nlt tag="ldap.syncResults.deletedGroups" /></span>
					</td>
				</tr>
			<%
				// Get the list of groups that were deleted.
				syncResults = ssSyncResults.getDeletedGroups();
			
				// Were any groups deleted?
				if ( syncResults != null && syncResults.size() > 0 )
				{
					// Yes
					for (i = 0; i < syncResults.size(); ++i)
					{
						name = (String)syncResults.get( i );
			%>
						<tr>
							<td>
								<span class="syncResultsSectionItem"><%= name %></span>
							</td>
						</tr>
			<%
					}
				}
				else
				{
					// No groups were deleted.
			%>
					<tr>
						<td>
							<span class="syncResultsSectionItem"><ssf:nlt tag="ldap.syncResults.none" /></span>
						</td>
					</tr>
			<%
				}
			%>
				<tr><td><span class="syncResultsSectionBottomSpace">&nbsp;</span></td></tr>
			</table>
		</div>

		<!-- Buttons -->
		<div class="margintop3 rowaltcolor" style="padding: 0.5em; border-top: 1px solid #babdb6">
			<a id="syncResultDlg_close" title="<ssf:nlt tag="ldap.syncResults.alt.close"/> href="#" onclick="closeSyncResultsDlg()">
				<input type="button" name="closeSyncResultsDlgBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
			</a>
		</div>
	</div>
</c:if>

<div id="ldapTemplate" style="display: none">
<fieldset class="ldapConfig ss_fieldset"><legend
	class="ldapTitle ss_legend"><ssf:nlt
	tag="ldap.connection.title" /> <span class="ldapTitle"></span></legend>
<div>
<button class="ldapDelete ss_submit"><ssf:nlt
	tag="ldap.connection.delete" /></button>
<br />
<br />
<table>
	<tr>
		<td nowrap></td>
		<td nowrap><span class="ss_fineprint ss_bright"><ssf:nlt
			tag="ldap.user.url.title" /></span></td>
	</tr>
	<tr>
		<td nowrap><label for="ldapUrl"><ssf:nlt
			tag="ldap.user.url" />&nbsp;</label></td>
		<td><input class="ldapUrl" id="ldapUrl" type="text" value=""
			size="140" /></td>
	</tr>
	<tr>
		<td nowrap><label for="ldapPrincipal"><ssf:nlt
			tag="ldap.user.principal" /></label></td>
		<td><input class="ldapPrincipal" id="ldapPrincipal" type="text"
			value="" size="140" /></td>
	</tr>
	<tr>
		<td nowrap><label for="ldapCredentials"><ssf:nlt
			tag="ldap.user.credential" /></label></td>
		<td><input class="ldapCredentials" id="ldapCredentials"
			type="password" value="" size="140" /></td>
	</tr>
</table>

<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
	tag="ldap.users" /></legend> <label for="ldapUserIdAttribute"><ssf:nlt
	tag="ldap.user.idmapping" />&nbsp;&nbsp;</label><input
	class="ldapUserIdAttribute" id="ldapUserIdAttribute" type="text"
	value="" size="40" /><br />
<br />
<label for="ldapMappings"><ssf:nlt tag="ldap.user.mappings" /></label>
<br />
<textarea class="ldapMappings" id="ldapMappings"
	style="height: 150px; width: 400px" wrap="hard"></textarea> <br />
<br />
<div class="ldapUserSearches">
<div class="ldapSearchList"></div>
<button class="addSearch ss_submit"><ssf:nlt
	tag="ldap.search.add" /></button>
</div>
</fieldset>
<fieldset class="ss_fieldset"><legend class="ss_legend"><ssf:nlt
	tag="ldap.groups" /></legend>
<div class="ldapGroupSearches">
<div class="ldapSearchList"></div>
<button class="addSearch ss_submit"><ssf:nlt
	tag="ldap.search.add" /></button>
</div>
</fieldset>
</div>
</fieldset>
</div>

<div id="ldapSearchTemplate" style="display: none">
<div class="ldapSearch" style="padding: 6px; border: 1px solid #cecece;">
<div>
<table>
	<tr>
		<td colspan="2">
		<div class="errorMessage"><ssf:nlt tag="ldap.error.invalid">
			<ssf:param name="value" value='<%=NLT.get("ldap.error.baseDn")%>' />
		</ssf:nlt></div>
		</td>
	</tr>
	<tr>
		<td nowrap><label for="ldapBaseDn"><ssf:nlt
			tag="ldap.search.baseDn" /></label></td>
		<td><input class="ldapBaseDn" id="ldapBaseDn" value="" size="120" />
		</td>
	</tr>

	<tr>
		<td colspan="2">
		<div class="errorMessage"><ssf:nlt tag="ldap.error.invalid">
			<ssf:param name="value" value='<%=NLT.get("ldap.error.filter")%>' />
		</ssf:nlt></div>
		</td>
	</tr>
	<tr>
		<td nowrap><label for="ldapFilter"><ssf:nlt
			tag="ldap.search.filter" /></label></td>
		<td><input class="ldapFilter" id="ldapFilter" value="" size="120" />
		</td>
	</tr>
</table>
</div>
<br />
<input type="checkbox" class="ldapSearchSubtree" id="ldapSearchSubtree"
	value="true" /> <label for="ldapSearchSubtree"><span
	style="padding-left: 4px;"><ssf:nlt
	tag="ldap.search.searchSubtree" /></span></label> <br />
<button class="deleteSearch ss_submit"><ssf:nlt
	tag="ldap.search.delete" /></button>
</div>
</div>
</body>
</html>