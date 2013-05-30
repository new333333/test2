//>>built
define("dijit/TitlePane",["dojo/_base/array","dojo/_base/declare","dojo/dom","dojo/dom-attr","dojo/dom-class","dojo/dom-geometry","dojo/fx","dojo/has","dojo/_base/kernel","dojo/keys","./_CssStateMixin","./_TemplatedMixin","./layout/ContentPane","dojo/text!./templates/TitlePane.html","./_base/manager","./a11yclick"],function(_1,_2,_3,_4,_5,_6,_7,_8,_9,_a,_b,_c,_d,_e,_f){
var _10=_2("dijit.TitlePane",[_d,_c,_b],{title:"",_setTitleAttr:{node:"titleNode",type:"innerHTML"},open:true,toggleable:true,tabIndex:"0",duration:_f.defaultDuration,baseClass:"dijitTitlePane",templateString:_e,doLayout:false,_setTooltipAttr:{node:"focusNode",type:"attribute",attribute:"title"},buildRendering:function(){
this.inherited(arguments);
_3.setSelectable(this.titleNode,false);
},postCreate:function(){
this.inherited(arguments);
if(this.toggleable){
this._trackMouseState(this.titleBarNode,this.baseClass+"Title");
}
var _11=this.hideNode,_12=this.wipeNode;
this._wipeIn=_7.wipeIn({node:_12,duration:this.duration,beforeBegin:function(){
_11.style.display="";
}});
this._wipeOut=_7.wipeOut({node:_12,duration:this.duration,onEnd:function(){
_11.style.display="none";
}});
},_setOpenAttr:function(_13,_14){
_1.forEach([this._wipeIn,this._wipeOut],function(_15){
if(_15&&_15.status()=="playing"){
_15.stop();
}
});
if(_14){
var _16=this[_13?"_wipeIn":"_wipeOut"];
_16.play();
}else{
this.hideNode.style.display=this.wipeNode.style.display=_13?"":"none";
}
if(this._started){
if(_13){
this._onShow();
}else{
this.onHide();
}
}
this.containerNode.setAttribute("aria-hidden",_13?"false":"true");
this.focusNode.setAttribute("aria-pressed",_13?"true":"false");
this._set("open",_13);
this._setCss();
},_setToggleableAttr:function(_17){
this.focusNode.setAttribute("role",_17?"button":"heading");
if(_17){
this.focusNode.setAttribute("aria-controls",this.id+"_pane");
this.focusNode.setAttribute("tabIndex",this.tabIndex);
this.focusNode.setAttribute("aria-pressed",this.open);
}else{
_4.remove(this.focusNode,"aria-controls");
_4.remove(this.focusNode,"tabIndex");
_4.remove(this.focusNode,"aria-pressed");
}
this._set("toggleable",_17);
this._setCss();
},_setContentAttr:function(_18){
if(!this.open||!this._wipeOut||this._wipeOut.status()=="playing"){
this.inherited(arguments);
}else{
if(this._wipeIn&&this._wipeIn.status()=="playing"){
this._wipeIn.stop();
}
_6.setMarginBox(this.wipeNode,{h:_6.getMarginBox(this.wipeNode).h});
this.inherited(arguments);
if(this._wipeIn){
this._wipeIn.play();
}else{
this.hideNode.style.display="";
}
}
},toggle:function(){
this._setOpenAttr(!this.open,true);
},_setCss:function(){
var _19=this.titleBarNode||this.focusNode;
var _1a=this._titleBarClass;
this._titleBarClass=this.baseClass+"Title"+(this.toggleable?"":"Fixed")+(this.open?"Open":"Closed");
_5.replace(_19,this._titleBarClass,_1a||"");
_5.replace(_19,this._titleBarClass.replace("TitlePaneTitle",""),(_1a||"").replace("TitlePaneTitle",""));
this.arrowNodeInner.innerHTML=this.open?"-":"+";
},_onTitleKey:function(e){
if(e.keyCode==_a.DOWN_ARROW&&this.open){
this.containerNode.focus();
e.preventDefault();
}
},_onTitleClick:function(){
if(this.toggleable){
this.toggle();
}
},setTitle:function(_1b){
_9.deprecated("dijit.TitlePane.setTitle() is deprecated.  Use set('title', ...) instead.","","2.0");
this.set("title",_1b);
}});
if(_8("dojo-bidi")){
_10.extend({_setTitleAttr:function(_1c){
this._set("title",_1c);
this.titleNode.innerHTML=_1c;
this.applyTextDir(this.titleNode);
},_setTooltipAttr:function(_1d){
this._set("tooltip",_1d);
if(this.textDir){
_1d=this.enforceTextDirWithUcc(null,_1d);
}
_4.set(this.focusNode,"title",_1d);
},_setTextDirAttr:function(_1e){
if(this._created&&this.textDir!=_1e){
this._set("textDir",_1e);
this.set("title",this.title);
this.set("tooltip",this.tooltip);
}
}});
}
return _10;
});
require({cache:{"url:dijit/templates/TitlePane.html":"<div>\n\t<div data-dojo-attach-event=\"ondijitclick:_onTitleClick, onkeydown:_onTitleKey\"\n\t\t\tclass=\"dijitTitlePaneTitle\" data-dojo-attach-point=\"titleBarNode\" id=\"${id}_titleBarNode\">\n\t\t<div class=\"dijitTitlePaneTitleFocus\" data-dojo-attach-point=\"focusNode\">\n\t\t\t<span data-dojo-attach-point=\"arrowNode\" class=\"dijitInline dijitArrowNode\" role=\"presentation\"></span\n\t\t\t><span data-dojo-attach-point=\"arrowNodeInner\" class=\"dijitArrowNodeInner\"></span\n\t\t\t><span data-dojo-attach-point=\"titleNode\" class=\"dijitTitlePaneTextNode\"></span>\n\t\t</div>\n\t</div>\n\t<div class=\"dijitTitlePaneContentOuter\" data-dojo-attach-point=\"hideNode\" role=\"presentation\">\n\t\t<div class=\"dijitReset\" data-dojo-attach-point=\"wipeNode\" role=\"presentation\">\n\t\t\t<div class=\"dijitTitlePaneContentInner\" data-dojo-attach-point=\"containerNode\" role=\"region\" id=\"${id}_pane\" aria-labelledby=\"${id}_titleBarNode\">\n\t\t\t\t<!-- nested divs because wipeIn()/wipeOut() doesn't work right on node w/padding etc.  Put padding on inner div. -->\n\t\t\t</div>\n\t\t</div>\n\t</div>\n</div>\n"}});
