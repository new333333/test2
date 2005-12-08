package com.sitescape.ef.repository.webdav;

import java.io.IOException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.methods.CheckinMethod;

public class SWebdavResource extends WebdavResource {

    public SWebdavResource(HttpURL httpURL, Credentials credentials, int action,
            int depth) throws HttpException, IOException {
    	super(httpURL, credentials, action, depth);
    }
    
    public SWebdavResource(HttpURL httpURL, int action, int depth)
    throws HttpException, IOException {
    	super(httpURL, action, depth);
    }
    
    public SWebdavResource(HttpURL httpURL, int depth)
    throws HttpException, IOException {
    	super(httpURL, depth);
    }
    
    public SWebdavResource(HttpURL httpURL, int depth, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, depth, followRedirects);
    }
    
    public SWebdavResource(HttpURL httpURL)
    throws HttpException, IOException {
    	super(httpURL);
    }
    
    public SWebdavResource(HttpURL httpURL, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, followRedirects);
    }
    
    public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort)
    throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort);
    }
    
    public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort, followRedirects);
    }
    
    public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort,
            Credentials proxyCredentials)
    throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort, proxyCredentials);
	}
    
	public SWebdavResource(HttpURL httpURL, String proxyHost, int proxyPort,
        Credentials proxyCredentials, boolean followRedirects)
      throws HttpException, IOException {
    	super(httpURL, proxyHost, proxyPort, proxyCredentials, followRedirects);		
	}
	
    public SWebdavResource(String escapedHttpURL)
    throws HttpException, IOException {
    	super(escapedHttpURL);
    }
    
    public SWebdavResource(String escapedHttpURL, boolean followRedirects)
    throws HttpException, IOException {
    	super(escapedHttpURL, followRedirects);
    }
    
    public SWebdavResource(String escapedHttpURL, Credentials credentials)
    throws HttpException, IOException {
    	super(escapedHttpURL, credentials);
    }
    
    public SWebdavResource(String escapedHttpURL, Credentials credentials,
            boolean followRedirects)
         throws HttpException, IOException {
    	super(escapedHttpURL, credentials, followRedirects);
    }
    
    public SWebdavResource(String escapedHttpURL, String proxyHost,
            int proxyPort) throws HttpException, IOException {
    	super(escapedHttpURL, proxyHost, proxyPort);
    }
    
    public SWebdavResource(String escapedHttpURL, String proxyHost,
            int proxyPort, Credentials proxyCredentials)
    throws HttpException, IOException {
    	super(escapedHttpURL, proxyHost, proxyPort, proxyCredentials);
    }
    
    public SWebdavResource(HttpURL httpURL, String additionalPath)
    throws HttpException, IOException {
    	super(httpURL, additionalPath);
    }
    
    public SWebdavResource(HttpURL httpURL, String additionalPath, boolean followRedirects)
    throws HttpException, IOException {
    	super(httpURL, additionalPath, followRedirects);
    }
	
    /**
     * Execute the CHECKIN method for the given path. 
     * 
     * @param path the server relative path of the resource to check in
     * @return the value of the Location response header if the method is
     * succeeded. Returns <code>null</code> otherwise. 
     * @throws HttpException
     * @throws IOException
     */
    public String checkin(String path) throws HttpException, IOException {

        setClient();
        CheckinMethod method = new CheckinMethod(URIUtil.encodePath(path));
        method.setDebug(debug);
        method.setFollowRedirects(this.followRedirects);

        generateIfHeader(method);
        generateTransactionHeader(method);
        int statusCode = client.executeMethod(method);

        setStatusCode(statusCode);

        boolean boolResult = (statusCode >= 200 && statusCode < 300) ? true : false;
	
        String location = null;
        
        if(boolResult) {
        	location = method.getResponseHeader("Location").getValue();
        }
        
        return location;
    }

}
