package com.asset.vodafone.npc.core.handler;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
	private static final String NPCJAXB= "NPC_JAXB";
	private static final String NPCDATABASE ="NPC_DATABASE";

	private NPCWSHandler() throws  NPCException {
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
			logger.info("Start validating the web service credentail for user: {} " , username);
			returnedMessage = checkWSCredentials(username, password);

			if (!returnedMessage.equals(validCredentials)) {

				logger.error(returnedMessage);
				return returnedMessage;

			}
			logger.info("Validating service credential has been done successully for user : {}" , username);
			logger.debug("Received NPC Message Request for user : {}  and the Message is : {}" ,username, message);
			if (intializeDataBaseConnection().equals(connectionFailureMessage)) {
				returnedMessage = connectionFailureMessage;
				logger.error(returnedMessage);
				return returnedMessage;

			}
			logger.info("Start Parsing Received Message from user : {}" , username);
			returnedMessage = parsingAndUpdatingMessageTimeFrame(message);
			if (returnedMessage.equals(databaseFailureMessage)

					|| returnedMessage.equals(unexpectedFailureMessage)) {
				returnedMessage = (databaseFailureMessage == null) ? unexpectedFailureMessage : databaseFailureMessage;
				logger.error(returnedMessage);
				return returnedMessage;
			}
			logger.info("Start Updating Port Data and Process Activation and Deactivation ");
			returnedMessage = callUpdatePortDataAndprocessActivationAndDeactivation();
			logger.info(
					"The process of inserting into PORT_ACTIVATION_STATUS and PORT_DISCONNECT_STATUS in case of message code is NP_ACTIVATED_STATUS_MESSAGE or NP_DEACT_STATUS  has completed Successfully");
			if (!returnedMessage.equals(""))
				return returnedMessage;
			logger.info("Starting Save Sync Messages Process");
			returnedMessage = saveSyncMessage();
			if (!returnedMessage.equals(""))
				return returnedMessage;
			logger.info("Process of Saving Sync Messages has done Successfully");
			String successMessage = npcProperties.getString("RETURNED_MESSAGE_SUCCESS").trim();
			logger.debug("Returned Message for the request with NPC Message ID {} and Internal Port ID {} = \" {} \" "
					,npcMessageModel.getNPCMessageID(), portMessageModel.getInternalPortID(),successMessage );
					
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

	public void logRequest(String message)  {
		final String xmlStr = message;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

		DocumentBuilder builder = null;
		try {

			builder = factory.newDocumentBuilder();

			Document doc = builder.parse(new InputSource(new StringReader(xmlStr)));

			String messageID = "";
			String messageCode = "";
			String portID = "";
			String mSISDN = "";
			String donorID = "";
			String recipientID = "";

			NodeList messageNodes = doc.getElementsByTagName("PortMessage");
			NodeList numbersToPort = doc.getElementsByTagName("NumbersToPort");

			Element elementNumbersToport = (Element) numbersToPort.item(0);
			Element element = (Element) messageNodes.item(0);

			if (messageNodes.getLength() > 0) {
				if (element.getElementsByTagName("MessageID").getLength() > 0)
					messageID = element.getElementsByTagName("MessageID").item(0).getTextContent();

				if (element.getElementsByTagName("MessageCode").getLength() > 0)
					messageCode = element.getElementsByTagName("MessageCode").item(0).getTextContent();

				if (element.getElementsByTagName("PortID").getLength() > 0)
					portID = element.getElementsByTagName("PortID").item(0).getTextContent();
				if (element.getElementsByTagName("DonorID").getLength() > 0)
					donorID = element.getElementsByTagName("DonorID").item(0).getTextContent();
				if (element.getElementsByTagName("RecipientID").getLength() > 0)
					recipientID = element.getElementsByTagName("RecipientID").item(0).getTextContent();

			}
			if (numbersToPort.getLength() > 0
					&& elementNumbersToport.getElementsByTagName("NumberFrom").getLength() > 0) {

				mSISDN = elementNumbersToport.getElementsByTagName("NumberFrom").item(0).getTextContent();
			}

			logger.debug("MessageID: {} | MessageCode: {} | PortID: {} | MSISDN: {} | Message TimeStamp: {} | Message Type: Received | DonerID: {} | RecipientID: {} |",
					messageID,messageCode,portID,mSISDN,new Date(),donorID,recipientID);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public String parsingAndUpdatingMessageTimeFrame(String message) throws NPCException {

		try {
			logRequest(message);
			npcMessageModel = parseMessage(npcService, message);
			portMessageModel = (PortMessageModel) npcMessageModel;
		
			if (npcMessageModel != null) {
				logger.debug("Parsing message XML  has done successfully with NPC Message ID: {} ", npcMessageModel.getNPCMessageID());
				npcMessageModel.setSent(true);
				npcMessageModel.setMessageXML(message);
				npcService.insertNPCMessage(npcMessageModel);
				
				logger.debug("Inserting message into Database has done successfully with NPC Message ID: {}  and Internal Port ID: {}", npcMessageModel.getNPCMessageID(),portMessageModel.getInternalPortID());

				npcService.updateNPCMessageCurrentAndNextDate(npcMessageModel);
				logger.debug("Updating time frames for NPC Message ID: {}  and Internal Port ID: {} has done successfully" , npcMessageModel.getNPCMessageID(),portMessageModel.getInternalPortID());

			} else {
				throw new NPCException(NPCException.JAXB_CREATE_OBJECT_ERROR_CODE);
			}
		} catch (NPCException ex1) {
			logger.error(ex1.getMessage(), ex1);
			if (ex1.getCode().startsWith(NPCJAXB)) {

				return databaseFailureMessage;

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
			logger.debug("Start Update port data table ");
			npcService.updatePortData(npcMessageModel, "SUB_ACTION_RECEIVED");
			PortDataModel portDataModel=npcService.getPortDataModel(portMessageModel);

			logger.info("Updating port data for NPC Message ID: {}  and Internal Port ID: {} has done successfully and the port Status updated with Status \" {} \"",
					 npcMessageModel.getNPCMessageID(),portMessageModel.getInternalPortID(), portDataModel.getPortStatus() );

			npcService.processActivationStatus(npcMessageModel);
			npcService.processDeactivationDone(npcMessageModel);
			logger.info(
					"The process of inserting into PORT_ACTIVATION_STATUS and PORT_DISCONNECT_STATUS in case of message code is NP_ACTIVATED_STATUS_MESSAGE or NP_DEACT_STATUS for NPC Message ID: {}  and Internal Port ID: {}  has completed..."
							 ,npcMessageModel.getNPCMessageID(), portMessageModel.getInternalPortID());

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
			logger.debug("The process of inserting or removing from SYNC table for NPC Message ID: {}  and Internal Port ID: {}   has completed..."
					,npcMessageModel.getNPCMessageID() ,portDataModel.getInternalPortID());
			npcService.saveSyncHistoryMessageByNPCMessage(npcMessageModel, portDataModel);
			logger.debug("The process of inserting into SYNC History table for NPC Message ID: {} and Internal Port ID: {}   has completed..."
					, npcMessageModel.getNPCMessageID() ,portDataModel.getInternalPortID());
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
	 */
	private NPCMessageModel parseMessage(NPCService npcService, String message) throws NPCException {
		ByteArrayInputStream inputStream = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(
					"com.asset.vodafone.npc.webservice.xsd.portmessage:com.asset.vodafone.npc.webservice.xsd.bulksyncmessage");
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			inputStream = new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
			
			NPCData npcData = (NPCData) unmarshaller.unmarshal(inputStream);
			
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
