package com.sitescape.team.module.template;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;

import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.security.AccessControlException;

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

    public Long addBinder(Long templateId, Long parentBinderId, String title, String name) throws AccessControlException, WriteFilesException;
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
	public Long addTemplate(Document document, boolean replace) throws AccessControlException;

	/**
	 * Create a template
	 * @param type
	 * @param updates
	 * @return
   	 * @throws AccessControlException
	 */
	public Long addTemplate(int type, Map updates) throws AccessControlException;
	/**
	 * Create a new template from an existing template
	 * @param parentId
	 * @param srcTemplateId
	 * @return
   	 * @throws AccessControlException
   	 * @throws WriteFilesException
	 */
	public Long addTemplate(Long parentId, Long srcTemplateId) throws AccessControlException, WriteFilesException;
	/**
	 * Create a template using an existing binder as the source.  Recurses through sub-binders
	 * @param binderId
	 * @return
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 */
	public Long addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException;
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

	public void updateDefaultTemplates(Long topId);
}
