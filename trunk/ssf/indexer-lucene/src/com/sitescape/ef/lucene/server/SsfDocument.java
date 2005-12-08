package com.sitescape.ef.lucene.server;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:
 * @author
 * @version 1.0
 */

import java.io.Serializable;
import java.rmi.*;
import org.apache.lucene.document.Document;


public class SsfDocument implements Serializable {

    public  String indexName;
    public  Document document;
    private String UID;

    /**
     *
     */
    public void SSFDocument(Document doc) throws RemoteException {
            document = doc;
    }

    public void setDocument(Document doc) {document = doc;}
    public Document getDocument() {return document;}

    public void setName(String name) { indexName = name ;}
    public String getName() {return indexName;}

    public void setUID(String uid) {UID = uid;}
    public String getUID() {return UID;}

}
