//Routines to display an expandable/contractable tree
//
//Start by defining a new tree object - new ssTree(treeId, className)
//Next, define the nodes in the tree - ssTree.defineNode(nodeId, parentId, text, image, url)
//Finally, create the tree - ssTree.create(openNodes)

if (!ssTree_widget_treeList) var ssTree_widget_treeList = new ssArray();

function ssTree(treeId, className, imageBase) {
	if (!ssTree_widget_treeList[treeId] || ssTree_widget_treeList[treeId] == null) {
        ssTree_widget_treeList[treeId] = this;
    }
    this.treeId = treeId;
	this.className = className;
	this.imageBase = imageBase;
	this.nodeList = new ssArray();
	this.openNodes = new ssArray();
	this.orderedNodeList = new ssArray();
	this.orderedNodeListCount = 0;

    this.defineNode = ssTree_defineNode;
    this.create = ssTree_create;
    this.hasChildNode = ssTree_hasChildNode;
    this.isNodeOpen = ssTree_isNodeOpen;
    this.setOpenNodes = ssTree_setOpenNodes;
    this.toggle = ssTree_toggle;
    this.toggleAll = ssTree_toggleAll;
    this.openNode = ssTree_openNode;
    this.openAll = ssTree_openAll;
    this.closeNode = ssTree_closeNode;
    this.closeAll = ssTree_closeAll;
    this.defineIcons = ssTree_defineIcons;
    this.getNode = ssTree_getNode;
    this.getDescendantsWithChildren = ssTree_getDescendantsWithChildren;
    this.divWrite = ssTree_divWrite;
    ssTree_defineBasicIcons(this);
}

function ssTree_defineNode(nodeId, parentId, image, imageOpen) {
	if (!this.nodeList[nodeId] || this.nodeList[nodeId] == null) {
        this.nodeList[nodeId] = new ssArray();
    }
	var n = this.nodeList[nodeId];
	n.nodeId = nodeId;
	n.parentId = parentId;
	n.childCount = 0;
	if (parentId != 0 && parentId != "") {
		if (!this.nodeList[parentId].children || this.nodeList[parentId].children == null) {
	        this.nodeList[parentId].children = new ssArray();
		}
		var c = this.nodeList[parentId].childCount++;
		this.nodeList[parentId].children[c] = nodeId;
	}
	n.imageClosed = this.imageBase + image;
	n.imageOpen = this.imageBase + imageOpen;
}

function ssTree_getNode(nodeId) {
	return this.nodeList[nodeId];
}

function ssTree_defineIcons(icons, iconsClosed, iconsOpen) {
	this.icons = icons
	this.iconsClosed = iconsClosed
	this.iconsOpen = iconsOpen
}

function ssTree_create(openNode) {
	if (ssArrayLength(this.nodeList) > 0) {
		if (openNode != 0 || openNode != null) {
			this.setOpenNodes(openNode);
		}
	}
}

function ssTree_divWrite(text) {
	//document.write(text);
}

function ssTree_hasChildNode(parentNode) {
	if (parentNode == "" || this.nodeList[parentNode] == null) {return false}
	var pn = this.nodeList[parentNode];
	if (pn.childCount > 0) {
		return true;
	} else {
		return false;
	}
}

function ssTree_isNodeOpen(node) {
	for (i = 0; i < ssArrayLength(this.openNodes); i++) {
		if (this.openNodes[i] == node) {
			return true;
		}
	}

	return false;
}

function ssTree_setOpenNodes(openNode) {
	for (var i in this.nodeList) {
		var n = this.nodeList[i];

		if (n.nodeId && n.nodeId == openNode) {
			this.openNode.push(n.nodeId);
			this.setOpenNodes(n.parentId);
		}
	}
}

function ssTree_toggle(treeId, node, bottom) {
	var divEl = document.getElementById(treeId + "div" + node);
	var joinEl	= document.getElementById(treeId + "join" + node);
	var iconEl = document.getElementById(treeId + "icon" + node);
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);

	if (divEl.style.display == "none") {
		if (bottom == 1) {
			joinEl.src = this.icons['minus_bottom'];	// minus_bottom.gif
		} else {
			joinEl.src = this.icons['minus'];           // minus.gif
		}

		iconEl.src = n.imageOpen;	                    // folder_open.gif
		divEl.style.display = "";
	} else {
		if (bottom == 1) {
			joinEl.src = this.icons['plus_bottom'];	    // plus_bottom.gif
		} else {
			joinEl.src = this.icons['plus'];            // plus.gif
		}

		iconEl.src = n.imageClosed;		                // folder.gif
		divEl.style.display = "none";
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	
	self.focus();
}

