package com.asset.vodafone.npc.core.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.models.BulkSyncMessageModel;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.models.PortMessageModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;

public class NPCMessageDAO {

	/**
	 * Method to Insert Message into NPC_MESSAGE table
	 * 
	 * @param conn
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void insertNPCMessage(Connection conn, NPCMessageModel npcMessageModel) throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO NPC_Message(NPC_MESSAGE_ID,IsPort,Sent,Transaction_Date,ReturnMessage,Current_Message_Max_Date,Current_Message_Min_Date,Create_User,Create_Date,Next_Message_Max_Date,Next_Message_Min_Date,User_Comment,MessageXML) VALUES ("
					+ npcMessageModel.getNPCMessageID() + "," + (npcMessageModel.isPort() ? 1 : 0) + ","
					+ (npcMessageModel.isSent() ? 1 : 0) + ","
					+ (!"null".equals(npcMessageModel.getTransactionDate())
							&& npcMessageModel.getTransactionDate() != null
									? "TO_DATE('" + npcMessageModel.getTransactionDate() + "','"
											+ "DD/MM/YYYY HH24:MI:SS" + "')"
									: "NULL")
					+ ",'" + npcMessageModel.getReturnedMessage() + "',"
					+ (!"null".equals(npcMessageModel.getCurrentMessageMaxDate())
							&& npcMessageModel.getCurrentMessageMaxDate() != null
									? "TO_DATE('" + npcMessageModel.getCurrentMessageMaxDate() + "','"
											+ "DD/MM/YYYY HH24:MI:SS" + "')"
									: "NULL")
					+ ","
					+ (!"null".equals(npcMessageModel.getCurrentMessageMinDate())
							&& npcMessageModel.getCurrentMessageMinDate() != null
									? "TO_DATE('" + npcMessageModel.getCurrentMessageMinDate() + "','"
											+ "DD/MM/YYYY HH24:MI:SS" + "')"
									: "NULL")
					+ ",'" + npcMessageModel.getCreatedUser() + "',"
					+ (!"null".equals(npcMessageModel.getCreatedDate()) && npcMessageModel.getCreatedDate() != null
							? "TO_DATE('" + npcMessageModel.getCreatedDate() + "','" + "DD/MM/YYYY HH24:MI:SS" + "'),"
							: "NULL")
					+ (!"null".equals(npcMessageModel.getNextMessageMaxDate())
							&& npcMessageModel.getNextMessageMaxDate() != null
									? "TO_DATE('" + npcMessageModel.getCurrentMessageMaxDate() + "','"
											+ "DD/MM/YYYY HH24:MI:SS" + "')"
									: "NULL")
					+ ","
					+ (!"null".equals(npcMessageModel.getNextMessageMinDate())
							&& npcMessageModel.getNextMessageMinDate() != null
									? "TO_DATE('" + npcMessageModel.getCurrentMessageMinDate() + "','"
											+ "DD/MM/YYYY HH24:MI:SS" + "')"
									: "NULL")
					+ "," + DBTypeConverter.toSQLVARCHAR2(npcMessageModel.getUserCommnet()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(npcMessageModel.getMessageXML()) + ")";
			stmt.execute(insertStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			throw new SQLException(message + "[" + insertStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method to reset Picked_by field in NPC_Message table if runner job failed to
	 * lock that record for sending it to NTRA
	 * 
	 * @param conn
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void resetPickedByField(final Connection conn, final NPCMessageModel npcMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET PICKED_BY = NULL WHERE " + "NPC_MESSAGE_ID" + " = "
					+ npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static void updateSentField(Connection conn, NPCMessageModel npcMessageModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET Sent=" + (npcMessageModel.isSent() ? 1 : 0) + " WHERE "
					+ "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static void updateMessageXMLField(Connection conn, NPCMessageModel npcMessageModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET MessageXML="
					+ DBTypeConverter.toSQLVARCHAR2(npcMessageModel.getMessageXML()) + " WHERE " + "NPC_MESSAGE_ID"
					+ " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * update Field after sending Method to update NPC_Message table after runner
	 * job has sent xml message to NTRA update MessageXML column , sent and
	 * Transaction_date
	 * 
	 * @param conn
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void updateFieldsAfterSending(Connection conn, NPCMessageModel npcMessageModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET MessageXML="
					+ DBTypeConverter.toSQLVARCHAR2(npcMessageModel.getMessageXML()) + "," + "Sent" + " = "
					+ (npcMessageModel.isSent() ? 1 : 0) + ", " + "ReturnMessage" + " = "
					+ DBTypeConverter.toSQLVARCHAR2(npcMessageModel.getReturnedMessage()) + ", " + "Transaction_Date"
					+ " = " + "TO_DATE('" + npcMessageModel.getTransactionDate() + "','" + "DD/MM/YYYY HH24:MI:SS"
					+ "')" + " WHERE " + "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();

			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * update NPC_MEssage table set Returned Message column=success if message has
	 * sent successfully
	 * 
	 * @param conn
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void updateReturnedMessageField(Connection conn, NPCMessageModel npcMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET ReturnMessage='" + npcMessageModel.getReturnedMessage() + "'"
					+ " WHERE " + "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method to update NPC_Message table set column'Current_Message_Max_Date' with
	 * current message max date value from npcMessageModel
	 * 
	 * @param conn            database connection
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void updateCurrentMessageMaxDateField(Connection conn, NPCMessageModel npcMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET Current_Message_Max_Date =  TO_DATE('"
					+ npcMessageModel.getCurrentMessageMaxDate() + "','" + "DD/MM/YYYY HH24:MI:SS" + "')" + " WHERE "
					+ "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method to update NPC_Message table set column'Current_Message_MIN_Date' with
	 * current message min date value from npcMessageModel
	 * 
	 * @param conn
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void updateCurrentMessageMinDateField(Connection conn, NPCMessageModel npcMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET Current_Message_Min_Date =  TO_DATE('"
					+ npcMessageModel.getCurrentMessageMinDate() + "','" + "DD/MM/YYYY HH24:MI:SS" + "')" + " WHERE "
					+ "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method to update NPC_Message table set column'NEXT_Message_MAX_Date' with
	 * Next message max date value from npcMessageModel
	 * 
	 * @param conn            database connection
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void updateNextMessageMaxDateField(Connection conn, NPCMessageModel npcMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET Next_Message_Max_Date =  TO_DATE('"
					+ npcMessageModel.getNextMessageMaxDate() + "','" + "DD/MM/YYYY HH24:MI:SS" + "')" + " WHERE "
					+ "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * Method to update NPC_Message table set column'NEXT_Message_MIN_Date' with
	 * Next message min date value from npcMessageMode
	 * 
	 * @param conn            database connection
	 * @param npcMessageModel
	 * @throws SQLException
	 */
	public static void updateNextMessageMinDateField(Connection conn, NPCMessageModel npcMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET Next_Message_Min_Date =  TO_DATE('"
					+ npcMessageModel.getNextMessageMinDate() + "','" + "DD/MM/YYYY HH24:MI:SS" + "')" + " WHERE "
					+ "NPC_MESSAGE_ID" + " = " + npcMessageModel.getNPCMessageID();
			stmt.execute(updateStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + updateStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	/**
	 * 
	 * @param conn
	 * @param npcMessageModel
	 * @param requiredMessageCodes
	 * @param optionalMessageCodes
	 * @return
	 * @throws SQLException
	 * @throws JAXBException
	 */
	public static NPCMessageModel getNPCMessage(Connection conn, NPCMessageModel npcMessageModel,
			Map<String, String[]> requiredMessageCodes, Map<String, String[]> optionalMessageCodes)
			throws SQLException, JAXBException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT NPC_MESSAGE_ID,IsPort,Sent,TO_CHAR(Transaction_Date,'DD/MM/YYYY HH24:MI:SS') AS Transaction_Date,ReturnMessage,TO_CHAR(Current_Message_Max_Date,'DD/MM/YYYY HH24:MI:SS') AS Current_Message_Max_Date,TO_CHAR(Current_Message_Min_Date,'DD/MM/YYYY HH24:MI:SS') AS Current_Message_Min_Date,Create_User,TO_CHAR(Create_Date,'DD/MM/YYYY HH24:MI:SS') AS Create_Date,TO_CHAR(Next_Message_Max_Date,'DD/MM/YYYY HH24:MI:SS') AS Next_Message_Max_Date,TO_CHAR(Next_Message_Min_Date,'DD/MM/YYYY HH24:MI:SS') AS Next_Message_Min_Date,User_Comment,MessageXML FROM NPC_Message WHERE NPC_MESSAGE_ID = "
					+ npcMessageModel.getNPCMessageID() + " AND " + "IsPort" + " = "
					+ (npcMessageModel.isPort() ? 1 : 0);
			rs = stmt.executeQuery(selectStmt);
			NPCMessageModel resultNPCMessageModel = null;
			while (rs.next()) {
				if (rs.getShort("IsPort") == 1)
					resultNPCMessageModel = PortMessageModel.createPortMessage(npcMessageModel);
				else
					resultNPCMessageModel = BulkSyncMessageModel.createBulkSyncMessage(npcMessageModel);
				NPCMessageModel.set(resultNPCMessageModel, "NPC_MESSAGE_ID", rs.getLong("NPC_MESSAGE_ID"));
				NPCMessageModel.set(resultNPCMessageModel, "IsPort", rs.getBoolean("IsPort"));
				NPCMessageModel.set(resultNPCMessageModel, "Sent", rs.getBoolean("Sent"));
				NPCMessageModel.set(resultNPCMessageModel, "Transaction_Date", rs.getString("Transaction_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "ReturnMessage", rs.getString("ReturnMessage"));
				NPCMessageModel.set(resultNPCMessageModel, "Current_Message_Max_Date",
						rs.getDate("Current_Message_Max_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Current_Message_Min_Date",
						rs.getDate("Current_Message_Min_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Create_User", rs.getString("Create_User"));
				NPCMessageModel.set(resultNPCMessageModel, "Create_Date", rs.getString("Create_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Next_Message_Max_Date",
						rs.getString("Next_Message_Max_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Next_Message_Min_Date",
						rs.getString("Next_Message_Min_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "User_Comment", rs.getString("User_Comment"));
				if (resultNPCMessageModel instanceof PortMessageModel) {
					PortMessageModel portMessageModel = (PortMessageModel) resultNPCMessageModel;
					PortMessageDAO.getPortMessage(conn, portMessageModel, requiredMessageCodes, optionalMessageCodes);
				} else if (resultNPCMessageModel instanceof BulkSyncMessageModel) {
					BulkSyncMessageModel bulkSyncMessageModel = (BulkSyncMessageModel) resultNPCMessageModel;
					BulkSyncMessageDAO.getBulkSyncMessage(conn, bulkSyncMessageModel);
				}
			}
			return resultNPCMessageModel;

		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static List<NPCMessageModel> getUnsentMessages(Connection conn, Map<String, String[]> requiredMessageCodes,
			Map<String, String[]> optionalMessageCodes) throws SQLException, JAXBException, NPCException {
		ArrayList<NPCMessageModel> unsentMessages = new ArrayList<>();
		unsentMessages.addAll(PortMessageDAO.getUnsentMessages(conn, requiredMessageCodes, optionalMessageCodes));
		unsentMessages.addAll(BulkSyncMessageDAO.getUnsentMessages(conn));
		return unsentMessages;
	}

	public static NPCMessageModel getLastNPCMessageByMessageCode(Connection conn, PortMessageModel portMessageModel)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT NPC_Message.NPC_MESSAGE_ID,IsPort,Sent,TO_CHAR(Transaction_Date,'DD/MM/YYYY HH24:MI:SS') AS Transaction_Date,ReturnMessage,TO_CHAR(Current_Message_Max_Date,'DD/MM/YYYY HH24:MI:SS') AS Current_Message_Max_Date,TO_CHAR(Current_Message_Min_Date,'DD/MM/YYYY HH24:MI:SS') AS Current_Message_Min_Date,Create_User,TO_CHAR(NPC_Message.Create_Date,'DD/MM/YYYY HH24:MI:SS') AS Create_Date,TO_CHAR(Next_Message_Max_Date,'DD/MM/YYYY HH24:MI:SS') AS Next_Message_Max_Date,TO_CHAR(Next_Message_Min_Date,'DD/MM/YYYY HH24:MI:SS') AS Next_Message_Min_Date,User_Comment,MessageXML FROM NPC_Message,PORT_MESSAGE WHERE NPC_Message.NPC_MESSAGE_ID = PORT_MESSAGE.NPC_MESSAGE_ID AND NPC_Message.Sent = 1 AND MESSAGECODE IN "
					+ DBTypeConverter.inStatement(portMessageModel.getPortMessageType().getMessageCode()) + " AND "
					+ "PORT_MESSAGE" + "." + "INTERNAL_PORT_ID" + " = "
					+ DBTypeConverter.toSQLVARCHAR2(portMessageModel.getInternalPortID());
			if (portMessageModel.getPortMessageType().getPortID() != null
					&& !"".equals(portMessageModel.getPortMessageType().getPortID()))
				selectStmt = selectStmt + " AND (PORT_MESSAGE.PORTID = "
						+ DBTypeConverter.toSQLVARCHAR2(portMessageModel.getPortMessageType().getPortID()) + " OR "
						+ "PORT_MESSAGE" + "." + "PORTID" + " IS NULL)";
			selectStmt = selectStmt + " ORDER  BY NPC_Message.Transaction_Date DESC ";
			rs = stmt.executeQuery(selectStmt);
			NPCMessageModel resultNPCMessageModel;
			for (resultNPCMessageModel = null; rs.next();) {
				resultNPCMessageModel = new NPCMessageModel();
				NPCMessageModel.set(resultNPCMessageModel, "NPC_MESSAGE_ID", rs.getLong("NPC_MESSAGE_ID"));
				NPCMessageModel.set(resultNPCMessageModel, "IsPort", rs.getBoolean("IsPort"));
				NPCMessageModel.set(resultNPCMessageModel, "Sent", rs.getBoolean("Sent"));
				NPCMessageModel.set(resultNPCMessageModel, "Transaction_Date", rs.getString("Transaction_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "ReturnMessage", rs.getString("ReturnMessage"));
				NPCMessageModel.set(resultNPCMessageModel, "Current_Message_Max_Date",
						rs.getString("Current_Message_Max_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Current_Message_Min_Date",
						rs.getString("Current_Message_Min_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Create_User", rs.getString("Create_User"));
				NPCMessageModel.set(resultNPCMessageModel, "Create_Date", rs.getString("Create_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Next_Message_Max_Date",
						rs.getString("Next_Message_Max_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "Next_Message_Min_Date",
						rs.getString("Next_Message_Min_Date"));
				NPCMessageModel.set(resultNPCMessageModel, "User_Comment", rs.getString("User_Comment"));
				break;
			}

			return resultNPCMessageModel;

		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static BigDecimal getTransactionDate(Connection conn, NPCMessageModel npcMessageModel) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			selectStmt = "SELECT TO_CHAR(transaction_date, 'YYYYMMDDHH24MISS') AS Transaction_Date FROM NPC_Message WHERE NPC_MESSAGE_ID = "
					+ npcMessageModel.getNPCMessageID();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(selectStmt);
			String transactionDate;
			for (transactionDate = ""; rs.next(); transactionDate = rs.getString("Transaction_Date"))
				;
			return new BigDecimal(transactionDate);

		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + selectStmt + "]");
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static final String NPC_MESSAGE = "NPC_Message";
	public static final String NPC_MESSAGE_ID = "NPC_MESSAGE_ID";
	public static final String IS_PORT = "IsPort";
	public static final String SENT = "Sent";
	public static final String TRANSACTION_DATE = "Transaction_Date";
	public static final String RETURN_MESSAGE = "ReturnMessage";
	public static final String CURRENT_MESSAGE_MAX_DATE = "Current_Message_Max_Date";
	public static final String CURRENT_MESSAGE_MIN_DATE = "Current_Message_Min_Date";
	public static final String CREATE_USER = "Create_User";
	public static final String CREATE_DATE = "Create_Date";
	public static final String NEXT_MESSAGE_MAX_DATE = "Next_Message_Max_Date";
	public static final String NEXT_MESSAGE_MIN_DATE = "Next_Message_Min_Date";
	public static final String USER_COMMENT = "User_Comment";
	public static final String MESSAGE_XML = "MessageXML";
	public static final short IS_PORT_TRUE = 1;
	public static final short IS_PORT_FALSE = 0;
	public static final short IS_SENT_TRUE = 1;
	public static final short IS_SENT_FALSE = 0;
	static final Logger logger = LoggerFactory.getLogger(NPCMessageDAO.class.getName());
}
