<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="org.kablink.teaming.web.util.MiscUtil" %>
<%@ page import="org.kablink.teaming.util.ResolveIds" %>
<%@ page import="org.kablink.teaming.domain.Principal" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:set var="entryId" value="${ss_entityId}" />
<jsp:useBean id="entryId" type="java.lang.Long" />
<% boolean entryInTrash = MiscUtil.isEntryPreDeleted(entryId); %>

<c:set var="ss_useExplicitFileVersionNumbers" value="true" scope="request" />
<c:set var="moreCount" value="20" scope="request" />
<c:set var="oper2" value='<%= request.getParameter("ssOperation2") %>' scope="request"/>
<c:if test="${!empty oper2}">
  <c:set var="ssOperation2" value="${oper2}" scope="request" />
</c:if>
<c:if test="${empty oper2}">
  <c:set var="ssOperation2" value="${moreCount}" scope="request" />
</c:if>

<ssf:ifadapter>
<body>
</ssf:ifadapter>

<style type="text/css">
del { font-weight: normal; text-decoration: line-through; color: #000; background-color: #e0e0e0; -moz-border-radius: 3px; border-radius: 3px; -webkit-border-radius: 3px;}
ins { font-weight: normal; text-decoration: none; color: #fff; background-color: #83bf30;-moz-border-radius: 3px; border-radius: 3px; -webkit-border-radius: 3px; }
</style>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
  var wDiffStyleDelete = 'font-weight: normal; text-decoration: none; color: #fff; background-color: #990033;';
  var wDiffStyleInsert = 'font-weight: normal; text-decoration: none; color: #fff; background-color: #009933;';
  ss_createOnLayoutChangeObj("ss_entryHistoryChange", function() {
	  if (typeof self.parent.ssf_onLayoutChange != "undefined") {
		  //ss_debug("ss_entryHistoryChange  Calling parent layout change object");
		  setTimeout("self.parent.ssf_onLayoutChange();", 100);
	  }
  });
</script>
<script type="text/javascript">
/*
 * Javascript Diff Algorithm
 *  By John Resig (http://ejohn.org/)
 *  Modified by Chu Alan "sprite"
 *  Hacked to suit SiteScape by Joe DeStefano
 *
 * More Info:
 *  http://ejohn.org/projects/javascript-diff-algorithm/
 */

function stripHtmlTags(s) {
    var n = s;
    n = n.replace(/(<div\s*[^>]*\s*)id=\"[^\"]*\"/gmi, "$1");
    //n = n.replace(/(<div\s*[^>]*\s*)class=\"[^\"]*\"/gmi, "$1");
    n = n.replace(/<ssHelpSpot.*<\/ssHelpSpot>/gmi, "");
    return n;
}
function escape(s) {
    var n = s;
    n = n.replace(/&nbsp;/g, " ");
    n = n.replace(/<p>/gi, "");
    n = n.replace(/<span>/gi, "");
    n = n.replace(/<\/span>/gi, "");
    n = n.replace(/<br>/gi, "\n");
    n = n.replace(/<br\/>/gi, "\n");
    n = n.replace(/<\/P>/gi, "");
    n = n.replace(/&/g, "&amp;");
    n = n.replace(/</g, "&lt;");
    n = n.replace(/>/g, "&gt;");
    n = n.replace(/"/g, "&quot;");

    return n;
}

function diffString( n, o ) {
  o = o.replace(/\s+$/, '');
  n = n.replace(/\s+$/, '');

  var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/) );
  var str = "";

  var oSpace = o.match(/\s+/g);
  if (oSpace == null) {
    oSpace = ["\n"];
  } else {
    oSpace.push("\n");
  }
  var nSpace = n.match(/\s+/g);
  if (nSpace == null) {
    nSpace = ["\n"];
  } else {
    nSpace.push("\n");
  }

  if (out.n.length == 0) {
      for (var i = 0; i < out.o.length; i++) {
        str += '<del>' + escape(out.o[i]) + oSpace[i] + "</del>";
      }
  } else {
    if (out.n[0].text == null) {
      for (n = 0; n < out.o.length && out.o[n].text == null; n++) {
        str += '<del>' + escape(out.o[n]) + oSpace[n] + "</del>";
      }
    }

    for ( var i = 0; i < out.n.length; i++ ) {
      if (out.n[i].text == null) {
        str += '<ins>' + escape(out.n[i]) + nSpace[i] + "</ins>";
      } else {
        var pre = "";

        for (n = out.n[i].row + 1; n < out.o.length && out.o[n].text == null; n++ ) {
          pre += '<del>' + escape(out.o[n]) + oSpace[n] + "</del>";
        }
        str += " " + out.n[i].text + nSpace[i] + pre;
      }
    }
  }
  
  return str;
}

function randomColor() {
    return "rgb(" + (Math.random() * 100) + "%, " + 
                    (Math.random() * 100) + "%, " + 
                    (Math.random() * 100) + "%)";
}
function diffString2( o, n ) {
  o = o.replace(/\s+$/, '');
  n = n.replace(/\s+$/, '');

  var out = diff(o == "" ? [] : o.split(/\s+/), n == "" ? [] : n.split(/\s+/) );

  var oSpace = o.match(/\s+/g);
  if (oSpace == null) {
    oSpace = ["\n"];
  } else {
    oSpace.push("\n");
  }
  var nSpace = n.match(/\s+/g);
  if (nSpace == null) {
    nSpace = ["\n"];
  } else {
    nSpace.push("\n");
  }

  var os = "";
  var colors = new Array();
  for (var i = 0; i < out.o.length; i++) {
      colors[i] = randomColor();

      if (out.o[i].text != null) {
          os += '<span style="background-color: ' +colors[i]+ '">' + 
                escape(out.o[i].text) + oSpace[i] + "</span>";
      } else {
          os += "<del>" + escape(out.o[i]) + oSpace[i] + "</del>";
      }
  }

  var ns = "";
  for (var i = 0; i < out.n.length; i++) {
      if (out.n[i].text != null) {
          ns += '<span style="background-color: ' +colors[out.n[i].row]+ '">' + 
                escape(out.n[i].text) + nSpace[i] + "</span>";
      } else {
          ns += "<ins>" + escape(out.n[i]) + nSpace[i] + "</ins>";
      }
  }

  return { o : os , n : ns };
}

function diff( o, n ) {
  var ns = new Object();
  var os = new Object();
  
  for ( var i = 0; i < n.length; i++ ) {
    if ( ns[ n[i] ] == null )
      ns[ n[i] ] = { rows: new Array(), o: null };
    ns[ n[i] ].rows.push( i );
  }
  
  for ( var i = 0; i < o.length; i++ ) {
    if ( os[ o[i] ] == null )
      os[ o[i] ] = { rows: new Array(), n: null };
    os[ o[i] ].rows.push( i );
  }
  
  for ( var i in ns ) {
    if ( ns[i].rows.length == 1 && typeof(os[i]) != "undefined" && os[i].rows.length == 1 ) {
      n[ ns[i].rows[0] ] = { text: n[ ns[i].rows[0] ], row: os[i].rows[0] };
      o[ os[i].rows[0] ] = { text: o[ os[i].rows[0] ], row: ns[i].rows[0] };
    }
  }
  
  for ( var i = 0; i < n.length - 1; i++ ) {
    if ( n[i].text != null && n[i+1].text == null && n[i].row + 1 < o.length && o[ n[i].row + 1 ].text == null && 
         n[i+1] == o[ n[i].row + 1 ] ) {
      n[i+1] = { text: n[i+1], row: n[i].row + 1 };
      o[n[i].row+1] = { text: o[n[i].row+1], row: i + 1 };
    }
  }
  
  for ( var i = n.length - 1; i > 0; i-- ) {
    if ( n[i].text != null && n[i-1].text == null && n[i].row > 0 && o[ n[i].row - 1 ].text == null && 
         n[i-1] == o[ n[i].row - 1 ] ) {
      n[i-1] = { text: n[i-1], row: n[i].row - 1 };
      o[n[i].row-1] = { text: o[n[i].row-1], row: i - 1 };
    }
  }
  
  return { o: o, n: n };
}

</script>


<script type="text/javascript">
function getMoreEntries() {
	var url = self.location.href;
	var op2 = "&ssOperation2=" + "${moreCount*2}";
	if (url.indexOf("&ssOperation2=") > 0) {
		var i = url.indexOf("&ssOperation2=") + 14;
		var count = url.substring(i, url.length);
		count = count.substring(0, count.indexOf("&"));
		var newCount = parseInt(count) + ${moreCount};
		op2 = "&ssOperation2=" + newCount;
		var url1 = url.substring(0, url.indexOf("&ssOperation2="));
		var url2 = url.substring(url.indexOf("&ssOperation2=") + 14 + count.length, url.length);
		url = url1 + url2;
	}
	var url1 = url.substring(0, url.indexOf("&operation="));
	var url2 = url.substring(url.indexOf("&operation="), url.length);
	url = url1 + op2 + url2;
	self.location.href = url;
}

function clearAllCheckboxes() {
	//Look through all of the checkboxes and clear them
	var inputElements = document.getElementsByTagName("input");
	for (var i = 0; i < inputElements.length; i++) {
		var cbObj = inputElements[i];
		if (cbObj != null && cbObj.type.toLowerCase() == "checkbox" && cbObj.getAttribute("nType") == "vsnCheckbox") {
			cbObj.checked = false;
		}
	}
}

var ss_diffOne = null;
var ss_diffTwo = null;

function ss_updateCompareButton() {
	ss_diffOne = null;
	ss_diffTwo = null;

	//Look through all of the checkboxes to see if there are two set
	var inputElements = document.getElementsByTagName("input");
	for (var i = 0; i < inputElements.length; i++) {
		var cbObj = inputElements[i];
		if (cbObj != null && cbObj.type.toLowerCase() == "checkbox" && cbObj.getAttribute("nType") == "vsnCheckbox" && cbObj.checked) {
			var vId = cbObj.id.substring(7);
			if (ss_diffOne == null) {
				ss_diffOne = vId;
			} else if (ss_diffTwo == null) {
				ss_diffTwo = vId;
			} else {
				ss_diffOne = null;
				ss_diffTwo = null;
				break;
			}
		}
	}
	if (ss_diffOne != null && ss_diffTwo != null) {
		document.getElementById("compareBtn").disabled=false;
	} else {
		document.getElementById("compareBtn").disabled=true;
	}
}

function dodiff()
{
	if(ss_diffOne != null && ss_diffTwo != null) {
		//var h = document.getElementById("diff-header");
		//h.innerHTML = h.innerHTML.replace("xyzzy", ss_diffOne).replace("yxzzx", ss_diffTwo);
		var vnA = document.getElementById("versionNumberA");
		vnA.innerHTML = ss_diffOne;
		var vnB = document.getElementById("versionNumberB");
		vnB.innerHTML = ss_diffTwo;
		document.getElementById("diff-title").innerHTML = diffString(document.getElementById("title"+ss_diffOne).innerHTML, document.getElementById("title"+ss_diffTwo).innerHTML);
		document.getElementById("diff-desc").innerHTML = diffString(document.getElementById("desc"+ss_diffOne).innerHTML, document.getElementById("desc"+ss_diffTwo).innerHTML);
		document.getElementById("diff").style.display='block';
		document.getElementById("diff").focus();
	}
	ss_resizeIframeArea();
}

function ss_resizeIframeArea() {
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	if (self.parent.ssf_onLayoutChange) self.parent.ssf_onLayoutChange();
}
</script>

<div class="tab_form">
	<ssf:form>
	<div class="marginbottom3">
		<span class="ss_style"><ssf:nlt tag="entry.version.instructions"/></span>
	</div>
	<form name="viewEntryHistoryForm" class="ss_style ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_editable_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="operation" value="view_edit_history" />
		</ssf:url>"
>

	<div id="compare-button" class="marginbottom3">
		<div>
			<input class="n-button" type="button" name="compareBtn" id="compareBtn" value="<ssf:nlt tag="button.compare"/>" disabled="true" onclick="dodiff();"/>
			<span class="ss_style"><ssf:nlt tag="entry.version.compare"/></span>
		</div>
	</div>

	<table class="ss_style" cellpadding="0" cellspacing="0">
		<tr class="ss_tab_table_columnhead">
			<th colspan="2"><ssf:nlt tag="entry.Version"/></th>
			<th><ssf:nlt tag="entry.modifiedOn"/></th>
			<th><ssf:nlt tag="entry.modifiedBy"/></th>
			<th><ssf:nlt tag="entry.change"/></th>
			<th colspan="2">&nbsp;</th>
		</tr>
	
		<c:forEach var="change" items="${ss_changeLogList}" varStatus="status" end="${ssOperation2 - 1}">
		  <c:set var="changeLog" value="${change.changeLog}"/>
		  <jsp:useBean id="changeLog" type="org.kablink.teaming.domain.ChangeLog" />
		  <tr class="ss_tab_table_row">
			<td>
			  <input type="checkbox" nType="vsnCheckbox" id="compare${change.folderEntry.attributes.logVersion}"
			  onChange="ss_updateCompareButton('${fn:length(ss_changeLogList)}')"
			  onClick="ss_updateCompareButton('${fn:length(ss_changeLogList)}')"
			  >
			</td>
			<td>
			  <span>${change.folderEntry.attributes.logVersion}</span>
			</td>
			<td>
			<a href="javascript: ;"
				onClick="ss_showHide('historyVersion_${status.count}');ss_resizeIframeArea();return false;"
				title="<ssf:nlt tag="entry.modifiedView"/>"
				><fmt:formatDate timeZone="${ssUser.timeZone.ID}" type="both" value="${change.changeLog.operationDate}"/>
			</a>
			</td>
			<td>
			  <ssf:showUser user="${change.changeLogEntry.modification.principal}"/>
			</td>
			<td>
			  <div><ssf:nlt tag="changeLog.operation.${change.operation}"/></div>
			</td>		
			<td colspan="2">
			<c:if test="${ss_accessControlMap['modifyEntry']}">
			  <% if (!entryInTrash) { %>
			  <a class="ss_tinyButton"
				href="<ssf:url><ssf:param 
				name="action" value="view_editable_history"/><ssf:param 
				name="operation" value="revert"/><ssf:param 
				name="entityId" value="${ss_entityId}"/><ssf:param 
				name="versionId" value="${change.folderEntry.attributes.logVersion}"/></ssf:url>"
				alt="<ssf:nlt tag="entry.comparison.revert"/>"
				title="<ssf:nlt tag="entry.comparison.revert"/>"
				><ssf:nlt tag="entry.revert"/></a>
			  <% } %>
			</c:if>
			</td>
		  </tr>
		  <tr>
			 <td colspan="7">
			   <c:if test="${!empty change.changeLogEntry}">
				<c:set var="changeLogEntry" value="${change.changeLogEntry}"/>
				<jsp:useBean id="changeLogEntry" type="org.kablink.teaming.domain.DefinableEntity" />
				<% 
					Element configEle = (Element)changeLogEntry.getEntryDefDoc().getRootElement().selectSingleNode("//item[@name='entryView']");
				%>
				<c:set var="configEle" value="<%= configEle %>" />
				<div id="historyVersion_${status.count}" style="display:none; padding:10px; border: 1px solid #333; background-color: #fff; margin-bottom: 5px;">
					<c:if test="${!empty configEle}">
					  <c:set var="ssBinderOriginalFromDescriptionHistory" value="${ssBinder}" />
					  <c:set var="ssBinder" value="${changeLogEntry.parentBinder}" scope="request"/>
					  <c:set var="ssEntryOriginalFromDescriptionHistory" value="${ssEntry}" />
					  <c:set var="ssEntry" value="${changeLogEntry}" scope="request"/>
					  <c:set var="ss_pseudoEntity" value="true" scope="request"/>
					  <ssf:displayConfiguration 
						configDefinition="${changeLogEntry.entryDefDoc}" 
						configElement="<%= configEle %>"
						configJspStyle="view" 
						entry="${changeLogEntry}" 
						processThisItem="true" />

						<c:if test="${!empty change.folderEntry.workflowState}">
						  <div style="padding-top:10px;">
						  	<span class="ss_bold"><ssf:nlt tag="entry.workflowStates"/></span>
						  </div>
						  <table cellspacing="5">
						   <tr>
						     <th><ssf:nlt tag="entry.processName"/></th>
						     <th><ssf:nlt tag="entry.thread"/></th>
						     <th><ssf:nlt tag="entry.stateName"/></th>
						   </tr>
							<c:forEach var="workflow" items="${change.folderEntry.workflowState}">
							  <tr>
							    <td valign="top">
									  <c:if test="${!empty workflow.value.attributes.process}">
									    ${workflow.value.attributes.process}
									  </c:if>
									  <c:if test="${empty workflow.value.attributes.process}">
									    ???
									  </c:if>
							    </td>
							    <td valign="top">
									  <c:if test="${!empty workflow.value.attributes.threadCaption}">
									    ${workflow.value.attributes.threadCaption}&nbsp
									  </c:if>
									  <c:if test="${empty workflow.value.attributes.threadCaption}">
									    ${workflow.value.attributes.thread}&nbsp
									  </c:if>
								</td>
								<td valign="top">
									  <c:if test="${!empty workflow.value.attributes.stateCaption}">
									    ${workflow.value.attributes.stateCaption}&nbsp
									  </c:if>
									  <c:if test="${empty workflow.value.attributes.stateCaption}">
									    ${workflow.value.attributes.name}&nbsp
									  </c:if>
							     </td>
							   </tr>
							</c:forEach>
						  </table>
						</c:if>

					  <c:set var="ssBinder" value="${ssBinderOriginalFromDescriptionHistory}" scope="request"/>
					  <c:set var="ssEntry" value="${ssEntryOriginalFromDescriptionHistory}" scope="request"/>
					</c:if>
				</div>
			   </c:if>
			  <div style="display:none;">
				<span class="ss_entryTitle" id="title${change.folderEntry.attributes.logVersion}">
				  ${change.folderEntry.attribute.title.value}
				</span>
			  </div>
			  <div style="display:none;" class="ss_entryContent ss_entryDescription" id="desc${change.folderEntry.attributes.logVersion}">
				<ssf:markup entity="${changeLogEntry}">${change.folderEntry.attribute.description.value}</ssf:markup>
			  </div>
			 </td>
		  </tr>
		</c:forEach>
	</table>
	
	<div class="margintop2 marginbottom3">
	  <c:if test="${fn:length(ss_changeLogList) > ssOperation2}">
		  <input class="ss_tinyButton" style="border: 0px; font-size: 11px;" type="button" name="moreBtn" value="<ssf:nlt tag="general.more"/>" 
		    onclick="getMoreEntries();"/>
	  </c:if>
	  <input class="ss_tinyButton" style="border: 0px; font-size: 11px;" type="button" name="clearAllBtn" value="<ssf:nlt tag="button.deselectAll"/>" 
	    onclick="clearAllCheckboxes();"/>
	</div>
		<sec:csrfInput />
	</form>

	<div id ="diff" class="ss_diff_content" style="display:none;">
		<div id="diff-header" class="head2"><ssf:nlt tag="entry.comparison">
			<ssf:param name="value" value="<span id=\"versionNumberA\">x</span>"/>
		  	<ssf:param name="value" value="<span id=\"versionNumberB\">x</span>"/>
			</ssf:nlt>
			<span style="padding: 3px; margin-left: 1em; font-weight: normal;"><ssf:nlt tag="entry.comparison.key"/></span>
			<div id="diff-key" class="margintop1" style="font-weight: normal; font-size: 12px; color: #666;">
				<span ><ssf:nlt tag="entry.comparison.note"/></span>
			</div>
		</div>
	
		<div class="head2 margintop3" style="color: #666"><ssf:nlt tag="general.title"/></div>
		<div id="diff-title" class="ss_entryTitle"></div>
		<div class="head2 margintop2" style="color: #666"><ssf:nlt tag="__description"/></div>
		<div id="diff-desc" class="ss_entryContent ss_entryDescription"></div>
	</div>

</ssf:form>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
