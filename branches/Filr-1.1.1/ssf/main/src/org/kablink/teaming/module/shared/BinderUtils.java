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
package org.kablink.teaming.module.shared;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.util.SpringContextUtil;

import java.util.HashMap;
import java.util.Map;

public class BinderUtils {

    public static Binder createBinder(long parentId, String title, String name, Long templateId) throws WriteFilesException, WriteEntryDataException {
        Binder binder;
        Binder parent = getBinderModule().getBinder(parentId);
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            binder = getTemplateModule().addBinder(template.getId(), parent.getId(), title, name);
        } else {
            Definition def = parent.getEntryDef();
            if (def==null) {
                throw new InternalException("Parent binder has no entry definition: " + parentId);
            }
            Map data = new HashMap(); // Input data
            // Title field, not name, is used as the name of the folder. Weird...
            data.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
            if (name!=null) {
                data.put(ObjectKeys.FIELD_BINDER_NAME, name);
            }
            //data.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.TRUE.toString());
            Map params = new HashMap();
            params.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);

            binder = getBinderModule().addBinder(parent.getId(), def.getId(),
                        new MapInputData(data), null, params);

            // Inherit configuration.
            inheritAll(binder.getId());
        }
        return binder;
    }

	static void inheritAll(Long binderId) {
		getBinderModule().setDefinitionsInherited(binderId, true, false);
		getBinderModule().setBinderVersionsInherited(binderId, true, false);
		getBinderModule().setBinderFileEncryptionInherited(binderId, true, false);
		getBinderModule().setTeamMembershipInherited(binderId, true, false);
	}

	private static BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}
	private static TemplateModule getTemplateModule() {
		return (TemplateModule) SpringContextUtil.getBean("templateModule");
	}
}
