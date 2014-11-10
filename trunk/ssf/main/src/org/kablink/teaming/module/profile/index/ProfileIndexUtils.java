/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.profile.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Group.GroupType;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.FieldFactory;

import static org.kablink.util.search.Constants.*;

/**
 * This class contains non-standard indexing logic specific to profile elements. 
 * In other word, only those processing behaviors beyond and above that provided
 * by the factory-shipped FieldBuilder* classes must be coded up here. Otherwise,
 * NEVER add hard-coded behavior here.
 *
 * @author ?
 */
public class ProfileIndexUtils {
	  public static void addName(Document doc, User user, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = FieldFactory.createFieldStoredNotAnalyzed(LOGINNAME_FIELD, user.getName());
        doc.add(docNumField);
    }    
    public static void addName(Document doc, Group user, boolean fieldsOnly) {
    	//Add the id of the creator (no, not that one...)
        Field docNumField = FieldFactory.createFieldStoredNotAnalyzed(GROUPNAME_FIELD, user.getName());
        doc.add(docNumField);
    }      
    public static void addName(Document doc, Application application, boolean fieldsOnly) {
        Field nameField = FieldFactory.createFieldStoredNotAnalyzed(APPLICATION_NAME_FIELD, application.getName());
        doc.add(nameField);
    }    
    public static void addName(Document doc, ApplicationGroup appGroup, boolean fieldsOnly) {
        Field nameField = FieldFactory.createFieldStoredNotAnalyzed(APPLICATION_GROUPNAME_FIELD, appGroup.getName());
        doc.add(nameField);
    }      

    public static void addEmail(Document doc, User user) {
    	// Does the user have an email address?
    	String ema = user.getEmailAddress();
        if (ema!=null) {
        	// Yes!  Add it to the index.
            Field emailAddressField = FieldFactory.createFieldStoredNotAnalyzed(EMAIL_FIELD, ema);
            doc.add(emailAddressField);

            // Does that email address have a domain part?
            int atPos = ema.lastIndexOf('@');
            if (0 < atPos) {
            	String domain = ema.substring(atPos + 1);
            	if ((null != domain) && (0 < domain.length())) {
            		// Yes!  Add that to the index too.
                    Field domainField = FieldFactory.createFieldStoredNotAnalyzed(EMAIL_DOMAIN_FIELD, domain);
                    doc.add(domainField);
            	}
            }
        }
    }

    public static void addDisabled(Document doc, Group group) {
        Field disabledGroupField = FieldFactory.createFieldStoredNotAnalyzed(DISABLED_PRINCIPAL_FIELD, String.valueOf(group.isDisabled()));
        doc.add(disabledGroupField);
    }

    public static void addDisabled(Document doc, User user) {
        Field disabledUserField = FieldFactory.createFieldStoredNotAnalyzed(DISABLED_PRINCIPAL_FIELD, String.valueOf(user.isDisabled()));
        doc.add(disabledUserField);
    }

    public static void addSiteAdmin(Document doc, User user) {
    	boolean isSiteAdmin = MiscUtil.getAdminModule().testUserAccess(user, AdminOperation.manageFunction);
        Field siteAdminField = FieldFactory.createFieldStoredNotAnalyzed(SITE_ADMIN_FIELD, String.valueOf(isSiteAdmin));
        doc.add(siteAdminField);
    }

    public static void addAvatarId(Document doc, User user) {
        String attachmentId = user.getAvatarAttachmentId();
        if (attachmentId != null) {
    		Field attachmentIdField = FieldFactory.createFieldStoredNotAnalyzed(AVATAR_ID_FIELD, attachmentId);
    		doc.add(attachmentIdField);
    	}
    }      
    public static void addWorkspaceId(Document doc, User user) {
    	if (user.getWorkspaceId() != null) {
    		Field workspaceIdField = FieldFactory.createFieldStoredNotAnalyzed(WORKSPACE_ID_FIELD, user.getWorkspaceId().toString());
    		doc.add(workspaceIdField);
    	}
    }
    public static void addReservedId(Document doc, Principal principal, boolean fieldsOnly) {
    	if (Validator.isNotNull(principal.getInternalId())) {
    		Field resIdField =  FieldFactory.createFieldStoredNotAnalyzed(RESERVEDID_FIELD, principal.getInternalId());
    		doc.add(resIdField);
    	}
    } 
    public static void addPersonFlag(Document doc, User user) {
        Field personField = FieldFactory.createFieldStoredNotAnalyzed(PERSONFLAG_FIELD, String.valueOf(user.isPerson()));
        doc.add(personField);
    }
    
    public static void addIdentityInfo(Document doc, UserPrincipal user) {
        Field field = FieldFactory.createFieldStoredNotAnalyzed(IDENTITY_INTERNAL_FIELD, String.valueOf(user.getIdentityInfo().isInternal()));
        doc.add(field);    	
    }
    
    public static void addDynamic(Document doc, Group group, boolean fieldsOnly) {
    	Field dynamicField = FieldFactory.createFieldStoredNotAnalyzed(IS_GROUP_DYNAMIC_FIELD, (group.isDynamic() ? Constants.TRUE : Constants.FALSE));
    	doc.add(dynamicField);
    }

    /**
     * 
     */
    public static void addIsLdapContainer( Document doc, User user )
    {
		Field path = FieldFactory.createFieldStoredNotAnalyzed( IS_LDAP_CONTAINER_FIELD, Constants.FALSE );
		doc.add( path );
    }

    /**
     * 
     */
    public static void addIsLdapContainer( Document doc, Group group )
    {
		Field path = FieldFactory.createFieldStoredNotAnalyzed( IS_LDAP_CONTAINER_FIELD, (group.isLdapContainer() ? Constants.TRUE : Constants.FALSE) );
		doc.add( path );
    }

    /**
     * 
     */
    public static void addIsFromLdap( Document doc, Group group )
    {
    	IdentityInfo idInfo;
    	Field fromLdapField;
    	boolean fromLdap;

    	fromLdap = false;
    	idInfo = group.getIdentityInfo();
    	if ( idInfo != null )
    		fromLdap = idInfo.isFromLdap();
    	
    	fromLdapField = FieldFactory.createFieldStoredNotAnalyzed( IS_GROUP_FROM_LDAP_FIELD, (fromLdap ? Constants.TRUE : Constants.FALSE) );
    	doc.add( fromLdapField );
    }

    /**
     * 
     */
    public static void addIsTeamGroup( Document doc, User user )
    {
		Field path = FieldFactory.createFieldStoredNotAnalyzed( IS_TEAM_GROUP_FIELD, Constants.FALSE );
		doc.add( path );
    }

    /**
     * 
     */
    public static void addIsTeamGroup( Document doc, Group group )
    {
    	String value;
    	
    	if ( group.getGroupType() == GroupType.team )
    		value = Constants.TRUE;
    	else
    		value = Constants.FALSE;
    	
		Field path = FieldFactory.createFieldStoredNotAnalyzed( IS_TEAM_GROUP_FIELD, value );
		doc.add( path );
    }

}
