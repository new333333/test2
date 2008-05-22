(function () {
    window.Potato = {
	// replaces the supplied input element with an anchor element which
	// posts when clicked.  Link text may optionally be supplied.
	// Returns the new link.
    postLink: function (input, value) {
	    var _input = $(input); // capture input in environment
	    var form = _input.parents("form");
	    var val = value? value : _input.attr("value");
	    var link = $(document.createElement("a"))
	    .attr("href", form.attr("action"))
	    .attr("innerHTML", val)
	    .addClass("postLink");
	    link.click(function() {
		    _input.click();
		});
	    _input.css("display", "none");
	    _input.after(link);
	    return link;
	},
    // swaps the order of arguments for a two-argument function.
    flip: function (f) {
	    return function(y, x) { return f(x, y); };
	},
    // returns a function with the first argument bound to the specified value
    partial: function (f, x) {
	    var _x = x;
	    return function (y) { return f (_x, y); }
	},
    // creates necessary divs to apply rounded box styling
    round: function (elem, class) {
	    var _class = class? class : '';
	    var float = $(elem).css("float");
	    $(elem).css("float", "none");
	    return $(elem).addClass("rnd_bd").wrap("<div class='rnd " + _class + "'><div class='rnd_content'></div></div>")
	    .parent()
	    .prepend("<div class='rnd_t' />")
	    .parents('div.rnd:first')
	    .css("float", float) /* make our box float if its content was */
	    .append("<div class='rnd_b'><div /></div>");
	},
    // moves a label out of the document flow so it floats over the following 
    // element (normally, its field)
    overLabel: function (label) {
	    var _label = $(label);
	    var field = $("#" + _label.attr("for"));
	    field.blur(function () {
		    if (!$(this).attr("value")) {
			_label.show();
		    }
		});
	    return _label
	    .addClass("overLabel")
	    .css({position:"absolute", padding:"0.25em"})
	    .click(function () {
		    $(this).hide();
		    $($(this).attr("for")).focus();
		});
	}
    }

})();