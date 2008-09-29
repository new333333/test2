/**
 * 
 */
package com.sitescape.team.module.extension;

import java.io.File;

import com.sitescape.team.util.EventSource;

/**
 * @author dml
 * 
 * Generic interface for classes which generate extension deployment events.
 *
 */
public interface ExtensionDeployNotifier<S extends ExtensionDeployNotifier<S>> extends EventSource<S, File> {
	

}
