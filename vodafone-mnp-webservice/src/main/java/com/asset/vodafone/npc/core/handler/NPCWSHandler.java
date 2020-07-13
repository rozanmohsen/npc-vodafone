package com.asset.vodafone.npc.core.handler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.models.BulkSyncMessageModel;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.models.NumbersToPortModel;
import com.asset.vodafone.npc.core.models.PortDataModel;
import com.asset.vodafone.npc.core.models.PortMessageModel;
import com.asset.vodafone.npc.core.models.SubscriberDataModel;
import com.asset.vodafone.npc.core.service.NPCService;
import com.asset.vodafone.npc.webservice.xsd.portmessage.BulkSyncMessageType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NPCData;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NumberDataType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.PortMessageType;
import com.asset.vodafone.npc.webservice.xsd.portmessage.SubscriberDataType;
import com.mega.common.encryption.blowfish.BinConverter;
import com.mega.common.encryption.blowfish.BlowfishHelper;

public class NPCWSHandler {
	ResourceBundle npcProperties = null;

	private static final Logger logger = LoggerFactory.getLogger(NPCWSHandler.class.getName());
	private static NPCWSHandler npcwshandler = null;
	NPCService npcService;
	private String unexpectedFailureMessage;
	private String connectionFailureMessage;
	private String databaseFailureMessage;
	private String validCredentials = "Valid credentials";
	private String returnedMessage = "";
	private NPCMessageModel npcMessageModel = null;
	private PortMessageModel portMessageModel = null;
	private static final String NPCJAXB = "NPC_JAXB";
	private static final String NPCDATABASE = "NPC_DATABASE";

	private NPCWSHandler() throws NPCException {
		try {
			npcProperties = loadNPCPropertiesFile("npc_web_service.properties");

			if (npcProperties != null) {

				unexpectedFailureMessage = npcProperties.getString("RETURNED_MESSAGE_UNEXPECTED_FAILURE").trim();
				connectionFailureMessage = npcProperties.getString("RETURNED_MESSAGE_DATABASE_CONNECTION_FAILURE")
						.trim();
				databaseFailureMessage = npcProperties.getString("RETURNED_MESSAGE_DATABASE_FAILURE").trim();
			} else {
				throw new NPCException(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE);
			}

		} catch (NPCException e) {

			logger.error(e.getMessage(), NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE, e);
			throw new NPCException(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE);
		}

	}

	/**
	 * get Instance of NPCWSHandler
	 * 
	 * @return NPCWSHandler
	 * 
	 * @throws Exception
	 */
	public static synchronized NPCWSHandler getInstance() throws NPCException {

		if (npcwshandler == null) {
			return new NPCWSHandler();
		} else {
			return npcwshandler;
		}
	}

