/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.Element;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.antivirus.VirusDetectedException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.User.ExtProvState;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.profile.ProfileModule.ProfileOperation;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.ModelAndViewWithException;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.encrypt.EncryptUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PasswordPolicyHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.GetterUtil;
import org.kablink.util.StringUtil;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings("unchecked")
public class ModifyEntryController extends SAbstractController {
	/**
	 * ?
	 * 
	 * @param reqeust
	 * @param response
	 * 
	 * @throws Exception
	 */
	@Override
	public void handleActionRequestAfterValidation(final ActionRequest request, final ActionResponse response) throws Exception {
		// If the user is the built-in admin or has zone administration
		// rights...
        User user = RequestContextHolder.getRequestContext().getUser();
		if (user.isAdmin() || (!(getAdminModule().testAccess( AdminOperation.manageFunction )))) {
			// ...simply perform the request.
			Exception ex = handleActionRequestAfterValidationImpl(request, response);
			if (null != ex) {
				throw ex;
			}
		}
		
		else {
			// ...otherwise, perform the request as the built-in admin
			// ...user.
			logger.info("User '" + user.getTitle() + "' is modifying a user the built-in admin user.");
			Exception ex = ((Exception) RunasTemplate.runasAdmin(
				new RunasCallback() {
					@Override
					public Object doAs() {
						return handleActionRequestAfterValidationImpl(request, response);
					}
				},
				RequestContextHolder.getRequestContext().getZoneName()));
			if (null != ex) {
				throw ex;
			}
		}
	}
	
