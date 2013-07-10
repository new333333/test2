/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

//jQuery Functions
$(document).ready(function(){
	$(".ss_related_close").click(function () {
		$(".relatedfileslist").slideUp("fast");
	});
});

// Controls whether the debugTrace() method displays anything.
// true -> It does.  false -> It doesn't.
var	ENABLE_RELATED_FILES_TRACING = false;

// Controls the number of entries displayed in a related items section
// before a 'more...' item is shown.
var RELATED_ENTRIES_BLOCK_SIZE = 5;


/*
 * Called to handle the response from an AJAX operation on
 * related files.
 */
function ajaxRelatedFilesByEntry_Response(data) {
	// Stop the spinner and display the results.
	ss_stopSpinner();

	// Trace the response data that we received from the AJAX call.
	traceAjaxResponse("ajaxRelatedFilesByEntry_Response()", data);

	// If the response indicated an error...
	if (0 == data.status) {
		// Display it and bail.
		alert(data.errDesc);
		return;
	}

	// Otherwise, construct the relevance list DIV's...
	buildRelationshipDIV("DIV_relatedFiles",      data.relatedFiles,      RELATED_ENTRIES_BLOCK_SIZE, "entry.relatedFiles.None"     );
	buildRelationshipDIV("DIV_relatedWorkspaces", data.relatedWorkspaces, RELATED_ENTRIES_BLOCK_SIZE, "entry.relatedWorkspaces.None");
	buildRelationshipDIV("DIV_relatedUsers",      data.relatedUsers,      RELATED_ENTRIES_BLOCK_SIZE, "entry.relatedUsers.None"     );

	// ...and show it.
	positionAndShowList(
		document.getElementById("ss_related_files"),
		document.getElementById(document.getElementById("ss_related_anchor_id").value));
}

/*
 * Called to submit an AJAX request to perform an operation on
 * related files.
 */
function ajaxRelatedFilesByEntry_Submit(binderId, entryId) {
	ss_startSpinner();
	ss_get_url(
		ss_buildAdapterUrl(
			ss_AjaxBaseUrl,
			{
				operation:"get_file_relationships_by_entry",
				binderId:binderId,
				entryId:entryId,
			}),
		ajaxRelatedFilesByEntry_Response);
}

/*
 * Builds the <DIV> containing the relationship data in aData.
 * 
 * <div class="ss_related_item">
 *       <a class="ss_related_anchor" href="...permalink...">...name...</a>
 * </div>
 */
function buildRelationshipDIV(sDIV, aData, iMaxPerSection, sNoRelationshipsKey) {
	// Locate and empty the DIV we're to build.
	eDIV = document.getElementById(sDIV);
	emptyDIV(eDIV);

	// Are there any relationships for this DIV?
	var	eInnerDIV;
	var c = aData.length;
	if (0 == c) {
		// No!  Just display a message saying that.
		eInnerDIV = document.createElement("DIV");
		eInnerDIV.className = "ss_related_item";
		eInnerDIV.appendChild(document.createTextNode(g_relevanceStrings[sNoRelationshipsKey]));
		eDIV.appendChild(eInnerDIV);
	}
	
	else {
		var i;
		var	aEach;
		
		// Yes, there are relationships for this DIV!  Scan them...
		for (i = 0; i < c; i += 1) {
			aEach = aData[i];
			
			// ...creating a DIV for each.
			eInnerDIV = document.createElement("DIV");
			eInnerDIV.className = "ss_related_item";
			
			if (iMaxPerSection == i) {
				eA = createAWithTEXT(eInnerDIV, "#", g_relevanceStrings["entry.more"]);
				eA.onclick = function() {
					// Track the current scroll position...
					var xy = getScrollPosition();
					
					// ...display 'more...' information...
					buildRelationshipDIV(sDIV, aData, (iMaxPerSection + RELATED_ENTRIES_BLOCK_SIZE), sNoRelationshipsKey);

					// ...and scroll back to the original position.
					var eRootDIV = document.getElementById("ss_related_files");
					if (eRootDIV.scrollIntoView)
						 eRootDIV.scrollIntoView(false);
					else window.scrollTo(xy.x, xy.y);
				}
				eInnerDIV.appendChild(eA);
				eDIV.appendChild(eInnerDIV);
				break;
			}
			
			eA = createAWithTEXT(eInnerDIV, aEach[1], aEach[0]);
			eInnerDIV.appendChild(eA);
			
			if ("DIV_relatedFiles" == sDIV) {
				eInnerDIV.appendChild(document.createTextNode(" ("))
				eA = createAWithTEXT(eInnerDIV, aEach[3], aEach[2]);
				eInnerDIV.appendChild(eA);
				eInnerDIV.appendChild(document.createTextNode(")"))
			}
			
			eDIV.appendChild(eInnerDIV);
		}
	}
}

