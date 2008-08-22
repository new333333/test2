

/* timeline.js */



Timeline.strings={};

Timeline.create=function(elmt,bandInfos,orientation,unit){
return new Timeline._Impl(elmt,bandInfos,orientation,unit);
};

Timeline.HORIZONTAL=0;
Timeline.VERTICAL=1;

Timeline._defaultTheme=null;

Timeline.createBandInfo=function(params){
var theme=("theme"in params)?params.theme:Timeline.getDefaultTheme();

var eventSource=("eventSource"in params)?params.eventSource:null;

var ether=new Timeline.LinearEther({
centersOn:("date"in params)?params.date:new Date(),
interval:Timeline.DateTime.gregorianUnitLengths[params.intervalUnit],
pixelsPerInterval:params.intervalPixels
});

var etherPainter=new Timeline.GregorianEtherPainter({
unit:params.intervalUnit,
multiple:("multiple"in params)?params.multiple:1,
theme:theme,
align:("align"in params)?params.align:undefined
});

var layout=new Timeline.StaticTrackBasedLayout({
eventSource:eventSource,
ether:ether,
showText:("showEventText"in params)?params.showEventText:true,
theme:theme
});

var eventPainterParams={
showText:("showEventText"in params)?params.showEventText:true,
layout:layout,
theme:theme
};
if("trackHeight"in params){
eventPainterParams.trackHeight=params.trackHeight;
}
if("trackGap"in params){
eventPainterParams.trackGap=params.trackGap;
}
var eventPainter=new Timeline.DurationEventPainter(eventPainterParams);

return{
width:params.width,
eventSource:eventSource,
timeZone:("timeZone"in params)?params.timeZone:0,
ether:ether,
etherPainter:etherPainter,
eventPainter:eventPainter
};
};

Timeline.createHotZoneBandInfo=function(params){
var theme=("theme"in params)?params.theme:Timeline.getDefaultTheme();

var eventSource=("eventSource"in params)?params.eventSource:null;

var ether=new Timeline.HotZoneEther({
centersOn:("date"in params)?params.date:new Date(),
interval:Timeline.DateTime.gregorianUnitLengths[params.intervalUnit],
pixelsPerInterval:params.intervalPixels,
zones:params.zones
});

var etherPainter=new Timeline.HotZoneGregorianEtherPainter({
unit:params.intervalUnit,
zones:params.zones,
theme:theme,
align:("align"in params)?params.align:undefined
});

var layout=new Timeline.StaticTrackBasedLayout({
eventSource:eventSource,
ether:ether,
theme:theme
});

var eventPainterParams={
showText:("showEventText"in params)?params.showEventText:true,
layout:layout,
theme:theme
};
if("trackHeight"in params){
eventPainterParams.trackHeight=params.trackHeight;
}
if("trackGap"in params){
eventPainterParams.trackGap=params.trackGap;
}
var eventPainter=new Timeline.DurationEventPainter(eventPainterParams);

return{
width:params.width,
eventSource:eventSource,
timeZone:("timeZone"in params)?params.timeZone:0,
ether:ether,
etherPainter:etherPainter,
eventPainter:eventPainter
};
};

Timeline.getDefaultTheme=function(){
if(Timeline._defaultTheme==null){
Timeline._defaultTheme=Timeline.ClassicTheme.create(Timeline.Platform.getDefaultLocale());
}
return Timeline._defaultTheme;
};

Timeline.setDefaultTheme=function(theme){
Timeline._defaultTheme=theme;
};

Timeline.loadXML=function(url,f){
var fError=function(statusText,status,xmlhttp){
alert("Failed to load data xml from "+url+"\n"+statusText);
};
var fDone=function(xmlhttp){
var xml=xmlhttp.responseXML;
if(!xml.documentElement&&xmlhttp.responseStream){
xml.load(xmlhttp.responseStream);
}
f(xml,url);
};
Timeline.XmlHttp.get(url,fError,fDone);
};


Timeline.loadJSON=function(url,f){
var fError=function(statusText,status,xmlhttp){
alert("Failed to load json data from "+url+"\n"+statusText);
};
var fDone=function(xmlhttp){
f(eval('('+xmlhttp.responseText+')'),url);
};
Timeline.XmlHttp.get(url,fError,fDone);
};


Timeline._Impl=function(elmt,bandInfos,orientation,unit){
this._containerDiv=elmt;

this._bandInfos=bandInfos;
this._orientation=orientation==null?Timeline.HORIZONTAL:orientation;
this._unit=(unit!=null)?unit:Timeline.NativeDateUnit;

this._initialize();
};

Timeline._Impl.prototype.dispose=function(){
for(var i=0;i<this._bands.length;i++){
this._bands[i].dispose();
}
this._bands=null;
this._bandInfos=null;
this._containerDiv.innerHTML="";
};

Timeline._Impl.prototype.getBandCount=function(){
return this._bands.length;
};

Timeline._Impl.prototype.getBand=function(index){
return this._bands[index];
};

Timeline._Impl.prototype.layout=function(){
this._distributeWidths();
};

Timeline._Impl.prototype.paint=function(){
for(var i=0;i<this._bands.length;i++){
this._bands[i].paint();
}
};

Timeline._Impl.prototype.getDocument=function(){
return this._containerDiv.ownerDocument;
};

Timeline._Impl.prototype.addDiv=function(div){
this._containerDiv.appendChild(div);
};

Timeline._Impl.prototype.removeDiv=function(div){
this._containerDiv.removeChild(div);
};

Timeline._Impl.prototype.isHorizontal=function(){
return this._orientation==Timeline.HORIZONTAL;
};

Timeline._Impl.prototype.isVertical=function(){
return this._orientation==Timeline.VERTICAL;
};

Timeline._Impl.prototype.getPixelLength=function(){
return this._orientation==Timeline.HORIZONTAL?
this._containerDiv.offsetWidth:this._containerDiv.offsetHeight;
};

Timeline._Impl.prototype.getPixelWidth=function(){
return this._orientation==Timeline.VERTICAL?
this._containerDiv.offsetWidth:this._containerDiv.offsetHeight;
};

Timeline._Impl.prototype.getUnit=function(){
return this._unit;
};

Timeline._Impl.prototype.loadXML=function(url,f){
var tl=this;


var fError=function(statusText,status,xmlhttp){
alert("Failed to load data xml from "+url+"\n"+statusText);
tl.hideLoadingMessage();
};
var fDone=function(xmlhttp){
try{
var xml=xmlhttp.responseXML;
if(!xml.documentElement&&xmlhttp.responseStream){
xml.load(xmlhttp.responseStream);
}
f(xml,url);
}finally{
tl.hideLoadingMessage();
}
};

this.showLoadingMessage();
window.setTimeout(function(){Timeline.XmlHttp.get(url,fError,fDone);},0);
};

Timeline._Impl.prototype.loadJSON=function(url,f){
var tl=this;


var fError=function(statusText,status,xmlhttp){
alert("Failed to load json data from "+url+"\n"+statusText);
tl.hideLoadingMessage();
};
var fDone=function(xmlhttp){
try{
f(eval('('+xmlhttp.responseText+')'),url);
}finally{
tl.hideLoadingMessage();
}
};

this.showLoadingMessage();
window.setTimeout(function(){Timeline.XmlHttp.get(url,fError,fDone);},0);
};

Timeline._Impl.prototype._initialize=function(){
var containerDiv=this._containerDiv;
var doc=containerDiv.ownerDocument;

containerDiv.className=
containerDiv.className.split(" ").concat("timeline-container").join(" ");

while(containerDiv.firstChild){
containerDiv.removeChild(containerDiv.firstChild);
}


var elmtCopyright=Timeline.Graphics.createTranslucentImage(doc,Timeline.urlPrefix+(this.isHorizontal()?"images/copyright-vertical.png":"images/copyright.png"));
elmtCopyright.className="timeline-copyright";
elmtCopyright.title="Timeline (c) SIMILE - http://simile.mit.edu/timeline/";
Timeline.DOM.registerEvent(elmtCopyright,"click",function(){window.location="http://simile.mit.edu/timeline/";});
containerDiv.appendChild(elmtCopyright);


this._bands=[];
for(var i=0;i<this._bandInfos.length;i++){
var band=new Timeline._Band(this,this._bandInfos[i],i);
this._bands.push(band);
}
this._distributeWidths();


for(var i=0;i<this._bandInfos.length;i++){
var bandInfo=this._bandInfos[i];
if("syncWith"in bandInfo){
this._bands[i].setSyncWithBand(
this._bands[bandInfo.syncWith],
("highlight"in bandInfo)?bandInfo.highlight:false
);
}
}


var message=Timeline.Graphics.createMessageBubble(doc);
message.containerDiv.className="timeline-message-container";
containerDiv.appendChild(message.containerDiv);

message.contentDiv.className="timeline-message";
message.contentDiv.innerHTML="<img src='"+Timeline.urlPrefix+"images/progress-running.gif' /> Loading...";

this.showLoadingMessage=function(){message.containerDiv.style.display="block";};
this.hideLoadingMessage=function(){message.containerDiv.style.display="none";};
};

Timeline._Impl.prototype._distributeWidths=function(){
var length=this.getPixelLength();
var width=this.getPixelWidth();
var cumulativeWidth=0;

for(var i=0;i<this._bands.length;i++){
var band=this._bands[i];
var bandInfos=this._bandInfos[i];
var widthString=bandInfos.width;

var x=widthString.indexOf("%");
if(x>0){
var percent=parseInt(widthString.substr(0,x));
var bandWidth=percent*width/100;
}else{
var bandWidth=parseInt(widthString);
}

band.setBandShiftAndWidth(cumulativeWidth,bandWidth);
band.setViewLength(length);

cumulativeWidth+=bandWidth;
}
};


Timeline._Band=function(timeline,bandInfo,index){
this._timeline=timeline;
this._bandInfo=bandInfo;
this._index=index;

this._locale=("locale"in bandInfo)?bandInfo.locale:Timeline.Platform.getDefaultLocale();
this._timeZone=("timeZone"in bandInfo)?bandInfo.timeZone:0;
this._labeller=("labeller"in bandInfo)?bandInfo.labeller:
timeline.getUnit().createLabeller(this._locale,this._timeZone);

this._dragging=false;
this._changing=false;
this._originalScrollSpeed=5;
this._scrollSpeed=this._originalScrollSpeed;
this._onScrollListeners=[];

var b=this;
this._syncWithBand=null;
this._syncWithBandHandler=function(band){
b._onHighlightBandScroll();
};
this._selectorListener=function(band){
b._onHighlightBandScroll();
};


var inputDiv=this._timeline.getDocument().createElement("div");
inputDiv.className="timeline-band-input";
this._timeline.addDiv(inputDiv);

this._keyboardInput=document.createElement("input");
this._keyboardInput.type="text";
inputDiv.appendChild(this._keyboardInput);
Timeline.DOM.registerEventWithObject(this._keyboardInput,"keydown",this,this._onKeyDown);
Timeline.DOM.registerEventWithObject(this._keyboardInput,"keyup",this,this._onKeyUp);


this._div=this._timeline.getDocument().createElement("div");
this._div.className="timeline-band";
this._timeline.addDiv(this._div);

Timeline.DOM.registerEventWithObject(this._div,"mousedown",this,this._onMouseDown);
Timeline.DOM.registerEventWithObject(this._div,"mousemove",this,this._onMouseMove);
Timeline.DOM.registerEventWithObject(this._div,"mouseup",this,this._onMouseUp);
Timeline.DOM.registerEventWithObject(this._div,"mouseout",this,this._onMouseOut);
Timeline.DOM.registerEventWithObject(this._div,"dblclick",this,this._onDblClick);


this._innerDiv=this._timeline.getDocument().createElement("div");
this._innerDiv.className="timeline-band-inner";
this._div.appendChild(this._innerDiv);


this._ether=bandInfo.ether;
bandInfo.ether.initialize(timeline);

this._etherPainter=bandInfo.etherPainter;
bandInfo.etherPainter.initialize(this,timeline);

this._eventSource=bandInfo.eventSource;
if(this._eventSource){
this._eventListener={
onAddMany:function(){b._onAddMany();},
onClear:function(){b._onClear();}
}
this._eventSource.addListener(this._eventListener);
}

this._eventPainter=bandInfo.eventPainter;
bandInfo.eventPainter.initialize(this,timeline);

this._decorators=("decorators"in bandInfo)?bandInfo.decorators:[];
for(var i=0;i<this._decorators.length;i++){
this._decorators[i].initialize(this,timeline);
}

this._bubble=null;
};

Timeline._Band.SCROLL_MULTIPLES=5;

Timeline._Band.prototype.dispose=function(){
this.closeBubble();

if(this._eventSource){
this._eventSource.removeListener(this._eventListener);
this._eventListener=null;
this._eventSource=null;
}

this._timeline=null;
this._bandInfo=null;

this._labeller=null;
this._ether=null;
this._etherPainter=null;
this._eventPainter=null;
this._decorators=null;

this._onScrollListeners=null;
this._syncWithBandHandler=null;
this._selectorListener=null;

this._div=null;
this._innerDiv=null;
this._keyboardInput=null;
this._bubble=null;
};

Timeline._Band.prototype.addOnScrollListener=function(listener){
this._onScrollListeners.push(listener);
};

Timeline._Band.prototype.removeOnScrollListener=function(listener){
for(var i=0;i<this._onScrollListeners.length;i++){
if(this._onScrollListeners[i]==listener){
this._onScrollListeners.splice(i,1);
break;
}
}
};

Timeline._Band.prototype.setSyncWithBand=function(band,highlight){
if(this._syncWithBand){
this._syncWithBand.removeOnScrollListener(this._syncWithBandHandler);
}

this._syncWithBand=band;
this._syncWithBand.addOnScrollListener(this._syncWithBandHandler);
this._highlight=highlight;
this._positionHighlight();
};

Timeline._Band.prototype.getLocale=function(){
return this._locale;
};

Timeline._Band.prototype.getTimeZone=function(){
return this._timeZone;
};

Timeline._Band.prototype.getLabeller=function(){
return this._labeller;
};

Timeline._Band.prototype.getIndex=function(){
return this._index;
};

Timeline._Band.prototype.getEther=function(){
return this._ether;
};

Timeline._Band.prototype.getEtherPainter=function(){
return this._etherPainter;
};

Timeline._Band.prototype.getEventSource=function(){
return this._eventSource;
};

Timeline._Band.prototype.getEventPainter=function(){
return this._eventPainter;
};

Timeline._Band.prototype.layout=function(){
this.paint();
};

Timeline._Band.prototype.paint=function(){
this._etherPainter.paint();
this._paintDecorators();
this._paintEvents();
};

Timeline._Band.prototype.softLayout=function(){
this.softPaint();
};

Timeline._Band.prototype.softPaint=function(){
this._etherPainter.softPaint();
this._softPaintDecorators();
this._softPaintEvents();
};

Timeline._Band.prototype.setBandShiftAndWidth=function(shift,width){
var inputDiv=this._keyboardInput.parentNode;
var middle=shift+Math.floor(width/2);
if(this._timeline.isHorizontal()){
this._div.style.top=shift+"px";
this._div.style.height=width+"px";

inputDiv.style.top=middle+"px";
inputDiv.style.left="-1em";
}else{
this._div.style.left=shift+"px";
this._div.style.width=width+"px";

inputDiv.style.left=middle+"px";
inputDiv.style.top="-1em";
}
};

Timeline._Band.prototype.getViewWidth=function(){
if(this._timeline.isHorizontal()){
return this._div.offsetHeight;
}else{
return this._div.offsetWidth;
}
};

Timeline._Band.prototype.setViewLength=function(length){
this._viewLength=length;
this._recenterDiv();
this._onChanging();
};

Timeline._Band.prototype.getViewLength=function(){
return this._viewLength;
};

Timeline._Band.prototype.getTotalViewLength=function(){
return Timeline._Band.SCROLL_MULTIPLES*this._viewLength;
};

Timeline._Band.prototype.getViewOffset=function(){
return this._viewOffset;
};

Timeline._Band.prototype.getMinDate=function(){
return this._ether.pixelOffsetToDate(this._viewOffset);
};

Timeline._Band.prototype.getMaxDate=function(){
return this._ether.pixelOffsetToDate(this._viewOffset+Timeline._Band.SCROLL_MULTIPLES*this._viewLength);
};

Timeline._Band.prototype.getMinVisibleDate=function(){
return this._ether.pixelOffsetToDate(0);
};

Timeline._Band.prototype.getMaxVisibleDate=function(){
return this._ether.pixelOffsetToDate(this._viewLength);
};

