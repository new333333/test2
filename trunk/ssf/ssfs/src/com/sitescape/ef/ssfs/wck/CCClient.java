package com.sitescape.ef.ssfs.wck;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;

public class CCClient {

	private static final CCClientCallback defaultCallback = new DefaultCCClientCallback();
	
	private String zoneName;
	private String userName;
	private Map<String,Object> cache;
	
	public CCClient(String zoneName, String userName) {
		this.zoneName = zoneName;
		this.userName = userName;
		// This cache is kept only for the duration of the lifetime of
		// this object instance, hence short-lived (relatively speaking).
		this.cache = new HashMap<String,Object>();
	}
	
	public boolean objectExists(String resourceUri, Map uri) throws CCClientException,
	NoAccessException {
		try {
			getPropertiesCached(resourceUri, uri);
			// If still here, the object exists.
			return true;
		}
		catch(NoSuchObjectException e) {
			// This exception indicates that the specified resource does not exist.
			return false;
		}
		
		/*
		Boolean returnObj = (Boolean) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_OBJECT_EXISTS, 
				defaultCallback);
		return returnObj.booleanValue();
		*/
	}
	
	public void createResource(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, AlreadyExistsException {
		CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_CREATE_RESOURCE, 
				defaultCallback);
	}
	
	public void createAndSetResource(String resourceUri, Map uri, final InputStream content) 
	throws CCClientException, NoAccessException, AlreadyExistsException {
		CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_CREATE_SET_RESOURCE, 
				new CCClientCallback() {
					public void additionalInput(HttpServletRequest req, Map m) {
						req.setAttribute(CrossContextConstants.INPUT_STREAM, content);
					}
				}
			);
	}
	
	public void setResource(String resourceUri, Map uri, final InputStream content) 
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
	
	public InputStream getResource(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		InputStream returnObj = (InputStream) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_RESOURCE, 
				defaultCallback);
		return returnObj;
	}
	
	public long getResourceLength(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Map props = getPropertiesCached(resourceUri, uri);
		
		Long length = (Long) props.get(CrossContextConstants.DAV_PROPERTIES_GET_CONTENT_LENGTH);
		
		return length.longValue();
		
		/*
		Long returnObj = (Long) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_RESOURCE_LENGTH, 
				defaultCallback);
		return returnObj.longValue();
		*/
	}
	
	public void removeResource(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		CCExecutionTemplate.execute(
				zoneName, userName, uri, CrossContextConstants.OPERATION_REMOVE_RESOURCE, 
				defaultCallback);		
	}
	
	public Date getLastModified(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Map props = getPropertiesCached(resourceUri, uri);

		return (Date) props.get(CrossContextConstants.DAV_PROPERTIES_GET_LAST_MODIFIED);

		/*
		Date returnObj = (Date) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_LAST_MODIFIED, 
				defaultCallback);
		return returnObj;
		*/
	}
	
	public Date getCreationDate(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Map props = getPropertiesCached(resourceUri, uri);

		return (Date) props.get(CrossContextConstants.DAV_PROPERTIES_CREATION_DATE);

		/*
		Date returnObj = (Date) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_CREATION_DATE, 
				defaultCallback);
		return returnObj;
		*/
	}
	
	public String[] getChildrenNames(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		String[] returnObj = (String[]) CCExecutionTemplate.execute(
				zoneName, userName, uri, 
				CrossContextConstants.OPERATION_GET_CHILDREN_NAMES, 
				defaultCallback);
		return returnObj;
	}
	
	public Map getProperties(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Map props = getPropertiesCached(resourceUri, uri);

		// Implementation Note:
		// Returns only a subset of the entries where the value is of String
		// type. This is because WCK allows only String values for properties
		// (see WebdavStoreAdapter.retrieveRevisionDescriptor method for details).
		// We may need to rework this restriction to support PROPFIND command. 
		// We shall see. 
		Map<String,String> result = new HashMap<String,String>();
		for(Iterator it = props.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Object val = entry.getValue();
			if(val instanceof String)
				result.put(key, (String) val);
		}

		return result;
	}
	
	private Map getPropertiesCached(String resourceUri, Map uri) throws CCClientException,
	NoAccessException, NoSuchObjectException {
		Object value = cache.get(resourceUri);
		
		if(value == null) {
			// Request never made for the uri. 
			try {
				Map props = (Map) CCExecutionTemplate.execute(
						zoneName, userName, uri, 
						CrossContextConstants.OPERATION_GET_PROPERTIES, 
						defaultCallback);
				cache.put(resourceUri, props);
				return props;
			}
			catch(NoAccessException e) {
				cache.put(resourceUri, e);
				throw e;
			}
			catch(NoSuchObjectException e) {
				cache.put(resourceUri, e);
				throw e;
			}
		}
		else if(value instanceof Map) {
			// Previous request was successful
			return (Map) value;
		}
		else if(value instanceof NoAccessException) {
			// Previous request threw NoAccessException.
			throw (NoAccessException) value; // Rethrow it
		}
		else if(value instanceof NoSuchObjectException) {
			// Previous request threw NoSuchObjectException.
			throw (NoSuchObjectException) value; // Rethrow it
		}
		else {
			// This should never happen.
			throw new CCClientException("Invalid cache entry type: " + value.getClass());
		}	
	}
	
	static class DefaultCCClientCallback implements CCClientCallback {
		public void additionalInput(HttpServletRequest req, Map m) {
		}
	}
}
