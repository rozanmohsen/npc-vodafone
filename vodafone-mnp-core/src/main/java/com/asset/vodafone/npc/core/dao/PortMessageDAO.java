package com.asset.vodafone.npc.core.dao;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.exception.NPCException;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.models.NumbersToPortModel;
import com.asset.vodafone.npc.core.models.PortMessageModel;
import com.asset.vodafone.npc.core.models.SubscriberDataModel;
import com.asset.vodafone.npc.core.service.NPCService;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;
import com.asset.vodafone.npc.webservice.xsd.portmessage.PortMessageType;

public class PortMessageDAO {
	private static final Logger logger = LoggerFactory.getLogger(PortMessageDAO.class.getName());

	private PortMessageDAO() {
	}

	public static void insertPortMessage(Connection conn, PortMessageModel portMessageModel) throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			logger.debug(
					"Start inserting port message data into Port message table with NPC Message ID {} and Internal Port ID {}",
					portMessageModel.getNPCMessageID(), portMessageModel.getInternalPortID());
			PortMessageType portMessageType = portMessageModel.getPortMessageType();
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO PORT_MESSAGE(NPC_MESSAGE_ID,MESSAGEID,MESSAGECODE,SERVICETYPE,PORTID,PORTINGREQUESTFORMID,DONORID,RECIPIENTID,ORIGINATORID,RESPONSEDUEDATE,NEWROUTE,NPDUEDATE,REJECTCODE,REJECTEDMESSAGECODE,TRANSFERFEE,AVERAGEINVOICEFEE,INVOICEDATE,PAYMENTDUE,INTERNAL_PORT_ID,COMMENTS1,COMMENTS2)VALUES ("
					+ DBTypeConverter.toSQLNumber(portMessageModel.getNPCMessageID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getMessageID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getMessageCode()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getServiceType()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getPortID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getPortReqFormID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getDonorID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getRecipientID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getOriginatorID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getResponseDueDate()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getNewRoute()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getNpDueDate()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getRejectCode()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getRejectedMessageCode()) + ","
					+ DBTypeConverter.toSQLNumber(portMessageType.getTransferFee()) + ","
					+ DBTypeConverter.toSQLNumber(portMessageType.getAverageInvoiceFee()) + ","
					+ DBTypeConverter.toSQLNumber(portMessageType.getInvoiceDate()) + ","
					+ DBTypeConverter.toSQLNumber(portMessageType.getPaymentDue()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageModel.getInternalPortID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getComments1()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(portMessageType.getComments2()) + ")";
			stmt.execute(insertStmt);
			logger.debug("Inserting port message data into Port message table has done successfully with");
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + insertStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public static List<PortMessageModel> getPortMessageHistory(Connection conn, PortMessageModel portMessageModel)
			throws SQLException, JAXBException {
		Statement stmt = null;
		ResultSet rs = null;
		final ArrayList<PortMessageModel> portMessageList = new ArrayList<>();
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT * FROM PORT_MESSAGE WHERE INTERNAL_PORT_ID = " + portMessageModel.getInternalPortID();
			rs = stmt.executeQuery(selectStmt);
			PortMessageModel rowModel = null;
			while (rs.next()) {
				rowModel = PortMessageModel.createPortMessage();
				PortMessageModel.set(rowModel, "NPC_MESSAGE_ID", rs.getObject("NPC_MESSAGE_ID"));
				PortMessageModel.set(rowModel, "MESSAGEID", rs.getObject("MESSAGEID"));
				PortMessageModel.set(rowModel, "MESSAGECODE", rs.getObject("MESSAGECODE"));
				PortMessageModel.set(rowModel, "SERVICETYPE", rs.getObject("SERVICETYPE"));
				PortMessageModel.set(rowModel, "PORTID", rs.getObject("PORTID"));
				PortMessageModel.set(rowModel, "PORTINGREQUESTFORMID", rs.getObject("PORTINGREQUESTFORMID"));
				PortMessageModel.set(rowModel, "DONORID", rs.getObject("DONORID"));
				PortMessageModel.set(rowModel, "RECIPIENTID", rs.getObject("RECIPIENTID"));
				PortMessageModel.set(rowModel, "ORIGINATORID", rs.getObject("ORIGINATORID"));
				PortMessageModel.set(rowModel, "RESPONSEDUEDATE", rs.getObject("RESPONSEDUEDATE"));
				PortMessageModel.set(rowModel, "NEWROUTE", rs.getObject("NEWROUTE"));
				PortMessageModel.set(rowModel, "NPDUEDATE", rs.getObject("NPDUEDATE"));
				PortMessageModel.set(rowModel, "REJECTCODE", rs.getObject("REJECTCODE"));
				PortMessageModel.set(rowModel, "REJECTEDMESSAGECODE", rs.getObject("REJECTEDMESSAGECODE"));
				PortMessageModel.set(rowModel, "TRANSFERFEE", rs.getObject("TRANSFERFEE"));
				PortMessageModel.set(rowModel, "AVERAGEINVOICEFEE", rs.getObject("AVERAGEINVOICEFEE"));
				PortMessageModel.set(rowModel, "COMMENTS1", rs.getObject("COMMENTS1"));
				PortMessageModel.set(rowModel, "COMMENTS2", rs.getObject("COMMENTS2"));
				PortMessageModel.set(rowModel, "INVOICEDATE", rs.getObject("INVOICEDATE"));
				PortMessageModel.set(rowModel, "PAYMENTDUE", rs.getObject("PAYMENTDUE"));
				PortMessageModel.set(rowModel, "INTERNAL_PORT_ID", rs.getObject("INTERNAL_PORT_ID"));
				portMessageList.add(rowModel);
			}
			return portMessageList;
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

	public static PortMessageModel getLastPortMessage(Connection conn, PortMessageModel portMessageModel)
			throws SQLException, JAXBException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT * FROM PORT_MESSAGE WHERE MESSAGEID =  SELECT MAX(INTERNAL_PORT_MESSAGE.NPC_MESSAGE_ID) FROM PORT_MESSAGE INTERNAL_PORT_MESSAGE WHERE ";
			String portIDCondition = portMessageModel.getPortMessageType().getPortID() != null
					&& !"".equals(portMessageModel.getPortMessageType().getPortID())
							? " INTERNAL_PORT_MESSAGE.PORTID="
									+ DBTypeConverter.toSQLVARCHAR2(portMessageModel.getPortMessageType().getPortID())
							: "";
			String internalPortIDCondition = portMessageModel.getInternalPortID() != null
					&& !"".equals(portMessageModel.getInternalPortID())
							? " INTERNAL_PORT_MESSAGE.INTERNAL_PORT_ID="
									+ DBTypeConverter.toSQLVARCHAR2(portMessageModel.getInternalPortID())
							: "";
			selectStmt = selectStmt + ("".equals(portIDCondition) && !"".equals(internalPortIDCondition)
					? portIDCondition + internalPortIDCondition
					: portIDCondition + " AND " + internalPortIDCondition) + ")";
			rs = stmt.executeQuery(selectStmt);
			PortMessageModel rowModel = PortMessageModel.createPortMessage();
			while (rs.next()) {
				PortMessageModel.set(rowModel, "NPC_MESSAGE_ID", rs.getObject("NPC_MESSAGE_ID"));
				PortMessageModel.set(rowModel, "MESSAGEID", rs.getObject("MESSAGEID"));
				PortMessageModel.set(rowModel, "MESSAGECODE", rs.getObject("MESSAGECODE"));
				PortMessageModel.set(rowModel, "SERVICETYPE", rs.getObject("SERVICETYPE"));
				PortMessageModel.set(rowModel, "PORTID", rs.getObject("PORTID"));
				PortMessageModel.set(rowModel, "PORTINGREQUESTFORMID", rs.getObject("PORTINGREQUESTFORMID"));
				PortMessageModel.set(rowModel, "DONORID", rs.getObject("DONORID"));
				PortMessageModel.set(rowModel, "RECIPIENTID", rs.getObject("RECIPIENTID"));
				PortMessageModel.set(rowModel, "ORIGINATORID", rs.getObject("ORIGINATORID"));
				PortMessageModel.set(rowModel, "RESPONSEDUEDATE", rs.getObject("RESPONSEDUEDATE"));
				PortMessageModel.set(rowModel, "NEWROUTE", rs.getObject("NEWROUTE"));
				PortMessageModel.set(rowModel, "NPDUEDATE", rs.getObject("NPDUEDATE"));
				PortMessageModel.set(rowModel, "REJECTCODE", rs.getObject("REJECTCODE"));
				PortMessageModel.set(rowModel, "REJECTEDMESSAGECODE", rs.getObject("REJECTEDMESSAGECODE"));
				PortMessageModel.set(rowModel, "TRANSFERFEE", rs.getObject("TRANSFERFEE"));
				PortMessageModel.set(rowModel, "AVERAGEINVOICEFEE", rs.getObject("AVERAGEINVOICEFEE"));
				PortMessageModel.set(rowModel, "COMMENTS1", rs.getObject("COMMENTS1"));
				PortMessageModel.set(rowModel, "COMMENTS2", rs.getObject("COMMENTS2"));
				PortMessageModel.set(rowModel, "INVOICEDATE", rs.getObject("INVOICEDATE"));
				PortMessageModel.set(rowModel, "PAYMENTDUE", rs.getObject("PAYMENTDUE"));
				PortMessageModel.set(rowModel, "INTERNAL_PORT_ID", rs.getObject("INTERNAL_PORT_ID"));
			}
			return rowModel;
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

	public static PortMessageModel getPortMessage(Connection conn, PortMessageModel portMessageModel)
			throws SQLException, JAXBException {
		return getPortMessage(conn, portMessageModel, null, null);
	}

	public static PortMessageModel getPortMessage(Connection conn, PortMessageModel portMessageModel,
			Map<String, String[]> requiredMessageCodes, Map<String, String[]> optionalMessageCodes)
			throws SQLException, JAXBException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT NPC_MESSAGE_ID,MESSAGEID,PORTID,SERVICETYPE,MESSAGECODE,PORTINGREQUESTFORMID,DONORID,RECIPIENTID,NPDUEDATE,NEWROUTE,REJECTCODE,REJECTEDMESSAGECODE,COMMENTS1,COMMENTS2,ORIGINATORID,INVOICEDATE,PAYMENTDUE,RESPONSEDUEDATE, NVL(TRANSFERFEE, 0),NVL(AVERAGEINVOICEFEE,0) FROM PORT_MESSAGE WHERE NPC_MESSAGE_ID = "
					+ portMessageModel.getNPCMessageID();
			rs = stmt.executeQuery(selectStmt);
			String[] fields = null;
			NumbersToPortModel numbersToPortModel;
			while (rs.next()) {
				if (requiredMessageCodes == null && optionalMessageCodes == null) {
					PortMessageModel.set(portMessageModel, "NPC_MESSAGE_ID", rs.getObject("NPC_MESSAGE_ID"));
					PortMessageModel.set(portMessageModel, "MESSAGEID", rs.getObject("MESSAGEID"));
					PortMessageModel.set(portMessageModel, "MESSAGECODE", rs.getObject("MESSAGECODE"));
					PortMessageModel.set(portMessageModel, "SERVICETYPE", rs.getObject("SERVICETYPE"));
					PortMessageModel.set(portMessageModel, "PORTID", rs.getObject("PORTID"));
					PortMessageModel.set(portMessageModel, "PORTINGREQUESTFORMID",
							rs.getObject("PORTINGREQUESTFORMID"));
					PortMessageModel.set(portMessageModel, "DONORID", rs.getObject("DONORID"));
					PortMessageModel.set(portMessageModel, "RECIPIENTID", rs.getObject("RECIPIENTID"));
					PortMessageModel.set(portMessageModel, "ORIGINATORID", rs.getObject("ORIGINATORID"));
					PortMessageModel.set(portMessageModel, "RESPONSEDUEDATE", rs.getObject("RESPONSEDUEDATE"));
					PortMessageModel.set(portMessageModel, "NEWROUTE", rs.getObject("NEWROUTE"));
					PortMessageModel.set(portMessageModel, "NPDUEDATE", rs.getObject("NPDUEDATE"));
					PortMessageModel.set(portMessageModel, "REJECTCODE", rs.getObject("REJECTCODE"));
					PortMessageModel.set(portMessageModel, "REJECTEDMESSAGECODE", rs.getObject("REJECTEDMESSAGECODE"));
					PortMessageModel.set(portMessageModel, "TRANSFERFEE", rs.getObject("TRANSFERFEE"));
					PortMessageModel.set(portMessageModel, "AVERAGEINVOICEFEE", rs.getObject("AVERAGEINVOICEFEE"));
					PortMessageModel.set(portMessageModel, "COMMENTS1", rs.getObject("COMMENTS1"));
					PortMessageModel.set(portMessageModel, "COMMENTS2", rs.getObject("COMMENTS2"));
					PortMessageModel.set(portMessageModel, "INVOICEDATE", rs.getObject("INVOICEDATE"));
					PortMessageModel.set(portMessageModel, "PAYMENTDUE", rs.getObject("PAYMENTDUE"));
					PortMessageModel.set(portMessageModel, "INTERNAL_PORT_ID", rs.getObject("INTERNAL_PORT_ID"));
				} else {

					if (requiredMessageCodes != null) {
						fields = requiredMessageCodes.get(rs.getString("MESSAGECODE"));
						for (int i = 1; i < fields.length; i++)
							PortMessageModel.set(portMessageModel, fields[i], rs.getObject(fields[i]));

						fields = optionalMessageCodes.get(rs.getString("MESSAGECODE"));
						for (int i = 1; i < fields.length; i++)
							PortMessageModel.set(portMessageModel, fields[i], String.valueOf(rs.getObject(fields[i])));
					}

				}
				SubscriberDataModel subscriberDataModel = SubscriberDataModel.createSubscriberData();
				subscriberDataModel.setNPCMessageID(portMessageModel.getNPCMessageID());
				portMessageModel.setSubscriberDataModel(SubscriberDataDAO.getSubscriberData(conn, subscriberDataModel));
				numbersToPortModel = NumbersToPortModel.createNumbersToPort();
				numbersToPortModel.setNPCMessageID(portMessageModel.getNPCMessageID());
			}

			return portMessageModel;

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

	public static List<PortMessageModel> getUnsentMessages(Connection conn, Map<String, String[]> requiredMessageCodes,
			Map<String, String[]> optionalMessageCodes) throws SQLException, JAXBException, NPCException {
		ArrayList<PortMessageModel> unsentMessages = new ArrayList<>();
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		String runnerFetchedRowNumber = "";
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			runnerFetchedRowNumber = System.getenv("RUNNER_FETCHED_ROW_NUMBER");
			if (runnerFetchedRowNumber == null) {
				runnerFetchedRowNumber = NPCService.getRunnerFetchedRowNumber();

			}
			int rowNumber = Integer.parseInt(runnerFetchedRowNumber);

			rs = stmt.executeQuery(
					"SELECT PICKED_BY FROM NPC_Message WHERE NPC_Message.Sent = 0 AND PICKED_BY IS NULL FOR UPDATE OF PICKED_BY");

			String jobId = "";
			try {
				jobId = InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);

			}
			for (int i = 0; rs.next() && i < rowNumber; i++) {
				rs.updateString("PICKED_BY", jobId);
				rs.updateRow();

			}

			conn.commit();
			conn.setAutoCommit(true);

			selectStmt = "SELECT *  FROM NPC_Message,PORT_MESSAGE WHERE NPC_Message.Sent = 0 AND PICKED_BY = '" + jobId
					+ "' AND NPC_Message.NPC_MESSAGE_ID = PORT_MESSAGE.NPC_MESSAGE_ID ORDER BY NPC_Message.NPC_MESSAGE_ID ASC ";

			rs = stmt.executeQuery(selectStmt);

			PortMessageModel portMessageModel = null;
			String[] fields = null;
			while (rs.next()) {
				portMessageModel = PortMessageModel.createPortMessage();
				NPCMessageModel.set(portMessageModel, "NPC_MESSAGE_ID", rs.getLong("NPC_MESSAGE_ID"));
				NPCMessageModel.set(portMessageModel, "IsPort", rs.getBoolean("IsPort"));
				NPCMessageModel.set(portMessageModel, "Sent", rs.getBoolean("Sent"));
				NPCMessageModel.set(portMessageModel, "Transaction_Date", rs.getDate("Transaction_Date"));
				NPCMessageModel.set(portMessageModel, "ReturnMessage", rs.getString("ReturnMessage"));
				NPCMessageModel.set(portMessageModel, "Current_Message_Max_Date",
						rs.getString("Current_Message_Max_Date"));
				NPCMessageModel.set(portMessageModel, "Current_Message_Min_Date",
						rs.getString("Current_Message_Min_Date"));
				NPCMessageModel.set(portMessageModel, "Create_User", rs.getString("Create_User"));
				NPCMessageModel.set(portMessageModel, "Create_Date", rs.getString("Create_Date"));
				NPCMessageModel.set(portMessageModel, "Next_Message_Max_Date", rs.getString("Next_Message_Max_Date"));
				NPCMessageModel.set(portMessageModel, "Next_Message_Min_Date", rs.getString("Next_Message_Min_Date"));
				NPCMessageModel.set(portMessageModel, "User_Comment", rs.getString("User_Comment"));
				NPCMessageModel.set(portMessageModel, "Picked_By", rs.getString("PICKED_BY"));
				if (requiredMessageCodes == null && optionalMessageCodes == null) {
					PortMessageModel.set(portMessageModel, "NPC_MESSAGE_ID", rs.getObject("NPC_MESSAGE_ID"));
					PortMessageModel.set(portMessageModel, "MESSAGEID", rs.getObject("MESSAGEID"));
					PortMessageModel.set(portMessageModel, "MESSAGECODE", rs.getObject("MESSAGECODE"));
					PortMessageModel.set(portMessageModel, "SERVICETYPE", rs.getObject("SERVICETYPE"));
					PortMessageModel.set(portMessageModel, "PORTID", rs.getObject("PORTID"));
					PortMessageModel.set(portMessageModel, "PORTINGREQUESTFORMID",
							rs.getObject("PORTINGREQUESTFORMID"));
					PortMessageModel.set(portMessageModel, "DONORID", rs.getObject("DONORID"));
					PortMessageModel.set(portMessageModel, "RECIPIENTID", rs.getObject("RECIPIENTID"));
					PortMessageModel.set(portMessageModel, "ORIGINATORID", rs.getObject("ORIGINATORID"));
					PortMessageModel.set(portMessageModel, "RESPONSEDUEDATE", rs.getObject("RESPONSEDUEDATE"));
					PortMessageModel.set(portMessageModel, "NEWROUTE", rs.getObject("NEWROUTE"));
					PortMessageModel.set(portMessageModel, "NPDUEDATE", rs.getObject("NPDUEDATE"));
					PortMessageModel.set(portMessageModel, "REJECTCODE", rs.getObject("REJECTCODE"));
					PortMessageModel.set(portMessageModel, "REJECTEDMESSAGECODE", rs.getObject("REJECTEDMESSAGECODE"));
					PortMessageModel.set(portMessageModel, "TRANSFERFEE", rs.getObject("TRANSFERFEE"));
					PortMessageModel.set(portMessageModel, "AVERAGEINVOICEFEE", rs.getObject("AVERAGEINVOICEFEE"));
					PortMessageModel.set(portMessageModel, "COMMENTS1", rs.getObject("COMMENTS1"));
					PortMessageModel.set(portMessageModel, "COMMENTS2", rs.getObject("COMMENTS2"));
					PortMessageModel.set(portMessageModel, "INVOICEDATE", rs.getObject("INVOICEDATE"));
					PortMessageModel.set(portMessageModel, "PAYMENTDUE", rs.getObject("PAYMENTDUE"));
					PortMessageModel.set(portMessageModel, "INTERNAL_PORT_ID", rs.getObject("INTERNAL_PORT_ID"));
				} else {
					try {
						if (requiredMessageCodes != null) {
							fields = requiredMessageCodes.get(rs.getString("MESSAGECODE"));
							for (int i = 1; i < fields.length; i++)
								try {
									Object fieldValue = rs.getObject(fields[i]);
									if (fieldValue == null)
										fieldValue = "";
									PortMessageModel.set(portMessageModel, fields[i], fieldValue);
								} catch (SQLException ex) {
									throw new NPCException(NPCException.PROCESSING_FIELD_NOT_FOUND_ERROR_CODE,
											MessageFormat.format(
													"The field \"{0}\" is not found in Port Message Table please check the name of this field in the properties file",
													fields[i]));
								}

							fields = optionalMessageCodes.get(rs.getString("MESSAGECODE"));
							for (int i = 1; i < fields.length; i++)
								try {
									PortMessageModel.set(portMessageModel, fields[i], rs.getObject(fields[i]));
								} catch (SQLException ex) {
									throw new NPCException(NPCException.PROCESSING_FIELD_NOT_FOUND_ERROR_CODE,
											MessageFormat.format(
													"The field \"{0}\" is not found in Port Message Table please check the name of this field in the properties file",
													fields[i]));
								}

							PortMessageModel.set(portMessageModel, "INTERNAL_PORT_ID",
									rs.getObject("INTERNAL_PORT_ID"));
						}
					} catch (Exception ex) {
						conn.setAutoCommit(false);
						NPCException npcexception = new NPCException(
								NPCException.PROCESSING_MESSAGE_CODE_NOT_FOUND_ERROR_CODE,
								MessageFormat.format(
										"Message Code \"{0}\" not found in current Participant message codes",
										rs.getString("MESSAGECODE")));
						logger.error(npcexception.getMessage(), npcexception);

						NPCService.insertFailedMessage(npcexception, portMessageModel.getNPCMessageID());

						logger.debug("Corrupted NPC Message ID : {0} and reason is: {1} " , portMessageModel.getNPCMessageID(),npcexception);

						NPCService.updateSentFieldForCorruptedMessages(portMessageModel);
						conn.commit();
						conn.setAutoCommit(true);
						continue;

					}
				}
				SubscriberDataModel subscriberDataModel = SubscriberDataModel.createSubscriberData();
				subscriberDataModel.setNPCMessageID(portMessageModel.getNPCMessageID());
				portMessageModel.setSubscriberDataModel(SubscriberDataDAO.getSubscriberData(conn, subscriberDataModel));
				NumbersToPortModel numbersToPortModel = NumbersToPortModel.createNumbersToPort();
				numbersToPortModel.setNPCMessageID(portMessageModel.getNPCMessageID());
				portMessageModel.setNumbersToPortList(NumbersToPortDAO.getNumbersToPortList(conn, numbersToPortModel));

				unsentMessages.add(portMessageModel);
			}
			if (rs != null) {
				rs.close();
			}
			return unsentMessages;
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

	public static void updateSentFieldForCorruptedMessages(Connection conn, PortMessageModel portMessageModel)
			throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE NPC_Message SET Sent= 2 WHERE NPC_MESSAGE_ID" + " = "
					+ portMessageModel.getNPCMessageID();
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

}
