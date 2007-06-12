package com.sitescape.team.util;

public interface StatusTicket {

	/**
	 * Returns the unique ID of the ticket.
	 * 
	 * @return
	 */
	public String getId();
	
	/**
	 * Update the ticket with new status.
	 * 
	 * @param status
	 * @return
	 */
	public void setStatus(String status);
	
	/**
	 * Retrieve the latest status.
	 * 
	 * @return
	 */
	public String getStatus();
	
	/**
	 * Done with the ticket.
	 * 
	 * @return
	 */
	public void done();
	
	public static final StatusTicket NULL_TICKET = new StatusTicket()
	{
		public String getId() { return "__NULL__"; }
		public void setStatus(String status) {}
		public String getStatus() { return ""; }
		public void done() {}
	};
}
