/**
 * 
 */
package org.kablink.teaming.extension;

import java.io.File;
import java.io.IOException;

/**
 * @author dml
 * 
 * Interface for a service which deploys and registers application extensions.
 * 
 */
public interface ExtensionDeployer {
	
	/**
	 * Deploys a specified {@link File}-based extension
	 * @param extension - the extension to be deployed
	 */
	public void deploy(File extension) throws IOException;

}
