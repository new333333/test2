/**
 * 
 */
package org.kablink.teaming.extension;

import java.io.File;
import java.io.IOException;

/**
 * 
 * Interface for a service which deploys and registers application extensions.
 * 
 */
public interface ExtensionDeployer {
	
	public void check();
	/**
	 * Deploys a specified {@link File}-based extension
	 * @param extension - the extension to be deployed
	 * @param full - deploy into database and disk or just disk
	 */
	public void deploy(File extension, boolean full) throws IOException;

}
