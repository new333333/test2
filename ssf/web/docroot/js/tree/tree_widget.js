//Routines to display an expandable/contractable tree
//
//Start by defining a new tree object - new ssTree(treeId, className)
//Next, define the nodes in the tree - ssTree.defineNode(nodeId, parentId, text, image, url)
//Finally, create the tree - ssTree.create(openNodes)

if (!ssTree_widget_treeList) {
	var ssTree_widget_treeList = new ssArray();
}

function ss_treeToggle(treeName, node, bottom, type) {
    var tObj = self.document.getElementById(treeName + "div" + node);
    var jObj = self.document.getElementById(treeName + "join" + node);
    var iObj = self.document.getElementById(treeName + "icon" + node);
    if (tObj == null) {
        alert('no tree div: ' + treeName + "div" + node);
    }
    if (tObj.style.display == "none" || tObj.style.visibility == 'hidden') {
        tObj.style.display = "block";
        tObj.style.visibility = 'visible';
		if (bottom == 1) {
			jObj.className = "ss_twMinusBottom";	 // minus_bottom.gif
		} else {
			jObj.className = "ss_twMinus";           // minus.gif
		}
		if (ss_treeIconsOpen[type]) iObj.src = ss_treeIconsOpen[type];
    } else {
        tObj.style.display = "none";
        tObj.style.visibility = 'hidden';
		if (bottom == 1) {
			jObj.className = "ss_twPlusBottom";	    // plus_bottom.gif
		} else {
			jObj.className = "ss_twPlus";           // plus.gif
		}
		if (ss_treeIconsClosed[type]) iObj.src = ss_treeIconsClosed[type];
    }
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	
	self.focus();
}

function ss_treeToggleAll(treeName, node, bottom, type) {
    var tObj = self.document.getElementById(treeName + "div" + node);
	if (tObj.style.display == "none") {
		ss_treeToggle(treeName, node, bottom, type)
	}
    var children = tObj.childNodes;
    for (var i = 0; i < children.length; i++) {
    	if (children[i].id && children[i].id.indexOf(treeName + "div") == 0) {
			var nodeRoot = treeName + "div";
			var childnode = children[i].id.substr(nodeRoot.length)
    		if (children[i].style.display == "none") {
    			ss_treeToggle(treeName, childnode, bottom, type)
    		}
    		ss_treeToggleAll(treeName, childnode, bottom, type)
    	}
    }
}

var ss_treeIcons = new ssArray();
var ss_treeIconsClosed = new ssArray();
var ss_treeIconsOpen = new ssArray();
function ssTree_defineBasicIcons(imageBase) {
	// Basic icons
	
	ss_treeIcons['root'] = imageBase + "/trees/root.gif";
	ss_treeIcons['spacer'] = imageBase + "/trees/spacer.gif";
	ss_treeIcons['line'] = imageBase + "/trees/line.gif";
	ss_treeIcons['join'] = imageBase + "/trees/join.gif";
	ss_treeIcons['join_bottom'] = imageBase + "/trees/join_bottom.gif";
	ss_treeIcons['minus'] = imageBase + "/trees/minus.gif";
	ss_treeIcons['minus_bottom'] = imageBase + "/trees/minus_bottom.gif";
	ss_treeIcons['plus'] = imageBase + "/trees/plus.gif";
	ss_treeIcons['plus_bottom'] = imageBase + "/trees/plus_bottom.gif";
	ss_treeIconsClosed['folder'] = imageBase + "/trees/folder.gif";
	ss_treeIconsOpen['folder'] = imageBase + "/trees/folder_open.gif";
	ss_treeIconsClosed['page'] = imageBase + "/trees/page.gif";
	
	// More icons
	
	ss_treeIconsClosed['doc'] = imageBase + "/trees/file_types/doc.gif";
	ss_treeIconsClosed['pdf'] = imageBase + "/trees/file_types/pdf.gif";
	ss_treeIconsClosed['ppt'] = imageBase + "/trees/file_types/ppt.gif";
	ss_treeIconsClosed['rtf'] = imageBase + "/trees/file_types/rtf.gif";
	ss_treeIconsClosed['sxc'] = imageBase + "/trees/file_types/sxc.gif";
	ss_treeIconsClosed['sxi'] = imageBase + "/trees/file_types/sxi.gif";
	ss_treeIconsClosed['sxw'] = imageBase + "/trees/file_types/sxw.gif";
	ss_treeIconsClosed['txt'] = imageBase + "/trees/file_types/txt.gif";
	ss_treeIconsClosed['xls'] = imageBase + "/trees/file_types/xls.gif";
}

//var dragsort = ss_ToolMan.dragsort()
function ss_setSortable(treeName) {
	//Sorting turned off because it doesn't work in IE - pmh
	//dragsort.makeListSortable(document.getElementById(treeName), ss_verticalOnly, ss_saveTreeOrder)
}

function ss_verticalOnly(item) {
	//item.ss_ToolManDragGroup.verticalOnly()
}

function ss_saveTreeOrder(item) {
	//var group = item.ss_ToolManDragGroup
	//group.register('dragend', ss_save)
	//group.setThreshold(4)
	//var list = group.element.parentNode
	//var id = list.getAttribute("id")
	//if (id == null) return
}

function ss_save(item) {
	//alert(item.group.element.id)
}


