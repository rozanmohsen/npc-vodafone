package com.asset.vodafone.npc.core.dao;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.BulkSyncMessageModel;
import com.asset.vodafone.npc.core.models.NPCMessageModel;
import com.asset.vodafone.npc.core.service.NPCService;
import com.asset.vodafone.npc.webservice.xsd.portmessage.BulkSyncMessageType;

public class BulkSyncMessageDAO {
	private BulkSyncMessageDAO() {

	}

	/**
	 * This is insertBulkSyncMessage method that insert message of type
	 * bulkSyncMessage into 'BULK_SYNC_MESSAGE'table
	 * 
	 * @param conn
	 * @param bulkSyncMessageModel
	 * @throws SQLException
	 */
	public static void insertBulkSyncMessage(Connection conn, BulkSyncMessageModel bulkSyncMessageModel)
			throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			stmt = conn.createStatement();
			BulkSyncMessageType bulkSyncMessageType = bulkSyncMessageModel.getBulkSyncMessageType();
			insertStmt = "INSERT INTO " + bulkSyncMessage + "(" + npcMessageID + "," + messageCode + "," + messageID
					+ "," + syncID + "," + startDate + "," + endDate + "," + comments1 + "," + comments2 + ") "
					+ "VALUES (" + bulkSyncMessageModel.getNPCMessageID() + ","
					+ (!"null".equals(bulkSyncMessageType.getMessageCode())
							&& bulkSyncMessageType.getMessageCode() != null
									? "'" + bulkSyncMessageType.getMessageCode() + "'"
									: "NULL")
					+ ","
					+ (!"null".equals(bulkSyncMessageType.getMessageID()) && bulkSyncMessageType.getMessageID() != null
							? "'" + bulkSyncMessageType.getMessageID() + "'"
							: "NULL")
					+ ","
					+ (!"null".equals(bulkSyncMessageType.getSyncID()) && bulkSyncMessageType.getSyncID() != null
							? "'" + bulkSyncMessageType.getSyncID() + "'"
							: "NULL")
					+ ","
					+ (!"null".equals(bulkSyncMessageType.getStartDate()) && bulkSyncMessageType.getStartDate() != null
							? "'" + bulkSyncMessageType.getStartDate() + "'"
							: "NULL")
					+ ","
					+ (!"null".equals(bulkSyncMessageType.getEndDate()) && bulkSyncMessageType.getEndDate() != null
							? "'" + bulkSyncMessageType.getEndDate() + "'"
							: "NULL")
					+ ","
					+ (!"null".equals(bulkSyncMessageType.getComments1()) && bulkSyncMessageType.getComments1() != null
							? "'" + bulkSyncMessageType.getComments1() + "'"
							: "NULL")
					+ ","
					+ (!"null".equals(bulkSyncMessageType.getComments2()) && bulkSyncMessageType.getComments2() != null
							? "'" + bulkSyncMessageType.getComments2() + "'"
							: "NULL")
					+ ")";
			stmt.execute(insertStmt);
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

