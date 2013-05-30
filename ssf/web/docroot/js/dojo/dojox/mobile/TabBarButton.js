//>>built
define("dojox/mobile/TabBarButton",["dojo/_base/connect","dojo/_base/declare","dojo/_base/event","dojo/_base/lang","dojo/dom","dojo/dom-class","dojo/dom-construct","dojo/dom-style","dojo/dom-attr","./View","./iconUtils","./_ItemBase","./Badge","./sniff","dojo/has!dojo-bidi?dojox/mobile/bidi/TabBarButton"],function(_1,_2,_3,_4,_5,_6,_7,_8,_9,_a,_b,_c,_d,_e,_f){
var _10=_2(_e("dojo-bidi")?"dojox.mobile.NonBidiTabBarButton":"dojox.mobile.TabBarButton",_c,{icon1:"",icon2:"",iconPos1:"",iconPos2:"",selected:false,transition:"none",tag:"li",badge:"",baseClass:"mblTabBarButton",closeIcon:"mblDomButtonWhiteCross",_selStartMethod:"touch",_selEndMethod:"touch",_moveTo:"",destroy:function(){
if(this.badgeObj){
delete this.badgeObj;
}
this.inherited(arguments);
},inheritParams:function(){
if(this.icon&&!this.icon1){
this.icon1=this.icon;
}
var _11=this.getParent();
if(_11){
if(!this.transition){
this.transition=_11.transition;
}
if(this.icon1&&_11.iconBase&&_11.iconBase.charAt(_11.iconBase.length-1)==="/"){
this.icon1=_11.iconBase+this.icon1;
}
if(!this.icon1){
this.icon1=_11.iconBase;
}
if(!this.iconPos1){
this.iconPos1=_11.iconPos;
}
if(this.icon2&&_11.iconBase&&_11.iconBase.charAt(_11.iconBase.length-1)==="/"){
this.icon2=_11.iconBase+this.icon2;
}
if(!this.icon2){
this.icon2=_11.iconBase||this.icon1;
}
if(!this.iconPos2){
this.iconPos2=_11.iconPos||this.iconPos1;
}
if(_11.closable){
if(!this.icon1){
this.icon1=this.closeIcon;
}
if(!this.icon2){
this.icon2=this.closeIcon;
}
_6.add(this.domNode,"mblTabBarButtonClosable");
}
}
},buildRendering:function(){
this.domNode=this.srcNodeRef||_7.create(this.tag);
if(this.srcNodeRef){
if(!this.label){
this.label=_4.trim(this.srcNodeRef.innerHTML);
}
this.srcNodeRef.innerHTML="";
}
this.labelNode=this.box=_7.create("div",{className:"mblTabBarButtonLabel"},this.domNode);
_9.set(this.domNode,"role","tab");
_9.set(this.domNode,"aria-selected","false");
this._moveTo=this._getMoveToId();
if(this._moveTo){
var _12=_5.byId(this._moveTo);
if(_12){
_9.set(this.domNode,"aria-controls",this._moveTo);
_9.set(_12,"role","tabpanel");
_9.set(_12,"aria-labelledby",this.id);
}
}
this.inherited(arguments);
},startup:function(){
if(this._started){
return;
}
this._dragstartHandle=this.connect(this.domNode,"ondragstart",_3.stop);
this.connect(this.domNode,"onkeydown","_onClick");
var _13=this.getParent();
if(_13&&_13.closable){
this._clickCloseHandler=this.connect(this.iconDivNode,"onclick","_onCloseButtonClick");
this._keydownCloseHandler=this.connect(this.iconDivNode,"onkeydown","_onCloseButtonClick");
this.iconDivNode.tabIndex="0";
}
this.inherited(arguments);
if(!this._isOnLine){
this._isOnLine=true;
this.set({icon:this._pendingIcon!==undefined?this._pendingIcon:this.icon,icon1:this.icon1,icon2:this.icon2});
delete this._pendingIcon;
}
_5.setSelectable(this.domNode,false);
},onClose:function(e){
_1.publish("/dojox/mobile/tabClose",[this]);
return this.getParent().onCloseButtonClick(this);
},_onCloseButtonClick:function(e){
if(e&&e.type==="keydown"&&e.keyCode!==13){
return;
}
if(this.onCloseButtonClick(e)===false){
return;
}
if(this.onClose()){
this.destroy();
}
},onCloseButtonClick:function(){
},_onClick:function(e){
if(e&&e.type==="keydown"&&e.keyCode!==13){
return;
}
if(this.onClick(e)===false){
return;
}
this.defaultClickAction(e);
},onClick:function(){
},_setIcon:function(_14,n){
if(!this.getParent()){
return;
}
this._set("icon"+n,_14);
if(!this.iconDivNode){
this.iconDivNode=_7.create("div",{className:"mblTabBarButtonIconArea"},this.domNode,"first");
}
if(!this["iconParentNode"+n]){
this["iconParentNode"+n]=_7.create("div",{className:"mblTabBarButtonIconParent mblTabBarButtonIconParent"+n},this.iconDivNode);
}
this["iconNode"+n]=_b.setIcon(_14,this["iconPos"+n],this["iconNode"+n],this.alt,this["iconParentNode"+n]);
this["icon"+n]=_14;
_6.toggle(this.domNode,"mblTabBarButtonHasIcon",_14&&_14!=="none");
},_getMoveToId:function(){
if(this.moveTo){
if(this.moveTo==="#"){
return "";
}
var _15="";
if(typeof (this.moveTo)==="object"&&this.moveTo.moveTo){
_15=this.moveTo.moveTo;
}else{
_15=this.moveTo;
}
if(_15){
_15=_a.prototype.convertToId(_15);
}
return _15;
}
},_setIcon1Attr:function(_16){
this._setIcon(_16,1);
},_setIcon2Attr:function(_17){
this._setIcon(_17,2);
},_getBadgeAttr:function(){
return this.badgeObj&&this.badgeObj.domNode.parentNode&&this.badgeObj.domNode.parentNode.nodeType==1?this.badgeObj.getValue():null;
},_setBadgeAttr:function(_18){
if(!this.badgeObj){
this.badgeObj=new _d({fontSize:11});
_8.set(this.badgeObj.domNode,{position:"absolute",top:"0px",right:"0px"});
}
this.badgeObj.setValue(_18);
if(_18){
this.domNode.appendChild(this.badgeObj.domNode);
}else{
if(this.domNode===this.badgeObj.domNode.parentNode){
this.domNode.removeChild(this.badgeObj.domNode);
}
}
},_setSelectedAttr:function(_19){
this.inherited(arguments);
_6.toggle(this.domNode,"mblTabBarButtonSelected",_19);
_9.set(this.domNode,"aria-selected",_19?"true":"false");
if(this._moveTo){
var _1a=_5.byId(this._moveTo);
if(_1a){
_9.set(_1a,"aria-hidden",_19?"false":"true");
}
}
}});
return _e("dojo-bidi")?_2("dojox.mobile.TabBarButton",[_10,_f]):_10;
});
