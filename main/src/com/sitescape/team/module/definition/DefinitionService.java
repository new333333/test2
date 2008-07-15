package com.sitescape.team.module.definition;

import org.dom4j.Document;

import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Workspace;

/**
 * An interface into privileged {@link Definition} functionality.
 * 
 * @author dml
 * 
 */
public interface DefinitionService extends DefinitionModule {

	/**
	 * Adds the specified {@link Document} as a {@link Definition}.
	 * 
	 * @param doc -
	 *            the <code>Document</code> to be added
	 * @param replace -
	 *            whether to replace an existing <code>Definition</code>, if
	 *            one exists.
	 * @param zone
	 *            -the {@link Workspace} object for the zone the
	 *            <code>Definition</code> is to apply to
	 * @return the newly added <code>Definition</code>
	 */
	Definition addDefinition(Document doc, boolean replace, Workspace zone);
}