Timeline._Band.prototype.getCenterVisibleDate=function(){
return this._ether.pixelOffsetToDate(this._viewLength/2);
};

Timeline._Band.prototype.setMinVisibleDate=function(date){
if(!this._changing){
this._moveEther(Math.round(-this._ether.dateToPixelOffset(date)));
}
};

Timeline._Band.prototype.setMaxVisibleDate=function(date){
if(!this._changing){
this._moveEther(Math.round(this._viewLength-this._ether.dateToPixelOffset(date)));
}
};

Timeline._Band.prototype.setCenterVisibleDate=function(date){
if(!this._changing){
this._moveEther(Math.round(this._viewLength/2-this._ether.dateToPixelOffset(date)));
}
};

Timeline._Band.prototype.dateToPixelOffset=function(date){
return this._ether.dateToPixelOffset(date)-this._viewOffset;
};

Timeline._Band.prototype.pixelOffsetToDate=function(pixels){
return this._ether.pixelOffsetToDate(pixels+this._viewOffset);
};

Timeline._Band.prototype.createLayerDiv=function(zIndex){
var div=this._timeline.getDocument().createElement("div");
div.className="timeline-band-layer";
div.style.zIndex=zIndex;
this._innerDiv.appendChild(div);

var innerDiv=this._timeline.getDocument().createElement("div");
innerDiv.className="timeline-band-layer-inner";
if(Timeline.Platform.browser.isIE){
innerDiv.style.cursor="move";
}else{
innerDiv.style.cursor="-moz-grab";
}
div.appendChild(innerDiv);

return innerDiv;
};

Timeline._Band.prototype.removeLayerDiv=function(div){
this._innerDiv.removeChild(div.parentNode);
};

Timeline._Band.prototype.closeBubble=function(){
if(this._bubble!=null){
this._bubble.close();
this._bubble=null;
}
};

Timeline._Band.prototype.openBubbleForPoint=function(pageX,pageY,width,height){
this.closeBubble();

this._bubble=Timeline.Graphics.createBubbleForPoint(
this._timeline.getDocument(),pageX,pageY,width,height);

return this._bubble.content;
};

Timeline._Band.prototype.scrollToCenter=function(date){
var pixelOffset=this._ether.dateToPixelOffset(date);
if(pixelOffset<-this._viewLength/2){
this.setCenterVisibleDate(this.pixelOffsetToDate(pixelOffset+this._viewLength));
}else if(pixelOffset>3*this._viewLength/2){
this.setCenterVisibleDate(this.pixelOffsetToDate(pixelOffset-this._viewLength));
}
this._autoScroll(Math.round(this._viewLength/2-this._ether.dateToPixelOffset(date)));
};

Timeline._Band.prototype._onMouseDown=function(innerFrame,evt,target){
this.closeBubble();

this._dragging=true;
this._dragX=evt.clientX;
this._dragY=evt.clientY;
};

Timeline._Band.prototype._onMouseMove=function(innerFrame,evt,target){
if(this._dragging){
var diffX=evt.clientX-this._dragX;
var diffY=evt.clientY-this._dragY;

this._dragX=evt.clientX;
this._dragY=evt.clientY;

this._moveEther(this._timeline.isHorizontal()?diffX:diffY);
this._positionHighlight();
}
};

Timeline._Band.prototype._onMouseUp=function(innerFrame,evt,target){
this._dragging=false;
this._keyboardInput.focus();
};

Timeline._Band.prototype._onMouseOut=function(innerFrame,evt,target){
var coords=Timeline.DOM.getEventRelativeCoordinates(evt,innerFrame);
coords.x+=this._viewOffset;
if(coords.x<0||coords.x>innerFrame.offsetWidth||
coords.y<0||coords.y>innerFrame.offsetHeight){
this._dragging=false;
}
};

Timeline._Band.prototype._onDblClick=function(innerFrame,evt,target){
var coords=Timeline.DOM.getEventRelativeCoordinates(evt,innerFrame);
var distance=coords.x-(this._viewLength/2-this._viewOffset);

this._autoScroll(-distance);
};

Timeline._Band.prototype._onKeyDown=function(keyboardInput,evt,target){
if(!this._dragging){
switch(evt.keyCode){
case 27:
break;
case 37:
case 38:
this._scrollSpeed=Math.min(50,Math.abs(this._scrollSpeed*1.05));
this._moveEther(this._scrollSpeed);
break;
case 39:
case 40:
this._scrollSpeed=-Math.min(50,Math.abs(this._scrollSpeed*1.05));
this._moveEther(this._scrollSpeed);
break;
default:
return true;
}
this.closeBubble();

Timeline.DOM.cancelEvent(evt);
return false;
}
return true;
};

Timeline._Band.prototype._onKeyUp=function(keyboardInput,evt,target){
if(!this._dragging){
this._scrollSpeed=this._originalScrollSpeed;

switch(evt.keyCode){
case 35:
this.setCenterVisibleDate(this._eventSource.getLatestDate());
break;
case 36:
this.setCenterVisibleDate(this._eventSource.getEarliestDate());
break;
case 33:
this._autoScroll(this._timeline.getPixelLength());
break;
case 34:
this._autoScroll(-this._timeline.getPixelLength());
break;
default:
return true;
}

this.closeBubble();

Timeline.DOM.cancelEvent(evt);
return false;
}
return true;
};

Timeline._Band.prototype._autoScroll=function(distance){
var b=this;
var a=Timeline.Graphics.createAnimation(function(abs,diff){
b._moveEther(diff);
},0,distance,1000);
a.run();
};

Timeline._Band.prototype._moveEther=function(shift){
this.closeBubble();

this._viewOffset+=shift;
this._ether.shiftPixels(-shift);
if(this._timeline.isHorizontal()){
this._div.style.left=this._viewOffset+"px";
}else{
this._div.style.top=this._viewOffset+"px";
}

if(this._viewOffset>-this._viewLength*0.5||
this._viewOffset<-this._viewLength*(Timeline._Band.SCROLL_MULTIPLES-1.5)){

this._recenterDiv();
}else{
this.softLayout();
}

this._onChanging();
}

Timeline._Band.prototype._onChanging=function(){
this._changing=true;

this._fireOnScroll();
this._setSyncWithBandDate();

this._changing=false;
};

Timeline._Band.prototype._fireOnScroll=function(){
for(var i=0;i<this._onScrollListeners.length;i++){
this._onScrollListeners[i](this);
}
};

Timeline._Band.prototype._setSyncWithBandDate=function(){
if(this._syncWithBand){
var centerDate=this._ether.pixelOffsetToDate(this.getViewLength()/2);
this._syncWithBand.setCenterVisibleDate(centerDate);
}
};

Timeline._Band.prototype._onHighlightBandScroll=function(){
if(this._syncWithBand){
var centerDate=this._syncWithBand.getCenterVisibleDate();
var centerPixelOffset=this._ether.dateToPixelOffset(centerDate);

this._moveEther(Math.round(this._viewLength/2-centerPixelOffset));

if(this._highlight){
this._etherPainter.setHighlight(
this._syncWithBand.getMinVisibleDate(),
this._syncWithBand.getMaxVisibleDate());
}
}
};

Timeline._Band.prototype._onAddMany=function(){
this._paintEvents();
};

Timeline._Band.prototype._onClear=function(){
this._paintEvents();
};

Timeline._Band.prototype._positionHighlight=function(){
if(this._syncWithBand){
var startDate=this._syncWithBand.getMinVisibleDate();
var endDate=this._syncWithBand.getMaxVisibleDate();

if(this._highlight){
this._etherPainter.setHighlight(startDate,endDate);
}
}
};

Timeline._Band.prototype._recenterDiv=function(){
this._viewOffset=-this._viewLength*(Timeline._Band.SCROLL_MULTIPLES-1)/2;
if(this._timeline.isHorizontal()){
this._div.style.left=this._viewOffset+"px";
this._div.style.width=(Timeline._Band.SCROLL_MULTIPLES*this._viewLength)+"px";
}else{
this._div.style.top=this._viewOffset+"px";
this._div.style.height=(Timeline._Band.SCROLL_MULTIPLES*this._viewLength)+"px";
}
this.layout();
};

Timeline._Band.prototype._paintEvents=function(){
this._eventPainter.paint();
};

Timeline._Band.prototype._softPaintEvents=function(){
this._eventPainter.softPaint();
};

Timeline._Band.prototype._paintDecorators=function(){
for(var i=0;i<this._decorators.length;i++){
this._decorators[i].paint();
}
};

Timeline._Band.prototype._softPaintDecorators=function(){
for(var i=0;i<this._decorators.length;i++){
this._decorators[i].softPaint();
}
};


/* platform.js */



Timeline.Platform.os={
isMac:false,
isWin:false,
isWin32:false,
isUnix:false
};
Timeline.Platform.browser={
isIE:false,
isNetscape:false,
isMozilla:false,
isFirefox:false,
isOpera:false,
isSafari:false,

majorVersion:0,
minorVersion:0
};

(function(){
var an=navigator.appName.toLowerCase();
var ua=navigator.userAgent.toLowerCase();



Timeline.Platform.os.isMac=(ua.indexOf('mac')!=-1);

Timeline.Platform.os.isWin=(ua.indexOf('win')!=-1);

Timeline.Platform.os.isWin32=Timeline.Platform.isWin&&(
ua.indexOf('95')!=-1||
ua.indexOf('98')!=-1||
ua.indexOf('nt')!=-1||
ua.indexOf('win32')!=-1||
ua.indexOf('32bit')!=-1
);
Timeline.Platform.os.isUnix=(ua.indexOf('x11')!=-1);



Timeline.Platform.browser.isIE=(an.indexOf("microsoft")!=-1);
Timeline.Platform.browser.isNetscape=(an.indexOf("netscape")!=-1);
Timeline.Platform.browser.isMozilla=(ua.indexOf("mozilla")!=-1);
Timeline.Platform.browser.isFirefox=(ua.indexOf("firefox")!=-1);
Timeline.Platform.browser.isOpera=(an.indexOf("opera")!=-1);


var parseVersionString=function(s){
var a=s.split(".");
Timeline.Platform.browser.majorVersion=parseInt(a[0]);
Timeline.Platform.browser.minorVersion=parseInt(a[1]);
};
var indexOf=function(s,sub,start){
var i=s.indexOf(sub,start);
return i>=0?i:s.length;
};

if(Timeline.Platform.browser.isMozilla){
var offset=ua.indexOf("mozilla/");
if(offset>=0){
parseVersionString(ua.substring(offset+8,indexOf(ua," ",offset)));
}
}

if(Timeline.Platform.browser.isIE){
var offset=ua.indexOf("msie ");
if(offset>=0){
parseVersionString(ua.substring(offset+5,indexOf(ua,";",offset)));
}
}
if(Timeline.Platform.browser.isNetscape){
var offset=ua.indexOf("rv:");
if(offset>=0){
parseVersionString(ua.substring(offset+3,indexOf(ua,")",offset)));
}
}

if(Timeline.Platform.browser.isFirefox){
var offset=ua.indexOf("firefox/");
if(offset>=0){
parseVersionString(ua.substring(offset+8,indexOf(ua," ",offset)));
}
}
})();

Timeline.Platform.getDefaultLocale=function(){
return Timeline.Platform.clientLocale;
};

/* data-structure.js */



Timeline.SortedArray=function(compare,initialArray){
this._a=(initialArray instanceof Array)?initialArray:[];
this._compare=compare;
};

Timeline.SortedArray.prototype.add=function(elmt){
var sa=this;
var index=this.find(function(elmt2){
return sa._compare(elmt2,elmt);
});

if(index<this._a.length){
this._a.splice(index,0,elmt);
}else{
this._a.push(elmt);
}
};

Timeline.SortedArray.prototype.remove=function(elmt){
var sa=this;
var index=this.find(function(elmt2){
return sa._compare(elmt2,elmt);
});

while(index<this._a.length&&this._compare(this._a[index],elmt)==0){
if(this._a[index]==elmt){
this._a.splice(index,1);
return true;
}else{
index++;
}
}
return false;
};

Timeline.SortedArray.prototype.removeAll=function(){
this._a=[];
};

Timeline.SortedArray.prototype.elementAt=function(index){
return this._a[index];
};

Timeline.SortedArray.prototype.length=function(){
return this._a.length;
};

Timeline.SortedArray.prototype.find=function(compare){
var a=0;
var b=this._a.length;

while(a<b){
var mid=Math.floor((a+b)/2);
var c=compare(this._a[mid]);
if(mid==a){
return c<0?a+1:a;
}else if(c<0){
a=mid;
}else{
b=mid;
}
}
return a;
};

Timeline.SortedArray.prototype.getFirst=function(){
return(this._a.length>0)?this._a[0]:null;
};

Timeline.SortedArray.prototype.getLast=function(){
return(this._a.length>0)?this._a[this._a.length-1]:null;
};



Timeline.EventIndex=function(unit){
var eventIndex=this;

this._unit=(unit!=null)?unit:Timeline.NativeDateUnit;
this._events=new Timeline.SortedArray(
function(event1,event2){
return eventIndex._unit.compare(event1.getStart(),event2.getStart());
}
);
this._indexed=true;
};

Timeline.EventIndex.prototype.getUnit=function(){
return this._unit;
};

Timeline.EventIndex.prototype.add=function(evt){
this._events.add(evt);
this._indexed=false;
};

Timeline.EventIndex.prototype.removeAll=function(){
this._events.removeAll();
this._indexed=false;
};

Timeline.EventIndex.prototype.getCount=function(){
return this._events.length();
};

Timeline.EventIndex.prototype.getIterator=function(startDate,endDate){
if(!this._indexed){
this._index();
}
return new Timeline.EventIndex._Iterator(this._events,startDate,endDate,this._unit);
};

Timeline.EventIndex.prototype.getAllIterator=function(){
return new Timeline.EventIndex._AllIterator(this._events);
};

Timeline.EventIndex.prototype.getEarliestDate=function(){
var evt=this._events.getFirst();
return(evt==null)?null:evt.getStart();
};

Timeline.EventIndex.prototype.getLatestDate=function(){
var evt=this._events.getLast();
if(evt==null){
return null;
}

if(!this._indexed){
this._index();
}

var index=evt._earliestOverlapIndex;
var date=this._events.elementAt(index).getEnd();
for(var i=index+1;i<this._events.length();i++){
date=this._unit.later(date,this._events.elementAt(i).getEnd());
}

return date;
};

Timeline.EventIndex.prototype._index=function(){


var l=this._events.length();
for(var i=0;i<l;i++){
var evt=this._events.elementAt(i);
evt._earliestOverlapIndex=i;
}

var toIndex=1;
for(var i=0;i<l;i++){
var evt=this._events.elementAt(i);
var end=evt.getEnd();

toIndex=Math.max(toIndex,i+1);
while(toIndex<l){
var evt2=this._events.elementAt(toIndex);
var start2=evt2.getStart();

if(this._unit.compare(start2,end)<0){
evt2._earliestOverlapIndex=i;
toIndex++;
}else{
break;
}
}
}
this._indexed=true;
};

Timeline.EventIndex._Iterator=function(events,startDate,endDate,unit){
this._events=events;
this._startDate=startDate;
this._endDate=endDate;
this._unit=unit;

this._currentIndex=events.find(function(evt){
return unit.compare(evt.getStart(),startDate);
});
if(this._currentIndex-1>=0){
this._currentIndex=this._events.elementAt(this._currentIndex-1)._earliestOverlapIndex;
}
this._currentIndex--;

this._maxIndex=events.find(function(evt){
return unit.compare(evt.getStart(),endDate);
});

this._hasNext=false;
this._next=null;
this._findNext();
};

Timeline.EventIndex._Iterator.prototype={
hasNext:function(){return this._hasNext;},
next:function(){
if(this._hasNext){
var next=this._next;
this._findNext();

return next;
}else{
return null;
}
},
_findNext:function(){
var unit=this._unit;
while((++this._currentIndex)<this._maxIndex){
var evt=this._events.elementAt(this._currentIndex);
if(unit.compare(evt.getStart(),this._endDate)<0&&
unit.compare(evt.getEnd(),this._startDate)>0){

this._next=evt;
this._hasNext=true;
return;
}
}
this._next=null;
this._hasNext=false;
}
};

Timeline.EventIndex._AllIterator=function(events){
this._events=events;
this._index=0;
};

Timeline.EventIndex._AllIterator.prototype={
hasNext:function(){
return this._index<this._events.length();
},
next:function(){
return this._index<this._events.length()?
this._events.elementAt(this._index++):null;
}
};

/* date-time.js */



Timeline.DateTime=new Object();

