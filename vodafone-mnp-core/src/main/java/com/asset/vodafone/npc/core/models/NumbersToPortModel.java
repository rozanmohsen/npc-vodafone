package com.asset.vodafone.npc.core.models;

import javax.xml.bind.JAXBException;

import com.asset.vodafone.npc.webservice.xsd.portmessage.NumberDataType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.ObjectFactory;

public class NumbersToPortModel {

	public static NumbersToPortModel createNumbersToPort() throws JAXBException {
		ObjectFactory objectFactory = new ObjectFactory();
		NumbersToPortModel numbersToPortModel = new NumbersToPortModel();
		numbersToPortModel.setNumberDataType(objectFactory.createNumberDataType());
		return numbersToPortModel;
	}

	public static void set(NumbersToPortModel numbersToPortModel, String fieldName, Object fieldValue) {
		if (fieldValue == null)
			return;
		if (fieldName.equals("NPC_MESSAGE_ID")) {
			numbersToPortModel.setNPCMessageID(((Long) fieldValue).longValue());
			return;
		}
		if (fieldName.equals("NUMBERFROM")) {
			numbersToPortModel.getNumberDataType().setNumberFrom(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("NUMBERTO")) {
			numbersToPortModel.getNumberDataType().setNumberTo(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("DATANUMBER")) {
			numbersToPortModel.getNumberDataType().setDataNumber(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("FAXNUMBER"))
			numbersToPortModel.getNumberDataType().setFaxNumber(String.valueOf(fieldValue));
	}

	public void setNumberDataType(NumberDataType numberDataType) {
		this.numberDataType = numberDataType;
	}

	public NumberDataType getNumberDataType() {
		return numberDataType;
	}

	public void setNPCMessageID(long npcMessageID) {
		this.npcMessageID = npcMessageID;
	}

	public long getNPCMessageID() {
		return npcMessageID;
	}

	private NumberDataType numberDataType;
	private long npcMessageID;
}
