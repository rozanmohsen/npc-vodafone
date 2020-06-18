package com.asset.vodafone.npc.core.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import com.asset.vodafone.npc.webservice.xsd.portmessage.ObjectFactory;
import com.asset.vodafone.npc.webservice.xsd.portmessage.PortMessageType;

public class PortMessageModel extends NPCMessageModel {

	protected PortMessageModel(NPCMessageModel npcMessageModel) {
		numbersToPortList = new ArrayList<>();
		portExists = true;
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

	protected PortMessageModel() {
		numbersToPortList = new ArrayList<>();
		portExists = true;
	}

	public static PortMessageModel createPortMessage(NPCMessageModel npcMessageModel) throws JAXBException {
		ObjectFactory objectFactory = new ObjectFactory();
		PortMessageType portMessageType = objectFactory.createPortMessageType();
		PortMessageModel portMessageModel = new PortMessageModel(npcMessageModel);
		portMessageModel.setPortMessageType(portMessageType);
		return portMessageModel;
	}

	public static PortMessageModel createPortMessage() throws JAXBException {
		ObjectFactory objectFactory = new ObjectFactory();
		PortMessageType portMessageType = objectFactory.createPortMessageType();
		PortMessageModel portMessageModel = new PortMessageModel();
		portMessageModel.setPortMessageType(portMessageType);
		return portMessageModel;
	}

	public static void set(PortMessageModel portMessageModel, String fieldName, Object fieldValue) {
		if (fieldValue == null)
			return;
		if (fieldName.equals("NPC_MESSAGE_ID")) {
			portMessageModel.setNPCMessageID((new Long(String.valueOf(fieldValue))).longValue());
			return;
		}
		if (fieldName.equals("MESSAGEID")) {
			portMessageModel.getPortMessageType().setMessageID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("INTERNAL_PORT_ID")) {
			portMessageModel.setInternalPortID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("MESSAGECODE")) {
			portMessageModel.getPortMessageType().setMessageCode(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("SERVICETYPE")) {
			portMessageModel.getPortMessageType().setServiceType(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("PORTID")) {
			portMessageModel.getPortMessageType().setPortID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("PORTINGREQUESTFORMID")) {
			portMessageModel.getPortMessageType().setPortReqFormID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("DONORID")) {
			portMessageModel.getPortMessageType().setDonorID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("RECIPIENTID")) {
			portMessageModel.getPortMessageType().setRecipientID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("ORIGINATORID")) {
			portMessageModel.getPortMessageType().setOriginatorID(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("RESPONSEDUEDATE")) {
			portMessageModel.getPortMessageType().setResponseDueDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("NEWROUTE")) {
			portMessageModel.getPortMessageType().setNewRoute(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("NPDUEDATE")) {
			portMessageModel.getPortMessageType().setNpDueDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("REJECTCODE")) {
			portMessageModel.getPortMessageType().setRejectCode(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("REJECTEDMESSAGECODE")) {
			portMessageModel.getPortMessageType().setRejectedMessageCode(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("TRANSFERFEE")) {
			try {
				portMessageModel.getPortMessageType().setTransferFee(new BigDecimal(String.valueOf(fieldValue)));

			} catch (NumberFormatException ex) {
				portMessageModel.getPortMessageType().setTransferFee(null);
			}
			return;
		}
		if (fieldName.equals("AVERAGEINVOICEFEE")) {
			try {
				portMessageModel.getPortMessageType().setAverageInvoiceFee(new BigDecimal(String.valueOf(fieldValue)));

			} catch (NumberFormatException ex) {
				portMessageModel.getPortMessageType().setAverageInvoiceFee(null);
			}
			return;
		}
		if (fieldName.equals("COMMENTS1")) {
			portMessageModel.getPortMessageType().setComments1(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("COMMENTS2")) {
			portMessageModel.getPortMessageType().setComments2(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("INVOICEDATE")) {
			portMessageModel.getPortMessageType().setInvoiceDate(String.valueOf(fieldValue));
			return;
		}
		if (fieldName.equals("PAYMENTDUE"))
			try {
				portMessageModel.getPortMessageType().setPaymentDue(new BigDecimal(String.valueOf(fieldValue)));

			} catch (NumberFormatException ex) {
				portMessageModel.getPortMessageType().setPaymentDue(null);
			}
	}

	public void setPortMessageType(PortMessageType portMessageType) {
		this.portMessageType = portMessageType;
	}

	public PortMessageType getPortMessageType() {
		return portMessageType;
	}

	public void setSubscriberDataModel(SubscriberDataModel subscriberDataModel) {
		this.subscriberDataModel = subscriberDataModel;
	}

	public SubscriberDataModel getSubscriberDataModel() {
		return subscriberDataModel;
	}

	public List<NumbersToPortModel> getNumbersToPortList() {
		return numbersToPortList;
	}

	public void setNumbersToPortList(List<NumbersToPortModel> arrayList) {
		this.numbersToPortList = (ArrayList<NumbersToPortModel>) arrayList;
	}

	public void setInternalPortID(String internalPortID) {
		this.internalPortID = internalPortID;
	}

	public String getInternalPortID() {
		return internalPortID;
	}

	public void setPortExists(boolean portExists) {
		this.portExists = portExists;
	}

	public boolean isPortExists() {
		if (portMessageType.getPortID() == null || "".equals(portMessageType.getPortID()))
			return false;
		else
			return portExists;
	}

	private PortMessageType portMessageType;
	private SubscriberDataModel subscriberDataModel;
	private ArrayList<NumbersToPortModel> numbersToPortList;
	private String internalPortID;
	private boolean portExists;
}