Timeline.DateTime.MILLISECOND=0;
Timeline.DateTime.SECOND=1;
Timeline.DateTime.MINUTE=2;
Timeline.DateTime.HOUR=3;
Timeline.DateTime.DAY=4;
Timeline.DateTime.WEEK=5;
Timeline.DateTime.MONTH=6;
Timeline.DateTime.YEAR=7;
Timeline.DateTime.DECADE=8;
Timeline.DateTime.CENTURY=9;
Timeline.DateTime.MILLENNIUM=10;

Timeline.DateTime.EPOCH=-1;
Timeline.DateTime.ERA=-2;

Timeline.DateTime.gregorianUnitLengths=[];
(function(){
var d=Timeline.DateTime;
var a=d.gregorianUnitLengths;

a[d.MILLISECOND]=1;
a[d.SECOND]=1000;
a[d.MINUTE]=a[d.SECOND]*60;
a[d.HOUR]=a[d.MINUTE]*60;
a[d.DAY]=a[d.HOUR]*24;
a[d.WEEK]=a[d.DAY]*7;
a[d.MONTH]=a[d.DAY]*31;
a[d.YEAR]=a[d.DAY]*365;
a[d.DECADE]=a[d.YEAR]*10;
a[d.CENTURY]=a[d.YEAR]*100;
a[d.MILLENNIUM]=a[d.YEAR]*1000;
})();

Timeline.DateTime.parseGregorianDateTime=function(o){
if(o==null){
return null;
}else if(o instanceof Date){
return o;
}

var s=o.toString();
if(s.length>0&&s.length<8){
var space=s.indexOf(" ");
if(space>0){
var year=parseInt(s.substr(0,space));
var suffix=s.substr(space+1);
if(suffix.toLowerCase()=="bc"){
year=1-year;
}
}else{
var year=parseInt(s);
}

var d=new Date(0);
d.setUTCFullYear(year);

return d;
}

try{
return new Date(Date.parse(s));
}catch(e){
return null;
}
};

Timeline.DateTime._iso8601DateRegExp="^(-?)([0-9]{4})("+[
"(-?([0-9]{2})(-?([0-9]{2}))?)",
"(-?([0-9]{3}))",
"(-?W([0-9]{2})(-?([1-7]))?)"
].join("|")+")?$";

Timeline.DateTime.setIso8601Date=function(dateObject,string){


var regexp=Timeline.DateTime._iso8601DateRegExp;
var d=string.match(new RegExp(regexp));
if(!d){
throw new Error("Invalid date string: "+string);
}

var sign=(d[1]=="-")?-1:1;
var year=sign*d[2];
var month=d[5];
var date=d[7];
var dayofyear=d[9];
var week=d[11];
var dayofweek=(d[13])?d[13]:1;

dateObject.setUTCFullYear(year);
if(dayofyear){
dateObject.setUTCMonth(0);
dateObject.setUTCDate(Number(dayofyear));
}else if(week){
dateObject.setUTCMonth(0);
dateObject.setUTCDate(1);
var gd=dateObject.getUTCDay();
var day=(gd)?gd:7;
var offset=Number(dayofweek)+(7*Number(week));

if(day<=4){
dateObject.setUTCDate(offset+1-day);
}else{
dateObject.setUTCDate(offset+8-day);
}
}else{
if(month){
dateObject.setUTCDate(1);
dateObject.setUTCMonth(month-1);
}
if(date){
dateObject.setUTCDate(date);
}
}

return dateObject;
};

Timeline.DateTime.setIso8601Time=function(dateObject,string){



var timezone="Z|(([-+])([0-9]{2})(:?([0-9]{2}))?)$";
var d=string.match(new RegExp(timezone));

var offset=0;
if(d){
if(d[0]!='Z'){
offset=(Number(d[3])*60)+Number(d[5]);
offset*=((d[2]=='-')?1:-1);
}
string=string.substr(0,string.length-d[0].length);
}


var regexp="^([0-9]{2})(:?([0-9]{2})(:?([0-9]{2})(\.([0-9]+))?)?)?$";
var d=string.match(new RegExp(regexp));
if(!d){
dojo.debug("invalid time string: "+string);
return false;
}
var hours=d[1];
var mins=Number((d[3])?d[3]:0);
var secs=(d[5])?d[5]:0;
var ms=d[7]?(Number("0."+d[7])*1000):0;

dateObject.setUTCHours(hours);
dateObject.setUTCMinutes(mins);
dateObject.setUTCSeconds(secs);
dateObject.setUTCMilliseconds(ms);

return dateObject;
};

Timeline.DateTime.setIso8601=function(dateObject,string){


var comps=(string.indexOf("T")==-1)?string.split(" "):string.split("T");

Timeline.DateTime.setIso8601Date(dateObject,comps[0]);
if(comps.length==2){
Timeline.DateTime.setIso8601Time(dateObject,comps[1]);
}
return dateObject;
};

Timeline.DateTime.parseIso8601DateTime=function(string){
try{
return Timeline.DateTime.setIso8601(new Date(0),string);
}catch(e){
return null;
}
};

Timeline.DateTime.roundDownToInterval=function(date,intervalUnit,timeZone,multiple,firstDayOfWeek){
var timeShift=timeZone*
Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.HOUR];

var date2=new Date(date.getTime()+timeShift);
var clearInDay=function(d){
d.setUTCMilliseconds(0);
d.setUTCSeconds(0);
d.setUTCMinutes(0);
d.setUTCHours(0);
};
var clearInYear=function(d){
clearInDay(d);
d.setUTCDate(1);
d.setUTCMonth(0);
};

switch(intervalUnit){
case Timeline.DateTime.MILLISECOND:
var x=date2.getUTCMilliseconds();
date2.setUTCMilliseconds(x-(x%multiple));
break;
case Timeline.DateTime.SECOND:
date2.setUTCMilliseconds(0);

var x=date2.getUTCSeconds();
date2.setUTCSeconds(x-(x%multiple));
break;
case Timeline.DateTime.MINUTE:
date2.setUTCMilliseconds(0);
date2.setUTCSeconds(0);

var x=date2.getUTCMinutes();
date2.setTime(date2.getTime()-
(x%multiple)*Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.MINUTE]);
break;
case Timeline.DateTime.HOUR:
date2.setUTCMilliseconds(0);
date2.setUTCSeconds(0);
date2.setUTCMinutes(0);

var x=date2.getUTCHours();
date2.setUTCHours(x-(x%multiple));
break;
case Timeline.DateTime.DAY:
clearInDay(date2);
break;
case Timeline.DateTime.WEEK:
clearInDay(date2);
var d=(date2.getUTCDay()+7-firstDayOfWeek)%7;
date2.setTime(date2.getTime()-
d*Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.DAY]);
break;
case Timeline.DateTime.MONTH:
clearInDay(date2);
date2.setUTCDate(1);

var x=date2.getUTCMonth();
date2.setUTCMonth(x-(x%multiple));
break;
case Timeline.DateTime.YEAR:
clearInYear(date2);

var x=date2.getUTCFullYear();
date2.setUTCFullYear(x-(x%multiple));
break;
case Timeline.DateTime.DECADE:
clearInYear(date2);
date2.setUTCFullYear(Math.floor(date2.getUTCFullYear()/10)*10);
break;
case Timeline.DateTime.CENTURY:
clearInYear(date2);
date2.setUTCFullYear(Math.floor(date2.getUTCFullYear()/100)*100);
break;
case Timeline.DateTime.MILLENNIUM:
clearInYear(date2);
date2.setUTCFullYear(Math.floor(date2.getUTCFullYear()/1000)*1000);
break;
}

date.setTime(date2.getTime()-timeShift);
};

Timeline.DateTime.roundUpToInterval=function(date,intervalUnit,timeZone,multiple,firstDayOfWeek){
var originalTime=date.getTime();
Timeline.DateTime.roundDownToInterval(date,intervalUnit,timeZone,multiple,firstDayOfWeek);
if(date.getTime()<originalTime){
date.setTime(date.getTime()+
Timeline.DateTime.gregorianUnitLengths[intervalUnit]*multiple);
}
};

Timeline.DateTime.incrementByInterval=function(date,intervalUnit){
switch(intervalUnit){
case Timeline.DateTime.MILLISECOND:
date.setTime(date.getTime()+1)
break;
case Timeline.DateTime.SECOND:
date.setTime(date.getTime()+1000);
break;
case Timeline.DateTime.MINUTE:
date.setTime(date.getTime()+
Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.MINUTE]);
break;
case Timeline.DateTime.HOUR:
date.setTime(date.getTime()+
Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.HOUR]);
break;
case Timeline.DateTime.DAY:
date.setUTCDate(date.getUTCDate()+1);
break;
case Timeline.DateTime.WEEK:
date.setUTCDate(date.getUTCDate()+7);
break;
case Timeline.DateTime.MONTH:
date.setUTCMonth(date.getUTCMonth()+1);
break;
case Timeline.DateTime.YEAR:
date.setUTCFullYear(date.getUTCFullYear()+1);
break;
case Timeline.DateTime.DECADE:
date.setUTCFullYear(date.getUTCFullYear()+10);
break;
case Timeline.DateTime.CENTURY:
date.setUTCFullYear(date.getUTCFullYear()+100);
break;
case Timeline.DateTime.MILLENNIUM:
date.setUTCFullYear(date.getUTCFullYear()+1000);
break;
}
};

Timeline.DateTime.removeTimeZoneOffset=function(date,timeZone){
return new Date(date.getTime()+
timeZone*Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.HOUR]);
};



/* debug.js */



Timeline.Debug=new Object();

Timeline.Debug.log=function(msg){
};

Timeline.Debug.exception=function(e){
alert("Caught exception: "+(Timeline.Platform.isIE?e.message:e));
};



/* dom.js */



Timeline.DOM=new Object();

Timeline.DOM.registerEventWithObject=function(elmt,eventName,obj,handler){
Timeline.DOM.registerEvent(elmt,eventName,function(elmt2,evt,target){
return handler.call(obj,elmt2,evt,target);
});
};

Timeline.DOM.registerEvent=function(elmt,eventName,handler){
var handler2=function(evt){
evt=(evt)?evt:((event)?event:null);
if(evt){
var target=(evt.target)?
evt.target:((evt.srcElement)?evt.srcElement:null);

if(target){

target=(target.nodeType==1||target.nodeType==9)?
target:target.parentNode;

}

return handler(elmt,evt,target);
}
return true;
}

if(Timeline.Platform.browser.isIE){
elmt.attachEvent("on"+eventName,handler2);
}else{
elmt.addEventListener(eventName,handler2,false);
}
};

Timeline.DOM.getPageCoordinates=function(elmt){
var left=0;
var top=0;

if(elmt.nodeType!=1){
elmt=elmt.parentNode;
}

while(elmt!=null){
left+=elmt.offsetLeft;
top+=elmt.offsetTop;

elmt=elmt.offsetParent;
}
return{left:left,top:top};
};

Timeline.DOM.getEventRelativeCoordinates=function(evt,elmt){
if(Timeline.Platform.browser.isIE){
return{
x:evt.offsetX,
y:evt.offsetY
};
}else{
var coords=Timeline.DOM.getPageCoordinates(elmt);
return{
x:evt.pageX-coords.left,
y:evt.pageY-coords.top
};
}
};

Timeline.DOM.cancelEvent=function(evt){
evt.returnValue=false;
evt.cancelBubble=true;
if("preventDefault"in evt){
evt.preventDefault();
}
};



/* graphics.js */



Timeline.Graphics=new Object();
Timeline.Graphics.pngIsTranslucent=(!Timeline.Platform.browser.isIE)||(Timeline.Platform.browser.majorVersion>6);


Timeline.Graphics.createTranslucentImage=function(doc,url,verticalAlign){
var elmt;
if(Timeline.Graphics.pngIsTranslucent){
elmt=doc.createElement("img");
elmt.setAttribute("src",url);
}else{
elmt=doc.createElement("img");
elmt.style.display="inline";
elmt.style.width="1px";
elmt.style.height="1px";
elmt.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"', sizingMethod='image')";
}
elmt.style.verticalAlign=(verticalAlign!=null)?verticalAlign:"middle";
return elmt;
};

Timeline.Graphics.setOpacity=function(elmt,opacity){
if(Timeline.Platform.browser.isIE){
elmt.style.filter="progid:DXImageTransform.Microsoft.Alpha(Style=0,Opacity="+opacity+")";
}else{
var o=(opacity/100).toString();
elmt.style.opacity=o;
elmt.style.MozOpacity=o;
}
};

Timeline.Graphics._bubbleMargins={
top:33,
bottom:42,
left:33,
right:40
}


Timeline.Graphics._arrowOffsets={
top:0,
bottom:9,
left:1,
right:8
}

Timeline.Graphics._bubblePadding=15;
Timeline.Graphics._bubblePointOffset=6;
Timeline.Graphics._halfArrowWidth=18;

Timeline.Graphics.createBubbleForPoint=function(doc,pageX,pageY,contentWidth,contentHeight){
function getWindowDims(){
if(typeof window.innerWidth=='number'){
return{w:window.innerWidth,h:window.innerHeight};
}else if(document.documentElement&&document.documentElement.clientWidth){
return{
w:document.documentElement.clientWidth,
h:document.documentElement.clientHeight
};
}else if(document.body&&document.body.clientWidth){
return{
w:document.body.clientWidth,
h:document.body.clientHeight
};
}
}

var bubble={
_closed:false,
_doc:doc,
close:function(){
if(!this._closed){
this._doc.body.removeChild(this._div);
this._doc=null;
this._div=null;
this._content=null;
this._closed=true;
}
}
};

var dims=getWindowDims();
var docWidth=dims.w;
var docHeight=dims.h;

var margins=Timeline.Graphics._bubbleMargins;
contentWidth=parseInt(contentWidth,10);
contentHeight=parseInt(contentHeight,10);
var bubbleWidth=margins.left+contentWidth+margins.right;
var bubbleHeight=margins.top+contentHeight+margins.bottom;

var pngIsTranslucent=Timeline.Graphics.pngIsTranslucent;
var urlPrefix=Timeline.urlPrefix;

var setImg=function(elmt,url,width,height){
elmt.style.position="absolute";
elmt.style.width=width+"px";
elmt.style.height=height+"px";
if(pngIsTranslucent){
elmt.style.background="url("+url+")";
}else{
elmt.style.filter="progid:DXImageTransform.Microsoft.AlphaImageLoader(src='"+url+"', sizingMethod='crop')";
}
}
var div=doc.createElement("div");
div.style.width=bubbleWidth+"px";
div.style.height=bubbleHeight+"px";
div.style.position="absolute";
div.style.zIndex=1000;
bubble._div=div;

var divInner=doc.createElement("div");
divInner.style.width="100%";
divInner.style.height="100%";
divInner.style.position="relative";
div.appendChild(divInner);

var createImg=function(url,left,top,width,height){
var divImg=doc.createElement("div");
divImg.style.left=left+"px";
divImg.style.top=top+"px";
setImg(divImg,url,width,height);
divInner.appendChild(divImg);
}

createImg(urlPrefix+"images/bubble-top-left.png",0,0,margins.left,margins.top);
createImg(urlPrefix+"images/bubble-top.png",margins.left,0,contentWidth,margins.top);
createImg(urlPrefix+"images/bubble-top-right.png",margins.left+contentWidth,0,margins.right,margins.top);

createImg(urlPrefix+"images/bubble-left.png",0,margins.top,margins.left,contentHeight);
createImg(urlPrefix+"images/bubble-right.png",margins.left+contentWidth,margins.top,margins.right,contentHeight);

createImg(urlPrefix+"images/bubble-bottom-left.png",0,margins.top+contentHeight,margins.left,margins.bottom);
createImg(urlPrefix+"images/bubble-bottom.png",margins.left,margins.top+contentHeight,contentWidth,margins.bottom);
createImg(urlPrefix+"images/bubble-bottom-right.png",margins.left+contentWidth,margins.top+contentHeight,margins.right,margins.bottom);

var divClose=doc.createElement("div");
divClose.style.left=(bubbleWidth-margins.right+Timeline.Graphics._bubblePadding-16-2)+"px";
divClose.style.top=(margins.top-Timeline.Graphics._bubblePadding+1)+"px";
divClose.style.cursor="pointer";
setImg(divClose,urlPrefix+"images/close-button.png",16,16);
Timeline.DOM.registerEventWithObject(divClose,"click",bubble,bubble.close);
divInner.appendChild(divClose);

var divContent=doc.createElement("div");
divContent.style.position="absolute";
divContent.style.left=margins.left+"px";
divContent.style.top=margins.top+"px";
divContent.style.width=contentWidth+"px";
divContent.style.height=contentHeight+"px";
divContent.style.overflow="auto";
divContent.style.background="white";
divInner.appendChild(divContent);
bubble.content=divContent;

(function(){
if(pageX-Timeline.Graphics._halfArrowWidth-Timeline.Graphics._bubblePadding>0&&
pageX+Timeline.Graphics._halfArrowWidth+Timeline.Graphics._bubblePadding<docWidth){

var left=pageX-Math.round(contentWidth/2)-margins.left;
left=pageX<(docWidth/2)?
Math.max(left,-(margins.left-Timeline.Graphics._bubblePadding)):
Math.min(left,docWidth+(margins.right-Timeline.Graphics._bubblePadding)-bubbleWidth);

if(pageY-Timeline.Graphics._bubblePointOffset-bubbleHeight>0){
var divImg=doc.createElement("div");

divImg.style.left=(pageX-Timeline.Graphics._halfArrowWidth-left)+"px";
divImg.style.top=(margins.top+contentHeight)+"px";
setImg(divImg,urlPrefix+"images/bubble-bottom-arrow.png",37,margins.bottom);
divInner.appendChild(divImg);

div.style.left=left+"px";
div.style.top=(pageY-Timeline.Graphics._bubblePointOffset-bubbleHeight+
Timeline.Graphics._arrowOffsets.bottom)+"px";

return;
}else if(pageY+Timeline.Graphics._bubblePointOffset+bubbleHeight<docHeight){
var divImg=doc.createElement("div");

divImg.style.left=(pageX-Timeline.Graphics._halfArrowWidth-left)+"px";
divImg.style.top="0px";
setImg(divImg,urlPrefix+"images/bubble-top-arrow.png",37,margins.top);
divInner.appendChild(divImg);

div.style.left=left+"px";
div.style.top=(pageY+Timeline.Graphics._bubblePointOffset-
Timeline.Graphics._arrowOffsets.top)+"px";

return;
}
}

var top=pageY-Math.round(contentHeight/2)-margins.top;
top=pageY<(docHeight/2)?
Math.max(top,-(margins.top-Timeline.Graphics._bubblePadding)):
Math.min(top,docHeight+(margins.bottom-Timeline.Graphics._bubblePadding)-bubbleHeight);

if(pageX-Timeline.Graphics._bubblePointOffset-bubbleWidth>0){
var divImg=doc.createElement("div");

divImg.style.left=(margins.left+contentWidth)+"px";
divImg.style.top=(pageY-Timeline.Graphics._halfArrowWidth-top)+"px";
setImg(divImg,urlPrefix+"images/bubble-right-arrow.png",margins.right,37);
divInner.appendChild(divImg);

div.style.left=(pageX-Timeline.Graphics._bubblePointOffset-bubbleWidth+
Timeline.Graphics._arrowOffsets.right)+"px";
div.style.top=top+"px";
}else{
var divImg=doc.createElement("div");

divImg.style.left="0px";
divImg.style.top=(pageY-Timeline.Graphics._halfArrowWidth-top)+"px";
setImg(divImg,urlPrefix+"images/bubble-left-arrow.png",margins.left,37);
divInner.appendChild(divImg);

div.style.left=(pageX+Timeline.Graphics._bubblePointOffset-
Timeline.Graphics._arrowOffsets.left)+"px";
div.style.top=top+"px";
}
})();

doc.body.appendChild(div);

return bubble;
};

