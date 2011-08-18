/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
	
	This widget (developed at SiteScape, Inc.) is a modification of dojo HtmlDragAndDrop.
*/

dojo.provide("dojo.dnd.ss_dashboard_drag_and_drop");
dojo.provide("dojo.dnd.ss_dashboard_source");
dojo.provide("dojo.dnd.ss_dashboard_target");
dojo.provide("dojo.dnd.ss_dashboard_object");

dojo.require("dojo.dnd.HtmlDragManager");
dojo.require("dojo.dnd.DragAndDrop");

dojo.lang.extend(dojo.dnd.HtmlDragManager, {
	/**
	* Get the DOM element that is meant to drag.
	* Loop through the parent nodes of the event target until
	* the element is found that was created as a DragSource and 
	* return it.
	*
	* @param event object The event for which to get the drag source.
	*/
	getDragSource: function(e){
		var tn = e.target;
		if(tn === dojo.body()){ return; }
		var ss_dashboard_component_found = 0;
		while((tn)){
			if (tn.className && tn.className.indexOf('ss_dashboard_component') != -1) {
				ss_dashboard_component_found = 1;
				break;
			}
			tn = tn.parentNode;
			if((!tn)||(tn === dojo.body())){ break; }
		}
		tn = e.target;
		var ta = dojo.html.getAttribute(tn, this.dsPrefix);
		while((!ta)&&(tn)){
			tn = tn.parentNode;
			if((!tn)||(tn === dojo.body())){ return; }
			ta = dojo.html.getAttribute(tn, this.dsPrefix);
		}
		//Don't enable drag for ss_dashboard_components if not on the drag handle
		if (e.target.className.indexOf('ss_dashboard_component_dragger') == -1 && 
			ss_dashboard_component_found) return;
		return this.dragSources[ta];
	}
});

dojo.dnd.ss_dashboard_source = function(node, type){
	node = dojo.byId(node);
	this.dragObjects = [];
	this.constrainToContainer = false;
	if(node){
		this.domNode = node;
		this.dragObject = node;
		// register us
		dojo.dnd.DragSource.call(this);
		// set properties that might have been clobbered by the mixin
		this.type = (type)||(this.domNode.nodeName.toLowerCase());
	}
}
dojo.inherits(dojo.dnd.ss_dashboard_source, dojo.dnd.DragSource);
dojo.lang.extend(dojo.dnd.ss_dashboard_source, {
	dragClass: "", // CSS classname(s) applied to node when it is being dragged

	onDragStart: function() {
		var dragObj = new dojo.dnd.ss_dashboard_object(this.dragObject, this.type);
		if(this.dragClass) { dragObj.dragClass = this.dragClass; }

		if (this.constrainToContainer) {
			dragObj.constrainTo(this.constrainingContainer || this.domNode.parentNode);
		}

		ss_enableDashboardDropTargets();

		return dragObj;
	},

	onDragEnd: function() {
		//Clear the "on" className settings
		var targets = ss_getElementsByClass('ss_dashboardDropTarget_.*', null, null)
		for (var i = 0; i < targets.length; i++) {
			targets[i].className = ss_replaceSubStr(targets[i].className, "_on", "_off");
			targets[i].className = ss_replaceSubStr(targets[i].className, "_over", "_off");
		}
		ss_disableDashboardDropTargets();
	},

	setDragHandle: function(node){
		node = dojo.byId(node);
		dojo.dnd.dragManager.unregisterDragSource(this);
		this.domNode = node;
		dojo.dnd.dragManager.registerDragSource(this);
	},

	setDragTarget: function(node){
		this.dragObject = node;
	},

	constrainTo: function(container) {
		this.constrainToContainer = true;
		if (container) {
			this.constrainingContainer = container;
		}
	},
	
	/*
	*
	* see dojo.dnd.DragSource.onSelected
	*/
	onSelected: function() {
		//Make dragging start immediately
		dojo.dnd.dragManager.threshold = 3
		for (var i=0; i<this.dragObjects.length; i++) {
			dojo.dnd.dragManager.selectedSources.push(new dojo.dnd.ss_dashboard_source(this.dragObjects[i]));
		}
	},

	/**
	* Register elements that should be dragged along with
	* the actual DragSource.
	*
	* Example usage:
	* 	var dragSource = new dojo.dnd.ss_dashboard_source(...);
	*	// add a single element
	*	dragSource.addDragObjects(dojo.byId('id1'));
	*	// add multiple elements to drag along
	*	dragSource.addDragObjects(dojo.byId('id2'), dojo.byId('id3'));
	*
	* el A dom node to add to the drag list.
	*/
	addDragObjects: function(/*DOMNode*/ el) {
		for (var i=0; i<arguments.length; i++) {
			this.dragObjects.push(arguments[i]);
		}
	}
});

