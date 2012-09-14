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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.Calendar;
import java.util.Date;

/**
 * User: david
 * Date: 7/25/12
 * Time: 11:38 AM
 */
@XmlRootElement(name="share")
public class Share extends BaseRestObject {
    private Long id;
    protected String comment;
	private LongIdLinkPair sharer;
    protected int daysToExpire;
    protected Date startDate;
    protected Date endDate;
    private EntityId recipient;
    private EntityId sharedEntity;
    private String role;
    private Boolean canShare;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LongIdLinkPair getSharer() {
		return sharer;
	}

	public void setSharer(LongIdLinkPair sharer) {
		this.sharer = sharer;
	}

    @XmlElement(name = "days_to_expire")
    public int getDaysToExpire() {
        return daysToExpire;
    }

    public void setDaysToExpire(int daysToExpire) {
        this.daysToExpire = daysToExpire;
    }

    @XmlElement(name = "sharing_date")
    public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@XmlElement(name = "expiration")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityId getRecipient() {
        return recipient;
    }

    public void setRecipient(EntityId recipient) {
        this.recipient = recipient;
    }

    @XmlElement(name = "shared_entity")
    public EntityId getSharedEntity() {
        return sharedEntity;
    }

    public void setSharedEntity(EntityId sharedEntity) {
        this.sharedEntity = sharedEntity;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @XmlElement(name = "can_share")
    public Boolean isCanShare() {
        return canShare;
    }

    public void setCanShare(Boolean canShare) {
        this.canShare = canShare;
    }
}
