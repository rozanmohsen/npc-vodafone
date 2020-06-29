package com.asset.vodafone.npc.core.service;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.dao.BulkSyncMessageDAO;
import com.asset.vodafone.npc.core.dao.FailedMessagesDAO;
import com.asset.vodafone.npc.core.dao.GenericDAO;
import com.asset.vodafone.npc.core.dao.NPCMessageDAO;
import com.asset.vodafone.npc.core.dao.NumbersToPortDAO;
import com.asset.vodafone.npc.core.dao.PortDataDAO;
import com.asset.vodafone.npc.core.dao.PortMessageDAO;
import com.asset.vodafone.npc.core.dao.SubscriberDataDAO;
import com.asset.vodafone.npc.core.dao.SyncDAO;
import com.asset.vodafone.npc.core.dao.SyncHistoryDAO;
import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.dao.*;
import com.asset.vodafone.npc.core.models.BulkSyncMessageModel;
import com.asset.vodafone.npc.core.models.FailedMessagesModel;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.models.NumbersToPortModel;
import com.asset.vodafone.npc.core.models.ParticipantModel;
import com.asset.vodafone.npc.core.models.PortDataModel;
import com.asset.vodafone.npc.core.models.PortMessageModel;
import com.asset.vodafone.npc.core.models.SyncHistoryModel;
import com.asset.vodafone.npc.core.models.SyncModel;
import com.asset.vodafone.npc.core.models.TimeSlotModel;
import com.asset.vodafone.npc.core.utils.BusinessWorkTime;
import com.asset.vodafone.npc.webservice.xsd.portmessage.NumberDataType;

public class NPCService {

	private static ResourceBundle npcProperties;
	private static Connection conn;
	private HashMap<String, String[]> requiredMessageCodes;
	private HashMap<String, String[]> optionalMessageCodes;
	private HashMap<String, String[]> messageCodesTimeFrames;
	private HashMap<String, String> timeFrameDependeny;
	private HashMap<String, String> portActions;
	private HashMap<String, String> synchronizationActions;
	private HashMap<String, String> disconnectionActions;
	private HashMap<String, String> closeActions;
	private String action;
	private String subAction;
	private String processCode;
	private String mnpSchemaName;

	/**
	 * constructor of NPCService class
	 */

	private NPCService() {
		action = "";
		subAction = "";
		processCode = "";
		initializeMessageCodesFromPropertiesFile();
		initializeMessageCodesTimeFrames();
		initializeTimeFramesDependencies();
		initializePortingActions();
		mnpSchemaName = System.getenv("MNP_SCHEMA_NAME");
	}

	public static String getRunnerFetchedRowNumber() {
		return npcProperties.getString("RUNNER_FETCHED_ROW_NUMBER");
	}

	/**
	 * Method to get instance of NPCService class open connection with database
	 * 
	 * @param processClass
	 * @param npcProperties
	 * @return
	 * @throws NPCException
	 */
	public static NPCService initiateDBConnection(ResourceBundle npcProperties) throws NPCException {
		NPCService.npcProperties = npcProperties;

		try {

			conn = getDBConnection();

		} catch (ClassNotFoundException ex) {
			throw new NPCException(ex, NPCException.DATABASE_CONNECTION_DRIVER_ERROR_CODE,
					String.format("Invalid or Wrong JDBC Driver %s", ex.getMessage()));

		} catch (SQLException ex) {
			logger.error("Cannot Establish Database Connection", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_CONNECTION_ESTABLISHING_ERROR_CODE, ex.getMessage());
		} catch (Exception e) {
			throw new NPCException(e, NPCException.DATABASE_CONNECTION_ESTABLISHING_ERROR_CODE,
					String.format("Cannot Establish Database Connection %s ", e.getMessage()));
		}
		return new NPCService();
	}

