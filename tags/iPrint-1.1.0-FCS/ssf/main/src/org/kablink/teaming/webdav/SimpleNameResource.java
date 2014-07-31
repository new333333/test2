package org.kablink.teaming.webdav;

import java.util.Date;

import org.kablink.teaming.util.ReleaseInfo;

import com.bradmcevoy.http.PropFindableResource;

public class SimpleNameResource extends WebdavResource implements PropFindableResource {

	static final String ID = "davs";
	static final String WEBDAV_PATH = "/davs";

	public SimpleNameResource(WebdavResourceFactory factory) {
		super(factory, WEBDAV_PATH, ID);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return getCreateDate();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(ReleaseInfo.getBuildDate()); // This is as good as any other random date
	}

}
