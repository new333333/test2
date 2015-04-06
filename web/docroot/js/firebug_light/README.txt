Firebug light 1.2:
-----------------

http://getfirebug.com/lite.html

- - - - -

Installing Firebug Lite 1.2

Insert this line of code into any page that you want to contain Firebug
lite:

    <script type='text/javascript' 
        src='http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js'></script>

- - - - -

Firebug Lite as bookmarklet

Create following link a bookmark in your toolbar and use Firebug Lite on any page:

javascript:var%20firebug=document.createElement('script');firebug.setAttribute('src','http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js');document.body.appendChild(firebug);(function(){if(window.firebug.version){firebug.init();}else{setTimeout(arguments.callee);}})();void(firebug);

- - - - -

Configuring Firebug Lite

Height of the firebug lite form is resizeable in latest version;

  <script type="text/javascript">
  firebug.env.height = 500;
  </script>
            

Also, developers can use their own css file;

  <script type="text/javascript">
  firebug.env.css = "/myown/firebug.css"
  </script>
            
- - - - -

Using Firebug Lite Offline

Download the source

Import firebug-lite-compressed.js or firebug-lite.js into your site's
directory. Find "firebug-lite.css" URL on the javascript file which you
imported and replace this with offline address of firebug-lite.css
file.

    <script language="javascript" type="text/javascript" 
        src="/path/to/firebug/firebug-lite.js"></script>

Note:  For use with Teaming, these .js files can be found in:

    ssf/web/docroot/js/firebug_light
    
- - - - -

Commands

Now FBLite supports all basic commands of Firebug.

firebug.watchXHR: Use this function to watch the status of
XmlHttpRequest objects.

    var req = new XmlHttpRequest;
    firebug.watchXHR(req);

firebug.inspect: Now elements can be inspected in javascript code:

    firebug.inspect(document.body.firstChild);
            

- - - - -

Notes

On some browsers, the console object is already declared. If you
observe errors in Safari, for instance, use the console commands in
this fashion:

    firebug.d.console.cmd.log("test");
    firebug.d.console.cmd.dir([ "test" ]);

Firebug Lite creates the variables "firebug" and doesn't affect or
interfere HTML elements that aren't create by itself.
