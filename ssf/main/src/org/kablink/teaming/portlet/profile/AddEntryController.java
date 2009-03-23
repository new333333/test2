/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.portlet.profile;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.kablink.teaming.IllegalCharacterInNameException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.NameMissingException;
import org.kablink.teaming.PasswordMismatchException;
import org.kablink.teaming.TextVerificationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.module.profile.impl.GuestProperties;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.portletadapter.portlet.PortletRequestImpl;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.ParamsWrappedActionRequest;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.web.portlet.ModelAndView;


/**
 * @author Peter Hurley
 *
 */
public class AddEntryController extends SAbstractController {
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {
		Map formData = request.getParameterMap();
		response.setRenderParameters(formData);
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		//See if the add entry form was submitted
		if (formData.containsKey("okBtn")) {
			//The form was submitted. Go process it
			String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
			Map fileMap=null;
			if (request instanceof MultipartFileSupport) {
				fileMap = ((MultipartFileSupport) request).getFileMap();
			} else {
				fileMap = new HashMap();
			}
			MapInputData inputData = new MapInputData(formData);
        	String name = inputData.getSingleValue(WebKeys.USER_PROFILE_NAME);
        	if (name == null || name.equals("")) throw new NameMissingException("errorcode.name.missing");
        	if (!BinderHelper.isBinderNameLegal(name)) throw new IllegalCharacterInNameException("errorcode.illegalCharacterInName");
        	String password = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD);
        	String password2 = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD2);
        	if (password == null || !password.equals(password2)) {
        		throw new PasswordMismatchException("errorcode.password.mismatch");
        	}
    		String operation = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
    		if (operation.equals(WebKeys.OPERATION_RELOAD_OPENER) ) {
    			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
    			response.setRenderParameter(WebKeys.URL_BINDER_ID, "");				
    		} else {
    			// Are we dealing with the Guest user?
    			if ( isGuestUser() )
    			{
    				String				kaptchaResponse;
    				String				kaptchaExpected;
    				ActionRequest		actionRequest;
    				PortletRequestImpl	portletRequest;
    				HttpServletRequest	httpServletRequest;

    				// Yes
    				// The request parameter is actually a ParamsWrappedActionRequest object.  Get the real ActionRequest object.
    				if ( request instanceof ParamsWrappedActionRequest )
    				{
	    				actionRequest = ((ParamsWrappedActionRequest)request).getActionRequest();
	    				if ( actionRequest instanceof PortletRequestImpl )
	    				{
		    				portletRequest = (PortletRequestImpl) actionRequest;
		    				httpServletRequest = portletRequest.getHttpServletRequest();
		    				// Get the text used to create the kaptcha image.  It is stored in the http session.
		    				kaptchaExpected = (String) httpServletRequest.getSession().getAttribute( com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY );
		    				
		    				// Get the text the user entered.
		    				kaptchaResponse = inputData.getSingleValue( WebKeys.TEXT_VERIFICATION_RESPONSE );
		    				if ( kaptchaExpected == null || kaptchaResponse == null || !kaptchaExpected.equalsIgnoreCase( kaptchaResponse  ) )
		    				{
		    					// The text entered by the user did not match the text used to create the kaptcha image.
		    	        		throw new TextVerificationException( "errorcode.textverification.mismatch" );
		    				}
	    				}
    				}
    			}
    			
				getProfileModule().addUser(entryType, inputData, fileMap, null);
				setupReloadOpener(response, binderId);
				//flag reload of folder listing
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
    		}
		} else if (formData.containsKey("cancelBtn")) {
			response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());				
			response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
			response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);

		}
			
	}
	private void setupReloadOpener(ActionResponse response, Long binderId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
	}
	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		Map model = new HashMap();
		ProfileBinder binder = getProfileModule().getProfileBinder();
		//Adding an entry; get the specific definition
		Map folderEntryDefs = DefinitionHelper.getEntryDefsAsMap(binder);
		String entryType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_ENTRY_TYPE, "");
		model.put(WebKeys.FOLDER, binder);
		model.put(WebKeys.ENTRY_DEFINITION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);

		// Are we dealing with the Guest user?
		if ( isGuestUser() )
		{
			// Yes, set the flag that will enable the text verification controls in the page.
			model.put( WebKeys.URL_DO_TEXT_VERIFICATION, "true" );
		}
		
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			DefinitionHelper.getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@type='form']");
		} else {
			DefinitionHelper.getDefinition(null, model, "//item[@name='profileEntryForm']");
		}
		return new ModelAndView(WebKeys.VIEW_ADD_ENTRY, model);
	}
	
	
	/**
	 * This method will determine if the user is the guest user.
	 */
	private boolean isGuestUser()
	{
		boolean	guestUser	= false;
		
		if ( RequestContextHolder.getRequestContext() != null )
		{
			User	user;
			
        	user = RequestContextHolder.getRequestContext().getUser();
    		if ( user != null )
    		{
				// Are we dealing with the Guest user?
    			if ( ObjectKeys.GUEST_USER_INTERNALID.equals( user.getInternalId() ) )
    				guestUser = true;
    		}
		}
		
		return guestUser;
	}// end isGuestUser()
}


