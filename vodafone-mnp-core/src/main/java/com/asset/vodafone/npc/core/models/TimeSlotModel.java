package com.asset.vodafone.npc.core.models;

public class TimeSlotModel {

	public TimeSlotModel(String timeSlotName) {
		this.timeSlotName = timeSlotName;
	}

	public void setTimeUnitID(short timeUnitID) {
		this.timeUnitID = timeUnitID;
	}

	public short getTimeUnitID() {
		return timeUnitID;
	}

	public void setTimeUnitName(String timeUnitName) {
		this.timeUnitName = timeUnitName;
	}

	public String getTimeUnitName() {
		return timeUnitName;
	}

	public void setTimeSlotName(String timeSlotName) {
		this.timeSlotName = timeSlotName;
	}

	public String getTimeSlotName() {
		return timeSlotName;
	}

	public void setTimeValue(int timeValue) {
		this.timeValue = timeValue;
	}

	public int getTimeValue() {
		return timeValue;
	}

	public void setBusiness(boolean business) {
		this.business = business;
	}

	public boolean isBusiness() {
		return business;
	}

	private short timeUnitID;
	private String timeUnitName;
	private String timeSlotName;
	private int timeValue;
	private boolean business;
}
