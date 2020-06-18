package com.asset.vodafone.npc.core.models;

public class PortDataModel {
	private PortDataModel() {
	}

	public static PortDataModel getInstance(NPCMessageModel npcMessageModel) {
		if (npcMessageModel instanceof PortMessageModel)
			return new PortDataModel((PortMessageModel) npcMessageModel);
		else
			return new PortDataModel();
	}

	public static PortDataModel getInstance() {
		return new PortDataModel();
	}

	private PortDataModel(PortMessageModel portMessageModel) {
		setPortID(portMessageModel.getPortMessageType().getPortID());
		setLastNPCMessageID(portMessageModel.getNPCMessageID());
		setInternalPortID(portMessageModel.getPortMessageType().getMessageID());
		setDonorID(portMessageModel.getPortMessageType().getDonorID());
		setRecipientID(portMessageModel.getPortMessageType().getRecipientID());
		setPortingRequestFormID(portMessageModel.getPortMessageType().getPortReqFormID());
		setNewRoute(portMessageModel.getPortMessageType().getNewRoute());
		setInternalPortID(portMessageModel.getInternalPortID());

		if (portMessageModel.getPortMessageType().getTransferFee() != null)
			setTransferFee(portMessageModel.getPortMessageType().getTransferFee().longValue());
		else
			setTransferFee(-1L);

		if (portMessageModel.getPortMessageType().getAverageInvoiceFee() != null)
			setAverageInvoiceFee(portMessageModel.getPortMessageType().getAverageInvoiceFee().longValue());
		else
			setAverageInvoiceFee(-1L);

		if (portMessageModel.getPortMessageType().getPaymentDue() != null)
			setPaymentDue(portMessageModel.getPortMessageType().getPaymentDue().longValue());
		else
			setPaymentDue(-1L);
		if (portMessageModel.getSubscriberDataModel() != null
				&& portMessageModel.getSubscriberDataModel().getSubscriberDataType() != null) {
			setOldSimNumber(portMessageModel.getSubscriberDataModel().getSubscriberDataType().getSIMCardNum());
			setAccountNumber(portMessageModel.getSubscriberDataModel().getSubscriberDataType().getAccountNum());
		}
		if (portMessageModel.isMessageSuccess())
			setTransactionStatus("Success");
		else
			setTransactionStatus("Failed");
		if (portMessageModel.getReturnedMessage() != null && !"".equals(portMessageModel.getReturnedMessage()))
			setComments(portMessageModel.getReturnedMessage());
		setErrorMessageReceived((short) 0);
	}

	public void copyPortData(PortDataModel portDataModel) {
		if (donorID == null || "".equals(donorID))
			setDonorID(portDataModel.getDonorID());
		if (recipientID == null || "".equals(recipientID))
			setRecipientID(portDataModel.getRecipientID());
		if (portingRequestFormID == null || "".equals(portingRequestFormID))
			setPortingRequestFormID(portDataModel.getPortingRequestFormID());
		if (newRoute == null || "".equals(newRoute))
			setNewRoute(portDataModel.getNewRoute());
		if (transferFee == -1L)
			setTransferFee(portDataModel.getTransferFee());
		if (averageInvoiceFee == -1L)
			setAverageInvoiceFee(portDataModel.getAverageInvoiceFee());
		if (paymentDue == -1L)
			setPaymentDue(portDataModel.getPaymentDue());
	}

	public void setPortDataID(long portDataID) {
		this.portDataID = portDataID;
	}

	public long getPortDataID() {
		return portDataID;
	}

	public void setLastNPCMessageID(long lastNPCMessageID) {
		this.lastNPCMessageID = lastNPCMessageID;
	}

	public long getLastNPCMessageID() {
		return lastNPCMessageID;
	}

	public void setPortStatus(String portStatus) {
		this.portStatus = portStatus;
	}

	public String getPortStatus() {
		return portStatus;
	}

	public void setPortID(String portID) {
		this.portID = portID;
	}

	public String getPortID() {
		return portID;
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

	public void setSr(String sr) {
		this.sr = sr;
	}

	public String getSr() {
		return sr;
	}

	public void setTransferFee(long transferFee) {
		this.transferFee = transferFee;
	}

	public long getTransferFee() {
		return transferFee;
	}

	public void setAverageInvoiceFee(long averageInvoiceFee) {
		this.averageInvoiceFee = averageInvoiceFee;
	}

	public long getAverageInvoiceFee() {
		return averageInvoiceFee;
	}

	public void setPortingRequestFormID(String portingRequestFormID) {
		this.portingRequestFormID = portingRequestFormID;
	}

	public String getPortingRequestFormID() {
		return portingRequestFormID;
	}

	public void setNprNPCMessageID(long nprNPCMessageID) {
		this.nprNPCMessageID = nprNPCMessageID;
	}

	public long getNprNPCMessageID() {
		return nprNPCMessageID;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getComments() {
		return comments;
	}

	public void setErrorMessageReceived(int errorMessageReceived) {
		this.errorMessageReceived = errorMessageReceived;
	}

	public int getErrorMessageReceived() {
		return errorMessageReceived;
	}

	public void setNewRoute(String newRoute) {
		this.newRoute = newRoute;
	}

	public String getNewRoute() {
		return newRoute;
	}

	public void setPaymentDue(long paymentDue) {
		this.paymentDue = paymentDue;
	}

	public long getPaymentDue() {
		return paymentDue;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setInternalPortID(String internalPortID) {
		this.internalPortID = internalPortID;
	}

	public String getInternalPortID() {
		return internalPortID;
	}

	public void setOldSimNumber(String oldSimNumber) {
		this.oldSimNumber = oldSimNumber;
	}

	public String getOldSimNumber() {
		return oldSimNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	private long portDataID;
	private String comments;
	private String portStatus;
	private String internalPortID;
	private String portID;
	private String donorID;
	private String recipientID;
	private String sr;
	private String newRoute;
	private String portingRequestFormID;
	private String createDate;
	private String oldSimNumber;
	private String accountNumber;
	private String transactionStatus;
	private long transferFee;
	private long averageInvoiceFee;
	private long paymentDue;
	private long nprNPCMessageID;
	private long lastNPCMessageID;
	private int errorMessageReceived;
}