	/**
	 * Method to return connection with database
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static Connection getDBConnection() throws ClassNotFoundException, SQLException {
		String dbURL = System.getenv("DB_URL");
		String dbUserName = System.getenv("DB_USER_NAME");
		String dbPassword = System.getenv("DB_PASSWORD");
		if (dbURL == null)
			dbURL = npcProperties.getString("DB_URL");
		if (dbUserName == null)
			dbUserName = npcProperties.getString("DB_USER_NAME");
		if (dbPassword == null)
			dbPassword = npcProperties.getString("DB_PASSWORD");

		if (conn == null || conn.isClosed()) {

			logger.debug("Start Initializing Database Connection..");
			Class.forName(String.valueOf(npcProperties.getString("DB_DRIVER")));
			conn = DriverManager.getConnection(dbURL, String.valueOf(dbUserName), String.valueOf(dbPassword));
			logger.debug("Initializing Database Connection has been done Successfully...");
		}

		return conn;
	}

	/**
	 * Insert MEssage into 5 tables
	 * ('NPC_Message','port_Message','Subscriber_Data','Numbers_to_port') if MEssage
	 * from type BulkSync insert it to 'Bulk_Sync_Message' table
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	public void insertNPCMessage(NPCMessageModel npcMessageModel) throws NPCException {
		try {
			String currentDate = GenericDAO.getCurrentDateTime(conn, "DD/MM/YYYY HH24:MI:SS");
			npcMessageModel.setCreatedDate(currentDate);
			npcMessageModel.setTransactionDate(currentDate);
		} catch (SQLException ex) {
			
			logger.error("Error in Insert statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement  ");
		}
		try {
			logger.debug("Start inserting into NPC_MESSAGE table with NPC Message ID {}",
					npcMessageModel.getNPCMessageID());
			NPCMessageDAO.insertNPCMessage(conn, npcMessageModel);
			logger.debug("NPC message has been insterted into NPC_MESSAGE table successfully");
			if (npcMessageModel instanceof PortMessageModel) {
				PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
				long internalPortID = GenericDAO.getInternalPortIDByPortIDOrMSISDN(conn, portMessageModel);
				if (internalPortID == -1L)
					internalPortID = GenericDAO.getNextValueOfSequence(conn, "INTERNAL_PORT_ID_SEQ ");
				portMessageModel.setInternalPortID(String.valueOf(internalPortID));
				logger.debug("Start inserting into PORT_MESSAGE table with NPC Message ID {}",
						npcMessageModel.getNPCMessageID());
				PortMessageDAO.insertPortMessage(conn, portMessageModel);
				logger.debug("NPC message has been insterted into PORT_MESSAGE table successfully");
				if (portMessageModel.getSubscriberDataModel() != null) {
					logger.debug("Start inserting into SUBSCRIBER_DATA table with NPC Message ID {}",
							npcMessageModel.getNPCMessageID());
					SubscriberDataDAO.insertSubscriberData(conn, portMessageModel.getSubscriberDataModel());
					logger.debug("Subscriber data has been inserted into SUBSCRIBER_DATA table successfully  ");
				}
				ArrayList<NumbersToPortModel> numbersToPortList = (ArrayList<NumbersToPortModel>) portMessageModel
						.getNumbersToPortList();
				logger.debug("Start inserting number data into NUMBERSTOPORT table with NPC Message ID {}",
						npcMessageModel.getNPCMessageID());
				for (int i = 0; i < numbersToPortList.size(); i++)
					NumbersToPortDAO.insertNumberData(conn, numbersToPortList.get(i));

				logger.debug("Number data has been inserted into NUMBERSTOPORT table  successfully");
				return;
			}
			if (npcMessageModel instanceof BulkSyncMessageModel) {
				BulkSyncMessageModel bulkSyncMessageModel = (BulkSyncMessageModel) npcMessageModel;
				logger.debug("Start inserting into BULK_SYNC_MESSAGE table with NPC Message ID {}",
						npcMessageModel.getNPCMessageID());
				BulkSyncMessageDAO.insertBulkSyncMessage(conn, bulkSyncMessageModel);
				logger.debug(
						"Bulk Sync Message has been inserted into BULK_SYNC_MESSAGE table successfully with NPC Message ID: {}",
						bulkSyncMessageModel.getNPCMessageID());
			}
				
				
			
		} catch (SQLException ex) {
			logger.error("Error in Insert statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE, "Error in Insert statement ");
		}
	}

	public long getNextNPCMessageID() throws NPCException {
		try {
			return GenericDAO.getNextValueOfSequence(conn, "NPC_MESSAGE_ID_SEQ");
		} catch (SQLException ex) {
			logger.error("Error in Select statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement ");
		}
	}

	/**
	 * insert Messages that failed to be sent into Failed_Message_Queue table
	 * 
	 * @param exception
	 * @param npcMessageID
	 * @throws NPCException
	 */
	public static void insertFailedMessage(NPCException exception, long npcMessageID) throws NPCException {
		try {
			FailedMessagesModel failedMessagesModel = new FailedMessagesModel();
			failedMessagesModel.setNPCMessageID(npcMessageID);
			failedMessagesModel.setReason(exception.getErrorStackTrace());
			logger.debug("Start Inserting Failed Message into FAILED_MESSAGES_QUEUE table");
			FailedMessagesDAO.insertFailedMessage(conn, failedMessagesModel);
			logger.debug("Inserting Failed Message into FAILED_MESSAGES_QUEUE table has been done successfully with NPC Message ID {}",failedMessagesModel.getNPCMessageID());
		} catch (SQLException ex) {
			logger.error("Error in Insert statement ", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE, "Error in Insert statement ");
		}
	}

	/**
	 * Method to return NPC MessageMOdel based on message code
	 * 
	 * @param npcMessageModel
	 * @return
	 * @throws NPCException
	 * @throws JAXBException
	 */
	public NPCMessageModel getNPCMessage(NPCMessageModel npcMessageModel) throws NPCException, JAXBException {
		try {
			return NPCMessageDAO.getNPCMessage(conn, npcMessageModel, requiredMessageCodes, optionalMessageCodes);
		} catch (SQLException ex) {
			logger.error("Error in Select statement ", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement");
		}
	}

	/**
	 * update NPC_MEssage table TransactionDate Field after sending Message to NTRA
	 * with current date
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	public void updateFieldsAfterSending(NPCMessageModel npcMessageModel) throws NPCException {
		try {
			npcMessageModel.setTransactionDate(GenericDAO.getCurrentDateTime(conn, "DD/MM/YYYY HH24:MI:SS"));
			logger.debug("Start updating Fields after sending message...");
			NPCMessageDAO.updateFieldsAfterSending(conn, npcMessageModel);
			logger.debug(
					"Updated Fields:  Sent = \" {} \"  | Transaction Date = \" {} \" | Machine IP address(Picked_By) = \" {} \"  | Returned Message = \" {} \" | Message Xml = \" {} \" ",
					npcMessageModel.isSent() ? 1 : 0, npcMessageModel.getTransactionDate(),
							npcMessageModel.getPickedBy(),npcMessageModel.getReturnedMessage(), npcMessageModel.getMessageXML()
					);
			logger.debug("Updating Fields after sending message has been done successfully...");
		} catch (SQLException ex) {
			logger.error("Error in Update statement ", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement ");
		}
	}

	/**
	 * update sent ,Returned Message and MessageXML after sending MEssage to NTRA
	 * 
	 * @param npcMessageID
	 * @param isSent
	 * @param returnedMessage
	 * @param messageXML
	 * @throws NPCException
	 */
	public void updateFieldsAfterSending(long npcMessageID, boolean isSent, String returnedMessage, String messageXML)
			throws NPCException {
		NPCMessageModel npcMessageModel = new NPCMessageModel();
		npcMessageModel.setNPCMessageID(npcMessageID);
		npcMessageModel.setReturnedMessage(returnedMessage);
		npcMessageModel.setSent(isSent);
		npcMessageModel.setMessageXML(messageXML);
		updateFieldsAfterSending(npcMessageModel);
	}

	/**
	 * updated NPC_Message table set Returned Message field
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	public void updateReturnedMessage(NPCMessageModel npcMessageModel) throws NPCException {
		try {
			NPCMessageDAO.updateReturnedMessageField(conn, npcMessageModel);
		} catch (SQLException ex) {
			logger.error("Error in Update statement ", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
		}
	}

	/**
	 * getUnsentMessages method return messages that has sent flag=0 and picked_by
	 * column =null that means there is no any job picked that message for sending
	 * 
	 * @return
	 * @throws NPCException
	 */
	public List<NPCMessageModel> getUnsentMessages() throws NPCException {
		try {
			return NPCMessageDAO.getUnsentMessages(conn, requiredMessageCodes, optionalMessageCodes);
		} catch (SQLException ex) {
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE,
					String.format("Error in Select statement %s", ex.getMessage()));
		} catch (JAXBException ex) {
			logger.error("Cannot Create JAXB Object", ex.getMessage());
			throw new NPCException(ex, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE, "Cannot Create JAXB Object");
		}
	}