dojo.dnd.ss_dashboard_object = function(node, type){
	this.domNode = dojo.byId(node);
	this.type = type;
	this.constrainToContainer = false;
	this.dragSource = null;
}
dojo.inherits(dojo.dnd.ss_dashboard_object, dojo.dnd.DragObject);
dojo.lang.extend(dojo.dnd.ss_dashboard_object, {
	dragClass: "",
	opacity: 0.5,
	createIframe: true,		// workaround IE6 bug

	// if true, node will not move in X and/or Y direction
	disableX: false,
	disableY: false,

	createDragNode: function() {
		var node = this.domNode.cloneNode(true);
		if(this.dragClass) { dojo.html.addClass(node, this.dragClass); }
		if(this.opacity < 1) { dojo.html.setOpacity(node, this.opacity); }
		if(node.tagName.toLowerCase() == "tr"){
			// dojo.debug("Dragging table row")
			// Create a table for the cloned row
			var doc = this.domNode.ownerDocument;
			var table = doc.createElement("table");
			var tbody = doc.createElement("tbody");
			tbody.appendChild(node);
			table.appendChild(tbody);

			// Set a fixed width to the cloned TDs
			var domTds = this.domNode.childNodes;
			var cloneTds = node.childNodes;
			for(var i = 0; i < domTds.length; i++){
			    if((cloneTds[i])&&(cloneTds[i].style)){
				    cloneTds[i].style.width = dojo.html.getContentBox(domTds[i]).width + "px";
			    }
			}
			node = table;
		}

		if((dojo.render.html.ie55||dojo.render.html.ie60) && this.createIframe){
			with(node.style) {
				top="0px";
				left="0px";
			}
			var outer = document.createElement("div");
			outer.appendChild(node);
			this.bgIframe = new dojo.html.BackgroundIframe(outer);
			outer.appendChild(this.bgIframe.iframe);
			node = outer;
		}
		node.style.zIndex = 999;
		return node;
	},

	onDragStart: function(e) {
		dojo.html.clearSelection();

		this.scrollOffset = dojo.html.getScroll().offset;
		this.dragStartPosition = dojo.html.getAbsolutePosition(this.domNode, true);
ss_debug('this.dragStartPosition.y = '+this.dragStartPosition.y)

		this.dragOffset = {y: this.dragStartPosition.y - e.pageY,
			x: this.dragStartPosition.x - e.pageX};
ss_debug('this.dragOffset = '+this.dragOffset.x + ', '+this.dragOffset.y)
ss_debug('e.pageY = '+e.pageY)

		//this.dragClone = this.createDragNode();
		this.dragClone = document.createElement("div");
		this.dragClone.setAttribute('align', 'left');
		this.dragClone.className = 'ss_dashboard_dragHandle_clone';
		ss_setOpacity(this.dragClone, .8)
		var titleEles = this.domNode.getElementsByTagName('strong');
		if (titleEles.length >= 1) 
				this.dragClone.appendChild(titleEles[0].cloneNode(true));
		
		dojo.html.setContentBox(this.dragClone, {width: dojo.html.getContentBox(this.domNode).width});

		this.containingBlockPosition = this.domNode.offsetParent ? 
			dojo.html.getAbsolutePosition(this.domNode.offsetParent) : {x:0, y:0};

		if (this.constrainToContainer) {
			this.constraints = this.getConstraints();
		}

		// set up for dragging
		with(this.dragClone.style){
			position = "absolute";
			top = this.dragOffset.y + e.pageY + "px";
			left = this.dragOffset.x + e.pageX + "px";
		}

		document.body.appendChild(this.dragClone);

		dojo.event.topic.publish('dragStart', { source: this } );
	},

	/** Return min/max x/y (relative to document.body) for this object) **/
	getConstraints: function() {
		if (this.constrainingContainer.nodeName.toLowerCase() == 'body') {
			var viewport = dojo.html.getViewport();
			var width = viewport.width;
			var height = viewport.height;
			var x = 0;
			var y = 0;
		} else {
			var content = dojo.html.getContentBox(this.constrainingContainer);
			width = content.width;
			height = content.height;
			x =
				this.containingBlockPosition.x +
				dojo.html.getPixelValue(this.constrainingContainer, "padding-left", true) +
				dojo.html.getBorderExtent(this.constrainingContainer, "left");
			y =
				this.containingBlockPosition.y +
				dojo.html.getPixelValue(this.constrainingContainer, "padding-top", true) +
				dojo.html.getBorderExtent(this.constrainingContainer, "top");
		}

		var mb = dojo.html.getMarginBox(this.domNode);
		return {
			minX: x,
			minY: y,
			maxX: x + width - mb.width,
			maxY: y + height - mb.height
		}
	},

	updateDragOffset: function() {
		var scroll = dojo.html.getScroll().offset;
		if(scroll.y != this.scrollOffset.y) {
			var diff = scroll.y - this.scrollOffset.y;
			this.dragOffset.y += diff;
			this.scrollOffset.y = scroll.y;
		}
		if(scroll.x != this.scrollOffset.x) {
			var diff = scroll.x - this.scrollOffset.x;
			this.dragOffset.x += diff;
			this.scrollOffset.x = scroll.x;
		}
	},

	/** Moves the node to follow the mouse */
	onDragMove: function(e){
		this.updateDragOffset();
		var x = this.dragOffset.x + e.pageX;
		var y = this.dragOffset.y + e.pageY;

		if (this.constrainToContainer) {
			if (x < this.constraints.minX) { x = this.constraints.minX; }
			if (y < this.constraints.minY) { y = this.constraints.minY; }
			if (x > this.constraints.maxX) { x = this.constraints.maxX; }
			if (y > this.constraints.maxY) { y = this.constraints.maxY; }
		}

		this.setAbsolutePosition(x, y);

		dojo.event.topic.publish('dragMove', { source: this } );
	},

	/**
	 * Set the position of the drag clone.  (x,y) is relative to <body>.
	 */
	setAbsolutePosition: function(x, y){
		// The drag clone is attached to document.body so this is trivial
		if(!this.disableY) { this.dragClone.style.top = y + "px"; }
		if(!this.disableX) { this.dragClone.style.left = x + "px"; }
	},


	/**
	 * If the drag operation returned a success we reomve the clone of
	 * ourself from the original position. If the drag operation returned
	 * failure we slide back over to where we came from and end the operation
	 * with a little grace.
	 */
	onDragEnd: function(e){
		switch(e.dragStatus){

			case "dropSuccess":
				this.dragClone.parentNode.removeChild(this.dragClone);
				this.dragClone = null;
				break;

			case "dropFailure": // slide back to the start
				var startCoords = dojo.html.getAbsolutePosition(this.dragClone, true);
				// offset the end so the effect can be seen
				var endCoords = { left: this.dragStartPosition.x + 1,
					top: this.dragStartPosition.y + 1};

				// animate
				var anim = dojo.lfx.slideTo(this.dragClone, endCoords, 500, dojo.lfx.easeOut);
				var dragObject = this;
				dojo.event.connect(anim, "onEnd", function (e) {
					// pause for a second (not literally) and disappear
					dojo.lang.setTimeout(function() {
							dragObject.dragClone.parentNode.removeChild(dragObject.dragClone);
							// Allow drag clone to be gc'ed
							dragObject.dragClone = null;
						},
						200);
				});
				anim.play();
				break;
		}

		// shortly the browser will fire an onClick() event,
		// but since this was really a drag, just squelch it
		dojo.event.connect(this.domNode, "onclick", this, "squelchOnClick");

		dojo.event.topic.publish('dragEnd', { source: this } );
	},

	squelchOnClick: function(e){
		// squelch this onClick() event because it's the result of a drag (it's not a real click)
		e.preventDefault();

		// but if a real click comes along, allow it
		dojo.event.disconnect(this.domNode, "onclick", this, "squelchOnClick");
	},

	constrainTo: function(container) {
		this.constrainToContainer=true;
		if (container) {
			this.constrainingContainer = container;
		} else {
			this.constrainingContainer = this.domNode.parentNode;
		}
	}
});

