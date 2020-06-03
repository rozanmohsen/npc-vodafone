package com.asset.vodafone.npc.core.models;

public class FailedMessagesModel {



	public void setNPCMessageID(long npcMessageID) {
		this.npcMessageID = npcMessageID;
	}

	public long getNPCMessageID() {
		return npcMessageID;
	}

	public void setDateOfFailure(String dateOfFailure) {
		this.dateOfFailure = dateOfFailure;
	}

	public String getDateOfFailure() {
		return dateOfFailure;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	private long npcMessageID;
	private String dateOfFailure;
	private String reason;
}