	/**
	 * getUnsentMessages method return arrayList BulkSyncMessageModel of unsent
	 * messages of type bulkSyncMessage from table NPC_MESSAGE and BULK_SYNC_MESSAGE
	 * WHERE NPC_MESSAGE.SENT=0 AND NPC_MESSAGE.NPC_MESSAGE_ID
	 * =BULK_SYNC_MESSAGE.NPC_MESSAGE_ID
	 * 
	 * @param conn
	 * @return ArrayList<BulkSyncMessageModel> unsentMessages
	 * @throws SQLException
	 * @throws JAXBException
	 */
	public static List<BulkSyncMessageModel> getUnsentMessages(Connection conn) throws SQLException, JAXBException {
		ArrayList<BulkSyncMessageModel> unsentMessages = new ArrayList<>();
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
			logger.debug("Start select unsent messages from NPC_Message and BULK_SYNC_MESSAGE ");
			
			selectStmt = "SELECT *  FROM NPC_Message,BULK_SYNC_MESSAGE WHERE NPC_Message.Sent = 0 AND PICKED_BY = '" + jobId
					+ "' AND NPC_Message.NPC_MESSAGE_ID = BULK_SYNC_MESSAGE.NPC_MESSAGE_ID ORDER BY NPC_Message.NPC_MESSAGE_ID ASC ";
			rs = stmt.executeQuery(selectStmt);
			BulkSyncMessageModel bulkSyncMessageModel = null;
			for (; rs.next(); unsentMessages.add(bulkSyncMessageModel)) {
				bulkSyncMessageModel = BulkSyncMessageModel.createBulkSyncMessage();
				NPCMessageModel.set(bulkSyncMessageModel, "NPC_MESSAGE_ID", rs.getLong("NPC_MESSAGE_ID"));
				NPCMessageModel.set(bulkSyncMessageModel, "IsPort", rs.getBoolean("IsPort"));
				NPCMessageModel.set(bulkSyncMessageModel, "Sent", rs.getBoolean("Sent"));
				NPCMessageModel.set(bulkSyncMessageModel, "Transaction_Date", rs.getDate("Transaction_Date"));
				NPCMessageModel.set(bulkSyncMessageModel, "ReturnMessage", rs.getString("ReturnMessage"));
				NPCMessageModel.set(bulkSyncMessageModel, "Current_Message_Max_Date",
						rs.getString("Current_Message_Max_Date"));
				NPCMessageModel.set(bulkSyncMessageModel, "Current_Message_Min_Date",
						rs.getString("Current_Message_Min_Date"));
				NPCMessageModel.set(bulkSyncMessageModel, "Create_User", rs.getString("Create_User"));
				NPCMessageModel.set(bulkSyncMessageModel, "Create_Date", rs.getString("Create_Date"));
				NPCMessageModel.set(bulkSyncMessageModel, "Next_Message_Max_Date",
						rs.getString("Next_Message_Max_Date"));
				NPCMessageModel.set(bulkSyncMessageModel, "Next_Message_Min_Date",
						rs.getString("Next_Message_Min_Date"));
				NPCMessageModel.set(bulkSyncMessageModel, "User_Comment", rs.getString("User_Comment"));
				NPCMessageModel.set(bulkSyncMessageModel, "Picked_By", rs.getString("PICKED_BY"));
				BulkSyncMessageModel.set(bulkSyncMessageModel, npcMessageID, rs.getObject(npcMessageID));
				BulkSyncMessageModel.set(bulkSyncMessageModel, messageID, rs.getObject(messageID));
				BulkSyncMessageModel.set(bulkSyncMessageModel, messageCode, rs.getObject(messageCode));
				BulkSyncMessageModel.set(bulkSyncMessageModel, syncID, rs.getObject(syncID));
				BulkSyncMessageModel.set(bulkSyncMessageModel, startDate, rs.getObject(startDate));
				BulkSyncMessageModel.set(bulkSyncMessageModel, endDate, rs.getObject(endDate));
				BulkSyncMessageModel.set(bulkSyncMessageModel, comments1, rs.getObject(comments1));
				BulkSyncMessageModel.set(bulkSyncMessageModel, comments2, rs.getObject(comments2));
			}

			rs.close();
			logger.debug("Retrieving unsent messages of type bulk sync message has been done successfully and number of unsent messages ={}",unsentMessages.size());
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

	/**
	 * getBulkSyncMessage method retrieve BulkSyncMessageModel of unsent messages
	 * 
	 * @param conn
	 * @param bulkSyncMessageModel
	 * @return
	 * @throws SQLException
	 */
	public static BulkSyncMessageModel getBulkSyncMessage(Connection conn, BulkSyncMessageModel bulkSyncMessageModel)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT *  FROM NPC_Message," + bulkSyncMessage + " WHERE " + "NPC_Message" + "." + "Sent"
					+ " = " + 0;
			for (rs = stmt.executeQuery(selectStmt); rs.next(); BulkSyncMessageModel.set(bulkSyncMessageModel,
					comments2, rs.getObject(comments2))) {
				BulkSyncMessageModel.set(bulkSyncMessageModel, npcMessageID, rs.getObject(npcMessageID));
				BulkSyncMessageModel.set(bulkSyncMessageModel, messageID, rs.getObject(messageID));
				BulkSyncMessageModel.set(bulkSyncMessageModel, messageCode, rs.getObject(messageCode));
				BulkSyncMessageModel.set(bulkSyncMessageModel, syncID, rs.getObject(syncID));
				BulkSyncMessageModel.set(bulkSyncMessageModel, startDate, rs.getObject(startDate));
				BulkSyncMessageModel.set(bulkSyncMessageModel, endDate, rs.getObject(endDate));
				BulkSyncMessageModel.set(bulkSyncMessageModel, comments1, rs.getObject(comments1));
			}

			return bulkSyncMessageModel;
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

	private static String bulkSyncMessage = "BULK_SYNC_MESSAGE";
	private static String npcMessageID = "NPC_Message_ID";
	private static String messageCode = "MessageCode";
	private static String messageID = "MessageID";
	private static String syncID = "SyncID";
	private static String startDate = "StartDate";
	private static String endDate = "EndDate";
	private static String comments1 = "Comments1";
	private static String comments2 = "Comments2";
	static final Logger logger = LoggerFactory.getLogger(BulkSyncMessageDAO.class.getName());

}