Timeline.Graphics.createMessageBubble=function(doc){
var containerDiv=doc.createElement("div");
if(Timeline.Graphics.pngIsTranslucent){
var topDiv=doc.createElement("div");
topDiv.style.height="33px";
topDiv.style.background="url("+Timeline.urlPrefix+"images/message-top-left.png) top left no-repeat";
topDiv.style.paddingLeft="44px";
containerDiv.appendChild(topDiv);

var topRightDiv=doc.createElement("div");
topRightDiv.style.height="33px";
topRightDiv.style.background="url("+Timeline.urlPrefix+"images/message-top-right.png) top right no-repeat";
topDiv.appendChild(topRightDiv);

var middleDiv=doc.createElement("div");
middleDiv.style.background="url("+Timeline.urlPrefix+"images/message-left.png) top left repeat-y";
middleDiv.style.paddingLeft="44px";
containerDiv.appendChild(middleDiv);

var middleRightDiv=doc.createElement("div");
middleRightDiv.style.background="url("+Timeline.urlPrefix+"images/message-right.png) top right repeat-y";
middleRightDiv.style.paddingRight="44px";
middleDiv.appendChild(middleRightDiv);

var contentDiv=doc.createElement("div");
middleRightDiv.appendChild(contentDiv);

var bottomDiv=doc.createElement("div");
bottomDiv.style.height="55px";
bottomDiv.style.background="url("+Timeline.urlPrefix+"images/message-bottom-left.png) bottom left no-repeat";
bottomDiv.style.paddingLeft="44px";
containerDiv.appendChild(bottomDiv);

var bottomRightDiv=doc.createElement("div");
bottomRightDiv.style.height="55px";
bottomRightDiv.style.background="url("+Timeline.urlPrefix+"images/message-bottom-right.png) bottom right no-repeat";
bottomDiv.appendChild(bottomRightDiv);
}else{
containerDiv.style.border="2px solid #7777AA";
containerDiv.style.padding="20px";
containerDiv.style.background="white";
Timeline.Graphics.setOpacity(containerDiv,90);

var contentDiv=doc.createElement("div");
containerDiv.appendChild(contentDiv);
}

return{
containerDiv:containerDiv,
contentDiv:contentDiv
};
};

Timeline.Graphics.createAnimation=function(f,from,to,duration){
return new Timeline.Graphics._Animation(f,from,to,duration);
};

Timeline.Graphics._Animation=function(f,from,to,duration){
this.f=f;

this.from=from;
this.to=to;
this.current=from;

this.duration=duration;
this.start=new Date().getTime();
this.timePassed=0;
};

Timeline.Graphics._Animation.prototype.run=function(){
var a=this;
window.setTimeout(function(){a.step();},100);
};

Timeline.Graphics._Animation.prototype.step=function(){
this.timePassed+=100;

var timePassedFraction=this.timePassed/this.duration;
var parameterFraction=-Math.cos(timePassedFraction*Math.PI)/2+0.5;
var current=parameterFraction*(this.to-this.from)+this.from;

try{
this.f(current,current-this.current);
}catch(e){
}
this.current=current;

if(this.timePassed<this.duration){
this.run();
}
};


/* html.js */



Timeline.HTML=new Object();

Timeline.HTML._e2uHash={};
(function(){
e2uHash=Timeline.HTML._e2uHash;
e2uHash['nbsp']='\u00A0[space]';
e2uHash['iexcl']='\u00A1';
e2uHash['cent']='\u00A2';
e2uHash['pound']='\u00A3';
e2uHash['curren']='\u00A4';
e2uHash['yen']='\u00A5';
e2uHash['brvbar']='\u00A6';
e2uHash['sect']='\u00A7';
e2uHash['uml']='\u00A8';
e2uHash['copy']='\u00A9';
e2uHash['ordf']='\u00AA';
e2uHash['laquo']='\u00AB';
e2uHash['not']='\u00AC';
e2uHash['shy']='\u00AD';
e2uHash['reg']='\u00AE';
e2uHash['macr']='\u00AF';
e2uHash['deg']='\u00B0';
e2uHash['plusmn']='\u00B1';
e2uHash['sup2']='\u00B2';
e2uHash['sup3']='\u00B3';
e2uHash['acute']='\u00B4';
e2uHash['micro']='\u00B5';
e2uHash['para']='\u00B6';
e2uHash['middot']='\u00B7';
e2uHash['cedil']='\u00B8';
e2uHash['sup1']='\u00B9';
e2uHash['ordm']='\u00BA';
e2uHash['raquo']='\u00BB';
e2uHash['frac14']='\u00BC';
e2uHash['frac12']='\u00BD';
e2uHash['frac34']='\u00BE';
e2uHash['iquest']='\u00BF';
e2uHash['Agrave']='\u00C0';
e2uHash['Aacute']='\u00C1';
e2uHash['Acirc']='\u00C2';
e2uHash['Atilde']='\u00C3';
e2uHash['Auml']='\u00C4';
e2uHash['Aring']='\u00C5';
e2uHash['AElig']='\u00C6';
e2uHash['Ccedil']='\u00C7';
e2uHash['Egrave']='\u00C8';
e2uHash['Eacute']='\u00C9';
e2uHash['Ecirc']='\u00CA';
e2uHash['Euml']='\u00CB';
e2uHash['Igrave']='\u00CC';
e2uHash['Iacute']='\u00CD';
e2uHash['Icirc']='\u00CE';
e2uHash['Iuml']='\u00CF';
e2uHash['ETH']='\u00D0';
e2uHash['Ntilde']='\u00D1';
e2uHash['Ograve']='\u00D2';
e2uHash['Oacute']='\u00D3';
e2uHash['Ocirc']='\u00D4';
e2uHash['Otilde']='\u00D5';
e2uHash['Ouml']='\u00D6';
e2uHash['times']='\u00D7';
e2uHash['Oslash']='\u00D8';
e2uHash['Ugrave']='\u00D9';
e2uHash['Uacute']='\u00DA';
e2uHash['Ucirc']='\u00DB';
e2uHash['Uuml']='\u00DC';
e2uHash['Yacute']='\u00DD';
e2uHash['THORN']='\u00DE';
e2uHash['szlig']='\u00DF';
e2uHash['agrave']='\u00E0';
e2uHash['aacute']='\u00E1';
e2uHash['acirc']='\u00E2';
e2uHash['atilde']='\u00E3';
e2uHash['auml']='\u00E4';
e2uHash['aring']='\u00E5';
e2uHash['aelig']='\u00E6';
e2uHash['ccedil']='\u00E7';
e2uHash['egrave']='\u00E8';
e2uHash['eacute']='\u00E9';
e2uHash['ecirc']='\u00EA';
e2uHash['euml']='\u00EB';
e2uHash['igrave']='\u00EC';
e2uHash['iacute']='\u00ED';
e2uHash['icirc']='\u00EE';
e2uHash['iuml']='\u00EF';
e2uHash['eth']='\u00F0';
e2uHash['ntilde']='\u00F1';
e2uHash['ograve']='\u00F2';
e2uHash['oacute']='\u00F3';
e2uHash['ocirc']='\u00F4';
e2uHash['otilde']='\u00F5';
e2uHash['ouml']='\u00F6';
e2uHash['divide']='\u00F7';
e2uHash['oslash']='\u00F8';
e2uHash['ugrave']='\u00F9';
e2uHash['uacute']='\u00FA';
e2uHash['ucirc']='\u00FB';
e2uHash['uuml']='\u00FC';
e2uHash['yacute']='\u00FD';
e2uHash['thorn']='\u00FE';
e2uHash['yuml']='\u00FF';
e2uHash['quot']='\u0022';
e2uHash['amp']='\u0026';
e2uHash['lt']='\u003C';
e2uHash['gt']='\u003E';
e2uHash['OElig']='';
e2uHash['oelig']='\u0153';
e2uHash['Scaron']='\u0160';
e2uHash['scaron']='\u0161';
e2uHash['Yuml']='\u0178';
e2uHash['circ']='\u02C6';
e2uHash['tilde']='\u02DC';
e2uHash['ensp']='\u2002';
e2uHash['emsp']='\u2003';
e2uHash['thinsp']='\u2009';
e2uHash['zwnj']='\u200C';
e2uHash['zwj']='\u200D';
e2uHash['lrm']='\u200E';
e2uHash['rlm']='\u200F';
e2uHash['ndash']='\u2013';
e2uHash['mdash']='\u2014';
e2uHash['lsquo']='\u2018';
e2uHash['rsquo']='\u2019';
e2uHash['sbquo']='\u201A';
e2uHash['ldquo']='\u201C';
e2uHash['rdquo']='\u201D';
e2uHash['bdquo']='\u201E';
e2uHash['dagger']='\u2020';
e2uHash['Dagger']='\u2021';
e2uHash['permil']='\u2030';
e2uHash['lsaquo']='\u2039';
e2uHash['rsaquo']='\u203A';
e2uHash['euro']='\u20AC';
e2uHash['fnof']='\u0192';
e2uHash['Alpha']='\u0391';
e2uHash['Beta']='\u0392';
e2uHash['Gamma']='\u0393';
e2uHash['Delta']='\u0394';
e2uHash['Epsilon']='\u0395';
e2uHash['Zeta']='\u0396';
e2uHash['Eta']='\u0397';
e2uHash['Theta']='\u0398';
e2uHash['Iota']='\u0399';
e2uHash['Kappa']='\u039A';
e2uHash['Lambda']='\u039B';
e2uHash['Mu']='\u039C';
e2uHash['Nu']='\u039D';
e2uHash['Xi']='\u039E';
e2uHash['Omicron']='\u039F';
e2uHash['Pi']='\u03A0';
e2uHash['Rho']='\u03A1';
e2uHash['Sigma']='\u03A3';
e2uHash['Tau']='\u03A4';
e2uHash['Upsilon']='\u03A5';
e2uHash['Phi']='\u03A6';
e2uHash['Chi']='\u03A7';
e2uHash['Psi']='\u03A8';
e2uHash['Omega']='\u03A9';
e2uHash['alpha']='\u03B1';
e2uHash['beta']='\u03B2';
e2uHash['gamma']='\u03B3';
e2uHash['delta']='\u03B4';
e2uHash['epsilon']='\u03B5';
e2uHash['zeta']='\u03B6';
e2uHash['eta']='\u03B7';
e2uHash['theta']='\u03B8';
e2uHash['iota']='\u03B9';
e2uHash['kappa']='\u03BA';
e2uHash['lambda']='\u03BB';
e2uHash['mu']='\u03BC';
e2uHash['nu']='\u03BD';
e2uHash['xi']='\u03BE';
e2uHash['omicron']='\u03BF';
e2uHash['pi']='\u03C0';
e2uHash['rho']='\u03C1';
e2uHash['sigmaf']='\u03C2';
e2uHash['sigma']='\u03C3';
e2uHash['tau']='\u03C4';
e2uHash['upsilon']='\u03C5';
e2uHash['phi']='\u03C6';
e2uHash['chi']='\u03C7';
e2uHash['psi']='\u03C8';
e2uHash['omega']='\u03C9';
e2uHash['thetasym']='\u03D1';
e2uHash['upsih']='\u03D2';
e2uHash['piv']='\u03D6';
e2uHash['bull']='\u2022';
e2uHash['hellip']='\u2026';
e2uHash['prime']='\u2032';
e2uHash['Prime']='\u2033';
e2uHash['oline']='\u203E';
e2uHash['frasl']='\u2044';
e2uHash['weierp']='\u2118';
e2uHash['image']='\u2111';
e2uHash['real']='\u211C';
e2uHash['trade']='\u2122';
e2uHash['alefsym']='\u2135';
e2uHash['larr']='\u2190';
e2uHash['uarr']='\u2191';
e2uHash['rarr']='\u2192';
e2uHash['darr']='\u2193';
e2uHash['harr']='\u2194';
e2uHash['crarr']='\u21B5';
e2uHash['lArr']='\u21D0';
e2uHash['uArr']='\u21D1';
e2uHash['rArr']='\u21D2';
e2uHash['dArr']='\u21D3';
e2uHash['hArr']='\u21D4';
e2uHash['forall']='\u2200';
e2uHash['part']='\u2202';
e2uHash['exist']='\u2203';
e2uHash['empty']='\u2205';
e2uHash['nabla']='\u2207';
e2uHash['isin']='\u2208';
e2uHash['notin']='\u2209';
e2uHash['ni']='\u220B';
e2uHash['prod']='\u220F';
e2uHash['sum']='\u2211';
e2uHash['minus']='\u2212';
e2uHash['lowast']='\u2217';
e2uHash['radic']='\u221A';
e2uHash['prop']='\u221D';
e2uHash['infin']='\u221E';
e2uHash['ang']='\u2220';
e2uHash['and']='\u2227';
e2uHash['or']='\u2228';
e2uHash['cap']='\u2229';
e2uHash['cup']='\u222A';
e2uHash['int']='\u222B';
e2uHash['there4']='\u2234';
e2uHash['sim']='\u223C';
e2uHash['cong']='\u2245';
e2uHash['asymp']='\u2248';
e2uHash['ne']='\u2260';
e2uHash['equiv']='\u2261';
e2uHash['le']='\u2264';
e2uHash['ge']='\u2265';
e2uHash['sub']='\u2282';
e2uHash['sup']='\u2283';
e2uHash['nsub']='\u2284';
e2uHash['sube']='\u2286';
e2uHash['supe']='\u2287';
e2uHash['oplus']='\u2295';
e2uHash['otimes']='\u2297';
e2uHash['perp']='\u22A5';
e2uHash['sdot']='\u22C5';
e2uHash['lceil']='\u2308';
e2uHash['rceil']='\u2309';
e2uHash['lfloor']='\u230A';
e2uHash['rfloor']='\u230B';
e2uHash['lang']='\u2329';
e2uHash['rang']='\u232A';
e2uHash['loz']='\u25CA';
e2uHash['spades']='\u2660';
e2uHash['clubs']='\u2663';
e2uHash['hearts']='\u2665';
e2uHash['diams']='\u2666';
})();

