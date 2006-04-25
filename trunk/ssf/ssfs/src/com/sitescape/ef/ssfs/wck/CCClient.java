package com.sitescape.ef.ssfs.wck;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;

public class CCClient {

	private static final CCClientCallback defaultCallback = new DefaultCCClientCallback();
	
	public static boolean objectExists(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException {
		Boolean returnObj = (Boolean) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_OBJECT_EXISTS, 
				defaultCallback);
		return returnObj.booleanValue();
	}
	
	public static void createResource(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, AlreadyExistsException {
		CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_CREATE_RESOURCE, 
				defaultCallback);
	}
	
	public static void setResource(String zoneName, String userName, Map uri, final InputStream content) 
	throws CCClientException, NoAccessException, NoSuchObjectException {	
		CCExecutionTemplate.execute(zoneName, userName, uri, 
				CrossContextConstants.OPERATION_SET_RESOURCE, 
			new CCClientCallback() {
				public void additionalInput(HttpServletRequest req, Map m) {
					req.setAttribute(CrossContextConstants.INPUT_STREAM, content);
				}
			}
		);
	}
	
	public static InputStream getResource(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		InputStream returnObj = (InputStream) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_RESOURCE, 
				defaultCallback);
		return returnObj;
	}
	
	public static long getResourceLength(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Long returnObj = (Long) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_RESOURCE_LENGTH, 
				defaultCallback);
		return returnObj.longValue();
	}
	
	public static void removeResource(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		CCExecutionTemplate.execute(
				zoneName, userName, uri, CrossContextConstants.OPERATION_REMOVE_RESOURCE, 
				defaultCallback);		
	}
	
	public static Date getLastModified(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Date returnObj = (Date) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_LAST_MODIFIED, 
				defaultCallback);
		return returnObj;
	}
	
	public static Date getCreationDate(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Date returnObj = (Date) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_CREATION_DATE, 
				defaultCallback);
		return returnObj;
	}
	
	public static String[] getChildrenNames(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		String[] returnObj = (String[]) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_CHILDREN_NAMES, 
				defaultCallback);
		return returnObj;
	}
	
	public static Map getProperties(String zoneName, String userName, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Map returnObj = (Map) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_PROPERTIES, 
				defaultCallback);
		return returnObj;
	}
	
	static class DefaultCCClientCallback implements CCClientCallback {
		public void additionalInput(HttpServletRequest req, Map m) {
		}
	}
}