dojo.dnd.ss_dashboard_target = function(node, types){
	if (arguments.length == 0) { return; }
	this.domNode = dojo.byId(node);
	dojo.dnd.DropTarget.call(this);
	if(types && dojo.lang.isString(types)) {
		types = [types];
	}
	this.acceptedTypes = types || [];
}

dojo.inherits(dojo.dnd.ss_dashboard_target, dojo.dnd.DropTarget);
dojo.lang.extend(dojo.dnd.ss_dashboard_target, {
	onDragOver: function(e) {
		if(!this.accepts(e.dragObjects)){ return false; }

		var height = parseInt(dojo.html.getContentBox(this.domNode).height)+"px";
		this.domNode.className = "ss_dashboardDropTarget_over";
		this.domNode.style.height = height;

		// cache the positions of the child nodes
		this.childBoxes = [];
		for (var i = 0, child; i < this.domNode.childNodes.length; i++) {
			child = this.domNode.childNodes[i];
			if (child.nodeType != dojo.html.ELEMENT_NODE) { continue; }
			var pos = dojo.html.getAbsolutePosition(child, true);
			var inner = dojo.html.getBorderBox(child);
			this.childBoxes.push({top: pos.y, bottom: pos.y+inner.height,
				left: pos.x, right: pos.x+inner.width, height: inner.height, 
				width: inner.width, node: child});
		}

		// TODO: use dummy node

		return true;
	},

	onDragOut: function(e) {
		this.domNode.className = "ss_dashboardDropTarget";

		if(this.dropIndicator) {
			this.dropIndicator.parentNode.removeChild(this.dropIndicator);
			delete this.dropIndicator;
		}
	},

	onDrop: function(e) {
		this.onDragOut(e);
		var sourceNode = e.dragObject.domNode;
		var targetNode = null;
		var dashboardTable = document.getElementById('ss_dashboardTable');
		var targets = ss_getElementsByClass('ss_dashboardProtoDropTarget', dashboardTable, 'div')
		for (var i = 0; i < targets.length; i++) {
			if (ss_dashboardClones[i] == this.domNode) {
				targetNode = targets[i];
				break;
			}
		}
		if (targetNode != null) {
			var sourceParentNode = sourceNode.parentNode;
			var targetParentNode = targetNode.parentNode;

			//ss_debug('sourceParentNode = '+sourceParentNode.id)
			var sourceDropSpotDiv = null;
			var sourceChildNodes = sourceParentNode.childNodes;
			var divNodes = new Array();
			for (var i = 0; i < sourceParentNode.childNodes.length; i++) {
				var childNode = sourceParentNode.childNodes.item(i);
				if (childNode.tagName && childNode.tagName.toLowerCase() == 'div') 
						divNodes[divNodes.length] = childNode;
			}
			
			for (var i = 0; i < divNodes.length; i++) {
				if (divNodes[i] == sourceNode && i < divNodes.length-1) {
					sourceDropSpotDiv = divNodes[i+1];
					var classString = 'ss_dashboardProtoDropTarget'
					if (sourceDropSpotDiv.tagName.toLowerCase() == 'div' && 
							sourceDropSpotDiv.className.substr(0, classString.length) == classString) {
						if (sourceDropSpotDiv != targetNode) {
							dojo.html.insertAfter(sourceDropSpotDiv, targetNode);
						}
						break;
					}
				}
			}
			if (sourceDropSpotDiv != targetNode) {
				var startCoords = dojo.html.getAbsolutePosition(sourceNode, true)
				dojo.html.insertAfter(sourceNode, targetNode);
				if (ss_dashboardSliderObj != null) ss_clearDashboardSlider();
				ss_dashboardSliderObjEndCoords = dojo.html.getAbsolutePosition(sourceNode, true)
				ss_dashboardSliderObj = sourceNode.cloneNode(true);
				ss_dashboardSliderObj.style.position = "absolute";
				ss_dashboardSliderObj.style.left = parseInt(startCoords.x) + "px"
				ss_dashboardSliderObj.style.top = parseInt(startCoords.y) + "px"
				dojo.html.setContentBox(ss_dashboardSliderObj, {width: dojo.html.getContentBox(sourceNode).width});
				var bodyObj = document.getElementsByTagName("body").item(0);
				bodyObj.appendChild(ss_dashboardSliderObj);
				ss_dashboardSliderTargetObj = sourceNode;
				dojo.html.setOpacity(ss_dashboardSliderTargetObj, .3);
				var top = ss_dashboardSliderObjEndCoords.y;
				var left = ss_dashboardSliderObjEndCoords.x;
				dojo.lfx.html.slideTo(ss_dashboardSliderObj, {top: top, left: left}, 400, null, ss_clearDashboardSlider).play();

				//Signal that the layout changed
				if (ssf_onLayoutChange) setTimeout('ssf_onLayoutChange();', 100);
				
				setTimeout('ss_savePenletLayout();', 200);
			}

			ss_disableDashboardDropTargets();
		}
		return true;

	},

	_getNodeUnderMouse: function(e){
		// find the child
		for (var i = 0, child; i < this.childBoxes.length; i++) {
			with (this.childBoxes[i]) {
				if (e.pageX >= left && e.pageX <= right &&
					e.pageY >= top && e.pageY <= bottom) { return i; }
			}
		}

		return -1;
	},

	createDropIndicator: function() {
		this.dropIndicator = document.createElement("div");
		with (this.dropIndicator.style) {
			position = "absolute";
			zIndex = 999;
			borderTopWidth = "1px";
			borderTopColor = "black";
			borderTopStyle = "solid";
			width = dojo.html.getBorderBox(this.domNode).width + "px";
			left = dojo.html.getAbsolutePosition(this.domNode, true).x + "px";
		}
	},

	onDragMove: function(e, dragObjects){
		var i = this._getNodeUnderMouse(e);

		if(!this.dropIndicator){
			this.createDropIndicator();
		}

		if(i < 0) {
			if(this.childBoxes.length) {
				var before = (dojo.html.gravity(this.childBoxes[0].node, e) & dojo.html.gravity.NORTH);
			} else {
				var before = true;
			}
		} else {
			var child = this.childBoxes[i];
			var before = (dojo.html.gravity(child.node, e) & dojo.html.gravity.NORTH);
		}
		this.placeIndicator(e, dragObjects, i, before);

		if(!dojo.html.hasParent(this.dropIndicator)) {
			document.body.appendChild(this.dropIndicator);
		}
	},

	/**
	 * Position the horizontal line that indicates "insert between these two items"
	 */
	placeIndicator: function(e, dragObjects, boxIndex, before) {
		with(this.dropIndicator.style){
			if (boxIndex < 0) {
				if (this.childBoxes.length) {
					top = (before ? this.childBoxes[0].top
						: this.childBoxes[this.childBoxes.length - 1].bottom) + "px";
				} else {
					top = dojo.html.getAbsolutePosition(this.domNode, true).y + "px";
				}
			} else {
				var child = this.childBoxes[boxIndex];
				top = (before ? child.top : child.bottom) + "px";
			}
		}
	},

	insert: function(e, refNode, position) {
		var node = e.dragObject.domNode;

		if(position == "before") {
			return dojo.html.insertBefore(node, refNode);
		} else if(position == "after") {
			return dojo.html.insertAfter(node, refNode);
		} else if(position == "append") {
			refNode.appendChild(node);
			return true;
		}

		return false;
	}
});
