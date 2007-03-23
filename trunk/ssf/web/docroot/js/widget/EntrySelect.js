dojo.provide("ss_widget.EntrySelect");

dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.html.stabile");

dojo.widget.defineWidget(
	"ss_widget.EntrySelect",
	dojo.widget.Select,
	{
		nestedUrl : '',
		widgetContainer : null,
		searchFieldIndex: '',
		widgetStepsRef : null,
		selectOption : function(/*Event*/ evt){
			if (this.widgetStepsRef != null) this.widgetStepsRef.destroy();
			ss_widget.EntrySelect.superclass.selectOption.call(this, evt);
			var id = this.widgetContainer.id+this.selectedResult[1];
			var stepsProp = {dataUrl:this.nestedUrl+"&ss_entry_def_id="+this.selectedResult[1], id:id, name:"elementName"+this.searchFieldIndex, searchFieldIndex:this.searchFieldIndex, nodeObj:this.widgetContainer, nestedUrl:this.nestedUrl, entryTypeId:this.selectedResult[1], widgetContainer:this.widgetContainer};
			this.widgetStepsRef = dojo.widget.createWidget("FieldSelect", stepsProp, this.widgetContainer, "last");
		}
	}
);
