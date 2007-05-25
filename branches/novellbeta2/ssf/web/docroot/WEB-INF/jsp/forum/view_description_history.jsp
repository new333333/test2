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
<%@ page import="org.dom4j.Element" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<style type="text/css">
del { color: red; }
ins { color: green; }
</style>
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

function escape(s) {
    var n = s;
    n = n.replace(/&nbsp;/g, " ");
    n = n.replace(/<p>/gi, "");
    n = n.replace(/<span>/gi, "");
    n = n.replace(/<\/span>/gi, "");
    n = n.replace(/<\/P>/gi, "\n");
    n = n.replace(/<br>/gi, "\n");
    n = n.replace(/<br\/>/gi, "\n");
    n = n.replace(/&/g, "&amp;");
    n = n.replace(/</g, "&lt;");
    n = n.replace(/>/g, "&gt;");
    n = n.replace(/"/g, "&quot;");

    return n;
}

function diffString( o, n ) {
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
var ss_diffOne = null;
var ss_diffTwo = null;

function ss_updateCompareButton()
{
	if(ss_diffOne && ss_diffTwo) {
		document.getElementById("compareBtn").disabled=false;
	} else {
		document.getElementById("compareBtn").disabled=true;
	}
}
function ss_setOne(id)
{
	ss_diffOne = id;
	ss_updateCompareButton();
}
function ss_setTwo(id)
{
	ss_diffTwo = id;
	ss_updateCompareButton();
}

function dodiff()
{
	if(ss_diffOne && ss_diffTwo) {
		var h = document.getElementById("diff-header");
		h.innerHTML = h.innerHTML.replace("xyzzy", ss_diffOne).replace("yxzzx", ss_diffTwo);
		document.getElementById("diff-title").innerHTML = diffString(document.getElementById("title"+ss_diffOne).innerHTML, document.getElementById("title"+ss_diffTwo).innerHTML);
		document.getElementById("diff-desc").innerHTML = diffString(document.getElementById("desc"+ss_diffOne).innerHTML, document.getElementById("desc"+ss_diffTwo).innerHTML);
		document.getElementById("diff").style.display='block';
	}
}
</script>


<ssf:ifadapter>
<body>
</ssf:ifadapter>

<div class="ss_style ss_portlet">
<h3><ssf:nlt tag="entry.versionHistory"/></h3>
<form class="ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_editable_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="operation" value="modifyEntry" />
		</ssf:url>"
>
<table class="ss_style" cellpadding="10" width="100%">
<th><ssf:nlt tag="entry.Version"/></th><th><ssf:nlt tag="entry.data"/></th>
<c:forEach var="change" items="${ss_changeLogList}">
<tr>
<td valign="top" width="10%" nowrap>
  <input type="radio" name="item1" 
  value="${change.folderEntry.attributes.logVersion}" onclick="ss_setOne('${change.folderEntry.attributes.logVersion}')"
  <c:if test="${change.folderEntry.attributes.logVersion == item1}"> checked="checked" </c:if> >
  <input style="padding-left:10px;" type="radio" name="item2" 
  value="${change.folderEntry.attributes.logVersion}" onclick="ss_setTwo('${change.folderEntry.attributes.logVersion}')"
  <c:if test="${change.folderEntry.attributes.logVersion == item2}"> checked="checked" </c:if> >
  <span style="padding-left:10px;">${change.folderEntry.attributes.logVersion}</span>
</td>
<td valign="top" width="90%">
  <ssf:expandableArea title="${change.folderEntry.attributes.modifiedOn}">
  <div><span class="ss_largeprint" id="title${change.folderEntry.attributes.logVersion}">${change.folderEntry.attribute.title}</span></div>
  <div><span class="ss_smallprint">${change.folderEntry.attributes.modifiedBy}</span></div>
  <div class="ss_entryContent ss_entryDescription" id="desc${change.folderEntry.attributes.logVersion}">
    ${change.folderEntry.attribute.description}
  </div>
  </ssf:expandableArea>
</td>
</tr>
</c:forEach>
<tr>
<td valign="top">
  <input type="button" name="compareBtn" id="compareBtn" value="<ssf:nlt tag="button.compare"/>" disabled="true" onclick="dodiff();"/>
</td>
<td></td>
</tr>
</table>
</form>

<br/>
<br/>

</div>

<div id ="diff" style="display:none">
<h3 id="diff-header"><ssf:nlt tag="entry.comparison">
  <ssf:param name="value" value="xyzzy"/>
  <ssf:param name="value" value="yxzzx"/>
  </ssf:nlt>
</h3>
<div id="diff-title" class="ss_largeprint"></div>
<div id="diff-desc" class="ss_entryContent ss_entryDescription"></div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
