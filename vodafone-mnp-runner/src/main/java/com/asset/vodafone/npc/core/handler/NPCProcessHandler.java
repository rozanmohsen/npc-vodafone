package com.asset.vodafone.npc.core.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.ws.client.WebServiceIOException;

import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.models.BulkSyncMessageModel;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.models.NumbersToPortModel;
import com.asset.vodafone.npc.core.models.PortDataModel;
import com.asset.vodafone.npc.core.models.PortMessageModel;
import com.asset.vodafone.npc.core.models.SubscriberDataModel;
import com.asset.vodafone.npc.core.models.SyncHistoryModel;
import com.asset.vodafone.npc.core.models.SyncModel;
import com.asset.vodafone.npc.core.service.NPCService;
import com.asset.vodafone.npc.webservice.client.NPCWebserviceProxy;
import com.asset.vodafone.npc.webservice.client.NPCWebserviceProxyConfiguration;
import com.asset.vodafone.npc.webservice.wsdl.ProcessNPCMsg;
import com.asset.vodafone.npc.webservice.xsd.bulksyncmessage.ActivatedNumberType;
import com.asset.vodafone.npc.webservice.xsd.bulksyncmessage.NPCBulkSyncData;
import com.asset.vodafone.npc.webservice.xsd.portmessage.BulkSyncMessageType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NPCData;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NPCMessageType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NumberDataType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.ObjectFactory;
import com.asset.vodafone.npc.webservice.xsd.portmessage.PortMessageType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.SubscriberDataType;
import com.mega.common.encryption.blowfish.BinConverter;
import com.mega.common.encryption.blowfish.BlowfishHelper;

public class NPCProcessHandler {
	private static NPCProcessHandler npcprocesshandler = null;
	private static final Logger logger = LoggerFactory.getLogger(NPCProcessHandler.class.getName());
	private static ResourceBundle npcProperties = null;
	private PortMessageModel portMessageModel = null;
	NPCService npcService;

	private NPCProcessHandler() throws Exception {

		try {
			loadNPCPropertiesFile("npc_web_service.properties");
		} catch (NPCException e) {

			logger.error("Error While Loading Properties File", e);
			throw new NPCException(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE);
		}

	}

	/**
	 * getInstance Method used to get NPCProcessHandler Instance
	 * 
	 * @return object of NPCProcessHandler Class
	 * @throws NPCException
	 * 
	 */
	public static synchronized NPCProcessHandler getInstance() throws Exception {
		if (npcprocesshandler == null) {
			return new NPCProcessHandler();
		} else {
			return npcprocesshandler;
		}

	}

	/**
	 * finalize Method used to release connection of database
	 */

	public void releaseConnection() {
		try {
			if (npcService != null)
				npcService.releaseConnection();
			logger.debug("Releasing the Database Connection..");

		} catch (NPCException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * Load properties file method to read properties file
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static void loadNPCPropertiesFile(String fileName) throws NPCException {
		FileInputStream inputStream = null;

		try {
			inputStream = new FileInputStream(fileName);
			npcProperties = new PropertyResourceBundle(inputStream);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE, ex);
			throw new NPCException(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);

			}
		}
	}