	/*
	 */
	private Exception handleActionRequestAfterValidationImpl(ActionRequest request, ActionResponse response) {
		Exception reply = null;
		try {
	        User user = RequestContextHolder.getRequestContext().getUser();
			Map formData = request.getParameterMap();
			Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (formData.containsKey("okBtn") && op.equals(WebKeys.OPERATION_DELETE) && WebHelper.isMethodPost(request)) {
				String deleteWs = PortletRequestUtils.getStringParameter(request, "deleteWs", null);
				Map options = new HashMap();
				options.put(ObjectKeys.INPUT_OPTION_DELETE_USER_WORKSPACE, GetterUtil.getBoolean(deleteWs, false));
				getProfileModule().deleteEntry(entryId, options);			
				setupReloadOpener(response, binderId, entryId);
			} else if (formData.containsKey("okBtn") && op.equals(WebKeys.OPERATION_DISABLE) && WebHelper.isMethodPost(request)) {
				getProfileModule().disableEntry(entryId, true);			
				response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
				response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
			} else if (formData.containsKey("okBtn") && op.equals(WebKeys.OPERATION_ENABLE) && WebHelper.isMethodPost(request)) {
				getProfileModule().disableEntry(entryId, false);			
				response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());		
				response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_LISTING);
				response.setRenderParameter(WebKeys.URL_OPERATION, WebKeys.OPERATION_RELOAD_LISTING);
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
			} else if (formData.containsKey("okBtn") && op.equals("") && WebHelper.isMethodPost(request)) {
		        //Modifying the profile is not available to the guest user
		        if (!ObjectKeys.GUEST_USER_INTERNALID.equals(user.getInternalId())) {
					//The modify form was submitted. Go process it
					Map fileMap=null;
					if (request instanceof MultipartFileSupport) {
						fileMap = ((MultipartFileSupport) request).getFileMap();
					} else {
						fileMap = new HashMap();
					}
					Set deleteAtts = new HashSet();
					for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
						Map.Entry e = (Map.Entry)iter.next();
						String key = (String)e.getKey();
						if (key.startsWith("_delete_")) {
							deleteAtts.add(key.substring(8));
						}
						
					}
					MapInputData inputData = getProfileModule().validateUserAttributes(entryId, formData);
					
					//Is the userName being changed?
					if (inputData.exists( WebKeys.USER_PROFILE_NAME )) {
						try {
							String newUserName = inputData.getSingleValue(WebKeys.USER_PROFILE_NAME);
							Principal p1 = getProfileModule().getEntry(entryId);
							if (p1 != null && !p1.getName().equals(newUserName)) {
								Principal p2 = null;
								try {
									p2 = getProfileModule().findUserByName(newUserName);
								} catch(NoUserByTheNameException nue) {}
								if (p2 != null) {
					        		setupReloadPreviousPage(response, NLT.get("errorcode.user.alreadyExists"));
					        		return null;
								}
							}
						} catch(AccessControlException ae) {
			        		setupReloadPreviousPage(response, NLT.get("errorcode.access.denied"));
			        		return null;
						} catch(Exception e) {
			        		setupReloadPreviousPage(response, NLT.get("errorcode.user.exists"));
			        		return null;
						}
					}
					
					// Is there a password field on the page?
		        	String  password = null;
					boolean passwordChanged = inputData.exists( WebKeys.USER_PROFILE_PASSWORD );
					User pwdUser = null;
		            if ( passwordChanged ) 
		            {
		            	// Yes
						// Are the passwords entered by the user the same?
			        	password = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD);
			        	String password2 = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD2);
			        	String password3 = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD3);
			        	if ( password == null || password2 == null || !password.equals(password2) ) {
			        		// No
			        		setupReloadPreviousPage(response, NLT.get("errorcode.password.mismatch"));
			        		return null;
			        	}
	
			        	// Note: The following code needs to be kept in synch with the similar check in 
			        	//       ProfileModuleImpl.changePassword() method.
			        	ProfileBinder binder = null;
			        	try {
			        		binder = getProfileModule().getProfileBinder();
			        	} catch(AccessControlException ex) {}
			        	
			            Principal p = getProfileModule().getEntry(entryId);
			            pwdUser = ((User) p);
			            if (binder == null || !getProfileModule().testAccess(binder, ProfileOperation.manageEntries) ||
			            		!(p instanceof User) || user.getName().equals(p.getName()) || ((User)p).isSuper()) {
			            	String passwordOriginal = inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD_ORIGINAL);
	
			            	//Check that the user knows the current password
			            	if ( (p instanceof User && !password.equals("") && !password.equals(password3)) ||
			            			(p instanceof User && !password.equals("") && !password.equals(password3) && 
			            			(user.getName().equals(p.getName()) || ((User)p).isSuper()) ))
			            	{
			            		// If the user didn't enter the current password or they entered it incorrectly, tell them about it.
			            		if ( passwordOriginal.equals("") || 
			            				!EncryptUtil.checkPassword(passwordOriginal, (User)p)) {
			                		setupReloadPreviousPage(response, NLT.get("errorcode.password.invalid"));
			                		return null;
			            		}
			            	}
			            }
			            if (password3 != null && !password3.equals("") && password.equals("")) {
	                		//The user is trying to set a blank password, give an error
			            	setupReloadPreviousPage(response, NLT.get("errorcode.password.invalid"));
	                		return null; 
			            }
			        	
			            if ( inputData.getSingleValue(WebKeys.USER_PROFILE_PASSWORD).equals("") ||
			            		password.equals(password3)) {
			            	//Don't allow blank password (either on purpose or by accident)
			            	//  password3 is a hidden field indicating what was put in the password field as a dummy value
			            	inputData.remove( WebKeys.USER_PROFILE_PASSWORD );
			            	passwordChanged = false;
			            }
		            }
	
		            // Is the password being changed?
		            Date changeDate = null;
		            if (passwordChanged) {
			            // Yes!  Is it the admin user changing somebody
		            	// else's password?
			            if ((null == user) || (!(user.isAdmin())) || user.getId().equals(entryId)) {
			            	// No, it's not the admin or it's somebody else
			            	// changing their own password!  Does the
			            	// password violate the system's password
			            	// policy?  
			            	List<String> ppViolations = PasswordPolicyHelper.getPasswordPolicyViolations(user, pwdUser, password);
			            	if (MiscUtil.hasItems(ppViolations)) {
			            		// Yes!  We need to pass the violations
			            		// back to the user.
				            	setupReloadPreviousPage(response, NLT.get("errorcode.password.violatesPolicy"), ppViolations);
		                		return null; 
			            	}
			            	
			            	// The last password change date is now.
			            	changeDate = new Date();
			            }
		            }
		            
		            try {
		            	getProfileModule().modifyEntry(entryId, inputData, fileMap, deleteAtts, null, null);
		            }
		            catch (VirusDetectedException ex) {
		            	// Is this modify from the GWT profile view?
		            	String profileMarker = inputData.getSingleValue(WebKeys.URL_PROFILE);
		            	if ((null == profileMarker) || (!(profileMarker.equals("1")))) {
							// No!  Return the error in the response.
							List<String> errorStrings = MiscUtil.getLocalizedVirusDetectedErrorStrings(ex.getErrors());
			            	setupReloadPreviousPage(response, NLT.get("errorcode.user.rejectedAttachment"), errorStrings);
				    		return null;
		            	}
		            	
						// Yes, this is from the GWT profile view!
						// Re-throw the exception so that it gets
		            	// handled in a format that the view can
		            	// handle.
		            	throw ex;
		            }
					if (passwordChanged) {
						getProfileModule().setLastPasswordChange(entryId, changeDate);
						
						// Bugzilla 880226:  If we're changing an
						//    external user's password, mark the
						//    account as having been verified.
						if ((null != pwdUser) && (!(pwdUser.isShared())) && (!(pwdUser.getIdentityInfo().isInternal()))) {
							if (ExtProvState.verified != pwdUser.getExtProvState()) {
								ExternalUserUtil.markAsVerified(pwdUser);
							}
						}
					}
					
					//Now look to see if there were groups specified (but only if allowed to manage these
					try {
						ProfileBinder binder = getProfileModule().getProfileBinder();
						if (getProfileModule().testAccess(binder, ProfileOperation.manageEntries)) {
							Principal p = getProfileModule().getEntry(entryId);
							Document def = p.getEntryDefDoc();
							if (def != null) {
								Element manageGroupEle = (Element) def.getRootElement().selectSingleNode("//item[@name='profileManageGroups']");
								if (manageGroupEle != null) {
									String[] idList = new String[0];
									if (PortletRequestUtils.getStringParameter(request, WebKeys.URL_USER_GROUPS_LIST) != null) {
										idList = PortletRequestUtils.getStringParameter(request, WebKeys.URL_USER_GROUPS_LIST).split(" ");
										for (String uid : idList) {
											try {
												Long id = Long.valueOf(uid.trim());
												Principal group = getProfileModule().getEntry(id); 
												if (group instanceof GroupPrincipal && group.getIdentityInfo().isFromLocal()) {
													((GroupPrincipal) group).addMember(p);
												}
											} catch(Exception e) {}
										}
									}
								}
							}
						}
					} catch(Exception e) {}
		
					//See if there was a request to reorder the graphic files
					@SuppressWarnings("unused")
					String graphicFileIds = PortletRequestUtils.getStringParameter(request, "_graphic_id_order", "");
		        }
		       
		        if(formData.containsKey("profile")) {
		    		response.setRenderParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		    		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
		        	response.setRenderParameter("profile", "1");
		        	if(formData.containsKey("reload")) {
			        	setupReloadOpener(response, binderId, entryId);
		        	}
		        } else {
					setupReloadOpener(response, binderId, entryId);
		        }
				//flag reload of folder listing
				//response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");
			} else if (formData.containsKey("cancelBtn")) {
				//The user clicked the cancel button
				setupCloseWindow(response);
			} else {
				response.setRenderParameters(formData);
			}
		}
		
		catch (Exception ex) {
			reply = ex;
		}
		
		return reply;
	}
	
	/*
	 */
	private void setupReloadOpener(ActionResponse response, Long folderId, Long entryId) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_OPENER);
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());
	}
	
	/*
	 */
	private void setupReloadPreviousPage(ActionResponse response, String errorMessage, List<String> messageDetails) {
		//return to view previous page
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_RELOAD_PREVIOUS_PAGE);
		response.setRenderParameter(WebKeys.ERROR_MESSAGE, errorMessage);
		if (MiscUtil.hasItems(messageDetails)) {
//!			...this needs to be implemented...
			response.setRenderParameter(
				WebKeys.ERROR_MESSAGE_PACKED_DETAILS,
				StringUtil.pack(messageDetails.toArray(new String[0])));
		}
	}
	
	/*
	 */
	private void setupReloadPreviousPage(ActionResponse response, String errorMessage) {
		// Always use the initial form of the method.
		setupReloadPreviousPage(response, errorMessage, null);
	}
	
	/*
	 */
	private void setupCloseWindow(ActionResponse response) {
		//return to view entry
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_CLOSE_WINDOW);
	}
	
	/*
	 */
	@SuppressWarnings("unused")
	private void setupViewEntry(ActionResponse response, Long folderId, Long entryId) {
		response.setRenderParameter(WebKeys.URL_BINDER_ID, folderId.toString());		
		response.setRenderParameter(WebKeys.URL_ENTRY_ID, entryId.toString());		
		response.setRenderParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PROFILE_ENTRY);
	}
	
	/**
	 * ?
	 * 
	 * @param reqeust
	 * @param response
	 * 
	 * @throws Exception
	 */
	@Override
	public ModelAndView handleRenderRequestAfterValidation(final RenderRequest request, final RenderResponse response) throws Exception {
		// If the user is the built-in admin or has zone administration
		// rights...
        User user = RequestContextHolder.getRequestContext().getUser();
		if (user.isAdmin() || (!(getAdminModule().testAccess( AdminOperation.manageFunction )))) {
			// ...simply perform the request.
			ModelAndViewWithException mv = handleRenderRequestAfterValidationImpl(request, response);
			Exception ex = mv.getException();
			if (null != ex) {
				throw ex;
			}
			return mv.getModelAndView();
		}
		
		else {
			// ...otherwise, perform the request as the built-in admin
			// ...user.
			ModelAndViewWithException mv = ((ModelAndViewWithException) RunasTemplate.runasAdmin(
				new RunasCallback() {
					@Override
					public Object doAs() {
						return handleRenderRequestAfterValidationImpl(request, response);
					}
				},
				RequestContextHolder.getRequestContext().getZoneName()));
			Exception ex = mv.getException();
			if (null != ex) {
				throw ex;
			}
			return mv.getModelAndView();
		}
	}
	
	/*
	 */
	private ModelAndViewWithException handleRenderRequestAfterValidationImpl(RenderRequest request, RenderResponse response) {
		ModelAndViewWithException reply = new ModelAndViewWithException();
		try {
			Map model = new HashMap();	
			Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_ENTRY_ID));				
			Principal entry  = getProfileModule().getEntry(entryId);
			String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
			if (op.equals(WebKeys.OPERATION_DELETE)) {
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.BINDER, entry.getParentBinder());
				reply.setModelAndView(new ModelAndView(WebKeys.VIEW_CONFIRM_DELETE_USER_WORKSPACE, model));
				return reply;
			} else if (op.equals(WebKeys.OPERATION_DISABLE) || op.equals(WebKeys.OPERATION_ENABLE)) {
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.BINDER, entry.getParentBinder());
				reply.setModelAndView(new ModelAndView(WebKeys.VIEW_CONFIRM_DISABLE_USER_ACCOUNT, model));
				return reply;
			} else {
		       if (!RequestContextHolder.getRequestContext().getUserId().equals(entryId)) {
		    	   getProfileModule().checkAccess(entry, ProfileOperation.modifyEntry);
		       }
				model.put(WebKeys.ENTRY, entry);
				model.put(WebKeys.FOLDER, entry.getParentBinder());
				model.put(WebKeys.BINDER, entry.getParentBinder());
				model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
				BinderHelper.setupStandardBeans(this, request, response, model, entry.getParentBinder().getId());
				try {
					ProfileBinder binder = getProfileModule().getProfileBinder();
					if (getProfileModule().testAccess(binder, ProfileOperation.manageEntries)) {
						model.put(WebKeys.IS_BINDER_ADMIN, true);
					}
				} catch(AccessControlException ex) {}
				if (entry.getEntryDefId() == null) {
					DefinitionHelper.getDefaultEntryView(entry, model, "//item[@name='entryForm' or @name='profileEntryForm']");
				} else {
					DefinitionHelper.getDefinition(entry.getEntryDefDoc(), model, "//item[@type='form']");
				}
				Map readOnly = new HashMap();
				for (String name:getAuthenticationModule().getMappedAttributes(entry)) {
					readOnly.put(name, Boolean.TRUE);
				}
				
				// Was this Principal sync'd from an ldap source?
				if((entry instanceof User) && !(((User)entry).getIdentityInfo().isFromLocal()))
				{
					// Yes, don't let the user change the password.
					readOnly.put( "password", Boolean.TRUE );
				}
				
				model.put(WebKeys.READ_ONLY, readOnly);
				reply.setModelAndView(new ModelAndView(WebKeys.VIEW_MODIFY_ENTRY, model));
				return reply;
			}
		}
		
		catch (Exception ex) {
			reply.setException(   ex  );
			reply.setModelAndView(null);
		}
			
		return reply;
	}
}
