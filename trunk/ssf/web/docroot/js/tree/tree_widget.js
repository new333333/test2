//Routines to display an expandable/contractable tree
//
//Start by defining a new tree object - new Tree(treeId, className)
//Next, define the nodes in the tree - Tree.defineNode(nodeId, parentId, text, image, url)
//Finally, create the tree - Tree.create(openNodes)

if (!Tree_widget_treeList) var Tree_widget_treeList = new Array();

function Tree(treeId, className, imageBase) {
	if (!Tree_widget_treeList[treeId] || Tree_widget_treeList[treeId] == null) {
        Tree_widget_treeList[treeId] = this;
    }
    this.treeId = treeId;
	this.className = className;
	this.imageBase = imageBase;
	this.nodeList = new Array();
	this.openNodes = new Array();
	this.orderedNodeList = new Array();
	this.orderedNodeListCount = 0;

    this.defineNode = Tree_defineNode;
    this.create = Tree_create;
    this.hasChildNode = Tree_hasChildNode;
    this.isNodeOpen = Tree_isNodeOpen;
    this.setOpenNodes = Tree_setOpenNodes;
    this.toggle = Tree_toggle;
    this.toggleAll = Tree_toggleAll;
    this.openNode = Tree_openNode;
    this.openAll = Tree_openAll;
    this.closeNode = Tree_closeNode;
    this.closeAll = Tree_closeAll;
    this.defineIcons = Tree_defineIcons;
    this.getNode = Tree_getNode;
    this.getDescendantsWithChildren = Tree_getDescendantsWithChildren;
    this.divWrite = Tree_divWrite;
    Tree_defineBasicIcons(this);
}

function Tree_defineNode(nodeId, parentId, image, imageOpen) {
	if (!this.nodeList[nodeId] || this.nodeList[nodeId] == null) {
        this.nodeList[nodeId] = new Array();
    }
	var n = this.nodeList[nodeId];
	n.nodeId = nodeId;
	n.parentId = parentId;
	n.childCount = 0;
	if (parentId != 0 && parentId != "") {
		if (!this.nodeList[parentId].children || this.nodeList[parentId].children == null) {
	        this.nodeList[parentId].children = new Array();
		}
		var c = this.nodeList[parentId].childCount++;
		this.nodeList[parentId].children[c] = nodeId;
	}
	n.imageClosed = this.imageBase + image;
	n.imageOpen = this.imageBase + imageOpen;
}

function Tree_getNode(nodeId) {
	return this.nodeList[nodeId];
}

function Tree_defineIcons(icons, iconsClosed, iconsOpen) {
	this.icons = icons
	this.iconsClosed = iconsClosed
	this.iconsOpen = iconsOpen
}

function Tree_create(openNode) {
	if (this.nodeList.length > 0) {
		if (openNode != 0 || openNode != null) {
			this.setOpenNodes(openNode);
		}
	}
}

function Tree_divWrite(text) {
	//document.write(text);
}

function Tree_hasChildNode(parentNode) {
	if (parentNode == "" || this.nodeList[parentNode] == null) {return false}
	var pn = this.nodeList[parentNode];
	if (pn.childCount > 0) {
		return true;
	} else {
		return false;
	}
}

function Tree_isNodeOpen(node) {
	for (i = 0; i < this.openNodes.length; i++) {
		if (this.openNodes[i] == node) {
			return true;
		}
	}

	return false;
}

function Tree_setOpenNodes(openNode) {
	for (var i in this.nodeList) {
		var n = this.nodeList[i];

		if (n.nodeId && n.nodeId == openNode) {
			this.openNode.push(n.nodeId);
			this.setOpenNodes(n.parentId);
		}
	}
}

function Tree_toggle(treeId, node, bottom) {
	var divEl = document.getElementById(treeId + "div" + node);
	var joinEl	= document.getElementById(treeId + "join" + node);
	var iconEl = document.getElementById(treeId + "icon" + node);
	var tree = Tree_widget_treeList[treeId];
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

	self.focus();
}

