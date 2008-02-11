/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.module.workspace.impl;

import java.util.Map;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.binder.impl.AbstractBinderProcessor;
import com.sitescape.team.module.shared.InputDataAccessor;

public class DefaultWorkspaceCoreProcessor extends AbstractBinderProcessor {
    public void moveBinder(Binder source, Binder destination) {
    	if (!(destination instanceof Workspace))
        	throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    	super.moveBinder(source, destination);
     }
 
    public Binder copyBinder(Binder source, Binder destination, Map params) {
    	if (!(destination instanceof Workspace))
        	throw new NotSupportedException("errorcode.notsupported.copyBinderDestination", new String[] {destination.getPathName()});
       	if (Integer.valueOf(Definition.USER_WORKSPACE_VIEW).equals(source.getDefinitionType()))
        	throw new NotSupportedException("errorcode.notsupported.copyBinder", new String[] {source.getPathName()});
    	return super.copyBinder(source, destination, params);
     }
    /*******************************************************************/
    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//User workspace - title change may come when userTitle changes,
    	//but definition does not include title as a form field
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
    		//if provided but not convered by definition, handle title here
    		if (!entryData.containsKey(ObjectKeys.FIELD_ENTITY_TITLE) && inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) {
    			binder.setTitle(inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_TITLE));    			
    		}
    		if (!entryData.containsKey(ObjectKeys.FIELD_BINDER_SEARCHTITLE) && inputData.exists(ObjectKeys.FIELD_BINDER_SEARCHTITLE)) {
    			((Workspace)binder).setSearchTitle(inputData.getSingleValue(ObjectKeys.FIELD_BINDER_SEARCHTITLE));    			
    		}

    	}
    	super.modifyBinder_postFillIn(binder, inputData, entryData, ctx);
    }
    
}
