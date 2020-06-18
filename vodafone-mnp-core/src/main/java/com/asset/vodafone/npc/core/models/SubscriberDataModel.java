package com.asset.vodafone.npc.core.models;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBException;

import com.asset.vodafone.npc.webservice.xsd.portmessage.ObjectFactory;
import com.asset.vodafone.npc.webservice.xsd.portmessage.SubscriberDataType;

public class SubscriberDataModel {

	public static SubscriberDataModel createSubscriberData() throws JAXBException {
		ObjectFactory objectFactory = new ObjectFactory();
		SubscriberDataModel subscriberDataModel = new SubscriberDataModel();
		subscriberDataModel.setSubscriberDataType(objectFactory.createSubscriberDataType());
		return subscriberDataModel;
	}

	public static void set(SubscriberDataModel subscriberDataModel, String fieldName, Object fieldValue) {
		if (fieldValue == null)
			return;
		if (fieldName.equals("NPC_MESSAGE_ID"))
			subscriberDataModel.setNPCMessageID(((Long) fieldValue).longValue());
		else if (fieldName.equals("COMPANYFLAG"))
			subscriberDataModel.getSubscriberDataType().setCompanyFlag(fieldValue.toString());
		else if (fieldName.equals("CORPORATIONNAME"))
			subscriberDataModel.getSubscriberDataType().setCorporationName(String.valueOf(fieldValue));
		else if (fieldName.equals("NAME")) {

			subscriberDataModel.getSubscriberDataType().setName(
					new String(String.valueOf(fieldValue).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));

			subscriberDataModel.getSubscriberDataType().setName(String.valueOf(fieldValue));

		} else if (fieldName.equals("ACCOUNTNUM"))
					subscriberDataModel.getSubscriberDataType().setAccountNum(String.valueOf(fieldValue));
		else if (fieldName.equals("SIMCARDNUM"))
			subscriberDataModel.getSubscriberDataType().setSIMCardNum(String.valueOf(fieldValue));
		else if (fieldName.equals("NIC"))
			subscriberDataModel.getSubscriberDataType().setNIC(String.valueOf(fieldValue));
		else if (fieldName.equals("CNIC"))
			subscriberDataModel.getSubscriberDataType().setCNIC(String.valueOf(fieldValue));
		else if (fieldName.equals("PASSPORTNUM"))
			subscriberDataModel.getSubscriberDataType().setPassportNum(String.valueOf(fieldValue));
		else if (fieldName.equals("OTHERID"))
			subscriberDataModel.getSubscriberDataType().setOtherId(String.valueOf(fieldValue));
		else if (fieldName.equals("DATEOFBIRTH"))
			subscriberDataModel.getSubscriberDataType().setDateOfBirth(String.valueOf(fieldValue));
		else if (fieldName.equals("CONTACTPHONE"))
			subscriberDataModel.getSubscriberDataType().setContactPhone(String.valueOf(fieldValue));
		else if (fieldName.equals("FAX"))
			subscriberDataModel.getSubscriberDataType().setFax(String.valueOf(fieldValue));
		else if (fieldName.equals("CITY"))
			subscriberDataModel.getSubscriberDataType().setCity(String.valueOf(fieldValue));
		else if (fieldName.equals("STREET"))
			subscriberDataModel.getSubscriberDataType().setStreet(String.valueOf(fieldValue));
		else if (fieldName.equals("SUBSCRIBER_NUMBER"))
			subscriberDataModel.getSubscriberDataType().setNumber(String.valueOf(fieldValue));
		else if (fieldName.equals("LOCALITY"))
			subscriberDataModel.getSubscriberDataType().setLocality(String.valueOf(fieldValue));
		else if (fieldName.equals("POSTCODE"))
			subscriberDataModel.getSubscriberDataType().setPostCode(String.valueOf(fieldValue));
		else if (fieldName.equals("GROUP_ID"))
			subscriberDataModel.getSubscriberDataType().setGroupID(String.valueOf(fieldValue));
		else if (fieldName.equals("AUTHORIZED_PERSON_NAME"))
			subscriberDataModel.getSubscriberDataType().setAuthorizedPersonName(String.valueOf(fieldValue));
		else if (fieldName.equals("CORPORATION_ADDRESS"))
			subscriberDataModel.getSubscriberDataType().setCorporationAddress(String.valueOf(fieldValue));
		else if (fieldName.equals("COMMERCIAL_REGISTRATION_NUM"))
			subscriberDataModel.getSubscriberDataType().setCommercialRegistrationNum(String.valueOf(fieldValue));
		else if (fieldName.equals("TAX_REGISTRATION_NUM"))
			subscriberDataModel.getSubscriberDataType().setTaxRegistrationNum(String.valueOf(fieldValue));
	}

	public void setSubscriberDataType(SubscriberDataType subscriberDataType) {
		this.subscriberDataType = subscriberDataType;
	}

	public SubscriberDataType getSubscriberDataType() {
		return subscriberDataType;
	}

	public void setNPCMessageID(long npcMessageID) {
		this.npcMessageID = npcMessageID;
	}

	public long getNPCMessageID() {
		return npcMessageID;
	}

	private SubscriberDataType subscriberDataType;
	private long npcMessageID;
}
