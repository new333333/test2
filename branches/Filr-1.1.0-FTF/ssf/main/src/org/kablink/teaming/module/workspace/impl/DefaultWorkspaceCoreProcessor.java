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
package org.kablink.teaming.module.workspace.impl;

import java.util.Map;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.impl.AbstractBinderProcessor;
import org.kablink.teaming.module.shared.InputDataAccessor;


public class DefaultWorkspaceCoreProcessor extends AbstractBinderProcessor {
    public void moveBinder(Binder source, Binder destination, Map options) {
    	if (!(destination instanceof Workspace))
        	throw new NotSupportedException("errorcode.notsupported.moveBinderDestination", new String[] {destination.getPathName()});
    	super.moveBinder(source, destination, options);
     }
 
    public Binder copyBinder(Binder source, Binder destination, Map options) {
    	if (!(destination instanceof Workspace))
        	throw new NotSupportedException("errorcode.notsupported.copyBinderDestination", new String[] {destination.getPathName()});
       	if (Integer.valueOf(Definition.USER_WORKSPACE_VIEW).equals(source.getDefinitionType()) ||
       			Integer.valueOf(Definition.EXTERNAL_USER_WORKSPACE_VIEW).equals(source.getDefinitionType()))
        	throw new NotSupportedException("errorcode.notsupported.copyBinder", new String[] {source.getPathName()});
    	return super.copyBinder(source, destination, options);
     }
    /*******************************************************************/
    protected void modifyBinder_postFillIn(Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//User workspace - title change may come when userTitle changes,
    	//but definition does not include title as a form field
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && ((type.intValue() == Definition.USER_WORKSPACE_VIEW) ||
    			(type.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW))) {
    		//if provided but not convered by definition, handle title here
    		if (!entryData.containsKey(ObjectKeys.FIELD_ENTITY_TITLE) && inputData.exists(ObjectKeys.FIELD_ENTITY_TITLE)) {
    			binder.setTitle(inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_TITLE));    			
    		}
    		if (!entryData.containsKey(ObjectKeys.FIELD_WS_SEARCHTITLE) && inputData.exists(ObjectKeys.FIELD_WS_SEARCHTITLE)) {
    			((Workspace)binder).setSearchTitle(inputData.getSingleValue(ObjectKeys.FIELD_WS_SEARCHTITLE));    			
    		}

    	}
    	super.modifyBinder_postFillIn(binder, inputData, entryData, ctx);
    }
    
}