var dblClickOpenAll = 1;
function ssTree_toggleAll(treeId, node, bottom) {
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);
	var divEl = document.getElementById(treeId + "div" + node);

	//Build a list of nodes
	tree.orderedNodeList = new ssArray();
	tree.orderedNodeList = tree.getDescendantsWithChildren(treeId, node, tree.orderedNodeList)
	tree.orderedNodeListCount = 0;
	
	//See if double clicking implies opening all nodes, or toggling them
	if (dblClickOpenAll) {
		//Open all nodes
		divEl.style.display = "none";
		ssTree_openNode(treeId, node);
		setTimeout('delayedOpenNodes("'+treeId+'")', 50);
	} else {
		//Toggle the nodes
		if (divEl.style.display == "none") {
			delayedCloseNodes(treeId);
		} else {
			delayedOpenNodes(treeId);
		}
	}
}

function delayedOpenNodes(treeId) {
	var tree = ssTree_widget_treeList[treeId];
	for (var i = 0; i < 5; i++) {
		if (tree.orderedNodeListCount < ssArrayLength(tree.orderedNodeList)) {
			tree.openNode(treeId, tree.orderedNodeList[tree.orderedNodeListCount])
			tree.orderedNodeListCount++
		} else {
			break
		}
	}
	if (tree.orderedNodeListCount < ssArrayLength(tree.orderedNodeList)) {
		setTimeout('delayedOpenNodes("'+treeId+'")', 1);
	}
}

function delayedCloseNodes(treeId) {
	var tree = ssTree_widget_treeList[treeId];
	for (var i = 0; i < 5; i++) {
		if (tree.orderedNodeListCount < ssArrayLength(tree.orderedNodeList)) {
			tree.closeNode(treeId, tree.orderedNodeList[tree.orderedNodeListCount])
			tree.orderedNodeListCount++
		} else {
			break
		}
	}
	if (tree.orderedNodeListCount < ssArrayLength(tree.orderedNodeList)) {
		setTimeout('delayedCloseNodes("'+treeId+'")', 1);
	}
}

function ssTree_getDescendantsWithChildren(treeId, node, orderedNodeList) {
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);
	if (tree.hasChildNode(node)) {
		for (var i=0; i < ssArrayLength(n.children); i++) {
			if (tree.hasChildNode(n.children[i])) {
				orderedNodeList[ssArrayLength(orderedNodeList)] = n.children[i];
				orderedNodeList = tree.getDescendantsWithChildren(treeId, n.children[i], orderedNodeList)
			}
		}
	}
	return orderedNodeList
}

