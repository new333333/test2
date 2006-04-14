/**********************************************************
 Adapted from the sortable lists example by Tim Taylor
 http://tool-man.org/examples/sorting.html
 
 **********************************************************/



var ss_DragDrop = {
	firstContainer : null,
	lastContainer : null,
    dragStartParent : null,
    dragStartCursor : null,
    dragThresholdExceeded : null,
    	
	initializeListContainer : function() {
		this.firstContainer = null;
		this.lastContainer = null;
		this.dragStartParent = null;
	    this.dragStartCursor = null;
	    this.dragThresholdExceeded = false;
	    this.isOutside = false
	},
	
	makeListContainer : function(list) {
		// each container becomes a linked list node
		if (this.firstContainer == null) {
			this.firstContainer = this.lastContainer = list;
			list.previousContainer = null;
			list.nextContainer = null;
		} else {
			list.previousContainer = this.lastContainer;
			list.nextContainer = null;
			this.lastContainer.nextContainer = list;
			this.lastContainer = list;
		}
		
		// these functions are called when an item is draged over
		// a container or out of a container bounds.  onDragOut
		// is also called when the drag ends with an item having
		// been added to the container
		// onDragDrop is called when the item is dropped
		list.onDragOver = new Function();
		list.onDragOut = new Function();
		list.onDragDrop = new Function();
		
    	var items = list.getElementsByTagName( "li" );
    	
		for (var i = 0; i < items.length; i++) {
			ss_DragDrop.makeItemDragable(items[i]);
		}
	},

	makeItemDragable : function(item) {
		ss_Drag.makeDraggable(item);
		item.setDragThreshold(5);
		
		// tracks if the item is currently outside all containers
		item.isOutside = false;
		
		item.onDragStart = ss_DragDrop.onDragStart;
		item.onDrag = ss_DragDrop.onDrag;
		item.onDragEnd = ss_DragDrop.onDragEnd;
	},

	onDragStart : function(nwPosition, sePosition, nwOffset, seOffset) {
        // remember the original parent
        ss_DragDrop.dragStartParent = this.parentNode;
        ss_DragDrop.dragStartCursor = this.style.cursor;
        //document.getElementById('debugLog').innerHTML = 'Remember parent: ' + ss_DragDrop.dragStartParent.id;
        	
		// update all container bounds, since they may have changed
		// on a previous drag
		//
		// could be more smart about when to do this
		var container = ss_DragDrop.firstContainer;
		while (container != null) {
			container.northwest = ss_Coordinates.northwestOffset( container, true );
			container.southeast = ss_Coordinates.southeastOffset( container, true );
			//document.getElementById('debugLog').innerHTML += ' northwest('+container.id+'): '+container.northwest;
			//document.getElementById('debugLog').innerHTML += ' southeast('+container.id+'): '+container.southeast;
			container = container.nextContainer;
		}
	
		// item starts out over current parent
		this.parentNode.onDragOver();
	},

	onDrag : function(nwPosition, sePosition, nwOffset, seOffset) {
		//document.getElementById('debugLog').innerHTML += ' Ondrag this:'+this.style.top+", "+this.style.left;
		// check if we were nowhere
		//document.getElementById('debugLog').innerHTML += ' Ondrag parentNode ('+this.parentNode.id+'):'+this.parentNode.northwest+", "+this.parentNode.southeast;
		if (this.isOutside) {
			// check each container to see if in its bounds
			var container = ss_DragDrop.firstContainer;
			while (container != null) {
				if (nwOffset.inside( container.northwest, container.southeast ) ||
					seOffset.inside( container.northwest, container.southeast )) {
					// we're inside this one
					container.onDragOver();
					this.isOutside = false;
					//document.getElementById('debugLog').innerHTML += ' Over '+container.id;
					this.style.cursor = "move";
					
					// since isOutside was true, the current parent is a
					// temporary clone of some previous container node and
					// it needs to be removed from the document
					var tempParent = this.parentNode;
					tempParent.removeChild( this );
					container.appendChild( this );
					tempParent.parentNode.removeChild( tempParent );
					break;
				}
				container = container.nextContainer;
			}
			// we're still not inside the bounds of any container
			if (this.isOutside)
				return;
		
		// check if we're outside our parent's bounds
		} else if (!(nwOffset.inside( this.parentNode.northwest, this.parentNode.southeast ) ||
			seOffset.inside( this.parentNode.northwest, this.parentNode.southeast ))) {
			//document.getElementById('debugLog').innerHTML += ' Ondrag set outside true:'+this.parentNode.northwest+", "+this.parentNode.southeast;
			
			this.parentNode.onDragOut();
			this.isOutside = true;
			this.dragThresholdExceeded = true;
			//document.getElementById('debugLog').innerHTML += ' Outside all containters';
			//Set the cursor to indicate "no drop". 
			this.style.cursor = "text";		//Do it first to something guaranteed to be understood by all browsers
			this.style.cursor = "not-allowed";
			
			// check if we're inside a new container's bounds
			var container = ss_DragDrop.firstContainer;
			while (container != null) {
				if (nwOffset.inside( container.northwest, container.southeast ) ||
					seOffset.inside( container.northwest, container.southeast )) {
					// we're inside this one
					container.onDragOver();
					this.isOutside = false;
					//document.getElementById('debugLog').innerHTML += 'Over '+container.id;
					this.style.cursor = "move";
					this.parentNode.removeChild( this );
					container.appendChild( this );
					break;
				}
				container = container.nextContainer;
			}
			// if we're not in any container now, make a temporary clone of
			// the previous container node and add it to the document
			if (this.isOutside) {
				var tempParent = this.parentNode.cloneNode( false );
				if (this.style.zIndex) tempParent.style.zIndex = parseInt(parseInt(this.style.zIndex) + 1);
				this.parentNode.removeChild( this );
				tempParent.appendChild( this );
				document.getElementsByTagName( "body" ).item(0).appendChild( tempParent );
				//document.getElementById('debugLog').innerHTML += 'Cloned node '+this.id;
				//document.getElementById('debugLog').innerHTML += 'x,y: '+this.style.left+", "+this.style.top;
				return;
			}
		}
		
		// if we get here, we're inside some container bounds, so we do
		// everything the original dragsort script did to swap us into the
		// correct position
		
		this.style.cursor = "move";
		var parent = this.parentNode;
				
		var item = this;
		var next = ss_DragUtils.nextItem(item);
		while (next != null && this.offsetTop >= next.offsetTop - 2) {
			var item = next;
			var next = ss_DragUtils.nextItem(item);
		}
		if (this != item) {
			ss_DragUtils.swap(this, next);
			this.dragThresholdExceeded = true;
			return;
		}

		var item = this;
		var previous = ss_DragUtils.previousItem(item);
		while (previous != null && this.offsetTop <= previous.offsetTop + 2) {
			var item = previous;
			var previous = ss_DragUtils.previousItem(item);
		}
		if (this != item) {
			ss_DragUtils.swap(this, item);
			this.dragThresholdExceeded = true;
			return;
		}
	},

	onDragEnd : function(nwPosition, sePosition, nwOffset, seOffset) {
		// if the drag ends and we're still outside all containers
		// it's time to remove ourselves from the document
		if (this.isOutside) {
			var tempParent = this.parentNode;
            //document.getElementById('debugLog').innerHTML += ' Restoring to: ' + ss_DragDrop.dragStartParent.id;
			this.parentNode.removeChild( this );
            ss_DragDrop.dragStartParent.appendChild(this);
            this.isOutside = false;
			tempParent.parentNode.removeChild( tempParent );
			// return;
		} else {
			// update all container bounds, since they may have changed
			// on a previous drag
			//
			// could be more smart about when to do this
			var container = ss_DragDrop.firstContainer;
			while (container != null) {
				container.northwest = ss_Coordinates.northwestOffset( container, true );
				container.southeast = ss_Coordinates.southeastOffset( container, true );
				container = container.nextContainer;
			}
		}
		//Restore the cursor
		this.style.cursor = ss_DragDrop.dragStartCursor;
		this.parentNode.onDragOut();
		if (this.dragThresholdExceeded) this.parentNode.onDragDrop();
		this.style["top"] = "0px";
		this.style["left"] = "0px";
	}
};

var ss_DragUtils = {
	swap : function(item1, item2) {
		var parent = item1.parentNode;
		parent.removeChild(item1);
		parent.insertBefore(item1, item2);

		item1.style["top"] = "0px";
		item1.style["left"] = "0px";
	},

	nextItem : function(item) {
		var sibling = item.nextSibling;
		while (sibling != null) {
			if (sibling.nodeName == item.nodeName) return sibling;
			sibling = sibling.nextSibling;
		}
		return null;
	},

	previousItem : function(item) {
		var sibling = item.previousSibling;
		while (sibling != null) {
			if (sibling.nodeName == item.nodeName) return sibling;
			sibling = sibling.previousSibling;
		}
		return null;
	}		
};
