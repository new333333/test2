/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(typeof dojo=="undefined"){
var dj_global=this;
var dj_currentContext=this;
function dj_undef(_1,_2){
return (typeof (_2||dj_currentContext)[_1]=="undefined");
}
if(dj_undef("djConfig",this)){
var djConfig={};
}
if(dj_undef("dojo",this)){
var dojo={};
}
dojo.global=function(){
return dj_currentContext;
};
dojo.locale=djConfig.locale;
dojo.version={major:0,minor:4,patch:0,flag:"",revision:Number("$Rev: 6258 $".match(/[0-9]+/)[0]),toString:function(){
with(dojo.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
dojo.evalProp=function(_3,_4,_5){
if((!_4)||(!_3)){
return undefined;
}
if(!dj_undef(_3,_4)){
return _4[_3];
}
return (_5?(_4[_3]={}):undefined);
};
dojo.parseObjPath=function(_6,_7,_8){
var _9=(_7||dojo.global());
var _a=_6.split(".");
var _b=_a.pop();
for(var i=0,l=_a.length;i<l&&_9;i++){
_9=dojo.evalProp(_a[i],_9,_8);
}
return {obj:_9,prop:_b};
};
dojo.evalObjPath=function(_e,_f){
if(typeof _e!="string"){
return dojo.global();
}
if(_e.indexOf(".")==-1){
return dojo.evalProp(_e,dojo.global(),_f);
}
var ref=dojo.parseObjPath(_e,dojo.global(),_f);
if(ref){
return dojo.evalProp(ref.prop,ref.obj,_f);
}
return null;
};
dojo.errorToString=function(_11){
if(!dj_undef("message",_11)){
return _11.message;
}else{
if(!dj_undef("description",_11)){
return _11.description;
}else{
return _11;
}
}
};
dojo.raise=function(_12,_13){
if(_13){
_12=_12+": "+dojo.errorToString(_13);
}
try{
if(djConfig.isDebug){
dojo.hostenv.println("FATAL exception raised: "+_12);
}
}
catch(e){
}
throw _13||Error(_12);
};
dojo.debug=function(){
};
dojo.debugShallow=function(obj){
};
dojo.profile={start:function(){
},end:function(){
},stop:function(){
},dump:function(){
}};
function dj_eval(_15){
return dj_global.eval?dj_global.eval(_15):eval(_15);
}
dojo.unimplemented=function(_16,_17){
var _18="'"+_16+"' not implemented";
if(_17!=null){
_18+=" "+_17;
}
dojo.raise(_18);
};
dojo.deprecated=function(_19,_1a,_1b){
var _1c="DEPRECATED: "+_19;
if(_1a){
_1c+=" "+_1a;
}
if(_1b){
_1c+=" -- will be removed in version: "+_1b;
}
dojo.debug(_1c);
};
dojo.render=(function(){
function vscaffold(_1d,_1e){
var tmp={capable:false,support:{builtin:false,plugin:false},prefixes:_1d};
for(var i=0;i<_1e.length;i++){
tmp[_1e[i]]=false;
}
return tmp;
}
return {name:"",ver:dojo.version,os:{win:false,linux:false,osx:false},html:vscaffold(["html"],["ie","opera","khtml","safari","moz"]),svg:vscaffold(["svg"],["corel","adobe","batik"]),vml:vscaffold(["vml"],["ie"]),swf:vscaffold(["Swf","Flash","Mm"],["mm"]),swt:vscaffold(["Swt"],["ibm"])};
})();
dojo.hostenv=(function(){
var _21={isDebug:false,allowQueryConfig:false,baseScriptUri:"",baseRelativePath:"",libraryScriptUri:"",iePreventClobber:false,ieClobberMinimal:true,preventBackButtonFix:true,delayMozLoadingFix:false,searchIds:[],parseWidgets:true};
if(typeof djConfig=="undefined"){
djConfig=_21;
}else{
for(var _22 in _21){
if(typeof djConfig[_22]=="undefined"){
djConfig[_22]=_21[_22];
}
}
}
return {name_:"(unset)",version_:"(unset)",getName:function(){
return this.name_;
},getVersion:function(){
return this.version_;
},getText:function(uri){
dojo.unimplemented("getText","uri="+uri);
}};
})();
dojo.hostenv.getBaseScriptUri=function(){
if(djConfig.baseScriptUri.length){
return djConfig.baseScriptUri;
}
var uri=new String(djConfig.libraryScriptUri||djConfig.baseRelativePath);
if(!uri){
dojo.raise("Nothing returned by getLibraryScriptUri(): "+uri);
}
var _25=uri.lastIndexOf("/");
djConfig.baseScriptUri=djConfig.baseRelativePath;
return djConfig.baseScriptUri;
};
(function(){
var _26={pkgFileName:"__package__",loading_modules_:{},loaded_modules_:{},addedToLoadingCount:[],removedFromLoadingCount:[],inFlightCount:0,modulePrefixes_:{dojo:{name:"dojo",value:"src"}},setModulePrefix:function(_27,_28){
this.modulePrefixes_[_27]={name:_27,value:_28};
},moduleHasPrefix:function(_29){
var mp=this.modulePrefixes_;
return Boolean(mp[_29]&&mp[_29].value);
},getModulePrefix:function(_2b){
if(this.moduleHasPrefix(_2b)){
return this.modulePrefixes_[_2b].value;
}
return _2b;
},getTextStack:[],loadUriStack:[],loadedUris:[],post_load_:false,modulesLoadedListeners:[],unloadListeners:[],loadNotifying:false};
for(var _2c in _26){
dojo.hostenv[_2c]=_26[_2c];
}
})();
dojo.hostenv.loadPath=function(_2d,_2e,cb){
var uri;
if(_2d.charAt(0)=="/"||_2d.match(/^\w+:/)){
uri=_2d;
}else{
uri=this.getBaseScriptUri()+_2d;
}
if(djConfig.cacheBust&&dojo.render.html.capable){
uri+="?"+String(djConfig.cacheBust).replace(/\W+/g,"");
}
try{
return !_2e?this.loadUri(uri,cb):this.loadUriAndCheck(uri,_2e,cb);
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.hostenv.loadUri=function(uri,cb){
if(this.loadedUris[uri]){
return true;
}
var _33=this.getText(uri,null,true);
if(!_33){
return false;
}
this.loadedUris[uri]=true;
if(cb){
_33="("+_33+")";
}
var _34=dj_eval(_33);
if(cb){
cb(_34);
}
return true;
};
dojo.hostenv.loadUriAndCheck=function(uri,_36,cb){
var ok=true;
try{
ok=this.loadUri(uri,cb);
}
catch(e){
dojo.debug("failed loading ",uri," with error: ",e);
}
return Boolean(ok&&this.findModule(_36,false));
};
dojo.loaded=function(){
};
dojo.unloaded=function(){
};
dojo.hostenv.loaded=function(){
this.loadNotifying=true;
this.post_load_=true;
var mll=this.modulesLoadedListeners;
for(var x=0;x<mll.length;x++){
mll[x]();
}
this.modulesLoadedListeners=[];
this.loadNotifying=false;
dojo.loaded();
};
dojo.hostenv.unloaded=function(){
var mll=this.unloadListeners;
while(mll.length){
(mll.pop())();
}
dojo.unloaded();
};
dojo.addOnLoad=function(obj,_3d){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.modulesLoadedListeners.push(obj);
}else{
if(arguments.length>1){
dh.modulesLoadedListeners.push(function(){
obj[_3d]();
});
}
}
if(dh.post_load_&&dh.inFlightCount==0&&!dh.loadNotifying){
dh.callLoaded();
}
};
dojo.addOnUnload=function(obj,_40){
var dh=dojo.hostenv;
if(arguments.length==1){
dh.unloadListeners.push(obj);
}else{
if(arguments.length>1){
dh.unloadListeners.push(function(){
obj[_40]();
});
}
}
};
dojo.hostenv.modulesLoaded=function(){
if(this.post_load_){
return;
}
if(this.loadUriStack.length==0&&this.getTextStack.length==0){
if(this.inFlightCount>0){
dojo.debug("files still in flight!");
return;
}
dojo.hostenv.callLoaded();
}
};
dojo.hostenv.callLoaded=function(){
if(typeof setTimeout=="object"){
setTimeout("dojo.hostenv.loaded();",0);
}else{
dojo.hostenv.loaded();
}
};
dojo.hostenv.getModuleSymbols=function(_42){
var _43=_42.split(".");
for(var i=_43.length;i>0;i--){
var _45=_43.slice(0,i).join(".");
if((i==1)&&!this.moduleHasPrefix(_45)){
_43[0]="../"+_43[0];
}else{
var _46=this.getModulePrefix(_45);
if(_46!=_45){
_43.splice(0,i,_46);
break;
}
}
}
return _43;
};
dojo.hostenv._global_omit_module_check=false;
dojo.hostenv.loadModule=function(_47,_48,_49){
if(!_47){
return;
}
_49=this._global_omit_module_check||_49;
var _4a=this.findModule(_47,false);
if(_4a){
return _4a;
}
if(dj_undef(_47,this.loading_modules_)){
this.addedToLoadingCount.push(_47);
}
this.loading_modules_[_47]=1;
var _4b=_47.replace(/\./g,"/")+".js";
var _4c=_47.split(".");
var _4d=this.getModuleSymbols(_47);
var _4e=((_4d[0].charAt(0)!="/")&&!_4d[0].match(/^\w+:/));
var _4f=_4d[_4d.length-1];
var ok;
if(_4f=="*"){
_47=_4c.slice(0,-1).join(".");
while(_4d.length){
_4d.pop();
_4d.push(this.pkgFileName);
_4b=_4d.join("/")+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,!_49?_47:null);
if(ok){
break;
}
_4d.pop();
}
}else{
_4b=_4d.join("/")+".js";
_47=_4c.join(".");
var _51=!_49?_47:null;
ok=this.loadPath(_4b,_51);
if(!ok&&!_48){
_4d.pop();
while(_4d.length){
_4b=_4d.join("/")+".js";
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
_4d.pop();
_4b=_4d.join("/")+"/"+this.pkgFileName+".js";
if(_4e&&_4b.charAt(0)=="/"){
_4b=_4b.slice(1);
}
ok=this.loadPath(_4b,_51);
if(ok){
break;
}
}
}
if(!ok&&!_49){
dojo.raise("Could not load '"+_47+"'; last tried '"+_4b+"'");
}
}
if(!_49&&!this["isXDomain"]){
_4a=this.findModule(_47,false);
if(!_4a){
dojo.raise("symbol '"+_47+"' is not defined after loading '"+_4b+"'");
}
}
return _4a;
};
dojo.hostenv.startPackage=function(_52){
var _53=String(_52);
var _54=_53;
var _55=_52.split(/\./);
if(_55[_55.length-1]=="*"){
_55.pop();
_54=_55.join(".");
}
var _56=dojo.evalObjPath(_54,true);
this.loaded_modules_[_53]=_56;
this.loaded_modules_[_54]=_56;
return _56;
};
dojo.hostenv.findModule=function(_57,_58){
var lmn=String(_57);
if(this.loaded_modules_[lmn]){
return this.loaded_modules_[lmn];
}
if(_58){
dojo.raise("no loaded module named '"+_57+"'");
}
return null;
};
dojo.kwCompoundRequire=function(_5a){
var _5b=_5a["common"]||[];
var _5c=_5a[dojo.hostenv.name_]?_5b.concat(_5a[dojo.hostenv.name_]||[]):_5b.concat(_5a["default"]||[]);
for(var x=0;x<_5c.length;x++){
var _5e=_5c[x];
if(_5e.constructor==Array){
dojo.hostenv.loadModule.apply(dojo.hostenv,_5e);
}else{
dojo.hostenv.loadModule(_5e);
}
}
};
dojo.require=function(_5f){
dojo.hostenv.loadModule.apply(dojo.hostenv,arguments);
};
dojo.requireIf=function(_60,_61){
var _62=arguments[0];
if((_62===true)||(_62=="common")||(_62&&dojo.render[_62].capable)){
var _63=[];
for(var i=1;i<arguments.length;i++){
_63.push(arguments[i]);
}
dojo.require.apply(dojo,_63);
}
};
dojo.requireAfterIf=dojo.requireIf;
dojo.provide=function(_65){
return dojo.hostenv.startPackage.apply(dojo.hostenv,arguments);
};
dojo.registerModulePath=function(_66,_67){
return dojo.hostenv.setModulePrefix(_66,_67);
};
dojo.setModulePrefix=function(_68,_69){
dojo.deprecated("dojo.setModulePrefix(\""+_68+"\", \""+_69+"\")","replaced by dojo.registerModulePath","0.5");
return dojo.registerModulePath(_68,_69);
};
dojo.exists=function(obj,_6b){
var p=_6b.split(".");
for(var i=0;i<p.length;i++){
if(!obj[p[i]]){
return false;
}
obj=obj[p[i]];
}
return true;
};
dojo.hostenv.normalizeLocale=function(_6e){
return _6e?_6e.toLowerCase():dojo.locale;
};
dojo.hostenv.searchLocalePath=function(_6f,_70,_71){
_6f=dojo.hostenv.normalizeLocale(_6f);
var _72=_6f.split("-");
var _73=[];
for(var i=_72.length;i>0;i--){
_73.push(_72.slice(0,i).join("-"));
}
_73.push(false);
if(_70){
_73.reverse();
}
for(var j=_73.length-1;j>=0;j--){
var loc=_73[j]||"ROOT";
var _77=_71(loc);
if(_77){
break;
}
}
};
dojo.hostenv.localesGenerated;
dojo.hostenv.registerNlsPrefix=function(){
dojo.registerModulePath("nls","nls");
};
dojo.hostenv.preloadLocalizations=function(){
if(dojo.hostenv.localesGenerated){
dojo.hostenv.registerNlsPrefix();
function preload(_78){
_78=dojo.hostenv.normalizeLocale(_78);
dojo.hostenv.searchLocalePath(_78,true,function(loc){
for(var i=0;i<dojo.hostenv.localesGenerated.length;i++){
if(dojo.hostenv.localesGenerated[i]==loc){
dojo["require"]("nls.dojo_"+loc);
return true;
}
}
return false;
});
}
preload();
var _7b=djConfig.extraLocale||[];
for(var i=0;i<_7b.length;i++){
preload(_7b[i]);
}
}
dojo.hostenv.preloadLocalizations=function(){
};
};
dojo.requireLocalization=function(_7d,_7e,_7f){
dojo.hostenv.preloadLocalizations();
var _80=[_7d,"nls",_7e].join(".");
var _81=dojo.hostenv.findModule(_80);
if(_81){
if(djConfig.localizationComplete&&_81._built){
return;
}
var _82=dojo.hostenv.normalizeLocale(_7f).replace("-","_");
var _83=_80+"."+_82;
if(dojo.hostenv.findModule(_83)){
return;
}
}
_81=dojo.hostenv.startPackage(_80);
var _84=dojo.hostenv.getModuleSymbols(_7d);
var _85=_84.concat("nls").join("/");
var _86;
dojo.hostenv.searchLocalePath(_7f,false,function(loc){
var _88=loc.replace("-","_");
var _89=_80+"."+_88;
var _8a=false;
if(!dojo.hostenv.findModule(_89)){
dojo.hostenv.startPackage(_89);
var _8b=[_85];
if(loc!="ROOT"){
_8b.push(loc);
}
_8b.push(_7e);
var _8c=_8b.join("/")+".js";
_8a=dojo.hostenv.loadPath(_8c,null,function(_8d){
var _8e=function(){
};
_8e.prototype=_86;
_81[_88]=new _8e();
for(var j in _8d){
_81[_88][j]=_8d[j];
}
});
}else{
_8a=true;
}
if(_8a&&_81[_88]){
_86=_81[_88];
}else{
_81[_88]=_86;
}
});
};
(function(){
var _90=djConfig.extraLocale;
if(_90){
if(!_90 instanceof Array){
_90=[_90];
}
var req=dojo.requireLocalization;
dojo.requireLocalization=function(m,b,_94){
req(m,b,_94);
if(_94){
return;
}
for(var i=0;i<_90.length;i++){
req(m,b,_90[i]);
}
};
}
})();
}
if(typeof window!="undefined"){
(function(){
if(djConfig.allowQueryConfig){
var _96=document.location.toString();
var _97=_96.split("?",2);
if(_97.length>1){
var _98=_97[1];
var _99=_98.split("&");
for(var x in _99){
var sp=_99[x].split("=");
if((sp[0].length>9)&&(sp[0].substr(0,9)=="djConfig.")){
var opt=sp[0].substr(9);
try{
djConfig[opt]=eval(sp[1]);
}
catch(e){
djConfig[opt]=sp[1];
}
}
}
}
}
if(((djConfig["baseScriptUri"]=="")||(djConfig["baseRelativePath"]==""))&&(document&&document.getElementsByTagName)){
var _9d=document.getElementsByTagName("script");
var _9e=/(__package__|dojo|bootstrap1)\.js([\?\.]|$)/i;
for(var i=0;i<_9d.length;i++){
var src=_9d[i].getAttribute("src");
if(!src){
continue;
}
var m=src.match(_9e);
if(m){
var _a2=src.substring(0,m.index);
if(src.indexOf("bootstrap1")>-1){
_a2+="../";
}
if(!this["djConfig"]){
djConfig={};
}
if(djConfig["baseScriptUri"]==""){
djConfig["baseScriptUri"]=_a2;
}
if(djConfig["baseRelativePath"]==""){
djConfig["baseRelativePath"]=_a2;
}
break;
}
}
}
var dr=dojo.render;
var drh=dojo.render.html;
var drs=dojo.render.svg;
var dua=(drh.UA=navigator.userAgent);
var dav=(drh.AV=navigator.appVersion);
var t=true;
var f=false;
drh.capable=t;
drh.support.builtin=t;
dr.ver=parseFloat(drh.AV);
dr.os.mac=dav.indexOf("Macintosh")>=0;
dr.os.win=dav.indexOf("Windows")>=0;
dr.os.linux=dav.indexOf("X11")>=0;
drh.opera=dua.indexOf("Opera")>=0;
drh.khtml=(dav.indexOf("Konqueror")>=0)||(dav.indexOf("Safari")>=0);
drh.safari=dav.indexOf("Safari")>=0;
var _aa=dua.indexOf("Gecko");
drh.mozilla=drh.moz=(_aa>=0)&&(!drh.khtml);
if(drh.mozilla){
drh.geckoVersion=dua.substring(_aa+6,_aa+14);
}
drh.ie=(document.all)&&(!drh.opera);
drh.ie50=drh.ie&&dav.indexOf("MSIE 5.0")>=0;
drh.ie55=drh.ie&&dav.indexOf("MSIE 5.5")>=0;
drh.ie60=drh.ie&&dav.indexOf("MSIE 6.0")>=0;
drh.ie70=drh.ie&&dav.indexOf("MSIE 7.0")>=0;
var cm=document["compatMode"];
drh.quirks=(cm=="BackCompat")||(cm=="QuirksMode")||drh.ie55||drh.ie50;
dojo.locale=dojo.locale||(drh.ie?navigator.userLanguage:navigator.language).toLowerCase();
dr.vml.capable=drh.ie;
drs.capable=f;
drs.support.plugin=f;
drs.support.builtin=f;
var _ac=window["document"];
var tdi=_ac["implementation"];
if((tdi)&&(tdi["hasFeature"])&&(tdi.hasFeature("org.w3c.dom.svg","1.0"))){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
if(drh.safari){
var tmp=dua.split("AppleWebKit/")[1];
var ver=parseFloat(tmp.split(" ")[0]);
if(ver>=420){
drs.capable=t;
drs.support.builtin=t;
drs.support.plugin=f;
}
}
})();
dojo.hostenv.startPackage("dojo.hostenv");
dojo.render.name=dojo.hostenv.name_="browser";
dojo.hostenv.searchIds=[];
dojo.hostenv._XMLHTTP_PROGIDS=["Msxml2.XMLHTTP","Microsoft.XMLHTTP","Msxml2.XMLHTTP.4.0"];
dojo.hostenv.getXmlhttpObject=function(){
var _b0=null;
var _b1=null;
try{
_b0=new XMLHttpRequest();
}
catch(e){
}
if(!_b0){
for(var i=0;i<3;++i){
var _b3=dojo.hostenv._XMLHTTP_PROGIDS[i];
try{
_b0=new ActiveXObject(_b3);
}
catch(e){
_b1=e;
}
if(_b0){
dojo.hostenv._XMLHTTP_PROGIDS=[_b3];
break;
}
}
}
if(!_b0){
return dojo.raise("XMLHTTP not available",_b1);
}
return _b0;
};
dojo.hostenv._blockAsync=false;
dojo.hostenv.getText=function(uri,_b5,_b6){
if(!_b5){
this._blockAsync=true;
}
var _b7=this.getXmlhttpObject();
function isDocumentOk(_b8){
var _b9=_b8["status"];
return Boolean((!_b9)||((200<=_b9)&&(300>_b9))||(_b9==304));
}
if(_b5){
var _ba=this,_bb=null,gbl=dojo.global();
var xhr=dojo.evalObjPath("dojo.io.XMLHTTPTransport");
_b7.onreadystatechange=function(){
if(_bb){
gbl.clearTimeout(_bb);
_bb=null;
}
if(_ba._blockAsync||(xhr&&xhr._blockAsync)){
_bb=gbl.setTimeout(function(){
_b7.onreadystatechange.apply(this);
},10);
}else{
if(4==_b7.readyState){
if(isDocumentOk(_b7)){
_b5(_b7.responseText);
}
}
}
};
}
_b7.open("GET",uri,_b5?true:false);
try{
_b7.send(null);
if(_b5){
return null;
}
if(!isDocumentOk(_b7)){
var err=Error("Unable to load "+uri+" status:"+_b7.status);
err.status=_b7.status;
err.responseText=_b7.responseText;
throw err;
}
}
catch(e){
this._blockAsync=false;
if((_b6)&&(!_b5)){
return null;
}else{
throw e;
}
}
this._blockAsync=false;
return _b7.responseText;
};
dojo.hostenv.defaultDebugContainerId="dojoDebug";
dojo.hostenv._println_buffer=[];
dojo.hostenv._println_safe=false;
dojo.hostenv.println=function(_bf){
if(!dojo.hostenv._println_safe){
dojo.hostenv._println_buffer.push(_bf);
}else{
try{
var _c0=document.getElementById(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId);
if(!_c0){
_c0=dojo.body();
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_bf));
_c0.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_bf+"</div>");
}
catch(e2){
window.status=_bf;
}
}
}
};
dojo.addOnLoad(function(){
dojo.hostenv._println_safe=true;
while(dojo.hostenv._println_buffer.length>0){
dojo.hostenv.println(dojo.hostenv._println_buffer.shift());
}
});
function dj_addNodeEvtHdlr(_c2,_c3,fp,_c5){
var _c6=_c2["on"+_c3]||function(){
};
_c2["on"+_c3]=function(){
fp.apply(_c2,arguments);
_c6.apply(_c2,arguments);
};
return true;
}
function dj_load_init(e){
var _c8=(e&&e.type)?e.type.toLowerCase():"load";
if(arguments.callee.initialized||(_c8!="domcontentloaded"&&_c8!="load")){
return;
}
arguments.callee.initialized=true;
if(typeof (_timer)!="undefined"){
clearInterval(_timer);
delete _timer;
}
var _c9=function(){
if(dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
};
if(dojo.hostenv.inFlightCount==0){
_c9();
dojo.hostenv.modulesLoaded();
}else{
dojo.addOnLoad(_c9);
}
}
if(document.addEventListener){
if(dojo.render.html.opera||(dojo.render.html.moz&&!djConfig.delayMozLoadingFix)){
document.addEventListener("DOMContentLoaded",dj_load_init,null);
}
window.addEventListener("load",dj_load_init,null);
}
if(dojo.render.html.ie&&dojo.render.os.win){
document.attachEvent("onreadystatechange",function(e){
if(document.readyState=="complete"){
dj_load_init();
}
});
}
if(/(WebKit|khtml)/i.test(navigator.userAgent)){
var _timer=setInterval(function(){
if(/loaded|complete/.test(document.readyState)){
dj_load_init();
}
},10);
}
if(dojo.render.html.ie){
dj_addNodeEvtHdlr(window,"beforeunload",function(){
dojo.hostenv._unloading=true;
window.setTimeout(function(){
dojo.hostenv._unloading=false;
},0);
});
}
dj_addNodeEvtHdlr(window,"unload",function(){
dojo.hostenv.unloaded();
if((!dojo.render.html.ie)||(dojo.render.html.ie&&dojo.hostenv._unloading)){
dojo.hostenv.unloaded();
}
});
dojo.hostenv.makeWidgets=function(){
var _cb=[];
if(djConfig.searchIds&&djConfig.searchIds.length>0){
_cb=_cb.concat(djConfig.searchIds);
}
if(dojo.hostenv.searchIds&&dojo.hostenv.searchIds.length>0){
_cb=_cb.concat(dojo.hostenv.searchIds);
}
if((djConfig.parseWidgets)||(_cb.length>0)){
if(dojo.evalObjPath("dojo.widget.Parse")){
var _cc=new dojo.xml.Parse();
if(_cb.length>0){
for(var x=0;x<_cb.length;x++){
var _ce=document.getElementById(_cb[x]);
if(!_ce){
continue;
}
var _cf=_cc.parseElement(_ce,null,true);
dojo.widget.getParser().createComponents(_cf);
}
}else{
if(djConfig.parseWidgets){
var _cf=_cc.parseElement(dojo.body(),null,true);
dojo.widget.getParser().createComponents(_cf);
}
}
}
}
};
dojo.addOnLoad(function(){
if(!dojo.render.html.ie){
dojo.hostenv.makeWidgets();
}
});
try{
if(dojo.render.html.ie){
document.namespaces.add("v","urn:schemas-microsoft-com:vml");
document.createStyleSheet().addRule("v\\:*","behavior:url(#default#VML)");
}
}
catch(e){
}
dojo.hostenv.writeIncludes=function(){
};
if(!dj_undef("document",this)){
dj_currentDocument=this.document;
}
dojo.doc=function(){
return dj_currentDocument;
};
dojo.body=function(){
return dojo.doc().body||dojo.doc().getElementsByTagName("body")[0];
};
dojo.byId=function(id,doc){
if((id)&&((typeof id=="string")||(id instanceof String))){
if(!doc){
doc=dj_currentDocument;
}
var ele=doc.getElementById(id);
if(ele&&(ele.id!=id)&&doc.all){
ele=null;
eles=doc.all[id];
if(eles){
if(eles.length){
for(var i=0;i<eles.length;i++){
if(eles[i].id==id){
ele=eles[i];
break;
}
}
}else{
ele=eles;
}
}
}
return ele;
}
return id;
};
dojo.setContext=function(_d4,_d5){
dj_currentContext=_d4;
dj_currentDocument=_d5;
};
dojo._fireCallback=function(_d6,_d7,_d8){
if((_d7)&&((typeof _d6=="string")||(_d6 instanceof String))){
_d6=_d7[_d6];
}
return (_d7?_d6.apply(_d7,_d8||[]):_d6());
};
dojo.withGlobal=function(_d9,_da,_db,_dc){
var _dd;
var _de=dj_currentContext;
var _df=dj_currentDocument;
try{
dojo.setContext(_d9,_d9.document);
_dd=dojo._fireCallback(_da,_db,_dc);
}
finally{
dojo.setContext(_de,_df);
}
return _dd;
};
dojo.withDoc=function(_e0,_e1,_e2,_e3){
var _e4;
var _e5=dj_currentDocument;
try{
dj_currentDocument=_e0;
_e4=dojo._fireCallback(_e1,_e2,_e3);
}
finally{
dj_currentDocument=_e5;
}
return _e4;
};
}
(function(){
if(typeof dj_usingBootstrap!="undefined"){
return;
}
var _e6=false;
var _e7=false;
var _e8=false;
if((typeof this["load"]=="function")&&((typeof this["Packages"]=="function")||(typeof this["Packages"]=="object"))){
_e6=true;
}else{
if(typeof this["load"]=="function"){
_e7=true;
}else{
if(window.widget){
_e8=true;
}
}
}
var _e9=[];
if((this["djConfig"])&&((djConfig["isDebug"])||(djConfig["debugAtAllCosts"]))){
_e9.push("debug.js");
}
if((this["djConfig"])&&(djConfig["debugAtAllCosts"])&&(!_e6)&&(!_e8)){
_e9.push("browser_debug.js");
}
var _ea=djConfig["baseScriptUri"];
if((this["djConfig"])&&(djConfig["baseLoaderUri"])){
_ea=djConfig["baseLoaderUri"];
}
for(var x=0;x<_e9.length;x++){
var _ec=_ea+"src/"+_e9[x];
if(_e6||_e7){
load(_ec);
}else{
try{
document.write("<scr"+"ipt type='text/javascript' src='"+_ec+"'></scr"+"ipt>");
}
catch(e){
var _ed=document.createElement("script");
_ed.src=_ec;
document.getElementsByTagName("head")[0].appendChild(_ed);
}
}
}
})();
dojo.provide("dojo.string.common");
dojo.string.trim=function(str,wh){
if(!str.replace){
return str;
}
if(!str.length){
return str;
}
var re=(wh>0)?(/^\s+/):(wh<0)?(/\s+$/):(/^\s+|\s+$/g);
return str.replace(re,"");
};
dojo.string.trimStart=function(str){
return dojo.string.trim(str,1);
};
dojo.string.trimEnd=function(str){
return dojo.string.trim(str,-1);
};
dojo.string.repeat=function(str,_f4,_f5){
var out="";
for(var i=0;i<_f4;i++){
out+=str;
if(_f5&&i<_f4-1){
out+=_f5;
}
}
return out;
};
dojo.string.pad=function(str,len,c,dir){
var out=String(str);
if(!c){
c="0";
}
if(!dir){
dir=1;
}
while(out.length<len){
if(dir>0){
out=c+out;
}else{
out+=c;
}
}
return out;
};
dojo.string.padLeft=function(str,len,c){
return dojo.string.pad(str,len,c,1);
};
dojo.string.padRight=function(str,len,c){
return dojo.string.pad(str,len,c,-1);
};
dojo.provide("dojo.string");
dojo.provide("dojo.lang.common");
dojo.lang.inherits=function(_103,_104){
if(typeof _104!="function"){
dojo.raise("dojo.inherits: superclass argument ["+_104+"] must be a function (subclass: ["+_103+"']");
}
_103.prototype=new _104();
_103.prototype.constructor=_103;
_103.superclass=_104.prototype;
_103["super"]=_104.prototype;
};
dojo.lang._mixin=function(obj,_106){
var tobj={};
for(var x in _106){
if((typeof tobj[x]=="undefined")||(tobj[x]!=_106[x])){
obj[x]=_106[x];
}
}
if(dojo.render.html.ie&&(typeof (_106["toString"])=="function")&&(_106["toString"]!=obj["toString"])&&(_106["toString"]!=tobj["toString"])){
obj.toString=_106.toString;
}
return obj;
};
dojo.lang.mixin=function(obj,_10a){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(obj,arguments[i]);
}
return obj;
};
dojo.lang.extend=function(_10d,_10e){
for(var i=1,l=arguments.length;i<l;i++){
dojo.lang._mixin(_10d.prototype,arguments[i]);
}
return _10d;
};
dojo.inherits=dojo.lang.inherits;
dojo.mixin=dojo.lang.mixin;
dojo.extend=dojo.lang.extend;
dojo.lang.find=function(_111,_112,_113,_114){
if(!dojo.lang.isArrayLike(_111)&&dojo.lang.isArrayLike(_112)){
dojo.deprecated("dojo.lang.find(value, array)","use dojo.lang.find(array, value) instead","0.5");
var temp=_111;
_111=_112;
_112=temp;
}
var _116=dojo.lang.isString(_111);
if(_116){
_111=_111.split("");
}
if(_114){
var step=-1;
var i=_111.length-1;
var end=-1;
}else{
var step=1;
var i=0;
var end=_111.length;
}
if(_113){
while(i!=end){
if(_111[i]===_112){
return i;
}
i+=step;
}
}else{
while(i!=end){
if(_111[i]==_112){
return i;
}
i+=step;
}
}
return -1;
};
dojo.lang.indexOf=dojo.lang.find;
dojo.lang.findLast=function(_11a,_11b,_11c){
return dojo.lang.find(_11a,_11b,_11c,true);
};
dojo.lang.lastIndexOf=dojo.lang.findLast;
dojo.lang.inArray=function(_11d,_11e){
return dojo.lang.find(_11d,_11e)>-1;
};
dojo.lang.isObject=function(it){
if(typeof it=="undefined"){
return false;
}
return (typeof it=="object"||it===null||dojo.lang.isArray(it)||dojo.lang.isFunction(it));
};
dojo.lang.isArray=function(it){
return (it&&it instanceof Array||typeof it=="array");
};
dojo.lang.isArrayLike=function(it){
if((!it)||(dojo.lang.isUndefined(it))){
return false;
}
if(dojo.lang.isString(it)){
return false;
}
if(dojo.lang.isFunction(it)){
return false;
}
if(dojo.lang.isArray(it)){
return true;
}
if((it.tagName)&&(it.tagName.toLowerCase()=="form")){
return false;
}
if(dojo.lang.isNumber(it.length)&&isFinite(it.length)){
return true;
}
return false;
};
dojo.lang.isFunction=function(it){
if(!it){
return false;
}
if((typeof (it)=="function")&&(it=="[object NodeList]")){
return false;
}
return (it instanceof Function||typeof it=="function");
};
dojo.lang.isString=function(it){
return (typeof it=="string"||it instanceof String);
};
dojo.lang.isAlien=function(it){
if(!it){
return false;
}
return !dojo.lang.isFunction()&&/\{\s*\[native code\]\s*\}/.test(String(it));
};
dojo.lang.isBoolean=function(it){
return (it instanceof Boolean||typeof it=="boolean");
};
dojo.lang.isNumber=function(it){
return (it instanceof Number||typeof it=="number");
};
dojo.lang.isUndefined=function(it){
return ((typeof (it)=="undefined")&&(it==undefined));
};
dojo.provide("dojo.lang.extras");
dojo.lang.setTimeout=function(func,_129){
var _12a=window,_12b=2;
if(!dojo.lang.isFunction(func)){
_12a=func;
func=_129;
_129=arguments[2];
_12b++;
}
if(dojo.lang.isString(func)){
func=_12a[func];
}
var args=[];
for(var i=_12b;i<arguments.length;i++){
args.push(arguments[i]);
}
return dojo.global().setTimeout(function(){
func.apply(_12a,args);
},_129);
};
dojo.lang.clearTimeout=function(_12e){
dojo.global().clearTimeout(_12e);
};
dojo.lang.getNameInObj=function(ns,item){
if(!ns){
ns=dj_global;
}
for(var x in ns){
if(ns[x]===item){
return new String(x);
}
}
return null;
};
dojo.lang.shallowCopy=function(obj,deep){
var i,ret;
if(obj===null){
return null;
}
if(dojo.lang.isObject(obj)){
ret=new obj.constructor();
for(i in obj){
if(dojo.lang.isUndefined(ret[i])){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}
}else{
if(dojo.lang.isArray(obj)){
ret=[];
for(i=0;i<obj.length;i++){
ret[i]=deep?dojo.lang.shallowCopy(obj[i],deep):obj[i];
}
}else{
ret=obj;
}
}
return ret;
};
dojo.lang.firstValued=function(){
for(var i=0;i<arguments.length;i++){
if(typeof arguments[i]!="undefined"){
return arguments[i];
}
}
return undefined;
};
dojo.lang.getObjPathValue=function(_137,_138,_139){
with(dojo.parseObjPath(_137,_138,_139)){
return dojo.evalProp(prop,obj,_139);
}
};
dojo.lang.setObjPathValue=function(_13a,_13b,_13c,_13d){
if(arguments.length<4){
_13d=true;
}
with(dojo.parseObjPath(_13a,_13c,_13d)){
if(obj&&(_13d||(prop in obj))){
obj[prop]=_13b;
}
}
};
dojo.provide("dojo.io.common");
dojo.io.transports=[];
dojo.io.hdlrFuncNames=["load","error","timeout"];
dojo.io.Request=function(url,_13f,_140,_141){
if((arguments.length==1)&&(arguments[0].constructor==Object)){
this.fromKwArgs(arguments[0]);
}else{
this.url=url;
if(_13f){
this.mimetype=_13f;
}
if(_140){
this.transport=_140;
}
if(arguments.length>=4){
this.changeUrl=_141;
}
}
};
dojo.lang.extend(dojo.io.Request,{url:"",mimetype:"text/plain",method:"GET",content:undefined,transport:undefined,changeUrl:undefined,formNode:undefined,sync:false,bindSuccess:false,useCache:false,preventCache:false,load:function(type,data,_144,_145){
},error:function(type,_147,_148,_149){
},timeout:function(type,_14b,_14c,_14d){
},handle:function(type,data,_150,_151){
},timeoutSeconds:0,abort:function(){
},fromKwArgs:function(_152){
if(_152["url"]){
_152.url=_152.url.toString();
}
if(_152["formNode"]){
_152.formNode=dojo.byId(_152.formNode);
}
if(!_152["method"]&&_152["formNode"]&&_152["formNode"].method){
_152.method=_152["formNode"].method;
}
if(!_152["handle"]&&_152["handler"]){
_152.handle=_152.handler;
}
if(!_152["load"]&&_152["loaded"]){
_152.load=_152.loaded;
}
if(!_152["changeUrl"]&&_152["changeURL"]){
_152.changeUrl=_152.changeURL;
}
_152.encoding=dojo.lang.firstValued(_152["encoding"],djConfig["bindEncoding"],"");
_152.sendTransport=dojo.lang.firstValued(_152["sendTransport"],djConfig["ioSendTransport"],false);
var _153=dojo.lang.isFunction;
for(var x=0;x<dojo.io.hdlrFuncNames.length;x++){
var fn=dojo.io.hdlrFuncNames[x];
if(_152[fn]&&_153(_152[fn])){
continue;
}
if(_152["handle"]&&_153(_152["handle"])){
_152[fn]=_152.handle;
}
}
dojo.lang.mixin(this,_152);
}});
dojo.io.Error=function(msg,type,num){
this.message=msg;
this.type=type||"unknown";
this.number=num||0;
};
dojo.io.transports.addTransport=function(name){
this.push(name);
this[name]=dojo.io[name];
};
dojo.io.bind=function(_15a){
if(!(_15a instanceof dojo.io.Request)){
try{
_15a=new dojo.io.Request(_15a);
}
catch(e){
dojo.debug(e);
}
}
var _15b="";
if(_15a["transport"]){
_15b=_15a["transport"];
if(!this[_15b]){
dojo.io.sendBindError(_15a,"No dojo.io.bind() transport with name '"+_15a["transport"]+"'.");
return _15a;
}
if(!this[_15b].canHandle(_15a)){
dojo.io.sendBindError(_15a,"dojo.io.bind() transport with name '"+_15a["transport"]+"' cannot handle this type of request.");
return _15a;
}
}else{
for(var x=0;x<dojo.io.transports.length;x++){
var tmp=dojo.io.transports[x];
if((this[tmp])&&(this[tmp].canHandle(_15a))){
_15b=tmp;
break;
}
}
if(_15b==""){
dojo.io.sendBindError(_15a,"None of the loaded transports for dojo.io.bind()"+" can handle the request.");
return _15a;
}
}
this[_15b].bind(_15a);
_15a.bindSuccess=true;
return _15a;
};
dojo.io.sendBindError=function(_15e,_15f){
if((typeof _15e.error=="function"||typeof _15e.handle=="function")&&(typeof setTimeout=="function"||typeof setTimeout=="object")){
var _160=new dojo.io.Error(_15f);
setTimeout(function(){
_15e[(typeof _15e.error=="function")?"error":"handle"]("error",_160,null,_15e);
},50);
}else{
dojo.raise(_15f);
}
};
dojo.io.queueBind=function(_161){
if(!(_161 instanceof dojo.io.Request)){
try{
_161=new dojo.io.Request(_161);
}
catch(e){
dojo.debug(e);
}
}
var _162=_161.load;
_161.load=function(){
dojo.io._queueBindInFlight=false;
var ret=_162.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
var _164=_161.error;
_161.error=function(){
dojo.io._queueBindInFlight=false;
var ret=_164.apply(this,arguments);
dojo.io._dispatchNextQueueBind();
return ret;
};
dojo.io._bindQueue.push(_161);
dojo.io._dispatchNextQueueBind();
return _161;
};
dojo.io._dispatchNextQueueBind=function(){
if(!dojo.io._queueBindInFlight){
dojo.io._queueBindInFlight=true;
if(dojo.io._bindQueue.length>0){
dojo.io.bind(dojo.io._bindQueue.shift());
}else{
dojo.io._queueBindInFlight=false;
}
}
};
dojo.io._bindQueue=[];
dojo.io._queueBindInFlight=false;
dojo.io.argsFromMap=function(map,_167,last){
var enc=/utf/i.test(_167||"")?encodeURIComponent:dojo.string.encodeAscii;
var _16a=[];
var _16b=new Object();
for(var name in map){
var _16d=function(elt){
var val=enc(name)+"="+enc(elt);
_16a[(last==name)?"push":"unshift"](val);
};
if(!_16b[name]){
var _170=map[name];
if(dojo.lang.isArray(_170)){
dojo.lang.forEach(_170,_16d);
}else{
_16d(_170);
}
}
}
return _16a.join("&");
};
dojo.io.setIFrameSrc=function(_171,src,_173){
try{
var r=dojo.render.html;
if(!_173){
if(r.safari){
_171.location=src;
}else{
frames[_171.name].location=src;
}
}else{
var idoc;
if(r.ie){
idoc=_171.contentWindow.document;
}else{
if(r.safari){
idoc=_171.document;
}else{
idoc=_171.contentWindow;
}
}
if(!idoc){
_171.location=src;
return;
}else{
idoc.location.replace(src);
}
}
}
catch(e){
dojo.debug(e);
dojo.debug("setIFrameSrc: "+e);
}
};
dojo.provide("dojo.lang.array");
dojo.lang.has=function(obj,name){
try{
return typeof obj[name]!="undefined";
}
catch(e){
return false;
}
};
dojo.lang.isEmpty=function(obj){
if(dojo.lang.isObject(obj)){
var tmp={};
var _17a=0;
for(var x in obj){
if(obj[x]&&(!tmp[x])){
_17a++;
break;
}
}
return _17a==0;
}else{
if(dojo.lang.isArrayLike(obj)||dojo.lang.isString(obj)){
return obj.length==0;
}
}
};
dojo.lang.map=function(arr,obj,_17e){
var _17f=dojo.lang.isString(arr);
if(_17f){
arr=arr.split("");
}
if(dojo.lang.isFunction(obj)&&(!_17e)){
_17e=obj;
obj=dj_global;
}else{
if(dojo.lang.isFunction(obj)&&_17e){
var _180=obj;
obj=_17e;
_17e=_180;
}
}
if(Array.map){
var _181=Array.map(arr,_17e,obj);
}else{
var _181=[];
for(var i=0;i<arr.length;++i){
_181.push(_17e.call(obj,arr[i]));
}
}
if(_17f){
return _181.join("");
}else{
return _181;
}
};
dojo.lang.reduce=function(arr,_184,obj,_186){
var _187=_184;
var ob=obj?obj:dj_global;
dojo.lang.map(arr,function(val){
_187=_186.call(ob,_187,val);
});
return _187;
};
dojo.lang.forEach=function(_18a,_18b,_18c){
if(dojo.lang.isString(_18a)){
_18a=_18a.split("");
}
if(Array.forEach){
Array.forEach(_18a,_18b,_18c);
}else{
if(!_18c){
_18c=dj_global;
}
for(var i=0,l=_18a.length;i<l;i++){
_18b.call(_18c,_18a[i],i,_18a);
}
}
};
dojo.lang._everyOrSome=function(_18f,arr,_191,_192){
if(dojo.lang.isString(arr)){
arr=arr.split("");
}
if(Array.every){
return Array[_18f?"every":"some"](arr,_191,_192);
}else{
if(!_192){
_192=dj_global;
}
for(var i=0,l=arr.length;i<l;i++){
var _195=_191.call(_192,arr[i],i,arr);
if(_18f&&!_195){
return false;
}else{
if((!_18f)&&(_195)){
return true;
}
}
}
return Boolean(_18f);
}
};
dojo.lang.every=function(arr,_197,_198){
return this._everyOrSome(true,arr,_197,_198);
};
dojo.lang.some=function(arr,_19a,_19b){
return this._everyOrSome(false,arr,_19a,_19b);
};
dojo.lang.filter=function(arr,_19d,_19e){
var _19f=dojo.lang.isString(arr);
if(_19f){
arr=arr.split("");
}
var _1a0;
if(Array.filter){
_1a0=Array.filter(arr,_19d,_19e);
}else{
if(!_19e){
if(arguments.length>=3){
dojo.raise("thisObject doesn't exist!");
}
_19e=dj_global;
}
_1a0=[];
for(var i=0;i<arr.length;i++){
if(_19d.call(_19e,arr[i],i,arr)){
_1a0.push(arr[i]);
}
}
}
if(_19f){
return _1a0.join("");
}else{
return _1a0;
}
};
dojo.lang.unnest=function(){
var out=[];
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isArrayLike(arguments[i])){
var add=dojo.lang.unnest.apply(this,arguments[i]);
out=out.concat(add);
}else{
out.push(arguments[i]);
}
}
return out;
};
dojo.lang.toArray=function(_1a5,_1a6){
var _1a7=[];
for(var i=_1a6||0;i<_1a5.length;i++){
_1a7.push(_1a5[i]);
}
return _1a7;
};
dojo.provide("dojo.lang.func");
dojo.lang.hitch=function(_1a9,_1aa){
var fcn=(dojo.lang.isString(_1aa)?_1a9[_1aa]:_1aa)||function(){
};
return function(){
return fcn.apply(_1a9,arguments);
};
};
dojo.lang.anonCtr=0;
dojo.lang.anon={};
dojo.lang.nameAnonFunc=function(_1ac,_1ad,_1ae){
var nso=(_1ad||dojo.lang.anon);
if((_1ae)||((dj_global["djConfig"])&&(djConfig["slowAnonFuncLookups"]==true))){
for(var x in nso){
try{
if(nso[x]===_1ac){
return x;
}
}
catch(e){
}
}
}
var ret="__"+dojo.lang.anonCtr++;
while(typeof nso[ret]!="undefined"){
ret="__"+dojo.lang.anonCtr++;
}
nso[ret]=_1ac;
return ret;
};
dojo.lang.forward=function(_1b2){
return function(){
return this[_1b2].apply(this,arguments);
};
};
dojo.lang.curry=function(ns,func){
var _1b5=[];
ns=ns||dj_global;
if(dojo.lang.isString(func)){
func=ns[func];
}
for(var x=2;x<arguments.length;x++){
_1b5.push(arguments[x]);
}
var _1b7=(func["__preJoinArity"]||func.length)-_1b5.length;
function gather(_1b8,_1b9,_1ba){
var _1bb=_1ba;
var _1bc=_1b9.slice(0);
for(var x=0;x<_1b8.length;x++){
_1bc.push(_1b8[x]);
}
_1ba=_1ba-_1b8.length;
if(_1ba<=0){
var res=func.apply(ns,_1bc);
_1ba=_1bb;
return res;
}else{
return function(){
return gather(arguments,_1bc,_1ba);
};
}
}
return gather([],_1b5,_1b7);
};
dojo.lang.curryArguments=function(ns,func,args,_1c2){
var _1c3=[];
var x=_1c2||0;
for(x=_1c2;x<args.length;x++){
_1c3.push(args[x]);
}
return dojo.lang.curry.apply(dojo.lang,[ns,func].concat(_1c3));
};
dojo.lang.tryThese=function(){
for(var x=0;x<arguments.length;x++){
try{
if(typeof arguments[x]=="function"){
var ret=(arguments[x]());
if(ret){
return ret;
}
}
}
catch(e){
dojo.debug(e);
}
}
};
dojo.lang.delayThese=function(farr,cb,_1c9,_1ca){
if(!farr.length){
if(typeof _1ca=="function"){
_1ca();
}
return;
}
if((typeof _1c9=="undefined")&&(typeof cb=="number")){
_1c9=cb;
cb=function(){
};
}else{
if(!cb){
cb=function(){
};
if(!_1c9){
_1c9=0;
}
}
}
setTimeout(function(){
(farr.shift())();
cb();
dojo.lang.delayThese(farr,cb,_1c9,_1ca);
},_1c9);
};
dojo.provide("dojo.string.extras");
dojo.string.substituteParams=function(_1cb,hash){
var map=(typeof hash=="object")?hash:dojo.lang.toArray(arguments,1);
return _1cb.replace(/\%\{(\w+)\}/g,function(_1ce,key){
if(typeof (map[key])!="undefined"&&map[key]!=null){
return map[key];
}
dojo.raise("Substitution not found: "+key);
});
};
dojo.string.capitalize=function(str){
if(!dojo.lang.isString(str)){
return "";
}
if(arguments.length==0){
str=this;
}
var _1d1=str.split(" ");
for(var i=0;i<_1d1.length;i++){
_1d1[i]=_1d1[i].charAt(0).toUpperCase()+_1d1[i].substring(1);
}
return _1d1.join(" ");
};
dojo.string.isBlank=function(str){
if(!dojo.lang.isString(str)){
return true;
}
return (dojo.string.trim(str).length==0);
};
dojo.string.encodeAscii=function(str){
if(!dojo.lang.isString(str)){
return str;
}
var ret="";
var _1d6=escape(str);
var _1d7,re=/%u([0-9A-F]{4})/i;
while((_1d7=_1d6.match(re))){
var num=Number("0x"+_1d7[1]);
var _1da=escape("&#"+num+";");
ret+=_1d6.substring(0,_1d7.index)+_1da;
_1d6=_1d6.substring(_1d7.index+_1d7[0].length);
}
ret+=_1d6.replace(/\+/g,"%2B");
return ret;
};
dojo.string.escape=function(type,str){
var args=dojo.lang.toArray(arguments,1);
switch(type.toLowerCase()){
case "xml":
case "html":
case "xhtml":
return dojo.string.escapeXml.apply(this,args);
case "sql":
return dojo.string.escapeSql.apply(this,args);
case "regexp":
case "regex":
return dojo.string.escapeRegExp.apply(this,args);
case "javascript":
case "jscript":
case "js":
return dojo.string.escapeJavaScript.apply(this,args);
case "ascii":
return dojo.string.encodeAscii.apply(this,args);
default:
return str;
}
};
dojo.string.escapeXml=function(str,_1df){
str=str.replace(/&/gm,"&amp;").replace(/</gm,"&lt;").replace(/>/gm,"&gt;").replace(/"/gm,"&quot;");
if(!_1df){
str=str.replace(/'/gm,"&#39;");
}
return str;
};
dojo.string.escapeSql=function(str){
return str.replace(/'/gm,"''");
};
dojo.string.escapeRegExp=function(str){
return str.replace(/\\/gm,"\\\\").replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm,"\\$1");
};
dojo.string.escapeJavaScript=function(str){
return str.replace(/(["'\f\b\n\t\r])/gm,"\\$1");
};
dojo.string.escapeString=function(str){
return ("\""+str.replace(/(["\\])/g,"\\$1")+"\"").replace(/[\f]/g,"\\f").replace(/[\b]/g,"\\b").replace(/[\n]/g,"\\n").replace(/[\t]/g,"\\t").replace(/[\r]/g,"\\r");
};
dojo.string.summary=function(str,len){
if(!len||str.length<=len){
return str;
}
return str.substring(0,len).replace(/\.+$/,"")+"...";
};
dojo.string.endsWith=function(str,end,_1e8){
if(_1e8){
str=str.toLowerCase();
end=end.toLowerCase();
}
if((str.length-end.length)<0){
return false;
}
return str.lastIndexOf(end)==str.length-end.length;
};
dojo.string.endsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.endsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.startsWith=function(str,_1ec,_1ed){
if(_1ed){
str=str.toLowerCase();
_1ec=_1ec.toLowerCase();
}
return str.indexOf(_1ec)==0;
};
dojo.string.startsWithAny=function(str){
for(var i=1;i<arguments.length;i++){
if(dojo.string.startsWith(str,arguments[i])){
return true;
}
}
return false;
};
dojo.string.has=function(str){
for(var i=1;i<arguments.length;i++){
if(str.indexOf(arguments[i])>-1){
return true;
}
}
return false;
};
dojo.string.normalizeNewlines=function(text,_1f3){
if(_1f3=="\n"){
text=text.replace(/\r\n/g,"\n");
text=text.replace(/\r/g,"\n");
}else{
if(_1f3=="\r"){
text=text.replace(/\r\n/g,"\r");
text=text.replace(/\n/g,"\r");
}else{
text=text.replace(/([^\r])\n/g,"$1\r\n").replace(/\r([^\n])/g,"\r\n$1");
}
}
return text;
};
dojo.string.splitEscaped=function(str,_1f5){
var _1f6=[];
for(var i=0,_1f8=0;i<str.length;i++){
if(str.charAt(i)=="\\"){
i++;
continue;
}
if(str.charAt(i)==_1f5){
_1f6.push(str.substring(_1f8,i));
_1f8=i+1;
}
}
_1f6.push(str.substr(_1f8));
return _1f6;
};
dojo.provide("dojo.dom");
dojo.dom.ELEMENT_NODE=1;
dojo.dom.ATTRIBUTE_NODE=2;
dojo.dom.TEXT_NODE=3;
dojo.dom.CDATA_SECTION_NODE=4;
dojo.dom.ENTITY_REFERENCE_NODE=5;
dojo.dom.ENTITY_NODE=6;
dojo.dom.PROCESSING_INSTRUCTION_NODE=7;
dojo.dom.COMMENT_NODE=8;
dojo.dom.DOCUMENT_NODE=9;
dojo.dom.DOCUMENT_TYPE_NODE=10;
dojo.dom.DOCUMENT_FRAGMENT_NODE=11;
dojo.dom.NOTATION_NODE=12;
dojo.dom.dojoml="http://www.dojotoolkit.org/2004/dojoml";
dojo.dom.xmlns={svg:"http://www.w3.org/2000/svg",smil:"http://www.w3.org/2001/SMIL20/",mml:"http://www.w3.org/1998/Math/MathML",cml:"http://www.xml-cml.org",xlink:"http://www.w3.org/1999/xlink",xhtml:"http://www.w3.org/1999/xhtml",xul:"http://www.mozilla.org/keymaster/gatekeeper/there.is.only.xul",xbl:"http://www.mozilla.org/xbl",fo:"http://www.w3.org/1999/XSL/Format",xsl:"http://www.w3.org/1999/XSL/Transform",xslt:"http://www.w3.org/1999/XSL/Transform",xi:"http://www.w3.org/2001/XInclude",xforms:"http://www.w3.org/2002/01/xforms",saxon:"http://icl.com/saxon",xalan:"http://xml.apache.org/xslt",xsd:"http://www.w3.org/2001/XMLSchema",dt:"http://www.w3.org/2001/XMLSchema-datatypes",xsi:"http://www.w3.org/2001/XMLSchema-instance",rdf:"http://www.w3.org/1999/02/22-rdf-syntax-ns#",rdfs:"http://www.w3.org/2000/01/rdf-schema#",dc:"http://purl.org/dc/elements/1.1/",dcq:"http://purl.org/dc/qualifiers/1.0","soap-env":"http://schemas.xmlsoap.org/soap/envelope/",wsdl:"http://schemas.xmlsoap.org/wsdl/",AdobeExtensions:"http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/"};
dojo.dom.isNode=function(wh){
if(typeof Element=="function"){
try{
return wh instanceof Element;
}
catch(E){
}
}else{
return wh&&!isNaN(wh.nodeType);
}
};
dojo.dom.getUniqueId=function(){
var _1fa=dojo.doc();
do{
var id="dj_unique_"+(++arguments.callee._idIncrement);
}while(_1fa.getElementById(id));
return id;
};
dojo.dom.getUniqueId._idIncrement=0;
dojo.dom.firstElement=dojo.dom.getFirstChildElement=function(_1fc,_1fd){
var node=_1fc.firstChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.nextSibling;
}
if(_1fd&&node&&node.tagName&&node.tagName.toLowerCase()!=_1fd.toLowerCase()){
node=dojo.dom.nextElement(node,_1fd);
}
return node;
};
dojo.dom.lastElement=dojo.dom.getLastChildElement=function(_1ff,_200){
var node=_1ff.lastChild;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.previousSibling;
}
if(_200&&node&&node.tagName&&node.tagName.toLowerCase()!=_200.toLowerCase()){
node=dojo.dom.prevElement(node,_200);
}
return node;
};
dojo.dom.nextElement=dojo.dom.getNextSiblingElement=function(node,_203){
if(!node){
return null;
}
do{
node=node.nextSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_203&&_203.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.nextElement(node,_203);
}
return node;
};
dojo.dom.prevElement=dojo.dom.getPreviousSiblingElement=function(node,_205){
if(!node){
return null;
}
if(_205){
_205=_205.toLowerCase();
}
do{
node=node.previousSibling;
}while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE);
if(node&&_205&&_205.toLowerCase()!=node.tagName.toLowerCase()){
return dojo.dom.prevElement(node,_205);
}
return node;
};
dojo.dom.moveChildren=function(_206,_207,trim){
var _209=0;
if(trim){
while(_206.hasChildNodes()&&_206.firstChild.nodeType==dojo.dom.TEXT_NODE){
_206.removeChild(_206.firstChild);
}
while(_206.hasChildNodes()&&_206.lastChild.nodeType==dojo.dom.TEXT_NODE){
_206.removeChild(_206.lastChild);
}
}
while(_206.hasChildNodes()){
_207.appendChild(_206.firstChild);
_209++;
}
return _209;
};
dojo.dom.copyChildren=function(_20a,_20b,trim){
var _20d=_20a.cloneNode(true);
return this.moveChildren(_20d,_20b,trim);
};
dojo.dom.removeChildren=function(node){
var _20f=node.childNodes.length;
while(node.hasChildNodes()){
node.removeChild(node.firstChild);
}
return _20f;
};
dojo.dom.replaceChildren=function(node,_211){
dojo.dom.removeChildren(node);
node.appendChild(_211);
};
dojo.dom.removeNode=function(node){
if(node&&node.parentNode){
return node.parentNode.removeChild(node);
}
};
dojo.dom.getAncestors=function(node,_214,_215){
var _216=[];
var _217=(_214&&(_214 instanceof Function||typeof _214=="function"));
while(node){
if(!_217||_214(node)){
_216.push(node);
}
if(_215&&_216.length>0){
return _216[0];
}
node=node.parentNode;
}
if(_215){
return null;
}
return _216;
};
dojo.dom.getAncestorsByTag=function(node,tag,_21a){
tag=tag.toLowerCase();
return dojo.dom.getAncestors(node,function(el){
return ((el.tagName)&&(el.tagName.toLowerCase()==tag));
},_21a);
};
dojo.dom.getFirstAncestorByTag=function(node,tag){
return dojo.dom.getAncestorsByTag(node,tag,true);
};
dojo.dom.isDescendantOf=function(node,_21f,_220){
if(_220&&node){
node=node.parentNode;
}
while(node){
if(node==_21f){
return true;
}
node=node.parentNode;
}
return false;
};
dojo.dom.innerXML=function(node){
if(node.innerXML){
return node.innerXML;
}else{
if(node.xml){
return node.xml;
}else{
if(typeof XMLSerializer!="undefined"){
return (new XMLSerializer()).serializeToString(node);
}
}
}
};
dojo.dom.createDocument=function(){
var doc=null;
var _223=dojo.doc();
if(!dj_undef("ActiveXObject")){
var _224=["MSXML2","Microsoft","MSXML","MSXML3"];
for(var i=0;i<_224.length;i++){
try{
doc=new ActiveXObject(_224[i]+".XMLDOM");
}
catch(e){
}
if(doc){
break;
}
}
}else{
if((_223.implementation)&&(_223.implementation.createDocument)){
doc=_223.implementation.createDocument("","",null);
}
}
return doc;
};
dojo.dom.createDocumentFromText=function(str,_227){
if(!_227){
_227="text/xml";
}
if(!dj_undef("DOMParser")){
var _228=new DOMParser();
return _228.parseFromString(str,_227);
}else{
if(!dj_undef("ActiveXObject")){
var _229=dojo.dom.createDocument();
if(_229){
_229.async=false;
_229.loadXML(str);
return _229;
}else{
dojo.debug("toXml didn't work?");
}
}else{
var _22a=dojo.doc();
if(_22a.createElement){
var tmp=_22a.createElement("xml");
tmp.innerHTML=str;
if(_22a.implementation&&_22a.implementation.createDocument){
var _22c=_22a.implementation.createDocument("foo","",null);
for(var i=0;i<tmp.childNodes.length;i++){
_22c.importNode(tmp.childNodes.item(i),true);
}
return _22c;
}
return ((tmp.document)&&(tmp.document.firstChild?tmp.document.firstChild:tmp));
}
}
}
return null;
};
dojo.dom.prependChild=function(node,_22f){
if(_22f.firstChild){
_22f.insertBefore(node,_22f.firstChild);
}else{
_22f.appendChild(node);
}
return true;
};
dojo.dom.insertBefore=function(node,ref,_232){
if(_232!=true&&(node===ref||node.nextSibling===ref)){
return false;
}
var _233=ref.parentNode;
_233.insertBefore(node,ref);
return true;
};
dojo.dom.insertAfter=function(node,ref,_236){
var pn=ref.parentNode;
if(ref==pn.lastChild){
if((_236!=true)&&(node===ref)){
return false;
}
pn.appendChild(node);
}else{
return this.insertBefore(node,ref.nextSibling,_236);
}
return true;
};
dojo.dom.insertAtPosition=function(node,ref,_23a){
if((!node)||(!ref)||(!_23a)){
return false;
}
switch(_23a.toLowerCase()){
case "before":
return dojo.dom.insertBefore(node,ref);
case "after":
return dojo.dom.insertAfter(node,ref);
case "first":
if(ref.firstChild){
return dojo.dom.insertBefore(node,ref.firstChild);
}else{
ref.appendChild(node);
return true;
}
break;
default:
ref.appendChild(node);
return true;
}
};
dojo.dom.insertAtIndex=function(node,_23c,_23d){
var _23e=_23c.childNodes;
if(!_23e.length){
_23c.appendChild(node);
return true;
}
var _23f=null;
for(var i=0;i<_23e.length;i++){
var _241=_23e.item(i)["getAttribute"]?parseInt(_23e.item(i).getAttribute("dojoinsertionindex")):-1;
if(_241<_23d){
_23f=_23e.item(i);
}
}
if(_23f){
return dojo.dom.insertAfter(node,_23f);
}else{
return dojo.dom.insertBefore(node,_23e.item(0));
}
};
dojo.dom.textContent=function(node,text){
if(arguments.length>1){
var _244=dojo.doc();
dojo.dom.replaceChildren(node,_244.createTextNode(text));
return text;
}else{
if(node.textContent!=undefined){
return node.textContent;
}
var _245="";
if(node==null){
return _245;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
_245+=dojo.dom.textContent(node.childNodes[i]);
break;
case 3:
case 2:
case 4:
_245+=node.childNodes[i].nodeValue;
break;
default:
break;
}
}
return _245;
}
};
dojo.dom.hasParent=function(node){
return node&&node.parentNode&&dojo.dom.isNode(node.parentNode);
};
dojo.dom.isTag=function(node){
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName==String(arguments[i])){
return String(arguments[i]);
}
}
}
return "";
};
dojo.dom.setAttributeNS=function(elem,_24b,_24c,_24d){
if(elem==null||((elem==undefined)&&(typeof elem=="undefined"))){
dojo.raise("No element given to dojo.dom.setAttributeNS");
}
if(!((elem.setAttributeNS==undefined)&&(typeof elem.setAttributeNS=="undefined"))){
elem.setAttributeNS(_24b,_24c,_24d);
}else{
var _24e=elem.ownerDocument;
var _24f=_24e.createNode(2,_24c,_24b);
_24f.nodeValue=_24d;
elem.setAttributeNode(_24f);
}
};
dojo.provide("dojo.undo.browser");
try{
if((!djConfig["preventBackButtonFix"])&&(!dojo.hostenv.post_load_)){
document.write("<iframe style='border: 0px; width: 1px; height: 1px; position: absolute; bottom: 0px; right: 0px; visibility: visible;' name='djhistory' id='djhistory' src='"+(dojo.hostenv.getBaseScriptUri()+"iframe_history.html")+"'></iframe>");
}
}
catch(e){
}
if(dojo.render.html.opera){
dojo.debug("Opera is not supported with dojo.undo.browser, so back/forward detection will not work.");
}
dojo.undo.browser={initialHref:window.location.href,initialHash:window.location.hash,moveForward:false,historyStack:[],forwardStack:[],historyIframe:null,bookmarkAnchor:null,locationTimer:null,setInitialState:function(args){
this.initialState=this._createState(this.initialHref,args,this.initialHash);
},addToHistory:function(args){
this.forwardStack=[];
var hash=null;
var url=null;
if(!this.historyIframe){
this.historyIframe=window.frames["djhistory"];
}
if(!this.bookmarkAnchor){
this.bookmarkAnchor=document.createElement("a");
dojo.body().appendChild(this.bookmarkAnchor);
this.bookmarkAnchor.style.display="none";
}
if(args["changeUrl"]){
hash="#"+((args["changeUrl"]!==true)?args["changeUrl"]:(new Date()).getTime());
if(this.historyStack.length==0&&this.initialState.urlHash==hash){
this.initialState=this._createState(url,args,hash);
return;
}else{
if(this.historyStack.length>0&&this.historyStack[this.historyStack.length-1].urlHash==hash){
this.historyStack[this.historyStack.length-1]=this._createState(url,args,hash);
return;
}
}
this.changingUrl=true;
setTimeout("window.location.href = '"+hash+"'; dojo.undo.browser.changingUrl = false;",1);
this.bookmarkAnchor.href=hash;
if(dojo.render.html.ie){
url=this._loadIframeHistory();
var _254=args["back"]||args["backButton"]||args["handle"];
var tcb=function(_256){
if(window.location.hash!=""){
setTimeout("window.location.href = '"+hash+"';",1);
}
_254.apply(this,[_256]);
};
if(args["back"]){
args.back=tcb;
}else{
if(args["backButton"]){
args.backButton=tcb;
}else{
if(args["handle"]){
args.handle=tcb;
}
}
}
var _257=args["forward"]||args["forwardButton"]||args["handle"];
var tfw=function(_259){
if(window.location.hash!=""){
window.location.href=hash;
}
if(_257){
_257.apply(this,[_259]);
}
};
if(args["forward"]){
args.forward=tfw;
}else{
if(args["forwardButton"]){
args.forwardButton=tfw;
}else{
if(args["handle"]){
args.handle=tfw;
}
}
}
}else{
if(dojo.render.html.moz){
if(!this.locationTimer){
this.locationTimer=setInterval("dojo.undo.browser.checkLocation();",200);
}
}
}
}else{
url=this._loadIframeHistory();
}
this.historyStack.push(this._createState(url,args,hash));
},checkLocation:function(){
if(!this.changingUrl){
var hsl=this.historyStack.length;
if((window.location.hash==this.initialHash||window.location.href==this.initialHref)&&(hsl==1)){
this.handleBackButton();
return;
}
if(this.forwardStack.length>0){
if(this.forwardStack[this.forwardStack.length-1].urlHash==window.location.hash){
this.handleForwardButton();
return;
}
}
if((hsl>=2)&&(this.historyStack[hsl-2])){
if(this.historyStack[hsl-2].urlHash==window.location.hash){
this.handleBackButton();
return;
}
}
}
},iframeLoaded:function(evt,_25c){
if(!dojo.render.html.opera){
var _25d=this._getUrlQuery(_25c.href);
if(_25d==null){
if(this.historyStack.length==1){
this.handleBackButton();
}
return;
}
if(this.moveForward){
this.moveForward=false;
return;
}
if(this.historyStack.length>=2&&_25d==this._getUrlQuery(this.historyStack[this.historyStack.length-2].url)){
this.handleBackButton();
}else{
if(this.forwardStack.length>0&&_25d==this._getUrlQuery(this.forwardStack[this.forwardStack.length-1].url)){
this.handleForwardButton();
}
}
}
},handleBackButton:function(){
var _25e=this.historyStack.pop();
if(!_25e){
return;
}
var last=this.historyStack[this.historyStack.length-1];
if(!last&&this.historyStack.length==0){
last=this.initialState;
}
if(last){
if(last.kwArgs["back"]){
last.kwArgs["back"]();
}else{
if(last.kwArgs["backButton"]){
last.kwArgs["backButton"]();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("back");
}
}
}
}
this.forwardStack.push(_25e);
},handleForwardButton:function(){
var last=this.forwardStack.pop();
if(!last){
return;
}
if(last.kwArgs["forward"]){
last.kwArgs.forward();
}else{
if(last.kwArgs["forwardButton"]){
last.kwArgs.forwardButton();
}else{
if(last.kwArgs["handle"]){
last.kwArgs.handle("forward");
}
}
}
this.historyStack.push(last);
},_createState:function(url,args,hash){
return {"url":url,"kwArgs":args,"urlHash":hash};
},_getUrlQuery:function(url){
var _265=url.split("?");
if(_265.length<2){
return null;
}else{
return _265[1];
}
},_loadIframeHistory:function(){
var url=dojo.hostenv.getBaseScriptUri()+"iframe_history.html?"+(new Date()).getTime();
this.moveForward=true;
dojo.io.setIFrameSrc(this.historyIframe,url,false);
return url;
}};
dojo.provide("dojo.io.BrowserIO");
dojo.io.checkChildrenForFile=function(node){
var _268=false;
var _269=node.getElementsByTagName("input");
dojo.lang.forEach(_269,function(_26a){
if(_268){
return;
}
if(_26a.getAttribute("type")=="file"){
_268=true;
}
});
return _268;
};
dojo.io.formHasFile=function(_26b){
return dojo.io.checkChildrenForFile(_26b);
};
dojo.io.updateNode=function(node,_26d){
node=dojo.byId(node);
var args=_26d;
if(dojo.lang.isString(_26d)){
args={url:_26d};
}
args.mimetype="text/html";
args.load=function(t,d,e){
while(node.firstChild){
if(dojo["event"]){
try{
dojo.event.browser.clean(node.firstChild);
}
catch(e){
}
}
node.removeChild(node.firstChild);
}
node.innerHTML=d;
};
dojo.io.bind(args);
};
dojo.io.formFilter=function(node){
var type=(node.type||"").toLowerCase();
return !node.disabled&&node.name&&!dojo.lang.inArray(["file","submit","image","reset","button"],type);
};
dojo.io.encodeForm=function(_274,_275,_276){
if((!_274)||(!_274.tagName)||(!_274.tagName.toLowerCase()=="form")){
dojo.raise("Attempted to encode a non-form element.");
}
if(!_276){
_276=dojo.io.formFilter;
}
var enc=/utf/i.test(_275||"")?encodeURIComponent:dojo.string.encodeAscii;
var _278=[];
for(var i=0;i<_274.elements.length;i++){
var elm=_274.elements[i];
if(!elm||elm.tagName.toLowerCase()=="fieldset"||!_276(elm)){
continue;
}
var name=enc(elm.name);
var type=elm.type.toLowerCase();
if(type=="select-multiple"){
for(var j=0;j<elm.options.length;j++){
if(elm.options[j].selected){
_278.push(name+"="+enc(elm.options[j].value));
}
}
}else{
if(dojo.lang.inArray(["radio","checkbox"],type)){
if(elm.checked){
_278.push(name+"="+enc(elm.value));
}
}else{
_278.push(name+"="+enc(elm.value));
}
}
}
var _27e=_274.getElementsByTagName("input");
for(var i=0;i<_27e.length;i++){
var _27f=_27e[i];
if(_27f.type.toLowerCase()=="image"&&_27f.form==_274&&_276(_27f)){
var name=enc(_27f.name);
_278.push(name+"="+enc(_27f.value));
_278.push(name+".x=0");
_278.push(name+".y=0");
}
}
return _278.join("&")+"&";
};
dojo.io.FormBind=function(args){
this.bindArgs={};
if(args&&args.formNode){
this.init(args);
}else{
if(args){
this.init({formNode:args});
}
}
};
dojo.lang.extend(dojo.io.FormBind,{form:null,bindArgs:null,clickedButton:null,init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _285=form.getElementsByTagName("input");
for(var i=0;i<_285.length;i++){
var _286=_285[i];
if(_286.type.toLowerCase()=="image"&&_286.form==form){
this.connect(_286,"onclick","click");
}
}
},onSubmit:function(form){
return true;
},submit:function(e){
e.preventDefault();
if(this.onSubmit(this.form)){
dojo.io.bind(dojo.lang.mixin(this.bindArgs,{formFilter:dojo.lang.hitch(this,"formFilter")}));
}
},click:function(e){
var node=e.currentTarget;
if(node.disabled){
return;
}
this.clickedButton=node;
},formFilter:function(node){
var type=(node.type||"").toLowerCase();
var _28d=false;
if(node.disabled||!node.name){
_28d=false;
}else{
if(dojo.lang.inArray(["submit","button","image"],type)){
if(!this.clickedButton){
this.clickedButton=node;
}
_28d=node==this.clickedButton;
}else{
_28d=!dojo.lang.inArray(["file","submit","reset","button"],type);
}
}
return _28d;
},connect:function(_28e,_28f,_290){
if(dojo.evalObjPath("dojo.event.connect")){
dojo.event.connect(_28e,_28f,this,_290);
}else{
var fcn=dojo.lang.hitch(this,_290);
_28e[_28f]=function(e){
if(!e){
e=window.event;
}
if(!e.currentTarget){
e.currentTarget=e.srcElement;
}
if(!e.preventDefault){
e.preventDefault=function(){
window.event.returnValue=false;
};
}
fcn(e);
};
}
}});
dojo.io.XMLHTTPTransport=new function(){
var _293=this;
var _294={};
this.useCache=false;
this.preventCache=false;
function getCacheKey(url,_296,_297){
return url+"|"+_296+"|"+_297.toLowerCase();
}
function addToCache(url,_299,_29a,http){
_294[getCacheKey(url,_299,_29a)]=http;
}
function getFromCache(url,_29d,_29e){
return _294[getCacheKey(url,_29d,_29e)];
}
this.clearCache=function(){
_294={};
};
function doLoad(_29f,http,url,_2a2,_2a3){
if(((http.status>=200)&&(http.status<300))||(http.status==304)||(location.protocol=="file:"&&(http.status==0||http.status==undefined))||(location.protocol=="chrome:"&&(http.status==0||http.status==undefined))){
var ret;
if(_29f.method.toLowerCase()=="head"){
var _2a5=http.getAllResponseHeaders();
ret={};
ret.toString=function(){
return _2a5;
};
var _2a6=_2a5.split(/[\r\n]+/g);
for(var i=0;i<_2a6.length;i++){
var pair=_2a6[i].match(/^([^:]+)\s*:\s*(.+)$/i);
if(pair){
ret[pair[1]]=pair[2];
}
}
}else{
if(_29f.mimetype=="text/javascript"){
try{
ret=dj_eval(http.responseText);
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=null;
}
}else{
if(_29f.mimetype=="text/json"||_29f.mimetype=="application/json"){
try{
ret=dj_eval("("+http.responseText+")");
}
catch(e){
dojo.debug(e);
dojo.debug(http.responseText);
ret=false;
}
}else{
if((_29f.mimetype=="application/xml")||(_29f.mimetype=="text/xml")){
ret=http.responseXML;
if(!ret||typeof ret=="string"||!http.getResponseHeader("Content-Type")){
ret=dojo.dom.createDocumentFromText(http.responseText);
}
}else{
ret=http.responseText;
}
}
}
}
if(_2a3){
addToCache(url,_2a2,_29f.method,http);
}
_29f[(typeof _29f.load=="function")?"load":"handle"]("load",ret,http,_29f);
}else{
var _2a9=new dojo.io.Error("XMLHttpTransport Error: "+http.status+" "+http.statusText);
_29f[(typeof _29f.error=="function")?"error":"handle"]("error",_2a9,http,_29f);
}
}
function setHeaders(http,_2ab){
if(_2ab["headers"]){
for(var _2ac in _2ab["headers"]){
if(_2ac.toLowerCase()=="content-type"&&!_2ab["contentType"]){
_2ab["contentType"]=_2ab["headers"][_2ac];
}else{
http.setRequestHeader(_2ac,_2ab["headers"][_2ac]);
}
}
}
}
this.inFlight=[];
this.inFlightTimer=null;
this.startWatchingInFlight=function(){
if(!this.inFlightTimer){
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
}
};
this.watchInFlight=function(){
var now=null;
if(!dojo.hostenv._blockAsync&&!_293._blockAsync){
for(var x=this.inFlight.length-1;x>=0;x--){
try{
var tif=this.inFlight[x];
if(!tif||tif.http._aborted||!tif.http.readyState){
this.inFlight.splice(x,1);
continue;
}
if(4==tif.http.readyState){
this.inFlight.splice(x,1);
doLoad(tif.req,tif.http,tif.url,tif.query,tif.useCache);
}else{
if(tif.startTime){
if(!now){
now=(new Date()).getTime();
}
if(tif.startTime+(tif.req.timeoutSeconds*1000)<now){
if(typeof tif.http.abort=="function"){
tif.http.abort();
}
this.inFlight.splice(x,1);
tif.req[(typeof tif.req.timeout=="function")?"timeout":"handle"]("timeout",null,tif.http,tif.req);
}
}
}
}
catch(e){
try{
var _2b0=new dojo.io.Error("XMLHttpTransport.watchInFlight Error: "+e);
tif.req[(typeof tif.req.error=="function")?"error":"handle"]("error",_2b0,tif.http,tif.req);
}
catch(e2){
dojo.debug("XMLHttpTransport error callback failed: "+e2);
}
}
}
}
clearTimeout(this.inFlightTimer);
if(this.inFlight.length==0){
this.inFlightTimer=null;
return;
}
this.inFlightTimer=setTimeout("dojo.io.XMLHTTPTransport.watchInFlight();",10);
};
var _2b1=dojo.hostenv.getXmlhttpObject()?true:false;
this.canHandle=function(_2b2){
return _2b1&&dojo.lang.inArray(["text/plain","text/html","application/xml","text/xml","text/javascript","text/json","application/json"],(_2b2["mimetype"].toLowerCase()||""))&&!(_2b2["formNode"]&&dojo.io.formHasFile(_2b2["formNode"]));
};
this.multipartBoundary="45309FFF-BD65-4d50-99C9-36986896A96F";
this.bind=function(_2b3){
if(!_2b3["url"]){
if(!_2b3["formNode"]&&(_2b3["backButton"]||_2b3["back"]||_2b3["changeUrl"]||_2b3["watchForURL"])&&(!djConfig.preventBackButtonFix)){
dojo.deprecated("Using dojo.io.XMLHTTPTransport.bind() to add to browser history without doing an IO request","Use dojo.undo.browser.addToHistory() instead.","0.4");
dojo.undo.browser.addToHistory(_2b3);
return true;
}
}
var url=_2b3.url;
var _2b5="";
if(_2b3["formNode"]){
var ta=_2b3.formNode.getAttribute("action");
if((ta)&&(!_2b3["url"])){
url=ta;
}
var tp=_2b3.formNode.getAttribute("method");
if((tp)&&(!_2b3["method"])){
_2b3.method=tp;
}
_2b5+=dojo.io.encodeForm(_2b3.formNode,_2b3.encoding,_2b3["formFilter"]);
}
if(url.indexOf("#")>-1){
dojo.debug("Warning: dojo.io.bind: stripping hash values from url:",url);
url=url.split("#")[0];
}
if(_2b3["file"]){
_2b3.method="post";
}
if(!_2b3["method"]){
_2b3.method="get";
}
if(_2b3.method.toLowerCase()=="get"){
_2b3.multipart=false;
}else{
if(_2b3["file"]){
_2b3.multipart=true;
}else{
if(!_2b3["multipart"]){
_2b3.multipart=false;
}
}
}
if(_2b3["backButton"]||_2b3["back"]||_2b3["changeUrl"]){
dojo.undo.browser.addToHistory(_2b3);
}
var _2b8=_2b3["content"]||{};
if(_2b3.sendTransport){
_2b8["dojo.transport"]="xmlhttp";
}
do{
if(_2b3.postContent){
_2b5=_2b3.postContent;
break;
}
if(_2b8){
_2b5+=dojo.io.argsFromMap(_2b8,_2b3.encoding);
}
if(_2b3.method.toLowerCase()=="get"||!_2b3.multipart){
break;
}
var t=[];
if(_2b5.length){
var q=_2b5.split("&");
for(var i=0;i<q.length;++i){
if(q[i].length){
var p=q[i].split("=");
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+p[0]+"\"","",p[1]);
}
}
}
if(_2b3.file){
if(dojo.lang.isArray(_2b3.file)){
for(var i=0;i<_2b3.file.length;++i){
var o=_2b3.file[i];
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}else{
var o=_2b3.file;
t.push("--"+this.multipartBoundary,"Content-Disposition: form-data; name=\""+o.name+"\"; filename=\""+("fileName" in o?o.fileName:o.name)+"\"","Content-Type: "+("contentType" in o?o.contentType:"application/octet-stream"),"",o.content);
}
}
if(t.length){
t.push("--"+this.multipartBoundary+"--","");
_2b5=t.join("\r\n");
}
}while(false);
var _2be=_2b3["sync"]?false:true;
var _2bf=_2b3["preventCache"]||(this.preventCache==true&&_2b3["preventCache"]!=false);
var _2c0=_2b3["useCache"]==true||(this.useCache==true&&_2b3["useCache"]!=false);
if(!_2bf&&_2c0){
var _2c1=getFromCache(url,_2b5,_2b3.method);
if(_2c1){
doLoad(_2b3,_2c1,url,_2b5,false);
return;
}
}
var http=dojo.hostenv.getXmlhttpObject(_2b3);
var _2c3=false;
if(_2be){
var _2c4=this.inFlight.push({"req":_2b3,"http":http,"url":url,"query":_2b5,"useCache":_2c0,"startTime":_2b3.timeoutSeconds?(new Date()).getTime():0});
this.startWatchingInFlight();
}else{
_293._blockAsync=true;
}
if(_2b3.method.toLowerCase()=="post"){
if(!_2b3.user){
http.open("POST",url,_2be);
}else{
http.open("POST",url,_2be,_2b3.user,_2b3.password);
}
setHeaders(http,_2b3);
http.setRequestHeader("Content-Type",_2b3.multipart?("multipart/form-data; boundary="+this.multipartBoundary):(_2b3.contentType||"application/x-www-form-urlencoded"));
try{
http.send(_2b5);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_2b3,{status:404},url,_2b5,_2c0);
}
}else{
var _2c5=url;
if(_2b5!=""){
_2c5+=(_2c5.indexOf("?")>-1?"&":"?")+_2b5;
}
if(_2bf){
_2c5+=(dojo.string.endsWithAny(_2c5,"?","&")?"":(_2c5.indexOf("?")>-1?"&":"?"))+"dojo.preventCache="+new Date().valueOf();
}
if(!_2b3.user){
http.open(_2b3.method.toUpperCase(),_2c5,_2be);
}else{
http.open(_2b3.method.toUpperCase(),_2c5,_2be,_2b3.user,_2b3.password);
}
setHeaders(http,_2b3);
try{
http.send(null);
}
catch(e){
if(typeof http.abort=="function"){
http.abort();
}
doLoad(_2b3,{status:404},url,_2b5,_2c0);
}
}
if(!_2be){
doLoad(_2b3,http,url,_2b5,_2c0);
_293._blockAsync=false;
}
_2b3.abort=function(){
try{
http._aborted=true;
}
catch(e){
}
return http.abort();
};
return;
};
dojo.io.transports.addTransport("XMLHTTPTransport");
};
dojo.provide("dojo.io.cookie");
dojo.io.cookie.setCookie=function(name,_2c7,days,path,_2ca,_2cb){
var _2cc=-1;
if(typeof days=="number"&&days>=0){
var d=new Date();
d.setTime(d.getTime()+(days*24*60*60*1000));
_2cc=d.toGMTString();
}
_2c7=escape(_2c7);
document.cookie=name+"="+_2c7+";"+(_2cc!=-1?" expires="+_2cc+";":"")+(path?"path="+path:"")+(_2ca?"; domain="+_2ca:"")+(_2cb?"; secure":"");
};
dojo.io.cookie.set=dojo.io.cookie.setCookie;
dojo.io.cookie.getCookie=function(name){
var idx=document.cookie.lastIndexOf(name+"=");
if(idx==-1){
return null;
}
var _2d0=document.cookie.substring(idx+name.length+1);
var end=_2d0.indexOf(";");
if(end==-1){
end=_2d0.length;
}
_2d0=_2d0.substring(0,end);
_2d0=unescape(_2d0);
return _2d0;
};
dojo.io.cookie.get=dojo.io.cookie.getCookie;
dojo.io.cookie.deleteCookie=function(name){
dojo.io.cookie.setCookie(name,"-",0);
};
dojo.io.cookie.setObjectCookie=function(name,obj,days,path,_2d7,_2d8,_2d9){
if(arguments.length==5){
_2d9=_2d7;
_2d7=null;
_2d8=null;
}
var _2da=[],_2db,_2dc="";
if(!_2d9){
_2db=dojo.io.cookie.getObjectCookie(name);
}
if(days>=0){
if(!_2db){
_2db={};
}
for(var prop in obj){
if(prop==null){
delete _2db[prop];
}else{
if(typeof obj[prop]=="string"||typeof obj[prop]=="number"){
_2db[prop]=obj[prop];
}
}
}
prop=null;
for(var prop in _2db){
_2da.push(escape(prop)+"="+escape(_2db[prop]));
}
_2dc=_2da.join("&");
}
dojo.io.cookie.setCookie(name,_2dc,days,path,_2d7,_2d8);
};
dojo.io.cookie.getObjectCookie=function(name){
var _2df=null,_2e0=dojo.io.cookie.getCookie(name);
if(_2e0){
_2df={};
var _2e1=_2e0.split("&");
for(var i=0;i<_2e1.length;i++){
var pair=_2e1[i].split("=");
var _2e4=pair[1];
if(isNaN(_2e4)){
_2e4=unescape(pair[1]);
}
_2df[unescape(pair[0])]=_2e4;
}
}
return _2df;
};
dojo.io.cookie.isSupported=function(){
if(typeof navigator.cookieEnabled!="boolean"){
dojo.io.cookie.setCookie("__TestingYourBrowserForCookieSupport__","CookiesAllowed",90,null);
var _2e5=dojo.io.cookie.getCookie("__TestingYourBrowserForCookieSupport__");
navigator.cookieEnabled=(_2e5=="CookiesAllowed");
if(navigator.cookieEnabled){
this.deleteCookie("__TestingYourBrowserForCookieSupport__");
}
}
return navigator.cookieEnabled;
};
if(!dojo.io.cookies){
dojo.io.cookies=dojo.io.cookie;
}
dojo.provide("dojo.io.*");
dojo.provide("dojo.uri.Uri");
dojo.uri=new function(){
this.dojoUri=function(uri){
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri(),uri);
};
this.moduleUri=function(_2e7,uri){
var loc=dojo.hostenv.getModulePrefix(_2e7);
if(!loc){
return null;
}
if(loc.lastIndexOf("/")!=loc.length-1){
loc+="/";
}
return new dojo.uri.Uri(dojo.hostenv.getBaseScriptUri()+loc,uri);
};
this.Uri=function(){
var uri=arguments[0];
for(var i=1;i<arguments.length;i++){
if(!arguments[i]){
continue;
}
var _2ec=new dojo.uri.Uri(arguments[i].toString());
var _2ed=new dojo.uri.Uri(uri.toString());
if((_2ec.path=="")&&(_2ec.scheme==null)&&(_2ec.authority==null)&&(_2ec.query==null)){
if(_2ec.fragment!=null){
_2ed.fragment=_2ec.fragment;
}
_2ec=_2ed;
}else{
if(_2ec.scheme==null){
_2ec.scheme=_2ed.scheme;
if(_2ec.authority==null){
_2ec.authority=_2ed.authority;
if(_2ec.path.charAt(0)!="/"){
var path=_2ed.path.substring(0,_2ed.path.lastIndexOf("/")+1)+_2ec.path;
var segs=path.split("/");
for(var j=0;j<segs.length;j++){
if(segs[j]=="."){
if(j==segs.length-1){
segs[j]="";
}else{
segs.splice(j,1);
j--;
}
}else{
if(j>0&&!(j==1&&segs[0]=="")&&segs[j]==".."&&segs[j-1]!=".."){
if(j==segs.length-1){
segs.splice(j,1);
segs[j-1]="";
}else{
segs.splice(j-1,2);
j-=2;
}
}
}
}
_2ec.path=segs.join("/");
}
}
}
}
uri="";
if(_2ec.scheme!=null){
uri+=_2ec.scheme+":";
}
if(_2ec.authority!=null){
uri+="//"+_2ec.authority;
}
uri+=_2ec.path;
if(_2ec.query!=null){
uri+="?"+_2ec.query;
}
if(_2ec.fragment!=null){
uri+="#"+_2ec.fragment;
}
}
this.uri=uri.toString();
var _2f1="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=this.uri.match(new RegExp(_2f1));
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
if(this.authority!=null){
_2f1="^((([^:]+:)?([^@]+))@)?([^:]*)(:([0-9]+))?$";
r=this.authority.match(new RegExp(_2f1));
this.user=r[3]||null;
this.password=r[4]||null;
this.host=r[5];
this.port=r[7]||null;
}
this.toString=function(){
return this.uri;
};
};
};
dojo.provide("dojo.uri.*");
dojo.provide("dojo.io.IframeIO");
dojo.io.createIFrame=function(_2f3,_2f4,uri){
if(window[_2f3]){
return window[_2f3];
}
if(window.frames[_2f3]){
return window.frames[_2f3];
}
var r=dojo.render.html;
var _2f7=null;
var turi=uri||dojo.uri.dojoUri("iframe_history.html?noInit=true");
var _2f9=((r.ie)&&(dojo.render.os.win))?"<iframe name=\""+_2f3+"\" src=\""+turi+"\" onload=\""+_2f4+"\">":"iframe";
_2f7=document.createElement(_2f9);
with(_2f7){
name=_2f3;
setAttribute("name",_2f3);
id=_2f3;
}
dojo.body().appendChild(_2f7);
window[_2f3]=_2f7;
with(_2f7.style){
if(!r.safari){
position="absolute";
}
left=top="0px";
height=width="1px";
visibility="hidden";
}
if(!r.ie){
dojo.io.setIFrameSrc(_2f7,turi,true);
_2f7.onload=new Function(_2f4);
}
return _2f7;
};
dojo.io.IframeTransport=new function(){
var _2fa=this;
this.currentRequest=null;
this.requestQueue=[];
this.iframeName="dojoIoIframe";
this.fireNextRequest=function(){
try{
if((this.currentRequest)||(this.requestQueue.length==0)){
return;
}
var cr=this.currentRequest=this.requestQueue.shift();
cr._contentToClean=[];
var fn=cr["formNode"];
var _2fd=cr["content"]||{};
if(cr.sendTransport){
_2fd["dojo.transport"]="iframe";
}
if(fn){
if(_2fd){
for(var x in _2fd){
if(!fn[x]){
var tn;
if(dojo.render.html.ie){
tn=document.createElement("<input type='hidden' name='"+x+"' value='"+_2fd[x]+"'>");
fn.appendChild(tn);
}else{
tn=document.createElement("input");
fn.appendChild(tn);
tn.type="hidden";
tn.name=x;
tn.value=_2fd[x];
}
cr._contentToClean.push(x);
}else{
fn[x].value=_2fd[x];
}
}
}
if(cr["url"]){
cr._originalAction=fn.getAttribute("action");
fn.setAttribute("action",cr.url);
}
if(!fn.getAttribute("method")){
fn.setAttribute("method",(cr["method"])?cr["method"]:"post");
}
cr._originalTarget=fn.getAttribute("target");
fn.setAttribute("target",this.iframeName);
fn.target=this.iframeName;
fn.submit();
}else{
var _300=dojo.io.argsFromMap(this.currentRequest.content);
var _301=cr.url+(cr.url.indexOf("?")>-1?"&":"?")+_300;
dojo.io.setIFrameSrc(this.iframe,_301,true);
}
}
catch(e){
this.iframeOnload(e);
}
};
this.canHandle=function(_302){
return ((dojo.lang.inArray(["text/plain","text/html","text/javascript","text/json","application/json"],_302["mimetype"]))&&(dojo.lang.inArray(["post","get"],_302["method"].toLowerCase()))&&(!((_302["sync"])&&(_302["sync"]==true))));
};
this.bind=function(_303){
if(!this["iframe"]){
this.setUpIframe();
}
this.requestQueue.push(_303);
this.fireNextRequest();
return;
};
this.setUpIframe=function(){
this.iframe=dojo.io.createIFrame(this.iframeName,"dojo.io.IframeTransport.iframeOnload();");
};
this.iframeOnload=function(_304){
if(!_2fa.currentRequest){
_2fa.fireNextRequest();
return;
}
var req=_2fa.currentRequest;
if(req.formNode){
var _306=req._contentToClean;
for(var i=0;i<_306.length;i++){
var key=_306[i];
if(dojo.render.html.safari){
var _309=req.formNode;
for(var j=0;j<_309.childNodes.length;j++){
var _30b=_309.childNodes[j];
if(_30b.name==key){
var _30c=_30b.parentNode;
_30c.removeChild(_30b);
break;
}
}
}else{
var _30d=req.formNode[key];
req.formNode.removeChild(_30d);
req.formNode[key]=null;
}
}
if(req["_originalAction"]){
req.formNode.setAttribute("action",req._originalAction);
}
if(req["_originalTarget"]){
req.formNode.setAttribute("target",req._originalTarget);
req.formNode.target=req._originalTarget;
}
}
var _30e=function(_30f){
var doc=_30f.contentDocument||((_30f.contentWindow)&&(_30f.contentWindow.document))||((_30f.name)&&(document.frames[_30f.name])&&(document.frames[_30f.name].document))||null;
return doc;
};
var _311;
var _312=false;
if(_304){
this._callError(req,"IframeTransport Request Error: "+_304);
}else{
var ifd=_30e(_2fa.iframe);
try{
var cmt=req.mimetype;
if((cmt=="text/javascript")||(cmt=="text/json")||(cmt=="application/json")){
var js=ifd.getElementsByTagName("textarea")[0].value;
if(cmt=="text/json"||cmt=="application/json"){
js="("+js+")";
}
_311=dj_eval(js);
}else{
if(cmt=="text/html"){
_311=ifd;
}else{
_311=ifd.getElementsByTagName("textarea")[0].value;
}
}
_312=true;
}
catch(e){
this._callError(req,"IframeTransport Error: "+e);
}
}
try{
if(_312&&dojo.lang.isFunction(req["load"])){
req.load("load",_311,req);
}
}
catch(e){
throw e;
}
finally{
_2fa.currentRequest=null;
_2fa.fireNextRequest();
}
};
this._callError=function(req,_317){
var _318=new dojo.io.Error(_317);
if(dojo.lang.isFunction(req["error"])){
req.error("error",_318,req);
}
};
dojo.io.transports.addTransport("IframeTransport");
};
dojo.provide("dojo.event.common");
dojo.event=new function(){
this._canTimeout=dojo.lang.isFunction(dj_global["setTimeout"])||dojo.lang.isAlien(dj_global["setTimeout"]);
function interpolateArgs(args,_31a){
var dl=dojo.lang;
var ao={srcObj:dj_global,srcFunc:null,adviceObj:dj_global,adviceFunc:null,aroundObj:null,aroundFunc:null,adviceType:(args.length>2)?args[0]:"after",precedence:"last",once:false,delay:null,rate:0,adviceMsg:false};
switch(args.length){
case 0:
return;
case 1:
return;
case 2:
ao.srcFunc=args[0];
ao.adviceFunc=args[1];
break;
case 3:
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isString(args[1]))&&(dl.isString(args[2]))){
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
}else{
if((dl.isObject(args[0]))&&(dl.isString(args[1]))&&(dl.isFunction(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
var _31d=dl.nameAnonFunc(args[2],ao.adviceObj,_31a);
ao.adviceFunc=_31d;
}else{
if((dl.isFunction(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))){
ao.adviceType="after";
ao.srcObj=dj_global;
var _31d=dl.nameAnonFunc(args[0],ao.srcObj,_31a);
ao.srcFunc=_31d;
ao.adviceObj=args[1];
ao.adviceFunc=args[2];
}
}
}
}
break;
case 4:
if((dl.isObject(args[0]))&&(dl.isObject(args[2]))){
ao.adviceType="after";
ao.srcObj=args[0];
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isString(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isFunction(args[1]))&&(dl.isObject(args[2]))){
ao.adviceType=args[0];
ao.srcObj=dj_global;
var _31d=dl.nameAnonFunc(args[1],dj_global,_31a);
ao.srcFunc=_31d;
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
if((dl.isString(args[0]))&&(dl.isObject(args[1]))&&(dl.isString(args[2]))&&(dl.isFunction(args[3]))){
ao.srcObj=args[1];
ao.srcFunc=args[2];
var _31d=dl.nameAnonFunc(args[3],dj_global,_31a);
ao.adviceObj=dj_global;
ao.adviceFunc=_31d;
}else{
if(dl.isObject(args[1])){
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=dj_global;
ao.adviceFunc=args[3];
}else{
if(dl.isObject(args[2])){
ao.srcObj=dj_global;
ao.srcFunc=args[1];
ao.adviceObj=args[2];
ao.adviceFunc=args[3];
}else{
ao.srcObj=ao.adviceObj=ao.aroundObj=dj_global;
ao.srcFunc=args[1];
ao.adviceFunc=args[2];
ao.aroundFunc=args[3];
}
}
}
}
}
}
break;
case 6:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundFunc=args[5];
ao.aroundObj=dj_global;
break;
default:
ao.srcObj=args[1];
ao.srcFunc=args[2];
ao.adviceObj=args[3];
ao.adviceFunc=args[4];
ao.aroundObj=args[5];
ao.aroundFunc=args[6];
ao.once=args[7];
ao.delay=args[8];
ao.rate=args[9];
ao.adviceMsg=args[10];
break;
}
if(dl.isFunction(ao.aroundFunc)){
var _31d=dl.nameAnonFunc(ao.aroundFunc,ao.aroundObj,_31a);
ao.aroundFunc=_31d;
}
if(dl.isFunction(ao.srcFunc)){
ao.srcFunc=dl.getNameInObj(ao.srcObj,ao.srcFunc);
}
if(dl.isFunction(ao.adviceFunc)){
ao.adviceFunc=dl.getNameInObj(ao.adviceObj,ao.adviceFunc);
}
if((ao.aroundObj)&&(dl.isFunction(ao.aroundFunc))){
ao.aroundFunc=dl.getNameInObj(ao.aroundObj,ao.aroundFunc);
}
if(!ao.srcObj){
dojo.raise("bad srcObj for srcFunc: "+ao.srcFunc);
}
if(!ao.adviceObj){
dojo.raise("bad adviceObj for adviceFunc: "+ao.adviceFunc);
}
if(!ao.adviceFunc){
dojo.debug("bad adviceFunc for srcFunc: "+ao.srcFunc);
dojo.debugShallow(ao);
}
return ao;
}
this.connect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.connect(ao);
}
ao.srcFunc="onkeypress";
}
if(dojo.lang.isArray(ao.srcObj)&&ao.srcObj!=""){
var _31f={};
for(var x in ao){
_31f[x]=ao[x];
}
var mjps=[];
dojo.lang.forEach(ao.srcObj,function(src){
if((dojo.render.html.capable)&&(dojo.lang.isString(src))){
src=dojo.byId(src);
}
_31f.srcObj=src;
mjps.push(dojo.event.connect.call(dojo.event,_31f));
});
return mjps;
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
if(ao.adviceFunc){
var mjp2=dojo.event.MethodJoinPoint.getForMethod(ao.adviceObj,ao.adviceFunc);
}
mjp.kwAddAdvice(ao);
return mjp;
};
this.log=function(a1,a2){
var _327;
if((arguments.length==1)&&(typeof a1=="object")){
_327=a1;
}else{
_327={srcObj:a1,srcFunc:a2};
}
_327.adviceFunc=function(){
var _328=[];
for(var x=0;x<arguments.length;x++){
_328.push(arguments[x]);
}
dojo.debug("("+_327.srcObj+")."+_327.srcFunc,":",_328.join(", "));
};
this.kwConnect(_327);
};
this.connectBefore=function(){
var args=["before"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectAround=function(){
var args=["around"];
for(var i=0;i<arguments.length;i++){
args.push(arguments[i]);
}
return this.connect.apply(this,args);
};
this.connectOnce=function(){
var ao=interpolateArgs(arguments,true);
ao.once=true;
return this.connect(ao);
};
this._kwConnectImpl=function(_32f,_330){
var fn=(_330)?"disconnect":"connect";
if(typeof _32f["srcFunc"]=="function"){
_32f.srcObj=_32f["srcObj"]||dj_global;
var _332=dojo.lang.nameAnonFunc(_32f.srcFunc,_32f.srcObj,true);
_32f.srcFunc=_332;
}
if(typeof _32f["adviceFunc"]=="function"){
_32f.adviceObj=_32f["adviceObj"]||dj_global;
var _332=dojo.lang.nameAnonFunc(_32f.adviceFunc,_32f.adviceObj,true);
_32f.adviceFunc=_332;
}
_32f.srcObj=_32f["srcObj"]||dj_global;
_32f.adviceObj=_32f["adviceObj"]||_32f["targetObj"]||dj_global;
_32f.adviceFunc=_32f["adviceFunc"]||_32f["targetFunc"];
return dojo.event[fn](_32f);
};
this.kwConnect=function(_333){
return this._kwConnectImpl(_333,false);
};
this.disconnect=function(){
if(arguments.length==1){
var ao=arguments[0];
}else{
var ao=interpolateArgs(arguments,true);
}
if(!ao.adviceFunc){
return;
}
if(dojo.lang.isString(ao.srcFunc)&&(ao.srcFunc.toLowerCase()=="onkey")){
if(dojo.render.html.ie){
ao.srcFunc="onkeydown";
this.disconnect(ao);
}
ao.srcFunc="onkeypress";
}
var mjp=dojo.event.MethodJoinPoint.getForMethod(ao.srcObj,ao.srcFunc);
return mjp.removeAdvice(ao.adviceObj,ao.adviceFunc,ao.adviceType,ao.once);
};
this.kwDisconnect=function(_336){
return this._kwConnectImpl(_336,true);
};
};
dojo.event.MethodInvocation=function(_337,obj,args){
this.jp_=_337;
this.object=obj;
this.args=[];
for(var x=0;x<args.length;x++){
this.args[x]=args[x];
}
this.around_index=-1;
};
dojo.event.MethodInvocation.prototype.proceed=function(){
this.around_index++;
if(this.around_index>=this.jp_.around.length){
return this.jp_.object[this.jp_.methodname].apply(this.jp_.object,this.args);
}else{
var ti=this.jp_.around[this.around_index];
var mobj=ti[0]||dj_global;
var meth=ti[1];
return mobj[meth].call(mobj,this);
}
};
dojo.event.MethodJoinPoint=function(obj,_33f){
this.object=obj||dj_global;
this.methodname=_33f;
this.methodfunc=this.object[_33f];
this.squelch=false;
};
dojo.event.MethodJoinPoint.getForMethod=function(obj,_341){
if(!obj){
obj=dj_global;
}
if(!obj[_341]){
obj[_341]=function(){
};
if(!obj[_341]){
dojo.raise("Cannot set do-nothing method on that object "+_341);
}
}else{
if((!dojo.lang.isFunction(obj[_341]))&&(!dojo.lang.isAlien(obj[_341]))){
return null;
}
}
var _342=_341+"$joinpoint";
var _343=_341+"$joinpoint$method";
var _344=obj[_342];
if(!_344){
var _345=false;
if(dojo.event["browser"]){
if((obj["attachEvent"])||(obj["nodeType"])||(obj["addEventListener"])){
_345=true;
dojo.event.browser.addClobberNodeAttrs(obj,[_342,_343,_341]);
}
}
var _346=obj[_341].length;
obj[_343]=obj[_341];
_344=obj[_342]=new dojo.event.MethodJoinPoint(obj,_343);
obj[_341]=function(){
var args=[];
if((_345)&&(!arguments.length)){
var evt=null;
try{
if(obj.ownerDocument){
evt=obj.ownerDocument.parentWindow.event;
}else{
if(obj.documentElement){
evt=obj.documentElement.ownerDocument.parentWindow.event;
}else{
if(obj.event){
evt=obj.event;
}else{
evt=window.event;
}
}
}
}
catch(e){
evt=window.event;
}
if(evt){
args.push(dojo.event.browser.fixEvent(evt,this));
}
}else{
for(var x=0;x<arguments.length;x++){
if((x==0)&&(_345)&&(dojo.event.browser.isEvent(arguments[x]))){
args.push(dojo.event.browser.fixEvent(arguments[x],this));
}else{
args.push(arguments[x]);
}
}
}
return _344.run.apply(_344,args);
};
obj[_341].__preJoinArity=_346;
}
return _344;
};
dojo.lang.extend(dojo.event.MethodJoinPoint,{unintercept:function(){
this.object[this.methodname]=this.methodfunc;
this.before=[];
this.after=[];
this.around=[];
},disconnect:dojo.lang.forward("unintercept"),run:function(){
var obj=this.object||dj_global;
var args=arguments;
var _34c=[];
for(var x=0;x<args.length;x++){
_34c[x]=args[x];
}
var _34e=function(marr){
if(!marr){
dojo.debug("Null argument to unrollAdvice()");
return;
}
var _350=marr[0]||dj_global;
var _351=marr[1];
if(!_350[_351]){
dojo.raise("function \""+_351+"\" does not exist on \""+_350+"\"");
}
var _352=marr[2]||dj_global;
var _353=marr[3];
var msg=marr[6];
var _355;
var to={args:[],jp_:this,object:obj,proceed:function(){
return _350[_351].apply(_350,to.args);
}};
to.args=_34c;
var _357=parseInt(marr[4]);
var _358=((!isNaN(_357))&&(marr[4]!==null)&&(typeof marr[4]!="undefined"));
if(marr[5]){
var rate=parseInt(marr[5]);
var cur=new Date();
var _35b=false;
if((marr["last"])&&((cur-marr.last)<=rate)){
if(dojo.event._canTimeout){
if(marr["delayTimer"]){
clearTimeout(marr.delayTimer);
}
var tod=parseInt(rate*2);
var mcpy=dojo.lang.shallowCopy(marr);
marr.delayTimer=setTimeout(function(){
mcpy[5]=0;
_34e(mcpy);
},tod);
}
return;
}else{
marr.last=cur;
}
}
if(_353){
_352[_353].call(_352,to);
}else{
if((_358)&&((dojo.render.html)||(dojo.render.svg))){
dj_global["setTimeout"](function(){
if(msg){
_350[_351].call(_350,to);
}else{
_350[_351].apply(_350,args);
}
},_357);
}else{
if(msg){
_350[_351].call(_350,to);
}else{
_350[_351].apply(_350,args);
}
}
}
};
var _35e=function(){
if(this.squelch){
try{
return _34e.apply(this,arguments);
}
catch(e){
dojo.debug(e);
}
}else{
return _34e.apply(this,arguments);
}
};
if((this["before"])&&(this.before.length>0)){
dojo.lang.forEach(this.before.concat(new Array()),_35e);
}
var _35f;
try{
if((this["around"])&&(this.around.length>0)){
var mi=new dojo.event.MethodInvocation(this,obj,args);
_35f=mi.proceed();
}else{
if(this.methodfunc){
_35f=this.object[this.methodname].apply(this.object,args);
}
}
}
catch(e){
if(!this.squelch){
dojo.debug(e);
}
}
if((this["after"])&&(this.after.length>0)){
dojo.lang.forEach(this.after.concat(new Array()),_35e);
}
return (this.methodfunc)?_35f:null;
},getArr:function(kind){
var type="after";
if((typeof kind=="string")&&(kind.indexOf("before")!=-1)){
type="before";
}else{
if(kind=="around"){
type="around";
}
}
if(!this[type]){
this[type]=[];
}
return this[type];
},kwAddAdvice:function(args){
this.addAdvice(args["adviceObj"],args["adviceFunc"],args["aroundObj"],args["aroundFunc"],args["adviceType"],args["precedence"],args["once"],args["delay"],args["rate"],args["adviceMsg"]);
},addAdvice:function(_364,_365,_366,_367,_368,_369,once,_36b,rate,_36d){
var arr=this.getArr(_368);
if(!arr){
dojo.raise("bad this: "+this);
}
var ao=[_364,_365,_366,_367,_36b,rate,_36d];
if(once){
if(this.hasAdvice(_364,_365,_368,arr)>=0){
return;
}
}
if(_369=="first"){
arr.unshift(ao);
}else{
arr.push(ao);
}
},hasAdvice:function(_370,_371,_372,arr){
if(!arr){
arr=this.getArr(_372);
}
var ind=-1;
for(var x=0;x<arr.length;x++){
var aao=(typeof _371=="object")?(new String(_371)).toString():_371;
var a1o=(typeof arr[x][1]=="object")?(new String(arr[x][1])).toString():arr[x][1];
if((arr[x][0]==_370)&&(a1o==aao)){
ind=x;
}
}
return ind;
},removeAdvice:function(_378,_379,_37a,once){
var arr=this.getArr(_37a);
var ind=this.hasAdvice(_378,_379,_37a,arr);
if(ind==-1){
return false;
}
while(ind!=-1){
arr.splice(ind,1);
if(once){
break;
}
ind=this.hasAdvice(_378,_379,_37a,arr);
}
return true;
}});
dojo.provide("dojo.event.topic");
dojo.event.topic=new function(){
this.topics={};
this.getTopic=function(_37e){
if(!this.topics[_37e]){
this.topics[_37e]=new this.TopicImpl(_37e);
}
return this.topics[_37e];
};
this.registerPublisher=function(_37f,obj,_381){
var _37f=this.getTopic(_37f);
_37f.registerPublisher(obj,_381);
};
this.subscribe=function(_382,obj,_384){
var _382=this.getTopic(_382);
_382.subscribe(obj,_384);
};
this.unsubscribe=function(_385,obj,_387){
var _385=this.getTopic(_385);
_385.unsubscribe(obj,_387);
};
this.destroy=function(_388){
this.getTopic(_388).destroy();
delete this.topics[_388];
};
this.publishApply=function(_389,args){
var _389=this.getTopic(_389);
_389.sendMessage.apply(_389,args);
};
this.publish=function(_38b,_38c){
var _38b=this.getTopic(_38b);
var args=[];
for(var x=1;x<arguments.length;x++){
args.push(arguments[x]);
}
_38b.sendMessage.apply(_38b,args);
};
};
dojo.event.topic.TopicImpl=function(_38f){
this.topicName=_38f;
this.subscribe=function(_390,_391){
var tf=_391||_390;
var to=(!_391)?dj_global:_390;
return dojo.event.kwConnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this.unsubscribe=function(_394,_395){
var tf=(!_395)?_394:_395;
var to=(!_395)?null:_394;
return dojo.event.kwDisconnect({srcObj:this,srcFunc:"sendMessage",adviceObj:to,adviceFunc:tf});
};
this._getJoinPoint=function(){
return dojo.event.MethodJoinPoint.getForMethod(this,"sendMessage");
};
this.setSquelch=function(_398){
this._getJoinPoint().squelch=_398;
};
this.destroy=function(){
this._getJoinPoint().disconnect();
};
this.registerPublisher=function(_399,_39a){
dojo.event.connect(_399,_39a,this,"sendMessage");
};
this.sendMessage=function(_39b){
};
};
dojo.provide("dojo.event.browser");
dojo._ie_clobber=new function(){
this.clobberNodes=[];
function nukeProp(node,prop){
try{
node[prop]=null;
}
catch(e){
}
try{
delete node[prop];
}
catch(e){
}
try{
node.removeAttribute(prop);
}
catch(e){
}
}
this.clobber=function(_39e){
var na;
var tna;
if(_39e){
tna=_39e.all||_39e.getElementsByTagName("*");
na=[_39e];
for(var x=0;x<tna.length;x++){
if(tna[x]["__doClobber__"]){
na.push(tna[x]);
}
}
}else{
try{
window.onload=null;
}
catch(e){
}
na=(this.clobberNodes.length)?this.clobberNodes:document.all;
}
tna=null;
var _3a2={};
for(var i=na.length-1;i>=0;i=i-1){
var el=na[i];
try{
if(el&&el["__clobberAttrs__"]){
for(var j=0;j<el.__clobberAttrs__.length;j++){
nukeProp(el,el.__clobberAttrs__[j]);
}
nukeProp(el,"__clobberAttrs__");
nukeProp(el,"__doClobber__");
}
}
catch(e){
}
}
na=null;
};
};
if(dojo.render.html.ie){
dojo.addOnUnload(function(){
dojo._ie_clobber.clobber();
try{
if((dojo["widget"])&&(dojo.widget["manager"])){
dojo.widget.manager.destroyAll();
}
}
catch(e){
}
try{
window.onload=null;
}
catch(e){
}
try{
window.onunload=null;
}
catch(e){
}
dojo._ie_clobber.clobberNodes=[];
});
}
dojo.event.browser=new function(){
var _3a6=0;
this.normalizedEventName=function(_3a7){
switch(_3a7){
case "CheckboxStateChange":
case "DOMAttrModified":
case "DOMMenuItemActive":
case "DOMMenuItemInactive":
case "DOMMouseScroll":
case "DOMNodeInserted":
case "DOMNodeRemoved":
case "RadioStateChange":
return _3a7;
break;
default:
return _3a7.toLowerCase();
break;
}
};
this.clean=function(node){
if(dojo.render.html.ie){
dojo._ie_clobber.clobber(node);
}
};
this.addClobberNode=function(node){
if(!dojo.render.html.ie){
return;
}
if(!node["__doClobber__"]){
node.__doClobber__=true;
dojo._ie_clobber.clobberNodes.push(node);
node.__clobberAttrs__=[];
}
};
this.addClobberNodeAttrs=function(node,_3ab){
if(!dojo.render.html.ie){
return;
}
this.addClobberNode(node);
for(var x=0;x<_3ab.length;x++){
node.__clobberAttrs__.push(_3ab[x]);
}
};
this.removeListener=function(node,_3ae,fp,_3b0){
if(!_3b0){
var _3b0=false;
}
_3ae=dojo.event.browser.normalizedEventName(_3ae);
if((_3ae=="onkey")||(_3ae=="key")){
if(dojo.render.html.ie){
this.removeListener(node,"onkeydown",fp,_3b0);
}
_3ae="onkeypress";
}
if(_3ae.substr(0,2)=="on"){
_3ae=_3ae.substr(2);
}
if(node.removeEventListener){
node.removeEventListener(_3ae,fp,_3b0);
}
};
this.addListener=function(node,_3b2,fp,_3b4,_3b5){
if(!node){
return;
}
if(!_3b4){
var _3b4=false;
}
_3b2=dojo.event.browser.normalizedEventName(_3b2);
if((_3b2=="onkey")||(_3b2=="key")){
if(dojo.render.html.ie){
this.addListener(node,"onkeydown",fp,_3b4,_3b5);
}
_3b2="onkeypress";
}
if(_3b2.substr(0,2)!="on"){
_3b2="on"+_3b2;
}
if(!_3b5){
var _3b6=function(evt){
if(!evt){
evt=window.event;
}
var ret=fp(dojo.event.browser.fixEvent(evt,this));
if(_3b4){
dojo.event.browser.stopEvent(evt);
}
return ret;
};
}else{
_3b6=fp;
}
if(node.addEventListener){
node.addEventListener(_3b2.substr(2),_3b6,_3b4);
return _3b6;
}else{
if(typeof node[_3b2]=="function"){
var _3b9=node[_3b2];
node[_3b2]=function(e){
_3b9(e);
return _3b6(e);
};
}else{
node[_3b2]=_3b6;
}
if(dojo.render.html.ie){
this.addClobberNodeAttrs(node,[_3b2]);
}
return _3b6;
}
};
this.isEvent=function(obj){
return (typeof obj!="undefined")&&(typeof Event!="undefined")&&(obj.eventPhase);
};
this.currentEvent=null;
this.callListener=function(_3bc,_3bd){
if(typeof _3bc!="function"){
dojo.raise("listener not a function: "+_3bc);
}
dojo.event.browser.currentEvent.currentTarget=_3bd;
return _3bc.call(_3bd,dojo.event.browser.currentEvent);
};
this._stopPropagation=function(){
dojo.event.browser.currentEvent.cancelBubble=true;
};
this._preventDefault=function(){
dojo.event.browser.currentEvent.returnValue=false;
};
this.keys={KEY_BACKSPACE:8,KEY_TAB:9,KEY_CLEAR:12,KEY_ENTER:13,KEY_SHIFT:16,KEY_CTRL:17,KEY_ALT:18,KEY_PAUSE:19,KEY_CAPS_LOCK:20,KEY_ESCAPE:27,KEY_SPACE:32,KEY_PAGE_UP:33,KEY_PAGE_DOWN:34,KEY_END:35,KEY_HOME:36,KEY_LEFT_ARROW:37,KEY_UP_ARROW:38,KEY_RIGHT_ARROW:39,KEY_DOWN_ARROW:40,KEY_INSERT:45,KEY_DELETE:46,KEY_HELP:47,KEY_LEFT_WINDOW:91,KEY_RIGHT_WINDOW:92,KEY_SELECT:93,KEY_NUMPAD_0:96,KEY_NUMPAD_1:97,KEY_NUMPAD_2:98,KEY_NUMPAD_3:99,KEY_NUMPAD_4:100,KEY_NUMPAD_5:101,KEY_NUMPAD_6:102,KEY_NUMPAD_7:103,KEY_NUMPAD_8:104,KEY_NUMPAD_9:105,KEY_NUMPAD_MULTIPLY:106,KEY_NUMPAD_PLUS:107,KEY_NUMPAD_ENTER:108,KEY_NUMPAD_MINUS:109,KEY_NUMPAD_PERIOD:110,KEY_NUMPAD_DIVIDE:111,KEY_F1:112,KEY_F2:113,KEY_F3:114,KEY_F4:115,KEY_F5:116,KEY_F6:117,KEY_F7:118,KEY_F8:119,KEY_F9:120,KEY_F10:121,KEY_F11:122,KEY_F12:123,KEY_F13:124,KEY_F14:125,KEY_F15:126,KEY_NUM_LOCK:144,KEY_SCROLL_LOCK:145};
this.revKeys=[];
for(var key in this.keys){
this.revKeys[this.keys[key]]=key;
}
this.fixEvent=function(evt,_3c0){
if(!evt){
if(window["event"]){
evt=window.event;
}
}
if((evt["type"])&&(evt["type"].indexOf("key")==0)){
evt.keys=this.revKeys;
for(var key in this.keys){
evt[key]=this.keys[key];
}
if(evt["type"]=="keydown"&&dojo.render.html.ie){
switch(evt.keyCode){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_LEFT_WINDOW:
case evt.KEY_RIGHT_WINDOW:
case evt.KEY_SELECT:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
case evt.KEY_NUMPAD_0:
case evt.KEY_NUMPAD_1:
case evt.KEY_NUMPAD_2:
case evt.KEY_NUMPAD_3:
case evt.KEY_NUMPAD_4:
case evt.KEY_NUMPAD_5:
case evt.KEY_NUMPAD_6:
case evt.KEY_NUMPAD_7:
case evt.KEY_NUMPAD_8:
case evt.KEY_NUMPAD_9:
case evt.KEY_NUMPAD_PERIOD:
break;
case evt.KEY_NUMPAD_MULTIPLY:
case evt.KEY_NUMPAD_PLUS:
case evt.KEY_NUMPAD_ENTER:
case evt.KEY_NUMPAD_MINUS:
case evt.KEY_NUMPAD_DIVIDE:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
case evt.KEY_PAGE_UP:
case evt.KEY_PAGE_DOWN:
case evt.KEY_END:
case evt.KEY_HOME:
case evt.KEY_LEFT_ARROW:
case evt.KEY_UP_ARROW:
case evt.KEY_RIGHT_ARROW:
case evt.KEY_DOWN_ARROW:
case evt.KEY_INSERT:
case evt.KEY_DELETE:
case evt.KEY_F1:
case evt.KEY_F2:
case evt.KEY_F3:
case evt.KEY_F4:
case evt.KEY_F5:
case evt.KEY_F6:
case evt.KEY_F7:
case evt.KEY_F8:
case evt.KEY_F9:
case evt.KEY_F10:
case evt.KEY_F11:
case evt.KEY_F12:
case evt.KEY_F12:
case evt.KEY_F13:
case evt.KEY_F14:
case evt.KEY_F15:
case evt.KEY_CLEAR:
case evt.KEY_HELP:
evt.key=evt.keyCode;
break;
default:
if(evt.ctrlKey||evt.altKey){
var _3c2=evt.keyCode;
if(_3c2>=65&&_3c2<=90&&evt.shiftKey==false){
_3c2+=32;
}
if(_3c2>=1&&_3c2<=26&&evt.ctrlKey){
_3c2+=96;
}
evt.key=String.fromCharCode(_3c2);
}
}
}else{
if(evt["type"]=="keypress"){
if(dojo.render.html.opera){
if(evt.which==0){
evt.key=evt.keyCode;
}else{
if(evt.which>0){
switch(evt.which){
case evt.KEY_SHIFT:
case evt.KEY_CTRL:
case evt.KEY_ALT:
case evt.KEY_CAPS_LOCK:
case evt.KEY_NUM_LOCK:
case evt.KEY_SCROLL_LOCK:
break;
case evt.KEY_PAUSE:
case evt.KEY_TAB:
case evt.KEY_BACKSPACE:
case evt.KEY_ENTER:
case evt.KEY_ESCAPE:
evt.key=evt.which;
break;
default:
var _3c2=evt.which;
if((evt.ctrlKey||evt.altKey||evt.metaKey)&&(evt.which>=65&&evt.which<=90&&evt.shiftKey==false)){
_3c2+=32;
}
evt.key=String.fromCharCode(_3c2);
}
}
}
}else{
if(dojo.render.html.ie){
if(!evt.ctrlKey&&!evt.altKey&&evt.keyCode>=evt.KEY_SPACE){
evt.key=String.fromCharCode(evt.keyCode);
}
}else{
if(dojo.render.html.safari){
switch(evt.keyCode){
case 63232:
evt.key=evt.KEY_UP_ARROW;
break;
case 63233:
evt.key=evt.KEY_DOWN_ARROW;
break;
case 63234:
evt.key=evt.KEY_LEFT_ARROW;
break;
case 63235:
evt.key=evt.KEY_RIGHT_ARROW;
break;
default:
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}else{
evt.key=evt.charCode>0?String.fromCharCode(evt.charCode):evt.keyCode;
}
}
}
}
}
}
if(dojo.render.html.ie){
if(!evt.target){
evt.target=evt.srcElement;
}
if(!evt.currentTarget){
evt.currentTarget=(_3c0?_3c0:evt.srcElement);
}
if(!evt.layerX){
evt.layerX=evt.offsetX;
}
if(!evt.layerY){
evt.layerY=evt.offsetY;
}
var doc=(evt.srcElement&&evt.srcElement.ownerDocument)?evt.srcElement.ownerDocument:document;
var _3c4=((dojo.render.html.ie55)||(doc["compatMode"]=="BackCompat"))?doc.body:doc.documentElement;
if(!evt.pageX){
evt.pageX=evt.clientX+(_3c4.scrollLeft||0);
}
if(!evt.pageY){
evt.pageY=evt.clientY+(_3c4.scrollTop||0);
}
if(evt.type=="mouseover"){
evt.relatedTarget=evt.fromElement;
}
if(evt.type=="mouseout"){
evt.relatedTarget=evt.toElement;
}
this.currentEvent=evt;
evt.callListener=this.callListener;
evt.stopPropagation=this._stopPropagation;
evt.preventDefault=this._preventDefault;
}
return evt;
};
this.stopEvent=function(evt){
if(window.event){
evt.returnValue=false;
evt.cancelBubble=true;
}else{
evt.preventDefault();
evt.stopPropagation();
}
};
};
dojo.provide("dojo.event.*");
dojo.provide("dojo.gfx.color");
dojo.gfx.color.Color=function(r,g,b,a){
if(dojo.lang.isArray(r)){
this.r=r[0];
this.g=r[1];
this.b=r[2];
this.a=r[3]||1;
}else{
if(dojo.lang.isString(r)){
var rgb=dojo.gfx.color.extractRGB(r);
this.r=rgb[0];
this.g=rgb[1];
this.b=rgb[2];
this.a=g||1;
}else{
if(r instanceof dojo.gfx.color.Color){
this.r=r.r;
this.b=r.b;
this.g=r.g;
this.a=r.a;
}else{
this.r=r;
this.g=g;
this.b=b;
this.a=a;
}
}
}
};
dojo.gfx.color.Color.fromArray=function(arr){
return new dojo.gfx.color.Color(arr[0],arr[1],arr[2],arr[3]);
};
dojo.extend(dojo.gfx.color.Color,{toRgb:function(_3cc){
if(_3cc){
return this.toRgba();
}else{
return [this.r,this.g,this.b];
}
},toRgba:function(){
return [this.r,this.g,this.b,this.a];
},toHex:function(){
return dojo.gfx.color.rgb2hex(this.toRgb());
},toCss:function(){
return "rgb("+this.toRgb().join()+")";
},toString:function(){
return this.toHex();
},blend:function(_3cd,_3ce){
var rgb=null;
if(dojo.lang.isArray(_3cd)){
rgb=_3cd;
}else{
if(_3cd instanceof dojo.gfx.color.Color){
rgb=_3cd.toRgb();
}else{
rgb=new dojo.gfx.color.Color(_3cd).toRgb();
}
}
return dojo.gfx.color.blend(this.toRgb(),rgb,_3ce);
}});
dojo.gfx.color.named={white:[255,255,255],black:[0,0,0],red:[255,0,0],green:[0,255,0],lime:[0,255,0],blue:[0,0,255],navy:[0,0,128],gray:[128,128,128],silver:[192,192,192]};
dojo.gfx.color.blend=function(a,b,_3d2){
if(typeof a=="string"){
return dojo.gfx.color.blendHex(a,b,_3d2);
}
if(!_3d2){
_3d2=0;
}
_3d2=Math.min(Math.max(-1,_3d2),1);
_3d2=((_3d2+1)/2);
var c=[];
for(var x=0;x<3;x++){
c[x]=parseInt(b[x]+((a[x]-b[x])*_3d2));
}
return c;
};
dojo.gfx.color.blendHex=function(a,b,_3d7){
return dojo.gfx.color.rgb2hex(dojo.gfx.color.blend(dojo.gfx.color.hex2rgb(a),dojo.gfx.color.hex2rgb(b),_3d7));
};
dojo.gfx.color.extractRGB=function(_3d8){
var hex="0123456789abcdef";
_3d8=_3d8.toLowerCase();
if(_3d8.indexOf("rgb")==0){
var _3da=_3d8.match(/rgba*\((\d+), *(\d+), *(\d+)/i);
var ret=_3da.splice(1,3);
return ret;
}else{
var _3dc=dojo.gfx.color.hex2rgb(_3d8);
if(_3dc){
return _3dc;
}else{
return dojo.gfx.color.named[_3d8]||[255,255,255];
}
}
};
dojo.gfx.color.hex2rgb=function(hex){
var _3de="0123456789ABCDEF";
var rgb=new Array(3);
if(hex.indexOf("#")==0){
hex=hex.substring(1);
}
hex=hex.toUpperCase();
if(hex.replace(new RegExp("["+_3de+"]","g"),"")!=""){
return null;
}
if(hex.length==3){
rgb[0]=hex.charAt(0)+hex.charAt(0);
rgb[1]=hex.charAt(1)+hex.charAt(1);
rgb[2]=hex.charAt(2)+hex.charAt(2);
}else{
rgb[0]=hex.substring(0,2);
rgb[1]=hex.substring(2,4);
rgb[2]=hex.substring(4);
}
for(var i=0;i<rgb.length;i++){
rgb[i]=_3de.indexOf(rgb[i].charAt(0))*16+_3de.indexOf(rgb[i].charAt(1));
}
return rgb;
};
dojo.gfx.color.rgb2hex=function(r,g,b){
if(dojo.lang.isArray(r)){
g=r[1]||0;
b=r[2]||0;
r=r[0]||0;
}
var ret=dojo.lang.map([r,g,b],function(x){
x=new Number(x);
var s=x.toString(16);
while(s.length<2){
s="0"+s;
}
return s;
});
ret.unshift("#");
return ret.join("");
};
dojo.provide("dojo.lfx.Animation");
dojo.lfx.Line=function(_3e7,end){
this.start=_3e7;
this.end=end;
if(dojo.lang.isArray(_3e7)){
var diff=[];
dojo.lang.forEach(this.start,function(s,i){
diff[i]=this.end[i]-s;
},this);
this.getValue=function(n){
var res=[];
dojo.lang.forEach(this.start,function(s,i){
res[i]=(diff[i]*n)+s;
},this);
return res;
};
}else{
var diff=end-_3e7;
this.getValue=function(n){
return (diff*n)+this.start;
};
}
};
dojo.lfx.easeDefault=function(n){
if(dojo.render.html.khtml){
return (parseFloat("0.5")+((Math.sin((n+parseFloat("1.5"))*Math.PI))/2));
}else{
return (0.5+((Math.sin((n+1.5)*Math.PI))/2));
}
};
dojo.lfx.easeIn=function(n){
return Math.pow(n,3);
};
dojo.lfx.easeOut=function(n){
return (1-Math.pow(1-n,3));
};
dojo.lfx.easeInOut=function(n){
return ((3*Math.pow(n,2))-(2*Math.pow(n,3)));
};
dojo.lfx.IAnimation=function(){
};
dojo.lang.extend(dojo.lfx.IAnimation,{curve:null,duration:1000,easing:null,repeatCount:0,rate:25,handler:null,beforeBegin:null,onBegin:null,onAnimate:null,onEnd:null,onPlay:null,onPause:null,onStop:null,play:null,pause:null,stop:null,connect:function(evt,_3f6,_3f7){
if(!_3f7){
_3f7=_3f6;
_3f6=this;
}
_3f7=dojo.lang.hitch(_3f6,_3f7);
var _3f8=this[evt]||function(){
};
this[evt]=function(){
var ret=_3f8.apply(this,arguments);
_3f7.apply(this,arguments);
return ret;
};
return this;
},fire:function(evt,args){
if(this[evt]){
this[evt].apply(this,(args||[]));
}
return this;
},repeat:function(_3fc){
this.repeatCount=_3fc;
return this;
},_active:false,_paused:false});
dojo.lfx.Animation=function(_3fd,_3fe,_3ff,_400,_401,rate){
dojo.lfx.IAnimation.call(this);
if(dojo.lang.isNumber(_3fd)||(!_3fd&&_3fe.getValue)){
rate=_401;
_401=_400;
_400=_3ff;
_3ff=_3fe;
_3fe=_3fd;
_3fd=null;
}else{
if(_3fd.getValue||dojo.lang.isArray(_3fd)){
rate=_400;
_401=_3ff;
_400=_3fe;
_3ff=_3fd;
_3fe=null;
_3fd=null;
}
}
if(dojo.lang.isArray(_3ff)){
this.curve=new dojo.lfx.Line(_3ff[0],_3ff[1]);
}else{
this.curve=_3ff;
}
if(_3fe!=null&&_3fe>0){
this.duration=_3fe;
}
if(_401){
this.repeatCount=_401;
}
if(rate){
this.rate=rate;
}
if(_3fd){
dojo.lang.forEach(["handler","beforeBegin","onBegin","onEnd","onPlay","onStop","onAnimate"],function(item){
if(_3fd[item]){
this.connect(item,_3fd[item]);
}
},this);
}
if(_400&&dojo.lang.isFunction(_400)){
this.easing=_400;
}
};
dojo.inherits(dojo.lfx.Animation,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Animation,{_startTime:null,_endTime:null,_timer:null,_percent:0,_startRepeatCount:0,play:function(_404,_405){
if(_405){
clearTimeout(this._timer);
this._active=false;
this._paused=false;
this._percent=0;
}else{
if(this._active&&!this._paused){
return this;
}
}
this.fire("handler",["beforeBegin"]);
this.fire("beforeBegin");
if(_404>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_405);
}),_404);
return this;
}
this._startTime=new Date().valueOf();
if(this._paused){
this._startTime-=(this.duration*this._percent/100);
}
this._endTime=this._startTime+this.duration;
this._active=true;
this._paused=false;
var step=this._percent/100;
var _407=this.curve.getValue(step);
if(this._percent==0){
if(!this._startRepeatCount){
this._startRepeatCount=this.repeatCount;
}
this.fire("handler",["begin",_407]);
this.fire("onBegin",[_407]);
}
this.fire("handler",["play",_407]);
this.fire("onPlay",[_407]);
this._cycle();
return this;
},pause:function(){
clearTimeout(this._timer);
if(!this._active){
return this;
}
this._paused=true;
var _408=this.curve.getValue(this._percent/100);
this.fire("handler",["pause",_408]);
this.fire("onPause",[_408]);
return this;
},gotoPercent:function(pct,_40a){
clearTimeout(this._timer);
this._active=true;
this._paused=true;
this._percent=pct;
if(_40a){
this.play();
}
return this;
},stop:function(_40b){
clearTimeout(this._timer);
var step=this._percent/100;
if(_40b){
step=1;
}
var _40d=this.curve.getValue(step);
this.fire("handler",["stop",_40d]);
this.fire("onStop",[_40d]);
this._active=false;
this._paused=false;
return this;
},status:function(){
if(this._active){
return this._paused?"paused":"playing";
}else{
return "stopped";
}
return this;
},_cycle:function(){
clearTimeout(this._timer);
if(this._active){
var curr=new Date().valueOf();
var step=(curr-this._startTime)/(this._endTime-this._startTime);
if(step>=1){
step=1;
this._percent=100;
}else{
this._percent=step*100;
}
if((this.easing)&&(dojo.lang.isFunction(this.easing))){
step=this.easing(step);
}
var _410=this.curve.getValue(step);
this.fire("handler",["animate",_410]);
this.fire("onAnimate",[_410]);
if(step<1){
this._timer=setTimeout(dojo.lang.hitch(this,"_cycle"),this.rate);
}else{
this._active=false;
this.fire("handler",["end"]);
this.fire("onEnd");
if(this.repeatCount>0){
this.repeatCount--;
this.play(null,true);
}else{
if(this.repeatCount==-1){
this.play(null,true);
}else{
if(this._startRepeatCount){
this.repeatCount=this._startRepeatCount;
this._startRepeatCount=0;
}
}
}
}
}
return this;
}});
dojo.lfx.Combine=function(_411){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._animsEnded=0;
var _412=arguments;
if(_412.length==1&&(dojo.lang.isArray(_412[0])||dojo.lang.isArrayLike(_412[0]))){
_412=_412[0];
}
dojo.lang.forEach(_412,function(anim){
this._anims.push(anim);
anim.connect("onEnd",dojo.lang.hitch(this,"_onAnimsEnded"));
},this);
};
dojo.inherits(dojo.lfx.Combine,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Combine,{_animsEnded:0,play:function(_414,_415){
if(!this._anims.length){
return this;
}
this.fire("beforeBegin");
if(_414>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_415);
}),_414);
return this;
}
if(_415||this._anims[0].percent==0){
this.fire("onBegin");
}
this.fire("onPlay");
this._animsCall("play",null,_415);
return this;
},pause:function(){
this.fire("onPause");
this._animsCall("pause");
return this;
},stop:function(_416){
this.fire("onStop");
this._animsCall("stop",_416);
return this;
},_onAnimsEnded:function(){
this._animsEnded++;
if(this._animsEnded>=this._anims.length){
this.fire("onEnd");
}
return this;
},_animsCall:function(_417){
var args=[];
if(arguments.length>1){
for(var i=1;i<arguments.length;i++){
args.push(arguments[i]);
}
}
var _41a=this;
dojo.lang.forEach(this._anims,function(anim){
anim[_417](args);
},_41a);
return this;
}});
dojo.lfx.Chain=function(_41c){
dojo.lfx.IAnimation.call(this);
this._anims=[];
this._currAnim=-1;
var _41d=arguments;
if(_41d.length==1&&(dojo.lang.isArray(_41d[0])||dojo.lang.isArrayLike(_41d[0]))){
_41d=_41d[0];
}
var _41e=this;
dojo.lang.forEach(_41d,function(anim,i,_421){
this._anims.push(anim);
if(i<_421.length-1){
anim.connect("onEnd",dojo.lang.hitch(this,"_playNext"));
}else{
anim.connect("onEnd",dojo.lang.hitch(this,function(){
this.fire("onEnd");
}));
}
},this);
};
dojo.inherits(dojo.lfx.Chain,dojo.lfx.IAnimation);
dojo.lang.extend(dojo.lfx.Chain,{_currAnim:-1,play:function(_422,_423){
if(!this._anims.length){
return this;
}
if(_423||!this._anims[this._currAnim]){
this._currAnim=0;
}
var _424=this._anims[this._currAnim];
this.fire("beforeBegin");
if(_422>0){
setTimeout(dojo.lang.hitch(this,function(){
this.play(null,_423);
}),_422);
return this;
}
if(_424){
if(this._currAnim==0){
this.fire("handler",["begin",this._currAnim]);
this.fire("onBegin",[this._currAnim]);
}
this.fire("onPlay",[this._currAnim]);
_424.play(null,_423);
}
return this;
},pause:function(){
if(this._anims[this._currAnim]){
this._anims[this._currAnim].pause();
this.fire("onPause",[this._currAnim]);
}
return this;
},playPause:function(){
if(this._anims.length==0){
return this;
}
if(this._currAnim==-1){
this._currAnim=0;
}
var _425=this._anims[this._currAnim];
if(_425){
if(!_425._active||_425._paused){
this.play();
}else{
this.pause();
}
}
return this;
},stop:function(){
var _426=this._anims[this._currAnim];
if(_426){
_426.stop();
this.fire("onStop",[this._currAnim]);
}
return _426;
},_playNext:function(){
if(this._currAnim==-1||this._anims.length==0){
return this;
}
this._currAnim++;
if(this._anims[this._currAnim]){
this._anims[this._currAnim].play(null,true);
}
return this;
}});
dojo.lfx.combine=function(_427){
var _428=arguments;
if(dojo.lang.isArray(arguments[0])){
_428=arguments[0];
}
if(_428.length==1){
return _428[0];
}
return new dojo.lfx.Combine(_428);
};
dojo.lfx.chain=function(_429){
var _42a=arguments;
if(dojo.lang.isArray(arguments[0])){
_42a=arguments[0];
}
if(_42a.length==1){
return _42a[0];
}
return new dojo.lfx.Chain(_42a);
};
dojo.provide("dojo.html.style");
dojo.html.getClass=function(node){
node=dojo.byId(node);
if(!node){
return "";
}
var cs="";
if(node.className){
cs=node.className;
}else{
if(dojo.html.hasAttribute(node,"class")){
cs=dojo.html.getAttribute(node,"class");
}
}
return cs.replace(/^\s+|\s+$/g,"");
};
dojo.html.getClasses=function(node){
var c=dojo.html.getClass(node);
return (c=="")?[]:c.split(/\s+/g);
};
dojo.html.hasClass=function(node,_430){
return (new RegExp("(^|\\s+)"+_430+"(\\s+|$)")).test(dojo.html.getClass(node));
};
dojo.html.prependClass=function(node,_432){
_432+=" "+dojo.html.getClass(node);
return dojo.html.setClass(node,_432);
};
dojo.html.addClass=function(node,_434){
if(dojo.html.hasClass(node,_434)){
return false;
}
_434=(dojo.html.getClass(node)+" "+_434).replace(/^\s+|\s+$/g,"");
return dojo.html.setClass(node,_434);
};
dojo.html.setClass=function(node,_436){
node=dojo.byId(node);
var cs=new String(_436);
try{
if(typeof node.className=="string"){
node.className=cs;
}else{
if(node.setAttribute){
node.setAttribute("class",_436);
node.className=cs;
}else{
return false;
}
}
}
catch(e){
dojo.debug("dojo.html.setClass() failed",e);
}
return true;
};
dojo.html.removeClass=function(node,_439,_43a){
try{
if(!_43a){
var _43b=dojo.html.getClass(node).replace(new RegExp("(^|\\s+)"+_439+"(\\s+|$)"),"$1$2");
}else{
var _43b=dojo.html.getClass(node).replace(_439,"");
}
dojo.html.setClass(node,_43b);
}
catch(e){
dojo.debug("dojo.html.removeClass() failed",e);
}
return true;
};
dojo.html.replaceClass=function(node,_43d,_43e){
dojo.html.removeClass(node,_43e);
dojo.html.addClass(node,_43d);
};
dojo.html.classMatchType={ContainsAll:0,ContainsAny:1,IsOnly:2};
dojo.html.getElementsByClass=function(_43f,_440,_441,_442,_443){
_443=false;
var _444=dojo.doc();
_440=dojo.byId(_440)||_444;
var _445=_43f.split(/\s+/g);
var _446=[];
if(_442!=1&&_442!=2){
_442=0;
}
var _447=new RegExp("(\\s|^)(("+_445.join(")|(")+"))(\\s|$)");
var _448=_445.join(" ").length;
var _449=[];
if(!_443&&_444.evaluate){
var _44a=".//"+(_441||"*")+"[contains(";
if(_442!=dojo.html.classMatchType.ContainsAny){
_44a+="concat(' ',@class,' '), ' "+_445.join(" ') and contains(concat(' ',@class,' '), ' ")+" ')";
if(_442==2){
_44a+=" and string-length(@class)="+_448+"]";
}else{
_44a+="]";
}
}else{
_44a+="concat(' ',@class,' '), ' "+_445.join(" ') or contains(concat(' ',@class,' '), ' ")+" ')]";
}
var _44b=_444.evaluate(_44a,_440,null,XPathResult.ANY_TYPE,null);
var _44c=_44b.iterateNext();
while(_44c){
try{
_449.push(_44c);
_44c=_44b.iterateNext();
}
catch(e){
break;
}
}
return _449;
}else{
if(!_441){
_441="*";
}
_449=_440.getElementsByTagName(_441);
var node,i=0;
outer:
while(node=_449[i++]){
var _44f=dojo.html.getClasses(node);
if(_44f.length==0){
continue outer;
}
var _450=0;
for(var j=0;j<_44f.length;j++){
if(_447.test(_44f[j])){
if(_442==dojo.html.classMatchType.ContainsAny){
_446.push(node);
continue outer;
}else{
_450++;
}
}else{
if(_442==dojo.html.classMatchType.IsOnly){
continue outer;
}
}
}
if(_450==_445.length){
if((_442==dojo.html.classMatchType.IsOnly)&&(_450==_44f.length)){
_446.push(node);
}else{
if(_442==dojo.html.classMatchType.ContainsAll){
_446.push(node);
}
}
}
}
return _446;
}
};
dojo.html.getElementsByClassName=dojo.html.getElementsByClass;
dojo.html.toCamelCase=function(_452){
var arr=_452.split("-"),cc=arr[0];
for(var i=1;i<arr.length;i++){
cc+=arr[i].charAt(0).toUpperCase()+arr[i].substring(1);
}
return cc;
};
dojo.html.toSelectorCase=function(_456){
return _456.replace(/([A-Z])/g,"-$1").toLowerCase();
};
dojo.html.getComputedStyle=function(node,_458,_459){
node=dojo.byId(node);
var _458=dojo.html.toSelectorCase(_458);
var _45a=dojo.html.toCamelCase(_458);
if(!node||!node.style){
return _459;
}else{
if(document.defaultView&&dojo.html.isDescendantOf(node,node.ownerDocument)){
try{
var cs=document.defaultView.getComputedStyle(node,"");
if(cs){
return cs.getPropertyValue(_458);
}
}
catch(e){
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_458);
}else{
return _459;
}
}
}else{
if(node.currentStyle){
return node.currentStyle[_45a];
}
}
}
if(node.style.getPropertyValue){
return node.style.getPropertyValue(_458);
}else{
return _459;
}
};
dojo.html.getStyleProperty=function(node,_45d){
node=dojo.byId(node);
return (node&&node.style?node.style[dojo.html.toCamelCase(_45d)]:undefined);
};
dojo.html.getStyle=function(node,_45f){
var _460=dojo.html.getStyleProperty(node,_45f);
return (_460?_460:dojo.html.getComputedStyle(node,_45f));
};
dojo.html.setStyle=function(node,_462,_463){
node=dojo.byId(node);
if(node&&node.style){
var _464=dojo.html.toCamelCase(_462);
node.style[_464]=_463;
}
};
dojo.html.setStyleText=function(_465,text){
try{
_465.style.cssText=text;
}
catch(e){
_465.setAttribute("style",text);
}
};
dojo.html.copyStyle=function(_467,_468){
if(!_468.style.cssText){
_467.setAttribute("style",_468.getAttribute("style"));
}else{
_467.style.cssText=_468.style.cssText;
}
dojo.html.addClass(_467,dojo.html.getClass(_468));
};
dojo.html.getUnitValue=function(node,_46a,_46b){
var s=dojo.html.getComputedStyle(node,_46a);
if((!s)||((s=="auto")&&(_46b))){
return {value:0,units:"px"};
}
var _46d=s.match(/(\-?[\d.]+)([a-z%]*)/i);
if(!_46d){
return dojo.html.getUnitValue.bad;
}
return {value:Number(_46d[1]),units:_46d[2].toLowerCase()};
};
dojo.html.getUnitValue.bad={value:NaN,units:""};
dojo.html.getPixelValue=function(node,_46f,_470){
var _471=dojo.html.getUnitValue(node,_46f,_470);
if(isNaN(_471.value)){
return 0;
}
if((_471.value)&&(_471.units!="px")){
return NaN;
}
return _471.value;
};
dojo.html.setPositivePixelValue=function(node,_473,_474){
if(isNaN(_474)){
return false;
}
node.style[_473]=Math.max(0,_474)+"px";
return true;
};
dojo.html.styleSheet=null;
dojo.html.insertCssRule=function(_475,_476,_477){
if(!dojo.html.styleSheet){
if(document.createStyleSheet){
dojo.html.styleSheet=document.createStyleSheet();
}else{
if(document.styleSheets[0]){
dojo.html.styleSheet=document.styleSheets[0];
}else{
return null;
}
}
}
if(arguments.length<3){
if(dojo.html.styleSheet.cssRules){
_477=dojo.html.styleSheet.cssRules.length;
}else{
if(dojo.html.styleSheet.rules){
_477=dojo.html.styleSheet.rules.length;
}else{
return null;
}
}
}
if(dojo.html.styleSheet.insertRule){
var rule=_475+" { "+_476+" }";
return dojo.html.styleSheet.insertRule(rule,_477);
}else{
if(dojo.html.styleSheet.addRule){
return dojo.html.styleSheet.addRule(_475,_476,_477);
}else{
return null;
}
}
};
dojo.html.removeCssRule=function(_479){
if(!dojo.html.styleSheet){
dojo.debug("no stylesheet defined for removing rules");
return false;
}
if(dojo.render.html.ie){
if(!_479){
_479=dojo.html.styleSheet.rules.length;
dojo.html.styleSheet.removeRule(_479);
}
}else{
if(document.styleSheets[0]){
if(!_479){
_479=dojo.html.styleSheet.cssRules.length;
}
dojo.html.styleSheet.deleteRule(_479);
}
}
return true;
};
dojo.html._insertedCssFiles=[];
dojo.html.insertCssFile=function(URI,doc,_47c,_47d){
if(!URI){
return;
}
if(!doc){
doc=document;
}
var _47e=dojo.hostenv.getText(URI,false,_47d);
if(_47e===null){
return;
}
_47e=dojo.html.fixPathsInCssText(_47e,URI);
if(_47c){
var idx=-1,node,ent=dojo.html._insertedCssFiles;
for(var i=0;i<ent.length;i++){
if((ent[i].doc==doc)&&(ent[i].cssText==_47e)){
idx=i;
node=ent[i].nodeRef;
break;
}
}
if(node){
var _483=doc.getElementsByTagName("style");
for(var i=0;i<_483.length;i++){
if(_483[i]==node){
return;
}
}
dojo.html._insertedCssFiles.shift(idx,1);
}
}
var _484=dojo.html.insertCssText(_47e);
dojo.html._insertedCssFiles.push({"doc":doc,"cssText":_47e,"nodeRef":_484});
if(_484&&djConfig.isDebug){
_484.setAttribute("dbgHref",URI);
}
return _484;
};
dojo.html.insertCssText=function(_485,doc,URI){
if(!_485){
return;
}
if(!doc){
doc=document;
}
if(URI){
_485=dojo.html.fixPathsInCssText(_485,URI);
}
var _488=doc.createElement("style");
_488.setAttribute("type","text/css");
var head=doc.getElementsByTagName("head")[0];
if(!head){
dojo.debug("No head tag in document, aborting styles");
return;
}else{
head.appendChild(_488);
}
if(_488.styleSheet){
_488.styleSheet.cssText=_485;
}else{
var _48a=doc.createTextNode(_485);
_488.appendChild(_48a);
}
return _488;
};
dojo.html.fixPathsInCssText=function(_48b,URI){
function iefixPathsInCssText(){
var _48d=/AlphaImageLoader\(src\=['"]([\t\s\w()\/.\\'"-:#=&?~]*)['"]/;
while(_48e=_48d.exec(_48b)){
url=_48e[1].replace(_490,"$2");
if(!_491.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_48b.substring(0,_48e.index)+"AlphaImageLoader(src='"+url+"'";
_48b=_48b.substr(_48e.index+_48e[0].length);
}
return str+_48b;
}
if(!_48b||!URI){
return;
}
var _48e,str="",url="";
var _493=/url\(\s*([\t\s\w()\/.\\'"-:#=&?]+)\s*\)/;
var _491=/(file|https?|ftps?):\/\//;
var _490=/^[\s]*(['"]?)([\w()\/.\\'"-:#=&?]*)\1[\s]*?$/;
if(dojo.render.html.ie55||dojo.render.html.ie60){
_48b=iefixPathsInCssText();
}
while(_48e=_493.exec(_48b)){
url=_48e[1].replace(_490,"$2");
if(!_491.exec(url)){
url=(new dojo.uri.Uri(URI,url).toString());
}
str+=_48b.substring(0,_48e.index)+"url("+url+")";
_48b=_48b.substr(_48e.index+_48e[0].length);
}
return str+_48b;
};
dojo.html.setActiveStyleSheet=function(_494){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")){
a.disabled=true;
if(a.getAttribute("title")==_494){
a.disabled=false;
}
}
}
};
dojo.html.getActiveStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("title")&&!a.disabled){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.getPreferredStyleSheet=function(){
var i=0,a,els=dojo.doc().getElementsByTagName("link");
while(a=els[i++]){
if(a.getAttribute("rel").indexOf("style")!=-1&&a.getAttribute("rel").indexOf("alt")==-1&&a.getAttribute("title")){
return a.getAttribute("title");
}
}
return null;
};
dojo.html.applyBrowserClass=function(node){
var drh=dojo.render.html;
var _4a0={dj_ie:drh.ie,dj_ie55:drh.ie55,dj_ie6:drh.ie60,dj_ie7:drh.ie70,dj_iequirks:drh.ie&&drh.quirks,dj_opera:drh.opera,dj_opera8:drh.opera&&(Math.floor(dojo.render.version)==8),dj_opera9:drh.opera&&(Math.floor(dojo.render.version)==9),dj_khtml:drh.khtml,dj_safari:drh.safari,dj_gecko:drh.mozilla};
for(var p in _4a0){
if(_4a0[p]){
dojo.html.addClass(node,p);
}
}
};
dojo.provide("dojo.html.display");
dojo.html._toggle=function(node,_4a3,_4a4){
node=dojo.byId(node);
_4a4(node,!_4a3(node));
return _4a3(node);
};
dojo.html.show=function(node){
node=dojo.byId(node);
if(dojo.html.getStyleProperty(node,"display")=="none"){
dojo.html.setStyle(node,"display",(node.dojoDisplayCache||""));
node.dojoDisplayCache=undefined;
}
};
dojo.html.hide=function(node){
node=dojo.byId(node);
if(typeof node["dojoDisplayCache"]=="undefined"){
var d=dojo.html.getStyleProperty(node,"display");
if(d!="none"){
node.dojoDisplayCache=d;
}
}
dojo.html.setStyle(node,"display","none");
};
dojo.html.setShowing=function(node,_4a9){
dojo.html[(_4a9?"show":"hide")](node);
};
dojo.html.isShowing=function(node){
return (dojo.html.getStyleProperty(node,"display")!="none");
};
dojo.html.toggleShowing=function(node){
return dojo.html._toggle(node,dojo.html.isShowing,dojo.html.setShowing);
};
dojo.html.displayMap={tr:"",td:"",th:"",img:"inline",span:"inline",input:"inline",button:"inline"};
dojo.html.suggestDisplayByTagName=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
var tag=node.tagName.toLowerCase();
return (tag in dojo.html.displayMap?dojo.html.displayMap[tag]:"block");
}
};
dojo.html.setDisplay=function(node,_4af){
dojo.html.setStyle(node,"display",((_4af instanceof String||typeof _4af=="string")?_4af:(_4af?dojo.html.suggestDisplayByTagName(node):"none")));
};
dojo.html.isDisplayed=function(node){
return (dojo.html.getComputedStyle(node,"display")!="none");
};
dojo.html.toggleDisplay=function(node){
return dojo.html._toggle(node,dojo.html.isDisplayed,dojo.html.setDisplay);
};
dojo.html.setVisibility=function(node,_4b3){
dojo.html.setStyle(node,"visibility",((_4b3 instanceof String||typeof _4b3=="string")?_4b3:(_4b3?"visible":"hidden")));
};
dojo.html.isVisible=function(node){
return (dojo.html.getComputedStyle(node,"visibility")!="hidden");
};
dojo.html.toggleVisibility=function(node){
return dojo.html._toggle(node,dojo.html.isVisible,dojo.html.setVisibility);
};
dojo.html.setOpacity=function(node,_4b7,_4b8){
node=dojo.byId(node);
var h=dojo.render.html;
if(!_4b8){
if(_4b7>=1){
if(h.ie){
dojo.html.clearOpacity(node);
return;
}else{
_4b7=0.999999;
}
}else{
if(_4b7<0){
_4b7=0;
}
}
}
if(h.ie){
if(node.nodeName.toLowerCase()=="tr"){
var tds=node.getElementsByTagName("td");
for(var x=0;x<tds.length;x++){
tds[x].style.filter="Alpha(Opacity="+_4b7*100+")";
}
}
node.style.filter="Alpha(Opacity="+_4b7*100+")";
}else{
if(h.moz){
node.style.opacity=_4b7;
node.style.MozOpacity=_4b7;
}else{
if(h.safari){
node.style.opacity=_4b7;
node.style.KhtmlOpacity=_4b7;
}else{
node.style.opacity=_4b7;
}
}
}
};
dojo.html.clearOpacity=function(node){
node=dojo.byId(node);
var ns=node.style;
var h=dojo.render.html;
if(h.ie){
try{
if(node.filters&&node.filters.alpha){
ns.filter="";
}
}
catch(e){
}
}else{
if(h.moz){
ns.opacity=1;
ns.MozOpacity=1;
}else{
if(h.safari){
ns.opacity=1;
ns.KhtmlOpacity=1;
}else{
ns.opacity=1;
}
}
}
};
dojo.html.getOpacity=function(node){
node=dojo.byId(node);
var h=dojo.render.html;
if(h.ie){
var opac=(node.filters&&node.filters.alpha&&typeof node.filters.alpha.opacity=="number"?node.filters.alpha.opacity:100)/100;
}else{
var opac=node.style.opacity||node.style.MozOpacity||node.style.KhtmlOpacity||1;
}
return opac>=0.999999?1:Number(opac);
};
dojo.provide("dojo.html.color");
dojo.html.getBackgroundColor=function(node){
node=dojo.byId(node);
var _4c3;
do{
_4c3=dojo.html.getStyle(node,"background-color");
if(_4c3.toLowerCase()=="rgba(0, 0, 0, 0)"){
_4c3="transparent";
}
if(node==document.getElementsByTagName("body")[0]){
node=null;
break;
}
node=node.parentNode;
}while(node&&dojo.lang.inArray(["transparent",""],_4c3));
if(_4c3=="transparent"){
_4c3=[255,255,255,0];
}else{
_4c3=dojo.gfx.color.extractRGB(_4c3);
}
return _4c3;
};
dojo.provide("dojo.html.common");
dojo.lang.mixin(dojo.html,dojo.dom);
dojo.html.body=function(){
dojo.deprecated("dojo.html.body() moved to dojo.body()","0.5");
return dojo.body();
};
dojo.html.getEventTarget=function(evt){
if(!evt){
evt=dojo.global().event||{};
}
var t=(evt.srcElement?evt.srcElement:(evt.target?evt.target:null));
while((t)&&(t.nodeType!=1)){
t=t.parentNode;
}
return t;
};
dojo.html.getViewport=function(){
var _4c6=dojo.global();
var _4c7=dojo.doc();
var w=0;
var h=0;
if(dojo.render.html.mozilla){
w=_4c7.documentElement.clientWidth;
h=_4c6.innerHeight;
}else{
if(!dojo.render.html.opera&&_4c6.innerWidth){
w=_4c6.innerWidth;
h=_4c6.innerHeight;
}else{
if(!dojo.render.html.opera&&dojo.exists(_4c7,"documentElement.clientWidth")){
var w2=_4c7.documentElement.clientWidth;
if(!w||w2&&w2<w){
w=w2;
}
h=_4c7.documentElement.clientHeight;
}else{
if(dojo.body().clientWidth){
w=dojo.body().clientWidth;
h=dojo.body().clientHeight;
}
}
}
}
return {width:w,height:h};
};
dojo.html.getScroll=function(){
var _4cb=dojo.global();
var _4cc=dojo.doc();
var top=_4cb.pageYOffset||_4cc.documentElement.scrollTop||dojo.body().scrollTop||0;
var left=_4cb.pageXOffset||_4cc.documentElement.scrollLeft||dojo.body().scrollLeft||0;
return {top:top,left:left,offset:{x:left,y:top}};
};
dojo.html.getParentByType=function(node,type){
var _4d1=dojo.doc();
var _4d2=dojo.byId(node);
type=type.toLowerCase();
while((_4d2)&&(_4d2.nodeName.toLowerCase()!=type)){
if(_4d2==(_4d1["body"]||_4d1["documentElement"])){
return null;
}
_4d2=_4d2.parentNode;
}
return _4d2;
};
dojo.html.getAttribute=function(node,attr){
node=dojo.byId(node);
if((!node)||(!node.getAttribute)){
return null;
}
var ta=typeof attr=="string"?attr:new String(attr);
var v=node.getAttribute(ta.toUpperCase());
if((v)&&(typeof v=="string")&&(v!="")){
return v;
}
if(v&&v.value){
return v.value;
}
if((node.getAttributeNode)&&(node.getAttributeNode(ta))){
return (node.getAttributeNode(ta)).value;
}else{
if(node.getAttribute(ta)){
return node.getAttribute(ta);
}else{
if(node.getAttribute(ta.toLowerCase())){
return node.getAttribute(ta.toLowerCase());
}
}
}
return null;
};
dojo.html.hasAttribute=function(node,attr){
return dojo.html.getAttribute(dojo.byId(node),attr)?true:false;
};
dojo.html.getCursorPosition=function(e){
e=e||dojo.global().event;
var _4da={x:0,y:0};
if(e.pageX||e.pageY){
_4da.x=e.pageX;
_4da.y=e.pageY;
}else{
var de=dojo.doc().documentElement;
var db=dojo.body();
_4da.x=e.clientX+((de||db)["scrollLeft"])-((de||db)["clientLeft"]);
_4da.y=e.clientY+((de||db)["scrollTop"])-((de||db)["clientTop"]);
}
return _4da;
};
dojo.html.isTag=function(node){
node=dojo.byId(node);
if(node&&node.tagName){
for(var i=1;i<arguments.length;i++){
if(node.tagName.toLowerCase()==String(arguments[i]).toLowerCase()){
return String(arguments[i]).toLowerCase();
}
}
}
return "";
};
if(dojo.render.html.ie&&!dojo.render.html.ie70){
if(window.location.href.substr(0,6).toLowerCase()!="https:"){
(function(){
var _4df=dojo.doc().createElement("script");
_4df.src="javascript:'dojo.html.createExternalElement=function(doc, tag){ return doc.createElement(tag); }'";
dojo.doc().getElementsByTagName("head")[0].appendChild(_4df);
})();
}
}else{
dojo.html.createExternalElement=function(doc,tag){
return doc.createElement(tag);
};
}
dojo.html._callDeprecated=function(_4e2,_4e3,args,_4e5,_4e6){
dojo.deprecated("dojo.html."+_4e2,"replaced by dojo.html."+_4e3+"("+(_4e5?"node, {"+_4e5+": "+_4e5+"}":"")+")"+(_4e6?"."+_4e6:""),"0.5");
var _4e7=[];
if(_4e5){
var _4e8={};
_4e8[_4e5]=args[1];
_4e7.push(args[0]);
_4e7.push(_4e8);
}else{
_4e7=args;
}
var ret=dojo.html[_4e3].apply(dojo.html,args);
if(_4e6){
return ret[_4e6];
}else{
return ret;
}
};
dojo.html.getViewportWidth=function(){
return dojo.html._callDeprecated("getViewportWidth","getViewport",arguments,null,"width");
};
dojo.html.getViewportHeight=function(){
return dojo.html._callDeprecated("getViewportHeight","getViewport",arguments,null,"height");
};
dojo.html.getViewportSize=function(){
return dojo.html._callDeprecated("getViewportSize","getViewport",arguments);
};
dojo.html.getScrollTop=function(){
return dojo.html._callDeprecated("getScrollTop","getScroll",arguments,null,"top");
};
dojo.html.getScrollLeft=function(){
return dojo.html._callDeprecated("getScrollLeft","getScroll",arguments,null,"left");
};
dojo.html.getScrollOffset=function(){
return dojo.html._callDeprecated("getScrollOffset","getScroll",arguments,null,"offset");
};
dojo.provide("dojo.html.layout");
dojo.html.sumAncestorProperties=function(node,prop){
node=dojo.byId(node);
if(!node){
return 0;
}
var _4ec=0;
while(node){
if(dojo.html.getComputedStyle(node,"position")=="fixed"){
return 0;
}
var val=node[prop];
if(val){
_4ec+=val-0;
if(node==dojo.body()){
break;
}
}
node=node.parentNode;
}
return _4ec;
};
dojo.html.setStyleAttributes=function(node,_4ef){
node=dojo.byId(node);
var _4f0=_4ef.replace(/(;)?\s*$/,"").split(";");
for(var i=0;i<_4f0.length;i++){
var _4f2=_4f0[i].split(":");
var name=_4f2[0].replace(/\s*$/,"").replace(/^\s*/,"").toLowerCase();
var _4f4=_4f2[1].replace(/\s*$/,"").replace(/^\s*/,"");
switch(name){
case "opacity":
dojo.html.setOpacity(node,_4f4);
break;
case "content-height":
dojo.html.setContentBox(node,{height:_4f4});
break;
case "content-width":
dojo.html.setContentBox(node,{width:_4f4});
break;
case "outer-height":
dojo.html.setMarginBox(node,{height:_4f4});
break;
case "outer-width":
dojo.html.setMarginBox(node,{width:_4f4});
break;
default:
node.style[dojo.html.toCamelCase(name)]=_4f4;
}
}
};
dojo.html.boxSizing={MARGIN_BOX:"margin-box",BORDER_BOX:"border-box",PADDING_BOX:"padding-box",CONTENT_BOX:"content-box"};
dojo.html.getAbsolutePosition=dojo.html.abs=function(node,_4f6,_4f7){
node=dojo.byId(node,node.ownerDocument);
var ret={x:0,y:0};
var bs=dojo.html.boxSizing;
if(!_4f7){
_4f7=bs.CONTENT_BOX;
}
var _4fa=2;
var _4fb;
switch(_4f7){
case bs.MARGIN_BOX:
_4fb=3;
break;
case bs.BORDER_BOX:
_4fb=2;
break;
case bs.PADDING_BOX:
default:
_4fb=1;
break;
case bs.CONTENT_BOX:
_4fb=0;
break;
}
var h=dojo.render.html;
var db=document["body"]||document["documentElement"];
if(h.ie){
with(node.getBoundingClientRect()){
ret.x=left-2;
ret.y=top-2;
}
}else{
if(document.getBoxObjectFor){
_4fa=1;
try{
var bo=document.getBoxObjectFor(node);
ret.x=bo.x-dojo.html.sumAncestorProperties(node,"scrollLeft");
ret.y=bo.y-dojo.html.sumAncestorProperties(node,"scrollTop");
}
catch(e){
}
}else{
if(node["offsetParent"]){
var _4ff;
if((h.safari)&&(node.style.getPropertyValue("position")=="absolute")&&(node.parentNode==db)){
_4ff=db;
}else{
_4ff=db.parentNode;
}
if(node.parentNode!=db){
var nd=node;
if(dojo.render.html.opera){
nd=db;
}
ret.x-=dojo.html.sumAncestorProperties(nd,"scrollLeft");
ret.y-=dojo.html.sumAncestorProperties(nd,"scrollTop");
}
var _501=node;
do{
var n=_501["offsetLeft"];
if(!h.opera||n>0){
ret.x+=isNaN(n)?0:n;
}
var m=_501["offsetTop"];
ret.y+=isNaN(m)?0:m;
_501=_501.offsetParent;
}while((_501!=_4ff)&&(_501!=null));
}else{
if(node["x"]&&node["y"]){
ret.x+=isNaN(node.x)?0:node.x;
ret.y+=isNaN(node.y)?0:node.y;
}
}
}
}
if(_4f6){
var _504=dojo.html.getScroll();
ret.y+=_504.top;
ret.x+=_504.left;
}
var _505=[dojo.html.getPaddingExtent,dojo.html.getBorderExtent,dojo.html.getMarginExtent];
if(_4fa>_4fb){
for(var i=_4fb;i<_4fa;++i){
ret.y+=_505[i](node,"top");
ret.x+=_505[i](node,"left");
}
}else{
if(_4fa<_4fb){
for(var i=_4fb;i>_4fa;--i){
ret.y-=_505[i-1](node,"top");
ret.x-=_505[i-1](node,"left");
}
}
}
ret.top=ret.y;
ret.left=ret.x;
return ret;
};
dojo.html.isPositionAbsolute=function(node){
return (dojo.html.getComputedStyle(node,"position")=="absolute");
};
dojo.html._sumPixelValues=function(node,_509,_50a){
var _50b=0;
for(var x=0;x<_509.length;x++){
_50b+=dojo.html.getPixelValue(node,_509[x],_50a);
}
return _50b;
};
dojo.html.getMargin=function(node){
return {width:dojo.html._sumPixelValues(node,["margin-left","margin-right"],(dojo.html.getComputedStyle(node,"position")=="absolute")),height:dojo.html._sumPixelValues(node,["margin-top","margin-bottom"],(dojo.html.getComputedStyle(node,"position")=="absolute"))};
};
dojo.html.getBorder=function(node){
return {width:dojo.html.getBorderExtent(node,"left")+dojo.html.getBorderExtent(node,"right"),height:dojo.html.getBorderExtent(node,"top")+dojo.html.getBorderExtent(node,"bottom")};
};
dojo.html.getBorderExtent=function(node,side){
return (dojo.html.getStyle(node,"border-"+side+"-style")=="none"?0:dojo.html.getPixelValue(node,"border-"+side+"-width"));
};
dojo.html.getMarginExtent=function(node,side){
return dojo.html._sumPixelValues(node,["margin-"+side],dojo.html.isPositionAbsolute(node));
};
dojo.html.getPaddingExtent=function(node,side){
return dojo.html._sumPixelValues(node,["padding-"+side],true);
};
dojo.html.getPadding=function(node){
return {width:dojo.html._sumPixelValues(node,["padding-left","padding-right"],true),height:dojo.html._sumPixelValues(node,["padding-top","padding-bottom"],true)};
};
dojo.html.getPadBorder=function(node){
var pad=dojo.html.getPadding(node);
var _518=dojo.html.getBorder(node);
return {width:pad.width+_518.width,height:pad.height+_518.height};
};
dojo.html.getBoxSizing=function(node){
var h=dojo.render.html;
var bs=dojo.html.boxSizing;
if((h.ie)||(h.opera)){
var cm=document["compatMode"];
if((cm=="BackCompat")||(cm=="QuirksMode")){
return bs.BORDER_BOX;
}else{
return bs.CONTENT_BOX;
}
}else{
if(arguments.length==0){
node=document.documentElement;
}
var _51d=dojo.html.getStyle(node,"-moz-box-sizing");
if(!_51d){
_51d=dojo.html.getStyle(node,"box-sizing");
}
return (_51d?_51d:bs.CONTENT_BOX);
}
};
dojo.html.isBorderBox=function(node){
return (dojo.html.getBoxSizing(node)==dojo.html.boxSizing.BORDER_BOX);
};
dojo.html.getBorderBox=function(node){
node=dojo.byId(node);
return {width:node.offsetWidth,height:node.offsetHeight};
};
dojo.html.getPaddingBox=function(node){
var box=dojo.html.getBorderBox(node);
var _522=dojo.html.getBorder(node);
return {width:box.width-_522.width,height:box.height-_522.height};
};
dojo.html.getContentBox=function(node){
node=dojo.byId(node);
var _524=dojo.html.getPadBorder(node);
return {width:node.offsetWidth-_524.width,height:node.offsetHeight-_524.height};
};
dojo.html.setContentBox=function(node,args){
node=dojo.byId(node);
var _527=0;
var _528=0;
var isbb=dojo.html.isBorderBox(node);
var _52a=(isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var ret={};
if(typeof args.width!="undefined"){
_527=args.width+_52a.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_527);
}
if(typeof args.height!="undefined"){
_528=args.height+_52a.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_528);
}
return ret;
};
dojo.html.getMarginBox=function(node){
var _52d=dojo.html.getBorderBox(node);
var _52e=dojo.html.getMargin(node);
return {width:_52d.width+_52e.width,height:_52d.height+_52e.height};
};
dojo.html.setMarginBox=function(node,args){
node=dojo.byId(node);
var _531=0;
var _532=0;
var isbb=dojo.html.isBorderBox(node);
var _534=(!isbb?dojo.html.getPadBorder(node):{width:0,height:0});
var _535=dojo.html.getMargin(node);
var ret={};
if(typeof args.width!="undefined"){
_531=args.width-_534.width;
_531-=_535.width;
ret.width=dojo.html.setPositivePixelValue(node,"width",_531);
}
if(typeof args.height!="undefined"){
_532=args.height-_534.height;
_532-=_535.height;
ret.height=dojo.html.setPositivePixelValue(node,"height",_532);
}
return ret;
};
dojo.html.getElementBox=function(node,type){
var bs=dojo.html.boxSizing;
switch(type){
case bs.MARGIN_BOX:
return dojo.html.getMarginBox(node);
case bs.BORDER_BOX:
return dojo.html.getBorderBox(node);
case bs.PADDING_BOX:
return dojo.html.getPaddingBox(node);
case bs.CONTENT_BOX:
default:
return dojo.html.getContentBox(node);
}
};
dojo.html.toCoordinateObject=dojo.html.toCoordinateArray=function(_53a,_53b,_53c){
if(_53a instanceof Array||typeof _53a=="array"){
dojo.deprecated("dojo.html.toCoordinateArray","use dojo.html.toCoordinateObject({left: , top: , width: , height: }) instead","0.5");
while(_53a.length<4){
_53a.push(0);
}
while(_53a.length>4){
_53a.pop();
}
var ret={left:_53a[0],top:_53a[1],width:_53a[2],height:_53a[3]};
}else{
if(!_53a.nodeType&&!(_53a instanceof String||typeof _53a=="string")&&("width" in _53a||"height" in _53a||"left" in _53a||"x" in _53a||"top" in _53a||"y" in _53a)){
var ret={left:_53a.left||_53a.x||0,top:_53a.top||_53a.y||0,width:_53a.width||0,height:_53a.height||0};
}else{
var node=dojo.byId(_53a);
var pos=dojo.html.abs(node,_53b,_53c);
var _540=dojo.html.getMarginBox(node);
var ret={left:pos.left,top:pos.top,width:_540.width,height:_540.height};
}
}
ret.x=ret.left;
ret.y=ret.top;
return ret;
};
dojo.html.setMarginBoxWidth=dojo.html.setOuterWidth=function(node,_542){
return dojo.html._callDeprecated("setMarginBoxWidth","setMarginBox",arguments,"width");
};
dojo.html.setMarginBoxHeight=dojo.html.setOuterHeight=function(){
return dojo.html._callDeprecated("setMarginBoxHeight","setMarginBox",arguments,"height");
};
dojo.html.getMarginBoxWidth=dojo.html.getOuterWidth=function(){
return dojo.html._callDeprecated("getMarginBoxWidth","getMarginBox",arguments,null,"width");
};
dojo.html.getMarginBoxHeight=dojo.html.getOuterHeight=function(){
return dojo.html._callDeprecated("getMarginBoxHeight","getMarginBox",arguments,null,"height");
};
dojo.html.getTotalOffset=function(node,type,_545){
return dojo.html._callDeprecated("getTotalOffset","getAbsolutePosition",arguments,null,type);
};
dojo.html.getAbsoluteX=function(node,_547){
return dojo.html._callDeprecated("getAbsoluteX","getAbsolutePosition",arguments,null,"x");
};
dojo.html.getAbsoluteY=function(node,_549){
return dojo.html._callDeprecated("getAbsoluteY","getAbsolutePosition",arguments,null,"y");
};
dojo.html.totalOffsetLeft=function(node,_54b){
return dojo.html._callDeprecated("totalOffsetLeft","getAbsolutePosition",arguments,null,"left");
};
dojo.html.totalOffsetTop=function(node,_54d){
return dojo.html._callDeprecated("totalOffsetTop","getAbsolutePosition",arguments,null,"top");
};
dojo.html.getMarginWidth=function(node){
return dojo.html._callDeprecated("getMarginWidth","getMargin",arguments,null,"width");
};
dojo.html.getMarginHeight=function(node){
return dojo.html._callDeprecated("getMarginHeight","getMargin",arguments,null,"height");
};
dojo.html.getBorderWidth=function(node){
return dojo.html._callDeprecated("getBorderWidth","getBorder",arguments,null,"width");
};
dojo.html.getBorderHeight=function(node){
return dojo.html._callDeprecated("getBorderHeight","getBorder",arguments,null,"height");
};
dojo.html.getPaddingWidth=function(node){
return dojo.html._callDeprecated("getPaddingWidth","getPadding",arguments,null,"width");
};
dojo.html.getPaddingHeight=function(node){
return dojo.html._callDeprecated("getPaddingHeight","getPadding",arguments,null,"height");
};
dojo.html.getPadBorderWidth=function(node){
return dojo.html._callDeprecated("getPadBorderWidth","getPadBorder",arguments,null,"width");
};
dojo.html.getPadBorderHeight=function(node){
return dojo.html._callDeprecated("getPadBorderHeight","getPadBorder",arguments,null,"height");
};
dojo.html.getBorderBoxWidth=dojo.html.getInnerWidth=function(){
return dojo.html._callDeprecated("getBorderBoxWidth","getBorderBox",arguments,null,"width");
};
dojo.html.getBorderBoxHeight=dojo.html.getInnerHeight=function(){
return dojo.html._callDeprecated("getBorderBoxHeight","getBorderBox",arguments,null,"height");
};
dojo.html.getContentBoxWidth=dojo.html.getContentWidth=function(){
return dojo.html._callDeprecated("getContentBoxWidth","getContentBox",arguments,null,"width");
};
dojo.html.getContentBoxHeight=dojo.html.getContentHeight=function(){
return dojo.html._callDeprecated("getContentBoxHeight","getContentBox",arguments,null,"height");
};
dojo.html.setContentBoxWidth=dojo.html.setContentWidth=function(node,_557){
return dojo.html._callDeprecated("setContentBoxWidth","setContentBox",arguments,"width");
};
dojo.html.setContentBoxHeight=dojo.html.setContentHeight=function(node,_559){
return dojo.html._callDeprecated("setContentBoxHeight","setContentBox",arguments,"height");
};
dojo.provide("dojo.lfx.html");
dojo.lfx.html._byId=function(_55a){
if(!_55a){
return [];
}
if(dojo.lang.isArrayLike(_55a)){
if(!_55a.alreadyChecked){
var n=[];
dojo.lang.forEach(_55a,function(node){
n.push(dojo.byId(node));
});
n.alreadyChecked=true;
return n;
}else{
return _55a;
}
}else{
var n=[];
n.push(dojo.byId(_55a));
n.alreadyChecked=true;
return n;
}
};
dojo.lfx.html.propertyAnimation=function(_55d,_55e,_55f,_560,_561){
_55d=dojo.lfx.html._byId(_55d);
var _562={"propertyMap":_55e,"nodes":_55d,"duration":_55f,"easing":_560||dojo.lfx.easeDefault};
var _563=function(args){
if(args.nodes.length==1){
var pm=args.propertyMap;
if(!dojo.lang.isArray(args.propertyMap)){
var parr=[];
for(var _567 in pm){
pm[_567].property=_567;
parr.push(pm[_567]);
}
pm=args.propertyMap=parr;
}
dojo.lang.forEach(pm,function(prop){
if(dj_undef("start",prop)){
if(prop.property!="opacity"){
prop.start=parseInt(dojo.html.getComputedStyle(args.nodes[0],prop.property));
}else{
prop.start=dojo.html.getOpacity(args.nodes[0]);
}
}
});
}
};
var _569=function(_56a){
var _56b=[];
dojo.lang.forEach(_56a,function(c){
_56b.push(Math.round(c));
});
return _56b;
};
var _56d=function(n,_56f){
n=dojo.byId(n);
if(!n||!n.style){
return;
}
for(var s in _56f){
if(s=="opacity"){
dojo.html.setOpacity(n,_56f[s]);
}else{
n.style[s]=_56f[s];
}
}
};
var _571=function(_572){
this._properties=_572;
this.diffs=new Array(_572.length);
dojo.lang.forEach(_572,function(prop,i){
if(dojo.lang.isFunction(prop.start)){
prop.start=prop.start(prop,i);
}
if(dojo.lang.isFunction(prop.end)){
prop.end=prop.end(prop,i);
}
if(dojo.lang.isArray(prop.start)){
this.diffs[i]=null;
}else{
if(prop.start instanceof dojo.gfx.color.Color){
prop.startRgb=prop.start.toRgb();
prop.endRgb=prop.end.toRgb();
}else{
this.diffs[i]=prop.end-prop.start;
}
}
},this);
this.getValue=function(n){
var ret={};
dojo.lang.forEach(this._properties,function(prop,i){
var _579=null;
if(dojo.lang.isArray(prop.start)){
}else{
if(prop.start instanceof dojo.gfx.color.Color){
_579=(prop.units||"rgb")+"(";
for(var j=0;j<prop.startRgb.length;j++){
_579+=Math.round(((prop.endRgb[j]-prop.startRgb[j])*n)+prop.startRgb[j])+(j<prop.startRgb.length-1?",":"");
}
_579+=")";
}else{
_579=((this.diffs[i])*n)+prop.start+(prop.property!="opacity"?prop.units||"px":"");
}
}
ret[dojo.html.toCamelCase(prop.property)]=_579;
},this);
return ret;
};
};
var anim=new dojo.lfx.Animation({beforeBegin:function(){
_563(_562);
anim.curve=new _571(_562.propertyMap);
},onAnimate:function(_57c){
dojo.lang.forEach(_562.nodes,function(node){
_56d(node,_57c);
});
}},_562.duration,null,_562.easing);
if(_561){
for(var x in _561){
if(dojo.lang.isFunction(_561[x])){
anim.connect(x,anim,_561[x]);
}
}
}
return anim;
};
dojo.lfx.html._makeFadeable=function(_57f){
var _580=function(node){
if(dojo.render.html.ie){
if((node.style.zoom.length==0)&&(dojo.html.getStyle(node,"zoom")=="normal")){
node.style.zoom="1";
}
if((node.style.width.length==0)&&(dojo.html.getStyle(node,"width")=="auto")){
node.style.width="auto";
}
}
};
if(dojo.lang.isArrayLike(_57f)){
dojo.lang.forEach(_57f,_580);
}else{
_580(_57f);
}
};
dojo.lfx.html.fade=function(_582,_583,_584,_585,_586){
_582=dojo.lfx.html._byId(_582);
var _587={property:"opacity"};
if(!dj_undef("start",_583)){
_587.start=_583.start;
}else{
_587.start=function(){
return dojo.html.getOpacity(_582[0]);
};
}
if(!dj_undef("end",_583)){
_587.end=_583.end;
}else{
dojo.raise("dojo.lfx.html.fade needs an end value");
}
var anim=dojo.lfx.propertyAnimation(_582,[_587],_584,_585);
anim.connect("beforeBegin",function(){
dojo.lfx.html._makeFadeable(_582);
});
if(_586){
anim.connect("onEnd",function(){
_586(_582,anim);
});
}
return anim;
};
dojo.lfx.html.fadeIn=function(_589,_58a,_58b,_58c){
return dojo.lfx.html.fade(_589,{end:1},_58a,_58b,_58c);
};
dojo.lfx.html.fadeOut=function(_58d,_58e,_58f,_590){
return dojo.lfx.html.fade(_58d,{end:0},_58e,_58f,_590);
};
dojo.lfx.html.fadeShow=function(_591,_592,_593,_594){
_591=dojo.lfx.html._byId(_591);
dojo.lang.forEach(_591,function(node){
dojo.html.setOpacity(node,0);
});
var anim=dojo.lfx.html.fadeIn(_591,_592,_593,_594);
anim.connect("beforeBegin",function(){
if(dojo.lang.isArrayLike(_591)){
dojo.lang.forEach(_591,dojo.html.show);
}else{
dojo.html.show(_591);
}
});
return anim;
};
dojo.lfx.html.fadeHide=function(_597,_598,_599,_59a){
var anim=dojo.lfx.html.fadeOut(_597,_598,_599,function(){
if(dojo.lang.isArrayLike(_597)){
dojo.lang.forEach(_597,dojo.html.hide);
}else{
dojo.html.hide(_597);
}
if(_59a){
_59a(_597,anim);
}
});
return anim;
};
dojo.lfx.html.wipeIn=function(_59c,_59d,_59e,_59f){
_59c=dojo.lfx.html._byId(_59c);
var _5a0=[];
dojo.lang.forEach(_59c,function(node){
var _5a2={};
dojo.html.show(node);
var _5a3=dojo.html.getBorderBox(node).height;
dojo.html.hide(node);
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:1,end:function(){
return _5a3;
}}},_59d,_59e);
anim.connect("beforeBegin",function(){
_5a2.overflow=node.style.overflow;
_5a2.height=node.style.height;
with(node.style){
overflow="hidden";
_5a3="1px";
}
dojo.html.show(node);
});
anim.connect("onEnd",function(){
with(node.style){
overflow=_5a2.overflow;
_5a3=_5a2.height;
}
if(_59f){
_59f(node,anim);
}
});
_5a0.push(anim);
});
return dojo.lfx.combine(_5a0);
};
dojo.lfx.html.wipeOut=function(_5a5,_5a6,_5a7,_5a8){
_5a5=dojo.lfx.html._byId(_5a5);
var _5a9=[];
dojo.lang.forEach(_5a5,function(node){
var _5ab={};
var anim=dojo.lfx.propertyAnimation(node,{"height":{start:function(){
return dojo.html.getContentBox(node).height;
},end:1}},_5a6,_5a7,{"beforeBegin":function(){
_5ab.overflow=node.style.overflow;
_5ab.height=node.style.height;
with(node.style){
overflow="hidden";
}
dojo.html.show(node);
},"onEnd":function(){
dojo.html.hide(node);
with(node.style){
overflow=_5ab.overflow;
height=_5ab.height;
}
if(_5a8){
_5a8(node,anim);
}
}});
_5a9.push(anim);
});
return dojo.lfx.combine(_5a9);
};
dojo.lfx.html.slideTo=function(_5ad,_5ae,_5af,_5b0,_5b1){
_5ad=dojo.lfx.html._byId(_5ad);
var _5b2=[];
var _5b3=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_5ae)){
dojo.deprecated("dojo.lfx.html.slideTo(node, array)","use dojo.lfx.html.slideTo(node, {top: value, left: value});","0.5");
_5ae={top:_5ae[0],left:_5ae[1]};
}
dojo.lang.forEach(_5ad,function(node){
var top=null;
var left=null;
var init=(function(){
var _5b8=node;
return function(){
var pos=_5b3(_5b8,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_5b3(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_5b3(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_5b8,true);
dojo.html.setStyleAttributes(_5b8,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:(_5ae.top||0)},"left":{start:left,end:(_5ae.left||0)}},_5af,_5b0,{"beforeBegin":init});
if(_5b1){
anim.connect("onEnd",function(){
_5b1(_5ad,anim);
});
}
_5b2.push(anim);
});
return dojo.lfx.combine(_5b2);
};
dojo.lfx.html.slideBy=function(_5bc,_5bd,_5be,_5bf,_5c0){
_5bc=dojo.lfx.html._byId(_5bc);
var _5c1=[];
var _5c2=dojo.html.getComputedStyle;
if(dojo.lang.isArray(_5bd)){
dojo.deprecated("dojo.lfx.html.slideBy(node, array)","use dojo.lfx.html.slideBy(node, {top: value, left: value});","0.5");
_5bd={top:_5bd[0],left:_5bd[1]};
}
dojo.lang.forEach(_5bc,function(node){
var top=null;
var left=null;
var init=(function(){
var _5c7=node;
return function(){
var pos=_5c2(_5c7,"position");
top=(pos=="absolute"?node.offsetTop:parseInt(_5c2(node,"top"))||0);
left=(pos=="absolute"?node.offsetLeft:parseInt(_5c2(node,"left"))||0);
if(!dojo.lang.inArray(["absolute","relative"],pos)){
var ret=dojo.html.abs(_5c7,true);
dojo.html.setStyleAttributes(_5c7,"position:absolute;top:"+ret.y+"px;left:"+ret.x+"px;");
top=ret.y;
left=ret.x;
}
};
})();
init();
var anim=dojo.lfx.propertyAnimation(node,{"top":{start:top,end:top+(_5bd.top||0)},"left":{start:left,end:left+(_5bd.left||0)}},_5be,_5bf).connect("beforeBegin",init);
if(_5c0){
anim.connect("onEnd",function(){
_5c0(_5bc,anim);
});
}
_5c1.push(anim);
});
return dojo.lfx.combine(_5c1);
};
dojo.lfx.html.explode=function(_5cb,_5cc,_5cd,_5ce,_5cf){
var h=dojo.html;
_5cb=dojo.byId(_5cb);
_5cc=dojo.byId(_5cc);
var _5d1=h.toCoordinateObject(_5cb,true);
var _5d2=document.createElement("div");
h.copyStyle(_5d2,_5cc);
if(_5cc.explodeClassName){
_5d2.className=_5cc.explodeClassName;
}
with(_5d2.style){
position="absolute";
display="none";
}
dojo.body().appendChild(_5d2);
with(_5cc.style){
visibility="hidden";
display="block";
}
var _5d3=h.toCoordinateObject(_5cc,true);
with(_5cc.style){
display="none";
visibility="visible";
}
var _5d4={opacity:{start:0.5,end:1}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_5d4[type]={start:_5d1[type],end:_5d3[type]};
});
var anim=new dojo.lfx.propertyAnimation(_5d2,_5d4,_5cd,_5ce,{"beforeBegin":function(){
h.setDisplay(_5d2,"block");
},"onEnd":function(){
h.setDisplay(_5cc,"block");
_5d2.parentNode.removeChild(_5d2);
}});
if(_5cf){
anim.connect("onEnd",function(){
_5cf(_5cc,anim);
});
}
return anim;
};
dojo.lfx.html.implode=function(_5d7,end,_5d9,_5da,_5db){
var h=dojo.html;
_5d7=dojo.byId(_5d7);
end=dojo.byId(end);
var _5dd=dojo.html.toCoordinateObject(_5d7,true);
var _5de=dojo.html.toCoordinateObject(end,true);
var _5df=document.createElement("div");
dojo.html.copyStyle(_5df,_5d7);
if(_5d7.explodeClassName){
_5df.className=_5d7.explodeClassName;
}
dojo.html.setOpacity(_5df,0.3);
with(_5df.style){
position="absolute";
display="none";
backgroundColor=h.getStyle(_5d7,"background-color").toLowerCase();
}
dojo.body().appendChild(_5df);
var _5e0={opacity:{start:1,end:0.5}};
dojo.lang.forEach(["height","width","top","left"],function(type){
_5e0[type]={start:_5dd[type],end:_5de[type]};
});
var anim=new dojo.lfx.propertyAnimation(_5df,_5e0,_5d9,_5da,{"beforeBegin":function(){
dojo.html.hide(_5d7);
dojo.html.show(_5df);
},"onEnd":function(){
_5df.parentNode.removeChild(_5df);
}});
if(_5db){
anim.connect("onEnd",function(){
_5db(_5d7,anim);
});
}
return anim;
};
dojo.lfx.html.highlight=function(_5e3,_5e4,_5e5,_5e6,_5e7){
_5e3=dojo.lfx.html._byId(_5e3);
var _5e8=[];
dojo.lang.forEach(_5e3,function(node){
var _5ea=dojo.html.getBackgroundColor(node);
var bg=dojo.html.getStyle(node,"background-color").toLowerCase();
var _5ec=dojo.html.getStyle(node,"background-image");
var _5ed=(bg=="transparent"||bg=="rgba(0, 0, 0, 0)");
while(_5ea.length>3){
_5ea.pop();
}
var rgb=new dojo.gfx.color.Color(_5e4);
var _5ef=new dojo.gfx.color.Color(_5ea);
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:rgb,end:_5ef}},_5e5,_5e6,{"beforeBegin":function(){
if(_5ec){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+rgb.toRgb().join(",")+")";
},"onEnd":function(){
if(_5ec){
node.style.backgroundImage=_5ec;
}
if(_5ed){
node.style.backgroundColor="transparent";
}
if(_5e7){
_5e7(node,anim);
}
}});
_5e8.push(anim);
});
return dojo.lfx.combine(_5e8);
};
dojo.lfx.html.unhighlight=function(_5f1,_5f2,_5f3,_5f4,_5f5){
_5f1=dojo.lfx.html._byId(_5f1);
var _5f6=[];
dojo.lang.forEach(_5f1,function(node){
var _5f8=new dojo.gfx.color.Color(dojo.html.getBackgroundColor(node));
var rgb=new dojo.gfx.color.Color(_5f2);
var _5fa=dojo.html.getStyle(node,"background-image");
var anim=dojo.lfx.propertyAnimation(node,{"background-color":{start:_5f8,end:rgb}},_5f3,_5f4,{"beforeBegin":function(){
if(_5fa){
node.style.backgroundImage="none";
}
node.style.backgroundColor="rgb("+_5f8.toRgb().join(",")+")";
},"onEnd":function(){
if(_5f5){
_5f5(node,anim);
}
}});
_5f6.push(anim);
});
return dojo.lfx.combine(_5f6);
};
dojo.lang.mixin(dojo.lfx,dojo.lfx.html);
dojo.provide("dojo.lfx.*");
dojo.provide("dojo.lang.type");
dojo.lang.whatAmI=function(_5fc){
dojo.deprecated("dojo.lang.whatAmI","use dojo.lang.getType instead","0.5");
return dojo.lang.getType(_5fc);
};
dojo.lang.whatAmI.custom={};
dojo.lang.getType=function(_5fd){
try{
if(dojo.lang.isArray(_5fd)){
return "array";
}
if(dojo.lang.isFunction(_5fd)){
return "function";
}
if(dojo.lang.isString(_5fd)){
return "string";
}
if(dojo.lang.isNumber(_5fd)){
return "number";
}
if(dojo.lang.isBoolean(_5fd)){
return "boolean";
}
if(dojo.lang.isAlien(_5fd)){
return "alien";
}
if(dojo.lang.isUndefined(_5fd)){
return "undefined";
}
for(var name in dojo.lang.whatAmI.custom){
if(dojo.lang.whatAmI.custom[name](_5fd)){
return name;
}
}
if(dojo.lang.isObject(_5fd)){
return "object";
}
}
catch(e){
}
return "unknown";
};
dojo.lang.isNumeric=function(_5ff){
return (!isNaN(_5ff)&&isFinite(_5ff)&&(_5ff!=null)&&!dojo.lang.isBoolean(_5ff)&&!dojo.lang.isArray(_5ff)&&!/^\s*$/.test(_5ff));
};
dojo.lang.isBuiltIn=function(_600){
return (dojo.lang.isArray(_600)||dojo.lang.isFunction(_600)||dojo.lang.isString(_600)||dojo.lang.isNumber(_600)||dojo.lang.isBoolean(_600)||(_600==null)||(_600 instanceof Error)||(typeof _600=="error"));
};
dojo.lang.isPureObject=function(_601){
return ((_601!=null)&&dojo.lang.isObject(_601)&&_601.constructor==Object);
};
dojo.lang.isOfType=function(_602,type,_604){
var _605=false;
if(_604){
_605=_604["optional"];
}
if(_605&&((_602===null)||dojo.lang.isUndefined(_602))){
return true;
}
if(dojo.lang.isArray(type)){
var _606=type;
for(var i in _606){
var _608=_606[i];
if(dojo.lang.isOfType(_602,_608)){
return true;
}
}
return false;
}else{
if(dojo.lang.isString(type)){
type=type.toLowerCase();
}
switch(type){
case Array:
case "array":
return dojo.lang.isArray(_602);
case Function:
case "function":
return dojo.lang.isFunction(_602);
case String:
case "string":
return dojo.lang.isString(_602);
case Number:
case "number":
return dojo.lang.isNumber(_602);
case "numeric":
return dojo.lang.isNumeric(_602);
case Boolean:
case "boolean":
return dojo.lang.isBoolean(_602);
case Object:
case "object":
return dojo.lang.isObject(_602);
case "pureobject":
return dojo.lang.isPureObject(_602);
case "builtin":
return dojo.lang.isBuiltIn(_602);
case "alien":
return dojo.lang.isAlien(_602);
case "undefined":
return dojo.lang.isUndefined(_602);
case null:
case "null":
return (_602===null);
case "optional":
dojo.deprecated("dojo.lang.isOfType(value, [type, \"optional\"])","use dojo.lang.isOfType(value, type, {optional: true} ) instead","0.5");
return ((_602===null)||dojo.lang.isUndefined(_602));
default:
if(dojo.lang.isFunction(type)){
return (_602 instanceof type);
}else{
dojo.raise("dojo.lang.isOfType() was passed an invalid type");
}
}
}
dojo.raise("If we get here, it means a bug was introduced above.");
};
dojo.lang.getObject=function(str){
var _60a=str.split("."),i=0,obj=dj_global;
do{
obj=obj[_60a[i++]];
}while(i<_60a.length&&obj);
return (obj!=dj_global)?obj:null;
};
dojo.lang.doesObjectExist=function(str){
var _60e=str.split("."),i=0,obj=dj_global;
do{
obj=obj[_60e[i++]];
}while(i<_60e.length&&obj);
return (obj&&obj!=dj_global);
};
dojo.provide("dojo.lang.assert");
dojo.lang.assert=function(_611,_612){
if(!_611){
var _613="An assert statement failed.\n"+"The method dojo.lang.assert() was called with a 'false' value.\n";
if(_612){
_613+="Here's the assert message:\n"+_612+"\n";
}
throw new Error(_613);
}
};
dojo.lang.assertType=function(_614,type,_616){
if(dojo.lang.isString(_616)){
dojo.deprecated("dojo.lang.assertType(value, type, \"message\")","use dojo.lang.assertType(value, type) instead","0.5");
}
if(!dojo.lang.isOfType(_614,type,_616)){
if(!dojo.lang.assertType._errorMessage){
dojo.lang.assertType._errorMessage="Type mismatch: dojo.lang.assertType() failed.";
}
dojo.lang.assert(false,dojo.lang.assertType._errorMessage);
}
};
dojo.lang.assertValidKeywords=function(_617,_618,_619){
var key;
if(!_619){
if(!dojo.lang.assertValidKeywords._errorMessage){
dojo.lang.assertValidKeywords._errorMessage="In dojo.lang.assertValidKeywords(), found invalid keyword:";
}
_619=dojo.lang.assertValidKeywords._errorMessage;
}
if(dojo.lang.isArray(_618)){
for(key in _617){
if(!dojo.lang.inArray(_618,key)){
dojo.lang.assert(false,_619+" "+key);
}
}
}else{
for(key in _617){
if(!(key in _618)){
dojo.lang.assert(false,_619+" "+key);
}
}
}
};
dojo.provide("dojo.AdapterRegistry");
dojo.AdapterRegistry=function(_61b){
this.pairs=[];
this.returnWrappers=_61b||false;
};
dojo.lang.extend(dojo.AdapterRegistry,{register:function(name,_61d,wrap,_61f,_620){
var type=(_620)?"unshift":"push";
this.pairs[type]([name,_61d,wrap,_61f]);
},match:function(){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[1].apply(this,arguments)){
if((pair[3])||(this.returnWrappers)){
return pair[2];
}else{
return pair[2].apply(this,arguments);
}
}
}
throw new Error("No match found");
},unregister:function(name){
for(var i=0;i<this.pairs.length;i++){
var pair=this.pairs[i];
if(pair[0]==name){
this.pairs.splice(i,1);
return true;
}
}
return false;
}});
dojo.provide("dojo.lang.repr");
dojo.lang.reprRegistry=new dojo.AdapterRegistry();
dojo.lang.registerRepr=function(name,_628,wrap,_62a){
dojo.lang.reprRegistry.register(name,_628,wrap,_62a);
};
dojo.lang.repr=function(obj){
if(typeof (obj)=="undefined"){
return "undefined";
}else{
if(obj===null){
return "null";
}
}
try{
if(typeof (obj["__repr__"])=="function"){
return obj["__repr__"]();
}else{
if((typeof (obj["repr"])=="function")&&(obj.repr!=arguments.callee)){
return obj["repr"]();
}
}
return dojo.lang.reprRegistry.match(obj);
}
catch(e){
if(typeof (obj.NAME)=="string"&&(obj.toString==Function.prototype.toString||obj.toString==Object.prototype.toString)){
return obj.NAME;
}
}
if(typeof (obj)=="function"){
obj=(obj+"").replace(/^\s+/,"");
var idx=obj.indexOf("{");
if(idx!=-1){
obj=obj.substr(0,idx)+"{...}";
}
}
return obj+"";
};
dojo.lang.reprArrayLike=function(arr){
try{
var na=dojo.lang.map(arr,dojo.lang.repr);
return "["+na.join(", ")+"]";
}
catch(e){
}
};
(function(){
var m=dojo.lang;
m.registerRepr("arrayLike",m.isArrayLike,m.reprArrayLike);
m.registerRepr("string",m.isString,m.reprString);
m.registerRepr("numbers",m.isNumber,m.reprNumber);
m.registerRepr("boolean",m.isBoolean,m.reprNumber);
})();
dojo.provide("dojo.lang.declare");
dojo.lang.declare=function(_630,_631,init,_633){
if((dojo.lang.isFunction(_633))||((!_633)&&(!dojo.lang.isFunction(init)))){
var temp=_633;
_633=init;
init=temp;
}
var _635=[];
if(dojo.lang.isArray(_631)){
_635=_631;
_631=_635.shift();
}
if(!init){
init=dojo.evalObjPath(_630,false);
if((init)&&(!dojo.lang.isFunction(init))){
init=null;
}
}
var ctor=dojo.lang.declare._makeConstructor();
var scp=(_631?_631.prototype:null);
if(scp){
scp.prototyping=true;
ctor.prototype=new _631();
scp.prototyping=false;
}
ctor.superclass=scp;
ctor.mixins=_635;
for(var i=0,l=_635.length;i<l;i++){
dojo.lang.extend(ctor,_635[i].prototype);
}
ctor.prototype.initializer=null;
ctor.prototype.declaredClass=_630;
if(dojo.lang.isArray(_633)){
dojo.lang.extend.apply(dojo.lang,[ctor].concat(_633));
}else{
dojo.lang.extend(ctor,(_633)||{});
}
dojo.lang.extend(ctor,dojo.lang.declare._common);
ctor.prototype.constructor=ctor;
ctor.prototype.initializer=(ctor.prototype.initializer)||(init)||(function(){
});
dojo.lang.setObjPathValue(_630,ctor,null,true);
return ctor;
};
dojo.lang.declare._makeConstructor=function(){
return function(){
var self=this._getPropContext();
var s=self.constructor.superclass;
if((s)&&(s.constructor)){
if(s.constructor==arguments.callee){
this._inherited("constructor",arguments);
}else{
this._contextMethod(s,"constructor",arguments);
}
}
var ms=(self.constructor.mixins)||([]);
for(var i=0,m;(m=ms[i]);i++){
(((m.prototype)&&(m.prototype.initializer))||(m)).apply(this,arguments);
}
if((!this.prototyping)&&(self.initializer)){
self.initializer.apply(this,arguments);
}
};
};
dojo.lang.declare._common={_getPropContext:function(){
return (this.___proto||this);
},_contextMethod:function(_63f,_640,args){
var _642,_643=this.___proto;
this.___proto=_63f;
try{
_642=_63f[_640].apply(this,(args||[]));
}
catch(e){
throw e;
}
finally{
this.___proto=_643;
}
return _642;
},_inherited:function(prop,args){
var p=this._getPropContext();
do{
if((!p.constructor)||(!p.constructor.superclass)){
return;
}
p=p.constructor.superclass;
}while(!(prop in p));
return (dojo.lang.isFunction(p[prop])?this._contextMethod(p,prop,args):p[prop]);
}};
dojo.declare=dojo.lang.declare;
dojo.provide("dojo.lang.*");
dojo.provide("dojo.html.*");
dojo.provide("dojo.html.util");
dojo.html.getElementWindow=function(_647){
return dojo.html.getDocumentWindow(_647.ownerDocument);
};
dojo.html.getDocumentWindow=function(doc){
if(dojo.render.html.safari&&!doc._parentWindow){
var fix=function(win){
win.document._parentWindow=win;
for(var i=0;i<win.frames.length;i++){
fix(win.frames[i]);
}
};
fix(window.top);
}
if(dojo.render.html.ie&&window!==document.parentWindow&&!doc._parentWindow){
doc.parentWindow.execScript("document._parentWindow = window;","Javascript");
var win=doc._parentWindow;
doc._parentWindow=null;
return win;
}
return doc._parentWindow||doc.parentWindow||doc.defaultView;
};
dojo.html.gravity=function(node,e){
node=dojo.byId(node);
var _64f=dojo.html.getCursorPosition(e);
with(dojo.html){
var _650=getAbsolutePosition(node,true);
var bb=getBorderBox(node);
var _652=_650.x+(bb.width/2);
var _653=_650.y+(bb.height/2);
}
with(dojo.html.gravity){
return ((_64f.x<_652?WEST:EAST)|(_64f.y<_653?NORTH:SOUTH));
}
};
dojo.html.gravity.NORTH=1;
dojo.html.gravity.SOUTH=1<<1;
dojo.html.gravity.EAST=1<<2;
dojo.html.gravity.WEST=1<<3;
dojo.html.overElement=function(_654,e){
_654=dojo.byId(_654);
var _656=dojo.html.getCursorPosition(e);
var bb=dojo.html.getBorderBox(_654);
var _658=dojo.html.getAbsolutePosition(_654,true,dojo.html.boxSizing.BORDER_BOX);
var top=_658.y;
var _65a=top+bb.height;
var left=_658.x;
var _65c=left+bb.width;
return (_656.x>=left&&_656.x<=_65c&&_656.y>=top&&_656.y<=_65a);
};
dojo.html.renderedTextContent=function(node){
node=dojo.byId(node);
var _65e="";
if(node==null){
return _65e;
}
for(var i=0;i<node.childNodes.length;i++){
switch(node.childNodes[i].nodeType){
case 1:
case 5:
var _660="unknown";
try{
_660=dojo.html.getStyle(node.childNodes[i],"display");
}
catch(E){
}
switch(_660){
case "block":
case "list-item":
case "run-in":
case "table":
case "table-row-group":
case "table-header-group":
case "table-footer-group":
case "table-row":
case "table-column-group":
case "table-column":
case "table-cell":
case "table-caption":
_65e+="\n";
_65e+=dojo.html.renderedTextContent(node.childNodes[i]);
_65e+="\n";
break;
case "none":
break;
default:
if(node.childNodes[i].tagName&&node.childNodes[i].tagName.toLowerCase()=="br"){
_65e+="\n";
}else{
_65e+=dojo.html.renderedTextContent(node.childNodes[i]);
}
break;
}
break;
case 3:
case 2:
case 4:
var text=node.childNodes[i].nodeValue;
var _662="unknown";
try{
_662=dojo.html.getStyle(node,"text-transform");
}
catch(E){
}
switch(_662){
case "capitalize":
var _663=text.split(" ");
for(var i=0;i<_663.length;i++){
_663[i]=_663[i].charAt(0).toUpperCase()+_663[i].substring(1);
}
text=_663.join(" ");
break;
case "uppercase":
text=text.toUpperCase();
break;
case "lowercase":
text=text.toLowerCase();
break;
default:
break;
}
switch(_662){
case "nowrap":
break;
case "pre-wrap":
break;
case "pre-line":
break;
case "pre":
break;
default:
text=text.replace(/\s+/," ");
if(/\s$/.test(_65e)){
text.replace(/^\s/,"");
}
break;
}
_65e+=text;
break;
default:
break;
}
}
return _65e;
};
dojo.html.createNodesFromText=function(txt,trim){
if(trim){
txt=txt.replace(/^\s+|\s+$/g,"");
}
var tn=dojo.doc().createElement("div");
tn.style.visibility="hidden";
dojo.body().appendChild(tn);
var _667="none";
if((/^<t[dh][\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody><tr>"+txt+"</tr></tbody></table>";
_667="cell";
}else{
if((/^<tr[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table><tbody>"+txt+"</tbody></table>";
_667="row";
}else{
if((/^<(thead|tbody|tfoot)[\s\r\n>]/i).test(txt.replace(/^\s+/))){
txt="<table>"+txt+"</table>";
_667="section";
}
}
}
tn.innerHTML=txt;
if(tn["normalize"]){
tn.normalize();
}
var _668=null;
switch(_667){
case "cell":
_668=tn.getElementsByTagName("tr")[0];
break;
case "row":
_668=tn.getElementsByTagName("tbody")[0];
break;
case "section":
_668=tn.getElementsByTagName("table")[0];
break;
default:
_668=tn;
break;
}
var _669=[];
for(var x=0;x<_668.childNodes.length;x++){
_669.push(_668.childNodes[x].cloneNode(true));
}
tn.style.display="none";
dojo.body().removeChild(tn);
return _669;
};
dojo.html.placeOnScreen=function(node,_66c,_66d,_66e,_66f,_670,_671){
if(_66c instanceof Array||typeof _66c=="array"){
_671=_670;
_670=_66f;
_66f=_66e;
_66e=_66d;
_66d=_66c[1];
_66c=_66c[0];
}
if(_670 instanceof String||typeof _670=="string"){
_670=_670.split(",");
}
if(!isNaN(_66e)){
_66e=[Number(_66e),Number(_66e)];
}else{
if(!(_66e instanceof Array||typeof _66e=="array")){
_66e=[0,0];
}
}
var _672=dojo.html.getScroll().offset;
var view=dojo.html.getViewport();
node=dojo.byId(node);
var _674=node.style.display;
node.style.display="";
var bb=dojo.html.getBorderBox(node);
var w=bb.width;
var h=bb.height;
node.style.display=_674;
if(!(_670 instanceof Array||typeof _670=="array")){
_670=["TL"];
}
var _678,_679,_67a=Infinity,_67b;
for(var _67c=0;_67c<_670.length;++_67c){
var _67d=_670[_67c];
var _67e=true;
var tryX=_66c-(_67d.charAt(1)=="L"?0:w)+_66e[0]*(_67d.charAt(1)=="L"?1:-1);
var tryY=_66d-(_67d.charAt(0)=="T"?0:h)+_66e[1]*(_67d.charAt(0)=="T"?1:-1);
if(_66f){
tryX-=_672.x;
tryY-=_672.y;
}
if(tryX<0){
tryX=0;
_67e=false;
}
if(tryY<0){
tryY=0;
_67e=false;
}
var x=tryX+w;
if(x>view.width){
x=view.width-w;
_67e=false;
}else{
x=tryX;
}
x=Math.max(_66e[0],x)+_672.x;
var y=tryY+h;
if(y>view.height){
y=view.height-h;
_67e=false;
}else{
y=tryY;
}
y=Math.max(_66e[1],y)+_672.y;
if(_67e){
_678=x;
_679=y;
_67a=0;
_67b=_67d;
break;
}else{
var dist=Math.pow(x-tryX-_672.x,2)+Math.pow(y-tryY-_672.y,2);
if(_67a>dist){
_67a=dist;
_678=x;
_679=y;
_67b=_67d;
}
}
}
if(!_671){
node.style.left=_678+"px";
node.style.top=_679+"px";
}
return {left:_678,top:_679,x:_678,y:_679,dist:_67a,corner:_67b};
};
dojo.html.placeOnScreenPoint=function(node,_685,_686,_687,_688){
dojo.deprecated("dojo.html.placeOnScreenPoint","use dojo.html.placeOnScreen() instead","0.5");
return dojo.html.placeOnScreen(node,_685,_686,_687,_688,["TL","TR","BL","BR"]);
};
dojo.html.placeOnScreenAroundElement=function(node,_68a,_68b,_68c,_68d,_68e){
var best,_690=Infinity;
_68a=dojo.byId(_68a);
var _691=_68a.style.display;
_68a.style.display="";
var mb=dojo.html.getElementBox(_68a,_68c);
var _693=mb.width;
var _694=mb.height;
var _695=dojo.html.getAbsolutePosition(_68a,true,_68c);
_68a.style.display=_691;
for(var _696 in _68d){
var pos,_698,_699;
var _69a=_68d[_696];
_698=_695.x+(_696.charAt(1)=="L"?0:_693);
_699=_695.y+(_696.charAt(0)=="T"?0:_694);
pos=dojo.html.placeOnScreen(node,_698,_699,_68b,true,_69a,true);
if(pos.dist==0){
best=pos;
break;
}else{
if(_690>pos.dist){
_690=pos.dist;
best=pos;
}
}
}
if(!_68e){
node.style.left=best.left+"px";
node.style.top=best.top+"px";
}
return best;
};
dojo.html.scrollIntoView=function(node){
if(!node){
return;
}
if(dojo.render.html.ie){
if(dojo.html.getBorderBox(node.parentNode).height<node.parentNode.scrollHeight){
node.scrollIntoView(false);
}
}else{
if(dojo.render.html.mozilla){
node.scrollIntoView(false);
}else{
var _69c=node.parentNode;
var _69d=_69c.scrollTop+dojo.html.getBorderBox(_69c).height;
var _69e=node.offsetTop+dojo.html.getMarginBox(node).height;
if(_69d<_69e){
_69c.scrollTop+=(_69e-_69d);
}else{
if(_69c.scrollTop>node.offsetTop){
_69c.scrollTop-=(_69c.scrollTop-node.offsetTop);
}
}
}
}
};
dojo.provide("dojo.html.selection");
dojo.html.selectionType={NONE:0,TEXT:1,CONTROL:2};
dojo.html.clearSelection=function(){
var _69f=dojo.global();
var _6a0=dojo.doc();
try{
if(_69f["getSelection"]){
if(dojo.render.html.safari){
_69f.getSelection().collapse();
}else{
_69f.getSelection().removeAllRanges();
}
}else{
if(_6a0.selection){
if(_6a0.selection.empty){
_6a0.selection.empty();
}else{
if(_6a0.selection.clear){
_6a0.selection.clear();
}
}
}
}
return true;
}
catch(e){
dojo.debug(e);
return false;
}
};
dojo.html.disableSelection=function(_6a1){
_6a1=dojo.byId(_6a1)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_6a1.style.MozUserSelect="none";
}else{
if(h.safari){
_6a1.style.KhtmlUserSelect="none";
}else{
if(h.ie){
_6a1.unselectable="on";
}else{
return false;
}
}
}
return true;
};
dojo.html.enableSelection=function(_6a3){
_6a3=dojo.byId(_6a3)||dojo.body();
var h=dojo.render.html;
if(h.mozilla){
_6a3.style.MozUserSelect="";
}else{
if(h.safari){
_6a3.style.KhtmlUserSelect="";
}else{
if(h.ie){
_6a3.unselectable="off";
}else{
return false;
}
}
}
return true;
};
dojo.html.selectElement=function(_6a5){
dojo.deprecated("dojo.html.selectElement","replaced by dojo.html.selection.selectElementChildren",0.5);
};
dojo.html.selectInputText=function(_6a6){
var _6a7=dojo.global();
var _6a8=dojo.doc();
_6a6=dojo.byId(_6a6);
if(_6a8["selection"]&&dojo.body()["createTextRange"]){
var _6a9=_6a6.createTextRange();
_6a9.moveStart("character",0);
_6a9.moveEnd("character",_6a6.value.length);
_6a9.select();
}else{
if(_6a7["getSelection"]){
var _6aa=_6a7.getSelection();
_6a6.setSelectionRange(0,_6a6.value.length);
}
}
_6a6.focus();
};
dojo.html.isSelectionCollapsed=function(){
dojo.deprecated("dojo.html.isSelectionCollapsed","replaced by dojo.html.selection.isCollapsed",0.5);
return dojo.html.selection.isCollapsed();
};
dojo.lang.mixin(dojo.html.selection,{getType:function(){
if(dojo.doc()["selection"]){
return dojo.html.selectionType[dojo.doc().selection.type.toUpperCase()];
}else{
var _6ab=dojo.html.selectionType.TEXT;
var oSel;
try{
oSel=dojo.global().getSelection();
}
catch(e){
}
if(oSel&&oSel.rangeCount==1){
var _6ad=oSel.getRangeAt(0);
if(_6ad.startContainer==_6ad.endContainer&&(_6ad.endOffset-_6ad.startOffset)==1&&_6ad.startContainer.nodeType!=dojo.dom.TEXT_NODE){
_6ab=dojo.html.selectionType.CONTROL;
}
}
return _6ab;
}
},isCollapsed:function(){
var _6ae=dojo.global();
var _6af=dojo.doc();
if(_6af["selection"]){
return _6af.selection.createRange().text=="";
}else{
if(_6ae["getSelection"]){
var _6b0=_6ae.getSelection();
if(dojo.lang.isString(_6b0)){
return _6b0=="";
}else{
return _6b0.isCollapsed||_6b0.toString()=="";
}
}
}
},getSelectedElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
if(dojo.doc()["selection"]){
var _6b1=dojo.doc().selection.createRange();
if(_6b1&&_6b1.item){
return dojo.doc().selection.createRange().item(0);
}
}else{
var _6b2=dojo.global().getSelection();
return _6b2.anchorNode.childNodes[_6b2.anchorOffset];
}
}
},getParentElement:function(){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
var p=dojo.html.selection.getSelectedElement();
if(p){
return p.parentNode;
}
}else{
if(dojo.doc()["selection"]){
return dojo.doc().selection.createRange().parentElement();
}else{
var _6b4=dojo.global().getSelection();
if(_6b4){
var node=_6b4.anchorNode;
while(node&&node.nodeType!=dojo.dom.ELEMENT_NODE){
node=node.parentNode;
}
return node;
}
}
}
},getSelectedText:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().text;
}else{
var _6b6=dojo.global().getSelection();
if(_6b6){
return _6b6.toString();
}
}
},getSelectedHtml:function(){
if(dojo.doc()["selection"]){
if(dojo.html.selection.getType()==dojo.html.selectionType.CONTROL){
return null;
}
return dojo.doc().selection.createRange().htmlText;
}else{
var _6b7=dojo.global().getSelection();
if(_6b7&&_6b7.rangeCount){
var frag=_6b7.getRangeAt(0).cloneContents();
var div=document.createElement("div");
div.appendChild(frag);
return div.innerHTML;
}
return null;
}
},hasAncestorElement:function(_6ba){
return (dojo.html.selection.getAncestorElement.apply(this,arguments)!=null);
},getAncestorElement:function(_6bb){
var node=dojo.html.selection.getSelectedElement()||dojo.html.selection.getParentElement();
while(node){
if(dojo.html.selection.isTag(node,arguments).length>0){
return node;
}
node=node.parentNode;
}
return null;
},isTag:function(node,tags){
if(node&&node.tagName){
for(var i=0;i<tags.length;i++){
if(node.tagName.toLowerCase()==String(tags[i]).toLowerCase()){
return String(tags[i]).toLowerCase();
}
}
}
return "";
},selectElement:function(_6c0){
var _6c1=dojo.global();
var _6c2=dojo.doc();
_6c0=dojo.byId(_6c0);
if(_6c2.selection&&dojo.body().createTextRange){
try{
var _6c3=dojo.body().createControlRange();
_6c3.addElement(_6c0);
_6c3.select();
}
catch(e){
dojo.html.selection.selectElementChildren(_6c0);
}
}else{
if(_6c1["getSelection"]){
var _6c4=_6c1.getSelection();
if(_6c4["removeAllRanges"]){
var _6c3=_6c2.createRange();
_6c3.selectNode(_6c0);
_6c4.removeAllRanges();
_6c4.addRange(_6c3);
}
}
}
},selectElementChildren:function(_6c5){
var _6c6=dojo.global();
var _6c7=dojo.doc();
_6c5=dojo.byId(_6c5);
if(_6c7.selection&&dojo.body().createTextRange){
var _6c8=dojo.body().createTextRange();
_6c8.moveToElementText(_6c5);
_6c8.select();
}else{
if(_6c6["getSelection"]){
var _6c9=_6c6.getSelection();
if(_6c9["setBaseAndExtent"]){
_6c9.setBaseAndExtent(_6c5,0,_6c5,_6c5.innerText.length-1);
}else{
if(_6c9["selectAllChildren"]){
_6c9.selectAllChildren(_6c5);
}
}
}
}
},getBookmark:function(){
var _6ca;
var _6cb=dojo.doc();
if(_6cb["selection"]){
var _6cc=_6cb.selection.createRange();
_6ca=_6cc.getBookmark();
}else{
var _6cd;
try{
_6cd=dojo.global().getSelection();
}
catch(e){
}
if(_6cd){
var _6cc=_6cd.getRangeAt(0);
_6ca=_6cc.cloneRange();
}else{
dojo.debug("No idea how to store the current selection for this browser!");
}
}
return _6ca;
},moveToBookmark:function(_6ce){
var _6cf=dojo.doc();
if(_6cf["selection"]){
var _6d0=_6cf.selection.createRange();
_6d0.moveToBookmark(_6ce);
_6d0.select();
}else{
var _6d1;
try{
_6d1=dojo.global().getSelection();
}
catch(e){
}
if(_6d1&&_6d1["removeAllRanges"]){
_6d1.removeAllRanges();
_6d1.addRange(_6ce);
}else{
dojo.debug("No idea how to restore selection for this browser!");
}
}
},collapse:function(_6d2){
if(dojo.global()["getSelection"]){
var _6d3=dojo.global().getSelection();
if(_6d3.removeAllRanges){
if(_6d2){
_6d3.collapseToStart();
}else{
_6d3.collapseToEnd();
}
}else{
dojo.global().getSelection().collapse(_6d2);
}
}else{
if(dojo.doc().selection){
var _6d4=dojo.doc().selection.createRange();
_6d4.collapse(_6d2);
_6d4.select();
}
}
},remove:function(){
if(dojo.doc().selection){
var _6d5=dojo.doc().selection;
if(_6d5.type.toUpperCase()!="NONE"){
_6d5.clear();
}
return _6d5;
}else{
var _6d5=dojo.global().getSelection();
for(var i=0;i<_6d5.rangeCount;i++){
_6d5.getRangeAt(i).deleteContents();
}
return _6d5;
}
}});
dojo.provide("dojo.html.iframe");
dojo.html.iframeContentWindow=function(_6d7){
var win=dojo.html.getDocumentWindow(dojo.html.iframeContentDocument(_6d7))||dojo.html.iframeContentDocument(_6d7).__parent__||(_6d7.name&&document.frames[_6d7.name])||null;
return win;
};
dojo.html.iframeContentDocument=function(_6d9){
var doc=_6d9.contentDocument||((_6d9.contentWindow)&&(_6d9.contentWindow.document))||((_6d9.name)&&(document.frames[_6d9.name])&&(document.frames[_6d9.name].document))||null;
return doc;
};
dojo.html.BackgroundIframe=function(node){
if(dojo.render.html.ie55||dojo.render.html.ie60){
var html="<iframe src='javascript:false'"+"' style='position: absolute; left: 0px; top: 0px; width: 100%; height: 100%;"+"z-index: -1; filter:Alpha(Opacity=\"0\");' "+">";
this.iframe=dojo.doc().createElement(html);
this.iframe.tabIndex=-1;
if(node){
node.appendChild(this.iframe);
this.domNode=node;
}else{
dojo.body().appendChild(this.iframe);
this.iframe.style.display="none";
}
}
};
dojo.lang.extend(dojo.html.BackgroundIframe,{iframe:null,onResized:function(){
if(this.iframe&&this.domNode&&this.domNode.parentNode){
var _6dd=dojo.html.getMarginBox(this.domNode);
if(_6dd.width==0||_6dd.height==0){
dojo.lang.setTimeout(this,this.onResized,100);
return;
}
this.iframe.style.width=_6dd.width+"px";
this.iframe.style.height=_6dd.height+"px";
}
},size:function(node){
if(!this.iframe){
return;
}
var _6df=dojo.html.toCoordinateObject(node,true,dojo.html.boxSizing.BORDER_BOX);
this.iframe.style.width=_6df.width+"px";
this.iframe.style.height=_6df.height+"px";
this.iframe.style.left=_6df.left+"px";
this.iframe.style.top=_6df.top+"px";
},setZIndex:function(node){
if(!this.iframe){
return;
}
if(dojo.dom.isNode(node)){
this.iframe.style.zIndex=dojo.html.getStyle(node,"z-index")-1;
}else{
if(!isNaN(node)){
this.iframe.style.zIndex=node;
}
}
},show:function(){
if(!this.iframe){
return;
}
this.iframe.style.display="block";
},hide:function(){
if(!this.iframe){
return;
}
this.iframe.style.display="none";
},remove:function(){
dojo.html.removeNode(this.iframe);
}});
dojo.provide("dojo.json");
dojo.json={jsonRegistry:new dojo.AdapterRegistry(),register:function(name,_6e2,wrap,_6e4){
dojo.json.jsonRegistry.register(name,_6e2,wrap,_6e4);
},evalJson:function(json){
try{
return eval("("+json+")");
}
catch(e){
dojo.debug(e);
return json;
}
},serialize:function(o){
var _6e7=typeof (o);
if(_6e7=="undefined"){
return "undefined";
}else{
if((_6e7=="number")||(_6e7=="boolean")){
return o+"";
}else{
if(o===null){
return "null";
}
}
}
if(_6e7=="string"){
return dojo.string.escapeString(o);
}
var me=arguments.callee;
var _6e9;
if(typeof (o.__json__)=="function"){
_6e9=o.__json__();
if(o!==_6e9){
return me(_6e9);
}
}
if(typeof (o.json)=="function"){
_6e9=o.json();
if(o!==_6e9){
return me(_6e9);
}
}
if(_6e7!="function"&&typeof (o.length)=="number"){
var res=[];
for(var i=0;i<o.length;i++){
var val=me(o[i]);
if(typeof (val)!="string"){
val="undefined";
}
res.push(val);
}
return "["+res.join(",")+"]";
}
try{
window.o=o;
_6e9=dojo.json.jsonRegistry.match(o);
return me(_6e9);
}
catch(e){
}
if(_6e7=="function"){
return null;
}
res=[];
for(var k in o){
var _6ee;
if(typeof (k)=="number"){
_6ee="\""+k+"\"";
}else{
if(typeof (k)=="string"){
_6ee=dojo.string.escapeString(k);
}else{
continue;
}
}
val=me(o[k]);
if(typeof (val)!="string"){
continue;
}
res.push(_6ee+":"+val);
}
return "{"+res.join(",")+"}";
}};

