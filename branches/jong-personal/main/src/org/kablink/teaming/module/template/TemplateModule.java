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
package org.kablink.teaming.module.template;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.security.AccessControlException;


public interface TemplateModule {
	public enum TemplateOperation {
		manageTemplate
	}
	/**
	 * The method name to be called is used as the operation.   This
	 * allows the adminModule to check for multiple rights or change requirments in the future.
	 * @param operation
	 * @return
	 */
   	public boolean testAccess(TemplateOperation operation);
   	public void checkAccess(TemplateOperation operation) throws AccessControlException;
   	/**
   	 * Create a new binder from an existing template.  If title is null, use the title from the template
   	 * @param templateId
   	 * @param parentBinderId
   	 * @param title
   	 * @param name - can be null
   	 * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
   	 */
    public Binder addBinder(Long templateId, Long parentBinderId, String title, String name) throws AccessControlException, WriteFilesException;
    
    public Binder addBinder(Long templateId, Long parentBinderId, String title, String name, Map overrideInputData, Map options) throws AccessControlException, WriteFilesException;
    /**
     * Create default template.  Should already exist
     * @param type
     * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
    */
    public TemplateBinder addDefaultTemplate(int type);
    /**
     * Create a template from a document
     * @param document
     * @param replace
     * @return
     */
	public TemplateBinder addTemplate(Binder localBinderParent, Document document, boolean replace) throws AccessControlException;
	public TemplateBinder addTemplate(Binder localBinderParent, InputStream stream, boolean replace) throws AccessControlException, DocumentException;
	/**
	 * Create a template
	 * @param type
	 * @param updates
	 * @return
   	 * @throws AccessControlException
	 */
	public TemplateBinder addTemplate(Binder localBinderParent, int type, Map updates) throws AccessControlException;
	/**
	 * Create a new template from an existing template
	 * @param parentId
	 * @param srcTemplateId
	 * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
	 */
	public TemplateBinder addTemplate(Long parentId, Long srcTemplateId) throws AccessControlException, WriteFilesException;
	/**
	 * Create a template using an existing binder as the source.  Recurses through sub-binders
	 * @param binderId
	 * @return
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public TemplateBinder addTemplateFromBinder(Binder parentBinder, Long binderId) throws AccessControlException, WriteFilesException;
	public void modifyTemplate(Long id, Map updates) throws AccessControlException;
    /**
     * Get a template
     * @param id
     * @return
     */
 	public TemplateBinder getTemplate(Long id); 
	public TemplateBinder getTemplateByName(String name);
 	/**
 	 * Get all system top level templates
 	 * @return
 	 */
	public List<TemplateBinder> getTemplates();
	public List<TemplateBinder> getTemplates(boolean includeHiddenTemplates);
 	/**
 	 * Get all binder level templates
 	 * @return
 	 */
	public List<TemplateBinder> getTemplates(Binder binder);
	/**
	 * Build a document used to export/import templates
	 * @param template
	 * @return
	 */
	public Document getTemplateAsXml(TemplateBinder template);
	/**
	 * Get all top level templates of the specified type
	 * @param type
	 * @return
	 */
	public List<TemplateBinder> getTemplates(int type);
	public List<TemplateBinder> getTemplates(int type, Binder binder, boolean includeAncestors);
	public List<TemplateBinder> getTemplates(int type, Binder binder, boolean includeAncestors, 
			boolean includeHiddenTemplates);

	public boolean updateDefaultTemplates(Long topId, boolean replace);
	
	public boolean checkIfBinderValidForTemplate(Binder binder, String[] errors);
	
    public List<Binder> _addNetFolderBindersInSync(Long templateId, Long parentBinderId, List<String> titleList, List<String> nameList, List<Map> overrideInputDataList, List<Map> optionsList) throws AccessControlException, WriteFilesException;

}
