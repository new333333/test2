/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.domain;
//component of zoneConfig
public class MailConfig  {
	protected boolean sendMailEnabled = true;
	protected boolean postingEnabled = false;
	protected boolean simpleUrlPostingEnabled = false;
	protected Long outgoingAttachmentSizeLimit;
	protected Long outgoingAttachmentSumLimit;
	
	public MailConfig()	{
	}
	public MailConfig(MailConfig src) {
		this.sendMailEnabled = src.sendMailEnabled;
		this.postingEnabled = src.postingEnabled;
		this.simpleUrlPostingEnabled = src.simpleUrlPostingEnabled;
		this.outgoingAttachmentSizeLimit = src.outgoingAttachmentSizeLimit;
		this.outgoingAttachmentSumLimit = src.outgoingAttachmentSumLimit;
	}
	public boolean isSendMailEnabled() {
		return sendMailEnabled;
	}
	public void setSendMailEnabled(boolean sendMailEnabled) {
		this.sendMailEnabled = sendMailEnabled;
	}
	public boolean isPostingEnabled() {
		return postingEnabled;
	}
	public void setPostingEnabled(boolean postingEnabled) {
		this.postingEnabled = postingEnabled;
	}
	public boolean isSimpleUrlPostingEnabled() {
		return simpleUrlPostingEnabled;
	}
	public void setSimpleUrlPostingEnabled(boolean simpleUrlPostingEnabled) {
		this.simpleUrlPostingEnabled = simpleUrlPostingEnabled;
	}
	public Long getOutgoingAttachmentSizeLimit() {
		return outgoingAttachmentSizeLimit;
	}
	public Long getOutgoingAttachmentSizeLimitKb() {
		if (outgoingAttachmentSizeLimit == null) return null;
		return outgoingAttachmentSizeLimit / 1024;
	}
	public void setOutgoingAttachmentSizeLimit(Long outgoingAttachmentSizeLimit) {
		this.outgoingAttachmentSizeLimit = outgoingAttachmentSizeLimit;
	}
	public Long getOutgoingAttachmentSumLimit() {
		return outgoingAttachmentSumLimit;
	}
	public Long getOutgoingAttachmentSumLimitKb() {
		if (outgoingAttachmentSumLimit == null) return null;
		return outgoingAttachmentSumLimit / 1024;
	}
	public void setOutgoingAttachmentSumLimit(Long outgoingAttachmentSumLimit) {
		this.outgoingAttachmentSumLimit = outgoingAttachmentSumLimit;
	}

}