	/**
	 * Definition of properties file
	 * 
	 * @param fileName
	 * @return
	 * 
	 * @throws Exception
	 */
	public static ResourceBundle loadNPCPropertiesFile(String fileName) throws NPCException {
		FileInputStream inputStream = null;
		PropertyResourceBundle resourceBundle = null;
		try {

			inputStream = new FileInputStream(fileName);
			resourceBundle = new PropertyResourceBundle(inputStream);
		} catch (IOException ex) {
			logger.error(ex.getMessage(), NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE, ex);
			throw new NPCException(NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE);

		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), NPCException.FATAL_ERROR_LOADING_PORPERTIES_FILE_ERROR_MESSAGE, e);

			}
		}
		return resourceBundle;
	}

	/**
	 * processNPCMessage method communicate with NTRA Web service with user name and
	 * password and message and receive web service response processNPCMessage
	 * Steps:- 1- validating connection with web service through user name and
	 * password 2-parsing Xml Message 3- insert Message into Database Tables 4-
	 * Updating time frames for NPC message 5-Updating port information for NPC
	 * message in Port_Data table in database 6- Activation and Deactivation Process
	 * 7-inserting or removing from SYNC and sync History table for NPC message
	 * 
	 * @param username
	 * @param password
	 * @param message
	 * @return
	 */
	public String processNPCMessage(String username, byte[] password, String message) {
		try {
			returnedMessage = "";
			logger.debug("Start validating the web service credentail for user: {} ", username);
			returnedMessage = checkWSCredentials(username, password);

			if (!returnedMessage.equals(validCredentials)) {
				logRequestData(npcMessageModel, returnedMessage);
				logger.error(returnedMessage);
				
				return returnedMessage;

			}
			logger.debug("Validating service credential has been done successully for user : {}", username);
			logger.info("Received NPC Message Request for user : {}  and the Message is : {}", username, message);
			if (intializeDataBaseConnection().equals(connectionFailureMessage)) {
				returnedMessage = connectionFailureMessage;
				logRequestData(npcMessageModel, returnedMessage);
				logger.error(returnedMessage);
				
				return returnedMessage;

			}
			logger.debug("Start Parsing Received Message from user : {}", username);
			returnedMessage = parsingAndUpdatingMessageTimeFrame(message);
			if (returnedMessage.equals(databaseFailureMessage)

					|| returnedMessage.equals(unexpectedFailureMessage)) {
				returnedMessage = (databaseFailureMessage == null) ? unexpectedFailureMessage : databaseFailureMessage;
				logRequestData(npcMessageModel, returnedMessage);
				logger.error(returnedMessage);
				
				return returnedMessage;
			}

			returnedMessage = callUpdatePortDataAndprocessActivationAndDeactivation();

			if (!returnedMessage.equals("")) {
				logRequestData(npcMessageModel, returnedMessage);
				logger.error(returnedMessage);
				return returnedMessage;
			}
			returnedMessage = saveSyncMessage();
			if (!returnedMessage.equals("")) {
				logRequestData(npcMessageModel, returnedMessage);
				logger.error(returnedMessage);
				return returnedMessage;
			}
			String successMessage = npcProperties.getString("RETURNED_MESSAGE_SUCCESS").trim();

			logRequestData(npcMessageModel, successMessage);

			return successMessage;
		} catch (Exception ex) {
			NPCException npcException = new NPCException(ex, NPCException.GENERAL_ERROR_CODE, "Unknown Error");
			logger.error(npcException.getErrorStackTrace(), ex);
			try {
				if (npcService != null)
					npcService.releaseConnection();
				logger.debug("Releasing the Database Connection..");
			} catch (NPCException ex4) {
				logger.error(ex4.getMessage());
			}
			return unexpectedFailureMessage;
		}

	}

	public void logRequestData(NPCMessageModel npcMessageModel, String response) throws NPCException, JAXBException {

		if(npcMessageModel ==null) {
			
			logger.info("Response: \" {} \" ",response);
			
		}
		
		else {
		if (npcMessageModel instanceof PortMessageModel) {

			String mSISDN = " \" - \" ";
			String companyFlag = " \" - \" ";
			String responseDueDate = "";
			String portID = "";
			String donorID = "";
			String recipientID = "";

			PortDataModel portDataModel = npcService.getPortDataModel(portMessageModel);

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
					" NPC Message ID: {} | MessageID: {} | MessageCode: {} | PortID: {} | MSISDN: {} | Message TimeStamp: {} | Message Type: Received | DonerID: {} | RecipientID: {} | Due Date: {} | Company Flag: {} ",
					npcMessageModel.getNPCMessageID(), portMessageModel.getPortMessageType().getMessageID(),
					portMessageModel.getPortMessageType().getMessageCode(), portID, mSISDN, new Date(), donorID,
					recipientID, responseDueDate, companyFlag);

			logger.info("Response: \" {} \" NPC Message ID: \" {} \" Internal Port ID: \" {} \" Port Status: \" {} \" ",
					response, npcMessageModel.getNPCMessageID(), portMessageModel.getInternalPortID(),
					portDataModel.getPortStatus());
		} else {

			BulkSyncMessageModel bulkSyncMessageModel = (BulkSyncMessageModel) npcMessageModel;

			String syncID = bulkSyncMessageModel.getBulkSyncMessageType().getSyncID();
			if (syncID == null)
				syncID = " \" - \" ";

			logger.info(
					"NPC Message ID: {} | MessageID: {} | MessageCode: {} | SyncID: {} | StartDate: {} | EndDate: {} | Message TimeStamp: {} | Message Type: Received | Comments1: {} | Comments2: {} ",
					npcMessageModel.getNPCMessageID(), bulkSyncMessageModel.getBulkSyncMessageType().getMessageID(),
					bulkSyncMessageModel.getBulkSyncMessageType().getMessageCode(), syncID,
					bulkSyncMessageModel.getBulkSyncMessageType().getStartDate(),
					bulkSyncMessageModel.getBulkSyncMessageType().getEndDate(), new Date(),
					bulkSyncMessageModel.getBulkSyncMessageType().getComments1(),
					bulkSyncMessageModel.getBulkSyncMessageType().getComments2());

			logger.info("Returned Message for the Bulk Sync Message  with NPC Message ID {}  = \" {} \" ",
					npcMessageModel.getNPCMessageID(), response);
		}
		}
	}

	public String checkWSCredentials(String username, byte[] password) {

		String npcUserName = System.getenv("NPC_USER_NAME");
		String npcPassword = System.getenv("NPC_PASSWORD");

		if (npcUserName == null)
			npcUserName = npcProperties.getString("NPC_USER_NAME");
		if (npcPassword == null)
			npcPassword = npcProperties.getString("NPC_PASSWORD");
		if ("".equals(username) || username == null)
			return MessageFormat.format(npcProperties.getString("RETURNED_MESSAGE_INVALID_USER_NAME"), username);
		if (password == null) {
			return npcProperties.getString("RETURNED_MESSAGE_NULL_PASSWORD");
		}

		if (!decrypt(npcUserName.trim()).equals(username))

			return MessageFormat.format(npcProperties.getString("RETURNED_MESSAGE_INVALID_USER_NAME").trim(), username);

		if (!decrypt(npcPassword.trim()).equals(Base64.getEncoder().encodeToString((password)))) {

			return npcProperties.getString("RETURNED_MESSAGE_INVALID_PASSWORD").trim();
		}
		return validCredentials;
	}

	public String intializeDataBaseConnection() {
		npcService = null;
		try {
			npcService = NPCService.initiateDBConnection(npcProperties);

		}

		catch (NPCException e) {
			logger.error(e.getMessage(), e);
			if (e.getCode().startsWith(NPCDATABASE)) {

				return connectionFailureMessage;
			}

		}
		returnedMessage = "";
		return returnedMessage;

	}

	public String parsingAndUpdatingMessageTimeFrame(String message) throws NPCException, SAXException, Exception {

		try {
			
			npcMessageModel = parseMessage(npcService, message);

			if (npcMessageModel != null) {

				logger.debug("Parsing message XML  has done successfully with NPC Message ID: {} ",
						npcMessageModel.getNPCMessageID());

				npcMessageModel.setSent(true);
				npcMessageModel.setMessageXML(message);
				npcService.insertNPCMessage(npcMessageModel);

				if (npcMessageModel instanceof PortMessageModel) {
					portMessageModel = (PortMessageModel) npcMessageModel;
					logger.debug(
							"Inserting message into Database has done successfully with NPC Message ID: {}  and Internal Port ID: {}",
							npcMessageModel.getNPCMessageID(), portMessageModel.getInternalPortID());
				}

				npcService.updateNPCMessageCurrentAndNextDate(npcMessageModel);

			} else {
				throw new NPCException(NPCException.JAXB_CREATE_OBJECT_ERROR_CODE);
			}
		} catch (NPCException ex1) {
			logger.error(ex1.getMessage(), ex1);
			if (ex1.getCode().startsWith(NPCJAXB)) {

				return npcProperties.getString("RETURNED_MESSAGE_MARSHALLING_FAILURE").trim();

			}
			if (ex1.getCode().startsWith(NPCDATABASE)) {

				return databaseFailureMessage;
			}

			return unexpectedFailureMessage;
		}
		return returnedMessage;
	}

	public String callUpdatePortDataAndprocessActivationAndDeactivation() {
		try {

			npcService.updatePortData(npcMessageModel, "SUB_ACTION_RECEIVED");
			npcService.processActivationStatus(npcMessageModel);
			npcService.processDeactivationDone(npcMessageModel);

		} catch (NPCException ex2) {
			logger.error(ex2.getMessage(), ex2);
			if (ex2.getCode().startsWith(NPCJAXB))
				returnedMessage = npcProperties.getString("RETURNED_MESSAGE_MARSHALLING_FAILURE").trim();
			if (ex2.getCode().startsWith(NPCDATABASE))
				returnedMessage = databaseFailureMessage;
			else
				returnedMessage = unexpectedFailureMessage;
		}
		return returnedMessage;
	}

	public String saveSyncMessage() {
		try {
			PortDataModel portDataModel = null;
			portDataModel = npcService.getPortDataModel(npcMessageModel);

			npcService.saveSyncMessageByNPCMessage(npcMessageModel, portDataModel);

			npcService.saveSyncHistoryMessageByNPCMessage(npcMessageModel, portDataModel);

		} catch (NPCException ex3) {
			logger.error(ex3.getMessage(), ex3);
			if (ex3.getCode().startsWith(NPCJAXB))
				returnedMessage = npcProperties.getString("RETURNED_MESSAGE_MARSHALLING_FAILURE").trim();
			if (ex3.getCode().startsWith(NPCDATABASE))
				returnedMessage = databaseFailureMessage;
			else
				returnedMessage = unexpectedFailureMessage;
		}
		return returnedMessage;
	}

	/**
	 * Parsing Xml Message to convert it to an object
	 * 
	 * @param npcService
	 * @param message
	 * @return
	 * @throws NPCException
	 * @throws SAXException
	 * 
	 */
	private NPCMessageModel parseMessage(NPCService npcService, String message) throws Exception {
		ByteArrayInputStream inputStream = null;
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(
					"com.asset.vodafone.npc.webservice.xsd.portmessage:com.asset.vodafone.npc.webservice.xsd.bulksyncmessage");

			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
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
			unmarshaller.setSchema(xsdSchema);

			inputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));

			NPCData npcData = (NPCData) unmarshaller.unmarshal(inputStream);
			logger.debug("validating NPC message against XSD schema has been done successfully");
			return createNPCMessage(npcService, npcData);

		} catch (JAXBException ex) {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
				throw new NPCException(e, NPCException.GENERAL_ERROR_CODE, "Unknown Error");
			}
			throw new NPCException(ex, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE, "Cannot Create JAXB Object");

		}

	}

	/**
	 * Method createNPCMEssage user to return NPCMessageModel for inserting that
	 * Model in database NPCWSHandler calling this method when parsing xml message
	 * received from NTRA web service
	 * 
	 * @param npcService
	 * @param npcData
	 * @return
	 * @throws NPCException
	 */
	private NPCMessageModel createNPCMessage(NPCService npcService, NPCData npcData) throws NPCException {

		List<Object> portOrBulkSuncMessageList = npcData.getNPCMessages().getPortMessageOrBulkSyncMessage();
		try {
			for (int i = 0; i < portOrBulkSuncMessageList.size(); i++) {
				if (portOrBulkSuncMessageList.get(i) instanceof PortMessageType) {

					PortMessageType portMessageType = (PortMessageType) portOrBulkSuncMessageList.get(i);
					PortMessageModel portMessageModelInstance = PortMessageModel.createPortMessage();
					portMessageModelInstance.setPortMessageType(portMessageType);
					portMessageModelInstance.setNPCMessageID(npcService.getNextNPCMessageID());
					portMessageModelInstance.setPort(true);
					SubscriberDataType subscriberDataType = portMessageType.getSubscriberData();
					SubscriberDataModel subscriberDataModel = null;

					if (subscriberDataType != null) {

						subscriberDataModel = SubscriberDataModel.createSubscriberData();
						subscriberDataModel.setNPCMessageID(portMessageModelInstance.getNPCMessageID());

						subscriberDataModel.setSubscriberDataType(subscriberDataType);
					}
					ArrayList<NumbersToPortModel> numbersToPortList = new ArrayList<>();
					List<NumberDataType> numberDataList = portMessageType.getNumbersToPort();
					for (int j = 0; j < numberDataList.size(); j++) {
						NumbersToPortModel numbersToPortModel = NumbersToPortModel.createNumbersToPort();
						numbersToPortModel.setNPCMessageID(portMessageModelInstance.getNPCMessageID());
						numbersToPortModel.setNumberDataType(numberDataList.get(j));
						numbersToPortList.add(numbersToPortModel);
					}

					portMessageModelInstance.setSubscriberDataModel(subscriberDataModel);
					portMessageModelInstance.setNumbersToPortList(numbersToPortList);
					return portMessageModelInstance;
				}
				if (portOrBulkSuncMessageList.get(i) instanceof BulkSyncMessageType) {
					BulkSyncMessageType bulkSyncMessageType = (BulkSyncMessageType) portOrBulkSuncMessageList.get(i);
					BulkSyncMessageModel bulkSyncMessageModel = BulkSyncMessageModel.createBulkSyncMessage();
					bulkSyncMessageModel.setBulkSyncMessageType(bulkSyncMessageType);
					bulkSyncMessageModel.setNPCMessageID(npcService.getNextNPCMessageID());
					bulkSyncMessageModel.setPort(false);
					return bulkSyncMessageModel;
				}
			}

			return null;
		} catch (JAXBException ex) {
			throw new NPCException(ex, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE, "Cannot Create JAXB Object");
		}
	}

	/**
	 * encrypt method used to encrypt user name and password using key
	 * 'VodafoneNPCApplication'
	 * 
	 * @param cardNumber
	 * @return
	 */
	public String encrypt(String cardNumber) {
		BlowfishHelper lBlowfishHelper = new BlowfishHelper("VodafoneNPCApplication");
		return BinConverter.bytesToBinHex(lBlowfishHelper.encrypt(cardNumber));
	}

	/**
	 * decrypt method to decrypt user name and password using key
	 * 'VodafoneNPCApplication'
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

}
