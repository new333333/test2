package com.sitescape.team.remoting.ws.model;

public class Workflow {
	private String definitionId;
	private Long tokenId;
	private String state;
	private String threadName;
	private Timestamp modification;
	public String getDefinitionId() {
		return definitionId;
	}
	public void setDefinitionId(String definitionId) {
		this.definitionId = definitionId;
	}	
	public Long getTokenId() {
 		return tokenId;
 	}
 	public void setTokenId(Long tokenId) {
 		this.tokenId = tokenId;
 	}
 	public String getState() {
 		return state;
 	}
 	public void setState(String state) {
 		this.state = state;
 	}
	public String getThreadName() {
 		return threadName;
 	}
 	public void setThreadName(String threadName) {
 		this.threadName = threadName;
 	}
	public Timestamp getModification() {
		return modification;
	}
	public void setModification(Timestamp modification) {
		this.modification = modification;
	}

}
