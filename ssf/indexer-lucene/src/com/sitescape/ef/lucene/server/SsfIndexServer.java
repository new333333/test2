package com.sitescape.ef.lucene.server;

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

/**
 * Title: SsfIndexServer
 * Description: This is the primary routine which starts up an indexserver.
 * Copyright:    Copyright (c) 200
 * Company:
 * @author Klein
 * @version 1.0
 */

public class SsfIndexServer {

    public static void main(String[] args) throws Exception {
        //if(System.getSecurityManager() == null) {
        //  System.setSecurityManager( new RMISecurityManager() );
        //}
        //System.setSecurityManager (new RMISecurityManager());
        SsfIndexer indexServer = new SsfIndexer("SSFINDEXER");
        System.out.println( "RMI SsfIndexServer ready..." );
    }
}

