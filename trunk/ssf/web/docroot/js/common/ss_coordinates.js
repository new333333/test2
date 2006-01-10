/**********************************************************
 Very minorly modified from the example by Tim Taylor
 http://tool-man.org/examples/sorting.html
 
 Added ss_Coordinate.prototype.inside( northwest, southeast );
 
 **********************************************************/

var ss_Coordinates = {
	ORIGIN : new ss_Coordinate(0, 0),

	northwestPosition : function(element) {
		var x = parseInt(element.style.left);
		var y = parseInt(element.style.top);

		return new ss_Coordinate(isNaN(x) ? 0 : x, isNaN(y) ? 0 : y);
	},

	southeastPosition : function(element) {
		return ss_Coordinates.northwestPosition(element).plus(
				new ss_Coordinate(element.offsetWidth, element.offsetHeight));
	},

	northwestOffset : function(element, isRecursive) {
		var offset = new ss_Coordinate(element.offsetLeft, element.offsetTop);

		if (!isRecursive) return offset;

		var parent = element.offsetParent;
		while (parent) {
			offset = offset.plus(
					new ss_Coordinate(parent.offsetLeft, parent.offsetTop));
			parent = parent.offsetParent;
		}
		return offset;
	},

	southeastOffset : function(element, isRecursive) {
		return ss_Coordinates.northwestOffset(element, isRecursive).plus(
				new ss_Coordinate(element.offsetWidth, element.offsetHeight));
	},

	fixEvent : function(event) {
		event.windowCoordinate = new ss_Coordinate(event.clientX, event.clientY);
	}
};

function ss_Coordinate(x, y) {
	this.x = x;
	this.y = y;
}

ss_Coordinate.prototype.toString = function() {
	return "(" + this.x + "," + this.y + ")";
}

ss_Coordinate.prototype.plus = function(that) {
	return new ss_Coordinate(this.x + that.x, this.y + that.y);
}

ss_Coordinate.prototype.minus = function(that) {
	return new ss_Coordinate(this.x - that.x, this.y - that.y);
}

ss_Coordinate.prototype.distance = function(that) {
	var deltaX = this.x - that.x;
	var deltaY = this.y - that.y;

	return Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
}

ss_Coordinate.prototype.max = function(that) {
	var x = Math.max(this.x, that.x);
	var y = Math.max(this.y, that.y);
	return new ss_Coordinate(x, y);
}

ss_Coordinate.prototype.constrain = function(min, max) {
	if (min.x > max.x || min.y > max.y) return this;

	var x = this.x;
	var y = this.y;

	if (min.x != null) x = Math.max(x, min.x);
	if (max.x != null) x = Math.min(x, max.x);
	if (min.y != null) y = Math.max(y, min.y);
	if (max.y != null) y = Math.min(y, max.y);

	return new ss_Coordinate(x, y);
}

ss_Coordinate.prototype.reposition = function(element) {
	element.style["top"] = this.y + "px";
	element.style["left"] = this.x + "px";
}

ss_Coordinate.prototype.equals = function(that) {
	if (this == that) return true;
	if (!that || that == null) return false;

	return this.x == that.x && this.y == that.y;
}

// returns true of this point is inside specified box
ss_Coordinate.prototype.inside = function(northwest, southeast) {
	if ((this.x >= northwest.x) && (this.x <= southeast.x) &&
		(this.y >= northwest.y) && (this.y <= southeast.y)) {
		
		return true;
	}
	return false;
}
