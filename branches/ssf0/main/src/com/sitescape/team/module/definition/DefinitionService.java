package com.sitescape.team.module.definition;

import org.dom4j.Document;

import com.sitescape.team.domain.Definition;

/**
 * An interface into privileged {@link Definition} functionality.
 * 
 * @author dml
 * 
 */
public interface DefinitionService extends DefinitionModule {

	/**
	 * Adds the specified {@link Document} as a {@link Definition} to the
	 * default zone.
	 * 
	 * @param doc -
	 *            the <code>Document</code> to be added
	 * @param replace -
	 *            whether to replace an existing <code>Definition</code>, if
	 *            one exists.
	 * @return the newly added <code>Definition</code>
	 */
	Definition addDefinition(Document doc, boolean replace);
}