function ssTree_openNode(treeId, node) {
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);
	if (tree.hasChildNode(node)) {
		var divEl = document.getElementById(treeId + "div" + node);
		var joinEl	= document.getElementById(treeId + "join" + node);
		var iconEl = document.getElementById(treeId + "icon" + node);
		if (!divEl.style || !divEl.style.display || divEl.style.display == "none") {
			if (tree.hasChildNode(node)) {
				joinEl.src = tree.icons['minus'];           // minus.gif
			} else {
				joinEl.src = tree.icons['minus_bottom'];	// minus_bottom.gif
			}
			iconEl.src = n.imageOpen;	                    // folder_open.gif
			divEl.style.display = "";

			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
}

function ssTree_closeNode(treeId, node) {
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);
	if (tree.hasChildNode(node)) {
		var divEl = document.getElementById(treeId + "div" + node);
		var joinEl	= document.getElementById(treeId + "join" + node);
		var iconEl = document.getElementById(treeId + "icon" + node);
	
		if (divEl.style.display != "none") {
			if (tree.hasChildNode(node)) {
				joinEl.src = tree.icons['plus'];            // plus.gif
			} else {
				joinEl.src = tree.icons['plus_bottom'];	    // plus_bottom.gif
			}
			iconEl.src = n.imageClosed;		                // folder.gif
			divEl.style.display = "none";

			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
}

function ssTree_openAll(treeId, node) {
	var divEl = document.getElementById(treeId + "div" + node);
	var joinEl	= document.getElementById(treeId + "join" + node);
	var iconEl = document.getElementById(treeId + "icon" + node);
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);
	if (!divEl.style || !divEl.style.display || divEl.style.display == "none") {
		if (tree.hasChildNode(node)) {
			joinEl.src = tree.icons['minus'];           // minus.gif
		} else {
			joinEl.src = tree.icons['minus_bottom'];	// minus_bottom.gif
		}
		iconEl.src = n.imageOpen;	                    // folder_open.gif
		divEl.style.display = "";

		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
	}
	
	if (tree.hasChildNode(node)) {
		setTimeout('delayedOpenAllNode("'+treeId+'", "'+ node+'")', 1);
	}
}

function delayedOpenAllNode(treeId, node) {
	var tree = ssTree_widget_treeList[treeId];
	for (var i in tree.nodeList[node].children) {
		var cn = tree.nodeList[node].children[i];
		if (tree.hasChildNode(cn)) {
			tree.openAll(treeId, cn);
		}
	}
}

function ssTree_closeAll(treeId, node) {
	var divEl = document.getElementById(treeId + "div" + node);
	var joinEl	= document.getElementById(treeId + "join" + node);
	var iconEl = document.getElementById(treeId + "icon" + node);
	var tree = ssTree_widget_treeList[treeId];
	var n = tree.getNode(node);

	if (divEl.style.display != "none") {
		if (tree.hasChildNode(node)) {
			joinEl.src = tree.icons['plus'];            // plus.gif
		} else {
			joinEl.src = tree.icons['plus_bottom'];	    // plus_bottom.gif
		}
		iconEl.src = n.imageClosed;		                // folder.gif
		divEl.style.display = "none";

		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
	}
	
	if (tree.hasChildNode(node)) {
		for (var i = 0; i < tree.nodeList[node].childCount; i++) {
			var cn = tree.nodeList[node].children[i];
			if (tree.hasChildNode(cn)) {
				setTimeout('ssTree_widget_treeList\["'+treeId+'"\].closeAll("'+treeId+'", "'+ cn+'")', 1);
				//tree.closeAll(treeId, cn);
			}
		}
	}
}

function ssTree_defineBasicIcons(tree) {
	var treeIcons = new ssArray();
	var treeIconsClosed = new ssArray();
	var treeIconsOpen = new ssArray();
	
	// Basic icons
	
	treeIcons['root'] = tree.imageBase + "/trees/root.gif";
	treeIcons['spacer'] = tree.imageBase + "/trees/spacer.gif";
	treeIcons['line'] = tree.imageBase + "/trees/line.gif";
	treeIcons['join'] = tree.imageBase + "/trees/join.gif";
	treeIcons['join_bottom'] = tree.imageBase + "/trees/join_bottom.gif";
	treeIcons['minus'] = tree.imageBase + "/trees/minus.gif";
	treeIcons['minus_bottom'] = tree.imageBase + "/trees/minus_bottom.gif";
	treeIcons['plus'] = tree.imageBase + "/trees/plus.gif";
	treeIcons['plus_bottom'] = tree.imageBase + "/trees/plus_bottom.gif";
	treeIconsClosed['folder'] = tree.imageBase + "/trees/folder.gif";
	treeIconsOpen['folder'] = tree.imageBase + "/trees/folder_open.gif";
	treeIconsClosed['page'] = tree.imageBase + "/trees/page.gif";
	
	// More icons
	
	treeIconsClosed['doc'] = tree.imageBase + "/trees/file_types/doc.gif";
	treeIconsClosed['pdf'] = tree.imageBase + "/trees/file_types/pdf.gif";
	treeIconsClosed['ppt'] = tree.imageBase + "/trees/file_types/ppt.gif";
	treeIconsClosed['rtf'] = tree.imageBase + "/trees/file_types/rtf.gif";
	treeIconsClosed['sxc'] = tree.imageBase + "/trees/file_types/sxc.gif";
	treeIconsClosed['sxi'] = tree.imageBase + "/trees/file_types/sxi.gif";
	treeIconsClosed['sxw'] = tree.imageBase + "/trees/file_types/sxw.gif";
	treeIconsClosed['txt'] = tree.imageBase + "/trees/file_types/txt.gif";
	treeIconsClosed['xls'] = tree.imageBase + "/trees/file_types/xls.gif";

	tree.defineIcons(treeIcons, treeIconsClosed, treeIconsOpen)
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


