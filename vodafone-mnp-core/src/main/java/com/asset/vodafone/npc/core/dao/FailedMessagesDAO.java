package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.FailedMessagesModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;

public class FailedMessagesDAO {
	static final Logger logger = LoggerFactory.getLogger(FailedMessagesDAO.class.getName());
	private FailedMessagesDAO() {
		
	}

	/**
	 * insertFailedMessage method used to insert messages that failed to be sent to
	 * NTRA
	 * 
	 * @param conn
	 * @param failedMessagesModel
	 * @throws SQLException
	 */
	public static void insertFailedMessage(Connection conn, FailedMessagesModel failedMessagesModel)
			throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO FAILED_MESSAGES_QUEUE(NPC_MESSAGE_ID,REASON) VALUES("
					+ DBTypeConverter.toSQLNumber(failedMessagesModel.getNPCMessageID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(failedMessagesModel.getReason()) + ")";
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
}
