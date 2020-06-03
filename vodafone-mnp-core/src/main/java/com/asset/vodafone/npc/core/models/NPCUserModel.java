package com.asset.vodafone.npc.core.models;

public class NPCUserModel {

	

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setArrivalDate(String arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public String getArrivalDate() {
		return arrivalDate;
	}

	public void setForwardDate(String forwardDate) {
		this.forwardDate = forwardDate;
	}

	public String getForwardDate() {
		return forwardDate;
	}

	public void setNpcDataID(long npcDataID) {
		this.npcDataID = npcDataID;
	}

	public long getNpcDataID() {
		return npcDataID;
	}

	public void setLogDate(String logDate) {
		this.logDate = logDate;
	}

	public String getLogDate() {
		return logDate;
	}

	private String username;
	private String arrivalDate;
	private String forwardDate;
	private String logDate;
	private long npcDataID;
}
