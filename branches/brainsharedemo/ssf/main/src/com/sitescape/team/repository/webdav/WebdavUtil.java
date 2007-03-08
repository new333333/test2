package com.sitescape.team.repository.webdav;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.apache.webdav.lib.ResponseEntity;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.methods.DepthSupport;

import com.sitescape.team.util.Constants;


public class WebdavUtil {

	/**
	 * Dump to the console the directory hierarchy at the specified resource.
	 * Used for debugging only.
	 *  
	 * @param wdr
	 * @throws HttpException
	 * @throws IOException
	 */
	public static void dumpHierarchy(WebdavResource wdr) throws HttpException, IOException {
		String savedPath = wdr.getPath(); 	// Save path
		dumpHierarchyInternal(wdr, Constants.TAB, 0);
		wdr.setPath(savedPath);				// Restore path
	}
	
	/**
	 * Dump to the console all properties of the resource at the specified path. 
	 * Used for debugging only. 
	 * 
	 * @param wdr
	 * @param path
	 * @throws HttpException
	 * @throws IOException
	 */
	public static void dumpAllProps(WebdavResource wdr, String path) throws HttpException, IOException{
		System.out.println("*** Webdav Properties for " + path);
		Enumeration e = wdr.propfindMethod(path, DepthSupport.DEPTH_0);
		while(e.hasMoreElements()) {
			Object o = e.nextElement();
			ResponseEntity re = (ResponseEntity) o;
			System.out.println("<Properties>");
			dumpProperties(re.getProperties());
			System.out.println("<Histories>");
			dumpProperties(re.getHistories());
			System.out.println("<Workspaces>");
			dumpProperties(re.getWorkspaces());	
		}
	}
	
	/**
	 * Dump to the console the value of the specified property of the resource 
	 * at the path. Used for debugging only.
	 * 
	 * @param wdr
	 * @param path
	 * @param propertyName
	 * @throws HttpException
	 * @throws IOException
	 */
	public static void dumpProp(WebdavResource wdr, String path, String propertyName) throws HttpException, IOException {
		System.out.println("*** Webdav Property [" + propertyName + "] for path [" + path + "]");
		Enumeration e = wdr.propfindMethod(path, propertyName);
		while(e.hasMoreElements()) {
			Object element = e.nextElement();
			System.out.println("element: [" + element + "]");
		}
	}
	
	public static String getSingleStringValue(WebdavResource wdr, String path, String propertyName)
		throws  HttpException, IOException {
		String value = null;
		Enumeration e = wdr.propfindMethod(path, propertyName);
		if(e.hasMoreElements())
			value = (String) e.nextElement();
		return value;
	}
	
	public static String getSingleHrefValue(WebdavResource wdr, String path, String propertyName)
		throws  HttpException, IOException {
		String value = getSingleStringValue(wdr, path, propertyName);
		if(value != null)
			return value.substring(value.indexOf(">") + 1, value.indexOf("</"));
		else 
			return null;
	}

	public static List<String> getHrefValues(WebdavResource wdr, String path, String propertyName)
		throws  HttpException, IOException {
		List<String> list = new ArrayList<String>();
		String value = getSingleStringValue(wdr, path, propertyName);

		if(value != null) {
			int chunkStartIndex = 0;
			int rabIndex,labIndex;
			String href;
			while(chunkStartIndex < value.length()) {
				rabIndex = value.indexOf(">", chunkStartIndex); 
				labIndex = value.indexOf("</", rabIndex+1);
				href = value.substring(rabIndex + 1, labIndex);
				list.add(href);
				chunkStartIndex = value.indexOf(">", labIndex + 1) + 1;
			}
		}
		
		return list;
	}
	
	public static List<String> getVersionNames(WebdavResource wdr, String httpUrl)
		throws HttpException, URIException, IOException {
		Vector properties = new Vector();
		properties.add("version-name");
		Enumeration e = wdr.reportMethod(new HttpURL(httpUrl),properties);
		Map<String,String> map = new HashMap<String,String>();
		while(e.hasMoreElements()) {
			ResponseEntity re = (ResponseEntity) e.nextElement();
			String href = re.getHref();
			for(Enumeration e2 = re.getProperties(); e2.hasMoreElements();) {
				Property p = (Property) e2.nextElement();
				if(p.getLocalName().equals("version-name")) {
					map.put(href, p.getPropertyAsString());
					break;
				}
			}
		}
		return new ArrayList<String>(map.values());
	}
	
	public static boolean exists(WebdavResource wdr, String resourcePath) 
		throws HttpException, IOException {
		return wdr.headMethod(resourcePath);
	}
	
	private static String getHrefValue(String href) {
		return href.substring(href.indexOf(">") + 1, href.indexOf("</"));
	}
	
	private static void dumpProperties(Enumeration e) {
		while(e.hasMoreElements()) {
			Property p = (Property) e.nextElement();
			System.out.println(p.getName() + "=" + p.getPropertyAsString());
		}
	}
	
	public static void createCollectionIfNecessary(WebdavResource wdr, String collPath) throws HttpException, IOException {
		// This can be very inefficient if the directory depth is large...
		// I wonder why WebDAV protocol does not support recursive creation 
		// in the first place, which would be much more efficient...
		createCollectionRecursivelyIfNecessary(wdr, collPath);
	}
	
	private static void createCollectionRecursivelyIfNecessary(WebdavResource wdr, String collPath) throws HttpException, IOException {
		if(wdr.headMethod(collPath)) {
			// The collection already exists. 
			return;
		}
		
		String parentPath = getParentPath(collPath);
		
		if(parentPath != null)
			createCollectionRecursivelyIfNecessary(wdr, parentPath);
		
		boolean result = wdr.mkcolMethod(collPath);
	}
	
	/**
	 * Returns the path string of this path's parent, or <code>null</code>
	 * if this path does not name a parent directory. 
	 * 
	 * @param path
	 * @return
	 */
	public static String getParentPath(String path) {
		if(path == null || path.length() < 2)
			return null;
		
		String sub = path.substring(0, path.length()-1);
		int idx = sub.lastIndexOf(Constants.SLASH);
		if(idx < 0)
			return null;
		else
			return sub.substring(0, idx+1);
	}
	
	private static void dumpHierarchyInternal(WebdavResource wdr, String tab, int indent) 
		throws HttpException, IOException {
		StringBuffer sb = new StringBuffer();
		buildIndent(sb, tab, indent);
		if(wdr.isCollection()) {
			String path = wdr.getPath();
			sb.append("[COLLECTION [").append(wdr.getName()).append("][").
				append(path).append("]]");
			System.out.println(sb.toString());
			String[] children = wdr.list();
			for(int i = 0; i < children.length; i++) {
				wdr.setPath(path + ((path.endsWith(Constants.SLASH))? "" : Constants.SLASH) + children[i]);
				dumpHierarchyInternal(wdr, tab, indent+1);
			}
		}
		else {
			sb.append("[LEAF [").append(wdr.getName()).append("][").
				append(wdr.getPath()).append("]]");
			System.out.println(sb.toString());
		}
	}
	
	private static void buildIndent(StringBuffer sb, String tab, int indent) {
		for(int i = 0; i < indent; i++) {
			sb.append(tab);
		}
	}
}
