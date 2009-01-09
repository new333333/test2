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
	public TemplateBinder addTemplate(Document document, boolean replace) throws AccessControlException;
	public TemplateBinder addTemplate(InputStream stream, boolean replace) throws AccessControlException, DocumentException;
	/**
	 * Create a template
	 * @param type
	 * @param updates
	 * @return
   	 * @throws AccessControlException
	 */
	public TemplateBinder addTemplate(int type, Map updates) throws AccessControlException;
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
	public TemplateBinder addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException;
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

	public boolean updateDefaultTemplates(Long topId, boolean replace);
}