Timeline.HTML.deEntify=function(s){
e2uHash=Timeline.HTML._e2uHash;

var re=/&(\w+?);/;
while(re.test(s)){
var m=s.match(re);
s=s.replace(re,e2uHash[m[1]]);
}
return s;
};

/* xmlhttp.js */



Timeline.XmlHttp=new Object();


Timeline.XmlHttp._onReadyStateChange=function(xmlhttp,fError,fDone){
switch(xmlhttp.readyState){









case 4:

try{
if(xmlhttp.status==0
||xmlhttp.status==200
){
if(fDone){
fDone(xmlhttp);
}

}else{
if(fError){
fError(
xmlhttp.statusText,

xmlhttp.status,

xmlhttp

);
}

}

}catch(e){

Timeline.Debug.exception(e);

}

break;
}
};


Timeline.XmlHttp._createRequest=function(){
if(Timeline.Platform.browser.isIE){
var programIDs=[
"Msxml2.XMLHTTP",
"Microsoft.XMLHTTP",
"Msxml2.XMLHTTP.4.0"
];
for(var i=0;i<programIDs.length;i++){
try{
var programID=programIDs[i];
var f=function(){
return new ActiveXObject(programID);
};
var o=f();






Timeline.XmlHttp._createRequest=f;

return o;
}catch(e){

}
}
throw new Error("Failed to create an XMLHttpRequest object");
}else{
try{
var f=function(){
return new XMLHttpRequest();
};
var o=f();






Timeline.XmlHttp._createRequest=f;

return o;
}catch(e){
throw new Error("Failed to create an XMLHttpRequest object");
}
}
};


Timeline.XmlHttp.get=function(url,fError,fDone){
var xmlhttp=Timeline.XmlHttp._createRequest();

xmlhttp.open("GET",url,true);
xmlhttp.onreadystatechange=function(){
Timeline.XmlHttp._onReadyStateChange(xmlhttp,fError,fDone);
};
xmlhttp.send(null);
};


Timeline.XmlHttp.post=function(url,body,fError,fDone){
var xmlhttp=Timeline.XmlHttp._createRequest();

xmlhttp.open("POST",url,true);
xmlhttp.onreadystatechange=function(){
Timeline.XmlHttp._onReadyStateChange(xmlhttp,fError,fDone);
};
xmlhttp.send(body);
};

Timeline.XmlHttp._forceXML=function(xmlhttp){
try{
xmlhttp.overrideMimeType("text/xml");
}catch(e){
xmlhttp.setrequestheader("Content-Type","text/xml");
}
};

/* decorators.js */



Timeline.SpanHighlightDecorator=function(params){
this._unit=("unit"in params)?params.unit:Timeline.NativeDateUnit;
this._startDate=(typeof params.startDate=="string")?
this._unit.parseFromObject(params.startDate):params.startDate;
this._endDate=(typeof params.endDate=="string")?
this._unit.parseFromObject(params.endDate):params.endDate;
this._startLabel=params.startLabel;
this._endLabel=params.endLabel;
this._color=params.color;
this._opacity=("opacity"in params)?params.opacity:100;
};

Timeline.SpanHighlightDecorator.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;

this._layerDiv=null;
};

Timeline.SpanHighlightDecorator.prototype.paint=function(){
if(this._layerDiv!=null){
this._band.removeLayerDiv(this._layerDiv);
}
this._layerDiv=this._band.createLayerDiv(10);
this._layerDiv.setAttribute("name","span-highlight-decorator");
this._layerDiv.style.display="none";

var minDate=this._band.getMinDate();
var maxDate=this._band.getMaxDate();

if(this._unit.compare(this._startDate,maxDate)<0&&
this._unit.compare(this._endDate,minDate)>0){

minDate=this._unit.later(minDate,this._startDate);
maxDate=this._unit.earlier(maxDate,this._endDate);

var minPixel=this._band.dateToPixelOffset(minDate);
var maxPixel=this._band.dateToPixelOffset(maxDate);

var doc=this._timeline.getDocument();

var createTable=function(){
var table=doc.createElement("table");
table.insertRow(0).insertCell(0);
return table;
};

var div=doc.createElement("div");
div.style.position="absolute";
div.style.overflow="hidden";
div.style.background=this._color;
if(this._opacity<100){
Timeline.Graphics.setOpacity(div,this._opacity);
}
this._layerDiv.appendChild(div);

var tableStartLabel=createTable();
tableStartLabel.style.position="absolute";
tableStartLabel.style.overflow="hidden";
tableStartLabel.style.fontSize="300%";
tableStartLabel.style.fontWeight="bold";
tableStartLabel.style.color=this._color;
tableStartLabel.rows[0].cells[0].innerHTML=this._startLabel;
this._layerDiv.appendChild(tableStartLabel);

var tableEndLabel=createTable();
tableEndLabel.style.position="absolute";
tableEndLabel.style.overflow="hidden";
tableEndLabel.style.fontSize="300%";
tableEndLabel.style.fontWeight="bold";
tableEndLabel.style.color=this._color;
tableEndLabel.rows[0].cells[0].innerHTML=this._endLabel;
this._layerDiv.appendChild(tableEndLabel);

if(this._timeline.isHorizontal()){
div.style.left=minPixel+"px";
div.style.width=(maxPixel-minPixel)+"px";
div.style.top="0px";
div.style.height="100%";

tableStartLabel.style.right=(this._band.getTotalViewLength()-minPixel)+"px";
tableStartLabel.style.width=(this._startLabel.length)+"em";
tableStartLabel.style.top="0px";
tableStartLabel.style.height="100%";
tableStartLabel.style.textAlign="right";

tableEndLabel.style.left=maxPixel+"px";
tableEndLabel.style.width=(this._endLabel.length)+"em";
tableEndLabel.style.top="0px";
tableEndLabel.style.height="100%";
}else{
div.style.top=minPixel+"px";
div.style.height=(maxPixel-minPixel)+"px";
div.style.left="0px";
div.style.width="100%";

tableStartLabel.style.bottom=minPixel+"px";
tableStartLabel.style.height="1.5px";
tableStartLabel.style.left="0px";
tableStartLabel.style.width="100%";

tableEndLabel.style.top=maxPixel+"px";
tableEndLabel.style.height="1.5px";
tableEndLabel.style.left="0px";
tableEndLabel.style.width="100%";
}
}
this._layerDiv.style.display="block";
};

Timeline.SpanHighlightDecorator.prototype.softPaint=function(){
};



Timeline.PointHighlightDecorator=function(params){
this._unit=("unit"in params)?params.unit:Timeline.NativeDateUnit;
this._date=(typeof params.date=="string")?
this._unit.parseFromObject(params.date):params.date;
this._width=("width"in params)?params.width:10;
this._color=params.color;
this._opacity=("opacity"in params)?params.opacity:100;
};

Timeline.PointHighlightDecorator.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;

this._layerDiv=null;
};

Timeline.PointHighlightDecorator.prototype.paint=function(){
if(this._layerDiv!=null){
this._band.removeLayerDiv(this._layerDiv);
}
this._layerDiv=this._band.createLayerDiv(10);
this._layerDiv.setAttribute("name","span-highlight-decorator");
this._layerDiv.style.display="none";

var minDate=this._band.getMinDate();
var maxDate=this._band.getMaxDate();

if(this._unit.compare(this._date,maxDate)<0&&
this._unit.compare(this._date,minDate)>0){

var pixel=this._band.dateToPixelOffset(this._date);
var minPixel=pixel-Math.round(this._width/2);

var doc=this._timeline.getDocument();

var div=doc.createElement("div");
div.style.position="absolute";
div.style.overflow="hidden";
div.style.background=this._color;
if(this._opacity<100){
Timeline.Graphics.setOpacity(div,this._opacity);
}
this._layerDiv.appendChild(div);

if(this._timeline.isHorizontal()){
div.style.left=minPixel+"px";
div.style.width=this._width+"px";
div.style.top="0px";
div.style.height="100%";
}else{
div.style.top=minPixel+"px";
div.style.height=this._width+"px";
div.style.left="0px";
div.style.width="100%";
}
}
this._layerDiv.style.display="block";
};

Timeline.PointHighlightDecorator.prototype.softPaint=function(){
};


/* ether-painters.js */



Timeline.GregorianEtherPainter=function(params){
this._params=params;
this._theme=params.theme;
this._unit=params.unit;
this._multiple=("multiple"in params)?params.multiple:1;
};

Timeline.GregorianEtherPainter.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;

this._backgroundLayer=band.createLayerDiv(0);
this._backgroundLayer.setAttribute("name","ether-background");
this._backgroundLayer.style.background=this._theme.ether.backgroundColors[band.getIndex()];

this._markerLayer=null;
this._lineLayer=null;

var align=("align"in this._params&&this._params.align!=undefined)?this._params.align:
this._theme.ether.interval.marker[timeline.isHorizontal()?"hAlign":"vAlign"];
var showLine=("showLine"in this._params)?this._params.showLine:
this._theme.ether.interval.line.show;

this._intervalMarkerLayout=new Timeline.EtherIntervalMarkerLayout(
this._timeline,this._band,this._theme,align,showLine);

this._highlight=new Timeline.EtherHighlight(
this._timeline,this._band,this._theme,this._backgroundLayer);
}

Timeline.GregorianEtherPainter.prototype.setHighlight=function(startDate,endDate){
this._highlight.position(startDate,endDate);
}

Timeline.GregorianEtherPainter.prototype.paint=function(){
if(this._markerLayer){
this._band.removeLayerDiv(this._markerLayer);
}
this._markerLayer=this._band.createLayerDiv(100);
this._markerLayer.setAttribute("name","ether-markers");
this._markerLayer.style.display="none";

if(this._lineLayer){
this._band.removeLayerDiv(this._lineLayer);
}
this._lineLayer=this._band.createLayerDiv(1);
this._lineLayer.setAttribute("name","ether-lines");
this._lineLayer.style.display="none";

var minDate=this._band.getMinDate();
var maxDate=this._band.getMaxDate();

var timeZone=this._band.getTimeZone();
var labeller=this._band.getLabeller();

Timeline.DateTime.roundDownToInterval(minDate,this._unit,timeZone,this._multiple,this._theme.firstDayOfWeek);

var p=this;
var incrementDate=function(date){
for(var i=0;i<p._multiple;i++){
Timeline.DateTime.incrementByInterval(date,p._unit);
}
};

while(minDate.getTime()<maxDate.getTime()){
this._intervalMarkerLayout.createIntervalMarker(
minDate,labeller,this._unit,this._markerLayer,this._lineLayer);

incrementDate(minDate);
}
this._markerLayer.style.display="block";
this._lineLayer.style.display="block";
};

Timeline.GregorianEtherPainter.prototype.softPaint=function(){
};



Timeline.HotZoneGregorianEtherPainter=function(params){
this._params=params;
this._theme=params.theme;

this._zones=[{
startTime:Number.NEGATIVE_INFINITY,
endTime:Number.POSITIVE_INFINITY,
unit:params.unit,
multiple:1
}];
for(var i=0;i<params.zones.length;i++){
var zone=params.zones[i];
var zoneStart=Timeline.DateTime.parseGregorianDateTime(zone.start).getTime();
var zoneEnd=Timeline.DateTime.parseGregorianDateTime(zone.end).getTime();

for(var j=0;j<this._zones.length&&zoneEnd>zoneStart;j++){
var zone2=this._zones[j];

if(zoneStart<zone2.endTime){
if(zoneStart>zone2.startTime){
this._zones.splice(j,0,{
startTime:zone2.startTime,
endTime:zoneStart,
unit:zone2.unit,
multiple:zone2.multiple
});
j++;

zone2.startTime=zoneStart;
}

if(zoneEnd<zone2.endTime){
this._zones.splice(j,0,{
startTime:zoneStart,
endTime:zoneEnd,
unit:zone.unit,
multiple:(zone.multiple)?zone.multiple:1
});
j++;

zone2.startTime=zoneEnd;
zoneStart=zoneEnd;
}else{
zone2.multiple=zone.multiple;
zone2.unit=zone.unit;
zoneStart=zone2.endTime;
}
}
}
}
};

Timeline.HotZoneGregorianEtherPainter.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;

this._backgroundLayer=band.createLayerDiv(0);
this._backgroundLayer.setAttribute("name","ether-background");
this._backgroundLayer.style.background=this._theme.ether.backgroundColors[band.getIndex()];

this._markerLayer=null;
this._lineLayer=null;

var align=("align"in this._params&&this._params.align!=undefined)?this._params.align:
this._theme.ether.interval.marker[timeline.isHorizontal()?"hAlign":"vAlign"];
var showLine=("showLine"in this._params)?this._params.showLine:
this._theme.ether.interval.line.show;

this._intervalMarkerLayout=new Timeline.EtherIntervalMarkerLayout(
this._timeline,this._band,this._theme,align,showLine);

this._highlight=new Timeline.EtherHighlight(
this._timeline,this._band,this._theme,this._backgroundLayer);
}

Timeline.HotZoneGregorianEtherPainter.prototype.setHighlight=function(startDate,endDate){
this._highlight.position(startDate,endDate);
}

Timeline.HotZoneGregorianEtherPainter.prototype.paint=function(){
if(this._markerLayer){
this._band.removeLayerDiv(this._markerLayer);
}
this._markerLayer=this._band.createLayerDiv(100);
this._markerLayer.setAttribute("name","ether-markers");
this._markerLayer.style.display="none";

if(this._lineLayer){
this._band.removeLayerDiv(this._lineLayer);
}
this._lineLayer=this._band.createLayerDiv(1);
this._lineLayer.setAttribute("name","ether-lines");
this._lineLayer.style.display="none";

var minDate=this._band.getMinDate();
var maxDate=this._band.getMaxDate();

var timeZone=this._band.getTimeZone();
var labeller=this._band.getLabeller();

var p=this;
var incrementDate=function(date,zone){
for(var i=0;i<zone.multiple;i++){
Timeline.DateTime.incrementByInterval(date,zone.unit);
}
};

var zStart=0;
while(zStart<this._zones.length){
if(minDate.getTime()<this._zones[zStart].endTime){
break;
}
zStart++;
}
var zEnd=this._zones.length-1;
while(zEnd>=0){
if(maxDate.getTime()>this._zones[zEnd].startTime){
break;
}
zEnd--;
}

for(var z=zStart;z<=zEnd;z++){
var zone=this._zones[z];

var minDate2=new Date(Math.max(minDate.getTime(),zone.startTime));
var maxDate2=new Date(Math.min(maxDate.getTime(),zone.endTime));

Timeline.DateTime.roundDownToInterval(minDate2,zone.unit,timeZone,zone.multiple,this._theme.firstDayOfWeek);
Timeline.DateTime.roundUpToInterval(maxDate2,zone.unit,timeZone,zone.multiple,this._theme.firstDayOfWeek);

while(minDate2.getTime()<maxDate2.getTime()){
this._intervalMarkerLayout.createIntervalMarker(
minDate2,labeller,zone.unit,this._markerLayer,this._lineLayer);

incrementDate(minDate2,zone);
}
}
this._markerLayer.style.display="block";
this._lineLayer.style.display="block";
};

Timeline.HotZoneGregorianEtherPainter.prototype.softPaint=function(){
};



Timeline.YearCountEtherPainter=function(params){
this._params=params;
this._theme=params.theme;
this._startDate=Timeline.DateTime.parseGregorianDateTime(params.startDate);
this._multiple=("multiple"in params)?params.multiple:1;
};

Timeline.YearCountEtherPainter.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;

this._backgroundLayer=band.createLayerDiv(0);
this._backgroundLayer.setAttribute("name","ether-background");
this._backgroundLayer.style.background=this._theme.ether.backgroundColors[band.getIndex()];

this._markerLayer=null;
this._lineLayer=null;

