//>>built
define("dojox/calendar/StoreMixin",["dojo/_base/declare","dojo/_base/array","dojo/_base/html","dojo/_base/lang","dojo/dom-class","dojo/Stateful","dojo/when"],function(_1,_2,_3,_4,_5,_6,_7){
return _1("dojox.calendar.StoreMixin",_6,{store:null,query:{},queryOptions:null,startTimeAttr:"startTime",endTimeAttr:"endTime",summaryAttr:"summary",allDayAttr:"allDay",cssClassFunc:null,decodeDate:null,encodeDate:null,displayedItemsInvalidated:false,itemToRenderItem:function(_8,_9){
if(this.owner){
return this.owner.itemToRenderItem(_8,_9);
}
return {id:_9.getIdentity(_8),summary:_8[this.summaryAttr],startTime:(this.decodeDate&&this.decodeDate(_8[this.startTimeAttr]))||this.newDate(_8[this.startTimeAttr],this.dateClassObj),endTime:(this.decodeDate&&this.decodeDate(_8[this.endTimeAttr]))||this.newDate(_8[this.endTimeAttr],this.dateClassObj),allDay:_8[this.allDayAttr]!=null?_8[this.allDayAttr]:false,cssClass:this.cssClassFunc?this.cssClassFunc(_8):null};
},renderItemToItem:function(_a,_b){
if(this.owner){
return this.owner.renderItemToItem(_a,_b);
}
var _c={};
_c[_b.idProperty]=_a.id;
_c[this.summaryAttr]=_a.summary;
_c[this.startTimeAttr]=(this.encodeDate&&this.encodeDate(_a.startTime))||_a.startTime;
_c[this.endTimeAttr]=(this.encodeDate&&this.encodeDate(_a.endTime))||_a.endTime;
return _4.mixin(_b.get(_a.id),_c);
},_computeVisibleItems:function(_d){
var _e=_d.startTime;
var _f=_d.endTime;
if(this.items){
_d.items=_2.filter(this.items,function(_10){
return this.isOverlapping(_d,_10.startTime,_10.endTime,_e,_f);
},this);
}
},_initItems:function(_11){
this.set("items",_11);
return _11;
},_refreshItemsRendering:function(_12){
},_updateItems:function(_13,_14,_15){
var _16=true;
var _17=null;
var _18=this.itemToRenderItem(_13,this.store);
_18._item=_13;
if(_14!=-1){
if(_15!=_14){
this.items.splice(_14,1);
if(this.setItemSelected&&this.isItemSelected(_18)){
this.setItemSelected(_18,false);
this.dispatchChange(_18,this.get("selectedItem"),null,null);
}
}else{
_17=this.items[_14];
var cal=this.dateModule;
_16=cal.compare(_18.startTime,_17.startTime)!=0||cal.compare(_18.endTime,_17.endTime)!=0;
_4.mixin(_17,_18);
}
}else{
if(_15!=-1){
var s=this._getItemStoreStateObj(_18);
if(s){
if(this.items[_15].id!=_18.id){
var l=this.items.length;
for(var i=l-1;i>=0;i--){
if(this.items[i].id==_18.id){
this.items.splice(i,1);
break;
}
}
this.items.splice(_15,0,_18);
}
_4.mixin(s.renderItem,_18);
}else{
this.items.splice(_15,0,_18);
}
this.set("items",this.items);
}
}
this._setItemStoreState(_18,"stored");
if(!this._isEditing){
if(_16){
this._refreshItemsRendering();
}else{
this.updateRenderers(_17);
}
}
},_setStoreAttr:function(_19){
this.displayedItemsInvalidated=true;
var r;
if(this._observeHandler){
this._observeHandler.remove();
this._observeHandler=null;
}
if(_19){
var _1a=_19.query(this.query,this.queryOptions);
if(_1a.observe){
this._observeHandler=_1a.observe(_4.hitch(this,this._updateItems),true);
}
_1a=_1a.map(_4.hitch(this,function(_1b){
var _1c=this.itemToRenderItem(_1b,_19);
_1c._item=_1b;
return _1c;
}));
r=_7(_1a,_4.hitch(this,this._initItems));
}else{
r=this._initItems([]);
}
this._set("store",_19);
return r;
},_getItemStoreStateObj:function(_1d){
if(this.owner){
return this.owner._getItemStoreStateObj(_1d);
}
var _1e=this.get("store");
if(_1e!=null&&this._itemStoreState!=null){
var id=_1d.id==undefined?_1e.getIdentity(_1d):_1d.id;
return this._itemStoreState[id];
}
return null;
},getItemStoreState:function(_1f){
if(this.owner){
return this.owner.getItemStoreState(_1f);
}
if(this._itemStoreState==null){
return "stored";
}
var _20=this.get("store");
var id=_1f.id==undefined?_20.getIdentity(_1f):_1f.id;
var s=this._itemStoreState[id];
if(_20!=null&&s!=undefined){
return s.state;
}
return "stored";
},_setItemStoreState:function(_21,_22){
if(this.owner){
this.owner._setItemStoreState(_21,_22);
return;
}
if(this._itemStoreState==undefined){
this._itemStoreState={};
}
var _23=this.get("store");
var id=_21.id==undefined?_23.getIdentity(_21):_21.id;
var s=this._itemStoreState[id];
if(_22=="stored"||_22==null){
if(s!=undefined){
delete this._itemStoreState[id];
}
return;
}
if(_23){
this._itemStoreState[id]={id:id,item:_21,renderItem:this.itemToRenderItem(_21,_23),state:_22};
}
}});
});
