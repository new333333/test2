define("dojox/calendar/Calendar", ["dojo/_base/declare", "dojo/_base/lang", "./CalendarBase", "./ColumnView", "./ColumnViewSecondarySheet", 
				"./VerticalRenderer", "./MatrixView",	"./HorizontalRenderer", "./LabelRenderer", 
				"./ExpandRenderer", "./Keyboard", "./Mouse", "dojo/text!./templates/Calendar.html", 
				"dijit/form/Button", "dijit/Toolbar", "dijit/ToolbarSeparator"],
	
	function(declare, lang, CalendarBase, ColumnView, ColumnViewSecondarySheet, VerticalRenderer, 
					 MatrixView, HorizontalRenderer, LabelRenderer, ExpandRenderer, Keyboard, Mouse, template){
	
	return declare("dojox.calendar.Calendar", CalendarBase, {
		
		templateString: template,
		
		// summary:
		//		This class defines a calendar widget that display events in time.
		
		_createDefaultViews: function(){
			// summary:
			//		Creates the default views:
			//		- A dojox.calendar.ColumnView instance used to display one day to seven days time intervals,
			//		- A dojox.calendar.MatrixView instance used to display the other time intervals.
			//		The views are mixed with Mouse and Keyboard to allow editing items using mouse and keyboard.

			var secondarySheetClass = declare([ColumnViewSecondarySheet, Keyboard, Mouse]);
			
			var colView = declare([ColumnView, Keyboard, Mouse])(lang.mixin({
				secondarySheetClass: secondarySheetClass,
				verticalRenderer: VerticalRenderer,
				horizontalRenderer: HorizontalRenderer,
				expandRenderer: ExpandRenderer
			}, this.columnViewProps));
			
			var matrixView = declare([MatrixView, Keyboard, Mouse])(lang.mixin({
				horizontalRenderer: HorizontalRenderer,
				labelRenderer: LabelRenderer,
				expandRenderer: ExpandRenderer
			}, this.matrixViewProps));
								
			this.columnView = colView;
			this.matrixView = matrixView;
			
			var views = [colView, matrixView];
			
			this.installDefaultViewsActions(views);
			
			return views;
		},
		
		installDefaultViewsActions: function(views){
			// summary:
			//		Installs the default actions on newly created default views.
			//		By default this action is registering:
			//		- the matrixViewRowHeaderClick method on the rowHeaderClick event of the matrix view.
			//		- the columnViewColumnHeaderClick method on the columnHeaderClick event of the column view.
			this.matrixView.on("rowHeaderClick", lang.hitch(this, this.matrixViewRowHeaderClick));
			this.columnView.on("columnHeaderClick", lang.hitch(this, this.columnViewColumnHeaderClick));
		}
		
	});
});
require({cache:{
'url:dojox/calendar/templates/Calendar.html':"<div>\n\t<div data-dojo-attach-point=\"buttonContainer\" class=\"buttonContainer\">\n\t\t<div data-dojo-attach-point=\"toolbar\" data-dojo-type=\"dijit.Toolbar\" >\n\t\t\t<button data-dojo-attach-point=\"previousButton\" data-dojo-type=\"dijit.form.Button\" >◄</button>\n\t\t\t<button data-dojo-attach-point=\"nextButton\" data-dojo-type=\"dijit.form.Button\" >►</button>\n\t\t\t<span data-dojo-type=\"dijit.ToolbarSeparator\"></span>\n\t\t\t<button data-dojo-attach-point=\"todayButton\" data-dojo-type=\"dijit.form.Button\" >Today</button>\n\t\t\t<span data-dojo-type=\"dijit.ToolbarSeparator\"></span>\n\t\t\t<button data-dojo-attach-point=\"dayButton\" data-dojo-type=\"dijit.form.Button\" >Day</button>\n\t\t\t<button data-dojo-attach-point=\"fourDaysButton\" data-dojo-type=\"dijit.form.Button\" >4 Days</button>\n\t\t\t<button data-dojo-attach-point=\"weekButton\" data-dojo-type=\"dijit.form.Button\" >Week</button>\t\t\t\n\t\t\t<button data-dojo-attach-point=\"monthButton\" data-dojo-type=\"dijit.form.Button\" >Month</button>\n\t\t</div>\n\t</div>\n\t<div data-dojo-attach-point=\"viewContainer\" class=\"viewContainer\"></div>\n</div>\n"}});
