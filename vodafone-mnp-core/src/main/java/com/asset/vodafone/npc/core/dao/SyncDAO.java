package com.asset.vodafone.npc.core.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asset.vodafone.npc.core.models.SyncModel;
import com.asset.vodafone.npc.core.utils.DBTypeConverter;

public class SyncDAO {
	private static final Logger logger = LoggerFactory.getLogger(SyncDAO.class.getName());

	public static void insertSync(Connection conn, SyncModel syncModel) throws SQLException {
		Statement stmt = null;
		String insertStmt = "";
		try {
			stmt = conn.createStatement();
			insertStmt = "INSERT INTO SYNC (MSISDN, BULK_SYNC_IDNUMBER, IDNUMBER, DONOR_ID, RECIPIENT_ID, NEWROUTE, TIMESTAMP)"
					+ "VALUES (" + DBTypeConverter.toSQLVARCHAR2(syncModel.getMsisdn()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getBulkSyncIDNumber()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getIdNumber()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getDonorID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getRecipientID()) + ","
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getNewRoute()) + ","
					+ DBTypeConverter.toSQLNumber(syncModel.getTimeStamp()) + ")";
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

	public static void updateSync(Connection conn, SyncModel syncModel) throws SQLException {
		Statement stmt = null;
		String updateStmt = "";
		try {
			stmt = conn.createStatement();
			updateStmt = "UPDATE SYNC SET BULK_SYNC_IDNUMBER = "
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getBulkSyncIDNumber()) + ", IDNUMBER = "
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getIdNumber()) + ", DONOR_ID = "
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getDonorID()) + ", RECIPIENT_ID = "
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getRecipientID()) + ", NEWROUTE = "
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getNewRoute()) + ", TIMESTAMP = "
					+ DBTypeConverter.toSQLNumber(syncModel.getTimeStamp()) + " WHERE  MSISDN = "
					+ syncModel.getMsisdn();
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

	public static boolean isMSISDNExists(Connection conn, SyncModel syncModel) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String selectStmt = "";
		try {
			stmt = conn.createStatement();
			selectStmt = "SELECT MSISDN FROM SYNC WHERE MSISDN = "
					+ DBTypeConverter.toSQLVARCHAR2(syncModel.getMsisdn());
			rs = stmt.executeQuery(selectStmt);
			return rs.next();
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

	public static void removeSYNC(Connection conn, SyncModel syncModel) throws SQLException {
		Statement stmt = null;
		String deleteStmt = "";
		try {
			stmt = conn.createStatement();
			deleteStmt = "DELETE FROM SYNC WHERE MSISDN = " + syncModel.getMsisdn();
			stmt.execute(deleteStmt);
		} catch (SQLException ex) {
			final String message = ex.getMessage();
			logger.error(message, ex);
			throw new SQLException(message + "[" + deleteStmt + "]");
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}
