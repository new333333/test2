/**
 * 
 */
package com.sitescape.team.module.extension;

import java.io.File;

import com.sitescape.team.util.EventListener;

/**
 * @author dml
 * 
 * Interface for a service which deploys and registers application extensions.
 * 
 */
public interface ExtensionDeployer<S extends ExtensionDeployNotifier<S>> extends EventListener<S, File> {
	
	/**
	 * Deploys a specified {@link File}-based extension
	 * @param extension - the extension to be deployed
	 */
	public void deploy(File extension);

}
