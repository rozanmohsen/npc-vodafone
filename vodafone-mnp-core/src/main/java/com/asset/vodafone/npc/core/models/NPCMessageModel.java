package com.asset.vodafone.npc.core.models;

public class NPCMessageModel {

	public NPCMessageModel() {
		messageSuccess = true;
		transactionDate = "";
		returnedMessage = "";
		currentMessageMaxDate = "";
		currentMessageMinDate = "";
		createdUser = "";
		createdDate = "";
		nextMessageMaxDate = "";
		nextMessageMinDate = "";
		userCommnet = "";
		messageXML = "";
		pickedBy = "";
	}

	public NPCMessageModel(long npcMessageID) {
		messageSuccess = true;
		transactionDate = "";
		returnedMessage = "";
		currentMessageMaxDate = "";
		currentMessageMinDate = "";
		createdUser = "";
		createdDate = "";
		nextMessageMaxDate = "";
		nextMessageMinDate = "";
		userCommnet = "";
		messageXML = "";
		pickedBy = "";
		this.npcMessageID = npcMessageID;
	}

	public NPCMessageModel(long npcMessageID, boolean isPort) {
		messageSuccess = true;
		transactionDate = "";
		returnedMessage = "";
		currentMessageMaxDate = "";
		currentMessageMinDate = "";
		createdUser = "";
		createdDate = "";
		nextMessageMaxDate = "";
		nextMessageMinDate = "";
		userCommnet = "";
		messageXML = "";
		pickedBy = "";
		this.npcMessageID = npcMessageID;
		port = isPort;
	}

	public static void set(NPCMessageModel npcMessageModel, String fieldName, Object fieldValue) {
		if (fieldValue == null)
			return;
		if (fieldName.equals("NPC_MESSAGE_ID")) {
			npcMessageModel.setNPCMessageID(((Long) fieldValue).longValue());
			return;
		}
		if (fieldName.equals("IsPort")) {
			npcMessageModel.setPort(((Boolean) fieldValue).booleanValue());
			return;
		}
		if (fieldName.equals("Sent")) {
			npcMessageModel.setSent(((Boolean) fieldValue).booleanValue());
			return;
		}
		if (fieldName.equals("Transaction_Date")) {
			npcMessageModel.setTransactionDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("ReturnMessage")) {
			npcMessageModel.setReturnedMessage(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Current_Message_Max_Date")) {
			npcMessageModel.setCurrentMessageMaxDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Current_Message_Min_Date")) {
			npcMessageModel.setCurrentMessageMinDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Create_User")) {
			npcMessageModel.setCreatedUser(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Create_Date")) {
			npcMessageModel.setCreatedDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Next_Message_Max_Date")) {
			npcMessageModel.setNextMessageMaxDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Next_Message_Min_Date")) {
			npcMessageModel.setNextMessageMinDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("User_Comment")) {
			npcMessageModel.setUserCommnet(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("MessageXML")) {
			npcMessageModel.setMessageXML(String.valueOf(fieldValue));

		}
		if (fieldName.equals("Picked_By"))
			npcMessageModel.setPickedBy(String.valueOf(fieldValue));
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}

	public boolean isSent() {
		return sent;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setReturnedMessage(String returnedMessage) {
		this.returnedMessage = returnedMessage;
	}

	public String getReturnedMessage() {
		return returnedMessage;
	}

	public void setCurrentMessageMaxDate(String currentMessageMaxDate) {
		this.currentMessageMaxDate = currentMessageMaxDate;
	}

	public String getCurrentMessageMaxDate() {
		return currentMessageMaxDate;
	}

	public void setCurrentMessageMinDate(String currentMessageMinDate) {
		this.currentMessageMinDate = currentMessageMinDate;
	}

	public String getCurrentMessageMinDate() {
		return currentMessageMinDate;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setNextMessageMaxDate(String nextMessageMaxDate) {
		this.nextMessageMaxDate = nextMessageMaxDate;
	}

	public String getNextMessageMaxDate() {
		return nextMessageMaxDate;
	}

	public void setNextMessageMinDate(String nextMessageMinDate) {
		this.nextMessageMinDate = nextMessageMinDate;
	}

	public String getNextMessageMinDate() {
		return nextMessageMinDate;
	}

	public void setUserCommnet(String userCommnet) {
		this.userCommnet = userCommnet;
	}

	public String getUserCommnet() {
		return userCommnet;
	}

	public void setMessageXML(String messageXML) {
		this.messageXML = messageXML;
	}

	public String getMessageXML() {
		return messageXML;
	}

	public void setNPCMessageID(long npcMessageID) {
		this.npcMessageID = npcMessageID;
	}

	public long getNPCMessageID() {
		return npcMessageID;
	}

	public String getPickedBy() {
		return pickedBy;
	}

	public void setPort(boolean port) {
		this.port = port;
	}

	public boolean isPort() {
		return port;
	}

	public void setMessageSuccess(boolean messageSuccess) {
		this.messageSuccess = messageSuccess;
	}

	public boolean isMessageSuccess() {
		return messageSuccess;
	}

	public void setPickedBy(String pickedBy) {
		this.pickedBy = pickedBy;
	}

	private long npcMessageID;
	private boolean port;
	private boolean sent;
	private boolean messageSuccess;
	private String transactionDate;
	private String returnedMessage;
	private String currentMessageMaxDate;
	private String currentMessageMinDate;
	private String createdUser;
	private String createdDate;
	private String nextMessageMaxDate;
	private String nextMessageMinDate;
	private String userCommnet;
	private String messageXML;
	private String pickedBy;

}
