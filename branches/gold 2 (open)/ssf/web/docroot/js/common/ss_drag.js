/*
 * drag.js - click & drag DOM elements
 *
 * originally based on Youngpup's dom-drag.js, www.youngpup.net
 */

/**********************************************************
 Further modified from the example by Tim Taylor
 http://tool-man.org/examples/sorting.html
 
 Changed onMouseMove where it calls group.onDrag and then
 adjusts the offset for changes to the DOM.  If the item
 being moved changed parents it would be off so changed to
 get the absolute offset (recursive northwestOffset).
 
 **********************************************************/

var ss_Drag = {
	BIG_Z_INDEX : 10000,
	group : null,
	isDragging : false,

	makeDraggable : function(group) {
		group.handle = group;
		group.handle.group = group;

		group.minX = null;
		group.minY = null;
		group.maxX = null;
		group.maxY = null;
		group.threshold = 10;
		group.thresholdY = 10;
		group.thresholdX = 10;

		group.onDragStart = new Function();
		group.onDragEnd = new Function();
		group.onDrag = new Function();
		
		// TODO: use element.prototype.myFunc
		group.setDragHandle = ss_Drag.setDragHandle;
		group.setDragThreshold = ss_Drag.setDragThreshold;
		group.setDragThresholdX = ss_Drag.setDragThresholdX;
		group.setDragThresholdY = ss_Drag.setDragThresholdY;
		group.constrain = ss_Drag.constrain;
		group.constrainVertical = ss_Drag.constrainVertical;
		group.constrainHorizontal = ss_Drag.constrainHorizontal;

		group.onmousedown = ss_Drag.onMouseDown;
	},

	constrainVertical : function() {
		var nwOffset = ss_Coordinates.northwestOffset(this, true);
		this.minX = nwOffset.x;
		this.maxX = nwOffset.x;
	},

	constrainHorizontal : function() {
		var nwOffset = ss_Coordinates.northwestOffset(this, true);
		this.minY = nwOffset.y;
		this.maxY = nwOffset.y;
	},

	constrain : function(nwPosition, sePosition) {
		this.minX = nwPosition.x;
		this.minY = nwPosition.y;
		this.maxX = sePosition.x;
		this.maxY = sePosition.y;
	},

	setDragHandle : function(handle) {
		if (handle && handle != null) 
			this.handle = handle;
		else
			this.handle = this;

		this.handle.group = this;
		this.onmousedown = null;
		this.handle.onmousedown = ss_Drag.onMouseDown;
	},

	setDragThreshold : function(threshold) {
		if (isNaN(parseInt(threshold))) return;

		this.threshold = threshold;
	},

	setDragThresholdX : function(threshold) {
		if (isNaN(parseInt(threshold))) return;

		this.thresholdX = threshold;
	},

	setDragThresholdY : function(threshold) {
		if (isNaN(parseInt(threshold))) return;

		this.thresholdY = threshold;
	},

	onMouseDown : function(event) {
		event = ss_Drag.fixEvent(event);
		ss_Drag.group = this.group;

		var group = this.group;
		var mouse = event.windowCoordinate;
		var nwOffset = ss_Coordinates.northwestOffset(group, true);
		var nwPosition = ss_Coordinates.northwestPosition(group);
		var sePosition = ss_Coordinates.southeastPosition(group);
		var seOffset = ss_Coordinates.southeastOffset(group, true);

		group.originalOpacity = group.style.opacity;
		group.originalZIndex = group.style.zIndex;
		group.initialWindowCoordinate = mouse;
		// TODO: need a better name, but don't yet understand how it
		// participates in the magic while dragging 
		group.dragCoordinate = mouse;

		ss_Drag.showStatus(mouse, nwPosition, sePosition, nwOffset, seOffset);

		//group.onDragStart(nwPosition, sePosition, nwOffset, seOffset);
		//(pmh) Changed to use the mouse cursor single point coordinates 
		//  instead of the box nw/se area coordinates. 
		//  This feels better as an indicator of when you are in the target area.
		group.onDragStart(nwPosition, sePosition, mouse, mouse);

		// TODO: need better constraint API
		if (group.minX != null)
			group.minMouseX = mouse.x - nwPosition.x + 
					group.minX - nwOffset.x;
		if (group.maxX != null) 
			group.maxMouseX = group.minMouseX + group.maxX - group.minX;

		if (group.minY != null)
			group.minMouseY = mouse.y - nwPosition.y + 
					group.minY - nwOffset.y;
		if (group.maxY != null) 
			group.maxMouseY = group.minMouseY + group.maxY - group.minY;

		group.mouseMin = new ss_Coordinate(group.minMouseX, group.minMouseY);
		group.mouseMax = new ss_Coordinate(group.maxMouseX, group.maxMouseY);

		document.onmousemove = ss_Drag.onMouseMove;
		document.onmouseup = ss_Drag.onMouseUp;

		return false;
	},

	showStatus : function(mouse, nwPosition, sePosition, nwOffset, seOffset) {
        return;
		window.status = 
				"mouse: " + mouse.toString() + "    " + 
				"NW pos: " + nwPosition.toString() + "    " + 
				"SE pos: " + sePosition.toString() + "    " + 
				"NW offset: " + nwOffset.toString() + "    " +
				"SE offset: " + seOffset.toString();
	},

	onMouseMove : function(event) {
		event = ss_Drag.fixEvent(event);
		var group = ss_Drag.group;
		var mouse = event.windowCoordinate;
		var nwOffset = ss_Coordinates.northwestOffset(group, true);
		var nwPosition = ss_Coordinates.northwestPosition(group);
		var sePosition = ss_Coordinates.southeastPosition(group);
		var seOffset = ss_Coordinates.southeastOffset(group, true);

		//ss_Drag.showStatus(mouse, nwPosition, sePosition, nwOffset, seOffset);

		if (!ss_Drag.isDragging) {
			if (group.threshold > 10) {
				var distance = group.initialWindowCoordinate.distance(
						mouse);
				if (distance < group.threshold) return true;
			} else if (group.thresholdY > 10) {
				var deltaY = Math.abs(group.initialWindowCoordinate.y - mouse.y);
				if (deltaY < group.thresholdY) return true;
			} else if (group.thresholdX > 10) {
				var deltaX = Math.abs(group.initialWindowCoordinate.x - mouse.x);
				if (deltaX < group.thresholdX) return true;
			}

			ss_Drag.isDragging = true;
			group.style["zIndex"] = ss_Drag.BIG_Z_INDEX;
			group.style["opacity"] = 0.75;
		}

		// TODO: need better constraint API
		var adjusted = mouse.constrain(group.mouseMin, group.mouseMax);
		nwPosition = nwPosition.plus(adjusted.minus(group.dragCoordinate));
		nwPosition.reposition(group);
		group.dragCoordinate = adjusted;

		// once dragging has started, the position of the group
		// relative to the mouse should stay fixed.  They can get out
		// of sync if the DOM is manipulated while dragging, so we
		// correct the error here
		//
		// TODO: what we really want to do is find the offset from
		// our corner to the mouse coordinate and adjust to keep it
		// the same
		
		// changed to be recursive/use absolute offset for corrections
		var offsetBefore = ss_Coordinates.northwestOffset(group, true);
		//group.onDrag(nwPosition, sePosition, nwOffset, seOffset);
		group.onDrag(nwPosition, sePosition, mouse, mouse);
		var offsetAfter = ss_Coordinates.northwestOffset(group, true);

		if (!offsetBefore.equals(offsetAfter)) {
			var errorDelta = offsetBefore.minus(offsetAfter);
			nwPosition = ss_Coordinates.northwestPosition(group).plus(errorDelta);
			nwPosition.reposition(group);
		}

		return false;
	},

	onMouseUp : function(event) {
		event = ss_Drag.fixEvent(event);
		var group = ss_Drag.group;

		var mouse = event.windowCoordinate;
		var nwOffset = ss_Coordinates.northwestOffset(group, true);
		var nwPosition = ss_Coordinates.northwestPosition(group);
		var sePosition = ss_Coordinates.southeastPosition(group);
		var seOffset = ss_Coordinates.southeastOffset(group, true);

		document.onmousemove = null;
		document.onmouseup   = null;
		group.onDragEnd(nwPosition, sePosition, nwOffset, seOffset);

		if (ss_Drag.isDragging) {
			// restoring zIndex before opacity avoids visual flicker in Firefox
			group.style["zIndex"] = group.originalZIndex;
			group.style["opacity"] = group.originalOpacity;
		}

		ss_Drag.group = null;
		ss_Drag.isDragging = false;

		return false;
	},

	fixEvent : function(event) {
		if (typeof event == 'undefined') event = window.event;
		ss_Coordinates.fixEvent(event);

		return event;
	}
};