var align=("align"in this._params)?this._params.align:
this._theme.ether.interval.marker[timeline.isHorizontal()?"hAlign":"vAlign"];
var showLine=("showLine"in this._params)?this._params.showLine:
this._theme.ether.interval.line.show;

this._intervalMarkerLayout=new Timeline.EtherIntervalMarkerLayout(
this._timeline,this._band,this._theme,align,showLine);

this._highlight=new Timeline.EtherHighlight(
this._timeline,this._band,this._theme,this._backgroundLayer);
};

Timeline.YearCountEtherPainter.prototype.setHighlight=function(startDate,endDate){
this._highlight.position(startDate,endDate);
};

Timeline.YearCountEtherPainter.prototype.paint=function(){
if(this._markerLayer){
this._band.removeLayerDiv(this._markerLayer);
}
this._markerLayer=this._band.createLayerDiv(100);
this._markerLayer.setAttribute("name","ether-markers");
this._markerLayer.style.display="none";

if(this._lineLayer){
this._band.removeLayerDiv(this._lineLayer);
}
this._lineLayer=this._band.createLayerDiv(1);
this._lineLayer.setAttribute("name","ether-lines");
this._lineLayer.style.display="none";

var minDate=new Date(this._startDate.getTime());
var maxDate=this._band.getMaxDate();
var yearDiff=this._band.getMinDate().getUTCFullYear()-this._startDate.getUTCFullYear();
minDate.setUTCFullYear(this._band.getMinDate().getUTCFullYear()-yearDiff%this._multiple);

var p=this;
var incrementDate=function(date){
for(var i=0;i<p._multiple;i++){
Timeline.DateTime.incrementByInterval(date,Timeline.DateTime.YEAR);
}
};
var labeller={
labelInterval:function(date,intervalUnit){
var diff=date.getUTCFullYear()-p._startDate.getUTCFullYear();
return{
text:diff,
emphasized:diff==0
};
}
};

while(minDate.getTime()<maxDate.getTime()){
this._intervalMarkerLayout.createIntervalMarker(
minDate,labeller,Timeline.DateTime.YEAR,this._markerLayer,this._lineLayer);

incrementDate(minDate);
}
this._markerLayer.style.display="block";
this._lineLayer.style.display="block";
};

Timeline.YearCountEtherPainter.prototype.softPaint=function(){
};



Timeline.QuarterlyEtherPainter=function(params){
this._params=params;
this._theme=params.theme;
this._startDate=Timeline.DateTime.parseGregorianDateTime(params.startDate);
};

Timeline.QuarterlyEtherPainter.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;

this._backgroundLayer=band.createLayerDiv(0);
this._backgroundLayer.setAttribute("name","ether-background");
this._backgroundLayer.style.background=this._theme.ether.backgroundColors[band.getIndex()];

this._markerLayer=null;
this._lineLayer=null;

var align=("align"in this._params)?this._params.align:
this._theme.ether.interval.marker[timeline.isHorizontal()?"hAlign":"vAlign"];
var showLine=("showLine"in this._params)?this._params.showLine:
this._theme.ether.interval.line.show;

this._intervalMarkerLayout=new Timeline.EtherIntervalMarkerLayout(
this._timeline,this._band,this._theme,align,showLine);

this._highlight=new Timeline.EtherHighlight(
this._timeline,this._band,this._theme,this._backgroundLayer);
};

Timeline.QuarterlyEtherPainter.prototype.setHighlight=function(startDate,endDate){
this._highlight.position(startDate,endDate);
};

Timeline.QuarterlyEtherPainter.prototype.paint=function(){
if(this._markerLayer){
this._band.removeLayerDiv(this._markerLayer);
}
this._markerLayer=this._band.createLayerDiv(100);
this._markerLayer.setAttribute("name","ether-markers");
this._markerLayer.style.display="none";

if(this._lineLayer){
this._band.removeLayerDiv(this._lineLayer);
}
this._lineLayer=this._band.createLayerDiv(1);
this._lineLayer.setAttribute("name","ether-lines");
this._lineLayer.style.display="none";

var minDate=new Date(0);
var maxDate=this._band.getMaxDate();

minDate.setUTCFullYear(Math.max(this._startDate.getUTCFullYear(),this._band.getMinDate().getUTCFullYear()));
minDate.setUTCMonth(this._startDate.getUTCMonth());

var p=this;
var incrementDate=function(date){
date.setUTCMonth(date.getUTCMonth()+3);
};
var labeller={
labelInterval:function(date,intervalUnit){
var quarters=(4+(date.getUTCMonth()-p._startDate.getUTCMonth())/3)%4;
if(quarters!=0){
return{text:"Q"+(quarters+1),emphasized:false};
}else{
return{text:"Y"+(date.getUTCFullYear()-p._startDate.getUTCFullYear()+1),emphasized:true};
}
}
};

while(minDate.getTime()<maxDate.getTime()){
this._intervalMarkerLayout.createIntervalMarker(
minDate,labeller,Timeline.DateTime.YEAR,this._markerLayer,this._lineLayer);

incrementDate(minDate);
}
this._markerLayer.style.display="block";
this._lineLayer.style.display="block";
};

Timeline.QuarterlyEtherPainter.prototype.softPaint=function(){
};



Timeline.EtherIntervalMarkerLayout=function(timeline,band,theme,align,showLine){
var horizontal=timeline.isHorizontal();
if(horizontal){
if(align=="Top"){
this.positionDiv=function(div,offset){
div.style.left=offset+"px";
div.style.top="0px";
};
}else{
this.positionDiv=function(div,offset){
div.style.left=offset+"px";
div.style.bottom="0px";
};
}
}else{
if(align=="Left"){
this.positionDiv=function(div,offset){
div.style.top=offset+"px";
div.style.left="0px";
};
}else{
this.positionDiv=function(div,offset){
div.style.top=offset+"px";
div.style.right="0px";
};
}
}

var markerTheme=theme.ether.interval.marker;
var lineTheme=theme.ether.interval.line;
var weekendTheme=theme.ether.interval.weekend;

var stylePrefix=(horizontal?"h":"v")+align;
var labelStyler=markerTheme[stylePrefix+"Styler"];
var emphasizedLabelStyler=markerTheme[stylePrefix+"EmphasizedStyler"];
var day=Timeline.DateTime.gregorianUnitLengths[Timeline.DateTime.DAY];

this.createIntervalMarker=function(date,labeller,unit,markerDiv,lineDiv){
var offset=Math.round(band.dateToPixelOffset(date));

if(showLine&&unit!=Timeline.DateTime.WEEK){
var divLine=timeline.getDocument().createElement("div");
divLine.style.position="absolute";

if(lineTheme.opacity<100){
Timeline.Graphics.setOpacity(divLine,lineTheme.opacity);
}

if(horizontal){
divLine.style.borderLeft="1px solid "+lineTheme.color;
divLine.style.left=offset+"px";
divLine.style.width="1px";
divLine.style.top="0px";
divLine.style.height="100%";
}else{
divLine.style.borderTop="1px solid "+lineTheme.color;
divLine.style.top=offset+"px";
divLine.style.height="1px";
divLine.style.left="0px";
divLine.style.width="100%";
}
lineDiv.appendChild(divLine);
}
if(unit==Timeline.DateTime.WEEK){
var firstDayOfWeek=theme.firstDayOfWeek;

var saturday=new Date(date.getTime()+(6-firstDayOfWeek-7)*day);
var monday=new Date(saturday.getTime()+2*day);

var saturdayPixel=Math.round(band.dateToPixelOffset(saturday));
var mondayPixel=Math.round(band.dateToPixelOffset(monday));
var length=Math.max(1,mondayPixel-saturdayPixel);

var divWeekend=timeline.getDocument().createElement("div");
divWeekend.style.position="absolute";

divWeekend.style.background=weekendTheme.color;
if(weekendTheme.opacity<100){
Timeline.Graphics.setOpacity(divWeekend,weekendTheme.opacity);
}

if(horizontal){
divWeekend.style.left=saturdayPixel+"px";
divWeekend.style.width=length+"px";
divWeekend.style.top="0px";
divWeekend.style.height="100%";
}else{
divWeekend.style.top=saturdayPixel+"px";
divWeekend.style.height=length+"px";
divWeekend.style.left="0px";
divWeekend.style.width="100%";
}
lineDiv.appendChild(divWeekend);
}

var label=labeller.labelInterval(date,unit);

var div=timeline.getDocument().createElement("div");
div.innerHTML=label.text;
div.style.position="absolute";
(label.emphasized?emphasizedLabelStyler:labelStyler)(div);

this.positionDiv(div,offset);
markerDiv.appendChild(div);

return div;
};
};



Timeline.EtherHighlight=function(timeline,band,theme,backgroundLayer){
var horizontal=timeline.isHorizontal();

this._highlightDiv=null;
this._createHighlightDiv=function(){
if(this._highlightDiv==null){
this._highlightDiv=timeline.getDocument().createElement("div");
this._highlightDiv.setAttribute("name","ether-highlight");
this._highlightDiv.style.position="absolute";
this._highlightDiv.style.background=theme.ether.highlightColor;

var opacity=theme.ether.highlightOpacity;
if(opacity<100){
Timeline.Graphics.setOpacity(this._highlightDiv,opacity);
}

backgroundLayer.appendChild(this._highlightDiv);
}
}

this.position=function(startDate,endDate){
this._createHighlightDiv();

var startPixel=Math.round(band.dateToPixelOffset(startDate));
var endPixel=Math.round(band.dateToPixelOffset(endDate));
var length=Math.max(endPixel-startPixel,3);
if(horizontal){
this._highlightDiv.style.left=startPixel+"px";
this._highlightDiv.style.width=length+"px";
this._highlightDiv.style.top="2px";
this._highlightDiv.style.height=(band.getViewWidth()-4)+"px";
}else{
this._highlightDiv.style.top=startPixel+"px";
this._highlightDiv.style.height=length+"px";
this._highlightDiv.style.left="2px";
this._highlightDiv.style.width=(band.getViewWidth()-4)+"px";
}
}
};



/* ethers.js */



Timeline.LinearEther=function(params){
this._params=params;
this._interval=params.interval;
this._pixelsPerInterval=params.pixelsPerInterval;
};

Timeline.LinearEther.prototype.initialize=function(timeline){
this._timeline=timeline;
this._unit=timeline.getUnit();

if("startsOn"in this._params){
this._start=this._unit.parseFromObject(this._params.startsOn);
}else if("endsOn"in this._params){
this._start=this._unit.parseFromObject(this._params.endsOn);
this.shiftPixels(-this._timeline.getPixelLength());
}else if("centersOn"in this._params){
this._start=this._unit.parseFromObject(this._params.centersOn);
this.shiftPixels(-this._timeline.getPixelLength()/2);
}else{
this._start=this._unit.makeDefaultValue();
this.shiftPixels(-this._timeline.getPixelLength()/2);
}
};

Timeline.LinearEther.prototype.setDate=function(date){
this._start=this._unit.cloneValue(date);
};

Timeline.LinearEther.prototype.shiftPixels=function(pixels){
var numeric=this._interval*pixels/this._pixelsPerInterval;
this._start=this._unit.change(this._start,numeric);
};

Timeline.LinearEther.prototype.dateToPixelOffset=function(date){
var numeric=this._unit.compare(date,this._start);
return this._pixelsPerInterval*numeric/this._interval;
};

Timeline.LinearEther.prototype.pixelOffsetToDate=function(pixels){
var numeric=pixels*this._interval/this._pixelsPerInterval;
return this._unit.change(this._start,numeric);
};



Timeline.HotZoneEther=function(params){
this._params=params;
this._interval=params.interval;
this._pixelsPerInterval=params.pixelsPerInterval;
};

Timeline.HotZoneEther.prototype.initialize=function(timeline){
this._timeline=timeline;
this._unit=timeline.getUnit();

this._zones=[{
startTime:Number.NEGATIVE_INFINITY,
endTime:Number.POSITIVE_INFINITY,
magnify:1
}];
var params=this._params;
for(var i=0;i<params.zones.length;i++){
var zone=params.zones[i];
var zoneStart=this._unit.parseFromObject(zone.start);
var zoneEnd=this._unit.parseFromObject(zone.end);

for(var j=0;j<this._zones.length&&this._unit.compare(zoneEnd,zoneStart)>0;j++){
var zone2=this._zones[j];

if(this._unit.compare(zoneStart,zone2.endTime)<0){
if(this._unit.compare(zoneStart,zone2.startTime)>0){
this._zones.splice(j,0,{
startTime:zone2.startTime,
endTime:zoneStart,
magnify:zone2.magnify
});
j++;

zone2.startTime=zoneStart;
}

if(this._unit.compare(zoneEnd,zone2.endTime)<0){
this._zones.splice(j,0,{
startTime:zoneStart,
endTime:zoneEnd,
magnify:zone.magnify*zone2.magnify
});
j++;

zone2.startTime=zoneEnd;
zoneStart=zoneEnd;
}else{
zone2.magnify*=zone.magnify;
zoneStart=zone2.endTime;
}
}
}
}

if("startsOn"in this._params){
this._start=this._unit.parseFromObject(this._params.startsOn);
}else if("endsOn"in this._params){
this._start=this._unit.parseFromObject(this._params.endsOn);
this.shiftPixels(-this._timeline.getPixelLength());
}else if("centersOn"in this._params){
this._start=this._unit.parseFromObject(this._params.centersOn);
this.shiftPixels(-this._timeline.getPixelLength()/2);
}else{
this._start=this._unit.makeDefaultValue();
this.shiftPixels(-this._timeline.getPixelLength()/2);
}
};

Timeline.HotZoneEther.prototype.setDate=function(date){
this._start=this._unit.cloneValue(date);
};

Timeline.HotZoneEther.prototype.shiftPixels=function(pixels){
this._start=this.pixelOffsetToDate(pixels);
};

Timeline.HotZoneEther.prototype.dateToPixelOffset=function(date){
return this._dateDiffToPixelOffset(this._start,date);
};

Timeline.HotZoneEther.prototype.pixelOffsetToDate=function(pixels){
return this._pixelOffsetToDate(pixels,this._start);
};

Timeline.HotZoneEther.prototype._dateDiffToPixelOffset=function(fromDate,toDate){
var scale=this._getScale();
var fromTime=fromDate;
var toTime=toDate;

var pixels=0;
if(this._unit.compare(fromTime,toTime)<0){
var z=0;
while(z<this._zones.length){
if(this._unit.compare(fromTime,this._zones[z].endTime)<0){
break;
}
z++;
}

while(this._unit.compare(fromTime,toTime)<0){
var zone=this._zones[z];
var toTime2=this._unit.earlier(toTime,zone.endTime);

pixels+=(this._unit.compare(toTime2,fromTime)/(scale/zone.magnify));

fromTime=toTime2;
z++;
}
}else{
var z=this._zones.length-1;
while(z>=0){
if(this._unit.compare(fromTime,this._zones[z].startTime)>0){
break;
}
z--;
}

while(this._unit.compare(fromTime,toTime)>0){
var zone=this._zones[z];
var toTime2=this._unit.later(toTime,zone.startTime);

pixels+=(this._unit.compare(toTime2,fromTime)/(scale/zone.magnify));

fromTime=toTime2;
z--;
}
}
return pixels;
};

Timeline.HotZoneEther.prototype._pixelOffsetToDate=function(pixels,fromDate){
var scale=this._getScale();
var time=fromDate;
if(pixels>0){
var z=0;
while(z<this._zones.length){
if(this._unit.compare(time,this._zones[z].endTime)<0){
break;
}
z++;
}

while(pixels>0){
var zone=this._zones[z];
var scale2=scale/zone.magnify;

if(zone.endTime==Number.POSITIVE_INFINITY){
time=this._unit.change(time,pixels*scale2);
pixels=0;
}else{
var pixels2=this._unit.compare(zone.endTime,time)/scale2;
if(pixels2>pixels){
time=this._unit.change(time,pixels*scale2);
pixels=0;
}else{
time=zone.endTime;
pixels-=pixels2;
}
}
z++;
}
}else{
var z=this._zones.length-1;
while(z>=0){
if(this._unit.compare(time,this._zones[z].startTime)>0){
break;
}
z--;
}

pixels=-pixels;
while(pixels>0){
var zone=this._zones[z];
var scale2=scale/zone.magnify;

if(zone.startTime==Number.NEGATIVE_INFINITY){
time=this._unit.change(time,-pixels*scale2);
pixels=0;
}else{
var pixels2=this._unit.compare(time,zone.startTime)/scale2;
if(pixels2>pixels){
time=this._unit.change(time,-pixels*scale2);
pixels=0;
}else{
time=zone.startTime;
pixels-=pixels2;
}
}
z--;
}
}
return time;
};

