package com.asset.vodafone.npc.core.models;

import javax.xml.bind.JAXBException;

import com.asset.vodafone.npc.webservice.xsd.portmessage.BulkSyncMessageType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.ObjectFactory;


public class BulkSyncMessageModel extends NPCMessageModel {

	private BulkSyncMessageModel() {
	}

	private BulkSyncMessageModel(NPCMessageModel npcMessageModel) {
		setNPCMessageID(npcMessageModel.getNPCMessageID());
		setPort(npcMessageModel.isPort());
		setSent(npcMessageModel.isSent());
		setTransactionDate(npcMessageModel.getTransactionDate());
		setReturnedMessage(npcMessageModel.getReturnedMessage());
		setCurrentMessageMaxDate(npcMessageModel.getCurrentMessageMaxDate());
		setCurrentMessageMinDate(npcMessageModel.getCurrentMessageMinDate());
		setCreatedUser(npcMessageModel.getCreatedUser());
		setCreatedDate(npcMessageModel.getCreatedDate());
		setNextMessageMaxDate(npcMessageModel.getNextMessageMaxDate());
		setNextMessageMinDate(npcMessageModel.getNextMessageMinDate());
		setUserCommnet(npcMessageModel.getUserCommnet());
		setMessageXML(npcMessageModel.getMessageXML());
	}

	public static BulkSyncMessageModel createBulkSyncMessage() throws JAXBException {
		ObjectFactory objectFactory = new ObjectFactory();
		BulkSyncMessageType bulkSyncMessageType = objectFactory.createBulkSyncMessageType();
		BulkSyncMessageModel bulkSyncMessageModel = new BulkSyncMessageModel();
		bulkSyncMessageModel.setBulkSyncMessageType(bulkSyncMessageType);
		return bulkSyncMessageModel;
	}

	public static BulkSyncMessageModel createBulkSyncMessage(NPCMessageModel npcMessageModel) throws JAXBException {
		ObjectFactory objectFactory = new ObjectFactory();
		BulkSyncMessageType bulkSyncMessageType = objectFactory.createBulkSyncMessageType();
		BulkSyncMessageModel bulkSyncMessageModel = new BulkSyncMessageModel(npcMessageModel);
		bulkSyncMessageModel.setBulkSyncMessageType(bulkSyncMessageType);
		return bulkSyncMessageModel;
	}

	public static void set(BulkSyncMessageModel bulkSyncMessageModel, String fieldName, Object fieldValue) {
		if (fieldValue == null)
			return;
		if (fieldName.equals("NPC_Message_ID")) {
			bulkSyncMessageModel.setNPCMessageID((new Long(String.valueOf(fieldValue))).longValue());
			return;
		}
		if (fieldName.equals("MessageCode")) {
			bulkSyncMessageModel.getBulkSyncMessageType().setMessageCode(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("MessageID")) {
			bulkSyncMessageModel.getBulkSyncMessageType().setMessageID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("SyncID")) {
			bulkSyncMessageModel.getBulkSyncMessageType().setSyncID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("StartDate")) {
			bulkSyncMessageModel.getBulkSyncMessageType().setStartDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("EndDate")) {
			bulkSyncMessageModel.getBulkSyncMessageType().setEndDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Comments1")) {
			bulkSyncMessageModel.getBulkSyncMessageType().setComments1(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("Comments2"))
			bulkSyncMessageModel.getBulkSyncMessageType().setComments2(String.valueOf(fieldValue));
	}

	public void setBulkSyncMessageType(BulkSyncMessageType bulkSyncMessageType) {
		this.bulkSyncMessageType = bulkSyncMessageType;
	}

	public BulkSyncMessageType getBulkSyncMessageType() {
		return bulkSyncMessageType;
	}

	private BulkSyncMessageType bulkSyncMessageType;
}
