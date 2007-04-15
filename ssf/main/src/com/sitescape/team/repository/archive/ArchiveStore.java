/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.repository.archive;

import com.sitescape.team.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.VersionAttachment;

public interface ArchiveStore {
	
	/**
	 * Archive the file version and return its unique URI or <code>null</code>
	 * if archiving is not supported.
	 * 
	 * @param binder
	 * @param entity
	 * @param v
	 * @return	archive URI or <code>null</code>
	 */
	public String write(Binder binder, DefinableEntity entity, 
			VersionAttachment v) throws UncheckedIOException; 
}