Timeline.HotZoneEther.prototype._getScale=function(){
return this._interval/this._pixelsPerInterval;
};


/* labellers.js */



Timeline.GregorianDateLabeller=function(locale,timeZone){
this._locale=locale;
this._timeZone=timeZone;
};

Timeline.GregorianDateLabeller.monthNames=[];
Timeline.GregorianDateLabeller.dayNames=[];
Timeline.GregorianDateLabeller.labelIntervalFunctions=[];

Timeline.GregorianDateLabeller.getMonthName=function(month,locale){
return Timeline.GregorianDateLabeller.monthNames[locale][month];
};

Timeline.GregorianDateLabeller.prototype.labelInterval=function(date,intervalUnit){
var f=Timeline.GregorianDateLabeller.labelIntervalFunctions[this._locale];
if(f==null){
f=Timeline.GregorianDateLabeller.prototype.defaultLabelInterval;
}
return f.call(this,date,intervalUnit);
};

Timeline.GregorianDateLabeller.prototype.labelPrecise=function(date){
return Timeline.DateTime.removeTimeZoneOffset(
date,
this._timeZone
).toUTCString();
};

Timeline.GregorianDateLabeller.prototype.defaultLabelInterval=function(date,intervalUnit){
var text;
var emphasized=false;

date=Timeline.DateTime.removeTimeZoneOffset(date,this._timeZone);

switch(intervalUnit){
case Timeline.DateTime.MILLISECOND:
text=date.getUTCMilliseconds();
break;
case Timeline.DateTime.SECOND:
text=date.getUTCSeconds();
break;
case Timeline.DateTime.MINUTE:
var m=date.getUTCMinutes();
if(m==0){
text=date.getUTCHours()+":00";
emphasized=true;
}else{
text=m;
}
break;
case Timeline.DateTime.HOUR:
text=date.getUTCHours()+"hr";
break;
case Timeline.DateTime.DAY:
text=Timeline.GregorianDateLabeller.getMonthName(date.getUTCMonth(),this._locale)+" "+date.getUTCDate();
break;
case Timeline.DateTime.WEEK:
text=Timeline.GregorianDateLabeller.getMonthName(date.getUTCMonth(),this._locale)+" "+date.getUTCDate();
break;
case Timeline.DateTime.MONTH:
var m=date.getUTCMonth();
if(m!=0){
text=Timeline.GregorianDateLabeller.getMonthName(m,this._locale);
break;
}
case Timeline.DateTime.YEAR:
case Timeline.DateTime.DECADE:
case Timeline.DateTime.CENTURY:
case Timeline.DateTime.MILLENNIUM:
var y=date.getUTCFullYear();
if(y>0){
text=date.getUTCFullYear();
}else{
text=(1-y)+"BC";
}
emphasized=
(intervalUnit==Timeline.DateTime.MONTH)||
(intervalUnit==Timeline.DateTime.DECADE&&y%100==0)||
(intervalUnit==Timeline.DateTime.CENTURY&&y%1000==0);
break;
default:
text=date.toUTCString();
}
return{text:text,emphasized:emphasized};
}



/* layouts.js */




Timeline.StaticTrackBasedLayout=function(params){
this._eventSource=params.eventSource;
this._ether=params.ether;
this._theme=params.theme;
this._showText=("showText"in params)?params.showText:true;

this._laidout=false;

var layout=this;
if(this._eventSource!=null){
this._eventSource.addListener({
onAddMany:function(){
layout._laidout=false;
}
});
}
};

Timeline.StaticTrackBasedLayout.prototype.initialize=function(timeline){
this._timeline=timeline;
};

Timeline.StaticTrackBasedLayout.prototype.getTrack=function(evt){
if(!this._laidout){
this._tracks=[];
this._layout();
this._laidout=true;
}
return this._tracks[evt.getID()];
};

Timeline.StaticTrackBasedLayout.prototype.getTrackCount=function(){
if(!this._laidout){
this._tracks=[];
this._layout();
this._laidout=true;
}
return this._trackCount;
};

Timeline.StaticTrackBasedLayout.prototype._layout=function(){
if(this._eventSource==null){
return;
}

var streams=[Number.NEGATIVE_INFINITY];
var layout=this;
var showText=this._showText;
var theme=this._theme;
var eventTheme=theme.event;

var layoutInstant=function(evt,startPixel,endPixel,streamOffset){
var finalPixel=startPixel-1;
if(evt.isImprecise()){
finalPixel=endPixel;
}
if(showText){
finalPixel=Math.max(finalPixel,startPixel+eventTheme.label.width);
}

return finalPixel;
};
var layoutDuration=function(evt,startPixel,endPixel,streamOffset){
if(evt.isImprecise()){
var startDate=evt.getStart();
var endDate=evt.getEnd();

var startPixel2=Math.round(layout._ether.dateToPixelOffset(startDate));
var endPixel2=Math.round(layout._ether.dateToPixelOffset(endDate));
}else{
var startPixel2=startPixel;
var endPixel2=endPixel;
}

var finalPixel=endPixel2;
var length=Math.max(endPixel2-startPixel2,1);

if(showText){
if(length<eventTheme.label.width){
finalPixel=endPixel2+eventTheme.label.width;
}
}

return finalPixel;
};
var layoutEvent=function(evt){
var startDate=evt.getStart();
var endDate=evt.getEnd();

var startPixel=Math.round(layout._ether.dateToPixelOffset(startDate));
var endPixel=Math.round(layout._ether.dateToPixelOffset(endDate));

var streamIndex=0;
for(;streamIndex<streams.length;streamIndex++){
if(streams[streamIndex]<startPixel){
break;
}
}
if(streamIndex>=streams.length){
streams.push(Number.NEGATIVE_INFINITY);
}

var streamOffset=(eventTheme.track.offset+
streamIndex*(eventTheme.track.height+eventTheme.track.gap))+"em";

layout._tracks[evt.getID()]=streamIndex;

if(evt.isInstant()){
streams[streamIndex]=layoutInstant(evt,startPixel,endPixel,streamOffset);
}else{
streams[streamIndex]=layoutDuration(evt,startPixel,endPixel,streamOffset);
}
};

var iterator=this._eventSource.getAllEventIterator();
while(iterator.hasNext()){
var evt=iterator.next();
layoutEvent(evt);
}

this._trackCount=streams.length;
};

/* painters.js */



Timeline.DurationEventPainter=function(params){
this._params=params;
this._theme=params.theme;
this._layout=params.layout;

this._showText=params.showText;
this._showLineForNoText=("showLineForNoText"in params)?
params.showLineForNoText:params.theme.event.instant.showLineForNoText;

this._filterMatcher=null;
this._highlightMatcher=null;
};

Timeline.DurationEventPainter.prototype.initialize=function(band,timeline){
this._band=band;
this._timeline=timeline;
this._layout.initialize(band,timeline);

this._eventLayer=null;
this._highlightLayer=null;
};

Timeline.DurationEventPainter.prototype.getLayout=function(){
return this._layout;
};

Timeline.DurationEventPainter.prototype.setLayout=function(layout){
this._layout=layout;
};

Timeline.DurationEventPainter.prototype.getFilterMatcher=function(){
return this._filterMatcher;
};

Timeline.DurationEventPainter.prototype.setFilterMatcher=function(filterMatcher){
this._filterMatcher=filterMatcher;
};

Timeline.DurationEventPainter.prototype.getHighlightMatcher=function(){
return this._highlightMatcher;
};

Timeline.DurationEventPainter.prototype.setHighlightMatcher=function(highlightMatcher){
this._highlightMatcher=highlightMatcher;
};

Timeline.DurationEventPainter.prototype.paint=function(){
var eventSource=this._band.getEventSource();
if(eventSource==null){
return;
}

if(this._highlightLayer!=null){
this._band.removeLayerDiv(this._highlightLayer);
}
this._highlightLayer=this._band.createLayerDiv(105);
this._highlightLayer.setAttribute("name","event-highlights");
this._highlightLayer.style.display="none";

if(this._eventLayer!=null){
this._band.removeLayerDiv(this._eventLayer);
}
this._eventLayer=this._band.createLayerDiv(110);
this._eventLayer.setAttribute("name","events");
this._eventLayer.style.display="none";

var minDate=this._band.getMinDate();
var maxDate=this._band.getMaxDate();

var doc=this._timeline.getDocument();

var p=this;
var eventLayer=this._eventLayer;
var highlightLayer=this._highlightLayer;

var showText=this._showText;
var theme=this._params.theme;
var eventTheme=theme.event;
var trackOffset=eventTheme.track.offset;
var trackHeight=("trackHeight"in this._params)?this._params.trackHeight:eventTheme.track.height;
var trackGap=("trackGap"in this._params)?this._params.trackGap:eventTheme.track.gap;


var appendIcon=function(evt,div){
var icon=evt.getIcon();
var img=Timeline.Graphics.createTranslucentImage(
doc,icon!=null?icon:eventTheme.instant.icon
);
div.appendChild(img);
div.style.cursor="pointer";

Timeline.DOM.registerEvent(div,"mousedown",function(elmt,domEvt,target){
p._onClickInstantEvent(img,domEvt,evt);
});
};
var createHighlightDiv=function(highlightIndex,startPixel,length,highlightOffset,highlightWidth){
if(highlightIndex>=0){
var color=eventTheme.highlightColors[Math.min(highlightIndex,eventTheme.highlightColors.length-1)];

var div=doc.createElement("div");
div.style.position="absolute";
div.style.overflow="hidden";
div.style.left=(startPixel-3)+"px";
div.style.width=(length+6)+"px";
div.style.top=highlightOffset+"em";
div.style.height=highlightWidth+"em";
div.style.background=color;


highlightLayer.appendChild(div);
}
};

var createInstantDiv=function(evt,startPixel,endPixel,streamOffset,highlightIndex,highlightOffset,highlightWidth){
if(evt.isImprecise()){
var length=Math.max(endPixel-startPixel,1);

var divImprecise=doc.createElement("div");
divImprecise.style.position="absolute";
divImprecise.style.overflow="hidden";

divImprecise.style.top=streamOffset;
divImprecise.style.height=trackHeight+"em";
divImprecise.style.left=startPixel+"px";
divImprecise.style.width=length+"px";

divImprecise.style.background=eventTheme.instant.impreciseColor;
if(eventTheme.instant.impreciseOpacity<100){
Timeline.Graphics.setOpacity(divImprecise,eventTheme.instant.impreciseOpacity);
}

eventLayer.appendChild(divImprecise);
}

var div=doc.createElement("div");
div.style.position="absolute";
div.style.overflow="hidden";
eventLayer.appendChild(div);

var foreground=evt.getTextColor();
var background=evt.getColor();

var realign=-8;
var length=16;
if(showText){
div.style.width=eventTheme.label.width+"px";
div.style.color=foreground!=null?foreground:eventTheme.label.outsideColor;

appendIcon(evt,div);
div.appendChild(doc.createTextNode(evt.getText()));
}else{
if(p._showLineForNoText){
div.style.width="1px";
div.style.borderLeft="1px solid "+(background!=null?background:eventTheme.instant.lineColor);
realign=0;
length=1;
}else{
appendIcon(evt,div);
}
}

div.style.top=streamOffset;
div.style.height=trackHeight+"em";
div.style.left=(startPixel+realign)+"px";

createHighlightDiv(highlightIndex,(startPixel+realign),length,highlightOffset,highlightWidth);
};
var createDurationDiv=function(evt,startPixel,endPixel,streamOffset,highlightIndex,highlightOffset,highlightWidth){
var attachClickEvent=function(elmt){
elmt.style.cursor="pointer";
Timeline.DOM.registerEvent(elmt,"mousedown",function(elmt,domEvt,target){
p._onClickDurationEvent(domEvt,evt,target);
});
};

var length=Math.max(endPixel-startPixel,1);
if(evt.isImprecise()){
var div=doc.createElement("div");
div.style.position="absolute";
div.style.overflow="hidden";

div.style.top=streamOffset;
div.style.height=trackHeight+"em";
div.style.left=startPixel+"px";
div.style.width=length+"px";

div.style.background=eventTheme.duration.impreciseColor;
if(eventTheme.duration.impreciseOpacity<100){
Timeline.Graphics.setOpacity(div,eventTheme.duration.impreciseOpacity);
}

eventLayer.appendChild(div);

var startDate=evt.getLatestStart();
var endDate=evt.getEarliestEnd();

var startPixel2=Math.round(p._band.dateToPixelOffset(startDate));
var endPixel2=Math.round(p._band.dateToPixelOffset(endDate));
}else{
var startPixel2=startPixel;
var endPixel2=endPixel;
}

var foreground=evt.getTextColor();
var outside=true;
if(startPixel2<=endPixel2){
length=Math.max(endPixel2-startPixel2,1);
outside=!(length>eventTheme.label.width);

div=doc.createElement("div");
div.style.position="absolute";
div.style.overflow="hidden";

div.style.top=streamOffset;
div.style.height=trackHeight+"em";
div.style.left=startPixel2+"px";
div.style.width=length+"px";

var background=evt.getColor();

div.style.background=background!=null?background:eventTheme.duration.color;
if(eventTheme.duration.opacity<100){
Timeline.Graphics.setOpacity(div,eventTheme.duration.opacity);
}

eventLayer.appendChild(div);
}else{
var temp=startPixel2;
startPixel2=endPixel2;
endPixel2=temp;
}
if(div==null){
console.log(evt);
}
attachClickEvent(div);

if(showText){
var divLabel=doc.createElement("div");
divLabel.style.position="absolute";

divLabel.style.top=streamOffset;
divLabel.style.height=trackHeight+"em";
divLabel.style.left=((length>eventTheme.label.width)?startPixel2:endPixel2)+"px";
divLabel.style.width=eventTheme.label.width+"px";
divLabel.style.color=foreground!=null?foreground:(outside?eventTheme.label.outsideColor:eventTheme.label.insideColor);
divLabel.style.overflow="hidden";
divLabel.appendChild(doc.createTextNode(evt.getText()));

eventLayer.appendChild(divLabel);
attachClickEvent(divLabel);
}

createHighlightDiv(highlightIndex,startPixel,endPixel-startPixel,highlightOffset,highlightWidth);
};

var createEventDiv=function(evt,highlightIndex){
var startDate=evt.getStart();
var endDate=evt.getEnd();

var startPixel=Math.round(p._band.dateToPixelOffset(startDate));
var endPixel=Math.round(p._band.dateToPixelOffset(endDate));

var streamOffset=(trackOffset+
p._layout.getTrack(evt)*(trackHeight+trackGap));

if(evt.isInstant()){
createInstantDiv(evt,startPixel,endPixel,streamOffset+"em",
highlightIndex,streamOffset-trackGap,trackHeight+2*trackGap);
}else{
createDurationDiv(evt,startPixel,endPixel,streamOffset+"em",
highlightIndex,streamOffset-trackGap,trackHeight+2*trackGap);
}
};

var filterMatcher=(this._filterMatcher!=null)?
this._filterMatcher:
function(evt){return true;};
var highlightMatcher=(this._highlightMatcher!=null)?
this._highlightMatcher:
function(evt){return-1;};

var iterator=eventSource.getEventIterator(minDate,maxDate);
while(iterator.hasNext()){
var evt=iterator.next();
if(filterMatcher(evt)){
createEventDiv(evt,highlightMatcher(evt));
}
}

this._highlightLayer.style.display="block";
this._eventLayer.style.display="block";
};

Timeline.DurationEventPainter.prototype.softPaint=function(){
};

Timeline.DurationEventPainter.prototype._onClickInstantEvent=function(icon,domEvt,evt){
domEvt.cancelBubble=true;

var c=Timeline.DOM.getPageCoordinates(icon);
this._showBubble(
c.left+Math.ceil(icon.offsetWidth/2),
c.top+Math.ceil(icon.offsetHeight/2),
evt
);
};

Timeline.DurationEventPainter.prototype._onClickDurationEvent=function(domEvt,evt,target){
domEvt.cancelBubble=true;
if("pageX"in domEvt){
var x=domEvt.pageX;
var y=domEvt.pageY;
}else{
var c=Timeline.DOM.getPageCoordinates(target);
var x=domEvt.offsetX+c.left;
var y=domEvt.offsetY+c.top;
}
this._showBubble(x,y,evt);
};

Timeline.DurationEventPainter.prototype._showBubble=function(x,y,evt){
var div=this._band.openBubbleForPoint(
x,y,
this._theme.event.bubble.width,
this._theme.event.bubble.height
);

evt.fillInfoBubble(div,this._theme,this._band.getLabeller());
};