/*
 * Creates an <A> with sText as its content.
 */
function createAWithTEXT(eContainer, sHREF, sText) {
	var	eA;
	var	eTEXTContainer;


	// Create an <A>...
	eA = document.createElement("A");
	eA.setAttribute("href", sHREF);
	eA.className = "ss_related_anchor";
	
	// ...containing a text Node in a <BR>.
    eTEXTContainer = document.createElement("NOBR");
    eTEXTContainer.appendChild(document.createTextNode(sText));
    eA.appendChild(eTEXTContainer);


	// If we get here, eA refers to the <A> created.  Return it.
	return(eA);
}

/*
 * If debugging is enabled for this file, raises JavaScript alert
 * containing the trace message. 
 */
function debugTrace(traceThis) {
	if (ENABLE_RELATED_FILES_TRACING) {
		alert(traceThis);
	}
}

/*
 * Removes the contents of a <DIV>.
 */
function emptyDIV(eDIV) {

	// If we weren't given a <DIV> to empty...
	if (null == eDIV) {
		// ...bail.
		return;
	}


	// Does the <DIV> contain any children?
	if (eDIV.hasChildNodes()) {
		var	an;
		var	c;
		var	i;
		var	n;


		// Yes!  Scan them...
		an = eDIV.childNodes;
		c  = an.length;
		for (i = (c - 1); i >= 0; i -= 1) {
			// ...removing each child's children...
			n = an[i];
			emptyDIV(n);

			// ...and removing the child itself.
			eDIV.removeChild(n);
		}
	}
}

/*
 * Called to update the scroll position widgets on the main form with
 * with the current scroll position values.
 */
function getScrollPosition()
{
	var	x;
	var	y;

	if (document.all)	// IE versus everything else.
		 {x = document.body.scrollLeft; y = document.body.scrollTop;}
	else {x = window.pageXOffset;       y = window.pageYOffset;     }

	return new xyPos(x, y);
}

/*
 * Positions and shows the root relevance list DIV.
 */
function positionAndShowList(eDIV, eRelatedAnchor) {
	// Calculate where to position the DIV...
	oACoords = dojo.coords(eRelatedAnchor, true);
    var top  =  parseInt(oACoords.y);                         if (top  < 0) top  = 0;
    var left = (parseInt(oACoords.x) + parseInt(oACoords.w)); if (left < 0) left = 0;
    
    // ...put it there...
    eDIV.style.top  = (top + "px");
    eDIV.style.left = (left + "px");

    // ...and show it.
   	$("#ss_related_files").slideDown("normal");
}

/*
 * Called when the user clicks a View Related Files anchor.
 */
function ss_showRelatedFilesForEntry(sA, binderId, entryId) {
	// Hide any list that's currently open...
   	$(".relatedfileslist").hide();
   	
   	// ...store the ID of the <A> that was clicked to invoke this
   	// ...method...
   	document.getElementById("ss_related_anchor_id").value = sA;
   	
   	// ...and submit an AJAX request for the relationship data.
	ajaxRelatedFilesByEntry_Submit(binderId, entryId);
}

/*
 * If debugging is enabled, dumps the response data we received from
 * the AJAX request.
 */
function traceAjaxResponse(sPre, ajaxResponse) {
	if (ENABLE_RELATED_FILES_TRACING) {
		var	traceThis = "";
		for (d in ajaxResponse) {
			traceThis += ("\n\t" + d + ":  " + ajaxResponse[d]);
		}
		debugTrace(sPre + ":  \n\nData:  " + traceThis);
	}
}

/*
 * Constructor for the xyPos object.
 */
function xyPos(x, y)
{
	this.x = x;
	this.y = y;
}
