package com.asset.vodafone.npc.core.models;

import java.math.BigDecimal;

public class SyncModel {

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setBulkSyncIDNumber(String bulkSyncIDNumber) {
		this.bulkSyncIDNumber = bulkSyncIDNumber;
	}

	public String getBulkSyncIDNumber() {
		return bulkSyncIDNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setDonorID(String donorID) {
		this.donorID = donorID;
	}

	public String getDonorID() {
		return donorID;
	}

	public void setRecipientID(String recipientID) {
		this.recipientID = recipientID;
	}

	public String getRecipientID() {
		return recipientID;
	}

	public void setNewRoute(String newRoute) {
		this.newRoute = newRoute;
	}

	public String getNewRoute() {
		return newRoute;
	}

	public void setTimeStamp(BigDecimal timeStamp) {
		this.timeStamp = timeStamp;
	}

	public BigDecimal getTimeStamp() {
		return timeStamp;
	}

	private String msisdn;
	private String bulkSyncIDNumber;
	private String idNumber;
	private String donorID;
	private String recipientID;
	private String newRoute;
	private BigDecimal timeStamp;
}
