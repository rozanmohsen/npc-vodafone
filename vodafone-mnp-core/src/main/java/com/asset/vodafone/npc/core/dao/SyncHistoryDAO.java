package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.SyncHistoryModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;

public class SyncHistoryDAO extends SyncDAO {

	private static final Logger logger = LoggerFactory.getLogger(SyncHistoryDAO.class.getName());

	public static void insertSyncHistory(Connection conn, SyncHistoryModel syncHistoryModel) throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO SYNC_HISTORY(MSISDN, BULK_SYNC_IDNUMBER, IDNUMBER, DONOR_ID, RECIPIENT_ID, NEWROUTE, SEQ_NUM, TIMESTAMP)"
					+ "VALUES (" + DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getMsisdn()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getBulkSyncIDNumber()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getIdNumber()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getDonorID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getRecipientID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getNewRoute()) + ","
					+ DBTypeConverter.toSQLNumber(syncHistoryModel.getSeqNumber()) + ","
					+ DBTypeConverter.toSQLNumber(syncHistoryModel.getTimeStamp()) + ")";
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

	public static long getLastMSISDNSequenceNumber(Connection conn, SyncHistoryModel syncHistoryModel)
			throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT NVL(MAX(SEQ_NUM), -1) AS SEQ_NUM FROM SYNC_HISTORY WHERE MSISDN = "
					+ DBTypeConverter.toSQLVARCHAR2(syncHistoryModel.getMsisdn());
			rs = stmt.executeQuery(selectStmt);
			long seqNumber;
			for (seqNumber = 0L; rs.next(); seqNumber = rs.getLong("SEQ_NUM"))
				;
			return seqNumber;

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
}
