/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
import com.sitescape.team.util.NLT;

public class DefaultWorkspaceCoreProcessor extends AbstractBinderProcessor {
    public void moveBinder(Binder source, Binder destination) {
    	if (!(destination instanceof Workspace))
        	throw new NotSupportedException(NLT.get("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()}));
    	super.moveBinder(source, destination);
     }
    /*******************************************************************/
    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData) {
    	//User workspace - title change may come when userTitle changes,
    	//but definition does not include title as a form field
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
    		//if provided but not convered by definition, handle title here
    		if (!entryData.containsKey(ObjectKeys.FIELD_ENTITY_TITLE) && inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) {
    			binder.setTitle(inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_TITLE));    			
    		}
    	}
    	super.modifyBinder_postFillIn(binder, inputData, entryData);
    }

}
