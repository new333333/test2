package com.sitescape.ef.ssfs.wck;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import com.sitescape.ef.ssfs.AlreadyExistsException;
import com.sitescape.ef.ssfs.CrossContextConstants;
import com.sitescape.ef.ssfs.NoAccessException;
import com.sitescape.ef.ssfs.NoSuchObjectException;
import com.sitescape.ef.ssfs.web.crosscontext.DispatchClient;
import com.sitescape.ef.web.util.AttributesAndParamsOnlyServletRequest;
import com.sitescape.ef.web.util.NullServletResponse;

public class CCExecutionTemplate {

	public static Object execute(String zoneName, String userName, Map uri, 
			Integer operationName, CCClientCallback action) 
	throws AlreadyExistsException, CCClientException, NoAccessException, 
	NoSuchObjectException {
		AttributesAndParamsOnlyServletRequest req = 
			new AttributesAndParamsOnlyServletRequest(Constants.CONTEXT_PATH);

		req.setAttribute(CrossContextConstants.ZONE_NAME, zoneName);
		req.setAttribute(CrossContextConstants.USER_NAME, userName);
		req.setAttribute(CrossContextConstants.URI, uri);
		req.setAttribute(CrossContextConstants.OPERATION, operationName);

		action.additionalInput(uri);
		
		NullServletResponse res = new NullServletResponse();
		
		try {
			DispatchClient.doDispatch(req, res);
			
			Object returnObj = req.getAttribute(CrossContextConstants.RETURN);
			
			return returnObj; // This may be null since not all operations return something.
		}
		catch(ServletException e) {
			String message = e.getMessage();
			String statusCode = (String) req.getAttribute(CrossContextConstants.ERROR);
			if(statusCode != null) {
				if(statusCode.equals(CrossContextConstants.ERROR_NO_ACCESS))
					throw new NoAccessException(message);
				else if(statusCode.equals(CrossContextConstants.ERROR_NO_SUCH_OBJECT))
					throw new NoSuchObjectException(message);
				else if(statusCode.equals(CrossContextConstants.ERROR_ALREADY_EXISTS))
					throw new AlreadyExistsException(message);
				else
					throw new CCClientException(message);
			}
			else {
				throw new CCClientException(message);
			}
		} 
		catch (IOException e) {
			throw new CCClientException(e.getMessage());
		}		
	}
}