/* sources.js */




Timeline.DefaultEventSource=function(eventIndex){
this._events=(eventIndex instanceof Object)?eventIndex:new Timeline.EventIndex();
this._listeners=[];
};

Timeline.DefaultEventSource.prototype.addListener=function(listener){
this._listeners.push(listener);
};

Timeline.DefaultEventSource.prototype.removeListener=function(listener){
for(var i=0;i<this._listeners.length;i++){
if(this._listeners[i]==listener){
this._listeners.splice(i,1);
break;
}
}
};

Timeline.DefaultEventSource.prototype.loadXML=function(xml,url){
var base=this._getBaseURL(url);

var wikiURL=xml.documentElement.getAttribute("wiki-url");
var wikiSection=xml.documentElement.getAttribute("wiki-section");

var dateTimeFormat=xml.documentElement.getAttribute("date-time-format");
var parseDateTimeFunction=this._events.getUnit().getParser(dateTimeFormat);

var node=xml.documentElement.firstChild;
var added=false;
while(node!=null){
if(node.nodeType==1){
var description="";
if(node.firstChild!=null&&node.firstChild.nodeType==3){
description=node.firstChild.nodeValue;
}
var evt=new Timeline.DefaultEventSource.Event(
parseDateTimeFunction(node.getAttribute("start")),
parseDateTimeFunction(node.getAttribute("end")),
parseDateTimeFunction(node.getAttribute("latestStart")),
parseDateTimeFunction(node.getAttribute("earliestEnd")),
node.getAttribute("isDuration")!="true",
node.getAttribute("title"),
description,
this._resolveRelativeURL(node.getAttribute("image"),base),
this._resolveRelativeURL(node.getAttribute("link"),base),
this._resolveRelativeURL(node.getAttribute("icon"),base),
node.getAttribute("color"),
node.getAttribute("textColor")
);
evt._node=node;
evt.getProperty=function(name){
return this._node.getAttribute(name);
};
evt.setWikiInfo(wikiURL,wikiSection);

this._events.add(evt);

added=true;
}
node=node.nextSibling;
}

if(added){
this._fire("onAddMany",[]);
}
};


Timeline.DefaultEventSource.prototype.loadJSON=function(data,url){
var base=this._getBaseURL(url);
var added=false;
if(data&&data.events){
var wikiURL=("wikiURL"in data)?data.wikiURL:null;
var wikiSection=("wikiSection"in data)?data.wikiSection:null;

var dateTimeFormat=("dateTimeFormat"in data)?data.dateTimeFormat:null;
var parseDateTimeFunction=this._events.getUnit().getParser(dateTimeFormat);

for(var i=0;i<data.events.length;i++){
var event=data.events[i];
var evt=new Timeline.DefaultEventSource.Event(
parseDateTimeFunction(event.start),
parseDateTimeFunction(event.end),
parseDateTimeFunction(event.latestStart),
parseDateTimeFunction(event.earliestEnd),
event.isDuration||false,
event.title,
event.description,
this._resolveRelativeURL(event.image,base),
this._resolveRelativeURL(event.link,base),
this._resolveRelativeURL(event.icon,base),
event.color,
event.textColor
);
evt._obj=event;
evt.getProperty=function(name){
return this._obj[name];
};
evt.setWikiInfo(wikiURL,wikiSection);

this._events.add(evt);
added=true;
}
}

if(added){
this._fire("onAddMany",[]);
}
};


Timeline.DefaultEventSource.prototype.loadSPARQL=function(xml,url){
var base=this._getBaseURL(url);

var dateTimeFormat='iso8601';
var parseDateTimeFunction=this._events.getUnit().getParser(dateTimeFormat);

if(xml==null){
return;
}


var node=xml.documentElement.firstChild;
while(node!=null&&(node.nodeType!=1||node.nodeName!='results')){
node=node.nextSibling;
}

var wikiURL=null;
var wikiSection=null;
if(node!=null){
wikiURL=node.getAttribute("wiki-url");
wikiSection=node.getAttribute("wiki-section");

node=node.firstChild;
}

var added=false;
while(node!=null){
if(node.nodeType==1){
var bindings={};
var binding=node.firstChild;
while(binding!=null){
if(binding.nodeType==1&&
binding.firstChild!=null&&
binding.firstChild.nodeType==1&&
binding.firstChild.firstChild!=null&&
binding.firstChild.firstChild.nodeType==3){
bindings[binding.getAttribute('name')]=binding.firstChild.firstChild.nodeValue;
}
binding=binding.nextSibling;
}

if(bindings["start"]==null&&bindings["date"]!=null){
bindings["start"]=bindings["date"];
}

var evt=new Timeline.DefaultEventSource.Event(
parseDateTimeFunction(bindings["start"]),
parseDateTimeFunction(bindings["end"]),
parseDateTimeFunction(bindings["latestStart"]),
parseDateTimeFunction(bindings["earliestEnd"]),
bindings["isDuration"]!="true",
bindings["title"],
bindings["description"],
this._resolveRelativeURL(bindings["image"],base),
this._resolveRelativeURL(bindings["link"],base),
this._resolveRelativeURL(bindings["icon"],base),
bindings["color"],
bindings["textColor"]
);
evt._bindings=bindings;
evt.getProperty=function(name){
return this._bindings[name];
};
evt.setWikiInfo(wikiURL,wikiSection);

this._events.add(evt);
added=true;
}
node=node.nextSibling;
}

if(added){
this._fire("onAddMany",[]);
}
};

Timeline.DefaultEventSource.prototype.add=function(evt){
this._events.add(evt);
this._fire("onAddOne",[evt]);
};

Timeline.DefaultEventSource.prototype.addMany=function(events){
for(var i=0;i<events.length;i++){
this._events.add(events[i]);
}
this._fire("onAddMany",[]);
};

Timeline.DefaultEventSource.prototype.clear=function(){
this._events.removeAll();
this._fire("onClear",[]);
};

Timeline.DefaultEventSource.prototype.getEventIterator=function(startDate,endDate){
return this._events.getIterator(startDate,endDate);
};

Timeline.DefaultEventSource.prototype.getAllEventIterator=function(){
return this._events.getAllIterator();
};

Timeline.DefaultEventSource.prototype.getCount=function(){
return this._events.getCount();
};

Timeline.DefaultEventSource.prototype.getEarliestDate=function(){
return this._events.getEarliestDate();
};

Timeline.DefaultEventSource.prototype.getLatestDate=function(){
return this._events.getLatestDate();
};

Timeline.DefaultEventSource.prototype._fire=function(handlerName,args){
for(var i=0;i<this._listeners.length;i++){
var listener=this._listeners[i];
if(handlerName in listener){
try{
listener[handlerName].apply(listener,args);
}catch(e){
Timeline.Debug.exception(e);
}
}
}
};

Timeline.DefaultEventSource.prototype._getBaseURL=function(url){
if(url.indexOf("://")<0){
var url2=this._getBaseURL(document.location.href);
if(url.substr(0,1)=="/"){
url=url2.substr(0,url2.indexOf("/",url2.indexOf("://")+3))+url;
}else{
url=url2+url;
}
}

var i=url.lastIndexOf("/");
if(i<0){
return"";
}else{
return url.substr(0,i+1);
}
};

Timeline.DefaultEventSource.prototype._resolveRelativeURL=function(url,base){
if(url==null||url==""){
return url;
}else if(url.indexOf("://")>0){
return url;
}else if(url.substr(0,1)=="/"){
return base.substr(0,base.indexOf("/",base.indexOf("://")+3))+url;
}else{
return base+url;
}
};


Timeline.DefaultEventSource.Event=function(
start,end,latestStart,earliestEnd,instant,
text,description,image,link,
icon,color,textColor){

this._id="e"+Math.floor(Math.random()*1000000);

this._instant=instant||(end==null);

this._start=start;
this._end=(end!=null)?end:start;

this._latestStart=(latestStart!=null)?latestStart:(instant?this._end:this._start);
this._earliestEnd=(earliestEnd!=null)?earliestEnd:(instant?this._start:this._end);

this._text=Timeline.HTML.deEntify(text);
this._description=Timeline.HTML.deEntify(description);
this._image=(image!=null&&image!="")?image:null;
this._link=(link!=null&&link!="")?link:null;

this._icon=(icon!=null&&icon!="")?icon:null;
this._color=(color!=null&&color!="")?color:null;
this._textColor=(textColor!=null&&textColor!="")?textColor:null;

this._wikiURL=null;
this._wikiSection=null;
};

Timeline.DefaultEventSource.Event.prototype={
getID:function(){return this._id;},

isInstant:function(){return this._instant;},
isImprecise:function(){return this._start!=this._latestStart||this._end!=this._earliestEnd;},

getStart:function(){return this._start;},
getEnd:function(){return this._end;},
getLatestStart:function(){return this._latestStart;},
getEarliestEnd:function(){return this._earliestEnd;},

getText:function(){return this._text;},
getDescription:function(){return this._description;},
getImage:function(){return this._image;},
getLink:function(){return this._link;},

getIcon:function(){return this._icon;},
getColor:function(){return this._color;},
getTextColor:function(){return this._textColor;},

getProperty:function(name){return null;},

getWikiURL:function(){return this._wikiURL;},
getWikiSection:function(){return this._wikiSection;},
setWikiInfo:function(wikiURL,wikiSection){
this._wikiURL=wikiURL;
this._wikiSection=wikiSection;
},

fillDescription:function(elmt){
elmt.innerHTML=this._description;
},
fillWikiInfo:function(elmt){
if(this._wikiURL!=null&&this._wikiSection!=null){
var wikiID=this.getProperty("wikiID");
if(wikiID==null||wikiID.length==0){
wikiID=this.getText();
}
wikiID=wikiID.replace(/\s/g,"_");

var url=this._wikiURL+this._wikiSection.replace(/\s/g,"_")+"/"+wikiID;
var a=document.createElement("a");
a.href=url;
a.target="new";
a.innerHTML=Timeline.strings[Timeline.Platform.clientLocale].wikiLinkLabel;

elmt.appendChild(document.createTextNode("["));
elmt.appendChild(a);
elmt.appendChild(document.createTextNode("]"));
}else{
elmt.style.display="none";
}
},
fillTime:function(elmt,labeller){
if(this._instant){
if(this.isImprecise()){
elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
elmt.appendChild(elmt.ownerDocument.createElement("br"));
elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._end)));
}else{
elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
}
}else{
if(this.isImprecise()){
elmt.appendChild(elmt.ownerDocument.createTextNode(
labeller.labelPrecise(this._start)+" ~ "+labeller.labelPrecise(this._latestStart)));
elmt.appendChild(elmt.ownerDocument.createElement("br"));
elmt.appendChild(elmt.ownerDocument.createTextNode(
labeller.labelPrecise(this._earliestEnd)+" ~ "+labeller.labelPrecise(this._end)));
}else{
elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._start)));
elmt.appendChild(elmt.ownerDocument.createElement("br"));
elmt.appendChild(elmt.ownerDocument.createTextNode(labeller.labelPrecise(this._end)));
}
}
},
fillInfoBubble:function(elmt,theme,labeller){
var doc=elmt.ownerDocument;

var title=this.getText();
var link=this.getLink();
var image=this.getImage();

if(image!=null){
var img=doc.createElement("img");
img.src=image;

theme.event.bubble.imageStyler(img);
elmt.appendChild(img);
}

var divTitle=doc.createElement("div");
var textTitle=doc.createTextNode(title);
if(link!=null){
var a=doc.createElement("a");
a.href=link;
a.appendChild(textTitle);
divTitle.appendChild(a);
}else{
divTitle.appendChild(textTitle);
}
theme.event.bubble.titleStyler(divTitle);
elmt.appendChild(divTitle);

var divBody=doc.createElement("div");
this.fillDescription(divBody);
theme.event.bubble.bodyStyler(divBody);
elmt.appendChild(divBody);

var divTime=doc.createElement("div");
this.fillTime(divTime,labeller);
theme.event.bubble.timeStyler(divTime);
elmt.appendChild(divTime);

var divWiki=doc.createElement("div");
this.fillWikiInfo(divWiki);
theme.event.bubble.wikiStyler(divWiki);
elmt.appendChild(divWiki);
}
};

/* themes.js */




Timeline.ClassicTheme=new Object();

Timeline.ClassicTheme.implementations=[];

Timeline.ClassicTheme.create=function(locale){
if(locale==null){
locale=Timeline.Platform.getDefaultLocale();
}

var f=Timeline.ClassicTheme.implementations[locale];
if(f==null){
f=Timeline.ClassicTheme._Impl;
}
return new f();
};

Timeline.ClassicTheme._Impl=function(){
this.firstDayOfWeek=0;

this.ether={
backgroundColors:[
"#EEE",
"#DDD",
"#CCC",
"#AAA"
],
highlightColor:"white",
highlightOpacity:50,
interval:{
line:{
show:true,
color:"#aaa",
opacity:25
},
weekend:{
color:"#FFFFE0",
opacity:30
},
marker:{
hAlign:"Bottom",
hBottomStyler:function(elmt){
elmt.className="timeline-ether-marker-bottom";
},
hBottomEmphasizedStyler:function(elmt){
elmt.className="timeline-ether-marker-bottom-emphasized";
},
hTopStyler:function(elmt){
elmt.className="timeline-ether-marker-top";
},
hTopEmphasizedStyler:function(elmt){
elmt.className="timeline-ether-marker-top-emphasized";
},

vAlign:"Right",
vRightStyler:function(elmt){
elmt.className="timeline-ether-marker-right";
},
vRightEmphasizedStyler:function(elmt){
elmt.className="timeline-ether-marker-right-emphasized";
},
vLeftStyler:function(elmt){
elmt.className="timeline-ether-marker-left";
},
vLeftEmphasizedStyler:function(elmt){
elmt.className="timeline-ether-marker-left-emphasized";
}
}
}
};

this.event={
track:{
offset:0.5,
height:1.5,
gap:0.5
},
instant:{
icon:Timeline.urlPrefix+"images/dull-blue-circle.png",
lineColor:"#58A0DC",
impreciseColor:"#58A0DC",
impreciseOpacity:20,
showLineForNoText:true
},
duration:{
color:"#58A0DC",
opacity:100,
impreciseColor:"#58A0DC",
impreciseOpacity:20
},
label:{
insideColor:"white",
outsideColor:"black",
width:200
},
highlightColors:[
"#FFFF00",
"#FFC000",
"#FF0000",
"#0000FF"
],
bubble:{
width:250,
height:125,
titleStyler:function(elmt){
elmt.className="timeline-event-bubble-title";
},
bodyStyler:function(elmt){
elmt.className="timeline-event-bubble-body";
},
imageStyler:function(elmt){
elmt.className="timeline-event-bubble-image";
},
wikiStyler:function(elmt){
elmt.className="timeline-event-bubble-wiki";
},
timeStyler:function(elmt){
elmt.className="timeline-event-bubble-time";
}
}
};
};

/* units.js */





Timeline.NativeDateUnit=new Object();

Timeline.NativeDateUnit.createLabeller=function(locale,timeZone){
return new Timeline.GregorianDateLabeller(locale,timeZone);
};


Timeline.NativeDateUnit.makeDefaultValue=function(){
return new Date();
};

Timeline.NativeDateUnit.cloneValue=function(v){
return new Date(v.getTime());
};

Timeline.NativeDateUnit.getParser=function(format){
if(typeof format=="string"){
format=format.toLowerCase();
}
return(format=="iso8601"||format=="iso 8601")?

Timeline.DateTime.parseIso8601DateTime:

Timeline.DateTime.parseGregorianDateTime;

};

Timeline.NativeDateUnit.parseFromObject=function(o){
return Timeline.DateTime.parseGregorianDateTime(o);
};


Timeline.NativeDateUnit.toNumber=function(v){
return v.getTime();
};

Timeline.NativeDateUnit.fromNumber=function(n){
return new Date(n);
};

Timeline.NativeDateUnit.compare=function(v1,v2){
var n1,n2;
if(typeof v1=="object"){
n1=v1.getTime();
}else{
n1=Number(v1);
}
if(typeof v2=="object"){
n2=v2.getTime();
}else{
n2=Number(v2);
}

return n1-n2;
};

Timeline.NativeDateUnit.earlier=function(v1,v2){
return Timeline.NativeDateUnit.compare(v1,v2)<0?v1:v2;
};

Timeline.NativeDateUnit.later=function(v1,v2){
return Timeline.NativeDateUnit.compare(v1,v2)>0?v1:v2;
};

Timeline.NativeDateUnit.change=function(v,n){
return new Date(v.getTime()+n);
};

