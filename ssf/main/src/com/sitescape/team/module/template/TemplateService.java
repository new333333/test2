/**
 * 
 */
package com.sitescape.team.module.template;

import org.dom4j.Document;

import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.Workspace;

/**
 * 
 * Provides additional functionality of {@link TemplateModule} for handling
 * {@link TemplateBinder} for internal, "trusted" services.
 * 
 * @author dml
 * 
 */
public interface TemplateService extends TemplateModule {
	
	/**
	 * Adds the specified {@link Document} as a {@link TemplateBinder} to the
	 * specified {@link Workspace}. If <code>replace</code> is true, any
	 * existing definition with the same name will be silently overwritten.
	 * 
	 * @param document -
	 *            the <code>TemplateBinder</code>s definition
	 * @param replace -
	 *            whether to overwrite an existing template definition if one
	 *            exists.
	 * @param zone -
	 *            the zone in which to add the template
	 * @return the newly created <code>TemplateBinder</code>
	 */
	public TemplateBinder addTemplate(Document document, boolean replace, Workspace zone);

}