	/**
	 * MEthod to initialize Message code from Properties file
	 */
	private void initializeMessageCodesFromPropertiesFile() {
		Enumeration<String> keys = npcProperties.getKeys();
		requiredMessageCodes = new HashMap<>();
		optionalMessageCodes = new HashMap<>();
		String propertyKey = "";
		while (keys.hasMoreElements()) {
			propertyKey = String.valueOf(keys.nextElement());
			if (propertyKey.startsWith("MESSAGE_CODE_")) {
				String keyValue = npcProperties.getString(propertyKey);
				String[] fields = null;
				if (propertyKey.endsWith("_REQUIRED")) {
					fields = (keyValue + ",").split("\\s*,\\s*");
					requiredMessageCodes.put(fields[0], fields);
				} else if (propertyKey.endsWith("_OPTIONAL")) {
					fields = (keyValue + ",").split("\\s*,\\s*");
					optionalMessageCodes.put(fields[0], fields);
				}
			}
		}
	}

	/**
	 * method to initialize time frame of Message codes
	 */
	private void initializeMessageCodesTimeFrames() {
		Enumeration<String> keys = npcProperties.getKeys();
		messageCodesTimeFrames = new HashMap<>();
		String propertyKey = "";
		while (keys.hasMoreElements()) {
			propertyKey = String.valueOf(keys.nextElement());
			if (propertyKey.endsWith("TIME_FRAME")) {
				if (propertyKey.endsWith("CURRENT_MAX_TIME_FRAME"))
					 processInitializingMessageCodesTimeFrames(propertyKey, (short) 0);
			} else if (propertyKey.endsWith("CURRENT_MIN_TIME_FRAME")) {
					 processInitializingMessageCodesTimeFrames(propertyKey, (short) 1);
			} else if (propertyKey.endsWith("NEXT_MAX_TIME_FRAME")) {
					 processInitializingMessageCodesTimeFrames(propertyKey, (short) 2);
			} else if (propertyKey.endsWith("NEXT_MIN_TIME_FRAME"))
					 processInitializingMessageCodesTimeFrames(propertyKey, (short) 3);
		}
	}

	/**
	 * Initialize Dependencies time Frame for message codes
	 */
	private void initializeTimeFramesDependencies() {
		Enumeration<String> keys = npcProperties.getKeys();
		timeFrameDependeny = new HashMap<>();
		String propertyKey = "";
		while (keys.hasMoreElements()) {
			propertyKey = String.valueOf(keys.nextElement());
			if (propertyKey.endsWith("TIME_DEPENDENCY")) {
				String keyValue = npcProperties.getString(propertyKey);
				String[] fields = (keyValue + ",").split("\\s*,\\s*");
				StringBuilder sb = new StringBuilder();
				String dependentMessageCodes = "";
				for (int i = 1; i < fields.length; i++) {
					sb.append(dependentMessageCodes + fields[i] + ",");
					dependentMessageCodes = sb.toString();
				}

				timeFrameDependeny.put(fields[0],
						dependentMessageCodes.substring(0, dependentMessageCodes.length() - 1));
			}
		}
	}

	/**
	 * Initialize port Action from properties file
	 */
	private void initializePortingActions() {
		Enumeration<String> keys = npcProperties.getKeys();
		String propertyKey = "";
		portActions = new HashMap<>();
		synchronizationActions = new HashMap<>();
		disconnectionActions = new HashMap<>();
		closeActions = new HashMap<>();
		while (keys.hasMoreElements()) {
			propertyKey = String.valueOf(keys.nextElement());
			String keyValue = npcProperties.getString(propertyKey);
			String[] fields = (keyValue + ",").split("\\s*,\\s*");
			if (propertyKey.startsWith("PORT_ACTION"))
				portActions.put(fields[0], fields[1]);
			else if (propertyKey.startsWith("SYNCHRONIZATION_ACTION"))
				synchronizationActions.put(fields[0], fields[1]);
			else if (propertyKey.startsWith("DISCONNECTION_ACTION"))
				disconnectionActions.put(fields[0], fields[1]);
			else if (propertyKey.startsWith("CLOSE_ACTION"))
				closeActions.put(fields[0], fields[1]);
		}
	}

	/**
	 * Initialization of messages codes time frames
	 * 
	 * @param propertyKey
	 * @param timeFrameIndex
	 */
	private void processInitializingMessageCodesTimeFrames(String propertyKey, short timeFrameIndex) {
		String keyValue = npcProperties.getString(propertyKey);
		String[] fields = (keyValue + ",").split("\\s*,\\s*");
		String[] timeFrames = null;
		if (!messageCodesTimeFrames.containsKey(fields[0])) {
			timeFrames = new String[4];
			messageCodesTimeFrames.put(fields[0], timeFrames);
		} else {
			timeFrames = messageCodesTimeFrames.get(fields[0]);
		}
		timeFrames[timeFrameIndex] = fields[1];
	}

	public void releaseConnection() throws NPCException {
		try {
			if (conn != null) {
				logger.debug("Log {} ", conn);

				conn.close();

			}
		} catch (SQLException ex) {
			logger.error("NPC General Error", ex.getMessage());
			throw new NPCException(ex, NPCException.GENERAL_ERROR_CODE, "Unknown Error");
		}
	}

