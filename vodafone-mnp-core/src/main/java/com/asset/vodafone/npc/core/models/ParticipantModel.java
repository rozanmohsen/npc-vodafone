package com.asset.vodafone.npc.core.models;

public class ParticipantModel {

	

	public void setParticipantID(String participantID) {
		this.participantID = participantID;
	}

	public String getParticipantID() {
		return participantID;
	}

	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}

	public String getParticipantName() {
		return participantName;
	}

	private String participantID;
	private String participantName;
}