	/**
	 * Method to Send xml message to NTRA web service through external service
	 * endpoint configured in 'npc_web_service' properties file or Environment
	 * Variables create file 'NPC_Process_Info.log' save the messages sent to NTRA
	 * 
	 * @param username
	 * @param password
	 * @param xmlMsg
	 * @return response from NTRA web service
	 * @throws NPCException
	 */
	public String sendMessage(String username, byte[] password, String xmlMsg, boolean isExternal) throws NPCException {
		String endpoint = "";

		if (isExternal) {
			endpoint = System.getenv("EXTERNAL_SERVICE_ENDPOINT");
			if (endpoint == null)
				endpoint = npcProperties.getString("EXTERNAL_SERVICE_ENDPOINT");
		} else {
			endpoint = System.getenv("INTERNAL_SERVICE_ENDPOINT");
			if (endpoint == null)
				endpoint = npcProperties.getString("INTERNAL_SERVICE_ENDPOINT");
		}
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(
				NPCWebserviceProxyConfiguration.class);
		NPCWebserviceProxy ntraWebserviceProxy = ctx.getBean(NPCWebserviceProxy.class);
		ntraWebserviceProxy.setDefaultUri(endpoint);
		ProcessNPCMsg req = new ProcessNPCMsg();
		req.setString1(username);
		req.setArrayOfbyte2(password);
		req.setString3(xmlMsg);

		try {

			ntraWebserviceProxy.setDefaultUri(endpoint);
			logger.debug("Sending NPC Message to NTRA for UserName : {} ", username);
			logger.debug("Calling NTRA Web Service EndPoint : {} ", endpoint);

			return ntraWebserviceProxy.processNPCMsg(req).getResult();

		} catch (WebServiceIOException e) {

			logger.error(e.getMessage(), e);
			throw new NPCException(e, NPCException.NPC_WEB_SERVICE_ERROR_CODE, "Error in sending npc message");
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			throw new NPCException(ex, NPCException.NPC_WEB_SERVICE_ERROR_CODE, "Error in sending npc message");
		} finally {
			ctx.close();
		}
	}

