/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */

package org.kablink.teaming.rest.v1.model;

import org.kablink.teaming.rest.v1.annotations.Undocumented;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Information about a file or folder that has been shared.
 */
@XmlRootElement(name="share")
public class Share extends BaseRestObject {
    private Long id;
    protected String comment;
	private LongIdLinkPair sharer;
    protected Date startDate;
    protected Integer daysToExpire;
    protected Date endDate;
    private ShareRecipient recipient;
    private EntityId sharedEntity;
    private String role;
    private Boolean canShare;
    private Access access;

    /**
     * The shared item's public URLs.
     */
    @XmlElementWrapper(name="permalinks")
    @XmlElement(name="permalink")
    private List<Link> additionalPermaLinks;

    /**
     * Note entered by the sharer.
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * A reference to the user who shared the item.
     */
    public LongIdLinkPair getSharer() {
		return sharer;
	}

	public void setSharer(LongIdLinkPair sharer) {
		this.sharer = sharer;
	}

    /**
     * The date the item was shared.
     */
    @XmlElement(name = "sharing_date")
    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

    /**
     * Days until the share expires.  This is the initial value set by the sharer, not necessarily the current number of days remaining.
     * When posting a share with <code>days_to_expire</code> to the REST API, the server calculates the expiration date and sets <code>expiration</code>
     * accordingly
     */
    @XmlElement(name = "days_to_expire")
    public Integer getDaysToExpire() {
        return daysToExpire;
    }

    public void setDaysToExpire(Integer daysToExpire) {
        this.daysToExpire = daysToExpire;
    }

    /**
     * Date and time when the share expires.  When posting a share to the REST API, only one of <code>days_to_expire</code> and <code>expiration</code>
     * should be specified.
     */
    @XmlElement(name = "expiration")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * ID of the share.
     */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Reference to the recipient of the shared item.
     */
    public ShareRecipient getRecipient() {
        return recipient;
    }

    public void setRecipient(ShareRecipient recipient) {
        this.recipient = recipient;
    }

    /**
     * Reference to the shared item.
     * @return
     */
    @XmlElement(name = "shared_entity")
    public EntityId getSharedEntity() {
        return sharedEntity;
    }

    public void setSharedEntity(EntityId sharedEntity) {
        this.sharedEntity = sharedEntity;
    }

    @Undocumented
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Undocumented
    @XmlElement(name = "can_share")
    public Boolean isCanShare() {
        return canShare;
    }

    public void setCanShare(Boolean canShare) {
        this.canShare = canShare;
    }

    /**
     * The access granted to the recipient.
     */
    @XmlElement(name = "access")
    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @XmlTransient
    public List<Link> getAdditionalPermaLinks() {
        return additionalPermaLinks;
    }

    public void setAdditionalPermaLinks(List<Link> additionalPermaLinks) {
        this.additionalPermaLinks = additionalPermaLinks;
    }

    public void addAdditionalPermaLink(String relation, String uri) {
        addAdditionalPermaLink(new Link(relation, uri));
    }

    public void addAdditionalPermaLink(String uri) {
        addAdditionalPermaLink(new Link(null, uri));
    }

    public void addAdditionalPermaLink(Link link) {
        if (additionalPermaLinks ==null) {
            additionalPermaLinks = new ArrayList<Link>();
        }
        additionalPermaLinks.add(link);
    }
}
