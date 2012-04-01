package org.kablink.teaming.webdav;

import java.util.Date;

import org.kablink.teaming.util.ReleaseInfo;

import com.bradmcevoy.http.PropFindableResource;

public class AliasResource extends WebdavResource implements PropFindableResource {

	static final String ID = "wda";
	static final String WEBDAV_PATH = "/wda";

	public AliasResource(WebdavResourceFactory factory) {
		super(factory);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	@Override
	public String getName() {
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
		return ReleaseInfo.getBuildDate(); // This is as good as any other random date
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.webdav.WebdavResource#getWebdavPath()
	 */
	@Override
	public String getWebdavPath() {
		return WEBDAV_PATH;
	}

}
