1. Build the sample and deploy the jar file containing the additional web
services operation into the ICEcore installation by running "ant deploy" 
from the command line.

2. Edit <icecore installation>/webapps/ssf/WEB-INF/server-config.wsdd and
replace com.sitescape.team.remoting.ws.JaxRpcFacade with 
com.sitescape.team.samples.extendedws.server.JaxRpcFacade2.

3. Edit <icecore installation>/webapps/ssf/WEB-INF/context/applicationContext.xml
and replace com.sitescape.team.remoting.ws.FacadeImpl with
com.sitescape.team.samples.extendedws.server.FacadeImpl2.
Also replace com.sitescape.team.remoting.Facade with 
com.sitescape.team.samples.extendedws.server.Facade2.

4. Restart ICEcore server.

5. To test the newly added WS operation, go to samples/remotingclient, execute
"ant zip", and run "wsclient.bat getBinderTitle 1" (on Windows). 
It runs a sample client program that invokes the getBinderTitle WS operation
that was built and deployed in the step 1 above.

You can also point your browser to http://<icecore host>:<port>/ssf/ws/Facade?wsdl
to make sure that the newly added WS operation is exported through WSDL.