	/**
	 * getNPCMEssage XML method used to convert xml message to an object
	 *
	 * @param npcData
	 * @return
	 * @throws NPCException
	 */
	private String getNPCMessageXML(NPCData npcData) throws NPCException, Exception {
		ByteArrayOutputStream outputSream = null;
		try {
			outputSream = new ByteArrayOutputStream();
			JAXBContext jaxbContext = JAXBContext.newInstance(
					"com.asset.vodafone.npc.webservice.xsd.portmessage:com.asset.vodafone.npc.webservice.xsd.bulksyncmessage");
			Marshaller marshaller = jaxbContext.createMarshaller();
			logger.debug("Loading XSD schema files to validate NPC message");
			InputStream portMessage = this.getClass().getClassLoader().getResourceAsStream("xsd/portmessage.xsd");

			InputStream bulkSyncMessage = this.getClass().getClassLoader()
					.getResourceAsStream("xsd/bulksyncmessage.xsd");

			if (portMessage == null || bulkSyncMessage == null) {
				throw new FileNotFoundException("XSD Schema file not found!");
			}

			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

			Schema xsdSchema = schemaFactory
					.newSchema(new Source[] { new StreamSource(portMessage), new StreamSource(bulkSyncMessage) });
			logger.debug("Start validating NPC message against xsd schema");
			marshaller.setSchema(xsdSchema);

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			marshaller.marshal(npcData, outputSream);
			logger.debug("validating NPC message against XSD schema has been done successfully");
			try {

				return outputSream.toString("utf-8");

			} catch (UnsupportedEncodingException ex) {
				return outputSream.toString();

			}
		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);

			try {

				outputSream.close();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
				throw new NPCException(ex);
			}
			throw new NPCException(e, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE, "Cannot Create JAXB Object");
		}
	}

	/**
	 * method to return NPCData object from portMessageModel
	 * 
	 * @param portMessageModel
	 * @return
	 * @throws NPCException
	 * @throws JAXBException
	 */
	private NPCData processPortMessage(PortMessageModel portMessageModel) throws NPCException, JAXBException {
		SubscriberDataModel subscriberDataModel = null;
		ArrayList<NumbersToPortModel> numbersToPortList = null;
		NPCData npcData = null;
		PortMessageType portMessageType = null;
		NPCMessageType npcMessageType = null;
		SubscriberDataType subscriberDataType = null;
		subscriberDataModel = portMessageModel.getSubscriberDataModel();
		numbersToPortList = (ArrayList<NumbersToPortModel>) portMessageModel.getNumbersToPortList();
		ObjectFactory objectFactory = new ObjectFactory();
		npcData = objectFactory.createNPCData();
		npcMessageType = objectFactory.createNPCMessageType();
		portMessageType = portMessageModel.getPortMessageType();
		subscriberDataType = subscriberDataModel.getSubscriberDataType();
		portMessageType.setSubscriberData(subscriberDataType);
		List<NumberDataType> numbersDataToPort = portMessageType.getNumbersToPort();
		for (int i = 0; i < numbersToPortList.size(); i++)
			numbersDataToPort.add(((NumbersToPortModel) numbersToPortList.get(i)).getNumberDataType());

		List<Object> portMessages = npcMessageType.getPortMessageOrBulkSyncMessage();
		portMessages.add(portMessageType);
		npcData.setNPCMessages(npcMessageType);
		return npcData;
	}

	/**
	 * Method to return NPCData object BulkSyncMessageModel
	 * 
	 * @param bulkSyncMessageModel
	 * @return
	 * @throws NPCException
	 * @throws JAXBException
	 */
	private NPCData processBulkSyncMessage(BulkSyncMessageModel bulkSyncMessageModel) {
		NPCData npcData = null;
		NPCMessageType npcMessageType = null;
		BulkSyncMessageType bulkSyncMessageType = null;
		ObjectFactory objectFactory = new ObjectFactory();
		npcData = objectFactory.createNPCData();
		npcMessageType = objectFactory.createNPCMessageType();
		bulkSyncMessageType = bulkSyncMessageModel.getBulkSyncMessageType();
		List<Object> bulkSyncMessages = npcMessageType.getPortMessageOrBulkSyncMessage();
		bulkSyncMessages.add(bulkSyncMessageType);
		npcData.setNPCMessages(npcMessageType);
		return npcData;
	}

	/**
	 * Method sendPendingMessage used to send xml Message to NTRA Web service
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	private void sendPendingMessage(NPCMessageModel npcMessageModel) throws NPCException, Exception {
		String internalUserName = System.getenv("INTERNAL_USER_NAME");
		String internalPassword = System.getenv("INTERNAL_PASSWORD");
		NPCData npcData = null;
		if (internalUserName == null)
			internalUserName = npcProperties.getString("INTERNAL_USER_NAME");
		if (internalPassword == null)
			internalPassword = npcProperties.getString("INTERNAL_PASSWORD");
		try {
			if (npcMessageModel instanceof PortMessageModel)
				npcData = processPortMessage((PortMessageModel) npcMessageModel);
			else if (npcMessageModel instanceof BulkSyncMessageModel)
				npcData = processBulkSyncMessage((BulkSyncMessageModel) npcMessageModel);
		} catch (JAXBException ex) {
			throw new NPCException(ex, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE, "Cannot Create JAXB Object");
		}
		String messageXML = getNPCMessageXML(npcData);

		logger.debug("Creating Message XML has done successfully..");
		String username = decrypt(internalUserName);
		byte[] password = decrypt(internalPassword).getBytes();

		String returnedMessage = "";
		try {
			logger.debug("Start sending NPC message...");
			logger.info("Sending NPC Message to NTRA : {}", messageXML);
			returnedMessage = sendMessage(username, password, messageXML, true);

		} catch (Exception e) {
			throw new NPCException(NPCException.GENERAL_ERROR_CODE, e.getMessage());
		}
		npcMessageModel.setReturnedMessage(returnedMessage);
		if (!validateReturnedMessage(returnedMessage)) {
			throw new NPCException(NPCException.GENERAL_ERROR_CODE, returnedMessage);
		} else {
			npcMessageModel.setSent(true);
			npcMessageModel.setMessageXML(messageXML);
			npcMessageModel.setMessageSuccess(
					npcProperties.getString("RETURNED_MESSAGE_SUCCESS").trim().equals(returnedMessage.trim()));

		}
	}

	/**
	 * method to Validate returned message from NTRA web service if success or
	 * another returned message
	 * 
	 * @param returnedMessage
	 * @return
	 */
	private boolean validateReturnedMessage(String returnedMessage) {
		if (npcProperties.getString("RETURNED_MESSAGE_SUCCESS").trim().equals(returnedMessage.trim()))
			return true;
		int returnedCodeIndex = returnedMessage.indexOf(npcProperties.getString("RETURNED_MESSAGE_DELIMITER").trim());
		if (returnedCodeIndex == -1)
			return true;
		String returnedCode = returnedMessage.substring(0, returnedCodeIndex).trim();
		String[] unexpectedCodes = npcProperties.getString("UNEXPECTED_RETURNED_MESSAGE_CODES").trim()
				.split("\\s*,\\s*");
		for (int i = 0; i < unexpectedCodes.length; i++)
			if (returnedCode.equals(unexpectedCodes[i]))
				return false;

		return true;
	}

	/**
	 * SendPendingMessages Method has many steps 1-getUnsent Messages 2-send
	 * messages to NTRA web service 3-update NPC_Message table after sending
	 * Messages 4-update NPCMessage Current and Max Date based on message code
	 * 5-update port_data table according to process code and action and SubAction
	 * 6-process Activation or deActivation 7-Save Message into Sync table if
	 * Message code in "NP RFS, NP RFS Broadcast" 8- if Message failed to be sent to
	 * NTRA runner job insert it into table'FAILED_MESSAGE_QUEUE'
	 */
	public void sendPendingMessages() {
		if (!initializeNPCService())
			return;

		NPCMessageModel npcMessageModel = null;

		List<NPCMessageModel> unsentMessages = new ArrayList<>();
		try {
			logger.debug("Start retrieving unsent NPC messages");
			unsentMessages = npcService.getUnsentMessages();
			logger.debug("Retrieving unsent NPC messges has been done Successfully...");
			logger.debug("Total number of unsent NPC messsages = {} ", unsentMessages.size());
		} catch (NPCException e1) {
			logger.error(e1.getErrorStackTrace(), (Throwable) e1);
		}

		for (int i = 0; i < unsentMessages.size(); i++) {
			try {
				npcMessageModel = unsentMessages.get(i);

				sendPendingMessage(npcMessageModel);

				logRequestData(npcMessageModel);

				npcService.updateFieldsAfterSending(npcMessageModel);

				npcService.updateNPCMessageCurrentAndNextDate(npcMessageModel);

				npcService.updatePortData(npcMessageModel, "SUB_ACTION_SENT");

				logResponseData(npcMessageModel);

				npcService.processActivationStatus(npcMessageModel);

				npcService.processDeactivationDone(npcMessageModel);

				npcService.saveSyncMessageByNPCMessage(npcMessageModel, npcService.getPortDataModel(npcMessageModel));

				npcService.saveSyncHistoryMessageByNPCMessage(npcMessageModel,
						npcService.getPortDataModel(npcMessageModel));

			} catch (NPCException ex) {
				try {
					logger.error(ex.getMessage(), (Throwable) ex);
					if (npcMessageModel != null) {
						NPCService.insertFailedMessage(ex, npcMessageModel.getNPCMessageID());
						this.npcService.resetPickedByField(npcMessageModel);
					}
				} catch (NPCException e2) {
					logger.error(ex.getMessage(), (Throwable) ex);
				}
			} catch (Exception ex2) {
				final NPCException npcException = new NPCException(ex2, NPCException.GENERAL_ERROR_CODE,
						"Unknown Error");
				logger.error(npcException.getErrorStackTrace(), (Throwable) ex2);
				try {
					NPCService.insertFailedMessage(npcException, npcMessageModel.getNPCMessageID());
					this.npcService.resetPickedByField(npcMessageModel);
				} catch (NPCException e) {
					logger.error(e.getMessage(), (Throwable) e);
				}
			}
		}
	}

	public void logRequestData(NPCMessageModel npcMessageModel) throws NPCException, JAXBException {
		if (npcMessageModel == null)
			return;

		if (npcMessageModel instanceof PortMessageModel) {

			portMessageModel = (PortMessageModel) npcMessageModel;

			String mSISDN = " \" - \" ";
			String companyFlag = " \" - \" ";
			String responseDueDate = "";
			String portID = "";
			String donorID = "";
			String recipientID = "";

			if (portMessageModel.getNumbersToPortList() != null) {
				ArrayList<NumbersToPortModel> numbersToPortList = new ArrayList<>();
				numbersToPortList = (ArrayList<NumbersToPortModel>) portMessageModel.getNumbersToPortList();

				for (int j = 0; j < numbersToPortList.size(); j++) {
					NumbersToPortModel numbersToPortModel = NumbersToPortModel.createNumbersToPort();
					numbersToPortModel = numbersToPortList.get(j);
					mSISDN = numbersToPortModel.getNumberDataType().getNumberFrom();
					if (mSISDN == null)
						mSISDN = " \" - \" ";
				}

			}

			responseDueDate = portMessageModel.getPortMessageType().getResponseDueDate();
			if (responseDueDate == null)
				responseDueDate = " \" - \" ";

			if (portMessageModel.getSubscriberDataModel() != null) {
				companyFlag = portMessageModel.getSubscriberDataModel().getSubscriberDataType().getCompanyFlag();
				if (companyFlag == null)
					companyFlag = " \" - \" ";
			}

			portID = portMessageModel.getPortMessageType().getPortID();
			if (portID == null)
				portID = " \" - \" ";

			donorID = portMessageModel.getPortMessageType().getDonorID();
			if (donorID == null)
				donorID = " \" - \" ";

			recipientID = portMessageModel.getPortMessageType().getRecipientID();
			if (recipientID == null)
				recipientID = " \" - \" ";

			logger.info(
					" NPC Message ID: {} | MessageID: {} | MessageCode: {} | PortID: {} | MSISDN: {} | Message TimeStamp: {} | Message Type: Sent | DonerID: {} | RecipientID: {} | Due Date: {} | Company Flag: {} ",
					npcMessageModel.getNPCMessageID(), portMessageModel.getPortMessageType().getMessageID(),
					portMessageModel.getPortMessageType().getMessageCode(), portID, mSISDN, new Date(), donorID,
					recipientID, responseDueDate, companyFlag);

		} else {

			BulkSyncMessageModel bulkSyncMessageModel = (BulkSyncMessageModel) npcMessageModel;

			String syncID = bulkSyncMessageModel.getBulkSyncMessageType().getSyncID();
			if (syncID == null)
				syncID = " \" - \" ";

			logger.info(
					" NPC Message ID: {} | MessageID: {} | MessageCode: {} | SyncID: {} | StartDate: {} | EndDate: {} | Message TimeStamp: {} | Message Type: Sent | Comments1: {} | Comments2: {} ",
					npcMessageModel.getNPCMessageID(), bulkSyncMessageModel.getBulkSyncMessageType().getMessageID(),
					bulkSyncMessageModel.getBulkSyncMessageType().getMessageCode(), syncID,
					bulkSyncMessageModel.getBulkSyncMessageType().getStartDate(),
					bulkSyncMessageModel.getBulkSyncMessageType().getEndDate(), new Date(),
					bulkSyncMessageModel.getBulkSyncMessageType().getComments1(),
					bulkSyncMessageModel.getBulkSyncMessageType().getComments2());

		}

	}

	public void logResponseData(NPCMessageModel npcMessageModel) throws NPCException, JAXBException {
		if (npcMessageModel == null)
			return;

		if (npcMessageModel instanceof PortMessageModel) {

			portMessageModel = (PortMessageModel) npcMessageModel;
			PortDataModel portDataModel = npcService.getPortDataModel(portMessageModel);

			String response = npcMessageModel.getReturnedMessage();
			if (response == null)
				response = " \" - \" ";

			String internalPortID = portMessageModel.getInternalPortID();
			if (internalPortID == null)
				internalPortID = " \" - \" ";

			String portStatus = portDataModel.getPortStatus();
			if (portStatus == null)
				portStatus = " \" - \" ";

			logger.info(
					"Response: \" {} \" | NPC Message ID: \" {} \" | Internal Port ID: \" {} \" | Port Status: \" {} \" ",
					response, npcMessageModel.getNPCMessageID(), internalPortID, portStatus);

		} else {

			String responseBulkSyncMessage = npcMessageModel.getReturnedMessage();
			if (responseBulkSyncMessage == null)
				responseBulkSyncMessage = " \" - \" ";

			logger.info("Response: \" {} \" | NPC Message ID: \" {} \" ", responseBulkSyncMessage,
					npcMessageModel.getNPCMessageID());
		}
	}

	/**
	 * initialize database connection Retries
	 * 
	 * @return boolean
	 */
	public boolean initializeNPCService() {
		int connectionRetries = -1;
		int numberOfRetries = 0;
		boolean connectionSucceeded = false;
		try {
			connectionRetries = (new Integer(npcProperties.getString("PROCESS_NUMBER_OF_DATABASE_CONNECTION_RETRIES")))
					.intValue();
		} catch (NumberFormatException ex) {
			connectionRetries = 3;
		}
		do
			try {
				npcService = NPCService.initiateDBConnection(npcProperties);
				connectionSucceeded = true;
			} catch (NPCException ex) {
				logger.error(ex.getMessage(), ex);
				if (ex.getCode().equals(NPCException.DATABASE_CONNECTION_ESTABLISHING_ERROR_CODE))
					numberOfRetries++;
				else
					return false;
			}
		while (numberOfRetries < connectionRetries && !connectionSucceeded);

		return connectionSucceeded;
	}

	/**
	 * encrypt Method used to encrypt user name and password using Blowfish jar with
	 * Key 'VodafoneNPCApplication'
	 * 
	 * @param cardNumber
	 * @return String
	 */
	public String encrypt(String cardNumber) {
		BlowfishHelper lBlowfishHelper = new BlowfishHelper("VodafoneNPCApplication");
		return BinConverter.bytesToBinHex(lBlowfishHelper.encrypt(cardNumber));
	}

	/**
	 * decrypt Method used to decrypt user name and password using Blowfish jar with
	 * Key 'VodafoneNPCApplication'
	 * 
	 * @param cardNumber
	 * @return
	 */
	public String decrypt(String cardNumber) {
		BlowfishHelper blowfishHelper = new BlowfishHelper("VodafoneNPCApplication");
		byte[] bcc = new byte[cardNumber.length() / 2];
		BinConverter.binHexToBytes(cardNumber, bcc, 0, 0, cardNumber.length() / 2);
		return new String(blowfishHelper.decrypt(bcc));
	}

	/**
	 * SaveActivatedNumber Method to save the received activated number from NTRA to
	 * insert these numbers into Sync table if not exist and update it if already
	 * exist and also save them into Sync_History table for historical purpose the
	 * file that contained Activated Numbers obtained from NTRA
	 * 
	 * @param syncMessageXML
	 * @throws NPCException
	 */
	public void saveActivatedNumbers(String syncMessageXML) throws NPCException {
		FileInputStream inputStream = null;
		npcService = null;
		try {
			npcService = NPCService.initiateDBConnection(npcProperties);
			try {
				JAXBContext jaxbContext = JAXBContext
						.newInstance("com.asset.vodafone.npc.webservice.xsd.bulksyncmessage");
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				logger.debug("Loading XSD schema file to validate bulk sync message");
				InputStream bulkSyncMessage = this.getClass().getClassLoader()
						.getResourceAsStream("xsd/bulksyncmessage.xsd");
				if (bulkSyncMessage == null) {
					throw new FileNotFoundException("XSD Schema file not found!");
				}
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
				schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

				Schema xsdSchema = schemaFactory.newSchema(new StreamSource(bulkSyncMessage));
				logger.debug("Start validating bulk sync message against xsd schema");
				unmarshaller.setSchema(xsdSchema);

				inputStream = new FileInputStream(syncMessageXML);
				NPCBulkSyncData npcBulkSyncData = (NPCBulkSyncData) unmarshaller.unmarshal(inputStream);
				logger.debug("validating bulk sync message against XSD schema has been done successfully");
				ArrayList<SyncModel> syncMessages = getSyncMessages(npcBulkSyncData);
				npcService.saveSyncMessages(syncMessages);
				npcService.saveSyncHistoryMessages(syncMessages);

				inputStream.close();

			} catch (JAXBException ex) {
				throw new NPCException((Throwable) ex, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE,
						"Cannot Create JAXB Object");
			} catch (FileNotFoundException ex2) {
				throw new NPCException((Throwable) ex2, NPCException.PROCESSING_SYNC_FILE_NOT_FOUND_ERROR_MESSAGE,
						"Participant \"{0}\" not found  please contact your Database administrator or check the resource file");
			} catch (Exception e) {
				logger.error(e.getMessage(), NPCException.GENERAL_ERROR_CODE);
				throw new NPCException((Throwable) e, NPCException.GENERAL_ERROR_CODE, "Unknown Error");
			}
		}

		catch (NPCException ex3) {
			logger.error(ex3.getMessage(), (Throwable) ex3);
			throw new NPCException(ex3.getMessage());
		} finally {
			try {
				if (npcService != null) {
					npcService.releaseConnection();
				}

			} catch (NPCException ex4) {
				logger.error(ex4.getMessage(), (Throwable) ex4);
			}
		}
	}

	/**
	 * Method to fill the syncModel with object ofnpcBulkSyncData and add this model
	 * into syncMessages Arraylist to insert it into Sync and SyncHistory tables
	 * 
	 * @return ArrayList of syncMessages
	 */
	private ArrayList<SyncModel> getSyncMessages(NPCBulkSyncData npcBulkSyncData) {
		List<ActivatedNumberType> activatedNumbers = npcBulkSyncData.getActivatedNumbers().getActivatedNumber();
		ActivatedNumberType activatedNumberType = null;
		ArrayList<SyncModel> syncMessages = new ArrayList<>();
		SyncModel syncModel = null;
		for (int i = 0; i < activatedNumbers.size(); i++) {
			activatedNumberType = activatedNumbers.get(i);
			syncModel = new SyncHistoryModel();
			syncModel.setMsisdn(activatedNumberType.getNumberFrom());
			syncModel.setBulkSyncIDNumber(npcBulkSyncData.getIDNumber());
			syncModel.setTimeStamp(new BigDecimal(npcBulkSyncData.getTimeStampOfLastBroadCast()));
			syncModel.setDonorID(activatedNumberType.getDonorId());
			syncModel.setRecipientID(activatedNumberType.getRecipientId());
			syncModel.setNewRoute(activatedNumberType.getNewRoute());
			syncModel.setIdNumber(activatedNumberType.getIDNumber());
			syncMessages.add(syncModel);
			if (activatedNumberType.getNumberTo() != null && !"".equals(activatedNumberType.getNumberTo())) {
				BigDecimal numberFrom = new BigDecimal(activatedNumberType.getNumberFrom());
				BigDecimal numberTo = new BigDecimal(activatedNumberType.getNumberTo());
				int diff = (int) (numberTo.longValue() - numberFrom.longValue());
				for (int j = 1; j <= diff; j++) {
					syncModel = new SyncHistoryModel();
					syncModel.setMsisdn(numberFrom.add(new BigDecimal(j)).toString());
					syncModel.setBulkSyncIDNumber(npcBulkSyncData.getIDNumber());
					syncModel.setTimeStamp(new BigDecimal(npcBulkSyncData.getTimeStampOfLastBroadCast()));
					syncModel.setDonorID(activatedNumberType.getDonorId());
					syncModel.setRecipientID(activatedNumberType.getRecipientId());
					syncModel.setNewRoute(activatedNumberType.getNewRoute());
					syncModel.setIdNumber(activatedNumberType.getIDNumber());
					syncMessages.add(syncModel);
				}

			}
		}

		return syncMessages;
	}

	/**
	 * encryptMode Method used to encrypt username or password
	 * 
	 * @param plainText
	 * @throws IOException
	 * @throws Exception
	 */
	private static void encryptMode(String[] plainText) throws Exception {
		NPCProcessHandler handler = getInstance();
		String encypted = "";
		for (int i = 1; i >= 1 && i < plainText.length; i++) {
			encypted = handler.encrypt(plainText[i]);

			logger.debug("Encrypted Value of {} = \" {} \" ", plainText[i], encypted);

		}
	}

	/**
	 * SyncMode Method used to Insert Activated Numbers that received from NTRA into
	 * Sync and SyncHistory Tables
	 * 
	 * @param syncMessageXML
	 * @throws IOException
	 * @throws Exception
	 */
	private static void syncMode(String syncMessageXML) throws Exception {
		NPCProcessHandler handler = getInstance();
		handler.saveActivatedNumbers(syncMessageXML);
	}

	/**
	 * mainMode Method is the main process of runner job that send Messages to NTRA
	 * web service through thread sleep every specific time of seconds configured in
	 * Environment Variables or properties file
	 * 
	 * @param handler
	 * @throws IOException
	 */
	private static void mainMode(NPCProcessHandler handler) throws IOException, NPCException {

		String processSleepDurationSeconds = System.getenv("PROCESS_SLEEP_DURATION_SECONDS");
		if (processSleepDurationSeconds == null)
			processSleepDurationSeconds = npcProperties.getString("PROCESS_SLEEP_DURATION_SECONDS");
		try {

			while (true) {
				handler.sendPendingMessages();
				Thread.sleep(Integer.parseInt(processSleepDurationSeconds) * 1000L);
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);

		} catch (Exception ex) {
			logger.error(ex.getMessage(), NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE, ex);
			throw new NPCException(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE);

		} finally {
			handler.releaseConnection();
		}
	}

	/**
	 * main Method of Runner job it has four modes:- encryptMode to Encrypt username
	 * and password of MNP Web Service. SyncMode to Save Activated Numbers that sent
	 * from NTRA in XML file. testNTRAMode to simulate sending Message from MNP to
	 * NTRA. mainMode to send Messages from MNP to NTRA. ServerSocket object used to
	 * Lock specific port on the network to enable only one instance of runner job
	 * running on machine
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		String singleApplicationInstanceLockPort = System.getenv("SINGLE_APPLICATION_INSTANCE_LOCK_PORT");
		try {

			if (args.length != 0) {
				if (args[0].equals("enc")) {
					encryptMode(args);

				} else if (args[0].equals("sync")) {
					syncMode(args[1]);

				} else if (args[0].equals("test")) {
					testMode(args[1], args[2]);

				}
			} else {
				ServerSocket serverSocket = null;
				try {
					NPCProcessHandler handler = getInstance();
					if (singleApplicationInstanceLockPort == null)
						singleApplicationInstanceLockPort = npcProperties
								.getString("SINGLE_APPLICATION_INSTANCE_LOCK_PORT");
					serverSocket = new ServerSocket(Integer.parseInt(singleApplicationInstanceLockPort));

					mainMode(handler);
				} catch (Exception e) {

					NPCException exception = new NPCException(NPCException.FATAL_ERROR_CODE, e.getMessage());

					logger.error(exception.getMessage(), exception);
				} finally {
					if (!(serverSocket == null || serverSocket.isClosed())) {
						serverSocket.close();
					}
				}
			}
		} catch (IOException ex) {

			NPCException exception = new NPCException(NPCException.FATAL_ERROR_CODE, ex.getMessage());
			logger.error(exception.getMessage(), exception);
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);

		}
	}

	/**
	 * testNTRAMode Method used to act as simulator sends xml Message saved in file
	 * to NTRA
	 * 
	 * @param xmlFileName
	 * @throws Exception
	 */
	private static void testMode(String testParty, String xmlFileName) throws Exception {
		try {
			NPCProcessHandler handler = getInstance();
			String messageDebug = "";
			if (testParty != null && testParty.equalsIgnoreCase("ntra")) {
				messageDebug = handler.sendMessage(handler.decrypt(npcProperties.getString("INTERNAL_USER_NAME")),
						handler.decrypt(npcProperties.getString("INTERNAL_PASSWORD")).getBytes(),
						new String(Files.readAllBytes(Paths.get(xmlFileName))), true);
				logger.debug(messageDebug);
			} else if (testParty != null && testParty.equalsIgnoreCase("voda")) {
				messageDebug = handler.sendMessage(handler.decrypt(npcProperties.getString("NPC_USER_NAME")),
						handler.decrypt(npcProperties.getString("NPC_USER_NAME")).getBytes(),
						new String(Files.readAllBytes(Paths.get(xmlFileName))), false);
				logger.debug(messageDebug);
			}
		} catch (IOException | NPCException ex) {
			logger.error(ex.getMessage());

		}
	}

}