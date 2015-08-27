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
package org.kablink.teaming.portlet.binder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;

import org.kablink.teaming.ConfigurationException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.antivirus.VirusDetectedException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.util.StatusTicket;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.teaming.web.util.WebHelper;

import org.springframework.web.portlet.ModelAndView;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings("unchecked")
public class ModifyBinderController extends AbstractBinderController {
	@Override
	public void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response) 
	throws Exception {

		Map formData = request.getParameterMap();
   		
		// The call to request.getParameterMap() is unescaping the mashup data.  What came in as html,data=<p>%2c%3b%3d</p>;
   		// is unescaped to html,data=<p>,;=</p>;  This causes problems later in DefinitionHelper.fixUpMashupConfiguration()
   		// See bug 695289
   		{
   			HttpServletRequest servletReq;
   			String mashupStr;
   			
   			servletReq = WebHelper.getHttpServletRequest( request );
   			
   			// Get the mashup string as it was passed to us.
   			mashupStr = servletReq.getParameter( "mashup" );
   			
   			// Put this escaped string into the variable, mid, which is passed to modifyBinder()
   			if ( mashupStr != null && mashupStr.length() > 0 )
   			{
   				formData = new HashMap( formData );
   				formData.put( "mashup", mashupStr );
   			}
   		}

		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				
		String binderType = PortletRequestUtils.getRequiredStringParameter(request, WebKeys.URL_BINDER_TYPE);	
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String deleteSource = PortletRequestUtils.getStringParameter(request, WebKeys.URL_DELETE_SOURCE, null);
		String purgeImmediately = PortletRequestUtils.getStringParameter(request, WebKeys.URL_PURGE_IMMEDIATELY, null);
		if (op.equals(WebKeys.OPERATION_SYNCHRONIZE_MIRRORED_FOLDER)) {
			// This trick is here to handle the situation where the synchronization
			// causes the binder to be deleted.
			Binder binder = getBinderModule().getBinder(binderId);
			// First, setup the view as if the binder is to be deleted.
			setupViewBinder(response, binder.getParentBinder());

			//StatusTicket statusTicket = WebStatusTicket.newStatusTicket(PortletRequestUtils.getStringParameter(request, WebKeys.URL_STATUS_TICKET_ID, "none"), request);
			StatusTicket statusTicket = null;
			// 5/20/2013 JK (Bug 818957) - We no longer allow foreground execution of full sync as of Filr 1.1.
			// When we enable this line of code again, we need to think through all the ramification and make
			// sure it doesn't screw up the overall design approach.
			if(getFolderModule().fullSynchronize(binderId, null, statusTicket)) {
				// The binder was not deleted (typical situation). 
				// Setup the right view which will override the previous setup.
				setupViewBinder(response, binderId, binderType);
			}
			else {
				// The binder was indeed deleted.  Finish it up.
				response.setRenderParameter(WebKeys.RELOAD_URL_FORCED, "");			
			}
		} else if ((formData.containsKey("okBtn") || formData.containsKey("applyBtn") || formData.containsKey( "gwtAddFileAttachment")) && WebHelper.isMethodPost(request)) {
			if (op.equals("") || op.equals(WebKeys.OPERATION_MODIFY)) { 			
				//	The modify form was submitted. Go process it
				Map fileMap = null;
				if (request instanceof MultipartFileSupport) {
					fileMap = ((MultipartFileSupport) request).getFileMap();
				} else {
					fileMap = new HashMap();
				}

				//Look for requests to create files
				BinderHelper.processCreateFileRequests(this, request, fileMap);
				
				Set deleteAtts = new HashSet();
				for (Iterator iter=formData.entrySet().iterator(); iter.hasNext();) {
					Map.Entry e = (Map.Entry)iter.next();
					String key = (String)e.getKey();
					if (key.startsWith("_delete_")) {
						deleteAtts.add(key.substring(8));
					}
					
				}
				MapInputData mid = new MapInputData(formData);
		   		if (mid.exists(ObjectKeys.FIELD_BINDER_MIRRORED)) {
		   			boolean mirrored = Boolean.parseBoolean(mid.getSingleValue(ObjectKeys.FIELD_BINDER_MIRRORED));
		   			if(mirrored) {
		   				Map formDataPlus = new HashMap(formData);
		   				formDataPlus.put(ObjectKeys.FIELD_BINDER_LIBRARY, "true");
		   				mid = new MapInputData(formDataPlus);
		   			}
		   		}
		   		
		   		try {
		   			getBinderModule().modifyBinder(binderId, mid, fileMap, deleteAtts, null);				
		   			if (formData.containsKey("okBtn"))
		   				setupReloadOpener(response, binderId);	
		   			if (formData.containsKey("applyBtn"))
		   				response.setRenderParameters(formData);
		   			if ( formData.containsKey( "gwtAddFileAttachment" ) )
		   				response.setRenderParameters( formData );
		   		} catch (ConfigurationException cf) {
		   			response.setRenderParameters(formData);
		   			response.setRenderParameter(WebKeys.EXCEPTION, MiscUtil.exToString(cf));
		   		}
				catch (VirusDetectedException ex) {
					// Is this a modify request from the GWT
					// AddFileAttachmentDlg?
					if (!(formData.containsKey("gwtAddFileAttachment"))) {
						// No!  Return the error in the response.
			   			response.setRenderParameters(formData);
			    		response.setRenderParameter(WebKeys.EXCEPTION, ((VirusDetectedException) ex).getLocalizedMessage("\n"));
			    		return;
					}
					
					// Yes, this is from the GWT AddFileAttachmentDlg!
					// Re-throw the exception so that it gets handled
					// in a format that the dialog can handle.
					throw ex;
				}
			} else if (op.equals(WebKeys.OPERATION_MOVE)) {
				//must be a move
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) {
					try {
						getBinderModule().moveBinder(binderId, destinationId, null);
						setupReloadOpener(response, binderId);
					} catch(NotSupportedException nse) {
			   			response.setRenderParameters(formData);
			   			response.setRenderParameter(WebKeys.EXCEPTION, nse.getMessage());
					}
				}
			} else if (op.equals(WebKeys.OPERATION_COPY)) {
				Long destinationId = TreeHelper.getSelectedId(formData);
				if (destinationId != null) {
					try {
						getBinderModule().copyBinder(binderId, destinationId, true, null);
						setupReloadOpener(response, binderId);
					} catch(NotSupportedException nse) {
			   			response.setRenderParameters(formData);
			   			response.setRenderParameter(WebKeys.EXCEPTION, nse.getMessage());
					}
				}
				
			} else if (op.equals(WebKeys.OPERATION_DELETE)) {
				// The delete-mirrored-binder form was submitted.
				//retrieve binder so we can return to parent
				Binder binder = getBinderModule().getBinder(binderId);			
				Binder parentBinder = binder.getParentBinder();			
				boolean doPurge = (binder.isMirrored() || Boolean.parseBoolean(purgeImmediately));
				//Make sure this isn't a system user workspace being deleted
				if (BinderHelper.isBinderSystemUserWS(binder)) {
					throw
						new NotSupportedException(
							(doPurge                                               ?
								"errorcode.notsupported.deleteBinder.systemUserWS" :
								"errorcode.notsupported.preDeleteBinder.systemUserWS"));
				}
				//get view data, before binder is deleted
				setupViewBinder(response, binder);
				if (doPurge) {
					getBinderModule().deleteBinder(binderId, Boolean.parseBoolean(deleteSource), null);
				}
				else {
					TrashHelper.preDeleteBinder(this, binderId);
				}
				setupViewBinderInParent(response, parentBinder.getId());
			} else {
				setupReloadOpener(response, binderId);			
			}	
		} else if (formData.containsKey("cancelBtn")) {
			//The user clicked the cancel button
			setupCloseWindow(response);
		} else if (op.equals(WebKeys.OPERATION_DELETE)) {
			response.setRenderParameters(formData);
		} else {
			response.setRenderParameters(formData);		
		}
	}

	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
		RenderResponse response) throws Exception {
		Long binderId = new Long(PortletRequestUtils.getRequiredLongParameter(request, WebKeys.URL_BINDER_ID));				

		Map model = new HashMap();	
		String action = PortletRequestUtils.getStringParameter(request, WebKeys.ACTION, "");
		model.put(WebKeys.OPERATION, action);
		String op = PortletRequestUtils.getStringParameter(request, WebKeys.URL_OPERATION, "");
		String path;
		BinderHelper.setupStandardBeans(this, request, response, model, binderId);
		BinderHelper.setupBinderQuotaBeans(this, request, response, model, binderId);
		
		if (op.equals(WebKeys.OPERATION_MOVE) || op.equals(WebKeys.OPERATION_COPY)) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			Workspace ws = getWorkspaceModule().getTopWorkspace();
			Document wsTree = getBinderModule().getDomBinderTree(ws.getId(), new WsDomTreeBuilder(ws, true, this),1);
			Element top = (Element)wsTree.getRootElement();
			if (top != null) {
				//cannot move to top
				top.addAttribute("displayOnly", "true");
			}
			model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);
			model.put(WebKeys.OPERATION, op);
			path = WebKeys.VIEW_MOVE_BINDER;
		} else if (op.equals(WebKeys.OPERATION_DELETE)) {
			Binder binder = getBinderModule().getBinder(binderId);
			model.put(WebKeys.BINDER, binder);
			path = WebKeys.VIEW_CONFIRM_DELETE_MIRRRED_BINDER;
		} else {
			String btn;
			
			btn = PortletRequestUtils.getStringParameter( request, "gwtAddFileAttachment", "");
			if ( btn != null && btn.length() > 0 )
			{
				return new ModelAndView( "forum/success", model );
			}

			Binder binder = getBinderModule().getBinder(binderId);
//			String binderType = PortletRequestUtils.getStringParameter(request, WebKeys.URL_BINDER_TYPE, "");

//			Map publicBinderDefs = new HashMap();
//			DefinitionUtils.getDefinitions(model);
//			if (binderType.equals(EntityIdentifier.EntityType.folder.name())) {
//				publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_FOLDER_DEFINITIONS);
//			} else if (binderType.equals(EntityIdentifier.EntityType.workspace.name())) {
//				publicBinderDefs = (Map) model.get(WebKeys.PUBLIC_WORKSPACE_DEFINITIONS);
//			}
			
//			model.put(WebKeys.PUBLIC_BINDER_DEFINITIONS, publicBinderDefs);

//			DefinitionUtils.getDefinitions(binder, model);
			
			model.put(WebKeys.BINDER, binder);
//			model.put(WebKeys.ENTRY, binder);
			model.put(WebKeys.BINDER_IS_EMPTY, getBinderModule().isBinderEmpty(binder));
			model.put(WebKeys.CONFIG_JSP_STYLE, Definition.JSP_STYLE_FORM);
			Definition binderDef = binder.getEntryDef();
			if (binderDef == null) {
				DefinitionHelper.getDefaultBinderDefinition(binder, model, "//item[@type='form']");
			} else {
				DefinitionHelper.getDefinition(binderDef, model, "//item[@type='form']");
			}

			//Build the mashup beans
			Document configDocument = (Document)model.get(WebKeys.CONFIG_DEFINITION);
			DefinitionHelper.buildMashupBeans(this, binder, configDocument, model, request );
			
			// Add the common data used by the GWT RequestInfo class
			GwtUIHelper.setCommonRequestInfoData( request, this, model );


			path = WebKeys.VIEW_MODIFY_BINDER;
		} 
		model.put(WebKeys.EXCEPTION, request.getParameter(WebKeys.EXCEPTION));
		return new ModelAndView(path, model);
	}
}