	/**
	 * Reset PickedBy method used if message has failed to be sent to NTRA reset
	 * picked_by column =null
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	public void resetPickedByField(final NPCMessageModel npcMessageModel) throws NPCException {
		try {
			NPCMessageDAO.resetPickedByField(NPCService.conn, npcMessageModel);
		} catch (SQLException ex) {
			logger.error("Error in Update statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
		}
	}

	public static void updateSentFieldForCorruptedMessages(PortMessageModel portMessageModel) throws NPCException {

		try {
			PortMessageDAO.updateSentFieldForCorruptedMessages(conn, portMessageModel);

		} catch (SQLException ex) {
			logger.error("Error in Update statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");

		}

	}

	/**
	 * Update updateNPCMessageCurrentDate and updateNPCMessageNextDate in
	 * NPC_Message table based on time frame dependency of each message code and
	 * message code time frame
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	public void updateNPCMessageCurrentAndNextDate(NPCMessageModel npcMessageModel) throws NPCException {
		if (npcMessageModel instanceof PortMessageModel) {

			PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
			updateNPCMessageCurrentDate(portMessageModel);
			updateNPCMessageNextDate(portMessageModel);

		}
	}

	/**
	 * Update updateNPCMessageCurrentDate in NPC_Message table based on time frame
	 * dependency of each message code and message code time frame
	 * 
	 * @param portMessageModel
	 * @throws NPCException
	 */
	private void updateNPCMessageCurrentDate(PortMessageModel portMessageModel) throws NPCException {
		String[] timeFrames = messageCodesTimeFrames.get(portMessageModel.getPortMessageType().getMessageCode());

		if (timeFrames == null)
			return;
		String currentMaxDateDependencyMessage = String.valueOf(timeFrameDependeny.get(timeFrames[0]));
		String currentMinDateDependencyMessage = String.valueOf(timeFrameDependeny.get(timeFrames[1]));
		try {
			if (!"null".equals(currentMaxDateDependencyMessage)) {
				String currentMaxDate = getNPCMessageDate(portMessageModel, timeFrames[0],
						currentMaxDateDependencyMessage);
				if (currentMaxDate != null) {
					portMessageModel.setCurrentMessageMaxDate(currentMaxDate);
					logger.debug("Start updating message current max date with date {} ",currentMaxDate);
					NPCMessageDAO.updateCurrentMessageMaxDateField(conn, portMessageModel);
					logger.debug("Updating message current max date has been done successfully");
				}
			}
			if (!"null".equals(currentMinDateDependencyMessage)) {

				String currentMinDate = getNPCMessageDate(portMessageModel, timeFrames[1],
						currentMinDateDependencyMessage);
				if (currentMinDate != null) {
					portMessageModel.setCurrentMessageMinDate(currentMinDate);
					logger.debug("Start updating message current min date with date {} ",currentMinDate);
					NPCMessageDAO.updateCurrentMessageMinDateField(conn, portMessageModel);
					logger.debug("Updating message current min date has been done successfully");

				}
			}
		} catch (SQLException ex) {
			logger.error("Error in Update statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
		}
	}

	/**
	 * method to get Message date to according to Business working time
	 * 
	 * @param portMessageModel
	 * @param timeSlot
	 * @param dependencyMessageCode
	 * @return
	 * @throws NPCException
	 */
	private String getNPCMessageDate(PortMessageModel portMessageModel, String timeSlot, String dependencyMessageCode)
			throws NPCException {
		NPCMessageModel datePortMessageModel = null;
		try {
			datePortMessageModel = PortMessageModel.createPortMessage();
			((PortMessageModel) datePortMessageModel).getPortMessageType().setMessageCode(dependencyMessageCode);
			if (portMessageModel.isPortExists())
				((PortMessageModel) datePortMessageModel).getPortMessageType()
						.setPortID(portMessageModel.getPortMessageType().getPortID());
			((PortMessageModel) datePortMessageModel).setInternalPortID(portMessageModel.getInternalPortID());
			datePortMessageModel = NPCMessageDAO.getLastNPCMessageByMessageCode(conn,
					(PortMessageModel) datePortMessageModel);
			if (datePortMessageModel == null)
				return null;
		} catch (SQLException ex) {
			logger.error("Error in Select statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement");
		} catch (JAXBException ex) {
			logger.error("Cannot Create JAXB Object", ex.getMessage());
			throw new NPCException(ex, NPCException.JAXB_CREATE_OBJECT_ERROR_CODE, "Cannot Create JAXB Object");
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			java.util.Date currentDate = sdf.parse(datePortMessageModel.getTransactionDate());
			TimeSlotModel timeSlotModel = new TimeSlotModel(timeSlot);
			try {
				String companyFlag = portMessageModel.getSubscriberDataModel().getSubscriberDataType().getCompanyFlag();
				timeSlotModel = TimeSlotDAO.getTimeSlotValue(conn, timeSlotModel, companyFlag);
			} catch (Exception e) {
				timeSlotModel = TimeSlotDAO.getTimeSlotValue(conn, timeSlotModel, null);
			}

			if (timeSlotModel == null)
				return null;
			java.util.Date resultDate = null;
			BusinessWorkTime businessWorkTime = new BusinessWorkTime(npcProperties);
			businessWorkTime.setCurrentTime(currentDate);
			if (timeSlotModel.isBusiness()) {
				businessWorkTime.addBusinessTime(timeSlotModel.getTimeUnitID(), timeSlotModel.getTimeValue());
				resultDate = businessWorkTime.getBusinessTime();
			} else {
				businessWorkTime.addCalendarTime(timeSlotModel.getTimeUnitID(), timeSlotModel.getTimeValue());
				resultDate = businessWorkTime.getCalendarTime();
			}
			return sdf.format(resultDate);
		} catch (SQLException ex) {
			logger.error("Error in Update statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
		} catch (ParseException ex) {
			logger.error("Error in parsing date", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_DATE_PARSING_ERROR_CODE, "Error in parsing date");
		} catch (NPCException ex) {
			logger.error("Error in Company flag tag ", ex.getMessage());
			throw new NPCException(ex.getMessage(), "Error in TimeSlotValue ");
		}
	}

	/**
	 * Method to update updateNPCMessageNextDate column in NPC_MEssage table
	 * according to time dependency for each Message code and message code time
	 * frame
	 * 
	 * @param portMessageModel
	 * @throws NPCException
	 */
	private void updateNPCMessageNextDate(PortMessageModel portMessageModel) throws NPCException {
		String[] timeFrames = messageCodesTimeFrames.get(portMessageModel.getPortMessageType().getMessageCode());

		if (timeFrames == null)
			return;
		String nextMaxDateDependencyMessage = String.valueOf(timeFrameDependeny.get(timeFrames[2]));
		String nextMinDateDependencyMessage = String.valueOf(timeFrameDependeny.get(timeFrames[3]));
		try {
			if (!"null".equals(nextMaxDateDependencyMessage)) {
				String nextMaxDate = getNPCMessageDate(portMessageModel, timeFrames[2], nextMaxDateDependencyMessage);
				if (nextMaxDate != null) {
					portMessageModel.setNextMessageMaxDate(nextMaxDate);
					logger.debug("Start Updating message Next max date with date {} ",nextMaxDate);
					NPCMessageDAO.updateNextMessageMaxDateField(conn, portMessageModel);
					logger.debug("Updating message  Next max date has been done successfully ");
				}
			}
			if (!"null".equals(nextMinDateDependencyMessage)) {
				String nextMinDate = getNPCMessageDate(portMessageModel, timeFrames[3], nextMinDateDependencyMessage);
				if (nextMinDate != null) {
					portMessageModel.setNextMessageMinDate(nextMinDate);
					logger.debug("Start Updating message Next max date with date {} ",nextMinDate);
					NPCMessageDAO.updateNextMessageMinDateField(conn, portMessageModel);
					logger.debug("Updating message Next max date has been done successfully ");

				}
			}
		} catch (SQLException ex) {
			logger.error("Error in Update statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
		}
	}

	/**
	 * Method to update Port Status column in Port_Data table with process code,
	 * Action and subAction based on Message code
	 * 
	 * @param npcMessageModel
	 * @param subActionKey
	 * @throws NPCException
	 */
	public void updatePortData(NPCMessageModel npcMessageModel, String subActionKey) throws NPCException {
		if (!(npcMessageModel instanceof PortMessageModel))
			return;
		subAction = npcProperties.getString(subActionKey);
		PortDataModel portDataModel = PortDataModel.getInstance(npcMessageModel);
		PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
		String messageCode = portMessageModel.getPortMessageType().getMessageCode();
		PortDataModel obtainedPortDataModel = getPortDataModel(portMessageModel);
		if (obtainedPortDataModel != null)
			portDataModel.copyPortData(obtainedPortDataModel);
		try {
			logger.debug("Start Update PORT_DATA table ");
			if (!messageCode.equals(npcProperties.getString("ERROR_NOTIFICATION_MESSAGE_CODE"))) {
				ParticipantModel participantModel = new ParticipantModel();
				participantModel.setParticipantID(npcProperties.getString("PARTICIPANT_ID"));
				try {
					participantModel = GenericDAO.getParticipantModel(conn, participantModel);
				} catch (SQLException ex) {
					throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE,
							"Error in Select statement");
				}
				if (participantModel == null)
					throw new NPCException(NPCException.PROCESSING_PARTICIPANT_NOT_FOUND_ERROR_CODE,
							MessageFormat.format(
									"Participant \"{0}\" not found  please contact your Database administrator or check the resource file",
									npcProperties.getString("PARTICIPANT_ID")));
				fillPortStatus(portDataModel, portMessageModel.getPortMessageType().getMessageCode(), participantModel);
				if (obtainedPortDataModel != null) {
					String[] statusParts = obtainedPortDataModel.getPortStatus().split("\\s*-\\s*");
					if ((statusParts[1] + "-" + statusParts[2]).equals(npcProperties.getString("PORT_RESUBMIT_ACTION")))
						action = statusParts[1];
				}
				portDataModel.setPortStatus(processCode + "-" + action + "-" + subAction);
			} else if (obtainedPortDataModel != null) {
				String[] statusParts = obtainedPortDataModel.getPortStatus().split("\\s*-\\s*");
				processCode = statusParts[0];
				action = statusParts[1];
				subAction = statusParts[2];
				portDataModel.setPortStatus(processCode + "-" + action + "-" + subAction);
				portDataModel.setErrorMessageReceived((short) 1);
			}
			if ("".equals(processCode) && obtainedPortDataModel != null) {
				String[] statusParts = obtainedPortDataModel.getPortStatus().split("\\s*-\\s*");
				processCode = statusParts[0];
				action = statusParts[1];
				portDataModel.setPortStatus(processCode + "-" + action + "-" + subAction);
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			logger.error(ex.getMessage());
			portDataModel.setPortStatus(null);
		}
		populatePortData(portDataModel, obtainedPortDataModel);
		logger.debug(
				"Updating PORT_DATA table for NPC Message ID: {}  and Internal Port ID: {} has been done successfully and the port Status updated with Status \" {} \"",
				npcMessageModel.getNPCMessageID(), portMessageModel.getInternalPortID(), portDataModel.getPortStatus());
		action = "";
		subAction = "";
		processCode = "";
	}

	/**
	 * insert Message Parameters in to Port Data table
	 * 
	 * @param portDataModel
	 * @param obtainedPortDataModel
	 * @throws NPCException
	 */
	private void populatePortData(PortDataModel portDataModel, PortDataModel obtainedPortDataModel)
			throws NPCException {
		if (obtainedPortDataModel == null)
			try {
				portDataModel.setPortDataID(GenericDAO.getNextValueOfSequence(conn, "PORT_DATA_ID_SEQ "));
				portDataModel.setNprNPCMessageID(portDataModel.getLastNPCMessageID());
				PortDataDAO.insertPortData(conn, portDataModel);
				return;
			} catch (SQLException ex) {
				throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE, "Error in Insert statement");
			}
		String portID = portDataModel.getPortID();
		String obtainedPortID = obtainedPortDataModel.getPortID();
		if (obtainedPortID != null && portID != null && !obtainedPortID.equals(portID))
			try {
				portDataModel.setPortDataID(GenericDAO.getNextValueOfSequence(conn, "PORT_DATA_ID_SEQ "));
				portDataModel.setNprNPCMessageID(portDataModel.getLastNPCMessageID());
				PortDataDAO.insertPortData(conn, portDataModel);
				return;
			} catch (SQLException ex) {
				logger.error("Error in Update statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
			}
		try {
			portDataModel.setPortDataID(obtainedPortDataModel.getPortDataID());
			PortDataDAO.updatePortData(conn, portDataModel);

		} catch (SQLException ex) {
			logger.error("Error in Update statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE, "Error in Update statement");
		}
	}

	/**
	 * Method to return PortDataModel to update Port status
	 * 
	 * @param npcMessageModel
	 * @return
	 * @throws NPCException
	 */
	public PortDataModel getPortDataModel(NPCMessageModel npcMessageModel) throws NPCException {
		if (!(npcMessageModel instanceof PortMessageModel))
			return null;
		PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
		try {
			PortDataModel portDataModel = PortDataModel.getInstance();
			if (portMessageModel.isPortExists())
				portDataModel.setPortID(portMessageModel.getPortMessageType().getPortID());
			portDataModel.setInternalPortID(portMessageModel.getInternalPortID());
			return PortDataDAO.getPortDataModel(conn, portDataModel);
		} catch (SQLException ex) {
			logger.error("Error in Select statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement");
		}
	}

	/**
	 * fill port Status Column with status of 3 parts process code, Action,SubAction
	 * based on Message code
	 * 
	 * @param portDataModel
	 * @param messageCode
	 * @param participantModel
	 */
	private void fillPortStatus(PortDataModel portDataModel, String messageCode, ParticipantModel participantModel) {
		String donorID = portDataModel.getDonorID();
		String recipientID = portDataModel.getRecipientID();
		if (portActions.containsKey(messageCode)) {
			action = String.valueOf(portActions.get(messageCode));
			obtainProcessCode(donorID, recipientID, participantModel, "PORT_IN_PROCESS_CODE", "PORT_OUT_PROCESS_CODE");
		}
		if (synchronizationActions.containsKey(messageCode) && "".equals(processCode)) {
			action = String.valueOf(synchronizationActions.get(messageCode));
			processCode = npcProperties.getString("PORT_SYNC_PROCESS_CODE");
		}
		if (disconnectionActions.containsKey(messageCode) && "".equals(processCode)) {
			action = String.valueOf(disconnectionActions.get(messageCode));
			obtainProcessCode(donorID, recipientID, participantModel, "DISCONNECTION_IN_PROCESS_CODE",
					"DISCONNECTION_OUT_PROCESS_CODE");
		}
		if (closeActions.containsKey(messageCode) && "".equals(processCode)) {
			action = String.valueOf(closeActions.get(messageCode));
			subAction = npcProperties.getString("SUB_ACTION_DONE");
			obtainProcessCode(donorID, recipientID, participantModel, "PORT_IN_PROCESS_CODE", "PORT_OUT_PROCESS_CODE");
		}
	}

	private void obtainProcessCode(String donorID, String recipientID, ParticipantModel participantModel,
			String processInKey, String processOutKey) {
		if (!"".equals(donorID) && donorID != null && ("".equals(recipientID) || recipientID == null)) {
			if (donorID.equals(participantModel.getParticipantID())) {
				processCode = npcProperties.getString(processOutKey);

			}
		} else if (!"".equals(recipientID) && recipientID != null && ("".equals(donorID) || donorID == null)) {
			if (recipientID.equals(participantModel.getParticipantID())) {
				processCode = npcProperties.getString(processInKey);

			}
		} else if (!"".equals(donorID) && donorID != null && !"".equals(recipientID) && recipientID != null) {
			if (donorID.equals(participantModel.getParticipantID())) {
				processCode = npcProperties.getString(processOutKey);

			}
			if (recipientID.equals(participantModel.getParticipantID()))
				processCode = npcProperties.getString(processInKey);
		}
	}

	/**
	 * insert Messages Port ID into Port_Activation_Status table during Activation
	 * Process done
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */

	public void processActivationStatus(NPCMessageModel npcMessageModel) throws NPCException {
		if (!(npcMessageModel instanceof PortMessageModel))
			return;
		PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
		String messageCode = portMessageModel.getPortMessageType().getMessageCode();
		if (messageCode.equals(npcProperties.getString("NP_ACTIVATED_STATUS_MESSAGE_CODE"))) {
			logger.debug("Start Process Activation Status for port ID {}  and Internal Port ID {} ",
					portMessageModel.getPortMessageType().getPortID(), portMessageModel.getInternalPortID());
			String comments2 = portMessageModel.getPortMessageType().getComments2();
			if ("null".equals(comments2) || comments2 == null)
				return;
			HashMap<String, String> fields = new HashMap<>();
			fields.put("PORTID", portMessageModel.getPortMessageType().getPortID());
			fields.put("ACTIVATION_COMMENT", comments2);
			fields.put("TRANSACTION_DATE", npcMessageModel.getTransactionDate());
			try {
				if (mnpSchemaName == null) {
					mnpSchemaName = npcProperties.getString("MNP_SCHEMA_NAME");
				}
				GenericDAO.insertIntoTable(conn, mnpSchemaName, "PORT_ACTIVATION_STATUS", fields);

				logger.debug(
						"Process Activation status has been done successfully for Port ID {} and Internal Port ID {}",
						portMessageModel.getPortMessageType().getPortID(), portMessageModel.getInternalPortID());

			} catch (SQLException ex) {
				logger.error("Error in Insert statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE, "Error in Insert statement");
			}

		}
	}

	/**
	 * Insert Message Port ID in to Process_Disconnect_Status during Deactivation
	 * Process Done
	 * 
	 * @param npcMessageModel
	 * @throws NPCException
	 */
	public void processDeactivationDone(NPCMessageModel npcMessageModel) throws NPCException {
		if (!(npcMessageModel instanceof PortMessageModel))
			return;
		PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
		String messageCode = portMessageModel.getPortMessageType().getMessageCode();
		if (messageCode.equals(npcProperties.getString("NP_DEACT_STATUS_MESSAGE_CODE"))) {
			logger.debug("Start Process Deactivation Done for port ID {}  and Internal Port ID {} ",
					portMessageModel.getPortMessageType().getPortID(), portMessageModel.getInternalPortID());
			String comments2 = portMessageModel.getPortMessageType().getComments2();
			if ("null".equals(comments2) || comments2 == null)
				return;
			HashMap<String, String> fields = new HashMap<>();
			fields.put("PORTID", portMessageModel.getPortMessageType().getPortID());
			fields.put("DISCONNECT_COMMENT", comments2);
			fields.put("TRANSACTION_DATE", npcMessageModel.getTransactionDate());
			try {
				if (mnpSchemaName == null) {
					mnpSchemaName = npcProperties.getString("MNP_SCHEMA_NAME");
				}
				GenericDAO.insertIntoTable(conn, mnpSchemaName, "PORT_DISCONNECT_STATUS", fields);

				logger.debug(
						"Process Deactivation Done has been done successfully for Port ID {} and Internal Port ID {}",
						portMessageModel.getPortMessageType().getPortID(), portMessageModel.getInternalPortID());
			} catch (SQLException ex) {
				logger.error("Error in Insert statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE, "Error in Insert statement");
			}
		}
	}

	/**
	 * update Sync table with new data of activated numbers if it is already exist
	 * and insert it if new Activated Numbers
	 * 
	 * @param syncMessages
	 * @throws NPCException
	 */
	public void saveSyncMessages(List<SyncModel> syncMessages) throws NPCException {
		SyncModel syncModel = null;
		boolean isMSISDNExists = false;
		for (int i = 0; i < syncMessages.size(); i++) {
			syncModel = syncMessages.get(i);
			try {
				isMSISDNExists = SyncDAO.isMSISDNExists(conn, syncModel);
			} catch (SQLException ex) {
				logger.error("Error in Select statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement");
			}
			if (isMSISDNExists)
				try {
					logger.debug("Start Updating SYNC table for MSISDN: {} ",syncModel.getMsisdn());
					SyncDAO.updateSync(conn, syncModel);
					logger.debug("Updating SYNC table has been done successfully for MSISDN: {}  ",syncModel.getMsisdn());
				} catch (SQLException ex) {
					logger.error("Error in Update statement", ex.getMessage());
					throw new NPCException(ex, NPCException.DATABASE_SQL_UPDATE_ERROR_CODE,
							"Error in Update statement");
				}
			else
				try {
					logger.debug("Start inserting into SYNC table for MSISDN: {} ",syncModel.getMsisdn());
					SyncDAO.insertSync(conn, syncModel);
					logger.debug("Inserting into SYNC table has been done successfully for MSISDN: {}  ",syncModel.getMsisdn());
				} catch (SQLException ex) {
					logger.error("Error in Insert statement", ex.getMessage());
					throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE,
							"Error in Insert statement");
				}
		}
	}

	/**
	 * Save Activated Numbers For Historical Purpose
	 * 
	 * @param syncMessages
	 * @throws NPCException
	 */
	public void saveSyncHistoryMessages(List<SyncModel> syncMessages) throws NPCException {
		SyncHistoryModel syncHistoryModel = null;
		long lastMSISDNSeqNumber;
		for (int i = 0; i < syncMessages.size(); i++) {
			syncHistoryModel = (SyncHistoryModel) syncMessages.get(i);
			try {
				lastMSISDNSeqNumber = SyncHistoryDAO.getLastMSISDNSequenceNumber(conn, syncHistoryModel);
			} catch (SQLException ex) {
				logger.error("Error in Select statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement");
			}
			if (lastMSISDNSeqNumber == -1L)
				syncHistoryModel.setSeqNumber(1L);
			else
				syncHistoryModel.setSeqNumber(lastMSISDNSeqNumber + 1L);
			try {
				logger.debug("Start inserting into SYNC_HISTORY table for MSISDN: {} ",syncHistoryModel.getMsisdn());
				SyncHistoryDAO.insertSyncHistory(conn, syncHistoryModel);
				logger.debug("Inserting into SYNC_HISTORY table has been done successfully for MSISDN: {}  ",syncHistoryModel.getMsisdn());
			} catch (SQLException ex) {
				logger.error("Error in Insert statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_INSERT_ERROR_CODE, "Error in Insert statement");
			}
		}
	}

	/**
	 * delete from Sync table where deactivation Process
	 * 
	 * @param syncMessages
	 * @throws NPCException
	 */
	private void removeSyncMessages(ArrayList<SyncModel> syncMessages) throws NPCException {
		SyncModel syncModel = null;
		for (int i = 0; i < syncMessages.size(); i++) {
			syncModel = syncMessages.get(i);
			try {
				logger.debug("Start remove Sync Message from SYNC table for MSISDN: {} ",syncModel.getMsisdn());
				SyncDAO.removeSYNC(conn, syncModel);
				logger.debug("removing Sync Message from SYNC table for MSISDN: {} has been done successfully",syncModel.getMsisdn());
			} catch (SQLException ex) {
				logger.error("Error in Delete statement", ex.getMessage());
				throw new NPCException(ex, NPCException.DATABASE_SQL_DELETE_ERROR_CODE, "Error in Delete Statement");
			}
		}
	}

	/**
	 * Save Sync Messages(Activated Numbers) in to Sync Table
	 * 
	 * @param npcMessageModel
	 * @param portDataModel
	 * @throws NPCException
	 */
	public void saveSyncMessageByNPCMessage(NPCMessageModel npcMessageModel, PortDataModel portDataModel)
			throws NPCException {
		if (!(npcMessageModel instanceof PortMessageModel))
			return;
		PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
		String messageCode = portMessageModel.getPortMessageType().getMessageCode();
		String[] insertIntoSyncMessageCodes = (npcProperties.getString("SYNC_INSERT_MESSAGE_CODES") + ",")
				.split("\\s*,\\s*");
		for (int i = 0; i < insertIntoSyncMessageCodes.length; i++)
			if (messageCode.equals(insertIntoSyncMessageCodes[i])) {
				ArrayList<SyncModel> syncList = getSyncList(portMessageModel, portDataModel);
				
				
				saveSyncMessages(syncList);

				logger.debug(
						"The process of Save Sync Messages with NPC Message ID: {}  and Internal Port ID: {}  has been done successfully...",
					npcMessageModel.getNPCMessageID(), portDataModel.getInternalPortID());
				return;
			}

		String[] removeSyncMessageCodes = (npcProperties.getString("SYNC_REMOVE_MESSAGE_CODES") + ",")
				.split("\\s*,\\s*");
		for (int i = 0; i < removeSyncMessageCodes.length; i++)
			if (messageCode.equals(removeSyncMessageCodes[i])) {
				ArrayList<SyncModel> syncList = getSyncList(portMessageModel, portDataModel);
				
				removeSyncMessages(syncList);
				logger.debug(
						"The process of remove Sync Messages from SYNC table with NPC Message ID: {}  and Internal Port ID: {}  has been done successfully...",
						npcMessageModel.getNPCMessageID(), portDataModel.getInternalPortID());
				return;
			}

	}

	/**
	 * Save SyncMessages in SyncHistory Table
	 * 
	 * @param npcMessageModel
	 * @param portDataModel
	 * @throws NPCException
	 */
	public void saveSyncHistoryMessageByNPCMessage(NPCMessageModel npcMessageModel, PortDataModel portDataModel)
			throws NPCException {
		if (!(npcMessageModel instanceof PortMessageModel))
			return;
		PortMessageModel portMessageModel = (PortMessageModel) npcMessageModel;
		String messageCode = portMessageModel.getPortMessageType().getMessageCode();
		String[] insertIntoSyncMessageCodes = (npcProperties.getString("SYNC_INSERT_MESSAGE_CODES") + ",")
				.split("\\s*,\\s*");
		for (int i = 0; i < insertIntoSyncMessageCodes.length; i++)
			if (messageCode.equals(insertIntoSyncMessageCodes[i])) {
				ArrayList<SyncModel> syncList = getSyncList(portMessageModel, portDataModel);
				
				saveSyncHistoryMessages(syncList);
				logger.debug(
						"The process of Save Sync History Messages with NPC Message ID: {} and Internal Port ID: {}   has been done successfully...",
						npcMessageModel.getNPCMessageID(), portDataModel.getInternalPortID());
				return;
			}

		String[] removeSyncMessageCodes = (npcProperties.getString("SYNC_REMOVE_MESSAGE_CODES") + ",")
				.split("\\s*,\\s*");
		for (int i = 0; i < removeSyncMessageCodes.length; i++)
			if (messageCode.equals(removeSyncMessageCodes[i])) {
				portDataModel.setRecipientID(portDataModel.getDonorID());
				ArrayList<SyncModel> syncList = getSyncList(portMessageModel, portDataModel);
				
				saveSyncHistoryMessages(syncList);
				logger.debug(
						"The process of remove Sync History Messages for with NPC Message ID: {} and Internal Port ID: {}   has been done successfully...",
						npcMessageModel.getNPCMessageID(), portDataModel.getInternalPortID());
				return;
			}

	}

	/**
	 * Retrieve Sync Messages List
	 * 
	 * @param portMessageModel
	 * @param portDataModel
	 * @return SyncModel
	 * @throws NPCException
	 */
	private ArrayList<SyncModel> getSyncList(PortMessageModel portMessageModel, PortDataModel portDataModel)
			throws NPCException {
		ArrayList<NumbersToPortModel> numbersToPortList = (ArrayList<NumbersToPortModel>) portMessageModel
				.getNumbersToPortList();
		NumberDataType numberDataType = null;
		SyncModel syncModel = null;
		ArrayList<SyncModel> syncMessages = new ArrayList<>();
		BigDecimal timestamp = null;
		try {
			timestamp = NPCMessageDAO.getTransactionDate(conn, portMessageModel);
		} catch (SQLException ex) {
			logger.error("Error in Select statement", ex.getMessage());
			throw new NPCException(ex, NPCException.DATABASE_SQL_SELECT_ERROR_CODE, "Error in Select statement");
		}
		for (int i = 0; i < numbersToPortList.size(); i++) {
			numberDataType = (numbersToPortList.get(i)).getNumberDataType();
			syncModel = new SyncHistoryModel();
			syncModel.setMsisdn(numberDataType.getNumberFrom());
			syncModel.setBulkSyncIDNumber(null);
			syncModel.setTimeStamp(timestamp);
			syncModel.setDonorID(portDataModel.getDonorID());
			syncModel.setRecipientID(portDataModel.getRecipientID());
			syncModel.setNewRoute(portDataModel.getNewRoute());
			syncModel.setIdNumber(portDataModel.getPortID());
			syncMessages.add(syncModel);
			if (numberDataType.getNumberTo() != null && !"".equals(numberDataType.getNumberTo())) {
				BigDecimal numberFrom = new BigDecimal(numberDataType.getNumberFrom());
				BigDecimal numberTo = new BigDecimal(numberDataType.getNumberTo());
				int diff = (int) (numberTo.longValue() - numberFrom.longValue());
				for (int j = 1; j <= diff; j++) {
					syncModel = new SyncHistoryModel();
					syncModel.setMsisdn(numberFrom.add(new BigDecimal(j)).toString());
					syncModel.setBulkSyncIDNumber(null);
					syncModel.setTimeStamp(timestamp);
					syncModel.setDonorID(portDataModel.getDonorID());
					syncModel.setRecipientID(portDataModel.getRecipientID());
					syncModel.setNewRoute(portDataModel.getNewRoute());
					syncModel.setIdNumber(portDataModel.getPortID());
					syncMessages.add(syncModel);
				}
			}
		}
		return syncMessages;
	}

	static final Logger logger = LoggerFactory.getLogger(NPCService.class.getName());

}
