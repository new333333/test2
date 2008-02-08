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
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.List;

public class TemplateBinder extends Binder {
	protected Description tDescription;
	protected String tTitle;
	public TemplateBinder() {
		super();
	}
	public TemplateBinder(TemplateBinder source) {
		super(source);
		tDescription = new Description(source.getTemplateDescription());
		tTitle = source.tTitle;
		mirrored=false;
	}
	public TemplateBinder(Binder source) {
		super(source);
		tDescription = new Description(source.getDescription());
		tTitle = source.getTitle();
		mirrored=false;
		
	}
    public boolean isMirroredAllowed() {
    	return false;
    }

	public EntityIdentifier.EntityType getEntityType() {
		if (definitionType == Definition.FOLDER_VIEW)
			return EntityIdentifier.EntityType.folder;
		else return EntityIdentifier.EntityType.workspace;
	}
    public List getEntryDefinitions() {
    	if (definitionType == Definition.FOLDER_VIEW)
    		return getDefs(Definition.FOLDER_ENTRY);
    	return new ArrayList();
    }
    public List getViewDefinitions() {
		return getDefs(definitionType);
    }
    /**
     * @hibernate.component prefix="tDescription_"
     */
    public Description getTemplateDescription() {
        return this.tDescription;
    }
    public void setTemplateDescription(Description tDescription) {
        if (this.tDescription != null)
        	// try to avoid unecessary updates
        	if (this.tDescription.equals(tDescription)) return;
    	this.tDescription = tDescription; 
    }
  
    public void setTemplateDescription(String tDescriptionText) {
		Description tmp = new Description(tDescriptionText);
    	if (tDescription != null) {
    		if (tDescription.equals(tmp)) return;
    	}
        this.tDescription = tmp; 
    }
    /**
     * @hibernate.property length="128"
     */
    public String getTemplateTitle() {
        return tTitle;
    }
    public void setTemplateTitle(String tTitle) {
    	this.tTitle = tTitle;
    }
    //we always can inherit
    public boolean isFunctionMembershipInherited() {
       return functionMembershipInherited;
    }
    //this is needed for templates, which may inherit from a yet to be determined parent
    public boolean isFunctionMembershipInheritanceSupported() {
    	return true;
    }


    public boolean isDefinitionInheritanceSupported() {
    	if (definitionType != Definition.FOLDER_VIEW) return false;
    	if (isRoot()) return true;  //may be added under a folder
    	if (getParentBinder().getDefinitionType() != Definition.FOLDER_VIEW) return false; //topFolder
    	return true;
    }

}