var dblClickOpenAll = 1;
function Tree_toggleAll(treeId, node, bottom) {
	var tree = Tree_widget_treeList[treeId];
	var n = tree.getNode(node);
	var divEl = document.getElementById(treeId + "div" + node);

	//Build a list of nodes
	tree.orderedNodeListCount = tree.orderedNodeList.length;
	tree.orderedNodeList = new Array();
	tree.orderedNodeList = tree.getDescendantsWithChildren(treeId, node, tree.orderedNodeList)
	tree.orderedNodeListCount = 0;
	
	//See if double clicking implies opening all nodes, or toggling them
	if (dblClickOpenAll) {
		//Open all nodes
		divEl.style.display = "none";
		Tree_openNode(treeId, node);
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
	var tree = Tree_widget_treeList[treeId];
	for (var i = 0; i < 5; i++) {
		if (tree.orderedNodeListCount < tree.orderedNodeList.length) {
			tree.openNode(treeId, tree.orderedNodeList[tree.orderedNodeListCount])
			tree.orderedNodeListCount++
		} else {
			break
		}
	}
	if (tree.orderedNodeListCount < tree.orderedNodeList.length) {
		setTimeout('delayedOpenNodes("'+treeId+'")', 1);
	}
}

function delayedCloseNodes(treeId) {
	var tree = Tree_widget_treeList[treeId];
	for (var i = 0; i < 5; i++) {
		if (tree.orderedNodeListCount < tree.orderedNodeList.length) {
			tree.closeNode(treeId, tree.orderedNodeList[tree.orderedNodeListCount])
			tree.orderedNodeListCount++
		} else {
			break
		}
	}
	if (tree.orderedNodeListCount < tree.orderedNodeList.length) {
		setTimeout('delayedCloseNodes("'+treeId+'")', 1);
	}
}

function Tree_getDescendantsWithChildren(treeId, node, orderedNodeList) {
	var tree = Tree_widget_treeList[treeId];
	var n = tree.getNode(node);
	if (tree.hasChildNode(node)) {
		for (var i=0; i < n.children.length; i++) {
			if (tree.hasChildNode(n.children[i])) {
				orderedNodeList[orderedNodeList.length] = n.children[i];
				orderedNodeList = tree.getDescendantsWithChildren(treeId, n.children[i], orderedNodeList)
			}
		}
	}
	return orderedNodeList
}

function Tree_openNode(treeId, node) {
	var tree = Tree_widget_treeList[treeId];
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
		}
	}
}

function Tree_closeNode(treeId, node) {
	var tree = Tree_widget_treeList[treeId];
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
		}
	}
}

function Tree_openAll(treeId, node) {
	var divEl = document.getElementById(treeId + "div" + node);
	var joinEl	= document.getElementById(treeId + "join" + node);
	var iconEl = document.getElementById(treeId + "icon" + node);
	var tree = Tree_widget_treeList[treeId];
	var n = tree.getNode(node);
	if (!divEl.style || !divEl.style.display || divEl.style.display == "none") {
		if (tree.hasChildNode(node)) {
			joinEl.src = tree.icons['minus'];           // minus.gif
		} else {
			joinEl.src = tree.icons['minus_bottom'];	// minus_bottom.gif
		}
		iconEl.src = n.imageOpen;	                    // folder_open.gif
		divEl.style.display = "";
	}
	
	if (tree.hasChildNode(node)) {
		setTimeout('delayedOpenAllNode("'+treeId+'", "'+ node+'")', 1);
	}
}

function delayedOpenAllNode(treeId, node) {
	var tree = Tree_widget_treeList[treeId];
	for (var i in tree.nodeList[node].children) {
		var cn = tree.nodeList[node].children[i];
		if (tree.hasChildNode(cn)) {
			tree.openAll(treeId, cn);
		}
	}
}

function Tree_closeAll(treeId, node) {
	var divEl = document.getElementById(treeId + "div" + node);
	var joinEl	= document.getElementById(treeId + "join" + node);
	var iconEl = document.getElementById(treeId + "icon" + node);
	var tree = Tree_widget_treeList[treeId];
	var n = tree.getNode(node);

	if (divEl.style.display != "none") {
		if (tree.hasChildNode(node)) {
			joinEl.src = tree.icons['plus'];            // plus.gif
		} else {
			joinEl.src = tree.icons['plus_bottom'];	    // plus_bottom.gif
		}
		iconEl.src = n.imageClosed;		                // folder.gif
		divEl.style.display = "none";
	}
	
	if (tree.hasChildNode(node)) {
		for (var i = 0; i < tree.nodeList[node].childCount; i++) {
			var cn = tree.nodeList[node].children[i];
			if (tree.hasChildNode(cn)) {
				setTimeout('Tree_widget_treeList\["'+treeId+'"\].closeAll("'+treeId+'", "'+ cn+'")', 1);
				//tree.closeAll(treeId, cn);
			}
		}
	}
}

function Tree_defineBasicIcons(tree) {
	var treeIcons = new Array();
	var treeIconsClosed = new Array();
	var treeIconsOpen = new Array();
	
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

var dragsort = ss_ToolMan.dragsort()
function ss_setSortable(treeName) {
	//Sorting turned off because it doesn't work in IE - pmh
	//dragsort.makeListSortable(document.getElementById(treeName), ss_verticalOnly, ss_saveTreeOrder)
}

function ss_verticalOnly(item) {
	item.ss_ToolManDragGroup.verticalOnly()
}

function ss_saveTreeOrder(item) {
	var group = item.ss_ToolManDragGroup
	group.register('dragend', ss_save)
	group.setThreshold(4)
	var list = group.element.parentNode
	var id = list.getAttribute("id")
	if (id == null) return
}

function ss_save(item) {
	alert(item.group.element.id)
}


